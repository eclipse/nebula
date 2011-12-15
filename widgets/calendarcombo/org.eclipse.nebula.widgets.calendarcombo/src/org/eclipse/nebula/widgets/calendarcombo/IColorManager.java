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

import org.eclipse.swt.graphics.Color;

public interface IColorManager {

    public static Color calendarBlueHeader = ColorCache.getColor(158, 190, 245);
    public static Color calendarBlueBorder = ColorCache.getColor(127, 157, 185);

    public static Color calendarOliveHeader = ColorCache.getColor(217, 217, 167);
    public static Color calendarOliveBorder = ColorCache.getColor(164, 185, 127);

    public static Color calendarSilverHeader = ColorCache.getColor(215, 215, 229);
    public static Color calendarSilverBorder = ColorCache.getColor(157, 157, 161);

    public static final int SKIN_NONE = -1;
    public static final int SKIN_AUTO_DETECT = 0; // auto detect, but only blue olive or silver
    public static final int SKIN_BLUE = 1;
    public static final int SKIN_OLIVE = 2;
    public static final int SKIN_SILVER = 3;
    //public static final int SKIN_OFFICE_2007 = 4;
    
	public Color getCalendarBorderColor();
	public Color getCalendarHeaderColor();
	public Color getLineColor();
	public Color getSelectedDayColor();
	public Color getSelectedDayBorderColor();
	public Color getCalendarBackgroundColor();
	public Color getDisabledDayForegroundColor();
	public Color getPreviousAndNextMonthForegroundColor();
	public Color getTextColor();
	public Color getArrowColor();
}
