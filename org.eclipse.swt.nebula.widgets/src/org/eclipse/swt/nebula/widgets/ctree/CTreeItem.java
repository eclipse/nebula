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

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;


/**
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 */
public class CTreeItem extends AbstractItem {

	private int treeCell;
	private CTreeItem parentItem;
	private List items = new ArrayList();


	public CTreeItem(CTree container, int style) {
		this(container, style, -1);
	}
	public CTreeItem(CTree container, int style, Class cellClass) {
		this(container, style, -1, new Class[] { cellClass } );
	}
	public CTreeItem(CTree container, int style, Class[] cellClasses) {
		this(container, style, -1, cellClasses);
	}
	public CTreeItem(CTree container, int style, int index) {
		this(container, style, index, null);
	}
	public CTreeItem(CTreeItem parent, int style) {
		this(parent, style, -1);
	}
	public CTreeItem(CTreeItem parent, int style, Class[] cellClasses) {
		super(parent.container, style, -1, cellClasses, new Object[] { parent } );
		if(hasTreeCell()) getTreeCell().setToggleVisible(false, true);
	}
	public CTreeItem(CTreeItem parent, int style, int index) {
		this(parent, style, index, null);
	}
	public CTreeItem(CTreeItem parent, int style, int index, Class[] cellClasses) {
		super(parent.container, style, index, cellClasses, new Object[] { parent } );
		if(hasTreeCell()) getTreeCell().setToggleVisible(false, true);
	}
	public CTreeItem(CTree parent, int style, int index, Class[] cellClasses) {
		super(parent, style, index, cellClasses);
		if(hasTreeCell()) getTreeCell().setToggleVisible(false, true);
	}

	protected void initialize(Object[] params) {
		treeCell = ((CTree) container).getTreeColumn();
		if(params != null && params.length > 0) {
			parentItem = (CTreeItem) params[0];
			parentItem.addItem(this);
		}
	}

	void addItem(CTreeItem item) {
		if(!items.contains(item)) {
			items.add(item);
			if(!items.isEmpty() && hasTreeCell()) {
				getTreeCell().setToggleVisible(true, true);
			}
		}
	}

	void removeItem(CTreeItem item) {
		items.remove(item);
		if(items.isEmpty() && hasTreeCell()) {
			getTreeCell().setToggleVisible(false);
		}
	}
	
	/**
	 * The Cells of a CTableTreeItem are considered contiguous and unified, therefore
	 * the contains method is overridden
	 */
	public boolean contains(Point pt) {
		return getBounds().contains(pt);
	}
	
	protected AbstractCell createCell(int column, int style) {
		return new CTreeCell(this, style);
	}
	
	public void dispose() {
		if((parentItem != null) && (!parentItem.isDisposed())) parentItem.removeItem(this);
		
		List l = new ArrayList(items);
		for(Iterator i = l.iterator(); i.hasNext(); ) {
			((CTreeItem) i.next()).dispose();
		}
		
		super.dispose();
	}

	public CTree getCTree() {
		return (CTree) container;
	}
	
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
	

	public int getItemCount() {
		return items.size();
	}
	
	public CTreeItem[] getItems() {
		if(items == null) return new CTreeItem[0];
		return (CTreeItem[]) items.toArray(new CTreeItem[items.size()]);
	}

	public CTreeItem getItem(int index) {
		if((index >= 0) && (index < items.size())) {
			return (CTreeItem) items.get(index);
		}
		return null;
	}
	
	public int getIndexOf(CTreeItem item) {
		return items.indexOf(item);
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

	public CTreeCell getTreeCell() {
		return hasTreeCell() ? (CTreeCell) cells[treeCell] : null;
	}
	
	public boolean hasTreeCell() {
		return (treeCell >= 0 && treeCell < cells.length);
	}
	
	public CTree getParent() {
		return getCTree();
	}
	
	public CTreeItem getParentItem() {
		return parentItem;
	}
	
	public int getTreeIndent() {
		if(hasTreeCell()) {
			return getTreeCell().getIndent();
		}
		return 0;
	}

	public boolean hasItems() {
		return !items.isEmpty();
	}
	
	public boolean hasParentItem() {
		return parentItem != null;
	}

	public Rectangle getTreeToggleBounds() {
		if(hasTreeCell() && getTreeCell().getToggleVisible()) {
			return getTreeCell().getToggleBounds();
		}
		return null;
	}
	
	public boolean isTreeTogglePoint(Point pt) {
		Rectangle r = getTreeToggleBounds();
		if(r != null) {
			return r.contains(pt);
		}
		return false;
	}

	/**
	 * Sets the Tree Expansion state of the Item
	 * <p>Does not affect the expansion state of individual expandable cells</p>
	 * @param expanded
	 */
	public void setExpanded(boolean expanded) {
		if(hasTreeCell() && getTreeCell().isOpen() != expanded) {
			cells[treeCell].setOpen(expanded);
		}
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
		AbstractCell[] cells = getCells();
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

	public void setText(int column, String string) {
		AbstractCell[] cells = getCells();
		if((column >= 0) && (column < cells.length)) {
			((CTreeCell) cells[column]).setText(string);
		}
	}
	
	public void setText(String string) {
		AbstractCell[] cells = getCells();
		if(cells.length > 0) {
			((CTreeCell) cells[0]).setText(string);
		}
	}
	
	public void setText(String[] strings) {
		AbstractCell[] cells = getCells();
		if(cells.length >= strings.length) {
			for(int i = 0; i < strings.length; i++) {
				((CTreeCell) cells[i]).setText(strings[i]);
			}
		}
	}
	
	public void setTreeIndent(int indent) {
		if(hasTreeCell()) {
			getTreeCell().setIndent(indent);
		}
	}	
}