/*******************************************************************************
 * Copyright (c) 2012, 2017 Diamond Light Source Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.linearscale;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;

public class LinearScaleTicks implements ITicksProvider {

	/** the array of tick label vales */
	private ArrayList<Double> values;

	/** the array of tick label */
	private ArrayList<String> labels;

	/** the array of tick label positions in pixels */
	private ArrayList<Integer> positions;

	/** the array of visibility state of tick label */
	private ArrayList<Boolean> visibilities;

	/** the array of label positions in pixels */
	private ArrayList<Integer> lPositions;

	/** the maximum width of tick labels */
	private int maxWidth;

	/** the maximum height of tick labels */
	private int maxHeight;

	/** number of pixels between major ticks */
	private int majorStepInPixel;

	/** number of pixels between minor ticks */
	private int minorStepInPixel;

	/** number of minor ticks between two major ticks */
	private int minorTicks;

	/** the array of minor tick positions in pixels */
	private ArrayList<Integer> minorPositions;

	private IScaleProvider scale;

	public LinearScaleTicks(IScaleProvider scale) {
		this.scale = scale;
		values = new ArrayList<Double>();
		labels = new ArrayList<String>();
		positions = new ArrayList<Integer>();
		lPositions = new ArrayList<Integer>();
		visibilities = new ArrayList<Boolean>();
		minorPositions = new ArrayList<Integer>();
	}

	@Override
	public List<Integer> getPositions() {
		return positions;
	}

	@Override
	public int getPosition(int index) {
		return positions.get(index);
	}

	@Override
	public double getValue(int index) {
		return values.get(index);
	}

	@Override
	public String getLabel(int index) {
		return labels.get(index);
	}

	@Override
	public int getLabelPosition(int index) {
		return lPositions.get(index);
	}

	@Override
	public boolean isVisible(int index) {
		return visibilities.get(index);
	}

	@Override
	public int getMajorCount() {
		return labels.size();
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

	@Override
	public Range update(final double min, final double max, final int length) {
		values.clear();
		labels.clear();
		positions.clear();
		lPositions.clear();
		visibilities.clear();
		minorPositions.clear();

		if (scale.isLogScaleEnabled()) {
			updateTickLabelForLogScale(min, max, length);
		} else {
			updateTickLabelForLinearScale(min, max, length);
		}

		updateTickVisibility();

		updateLabelPositionsAndMaxDimensions(length);

		updateMinorTickParameters();

		if (!scale.isLogScaleEnabled()) {
			updateMinorTicks();
		}

		return null;
	}

	@Override
	public String getDefaultFormatPattern(double min, double max) {
		String format = null;

		// calculate the default decimal format
		double mantissa = Math.abs(max - min);
		if (Math.abs(mantissa) > 0.1)
			format = "############.##";
		else {
			format = "##.##";
			while (mantissa < 1) {
				mantissa *= 10.0;
				format += "#";
			}
		}

		return format;
	}

	/**
	 * Updates tick label for log scale.
	 * 
	 * @param length
	 *            the length of scale
	 */
	private void updateTickLabelForLogScale(double min, double max, int length) {
		if (min <= 0 || max <= 0)
			throw new IllegalArgumentException("the range for log scale must be in positive range");
		boolean minBigger = max < min;
		// if (min >= max) {
		// throw new IllegalArgumentException("min must be less than max.");
		// }

		int digitMin = (int) Math.ceil(Math.log10(min));
		int digitMax = (int) Math.ceil(Math.log10(max));

		final BigDecimal MIN = new BigDecimal(new Double(min).toString());
		BigDecimal tickStep = pow(10, digitMin - 1);
		BigDecimal firstPosition;

		if (MIN.remainder(tickStep).doubleValue() <= 0) {
			firstPosition = MIN.subtract(MIN.remainder(tickStep));
		} else {
			if (minBigger)
				firstPosition = MIN.subtract(MIN.remainder(tickStep));
			else
				firstPosition = MIN.subtract(MIN.remainder(tickStep)).add(tickStep);
		}

		// add min

		if (MIN.compareTo(firstPosition) == (minBigger ? 1 : -1)) {
			values.add(min);
			labels.add(scale.format(MIN.doubleValue()));
			positions.add(scale.getMargin());
		}

		for (int i = digitMin; minBigger ? i >= digitMax : i <= digitMax; i += minBigger ? -1 : 1) {
			if (Math.abs(digitMax - digitMin) > 20) {// if the range is too big,
														// skip minor ticks.
				BigDecimal v = pow(10, i);
				if (v.doubleValue() > max)
					break;
				labels.add(scale.format(v.doubleValue()));
				values.add(v.doubleValue());

				int tickLabelPosition = (int) ((Math.log10(v.doubleValue()) - Math.log10(min))
						/ (Math.log10(max) - Math.log10(min)) * length) + scale.getMargin();
				positions.add(tickLabelPosition);
			} else {
				for (BigDecimal j = firstPosition; minBigger ? j.doubleValue() >= pow(10, i - 1).doubleValue()
						: j.doubleValue() <= pow(10, i).doubleValue(); j = minBigger ? j.subtract(tickStep)
								: j.add(tickStep)) {
					if (minBigger ? j.doubleValue() < max : j.doubleValue() > max) {
						break;
					}

					labels.add(scale.format(j.doubleValue()));
					values.add(j.doubleValue());

					int tickLabelPosition = (int) ((Math.log10(j.doubleValue()) - Math.log10(min))
							/ (Math.log10(max) - Math.log10(min)) * length) + scale.getMargin();
					positions.add(tickLabelPosition);
				}
				tickStep = minBigger ? tickStep.divide(pow(10, 1)) : tickStep.multiply(pow(10, 1));
				firstPosition = minBigger ? pow(10, i - 1) : tickStep.add(pow(10, i));
			}
		}

		// add max
		if (minBigger ? max < values.get(values.size() - 1) : max > values.get(values.size() - 1)) {
			values.add(max);
			labels.add(scale.format(max));
			positions.add(scale.getMargin() + length);
		}
	}

	@Override
	public int getHeadMargin() {
		final Range r = scale.getScaleRange();
		final Dimension l = scale.calculateDimension(r.getLower());
		final Dimension h = scale.calculateDimension(r.getUpper());
		if (scale.isHorizontal()) {
			return (int) Math.ceil(Math.max(l.width, h.width) / 2.0);
		}
		return (int) Math.ceil(Math.max(l.height, h.height) / 2.0);
	}

	@Override
	public int getTailMargin() {
		return getHeadMargin();
	}

	/**
	 * Updates tick label for normal scale.
	 * 
	 * @param length
	 *            scale tick length (without margin)
	 */
	private void updateTickLabelForLinearScale(double min, double max, int length) {
		BigDecimal gridStepBigDecimal = getGridStep(length, min, max);
		majorStepInPixel = (int) (length * gridStepBigDecimal.doubleValue() / (max - min));
		updateTickLabelForLinearScale(min, max, length, gridStepBigDecimal);
	}

	/**
	 * Updates tick label for normal scale.
	 * 
	 * @param length
	 *            scale tick length (without margin)
	 * @param tickStep
	 *            the tick step
	 */
	private void updateTickLabelForLinearScale(double min, double max, int length, BigDecimal tickStep) {
		boolean minBigger = max < min;

		final BigDecimal MIN = new BigDecimal(new Double(min).toString());
		BigDecimal firstPosition;

		// make firstPosition as the right most of min based on tickStep
		/* if (min % tickStep <= 0) */
		if (MIN.remainder(tickStep).doubleValue() <= 0) {
			/* firstPosition = min - min % tickStep */
			firstPosition = MIN.subtract(MIN.remainder(tickStep));
		} else {
			/* firstPosition = min - min % tickStep + tickStep */
			firstPosition = MIN.subtract(MIN.remainder(tickStep)).add(tickStep);
		}

		// the unit time starts from 1:00
		if (scale.isDateEnabled()) {
			BigDecimal zeroOclock = firstPosition.subtract(new BigDecimal(new Double(3600000).toString()));
			if (MIN.compareTo(zeroOclock) == -1) {
				firstPosition = zeroOclock;
			}
		}

		// add min
		int r = minBigger ? 1 : -1;
		if (MIN.compareTo(firstPosition) == r) {
			values.add(min);
			labels.add(scale.format(MIN.doubleValue()));
			positions.add(scale.getMargin());
		}

		for (BigDecimal b = firstPosition; max >= min ? b.doubleValue() <= max
				: b.doubleValue() >= max; b = b.add(tickStep)) {
			labels.add(scale.format(b.doubleValue()));
			values.add(b.doubleValue());

			int tickLabelPosition = (int) ((b.doubleValue() - min) / (max - min) * length) + scale.getMargin();
			// - LINE_WIDTH;
			positions.add(tickLabelPosition);
		}

		// add max
		if ((minBigger ? max < values.get(values.size() - 1) : max > values.get(values.size() - 1))) {
			values.add(max);
			labels.add("");
			positions.add(scale.getMargin() + length);
		}

	}

	/**
	 * Updates the visibility of tick labels.
	 */
	private void updateTickVisibility() {

		// initialize the array of tick label visibility state
		visibilities.clear();
		for (int i = 0; i < positions.size(); i++) {
			visibilities.add(Boolean.TRUE);
		}

		if (positions.size() == 0) {
			return;
		}

		// set the tick label visibility
		int previousPosition = 0;
		String previousLabel = null;
		for (int i = 0; i < positions.size(); i++) {

			// check if there is enough space to draw tick label
			boolean hasSpaceToDraw = true;
			if (i != 0) {
				try {
					hasSpaceToDraw = hasSpaceToDraw(previousPosition, positions.get(i), previousLabel, labels.get(i));
				} catch (java.lang.IndexOutOfBoundsException iobe) {
					hasSpaceToDraw = false;
				}
			}

			// check if the same tick label is repeated
			String currentLabel = labels.get(i);
			boolean isRepeatSameTickAndNotEnd = currentLabel.equals(previousLabel)
					&& (i != 0 && i != positions.size() - 1);

			// check if the tick label value is major
			boolean isMajorTickOrEnd = true;
			if (scale.isLogScaleEnabled()) {
				isMajorTickOrEnd = isMajorTick(values.get(i)) || i == 0 || i == positions.size() - 1;
			}

			if (!hasSpaceToDraw || isRepeatSameTickAndNotEnd || !isMajorTickOrEnd) {
				try {
					visibilities.set(i, Boolean.FALSE);
				} catch (java.lang.IndexOutOfBoundsException iobe) {
					// Ignored or causes SWT error.
				}
			} else {
				previousPosition = positions.get(i);
				previousLabel = currentLabel;
			}
		}
	}

	/**
	 * Checks if the tick label is major (...,0.01,0.1,1,10,100,...).
	 * 
	 * @param tickValue
	 *            the tick label value
	 * @return true if the tick label is major
	 */
	private boolean isMajorTick(double tickValue) {
		if (!scale.isLogScaleEnabled()) {
			return true;
		}

		if (Math.log10(tickValue) % 1 == 0) {
			return true;
		}

		return false;
	}

	/**
	 * Returns the state indicating if there is a space to draw tick label.
	 * 
	 * @param previousPosition
	 *            the previously drawn tick label position.
	 * @param tickLabelPosition
	 *            the tick label position.
	 * @param previousTickLabel
	 *            the previous tick label.
	 * @param tickLabel
	 *            the tick label text
	 * @return true if there is a space to draw tick label
	 */
	private boolean hasSpaceToDraw(int previousPosition, int tickLabelPosition, String previousTickLabel,
			String tickLabel) {

		if (!scale.isHorizontal())
			return true;

		Dimension tickLabelSize = scale.calculateDimension(tickLabel);
		Dimension previousTickLabelSize = scale.calculateDimension(previousTickLabel);
		int interval = tickLabelPosition - previousPosition;
		int textLength = (int) (scale.isHorizontal() ? (tickLabelSize.width / 2.0 + previousTickLabelSize.width / 2.0)
				: tickLabelSize.height);
		boolean noLapOnPrevoius = interval > textLength;

		boolean noLapOnEnd = true;
		if (tickLabelPosition != positions.get(positions.size() - 1)) {
			Dimension endTickLabelSize = scale.calculateDimension(labels.get(labels.size() - 1));
			interval = positions.get(positions.size() - 1) - tickLabelPosition;
			textLength = (int) (scale.isHorizontal() ? (tickLabelSize.width / 2.0 + endTickLabelSize.width / 2.0)
					: tickLabelSize.height);
			noLapOnEnd = interval > textLength;
		}
		return noLapOnPrevoius && noLapOnEnd;
	}

	/**
	 * Update positions and max dimensions of tick labels
	 */
	private void updateLabelPositionsAndMaxDimensions(int length) {
		maxWidth = 0;
		maxHeight = 0;
		for (int i = 0; i < labels.size(); i++) {
			if (visibilities.size() > i && visibilities.get(i) == true) {
				final String text = labels.get(i);
				final Dimension d = scale.calculateDimension(text);
				if (labels.get(0).startsWith("-") && !text.startsWith("-")) {
					d.width += scale.calculateDimension("-").width;
				}
				if (d.width > maxWidth) {
					maxWidth = d.width;
				}
				if (d.height > maxHeight) {
					maxHeight = d.height;
				}
			}
		}

		if (scale.isHorizontal()) { // re-expand length (so labels can flow into
									// margins)
			length += maxWidth;
		} else {
			length += maxHeight;
		}

		for (int i = 0; i < labels.size(); i++) {
			int p = positions.get(i);
			if (visibilities.size() > i && visibilities.get(i) == true) {
				final Dimension d = scale.calculateDimension(labels.get(i));
				if (scale.isHorizontal()) {
					p = (int) Math.ceil(p - d.width * 0.5);
					if (p < 0) {
						p = 0;
					} else if (p + d.width >= length) {
						p = length - 1 - d.width;
					}
				} else {
					p = (int) Math.ceil(length - p - d.height * 0.5);
					if (p < 0) {
						p = 0;
					} else if (p + d.height >= length) {
						p = length - 1 - d.height;
					}
				}
			}
			lPositions.add(p);
		}
	}

	private void updateMinorTickParameters() {
		if (scale.isDateEnabled()) {
			minorTicks = 6;
			minorStepInPixel = (int) (majorStepInPixel / 6.0);
			return;
		}

		if (majorStepInPixel / 5 >= scale.getMinorTickMarkStepHint()) {
			minorTicks = 5;
			minorStepInPixel = (int) (majorStepInPixel / 5.0);
			return;
		}

		if (majorStepInPixel / 4 >= scale.getMinorTickMarkStepHint()) {
			minorTicks = 4;
			minorStepInPixel = (int) (majorStepInPixel / 4.0);
			return;
		}

		minorTicks = 2;
		minorStepInPixel = (int) (majorStepInPixel / 2.0);
	}

	/**
	 * Calculates the value of the first argument raised to the power of the
	 * second argument.
	 * 
	 * @param base
	 *            the base
	 * @param expornent
	 *            the exponent
	 * @return the value <tt>a<sup>b</sup></tt> in <tt>BigDecimal</tt>
	 */
	private BigDecimal pow(double base, int expornent) {
		BigDecimal value;
		if (expornent > 0) {
			value = new BigDecimal(new Double(base).toString()).pow(expornent);
		} else {
			value = BigDecimal.ONE.divide(new BigDecimal(new Double(base).toString()).pow(-expornent));
		}
		return value;
	}

	/**
	 * Gets the grid step.
	 * 
	 * @param lengthInPixels
	 *            scale length in pixels
	 * @param min
	 *            minimum value
	 * @param max
	 *            maximum value
	 * @return rounded value.
	 */
	private BigDecimal getGridStep(int lengthInPixels, double min, double max) {
		if ((int) scale.getMajorGridStep() != 0) {
			return new BigDecimal(scale.getMajorGridStep());
		}

		if (lengthInPixels <= 0) {
			lengthInPixels = 1;
		}
		boolean minBigger = false;
		if (min >= max) {
			if (max == min)
				max++;
			else {
				minBigger = true;
				double swap = min;
				min = max;
				max = swap;
			}
			// throw new IllegalArgumentException("min must be less than max.");
		}

		double length = Math.abs(max - min);
		double majorTickMarkStepHint = scale.getMajorTickMarkStepHint();
		if (majorTickMarkStepHint > lengthInPixels)
			majorTickMarkStepHint = lengthInPixels;
		// if(min > max)
		// majorTickMarkStepHint = -majorTickMarkStepHint;
		double gridStepHint = length / lengthInPixels * majorTickMarkStepHint;

		if (scale.isDateEnabled()) {
			// by default, make the least step to be minutes

			long timeStep;
			if (max - min < 10000) // < 10 sec, step = 1 sec
				timeStep = 1000l;
			else if (max - min < 60000) // < 1 min, step = 10 sec
				timeStep = 10000l;
			else if (max - min < 43200000) // < 12 hour, step = 1 min
				timeStep = 60000l;
			else if (max - min < 604800000) // < 7 days, step = 1 hour
				timeStep = 3600000l;
			else
				timeStep = 86400000l;

			if (scale.getTimeUnit() == Calendar.SECOND) {
				timeStep = 1000l;
			} else if (scale.getTimeUnit() == Calendar.MINUTE) {
				timeStep = 60000l;
			} else if (scale.getTimeUnit() == Calendar.HOUR_OF_DAY) {
				timeStep = 3600000l;
			} else if (scale.getTimeUnit() == Calendar.DATE) {
				timeStep = 86400000l;
			} else if (scale.getTimeUnit() == Calendar.MONTH) {
				timeStep = 30l * 86400000l;
			} else if (scale.getTimeUnit() == Calendar.YEAR) {
				timeStep = 365l * 86400000l;
			}
			double temp = gridStepHint + (timeStep - gridStepHint % timeStep);
			return new BigDecimal(temp);
		}

		double mantissa = gridStepHint;
		int exponent = 0;
		if (mantissa < 1) {
			if (mantissa != 0)
				while (mantissa < 1) {
					mantissa *= 10.0;
					exponent--;
				}
		} else {
			while (mantissa >= 10) {
				mantissa /= 10.0;
				exponent++;
			}
		}

		BigDecimal gridStep;
		if (mantissa > 7.5) {
			gridStep = BigDecimal.TEN.multiply(pow(10, exponent)); // 10.0 * 10
																	// **
																	// exponent
		} else if (mantissa > 3.5) {
			gridStep = new BigDecimal(new Double(5).toString()).multiply(pow( // 5.0
																				// *
																				// 10
																				// **
																				// exponent
					10, exponent));
		} else if (mantissa > 1.5) {
			gridStep = new BigDecimal(new Double(2).toString()).multiply(pow( // 2.0
																				// *
																				// 10
																				// **
																				// exponent
					10, exponent));
		} else {
			gridStep = pow(10, exponent); // 1.0 * 10 ** exponent
		}
		if (minBigger)
			gridStep = gridStep.negate();
		return gridStep;
	}

	private void updateMinorTicks() {
		final int imax = getMajorCount();

		double lp = positions.get(0);
		double cp, dp, tp;
		for (int i = 1; i < imax; i++) {
			cp = positions.get(i);
			dp = cp - lp;
			// add the first minor ticks which is start from min value
			if (i == 1 && dp < majorStepInPixel) {
				tp = cp;
				while ((tp - lp) > minorStepInPixel + 3) {
					tp -= minorStepInPixel;
					minorPositions.add((int) tp);
				}
			} // add the last minor ticks which is end to max value
			else if (i == imax - 1 && dp < majorStepInPixel) {
				tp = lp;
				while ((getPosition(i) - tp) > minorStepInPixel + 3) {
					tp += minorStepInPixel;
					minorPositions.add((int) tp);
				}
			} else { // add regular minor ticks
				for (int j = 0; j < minorTicks; j++) {
					tp = lp + (dp * j) / minorTicks;
					minorPositions.add((int) tp);
				}
			}
			lp = cp;
		}
	}
}
