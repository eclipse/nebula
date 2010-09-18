/*******************************************************************************
 * Copyright (c) 2010 Ubiquiti Networks, Inc. and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl<tom.schindl@bestsolution.at> - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.pgroup;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class SimpleToolItemRenderer extends AbstractToolItemRenderer {
	private int padding = 2;
	private int dropDownWidth = 10;

	public void paint(GC gc, Object value) {
		PGroupToolItem item = (PGroupToolItem) value;

		Rectangle rect = getBounds();
		int alpha = gc.getAlpha();
		Color bg = gc.getBackground();

		if( isHover() && ! item.getSelection() ) {
			gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
			gc.setAlpha(100);
			gc.fillRoundRectangle(rect.x, rect.y,rect.width,rect.height,3,3);
			gc.setBackground(bg);
			gc.setAlpha(alpha);
		}

		if( item.getSelection() ) {
			gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_BLACK));
			gc.setAlpha(100);
			gc.fillRoundRectangle(rect.x, rect.y,rect.width,rect.height,3,3);
			gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
			gc.fillRoundRectangle(rect.x+1, rect.y+1,rect.width-2,rect.height-2,3,3);
			
			gc.setBackground(bg);
			gc.setAlpha(alpha);
		}
		
		if (item.getText().length() > 0 && item.getImage() != null
				&& getSizeType() != MIN) {
			gc.drawImage(item.getImage(), rect.x + padding, rect.y + (int)(rect.height / 2.0 - item.getImage().getImageData().height / 2.0));
			Point p = gc.textExtent(item.getText());
			gc.drawString(item.getText(), rect.x + padding + item.getImage().getImageData().width + 2, rect.y + (int)(rect.height / 2.0 - p.y / 2.0), true);
		} else if (item.getImage() != null) {
			gc.drawImage(item.getImage(), rect.x + padding, rect.y + (int)(rect.height / 2.0 - item.getImage().getImageData().height / 2.0));
		} else if (item.getText().length() > 0) {
			Point p = gc.textExtent(item.getText());
			gc.drawString(item.getText(), rect.x + padding, rect.y + (int)(rect.height / 2.0 - p.y / 2.0), true);
		}

		if( (item.getStyle() & SWT.DROP_DOWN) != 0 ) {
			gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_BLACK));
			gc.fillPolygon(new int[] { rect.x + rect.width - 2, rect.y + rect.height / 2, rect.x + rect.width - 8, rect.y + rect.height / 2, rect.x + rect.width - 5, rect.y + rect.height / 2 + 4 }  );
		}

		gc.setAlpha(alpha);
		gc.setBackground(bg);
	}

	public Point computeSize(GC gc, PGroupToolItem item, int type) {
		int dropDown = (item.getStyle() & SWT.DROP_DOWN) != 0 ? dropDownWidth : 0;

		if( item.getText().length() > 0 && item.getImage() != null ) {
			Point p = gc.textExtent(item.getText());
			int y = p.y;
			if( type == DEFAULT ) {
				return new Point(p.x + item.getImage().getImageData().width + padding * 3 + dropDown, y);
			} else {
				return new Point(item.getImage().getImageData().width + padding * 2 + dropDown, y);
			}
		} else if( item.getText().length() > 0 ) {
			Point p = gc.textExtent(item.getText());
			int x = p.x + padding * 2 + dropDown;
			int y = p.y;
			return new Point(x, y);
		} else if( item.getImage() != null ) {
			int x = item.getImage().getImageData().width + padding * 2 + dropDown;
			int y = item.getImage().getImageData().height;
			return new Point(x, y);
		}

		return null;
	}

	public Rectangle computeDropDownArea(Rectangle totalRect) {
		return new Rectangle(totalRect.x + totalRect.width - dropDownWidth, totalRect.y, totalRect.width, totalRect.height);
	}
}