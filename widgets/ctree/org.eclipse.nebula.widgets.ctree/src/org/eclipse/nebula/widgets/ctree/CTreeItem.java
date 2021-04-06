/****************************************************************************
* Copyright (c) 2005-2006 Jeremy Dowdall
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

package org.eclipse.nebula.widgets.ctree;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;


/**
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 */
public class CTreeItem extends Item {


	/**
	 * The container to which this item belongs.
	 */
	protected CTree ctree;
	/**
	 * The cells which belong to, or are contained by, this item.
	 */
	protected CTreeCell[] cells;
	/**
	 * Whether or not this item is enabled.
	 */
	protected boolean enabled = true;
	/**
	 * Whether or not this item is visible.
	 */
	protected boolean visible = true;
	/**
	 * Whether or not this item is actually painted to the screen.
	 */
	boolean painted = false;
	
	private int top = -1;
	private int height = -1;
	
	private int checkCell;
	private int treeCell;
	private CTreeItem parentItem;
	private List items = new ArrayList();
	private boolean autoHeight;

	private CTreeItem next;
	private CTreeItem previous;
	int computedHeight = -1;
	
	private CTreeItem(CTree parent, CTreeItem parentItem, int style, int index) {
		super(parent, style);
		this.ctree = parent;
		this.parentItem = parentItem;
		this.cells = new CTreeCell[parent.getColumnCount()];
		createCells(parent.cellClasses);
		this.treeCell = parent.getTreeColumn();
		if(parentItem != null) {
			this.checkCell = parent.getCheckColumn();
			this.parentItem = (CTreeItem) parentItem;
			((CTreeItem) parentItem).addItem(index, this);
		} else {
			this.checkCell = parent.getCheckRoots() ? parent.getCheckColumn() : -1;
			parent.addItem(index, this);
		}
	}
	
	public CTreeItem(CTree parent, int style) {
		this(parent, null, style, -1);
	}

	public CTreeItem(CTree parent, int style, int index) {
		this(parent, null, style, index);
	}
	
	public CTreeItem(CTreeItem parent, int style) {
		this(parent.ctree, parent, style, -1);
	}
	
	public CTreeItem(CTreeItem parent, int style, int index) {
		this(parent.ctree, parent, style, index);
	}

	void addItem(int index, CTreeItem item) {
		if(index < 0 || index > items.size()-1) {
			items.add(item);
		} else {
			items.add(index, item);
		}
		ctree.addedItems.add(item);
		redraw();
	}

	public void addListener(int eventType, Listener handler) {
		for(int i = 0; i < cells.length; i++) {
			cells[i].addListener(eventType, handler);
		}
	}
	
	protected void checkSubclass() {
	}
	
	/**
	 * Computes the size of each cell using the widthHint and heightHint with the same
	 * index as the cell.
	 * @return int the computed height
	 */
	public int computeHeight() {
		int[] widths = ctree.getColumnWidths();
		computedHeight = cells[0].computeSize(widths[0], -1).y;
		for(int i = 1; i < cells.length; i++) {
			computedHeight = Math.max(computedHeight, cells[i].computeSize(widths[i], -1).y);
		}
		return computedHeight;
	}
	
	boolean contains(Control control) {
		for(int i = 0; i < cells.length; i++) {
			if(cells[i].contains(control)) return true;
		}
		return false;
	}
	
	/**
	 * The Cells of a CTreeItem are considered contiguous and unified, therefore
	 * the contains method is overridden
	 */
	public boolean contains(Point pt) {
		return contains(pt.x,pt.y);
	}
	
	/**
	 * The Cells of a CTreeItem are considered contiguous and unified, therefore
	 * the contains method is overridden
	 */
	public boolean contains(int x, int y) {
		Rectangle[] ba = getCellBounds();
		for(int i = 0; i < ba.length; i++) {
			if(ba[i].contains(x,y)) return true;
		}
		return false;
	}

	/**
	 * Creates a cell of the default class, as determined by the implementation
	 * <p>Is used to auto-fill a cell when no specific cell class is provided for the 
	 * given column</p>
	 * @param index the index the new cell
	 * @return the new cell
	 */
	protected void createCell(int index, int style) {
		if(hasCell(index)) {
			cells[index] = new CTreeCell(this, style);
		}
	}
	
	
	
