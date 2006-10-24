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

package org.eclipse.swt.nebula.widgets.ctabletree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.nebula.widgets.ctabletree.ccontainer.CContainer;
import org.eclipse.swt.nebula.widgets.ctabletree.ccontainer.CContainerItem;
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
public class CTableTree extends CContainer {

	public static final int OP_TREE_COL			= 6;
	public static final int OP_TREE_COLLAPSE	= 7;
	public static final int OP_TREE_EXPAND 		= 8;

	private boolean selectOnTreeToggle = false;
	private int treeColumn = 0;
	private int treeIndent = 16;

	public CTableTree(Composite parent, int style) {
		super(parent, style);
//		setLinesVisible(true);
		setLayout(layout = new CTableTreeLayout(this));
	}

	protected void addItem(int index, CContainerItem item) {
		super.addItem(index, item);
		CTableTreeItem citem = (CTableTreeItem) item;
		if(citem.hasParentItem() && citem.getParentItem().getVisible()) {
			redraw(citem.getParentItem());
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

	public int getBottomIndex() {
		return visibleItems.indexOf(getBottomItem());
	}

	public CContainerItem getBottomItem() {
		int bot = getOrigin().y;
		bot += getClientArea().height;
		int itop = 0;
		int ibot = 0;
		for(Iterator i = visibleItems.iterator(); i.hasNext(); ) {
			CContainerItem item = (CContainerItem) i.next();
			Rectangle r = item.getUnifiedBounds();
			ibot = r.y+r.height;
			if(itop < bot && bot <= ibot) {
				return item;
			}
			itop = r.y+r.height;
		}
		return (CContainerItem) visibleItems.get(visibleItems.size()-1);
	}

	protected List getItems(CContainerItem item) {
		List l = new ArrayList();
		CTableTreeItem[] items = ((CTableTreeItem) item).getItems();
		for(int i = 0; i < items.length; i++) {
			l.add(items[i]);
//			if(treeColumn >= 0 && items[i].getExpanded()) {
				l.addAll(getItems(items[i]));
//			}
		}
		return l;
	}

	public int getItemCount() {
		int count = 0;
		for(Iterator i = visibleItems.iterator(); i.hasNext(); ) {
			if(!((CTableTreeItem) i.next()).hasParentItem()) {
				count++;
			}
		}
		return count;
	}

//	public CContainerItem getParentItem() {
//		return null;
//	}

	/**
	 * @see CTableTree#setSelectOnTreeToggle(boolean)
	 * @return
	 */
	public boolean getSelectOnTreeToggle() {
		return selectOnTreeToggle;
	}

	public int getTopIndex() {
		return visibleItems.indexOf(getTopItem());
	}

	public CContainerItem getTopItem() {
		int top = getOrigin().y;
		top += marginHeight;
		int itop = 0;
		int ibot = 0;
		for(Iterator i = visibleItems.iterator(); i.hasNext(); ) {
			CContainerItem item = (CContainerItem) i.next();
			Rectangle r = item.getUnifiedBounds();
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

	protected boolean handleMouseEvents(CContainerItem item, Event event) {
		if(item != null && item instanceof CTableTreeItem) {
			CTableTreeItem ti = (CTableTreeItem) item;
			Point pt = new Point(event.x, event.y);
			switch (event.type) {
			case SWT.MouseDoubleClick:
			case SWT.MouseDown:
				if(!selectOnTreeToggle && ti.isTreeTogglePoint(pt)) return false;
				break;
			case SWT.MouseUp:
				pt = new Point(event.x, event.y);
				if(item != null && item.isTogglePoint(pt)) {
					boolean open = item.isOpen(pt);
					boolean tree = ti.isTreeTogglePoint(pt);
					setDirtyFlag(DIRTY_VISIBLE);
					setOperation(open ? (tree ? OP_TREE_EXPAND : OP_CELL_EXPAND) : 
						(tree ? OP_TREE_COLLAPSE : OP_CELL_COLLAPSE));
					item.removeListener(open ? SWT.Expand : SWT.Collapse, this);
					item.setOpen(pt, !open);
					item.addListener(open ? SWT.Expand : SWT.Collapse, this);
					layout(item);
					fireTreeEvent(item, SWT.Collapse == event.type);
					if(tree) break;
				}
				break;
			}
			return true;
		}
		return false;
	}

	/**
	 * Convenience method indicating the layout of this CTableTree - Flat or Hierarchical (Tree)
	 * <p>Layout is determined by whether or not there is a Tree Column and is synonymous to 
	 * calling "getTreeColumn() < 0"</p>
	 * @return the layout of this CTableTree - true if Flat, false if Hierarchical
	 */
	public boolean isFlat() {
		return treeColumn < 0;
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
	public boolean isVisible(CTableTreeItem item) {
		CTableTreeItem parentItem = item.getParentItem();
		if(parentItem == null) return true;
		if(parentItem.getExpanded()) return isVisible(parentItem);
		return false;
	}

	protected void paintItemBackgrounds(GC gc, Rectangle ebounds) {
		if(gtk && nativeGrid) {
			Rectangle r = getBodyClientArea();
			int gridline = getGridLineWidth();

			for(int i = 0; i < paintedItems.size(); i++) {
				CTableTreeItem item = (CTableTreeItem) paintedItems.get(i);
				if(linesVisible && ((topIndex + i + 1) % 2 == 0)) {
					gc.setBackground(getColors().getGrid());
					gc.fillRectangle(
							r.x,
							item.getUnifiedBounds().y-ebounds.y,
							r.width,
							item.getFixedTitleHeight()+gridline
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
				CContainerItem item = (CContainerItem) i.next();
				if(item.isVisible()) {
					Rectangle r = item.getUnifiedBounds();
					r.x = 0;
					r.y -= getOrigin().y;
					r.width = getBodySize().x;
					r.height = item.fixedTitleHeight + getGridLineWidth();
					gc.setBackground(colors.getItemBackgroundSelected());
					gc.fillRectangle(r);
				}
			}
		}
	}
	
//	protected void paintGridLines(GC gc, Rectangle ebounds) {
//	}

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
	 * not yet implemented
	 * <p>post a feature request if you need it enabled</p>
	 */
	public void setTopIndex(int index) {
	}

	/**
	 * not yet implemented
	 * <p>post a feature request if you need it enabled</p>
	 */
	public void setTopItem(CContainerItem item) {
	}

	/**
	 * When used as a Tree, setting the Tree Column indicates which
	 * column should act as the tree by placing the expansion toggle in its cell.
	 * <p>If column is greater than the number of columns, then it is set to -1
	 * which  indicates that there is no Tree Column and this control is to be 
	 * used as a flat List or Table</p>
	 * <p>Note that the CTableTree must be empty while setting the tree column.</p>
	 * @param column the column to use for the tree or -1 for a flat layout
	 * @return true if the tree column was changed, false otherwise
	 */
	public boolean setTreeColumn(int column) {
		if(treeColumn != column && items.isEmpty()) {
			if(column < 0) column = -1;
			treeColumn = column;
			return true;
		}
		return false;
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

	protected void updateOrderedItems() {
		if(treeColumn < 0) {  // flat
			super.updateOrderedItems();
		} else { // hierarchical (tree)
			orderedItems = new ArrayList();
			for(Iterator i = items.iterator(); i.hasNext(); ) {
				CTableTreeItem item = (CTableTreeItem) i.next();
				if(item.getParentItem() == null) {
					orderedItems.add(item);
					orderedItems.addAll(getItems(item));
				}
			}
			dirtyFlags &= ~DIRTY_ORDERED;
		}
	}

	/**
	 * Sets the visibility and indentation of all items according to layout type
	 */
	protected void updateVisibleItems() {
		if(treeColumn < 0) {  // flat
			if(isOperation(OP_ADD)) {
				visibleItems.add(orderedItems.indexOf(dirtyItem), dirtyItem);
				((CTableTreeItem) dirtyItem).setTreeIndent(0);
			} else if(isOperation(OP_REMOVE)) {
				visibleItems.removeAll(Arrays.asList(removedItems));
			} else {
				visibleItems = new ArrayList(orderedItems);
				for(Iterator i = visibleItems.iterator(); i.hasNext(); ) {
					CTableTreeItem item = (CTableTreeItem) i.next();
					if(!item.getVisible()) {
						i.remove();
					} else {
						item.setTreeIndent(0);
					}
				}
			}
		} else { // hierarchical (tree)
			visibleItems = new ArrayList(orderedItems);
			for(Iterator i = visibleItems.iterator(); i.hasNext(); ) {
				CTableTreeItem item = (CTableTreeItem) i.next();
				if(!item.getVisible() || !isVisible(item)) {
					i.remove();
				} else {
					CTableTreeItem parent = item.getParentItem();
					if(parent == null) {
						item.setTreeIndent(0);
					} else {
						item.setTreeIndent(parent.getTreeIndent()+treeIndent);
					}
				}
			}
		}
		dirtyFlags &= ~DIRTY_VISIBLE;
	}
}
