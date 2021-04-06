/****************************************************************************
 * Copyright (c) 2008, 2009 Jeremy Dowdall
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/

package org.eclipse.nebula.cwt.v;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;

public class VControlPainter implements IControlPainter {

	private static double getX(VControl control, double width) {
		double x;

		if(control.xAlign == SWT.LEFT) {
			x = control.marginLeft;
		} else if(control.xAlign == SWT.RIGHT) {
			x = control.bounds.width - width - control.marginRight;
		} else { // CENTERED / Default
			x = ((control.bounds.width - width) / 2.0);
		}

		x += control.bounds.x;

		return x;
	}

	private static double getY(VControl control, double height) {
		double y;

		if(control.yAlign == SWT.TOP) {
			y = control.marginTop;
		} else if(control.yAlign == SWT.BOTTOM) {
			y = control.bounds.height - height - control.marginBottom;
		} else { // CENTERED / Default
			y = ((control.bounds.height - height) / 2.0);
		}

		y += control.bounds.y;

		return y;
	}

	private static void paintImage(VControl control, Event e) {
		Rectangle ibounds = control.image.getBounds();
		if(control.scaleImage) {
			Rectangle cbounds = control.getClientArea();
			e.gc.drawImage(control.image, 0, 0, ibounds.width, ibounds.height, cbounds.x, cbounds.y, cbounds.width, cbounds.height);
		} else {
			e.gc.drawImage(control.image, (int)getX(control, ibounds.width), (int)getY(control, ibounds.height));
		}
	}

	private static void paintImageAndText(VControl control, Event e) {
		e.gc.setTextAntialias(SWT.ON);
		if(control.foreground != null && !control.foreground.isDisposed()) {
			e.gc.setForeground(control.foreground);
		}

		Rectangle ibounds = control.image.getBounds();
		Point tsize = e.gc.textExtent(control.text);

		int x = (int)getX(control, ibounds.width + tsize.x);
		
		e.gc.drawImage(control.image, x, (int)getY(control, ibounds.height));
		
		x += ibounds.width + 3;
		
		e.gc.drawText(control.text, x, (int)getY(control, tsize.y), true);
	}

	private static void paintOval(VControl control, Event e, int x, int y) {
		if(control.fill != null && !control.fill.isDisposed()) {
			e.gc.setBackground(control.fill);
			e.gc.fillOval(x, y, control.points[0], control.points[1]);
		}

		e.gc.drawOval(x, y, control.points[0], control.points[1]);
	}

	private static void paintPoly(VControl control, Event e, int x, int y, int minX, int minY) {
		int[] data = new int[control.points.length];
		for(int i = 0; i < control.points.length; i += 2) {
			data[i] = control.points[i] + x - minX - 1;
		}
		for(int i = 1; i < data.length; i += 2) {
			data[i] = control.points[i] + y - minY;
		}

		if(control.fill != null && !control.fill.isDisposed()) {
			e.gc.setBackground(control.fill);
			e.gc.fillPolygon(data);
		}

		e.gc.drawPolygon(data);
	}

	private static void paintPolygon(VControl control, Event e) {
		e.gc.setAntialias(SWT.ON);
		if(control.foreground != null && !control.foreground.isDisposed()) {
			e.gc.setForeground(control.foreground);
		}

		int minX = (control.points.length > 2) ? control.points[0] : 0;
		int maxX = control.points[0];
		int minY = (control.points.length > 2) ? control.points[1] : 0;
		int maxY = control.points[1];
		for(int i = 2; i < (control.points.length - 1); i++) {
			minX = Math.min(minX, control.points[i]);
			maxX = Math.max(maxX, control.points[i]);
			minY = Math.min(minY, control.points[i + 1]);
			maxY = Math.max(maxY, control.points[i + 1]);
		}

		int x = (int)getX(control, maxX-minX);
		int y = (int)getY(control, maxY-minY);
		
		if(control.points.length > 2) {
			paintPoly(control, e, x, y, minX, minY);
		} else {
			paintOval(control, e, x, y);
		}
	}
	
	private static void paintPolygonAndText(VControl control, Event e) {
		e.gc.setAntialias(SWT.ON);
		e.gc.setTextAntialias(SWT.ON);
		if(control.foreground != null && !control.foreground.isDisposed()) {
			e.gc.setForeground(control.foreground);
		}

		int minX = (control.points.length > 2) ? control.points[0] : 0;
		int maxX = control.points[0];
		int minY = (control.points.length > 2) ? control.points[1] : 0;
		int maxY = control.points[1];
		for(int i = 2; i < (control.points.length - 1); i++) {
			minX = Math.min(minX, control.points[i]);
			maxX = Math.max(maxX, control.points[i]);
			minY = Math.min(minY, control.points[i + 1]);
			maxY = Math.max(maxY, control.points[i + 1]);
		}

		Point psize = new Point(maxX-minX, maxY-minY);
		Point tsize = e.gc.textExtent(control.text);

		int x = (int)getX(control, psize.x+tsize.x);
		int y = (int)getY(control, psize.y);
		
		if(control.points.length > 2) {
			paintPoly(control, e, x, y, minX, minY);
		} else {
			paintOval(control, e, x, y);
		}
		
		x += psize.x + 3;
		
		e.gc.drawText(control.text, x, (int)getY(control, tsize.y), true);
	}
	
	private static void paintText(VControl control, Event e) {
		Font current = e.gc.getFont();
		e.gc.setTextAntialias(SWT.ON);
		if (control.foreground != null && !control.foreground.isDisposed()) {
			e.gc.setForeground(control.foreground);
		}
		
		if (control.font != null && !control.font.isDisposed() ) {
			e.gc.setFont(control.font);
		}
		
		Point size = e.gc.textExtent(control.text);
		e.gc.drawText(control.text, (int)getX(control, size.x), (int)getY(control, size.y), true);
		e.gc.setFont(current);
	}

	public void dispose() {
		// nothing to do
	}

	public void paintBackground(VControl control, Event e) {
		int alpha = e.gc.getAlpha();
		if(!control.isEnabled()) {
			control.setAlpha(e.gc, 170);
		}
		if(control.background != null && !control.background.isDisposed()) {
			e.gc.setBackground(control.background);
			e.gc.fillRectangle(control.bounds);
		}
		e.gc.setAlpha(alpha);
	}

	public void paintBorders(VControl control, Event e) {
		int alpha = e.gc.getAlpha();
		if(!control.isEnabled()) {
			control.setAlpha(e.gc, 170);
		}
		if(control.hasStyle(SWT.BORDER)) {
			Rectangle r = control.getBounds();
			e.gc.setForeground(control.getForeground());
			e.gc.drawRectangle(r.x, r.y, r.width - 1, r.height - 1);
		}
		e.gc.setAlpha(alpha);
	}

	public void paintContent(VControl control, Event e) {
		int alpha = e.gc.getAlpha();
		Font current = e.gc.getFont();
		if(!control.isEnabled()) {
			control.setAlpha(e.gc, 170);
		}
		if(control.svg != null) {
			if(control.text != null) {
				paintImageAndText(control, e);
			} else {
				control.svg.apply(e.gc, control.getClientArea());
			}
		} else if(control.image != null && !control.image.getDevice().isDisposed()) {
			if(control.text != null) {
				paintImageAndText(control, e);
			} else {
				paintImage(control, e);
			}
		} else if(control.points != null && control.points.length > 0) {
			if(control.text != null) {
				paintPolygonAndText(control, e);
			} else {
				paintPolygon(control, e);
			}
		} else if(control.text != null) {
			paintText(control, e);
		}
		e.gc.setAlpha(alpha);
		e.gc.setFont(current);
	}
	
}
