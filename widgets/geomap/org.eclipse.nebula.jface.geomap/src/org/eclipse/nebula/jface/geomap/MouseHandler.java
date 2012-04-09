/*******************************************************************************
 * Copyright (c) 2012 Hallvard Tr¾tteberg.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Hallvard Tr¾tteberg - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.jface.geomap;

import org.eclipse.nebula.widgets.geomap.GeoMap;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;

/**
 * Handles mouse interaction, on behalf of a GeoMapViewer. Makes it easier to customize the viewer.
 * 
 * @author hal
 *
 */
class MouseHandler implements MouseListener, MouseMoveListener, MouseTrackListener, MouseWheelListener, PaintListener {

	protected final GeoMap geoMap;

	/**
	 * @param geoMap
	 */
	MouseHandler(GeoMap geoMap) {
		this.geoMap = geoMap;
	}

    public void mouseEnter(MouseEvent e) {
        this.geoMap.forceFocus();
    }
    public void mouseExit(MouseEvent e) {
    }

    protected Point dragCoords;
    protected Point downCoords;
    
    public final void mouseDown(MouseEvent e) {
    	dragCoords = new Point(e.x, e.y);
    	downCoords = new Point(e.x, e.y);
    	if (handleDown(e)) {
    		this.geoMap.redraw();
    	}
    }

    public final void mouseMove(MouseEvent e) {
        if (dragCoords != null) {
        	dragCoords.x = e.x;
        	dragCoords.y = e.y;
        	if (handleDrag(e)) {
        		this.geoMap.redraw();
        	}
        }
    }
    
    public final void mouseUp(MouseEvent e) {
    	if (handleUp(e)) {
    		this.geoMap.redraw();
    	}
    	downCoords = null;
    	dragCoords = null;
    }

    protected boolean handleDown(MouseEvent e) {
    	return false;
    }
    protected boolean handleDrag(MouseEvent e) {
    	return false;
    }
    protected boolean handleUp(MouseEvent e) {
    	return false;
    }
    
    // stubs

    public void mouseHover(MouseEvent e) {
    }
    
    public void mouseDoubleClick(MouseEvent e) {
    }
    
    public void mouseScrolled(MouseEvent e) {
    }

    // if handler has own, visual state
    
	public void paintControl(PaintEvent e) {
	}
}
