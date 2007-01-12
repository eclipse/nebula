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

import java.lang.reflect.Constructor;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
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
public abstract class AbstractItem extends Item {

	protected AbstractContainer container;
	protected AbstractCell[] cells;
	protected boolean enabled = true;
	protected boolean visible = true;
	
	// fixes the height of all cells to the value of fixedHeight
	public boolean useFixedHeight = false;
	public int fixedHeight = SWT.DEFAULT;
	// fixes the height of the title region of all cells to the value of fixedHeight
	public boolean useFixedTitleHeight = true;
	public int fixedTitleHeight = SWT.DEFAULT;
	private boolean autoHeight = false;

	public AbstractItem(AbstractContainer parent, int style) {
		this(parent, style, -1);
	}
	public AbstractItem(AbstractContainer parent, int style, Class cellClass) {
		this(parent, style, -1, new Class[] { cellClass }, null);
	}
	public AbstractItem(AbstractContainer parent, int style, Class[] cellClasses) {
		this(parent, style, -1, cellClasses, null);
	}
	public AbstractItem(AbstractContainer parent, int style, int index) {
		this(parent, style, index, new Class[0], null);
	}
	public AbstractItem(AbstractContainer parent, int style, int index, Class[] cellClasses) {
		this(parent, style, index, cellClasses, null);
	}
	protected AbstractItem(AbstractContainer parent, int style, int index, Class[] cellClasses, Object[] params) {
		super(parent, style);
		container = parent;
		initialize(params);
		cells = createCells(cellClasses != null ? cellClasses : container.cellClasses);
		container.addItem(index, this);
	}

	public void addListener(int eventType, Listener handler) {
		for(int i = 0; i < cells.length; i++) {
			cells[i].addListener(eventType, handler);
		}
	}


	protected void checkSubclass() {
		// TODO Auto-generated method stub
	}
	
	/**
	 * A convenience method to compute the size of each cell using the same hints
	 * @param widthHint
	 * @param heightHint
	 * @return an array of Point objects representing the computed cell sizes
	 */
	public Point[] computeSize(int widthHint, int heightHint) {
		int[] wa = new int[cells.length];
		int[] ha = new int[cells.length];
		Arrays.fill(wa, widthHint);
		Arrays.fill(ha, heightHint);
		return computeSize(wa, ha);
	}
	
	/**
	 * A convenience method to compute the size of each cell using the same widthHints
	 * @param widthHint
	 * @param heightHint
	 * @return an array of Point objects representing the computed cell sizes
	 */
	public Point[] computeSize(int[] widthHint, int heightHint) {
		int[] ha = new int[cells.length];
		Arrays.fill(ha, heightHint);
		return computeSize(widthHint, ha);
	}
	
	/**
	 * A convenience method to compute the size of each cell using the same heightHints
	 * @param widthHint
	 * @param heightHint
	 * @return an array of Point objects representing the computed cell sizes
	 */
	public Point[] computeSize(int widthHint, int[] heightHint) {
		int[] wa = new int[cells.length];
		Arrays.fill(wa, widthHint);
		return computeSize(wa, heightHint);
	}
	
	/**
	 * Computes the size of each cell using the widthHint and heightHint with the same
	 * index as the cell.
	 * @param widthHint
	 * @param heightHint
	 * @return an array of Point objects representing the computed cell sizes
	 */
	public Point[] computeSize(int[] widthHint, int[] heightHint) {
		Point[] cellSize = new Point[cells.length];
		
		// Set the title height, if requested to be constant across cells
		if(useFixedTitleHeight) {
			fixedTitleHeight = cells[0].computeTitleHeight(heightHint[0]);
			for(int i = 1; i < cells.length; i++) {
				fixedTitleHeight = Math.max(fixedTitleHeight, cells[i].computeTitleHeight(heightHint[i]));
			}
		}

		for(int i = 0; i < cells.length; i++) {
			cellSize[i] = cells[i].computeSize(widthHint[i], heightHint[i]);
		}
		
		return cellSize;
	}
	
