/*******************************************************************************
 * Copyright (c) 2005, 2009 Eric Wuillai.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eric Wuillai (eric@wdev91.com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.formattedtext;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Hashtable;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;

/**
 * This class provides formatting of <code>Number</code> values in a
 * <code>FormattedText</code>.<p>
 * 
 * Formatter is composed of an edit pattern and a display pattern.<br>
 * Display pattern uses the same syntax than <code>DecimalFormat</code>, and
 * uses it to compute the value to display.<br>
 * Edit pattern is more limited and composed of two part, the int part and
 * the decimal part. Formatting characters allow to specify number of digits,
 * minimal length, decimal position, grouping and negative sign.<p>
 * 
 * <h4>Patterns Characters</h4>
 * <table border=0 cellspacing=3 cellpadding=0 summary="Chart shows pattern letters, date/time component, presentation, and examples.">
 *   <tr bgcolor="#ccccff">
 *     <th align=left>Symbol</th>
 *     <th align=left>Meaning</th>
 *   </tr>
 *   <tr>
 *   	 <td><code>0</code></td>
 *     <td>Digit</td>
 *   </tr>
 *   <tr bgcolor="#eeeeff">
 *   	 <td><code>#</code></td>
 *     <td>Digit, zero shows as absent</td>
 *   </tr>
 *   <tr>
 *   	 <td><code>.</code></td>
 *     <td>Decimal separator</td>
 *   </tr>
 *   <tr bgcolor="#eeeeff">
 *   	 <td><code>-</code></td>
 *     <td>Minus sign</td>
 *   </tr>
 *   <tr>
 *   	 <td><code>,</code></td>
 *     <td>Grouping separator</td>
 *   </tr>
 * </table>
 * 
 * <h4>Examples</h4>
 * <ul>
 * 	 <li><code>new NumberFormatter("#,##0.00")</code> - 1234.5 will edit and
 *   display as "1,234.50".</li>
 * </ul>
 */
public class NumberFormatter extends AbstractFormatter {
  private static final char P_DIGIT = '#';
  private static final char P_ZERODIGIT = '0';
  private static final char P_DECIMAL_SEP = '.';
  private static final char P_GROUP_SEP = ',';
  private static final char P_MINUS = '-';

  /** Cache of number patterns by locales */
  protected static Hashtable cachedPatterns = new Hashtable();

  /** Number formatter for display */
  protected DecimalFormat nfDisplay;
  /** Number formatter for display edit */
  protected DecimalFormat nfEdit;
  /** Buffer for the edit value */
  protected StringBuffer editValue;
  /** Number containing the current value */
  protected Number value;
  /** Edit pattern */
  protected String editPattern;
  /** The locale used */
  protected Locale locale;
  /** Length of groups (0 if no group separator) */
  protected int groupLen = 0;
  /** Current number of digits of the int part */
  protected int intCount = 0;
  /** Maximum number of digits of the int part */
  protected int intLen = 0;
  /** Maximum number of digits of the decimal part */
  protected int decimalLen = 0;
  /** Minimum number of digits of the int part (complement by 0) */
  protected int zeroIntLen = 0;
  /** Minimum number of digits of the decimal part (complement by 0) */
  protected int zeroDecimalLen = 0;
  /** Flag for display of the negative sign */
  protected boolean minus = false;
  /** Flag indicating that the current value is negative */
  protected boolean negative = false;
  /** Symbols used to format numbers */
  protected DecimalFormatSymbols symbols;
  /** Flag indicating the use of the 0xAO (no-break space) grouping separator */
  protected boolean nbspSeparator;
  /** Flag for display of the decimal separator */
  protected boolean alwaysShowDec;
  /** Flag indicating that the buffer is modified and the Number value must be computed */
  protected boolean modified;
  /** Flag indicating that the int part has a fixed length */
  protected boolean fixedInt = true;
  /** Flag indicating that the decimal part has a fixed length */
  protected boolean fixedDec = true;
  /** Length of the prefix part in the cache */
  protected int prefixLen = 0;
  /** Length of the suffix part in the cache */
  protected int suffixLen = 0;

  /**
   * Constructs a new instance with all defaults :
   * <ul>
   *   <li>edit mask from NumberPatterns for the default locale</li>
   *   <li>display mask identical to the edit mask</li>
   *   <li>default locale</li>
   * </ul>
   */
  public NumberFormatter() {
    this(null, null, Locale.getDefault());
  }

  /**
   * Constructs a new instance with default edit and display masks for the given
   * locale.
   * 
   * @param loc locale
   */
  public NumberFormatter(Locale loc) {
    this(null, null, loc);
  }

