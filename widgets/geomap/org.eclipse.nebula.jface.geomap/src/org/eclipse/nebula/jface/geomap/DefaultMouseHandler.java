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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class DefaultMouseHandler extends MouseHandler {

	public DefaultMouseHandler(GeoMap geoMap) {
		super(geoMap);
	}

	private Point downPosition = null;
	private boolean isPanning = false, isZooming = false;
	
    @Override
    protected boolean handleDown(MouseEvent e) {
    	downPosition = this.geoMap.getMapPosition();
        if (isPanning = isPanDownEvent(e)) {
        }
        if (isZooming = isZoomDownEvent(e)) {
        }
        if (isCenterDownEvent(e)) {
        	this.geoMap.setCenterPosition(this.geoMap.getCursorPosition());
        	return true;
        }
        return false;
    }

    private Rectangle zoomRectangle;
    
    @Override
    protected boolean handleDrag(MouseEvent e) {
    	// undo the effect of MapWidget's own panning
    	this.geoMap.setMapPosition(downPosition.x, downPosition.y);
    	// do our own panning
    	if (isPanning && downPosition != null) {
    		int tx = downCoords.x - e.x;
    		int ty = downCoords.y - e.y;
    		this.geoMap.setMapPosition(downPosition.x + tx, downPosition.y + ty);
    		return true;
    	}
    	if (isZooming && downCoords != null && dragCoords != null) {
    		int minX = Math.min(downCoords.x, dragCoords.x), minY = Math.min(downCoords.y, dragCoords.y);
    		int maxX = Math.max(downCoords.x, dragCoords.x), maxY = Math.max(downCoords.y, dragCoords.y);
    		Point mapPosition = this.geoMap.getMapPosition();
    		zoomRectangle = new Rectangle(mapPosition.x + minX, mapPosition.y + minY, maxX - minX, maxY - minY);
    		return true;
    	}
        return false;
    }
    
    @Override
	public void paintControl(PaintEvent e) {
		super.paintControl(e);
		if (zoomRectangle != null) {
			Point mapPosition = this.geoMap.getMapPosition();
			e.gc.drawRectangle(zoomRectangle.x - mapPosition.x, zoomRectangle.y - mapPosition.y, zoomRectangle.width, zoomRectangle.height);
		}
	}

	@Override
    protected boolean handleUp(MouseEvent e) {
        if (e.count == 1) {
        	handleDrag(e);
        }
    	if (isZooming && downCoords != null) {
    		Point mapSize = this.geoMap.getSize();
    		int diff = Math.min(mapSize.x / zoomRectangle.width, mapSize.y / zoomRectangle.height);
    		while (diff > 1) {
    			Point mapPosition = this.geoMap.getMapPosition();
    			// pivot on center of zoom rectangle
    			Point pivot = new Point(zoomRectangle.x - mapPosition.x + zoomRectangle.width / 2, zoomRectangle.y - mapPosition.y + zoomRectangle.height / 2);
				this.geoMap.zoomIn(pivot);
				// scale zoom rectangle up, to match zoom level
				zoomRectangle = new Rectangle(zoomRectangle.x * 2, zoomRectangle.y * 2, zoomRectangle.width * 2, zoomRectangle.height * 2);
    			diff /= 2;
    		}
    	}
        downPosition = null;
        isPanning = false;
        isZooming = false;
        zoomRectangle = null;
        return true;
    }
    
    // can be overridden to provide new functionality
    
	protected boolean isCenterDownEvent(MouseEvent e) {
		return e.button == 1 && (e.stateMask & SWT.CTRL) != 0;
	}

	protected boolean isPanDownEvent(MouseEvent e) {
		return e.button == 1 && (e.stateMask & SWT.SHIFT) == 0;
	}

	protected boolean isZoomDownEvent(MouseEvent e) {
		return e.button == 1 && (e.stateMask & SWT.SHIFT) != 0;
	}
}
