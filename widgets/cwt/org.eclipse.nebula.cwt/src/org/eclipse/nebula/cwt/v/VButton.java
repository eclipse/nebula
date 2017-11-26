/****************************************************************************
 * Copyright (c) 2008, 2009 Jeremy Dowdall
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/

package org.eclipse.nebula.cwt.v;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Event;

/**
 * Control to mimic a button.
 */
public class VButton extends VControl {

	ImageData oldImageData;
	boolean paintNative = false;
	boolean paintInactive = false;
	private boolean armed = false;

	public VButton(VPanel panel, int style) {
		super(panel, style);
		if (!hasStyle(SWT.CHECK | SWT.RADIO | SWT.TOGGLE)) {
			setStyle(SWT.PUSH, true);
		}
		setPainter(new VButtonPainter());
		addListener(SWT.MouseDown);
		addListener(SWT.MouseUp);
	}

	/**
	 * @see #setPaintNative(boolean)
	 */
	public boolean getNativeBackground() {
		return paintNative;
	}

	/**
	 * @return true if this button is currently selected.
	 */
	public boolean getSelection() {
		return hasState(STATE_SELECTED);
	}

	@Override
	public Type getType() {
		return VControl.Type.Button;
	}

	protected void filterEvent(Event event) {
		if (hasState(STATE_ACTIVE)) {
			switch (event.type) {
			case SWT.MouseDown:
				setFocus();

				if (event.button != 1) {
					break;
				}

				if (hasStyle(SWT.PUSH)) {
					if (setState(STATE_SELECTED, true)) {
						redraw();
					}
					armed = true;
				} else {
					setState(STATE_SELECTED, !hasState(STATE_SELECTED));
					Event e = createEvent(event);
					notifyListeners(SWT.Selection, e);
					redraw();
				}
				break;

			case SWT.MouseUp:
				if (event.button != 1) {
					break;
				}

				if (hasStyle(SWT.PUSH)) {
					if (setState(STATE_SELECTED, false)) {
						redraw();
					}
				}
				if (armed) {
					Event e = createEvent(event);
					notifyListeners(SWT.Selection, e);
				}
				armed = false;
				break;
			}
		}
	}

	private Event createEvent(Event event) {
		Event e = new Event();
		e.type = SWT.Selection;
		e.data = VButton.this;
		e.button = event.button;
		e.detail = event.detail;
		e.display = event.display;
		e.gc = event.gc;
		e.height = event.height;
		e.stateMask = event.stateMask;
		e.time = event.time;
		e.width = event.width;
		e.x = event.x;
		e.y = event.y;
		return e;
	}

	/**
	 * @param paintInactive
	 *            true to paint the button in an inactive state.
	 */
	public void setPaintInactive(boolean paintInactive) {
		this.paintInactive = paintInactive;
	}

	/**
	 * Setting this value currently does nothing but subclasses can take
	 * advantage of this flag.
	 * <p/>
	 * Painting native is the act of letting the underlying OS take care of
	 * painting the background in some way or another.
	 * 
	 * @param paintNative
	 *            true to paint native
	 */
	public void setPaintNative(boolean paintNative) {
		this.paintNative = paintNative;
	}

	/**
	 * Marks this button as being "selected" by the user.
	 * 
	 * @param select
	 *            true to make the button "selected".
	 */
	public void setSelection(boolean select) {
		if (!hasStyle(SWT.PUSH)) {
			if (setState(STATE_SELECTED, select)) {
				redraw();
			}
		}
	}
}