	public void createCell(int index, int style, Class clazz) {
		if(!hasCell(index)) return;
		
		Map memento = null;
		if(cells[index] != null) memento = cells[index].saveState();
		if(clazz != null) {
			boolean failed = true;
			try {
				// see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4301875
				Constructor[] constructors = clazz.getConstructors();
				for(int j = 0; j < constructors.length; j++) {
					Class[] params = constructors[j].getParameterTypes();
					if(params.length == 2 && 
							params[0].isInstance(this) && 
							params[1].equals(int.class)) {
						cells[index] = (CTreeCell) constructors[j].newInstance(
								new Object[] { this, new Integer(style) } );
						failed = false;
						break;
					}
				}
			} catch (Exception e) {
			}
			if(failed) SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		} else {
			createCell(index, style);
		}
		if(memento != null) cells[index].restoreState(memento);
	}

	protected void createCells(Object parent) {
		for(int i = 0; i < cells.length; i++) {
			if(ctree.cellClasses != null && i < ctree.cellClasses.length) {
				createCell(i, getCellStyle(i), ctree.cellClasses[i]);
			} else {
				createCell(i, getCellStyle(i));
			}
		}
	}
	
	public void dispose() {
		if((parentItem != null) && (!parentItem.isDisposed())) parentItem.removeItem(this);
		
		List l = new ArrayList(items);
		for(Iterator i = l.iterator(); i.hasNext(); ) {
			((CTreeItem) i.next()).dispose();
		}
		// TODO dispose listener
		for(int i = 0; i < cells.length; i++) {
			cells[i].dispose();
		}
		ctree.removeItem(this);
		super.dispose();
	}

	public boolean getAutoHeight() {
		return autoHeight;
	}
	
	public Color getBackground() {
		return cells[0].getBackground();
	}
	
	public Color getBackground(int index) {
		if(hasCell(index)) return cells[index].getBackground();
		return null;
	}
	
//	private CTreeCell[] createCells(Class[] cellClasses) {
//		final int cellStyle = getStyle();
//
//		if(!container.autoFillCells && (cellClasses == null || cellClasses.length == 0)) {
//			return new CTreeCell[] { createCell(0, cellStyle) };
//		} else {
//			if(cellClasses == null) cellClasses = new Class[0]; 
//			CTreeCell[] ca = new CTreeCell[
//	                                         container.autoFillCells ? 
//	                                        		 container.getColumnCount() :
//	                                        			 cellClasses.length];
//			for(int i = 0; i < ca.length; i++) {
//				CTreeCell cell = null;
//				if(i < cellClasses.length && cellClasses[i] != null) {
//					try {
//						// see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4301875
//						Constructor[] constructors = cellClasses[i].getConstructors();
//						for(int j = 0; j < constructors.length; j++) {
//							Class[] params = constructors[j].getParameterTypes();
//							if(params.length == 2 && 
//									params[0].isInstance(this) && 
//									params[1].equals(int.class)) {
//								cell = (CTreeCell) constructors[j].newInstance(
//										new Object[] { this, new Integer(cellStyle) } );
//								break;
//							}
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//				ca[i] = (cell != null) ? cell : createCell(i, cellStyle);
//			}
//			return ca;
//		}
//	}
	
//	/**
//	 * Provides a chance for subclasses to initialize themselves before the cells are created and the item
//	 * is added to its parent Container.
//	 * @param params the parameters
//	 */
//	protected abstract void initialize(Object[] params);
	
	int getBottom() {
		return top+height;
	}

	public Rectangle getBounds() {
		return new Rectangle(0,top,ctree.getClientArea().width,height);
	}
	
	public Rectangle getBounds(int index) {
		if(hasCell(index)) {
			CTreeColumn column = ctree.getColumn(index);
			return new Rectangle(column.getLeft(),top,column.getWidth(),height);
		}
		return null;
	}
	
	public CTreeCell getCell(int cell) {
		if(cell >= 0 && cell < cells.length) {
			return cells[cell];
		}
		return null;
	}
	
	public CTreeCell getCell(Point pt) {
		return getCell(pt.x,pt.y);
	}
	
	public CTreeCell getCell(int x, int y) {
		for(int i = 0; i < cells.length; i++) {
			if(cells[i].getBounds().contains(x,y)) {
				return cells[i];
			}
		}
		return null;
	}

	Rectangle[] getCellBounds() {
		Rectangle[] bounds = new Rectangle[cells.length];
		for(int i = 0; i < bounds.length; i++) {
			bounds[i] = cells[i].getBounds();
		}
		return bounds;
	}
	
