/*******************************************************************************
 * Copyright (c) 2010, 2017 Oak Ridge National Laboratory and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.linearscale;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.visualization.internal.xygraph.utils.LargeNumberUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

/**
 * Linear(straight) scale has the tick labels and tick marks on a straight line.
 * It can be used for any scale based widget, such as 2D plot, chart, graph,
 * thermometer or tank etc. <br>
 * A scale is comprised of Margins, Scale line, tick labels and tick marks which
 * include minor ticks and major ticks. <br>
 * 
 * Margin is half of the label's length(Horizontal Scale) or height(Vertical
 * scale), so that the label can be displayed correctly. So the range must be
 * set before you can get the correct margin.<br>
 * <br>
 * 
 * |Margin|______|______|______|______|______|______|Margin| <br>
 * 
 *
 * @author Xihui Chen
 * @author Baha El-Kassaby, Peter Chang: Diamond Light Source contribution
 * 
 */
public class LinearScale extends AbstractScale implements IScaleProvider {

	/** scale direction */
	public enum Orientation {

		/** the constant to represent horizontal scales */
		HORIZONTAL,

		/** the constant to represent vertical scales */
		VERTICAL
	}

	protected static final int SPACE_BTW_MARK_LABEL = 2;

	/** scale direction, no meaning for round scale */
	private Orientation orientation = Orientation.HORIZONTAL;

	/** the scale tick labels */
	private LinearScaleTickLabels tickLabels;

	/** the scale tick marks */
	private LinearScaleTickMarks tickMarks;

	/** the length of the whole scale */
	private int length;

	private int margin;

	/**
	 * Constructor.
	 */
	public LinearScale() {
		tickLabels = createLinearScaleTickLabels();
		tickMarks = createLinearScaleTickMarks();
		add(tickMarks);
		add(tickLabels);
		// setFont(XYGraphMediaFactory.getInstance().getFont(
		// XYGraphMediaFactory.FONT_ARIAL));

	}

	/**
	 * Creates the linearScaleTickLabel. To be overridden if necessary if
	 * another Axis implementation is used.
	 *
	 */
	protected LinearScaleTickLabels createLinearScaleTickLabels() {
		return new LinearScaleTickLabels(this);
	}

	/**
	 * Creates the LinearScaleTickMarks. To be overridden if necessary if
	 * another Axis implementation is used.
	 *
	 */
	protected LinearScaleTickMarks createLinearScaleTickMarks() {
		return new LinearScaleTickMarks(this);
	}

	private void calcMargin() {
		if (isHorizontal()) {
			margin = (int) Math
					.ceil(Math.max(FigureUtilities.getTextExtents(format(getRange().getLower(), true), getFont()).width,
							FigureUtilities.getTextExtents(format(getRange().getUpper(), true), getFont()).width)
							/ 2.0);
		} else
			margin = (int) Math.ceil(
					Math.max(FigureUtilities.getTextExtents(format(getRange().getLower(), true), getFont()).height,
							FigureUtilities.getTextExtents(format(getRange().getUpper(), true), getFont()).height)
							/ 2.0);
	}

	/**
	 * @return the length of the whole scale (include margin)
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param length
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * If updateTick is True, {@link getMargin()} is used where the tick are
	 * updated, if not, the method returns {@link margin}.
	 *
	 * @param updateTick
	 * @return
	 */
	public int getMargin(boolean updateTick) {
		if(updateTick)
			return getMargin();
		return margin;
	}

	/**
	 * Margin is half of the label's length(Horizontal Scale) or height(Vertical
	 * scale), so that the label can be displayed correctly. So the range and
	 * format pattern must be set correctly before you can get the correct
	 * margin.
	 * 
	 * @return the margin
	 */
	public int getMargin() {
		updateTick();
		return margin;
	}

	/**
	 * @param margin
	 */
	public void setMargin(int margin) {
		this.margin = margin;
	}

	/**
	 * @return the orientation
	 */
	public Orientation getOrientation() {
		return orientation;
	}

	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {

		Dimension size = new Dimension(wHint, hHint);
		LinearScaleTickLabels fakeTickLabels = createLinearScaleTickLabels();

		if (isHorizontal()) {
			// length = wHint;
			fakeTickLabels.update(wHint - 2 * getMargin());
			size.height = fakeTickLabels.getTickLabelMaxHeight() + SPACE_BTW_MARK_LABEL
					+ LinearScaleTickMarks.MAJOR_TICK_LENGTH;
		} else {
			// length = hHint;
			fakeTickLabels.update(hHint - 2 * getMargin());
			size.width = fakeTickLabels.getTickLabelMaxLength() + SPACE_BTW_MARK_LABEL
					+ LinearScaleTickMarks.MAJOR_TICK_LENGTH;

		}

		return size;

	}

	@Override
	public ITicksProvider getTicksProvider() {
		return tickLabels.getTicksProvider();
	}

