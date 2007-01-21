/****************************************************************************
 * Copyright (c) 2005-2006 Jeremy Dowdall
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/

package org.eclipse.swt.nebula.widgets.ctree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.swt.widgets.Widget;


/**
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 */
public class CTree extends AbstractContainer {

	public static final int OP_TREE_COL			= 6;
	public static final int OP_TREE_COLLAPSE	= 7;
	public static final int OP_TREE_EXPAND 		= 8;

	private boolean selectOnTreeToggle = false;
	private int treeColumn = 0;
	private int treeIndent = 16;

	/**
	 * A list of items which have the CTree as their parent.<br>
	 * These are the items to be returned by the getItem methods.
	 */
	List roots = new ArrayList();
	
	public CTree(Composite parent, int style) {
		super(parent, style);
//		setLinesVisible(true);
		setLayout(layout = new CTreeLayout(this));
	}

	protected void addItem(int index, AbstractItem item) {
		super.addItem(index, item);
		CTreeItem citem = (CTreeItem) item;
		if(!citem.hasParentItem()) {
			roots.add(citem);
		} else {
			citem.setTreeIndent(citem.getParentItem().getTreeIndent() + treeIndent);
			if(citem.getParentItem().getVisible()) {
				redraw(citem.getParentItem().getTreeCell());
			}
		}
	}
	
	public void addTreeListener(TreeListener listener) {
		checkWidget ();
		if(listener != null) {
			TypedListener typedListener = new TypedListener (listener);
			addListener (SWT.Collapse, typedListener);
			addListener (SWT.Expand, typedListener);
		}
	}

	private void fireTreeEvent(Widget item, boolean collapse) {
		Event event = new Event();
		event.type = collapse ? SWT.Collapse : SWT.Expand;
		event.item = item;
		notifyListeners(event.type, event);
	}

	public CTreeColumn getColumn(int index) {
		return (CTreeColumn) internalGetColumn(index);
	}
	
//	public int getLastPaintedItemIndex() {
//		return visibleItems.indexOf(getLastPaintedItem());
//	}

	protected List getPaintedItems() {
		int top = getScrollPosition().y;
		int bot = top + getClientArea().height;
		int itop = 0;
		int ibot = 0;
		List list = new ArrayList();
		boolean painting = false;
		for(Iterator i = visibleItems.iterator(); i.hasNext(); ) {
			CTreeItem item = (CTreeItem) i.next();
			Rectangle r = item.getBounds();
			ibot = r.y+r.height;
			if(itop <= top && top < ibot) {
				painting = true;
			}
			if(painting) {
				list.add(item);
			}
			if(itop < bot && bot <= ibot) {
				break;
			}
			itop = r.y+r.height;
		}
		return list;
	}
	
//	public AbstractItem getLastPaintedItem() {
//		int bot = getScrollPosition().y;
//		bot += getClientArea().height;
//		int itop = 0;
//		int ibot = 0;
//		for(Iterator i = visibleItems.iterator(); i.hasNext(); ) {
//			AbstractItem item = (AbstractItem) i.next();
//			Rectangle r = item.getBounds();
//			ibot = r.y+r.height;
//			if(itop < bot && bot <= ibot) {
//				return item;
//			}
//			itop = r.y+r.height;
//		}
//		return (AbstractItem) visibleItems.get(visibleItems.size()-1);
//	}

	/**
	 * returns a deep list of items belonging to the given item
	 * {@inheritDoc}
	 */
	protected List getItems(AbstractItem item) {
		return getItems(item, true);
	}
	protected List getItems(AbstractItem item, boolean includeCollapsed) {
		List l = new ArrayList();
		CTreeItem[] items = ((CTreeItem) item).getItems();
		for(int i = 0; i < items.length; i++) {
			l.add(items[i]);
			if(includeCollapsed || items[i].getExpanded()) {
				l.addAll(getItems(items[i]));
			}
		}
		return l;
	}

	public int getItemCount() {
		return roots.size();
	}

	public CTreeItem getItem(Point p) {
		return (CTreeItem) internalGetItem(p);
	}
	
