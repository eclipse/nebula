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

package org.eclipse.swt.nebula.widgets.ctree;

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
public abstract class AbstractColumn extends TableColumn {
	
	AbstractContainer container;
	private boolean autoWidth = false;
	private Color color;

	
	public AbstractColumn(AbstractContainer parent, int style) {
		super(parent.getInternalTable(), style);
		this.container = parent;
		container.setSortColumn(this);
		container.setSortDirection(SWT.DOWN);
		Listener listener = new Listener() {
			public void handleEvent(Event e) {
				switch(e.type) {
				case SWT.Move:
				case SWT.Resize:
					container.layout(e.type, AbstractColumn.this);
					container.redraw();
					break;
//				case SWT.Selection:
//					if(container.internalGetSortColumn() == AbstractColumn.this) {
//						if(SWT.UP == container.getSortDirection()) {
//							container.setSortDirection(SWT.DOWN);
//						} else {
//							container.setSortDirection(SWT.UP);
//						}
//					} else {
//						container.setSortColumn(AbstractColumn.this);
//						container.setSortDirection(SWT.DOWN);
//					}
				}
			}
		};
		addListener(SWT.Move, listener);
		addListener(SWT.Resize, listener);
//		addListener(SWT.Selection, listener);
	}

	protected void checkSubclass() {
		// ouch, that hurts! :)
	}
	
	public Color getColor() {
		return color;
	}

	private boolean isLast() {
		int[] ixs = container.getColumnOrder();
		return container.internalGetColumn(ixs[ixs.length-1]) == this;
	}
	
	public boolean isVisible() {
		Rectangle r = container.getClientArea();
		r.x += container.getScrollPosition().x;
		return r.intersects(getBounds());
	}
	
	public Rectangle getBounds() {
		return new Rectangle(getLeft(), container.getClientArea().y, getWidth(), container.getClientArea().height);
	}
	
	public int getLeft() {
		int gridline = container.getGridLineWidth();
		int[] ixs = container.getColumnOrder();
		int x = container.getClientArea().x;
		for(int i = 0; i < ixs.length; i++) {
			AbstractColumn column = container.internalGetColumn(ixs[i]);
			if(column != this) {
				x += column.getWidth() + gridline;
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
					ebounds.y - container.getScrollPosition().y,
					getWidth(),
					container.getClientArea().height
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
