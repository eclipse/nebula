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
public abstract class AbstractContainerLayout extends Layout {

	protected AbstractContainer container;

	protected int[] columnWidths;
	protected Point headerSize = new Point(0,0);
	protected int 	contentHeight = 0;
	private Point 	size = new Point(0,0);

	/**
	 * @param tree
	 */
	protected AbstractContainerLayout(AbstractContainer container) {
		this.container = container;
	}

	private void computeColumnWidths() {
		//// CREATE SUBROUTINE?
		int num_fillers = 0;
		AbstractColumn[] ca = container.internalGetColumns();
		columnWidths = new int[ca.length];
		for(int i = 0; i < ca.length; i++) {
			if(ca[i].isAutoWidth()) {
				num_fillers++;
			} else {
				columnWidths[i] = ca[i].getWidth();
			}
		}
		//// END CREATE SUBROUTINE?
		
		if(num_fillers > 0) {
			int[] fillers = new int[num_fillers];

			int xconsumed = 0;
			for(int i = 0, j = 0; i < ca.length && j < num_fillers; i++) {
				if(ca[i].isAutoWidth()) {
					fillers[j++] = i;
				} else {
					xconsumed += columnWidths[i];
				}
			}

			int fillerWidth0 = (headerSize.x - xconsumed) / num_fillers;
			int fillerWidth1 = headerSize.x - xconsumed - (fillerWidth0 * (num_fillers - 1));
			if(fillerWidth0 == 0) fillerWidth0 = 1;
			if(fillerWidth1 == 0) fillerWidth1 = 1;

			for(int i = 0; i < num_fillers; i++) {
				if(i == num_fillers-1) {
					columnWidths[fillers[i]] = fillerWidth1;
				} else {
					columnWidths[fillers[i]] = fillerWidth0;
				}
			}
		}
	}

	/**
	 * Compute the height of the Content area without consideration for cached values.
	 */
	protected abstract void computeContentHeight();

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
	protected abstract void computeContentHeight(int eventType, AbstractCell cell);

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
	protected abstract void computeContentHeight(int eventType, AbstractColumn column);

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
	protected abstract void computeContentHeight(int eventType, AbstractItem item);

	private void computeHeaderSize() {
		headerSize.x = headerSize.y = 0;
		if(container.internalTable != null) {
			int gridLine = container.getGridLineWidth();
			for(int i = 0; i < columnWidths.length; i++) {
				headerSize.x += (columnWidths[i] + gridLine);
			}
			headerSize.y = container.internalTable.getHeaderHeight();
		}
	}

	protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
		computeColumnWidths();
		computeHeaderSize();
		computeContentHeight();
	
		size.x = headerSize.x;
		size.y = headerSize.y + contentHeight;

		Point returnSize = new Point(size.x, size.y);
		
		if(container.hBar != null && !container.hBar.isDisposed() && !container.hBar.getVisible()) {
			returnSize.y -= (container.hBar.getSize().y + 3);
		}
		if(container.vBar != null && !container.vBar.isDisposed() && !container.vBar.getVisible()) {
			returnSize.x -= (container.vBar.getSize().x + 3);
		}
		
		return returnSize;
	}
	
	private void computeSize(int eventType, AbstractCell cell) {
		computeContentHeight(eventType, cell);
		size.x = headerSize.x;
		size.y = headerSize.y + contentHeight;
	}

	private void computeSize(int eventType, AbstractColumn column) {
		if(SWT.Resize == eventType) {
			computeColumnWidths();
			computeHeaderSize();
			computeContentHeight(eventType, column);
			size.x = headerSize.x;
			size.y = headerSize.y + contentHeight;
		}
	}
	
	private void computeSize(int eventType, AbstractItem item) {
		if(SWT.Move != eventType) {
			computeContentHeight(eventType, item);
			size.x = headerSize.x;
			size.y = headerSize.y + contentHeight;
		}
	}

	protected void layout(Composite composite, boolean flushCache) {
		Rectangle area = composite.getClientArea();
		if(area.isEmpty()) return;
		
		if(flushCache) computeSize(composite, -1, -1, flushCache);

		container.header.setBounds(
				area.x,
				area.y,
				area.width,
				headerSize.y
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
	void layout(int eventType, AbstractCell cell) {
		// TODO: handle SWT.Move for cells too?
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
	void layout(int eventType, AbstractColumn column) {
		computeSize(eventType, column);
		layoutContent(eventType, column);
		if(SWT.Resize == eventType) {
			updateScrollBars();
		}
	}
	
	/**
	 * Layout the Content area after the given item has experienced an event of the given type.
	 * Because an event has occurred, indicating a change in content, computeSize will be called.
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
	void layout(int eventType, AbstractItem item) {
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
	protected abstract void layoutContent();

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
	protected abstract void layoutContent(int eventType, AbstractCell cell);

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
	protected abstract void layoutContent(int eventType, AbstractColumn column);

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
	protected abstract void layoutContent(int eventType, AbstractItem item);

	protected void layoutHeader() {
		if(container.internalTable != null) {
			Rectangle tBounds = container.header.getClientArea();
			tBounds.height += 30; // TODO: does this value matter, as long as it's > the scrollbar size?
			// pogramatic scrolling doesn't work on win32
			if(AbstractContainer.win32 && container.hBar != null) {
				int sel = container.hBar.getSelection();
				tBounds.x = -sel;
				tBounds.width += sel;
			}
			container.internalTable.setBounds(tBounds);
		}
	}
	
	/**
	 * Recalculate the correct state of the scrollbars and set their visibility accordingly.
	 */
	private void updateScrollBars() {
		Rectangle area = container.getClientArea();
		if(area.isEmpty()) return;
		
		if(area.width < size.x) {
			container.hBar.setMaximum(size.x);
			container.hBar.setThumb(area.width);
			if(!container.hBar.getVisible()) {
				container.hBar.setVisible(true);
			}
		} else {
			container.hBar.setMaximum(1);
			if(container.hBar.getVisible()) {
				container.hBar.setVisible(false);
			}
		}

		if(area.height < size.y) {
			container.vBar.setMaximum(size.y);
			container.vBar.setThumb(area.height);
			if(!container.vBar.getVisible()) {
				container.vBar.setVisible(true);
			}
		} else {
			container.vBar.setMaximum(1);
			if(container.vBar.getVisible()) {
				container.vBar.setVisible(false);
			}
		}
	}
}