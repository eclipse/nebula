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

import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public abstract class AbstractSettings implements ISettings {
	public int getArrowLeftSpacing() {
		return 6;
	}

	public int getArrowTopSpacing() {
		return 4;
	}

	public int getOneDateBoxSize() {
		return 11;
	}

	public int getBoxSpacer() {
		return 6;
	}

	public int getCalendarHeight() {
		return 164;
	}

	public int getCalendarWidth() {
		return 154; //154?
	}
	
	public int getCalendarWidthMacintosh() {
		return 154;
	}

	public int getCalendarHeightMacintosh() {
		return 168;
	}

	public int getDatesLeftMargin() {
		return 15;
	}

	public int getDatesRightMargin() {
		return 17;
	}

	public int getHeaderHeight() {
		return 16;
	}

	public int getHeaderLeftMargin() {
		return 6;
	}

	public int getHeaderRightMargin() {
		return 4;
	}

	public int getHeaderTopMargin() {
		return 4;
	}

	public boolean showCalendarInRightCorner() {
		return true;
	}

	public String getDateFormat() {
		return "MM/dd/yyyy";
	}

	public int getButtonHeight() {
		return 20;
	}

	public int getButtonsHorizontalSpace() {
		return 16;
	}

	public int getCarbonButtonsHorizontalSpace() {
		return 0;
	}

	public int getButtonVerticalSpace() {
		return 133;
	}

	public int getCarbonButtonVerticalSpace() {
		return 138;
	}

	public int getButtonWidth() {
		return 45;
	}
	
	public int getButtonWidthCarbon() {
		return 65;
	}

	public String getNoneText() {
		return "None";
	}

	public String getTodayText() {
		return "Today";
	}

	public String getNoDateSetText() {
		return "";
	}

	public boolean showMonthPickerOnMonthNameMousePress() {
		return true;
	}

	public int getCarbonButtonHeight() {
		return 26;
	}

	public int getCarbonButtonWidth() {
		return 25;
	}

	public int getGTKButtonWidth() {
		return 25;
	}

	public int getWindowsButtonWidth() {
		return 19;
	}

	public Locale getLocale() {
		return Locale.getDefault();
	}

	public Font getCarbonDrawFont() {
		return new Font(Display.getDefault(), "Arial", 12, SWT.NORMAL);
	}
	
}
