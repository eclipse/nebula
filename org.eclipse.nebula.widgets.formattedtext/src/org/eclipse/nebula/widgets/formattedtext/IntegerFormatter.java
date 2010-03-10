package org.eclipse.nebula.widgets.formattedtext;

import java.util.Locale;

/**
 * This class provides formatting of <code>Integer</code> values in a
 * <code>FormattedText</code>.<p>
 * 
 * NumberFormatter returns different numeric types based on the current
 * value in the Text field. IntegerFormatter is an override of NumberFormatter
 * allowing to guaranty to always return Integer values (Number.intValue()).
 */
public class IntegerFormatter extends NumberFormatter {
  public IntegerFormatter() {
		super();
	}

	public IntegerFormatter(Locale loc) {
		super(loc);
	}

	public IntegerFormatter(String editPattern, Locale loc) {
		super(editPattern, loc);
	}

	public IntegerFormatter(String editPattern, String displayPattern, Locale loc) {
		super(editPattern, displayPattern, loc);
	}

	public IntegerFormatter(String editPattern, String displayPattern) {
		super(editPattern, displayPattern);
	}

	public IntegerFormatter(String editPattern) {
		super(editPattern);
	}

	/**
   * Returns the current value of the text control if it is a valid <code>Integer</code>.
   * If the buffer is flagged as modified, the value is recalculated by parsing
   * with the <code>nfEdit</code> initialized with the edit pattern. If the
   * number is not valid, returns <code>null</code>.
   * 
   * @return current number value if valid, <code>null</code> else
   * @see ITextFormatter#getValue()
   */
	public Object getValue() {
		Object value = super.getValue();
		if ( value instanceof Number ) {
			return new Integer(((Number) value).intValue());
		}
		return super.getValue();
	}

  /**
   * Returns the type of value this {@link ITextFormatter} handles,
   * i.e. returns in {@link #getValue()}.<br>
   * An IntegerFormatter always returns an Integer value.
   * 
   * @return The value type.
   */
	public Class getValueType() {
		return Integer.class;
	}
}