	public CTreeColumn getCellColumn(CTreeCell cell) {
		return ctree.getColumn(getCellIndex(cell));
	}
	
	public int getCellIndex(CTreeCell cell) {
		return Arrays.asList(cells).indexOf(cell);
	}
	
	public CTreeCell[] getCells() {
		return cells;
	}
	
	Point[] getCellSizes() {
		Point[] sa = new Point[cells.length];
		for(int i = 0; i < sa.length; i++) {
			sa[i] = cells[i].getSize();
		}
		return sa;
	}

	protected int getCellStyle(int index) {
		if(hasCell(index)) {
			int style = ctree.getColumn(index).getStyle();
			return style;
		}
		return 0;
	}
	
	public CTreeCell getCheckCell() {
		return hasCheckCell() ? (CTreeCell) cells[checkCell] : null;
	}
	
	public CTree getContainer() {
		return ctree;
	}

	public CTree getCTree() {
		return (CTree) ctree;
	}

//	public Composite getChildArea(int column) {
//		if((column >= 0) && (column < cells.length)) {
//			return cells[column].getChildArea();
//		}
//		return null;
//	}

//	protected List getColorManagedControls() {
//		List list = new ArrayList();
//		for(int i = 0; i < cells.length; i++) {
//			list.addAll(cells[i].getColorManagedControls());
//		}
//		return list;
//	}

	public CTreeCell getCTreeCell(int column) {
		return (CTreeCell) getCell(column);
	}
	
//	protected List getEventManagedControls() {
//		if(enabled) {
//			List list = new ArrayList();
//			for(int i = 0; i < cells.length; i++) {
//				list.addAll(cells[i].getEventManagedControls());
//			}
//			return list;
//		} else {
//			return Collections.EMPTY_LIST;
//		}
//	}

	/**
	 * Returns the Tree Cell expansion state
	 * <p>If there is no Tree Cell, simply returns false</p>
	 * @return
	 * @see org.aspencloud.widgets.ccontainer#getExpanded(boolean)
	 */
	public boolean getExpanded() {
		return (hasTreeCell()) ? getTreeCell().isOpen() : false;
	}
	
	/**
	 * Get the font being used by the first cell
	 * @return Font
	 */
	public Font getFont() {
		return cells[0].getFont();
	}
	
//	public int getHeight() {
//		int height = cells[0].getBounds().height;
//		for(int i = 1; i < cells.length; i++) {
//			height = Math.max(height, cells[i].getBounds().height);
//		}
//		return height;
//	}
	
//	public Point[] getLocation() {
//		Point[] la = new Point[cells.length];
//		for(int i = 0; i < la.length; i++) {
//			la[i] = cells[i].getLocation();
//		}
//		return la;
//	}

	/**
	 * Get the font being used by the specified cell
	 * @param index an int used to specify the cell by an index
	 * @return Font
	 */
	public Font getFont(int index) {
		if(hasCell(index)) return cells[index].getFont();
		return null;
	}
	
//	public Composite getTitleArea(int column) {
//		if((column >= 0) && (column < cells.length)) {
//			return cells[column].getChildArea();
//		}
//		return null;
//	}

	public Color getForeground() {
		return cells[0].getForeground();
	}
	
//	ItemIterator iterator() {
//		return new ItemIterator(this);
//	}

	public Color getForeground(int index) {
		if(hasCell(index)) return cells[index].getForeground();
		return null;
	}
	
	int getHeight() {
		return height;
	}
	
	/**
	 * If this item has a tree column, this method will return the first image from that column
	 * @return the image from the tree column, null if neither exist
	 */
	public Image getImage() {
		if(hasTreeCell()) {
			return getTreeCell().getImage();
		}
		return null;
	}
	
	/**
	 * @param column the column from which to get the image
	 * @return the first image from the given column
	 */
	public Image getImage(int column) {
		if(column >= 0 && column < cells.length) {
			return ((CTreeCell) cells[column]).getImage();
		}
		return null;
	}
	
	/**
	 * @param column the column from which to get the images
	 * @return the images from the given column
	 */
	public Image[] getImages(int column) {
		if(column >= 0 && column < cells.length) {
			return ((CTreeCell) cells[column]).getImages();
		}
		return new Image[0];
	}
	
