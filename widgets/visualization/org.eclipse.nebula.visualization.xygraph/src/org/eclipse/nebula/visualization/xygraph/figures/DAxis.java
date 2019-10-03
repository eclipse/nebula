/*******************************************************************************
 * Copyright (c) 2017 Diamond Light Source and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.figures;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.visualization.internal.xygraph.utils.LargeNumberUtils;
import org.eclipse.nebula.visualization.xygraph.linearscale.ITicksProvider;
import org.eclipse.nebula.visualization.xygraph.linearscale.LinearScaleTickLabels;
import org.eclipse.nebula.visualization.xygraph.linearscale.LinearScaleTickLabels2;
import org.eclipse.nebula.visualization.xygraph.linearscale.LinearScaleTickMarks;
import org.eclipse.nebula.visualization.xygraph.linearscale.LinearScaleTickMarks2;
import org.eclipse.nebula.visualization.xygraph.linearscale.Range;
import org.eclipse.swt.widgets.Display;

/**
 * The Diamond Light Source implementation of the axis figure.
 *
 * @author Baha El-Kassaby - Diamond Light Source contributions
 */
public class DAxis extends Axis {

	/** if true, then ticks are based on axis dataset indexes */
	private boolean ticksIndexBased;

	private Map<Integer, Format> cachedFormats = new HashMap<Integer, Format>();

	private boolean ticksAtEnds = true;

	/** the user format */
	protected boolean userDefinedFormat = false;

	private boolean axisAutoscaleTight = false;

	/** the default minimum value of log scale range */
	private final static double DEFAULT_LOG_SCALE_MIN = 0.0001d;

	// used if difference between min and max is zero
	private static final double ZERO_RANGE_LOWEST_FRACTION = Math.pow(2, -53);

	/**
	 * Constructor that creates a DAxis with no title
	 */
	public DAxis() {
		this(null, false);
	}

	/**
	 * Constructor
	 *
	 * @param title
	 *            title of the axis
	 * @param yAxis
	 *            true if this is the Y-Axis, false if this is the X-Axis.
	 */
	public DAxis(final String title, final boolean yAxis) {
		super(title, yAxis);
	}

	@Override
	protected LinearScaleTickLabels createLinearScaleTickLabels() {
		return new LinearScaleTickLabels2(this);
	}

	@Override
	protected LinearScaleTickMarks createLinearScaleTickMarks() {
		return new LinearScaleTickMarks2(this);
	}

	/**
	 * Calculate span of a textual form of object in scale's orientation
	 *
	 * @param obj
	 *            object
	 * @return span in pixel
	 */
	public int calculateSpan(Object obj) {
		final Dimension extent = getDimension(obj);
		if (isHorizontal()) {
			return extent.width;
		}
		return extent.height;
	}

	@Override
	public int getMargin() {
		if (isDirty())
			setMargin(getTicksProvider().getHeadMargin());
		return getMargin(false);
	}

	/**
	 * Get scaling for axis in terms of pixels/unit
	 *
	 * @return scaling
	 */
	public double getScaling() {
		int length = getLength();
		int margin = getMargin();
		if (isLogScaleEnabled())
			return (Math.log10(max) - Math.log10(min)) / (length - 2 * margin);
		return (max - min) / (length - 2 * margin);
	}

	@Override
	protected void layout() {
		figureLayout();
		layoutTicks();
		fireRevalidated();
	}

	protected void layoutTicks() {
		updateTick();
		Rectangle area = getClientArea();
		LinearScaleTickLabels tickLabels = getScaleTickLabels();
		LinearScaleTickMarks tickMarks = getScaleTickMarks();
		if (isHorizontal()) {
			if (getTickLabelSide() == LabelSide.Primary) {
				tickLabels.setBounds(
						new Rectangle(area.x, area.y + LinearScaleTickMarks.MAJOR_TICK_LENGTH + SPACE_BTW_MARK_LABEL,
								area.width, area.height - LinearScaleTickMarks.MAJOR_TICK_LENGTH));
				tickMarks.setBounds(area);
			} else {
				tickLabels.setBounds(new Rectangle(area.x,
						area.y + area.height - LinearScaleTickMarks.MAJOR_TICK_LENGTH
								- tickLabels.getTickLabelMaxHeight() - SPACE_BTW_MARK_LABEL,
						area.width, tickLabels.getTickLabelMaxHeight()));
				tickMarks.setBounds(new Rectangle(area.x, area.y + area.height - LinearScaleTickMarks.MAJOR_TICK_LENGTH,
						area.width, LinearScaleTickMarks.MAJOR_TICK_LENGTH));
			}
		} else {
			if (getTickLabelSide() == LabelSide.Primary) {
				tickLabels.setBounds(new Rectangle(
						area.x + area.width - LinearScaleTickMarks.MAJOR_TICK_LENGTH
								- tickLabels.getTickLabelMaxLength() - SPACE_BTW_MARK_LABEL,
						area.y, tickLabels.getTickLabelMaxLength(), area.height));
				tickMarks.setBounds(new Rectangle(
						area.x + area.width - LinearScaleTickMarks.MAJOR_TICK_LENGTH - LinearScaleTickMarks.LINE_WIDTH,
						area.y, LinearScaleTickMarks.MAJOR_TICK_LENGTH + LinearScaleTickMarks.LINE_WIDTH, area.height));
			} else {
				tickLabels.setBounds(new Rectangle(area.x + LinearScaleTickMarks.MAJOR_TICK_LENGTH + SPACE_BTW_MARK_LABEL,
								area.y, tickLabels.getTickLabelMaxLength(), area.height));
				tickMarks.setBounds(new Rectangle(area.x, area.y, LinearScaleTickMarks.MAJOR_TICK_LENGTH, area.height));
			}
		}
	}

