/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.ganttchart.dnd;

import org.eclipse.nebula.widgets.ganttchart.GanttComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DragSourceEffect;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public class GanttDragSourceEffect extends DragSourceEffect {

	private GanttComposite	_parent;
	private Image			_dragImage;

	public GanttDragSourceEffect(GanttComposite parent) {
		super(parent);
		_parent = parent;
	}

	public Control getControl() {
		return _parent;
	}

	public void dragFinished(DragSourceEvent event) {
		if (_dragImage != null) {
			_dragImage.dispose();
		}
	}

	public void dragStart(DragSourceEvent event) {
		Image dragSourceImage = new Image(Display.getDefault(),160,160);
		GC gc = new GC(dragSourceImage);
		gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
		gc.fillRectangle(0, 0, 160, 160);
		gc.dispose();
		_dragImage = dragSourceImage;
		System.err.println("Image set " + _dragImage);
		event.image = _dragImage;//ImageCache.getImage("icons/lock_tiny.gif");
	}

}
