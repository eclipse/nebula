/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Eric Wuillai    - modification of CCombo into an abstract combo
 *******************************************************************************/
package org.eclipse.nebula.widgets.datechooser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TypedListener;

/**
 * Abstract class for combo widgets composed of a <code>Text</code>, a
 * <code>Button</code> and a popup associated to the button.<p>
 * 
 * The creation of text, button and popup content is delegated to abstract
 * methods.<p>
 * 
 * Note that although this class is a subclass of <code>Composite</code>,
 * it does not make sense to add children to it, or set a layout on it.<p>
 * 
 * <dl>
 * <dt><b>Styles:</b>
 * <dd>BORDER, READ_ONLY, FLAT</dd>
 * <dt><b>Events:</b>
 * <dd>Selection</dd>
 * </dl>
 */
public abstract class AbstractCombo extends Composite {
	/** GTK platform constant */
	public static final boolean GTK = "gtk".equals(SWT.getPlatform());
	/** WIN32 platform constant */
	public static final boolean WIN32 = "win32".equals(SWT.getPlatform());

	/** The parent Shell */
	Shell _shell;
	/** Text widget for the input */
	protected Text text;
	/** Popup shell for the selection */
	protected Shell popup;
	/** Button opening the popup */
	protected Button button;
	/** Content of the popup */
	protected Control popupContent;
	/** Listener for all internal events */
	protected Listener listener;
	/** Listener for external events */
	protected Listener filter;
	/** Flag indicating if the widget has focus or not */
	protected boolean hasFocus;
	/** Flag to show button only if combo has the focus */
	protected boolean showButtonOnFocus = false;
	/** Flag indicating that the popup content must be created each time the combo is dropped */
	protected boolean createOnDrop = false;

	/**
	 * Constructs a new instance of this class given its parent
	 * and a style value describing its behavior and appearance.<p>
	 * 
	 * @param parent a widget which will be the parent of the new instance (cannot be null)
	 * @param style the style of widget to construct
	 */
	public AbstractCombo(Composite parent, int style) {
		super(parent, style = checkStyle (style));
		_shell = super.getShell ();
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = layout.marginWidth = layout.marginHeight = 0;
		super.setLayout(layout);

		// Creates the Text widget
		int textStyle = SWT.SINGLE;
		if ( (style & SWT.READ_ONLY) != 0 ) textStyle |= SWT.READ_ONLY;
		if ( (style & SWT.FLAT) != 0 ) textStyle |= SWT.FLAT;
		text = createTextControl(textStyle);
    GridData data = new GridData(GridData.FILL_BOTH);
    text.setLayoutData(data);

		int buttonStyle = SWT.ARROW | SWT.DOWN;
		if ( (style & SWT.FLAT) != 0 )
			buttonStyle |= SWT.FLAT;
		button = createButtonControl(buttonStyle);
		button.setLayoutData(new GridData(GridData.FILL_VERTICAL));

		listener = new Listener () {
			public void handleEvent (Event event) {
				if ( popup == event.widget ) {
					popupEvent(event);
					return;
				}
				if ( text == event.widget ) {
					textEvent(event);
					return;
				}
				if ( popupContent == event.widget ) {
					contentEvent(event);
					return;
				}
				if ( button == event.widget ) {
					buttonEvent(event);
					return;
				}
				if ( AbstractCombo.this == event.widget ) {
					comboEvent(event);
					return;
				}
				if ( getShell() == event.widget ) {
					getDisplay().asyncExec(new Runnable() {
						public void run() {
							if ( isDisposed () ) return;
							handleFocus (SWT.FocusOut);
						}
					});
				}
			}
		};
		filter = new Listener() {
			public void handleEvent(Event event) {
				if ( isDisposed () ) return;
				Shell shell = ((Control)event.widget).getShell ();
				if ( shell == AbstractCombo.this.getShell() ) {
					handleFocus(SWT.FocusOut);
				}
			}
		};

		// comboEvent
  	this.addListener(SWT.Dispose, listener);
		this.addListener(SWT.FocusIn, listener);
		this.addListener(SWT.Move, listener);
		// textEvent
		text.addListener(SWT.Modify, listener);
		text.addListener(SWT.FocusIn, listener);
		text.addListener(SWT.KeyDown, listener);
		text.addListener(SWT.Traverse, listener);
		// buttonEvent
		button.addListener(SWT.FocusIn, listener);
		button.addListener(SWT.MouseDown, listener);
		button.addListener(SWT.MouseUp, listener);
		button.addListener(SWT.Selection, listener);
	}