	/**
	 * @param isTicksIndexBased
	 *            if true, make ticks based on axis dataset indexes
	 */
	public void setTicksIndexBased(boolean isTicksIndexBased) {
		if (ticksIndexBased != isTicksIndexBased)
			((LinearScaleTickLabels2)getScaleTickLabels()).setTicksIndexBased(isTicksIndexBased);
		ticksIndexBased = isTicksIndexBased;
	}

	/**
	 *
	 * @return True if ticks are index based
	 */
	public boolean isTicksIndexBased() {
		return ticksIndexBased;
	}

	@Override
	public String format(Object obj) {
		return format(obj, 0);
	}

	@Override
	public void updateTick() {
		if (isDirty()) {
			setLength(isHorizontal() ? getClientArea().width : getClientArea().height);
			int l = getTickLength();
			if (l > 0) {
				Range r = getScaleTickLabels().update(l);
				if (r != null && !r.equals(getRange())) {
					setLocalRange(r);
				} else {
					setLocalRange(null);
				}
			}
			setDirty(false);
		}
	}

	/**
	 * Formats the given object as a DateFormat if Date is enabled or as a
	 * DecimalFormat. This is based on an internal format pattern given the
	 * object in parameter. When formatting a date, if minOrMaxDate is true as
	 * well as autoFormat, then the SimpleDateFormat us used to format the
	 * object.
	 *
	 * @param obj
	 *            the object
	 * @param extraDP
	 *            must be non-negative
	 * @return the formatted string
	 */
	public String format(Object obj, int extraDP) {
		if (extraDP < 0) {
			throw new IllegalArgumentException("Number of extra decimal places must be non-negative");
		}
		String formatPattern = getFormatPattern();
		boolean autoFormat = isAutoFormat();
		if (cachedFormats.get(extraDP) == null) {
			if (isDateEnabled()) {
				if (autoFormat || formatPattern == null || formatPattern.equals("")
						|| formatPattern.equals(default_decimal_format)
						|| formatPattern.equals(DEFAULT_ENGINEERING_FORMAT)) {
					// (?) overridden anyway
					formatPattern = DEFAULT_DATE_FORMAT;
					int timeUnit = getTimeUnit();
					double length = Math.abs(max - min);
					// less than a second
					if (length <= 1000 || timeUnit == Calendar.MILLISECOND) {
						formatPattern = "HH:mm:ss.SSS";
					}
					// less than a hour
					else if (length <= 3600000d || timeUnit == Calendar.SECOND) {
						formatPattern = "HH:mm:ss";
					}
					// less than a day
					else if (length <= 86400000d || timeUnit == Calendar.MINUTE) {
						formatPattern = "HH:mm";
					}
					// less than a week
					else if (length <= 604800000d || timeUnit == Calendar.HOUR_OF_DAY) {
						formatPattern = "dd HH:mm";
					}
					// less than a month
					else if (length <= 2592000000d || timeUnit == Calendar.DATE) {
						formatPattern = "MMMMM d";
					}
					// less than a year
					else if (length <= 31536000000d || timeUnit == Calendar.MONTH) {
						formatPattern = "yyyy MMMMM";
					} else {// if (timeUnit == Calendar.YEAR) {
						formatPattern = "yyyy";
					}
					if (formatPattern == null || formatPattern.equals("")) {
						autoFormat = true;
					}
				}
				internalSetFormatPattern(formatPattern);
				cachedFormats.put(extraDP, new SimpleDateFormat(formatPattern));
			} else {
				if (formatPattern == null || formatPattern.isEmpty() || formatPattern.equals(default_decimal_format)
						|| formatPattern.equals(DEFAULT_DATE_FORMAT)) {
					formatPattern = getAutoFormat(min, max);
					internalSetFormatPattern(formatPattern);
					if (formatPattern == null || formatPattern.equals("")) {
						autoFormat = true;
					}
				}

				String ePattern = formatPattern;
				if (extraDP > 0) {
					int e = formatPattern.lastIndexOf('E');
					StringBuilder temp = new StringBuilder(e == -1 ? formatPattern : formatPattern.substring(0, e));
					for (int i = 0; i < extraDP; i++) {
						temp.append('#');
					}
					if (e != -1) {
						temp.append(formatPattern.substring(e));
					}
					ePattern = temp.toString();
				}
				cachedFormats.put(extraDP, new DecimalFormat(ePattern));
			}
			internalSetAutoFormat(autoFormat);
		}
		if (isDateEnabled() && obj instanceof Number) {
			return cachedFormats.get(extraDP).format(new Date(((Number) obj).longValue()));
		}
		return cachedFormats.get(extraDP).format(obj);
	}

