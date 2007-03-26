/*******************************************************************************
 * Copyright (c) 2005, 2007 Eric Wuillai.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eric Wuillai (eric@wdev91.com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.formattedtext;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;

/**
 * This class provides formatting of <code>String</code> values in a
 * <code>FormattedText</code>.<p>
 * 
 * <h4>Pattern Characters</h4>
 * <table border=0 cellspacing=3 cellpadding=0 summary="Chart shows pattern letters, date/time component, presentation, and examples.">
 *   <tr bgcolor="#ccccff">
 *     <th align=left>Symbol</th>
 *     <th align=left>Meaning</th>
 *   </tr>
 *   <tr>
 *   	 <td><code>#</code></td>
 *     <td>Digit</td>
 *   </tr>
 *   <tr bgcolor="#eeeeff">
 *   	 <td><code>A</code></td>
 *     <td>Alphanumeric</td>
 *   </tr>
 *   <tr>
 *   	 <td><code>U</code></td>
 *     <td>Alphanumeric converted in uppercase</td>
 *   </tr>
 *   <tr bgcolor="#eeeeff">
 *   	 <td><code>L</code></td>
 *     <td>Alphanumeric converted in lowercase</td>
 *   </tr>
 * </table>
 * All other characters are considered as separators.
 * 
 * <h4>Examples</h4>
 * <code>newFormatter("(###) UUU-AAAA");</code><br>
 * <code>setValue("123aBcDeF");</code> -> will display as "(123) ABC-DeF".
 */
public class MaskFormatter extends AbstractFormatter {
  private static final char P_DIGIT = '#';
  private static final char P_ALPHANUM = 'A';
  private static final char P_UPPERCASE = 'U';
  private static final char P_LOWERCASE = 'L';

  /** Edit mask */
  protected String editPattern;
  /** Buffer for the edit value */
  protected StringBuffer editValue;
  /** Number of positions if the mask */
  protected int pos;
  /** Current number of characters if the buffer (except mask characters) */
  protected int count = 0;

  /**
   * Constructs a new instance with the given edit mask.
   * 
   * @param editPattern edit mask
   */
  public MaskFormatter(String editPattern) {
  	if ( editPattern == null ) SWT.error(SWT.ERROR_NULL_ARGUMENT);
    this.editPattern = editPattern;

    // Initializes the buffer with the formatting characters
    editValue = new StringBuffer(editPattern.length());
    for (int i = 0; i < editPattern.length(); i++) {
      char c = editPattern.charAt(i);
      if ( c != P_DIGIT && c != P_ALPHANUM && c != P_UPPERCASE && c != P_LOWERCASE ) {
        editValue.append(c);
      } else {
        editValue.append(SPACE);
        pos++;
      }
    }
  }

  /**
   * Clear a part of the edition cache. Mask characters are preserved in their
   * positions.
   * 
   * @param start beginning index
   * @param len length of portion to clear
   */
  protected void clearText(int start, int len) {
    for (int i = start; i < start + len && i < editValue.length(); i++) {
      char c = editPattern.charAt(i);
      if ( c == P_DIGIT || c == P_ALPHANUM || c == P_UPPERCASE || c == P_LOWERCASE ) {
        if ( editValue.charAt(i) != SPACE ) {
          count--;
        }
        editValue.setCharAt(i, SPACE);
      }
    }
  }

