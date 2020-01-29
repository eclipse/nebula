/*******************************************************************************
 * Copyright (c) 2008, 2012 Stepan Rutz.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Stepan Rutz - initial implementation
 *    Hallvard Trætteberg - further cleanup and development
 *******************************************************************************/

package org.eclipse.nebula.widgets.geomap.internal;

import org.eclipse.nebula.widgets.geomap.GeoMapUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Implements default interactive behavior, with support for panning and
 * zooming.
 * 
 * @since 3.3
 *
 */
public abstract class DefaultMouseHandler
		implements MouseListener, MouseWheelListener, MouseMoveListener,
		MouseTrackListener, PaintListener {

	/**
	 *
	 */
	private final GeoMapPositioned geoMap;

	/**
	 * @param geoMap
	 * @param control
	 */
	public DefaultMouseHandler(GeoMapPositioned geoMap) {
		this.geoMap = geoMap;
	}

	/**
	 * @return
	 */
	protected GeoMapPositioned getGeoMap() {
		return this.geoMap;
	}

	/**
	 * Zoom in at cursor position
	 * 
	 * @param e
	 *            the MouseEvent
	 */
	protected void zoomIn(MouseEvent e) {
		GeoMapUtil.zoomIn(getGeoMap(), new Point(e.x, e.y));
	}

	/**
	 * Zoom out at cursor position
	 * 
	 * @param e
	 *            the MouseEvent
	 */
	protected void zoomOut(MouseEvent e) {
		GeoMapUtil.zoomOut(getGeoMap(), new Point(e.x, e.y));
	}

	/**
	 * Sets the map position
	 * 
	 * @param x
	 *            the x or x offset
	 * @param y
	 *            the y or y offset
	 * @param relative
	 *            tells whether x and y are offsets
	 */
	protected void pan(int x, int y, boolean relative) {
		if (relative) {
			Point p = getGeoMap().getMapPosition();
			x += p.x;
			y += p.y;
		}
		getGeoMap().setMapPosition(x, y);
	}

	/**
	 * Gets the size of the map viewport/pane.
	 * 
	 * @return the size of the map viewport/pane
	 */
	public abstract Point getMapSize();

	/**
	 * Center at cursor position
	 * 
	 * @param e
	 *            the MouseEvent
	 */
	protected void center(MouseEvent e) {
		Point size = getMapSize();
		Point mapPosition = getGeoMap().getMapPosition();
		getGeoMap().setMapPosition(mapPosition.x + e.x - size.x / 2,
				mapPosition.y + e.y - size.y / 2);
	}

	//

	/**
	 * @return Returns the zoomClickCount.
	 */
	public int getZoomClickCount() {
		return zoomClickCount;
	}

	/**
	 * Sets the number of clicks that triggers a zoom.
	 * 
	 * @param zoomClickCount
	 *            The zoomClickCount to set.
	 */
	public void setZoomClickCount(int zoomClickCount) {
		this.zoomClickCount = zoomClickCount;
	}

	/**
	 * @return Returns the panCenterButtons.
	 */
	public int getPanCenterButtons() {
		return panCenterButtons;
	}

	/**
	 * @param panCenterButtons
	 *            The panCenterButtons to set.
	 */
	public void setPanCenterButtons(int panCenterButtons) {
		this.panCenterButtons = panCenterButtons;
	}

	/**
	 * @return Returns the zoomInClickButtons.
	 */
	public int getZoomInClickButtons() {
		return zoomInClickButtons;
	}

	/**
	 * Sets the button(s) that triggers a zoom in.
	 * 
	 * @param zoomInClickButtons
	 *            The zoomInClickButtons to set.
	 */
	public void setZoomInClickButtons(int zoomInClickButtons) {
		this.zoomInClickButtons = zoomInClickButtons;
	}

	/**
	 * @return Returns the zoomOutClickButtons.
	 */
	public int getZoomOutClickButtons() {
		return zoomOutClickButtons;
	}

	/**
	 * Sets the button(s) that triggers a zoom out.
	 * 
	 * @param zoomOutClickButtons
	 *            The zoomOutClickButtons to set.
	 */
	public void setZoomOutClickButtons(int zoomOutClickButtons) {
		this.zoomOutClickButtons = zoomOutClickButtons;
	}

	/**
	 * @return Returns the panClickCount.
	 */
	public int getPanClickCount() {
		return panClickCount;
	}

	/**
	 * Sets the number of clicks that triggers a pan.
	 * 
	 * @param panClickCount
	 *            The panClickCount to set.
	 */
	public void setPanClickCount(int panClickCount) {
		this.panClickCount = panClickCount;
	}

	/**
	 * @return Returns the panButtons.
	 */
	public int getPanButtons() {
		return panButtons;
	}

	/**
	 * Sets the button(s) that triggers a pan.
	 * 
	 * @param panButtons
	 *            The panButtons to set.
	 */
	public void setPanButtons(int panButtons) {
		this.panButtons = panButtons;
	}

	/**
	 * @return Returns the panScrollButtons.
	 */
	public int getPanScrollButtons() {
		return panScrollButtons;
	}

	/**
	 * Sets the button(s) that triggers a pan, when using the scroll wheel.
	 * 
	 * @param panScrollButtons
	 *            The panScrollButtons to set.
	 */
	public void setPanScrollButtons(int panScrollButtons) {
		this.panScrollButtons = panScrollButtons;
	}

	/**
	 * @return Returns the panScrollSpeed.
	 */
	public int getPanScrollSpeed() {
		return panScrollSpeed;
	}

	/**
	 * Sets the panning speed, when using the scroll wheel.
	 * 
	 * @param panScrollSpeed
	 *            The panScrollSpeed to set.
	 */
	public void setPanScrollSpeed(int panScrollSpeed) {
		this.panScrollSpeed = panScrollSpeed;
	}

	/**
	 * @return Returns the zoomScrollButtons.
	 */
	public int getZoomScrollButtons() {
		return zoomScrollButtons;
	}

	/**
	 * Sets the button(s) that triggers a zoom, when using the scroll wheel.
	 * 
	 * @param zoomScrollButtons
	 *            The zoomScrollButtons to set.
	 */
	public void setZoomScrollButtons(int zoomScrollButtons) {
		this.zoomScrollButtons = zoomScrollButtons;
	}

	/**
	 * @return Returns the zoomRectangleButtons.
	 */
	public int getZoomRectangleButtons() {
		return zoomRectangleButtons;
	}

	/**
	 * Sets the button(s) that triggers a zoom (rectangle).
	 * 
	 * @param zoomRectangleButtons
	 *            The zoomRectangleButtons to set.
	 */
	public void setZoomRectangleButtons(int zoomRectangleButtons) {
		this.zoomRectangleButtons = zoomRectangleButtons;
	}

	//

	private Point panStart;
	private Point downPosition;

	@Override
	public void mouseEnter(MouseEvent e) {
		// control.forceFocus();
	}

	@Override
	public void mouseExit(MouseEvent e) {
		// ignore
	}

	@Override
	public void mouseHover(MouseEvent e) {
		// ignore
	}

	/**
	 * Checks that the MouseEvent corresponds to the provided buttons bit mask.
	 * The buttons are or'ed button bits for modifiers keys and mouse buttons.
	 * 
	 * @param e
	 *            the MouseEvent
	 * @param buttons
	 *            Or'ed button bits for modifiers keys and mouse buttons.
	 * @return true if the MouseEvent corresponds to the provided buttons, false
	 *         otherwise
	 */
	protected boolean checkButtons(MouseEvent e, int buttons) {
		int mask = e.stateMask;
		switch (e.button) {
		case 1:
			mask |= SWT.BUTTON1;
			break;
		case 2:
			mask |= SWT.BUTTON2;
			break;
		case 3:
			mask |= SWT.BUTTON3;
			break;
		case 4:
			mask |= SWT.BUTTON4;
			break;
		case 5:
			mask |= SWT.BUTTON5;
			break;
		}
		return mask == buttons;
	}

	@Override
	public void mouseDown(MouseEvent e) {
		handleDown(e);
	}

	@Override
	public void mouseMove(MouseEvent e) {
		if (isPanning()) {
			handlePanDrag(e);
		} else if (zoomStart != null) {
			handleZoomDrag(e);
		}
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		handleZoomClick(e);
	}

	private int zoomClickCount = 1;
	private int zoomInClickButtons = SWT.BUTTON1;
	private int zoomOutClickButtons = SWT.BUTTON3;
	private int zoomRectangleButtons = SWT.BUTTON1 | SWT.SHIFT;

	private Point zoomStart = null;
	private Rectangle zoomRectangle = null;

	/**
	 * Checks if a click event is a zoom and performs it.
	 * 
	 * @param e
	 *            the MouseEvent
	 * @return if the click event is a zoom
	 */
	protected boolean handleZoomClick(MouseEvent e) {
		if (e.count != zoomClickCount) {
			return false;
		}
		if (checkButtons(e, zoomInClickButtons)) {
			zoomIn(e);
			return true;
		} else if (checkButtons(e, zoomOutClickButtons)) {
			zoomOut(e);
			return true;
		}
		return false;
	}

	private int panClickCount = 1;
	private int panButtons = SWT.BUTTON1;
	private int panCenterButtons = SWT.BUTTON1 | SWT.CTRL;

	/**
	 * Checks if a down event is (the start of) a pan or zoom and initiates it.
	 * 
	 * @param e
	 *            the MouseEvent
	 * @return if the click event is a zoom
	 */
	protected boolean handleDown(MouseEvent e) {
		if (e.count != panClickCount) {
			return false;
		}
		if (checkButtons(e, panCenterButtons)) {
			center(e);
			return true;
		}
		if (isPanStart(e)) {
			return panStart(e);
		} else if (isZoomStart(e)) {
			return zoomStart(e);
		}
		return false;
	}

	/**
	 * @param e
	 *            the MouseEvent
	 * @return if the MouseEvent is considered start of a pan
	 */
	protected boolean isPanStart(MouseEvent e) {
		return checkButtons(e, panButtons);
	}

	/**
	 * @param e
	 *            the MouseEvent
	 * @return if the MouseEvent is considered start of a zoom
	 */
	protected boolean isZoomStart(MouseEvent e) {
		return checkButtons(e, zoomRectangleButtons);
	}

	/**
	 * Initiates a pan.
	 * 
	 * @param e
	 *            the MouseEvent
	 * @return if pan was really initiated
	 */
	protected boolean panStart(MouseEvent e) {
		panStart = new Point(e.x, e.y);
		downPosition = getGeoMap().getMapPosition();
		return true;
	}

	/**
	 * @return if a pan has been initiated.
	 */
	protected boolean isPanning() {
		return panStart != null;
	}

	/**
	 * Initiates a zoom (rectangle).
	 * 
	 * @param e
	 *            the MouseEvent
	 * @return if zoom was really initiated
	 */
	protected boolean zoomStart(MouseEvent e) {
		zoomStart = new Point(e.x, e.y);
		return true;
	}

	/**
	 * @return if a zoom has been initiated.
	 */
	protected boolean isZooming() {
		return zoomStart != null;
	}

	@Override
	public void mouseUp(MouseEvent e) {
		boolean consumed = false;
		if (isPanning() && handlePanUp(e)) {
			consumed = true;
		} else if (isZooming() && handleZoomUp(e)) {
			consumed = true;
		}
		if (!consumed) {
			handleZoomClick(e);
		}
	}

	private int panScrollButtons = SWT.NONE;
	private int panScrollSpeed = 15;
	private int zoomScrollButtons = SWT.NONE;

	@Override
	public void mouseScrolled(MouseEvent e) {
		if (e.count > 0 && checkButtons(e, zoomScrollButtons)) {
			zoomIn(e);
		} else if (e.count < 0 && checkButtons(e, zoomScrollButtons)) {
			zoomOut(e);
		} else if (checkButtons(e, panScrollButtons)) {
			pan(e.count * panScrollSpeed, 0, true);
		}
	}

	/**
	 * Handles one pan step, according to the distance from the click to the
	 * current position
	 * 
	 * @param e
	 *            the MouseEvent
	 * @return if pan was active and the movement offset large enough
	 */
	protected boolean handlePanDrag(MouseEvent e) {
		if (isPanning()) {
			int dx = panStart.x - e.x, dy = panStart.y - e.y;
			if (dx * dx + dy * dy >= 4) {
				pan(downPosition.x + dx, downPosition.y + dy, false);
				return true;
			}
		}
		return false;
	}

	/**
	 * Handles one zoom step, extending the zoom rectangle.
	 * 
	 * @param e
	 *            the MouseEvent
	 * @return if zoom was active
	 */
	protected boolean handleZoomDrag(MouseEvent e) {
		if (isZooming()) {
			int minX = Math.min(zoomStart.x, e.x),
					minY = Math.min(zoomStart.y, e.y);
			int maxX = Math.max(zoomStart.x, e.x),
					maxY = Math.max(zoomStart.y, e.y);
			Point mapPosition = geoMap.getMapPosition();
			zoomRectangle = new Rectangle(mapPosition.x + minX,
					mapPosition.y + minY, maxX - minX, maxY - minY);
			return true;
		}
		return false;
	}

	/**
	 * Handles end of pan.
	 * 
	 * @param e
	 *            the MouseEvent
	 * @return if pan was active
	 */
	protected boolean handlePanUp(MouseEvent e) {
		boolean result = handlePanDrag(e);
		panStart = null;
		downPosition = null;
		return result;
	}

	/**
	 * Handles zooming to rectangle.
	 * 
	 * @param e
	 *            the MouseEvent
	 * @return if zoom was active
	 */
	protected boolean handleZoomUp(MouseEvent e) {
		if (isZooming()) {
			if (zoomRectangle != null && zoomRectangle.width >= 2
					&& zoomRectangle.height >= 2) {
				GeoMapUtil.zoomTo(getGeoMap(), getMapSize(), zoomRectangle, -1);
			}
			zoomStart = null;
			zoomRectangle = null;
			return true;
		}
		return false;
	}

	@Override
	public void paintControl(PaintEvent e) {
		if (zoomRectangle != null) {
			Point mapPosition = geoMap.getMapPosition();
			e.gc.drawRectangle(zoomRectangle.x - mapPosition.x,
					zoomRectangle.y - mapPosition.y, zoomRectangle.width,
					zoomRectangle.height);
		}
	}
}
