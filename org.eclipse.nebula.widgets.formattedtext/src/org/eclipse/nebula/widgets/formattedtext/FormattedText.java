/*******************************************************************************
 * Copyright (c) 2005, 2008 Eric Wuillai.
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
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * Formatted text viewer. Add formating capabilities to the <code>Text</code>
 * widget of SWT. This control works on the same principle than the JFace
 * viewers. The embedded text widget is accessible by the getControl() method,
 * allowing to apply to it all necessary behaviors (layout, listeners...).<p>
 * 
 * Formatting is delegated to formatter objects implementing the <code>ITextFormatter</code>
 * interface. Each formatter class manages a base class of values (date, number...).<br>
 * Formatters are associated by 2 differents means :
 * <ul>
 * 	 <li>By the <code>setFormatter()</code> method.</li>
 * 	 <li>When <code>setValue()</code> is called and there is currently no formatter,
 *   a new one is automatically created based on the class of the value.</li>
 * </ul>
 * 
 * <h4>Styles:</h4>
 * <blockquote>
 * 	 CENTER, LEFT, RIGHT, READ_ONLY
 * </blockquote>
 */
public class FormattedText {
  /** Encapsulated Text widget */
  protected Text text;
  /** Formatter */
  protected ITextFormatter formatter = null;
  /** Save position of cursor when the focus is lost */
  protected int caretPos;
  /** Layout */
  protected GridLayout layout;
  /** Filter for modify events */
  protected Listener modifyFilter;

  protected static int count = 0;
  protected int id = ++count;

  /**
	 * Creates a formatted text on a newly-created text control under the given
	 * parent. The text control is created using the SWT style bits
	 * <code>BORDER</code>.
	 * 
	 * @param parent the parent control
	 */
  public FormattedText(Composite parent) {
  	this(parent, SWT.BORDER);
  }

  /**
   * Creates a formatted text on a newly-created text control under the given
	 * parent. The text control is created using the given SWT style bits.
   * 
   * @param parent the parent control
   * @param style the SWT style bits used to create the text
   */
  public FormattedText(Composite parent, int style) {
  	this(new Text(parent, style & (~ (SWT.MULTI | SWT.PASSWORD | SWT.WRAP))));
  }

	/**
	 * Creates a formatted text on the given text control.
	 * 
	 * @param t the text control
	 */
  public FormattedText(Text t) {
  	this.text = t;

  	text.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        if ( formatter != null && text.getEditable() ) {
          formatter.setIgnore(true);
          setText(formatter.getEditString());
          text.setSelection(caretPos);
          formatter.setIgnore(false);
        }
      }

      public void focusLost(FocusEvent e) {
        if ( formatter != null && text.getEditable() ) {
          formatter.setIgnore(true);
          caretPos = text.getCaretPosition();
          setText(formatter.getDisplayString());
          formatter.setIgnore(false);
        }
      }
    });

  	modifyFilter = new Listener() {
  		public void handleEvent(Event event) {
  			event.type = SWT.None;
  		}
  	};

  	text.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
	      text				 = null;
	      modifyFilter = null;
	      formatter		 = null;
      }
  	});
  }

  /**
   * Returns the primary <code>Text</code> control associated with this viewer.
   * 
   * @return the SWT text control which displays this viewer's content
   */
  public Text getControl() {
  	return text;
  }

  /**
   * Returns the formatter associated to the <code>Text</code> widget.
   * 
   * @return Formatter, or <code>null</code> if no formatter is currently associated
   */
  public ITextFormatter getFormatter() {
    return formatter;
  }

  /**
   * Returns the current value of the widget.<p>
   * 
   * The returned value is provided by the formatter and is of the type managed
   * bu the formatter. For exemple a <code>DateFormatter</code> will return a
   * <code>Date</code> value.<br>
   * If no formatter is associated, the <code>String</code> contained in the
   * <code>Text</code> widget is returned.
   * 
   * @return Current value
   */
  public Object getValue() {
    return formatter != null ? formatter.getValue() : text.getText();
  }

  /**
   * Returns <code>true</code> if the current value is valid, else <code>false</code>.
   * 
   * @return <code>true</code> if valid.
   */
  public boolean isValid() {
    return formatter != null ? formatter.isValid() : true;
  }

  /**
   * Associates a formatter to the widget.<br>
   * Parameter can not be null. In some situations, the FormattedText component
   * must not do formatting (eg. when reusing the same object for editing of
   * different types of values). In this case, use a StringFormatter. This
   * formatter do no formatting.
   * 
   * @param formatter formatter
   */
  public void setFormatter(ITextFormatter formatter) {
  	if ( formatter == null ) SWT.error(SWT.ERROR_NULL_ARGUMENT);
  	if ( this.formatter != null ) {
  		text.removeVerifyListener(this.formatter);
  		this.formatter.detach();
  	}
  	this.formatter = formatter;
    this.formatter.setText(text);
    text.addVerifyListener(this.formatter);
    formatter.setIgnore(true);
    text.setText(formatter.getDisplayString());
    formatter.setIgnore(false);
  }

  /**
   * Sets the Text widget value, preventing fire of Modify events.
   * 
   * @param value The String value to display in the widget
   */
  private void setText(String value) {
  	Display display = text.getDisplay();
  	try {
  		display.addFilter(SWT.Modify, modifyFilter);
  		text.setText(value);
  	} finally {
  		display.removeFilter(SWT.Modify, modifyFilter);
  	}
  }

  /**
   * Sets a new value.<p>
   * 
   * If no formatter is currently associated to he widget, a new one is created
   * by the factory based on the value's class.<br>
   * If the value is incompatible with the formatter, an <code>IllegalArgumentException</code>
   * is returned.
   * 
   * @param value new value
   */
  public void setValue(Object value) {
    if ( formatter == null ) {
      setFormatter(DefaultFormatterFactory.createFormatter(value));
    }
    formatter.setValue(value);
    formatter.setIgnore(true);
    text.setText(text.isFocusControl()
                 ? formatter.getEditString()
                 : formatter.getDisplayString());
    formatter.setIgnore(false);
  }
}
