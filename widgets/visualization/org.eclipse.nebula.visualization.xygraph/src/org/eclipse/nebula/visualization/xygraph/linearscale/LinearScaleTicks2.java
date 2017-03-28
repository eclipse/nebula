/*******************************************************************************
 * Copyright (c) 2012, 2017 Diamond Light Source Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.linearscale;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.nebula.visualization.xygraph.linearscale.TickFactory.TickFormatting;

/**
 * Class to represent a major tick
 */
public class LinearScaleTicks2 implements ITicksProvider {
	protected List<Tick> ticks;

	/** the maximum width of tick labels */
	private int maxWidth;

	/** the maximum height of tick labels */
	private int maxHeight;

	/** the array of minor tick positions in pixels */
	protected ArrayList<Integer> minorPositions;

	protected IScaleProvider scale;

	private boolean ticksIndexBased;

	public LinearScaleTicks2(IScaleProvider scale) {
		this.scale = scale;
		minorPositions = new ArrayList<Integer>();
	}

	@Override
	public List<Integer> getPositions() {
		List<Integer> positions = new ArrayList<Integer>();
		for (Tick t : ticks)
			positions.add((int) Math.round(t.getPosition()));
		return positions;
	}

	@Override
	public int getPosition(int index) {
		return (int) Math.round(ticks.get(index).getPosition());
	}

	@Override
	public int getLabelPosition(int index) {
		return ticks.get(index).getTextPosition();
	}

	@Override
	public double getValue(int index) {
		return ticks.get(index).getValue();
	}

	@Override
	public String getLabel(int index) {
		return ticks.get(index).getText();
	}

	@Override
	public boolean isVisible(int index) {
		return true;
	}

	@Override
	public int getMajorCount() {
		return ticks == null ? 0 : ticks.size();
	}

	@Override
	public int getMinorCount() {
		return minorPositions.size();
	}

	@Override
	public int getMinorPosition(int index) {
		return minorPositions.get(index);
	}

	@Override
	public int getMaxWidth() {
		return maxWidth;
	}

	@Override
	public int getMaxHeight() {
		return maxHeight;
	}

	private final static int TICKMINDIST_IN_PIXELS_X = 40;
	private final static int TICKMINDIST_IN_PIXELS_Y = 30;
	private final static int MAX_TICKS = 12;
	private final static int MIN_TICKS = 3;

	@Override
	public Range update(final double min, final double max, int length) {
		if (scale.isLogScaleEnabled() && (min <= 0 || max <= 0))
			throw new IllegalArgumentException("Range for log scale must be in positive range");

		final int maximumNumTicks = Math.min(MAX_TICKS,
				length / (scale.isHorizontal() ? TICKMINDIST_IN_PIXELS_X : TICKMINDIST_IN_PIXELS_Y) + 1);
		int numTicks = Math.max(3, maximumNumTicks);

		final TickFactory tf;
		if (scale instanceof AbstractScale) {
			AbstractScale aScale = (AbstractScale) scale;
			if (aScale.hasUserDefinedFormat()) {
				tf = new TickFactory(scale);
			} else if (aScale.isAutoFormat()) {
				tf = new TickFactory(TickFormatting.autoMode, scale);
			} else {
				String format = aScale.getFormatPattern();
				if (format.contains("E")) {
					tf = new TickFactory(TickFormatting.useExponent, scale);
				} else {
					tf = new TickFactory(TickFormatting.autoMode, scale);
				}
			}
		} else {
			tf = new TickFactory(TickFormatting.autoMode, scale);
		}

		final int hMargin = getHeadMargin();
		final int tMargin = getTailMargin();

		// loop until labels fit
		do {
			if (ticksIndexBased) {
				ticks = tf.generateIndexBasedTicks(min, max, numTicks, !scale.hasTicksAtEnds());
			} else if (scale.isLogScaleEnabled()) {
				ticks = tf.generateLogTicks(min, max, numTicks, true, !scale.hasTicksAtEnds());
			} else {
				ticks = tf.generateTicks(min, max, numTicks, true, !scale.hasTicksAtEnds());
			}
		} while (!updateLabelPositionsAndCheckGaps(length, hMargin, tMargin, min > max) && numTicks-- > MIN_TICKS);

		updateMinorTicks(hMargin + length);
		if (scale.hasTicksAtEnds() && ticks.size() > 1)
			return new Range(ticks.get(0).getValue(), ticks.get(ticks.size() - 1).getValue());

		return null;
	}

