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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.swt.widgets.Widget;


/**
 */
public class CTableTree extends CContainer {

	public static final int OP_TREE_COL			= 6;
	public static final int OP_TREE_COLLAPSE		= 7;
	public static final int OP_TREE_EXPAND 		= 8;

	private boolean selectOnTreeToggle = false;
	private int treeColumn = -1;
	private int treeIndent = 16;

	public CTableTree(Composite parent, int style) {
		super(parent, style);
		setLinesVisible(true);
		setLayout(layout = new CTableTreeLayout(this));
	}

	protected void addItem(int index, CContainerItem item) {
		super.addItem(index, item);
		CTableTreeItem citem = (CTableTreeItem) item;
		if(citem.hasParentItem() && citem.getParentItem().getVisible()) {
			paint(citem.getParentItem());
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
		if(!visibleItems.isEmpty()) {
			Point pt = getOrigin();
			pt.x += marginWidth;
			pt.y += getClientArea().height;
			for(Iterator i = visibleItems.iterator(); i.hasNext(); ) {
				CContainerItem item = (CContainerItem) i.next();
				if(item.contains(pt)) return item;
			}
			return (CContainerItem) visibleItems.get(visibleItems.size()-1);
		}
		return null;
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


	public CContainerItem getParentItem() {
		return null;
	}

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
		Point pt = getOrigin();
		pt.x += marginWidth;
		pt.y += marginHeight;
		for(Iterator i = visibleItems.iterator(); i.hasNext(); ) {
			CContainerItem item = (CContainerItem) i.next();
			if(item.contains(pt)) return item;
		}
		return null;
	}

	public int getTreeColumn() {
		return treeColumn;
	}

	public int getTreeIndent() {
		return treeIndent;
	}

	protected void handleMouseEvents(Event event) {
		CTableTreeItem item;
		switch (event.type) {
		case SWT.MouseDoubleClick:
			if(!selectOnTreeToggle || !selectOnToggle) {
				Point pt = new Point(event.x, event.y);
				item = (CTableTreeItem) getItem(pt);
				if(item != null) {
					if(!selectOnTreeToggle && item.isTreeTogglePoint(pt)) break;
					if(!selectOnToggle && item.isTogglePoint(pt)) break;
				}
			}
			fireSelectionEvent(true);
			break;
		case SWT.MouseDown:
			if(!hasFocus) setFocus();
			Point pt = new Point(event.x, event.y);
//			if(event.widget != body) {
//				if(event.widget instanceof Control) {
//					pt = getDisplay().map((Control) event.widget, body, pt);
//				} else {
//					break;
//				}
//			}
			item = (CTableTreeItem) getItem(pt);
			if(item == null) {
				if((event.stateMask & (SWT.CTRL | SWT.SHIFT)) == 0) {
					clearSelection();
				}
				break;
			}
//			if(!body.isFocusControl()) {
//				setFocus();
//			}
			switch (event.button) {
			// TODO - popup menu: not just for mouse down events!
			case 3:
				Menu menu = getMenu();
				if((menu != null) && ((menu.getStyle() & SWT.POP_UP) != 0)) {
					menu.setVisible(true);
				}
			case 1:
				if(!selectOnTreeToggle && item.isTreeTogglePoint(pt)) break;
				if(!selectOnToggle && item.isTogglePoint(pt)) break;
				if((event.stateMask & SWT.SHIFT) != 0) {
					if(shiftSel == null) {
						if(selection.isEmpty()) selection.add(item);
						shiftSel = (CContainerItem) selection.get(selection.size() - 1);
					}
					int start = items.indexOf(shiftSel);
					int end = items.indexOf(item);
					setSelection(start, end);
				} else if((event.stateMask & SWT.CONTROL) != 0) {
					toggleSelection(item);
					shiftSel = null;
				} else {
					setSelection(item);
					shiftSel = null;
				}
				break;
			}
			break;
		case SWT.MouseMove:
			break;
		case SWT.MouseUp:
			pt = new Point(event.x, event.y);
			item = (CTableTreeItem) getItem(pt);
			if(item == null) break;
			if(item.isTogglePoint(pt)) {
				boolean open = item.isOpen(pt);
				boolean tree = item.isTreeTogglePoint(pt);
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
							item.getUnifiedBounds().y-getOrigin().y,
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
					r.width = getBodySize().x;
					r.height += getGridLineWidth();
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
	 * <p>Note that the CTableTree must be empty while setting the tree column, and the
	 * column must already exist.</p>
	 * @param column the column to use for the tree or -1 for a flat layout
	 * @return true if the tree column was changed, false otherwise
	 */
	public boolean setTreeColumn(int column) {
		if(treeColumn != column && items.isEmpty()) {
			if(column < -1 || column >= getColumnCount()) column = -1;
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