  /**
   * Constructs a new instance with the given edit mask. Display mask is
   * identical to the edit mask, and locale is the default one.
   * 
   * @param editPattern edit mask
   */
  public NumberFormatter(String editPattern) {
    this(editPattern, null, Locale.getDefault());
  }

  /**
   * Constructs a new instance with the given edit mask and locale. Display mask
   * is identical to the edit mask.
   * 
   * @param editPattern edit mask
   * @param loc locale
   */
  public NumberFormatter(String editPattern, Locale loc) {
    this(editPattern, null, loc);
  }

  /**
   * Constructs a new instance with the given edit and display masks. Uses the
   * default locale.
   * 
   * @param editPattern edit mask
   * @param displayPattern display mask
   */
  public NumberFormatter(String editPattern, String displayPattern) {
    this(editPattern, displayPattern, Locale.getDefault());
  }

  /**
   * Constructs a new instance with the given masks and locale.
   * 
   * @param editPattern edit mask
   * @param displayPattern display mask
   * @param loc locale
   */
  public NumberFormatter(String editPattern, String displayPattern, Locale loc) {
  	this.locale		= loc;
    alwaysShowDec = false;
    setPatterns(editPattern, displayPattern, loc);
    setValue(null);
  }

  /**
   * Clears a part of the edition cache. The start and len parameters are
   * adjusted to avoid clearing in prefix and suffix parts of the cache.
   * 
   * @param start beginning index
   * @param len length of portion to clear
   */
  protected void clearText(int start, int len) {
  	if ( start < prefixLen ) {
  		len -= prefixLen - start;
  		start = prefixLen;
  	}
  	if ( start + len >= editValue.length() - suffixLen ) {
  		len = editValue.length() - suffixLen - start;
  	}
  	int d = editValue.indexOf(EMPTY + symbols.getDecimalSeparator());
  	boolean decimal = d >= start && d < start + len;
  	for (int i = 0; i < len; i++) {
  		char c = editValue.charAt(start + i);
  		if ( c >= '0' && c <= '9' ) {
  			if ( d < 0 || start + i < d ) {
  				intCount--;
  			}
  		} else if ( c == symbols.getMinusSign() ) {
  			negative = false;
  		}
  	}
  	editValue.delete(start, start + len);
  	if ( decimal && (start < editValue.length() || alwaysShowDec) ) {
  		editValue.insert(start, symbols.getDecimalSeparator());
  	}
  }

  /**
   * Formats the edit buffer. Inserts group separators to the right places,
   * deletes excess decimal digits and add 0 to complete to the minimal length
   * of int and decimal parts. The position of the cursor is preserved.
   * 
   * @param curseur Current position of the cursor
   * @return New position of the cursor
   */
  protected int format(int curseur) {
  	int i = prefixLen + (negative ? 1 : 0);
  	char c;

  	// Inserts zeros in the int part
  	while ( intCount < zeroIntLen ) {
  		editValue.insert(i, '0');
  		intCount++;
  		curseur++;
  	}
  	while ( intCount > zeroIntLen ) {
  		if ( editValue.charAt(i) == '0' ) {
    		intCount--;
  		} else if ( editValue.charAt(i) != symbols.getGroupingSeparator() ) {
  			break;
  		}
  		editValue.deleteCharAt(i);
  		if ( curseur > i ) curseur--;
  	}

  	// Recreates the groups in the int part
  	if ( groupLen > 0 ) {
    	int n = intCount > groupLen ? groupLen - intCount % groupLen : 0;
    	if ( n == groupLen ) {
    		n = 0;
    	}
    	for (; i < editValue.length() - suffixLen; i++) {
    		c = editValue.charAt(i);
    		if ( c >= '0' && c <= '9' ) {
    			if ( n == groupLen ) {
    				editValue.insert(i, symbols.getGroupingSeparator());
    				if ( curseur >= i ) {
    					curseur++;
    				}
    				n = 0;
    			} else {
    				n++;
    			}
    		} else if ( c == symbols.getGroupingSeparator() ) {
    			if ( n != groupLen ) {
    				editValue.deleteCharAt(i);
    				if ( curseur >= i ) {
    					curseur--;
    				}
    				i--;
    			} else {
    				n = 0;
    			}
    		} else if ( c == symbols.getDecimalSeparator() ) {
    			if ( i > 0 && editValue.charAt(i - 1) == symbols.getGroupingSeparator() ) {
    				editValue.deleteCharAt(i - 1);
    				if ( curseur >= i ) {
    					curseur--;
    				}
    				i--;
    			}
    			break;
    		} else {
    			break;
    		}
    	}
  	}

  	// Truncates / completes by zeros the decimal part
  	i = editValue.indexOf(EMPTY + symbols.getDecimalSeparator());
  	if ( i < 0 && (zeroDecimalLen > 0 || alwaysShowDec) ) {
  		i = editValue.length() - suffixLen;
  		editValue.insert(i, symbols.getDecimalSeparator());
  	}
  	if ( i >= 0 ) {
  		int j;
  		for (j = i + 1; j < editValue.length() - suffixLen;) {
  			c = editValue.charAt(j);
  			if ( c == symbols.getGroupingSeparator() ) {
  				editValue.deleteCharAt(j);
  			} else if ( c < '0' || c > '9' ) {
  				break;
  			} else {
  				j++;
  			}
  		}
  		if ( fixedDec && (j - i - 1) > decimalLen ) {
  			editValue.delete(i + decimalLen + 1, j);
  			if ( curseur > i + decimalLen ) {
  				curseur = i + decimalLen;
  			}
  		} else {
  			while ( (j - i - 1) < zeroDecimalLen ) {
  				editValue.insert(j++, '0');
  			}
  		}
  	}

  	return curseur;
  }

