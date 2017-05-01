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
import java.util.Date;
import java.util.List;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.geometry.Dimension;

/**
 * Default scale tick mark algorithm
 *
 */
public class LinearScaleTicks implements ITicksProvider {

	/**
	 * The name of this tick provider
	 */
	public static final String NAME = "DEFAULT";

	private static final String MINUS = "-";

	private static final int TICK_LABEL_GAP = 2;

	/**
	 * Get base^exponent
	 */
	private BigDecimal pow(double base, int exponent) {
		BigDecimal result;
		if (exponent >= 0) {
			result = BigDecimal.valueOf(base).pow(exponent);
		} else {
			result = BigDecimal.ONE.divide(BigDecimal.valueOf(base).pow(-exponent));
		}
		return result;
	}

	/** default: show max label */
	private boolean showMaxLabel = true;
	/** default: show min label */
	private boolean showMinLabel = true;

	/** the array of tick label vales */
	private ArrayList<Double> tickLabelValues;

	/** the array of tick label */
	private ArrayList<String> tickLabels;

	/** the array of tick label position in pixels */
	private ArrayList<Integer> tickLabelPositions;

	/** the array of visibility state of tick label */
	private ArrayList<Boolean> tickLabelVisibilities;

	/** the maximum length of tick labels */
	private int tickLabelMaxLength;

	/** the maximum height of tick labels */
	private int tickLabelMaxHeight;

	private int gridStepInPixel;

	/** the array of minor tick positions in pixels */
	private ArrayList<Integer> minorPositions;

	private IScaleProvider scale;

	/**
	 * constructor
	 *
	 * @param scale
	 */
	public LinearScaleTicks(IScaleProvider scale) {
		this.scale = scale;
		tickLabelValues = new ArrayList<Double>();
		tickLabels = new ArrayList<String>();
		tickLabelPositions = new ArrayList<Integer>();
		tickLabelVisibilities = new ArrayList<Boolean>();
		minorPositions = new ArrayList<Integer>();
	}

	/**
	 * @return the gridStepInPixel
	 */
	public int getGridStepInPixels() {
		return gridStepInPixel;
	}

	/**
	 * @return the tickLabelMaxHeight
	 */
	public int getTickLabelMaxHeight() {
		return tickLabelMaxHeight;
	}

	/**
	 * @return the tickLabelMaxLength
	 */
	public int getTickLabelMaxLength() {
		return tickLabelMaxLength;
	}

	@Override
	public List<Integer> getPositions() {
		return tickLabelPositions;
	}

	@Override
	public List<Boolean> getVisibilities() {
		return tickLabelVisibilities;
	}

	@Override
	public int getPosition(int index) {
		return tickLabelPositions.get(index);
	}

	@Override
	public double getValue(int index) {
		return tickLabelValues.get(index);
	}

	@Override
	public String getLabel(int index) {
		return tickLabels.get(index);
	}

	@Override
	public List<String> getLabels() {
		return tickLabels;
	}

	@Override
	public int getLabelPosition(int index) {
		return tickLabelPositions.get(index);
	}

	@Override
	public boolean isVisible(int index) {
		return tickLabelVisibilities.get(index);
	}

	@Override
	public int getMajorCount() {
		return tickLabels.size();
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
		return tickLabelMaxLength;
	}

	@Override
	public int getMaxHeight() {
		return tickLabelMaxHeight;
	}

	@Override
	public boolean isShowMaxLabel() {
		return showMaxLabel;
	}

	@Override
	public void setShowMaxLabel(boolean showMaxLabel) {
		this.showMaxLabel = showMaxLabel;
	}

	@Override
	public boolean isShowMinLabel() {
		return showMinLabel;
	}

