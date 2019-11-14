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

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Shell;

/**
 * Instances of this class are controls located on the top of a shell. They
 * display a dark panel on this shell
 */
public class DarkPanel {
	private final Shell parent;
	private static final String DARK_PANEL_KEY = "org.eclipse.nebula.widgets.opal.panels.DarkPanel";
	private int alpha;
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
	public DarkPanel(final Shell shell) {
		if (shell == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}

		if (shell.isDisposed()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}

		parent = shell;
		if (shell.getData(DARK_PANEL_KEY) != null) {
			throw new IllegalArgumentException("This shell has already an infinite panel attached on it !");
		}
		shell.setData(DARK_PANEL_KEY, this);
		alpha = 100;
	}

	/**
	 * Show the dark panel
	 */
	public void show() {
		if (parent.isDisposed()) {
			SWT.error(SWT.ERROR_WIDGET_DISPOSED);
		}

		panel = new Shell(parent, SWT.APPLICATION_MODAL | SWT.NO_TRIM);
		panel.setLayout(new FillLayout());
		panel.setAlpha(alpha);

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
		final Rectangle clientArea = ((Canvas) e.widget).getClientArea();
		final GC gc = e.gc;
		gc.setBackground(panel.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		gc.fillRectangle(clientArea);
	}

	/**
	 * Hide the dark panel
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
	 * @return the alpha value
	 */
	public int getAlpha() {
		return alpha;
	}

	/**
	 * @param alpha the alpha to set
	 */
	public void setAlpha(final int alpha) {
		this.alpha = alpha;
	}

}
