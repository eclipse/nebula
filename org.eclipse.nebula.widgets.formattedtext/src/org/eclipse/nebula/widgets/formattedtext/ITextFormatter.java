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

import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

/**
 * Interface defining all the text formatters.<p>
 * 
 * Each formatter is associated with a <code>Text</code> control and can not be
 * shared. Formatters have and edit mask applied when the associated Text has
 * the focus, and a display mask for when the Text looses the focus.
 * The formatter must control editing keystroke by keystroke. For this is it
 * declared as a VerifyListener of the <code>Text</code> widget.
 */
public interface ITextFormatter extends VerifyListener {
	/**
	 * Called when the formatter is replaced by an other one in the <code>FormattedText</code>
	 * control. Allow to release resources like additional listeners.
	 */
	public void detach();

	/**
   * Returns the current value formatted for display.
   * This method is called by <code>FormattedText</code> when the <code>Text</code>
   * widget looses focus.
   * 
   * @return display string
   */
  public String getDisplayString();

  /**
   * Returns the current value formatted for editing.
   * This method is called by <code>FormattedText</code> when the <code>Text</code>
   * widget gains focus.
   * 
   * @return edit string
   */
  public String getEditString();

  /**
   * Returns the current value of the text control. If the current value is
   * invalid for its type (ex. Date missing parts), returns <code>null</code>.
   * 
   * @return current value
   */
  public Object getValue();

  /**
   * Returns the type of value this {@link ITextFormatter} handles,
   * i.e. returns in {@link #getValue()}.
   * 
   * @return The value type.
   */
  public Class getValueType();

  /**
   * Returns <code>true</code> if current edited value is empty, else returns
   * <code>false</code>. An empty value depends of the formatter and is not
   * just an empty string in the Text widget.
   * 
   * @return true if empty, else false
   */
  public boolean isEmpty();

  /**
   * Returns <code>true</code> if current edited value is valid, else returns
   * <code>false</code>.
   * 
   * @return true if valid, else false
   */
  public boolean isValid();

  /**
   * Specify whether or not <code>VerifyEvent</code> events must be processed.
   * Those events are the base of all formatters, allowing  on-the-fly
   * processing of each text change in the Text widget.
   * In some situations (e.g. when focus change), the <code>FormattedText</code>
   * must change the text in the widget without formatting.
   * 
   * @param ignore when true, VerifyEvent events are processed.
   */
  public void setIgnore(boolean ignore);

  /**
   * Sets the <code>Text</code> widget that will be managed by this formatter.
   * 
   * @param text Text widget
   */
  public void setText(Text text);

  /**
   * Sets the value to edit.
   * 
   * @param value value
   */
  public void setValue(Object value);
}
