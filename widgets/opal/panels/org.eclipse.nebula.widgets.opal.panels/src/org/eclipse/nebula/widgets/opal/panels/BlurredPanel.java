/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.panels;

import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Shell;

/**
 * Instances of this class are controls located on the top of a shell. They
 * display a blurred version of the content of the shell
 */
public class BlurredPanel {
	private final Shell parent;
	private static final String BLURED_PANEL_KEY = "org.eclipse.nebula.widgets.opal.panels.BlurredPanel";
	private int radius;
	private Shell panel;
	private Canvas canvas;

	/**
	 * Constructs a new instance of this class given its parent.
	 *
	 * @param shell a shell that will be the parent of the new instance (cannot
	 *            be null)
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the parent has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the parent</li>
	 *                </ul>
	 */
	public BlurredPanel(final Shell shell) {
		if (shell == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}

		if (shell.isDisposed()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}

		parent = shell;
		if (shell.getData(BLURED_PANEL_KEY) != null) {
			throw new IllegalArgumentException("This shell has already an infinite panel attached on it !");
		}
		shell.setData(BLURED_PANEL_KEY, this);
		radius = 2;
	}

	/**
	 * Show the blurred panel
	 */
	public void show() {
		if (parent.isDisposed()) {
			SWT.error(SWT.ERROR_WIDGET_DISPOSED);
		}

		panel = new Shell(parent, SWT.APPLICATION_MODAL | SWT.NO_TRIM);
		panel.setLayout(new FillLayout());

		panel.addListener(SWT.KeyUp, event -> {
			event.doit = false;
		});

		canvas = new Canvas(panel, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
		canvas.addPaintListener(event -> {
			paintCanvas(event);
		});

		panel.setBounds(panel.getDisplay().map(parent, null, parent.getClientArea()));
		panel.open();

	}

	/**
	 * Paint the canvas that holds the panel
	 *
	 * @param e {@link PaintEvent}
	 */
	private void paintCanvas(final PaintEvent e) {
		// Paint the panel
		e.gc.drawImage(createBlurredImage(), 0, 0);
	}

	private Image createBlurredImage() {
		final GC gc = new GC(parent);
		final Image image = new Image(parent.getDisplay(), parent.getSize().x, parent.getSize().y);
		gc.copyArea(image, 0, 0);
		gc.dispose();

		return new Image(parent.getDisplay(), SWTGraphicUtil.blur(image.getImageData(), radius));

	}

	/**
	 * Hide the panel
	 */
	public void hide() {
		if (parent.isDisposed()) {
			SWT.error(SWT.ERROR_WIDGET_DISPOSED);
		}

		if (panel == null || panel.isDisposed()) {
			return;
		}

		panel.dispose();
	}

	/**
	 * @return the radius of the blur effect
	 */
	public int getRadius() {
		return radius;
	}

	/**
	 * @param radius the radius to set
	 */
	public void setRadius(final int radius) {
		this.radius = radius;
	}

}
