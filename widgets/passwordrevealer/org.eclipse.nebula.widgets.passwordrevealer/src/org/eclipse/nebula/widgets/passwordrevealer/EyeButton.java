/*******************************************************************************
 * Copyright (c) 2019 Laurent CARON.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API
 * and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.passwordrevealer;

import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

class EyeButton extends Canvas {
	private static final int CIRCLE_RAY = 4;
	private boolean mouseIn;
	private boolean pressed;
	private final Color color;

	EyeButton(Composite parent, int style) {
		super(parent, SWT.DOUBLE_BUFFERED);
		addListeners();
		color = new Color(parent.getDisplay(), 0, 127, 222);
		SWTGraphicUtil.addDisposer(this, color);
		setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}

	private void addListeners() {
		addPaintListener(e -> {
			paintControl(e);
		});

		addListener(SWT.MouseEnter, event -> {
			mouseIn = true;
			redraw();
		});

		addListener(SWT.MouseExit, event -> {
			mouseIn = false;
			pressed = false;
			redraw();
		});

		addListener(SWT.MouseDown, event -> {
			pressed = true;
			((PasswordRevealer) getParent()).revealPassword();
			redraw();
		});

		addListener(SWT.MouseUp, event -> {
			pressed = false;
			((PasswordRevealer) getParent()).hidePassword();
			redraw();
		});
	}

	private void paintControl(PaintEvent e) {
		final GC gc = e.gc;
		if (!mouseIn && !pressed) {
			drawEye(gc, getDisplay().getSystemColor(SWT.COLOR_GRAY));
			return;
		}

		if (mouseIn && !pressed) {
			drawEye(gc, color);
			return;
		}

		final Rectangle rect = getClientArea();
		gc.setBackground(color);
		gc.fillRectangle(rect);

		drawEye(gc, getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}

	private void drawEye(GC gc, Color clr) {
		gc.setAdvanced(true);
		gc.setAntialias(SWT.ON);
		gc.setLineWidth(2);

		final Rectangle rect = getClientArea();
		final int eyeWidth = (int) (rect.width * .7);
		final int eyeHeight = (int) (rect.height * .5);
		gc.setForeground(clr);
		gc.drawOval((int) (rect.width * .15), (int) (rect.height * .25), eyeWidth, eyeHeight);

		gc.setBackground(clr);
		gc.fillOval(rect.width / 2 - CIRCLE_RAY / 2, rect.height / 2 - CIRCLE_RAY / 2, CIRCLE_RAY, CIRCLE_RAY);
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		final PasswordRevealer parent = (PasswordRevealer) getParent();
		final int preferred = parent.passwordField.computeSize(SWT.DEFAULT, hHint, changed).y;
		return super.computeSize(Math.max(preferred, 20), Math.max(preferred, 20), changed);
	}

}