  /**
   * Returns the default edit pattern for a given locale.
   * 
   * @param loc locale
   * @return Edit pattern
   */
  protected String getDefaultPattern(Locale loc) {
    String edit = (String) cachedPatterns.get(loc);
    if ( edit == null ) {
    	NumberFormat nf = NumberFormat.getNumberInstance(loc);
    	if ( ! (nf instanceof DecimalFormat) ) {
    		SWT.error(SWT.ERROR_INVALID_ARGUMENT);
    	}
  		edit = ((DecimalFormat) nf).toPattern();
      int i = edit.indexOf(((DecimalFormat) nf).getDecimalFormatSymbols()
                           .getPatternSeparator());
      if ( i >= 0 ) {
        edit = edit.substring(0, i);
      }
      cachedPatterns.put(loc, edit);
    }
    return edit;
  }

  /**
   * Returns the current value formatted for display.
   * This method is called by <code>FormattedText</code> when the <code>Text</code>
   * widget looses focus.
   * The displayed value is the result of formatting on the <code>Number</code>
   * with a <code>DecimalFormat<code> for the display pattern passed in
   * constructor.
   * 
   * @return display string if valid, empty string else
   * @see ITextFormatter#getDisplayString()
   */
  public String getDisplayString() {
    return editValue.substring(0, prefixLen)
    			 + ((getValue() != null
    			     ? nfDisplay.format(value) : EMPTY))
    			 + editValue.substring(editValue.length() - suffixLen);
  }

  /**
   * Returns the current value formatted for editing.
   * This method is called by <code>FormattedText</code> when the <code>Text</code>
   * widget gains focus.
   * The value returned is the content of the StringBuilder <code>editValue</code>
   * used as cache.
   * 
   * @return edit string
   * @see ITextFormatter#getEditString()
   */
  public String getEditString() {
    return editValue.toString();
  }

  /**
   * Returns the current value of the text control if it is a valid <code>Number</code>.
   * If the buffer is flagged as modified, the value is recalculated by parsing
   * with the <code>nfEdit</code> initialized with the edit pattern. If the
   * number is not valid, returns <code>null</code>.
   * 
   * @return current number value if valid, <code>null</code> else
   * @see ITextFormatter#getValue()
   */
  public Object getValue() {
  	if ( modified ) {
      try {
  			value = nfEdit.parse(editValue.substring(prefixLen, editValue.length() - suffixLen));
  		} catch (ParseException e1) {
  			if ( zeroIntLen + zeroDecimalLen == 0 
  					 && (editValue.length() == 0
  							 || editValue.charAt(0) == symbols.getDecimalSeparator()) ) {
					value = null;
  			} else {
					value = new Integer(0);
  			}
  		}
  		modified = false;
  	}
    return value;
  }

  /**
   * Returns the type of value this {@link ITextFormatter} handles,
   * i.e. returns in {@link #getValue()}.<br>
   * A NumberFormatter always returns a Number value.
   * 
   * @return The value type.
   */
  public Class getValueType() {
		return Number.class;
	}

  /**
   * Returns <code>true</code> if current edited value is empty, else returns
   * <code>false</code>.<br>
   * 
   * @return <code>true</code> if empty, else <code>false</code>
   */
  public boolean isEmpty() {
    return ! isValid();
  }

  /**
   * Returns <code>true</code> if current edited value is valid, else returns
   * <code>false</code>.<br>
   * A NumberFormatter is valid if the cached value is not null.
   * 
   * @return <code>true</code> if valid, else <code>false</code>
   * @see ITextFormatter#isValid()
   */
  public boolean isValid() {
    return getValue() != null;
  }

