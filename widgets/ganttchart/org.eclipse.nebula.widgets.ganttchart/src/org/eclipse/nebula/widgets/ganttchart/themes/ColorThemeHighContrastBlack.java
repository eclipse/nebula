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

public class ColorThemeHighContrastBlack implements IColorManager {

    public Color getArrowColor() {
        return ColorCache.getColor(0, 255, 255);
    }

    public Color getReverseArrowColor() {
        return ColorCache.getColor(255, 0, 0);
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
        return ColorCache.getColor(50, 50, 50);
    }

    public Color getWeekDividerLineColor() {
        return ColorCache.getColor(100, 100, 100);
    }

    public Color getPercentageBarColorTop() {
        return ColorCache.getColor(253, 239, 80);
    }

    public Color getPercentageBarColorBottom() {
        return ColorCache.getColor(253, 239, 80);
    }

    public Color getPercentageBarRemainderColorTop() {
        return ColorCache.getBlack();
    }

    public Color getPercentageBarRemainderColorBottom() {
        return ColorCache.getBlack();
    }

    public Color getTextColor() {
        return ColorCache.getColor(53, 228, 61);
    }

    public Color getTodayBackgroundColorTop() {
        return ColorCache.getColor(228, 53, 61);
    }

    public Color getTodayBackgroundColorBottom() {
        return getTodayBackgroundColorTop();
    }

    public Color getTextHeaderBackgroundColorTop() {
        return ColorCache.getBlack();
    }

    public Color getTextHeaderBackgroundColorBottom() {
        return ColorCache.getBlack();
    }

    public Color getTimeHeaderBackgroundColorBottom() {
        return ColorCache.getBlack();
    }

    public Color getTimeHeaderBackgroundColorTop() {
        return getTextHeaderBackgroundColorTop();
    }

    public Color getHourTimeDividerColor() {
        return ColorCache.getColor(170, 170, 170);
    }

    public Color getMonthTimeDividerColor() {
        return getHourTimeDividerColor();
    }

    public Color getWeekTimeDividerColor() {
        return getMonthTimeDividerColor();
    }

    public Color getYearTimeDividerColor() {
        return getHourTimeDividerColor();
    }

    public Color getWeekdayBackgroundColorTop() {
        return ColorCache.getBlack();
    }

    public Color getWeekdayBackgroundColorBottom() {
        return ColorCache.getBlack();
    }

    public Color getWhite() {
        return ColorCache.getWhite();
    }

    public Color getSaturdayBackgroundColorTop() {
        return ColorCache.getColor(80, 80, 80);
    }

    public Color getSaturdayBackgroundColorBottom() {
        return ColorCache.getColor(80, 80, 80);
    }

    public Color getSaturdayTextColor() {
        return getTextColor();
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
        return getTextColor();
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
        return ColorCache.getBlack();
    }

    public Color getZoomBackgroundColorBottom() {
        return ColorCache.getBlack();
    }

    public Color getZoomBorderColor() {
        return ColorCache.getWhite();
    }

    public Color getZoomTextColor() {
        return getTextColor();
    }

    public Color getTooltipBackgroundColor() {
        return getAdvancedTooltipInnerFillBottomColor();
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
        return ColorCache.getColor(80, 80, 80);
    }

    public Color getTodayLineColor() {
        return ColorCache.getColor(0, 255, 0);
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
        return ColorCache.getColor(253, 186, 80);
    }

    public Color getAdvancedTooltipBorderColor() {
        return ColorCache.getWhite();
    }

    public Color getAdvancedTooltipDividerColor() {
        return ColorCache.getWhite();
    }

    public Color getAdvancedTooltipDividerShadowColor() {
        return ColorCache.getWhite();
    }

    public Color getAdvancedTooltipInnerFillBottomColor() {
        return getAdvancedTooltipInnerFillTopColor();
    }

    public Color getAdvancedTooltipInnerFillTopColor() {
        return ColorCache.getColor(7, 254, 246);
    }

    public Color getAdvancedTooltipShadowCornerInnerColor() {
        return getAdvancedTooltipInnerFillTopColor();
    }

    public Color getAdvancedTooltipShadowCornerOuterColor() {
        return getAdvancedTooltipInnerFillTopColor();
    }

    public Color getAdvancedTooltipShadowInnerCornerColor() {
        return getAdvancedTooltipInnerFillTopColor();
    }

    public Color getAdvancedTooltipTextColor() {
        return ColorCache.getWhite();
    }

    public Color getActiveSessionBarColorLeft() {
        return getBlack();
    }

    public Color getActiveSessionBarColorRight() {
        return getBlack();
    }

    public Color getNonActiveSessionBarColorLeft() {
        return ColorCache.getBlack();
    }

    public Color getNonActiveSessionBarColorRight() {
        return ColorCache.getBlack();
    }

    public Color getSessionBarDividerColorLeft() {
        return ColorCache.getBlack();
    }

    public Color getSessionBarDividerColorRight() {
        return ColorCache.getBlack();
    }

    public Color getSelectedDayColorBottom() {
        return getSelectedDayColorTop();
    }

    public Color getSelectedDayColorTop() {
        return ColorCache.getColor(227, 229, 32);
    }

    public Color getSelectedDayHeaderColorBottom() {
        return ColorCache.getColor(230, 230, 230);
    }

    public Color getSelectedDayHeaderColorTop() {
        return ColorCache.getColor(255, 255, 255);
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