	static int checkStyle (int style) {
		int mask = SWT.BORDER | SWT.READ_ONLY | SWT.FLAT | SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT;
		return SWT.NO_FOCUS | (style & mask);
	}

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when the receiver's text is modified, by sending
	 * it one of the messages defined in the <code>ModifyListener</code>
	 * interface.
	 *
	 * @param listener the listener which should be notified
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * @see ModifyListener
	 * @see #removeModifyListener
	 */
	public void addModifyListener(ModifyListener listener) {
		checkWidget();
		if ( listener == null ) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		TypedListener typedListener = new TypedListener(listener);
		addListener(SWT.Modify, typedListener);
	}

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when the receiver's selection changes, by sending
	 * it one of the messages defined in the <code>SelectionListener</code>
	 * interface.
	 * <p>
	 * <code>widgetSelected</code> is called when the combo's list selection changes.
	 * <code>widgetDefaultSelected</code> is typically called when ENTER is pressed the combo's text area.
	 * </p>
	 *
	 * @param listener the listener which should be notified
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * @see SelectionListener
	 * @see #removeSelectionListener
	 * @see SelectionEvent
	 */
	public void addSelectionListener(SelectionListener listener) {
		checkWidget();
		if ( listener == null ) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		TypedListener typedListener = new TypedListener(listener);
		addListener(SWT.Selection,typedListener);
		addListener(SWT.DefaultSelection,typedListener);
	}

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when the receiver's text is verified, by sending
	 * it one of the messages defined in the <code>VerifyListener</code>
	 * interface.
	 *
	 * @param listener the listener which should be notified
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * @see VerifyListener
	 * @see #removeVerifyListener
	 */
	public void addVerifyListener(VerifyListener listener) {
		checkWidget();
		if ( listener == null ) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		TypedListener typedListener = new TypedListener(listener);
		addListener(SWT.Verify, typedListener);
	}

	/**
	 * Called just before the popup is dropped. Override to execute actions
	 * before the apparition of the popup.<p>
	 * 
	 * By default, do nothing.
	 */
	protected void beforeDrop() {
	}

	/**
	 * Manages button events.
	 * 
	 * @param event event
	 */
	protected void buttonEvent(Event event) {
		switch (event.type) {
			case SWT.FocusIn :
				handleFocus(SWT.FocusIn);
				break;
			case SWT.MouseDown : {
				Event mouseEvent = new Event ();
				mouseEvent.button = event.button;
				mouseEvent.count = event.count;
				mouseEvent.stateMask = event.stateMask;
				mouseEvent.time = event.time;
				mouseEvent.x = event.x; mouseEvent.y = event.y;
				notifyListeners (SWT.MouseDown, mouseEvent);
				event.doit = mouseEvent.doit;
				break;
			}
			case SWT.MouseUp : {
				Event mouseEvent = new Event ();
				mouseEvent.button = event.button;
				mouseEvent.count = event.count;
				mouseEvent.stateMask = event.stateMask;
				mouseEvent.time = event.time;
				mouseEvent.x = event.x; mouseEvent.y = event.y;
				notifyListeners (SWT.MouseUp, mouseEvent);
				event.doit = mouseEvent.doit;
				break;
			}
			case SWT.Selection :
				text.setFocus();
				dropDown(!isDropped());
				break;
		}
	}

	/**
	 * Manages global combo events.
	 * 
	 * @param event event
	 */
	protected void comboEvent(Event event) {
		switch (event.type) {
			case SWT.Dispose : {
				removeListener(SWT.Dispose, listener);
				notifyListeners(SWT.Dispose, event);
				event.type = SWT.None;

				if ( popup != null && ! popup.isDisposed() ) {
					popupContent.removeListener(SWT.Dispose, listener);
					popup.dispose();
				}
				getShell().removeListener(SWT.Deactivate, listener);
				getDisplay().removeFilter(SWT.FocusIn, filter);
				popup				 = null;
				text				 = null;
				popupContent = null;
				button			 = null;
				_shell			 = null;
				break;
			}
			case SWT.FocusIn: {
				Control focusControl = getDisplay().getFocusControl();
				if ( focusControl == button || focusControl == popupContent ) return;
				if ( isDropped() ) {
					popupContent.setFocus();
				} else {
					text.setFocus();
				}
				break;
			}
			case SWT.Move :
				dropDown(false);
				break;
		}
	}