  /**
   * Returns the current value formatted for display.<p>
   * There is no difference in this formatter between edit and display values.
   * So this method returns the edit string.
   * 
   * @return display string
   * @see ITextFormatter#getDisplayString()
   */
  public String getDisplayString() {
    return getEditString();
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
   * Returns the current value.
   * The value returned is the content of the edit cache without any mask
   * characters.
   * 
   * @return current string value
   * @see ITextFormatter#getValue()
   */
  public Object getValue() {
    StringBuffer value = new StringBuffer(editValue.length());
    for (int i = 0; i < editValue.length(); i++) {
      char c = editPattern.charAt(i);
      if ( c == P_DIGIT || c == P_ALPHANUM || c == P_UPPERCASE || c == P_LOWERCASE ) {
        value.append(editValue.charAt(i));
      }
    }
    return value.toString();
  }

  /**
   * Inserts a sequence of characters in the edit buffer. The current content
   * of the buffer is overrided. The new position of the cursor is computed and
   * returned. Mask characters are preserved in their positions.
   * 
   * @param txt String of characters to insert
   * @param start Starting position of insertion
   * @return New position of the cursor
   */
  protected int insertText(String txt, int start) {
    int i = start, j = 0;
    char p, c, o;

    while ( i < editPattern.length() && j < txt.length() ) {
      p = editPattern.charAt(i);
      c = txt.charAt(j);
      o = editValue.charAt(i);
      switch ( p ) {
        case P_DIGIT :
          if ( Character.isDigit(c) ) {
            editValue.setCharAt(i, c);
          } else {
            throw new IllegalArgumentException(INVALID_VALUE);
          }
          break;
        case P_UPPERCASE :
          if ( Character.isLetterOrDigit(c) ) {
            editValue.setCharAt(i, Character.toUpperCase(c));
          } else {
            throw new IllegalArgumentException(INVALID_VALUE);
          }
          break;
        case P_LOWERCASE :
          if ( Character.isLetterOrDigit(c) ) {
            editValue.setCharAt(i, Character.toLowerCase(c));
          } else {
            throw new IllegalArgumentException(INVALID_VALUE);
          }
          break;
        case P_ALPHANUM :
          if ( Character.isLetterOrDigit(c) ) {
            editValue.setCharAt(i, c);
          } else {
            throw new IllegalArgumentException(INVALID_VALUE);
          }
          break;
        default :   // Separator
          if ( p != c ) {
            if ( Character.isLetterOrDigit(c) ) {
              i++;
              continue;
            } else {
              throw new IllegalArgumentException(INVALID_VALUE);
            }
          }
          o = '*';
          break;
      }
      if ( o == SPACE && c != SPACE ) {
        count++;
      }
      i++;
      j++;
    }
    return i;
  }

  /**
   * Returns <code>true</code> if current edited value is valid, else returns
   * <code>false</code>. An empty value is considered as invalid.
   * 
   * @return true if valid, else false
   * @see ITextFormatter#isValid()
   */
  public boolean isValid() {
    return (count == 0 || count == pos);
  }

  /**
   * Sets the value to edit. The value provided must be a <code>String</code>.
   * 
   * @param value string value
   * @throws IllegalArgumentException if not a string
   * @see ITextFormatter#setValue(java.lang.Object)
   */
  public void setValue(Object value) {
    if ( value instanceof String ) {
      insertText((String) value, 0);
      updateText(editValue.toString(), 0);
    } else if ( value == null ) {
    	clearText(0, editValue.length());
    } else {
      throw new IllegalArgumentException(INVALID_VALUE);
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
    e.doit = false;
    if ( e.keyCode == SWT.BS || e.keyCode == SWT.DEL ) {
      // Clears
      clearText(e.start, (e.end > e.start) ? e.end - e.start : 1);
      updateText(editValue.toString(), e.start);
    } else {
      // Inserts
      int p;
      try {
        p = insertText(e.text, e.start);
        if ( e.end - e.start > e.text.length() ) {
          clearText(e.start + e.text.length(), e.end - e.start - e.text.length());
        }
      } catch (IllegalArgumentException iae) {
        beep();
        p = e.start;
      }

      // Computes new position of the cursor
      char c;
      while ( p < editPattern.length() && (c = editPattern.charAt(p)) != P_DIGIT
              && c != P_ALPHANUM && c != P_UPPERCASE && c!= P_LOWERCASE ) {
        p++;
      }

      updateText(editValue.toString(), p);
    }
  }
}
