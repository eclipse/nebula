/*******************************************************************************
 * Copyright (c) 2006-2019 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    chris.gross@us.ibm.com - initial API and implementation
 *    laurent.caron@gmail.com - Bug 316623 - Toggle for Mac
 *******************************************************************************/
package org.eclipse.nebula.widgets.grid.internal;

import java.util.Locale;

import org.eclipse.nebula.widgets.grid.AbstractRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;

/**
 * The renderer for tree item plus/minus expand/collapse toggle.
 *
 * @author chris.gross@us.ibm.com
 * @since 2.0.0
 */
public class ToggleRenderer extends AbstractRenderer {

	private static final boolean IS_MAC ;
	static {
		final String osProperty = System.getProperty("os.name");
		if (osProperty != null) {
			final String osName = osProperty.toUpperCase(Locale.getDefault());
			IS_MAC = osName.indexOf("MAC") > -1;
		} else {
			IS_MAC = false;
		}
	}

	/**
	 * Default constructor.
	 */
	public ToggleRenderer() {
		if (IS_MAC) {
			setSize(new Point(10, 10));
		} else {
			this.setSize(9, 9);
		}
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.IRenderer#paint(org.eclipse.swt.graphics.GC,
	 *      java.lang.Object)
	 */
	public void paint(GC gc, Object value) {

		if (IS_MAC) {
			paintMacTwistie(gc);
		} else {
			paintPlusMinus(gc);
		}

	}

	private void paintMacTwistie(GC gc) {
		Transform transform = new Transform(gc.getDevice());
		transform.translate(getBounds().x, getBounds().y);
		gc.setTransform(transform);

		Color back = gc.getBackground();
		Color fore = gc.getForeground();

		if (!isHover()) {
			gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		} else {
			gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_LIST_SELECTION));
		}

		gc.setBackground(gc.getForeground());
		if (isExpanded()) {
			gc.drawPolygon(new int[] { 1, 3, 4, 6, 5, 6, 8, 3 });
			gc.fillPolygon(new int[] { 1, 3, 4, 6, 5, 6, 8, 3 });
		} else {
			gc.drawPolygon(new int[] { 3, 1, 6, 4, 6, 5, 3, 8 });
			gc.fillPolygon(new int[] { 3, 1, 6, 4, 6, 5, 3, 8 });
		}

		if (isFocus()) {
			gc.setBackground(back);
			gc.setForeground(fore);
			gc.drawFocus(-1, -1, 12, 12);
		}

		gc.setTransform(null);
		transform.dispose();
	}

	private void paintPlusMinus(GC gc) {
		gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

		gc.fillRectangle(getBounds());

		gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));

		gc.drawRectangle(getBounds().x, getBounds().y, getBounds().width - 1, getBounds().height - 1);

		gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));

		gc.drawLine(getBounds().x + 2, getBounds().y + 4, getBounds().x + 6, getBounds().y + 4);

		if (!isExpanded()) {
			gc.drawLine(getBounds().x + 4, getBounds().y + 2, getBounds().x + 4, getBounds().y + 6);
		}
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.IRenderer#computeSize(org.eclipse.swt.graphics.GC,
	 *      int, int, java.lang.Object)
	 */
	public Point computeSize(GC gc, int wHint, int hHint, Object value) {
		return IS_MAC ? new Point(10, 10) : new Point(9, 9);
	}
}