	/**
	 * Manages popup content events. SelectionEvent are notified to all
	 * registered SelectionListeners of the combo.
	 * 
	 * @param event event
	 */
	protected void contentEvent(Event event) {
		switch (event.type) {
			case SWT.FocusIn :
				handleFocus(SWT.FocusIn);
				break;
			case SWT.Selection : {
				if ( doSelection() ) {
					dropDown(false);
					Event e = new Event();
					e.time			= event.time;
					e.stateMask = event.stateMask;
					e.doit			= event.doit;
					e.data			= event.data;
					notifyListeners(SWT.Selection, e);
					event.doit = e.doit;
				}
				break;
			}
		}
	}

	/**
	 * Copies the selected text.
	 * <p>
	 * The current selection is copied to the clipboard.
	 * </p>
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void copy() {
		checkWidget();
		text.copy();
	}

	/**
	 * Creates the arrow button widget. Override to change appearance or
	 * behavior of the button.
	 * 
	 * @param style button style
	 * @return The created button
	 */
	protected Button createButtonControl(int style) {
		Button button = new Button(this, style);
		return button;
	}

	/**
	 * Creates the popup that will be displayed when the button is selected.
	 * Popup is a new Shell containing a unique Control. The content is specific
	 * to each implementation of the Combo and is created overriding the
	 * createPopupContent method.
	 */
	protected void createPopup() {
		popup = new Shell(getShell(), SWT.TOOL | SWT.ON_TOP);
		popupContent = createPopupContent(popup);
		popupContent.setFont(text.getFont());
		popupContent.setBackground(text.getBackground());
		popupContent.setForeground(text.getForeground());
		popup.pack();

		popup.addListener(SWT.Close, listener);
		popup.addListener(SWT.Deactivate, listener);
		popupContent.addListener(SWT.FocusIn, listener);
		popupContent.addListener(SWT.Selection, listener);
	}

	/**
	 * Creates the popup content. The popup is dependent of each implementation.
	 * Content can be a <code>List</code>, a <code>Table</code> or every other
	 * control.<br>
	 * The <code>popupContent</code> attribute must be set with the created
	 * control. The parent must be the <code>shell</code> attribute, that is
	 * initialized by default with a <code>FillLayout</code>.
	 * 
	 * @param parent The parent Composite that will contain the control
	 * @return The created Control for the popup content 
	 */
	protected abstract Control createPopupContent(Composite parent);

	/**
	 * Creates the text widget. Override to change appearance or behavior of
	 * the default text that is created here.
	 * 
	 * @param style text style
	 * @return the created Text control
	 */
	protected Text createTextControl(int style) {
		Text text = new Text(this, style);
		return text;
	}

	/**
	 * Cuts the selected text.
	 * <p>
	 * The current selection is first copied to the clipboard and then deleted
	 * from the widget.
	 * </p>
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void cut() {
		checkWidget();
		text.cut();
	}

	/**
	 * This method is called when a SWT.Selection is notify in the popup content,
	 * allowing to update the Text widget content.
	 * 
	 * @return true if the SWT.Selection event must be propagated, else false
	 */
	protected abstract boolean doSelection();

	/**
	 * Manages drop down of the popup.
	 * 
	 * @param drop <code>true</code> to drop the popup, <code>false</code> to close
	 */
	protected void dropDown(boolean drop) {
		if ( drop == isDropped() ) return;

		if ( ! drop ) {
			if ( popup != null ) {
				popup.setVisible(false);
				if ( createOnDrop ) {
					popup.dispose();
					popup = null;
					popupContent = null;
				}
			}
			if ( ! isDisposed () && isFocusControl() ) {
				text.setFocus();
			}
			return;
		}
		if ( ! isVisible() ) return;
		if ( popup == null || getShell() != popup.getParent () ) {
			if ( popup != null ) {
				popup.dispose();
				popup = null;
				popupContent = null;
			}
			createPopup();
		}
		setPopupLocation();
		beforeDrop();
		popup.setVisible(true);
		if ( isFocusControl() ) popupContent.setFocus ();
	}

