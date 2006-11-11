/****************************************************************************
* Copyright (c) 2006 Jeremy Dowdall
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
*****************************************************************************/

package org.eclipse.swt.nebula.widgets.ctabletree;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;


/**
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 */
public class CContainerColumn extends TableColumn {
	
	CContainer container;
	private boolean autoWidth = false;
	private Color color;

	
	public CContainerColumn(CContainer parent, int style) {
		super(parent.getInternalTable(), style);
		this.container = parent;
		addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event e) {
				if(SWT.Resize == e.type) {
					container.setOperation(CContainer.OP_COLUMN_RESIZE);
					container.opLayout();
					container.redraw();
				}
			}
		});
	}

	protected void checkSubclass() {
		// ouch, that hurts! :)
	}
	
	public Color getColor() {
		return color;
	}

	private boolean isLast() {
		int[] ixs = container.getColumnOrder();
		return container.getColumn(ixs[ixs.length-1]) == this;
	}
	
	public int getLeft() {
		int[] ixs = container.getColumnOrder();
		int x = 0;
		for(int i = 0; i < ixs.length; i++) {
			CContainerColumn column = container.getColumn(i);
			if(column != this) {
				x += column.getWidth();
			} else {
				break;
			}
		}
		return x;
	}
	
	public int getRight() {
		return getLeft() +(isLast() ? 
				container.layout.columnWidths[container.layout.columnWidths.length-1] :
					getWidth());
	}

	public boolean isAutoWidth() {
		return autoWidth;
	}
	
	public void pack() {
		// TODO: presently only packs the Header...
		super.pack();
	}
	
	public void paint(GC gc, Rectangle ebounds) {
		if(color != null) {
			gc.setAlpha(35);
			gc.setBackground(color);
			gc.fillRectangle(
					getLeft(),
					ebounds.y - container.getOrigin().y,
					getWidth(),
					container.getBodySize().y
					);
		}
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public void setFillLayout(boolean fill) {
		autoWidth = fill;
		setResizable(!fill);
	}
}
