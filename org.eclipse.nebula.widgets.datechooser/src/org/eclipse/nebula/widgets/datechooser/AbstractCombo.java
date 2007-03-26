/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
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
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
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
	public static final boolean GTK = "gtk".equals(SWT.getPlatform());
	public static final boolean WIN32 = "win32".equals(SWT.getPlatform());

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

	/**
	 * Constructs a new instance of this class given its parent
	 * and a style value describing its behavior and appearance.<p>
	 * 
	 * @param parent a widget which will be the parent of the new instance (cannot be null)
	 * @param style the style of widget to construct
	 */
	public AbstractCombo(Composite parent, int style) {
		super(parent, style);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = layout.marginWidth = layout.marginHeight = 0;
		super.setLayout(layout);

		// Creates the Text widget
		int textStyle = SWT.SINGLE;
		if ( (style & SWT.READ_ONLY) != 0 ) textStyle |= SWT.READ_ONLY;
		if ( (style & SWT.FLAT) != 0 ) textStyle |= SWT.FLAT;
		createTextControl(textStyle);
    GridData data = new GridData(GridData.FILL_BOTH);
    text.setLayoutData(data);

		int buttonStyle = SWT.ARROW | SWT.DOWN;
		if ( (style & SWT.FLAT) != 0 )
			buttonStyle |= SWT.FLAT;
		createButtonControl(buttonStyle);
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
					handleFocus(SWT.FocusOut);
				}
			}
		};
		filter = new Listener() {
			public void handleEvent(Event event) {
				Shell shell = ((Control)event.widget).getShell ();
				if ( shell == AbstractCombo.this.getShell() ) {
					handleFocus(SWT.FocusOut);
				}
			}
		};

		popup = new Shell(getShell(), SWT.TOOL | SWT.ON_TOP);
		popup.setLayout(new FillLayout());
		createPopupContent();
  	popup.pack();

  	this.addListener(SWT.Dispose, listener);
		this.addListener(SWT.Move, listener);
		button.addListener(SWT.Selection, listener);
		button.addListener(SWT.FocusIn, listener);
		text.addListener(SWT.Modify, listener);
		popup.addListener(SWT.Deactivate, listener);
		popup.addListener(SWT.Close, listener);
		popupContent.addListener(SWT.Selection, listener);
	}

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when the receiver's text is modified, by sending
	 * it one of the messages defined in the <code>ModifyListener</code>
	 * interface.
	 *
	 * @param lsnr the listener which should be notified
	 * @see ModifyListener
	 * @see #removeModifyListener
	 */
	public void addModifyListener(ModifyListener lsnr) {
		checkWidget();
		if ( lsnr == null ) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		TypedListener typedListener = new TypedListener(lsnr);
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
	 * @param lsnr the listener which should be notified
	 * @see SelectionListener
	 * @see #removeSelectionListener
	 */
	public void addSelectionListener(SelectionListener lsnr) {
		checkWidget();
		if ( lsnr == null ) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		TypedListener typedListener = new TypedListener(lsnr);
		addListener(SWT.Selection,typedListener);
		addListener(SWT.DefaultSelection,typedListener);
	}

	/**
	 * Called just before the popup is droppped. Override to execute actions
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
			case SWT.Selection :
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
			case SWT.Dispose :
				if (popup != null && !popup.isDisposed ()) {
					popupContent.removeListener(SWT.Dispose, listener);
					popup.dispose ();
				}
				getShell().removeListener(SWT.Deactivate, listener);
				getDisplay().removeFilter(SWT.FocusIn, filter);
				popup				 = null;
				text				 = null;
				popupContent = null;
				button			 = null;
				break;
			case SWT.Move :
				dropDown(false);
				break;
		}
	}

	/**
	 * Manages popup content events. SelectionEvent are notified to all registered
	 * SelectionListeners of the combo.
	 * 
	 * @param event event
	 */
	protected void contentEvent(Event event) {
		switch (event.type) {
			case SWT.Selection :
				Event e = new Event();
				e.time			= event.time;
				e.stateMask = event.stateMask;
				e.doit			= event.doit;
				e.data			= event.data;
				notifyListeners(SWT.Selection, e);
				event.doit = e.doit;
				break;
		}
	}

	/**
	 * Creates the arrow button widget. Override to change appearance or
	 * behaviour of the button.
	 * 
	 * @param style button style
	 */
	protected void createButtonControl(int style) {
		button = new Button(this, style);
	}

	/**
	 * Creates the popup content. The popup is dependent of each implementation.
	 * Content can be a <code>List</code>, a <code>Table</code> or every other
	 * control.<br>
	 * The <code>popupContent</code> attribute must be setted with the created
	 * control. The parent must be the <code>shell</code> attribute, that is
	 * initialized by default with a <code>FillLayout</code>.
	 */
	protected abstract void createPopupContent();

	/**
	 * Creates the text widget. Override to change appearance or behaviour of
	 * the text.
	 * 
	 * @param style text style
	 */
	protected void createTextControl(int style) {
		text = new Text(this, style);
	}

	/**
	 * Disposes of the operating system resources associated with the receiver
	 * and all its descendents.
	 * 
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	public void dispose() {
		if ( ! popupContent.isDisposed() ) popupContent.dispose();
		if ( ! popup.isDisposed() ) popup.dispose();
		super.dispose();
	}

	/**
	 * Manages drop down of the popup.
	 * 
	 * @param drop <code>true</code> to drop the popup, <code>false</code> to close
	 */
	protected void dropDown(boolean drop) {
		if ( drop == isDropped() ) return;
		if ( !drop ) {
			popup.setVisible(false);
			if ( ! isDisposed() && button.isFocusControl() ) {
				text.setFocus();
			}
			return;
		}

		setPopupLocation();
		beforeDrop();
		popup.setVisible(true);
		popupContent.setFocus();
	}

	/**
	 * Gets the editable state.
	 *
	 * @return whether or not the reciever is editable
	 */
	public boolean getEditable() {
		checkWidget();
		return text.getEditable();
	}

	/**
	 * Returns a <code>Point</code> whose x coordinate is the start of the
	 * selection in the receiver's text field, and whose y coordinate is the end
	 * of the selection. The returned values are zero-relative. An "empty"
	 * selection as indicated by the the x and y coordinates having the same
	 * value.
	 *
	 * @return a point representing the selection start and end
	 */
	public Point getSelection() {
		checkWidget();
		return text.getSelection();
	}

	/**
	 * Returns the receiver's style information.
	 * 
	 * @see org.eclipse.swt.widgets.Widget#getStyle()
	 */
	public int getStyle() {
		int style = super.getStyle();
		style &= ~SWT.READ_ONLY;
		if ( !text.getEditable() ) style |= SWT.READ_ONLY; 
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
	 */
	public int getTextHeight() {
		checkWidget();
		return text.getLineHeight();
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
				if ( getEditable() ) text.selectAll();
				hasFocus = true;
				Shell shell = getShell();
				shell.removeListener(SWT.Deactivate, listener);
				shell.addListener(SWT.Deactivate, listener);
				Display display = getDisplay();
				display.removeFilter(SWT.FocusIn, filter);
				notifyListeners(SWT.FocusIn, new Event());
				display.addFilter(SWT.FocusIn, filter);
				break;
			}
			case SWT.FocusOut : {
				if ( ! hasFocus ) return;
				Control focusControl = getDisplay().getFocusControl();
				if ( focusControl == button || popupContent.isFocusControl() || focusControl == text) return;
				hasFocus = false;
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
		return popup.getVisible();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Control#isFocusControl()
	 */
	public boolean isFocusControl() {
		checkWidget();
		if ( text.isFocusControl() || button.isFocusControl()
				 || popupContent.isFocusControl() || popup.isFocusControl() ) {
			return true;
		} 
		return super.isFocusControl();
	}

	/**
	 * Manages popup shell events.
	 * 
	 * @param event event
	 */
	protected void popupEvent(Event event) {
		switch (event.type) {
			case SWT.Deactivate :
				dropDown(false);
				break;
			case SWT.Close :
				event.doit = false;
				dropDown(false);
				break;
		}
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when the receiver's text is modified.
	 *
	 * @param lsnr the listener which should no longer be notified
	 * @see ModifyListener
	 * @see #addModifyListener
	 */
	public void removeModifyListener(ModifyListener lsnr) {
		checkWidget();
		if ( lsnr == null ) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		removeListener(SWT.Modify, lsnr);	
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when the receiver's selection changes.
	 *
	 * @param lsnr the listener which should no longer be notified
	 * @see SelectionListener
	 * @see #addSelectionListener
	 */
	public void removeSelectionListener(SelectionListener lsnr) {
		checkWidget();
		if ( lsnr == null ) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		removeListener(SWT.Selection, lsnr);
		removeListener(SWT.DefaultSelection,lsnr);	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Control#setBackground(org.eclipse.swt.graphics.Color)
	 */
	public void setBackground(Color color) {
		super.setBackground(color);
		if ( text != null ) text.setBackground(color);
		if ( popupContent != null ) popupContent.setBackground(color);
		if ( button != null ) button.setBackground(color);
	}

	/**
	 * Sets the editable state.
	 *
	 * @param editable the new editable state
	 */
	public void setEditable(boolean editable) {
		checkWidget();
		text.setEditable(editable);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Control#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if ( popup != null ) popup.setVisible(false);
		if ( text != null ) text.setEnabled(enabled);
		if ( button != null ) button.setEnabled(enabled);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Control#setFocus()
	 */
	public boolean setFocus() {
		checkWidget();
		return text.setFocus();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Control#setFont(org.eclipse.swt.graphics.Font)
	 */
	public void setFont(Font font) {
		super.setFont(font);
		text.setFont(font);
		popupContent.setFont(font);
		pack();
  	popup.pack();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Control#setForeground(org.eclipse.swt.graphics.Color)
	 */
	public void setForeground(Color color) {
		super.setForeground(color);
		if ( text != null ) text.setForeground(color);
		if ( popupContent != null ) popupContent.setForeground(color);
		if ( button != null ) button.setForeground(color);
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
	 * Calculates and returns the location of popup.<br>
	 * Called just before than the popup is dropped.
	 */
	protected void setPopupLocation() {
  	Rectangle r = getBounds();
  	Point p = Display.getCurrent().map(this, null, 0, r.height);
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
		if ( selection == null ) SWT.error (SWT.ERROR_NULL_ARGUMENT);
		text.setSelection(selection.x, selection.y);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Control#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if ( isDisposed () ) return;
		if ( !visible ) popup.setVisible(false);
	}

	/**
	 * Manages text widget events. ModifyEvent are notified to all registered
	 * ModifyListener of the combo.
	 * 
	 * @param event event
	 */
	protected void textEvent(Event event) {
		switch (event.type) {
			case SWT.Modify :
				Event e = new Event ();
				e.time = event.time;
				notifyListeners(SWT.Modify, e);
				break;
		}
	}
}
