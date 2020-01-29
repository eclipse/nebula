/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.ganttchart.themes;

import org.eclipse.nebula.widgets.ganttchart.ColorCache;
import org.eclipse.nebula.widgets.ganttchart.GanttSection;
import org.eclipse.nebula.widgets.ganttchart.IColorManager;
import org.eclipse.swt.graphics.Color;

public class ColorThemeGrayBlue implements IColorManager {

	public Color getArrowColor() {
		return ColorCache.getColor(0, 0, 0);
	}

	public Color getReverseArrowColor() {
		return ColorCache.getColor(128, 0, 0);
	}

	public Color getBlack() {
		return ColorCache.getColor(0, 0, 0);
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

	public Color getWeekDividerLineColor() {
		return ColorCache.getColor(75, 107, 143);
	}

	public Color getPercentageBarColorTop() {
		return ColorCache.getColor(84, 84, 84);
	}
		
	public Color getPercentageBarColorBottom() {
		return ColorCache.getColor(0, 0, 0);		
	}

	public Color getPercentageBarRemainderColorTop() {
		return ColorCache.getColor(200, 200, 200);	
	}

	public Color getPercentageBarRemainderColorBottom() {
		return ColorCache.getColor(111, 111, 111);
	}

	public Color getTextColor() {
		return ColorCache.getColor(0, 0, 0);
	}

	public Color getTodayBackgroundColorTop() {
		return ColorCache.getColor(217, 219, 189);
	}
	
	public Color getTodayBackgroundColorBottom() {
		return ColorCache.getColor(225, 232, 228);
	}

	public Color getTextHeaderBackgroundColorTop() {
		return ColorCache.getColor(173, 187, 210);
	}
	
	public Color getTextHeaderBackgroundColorBottom() {
		return ColorCache.getColor(200, 209, 226);
	}

	public Color getTimeHeaderBackgroundColorBottom() {
		return ColorCache.getColor(173, 187, 210);
	}

	public Color getTimeHeaderBackgroundColorTop() {
		return ColorCache.getColor(200, 209, 226);
	}
	
	public Color getHourTimeDividerColor() {
		return ColorCache.getColor(166, 183, 207);
	}

	public Color getMonthTimeDividerColor() {
		return ColorCache.getColor(166, 183, 207);
	}

	public Color getWeekTimeDividerColor() {
		return getMonthTimeDividerColor();
	}

	public Color getYearTimeDividerColor() {
		return getHourTimeDividerColor();
	}

	public Color getWeekdayBackgroundColorTop() {
		return ColorCache.getColor(236, 239, 244);
	}
		
	public Color getWeekdayBackgroundColorBottom() {
		return getWhite();
	}

	public Color getWhite() {
		return ColorCache.getWhite();
	}

	public Color getSaturdayBackgroundColorTop() {
		return ColorCache.getColor(224, 228, 235);
	}
	
	public Color getSaturdayBackgroundColorBottom() {
		return ColorCache.getColor(224, 228, 235);
	}

	public Color getSaturdayTextColor() {
		return ColorCache.getColor(92, 75, 29);
	}

	public Color getSundayBackgroundColorTop() {
		return getSaturdayBackgroundColorTop();
	}

	public Color getSundayBackgroundColorBottom() {
		return getSaturdayBackgroundColorBottom();
	}
		
	public Color getHolidayBackgroundColorTop() {
		return getSaturdayBackgroundColorTop();
	}

	public Color getHolidayBackgroundColorBottom() {
		return getSaturdayBackgroundColorBottom();
	}
		
	public Color getWeekdayTextColor() {
		return getBlack();
	}

	public Color getSundayTextColor() {
		return getSaturdayTextColor();
	}

	public Color getRevisedEndColor() {
		return ColorCache.getColor(255, 0, 0);
	}

	public Color getRevisedStartColor() {
		return ColorCache.getColor(0, 180, 0);
	}

	public Color getZoomBackgroundColorTop() {
		return ColorCache.getColor(131, 131, 131);
	}
		
	public Color getZoomBackgroundColorBottom() {
		return ColorCache.getColor(71, 74, 62);
	}

	public Color getZoomBorderColor() {
		return ColorCache.getWhite();
	}
	
	public Color getZoomTextColor() {
		return ColorCache.getWhite();
	}

	public Color getTooltipBackgroundColor() {
		return ColorCache.getColor(217, 229, 242);
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

	public Color getTopHorizontalLinesColor() {
		return ColorCache.getColor(138, 175, 228);
	}

	public Color getTodayLineColor() {
		return ColorCache.getColor(253, 145, 80);
	}

	public int getTodayLineAlpha() {
		return 125;
	}

	public int getWeekDividerAlpha() {
		return 50;
	}

	public boolean useAlphaDrawing() {
		return false;
	}

	public boolean useAlphaDrawingOn3DEventDropShadows() {
		return true;
	}

	public Color getTickMarkColor() {
		return ColorCache.getColor(170, 164, 152);
	}

	public Color getAdvancedTooltipBorderColor() {
		return ColorCache.getColor(118, 118, 118);
	}

	public Color getAdvancedTooltipDividerColor() {
		return ColorCache.getColor(158, 187, 221);
	}

	public Color getAdvancedTooltipDividerShadowColor() {
		return ColorCache.getColor(255, 255, 255);
	}

	public Color getAdvancedTooltipInnerFillBottomColor() {
		return ColorCache.getColor(204, 217, 234);
	}

	public Color getAdvancedTooltipInnerFillTopColor() {
		return ColorCache.getColor(255, 251, 252);
	}

	public Color getAdvancedTooltipShadowCornerInnerColor() {
		return ColorCache.getColor(131, 131, 131);
	}

	public Color getAdvancedTooltipShadowCornerOuterColor() {
		return ColorCache.getColor(148, 148, 148);
	}

	public Color getAdvancedTooltipShadowInnerCornerColor() {
		return ColorCache.getColor(186, 186, 186);
	}

	public Color getAdvancedTooltipTextColor() {
		return ColorCache.getColor(79, 77, 78);
	}

	public Color getActiveSessionBarColorLeft() {
		return getTimeHeaderBackgroundColorTop();
	}

	public Color getActiveSessionBarColorRight() {
		return getTimeHeaderBackgroundColorBottom();
	}

	public Color getNonActiveSessionBarColorLeft() {
		return ColorCache.getColor(185, 195, 219);
	}

	public Color getNonActiveSessionBarColorRight() {
		return ColorCache.getColor(199, 208, 225);
	}

	public Color getSessionBarDividerColorLeft() {
		return ColorCache.getColor(176, 188, 213);
	}

	public Color getSessionBarDividerColorRight() {
		return ColorCache.getColor(202, 210, 225);
	}	
	
	public Color getSelectedDayColorBottom() {
		return ColorCache.getColor(200, 212, 222);
	}

	public Color getSelectedDayColorTop() {
		return ColorCache.getColor(173, 192, 207);
	}

	public Color getSelectedDayHeaderColorBottom() {
		return ColorCache.getColor(204, 216, 228);
	}

	public Color getSelectedDayHeaderColorTop() {
		return ColorCache.getColor(222, 236, 250);
	}

    public Color getPhaseHeaderBackgroundColorBottom() {
        return getTimeHeaderBackgroundColorBottom();
    }

    public Color getPhaseHeaderBackgroundColorTop() {
        return getTimeHeaderBackgroundColorTop();
    }
	
    public Color getOriginalLocationColor() {
        return ColorCache.getColor(253, 145, 80);
    }   	

    public Color getVerticalInsertMarkerColor() {   
        return getOriginalLocationColor();
    }

	public Color getPeriodLineColor() {
        return ColorCache.getColor(255, 0, 0);
	}

	public Color getSectionDetailAreaForegroundColor(GanttSection section) {
		return getWeekdayBackgroundColorTop();
	}
	
	public Color getSectionDetailAreaBackgroundColor(GanttSection section) {
		return getWeekdayBackgroundColorBottom();
	}
	
	public boolean drawSectionDetailGradientTopDown() {
		return true;
	}

}