	/**
	 * Returns a (possibly empty) array of items contained in the
	 * receiver that are direct item children of the receiver.  These
	 * are the roots of the tree.
	 * <p>
	 * Note: This is not the actual structure used by the receiver
	 * to maintain its list of items, so modifying the array will
	 * not affect the receiver. 
	 * </p>
	 *
	 * @return the items
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public CTreeItem[] getItems() {
		return roots.isEmpty() ?
				new CTreeItem[0] : 
					(CTreeItem[]) roots.toArray(new CTreeItem[roots.size()]);
	}

	public CTreeItem[] getSelection() {
		return selection.isEmpty() ? 
			new CTreeItem[0] : 
				(CTreeItem[]) selection.toArray(new CTreeItem[selection.size()]);
	}

	/**
	 * @see CTree#setSelectOnTreeToggle(boolean)
	 * @return
	 */
	public boolean getSelectOnTreeToggle() {
		return selectOnTreeToggle;
	}

//	public int getFirstPaintedItemIndex() {
//		return visibleItems.indexOf(getTopItem());
//	}

	public CTreeItem getTopItem() {
		int top = getScrollPosition().y;
		int itop = 0;
		int ibot = 0;
		for(Iterator i = visibleItems.iterator(); i.hasNext(); ) {
			CTreeItem item = (CTreeItem) i.next();
			Rectangle r = item.getBounds();
			ibot = r.y+r.height;
			if(itop <= top && top < ibot) {
				return item;
			}
			itop = r.y+r.height;
		}
		return null;
	}

	public int getTreeColumn() {
		return treeColumn;
	}

	public int getTreeIndent() {
		return treeIndent;
	}

	public CTreeItem[] getVisibleItems() {
		return (visibleItems.isEmpty()) ? new CTreeItem[0] : (CTreeItem[]) visibleItems.toArray(new CTreeItem[visibleItems.size()]);
	}

	protected boolean handleMouseEvents(AbstractItem item, Event event) {
		if(item != null && item instanceof CTreeItem) {
			CTreeItem ti = (CTreeItem) item;
			Point pt = mapPoint(event.x, event.y);
			switch (event.type) {
			case SWT.MouseDoubleClick:
			case SWT.MouseDown:
				if(!selectOnTreeToggle && ti.isTreeTogglePoint(pt)) return false;
				break;
			case SWT.MouseUp:
				if(ti.isTogglePoint(pt)) {
					boolean open = ti.isOpen(pt);
					boolean tree = ti.isTreeTogglePoint(pt);
					int etype = open ? SWT.Collapse : SWT.Expand;
					ti.removeListener(etype, this);
					ti.setOpen(pt, !open);
					ti.addListener(etype, this);
					layout(etype, ti.getTreeCell());
					fireTreeEvent(ti, SWT.Collapse == event.type);
					if(tree) break;
				}
				break;
			}
			return true;
		}
		return false;
	}

	/**
	 * Convenience method indicating whether or not the treeColumn is set to an
	 * actual column, and thus the tree hierarchy will be displayed.
	 * <p>Note that if the hierarchy is not displayed, then certain methods are
	 * able to be optimized and will take advantage of this fact</p>
	 * @return true if treeColumn is set to an existing column, false otherwise
	 */
	public boolean hasTreeColumn() {
		return treeColumn >= 0 && treeColumn < getColumnCount();
	}

	
	/**
	 * Use this method to find out if an item is visible, and thus has the potential to be
	 * painted.
	 * <p>An Item will be visible when every parent between it and the root of the tree
	 * is expanded</p>
	 * @param item the item in question
	 * @return true if the item can be painted to the screen, false if it is hidden due to a
	 * parent item being collapsed
	 */
	public boolean isVisible(CTreeItem item) {
		CTreeItem parentItem = item.getParentItem();
		if(parentItem == null) return true;
		if(parentItem.getExpanded()) return isVisible(parentItem);
		return false;
	}

	void layout(int eventType, AbstractCell cell) {
		if((SWT.Collapse == eventType || SWT.Expand == eventType) && ((CTreeCell) cell).isTreeCell()) {
			if(SWT.Collapse == eventType) {
				visibleItems.removeAll(getItems(cell.item, false));
				layout.layout(eventType, cell);
			} else if(isVisible((CTreeItem) cell.item)) {
				visibleItems.addAll(visibleItems.indexOf(cell.item)+1, getItems(cell.item, false));
				layout.layout(eventType, cell);
			}
			updatePaintedList = true;
		} else {
			super.layout(eventType, cell);
		}
	}
	