	/**
	 * Gets the editable state.
	 *
	 * @return whether or not the receiver is editable
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public boolean getEditable() {
		checkWidget();
		return text.getEditable();
	}

	/**
	 * Returns <code>true</code> if the receiver's popup is visible,
	 * and <code>false</code> otherwise.
	 * <p>
	 * If one of the receiver's ancestors is not visible or some
	 * other condition makes the receiver not visible, this method
	 * may still indicate that it is considered visible even though
	 * it may not actually be showing.
	 * </p>
	 *
	 * @return the receiver's popup's visibility state
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public boolean getPopupVisible () {
		checkWidget ();
		return isDropped();
	}

	/**
	 * Returns a <code>Point</code> whose x coordinate is the start of the
	 * selection in the receiver's text field, and whose y coordinate is the end
	 * of the selection. The returned values are zero-relative. An "empty"
	 * selection as indicated by the the x and y coordinates having the same
	 * value.
	 *
	 * @return a point representing the selection start and end
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public Point getSelection() {
		checkWidget();
		return text.getSelection();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Control#getShell()
	 */
	public Shell getShell () {
		checkWidget ();
		Shell shell = super.getShell ();
		if ( shell != _shell ) {
			if ( _shell != null && !_shell.isDisposed () ) {
				_shell.removeListener (SWT.Deactivate, listener);
			}
			_shell = shell;
		}
		return _shell;
	}

	/**
	 * Returns the receiver's style information.
	 * 
	 * @see org.eclipse.swt.widgets.Widget#getStyle()
	 */
	public int getStyle() {
		int style = super.getStyle();
		style &= ~SWT.READ_ONLY;
		if ( ! text.getEditable() ) style |= SWT.READ_ONLY; 
		return style;
	}

	/**
	 * Returns a string containing a copy of the contents of the receiver's text
	 * field.
	 *
	 * @return the receiver's text
	 */
	public String getText() {
		checkWidget();
		return text.getText();
	}

	/**
	 * Returns the height of the receivers's text field.
	 *
	 * @return the text height
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public int getTextHeight() {
		checkWidget();
		return text.getLineHeight();
	}

	/**
	 * Returns the maximum number of characters that the receiver's
	 * text field is capable of holding. If this has not been changed
	 * by <code>setTextLimit()</code>, it will be the constant
	 * <code>Combo.LIMIT</code>.
	 * 
	 * @return the text limit
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public int getTextLimit () {
		checkWidget ();
		return text.getTextLimit ();
	}

	/**
	 * Manages the focus on the combo.
	 * 
	 * @param type SWT.FocusIn or SWT.FocusOut
	 */
	protected void handleFocus(int type) {
		if ( isDisposed() ) return;
		switch (type) {
			case SWT.FocusIn : {
				if ( hasFocus ) return;
				hasFocus = true;
				updateButtonDisplay();
				Shell shell = getShell();
				shell.removeListener(SWT.Deactivate, listener);
				shell.addListener(SWT.Deactivate, listener);
				Display display = getDisplay();
				display.removeFilter(SWT.FocusIn, filter);
				display.addFilter(SWT.FocusIn, filter);
				notifyListeners(SWT.FocusIn, new Event());
				break;
			}
			case SWT.FocusOut : {
				if ( ! hasFocus ) return;
				Control focusControl = getDisplay().getFocusControl();
				if ( focusControl == button
						 || (popupContent != null && popupContent.isFocusControl())
						 || focusControl == text) return;
				hasFocus = false;
				updateButtonDisplay();
				Shell shell = getShell();
				shell.removeListener(SWT.Deactivate, listener);
				getDisplay().removeFilter(SWT.FocusIn, filter);
				notifyListeners(SWT.FocusOut, new Event());
				break;
			}
		}
	}

	/**
	 * Returns <code>true</code> if popup is dropped (visible), else <code>false</code>.
	 * 
	 * @return boolean indicating if popup is dropped
	 */
	protected boolean isDropped() {
		return popup != null && ! popup.isDisposed() && popup.getVisible();
	}