	protected String getAutoFormat(double min, double max) {
		ITicksProvider ticks = getTicksProvider();
		if (ticks == null) {
			if ((max != 0 && Math.abs(Math.log10(Math.abs(max))) >= ENGINEERING_LIMIT)
					|| (min != 0 && Math.abs(Math.log10(Math.abs(min))) >= ENGINEERING_LIMIT)) {
				return DEFAULT_ENGINEERING_FORMAT;
			}
			return default_decimal_format;
		}
		return ticks.getDefaultFormatPattern(min, max);
	}

	@Override
	public void setDateEnabled(boolean dateEnabled) {
		cachedFormats.clear();
		super.setDateEnabled(dateEnabled);
	}

	@Override
	public void setFormatPattern(String formatPattern) {
		this.userDefinedFormat = true;
		setFormat(formatPattern);
	}

	private void setFormat(String formatPattern) {
		cachedFormats.clear();
		super.setFormatPattern(formatPattern);
	}

	@Override
	public void setRange(double lower, double upper) {
		internalSetRange(lower, upper, false);
	}

	private void internalSetRange(double lower, double upper, boolean ticksAtEnd) {
		Range old_range = getRange();
		if (old_range.getLower() == lower && old_range.getUpper() == upper) {
			return;
		}

		setTicksAtEnds(ticksAtEnd);

		if (Double.isNaN(lower) || Double.isNaN(upper) || Double.isInfinite(lower) || Double.isInfinite(upper)) {
			throw new IllegalArgumentException("Illegal range: lower=" + lower + ", upper=" + upper);
		}

		if (lower == upper) {
			double delta = (lower == 0 ? 1 : Math.abs(lower));
			double limit = delta * ZERO_RANGE_LOWEST_FRACTION;
			double h;
			double l;
			do { // split ends of range apart equally
				delta /= 2;
				h = upper + delta;
				l = lower - delta;
			} while ((Double.isInfinite(h) || Double.isInfinite(l)) && delta > limit);

			if (Double.isInfinite(h)) { // limit splitting to one side
				lower = l - delta;
			}
			if (Double.isInfinite(l)) {
				upper = h + delta;
			} else {
				upper = h;
				lower = l;
			}
		}
		if (isLogScaleEnabled()) {
			if (upper <= 0)
				upper = DEFAULT_LOG_SCALE_MAX;
			if (lower <= 0)
				lower = DEFAULT_LOG_SCALE_MIN * upper;
		}
		min = lower;
		max = upper;
		internalSetRange(new Range(min, max));
		cachedFormats.clear();
		setDirty(true);
		revalidate();
		repaint();

		fireAxisRangeChanged(old_range, getRange());
	}

	@Override
	public void setAutoFormat(boolean autoFormat) {
		if (autoFormat) {
			cachedFormats.clear();
		}
		super.setAutoFormat(autoFormat);
	}