	void layout(int eventType, AbstractItem item) {
		if(hasTreeColumn()) {
			if(SWT.Show == eventType && !visibleItems.contains(item)) {
				if(!isVisible((CTreeItem) item)) return;
				if(visibleItems.isEmpty()) {
					visibleItems.add(item);
				} else {
					CTreeItem cti = ((CTreeItem) item).getItem(true);
					while(cti != null && !visibleItems.contains(cti = cti.getItem(true)));
					visibleItems.add(visibleItems.indexOf(cti)+1, item);
				}
				layout.layout(eventType, item);
				updatePaintedList = true;
			} else if(SWT.Hide == eventType && visibleItems.remove(item)) {
				layout.layout(eventType, item);
				updatePaintedList = true;
			}
		} else {
			super.layout(eventType, item);
		}
	}
	
	protected void paintGridLines(GC gc, Rectangle ebounds) {
		Rectangle r = getClientArea();

		if(linesVisible && (!nativeGrid || getGridLineWidth() > 0)) {
			gc.setForeground(getColors().getGrid());

			int y = getScrollPosition().y;
			int rowHeight = 0;
			
			if(win32 && nativeGrid) {
				if(visibleItems.isEmpty()) {
					if(emptyMessage.length() > 0) {
						Point tSize = gc.textExtent(emptyMessage);
						rowHeight = tSize.y+2;
					}
				} else {
					int gridline = getGridLineWidth();
					for(Iterator i = visibleItems.iterator(); i.hasNext(); ) {
						gc.drawLine(r.x, y, r.x+r.width, y);
						AbstractItem item = (AbstractItem) i.next();
						y += item.getSize().y + gridline;
					}
					rowHeight = ((CTreeItem) visibleItems.get(
							visibleItems.size() - 1)).getSize().y;
				}
			} 

			if(hLines) {
				int gridline = getGridLineWidth();
				while(y < r.height) {
					gc.drawLine(r.x, y, r.x+r.width, y);
					y += rowHeight + gridline;
				}
			}
			if(vLines) {
				if(getColumnWidths() != null && getColumnWidths().length > 1) {
					int x = -1;
					for(int i = 0; i < getColumnWidths().length; i++) {
						x += getColumnWidths()[i];
						gc.drawLine(
								x,
								r.y,
								x,
								r.y+r.height
						);
					}
				}
			}
		}
	}

	protected void paintItemBackgrounds(GC gc, Rectangle ebounds) {
		if(gtk && nativeGrid && !paintedItems.isEmpty()) {
			Rectangle r = getClientArea();
			int gridline = getGridLineWidth();
			int firstPaintedIndex = visibleItems.indexOf(paintedItems.get(0));
			for(int i = 0; i < paintedItems.size(); i++) {
				CTreeItem item = (CTreeItem) paintedItems.get(i);
				if(linesVisible && ((firstPaintedIndex + i + 1) % 2 == 0)) {
					gc.setBackground(getColors().getGrid());
					gc.fillRectangle(
							r.x,
							item.getBounds().y-ebounds.y,
							r.width,
							item.getSize().y+gridline
					);
					item.setIsGridLine(true);
				} else {
					item.setIsGridLine(false);
				}
			}
		}
	}

	protected void paintSelectionIndicators(GC gc, Rectangle ebounds) {
		if(win32 && !selection.isEmpty()) {
			for(Iterator i = selection.iterator(); i.hasNext(); ) {
				CTreeItem item = (CTreeItem) i.next();
				if(item.isVisible()) {
					Rectangle r = item.getBounds();
					r.x = 0;
					r.y -= getScrollPosition().y;
					r.width = getClientArea().width;
					r.height = item.getSize().y + getGridLineWidth();
					gc.setBackground(colors.getItemBackgroundSelected());
					gc.fillRectangle(r);
				}
			}
		}
	}
	
	protected void removeItem(AbstractItem item) {
		super.removeItem(item);
		if(!((CTreeItem) item).hasParentItem()) {
			roots.add(item);
		}
	}
	
	public void removeTreeListener(TreeListener listener) {
		checkWidget ();
		if(listener != null) {
			removeListener(SWT.Collapse, listener);
			removeListener(SWT.Expand, listener);
		}
	}

