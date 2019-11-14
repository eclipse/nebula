/*******************************************************************************
 * Copyright (c) 2012 Hallvard Trætteberg.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Hallvard Trætteberg - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.geomap.jface;

import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.widgets.geomap.GeoMap;
import org.eclipse.nebula.widgets.geomap.GeoMapUtil;
import org.eclipse.nebula.widgets.geomap.PointD;
import org.eclipse.nebula.widgets.geomap.internal.DefaultMouseHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

/**
 * A JFace viewer for the GeoMap widget, that shows the geo-location of the
 * input elements. The LocationProvider maps the input elements to geo-locations
 * 
 * @author hal
 *
 */
public class GeoMapViewer extends ContentViewer {

	private GeoMap geoMap;

	private LocationProvider locationProvider;

	private Object selection = null;
	private Point selectionOffset = null;

	private DefaultMouseHandler mouseHandler;

	/**
	 * Creates a GeoMapViewer for a specific GeoMap
	 * 
	 * @param geoMap
	 *            the GeoMap
	 */
	public GeoMapViewer(GeoMap geoMap) {
		this.geoMap = geoMap;
		hookControl(geoMap);
		this.geoMap.removeMouseHandler(this.geoMap.getDefaultMouseHandler());
		this.geoMap
				.addMouseHandler(mouseHandler = new MovePinMouseHandler(this));
		geoMap.addPaintListener(e -> paintOverlay(e));
		geoMap.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.keyCode) {
				case SWT.ESC:
					revealAll();
					break;
				}
			}
		});
	}

	/**
	 * Creates a GeoMapViewer with a default GeoMap inside a specific Composite
	 * 
	 * @param parent
	 *            the parent Composite
	 * @param flags
	 *            the SWT options
	 */
	public GeoMapViewer(Composite parent, int flags) {
		this(new GeoMap(parent, flags));
	}

	@Override
	protected void handleDispose(DisposeEvent event) {
		this.geoMap.removeMouseHandler(mouseHandler);
		if (lastToolTip != null) {
			lastToolTip.deactivate();
			lastToolTip = null;
		}
		if (toolTip != null) {
			toolTip.deactivate();
			toolTip = null;
		}
		super.handleDispose(event);
	}

	@Override
	protected void handleLabelProviderChanged(LabelProviderChangedEvent event) {
		super.handleLabelProviderChanged(event);
		refresh();
	}

	/**
	 * Returns the location provider for this GeoMapViewer.
	 * 
	 * @return the location provider
	 */
	public LocationProvider getLocationProvider() {
		return locationProvider;
	}

	/**
	 * Sets the location provider for this GeoMapViewer. The location provider
	 * determines where the icon for each element is placed.
	 * 
	 * @param locationProvider
	 *            the location provider
	 */
	public void setLocationProvider(LocationProvider locationProvider) {
		this.locationProvider = locationProvider;
	}

	//

	private void paintOverlay(PaintEvent e) {
		doContents(e.gc, null, selection);
	}

	private Object[] getElements() {
		IContentProvider contentProvider = getContentProvider();
		return contentProvider instanceof IStructuredContentProvider
				? ((IStructuredContentProvider) contentProvider)
						.getElements(getInput())
				: null;
	}

	private Object doContents(GC gc, Rectangle contain, Object selection) {
		Object[] elements = getElements();
		if (elements != null && getLocationProvider() != null) {
			for (Object element : elements) {
				Object found = doContent(element, gc, contain, selection);
				if (found != null) {
					return found;
				}
			}
		}
		return null;
	}

	/**
	 * Gets the element found within the rectangle of the given position and
	 * size
	 * 
	 * @param x
	 *            the x coordinate of center of the rectangle
	 * @param y
	 *            the y coordinate of center of the rectangle
	 * @param thumbSize
	 *            the width and height of the rectangle
	 * @return the element found or null, if none where found
	 */
	public Object getElementAt(int x, int y, int thumbSize) {
		return doContents(null, new Rectangle(x - thumbSize / 2,
				y - thumbSize / 2, thumbSize, thumbSize), null);
	}

	/**
	 * Constant indicating that location markers should not be clipped
	 */
	public static int NO_CLIP = 0;

	/**
	 * Constant indicating the location markers should be clipped, based on
	 * their position
	 */
	public static int CLIP_ON_ELEMENT_POSITION = 1;

	/**
	 * Constant indicating the location markers should be clipped, based on the
	 * bounding box of their image
	 */
	public static int CLIP_ON_IMAGE_BOUNDS = 2;

	private int clipRule = CLIP_ON_ELEMENT_POSITION;

	/**
	 * Sets the clipping rule
	 * 
	 * @param clipRule
	 *            the clipping rule, one of the constants NO_CLIP,
	 *            CLIP_ON_ELEMENT_POSITION or CLIP_ON_IMAGE_BOUNDS
	 */
	public void setClipRule(int clipRule) {
		this.clipRule = clipRule;
	}

	private Object doContent(Object element, GC gc, Rectangle contain,
			Object selection) {
		Point p = getElementPosition(element, null, true);
		if (p == null) {
			return null;
		}
		if (gc != null && clipRule == CLIP_ON_ELEMENT_POSITION) {
			if (p.x < 0 || p.y < 0) {
				return null;
			}
			Point size = geoMap.getSize();
			if (p.x > size.x || p.y > size.y) {
				return null;
			}
		}
		IBaseLabelProvider labelProvider = getLabelProvider();
		Image image = null;
		if (labelProvider instanceof ILabelProvider) {
			((ILabelProvider) labelProvider).getText(element);
			image = ((ILabelProvider) labelProvider).getImage(element);
		}
		if (image == null) {
			return null;
		}
		Rectangle bounds = image.getBounds();
		bounds.x = p.x;
		bounds.y = p.y;
		if (labelProvider instanceof IPinPointProvider) {
			Point pinPoint = ((IPinPointProvider) labelProvider)
					.getPinPoint(element);
			if (pinPoint != null) {
				bounds.x -= pinPoint.x;
				bounds.y -= pinPoint.y;
			}
		}
		if (gc != null) {
			boolean shouldClip = false;
			if (clipRule == CLIP_ON_IMAGE_BOUNDS) {
				Point size = geoMap.getSize();
				shouldClip = !bounds.intersects(0, 0, size.x, size.y);
			}
			if (!shouldClip) {
				boolean isSelected = selection != null && element == selection;
				if (isSelected && selectionOffset != null) {
					bounds.x += selectionOffset.x;
					bounds.y += selectionOffset.y;
				}
				gc.drawImage(image, bounds.x, bounds.y);
				if (isSelected) {
					gc.drawRectangle(bounds.x, bounds.y, bounds.width,
							bounds.height);
				}
			}
		}
		if (contain != null && bounds.contains(contain.x, contain.y)
				&& bounds.contains(contain.x + contain.width,
						contain.y + contain.height)) {
			return element;
		}
		return null;
	}

	private Point getElementPosition(Object element, Point into,
			boolean mapRelative) {
		PointD lonLat = getLocationProvider().getLonLat(element);
		if (lonLat == null) {
			lonLat = element instanceof PointD ? (PointD) element
					: element instanceof Located
							? ((Located) element).getLonLat()
							: null;
		}
		if (lonLat == null) {
			return null;
		}
		int x = GeoMapUtil.lon2position(lonLat.x, geoMap.getZoom());
		int y = GeoMapUtil.lat2position(lonLat.y, geoMap.getZoom());
		if (mapRelative) {
			Point p = geoMap.getMapPosition();
			x -= p.x;
			y -= p.y;
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
	 * 
	 * @return the underlying GeoMap control
	 */
	public GeoMap getGeoMap() {
		return geoMap;
	}

	@Override
	public ISelection getSelection() {
		return selection != null ? new StructuredSelection(selection)
				: StructuredSelection.EMPTY;
	}

	@Override
	public void refresh() {
		geoMap.redraw();
	}

	@Override
	protected void inputChanged(Object input, Object oldInput) {
		refresh();
	}

	@Override
	public void setSelection(ISelection selection, boolean reveal) {
		setSelection(selection instanceof IStructuredSelection
				? ((IStructuredSelection) selection).getFirstElement()
				: null);
		if (reveal && this.selection != null) {
			reveal(this.selection, true);
		}
	}

	private int revealMargin = 10;

	/**
	 * Pans the viewer so the element is revealed
	 * 
	 * @param selection
	 *            the element to reveal
	 * @param center
	 *            whether to center on the element
	 */
	public void reveal(Object selection, boolean center) {
		Point position = getElementPosition(selection, new Point(0, 0), true);
		Point size = geoMap.getSize();
		Rectangle insideMargin = new Rectangle(revealMargin, revealMargin,
				size.x - revealMargin, size.y - revealMargin);
		if (position != null && (center || !insideMargin.contains(position))) {
			Point mapPosition = geoMap.getMapPosition();
			geoMap.setCenterPosition(new Point(position.x + mapPosition.x,
					position.y + mapPosition.y));
		}
	}

	/**
	 * Pans and zooms so all elements are revealed.
	 */
	public void revealAll() {
		zoomTo(((IStructuredContentProvider) getContentProvider())
				.getElements(getInput()), -1);
	}

	private void setSelection(Object selection) {
		this.selection = selection;
		refresh();
		fireSelectionChanged(new SelectionChangedEvent(this,
				new StructuredSelection(selection)));
	}

	//

	/**
	 * Constant indicating the selection cannot be moved, i.e. its location is
	 * read-only
	 */
	public final static int MOVE_SELECTION_NONE = 0;

	/**
	 * Constant indicating the selection will be moved immediately, using the
	 * LocationProvider's set method
	 */
	public final static int MOVE_SELECTION_ALLOW_CHECK_IMMEDIATE = 1;

	/**
	 * Constant indicating the selection will be moveable, but the actual move
	 * is performed using the LocationProvider's set method, when dropping
	 */
	public final static int MOVE_SELECTION_ALLOW_CHECK_LATE = 2;

	private int moveSelectionMode = MOVE_SELECTION_ALLOW_CHECK_IMMEDIATE;

	/**
	 * Sets the current move mode, one of MOVE_SELECTION_NONE,
	 * MOVE_SELECTION_ALLOW_CHECK_IMMEDIATE or MOVE_SELECTION_ALLOW_CHECK_LATE
	 * 
	 * @param moveSelectionMode
	 */
	public void setMoveSelectionMode(int moveSelectionMode) {
		this.moveSelectionMode = moveSelectionMode;
	}

	/**
	 * Gets the current move mode
	 * 
	 * @return the current move mode
	 */
	public int getMoveSelectionMode() {
		return moveSelectionMode;
	}

	private ToolTip toolTip, lastToolTip;

	/**
	 * Gets the current ToolTip
	 * 
	 * @return the current ToolTip
	 */
	public ToolTip getToolTip() {
		if (toolTip == null) {
			toolTip = new DefaultToolTip(getControl());
		}
		return toolTip;
	}

	private int thumbSize = 7;

	private class MovePinMouseHandler extends
			org.eclipse.nebula.widgets.geomap.internal.DefaultMouseHandler {

		MovePinMouseHandler(GeoMapViewer geoMapViewer) {
			super(geoMapViewer.getGeoMap());
		}

		@Override
		public Point getMapSize() {
			return getControl().getSize();
		}

		@Override
		protected boolean isPanStart(MouseEvent e) {
			return super.isPanStart(e)
					&& getElementAt(e.x, e.y, thumbSize) == null;
		}

		private Point selectionStart = null;

		private boolean isSelecting() {
			return selectionOffset != null;
		}

		@Override
		public void mouseDown(MouseEvent e) {
			super.mouseDown(e);
			if (isPanning() || isZooming()) {
				return;
			}
			Object element = getElementAt(e.x, e.y, thumbSize);
			if (element != null) {
				selectionStart = new Point(e.x, e.y);
				selectionOffset = new Point(0, 0);
				PointD lonLat = getLocationProvider().getLonLat(element);
				if (moveSelectionMode == MOVE_SELECTION_NONE
						|| moveSelectionMode == MOVE_SELECTION_ALLOW_CHECK_IMMEDIATE
								&& !getLocationProvider().setLonLat(element,
										lonLat.x, lonLat.y)) {
					selectionOffset = null;
				}
				setSelection(element);
			}
		}

		@Override
		public void mouseMove(MouseEvent e) {
			if (isSelecting()) {
				selectionOffset.x = e.x - selectionStart.x;
				selectionOffset.y = e.y - selectionStart.y;
				refresh();
			} else {
				super.mouseMove(e);
			}
		}

		@Override
		public void mouseUp(MouseEvent e) {
			if (isSelecting()) {
				PointD lonLat = getLocationProvider().getLonLat(selection);
				int zoom = geoMap.getZoom();
				Point position = GeoMapUtil.computePosition(lonLat, zoom);
				Point newPosition = new Point(position.x + selectionOffset.x,
						position.y + selectionOffset.y);
				lonLat = GeoMapUtil.getLongitudeLatitude(newPosition, zoom);
				getLocationProvider().setLonLat(selection, lonLat.x, lonLat.y);
				reveal(selection, checkButtons(e, getPanCenterButtons()));
				selectionStart = null;
				selectionOffset = null;
			} else {
				super.mouseUp(e);
			}
		}

		@Override
		public void mouseHover(MouseEvent e) {
			handleToolTip(e);
		}
	}

	void handleToolTip(Event e) {
		handleToolTip(e.x, e.y);
	}

	void handleToolTip(MouseEvent e) {
		handleToolTip(e.x, e.y);
	}

	private void handleToolTip(int x, int y) {
		if (getLabelProvider() instanceof IToolTipProvider) {
			// DefaultToolTip toolTip = (DefaultToolTip) getToolTip();
			Object element = getElementAt(x, y, thumbSize);
			Object toolTip = ((IToolTipProvider) getLabelProvider())
					.getToolTip(element);
			if (toolTip instanceof String) {
				if (getToolTip() instanceof DefaultToolTip) {
					DefaultToolTip defaultToolTip = (DefaultToolTip) getToolTip();
					defaultToolTip.setText((String) toolTip);
					toolTip = defaultToolTip;
				}
			}
			if (lastToolTip != toolTip) {
				if (lastToolTip != null) {
					lastToolTip.deactivate();
				}
				lastToolTip = (ToolTip) toolTip;
				if (lastToolTip != null) {
					lastToolTip.activate();
				}
			}
		}
	}

	private Rectangle getBounds(Object[] elements) {
		Rectangle rect = null;
		for (Object element : elements) {
			PointD location = getLocationProvider().getLonLat(element);
			if (location == null) {
				continue;
			}
			Point position = GeoMapUtil.computePosition(location,
					geoMap.getZoom());
			if (rect == null) {
				rect = new Rectangle(position.x, position.y, 1, 1);
			} else {
				if (position.x < rect.x) {
					rect.width += rect.x - position.x;
					rect.x = position.x;
				} else if (position.x > rect.x + rect.width) {
					rect.width = position.x - rect.x;
				}
				if (position.y < rect.y) {
					rect.height += rect.y - position.y;
					rect.y = position.y;
				} else if (position.y > rect.y + rect.height) {
					rect.height = position.y - rect.y;
				}
			}
		}
		return rect;
	}

	private static final int MAX_ZOOM_TO = 12;

	/**
	 * Zooms out so all elements are revealed, but only upto a certain zoom
	 * level.
	 * 
	 * @param elements
	 *            the elements to reveal
	 * @param maxZoom
	 *            the maximum zoom
	 */
	public void zoomTo(Object[] elements, int maxZoom) {
		getGeoMap().setZoom(getGeoMap().getTileServer().getMaxZoom());
		Rectangle rect = getBounds(elements);
		if (rect == null) {
			return;
		}
		GeoMapUtil.zoomTo(getGeoMap(), getGeoMap().getSize(), rect,
				maxZoom >= 0 ? maxZoom : MAX_ZOOM_TO);
	}
}
