/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - Initial
 * implementation and API
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.heapmanager;

import org.eclipse.nebula.widgets.opal.commons.ResourceManager;
import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

/**
 * Instances of this class are controls that display the memory used, the whole
 * memory, and contains a button to perform a GC
 */
public class HeapManager extends Composite {
	private Canvas bar;
	private Button button;
	private int heapMaxSize;
	private int heapSize;
	private Color barBorderColor;
	private Color barInnerColor;
	private Color barTextColor;
	private Color barGradientColorTopStart;
	private Color barGradientColorTopEnd;
	private Color barGradientColorMiddleStart;

	/**
	 * Constructs a new instance of this class given its parent and a style value
	 * describing its behavior and appearance.
	 * <p>
	 * The style value is either one of the style constants defined in class
	 * <code>SWT</code> which is applicable to instances of this class, or must be
	 * built by <em>bitwise OR</em>'ing together (that is, using the
	 * <code>int</code> "|" operator) two or more of those <code>SWT</code> style
	 * constants. The class description lists the style constants that are
	 * applicable to the class. Style bits are also inherited from superclasses.
	 * </p>
	 *
	 * @param parent a widget which will be the parent of the new instance (cannot
	 *            be null)
	 * @param style the style of widget to construct
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the parent</li>
	 *                </ul>
	 *
	 * @see Composite#Composite(Composite, int)
	 * @see Widget#getStyle
	 */
	public HeapManager(final Composite parent, final int style) {
		super(parent, style);
		final GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = gridLayout.verticalSpacing = 0;
		setLayout(gridLayout);

		createBar();
		createButton();
		updateContent();
		createDefaultColors();
	}

	/**
	 * Creates the bar that displays the memory
	 */
	private void createBar() {
		bar = new Canvas(this, SWT.NONE);
		final GridData gd = new GridData(GridData.FILL, GridData.FILL, true, false);
		gd.minimumWidth = 100;
		gd.heightHint = 30;
		bar.setLayoutData(gd);
		heapMaxSize = (int) (Runtime.getRuntime().maxMemory() / (1024 * 1024));
		bar.addPaintListener(e -> {
			drawBar(e);
		});
	}

	/**
	 * Draw the bar
	 *
	 * @param e {@link PaintEvent}
	 */
	private void drawBar(final PaintEvent e) {
		final GC gc = e.gc;
		final Rectangle clientArea = bar.getClientArea();

		gc.setForeground(barBorderColor);
		gc.setBackground(barInnerColor);
		gc.fillRectangle(clientArea);
		gc.drawRectangle(clientArea.x, clientArea.y, clientArea.width - 1, clientArea.height - 1);

		final float width = (clientArea.width - 2f) * heapSize / heapMaxSize;

		gc.setForeground(barGradientColorTopStart);
		gc.setBackground(barGradientColorTopEnd);
		gc.fillGradientRectangle(clientArea.x + 1, clientArea.y + 1, (int) width, clientArea.height / 2, true);

		gc.setForeground(barGradientColorMiddleStart);
		gc.setBackground(barBorderColor);
		gc.fillGradientRectangle(clientArea.x + 1, clientArea.height / 2, (int) width, clientArea.height / 2, true);

		final String message = heapSize + " " + ResourceManager.getLabel(ResourceManager.MEGABYTES) + "/" + //
				heapMaxSize + " " + ResourceManager.getLabel(ResourceManager.MEGABYTES);
		final Point size = gc.stringExtent(message);

		gc.setForeground(barTextColor);
		gc.setFont(getFont());
		gc.drawText(message, (clientArea.width - size.x) / 2, (clientArea.height - size.y) / 2, true);

		gc.dispose();

	}

	/**
	 * Create the button used to perform GC
	 */
	private void createButton() {
		button = new Button(this, SWT.PUSH);
		final Image image = SWTGraphicUtil.createImageFromFile("images/trash.png");
		button.setImage(image);
		SWTGraphicUtil.addDisposer(button, image);
		button.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		button.addListener(SWT.Selection, e -> {
			System.gc();
		});
		button.setToolTipText(ResourceManager.getLabel(ResourceManager.PERFORM_GC));
		button.pack();
	}

	/**
	 * Update the content of the bar
	 */
	private void updateContent() {
		getDisplay().timerExec(500, new Runnable() {
			@Override
			public void run() {
				heapSize = (int) (Runtime.getRuntime().totalMemory() / (1024 * 1024));
				if (!isDisposed()) {
					bar.redraw();
					if (!getDisplay().isDisposed()) {
						getDisplay().timerExec(500, this);
					}
				}
			}
		});
	}

	/**
	 * Creates the default colors
	 */
	private void createDefaultColors() {
		barTextColor = SWTGraphicUtil.getDefaultColor(this, 57, 98, 149);
		barInnerColor = SWTGraphicUtil.getDefaultColor(this, 219, 230, 243);
		barBorderColor = SWTGraphicUtil.getDefaultColor(this, 101, 148, 207);
		barGradientColorTopStart = SWTGraphicUtil.getDefaultColor(this, 175, 202, 237);
		barGradientColorTopEnd = SWTGraphicUtil.getDefaultColor(this, 136, 177, 229);
		barGradientColorMiddleStart = SWTGraphicUtil.getDefaultColor(this, 112, 161, 223);
	}

	/**
	 * @return the barBorderColor
	 */
	public Color getBarBorderColor() {
		return barBorderColor;
	}

	/**
	 * @param barBorderColor the barBorderColor to set
	 */
	public void setBarBorderColor(final Color barBorderColor) {
		this.barBorderColor = barBorderColor;
	}

	/**
	 * @return the barInnerColor
	 */
	public Color getBarInnerColor() {
		return barInnerColor;
	}

	/**
	 * @param barInnerColor the barInnerColor to set
	 */
	public void setBarInnerColor(final Color barInnerColor) {
		this.barInnerColor = barInnerColor;
	}

	/**
	 * @return the barTextColor
	 */
	public Color getBarTextColor() {
		return barTextColor;
	}

	/**
	 * @param barTextColor the barTextColor to set
	 */
	public void setBarTextColor(final Color barTextColor) {
		this.barTextColor = barTextColor;
	}

	/**
	 * @return the barGradientColorTopStart
	 */
	public Color getBarGradientColorTopStart() {
		return barGradientColorTopStart;
	}

	/**
	 * @param barGradientColorTopStart the barGradientColorTopStart to set
	 */
	public void setBarGradientColorTopStart(final Color barGradientColorTopStart) {
		this.barGradientColorTopStart = barGradientColorTopStart;
	}

	/**
	 * @return the barGradientColorTopEnd
	 */
	public Color getBarGradientColorTopEnd() {
		return barGradientColorTopEnd;
	}

	/**
	 * @param barGradientColorTopEnd the barGradientColorTopEnd to set
	 */
	public void setBarGradientColorTopEnd(final Color barGradientColorTopEnd) {
		this.barGradientColorTopEnd = barGradientColorTopEnd;
	}

	/**
	 * @return the barGradientColorMiddleStart
	 */
	public Color getBarGradientColorMiddleStart() {
		return barGradientColorMiddleStart;
	}

	/**
	 * @param barGradientColorMiddleStart the barGradientColorMiddleStart to set
	 */
	public void setBarGradientColorMiddleStart(final Color barGradientColorMiddleStart) {
		this.barGradientColorMiddleStart = barGradientColorMiddleStart;
	}

}