	/**
	 * If the the user clicks on the toggle of the treeCell the corresponding item will
	 * become selected if, and only if, selectOnTreeToggle is true
	 * @param select the new state of selectOnTreeToggle
	 */
	public void setSelectOnTreeToggle(boolean select) {
		selectOnToggle = select;
	}

	/**
	 * The Tree Column indicates which column should act as the tree by placing 
	 * the expansion toggle in its cell.
	 * <p>If column is greater than the number of columns, or is less than zero
	 * then no column will have an expansion toggle (or room for one).</p>
	 * @param column the column to use for the tree
	 */
	public void setTreeColumn(int column) {
		if(treeColumn != column) {
			treeColumn = column;
			if(!hasTreeColumn()) {
//				orderedItems = new ArrayList(items);
				visibleItems = new ArrayList(items);
				for(Iterator i = visibleItems.iterator(); i.hasNext(); ) {
					CTreeItem item = (CTreeItem) i.next();
					if(!item.getVisible()) {
						i.remove();
					} else {
						item.setTreeIndent(0);
					}
				}
			} else {
				visibleItems = new ArrayList(items);
				for(Iterator i = items.iterator(); i.hasNext(); ) {
					CTreeItem item = (CTreeItem) i.next();
					if(item.getParentItem() == null) {
						if(isVisible(item)) {
							visibleItems.add(item);
							visibleItems.addAll(getItems(item, false));
						}
					}
				}
			}
		}
	}

	/**
	 * Sets the amount to indent child items from their parent.
	 * <p>Suitable defaults are set according to SWT.Platform, but the option
	 * to customize is still exposed through this metho</p>
	 * <p>Note that only the Tree Column will be indented; if there is no Tree Column
	 * then this setting will have no affect.  If you need the entire Item and all of
	 * its columns to be indented, please file a feature request at 
	 * sourceforge.net/projects/calypsorcp</p>
	 * @param indent
	 */
	public void setTreeIndent(int indent) {
		treeIndent = indent;
	}

//	protected void addOrderedItem(AbstractItem item) {
//		CTreeItem cti = (CTreeItem) item;
//		if(!hasTreeColumn() || !cti.hasParentItem()) {
//			orderedItems.add(item);
//		} else {
//			CTreeItem parent = cti.getParentItem();
//			int ix = 0;//orderedItems.indexOf(parent);
//			if(parent.hasItems()) {
//				ix += parent.getItemCount();
//			}
//			orderedItems.add(ix, item);
//		}
//	}

	public void clear(int index, boolean all) {
		// TODO Auto-generated method stub
	}

	public void clearAll(boolean all) {
		// TODO Auto-generated method stub
	}

	public CTreeItem getItem(int index) {
		if(index >= 0 && index < roots.size()) {
			return (CTreeItem) roots.get(index);
		}
		return null;
	}

	public CTreeItem getParentItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public CTreeColumn getSortColumn() {
		return (CTreeColumn) internalGetSortColumn();
	}

//	public int getSortDirection() {
//		// TODO Auto-generated method stub
//		return 0;
//	}

	public int indexOf(CTreeColumn column) {
		return indexOf(column);
	}

	public int indexOf(CTreeItem item) {
		return roots.indexOf(item);
	}

	public void setColumnOrder(int[] order) {
		if(internalTable != null) internalTable.setColumnOrder(order);
	}

	public void setInsertMark(CTreeItem item, boolean before) {
		// TODO Auto-generated method stub
	}

	public void setItemCount(int count) {
		// TODO Auto-generated method stub
	}

	public void setSelection(CTreeItem item) {
		// TODO Auto-generated method stub
	}

	public void setSelection(CTreeItem[] items) {
		// TODO Auto-generated method stub
	}

//	public void setSortColumn(CTreeColumn column) {
//		
//	}

//	public void setSortDirection(int direction) {
//		// TODO Auto-generated method stub
//	}

	public void setTopItem(CTreeItem item) {
		setScrollPosition(
				new Point(getScrollPosition().x, item.getBounds().y - getContentArea().y));
	}

//	public void showColumn(CTreeColumn column) {
//		// TODO Auto-generated method stub
//	}

}
