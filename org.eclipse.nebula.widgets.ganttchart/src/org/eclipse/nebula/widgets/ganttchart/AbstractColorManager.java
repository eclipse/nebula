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

import org.eclipse.swt.graphics.Color;

public abstract class AbstractColorManager implements IColorManager {

	public Color getArrowColor() {
		return ColorCache.getColor(0, 0, 0);
	}

	public Color getBlack() {
		return ColorCache.getColor(0, 0, 0);
	}

	public Color getDayBackgroundColor() {
		return ColorCache.getColor(200, 200, 200);
	}

	public Color getEventBorderColor() {
		return ColorCache.getColor(0, 0, 0);
	}

	public Color getFadeOffColor1() {
		return ColorCache.getColor(147, 147, 147);
	}

	public Color getFadeOffColor2() {
		return ColorCache.getColor(170, 170, 170);
	}

	public Color getFadeOffColor3() {
		return ColorCache.getColor(230, 230, 230);
	}

	public Color getLineColor() {
		return ColorCache.getColor(220, 220, 220);
	}

	public Color getLineTodayColor() {
		return ColorCache.getColor(100, 100, 100);
	}

	public Color getLineWeekDividerColor() {
		return ColorCache.getColor(100, 100, 100);
	}

	public Color getPercentageBarColor() {
		return ColorCache.getColor(0, 0, 0);
	}

	public Color getTextColor() {
		return ColorCache.getColor(0, 0, 0);
	}

	public Color getTodayBackgroundColor() {
		return ColorCache.getColor(220, 237, 225);
	}

	public Color getTextHeaderBackgroundColor() {
		return ColorCache.getColor(240, 240, 240);
	}

	public Color getWeekdayBackgroundColor() {
		return getWhite();
	}

	public Color getWhite() {
		return ColorCache.getWhite();
	}

	public Color getSaturdayBackgroundColor() {
		return ColorCache.getColor(240, 240, 240);
	}

	public Color getSaturdayColor() {
		return ColorCache.getColor(0, 0, 0);
	}

	public Color getSundayBackgroundColor() {
		return ColorCache.getColor(240, 240, 240);
	}

	public Color getSundayColor() {
		return ColorCache.getColor(0, 0, 0);
	}

	public Color getRevisedEndColor() {
		return ColorCache.getColor(255, 0, 0);
	}

	public Color getRevisedStartColor() {
		return ColorCache.getColor(0, 180, 0);
	}

	public Color getZoomBackgroundColor() {
		return ColorCache.getColor(200, 200, 200);
	}

	public Color getZoomForegroundColor() {
		return getBlack();
	}

	public Color getTooltipBackgroundColor() {
		return ColorCache.getColor(253, 254, 226);
	}

	public Color getTooltipForegroundColor() {
		return getBlack();
	}

	public Color getTooltipForegroundColorFaded() {
		return ColorCache.getColor(100, 100, 100);
	}

	public Color getScopeBorderColor() {
		return getBlack();
	}

	public Color getScopeGradientColorBottom() {
		return ColorCache.getColor(255, 255, 255);
	}

	public Color getScopeGradientColorTop() {
		return ColorCache.getColor(98, 98, 98);
	}
	
	
	
}