	@Override
	public String getDefaultFormatPattern(double min, double max) {
		String format = null;

		// calculate the default decimal format
		double mantissa = Math.max(Math.abs(min), Math.abs(max));
		double power = mantissa == 0 ? -1 : Math.log10(mantissa);

		if (power >= AbstractScale.ENGINEERING_LIMIT || power < -6) {
			format = AbstractScale.DEFAULT_ENGINEERING_FORMAT;
		} else if (power <= 0) {
			StringBuilder form = new StringBuilder("##0.00");
			while (power < -1) {
				power += 1;
				form.append("#");
			}
			format = form.toString();
		} else {
			format = "############.##";
		}
		return format;
	}

	@Override
	public int getHeadMargin() {
		if (ticks == null || ticks.size() == 0 || maxWidth == 0 || maxHeight == 0) {
			// System.err.println("No ticks yet!");
			final Dimension l = scale.calculateDimension(scale.getScaleRange().getLower());
			if (scale.isHorizontal()) {
				// System.err.println("calculate X margin with " + r);
				return l.width;
			}
			// System.err.println("calculate Y margin with " + r);
			return l.height;
		}
		return scale.isHorizontal() ? (maxWidth + 1) / 2 : (maxHeight + 1) / 2;
	}

	@Override
	public int getTailMargin() {
		if (ticks == null || ticks.size() == 0 || maxWidth == 0 || maxHeight == 0) {
			// System.err.println("No ticks yet!");
			final Dimension h = scale.calculateDimension(scale.getScaleRange().getUpper());
			if (scale.isHorizontal()) {
				// System.err.println("calculate X margin with " + r);
				return h.width;
			}
			// System.err.println("calculate Y margin with " + r);
			return h.height;
		}
		return scale.isHorizontal() ? (maxWidth + 1) / 2 : (maxHeight + 1) / 2;
	}

	private static final String MINUS = "-";

	/**
	 * Update positions and max dimensions of tick labels
	 * 
	 * @return true if there is no overlaps
	 */
	private boolean updateLabelPositionsAndCheckGaps(int length, final int hMargin, final int tMargin,
			final boolean isReversed) {
		final int imax = ticks.size();
		if (imax == 0) {
			return true;
		}

		maxWidth = 0;
		maxHeight = 0;
		final boolean hasNegative = ticks.get(0).getText().startsWith(MINUS);
		final int minus = scale.calculateDimension(MINUS).width;
		for (Tick t : ticks) {
			final String l = t.getText();
			final Dimension d = scale.calculateDimension(l);
			if (hasNegative && !l.startsWith(MINUS)) {
				d.width += minus;
			}
			if (d.width > maxWidth) {
				maxWidth = d.width;
			}
			if (d.height > maxHeight) {
				maxHeight = d.height;
			}
		}

		if (length <= 0)
			return true; // sanity check

		// System.err.println("Max labels have w:" + maxWidth + ", h:" +
		// maxHeight);
		if (isReversed) {
			for (Tick t : ticks) {
				t.setPosition(length - length * t.getPosition() + hMargin);
			}
		} else {
			for (Tick t : ticks) {
				t.setPosition(length * t.getPosition() + hMargin);
			}
		}
		length += hMargin + tMargin; // re-expand length (so labels can flow
										// into margins)
		if (scale.isHorizontal()) {
			final int space = (int) (0.67 * scale.calculateDimension(" ").width);
			int last = 0;
			for (Tick t : ticks) {
				final Dimension d = scale.calculateDimension(t.getText());
				int w = d.width;
				int p = (int) Math.ceil(t.getPosition() - w * 0.5);
				if (p < 0) {
					p = 0;
				} else if (p + w >= length) {
					p = length - 1 - w;
				}
				t.setTextPosition(p);
				if (last > p) {
					if (ticks.indexOf(t) == (imax - 1) || imax > MIN_TICKS) {
						return false;
					} else {
						t.setText("");
					}
				} else {
					last = p + w + space;
				}
			}
		} else {
			for (Tick t : ticks) {
				final Dimension d = scale.calculateDimension(t.getText());
				int h = d.height;
				int p = (int) Math.ceil(length - 1 - t.getPosition() - h * 0.5);
				if (p < 0) {
					p = 0;
				} else if (p + h >= length) {
					p = length - 1 - h;
				}
				t.setTextPosition(p);
			}
		}

		return true;
	}