	CTreeItem getItem(boolean up) {
		if(hasParentItem()) {
			CTreeItem parent = getParentItem();
			int ix = parent.indexOf(this);
			if(up) {
				if(ix == 0) return parent;
				return parent.getItem(ix - 1);
			} else {
				if(ix > parent.getItemCount() - 1) return parent.getItem(false);
				return parent.getItem(ix + 1);
			}
		} else {
			CTree parent = getParent();
			int ix = parent.indexOf(this);
			if(up) {
				if(ix == 0) return null;
				return parent.getItem(ix - 1);
			} else {
				if(ix > parent.getItemCount() - 1) return null;
				return parent.getItem(ix + 1);
			}
		}
	}
	
	public CTreeItem getItem(int index) {
		if((index >= 0) && (index < items.size())) {
			return (CTreeItem) items.get(index);
		}
		return null;
	}
	
//	private int[] order;
//	private void updateCellOrder() {
//		int[] newOrder = container.getColumnOrder();
//		if(!Arrays.equals(order, newOrder)) {
//			order = newOrder;
//			for(int i = 0; i < order.length; i++) {
//				cells[order[i]].bounds.x = container.internalGetColumn(i).getLeft();
//			}
//		}
//	}
	
	public int getItemCount() {
		return items.size();
	}
	
	public CTreeItem[] getItems() {
		if(items == null) return new CTreeItem[0];
		return (CTreeItem[]) items.toArray(new CTreeItem[items.size()]);
	}

	public CTree getParent() {
		return getCTree();
	}
	
	public int getParentIndent() {
		return hasParentItem() ? parentItem.getTreeIndent() : 0;
	}
	
	public CTreeItem getParentItem() {
		return parentItem;
	}

	public Point getSize() {
		return new Point(ctree.getClientArea().width, height);
	}
	
	/**
	 * If this item has a tree column, this method will return the first image from that column
	 * @return the image from the tree column, null if neither exist
	 */
	public String getText() {
		if(hasTreeCell()) {
			return getTreeCell().getText();
		}
		return null;
	}
	
	/**
	 * @param column the column from which to get the image
	 * @return the first image from the given column
	 */
	public String getText(int column) {
		if(column >= 0 && column < cells.length) {
			return ((CTreeCell) cells[column]).getText();
		}
		return null;
	}
	
	int getTop() {
		return top;
	}
	
	public CTreeCell getTreeCell() {
		return hasTreeCell() ? (CTreeCell) cells[treeCell] : null;
	}
	
//	public void setLocation(int cell, Point location) {
//		if(cell >= 0 && cell < cells.length) {
//			cells[cell].setLocation(location);
//		}
//	}

//	public void setLocation(Point location) {
//		cells[0].setLocation(location);
//	}

//	public void setLocation(Point[] location) {
//		for(int i = 0; i < cells.length; i++) {
//			cells[i].setLocation(location[i]);
//		}
//	}
	
	public int getTreeIndent() {
		if(hasTreeCell()) {
			return getTreeCell().getIndent();
		}
		return 0;
	}

//	public Rectangle getTreeToggleBounds() {
//		if(hasTreeCell() && getTreeCell().getToggleVisible()) {
//			return getTreeCell().getToggleBounds();
//		}
//		return null;
//	}

	/**
	 * Returns whether or not this CContainerItem is requesting to be visible.
	 * In other words, if its internal visibility flag is set to true.
	 * There may exist other conditions which make this item actually not visible within
	 * its container.
	 * @return true if internally considered visible, false otherwise
	 * @see Control#getVisible()
	 * @see CTreeItem#isVisible()
	 */
	public boolean getVisible() {
		return visible;
	}

//	protected abstract int getFirstPaintedCellIndex();
//	protected abstract int getLastPaintedCellIndex();

	/**
	 * Give the Item a chance to handle the mouse event
	 * @param event the Event
	 * @return 0: do nothing, 1: redraw, 2: layout
	 */
	public int handleMouseEvent(Event event, boolean selectionActive) {
		int result = 0;
//		for(int i = 0; i < cells.length; i++) {
//			result |= cells[i].handleMouseEvent(event, selectionActive);
//		}
		return result;
	}
	
	boolean hasCell(int index) {
		return (index >= 0 && index < cells.length);
	}

	public boolean hasCheckCell() {
		return (checkCell >= 0 && checkCell < cells.length);
	}
	
	public boolean hasItems() {
		return !items.isEmpty();
	}
	
	boolean hasNext() { return next != null; }
	
