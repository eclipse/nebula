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

public class VButton extends VControl {
	
	/** Part state selected */
	public static final int SELECTED = 1 << 1;
	/** Part state focused */
	public static final int FOCUSED = 1 << 2;
	/** Part state pressed */
	public static final int PRESSED = 1 << 3;
	/** Part state active */
	public static final int ACTIVE = 1 << 4;
	/** Part state disabled */
	public static final int DISABLED = 1 << 5;
	/** Part state hot */
	public static final int HOT = 1 << 6;
	/** Part state defaulted */
	public static final int DEFAULTED = 1 << 7;
	/** Part state grayed */
	public static final int GRAYED = 1 << 8;


	ImageData oldImageData;
	boolean paintNative = false;
	boolean paintInactive = false;
	private boolean armed = false;

	public VButton(VPanel panel, int style) {
		super(panel, style);
		if(!hasStyle(SWT.CHECK | SWT.RADIO | SWT.TOGGLE)) {
			setStyle(SWT.PUSH, true);
		}
		
		setPainter(new VButtonPainter());
		
		addListener(SWT.MouseDown);
		addListener(SWT.MouseUp);
	}

	public boolean getNativeBackground() {
		return paintNative;
	}

	public boolean getSelection() {
		return hasState(STATE_SELECTED);
	}

	@Override
	public Type getType() {
		return VControl.Type.Button;
	}

	protected void filterEvent(Event event) {
		if(hasState(STATE_ACTIVE)) {
			switch(event.type) {
			case SWT.MouseDown:
				setFocus();
				
				if(event.button != 1) {
					break;
				}
	
				if(hasStyle(SWT.PUSH)) {
					if(setState(STATE_SELECTED, true)) {
						redraw();
					}
					armed = true;
				} else {
					setState(STATE_SELECTED, !hasState(STATE_SELECTED));
	
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
	
					notifyListeners(SWT.Selection, e);
					
					redraw();
				}
				break;
			case SWT.MouseUp:
				if(event.button != 1) {
					break;
				}
	
				if(hasStyle(SWT.PUSH)) {
					if(setState(STATE_SELECTED, false)) {
						redraw();
					}
				}
				if(armed) {
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
	
					notifyListeners(SWT.Selection, e);
				}
				armed = false;
				break;
			}
		}
	}

	public void setPaintInactive(boolean paintInactive) {
		this.paintInactive = paintInactive;
	}

	public void setPaintNative(boolean paintNative) {
		this.paintNative = paintNative;
	}

	public void setSelection(boolean select) {
		if(!hasStyle(SWT.PUSH)) {
			if(setState(STATE_SELECTED, select)) {
				redraw();
			}
		}
	}

}
