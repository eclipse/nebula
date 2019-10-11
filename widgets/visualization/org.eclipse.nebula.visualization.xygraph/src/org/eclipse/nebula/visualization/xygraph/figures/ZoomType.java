/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.figures;

import org.eclipse.nebula.visualization.xygraph.Messages;
import org.eclipse.nebula.visualization.xygraph.util.SingleSourceHelper2;
import org.eclipse.nebula.visualization.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * The type of zoom on XYGraph.
 * 
 * @author Xihui Chen
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public enum ZoomType {
	/** Interactive Rubberband zoom */
	RUBBERBAND_ZOOM(Messages.Zoom_Rubberband, XYGraphMediaFactory.getInstance().getImage("images/RubberbandZoom.png"),
			XYGraphMediaFactory.getInstance().getImage("images/RubberbandZoomCursor.png"),
			XYGraphMediaFactory.getInstance().getImage("images/HorizontalZoomCursorOnXAxis.png"),
			XYGraphMediaFactory.getInstance().getImage("images/VerticalZoomCursorOnYAxis.png"),
			XYGraphFlags.COMBINED_ZOOM | XYGraphFlags.SEPARATE_ZOOM, SWT.CURSOR_CROSS),

	/** Interactive Dynamic zoom */
	DYNAMIC_ZOOM(Messages.Zoom_Dynamic, XYGraphMediaFactory.getInstance().getImage("images/RubberbandZoom.png"),
			XYGraphMediaFactory.getInstance().getImage("images/RubberbandZoomCursor.png"),
			XYGraphMediaFactory.getInstance().getImage("images/HorizontalZoomCursorOnXAxis.png"),
			XYGraphMediaFactory.getInstance().getImage("images/VerticalZoomCursorOnYAxis.png"),
			XYGraphFlags.COMBINED_ZOOM | XYGraphFlags.SEPARATE_ZOOM, SWT.CURSOR_CROSS),

	/** Zoom via 'cursors' for horizontal start/end position */
	HORIZONTAL_ZOOM(Messages.Zoom_Horiz, XYGraphMediaFactory.getInstance().getImage("images/HorizontalZoom.png"),
			XYGraphMediaFactory.getInstance().getImage("images/HorizontalZoomCursor.png"),
			XYGraphMediaFactory.getInstance().getImage("images/HorizontalZoomCursorOnXAxis.png"), null,
			XYGraphFlags.COMBINED_ZOOM | XYGraphFlags.SEPARATE_ZOOM, SWT.CURSOR_SIZEWE),

	/** Zoom via 'cursors' for vertical start/end position */
	VERTICAL_ZOOM(Messages.Zoom_Vert, XYGraphMediaFactory.getInstance().getImage("images/VerticalZoom.png"),
			XYGraphMediaFactory.getInstance().getImage("images/VerticalZoomCursor.png"), null,
			XYGraphMediaFactory.getInstance().getImage("images/VerticalZoomCursorOnYAxis.png"),
			XYGraphFlags.COMBINED_ZOOM | XYGraphFlags.SEPARATE_ZOOM, SWT.CURSOR_SIZENS),

	/** Zoom 'in' around mouse pointer */
	ZOOM_IN(Messages.Zoom_In, XYGraphMediaFactory.getInstance().getImage("images/ZoomIn.png"),
			XYGraphMediaFactory.getInstance().getImage("images/ZoomInCursor.png"),
			XYGraphMediaFactory.getInstance().getImage("images/ZoomInCursorOnXAxis.png"),
			XYGraphMediaFactory.getInstance().getImage("images/ZoomInCursorOnYAxis.png"), XYGraphFlags.COMBINED_ZOOM,
			SWT.CURSOR_HAND),

	/** Zoom 'out' around mouse pointer */
	ZOOM_OUT(Messages.Zoom_Out, XYGraphMediaFactory.getInstance().getImage("images/ZoomOut.png"),
			XYGraphMediaFactory.getInstance().getImage("images/ZoomOutCursor.png"),
			XYGraphMediaFactory.getInstance().getImage("images/ZoomOutCursorOnXAxis.png"),
			XYGraphMediaFactory.getInstance().getImage("images/ZoomOutCursorOnYAxis.png"), XYGraphFlags.COMBINED_ZOOM,
			SWT.CURSOR_HAND),

	/** Zoom 'in' around mouse pointer along horizontal axis */
	ZOOM_IN_HORIZONTALLY(Messages.Zoom_InHoriz, XYGraphMediaFactory.getInstance().getImage("images/ZoomInHoriz.png"),
			XYGraphMediaFactory.getInstance().getImage("images/ZoomInHorizCursor.png"),
			XYGraphMediaFactory.getInstance().getImage("images/ZoomInCursorOnXAxis.png"), null,
			XYGraphFlags.SEPARATE_ZOOM, SWT.CURSOR_SIZEWE),

	/** Zoom 'out' around mouse pointer along horizontal axis */
	ZOOM_OUT_HORIZONTALLY(Messages.Zoom_OutHoriz, XYGraphMediaFactory.getInstance().getImage("images/ZoomOutHoriz.png"),
			XYGraphMediaFactory.getInstance().getImage("images/ZoomOutHorizCursor.png"),
			XYGraphMediaFactory.getInstance().getImage("images/ZoomOutCursorOnXAxis.png"), null,
			XYGraphFlags.SEPARATE_ZOOM, SWT.CURSOR_SIZEWE),

	/** Zoom 'in' around mouse pointer along vertical axis */
	ZOOM_IN_VERTICALLY(Messages.Zoom_InVert, XYGraphMediaFactory.getInstance().getImage("images/ZoomInVert.png"),
			XYGraphMediaFactory.getInstance().getImage("images/ZoomInVertCursor.png"), null,
			XYGraphMediaFactory.getInstance().getImage("images/ZoomInCursorOnYAxis.png"), XYGraphFlags.SEPARATE_ZOOM,
			SWT.CURSOR_SIZENS),

	/** Zoom 'out' around mouse pointer along vertical axes */
	ZOOM_OUT_VERTICALLY(Messages.Zoom_OutVert, XYGraphMediaFactory.getInstance().getImage("images/ZoomOutVert.png"),
			XYGraphMediaFactory.getInstance().getImage("images/ZoomOutVertCursor.png"), null,
			XYGraphMediaFactory.getInstance().getImage("images/ZoomOutCursorOnYAxis.png"), XYGraphFlags.SEPARATE_ZOOM,
			SWT.CURSOR_SIZENS),

	/** Zoom 'out' around mouse pointer */
	PANNING(Messages.Zoom_Pan, XYGraphMediaFactory.getInstance().getImage("images/Panning.png"),
			XYGraphMediaFactory.getInstance().getImage("images/PanningCursor.png"),
			XYGraphMediaFactory.getInstance().getImage("images/PanningCursorOnAxis.png"),
			XYGraphMediaFactory.getInstance().getImage("images/PanningCursorOnAxis.png"),
			XYGraphFlags.COMBINED_ZOOM | XYGraphFlags.SEPARATE_ZOOM, SWT.CURSOR_SIZEALL, false),

	/** Disarm zoom behavior */
	NONE(Messages.Zoom_None, XYGraphMediaFactory.getInstance().getImage("images/MouseArrow.png"), null, null, null,
			XYGraphFlags.COMBINED_ZOOM | XYGraphFlags.SEPARATE_ZOOM, SWT.CURSOR_ARROW, false);

	final private Image iconImage;
	final private String description;
	/**
	 * @see #setCursor(Cursor)
	 */
	private Cursor overrideCursor;
	final private Cursor defaultCursor;
	final private Cursor cursorOnXAxis;
	final private Cursor cursorOnYAxis;

	final private int flags;
	final private boolean isZoom;

	private ZoomType(final String description, final Image iconImage, final Image cursorImage,
			final Image cursorImageOnXAxis, final Image cursorImageOnYAxis, final int flags,
			final int backUpSWTCursorType) {
		this(description, iconImage, cursorImage, cursorImageOnXAxis, cursorImageOnYAxis, flags, backUpSWTCursorType,
				true);
	}

	/**
	 * Initialize
	 * 
	 * @param description
	 *            Description used for tool tip
	 * @param iconImage
	 *            Button icon
	 * @param cursorImage
	 *            Cursor when zoom type is selected
	 * @param flags
	 *            Bitwise 'or' of flags that specify in which zoom
	 *            configurations this zoom type should be included
	 * @see XYGraphFlags#COMBINED_ZOOM
	 * @see XYGraphFlags#SEPARATE_ZOOM
	 */
	private ZoomType(final String description, final Image iconImage, final Image cursorImage,
			final Image cursorImageOnXAxis, final Image cursorImageOnYAxis, final int flags,
			final int backUpSWTCursorType, boolean isZoom) {
		this.description = description;
		this.iconImage = iconImage;
		if (cursorImage == null)
			defaultCursor = Display.getDefault().getSystemCursor(SWT.CURSOR_ARROW);
		else
			defaultCursor = SingleSourceHelper2.createCursor(Display.getDefault(), cursorImage.getImageData(), 8, 8,
					backUpSWTCursorType);
		if (cursorImageOnXAxis == null) {
			cursorOnXAxis = defaultCursor;
		} else {
			cursorOnXAxis = SingleSourceHelper2.createCursor(Display.getDefault(), cursorImageOnXAxis.getImageData(), 8,
					8, backUpSWTCursorType);
		}
		if (cursorImageOnYAxis == null) {
			cursorOnYAxis = defaultCursor;
		} else {
			cursorOnYAxis = SingleSourceHelper2.createCursor(Display.getDefault(), cursorImageOnYAxis.getImageData(), 8,
					8, backUpSWTCursorType);
		}

		this.flags = flags;
		this.isZoom = isZoom;
	}

	/**
	 * @return the iconImageData
	 */
	public Image getIconImage() {
		return iconImage;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the cursor
	 */
	public Cursor getCursor() {
		if (overrideCursor != null) {
			return overrideCursor;
		}
		return defaultCursor;
	}

	/**
	 * @return the cursor on axis.
	 */
	public Cursor getCursorOnAxis(boolean horizontalAxis) {
		if (overrideCursor != null) {
			return overrideCursor;
		}
		if (horizontalAxis)
			return cursorOnXAxis;
		else
			return cursorOnYAxis;
	}

	/**
	 * Check if this zoom mode should be offered when a graph was created with
	 * given flags
	 * 
	 * @param flags
	 *            Flags of the XYGraph tool bar
	 * @return <code>true</code> if this zoom type applies
	 */
	public boolean useWithFlags(final int flags) {
		return (this.flags & flags) > 0;
	}

	@Override
	public String toString() {
		return description;
	}

	/**
	 * Return the unique id for the enum.
	 * 
	 * @return class name . enum name
	 */
	public String getId() {
		return getClass().getName() + "." + name();
	}

	/**
	 * XXX: Using this is a bad idea, it modifies global state and as a result
	 * does not fully work as intended. The overriding of the cursor *must* be
	 * done outside of the enum. The failing case is when more than one plot is
	 * open at the same time and each plot tries to set the override cursor. In
	 * that case the last one wins. Nowhere within the Nebula code base calls
	 * this method, it only exists to support clients that used this in the past
	 * and is therefore deprecated.
	 * <p>
	 * Override the cursor for the given zoom type.
	 * <p>
	 * Overriding the cursor is a normal operation for the {@link #NONE} cursor
	 * as when the cursor is NONE it is deactivated, so external control has an
	 * effect on the cursor.
	 * <p>
	 * When set to non-<code>null</code> value, {@link #getCursor()} and
	 * {@link #getCursorOnAxis(boolean)} will return the overridden cursor.
	 * 
	 * @param cursor
	 *            to use when overridden
	 * @deprecated see Javadocs above for details
	 */
	@Deprecated
	public void setCursor(Cursor cursor) {
		this.overrideCursor = cursor;
	}

	/**
	 * Some of the so-called ZoomTypes are not actually Zooms. The
	 * {@link #isZoom()} returns <code>true</code> if the Zoom type is actually
	 * a zoom operation. Returns <code>true</code> for all items, except for
	 * {@link #NONE} and {@value #PANNING}.
	 * 
	 * @return <code>true</code> if an actual zoom type
	 */
	public boolean isZoom() {
		return isZoom;
	}
}