	public boolean hasParentItem() {
		return parentItem != null;
	}
	boolean hasPrevious() { return previous != null; }

//	void addItem(CTreeItem item) {
//		addItem(-1, item);
//	}

	public boolean hasTreeCell() {
		return (treeCell >= 0 && treeCell < cells.length);
	}
	public int indexOf(CTreeItem item) {
		return items.indexOf(item);
	}

	/**
	 * This method returns true if ANY of the cells are open.
	 * @return
	 * @see org.aspencloud.widgets.ccontainer#getExpanded()
	 */
	public boolean isOpen() {
		boolean ex = false;
		for(int i = 0; i < cells.length; i++) {
			if(cells[i].isOpen()) {
				ex = true;
				break;
			}
		}
		return ex;
	}
	
	public boolean isOpen(int cell) {
		if(cell >= 0 && cell < cells.length) {
			return cells[cell].isOpen();
		}
		return false;
	}
	
	public boolean isOpen(int x, int y) {
		for(int i = 0; i < cells.length; i++) {
			if(cells[i].getBounds().contains(x,y)) return cells[i].isOpen();
		}
		return false;
	}

	public boolean isSelected() {
		for(int i = 0; i < cells.length; i++) {
			if(cells[i].isSelected()) return true;
		}
		return false;
	}
	
	public boolean isToggle(int x, int y) {
		for(int i = 0; i < cells.length; i++) {
			if(cells[i].isToggle(x, y)) return true;
		}
		return false;
	}
	
	public boolean isTreeToggle(int x, int y) {
		if(hasTreeCell()) {
			return getTreeCell().isToggle(x, y);
		}
		return false;
	}
	
	/**
	 * Returns true if the receiver is visible and all parents up to and including the 
	 * root of its container are visible. Otherwise, false is returned.
	 * @return true if visible, false otherwise
	 * @see CTreeItem#getVisible()
	 * @see Control#isVisible()
	 */
	public boolean isVisible() {
		return ((CTree) ctree).isVisible(this);
	}
	
	CTreeItem next() { return next; }

	CTreeItem nextVisible() {
		for(CTreeItem i = this; i.hasNext(); ) {
			CTreeItem item = i.next();
			if(item.getVisible()) return item;
		}
		return null;
	}

//	protected int getFirstPaintedCellIndex() {
//		return container.getFirstPaintedColumnIndex();
//	}
	
	public void paint(GC gc, Rectangle ebounds) {
//		updateCellOrder();
		updatePaintedCells();
		for(int i = 0; i < cells.length; i++) {
			cells[i].paint(gc, ebounds);
		}
	}
	
	CTreeItem previous() { return previous; }

//	int getIndex() {
//		if(hasParentItem()) return getParentItem().indexOf(this);
//		return getParent().indexOf(this);
//	}
	
	CTreeItem previousVisible() {
		for(CTreeItem i = this; i.hasPrevious(); ) {
			CTreeItem item = i.previous();
			if(item.getVisible()) return item;
		}
		return null;
	}
	
	public void redraw() {
		if(painted) ctree.redraw(this);
	}
	
	void removeItem(CTreeItem item) {
		items.remove(item);
		if(!ctree.removedItems.contains(item)) {
			ctree.removedItems.add(item);
			boolean selChange = ctree.selection.remove(item);
			if(selChange) ctree.fireSelectionEvent(false);
			redraw();
		}
		if(items.isEmpty() && hasTreeCell()) {
			getTreeCell().setToggleVisible(false);
		}
	}

	public void removeListener(int eventType, Listener handler) {
		for(int i = 0; i < cells.length; i++) {
			cells[i].removeListener(eventType, handler);
		}
	}
	
	public void setBackground(Color color) {
		for(int i = 0; i < cells.length; i++) {
			cells[i].setBackground(color);
		}
	}

//	protected int getLastPaintedCellIndex() {
//		return container.getLastPaintedColumnIndex();
//	}
	
