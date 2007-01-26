/*
 * Copyright (C) 2005 David Orme <djo@coconut-palm-software.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Orme     - Initial API and implementation
 *     The Pampered Chef - Expanded to handle sorting
 */
package org.eclipse.swt.nebula.widgets.compositetable;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Class AbstractHeader. A Header class making it easier to implement a
 * sorted table where clicking on a header column sets or changes the sort
 * order.
 * 
 * @author djo
 */
public abstract class AbstractHeader extends Composite {

    private String[] columnLabelStrings;
    private int[] columnAlignments;

    Table headerTable;
    List tableColumns;

	private int sortDirection = SWT.NONE; // SWT.NONE, SWT.UP, SWT.DOWN
	private int lastSortColumn = -1;

    // Begin main implementation-----------------------------------------------
    
	/**
     * A Header object for CompositeTable that can tell clients to re-sort,
     * and can move/resize the columns (if used with an appropriate layout 
     * manager).
     * 
	 * @param parent The SWT parent
	 * @param style SWT style bits.  The same style bits accepted by Composite.
	 */
	public AbstractHeader(Composite parent, int style) {
		super(parent, style);
        headerTable = new Table(this, SWT.NULL);
        headerTable.setHeaderVisible(true);
        addControlListener(controlListener);
        addDisposeListener(disposeListener);
	}

    /**
     * Clients must override this method to reset the current sort column/order
     * if they want to support sorting.
     * 
     * @param column
     *            The column on which to sort.
     * @param sortDirection
     *            SWT.NONE, SWT.UP, or SWT.DOWN.
     * @return boolean true if sorting occurred; false otherwise.
     */
    protected boolean sortOnColumn(int column, int sortDirection) {
        return false;
    }

    private boolean movable = true;
    
    /**
     * Sets if the columns are movable.
     * 
     * @param movable true if the columns are movable; false otherwise.
     */
    public void setMovable(boolean movable) {
        this.movable = movable;
        for (Iterator columnsIter = tableColumns.iterator(); columnsIter.hasNext();) {
            TableColumn column = (TableColumn) columnsIter.next();
            column.setMoveable(movable);
        }
    }
    
    /**
     * Returns if the columns are movable.
     * 
     * @return boolean true if the columns are movable; false otherwise.
     */
    public boolean isMovable() {
        return movable;
    }
    
    private boolean resizable = true;
    
    /**
     * Sets if the columns are resizable.
     * 
     * @param resizable true if the columns should be resizable; false otherwise.
     */
    public void setResizable(boolean resizable) {
        this.resizable = resizable;
        for (Iterator columnsIter = tableColumns.iterator(); columnsIter.hasNext();) {
            TableColumn column = (TableColumn) columnsIter.next();
            column.setResizable(resizable);
        }
    }
    
    /**
     * Returns if the columns are resizable.
     * 
     * @return boolean true if the columns should be resizable; false otherwise.
     */
    public boolean isResizable() {
        return resizable;
    }
    
    /**
     * Clients must call this method (or its overloaded cousin) exactly once 
     * in their constructor to set the column text strings.  All alignments
     * will be set to SWT.LEFT.
     * 
     * @param columnTextStrings String[] The text strings to display in each column
     */
    public void setColumnText(String[] columnTextStrings) {
        this.columnLabelStrings = columnTextStrings;
        this.columnAlignments = new int[columnLabelStrings.length];
        for (int i = 0; i < columnAlignments.length; i++) {
            columnAlignments[i] = SWT.LEFT;
        }
        initializeColumns();
    }
    
    /**
     * Clients must call this method (or its overloaded cousin) exactly once 
     * in their constructor to set the column text strings and alignments.
     * 
     * @param columnTextStrings String[] The text strings to display in each column
     * @param alignments An array of SWT style bits. Each element is one of:
     * SWT.LEFT, SWT.CENTER, or SWT.RIGHT.
     */
    public void setColumnText(String[] columnTextStrings, int[] alignments) {
        this.columnLabelStrings = columnTextStrings;
        this.columnAlignments = alignments;
        initializeColumns();
    }
    
    private List columnControlListeners = new ArrayList();
    
    public void addColumnControlListener(ControlListener c) {
        columnControlListeners.add(c);
    }
    
    public void removeColumnControlListener(ControlListener c) {
        columnControlListeners.remove(c);
    }

    private void initializeColumns() {
        this.tableColumns = new ArrayList();
        for (int i = 0; i < columnLabelStrings.length; i++) {
            TableColumn column = new TableColumn(headerTable, columnAlignments[i]);
            column.setMoveable(movable);
            column.setResizable(resizable);
            column.setText(columnLabelStrings[i]);
            this.tableColumns.add(column);
            column.addControlListener(columnControlListener);
            column.addSelectionListener(columnSelectionListener);
        }
    }

    private int toggleSortDirection() {
        if (sortDirection == SWT.NONE) {
            sortDirection = SWT.DOWN;
        } else if (sortDirection == SWT.DOWN) {
            sortDirection = SWT.UP;
        } else if (sortDirection == SWT.UP) {
            sortDirection = SWT.DOWN;
        }
        return sortDirection;
    }

    public Point computeSize(int wHint, int hHint) {
        return computeSize(wHint, hHint, true);
    }
    
    public Point computeSize(int wHint, int hHint, boolean changed) {
        Point preferredSize = super.computeSize(wHint, hHint, changed);
        preferredSize.y = headerTable.getHeaderHeight();
        return preferredSize;
    }

    // Event handlers ---------------------------------------------------------
    
    private ControlListener controlListener = new ControlAdapter() {
        public void controlResized(ControlEvent e) {
            Point size = AbstractHeader.this.getSize();
            headerTable.setBounds(0, 0, size.x, size.y);
        }
    };
    
    private ControlListener columnControlListener = new ControlAdapter() {
        public void controlResized(ControlEvent e) {
            for (Iterator i = columnControlListeners.iterator(); i.hasNext();) {
                ControlListener listener = (ControlListener) i.next();
                listener.controlResized(e);
            }
        }
        public void controlMoved(ControlEvent e) {
            for (Iterator i = columnControlListeners.iterator(); i.hasNext();) {
                ControlListener listener = (ControlListener) i.next();
                listener.controlMoved(e);
            }
        }
    };
    
    private SelectionListener columnSelectionListener = new SelectionListener() {
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
        
        public void widgetSelected(SelectionEvent e) {
            TableColumn tableColumn = (TableColumn)e.widget;
            
            int c = tableColumns.indexOf(tableColumn);
            if (c != lastSortColumn) {
                sortDirection = SWT.NONE;
            }
            
            if (sortOnColumn(c, toggleSortDirection())) {
                if (c != lastSortColumn) headerTable.setSortColumn(tableColumn);
                headerTable.setSortDirection(sortDirection);
                
                lastSortColumn = c;
            }
        }
    };
    
    private DisposeListener disposeListener = new DisposeListener() {
        public void widgetDisposed(DisposeEvent e) {
            AbstractHeader header = AbstractHeader.this;
            header.removeControlListener(controlListener);
        }
    };
}
