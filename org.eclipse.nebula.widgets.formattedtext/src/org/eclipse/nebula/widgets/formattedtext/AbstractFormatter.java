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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

/**
 * Base class of formatters. Formatters can directly implement <code>ITextFormatter</code>,
 * or inherit this abstract class.<p>
 * Provide several common functionalities and constants for the formatters. 
 */
public abstract class AbstractFormatter implements ITextFormatter {
  /** Space character */
  protected static final char SPACE = ' ';
  /** Empty String */
  protected static final String EMPTY = ""; //$NON-NLS-1$

  /** Managed <code>Text</code> widget */
  protected Text text;
  /** Flag indicating if VerifyEvent must be ignored (true) or not (false) */
  protected boolean ignore;

  /**
   * Emits an audio beep.
   */
  protected void beep() {
    if ( text != null ) {
      text.getDisplay().beep();
    }
  }

	/**
	 * Called when the formatter is replaced by an other one in the <code>FormattedText</code>
	 * control. Allow to release resources like additional listeners.<p>
	 * 
	 * By default, do nothing. Override if needed.
	 * 
	 * @see ITextFormatter#detach()
	 */
  public void detach() {
	}

  /**
   * Sets the <code>ignore</code> flag.
   * 
   * @param ignore when true, VerifyEvent events are processed.
   * @see ITextFormatter#setIgnore(boolean)
   */
  public void setIgnore(boolean ignore) {
    this.ignore = ignore;
  }

  /**
   * Sets the <code>Text</code> widget that will be managed by this formatter.
   * 
   * @param text Text widget
   * @see ITextFormatter#setText(Text)
   */
  public void setText(Text text) {
  	if ( text == null ) SWT.error(SWT.ERROR_NULL_ARGUMENT);
    this.text = text;
  }

  /**
   * Updates the text in the <code>Text</code> widget. The absolute position
   * of the cursor in the widget is preserved.
   * 
   * @param t new text
   */
  protected void updateText(String t) {
  	if ( text != null ) {
      updateText(t, text.getCaretPosition());
  	}
  }

  /**
   * Updates the text in the <code>Text</code> widget. The cursor is set to
   * the given position.
   * 
   * @param t new text
   * @param pos new cursor's position
   */
  protected void updateText(String t, int pos) {
  	if ( text != null ) {
  		String oldText = text.getText();
	    ignore = true;
  		if ( ! oldText.equals(t) ) {
  			text.setText(t);
  		}
	    text.setSelection(pos);
	    ignore = false;
  	}
  }
}
