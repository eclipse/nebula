package org.eclipse.nebula.widgets.calendarcombo;

public interface IDateParseExceptionListener {

    /**
     * This is notified when there is a {@link CalendarDateParseException} thrown in the CalendarCombo due
     * to a bad date.
     * 
     * @param dateParseException {@link CalendarDateParseException} that is thrown.
     */
    public void parseExceptionThrown(CalendarDateParseException dateParseException);
}
