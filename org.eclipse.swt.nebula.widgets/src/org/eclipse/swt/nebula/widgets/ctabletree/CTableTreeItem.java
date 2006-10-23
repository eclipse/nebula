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
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.nebula.widgets.ctabletree.ccontainer.CContainerCell;
import org.eclipse.swt.nebula.widgets.ctabletree.ccontainer.CContainerItem;


/**
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 */
public class CTableTreeItem extends CContainerItem {

	private int treeCell;
	private CTableTreeItem parentItem;
	private List items = new ArrayList();


	public CTableTreeItem(CTableTree container, int style) {
		this(container, style, -1);
	}
	public CTableTreeItem(CTableTree container, int style, Class cellClass) {
		this(container, style, -1, new Class[] { cellClass } );
	}
	public CTableTreeItem(CTableTree container, int style, Class[] cellClasses) {
		this(container, style, -1, cellClasses);
	}
	public CTableTreeItem(CTableTree container, int style, int index) {
		this(container, style, index, new Class[0]);
	}
	public CTableTreeItem(CTableTreeItem parent, int style) {
		this(parent, style, -1);
	}
	public CTableTreeItem(CTableTreeItem parent, int style, Class[] cellClasses) {
		super(parent.container, style, -1, cellClasses, new Object[] { parent } );
		if(hasTreeCell()) getTreeCell().setToggleVisible(false, true);
	}
	public CTableTreeItem(CTableTreeItem parent, int style, int index) {
		this(parent, style, index, new Class[0]);
	}
	public CTableTreeItem(CTableTreeItem parent, int style, int index, Class[] cellClasses) {
		super(parent.container, style, index, cellClasses, new Object[] { parent } );
		if(hasTreeCell()) getTreeCell().setToggleVisible(false, true);
	}
	public CTableTreeItem(CTableTree parent, int style, int index, Class[] cellClasses) {
		super(parent, style, index, cellClasses);
		if(hasTreeCell()) getTreeCell().setToggleVisible(false, true);
	}

	protected void initialize(Object[] params) {
		treeCell = ((CTableTree) container).getTreeColumn();
		if(params != null && params.length > 0) {
			parentItem = (CTableTreeItem) params[0];
			parentItem.addItem(this);
		}
	}

	void addItem(CTableTreeItem item) {
		if(!items.contains(item)) {
			items.add(item);
			if(!items.isEmpty() && hasTreeCell()) {
				getTreeCell().setToggleVisible(true, true);
			}
		}
	}

	void removeItem(CTableTreeItem item) {
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
		return getUnifiedBounds().contains(pt);
	}
	
	protected CContainerCell createCell(int column, int style) {
		return new CTableTreeCell(this, style);
	}
	
	public void dispose() {
		if((parentItem != null) && (!parentItem.isDisposed())) parentItem.removeItem(this);
		
		List l = new ArrayList(items);
		for(Iterator i = l.iterator(); i.hasNext(); ) {
			((CTableTreeItem) i.next()).dispose();
		}
		
		super.dispose();
	}
	
	public CTableTree getCTableTree() {
		return (CTableTree) container;
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
	
	public int getItemCount() {
		return items.size();
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
			return ((CTableTreeCell) cells[column]).getImage();
		}
		return null;
	}
	
	/**
	 * @param column the column from which to get the images
	 * @return the images from the given column
	 */
	public Image[] getImages(int column) {
		if(column >= 0 && column < cells.length) {
			return ((CTableTreeCell) cells[column]).getImages();
		}
		return new Image[0];
	}
	

	public CTableTreeItem getItem(int index) {
		if((index >= 0) && (index < items.size())) {
			return (CTableTreeItem) items.get(index);
		}
		return null;
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
			return ((CTableTreeCell) cells[column]).getText();
		}
		return null;
	}

	public CTableTreeCell getTreeCell() {
		return (CTableTreeCell) cells[treeCell];
	}
	
	public boolean hasTreeCell() {
		return (treeCell >= 0 && treeCell < cells.length);
	}
	
	public CTableTreeItem[] getItems() {
		if(items == null) return new CTableTreeItem[0];
		return (CTableTreeItem[]) items.toArray(new CTableTreeItem[items.size()]);
	}
	
	public CTableTreeItem getParentItem() {
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
			((CTableTreeCell) cells[column]).setImage(image);
		}
	}

	public void setImages(Image[] images) {
		CContainerCell[] cells = getCells();
		if(cells.length >= images.length) {
			for(int i = 0; i < images.length; i++) {
				((CTableTreeCell) cells[i]).setImage(images[i]);
			}
		}
	}

	public void setImages(int column, Image[] images) {
		if((column >= 0) && (column < cells.length)) {
			((CTableTreeCell) cells[column]).setImages(images);
		}
	}

	public void setText(int column, String string) {
		CContainerCell[] cells = getCells();
		if((column >= 0) && (column < cells.length)) {
			((CTableTreeCell) cells[column]).setText(string);
		}
	}
	
	public void setText(String string) {
		CContainerCell[] cells = getCells();
		if(cells.length > 0) {
			((CTableTreeCell) cells[0]).setText(string);
		}
	}
	
	public void setText(String[] strings) {
		CContainerCell[] cells = getCells();
		if(cells.length >= strings.length) {
			for(int i = 0; i < strings.length; i++) {
				((CTableTreeCell) cells[i]).setText(strings[i]);
			}
		}
	}
	
	public void setTreeIndent(int indent) {
		if(hasTreeCell()) {
			getTreeCell().setIndent(indent);
		}
	}	
}