	@Override
	public void setShowMinLabel(boolean showMinLabel) {
		this.showMinLabel = showMinLabel;
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
	private double getGridStep(int lengthInPixels, double min, double max) {
		if ((int) scale.getMajorGridStep() != 0) {
			return scale.getMajorGridStep();
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
		}

		double length = Math.abs(max - min);
		double majorTickMarkStepHint = scale.getMajorTickMarkStepHint();
		if (majorTickMarkStepHint > lengthInPixels)
			majorTickMarkStepHint = lengthInPixels;
		double gridStepHint = length / lengthInPixels * majorTickMarkStepHint;

		if (scale.isDateEnabled()) {
			double temp = getTimeGridStep(min, max, gridStepHint);
			if (minBigger)
				temp = -temp;
			return temp;
		}

		double mantissa = gridStepHint;
		int exp = 0;
		if (mantissa < 1) {
			if (mantissa != 0)
				while (mantissa < 1) {
					mantissa *= 10.0;
					exp--;
				}
		} else {
			while (mantissa >= 10) {
				mantissa /= 10.0;
				exp++;
			}
		}

		double gridStep;
		if (mantissa > 7.5) {
			// 10*10^exp
			gridStep = 10 * Math.pow(10, exp);
		} else if (mantissa > 3.5) {
			// 5*10^exp
			gridStep = 5 * Math.pow(10, exp);
		} else if (mantissa > 1.5) {
			// 2.0*10^exp
			gridStep = 2 * Math.pow(10, exp);
		} else {
			gridStep = Math.pow(10, exp); // 1*10^exponent
		}
		if (minBigger)
			gridStep = -gridStep;
		return gridStep;
	}

	/**
	 * Given min, max and the gridStepHint, returns the time grid step as a
	 * double.
	 *
	 * @param min
	 *            minimum value
	 * @param max
	 *            maximum value
	 * @param gridStepHint
	 * @return time rounded value
	 */
	private double getTimeGridStep(double min, double max, double gridStepHint) {
		// by default, make the least step to be minutes
		long timeStep;
		if (max - min < 1000) // <1 sec, step = 10 ms
			timeStep = 10l;
		else if (max - min < 60000) // < 1 min, step = 1 sec
			timeStep = 1000l;
		else if (max - min < 600000) // < 10 min, step = 10 sec
			timeStep = 10000l;
		else if (max - min < 6400000) // < 2 hour, step = 1 min
			timeStep = 60000l;
		else if (max - min < 43200000) // < 12 hour, step = 10 min
			timeStep = 600000l;
		else if (max - min < 86400000) // < 24 hour, step = 30 min
			timeStep = 1800000l;
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
		return temp;
	}

	/**
	 * If it has enough space to draw the tick label
	 */
	private boolean hasSpaceToDraw(int previousPosition, int tickLabelPosition, String previousTickLabel,
			String tickLabel) {
		Dimension tickLabelSize = FigureUtilities.getTextExtents(tickLabel, scale.getFont());
		Dimension previousTickLabelSize = FigureUtilities.getTextExtents(previousTickLabel, scale.getFont());
		int interval = tickLabelPosition - previousPosition;
		int textLength = (int) (scale.isHorizontal() ? (tickLabelSize.width / 2.0 + previousTickLabelSize.width / 2.0)
				: tickLabelSize.height);
		boolean noLapOnPrevoius = true;

		boolean noLapOnEnd = true;
		// if it is not the end tick label
		if (tickLabelPosition != tickLabelPositions.get(tickLabelPositions.size() - 1)) {
			noLapOnPrevoius = interval > (textLength + TICK_LABEL_GAP);
			Dimension endTickLabelSize = FigureUtilities.getTextExtents(tickLabels.get(tickLabels.size() - 1),
					scale.getFont());
			interval = tickLabelPositions.get(tickLabelPositions.size() - 1) - tickLabelPosition;
			textLength = (int) (scale.isHorizontal() ? (tickLabelSize.width / 2.0 + endTickLabelSize.width / 2.0)
					: tickLabelSize.height);
			noLapOnEnd = interval > textLength + TICK_LABEL_GAP;
		}
		return noLapOnPrevoius && noLapOnEnd;
	}

	/**
	 * Checks if the tick label is major tick. For example: 0.001, 0.01, 0.1, 1,
	 * 10, 100...
	 */
	private boolean isMajorTick(double tickValue) {
		if (!scale.isLogScaleEnabled()) {
			return true;
		}

		double log10 = Math.log10(tickValue);
		if (log10 == Math.rint(log10)) {
			return true;
		}

		return false;
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
	 * Updates tick label for normal scale.
	 * 
	 * @param min
	 * @param max
	 * @param length
	 *            scale tick length (without margin)
	 */
	private void updateTickLabelForLinearScale(double min, double max, int length) {
		double gridStep = getGridStep(length, min, max);
		gridStepInPixel = (int) (length * gridStep / (max - min));
		updateTickLabelForLinearScale(min, max, length, gridStep);
	}

	/**
	 * Updates tick label for normal scale.
	 * 
	 * @param min
	 * @param max
	 * @param length
	 *            scale tick length (without margin)
	 * @param tickStep
	 *            the tick step
	 */
	private void updateTickLabelForLinearScale(double min, double max, int length, double tickStep) {
		boolean minBigger = max < min;

		double firstPosition;

		// make firstPosition as the right most of min based on tickStep
		if (min % tickStep <= 0) {
			firstPosition = min - min % tickStep;
		} else {
			firstPosition = min - min % tickStep + tickStep;
		}

		// the unit time starts from 1:00
		if (scale.isDateEnabled()) {
			double zeroOclock = firstPosition - 3600000;
			if (min < zeroOclock) {
				firstPosition = zeroOclock;
			}
		}

		// add min
		boolean minDateAdded = false;
		if (min > firstPosition == minBigger) {
			tickLabelValues.add(min);
			String lblStr;
			if (isShowMinLabel()) {
				if (scale.isDateEnabled()) {
					Date date = new Date((long) min);
					lblStr = scale.format(date, true);
					minDateAdded = true;
				} else {
					lblStr = scale.format(min);
				}
			} else
				lblStr = "";
			tickLabels.add(lblStr);
			tickLabelPositions.add(scale.getMargin());
		}

		int i = 1;
		for (double b = firstPosition; max >= min ? b < max : b > max; b = firstPosition + i++ * tickStep) {
			if (scale.isDateEnabled()) {
				Date date = new Date((long) b);
				tickLabels.add(scale.format(date, b == firstPosition && !minDateAdded));
			} else {
				tickLabels.add(scale.format(b));
			}
			tickLabelValues.add(b);

			int tickLabelPosition = (int) ((b - min) / (max - min) * length) + scale.getMargin();
			// - LINE_WIDTH;
			tickLabelPositions.add(tickLabelPosition);
		}

		// always add max
		tickLabelValues.add(max);
		String lblStr;
		if (showMaxLabel) {
			if (scale.isDateEnabled()) {
				Date date = new Date((long) max);
				lblStr = scale.format(date, true);
			} else {
				lblStr = scale.format(max);
			}
		} else
			lblStr = "";
		tickLabels.add(lblStr);
		tickLabelPositions.add(scale.getMargin() + length);
		// }

	}

	/**
	 * Updates tick label for log scale.
	 * @param min
	 * @param max
	 * @param length
	 *            the length of scale
	 */
	private void updateTickLabelForLogScale(double min, double max,  int length) {
		if (min <= 0 || max <= 0)
			throw new IllegalArgumentException("the range for log scale must be in positive range");
		boolean minBigger = max < min;

		double logMin = Math.log10(min);
		int minLogDigit = (int) Math.ceil(logMin);
		int maxLogDigit = (int) Math.ceil(Math.log10(max));

		final BigDecimal minDec = BigDecimal.valueOf(min);
		BigDecimal tickStep = pow(10, minLogDigit - 1);
		BigDecimal firstPosition;

		if (minDec.remainder(tickStep).doubleValue() <= 0) {
			firstPosition = minDec.subtract(minDec.remainder(tickStep));
		} else {
			if (minBigger)
				firstPosition = minDec.subtract(minDec.remainder(tickStep));
			else
				firstPosition = minDec.subtract(minDec.remainder(tickStep)).add(tickStep);
		}

		// add min
		boolean minDateAdded = false;
		if (minDec.compareTo(firstPosition) == (minBigger ? 1 : -1)) {
			minDateAdded = addMinMaxTickInfo(min, length, true);
		}

		for (int i = minLogDigit; minBigger ? i >= maxLogDigit : i <= maxLogDigit; i += minBigger ? -1 : 1) {
			// if the range is too big skip minor ticks
			if (Math.abs(maxLogDigit - minLogDigit) > 20) {
				BigDecimal v = pow(10, i);
				if (v.doubleValue() > max)
					break;
				addTickInfo(v, max, logMin, length, i == minLogDigit, minDateAdded);
			} else {
				// must use BigDecimal because it involves equal comparison
				for (BigDecimal j = firstPosition; minBigger ? j.doubleValue() >= pow(10, i - 1).doubleValue()
						: j.doubleValue() <= pow(10, i).doubleValue(); j = minBigger ? j.subtract(tickStep)
								: j.add(tickStep)) {
					if (minBigger ? j.doubleValue() < max : j.doubleValue() > max) {
						break;
					}
					addTickInfo(j, max, logMin, length, j == firstPosition, minDateAdded);
				}
				tickStep = minBigger ? tickStep.divide(pow(10, 1)) : tickStep.multiply(pow(10, 1));
				firstPosition = minBigger ? pow(10, i - 1) : tickStep.add(pow(10, i));
			}
		}

		// add max
		if (minBigger ? max < tickLabelValues.get(tickLabelValues.size() - 1)
				: max > tickLabelValues.get(tickLabelValues.size() - 1)) {
			addMinMaxTickInfo(max, length, false);
		}
	}

	/**
	 * Add the tick labels, positions and values to the corresponding List used
	 * to store them.
	 *
	 * @param d
	 *            BigDecimal value
	 * @param max
	 *            maximum value
	 * @param logMin
	 *            value used to calculate tick label position
	 * @param length
	 *            value used to calculate tick label position
	 * @param isFirstPosition
	 *            needed for date label
	 * @param minDateAdded
	 *            needed for date label
	 */
	private void addTickInfo(BigDecimal d, double max, double logMin, int length, boolean isFirstPosition,
			boolean minDateAdded) {
		if (scale.isDateEnabled()) {
			Date date = new Date((long) d.doubleValue());
			tickLabels.add(scale.format(date, isFirstPosition && !minDateAdded));
		} else {
			tickLabels.add(scale.format(d.doubleValue()));
		}
		int tickLabelPosition = (int) ((Math.log10(d.doubleValue()) - logMin) / (Math.log10(max) - logMin) * length)
				+ scale.getMargin();
		tickLabelPositions.add(tickLabelPosition);
		tickLabelValues.add(d.doubleValue());
	}

	/**
	 * Add the tick labels, positions and values for the min and max case to the
	 * corresponding List used to store them.
	 *
	 * @param value
	 * @param length
	 *            used for max position
	 * @param isMin
	 *            if True, we add the min related info, otherwise the max
	 *            related info
	 * @return minDateAdded false by default, true if min and date are
	 *         enabled
	 */
	private boolean addMinMaxTickInfo(double value, int length, boolean isMin) {
		boolean minDateAdded = false;
		if (isMin) {
			tickLabelValues.add(value);
			BigDecimal minDec = BigDecimal.valueOf(value);
			if (scale.isDateEnabled()) {
				Date date = new Date((long) minDec.doubleValue());
				tickLabels.add(scale.format(date, true));
				minDateAdded = true;
			} else {
				tickLabels.add(scale.format(minDec.doubleValue()));
			}
			tickLabelPositions.add(scale.getMargin());
		} else {
			tickLabelValues.add(value);
			if (scale.isDateEnabled()) {
				Date date = new Date((long) value);
				tickLabels.add(scale.format(date, true));
			} else {
				tickLabels.add(scale.format(value));
			}
			tickLabelPositions.add(scale.getMargin() + length);
		}
		return minDateAdded;
	}

	/**
	 * Gets max length of tick label.
	 */
	private void updateTickLabelMaxLengthAndHeight() {
		int maxLength = 0;
		int maxHeight = 0;
		for (int i = 0; i < tickLabels.size(); i++) {
			if (tickLabelVisibilities.size() > i && tickLabelVisibilities.get(i)) {
				Dimension p = FigureUtilities.getTextExtents(tickLabels.get(i), scale.getFont());
				if (tickLabels.get(0).startsWith(MINUS) && !tickLabels.get(i).startsWith(MINUS)) {
					p.width += FigureUtilities.getTextExtents(MINUS, scale.getFont()).width;
				}
				if (p.width > maxLength) {
					maxLength = p.width;
				}
				if (p.height > maxHeight) {
					maxHeight = p.height;
				}
			}
		}
		tickLabelMaxLength = maxLength;
		tickLabelMaxHeight = maxHeight;
	}

	@Override
	public int getHeadMargin() {
		final Range r = scale.getScaleRange();
		final Dimension l = scale.getDimension(r.getLower());
		final Dimension h = scale.getDimension(r.getUpper());
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
	 * Updates the visibility of tick labels.
	 */
	private void updateTickVisibility() {

		tickLabelVisibilities.clear();

		if (tickLabelPositions.isEmpty())
			return;

		for (int i = 0; i < tickLabelPositions.size(); i++) {
			tickLabelVisibilities.add(Boolean.TRUE);
		}

		// set the tick label visibility
		int previousPosition = 0;
		String previousLabel = null;
		for (int i = 0; i < tickLabelPositions.size(); i++) {
			// check if it has space to draw
			boolean hasSpaceToDraw = true;
			String currentLabel = tickLabels.get(i);
			int currentPosition = tickLabelPositions.get(i);
			if (i != 0) {
				hasSpaceToDraw = hasSpaceToDraw(previousPosition, currentPosition, previousLabel, currentLabel);
			}

			// check if repeated
			boolean isRepeatSameTickAndNotEnd = currentLabel.equals(previousLabel)
					&& (i != 0 && i != tickLabelPositions.size() - 1);

			// check if it is major tick label
			boolean isMajorTickOrEnd = true;
			if (scale.isLogScaleEnabled()) {
				isMajorTickOrEnd = isMajorTick(tickLabelValues.get(i)) || i == 0 || i == tickLabelPositions.size() - 1;
			}

			if (!hasSpaceToDraw || isRepeatSameTickAndNotEnd || !isMajorTickOrEnd) {
				tickLabelVisibilities.set(i, Boolean.FALSE);
			} else {
				previousPosition = currentPosition;
				previousLabel = currentLabel;
			}
		}
	}

	@Override
	public Range update(final double min, final double max, final int length) {
		tickLabels.clear();
		tickLabelValues.clear();
		tickLabelPositions.clear();

		if (scale.isLogScaleEnabled()) {
			updateTickLabelForLogScale(min, max, length);
		} else {
			updateTickLabelForLinearScale(min, max, length);
		}

		updateTickVisibility();
		updateTickLabelMaxLengthAndHeight();
		return null;
	}
}