	/**
	 * Returns <code>true</code> if the receiver has the user-interface focus,
	 * and <code>false</code> otherwise.
	 * 
	 * @see org.eclipse.swt.widgets.Control#isFocusControl()
	 */
	public boolean isFocusControl() {
		checkWidget();
		if ( text.isFocusControl() || button.isFocusControl()
				 || (popupContent != null && popupContent.isFocusControl())
				 || (popup != null && popup.isFocusControl()) ) {
			return true;
		} 
		return super.isFocusControl();
	}

	/**
	 * Returns <code>true</code> if button is displayed only when the combo has
	 * has focus and <code>false</code> otherwise.
	 * 
	 * @return boolean indicating if combo must show button only on focus
	 */
	public boolean isShowButtonOnFocus() {
  	return showButtonOnFocus;
  }

	/**
	 * Pastes text from clipboard.
	 * <p>
	 * The selected text is deleted from the widget
	 * and new text inserted from the clipboard.
	 * </p>
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void paste() {
		checkWidget();
		text.paste();
	}

	/**
	 * Manages popup shell events.
	 * 
	 * @param event event
	 */
	protected void popupEvent(Event event) {
		switch (event.type) {
			case SWT.Close :
				event.doit = false;
				dropDown(false);
				break;
			case SWT.Deactivate :
				/*
				 * Bug in GTK. When the arrow button is pressed the popup control receives a
				 * deactivate event and then the arrow button receives a selection event. If 
				 * we hide the popup in the deactivate event, the selection event will show 
				 * it again. To prevent the popup from showing again, we will let the selection 
				 * event of the arrow button hide the popup.
				 * In Windows, hiding the popup during the deactivate causes the deactivate 
				 * to be called twice and the selection event to be disappear.
				 */
				if ( ! "carbon".equals(SWT.getPlatform()) ) {
					Point point = button.toControl(getDisplay().getCursorLocation());
					Point size = button.getSize();
					Rectangle rect = new Rectangle(0, 0, size.x, size.y);
					if ( ! rect.contains(point) ) dropDown(false);
				} else {
					dropDown(false);
				}
				break;
		}
	}

	/**
	 * Causes the entire bounds of the receiver to be marked as needing to be
	 * redrawn. The next time a paint request is processed, the control will be
	 * completely painted, including the background.
	 */
	public void redraw () {
		super.redraw();
		text.redraw();
		button.redraw();
		if ( popup.isVisible() ) popupContent.redraw();
	}

