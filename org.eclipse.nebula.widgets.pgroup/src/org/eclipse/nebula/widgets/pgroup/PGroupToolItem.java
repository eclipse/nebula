/*******************************************************************************
 * Copyright (c) 2010 Ubiquiti Networks, Inc. and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl<tom.schindl@bestsolution.at> - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.pgroup;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TypedListener;

public class PGroupToolItem extends Item {
	private Rectangle bounds;
	private boolean selection;
	private Rectangle dropdownArea;

	public PGroupToolItem(PGroup parent, int style) {
		super(parent, style);
		parent.addToolItem(this);
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void setSelection(boolean selection) {
		if ((getStyle() & (SWT.CHECK | SWT.RADIO)) == 0) return;
		this.selection = selection;
	}
	public boolean getSelection () {
		return selection;
	}

	public void addSelectionListener(SelectionListener listener) {
		TypedListener typedListener = new TypedListener(listener);
		addListener(SWT.Selection,typedListener);
		addListener(SWT.DefaultSelection,typedListener);
	}

	public void removeSelectionListener(SelectionListener listener) {
		removeListener(SWT.Selection, listener);
		removeListener(SWT.DefaultSelection,listener);
	}


	void setDropDownArea(Rectangle dropdownArea) {
		this.dropdownArea = dropdownArea;
	}

	void onMouseDown(Event e) {
		if( (getStyle() & SWT.DROP_DOWN) == 0 ) {
			setSelection(!getSelection());
			notifyListeners(SWT.Selection, new Event());
		} else {
			if( dropdownArea == null || ! dropdownArea.contains(e.x,e.y) ) {
				notifyListeners(SWT.Selection, new Event());
			} else {
				Event event = new Event();
				event.detail = SWT.ARROW;
				event.x = e.x;
				event.y = e.y;
				notifyListeners(SWT.Selection, event);
			}
		}
	}
}