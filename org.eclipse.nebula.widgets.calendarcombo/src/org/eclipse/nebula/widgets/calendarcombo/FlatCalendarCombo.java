package org.eclipse.nebula.widgets.calendarcombo;

import org.eclipse.swt.widgets.Composite;

public class FlatCalendarCombo extends CustomCombo {

	public FlatCalendarCombo(CalendarCombo cc, Composite parent, int style) {
		super(parent, style = CustomCombo.checkStyle(style));
	}
	
}