	/**
	 * Causes the rectangular area of the receiver specified by the arguments to
	 * be marked as needing to be redrawn. The next time a paint request is
	 * processed, that area of the receiver will be painted, including the
	 * background. If the all flag is true, any children of the receiver which
	 * intersect with the specified area will also paint their intersecting
	 * areas. If the all flag is false, the children will not be painted.
	 * 
	 * @param x 
	 * @param y 
	 * @param width 
	 * @param height 
	 * @param all 
	 */
	public void redraw (int x, int y, int width, int height, boolean all) {
		super.redraw(x, y, width, height, true);
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when the receiver's text is modified.
	 *
	 * @param listener the listener which should no longer be notified
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * @see ModifyListener
	 * @see #addModifyListener
	 */
	public void removeModifyListener(ModifyListener listener) {
		checkWidget();
		if ( listener == null ) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		removeListener(SWT.Modify, listener);	
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when the receiver's selection changes.
	 *
	 * @param listener the listener which should no longer be notified
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * @see SelectionListener
	 * @see #addSelectionListener
	 */
	public void removeSelectionListener(SelectionListener listener) {
		checkWidget();
		if ( listener == null ) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		removeListener(SWT.Selection, listener);
		removeListener(SWT.DefaultSelection,listener);	
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when the control is verified.
	 *
	 * @param listener the listener which should no longer be notified
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * @see VerifyListener
	 * @see #addVerifyListener
	 */
	public void removeVerifyListener(VerifyListener listener) {
		checkWidget();
		if ( listener == null ) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		removeListener(SWT.Verify, listener);
	}

	/**
	 * Selects all the text in the receiver.
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void selectAll() {
		checkWidget();
		text.selectAll();
	}

	/**
	 * Sets the receiver's background color to the color specified by the
	 * argument, or to the default system color for the control if the argument
	 * is null.
	 * 
	 * @see org.eclipse.swt.widgets.Control#setBackground(org.eclipse.swt.graphics.Color)
	 */
	public void setBackground(Color color) {
		super.setBackground(color);
		if ( text != null ) text.setBackground(color);
		if ( button != null ) button.setBackground(color);
		if ( popupContent != null ) popupContent.setBackground(color);
	}

	/**
	 * Sets the flag indicating if the popup content must be created each time
	 * the combo is dropped.
	 * It can be necessary to set this flag to true when the popup content use
	 * to many controls, as the DateChooser.
	 * 
	 * @param createOnDrop true if popup is recreated at each drop, else false
	 */
	protected void setCreateOnDrop(boolean createOnDrop) {
		this.createOnDrop = createOnDrop;
	}

	/**
	 * Sets the editable state.
	 *
	 * @param editable the new editable state
	 */
	public void setEditable(boolean editable) {
		checkWidget();
		text.setEditable(editable);
		button.setEnabled(editable);
	}

	/**
	 * Enables the receiver if the argument is <code>true</code>, and disables it
	 * otherwise.
	 * 
	 * @see org.eclipse.swt.widgets.Control#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if ( isDropped() ) dropDown(false);
		if ( text != null ) text.setEnabled(enabled);
		if ( button != null ) button.setEnabled(enabled);
	}

	/**
	 * Causes the receiver to have the keyboard focus, such that all keyboard
	 * events will be delivered to it.
	 * 
	 * @see org.eclipse.swt.widgets.Control#setFocus()
	 */
	public boolean setFocus() {
		checkWidget();
		if ( ! isEnabled () || ! isVisible () ) return false;
		if ( isFocusControl () ) return true;
		return text.setFocus();
	}

	/**
	 * Sets the font that the receiver will use to paint textual information to
	 * the font specified by the argument, or to the default font for that kind
	 * of control if the argument is null.
	 * 
	 * @see org.eclipse.swt.widgets.Control#setFont(org.eclipse.swt.graphics.Font)
	 */
	public void setFont(Font font) {
		super.setFont(font);
		text.setFont(font);
		if ( popupContent != null ) {
			popupContent.setFont(font);
		}
		pack();
	}

	/**
	 * Sets the receiver's foreground color to the color specified by the
	 * argument, or to the default system color for the control if the argument
	 * is null.
	 * 
	 * @see org.eclipse.swt.widgets.Control#setForeground(org.eclipse.swt.graphics.Color)
	 */
	public void setForeground(Color color) {
		super.setForeground(color);
		if ( text != null ) text.setForeground(color);
		if ( button != null ) button.setForeground(color);
		if ( popupContent != null ) popupContent.setForeground(color);
	}

	/**
	 * Sets the layout which is associated with the receiver to be
	 * the argument which may be null.
	 * <p>
	 * Note : No Layout can be set on this Control because it already
	 * manages the size and position of its children.
	 * </p>
	 *
	 * @param layout the receiver's new layout or null
	 */
	public void setLayout(Layout layout) {
		checkWidget();
		return;
	}

	/**
	 * Sets the receiver's pop up menu to the argument. All controls may
	 * optionally have a pop up menu that is displayed when the user requests
	 * one for the control. The sequence of key strokes, button presses and/or
	 * button releases that are used to request a pop up menu is platform
	 * specific.
	 * 
	 * Note: Disposing of a control that has a pop up menu will dispose of the
	 * menu. To avoid this behavior, set the menu to null before the control
	 * is disposed.
	 * 
	 * @param menu the new pop up menu
	 */
	public void setMenu(Menu menu) {
		text.setMenu(menu);
	}

	/**
	 * Calculates and returns the location of popup.<br>
	 * Called just before than the popup is dropped.
	 */
	protected void setPopupLocation() {
		Display display = Display.getCurrent();
  	Rectangle r = getBounds();
  	Point p = display.map(this, null, 0, r.height);
  	Rectangle sb = display.getBounds();
  	if ( p.y + popup.getSize().y > sb.height ) {
  		p.y -= r.height + popup.getSize().y + getBorderWidth();
  	}
  	int popx = popup.getSize().x;
  	if ( p.x + popx > sb.width ) {
  		p.x -= popx - r.width + getBorderWidth();
  	} else if ( popx < r.width ) {
  		p.x += r.width - popx;
  	}
  	popup.setLocation(p.x, p.y);
  }

	/**
	 * Sets the selection in the receiver's text field to the
	 * range specified by the argument whose x coordinate is the
	 * start of the selection and whose y coordinate is the end
	 * of the selection. 
	 *
	 * @param selection a point representing the new selection start and end
	 */
	public void setSelection(Point selection) {
		checkWidget();
		if ( selection == null ) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		text.setSelection(selection.x, selection.y);
	}

	/**
	 * Sets the flag indicating if the button must be shown only if the combo
	 * has the focus (<code>true</code>).
	 * 
	 * @param showButtonOnFocus <code>true<code> if button must be shown on focus only
	 */
	public void setShowButtonOnFocus(boolean showButtonOnFocus) {
  	this.showButtonOnFocus = showButtonOnFocus;
  	updateButtonDisplay();
  }

	/**
	 * Sets the maximum number of characters that the receiver's
	 * text field is capable of holding to be the argument.
	 *
	 * @param limit new text limit
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_CANNOT_BE_ZERO - if the limit is zero</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void setTextLimit(int limit) {
		checkWidget();
		text.setTextLimit(limit);
	}

	/**
	 * Sets the receiver's tool tip text to the argument, which may be null
	 * indicating that no tool tip text should be shown.
	 * 
	 * @param string the new tool tip text (or null)
	 */
	public void setToolTipText(String string) {
		checkWidget();
		super.setToolTipText(string);
		button.setToolTipText(string);
		text.setToolTipText(string);		
	}

	/**
	 * Marks the receiver as visible if the argument is <code>true</code>, and
	 * marks it invisible otherwise.
	 * 
	 * @see org.eclipse.swt.widgets.Control#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if ( isDisposed() ) return;
		if ( popup == null || popup.isDisposed() ) return;
		if ( ! visible ) {
			popup.setVisible(false);
		}
	}

	/**
	 * Manages text widget events. ModifyEvent are notified to all registered
	 * ModifyListener of the combo.
	 * 
	 * @param event event
	 */
	protected void textEvent(Event event) {
		switch ( event.type ) {
			case SWT.FocusIn :
				handleFocus(SWT.FocusIn);
				break;
			case SWT.Modify : {
				Event e = new Event ();
				e.time = event.time;
				notifyListeners(SWT.Modify, e);
				break;
			}
			case SWT.KeyDown : {
				Event keyEvent = new Event ();
				keyEvent.time      = event.time;
				keyEvent.character = event.character;
				keyEvent.keyCode   = event.keyCode;
				keyEvent.stateMask = event.stateMask;
				notifyListeners(SWT.KeyDown, keyEvent);
				if ( (event.stateMask & SWT.ALT) != 0 && event.keyCode == SWT.ARROW_DOWN ) {
					event.doit = false;
					dropDown(true);
				}
				break;
			}
			case SWT.Traverse : {
				switch ( event.detail ) {
					case SWT.TRAVERSE_ARROW_PREVIOUS:
					case SWT.TRAVERSE_ARROW_NEXT:
						// The enter causes default selection and
						// the arrow keys are used to manipulate the list contents so
						// do not use them for traversal.
						event.doit = false;
						break;
					case SWT.TRAVERSE_TAB_PREVIOUS:
						event.doit = traverse(SWT.TRAVERSE_TAB_PREVIOUS);
						event.detail = SWT.TRAVERSE_NONE;
						return;
				}		
				Event e = new Event ();
				e.time = event.time;
				e.detail = event.detail;
				e.doit = event.doit;
				e.character = event.character;
				e.keyCode = event.keyCode;
				notifyListeners(SWT.Traverse, e);
				event.doit = e.doit;
				event.detail = e.detail;
				break;
			}
		}
	}

	/**
	 * Updates the visibility of the button in function of the flag and the focus.
	 */
	protected void updateButtonDisplay() {
		if ( showButtonOnFocus ) {
			GridData data = (GridData) button.getLayoutData();
			data.exclude = ! hasFocus;
			button.setVisible(hasFocus);
			super.layout(false);
		}
	}
}