  /**
   * Sets the flag to always display the decimal separator, even if the decimal
   * part is empty.
   * 
   * @param show true / false
   */
  public void setDecimalSeparatorAlwaysShown(boolean show) {
    alwaysShowDec = show;
    int i = editValue.lastIndexOf(EMPTY + symbols.getDecimalSeparator());
    if ( alwaysShowDec ) {
      if ( i == -1 ) {
        editValue.append(symbols.getDecimalSeparator());
      }
    } else {
      if ( i > -1 && i == editValue.length() - 1 ) {
        editValue.deleteCharAt(i);
      }
    }
  }

  /**
   * Sets the fixed length flags.<br>
   * By default, int and decimal part of the pattern have a fixed length.
   * 
   * @param fixedInt flag for int part
   * @param fixedDec flag for decimal part
   */
  public void setFixedLengths(boolean fixedInt, boolean fixedDec) {
  	this.fixedInt = fixedInt;
  	this.fixedDec = fixedDec;
  }

  /**
   * Sets the patterns and initializes the technical attributes used to manage
   * the operations.
   * 
   * @param edit edit pattern
   * @param display display pattern
   * @param loc Locale to use
   * @throws IllegalArgumentException if a pattern is invalid
   */
  protected void setPatterns(String edit, String display, Locale loc) {
    // Symbols
    symbols				= new DecimalFormatSymbols(loc);
    nbspSeparator = symbols.getGroupingSeparator() == 0xA0;

    // Get default edit pattern if null
    if ( edit == null ) {
      edit = getDefaultPattern(loc);
    }

    // Analyze the edit pattern
    boolean grouping = false;
    boolean decimal = false;
    groupLen = intLen = decimalLen = zeroIntLen = zeroDecimalLen = 0;
    minus = false;
    int i;
    for (i = 0; i < edit.length(); i++) {
      switch ( edit.charAt(i) ) {
      	case P_MINUS :
      		if ( i != 0 ) {
      			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
      		}
      		minus = true;
      		break;
        case P_GROUP_SEP :
          if ( ! decimal ) {
            grouping = true;
            groupLen = 0;
          } else {
      			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
          }
          break;
        case P_DECIMAL_SEP :
          grouping = false;
          decimal  = true;
          break;
        case P_ZERODIGIT :
          if ( decimal ) {
            zeroDecimalLen++;
          } else {
            zeroIntLen++;
          }
          // Continue on P_DIGIT...
        case P_DIGIT :
          if ( decimal ) {
            decimalLen++;
          } else {
            intLen++;
            if ( grouping ) {
              groupLen++;
            }
          }
          break;
        default :
    			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
      }
    }
    editPattern = edit;
    nfEdit = new DecimalFormat(minus ? editPattern.substring(1) : editPattern,
    													 symbols);
    editValue = new StringBuffer();

    // Create the display formatter
    nfDisplay = display != null ? new DecimalFormat(display, symbols) : nfEdit;

    // Initialize the edit cache
    intCount = 0;
    for (i = 0; i < zeroIntLen; i++) {
      editValue.append('0');
      intCount++;
    }
    if ( alwaysShowDec || zeroDecimalLen > 0 ) {
      editValue.append(symbols.getDecimalSeparator());
    }
    for (i = 0; i < zeroDecimalLen; i++) {
      editValue.append('0');
    }
  }

  /**
   * Sets a prefix to display before the value.<br>
   * 
   * To clear the current prefix, call the <code>setPrefix</code> method with a
   * <code>null</code> parameter.
   * 
   * @param prefix prefix to display, or <code>null</code> to clear
   */
  protected void setPrefix(String prefix) {
  	if ( prefixLen > 0 ) {
  		editValue.delete(0, prefixLen);
  		prefixLen = 0;
  	}
  	if ( prefix != null ) {
  		editValue.insert(0, prefix);
  		prefixLen = prefix.length();
  	}
  }

  /**
   * Sets a suffix to display after the value.<br>
   * 
   * To clear the current suffix, call the <code>setSuffix</code> method with a
   * <code>null</code> parameter.
   * 
   * @param suffix suffix to display, or <code>null</code> to clear
   */
  protected void setSuffix(String suffix) {
  	if ( suffixLen > 0 ) {
    	editValue.delete(editValue.length() - suffixLen, editValue.length());
    	suffixLen = 0;
  	}
  	if ( suffix != null ) {
  		editValue.append(suffix);
  		suffixLen = suffix.length();
  	}
  }