	/**
	 * Gets the scale tick labels.
	 * 
	 * @return the scale tick labels
	 */
	public LinearScaleTickLabels getScaleTickLabels() {
		return tickLabels;
	}

	/**
	 * Gets the scale tick marks.
	 * 
	 * @return the scale tick marks
	 */
	public LinearScaleTickMarks getScaleTickMarks() {
		return tickMarks;
	}

	/**
	 * @return the length of the tick part (without margin)
	 */
	public int getTickLength() {
		return length - 2 * getMargin();
	}

	/**
	 * Get the position of the value based on scale.
	 * 
	 * @param value
	 *            the value to find its position. Support value out of range.
	 * @param relative
	 *            return the position relative to the left/bottom bound of the
	 *            scale if true. If false, return the absolute position which
	 *            has the scale bounds counted.
	 * @return position in pixels
	 */
	public int getValuePosition(double value, boolean relative) {
		return (int) Math.round(getValuePrecisePosition(value, relative));
	}

	/**
	 * Get the position of the value based on scale.
	 * 
	 * @param value
	 *            the value to find its position. Support value out of range.
	 * @param relative
	 *            return the position relative to the left/bottom bound of the
	 *            scale if true. If false, return the absolute position which
	 *            has the scale bounds counted.
	 * @return position in pixels
	 */
	public double getValuePrecisePosition(double value, boolean relative) {
		if (dirty)
			updateTick();
		// coerce to range
		// value = value < min ? min : (value > max ? max : value);
		Range r = getLocalRange();
		double min = r.getLower();
		double max = r.getUpper();
		double pixelsToStart = 0;
		double l = length - 2 * margin;
		if (isLogScaleEnabled()) {
			if (value <= 0) {
				pixelsToStart = margin;
			} else {
				pixelsToStart = ((Math.log10(value) - Math.log10(min)) / (Math.log10(max) - Math.log10(min)) * l) + margin;
			}
		} else {
			double f = LargeNumberUtils.maxMagnitude(min, max);
			max /= f;
			min /= f;
			double t = max - min;
			pixelsToStart = ((value / f - min) / t * l) + margin;
		}

		if (relative) {
			return orientation == Orientation.HORIZONTAL ? pixelsToStart : length - pixelsToStart;
		} else {
			return orientation == Orientation.HORIZONTAL ? pixelsToStart + bounds.x : length - pixelsToStart + bounds.y;
		}
	}

	/**
	 * Get the corresponding value on the position of the scale.
	 * 
	 * @param position
	 * @param relative
	 *            if true the position is relative to the left/bottom bound of the
	 *            scale; if false it is the absolute position.
	 * @return the value corresponding to the position.
	 */
	public double getPositionValue(int position, boolean relative) {
		return getPositionValue((double) position, relative);
	}

	/**
	 * Get the corresponding value on the position of the scale.
	 * 
	 * @param position
	 * @param relative
	 *            if true the position is relative to the left/bottom bound of the
	 *            scale; if false it is the absolute position.
	 * @return the value corresponding to the position.
	 */
	public double getPositionValue(double position, boolean relative) {
		updateTick();
		// coerce to range
		double pixelsToStart;
		double value;
		if (relative) {
			pixelsToStart = isHorizontal() ? position : length - position;
		} else {
			pixelsToStart = isHorizontal() ? position - bounds.x : length + bounds.y - position;
		}

		Range r = getLocalRange();
		double min = r.getLower();
		double max = r.getUpper();
		double l = length - 2 * margin;
		if (isLogScaleEnabled()) {
			value = Math.pow(10, (pixelsToStart - margin) * (Math.log10(max) - Math.log10(min)) / l
					+ Math.log10(min));
		} else {
			double f = LargeNumberUtils.maxMagnitude(min, max);
			max /= f;
			min /= f;
			double t = max - min;
			value = ((pixelsToStart - margin) / l * t + min) * f;
		}

		return value;
	}

	@Override
	public boolean isHorizontal() {
		return orientation == Orientation.HORIZONTAL;
	}

	/**
	 * shows the maximum value of the range as a label
	 * 
	 * @return true (default) if shown
	 */
	public boolean isShowMaxLabel() {
		return tickLabels.isShowMaxLabel();
	}

	/**
	 * shows the minimum value of the range as a label
	 * 
	 * @return true (default) if shown
	 */
	public boolean isShowMinLabel() {
		return tickLabels.isShowMinLabel();
	}

