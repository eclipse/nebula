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

package org.eclipse.nebula.widgets.calendarcombo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public abstract class AbstractColorManager implements IColorManager {

	private int mTheme;
	
	public AbstractColorManager() {
		this(SKIN_SILVER);
	}
	
	public AbstractColorManager(int theme) {
		mTheme = theme;
		
		if (mTheme == SKIN_AUTO_DETECT)
			autoDetect();
	}

	public Color getCalendarHeaderColor() {
		switch (mTheme) {
			case SKIN_BLUE:
				return calendarBlueHeader;
			case SKIN_OLIVE:
				return calendarOliveHeader;
			case SKIN_SILVER:
				return calendarSilverHeader;
		}

		return null;
	}

	public Color getCalendarBorderColor() {
		switch (mTheme) {
			case SKIN_BLUE:
				return calendarBlueBorder;
			case SKIN_OLIVE:
				return calendarOliveBorder;
			case SKIN_SILVER:
				return calendarSilverBorder;
		}

		return null;
	}
	
	public Color getCalendarBackgroundColor() {
		return ColorCache.getWhite();
	}

	public Color getLineColor() {
		return ColorCache.getColor(172, 168, 153);
	}

	public Color getSelectedDayBorderColor() {
		return ColorCache.getColor(187, 85, 3);
	}

	public Color getSelectedDayColor() {
		return ColorCache.getColor(251, 230, 148);
	}

	public Color getTextColor() {
		return ColorCache.getBlack();
	}
	
	public Color getArrowColor() {
		return ColorCache.getBlack();
	}
	
	public Color getPreviousAndNextMonthForegroundColor() {
		return ColorCache.getColor(172, 168, 153);
	}
	
	public Color getDisabledDayForegroundColor() {
		return ColorCache.getColor(172, 168, 153);
	}

	public void setTheme(int theme) {
		mTheme = theme;
	}

	public int getTheme() {
		return mTheme;
	}
	
	private void autoDetect() {
		RGB bgGradient = Display.getDefault().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT).getRGB();

		int r = bgGradient.red;
		int g = bgGradient.green;
		int b = bgGradient.blue;

		int style = SKIN_NONE;

		if (r == 200 && g == 200 && b == 200) {
			style = SKIN_SILVER;
		} else if (r == 198 && g == 210 && b == 162) {
			style = SKIN_OLIVE;
		} else if (r == 61 && g == 149 && b == 255) {
			style = SKIN_BLUE;
		}

		if (style == SKIN_NONE) {
			style = SKIN_BLUE;
		}

		mTheme = style;
	}
	
	public void dispose() {
	}

}
