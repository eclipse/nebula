/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.ganttchart;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * A GanttSection is a "box" section of the chart. A section will automatically get a left-side border that shows the name, and the background colors drawn for that section can
 * differ from the rest of the chart. Here's an view of it: <br>
 * <br>
 * ................................................<br>
 * Header<br>
 * ................................................<br>
 * n<br>
 * a &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Section<br>
 * m<br>
 * e ................................................<br>
 * <br>
 */
public class GanttSection implements IFillBackgroundColors {

	private String					name;
	private GanttComposite			parent;
	private List					ganttEvents;
	private Rectangle				bounds;
	private Image					nameImage;
	private boolean					needsNameUpdate;
	private IFillBackgroundColors	fillColorManager;

	private Color					mSaturdayBackgroundColorTop;
	private Color					mSaturdayBackgroundColorBottom;
	private Color					mSundayBackgroundColorTop;
	private Color					mSundayBackgroundColorBottom;
	private Color					mWeekdayBackgroundColorTop;
	private Color					mWeekdayBackgroundColorBottom;
	private Color					mSelectedBackgroundColorTop;
	private Color					mSelectedBackgroundColorBottom;
	private Color					mSelectedBackgroundHeaderColorTop;
	private Color					mSelectedBackgroundHeaderColorBottom;

	// private items
	private Point					mNameExtent;

	private int						mTextOrientation	= SWT.VERTICAL;

	/**
	 * Creates a new GanttSection.
	 * 
	 * @param parent GanttChart
	 * @param name GanttSection name
	 */
	public GanttSection(GanttChart parent, String name) {
		this.name = name;
		this.parent = parent.getGanttComposite();
		this.parent.addSection(this);
		this.ganttEvents = new ArrayList();
		this.fillColorManager = parent.getColorManager();
	}

	/**
	 * Creates a new GanttSection with a fill manager that controls background colors.
	 *  
	 * @param parent GanttChart
	 * @param name GanttSection name
	 * @param fillManager Fill manager
	 */
	public GanttSection(GanttChart parent, String name, IFillBackgroundColors fillManager) {
		this.name = name;
		this.parent = parent.getGanttComposite();
		this.parent.addSection(this);
		this.ganttEvents = new ArrayList();
		this.fillColorManager = fillManager;
	}

	/**
	 * Adds a Gantt Chart item (GanttSection, GanttGroup) to this section.
	 * 
	 * @param event Item to add
	 */
	public void addGanttEvent(IGanttChartItem event) {
		if (!ganttEvents.contains(event))
			ganttEvents.add(event);
	}

	/**
	 * Removes a Gantt Chart item (GanttSection, GanttGroup) from this section.
	 * 
	 * @param event Item to remove
	 */
	public void removeGanttEvent(IGanttChartItem event) {
		ganttEvents.remove(event);
	}

	/**
	 * Returns a list of all IGanttChartItems (GanttEvent and GanttGroup) contained in this section.
	 * 
	 * @return List of items
	 */
	public List getEvents() {
		return ganttEvents;
	}

	/**
	 * Sets the name of this section. This method does not force a redraw.
	 * 
	 * @param name GanttSection name
	 */
	public void setName(String name) {
		this.name = name;
		this.needsNameUpdate = true;
	}

	/**
	 * Returns the name of this section. 
	 * 
	 * @return GanttSection name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the bounds of this GanttSection
	 * 
	 * @return Rectangle
	 */
	public Rectangle getBounds() {
		return bounds;
	}

	
	// note to self: this does not take into account the height the name will take up
	// this method can NOT use the bounds on the events as this method will be called prior to events being drawn and thus have no values for bounds
	/*
	 * int _getEventsHeight(ISettings settings) { int height = 0;
	 * 
	 * if (ganttEvents.size() == 0) return settings.getMinimumSectionHeight();
	 * 
	 * height += settings.getEventsTopSpacer();
	 * 
	 * GanttGroup lastGroup = null; for (int i = 0; i < ganttEvents.size(); i++) { IGanttChartItem event = (IGanttChartItem) ganttEvents.get(i); if (event instanceof GanttEvent) {
	 * if (lastGroup != null) height += settings.getEventSpacer();
	 * 
	 * GanttEvent ge = (GanttEvent) ganttEvents.get(i); if (!ge.isAutomaticRowHeight()) height += ge.getFixedRowHeight(); else height += settings.getEventHeight(); // skip last
	 * event check, we need spacing there too height += settings.getEventSpacer(); lastGroup = null; } else if (event instanceof GanttGroup) { GanttGroup gg = (GanttGroup) event;
	 * 
	 * if (gg != lastGroup) { if (!gg.isAutomaticRowHeight()) { height += gg.getFixedRowHeight(); } else { height += settings.getEventHeight(); //height +=
	 * settings.getEventHeight(); //height += settings.getEventSpacer()/2; }
	 * 
	 * if (i != ganttEvents.size()-1) height += settings.getEventSpacer(); }
	 * 
	 * lastGroup = gg; } }
	 * 
	 * if (height < settings.getMinimumSectionHeight()) height = settings.getMinimumSectionHeight(); // System.err.println(getName() + " " + height + " " + ganttEvents.size());
	 * 
	 * return height; }
	 */

	public Color getSaturdayBackgroundColorBottom() {
		return mSaturdayBackgroundColorBottom == null ? fillColorManager.getSaturdayBackgroundColorBottom() : mSaturdayBackgroundColorBottom;
	}

	public Color getSaturdayBackgroundColorTop() {
		return mSaturdayBackgroundColorTop == null ? fillColorManager.getSaturdayBackgroundColorTop() : mSaturdayBackgroundColorTop;
	}

