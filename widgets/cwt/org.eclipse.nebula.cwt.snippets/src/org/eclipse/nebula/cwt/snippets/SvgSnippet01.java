/****************************************************************************
 * Copyright (c) 2008, 2009 Jeremy Dowdall
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/

package org.eclipse.nebula.cwt.snippets;

import org.eclipse.nebula.cwt.svg.SvgDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SvgSnippet01 {

	private static float scale = 1.0f;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("SvgSnippet01");

		final SvgDocument svg = SvgDocument.load(SvgSnippet01.class.getResourceAsStream("resources/clock.svg"));

		shell.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.setAntialias(SWT.ON);

				Transform transform = new Transform(e.gc.getDevice());
				transform.scale(scale, scale);
				e.gc.setTransform(transform);

				svg.apply(e.gc, shell.getClientArea());

				transform.dispose();
			}
		});
		shell.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				shell.redraw();
			}
		});
		shell.addMouseWheelListener(new MouseWheelListener() {
			public void mouseScrolled(MouseEvent e) {
				if(e.count > 0) {
					scale *= 1.1f;
				} else {
					scale *= 0.9f;
				}
				shell.redraw();
			}
		});

		shell.pack();
		Point size = shell.getSize();
		size.x += 300;
		size.y += 300;
		Rectangle screen = display.getMonitors()[0].getBounds();
		shell.setBounds((screen.width - size.x) / 2, (screen.height - size.y) / 2, size.x, size.y);
		shell.open();
		while(!shell.isDisposed()) {
			if(!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
