/*******************************************************************************
 * Copyright (c) 2008 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 248956)
 *******************************************************************************/

package org.eclipse.nebula.widgets.radiogroup;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;

/**
 * Instances of this class represent a selectable user interface object that
 * represents an radio button in a radio group.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 */
public class RadioItem extends Item {
	private RadioGroup parent;
	private Button button;

	public RadioItem(RadioGroup parent, int style) {
		this(parent, style, -1);
	}

	public RadioItem(final RadioGroup parent, int style, int index) {
		super(parent, style, checkIndex(parent, index));
		this.parent = parent;

		Listener disposeListener = new Listener() {
			public void handleEvent(Event event) {
				dispose();
			}
		};
		Listener selectionListener = new Listener() {
			public void handleEvent(Event event) {
				parent.itemSelected(RadioItem.this, event);
			}
		};

		button = new Button(parent, parent.buttonStyle);

		button.addListener(SWT.Selection, selectionListener);
		parent.addListener(SWT.Dispose, disposeListener);
		button.addListener(SWT.Dispose, disposeListener);

		addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event event) {
				if (parent != null) {
					parent.removeItem(RadioItem.this);
				}
				if (button != null) {
					button.dispose();
					if (parent != null && !parent.isDisposed())
						parent.layout(false);
				}
				RadioItem.this.parent = null;
				button = null;
			}
		});
		parent.addItem(this, index);
	}

	private static int checkIndex(RadioGroup parent, int position) {
		if (position == -1)
			return parent.getItemCount();
		if (position < 0 || position > parent.getItemCount())
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		return position;
	}
	
	Button getButton() {
		return button;
	}

	public RadioGroup getParent() {
		return parent;
	}

	public String getText() {
		checkWidget();
		return button.getText();
	}

	public void setText(String string) {
		checkWidget();
		button.setText(string);
		parent.layout(new Control[] { button });
	}

	public Image getImage() {
		checkWidget();
		return button.getImage();
	}

	public void setImage(Image image) {
		checkWidget();
		button.setImage(image);
		parent.layout(new Control[] { button });
	}

	public Color getForeground() {
		checkWidget();
		return button.getForeground();
	}

	public void setForeground(Color foreground) {
		checkWidget();
		if (foreground == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		button.setForeground(foreground);
	}

	public Color getBackground() {
		checkWidget();
		return button.getBackground();
	}

	public void setBackground(Color background) {
		checkWidget();
		if (background == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		button.setBackground(background);
	}

	public Font getFont() {
		checkWidget();
		return button.getFont();
	}

	public void setFont(Font font) {
		checkWidget();
		if (font == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		button.setFont(font);
	}
}
