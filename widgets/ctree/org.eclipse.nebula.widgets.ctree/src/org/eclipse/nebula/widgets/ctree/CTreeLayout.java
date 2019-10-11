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

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

/**
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 */
class CTreeLayout extends Layout {


	/**
	 * true if the platform is detected as being "carbon"
	 */
	public static final boolean carbon = "carbon".equals(SWT.getPlatform());
	/**
	 * true if the platform is detected as being "gtk"
	 */
	public static final boolean gtk = "gtk".equals(SWT.getPlatform());
	/**
	 * true if the platform is detected as being "win32"
	 */
	public static final boolean win32 = "win32".equals(SWT.getPlatform());

	/**
	 * the CTree
	 */
	protected CTree ctree;

	/**
	 * the size of the container's header composite
	 */
	protected Point headerSize = new Point(0,0);
	/**
	 * the height of the container contents
	 */
	protected int 	contentHeight = 0;
	
	private Point 	size = new Point(0,0);

	int itemHeight = CTree.gtk ? 25 : 20;
	private boolean itemHeightSet = false;
	private boolean fixedHeight = false;
	
	/**
	 * @param ctree
	 */
	public CTreeLayout(CTree ctree) {
		this.ctree = ctree;
	}
	

	/**
	 * Compute the height of the Content area without consideration for cached values.
	 */
	protected void computeContentHeight() {
		if(ctree.isEmpty()) {
			contentHeight = 0;
		} else {
			int gridLine = ctree.getGridLineWidth();
			List vitems = ctree.items(false);
			List pitems = ctree.getPaintedItems();
			contentHeight = (vitems.size() - pitems.size()) * getItemHeight();
			for(Iterator i = pitems.iterator(); i.hasNext(); ) {
				contentHeight += ((CTreeItem) i.next()).computeHeight();
				contentHeight += gridLine;
			}
			contentHeight -= gridLine;
		}
	}

	/**
	 * Compute the height of the Content area after the given cell has experienced an event of
	 * the given type.
	 * <p>
	 *  Event types:
	 *  <ul>
	 *   <li>SWT.Collapse</li>
	 *   <li>SWT.Expand</li>
	 *   <li>SWT.Resize</li>
	 *  </ul>
	 * </p>
	 * Note that subclasses do not have to handle all types and may also implement more of necessary.
	 * @param eventType
	 * @param cell
	 */
	protected void computeContentHeight(int eventType, CTreeCell cell) {
		computeContentHeight();
	}
	
	/**
	 * Compute the height of the Content area after the given column has experienced an event of
	 * the given type.
	 * <p>
	 *  Event types:
	 *  <ul>
	 *   <li>SWT.Move</li>
	 *   <li>SWT.Resize</li>
	 *  </ul>
	 * </p>
	 * Note that subclasses do not have to handle all types and may also implement more of necessary.
	 * @param eventType
	 * @param column
	 */
	protected void computeContentHeight(int eventType, CTreeColumn column) {
		computeContentHeight();
	}

	/**
	 * Compute the height of the Content area after the given item has experienced an event of
	 * the given type.
	 * <p>
	 *  Event types:
	 *  <ul>
	 *   <li>SWT.Hide</li>
	 *   <li>SWT.Move</li>
	 *   <li>SWT.Show</li>
	 *  </ul>
	 * </p>
	 * Note that subclasses do not have to handle all types and may also implement more of necessary.
	 * @param eventType
	 * @param item
	 */
	protected void computeContentHeight(int eventType, CTreeItem item) {
		computeContentHeight();
	}
	
	private void computeHeaderSize() {
		headerSize.x = headerSize.y = 0;
		int gridLine = ctree.getGridLineWidth();
		CTreeColumn[] columns = ctree.getColumns();
		for(int i = 0; i < columns.length; i++) {
			if(!columns[i].getAutoWidth()) {
				headerSize.x += columns[i].getWidth();
			}
			headerSize.x += gridLine;
		}
		headerSize.y = ctree.getHeaderHeight();
	}

	protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
		computeHeaderSize();
		computeContentHeight();

		size.x = headerSize.x;
		size.y = headerSize.y + contentHeight;

		Point returnSize = new Point(size.x, size.y);
		
		if(ctree.hBar != null && !ctree.hBar.isDisposed() && !ctree.hBar.getVisible()) {
			returnSize.y -= (ctree.hBar.getSize().y + 3);
		}
		if(ctree.vBar != null && !ctree.vBar.isDisposed() && !ctree.vBar.getVisible()) {
			returnSize.x -= (ctree.vBar.getSize().x + 3);
		}
		