	public void setBackground(int index, Color color) {
		if(hasCell(index)) cells[index].setBackground(color);
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Sets the Tree Expansion state of the Item
	 * <p>Does not affect the expansion state of individual expandable cells</p>
	 * @param expanded
	 */
	public void setExpanded(boolean expanded) {
		if(hasTreeCell() && getTreeCell().isOpen() != expanded) {
			getTreeCell().setOpen(expanded);
			layout(expanded ? SWT.Expand : SWT.Collapse);
		}
	}
	
	public void layout(int eventType) {
		ctree.layout(eventType, this);
	}
	
	public boolean setFocus() {
		for(int i = 0; i < cells.length; i++) {
			if(cells[i].setFocus()) return true;
		}
		return false;
	}

	public void setFont(Font font) {
		for(int i = 0; i < cells.length; i++) {
			cells[i].setFont(font);
		}
	}
	
	public void setFont(int index, Font font) {
		if(hasCell(index)) cells[index].setFont(font);
	}
	
	public void setForeground(Color color) {
		for(int i = 0; i < cells.length; i++) {
			cells[i].setForeground(color);
		}
	}
	
	public void setForeground(int index, Color color) {
		if(hasCell(index)) cells[index].setForeground(color);
	}

	public void setGridLine(boolean gridLine) {
		for(int i = 0; i < cells.length; i++) {
			((CTreeCell) cells[i]).setGridLine(gridLine);
		}
	}
	
	void setHeight(int height) {
		this.height = height;
		redraw();
	}
	
	public void setImage(Image image) {
		if(hasTreeCell()) {
			getTreeCell().setImage(image);
		}
	}

	public void setImage(int column, Image image) {
		if((column >= 0) && (column < cells.length)) {
			((CTreeCell) cells[column]).setImage(image);
		}
	}
	
	public void setImages(Image[] images) {
		CTreeCell[] cells = getCells();
		if(cells.length >= images.length) {
			for(int i = 0; i < images.length; i++) {
				((CTreeCell) cells[i]).setImage(images[i]);
			}
		}
	}

	public void setImages(int column, Image[] images) {
		if((column >= 0) && (column < cells.length)) {
			((CTreeCell) cells[column]).setImages(images);
		}
	}
	
	void setNext(CTreeItem item) { next = item; }

	public void setOpen(boolean open) {
		for(int i = 0; i < cells.length; i++) {
			cells[i].setOpen(open);
		}
	}
	
	public void setOpen(int cell, boolean open) {
		if(cell >= 0 && cell < cells.length) {
			cells[cell].setOpen(open);
		}
	}

	public void setOpen(int x, int y, boolean open) {
		for(int i = 0; i < cells.length; i++) {
			if(cells[i].getBounds().contains(x,y)) {
				cells[i].setOpen(open);
				break;
			}
		}
	}
	
	void setPainted(boolean painted) {
		if(this.painted != painted) {
			this.painted = painted;
			if(painted) {
				updatePaintedCells();
			} else {
				for(int i = 0; i < cells.length; i++) {
					cells[i].setPainted(false);
				}
			}
			ctree.firePaintedItemEvent(this, painted);
		}
	}

	void setPrevious(CTreeItem item) {
		previous = item;
	}
	
	public void setSelected(boolean selected) {
		if(enabled) {
			for(int i = 0; i < cells.length; i++) {
				cells[i].setSelected(selected);
			}
		}
	}

	public void setText(int column, String string) {
		CTreeCell[] cells = getCells();
		if((column >= 0) && (column < cells.length)) {
			((CTreeCell) cells[column]).setText(string);
		}
	}

	public void setText(String string) {
		CTreeCell[] cells = getCells();
		if(cells.length > 0) {
			((CTreeCell) cells[0]).setText(string);
		}
	}
	
	public void setText(String[] strings) {
		CTreeCell[] cells = getCells();
		if(cells.length >= strings.length) {
			for(int i = 0; i < strings.length; i++) {
				((CTreeCell) cells[i]).setText(strings[i]);
			}
		}
	}

	void setTop(int top) {
		this.top = top;
	}

	public void setTreeIndent(int indent) {
		if(hasTreeCell()) {
			getTreeCell().setIndent(indent);
		}
	}

	public void setVisible(boolean visible) {
		if(this.visible != visible) {
			this.visible = visible;
			for(int i = 0; i < cells.length; i++) {
				cells[i].setVisible(visible);
			}
			ctree.layout(visible ? SWT.Show : SWT.Hide, this);
		}
	}
	
	public void update() {
		for(int i = 0; i < cells.length; i++) {
			cells[i].update();
		}
	}
	
	protected void updateColors() {
		for(int i = 0; i < cells.length; i++) {
			cells[i].updateColors();
		}
	}
	
	private void updatePaintedCells() {
		for(int i = 0; i < cells.length; i++) {
			cells[i].setPainted(ctree.getColumn(i).isVisible());
		}
	}	
}