/*******************************************************************************
 * Copyright (c) 2011,2012 Laurent CARON
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - Initial
 * implementation and API Marnix van Bochove (mgvanbochove at gmail dot com) -
 * Enhancements and bug fixes
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.checkboxgroup;

import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.nebula.widgets.opal.commons.SelectionListenerUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Widget;

/**
 * Instances of this class provide an etched border with a title and a checkbox.
 * If the checkbox is checked, the content of the composite is enabled. If the
 * checkbox is unchecked, the content of the composite is disabled, thus not
 * editable.
 * <p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>BORDER</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 */
public class CheckBoxGroup extends Canvas implements PaintListener {
	protected Button button;
	private final Composite content;

	private boolean transparent = false;

	/**
	 * Constructs a new instance of this class given its parent and a style value
	 * describing its behavior and appearance.
	 * <p>
	 * The style value is either one of the style constants defined in class
	 * <code>SWT</code> which is applicable to instances of this class, or must be
	 * built by <em>bitwise OR</em>'ing together (that is, using the
	 * <code>int</code> "|" operator) two or more of those <code>SWT</code> style
	 * constants. The class description lists the style constants that are
	 * applicable to the class. Style bits are also inherited from superclasses.
	 * </p>
	 *
	 * @param parent a widget which will be the parent of the new instance (cannot
	 *            be null)
	 * @param style the style of widget to construct
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the parent</li>
	 *                </ul>
	 *
	 * @see Composite#Composite(Composite, int)
	 * @see SWT#BORDER
	 * @see Widget#getStyle
	 */
	public CheckBoxGroup(final Composite parent, final int style) {
		super(parent, style);
		super.setLayout(new GridLayout());

		createCheckBoxButton();

		content = new Composite(this, style);
		content.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		addPaintListener(this);
	}

	private void createCheckBoxButton() {
		button = new Button(this, SWT.CHECK);
		final GridData gdButton = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gdButton.horizontalIndent = 15;
		button.setLayoutData(gdButton);
		button.setSelection(true);
		button.setBackground(getBackground());
		button.pack();

		button.addListener(SWT.Selection, e -> {
			e.doit = SelectionListenerUtil.fireSelectionListeners(this,e);
			if (!e.doit) {
				return;
			}
			if (button.getSelection()) {
				activate();
			} else {
				deactivate();
			}
		});
	}

	/**
	 * Activate the content
	 */
	public void activate() {
		button.setSelection(true);
		SWTGraphicUtil.enableAllChildrenWidgets(content);
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified when
	 * the user changes the receiver's selection, by sending it one of the messages
	 * defined in the <code>SelectionListener</code> interface.
	 * <p>
	 * When <code>widgetSelected</code> is called, the item field of the event
	 * object is valid. If the receiver has the <code>SWT.CHECK</code> style and the
	 * check selection changes, the event object detail field contains the value
	 * <code>SWT.CHECK</code>. <code>widgetDefaultSelected</code> is typically
	 * called when an item is double-clicked. The item field of the event object is
	 * valid for default selection, but the detail field is not used.
	 * </p>
	 *
	 * @param listener the listener which should be notified when the user changes
	 *            the receiver's selection
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see SelectionListener
	 * @see #removeSelectionListener
	 * @see SelectionEvent
	 */
	public void addSelectionListener(final SelectionListener listener) {
		checkWidget();
		SelectionListenerUtil.addSelectionListener(this, listener);	
	}

	/**
	 * Deactivate the content
	 */
	public void deactivate() {
		button.setSelection(false);
		SWTGraphicUtil.disableAllChildrenWidgets(content);
	}

	/**
	 * @return <code>true</code> if the content is activated, <code>false</code>
	 *         otherwise
	 */
	public boolean isActivated() {
		return button.getSelection();
	}

	/**
	 * @see org.eclipse.swt.widgets.Composite#getLayout()
	 */
	@Override
	public Layout getLayout() {
		return content.getLayout();
	}

	/**
	 * Removes the listener from the collection of listeners who will be notified
	 * when the user changes the receiver's selection.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see SelectionListener
	 * @see #addSelectionListener
	 */
	public void removeSelectionListener(final SelectionListener listener) {
		checkWidget();
		SelectionListenerUtil.removeSelectionListener(this, listener);
	}

	
	
	/** 
	 * @see org.eclipse.swt.widgets.Control#setBackground(org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setBackground(final Color color) {
		super.setBackground(color);
		button.setBackground(color);
	}

	/**
	 * @see org.eclipse.swt.widgets.Composite#setFocus()
	 */
	@Override
	public boolean setFocus() {
		return content.setFocus();
	}

	/**
	 * @see org.eclipse.swt.widgets.Composite#setLayout(org.eclipse.swt.widgets.Layout)
	 */
	@Override
	public void setLayout(final Layout layout) {
		content.setLayout(layout);
	}

	// ------------------------------------ Getters and Setters

	/**
	 * @return the text of the button
	 */
	public String getText() {
		return button.getText();
	}

	/**
	 * @param text the text of the button to set
	 */
	public void setText(final String text) {
		button.setText(text);
	}

	/**
	 * @return the font of the button
	 */
	@Override
	public Font getFont() {
		return button.getFont();
	}

	/**
	 * @param font the font to set
	 */
	@Override
	public void setFont(final Font font) {
		button.setFont(font);
	}

	/**
	 * @return the content of the group
	 */
	public Composite getContent() {
		return content;
	}

	public boolean isTransparent() {
		return transparent;
	}

	public void setTransparent(final boolean transparent) {
		this.transparent = transparent;
		if (transparent) {
			setBackgroundMode(SWT.INHERIT_DEFAULT);
			content.setBackgroundMode(SWT.INHERIT_DEFAULT);
		}
	}

	@Override
	public void paintControl(final PaintEvent paintEvent) {
		if (paintEvent.widget == this) {
			drawWidget(paintEvent.gc);
		}
	}

	/**
	 * Draws the widget
	 */
	private void drawWidget(final GC gc) {
		final Rectangle rect = getClientArea();
		final int margin = (int) (button.getSize().y * 1.5);
		final int startY = margin / 2;

		gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		gc.drawRoundRectangle(1, startY, rect.width - 2, rect.height - startY - 2, 2, 2);

		gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		gc.drawRoundRectangle(2, startY + 1, rect.width - 4, rect.height - startY - 4, 2, 2);
	}

	/**
	 * Sets the selection state of the receiver
	 *
	 * @param selection the new selection state
	 */
	public void setSelection(boolean selection) {
		if (selection) {// TODO
			activate();
		} else {
			deactivate();
		}
	}

	/**
	 * Returns <code>true</code> if the receiver is selected, and false otherwise
	 *
	 * @return the selection state
	 */
	public boolean getSelection() {
		return isActivated();
	}
}