	@Override
	public void setLogScale(boolean enabled) throws IllegalStateException {
		boolean cur = isLogScaleEnabled();

		if (cur == enabled) {
			return;
		}

		if (enabled) {
			if (min == DEFAULT_MIN && max == DEFAULT_MAX) {
				min = DEFAULT_LOG_SCALE_MIN;
				max = DEFAULT_LOG_SCALE_MAX;
			}
			if (max <= 0) {
				max = DEFAULT_LOG_SCALE_MAX;
			}
			if (min <= 0) {
				min = DEFAULT_LOG_SCALE_MIN * max;
			}
			if (max <= min) {
				max = min + DEFAULT_LOG_SCALE_MAX;
			}
		} else if (min == DEFAULT_LOG_SCALE_MIN && max == DEFAULT_LOG_SCALE_MAX) {
			min = DEFAULT_MIN;
			max = DEFAULT_MAX;
		}
		internalSetLogScaleEnabled(enabled);
		setTicksAtEnds(true);
		internalSetRange(new Range(min, max));
		setDirty(true);
		revalidate();
		repaint();

		final IXYGraph xyGraph = getXYGraph();
		if (cur != enabled && xyGraph != null) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					xyGraph.performAutoScale();
					xyGraph.getPlotArea().layout();
					xyGraph.revalidate();
					xyGraph.repaint();
				}
			});
		}
		setTicksAtEnds(true);
	}

	@Override
	public boolean performAutoScale(boolean force) {
		// Anything to do? Autoscale not enabled nor forced?
		if (getTraceList().size() <= 0 || !(force || getAutoScale())) {
			return false;
		}

		// Get range of data in all traces
		Range range = getTraceDataRange();
		if (range == null) {
			return false;
		}

		double dataMin = range.getLower();
		double dataMax = range.getUpper();

		// Get current axis range, determine how 'different' they are
		double axisMax = getRange().getUpper();
		double axisMin = getRange().getLower();

		if (rangeIsUnchanged(dataMin, dataMax, axisMin, axisMax) || Double.isInfinite(dataMin)
				|| Double.isInfinite(dataMax) || Double.isNaN(dataMin) || Double.isNaN(dataMax)) {
			return false;
		}

		// The threshold is 'shared' between upper and lower range, times by 0.5
		double f = Math.max(LargeNumberUtils.maxMagnitude(axisMin, axisMax), LargeNumberUtils.maxMagnitude(dataMin, dataMax));
		axisMin /= f;
		axisMax /= f;
		dataMin /= f;
		dataMax /= f;
		final double thr = (axisMax - axisMin) * 0.5 * getAutoScaleThreshold();

		boolean lowerChanged = (dataMin - axisMin) < 0 || (dataMin - axisMin) >= thr;
		boolean upperChanged = (axisMax - dataMax) < 0 || (axisMax - dataMax) >= thr;
		// If both the changes are lower than threshold, return
		if (!lowerChanged && !upperChanged) {
			return false;
		}

		// Calculate updated range
		double newMax = upperChanged ? dataMax : axisMax;
		double newMin = lowerChanged ? dataMin : axisMin;

		if (isInverted()) {
			double t = newMin;
			newMin = newMax;
			newMax = t;
		}
		internalSetRange(newMin * f, newMax * f, !axisAutoscaleTight);
		return true;
	}

	/**
	 * Determines if upper or lower data has changed from current axis limits
	 *
	 * @param dataMin
	 *            - min of data in buffer
	 * @param dataMax
	 *            - max of data in buffer
	 * @param axisMin
	 *            - current axis min
	 * @param axisMax
	 *            - current axis max
	 * @return TRUE if data and axis max and min values are equal
	 */
	private boolean rangeIsUnchanged(double dataMin, double dataMax, double axisMin, double axisMax) {
		return Double.doubleToLongBits(dataMin) == Double.doubleToLongBits(axisMin)
				&& Double.doubleToLongBits(dataMax) == Double.doubleToLongBits(axisMax);
	}

	/**
	 *
	 */
	public void clear() {
		for (Iterator<IAxisListener> it = listeners.iterator(); it.hasNext();) {
			if (getTraceList().contains(it.next()))
				it.remove();
		}
		getTraceList().clear();
	}

	/**
	 * @param axisTight
	 *            set whether autoscale sets axis range tight to the data or the
	 *            end of axis is set to the nearest tickmark
	 */
	public void setAxisAutoscaleTight(boolean axisTight) {
		this.axisAutoscaleTight = axisTight;
	}

	/**
	 * @return true if autoscaling axis is tight to displayed data
	 */
	public boolean isAxisAutoscaleTight() {
		return this.axisAutoscaleTight;
	}

	/**
	 * Sets whether ticks at ends of axis are shown
	 *
	 * @param ticksAtEnds
	 */
	public void setTicksAtEnds(boolean ticksAtEnds) {
		this.ticksAtEnds = ticksAtEnds;
	}

	/**
	 * Returns true if ticks at end of axis are shown
	 */
	public boolean hasTicksAtEnds() {
		return ticksAtEnds;
	}

	/**
	 * Sets whether there is a user defined format or not
	 *
	 * @param hasUserDefinedFormat
	 */
	public void setHasUserDefinedFormat(boolean hasUserDefinedFormat) {
		userDefinedFormat = hasUserDefinedFormat;
	}

	/**
	 *
	 * @return true if user format is defined
	 */
	public boolean hasUserDefinedFormat() {
		return userDefinedFormat;
	}
}