		return returnSize;
	}

	private void computeSize(int eventType, CTreeCell cell) {
		computeContentHeight(eventType, cell);
		size.x = headerSize.x;
		size.y = headerSize.y + contentHeight;
	}

	private void computeSize(int eventType, CTreeColumn column) {
		if(SWT.Resize == eventType) {
			computeHeaderSize();
			computeContentHeight(eventType, column);
			size.x = headerSize.x;
			size.y = headerSize.y + contentHeight;
		}
	}
	
	private void computeSize(int eventType, CTreeItem item) {
		if(SWT.Move != eventType) {
			computeContentHeight(eventType, item);
			size.x = headerSize.x;
			size.y = headerSize.y + contentHeight;
		}
	}

	int getItemHeight() {
		if(itemHeightSet) return itemHeight;
		if(ctree.isEmpty()) return itemHeight;
		itemHeight = ((CTreeItem) ctree.itemList.get(0)).computeHeight();
		itemHeightSet = true;
		return itemHeight;
	}
	
	protected void layout(Composite composite, boolean flushCache) {
		Rectangle area = composite.getClientArea();
		if(area.isEmpty()) return;
		
		if(flushCache) computeSize(composite, -1, -1, flushCache);

		ctree.getHeader().setBounds(
				area.x,
				area.y,
				area.width,
				headerSize.y
			);

		Point origin = ctree.getScrollPosition();
		int height = area.height-headerSize.y;
		ctree.body.setBounds(
				area.x-origin.x,
				area.y+headerSize.y-origin.y,
				(gtk ? Math.max(area.width, headerSize.x) : area.width),
				(gtk ? Math.max(height, contentHeight) : height)
			);
		
		layoutHeader();
		
		layoutContent();
		
		updateScrollBars();
	}

	/**
	 * Layout the Content area after the given cell has experienced an event of the given type.
	 * <p>
	 *  Event types:
	 *  <ul>
	 *   <li>SWT.Collapse</li>
	 *   <li>SWT.Expand</li>
	 *   <li>SWT.Resize</li>
	 *  </ul>
	 * </p>
	 * Note that subclasses do not have to handle all types and may also implement more of necessary.
	 * @param eventType
	 * @param cell
	 */
	void layout(int eventType, CTreeCell cell) {
		computeSize(eventType, cell);
		layoutContent(eventType, cell);
		updateScrollBars();
	}

	/**
	 * Layout the Content area after the given column has experienced an event of the given type.
	 * <p>
	 *  Event types:
	 *  <ul>
	 *   <li>SWT.Move</li>
	 *   <li>SWT.Resize</li>
	 *  </ul>
	 * </p>
	 * Note that subclasses do not have to handle all types and may also implement more of necessary.
	 * @param eventType
	 * @param column
	 */
	void layout(int eventType, CTreeColumn column) {
		if(SWT.Resize == eventType) {
			computeSize(eventType, column);
			if(gtk) {
				ctree.body.setSize(
					Math.max(ctree.getClientArea().width, headerSize.x),
					ctree.body.getSize().y
				);
			}
			layoutContent(eventType, column);
			updateScrollBars();
		}
	}

	/**
	 * Layout the Content area after the given item has experienced an event of the given type.
	 * Because an event has occurred, indicating a change in content, computeSize will be called.
	 * <p>
	 *  Event types:
	 *  <ul>
	 *   <li>SWT.Collapse</li>
	 *   <li>SWT.Expand</li>
	 *   <li>SWT.Hide</li>
	 *   <li>SWT.Move</li>
	 *   <li>SWT.Show</li>
	 *  </ul>
	 * </p>
	 * Note that subclasses do not have to handle all types and may also implement more of necessary.
	 * @param eventType
	 * @param item
	 */
	void layout(int eventType, CTreeItem item) {
		if(SWT.Move != eventType) {
			computeSize(eventType, item);
		}
		layoutContent(eventType, item);
		if(SWT.Move != eventType) {
			updateScrollBars();
		}
	}

	/**
	 * Compute the height of the Content area without consideration for cached values.
	 */
	protected void layoutContent() {
		int gridLine = ctree.getGridLineWidth();
		int top = 0;
		for(Iterator i = ctree.items(false).iterator(); i.hasNext(); ) {
			CTreeItem item = (CTreeItem) i.next();
			item.setTop(top);
			item.setHeight(fixedHeight ? getItemHeight() : item.computeHeight());
			top += item.getHeight() + gridLine;
		}
		ctree.body.redraw();
	}

	/**
	 * Layout the Content area after the given cell has experienced an event of the given type.
	 * <p>
	 *  Event types:
	 *  <ul>
	 *   <li>SWT.Collapse</li>
	 *   <li>SWT.Expand</li>
	 *   <li>SWT.Resize</li>
	 *  </ul>
	 * </p>
	 * Note that subclasses do not have to handle all types and may also implement more of necessary.
	 * @param eventType
	 * @param cell
	 */
	protected void layoutContent(int eventType, CTreeCell cell) {
		layoutContent();
	}

	/**
	 * Layout the Content area after the given column has experienced an event of the given type.
	 * <p>
	 *  Event types:
	 *  <ul>
	 *   <li>SWT.Move</li>
	 *   <li>SWT.Resize</li>
	 *  </ul>
	 * </p>
	 * Note that subclasses do not have to handle all types and may also implement more of necessary.
	 * @param eventType
	 * @param column
	 */
	protected void layoutContent(int eventType, CTreeColumn column) {
		layoutContent();
	}
	
	/**
	 * Layout the Content area after the given item has experienced an event of the given type.
	 * This method is called after any necessary call to computeSize.
	 * <p>
	 *  Event types:
	 *  <ul>
	 *   <li>SWT.Hide</li>
	 *   <li>SWT.Move</li>
	 *   <li>SWT.Show</li>
	 *  </ul>
	 * </p>
	 * Note that subclasses do not have to handle all types and may also implement more of necessary.
	 * @param eventType
	 * @param item
	 */
	protected void layoutContent(int eventType, CTreeItem item) {
		layoutContent();
	}
	
	/**
	 * layout the header.  will recalcuate and resize any autowidth columns if necessary
	 */
	protected void layoutHeader() {
		if(ctree.nativeHeader) {
			Rectangle tBounds = ctree.getHeader().getClientArea();
			tBounds.height += 30; // TODO: does this value matter, as long as it's > the scrollbar size?
			// pogramatic scrolling doesn't work on win32
			if(CTree.win32 && ctree.hBar != null) {
				int sel = ctree.hBar.getSelection();
				tBounds.x = -sel;
				tBounds.width += sel;
			}
			ctree.internalTable.setBounds(tBounds);
		}
		
		int num_fillers = 0;
		CTreeColumn[] columns = ctree.getColumns();
		for(int i = 0; i < columns.length; i++) {
			if(columns[i].getAutoWidth()) {
				num_fillers++;
			}
		}
		
		if(num_fillers > 0) {
			int[] fillers = new int[num_fillers];

			int xconsumed = 0;
			for(int i = 0, j = 0; i < columns.length && j < num_fillers; i++) {
				if(columns[i].getAutoWidth()) {
					fillers[j++] = i;
				} else {
					xconsumed += columns[i].getWidth();
				}
			}

			int headerWidth = ctree.getHeader().getSize().x;
			int fillerWidth0 = (headerWidth - xconsumed) / num_fillers;
			int fillerWidth1 = headerWidth - xconsumed - (fillerWidth0 * (num_fillers - 1));
			if(fillerWidth0 == 0) fillerWidth0 = 1;
			if(fillerWidth1 == 0) fillerWidth1 = 1;

			for(int i = 0; i < num_fillers; i++) {
				if(i == num_fillers-1) {
					columns[fillers[i]].setWidth(fillerWidth1);
				} else {
					columns[fillers[i]].setWidth(fillerWidth0);
				}
			}
		}
	}

	void setItemHeight(int height) {
		itemHeight = height;
		itemHeightSet = true;
	}

	/**
	 * Recalculate the correct state of the scrollbars and set their visibility accordingly.
	 */
	private void updateScrollBars() {
		Rectangle area = ctree.getClientArea();
		if(area.isEmpty()) return;
		
		if(ctree.hBar != null) {
			if(area.width < size.x) {
				ctree.hBar.setMaximum(size.x);
				ctree.hBar.setThumb(area.width);
				if(!ctree.hBar.getVisible()) {
					ctree.hBar.setVisible(true);
				}
			} else {
				ctree.hBar.setMaximum(1);
				if(ctree.hBar.getVisible()) {
					ctree.hBar.setVisible(false);
				}
			}
		}

		if(ctree.vBar != null) {
			if(area.height < size.y) {
				ctree.vBar.setMaximum(size.y);
				ctree.vBar.setThumb(area.height);
				if(!ctree.vBar.getVisible()) {
					ctree.vBar.setVisible(true);
				}
			} else {
				ctree.vBar.setMaximum(1);
				if(ctree.vBar.getVisible()) {
					ctree.vBar.setVisible(false);
				}
			}
		}
	}
}