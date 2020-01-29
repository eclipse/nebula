/*******************************************************************************
 * Copyright (c) 2013 GODYO Business Solutions AG and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Peter Hermsdorf - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.ganttchart;

import java.util.Calendar;

import org.eclipse.swt.graphics.Rectangle;

/**
 * One Holiday Object represents one Holiday/Spare/Special Day in the GANTT chart.<br>
 * <br>
 * This object can take an additional tooltip which pops up when hovering the mouse over a holiday.<br>
 * <br>
 * The Tooltip is only displayed when the tooltip is not null and {@link ISettings#showHolidayToolTips()} 
 * is configured to return true. 
 * <br>
 * 
 */
public class Holiday {
	
	private final Calendar date;
	private final String tooltip;
	private Rectangle bounds;

	public Holiday(Calendar date) {
		this(date, null);
	}
	
	public Holiday(Calendar date, String tooltip) {
		this.date = date;
		this.tooltip = tooltip;
	}
	
	void resetBounds() {
		bounds = null;
	}
	
	void updateBounds(Rectangle rec) {
		if(bounds == null) {
			bounds = rec;
		}else {
			this.bounds = bounds.union(rec);
		}
	}
	
	Rectangle getBounds() {
		return bounds;
	}
	
	public String getTooltip() {
		return tooltip;
	}
	
	public Calendar getDate() {
		return date;
	}

	public void updateX(int x) {
		bounds.x = x;
	}

	public void updateY(int y) {
		bounds.y = y;
	}

	public boolean hasTooltip() {
		return tooltip != null;
	}
}