	boolean contains(Control control) {
		for(int i = 0; i < cells.length; i++) {
			if(cells[i].contains(control)) return true;
		}
		return false;
	}

	public boolean contains(Point pt) {
		Rectangle[] ba = getCellBounds();
		for(int i = 0; i < ba.length; i++) {
			if(ba[i].contains(pt)) return true;
		}
		return false;
	}
	
	/**
	 * Creates a cell of the default class, as determined by the implementation
	 * <p>Is used to auto-fill a cell when no specific cell class is provided for the 
	 * given column</p>
	 * @param column the column requested the new cell
	 * @param style the cell's bitwise OR'd style
	 * @return the new cell
	 */
	protected abstract AbstractCell createCell(int column, int style);
	
	private AbstractCell[] createCells(Class[] cellClasses) {
		final int cellStyle = getStyle();

		if(!container.autoFillCells && (cellClasses == null || cellClasses.length == 0)) {
			return new AbstractCell[] { createCell(0, cellStyle) };
		} else {
			if(cellClasses == null) cellClasses = new Class[0]; 
			AbstractCell[] ca = new AbstractCell[
	                                         container.autoFillCells ? 
	                                        		 container.getColumnCount() :
	                                        			 cellClasses.length];
			for(int i = 0; i < ca.length; i++) {
				AbstractCell cell = null;
				if(i < cellClasses.length && cellClasses[i] != null) {
					try {
						// see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4301875
						Constructor[] constructors = cellClasses[i].getConstructors();
						for(int j = 0; j < constructors.length; j++) {
							Class[] params = constructors[j].getParameterTypes();
							if(params.length == 2 && 
									params[0].isInstance(this) && 
									params[1].equals(int.class)) {
								cell = (AbstractCell) constructors[j].newInstance(
										new Object[] { this, new Integer(cellStyle) } );
								break;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				ca[i] = (cell != null) ? cell : createCell(i, cellStyle);
			}
			return ca;
		}
	}
	
	/**
	 * Provides a chance for subclasses to initialize themselves before the cells are created and the item
	 * is added to its parent Container.
	 * @param params the parameters
	 */
	protected abstract void initialize(Object[] params);
	
	public void dispose() {
		// TODO dispose listener
		for(int i = 0; i < cells.length; i++) {
			cells[i].dispose();
		}
		container.removeItem(this);
		super.dispose();
	}

	public Color getBackground() {
		return cells[0].getBackground();
	}
	
	public Color getBackground(int index) {
		if(hasCell(index)) return cells[index].getBackground();
		return null;
	}
	
	public Rectangle getBounds(int index) {
		if(hasCell(index)) return cells[index].getBounds();
		return null;
	}
	
	Rectangle[] getCellBounds() {
		Rectangle[] bounds = new Rectangle[cells.length];
		for(int i = 0; i < bounds.length; i++) {
			bounds[i] = cells[i].getBounds();
		}
		return bounds;
	}

	public AbstractCell getCell(int cell) {
		if(cell >= 0 && cell < cells.length) {
			return cells[cell];
		}
		return null;
	}
	
	public AbstractCell getCell(Point pt) {
		for(int i = 0; i < cells.length; i++) {
			if(cells[i].getBounds().contains(pt)) {
				return cells[i];
			}
		}
		return null;
	}
	
	public int getCellIndex(AbstractCell cell) {
		return Arrays.asList(cells).indexOf(cell);
	}
	
	public AbstractCell[] getCells() {
		return cells;
	}

	public Color getForeground() {
		return cells[0].getForeground();
	}
	
	public Color getForeground(int index) {
		if(hasCell(index)) return cells[index].getForeground();
		return null;
	}
	

	public boolean isAutoHeight() {
		return autoHeight;
	}
	
	protected boolean isCellState(int opcode) {
		for(int i = 0; i < cells.length; i++) {
			if(cells[i].isCellState(opcode)) return true;
		}
		return false;
	}

	protected boolean isCellState(int cell, int opcode) {
		if(cell >= 0 && cell < cells.length) {
			return cells[cell].isCellState(opcode);
		}
		return false;
	}

	public Composite getChildArea(int column) {
		if((column >= 0) && (column < cells.length)) {
			return cells[column].getChildArea();
		}
		return null;
	}

//	protected List getColorManagedControls() {
//		List list = new ArrayList();
//		for(int i = 0; i < cells.length; i++) {
//			list.addAll(cells[i].getColorManagedControls());
//		}
//		return list;
//	}

	public AbstractContainer getContainer() {
		return container;
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
	
	public int getFixedHeight() {
		return fixedHeight;
	}

	public int getFixedTitleHeight() {
		return fixedTitleHeight;
	}

	public Font getFont() {
		return cells[0].getFont();
	}
	
	public Font getFont(int index) {
		if(hasCell(index)) return cells[index].getFont();
		return null;
	}
	
	public int getHeight() {
		int height = cells[0].getBounds().height;
		for(int i = 1; i < cells.length; i++) {
			height = Math.max(height, cells[i].getBounds().height);
		}
		return height;
	}
	
//	public int getIndex() {
//		return container.visibleItems.indexOf(this);
//	}

	public Point[] getLocation() {
		Point[] la = new Point[cells.length];
		for(int i = 0; i < la.length; i++) {
			la[i] = cells[i].getLocation();
		}
		return la;
	}

	Point[] getCellSizes() {
		Point[] sa = new Point[cells.length];
		for(int i = 0; i < sa.length; i++) {
			sa[i] = cells[i].getSize();
		}
		return sa;
	}
	
	public Composite getTitleArea(int column) {
		if((column >= 0) && (column < cells.length)) {
			return cells[column].getChildArea();
		}
		return null;
	}

	public Rectangle getBounds() {
		Rectangle[] ba = getCellBounds();
		Rectangle ub = new Rectangle(ba[0].x,ba[0].y,ba[0].width,ba[0].height);
		for(int i = 1; i < ba.length; i++) {
			ub.add(new Rectangle(ba[i].x,ba[i].y,ba[i].width,ba[i].height));
		}
		ub.x = 0;
		ub.width = container.getBodySize().x;
		return ub;
	}
	
	public Point getSize() {
		Rectangle ub = getBounds();
		return new Point(ub.width, ub.height);
	}

	/**
	 * Returns whether or not this CContainerItem is requesting to be visible.
	 * In other words, if its internal visibility flag is set to true.
	 * There may exist other conditions which make this item actually not visible within
	 * its container.
	 * @return true if internally considered visible, false otherwise
	 * @see Control#getVisible()
	 * @see AbstractItem#isVisible()
	 */
	public boolean getVisible() {
		return visible;
	}
	
	/**
	 * Returns true if the receiver is visible and all parents up to and including the 
	 * root of its container are visible. Otherwise, false is returned.
	 * @return true if visible, false otherwise
	 * @see AbstractItem#getVisible()
	 * @see Control#isVisible()
	 */
	public boolean isVisible() {
		return container.visibleItems.contains(this);
	}
	
	boolean hasCell(int index) {
		return (index >= 0 && index < cells.length);
	}
	
	/**
	 * Give the Item a chance to handle the mouse event
	 * @param event the Event
	 * @return 0: do nothing, 1: redraw, 2: layout
	 */
	public int handleMouseEvent(Event event, boolean selectionActive) {
		int result = 0;
		for(int i = 0; i < cells.length; i++) {
			result |= cells[i].handleMouseEvent(event, selectionActive);
		}
		return result;
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
	
	public boolean isOpen(Point pt) {
		for(int i = 0; i < cells.length; i++) {
			if(cells[i].getToggleBounds().contains(pt)) return cells[i].isOpen();
		}
		return false;
	}
	
	public boolean isSelected() {
		for(int i = 0; i < cells.length; i++) {
			if(cells[i].isSelected()) return true;
		}
		return false;
	}
	
	public boolean isTogglePoint(Point pt) {
		for(int i = 0; i < cells.length; i++) {
			if(cells[i].getToggleVisible() && cells[i].getToggleBounds().contains(pt)) 
				return true;
		}
		return false;
	}
	
	public void paint(GC gc, Rectangle ebounds) {
		for(int i = 0; i < cells.length; i++) {
			cells[i].paint(gc, ebounds);
		}
	}
	
	public void removeListener(int eventType, Listener handler) {
		for(int i = 0; i < cells.length; i++) {
			cells[i].addListener(eventType, handler);
		}
	}

	public void setAutoHeight(boolean auto) {
		autoHeight = auto;
	}
	
	public void setBackground(Color color) {
		for(int i = 0; i < cells.length; i++) {
			cells[i].setBackground(color);
		}
	}
	
	public void setBackground(int index, Color color) {
		if(hasCell(index)) cells[index].setBackground(color);
	}
	
	public void setBounds(int cell, Rectangle bounds) {
		if(cell >= 0 && cell < cells.length) {
			cells[cell].setBounds(bounds);
		}
	}
	
	public void setBounds(Rectangle bounds) {
		cells[0].setBounds(bounds);
	}
	
	public void setBounds(Rectangle[] bounds) {
		int i = 0, j = 0;
		for( ; i < cells.length && j < bounds.length; i++) {
			int span = cells[i].colSpan;
			if(span > 1) {
				Rectangle ub = new Rectangle(bounds[j].x,bounds[j].y,bounds[j].width,bounds[j].height);
				for(j++; j < (i + span) && j < bounds.length; j++) {
					ub.add(new Rectangle(bounds[j].x,bounds[j].y,bounds[j].width,bounds[j].height));
				}
				cells[i].setBounds(ub.x, ub.y, ub.width, ub.height);
			} else {
				cells[i].setBounds(bounds[j].x, bounds[j].y, bounds[j].width, bounds[j].height);
				j++;
			}
		}
		if(i < cells.length) {
			j = bounds.length-1;
			for( ; i < cells.length; i++) {
				cells[i].setBounds(bounds[j].x,bounds[j].y,0,0);
			}
		}
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setFixedHeight(int height) {
		fixedHeight = height;
	}
	
	public void setFixedTitleHeight(int height) {
		fixedTitleHeight = height;
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
	
	/**
	 * @return true if ALL cells are of type SWT.SIMPLE, false otherwise
	 */
//	public boolean isSimple() {
//		for(int i = 0; i < cells.length; i++) {
//			if((cells[i].getStyle() & (SWT.SIMPLE|SWT.DROP_DOWN)) == 0) return true;
//		}
//		return false;
//	}
	
	public void setIsGridLine(boolean isGridLine) {
		for(int i = 0; i < cells.length; i++) {
			cells[i].setIsGridLine(isGridLine);
		}
	}

	public void setLocation(int cell, Point location) {
		if(cell >= 0 && cell < cells.length) {
			cells[cell].setLocation(location);
		}
	}

	public void setLocation(Point location) {
		cells[0].setLocation(location);
	}

	public void setLocation(Point[] location) {
		for(int i = 0; i < cells.length; i++) {
			cells[i].setLocation(location[i]);
		}
	}
	
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

	public void setOpen(Point pt, boolean open) {
		for(int i = 0; i < cells.length; i++) {
			if(cells[i].getBounds().contains(pt)) {
				cells[i].setOpen(open);
				break;
			}
		}
	}

	public void setSelected(boolean selected) {
		if(enabled) {
			for(int i = 0; i < cells.length; i++) {
				cells[i].setSelected(selected);
			}
		}
	}

	public void setSize(int cell, Point size) {
		if(cell >= 0 && cell < cells.length) {
			cells[cell].setSize(size);
		}
	}
	
	public void setSize(Point size) {
		cells[0].setSize(size);
	}
	
	public void setSize(Point[] size) {
		for(int i = 0; i < cells.length; i++) {
			cells[i].setSize(size[i]);
		}
	}

	public void setVisible(boolean visible) {
		if(this.visible != visible) {
			this.visible = visible;
			for(int i = 0; i < cells.length; i++) {
				cells[i].setVisible(visible);
			}
			container.setDirtyFlag(AbstractContainer.DIRTY_VISIBLE);
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
}
