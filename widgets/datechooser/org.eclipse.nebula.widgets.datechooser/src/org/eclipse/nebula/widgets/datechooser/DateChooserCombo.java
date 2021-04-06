/*******************************************************************************
 * Copyright (c) 2005, 2009 Eric Wuillai.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Eric Wuillai (eric@wdev91.com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.datechooser;

import java.util.Date;
import java.util.Locale;

import org.eclipse.nebula.widgets.formattedtext.DateFormatter;
import org.eclipse.nebula.widgets.formattedtext.DefaultFormatterFactory;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

/**
 * DateChooserCombo widget. This class represents a date field editor that combines
 * a text field and a calendar. Implementation is based on <code>FormattedText</code>
 * and <code>DateChooser</code>.
 * <p>
 * 
 * Issues notification when the text content is modified or when a date is
 * selected in the calendar.
 * <p>
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
	/** Flag to set footer visible or not in the popup */
	protected boolean footerVisible = false;
	/** Flag to set grid visible or not in the popup */
	protected int gridVisible = DateChooser.GRID_FULL;
	/** Flag to set weeks numbers visible or not */
	protected boolean weeksVisible = false;
	/** Calendar theme */
	protected DateChooserTheme theme;
	/** Locale used for localized names and formats */
	protected Locale locale;

	static {
		buttonImage = new Image(Display.getCurrent(), DateChooserCombo.class.getResourceAsStream(IMAGE));
	}

	/**
	 * Constructs a new instance of this class given its parent and a style value
	 * describing its behavior and appearance.
	 * <p>
	 * 
	 * The widget is initialized with a default image for the button, and a
	 * default <code>DateFormatter</code>.
	 * 
	 * @param parent a composite control which will be the parent of the new instance (cannot be null)
	 * @param style the style of control to construct
	 */
	public DateChooserCombo(Composite parent, int style) {
		super(parent, style);
		setTheme(DateChooserTheme.getDefaultTheme());
		setImage(buttonImage);
		setCreateOnDrop(true);
		pack();
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified when
	 * keys are pressed and released on the system keyboard, by sending it one of
	 * the messages defined in the KeyListener interface.<b>
	 * The listener is set on the Text widget, as there is no sense to have it
	 * on the Composite.
	 * 
	 * @param listener the listener which should be notified
	 */
	public void addKeyListener(KeyListener listener) {
		checkWidget();
		text.addKeyListener(listener);
	}

	/**
	 * Called just before the popup is dropped. The selected date of the
	 * calendar is set to the current date present in the formatted text.
	 * 
	 * @see org.eclipse.nebula.widgets.datechooser.AbstractCombo#beforeDrop()
	 */
	protected void beforeDrop() {
		Date d = (Date) formattedText.getValue();
		DateChooser cal = (DateChooser) popupContent;
		if (d != null) {
			cal.setSelectedDate(d);
			cal.setFocusOnDate(d);
		} else {
			cal.clearSelection();
			cal.setFocusOnToday(false);
		}
	}

	/**
	 * Returns the preferred size of the receiver.<br>
	 * If wHint == SWT.DEFAULT, the preferred size is computed to adjust the width
	 * to display a date in the MM/dd/yyyy format. If a DateFormatter with more
	 * larger edit or display patterns is used, the width of the combo must be
	 * set programmatically.
	 * 
	 * @param wHint the width hint (can be SWT.DEFAULT)
	 * @param hHint the height hint (can be SWT.DEFAULT)
	 * @param changed <code>true</code> if the control's contents have changed, and <code>false</code> otherwise
	 * @return the preferred size of the control.
	 */
	public Point computeSize(int wHint, int hHint, boolean changed) {
		checkWidget();
		Point size = new Point(wHint, hHint);
		Point textSize = text.computeSize(SWT.DEFAULT, SWT.DEFAULT, changed);
		Point buttonSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, changed);
		int borderWidth = getBorderWidth();

		if (wHint == SWT.DEFAULT) {
			GC gc = new GC(formattedText.getControl());
			int width = gc.textExtent("01/01/2000 ").x;
			gc.dispose();
			size.x = width + buttonSize.x + 2 * borderWidth;
		}
		if (hHint == SWT.DEFAULT) {
			if (WIN32) {
				buttonSize.y = ((GridData) button.getLayoutData()).heightHint;
			}
			size.y = Math.max(textSize.y, buttonSize.y) + 2 * borderWidth;
		}

		return size;
	}

	/**
	 * Creates the button widget. The default appearance with an arrow is
	 * replaced by a button with an image.
	 * 
	 * @param style button style
	 * @return the created Button control
	 * @see org.eclipse.nebula.widgets.datechooser.AbstractCombo#createButtonControl(int)
	 */
	protected Button createButtonControl(int style) {
		style &= ~(SWT.ARROW | SWT.DOWN);
		return new Button(this, style | SWT.PUSH);
	}

	/**
	 * Creates the popup content. The content is a <code>DateChooser</code>.
	 * 
	 * @param parent The parent Composite that will contain the control
	 * @return The created Control for the popup content
	 */
	protected Control createPopupContent(Composite parent) {
		DateChooser cal = new DateChooser(parent, SWT.NONE);
		cal.setTheme(theme);
		if (locale != null) {
			cal.setLocale(locale);
		}
		cal.setGridVisible(gridVisible);
		cal.setFooterVisible(footerVisible);
		cal.setWeeksVisible(weeksVisible);
		cal.setAutoSelectOnFooter(true);
		cal.pack();
		return cal;
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
	 * @return the created Text control
	 * @see org.eclipse.nebula.widgets.datechooser.AbstractCombo#createTextControl(int)
	 */
	protected Text createTextControl(int style) {
		formattedText = new FormattedText(this, SWT.NONE);
		formattedText.setFormatter(DefaultFormatterFactory.createFormatter(Date.class));
		return formattedText.getControl();
	}

	/**
	 * This method is called when a SWT.Selection is notify in the popup content,
	 * allowing to update the Text widget content.
	 * 
	 * @return true if the SWT.Selection event must be propagated, else false
	 */
	protected boolean doSelection() {
		formattedText.setValue(((DateChooser) popupContent).getSelectedDate());
		return true;
	}

	/**
	 * @see org.eclipse.nebula.widgets.datechooser.AbstractCombo#dropDown(boolean)
	 */
	protected void dropDown(boolean drop) {
		super.dropDown(drop);
		if (drop && GTK) {
			/*
			 * Bug GTK. When the popup is displayed, the calendar does not gain the
			 * focus until the user click into it with the mouse. Then the keyboard
			 * is unusable.
			 */
			popupContent.traverse(SWT.TRAVERSE_TAB_NEXT);
		}
	}

	/**
	 * Returns the grid visibility status.
	 * 
	 * @return Returns the grid visible status.
	 */
	public int getGridVisible() {
		checkWidget();
		return gridVisible;
	}

	/**
	 * Returns the current <code>Date</code> value of the widget.
	 * <p>
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
		return footerVisible;
	}

	/**
	 * Returns true if grid is visible in the calendar popup.
	 * 
	 * @return Returns the grid visible status.
	 * @deprecated
	 */
	public boolean isGridVisible() {
		checkWidget();
		return gridVisible == DateChooser.GRID_FULL;
	}

	/**
	 * Returns true if weeks numbers are visible.
	 * 
	 * @return Returns the weeks numbers visible status.
	 */
	public boolean isWeeksVisible() {
		checkWidget();
		return weeksVisible;
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when keys are pressed and released on the system keyboard.
	 *
	 * @param listener the listener which should no longer be notified
	 */
	public void removeKeyListener(KeyListener listener) {
		checkWidget();
		formattedText.getControl().removeKeyListener(listener);
	}

	/**
	 * Sets the footer of popup calendar visible or not. The footer displays the
	 * today date. It is not visible by default.
	 * 
	 * @param footerVisible <code>true</code> to set footer visible, else <code>false</code>
	 */
	public void setFooterVisible(boolean footerVisible) {
		checkWidget();
		this.footerVisible = footerVisible;
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
		this.locale = formatter.getLocale();
	}

	/**
	 * Sets the grid visible or not in the calendar popup. By default, the grid
	 * is visible.
	 * 
	 * @param gridVisible <code>true</code> to set grid visible, else <code>false</code>
	 * @deprecated
	 */
	public void setGridVisible(boolean gridVisible) {
		setGridVisible(gridVisible ? DateChooser.GRID_FULL : DateChooser.GRID_NONE);
	}

	/**
	 * Sets the grid visible or not. By default, the grid is visible. The
	 * possible values are GRID_FULL, GRID_LINES and GRID_NONE.
	 * 
	 * @param gridVisible grid visibility flag
	 */
	public void setGridVisible(int gridVisible) {
		this.gridVisible = gridVisible;
	}

	/**
	 * Sets a new image to display on the button, replacing the default one.
	 * 
	 * @param image new image
	 */
	public void setImage(Image image) {
		checkWidget();
		if (image == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		GridData buttonLayout = (GridData) button.getLayoutData();
		if (WIN32) {
			ImageData id = image.getImageData();
			buttonLayout.widthHint = id.width + 4;
			buttonLayout.heightHint = id.height + 6;
		}
		button.setImage(image);
		pack();
	}

	/**
	 * Sets the locale used both by the input mask and the calendar.
	 * 
	 * @param locale locale
	 */
	public void setLocale(Locale locale) {
		checkWidget();
		this.locale = locale;
		((DateFormatter) formattedText.getFormatter()).setLocale(locale);
	}

	/**
	 * Sets the theme to apply to the calendar popup.
	 * 
	 * @param theme new theme (must not be null)
	 */
	public void setTheme(DateChooserTheme theme) {
		checkWidget();
		if (theme == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		this.theme = theme;
		this.gridVisible = theme.gridVisible;
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

	/**
	 * Sets the weeks numbers visible or not. By default, the weeks are NOT
	 * visible.
	 * 
	 * @param weeksVisible <code>true</code> to set weeks visible, else <code>false</code>
	 */
	public void setWeeksVisible(boolean weeksVisible) {
		checkWidget();
		this.weeksVisible = weeksVisible;
	}
}