	@Override
	protected void layout() {
		super.layout();
		updateTick();
		Rectangle area = getClientArea();
		if (isHorizontal() && getTickLabelSide() == LabelSide.Primary) {
			tickLabels.setBounds(
					new Rectangle(area.x, area.y + LinearScaleTickMarks.MAJOR_TICK_LENGTH + SPACE_BTW_MARK_LABEL,
							area.width, area.height - LinearScaleTickMarks.MAJOR_TICK_LENGTH));
			tickMarks.setBounds(area);
		} else if (isHorizontal() && getTickLabelSide() == LabelSide.Secondary) {
			tickLabels.setBounds(new Rectangle(
					area.x, area.y + area.height - LinearScaleTickMarks.MAJOR_TICK_LENGTH
							- tickLabels.getTickLabelMaxHeight() - SPACE_BTW_MARK_LABEL,
					area.width, tickLabels.getTickLabelMaxHeight()));
			tickMarks.setBounds(new Rectangle(area.x, area.y + area.height - LinearScaleTickMarks.MAJOR_TICK_LENGTH,
					area.width, LinearScaleTickMarks.MAJOR_TICK_LENGTH));
		} else if (getTickLabelSide() == LabelSide.Primary) {
			tickLabels.setBounds(new Rectangle(
							area.x + area.width - LinearScaleTickMarks.MAJOR_TICK_LENGTH
									- tickLabels.getTickLabelMaxLength() - SPACE_BTW_MARK_LABEL,
							area.y, tickLabels.getTickLabelMaxLength(), area.height));
			tickMarks.setBounds(new Rectangle(area.x + area.width - LinearScaleTickMarks.MAJOR_TICK_LENGTH, area.y,
					LinearScaleTickMarks.MAJOR_TICK_LENGTH, area.height));
		} else {
			tickLabels.setBounds(new Rectangle(area.x + LinearScaleTickMarks.MAJOR_TICK_LENGTH + SPACE_BTW_MARK_LABEL,
					area.y, tickLabels.getTickLabelMaxLength(), area.height));
			tickMarks.setBounds(new Rectangle(area.x, area.y, LinearScaleTickMarks.MAJOR_TICK_LENGTH, area.height));
		}
	}

	/**
	 * Simple call of {@link void org.eclipse.draw2d.Figure.layout()}
	 */
	public void figureLayout() {
		super.layout();
	}

	@Override
	public void setBounds(Rectangle rect) {
		if (!bounds.equals(rect)) {
			setDirty(true);
			if (isHorizontal()) {
				length = rect.width - getInsets().getWidth();
			} else {
				length = rect.height - getInsets().getHeight();
			}
		}
		super.setBounds(rect);

	}

	/*
	 * @see IAxisTick#setFont(Font)
	 */
	@Override
	public void setFont(Font font) {
		if (font != null && font.isDisposed()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		tickLabels.setFont(font);
		super.setFont(font);

	}

	/*
	 * @see IAxisTick#setForeground(Color)
	 */
	@Override
	public void setForegroundColor(Color color) {
		tickMarks.setForegroundColor(color);
		tickLabels.setForegroundColor(color);
		super.setForegroundColor(color);
	}

	/**
	 * sets the orientation
	 * 
	 * @param orientation
	 *            the orientation to set
	 */
	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
		setDirty(true);
		revalidate();

	}

	/**
	 * sets the visibility of the maximum label
	 * 
	 * @param b
	 *            show maximal value as a label
	 */
	public void setShowMaxLabel(boolean b) {
		tickLabels.setShowMaxLabel(b);

	}

	/**
	 * sets the visibility of the minimum label
	 * 
	 * @param b
	 *            show minimum value as a label
	 */
	public void setShowMinLabel(boolean b) {
		tickLabels.setShowMinLabel(b);

	}

	private Range localRange = null;

	/**
	 * @return range used for axis (not range given by data)
	 */
	public Range getLocalRange() {
		return localRange == null ? super.getRange() : localRange;
	}

	/**
	 * @param localRange
	 */
	public void setLocalRange(Range localRange) {
		this.localRange = localRange;
	}

	/**
	 * Updates the tick, recalculate all parameters, such as margin, length...
	 */
	@Override
	public void updateTick() {
		if (isDirty()) {
			calcMargin();
			setDirty(false);
			length = isHorizontal() ? getClientArea().width : getClientArea().height;
			int l = length - 2 * margin;
			if (l > 0) {
				tickLabels.update(l);
			}
		}
	}

	@Override
	protected boolean useLocalCoordinates() {
		return true;
	}

	@Override
	public Range getScaleRange() {
		return getRange();
	}

	/**
	 * Calculate dimension of a textual form of object
	 *
	 * @param obj
	 *            object
	 * @return dimension
	 */
	@Override
	public Dimension getDimension(Object obj) {
		if (obj == null)
			return new Dimension();
		if (obj instanceof String)
			return FigureUtilities.getTextExtents((String) obj, getFont());
		return FigureUtilities.getTextExtents(format(obj), getFont());
	}

	@Override
	public boolean isPrimary() {
		return getTickLabelSide() == LabelSide.Primary;
	}

	/**
	 * Override to provide custom axis labels.
	 */
	@Override
	public double getLabel(double value) {
		return value;
	}

	@Override
	public boolean isLabelCustomised() {
		return false;
	}

	@Override
	public boolean hasTicksAtEnds() {
		return true;
	}
}