	public Color getSundayBackgroundColorBottom() {
		return mSundayBackgroundColorBottom == null ? fillColorManager.getSundayBackgroundColorBottom() : mSundayBackgroundColorBottom;
	}

	public Color getSundayBackgroundColorTop() {
		return mSundayBackgroundColorTop == null ? fillColorManager.getSundayBackgroundColorTop() : mSundayBackgroundColorTop;
	}

	public Color getWeekdayBackgroundColorBottom() {
		return mWeekdayBackgroundColorBottom == null ? fillColorManager.getWeekdayBackgroundColorBottom() : mWeekdayBackgroundColorBottom;
	}

	public Color getWeekdayBackgroundColorTop() {
		return mWeekdayBackgroundColorTop == null ? fillColorManager.getWeekdayBackgroundColorTop() : mWeekdayBackgroundColorTop;
	}

	public Color getSelectedDayColorBottom() {
		return mSelectedBackgroundColorBottom == null ? fillColorManager.getSelectedDayColorBottom() : mSelectedBackgroundColorBottom;
	}

	public Color getSelectedDayColorTop() {
		return mSelectedBackgroundColorTop == null ? fillColorManager.getSelectedDayColorTop() : mSelectedBackgroundColorTop;
	}
	
	public Color getSelectedDayHeaderColorBottom() {
		return mSelectedBackgroundHeaderColorBottom == null ? fillColorManager.getSelectedDayHeaderColorBottom() : mSelectedBackgroundHeaderColorBottom;
	}

	public Color getSelectedDayHeaderColorTop() {
		return mSelectedBackgroundHeaderColorTop == null ? fillColorManager.getSelectedDayHeaderColorTop() : mSelectedBackgroundHeaderColorTop;
	}

	public void setSaturdayBackgroundColorTop(Color saturdayBackgroundColorTop) {
		mSaturdayBackgroundColorTop = saturdayBackgroundColorTop;
	}

	public void setSaturdayBackgroundColorBottom(Color saturdayBackgroundColorBottom) {
		mSaturdayBackgroundColorBottom = saturdayBackgroundColorBottom;
	}

	public void setSundayBackgroundColorTop(Color sundayBackgroundColorTop) {
		mSundayBackgroundColorTop = sundayBackgroundColorTop;
	}

	public void setSundayBackgroundColorBottom(Color sundayBackgroundColorBottom) {
		mSundayBackgroundColorBottom = sundayBackgroundColorBottom;
	}

	public void setWeekdayBackgroundColorTop(Color weekdayBackgroundColorTop) {
		mWeekdayBackgroundColorTop = weekdayBackgroundColorTop;
	}

	public void setWeekdayBackgroundColorBottom(Color weekdayBackgroundColorBottom) {
		mWeekdayBackgroundColorBottom = weekdayBackgroundColorBottom;
	}
	
	public void setSelectedBackgroundColorTop(Color selectedBackgroundColorTop) {
		mSelectedBackgroundColorTop = selectedBackgroundColorTop;
	}

	public void setSelectedBackgroundColorBottom(Color selectedBackgroundColorBottom) {
		mSelectedBackgroundColorBottom = selectedBackgroundColorBottom;
	}

	public void setSelectedBackgroundHeaderColorTop(Color selectedBackgroundHeaderColorTop) {
		mSelectedBackgroundHeaderColorTop = selectedBackgroundHeaderColorTop;
	}

	public void setSelectedBackgroundHeaderColorBottom(Color selectedBackgroundHeaderColorBottom) {
		mSelectedBackgroundHeaderColorBottom = selectedBackgroundHeaderColorBottom;
	}

	/**
	 * Returns the text orientation of the section. Default is SWT.VERTICAL.
	 * 
	 * @return Text orientation.
	 */
	public int getTextOrientation() {
		return mTextOrientation;
	}

	/**
	 * Sets the text orientation of the section. One of SWT.HORIZONTAL or SWT.VERTICAL. Default is SWT.VERTICAL.
	 * 
	 * @param textOrientation SWT.VERTICAL or SWT.HORIZONTAL
	 */
	public void setTextOrientation(int textOrientation) {
		mTextOrientation = textOrientation;
	}

	/**
	 * Removes this section from the chart. Do note that all belonging GanttEvents will be orphaned, so you should
	 * probably deal with that post disposal.
	 */
	public void dispose() {
		parent.removeSection(this);
		parent.redraw();
	}
	
	Point getNameExtent() {
		return mNameExtent;
	}

	void setNameExtent(Point extent) {
		this.mNameExtent = extent;
	}

	void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	Image getNameImage() {
		return nameImage;
	}

	void setNameImage(Image nameImage) {
		this.nameImage = nameImage;
		this.needsNameUpdate = false;
	}

	boolean needsNameUpdate() {
		return needsNameUpdate;
	}

	void setNeedsNameUpdate(boolean need) {
		needsNameUpdate = need;
	}

	int getEventsHeight(ISettings settings) {
		if (ganttEvents.size() == 0)
			return settings.getMinimumSectionHeight();

		int height = settings.getEventsTopSpacer();

		for (int i = 0; i < ganttEvents.size(); i++) {
			IGanttChartItem event = (IGanttChartItem) ganttEvents.get(i);

			if (!event.isAutomaticRowHeight())
				height += event.getFixedRowHeight();
			else
				height += settings.getEventHeight();

			if (i != ganttEvents.size() - 1)
				height += settings.getEventSpacer();
		}

		height += settings.getEventsBottomSpacer();

		if (height < settings.getMinimumSectionHeight())
			height = settings.getMinimumSectionHeight();

		return height;
	}

}
