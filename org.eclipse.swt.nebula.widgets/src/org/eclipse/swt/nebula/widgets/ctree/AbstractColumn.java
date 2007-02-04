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
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;


/**
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 */
public class AbstractColumn extends Item {
	
	AbstractContainer container;
	TableColumn nativeColumn;
	
	private String toolTipText;
	private int width;
	private int alignment;
	private boolean moveable;
	private boolean resizeable;
	private boolean autoWidth;
	private Color bgcolor;
	private Color fgcolor;
	boolean isNative;

	public AbstractColumn(AbstractContainer parent, int style) {
		super(parent, style);
		container = parent;
		container.addColumn(this, style);
		if(container.getNativeHeader()) {
			nativeColumn = new TableColumn(container.getInternalTable(), style);
			isNative = true;
			autoWidth = false;
		} else {
			isNative = false;
			autoWidth = true;
		}
		container.setSortColumn(this);
		container.setSortDirection(SWT.DOWN);
		Listener listener = new Listener() {
			public void handleEvent(Event e) {
				switch(e.type) {
				case SWT.Move:
					if(container.getNativeHeader()) {
						container.columnOrder = nativeColumn.getParent().getColumnOrder();
					}
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
		
		addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event event) {
				doDispose();
			}
		});
	}

	public void addControlListener(ControlListener listener) {
		if(isNative) {
			nativeColumn.addControlListener(listener);
		} else {
			// TODO
		}
	}

	public void addListener(int eventType, Listener listener) {
		if(isNative) {
			nativeColumn.addListener(eventType, listener);
		} else {
			super.addListener(eventType, listener);
		}
	}
	
	public void addSelectionListener(SelectionListener listener) {
		if(isNative) {
			nativeColumn.addSelectionListener(listener);
		} else {
			// TODO
		}
	}

	protected void doDispose() {
		if(container != null && !container.isDisposed()) {
			container.removeColumn(this);
			if(nativeColumn != null && !nativeColumn.isDisposed()) {
				nativeColumn.dispose();
			}
		}
	}
	
	public int getAlignment() {
		if(isNative) {
			return nativeColumn.getAlignment();
		} else {
			return alignment;
		}
	}
	
	public Rectangle getBounds() {
		return new Rectangle(getLeft(), container.getClientArea().y, getWidth(), container.getClientArea().height);
	}
	
	public Color getBackground() {
		return bgcolor;
	}
	
	public Color getForeground() {
		return fgcolor;
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

	public boolean getMoveable() {
		if(isNative) {
			return nativeColumn.getMoveable();
		} else {
			return moveable;
		}
	}
	
	public AbstractContainer getParent() {
		return container;
	}
	
	public boolean getResizable() {
		if(isNative) {
			return nativeColumn.getResizable();
		} else {
			return resizeable;
		}
	}
	
	public int getRight() {
		return getLeft() + getWidth();
	}

	public String getText() {
		if(isNative) {
			return nativeColumn.getText();
		} else {
			return super.getText();
		}
	}
	
	public String getToolTipText() {
		if(isNative) {
			return nativeColumn.getToolTipText();
		} else {
			return toolTipText;
		}
	}

	public int getWidth() {
		if(isNative) {
			return nativeColumn.getWidth();
		} else {
			return width;
		}
	}
	
	public boolean getAutoWidth() {
		return autoWidth;
	}

//	private boolean isLast() {
//		int[] ixs = container.getColumnOrder();
//		return container.internalGetColumn(ixs[ixs.length-1]) == this;
//	}

	public boolean isVisible() {
		Rectangle r = container.getClientArea();
		r.x += container.getScrollPosition().x;
		return r.intersects(getBounds());
	}

	public void pack() {
		// TODO: presently only packs the Header...
		if(isNative) {
			nativeColumn.pack();
		}
	}

	public void paint(GC gc, Rectangle ebounds) {
		if(bgcolor != null) {
			gc.setAlpha(35);
			gc.setBackground(bgcolor);
			gc.fillRectangle(
					getLeft(),
					ebounds.y - container.getScrollPosition().y,
					getWidth(),
					container.getClientArea().height
					);
		}
	}

	public void removeControlListener(ControlListener listener) {
		if(isNative) {
			nativeColumn.removeControlListener(listener);
		} else {
			// TODO
		}
	}

	public void removeSelectionListener(SelectionListener listener) {
		if(isNative) {
			nativeColumn.removeSelectionListener(listener);
		} else {
			// TODO
		}
	}

	public void setAlignment(int alignment) {
		if(isNative) {
			nativeColumn.setAlignment(alignment);
		} else {
			this.alignment = alignment;
		}
	}

	public void setBackground(Color color) {
		this.bgcolor = color;
	}

	public void setAutoWidth(boolean auto) {
		autoWidth = auto;
		setResizable(!auto);
	}

	public void setForeground(Color color) {
		this.fgcolor = color;
	}

	public void setImage(Image image) {
		if(isNative) {
			nativeColumn.setImage(image);
		} else {
			super.setImage(image);
		}
	}

	public void setMoveable(boolean moveable) {
		if(isNative) {
			nativeColumn.setMoveable(moveable);
		} else {
			this.moveable = moveable;
		}
	}

	public void setResizable(boolean resizable) {
		if(isNative) {
			nativeColumn.setResizable(resizable);
		} else {
			this.resizeable = resizable;
		}
	}

	public void setText(String string) {
		if(isNative) {
			nativeColumn.setText(string);
		} else {
			super.setText(string);
		}
	}

	public void setToolTipText(String string) {
		if(isNative) {
			nativeColumn.setToolTipText(string);
		} else {
			toolTipText = string;
		}
	}

	public void setWidth(int width) {
		if(isNative) {
			nativeColumn.setWidth(width);
		} else {
			this.width = width;
		}
	}
}
