package org.eclipse.nebula.widgets.calendarcombo;

public class CalendarDateParseException extends Exception {

    private static final long serialVersionUID            = 8637307656859889011L;

    /**
     * If entered date has no splitter characters in it
     */
    public static final int   TYPE_NO_SLPITTER_CHAR       = 1;
    
    /**
     * If expected date splitters and entered splitters are not the same in count
     */
    public static final int   TYPE_INSUFFICIENT_SPLITTERS = 2;
    
    /**
     * If a calendar type defined in the Java calendar (such as YYYY) does not compute.
     */
    public static final int   TYPE_UNKNOWN_CALENDAR_TYPE  = 3;
    
    /**
     * If month parsing failed.
     */
    public static final int   TYPE_UNABLE_TO_PARSE_MONTH  = 4;
    
    /**
     * On normal Exception
     */
    public static final int   TYPE_EXCEPTION              = 5;

    private int               _type;

    public CalendarDateParseException(int type) {
        super();
        _type = type;
    }

    public CalendarDateParseException(String message, Throwable cause, int type) {
        super(message, cause);
        _type = type;
    }

    public CalendarDateParseException(String message, int type) {
        super(message);
        _type = type;
    }

    public CalendarDateParseException(Throwable cause, int type) {
        super(cause);
        _type = type;
    }

    /**
     * Returns the date parse error type. One of {@link #TYPE_NO_SLPITTER_CHAR}, {@link #TYPE_INSUFFICIENT_SPLITTERS},
     * {@link #TYPE_UNKNOWN_CALENDAR_TYPE} and {@value #TYPE_UNABLE_TO_PARSE_MONTH}
     * 
     * @return date parse error type
     */
    public int getDateParseErrorType() {
        return _type;
    }

}
