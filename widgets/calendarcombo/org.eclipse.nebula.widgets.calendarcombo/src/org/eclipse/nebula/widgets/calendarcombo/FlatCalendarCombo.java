package org.eclipse.nebula.widgets.calendarcombo;

import org.eclipse.swt.widgets.Composite;

public class FlatCalendarCombo extends CustomCombo {

	public FlatCalendarCombo(CalendarCombo cc, Composite parent, int style) {
		super(parent, style = CustomCombo.checkStyle(style));
	}

	protected void dropDown(boolean drop) {
		// flat combos on mac don't like empty lists, so we override this to do nothing, which solves the issue, strangely enough
		if (CalendarCombo.OS_CARBON)
			return;
		
		super.dropDown(drop);
	}
	
	
}
