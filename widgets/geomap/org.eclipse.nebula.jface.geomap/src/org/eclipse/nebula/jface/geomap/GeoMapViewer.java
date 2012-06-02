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

import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.geomap.GeoMap;
import org.eclipse.nebula.widgets.geomap.PointD;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * A JFace viewer for the GeoMap widget
 * @author hal
 *
 */
public class GeoMapViewer extends ContentViewer {

	private GeoMap geoMap;

	private LocationProvider locationProvider;

	private Object selection = null;
	private Point selectionOffset = null;

	private MouseHandler mouseHandler;

	/**
	 * Creates a GeoMapViewer for a specific GeoMap
	 * @param geoMap the GeoMap
	 */
	public GeoMapViewer(GeoMap geoMap) {
		this.geoMap = geoMap;
		setMouseHandler(new MovePinMouseHandler(geoMap));
		geoMap.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				paintOverlay(e);
			}
		});
	}

	private void setMouseHandler(MovePinMouseHandler mouseHandler) {
		if (this.mouseHandler != null) {
			geoMap.removeMouseListener(this.mouseHandler);
			geoMap.removeMouseMoveListener(this.mouseHandler);
			geoMap.removeMouseTrackListener(this.mouseHandler);
			geoMap.removeMouseWheelListener(this.mouseHandler);
		}
		this.mouseHandler = mouseHandler;
		if (this.mouseHandler != null) {
			geoMap.addMouseListener(this.mouseHandler);
			geoMap.addMouseMoveListener(this.mouseHandler);
			geoMap.addMouseTrackListener(this.mouseHandler);
			geoMap.addMouseWheelListener(this.mouseHandler);
		}
	}

	/**
	 * Creates a GeoMapViewer with a default GeoMap inside a specific Composite
	 * @param parent the parent Composite
	 * @param flags the SWT options
	 */
	public GeoMapViewer(Composite parent, int flags) {
		this(new GeoMap(parent, flags));
	}

	@Override
	protected void handleDispose(DisposeEvent event) {
		super.handleDispose(event);
	}

	@Override
	protected void handleLabelProviderChanged(LabelProviderChangedEvent event) {
		super.handleLabelProviderChanged(event);
		refresh();
	}

	/**
	 * Returns the location provider for this GeoMapViewer.
	 * @return the location provider
	 */
	public LocationProvider getLocationProvider() {
		return locationProvider;
	}

	/**
	 * Sets the location provider for this GeoMapViewer. The location provider determines where the icon for each element is placed.
	 * @param locationProvider the location provider
	 */
	public void setLocationProvider(LocationProvider locationProvider) {
		this.locationProvider = locationProvider;
	}

	//

	protected void paintOverlay(PaintEvent e) {
		doContents(e.gc, null, selection);
		if (mouseHandler != null) {
			mouseHandler.paintControl(e);
		}
	}

	private Object doContents(GC gc, Rectangle contain, Object selection) {
		//		System.out.println(mapWidget.getMapPosition() + "/" + mapWidget.getCenterPosition() + "@" + mapWidget.getZoom());
		IContentProvider contentProvider = getContentProvider();
		Object[] contents = (contentProvider instanceof IStructuredContentProvider ? ((IStructuredContentProvider) contentProvider).getElements(getInput()) : null);
		if (contents != null && getLocationProvider() != null) {
			for (int i = 0; i < contents.length; i++) {
				Object element = contents[i];
				Object found = doContent(element, gc, contain, selection);
				if (found != null) {
					return found;
				}
			}
		}
		return null;
	}

	private Object doContent(Object element, GC gc, Rectangle contain, Object selection) {
		Point p = getElementPosition(element, null, true, true);
		if (p == null) {
			return null;
		}
		IBaseLabelProvider labelProvider = getLabelProvider();
		Image image = null;
		if (labelProvider instanceof ILabelProvider) {
			@SuppressWarnings("unused")
			String text = ((ILabelProvider) labelProvider).getText(element);
			image = ((ILabelProvider) labelProvider).getImage(element);
		}
		if (image == null) {
			return null;
		}
		Rectangle bounds = image.getBounds();
		bounds.x = p.x;
		bounds.y = p.y;
		if (gc != null) {
			boolean isSelected = selection != null && element == selection;
			if (isSelected && selectionOffset != null) {
				bounds.x += selectionOffset.x;
				bounds.y += selectionOffset.y;
			}
			gc.drawImage(image, bounds.x, bounds.y);
			if (isSelected) {
				gc.drawRectangle(bounds.x, bounds.y, bounds.width, bounds.height);
			}
		}
		if (contain != null && bounds.contains(contain.x, contain.y) && bounds.contains(contain.x + contain.width, contain.y + contain.height)) {
			return element;
		}
		return null;
	}

	private Point getElementPosition(Object element, Point into, boolean mapRelative, boolean imageRelative) {
		PointD lonLat = (element instanceof PointD ? (PointD) element : getLocationProvider().getLonLat(element));
		if (lonLat == null) {
			return null;
		}
		int x = GeoMap.lon2position(lonLat.x, geoMap.getZoom());
		int y = GeoMap.lat2position(lonLat.y, geoMap.getZoom());
		if (mapRelative) {
			Point p = geoMap.getMapPosition();
			x -= p.x;
			y -= p.y;
		}
		if (imageRelative) {
			IBaseLabelProvider labelProvider = getLabelProvider();
			if (labelProvider instanceof IPinPointProvider) {
				Point p = ((IPinPointProvider) labelProvider).getPinPoint(element);
				if (p != null) {
					x -= p.x;
					y -= p.y;
				}
			}
		}
		if (into == null) {
			into = new Point(x, y);
		} else {
			into.x = x;
			into.y = y;
		}
		return into;
	}

	@Override
	public Control getControl() {
		return getGeoMap();
	}

	/**
	 * Returns the underlying GeoMap control
	 * @return the underlying GeoMap control
	 */
	public GeoMap getGeoMap() {
		return geoMap;
	}

	@Override
	public ISelection getSelection() {
		return (selection != null ? new StructuredSelection(selection) : StructuredSelection.EMPTY);
	}

	@Override
	public void refresh() {
		geoMap.redraw();
	}

	@Override
	public void setSelection(ISelection selection, boolean reveal) {
		setSelection(selection instanceof IStructuredSelection ? ((IStructuredSelection) selection).getFirstElement() : null);
		if (reveal && this.selection != null) {
			reveal(this.selection, true);
		}
	}

	private int centerOnSelectionMargin = 10;

	private void reveal(Object selection, boolean center) {
		Point position = getElementPosition(selection, new Point(0, 0), true, false);
		Point size = geoMap.getSize();
		Rectangle insideMargin = new Rectangle(centerOnSelectionMargin, centerOnSelectionMargin, size.x - centerOnSelectionMargin, size.y - centerOnSelectionMargin);
		if (position != null && (center || (! insideMargin.contains(position)))) {
			Point mapPosition = geoMap.getMapPosition();
			geoMap.setCenterPosition(new Point(position.x + mapPosition.x, position.y + mapPosition.y));
		}
	}

	private void setSelection(Object selection) {
		this.selection = selection;
		refresh();
	}

	//

	public final static int
		MOVE_SELECTION_NONE = 0,
		MOVE_SELECTION_ALLOW_CHECK_IMMEDIATE = 1,
		MOVE_SELECTION_ALLOW_CHECK_LATE = 2;
	
	private int moveSelectionMode = MOVE_SELECTION_ALLOW_CHECK_IMMEDIATE;

	public void setMoveSelectionMode(int moveSelectionMode) {
		this.moveSelectionMode = moveSelectionMode;
	}

	public int getMoveSelectionMode() {
		return moveSelectionMode;
	}

	private class MovePinMouseHandler extends DefaultMouseHandler {

		MovePinMouseHandler(GeoMap geoMap) {
			super(geoMap);
		}

		private int thumbSize = 7;

		private Rectangle createPointRectangle(int x, int y) {
			return new Rectangle(x - thumbSize / 2, y - thumbSize / 2, thumbSize, thumbSize);
		}

		protected boolean isPanDownEvent(MouseEvent e) {
			return super.isPanDownEvent(e) && doContents(null, createPointRectangle(e.x, e.y), null) == null;
		}

		protected boolean handleDown(MouseEvent e) {
			boolean redraw = super.handleDown(e);
			Object contains = doContents(null, createPointRectangle(e.x, e.y), null);
			if (contains != null) {
				selectionOffset = new Point(0, 0);
				PointD lonLat = getLocationProvider().getLonLat(contains);
				if (moveSelectionMode == MOVE_SELECTION_NONE ||
					(moveSelectionMode == MOVE_SELECTION_ALLOW_CHECK_IMMEDIATE && (! getLocationProvider().setLonLat(contains, lonLat.x, lonLat.y)))) {
					selectionOffset = null;
				}
				setSelection(contains);
				redraw = true;
			}
			return redraw;
		}

		@Override
		protected boolean handleDrag(MouseEvent e) {
			boolean redraw = super.handleDrag(e);
			if (selectionOffset != null) {
				selectionOffset.x = e.x - downCoords.x;
				selectionOffset.y = e.y - downCoords.y;
				return true;
			}
			return redraw;
		}

		@Override
		protected boolean handleUp(MouseEvent e) {
			boolean redraw = super.handleUp(e);
			if (selectionOffset != null) {
				Point oldPosition = getElementPosition(selection, new Point(0, 0), false, false);
				Point newPosition = new Point(oldPosition.x + selectionOffset.x, oldPosition.y + selectionOffset.y);
				PointD lonLat = geoMap.getLongitudeLatitude(newPosition);
				@SuppressWarnings("unused")
				boolean changed = getLocationProvider().setLonLat(selection, lonLat.x, lonLat.y);
				reveal(selection, (e.stateMask & SWT.CTRL) != 0);
				selectionOffset = null;
				redraw = true;
			}
			return redraw;
		}
	}
}