	private static final double LAST_STEP_FRAC = 1 - Math.log10(9); // fraction
																	// of major
																	// tick step
																	// between 9
																	// and 10

	private void updateMinorTicks(final int end) {
		minorPositions.clear();

		final int jmax = ticks.size();
		if (jmax <= 1)
			return;

		double majorStepInPixel = (ticks.get(jmax - 1).getPosition() - ticks.get(0).getPosition()) / (jmax - 1);
		if (majorStepInPixel == 0)
			return;

		int minorTicks;

		if (scale.isLogScaleEnabled()) {
			if (majorStepInPixel * LAST_STEP_FRAC >= scale.getMinorTickMarkStepHint()) {
				minorTicks = 10
						* (int) Math.round(Math.abs(Math.log10(ticks.get(1).getValue() / ticks.get(0).getValue())));
				if (minorTicks > 10)
					return; // gap is greater than a decade
				double p = ticks.get(0).getPosition();
				if (p > 0) {
					p -= majorStepInPixel;
					for (int i = 1; i < minorTicks; i++) {
						int q = (int) (p + majorStepInPixel * Math.log10((10. * i) / minorTicks));
						if (q >= 0 && q < end)
							minorPositions.add(q);
					}
				}
				for (int j = 0; j < jmax; j++) {
					p = ticks.get(j).getPosition();
					for (int i = 1; i < minorTicks; i++) {
						int q = (int) (p + majorStepInPixel * Math.log10((10. * i) / minorTicks));
						if (q >= 0 && q < end)
							minorPositions.add(q);
					}
				}
			}
		} else {
			double step = Math.abs(majorStepInPixel);
			if (ticksIndexBased) {
				minorTicks = (int) Math.abs(ticks.get(1).getValue() - ticks.get(0).getValue());
				if (minorTicks == 1)
					return;
				if (minorTicks > step / 5) {
					if (step / 5 >= scale.getMinorTickMarkStepHint()) {
						minorTicks = 5;
					} else if (step / 4 >= scale.getMinorTickMarkStepHint()) {
						minorTicks = 4;
					} else {
						minorTicks = 2;
					}
				} else if (minorTicks > 5)
					minorTicks = 5;
			} else {
				if (scale.isDateEnabled()) {
					minorTicks = 6;
				} else if (step / 5 >= scale.getMinorTickMarkStepHint()) {
					minorTicks = 5;
				} else if (step / 4 >= scale.getMinorTickMarkStepHint()) {
					minorTicks = 4;
				} else {
					minorTicks = 2;
				}
			}

			double minorStepInPixel = majorStepInPixel / minorTicks;
			double p = ticks.get(0).getPosition();
			if (p > 0) {
				p -= majorStepInPixel;
				for (int i = 1; i < minorTicks; i++) {
					int q = (int) Math.floor(p + i * minorStepInPixel);
					if (q >= 0 && q < end)
						minorPositions.add(q);
				}
			}
			for (int j = 0; j < jmax; j++) {
				p = ticks.get(j).getPosition();
				for (int i = 1; i < minorTicks; i++) {
					int q = (int) Math.floor(p + i * minorStepInPixel);
					if (q >= 0 && q < end)
						minorPositions.add(q);
				}
			}
		}
	}

	/**
	 * @param isTicksIndexBased
	 *            if true, make ticks based on axis dataset indexes
	 */
	public void setTicksIndexBased(boolean isTicksIndexBased) {
		ticksIndexBased = isTicksIndexBased;
	}
}
