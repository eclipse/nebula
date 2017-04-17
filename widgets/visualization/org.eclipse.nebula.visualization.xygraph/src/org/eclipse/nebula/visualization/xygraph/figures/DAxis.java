/*******************************************************************************
 * Copyright (c) 2017 Baha El-Kassaby and others.
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
import java.util.Map;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.visualization.xygraph.linearscale.ITicksProvider;
import org.eclipse.nebula.visualization.xygraph.linearscale.LinearScaleTickLabels;
import org.eclipse.nebula.visualization.xygraph.linearscale.LinearScaleTickLabels2;
import org.eclipse.nebula.visualization.xygraph.linearscale.LinearScaleTickMarks;
import org.eclipse.nebula.visualization.xygraph.linearscale.Range;

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

	private boolean forceRange;

	/** the user format */
	protected boolean userDefinedFormat = false;

	/**
	 * used if difference between min and max is too small
	 */
	private static final double ZERO_RANGE_FRACTION = 0.125;


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
			margin = getTicksProvider().getHeadMargin();
		return margin;
	}

	/**
	 * Get scaling for axis in terms of pixels/unit
	 *
	 * @return scaling
	 */
	public double getScaling() {
		if (isLogScaleEnabled())
			return (Math.log10(max) - Math.log10(min)) / (length - 2 * margin);
		return (max - min) / (length - 2 * margin);
	}

	@Override
	protected void layout() {
		super.layout();
		layoutTicks();
	}

	protected void layoutTicks() {
		updateTick();
		Rectangle area = getClientArea();
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
			((LinearScaleTickLabels2)tickLabels).setTicksIndexBased(isTicksIndexBased);
		ticksIndexBased = isTicksIndexBased;
	}

	@Override
	public String format(Object obj) {
		return format(obj, 0);
	}

	@Override
	public void updateTick() {
		if (isDirty()) {
			length = isHorizontal() ? getClientArea().width : getClientArea().height;
			if (length > 2 * getMargin()) {
				Range r = tickLabels.update(length - 2 * getMargin());
				if (r != null && !r.equals(range) && !forceRange) {
					localRange = r;
				} else {
					localRange = null;
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
		if (cachedFormats.get(extraDP) == null) {
			if (isDateEnabled()) {
				if (autoFormat || formatPattern == null || formatPattern.equals("")
						|| formatPattern.equals(default_decimal_format)
						|| formatPattern.equals(DEFAULT_ENGINEERING_FORMAT)) {
					// (?) overridden anyway
					formatPattern = DEFAULT_DATE_FORMAT;
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
				cachedFormats.put(extraDP, new SimpleDateFormat(formatPattern));
			} else {
				if (formatPattern == null || formatPattern.isEmpty() || formatPattern.equals(default_decimal_format)
						|| formatPattern.equals(DEFAULT_DATE_FORMAT)) {
					formatPattern = getAutoFormat(min, max);
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
		}

		if (isDateEnabled() && obj instanceof Number) {
			return cachedFormats.get(extraDP).format(new Date(((Number) obj).longValue()));
		}
		return cachedFormats.get(extraDP).format(obj);
	}

	private String getAutoFormat(double min, double max) {
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
		this.dateEnabled = dateEnabled;
		cachedFormats.clear();
		setDirty(true);
		revalidate();
	}

	@Override
	public void setFormatPattern(String formatPattern) {
		this.userDefinedFormat = true;
		setFormat(formatPattern);
	}

	private void setFormat(String formatPattern) {
		try {
			new DecimalFormat(formatPattern);
		} catch (NullPointerException e) {
			throw e;
		} catch (IllegalArgumentException e) {
			throw e;
		}
		cachedFormats.clear();
		this.formatPattern = formatPattern;

		autoFormat = false;
		setDirty(true);
		revalidate();
		repaint();
	}

	@Override
	public void setRange(double lower, double upper) {
		if (Double.isNaN(lower) || Double.isNaN(upper) || Double.isInfinite(lower) || Double.isInfinite(upper)
				|| Double.isInfinite(upper - lower)) {
			throw new IllegalArgumentException("Illegal range: lower=" + lower + ", upper=" + upper);
		}

		forceRange = lower == upper;
		if (forceRange) {
			final double delta = (lower == 0 ? 1 : Math.abs(lower)) * ZERO_RANGE_FRACTION;
			upper += delta;
			lower -= delta;
			if (Double.isInfinite(upper))
				throw new IllegalArgumentException("Illegal range: lower=" + lower + ", upper=" + upper);
		}

		if (logScaleEnabled) {
			if (upper <= 0)
				upper = DEFAULT_LOG_SCALE_MAX;
			if (lower <= 0)
				lower = DEFAULT_LOG_SCALE_MIN * upper;
		}

		min = lower;
		max = upper;
		range = new Range(min, max);
		cachedFormats.clear();
		setDirty(true);
		revalidate();
		repaint();
	}

	@Override
	public void setAutoFormat(boolean autoFormat) {
		this.autoFormat = autoFormat;
		if (autoFormat) {
			formatPattern = null;
			cachedFormats.clear();
			setRange(getRange());
			format(0);
		}
	}

	@Override
	public void setLogScale(boolean enabled) throws IllegalStateException {
		super.setLogScale(enabled);
		setTicksAtEnds(true);
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