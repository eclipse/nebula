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
package org.eclipse.nebula.widgets.datechooser;

import java.util.Date;
import java.util.Locale;

import org.eclipse.nebula.widgets.formattedtext.DateFormatter;
import org.eclipse.nebula.widgets.formattedtext.DefaultFormatterFactory;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

/**
 * DateChooserCombo widget. This class representes a date field editor that combines
 * a text field and a calendar. Implementation is based on <code>FormattedText</code>
 * and <code>DateChooser</code>.<p>
 * 
 * Issues notification when the text content is modified or when a date is
 * selected in the calendar.<p>
 * 
 * <dl>
 * <dt><b>Styles:</b>
 * <dd>BORDER, READ_ONLY, FLAT</dd>
 * <dt><b>Events:</b>
 * <dd>Modify</dd>
 * <dd>Selection</dd>
 * </dl>
 */
public class DateChooserCombo extends AbstractCombo {
	/** Default image filename */
	protected static final String IMAGE = "/org/eclipse/nebula/widgets/datechooser/DateChooserCombo.png";

	/** Default image for the button */
	protected static Image buttonImage;

	/** FormattedText widget for edition of the date */
	protected FormattedText formattedText;

	static {
		buttonImage = new Image(Display.getCurrent(),
		                        DateChooserCombo.class.getResourceAsStream(IMAGE));
	}

	/**
   * Constructs a new instance of this class given its parent and a style value
   * describing its behavior and appearance.<p>
   * 
   * The widget is initialized with a default image for the button, and a
   * default <code>DateFormatter</code>.
   * 
   * @param parent a composite control which will be the parent of the new instance (cannot be null)
   * @param style the style of control to construct
	 */
	public DateChooserCombo(Composite parent, int style) {
    super(parent, style);
		setImage(buttonImage);
		pack();
	}

	/**
	 * Called just before the popup is droppped. The selected date of the
	 * calendar is setted to the current date present in the formatted text.
	 * 
	 * @see org.eclipse.nebula.widgets.datechooser.AbstractCombo#beforeDrop()
	 */
	protected void beforeDrop() {
		Date d = (Date) formattedText.getValue();
		DateChooser cal = (DateChooser) popupContent;
		if ( d != null ) {
			cal.setSelectedDate(d);
		} else {
			cal.clearSelection();
			cal.setFocusOnToday(false);
		}
	}

	/**
	 * Manages popup content events. Extend the selection behaviour, adding the
	 * selected date in the <code>data</code> attribute of the event.
	 * 
	 * @param event event
	 * @see org.eclipse.nebula.widgets.datechooser.AbstractCombo#contentEvent(org.eclipse.swt.widgets.Event)
	 */
	protected void contentEvent(Event event) {
		switch (event.type) {
			case SWT.Selection :
				super.contentEvent(event);
				if ( event.doit ) {
					formattedText.setValue(((DateChooser) popupContent).getSelectedDate());
					dropDown(false);
					text.setFocus();
				}
				break;
			default :
				super.contentEvent(event);
				break;
		}
	}

	/**
	 * Creates the button widget. The default appearence with an arrow is
	 * replaced by a button with an image.
	 * 
	 * @param style button style
	 * @see org.eclipse.nebula.widgets.datechooser.AbstractCombo#createButtonControl(int)
	 */
	protected void createButtonControl(int style) {
		style &= ~(SWT.ARROW | SWT.DOWN);
		button = new Button(this, style | SWT.PUSH);
	}

	/**
	 * Creates the popup content. The content is a <code>DateChooser</code>.
	 * 
	 * @see org.eclipse.nebula.widgets.datechooser.AbstractCombo#createPopupContent()
	 */
	protected void createPopupContent() {
		DateChooser cal = new DateChooser(popup, SWT.NONE);
		cal.setAutoSelectOnFooter(true);
  	popupContent = cal;
	}

	/**
	 * Creates the text widget. Overrides the default implementation to create a
	 * <code>FormattedText</code> with the default formatter for <code>Date</code>
	 * values.
	 * The formatter is provided by <code>DefaultFormatterFactory</code>. By default
	 * a <code>DateFormatter</code> is returned. This can be changed by
	 * registering a new formatter for Date class.
	 * 
	 * @param style text style
	 * @see org.eclipse.nebula.widgets.datechooser.AbstractCombo#createTextControl(int)
	 */
	protected void createTextControl(int style) {
    formattedText = new FormattedText(this, SWT.NONE);
    formattedText.setFormatter(DefaultFormatterFactory.createFormatter(Date.class));
    text = formattedText.getControl();
	}

	/**
   * Returns the current <code>Date</code> value of the widget.<p>
   * 
   * @return Current value
   */
  public Date getValue() {
		checkWidget();
  	return (Date) formattedText.getValue();
  }

  /**
	 * Returns true if footer is visible in the popup calendar.
	 * 
	 * @return <code>true</code> if footer visible, else <code>false</code>
	 */
	public boolean isFooterVisible() {
		checkWidget();
		return ((DateChooser) popupContent).isFooterVisible();
	}

  /**
	 * Returns true if grid is visible in the calendar popup.
	 * 
	 * @return Returns the grid visible status.
	 */
	public boolean isGridVisible() {
		checkWidget();
		return ((DateChooser) popupContent).isGridVisible();
	}

	/**
	 * Sets the footer of popup calendar visible or not. The footer displays the
	 * today date. It is not visible by default.
	 * 
	 * @param newVisible <code>true</code> to set footer visible, else <code>false</code>
	 */
	public void setFooterVisible(boolean newVisible) {
		checkWidget();
		((DateChooser) popupContent).setFooterVisible(newVisible);
  	popup.pack();
	}

	/**
   * Associates a new <code>DateFormatter</code> to the text widget, replacing
   * the default one.
   * 
   * @param formatter date formatter
   */
  public void setFormatter(DateFormatter formatter) {
		checkWidget();
  	formattedText.setFormatter(formatter);
  }

  /**
	 * Sets the grid visible or not in the calendar popup. By default, the grid
	 * is visible.
	 * 
	 * @param gridVisible <code>true</code> to set grid visible, else <code>false</code>
	 */
	public void setGridVisible(boolean gridVisible) {
		checkWidget();
		((DateChooser) popupContent).setGridVisible(gridVisible);
	}

	/**
   * Sets a new image to display on the button, replacing the default one.
   * 
   * @param image new image
   */
  public void setImage(Image image) {
		checkWidget();
  	GridData ld = (GridData) button.getLayoutData();
		if ( WIN32 ) {
	  	ImageData id	= image.getImageData();
	  	ld.widthHint	= id.width + 4;
	  	ld.heightHint = id.height + 4;
		}
  	ld.grabExcessVerticalSpace = true;
  	button.setImage(image);
  }

  /**
   * Sets the locale used both by the input mask and the calendar.
   * 
   * @param loc locale
   */
  public void setLocale(Locale loc) {
		checkWidget();
  	((DateFormatter) formattedText.getFormatter()).setLocale(loc);
		((DateChooser) popupContent).setLocale(loc);
		popup.pack();
  }

  /**
	 * Sets the theme to apply to the calendar popup.
	 * 
	 * @param theme new theme (must not be null)
	 */
	public void setTheme(DateChooserTheme theme) {
		checkWidget();
		((DateChooser) popupContent).setTheme(theme);
		popup.pack();
	}

	/**
   * Sets a new <code>Date</code> value.
   * 
   * @param value new date value
   */
	public void setValue(Date value) {
		checkWidget();
		formattedText.setValue(value);
	}
}