  /**
   * Sets the value to edit. The value provided must be a <code>Number</code>.
   * 
   * @param value number value
   * @throws IllegalArgumentException if not a number
   * @see ITextFormatter#setValue(java.lang.Object)
   */
  public void setValue(Object value) {
    boolean decimal = false;
    if ( value instanceof Number ) {
      this.value = (Number) value;
      editValue.delete(prefixLen, editValue.length() - suffixLen);
      editValue.insert(prefixLen, nfEdit.format(this.value));
      intCount = 0;
      for (int i = prefixLen; i < editValue.length() - suffixLen; i++) {
      	char c = editValue.charAt(i);
      	if ( c == symbols.getDecimalSeparator() ) {
      		decimal = true;
      	} else if ( c >= '0' && c <= '9' ) {
      		if ( ! decimal ) {
      			intCount++;
      		}
      	}
      }
      modified = false;
    } else if ( value == null ) {
    	clearText(0, editValue.length());
      updateText(editValue.toString(), format(0));
      if ( zeroIntLen + zeroDecimalLen > 0 ) {
      	this.value = new Integer(0);
      } else {
      	this.value = null;
      }
      modified = false;
    } else {
      SWT.error(SWT.ERROR_INVALID_ARGUMENT);
    }
  }

  /**
   * Handles a <code>VerifyEvent</code> sent when the text is about to be modified.
   * This method is the entry point of all operations of formatting.
   * 
   * @see org.eclipse.swt.events.VerifyListener#verifyText(org.eclipse.swt.events.VerifyEvent)
   */
  public void verifyText(VerifyEvent e) {
    if ( ignore ) {
      return;
    }

    int p; // Current insertion position in the edit cache
    e.doit = false;

    if ( e.keyCode == SWT.BS || e.keyCode == SWT.DEL ) {
    	clearText(e.start, (e.end > e.start) ? e.end - e.start : 1);
    	p = e.start;
    } else {
    	if ( e.end > e.start ) {
    		clearText(e.start, e.end - e.start);
    	}
    	p = e.start;

    	int d = editValue.indexOf(EMPTY + symbols.getDecimalSeparator()); // Decimal separator position
    	for (int i = 0; i < e.text.length(); i++) {
    		if ( p < prefixLen || p > editValue.length() - suffixLen ) break;
    		char c = e.text.charAt(i);
    		if ( c >= '0' && c <= '9' ) {
    			// Controls the number of digits by group
    			if ( d >= 0 && p > d ) {
    				if ( fixedDec && p > d + decimalLen ) {
    					beep();
    					break;
    				}
    			} else {
    				if ( fixedInt && intCount >= intLen ) {
    					beep();
    					break;
    				}
    				if ( d >= 0 ) {
      				d++;
    				}
    				intCount++;
    			}
    			// Insert the char
    			editValue.insert(p++, c);
    		} else if ( groupLen > 0
    								&& (c == symbols.getGroupingSeparator()
    										|| (c == ' ' && nbspSeparator)) ) {
    			/*
    			 * Some locales (eg. French) return a no-break space as the grouping
    			 * separator. This character is not natural to use for users. So we
    			 * recognize too the simple space as the grouping separator.
    			 * 
    			 * Java bug: 4510618
    			 */
    			if ( d >= 0 && p > d ) {
    				beep();
  					break;
    			}
    			int n = editValue.indexOf(EMPTY + symbols.getGroupingSeparator(), p);
    			if ( n > p ) {
    				p = n + 1;
    			}
    		} else if ( c == symbols.getMinusSign() ) {
    			if ( p != prefixLen || ! minus ) {
    				beep(); // Minus sign only possible in first position
    				break;
    			}
    			if ( this.editValue.length() == 0 || editValue.charAt(0) != c ) {
      			editValue.insert(p++, c);
      			negative = true;
      			d++;
    			} else {
    				editValue.deleteCharAt(0);
      			negative = false;
    				d--;
    			}
    		} else if ( c == symbols.getDecimalSeparator()
    								&& (decimalLen > 0 || ! fixedDec ) ) {
    			if ( d >= 0 ) {
    				if ( d < p ) {
    					beep();
    					break;
    				} else {
    					p = d + 1;
    					continue;
    				}
    			}
          d = p;
          editValue.insert(p++, c);
          intCount = 0;
          for (int j = 0; j < d; j++) {
            char c1 = editValue.charAt(j);
            if ( c1 >= '0' && c1 <= '9' ) {
              intCount++;
            }
          }
    		} else {
  				beep();
  				break;
    		}
    	}
    }
    p = format(p);
    modified = true;
    updateText(getEditString(), p);
  }
}
