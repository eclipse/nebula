/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jeremy Dowdall <jeremyd@aspencloud.com>
 *     			 - modified for use with org.aspencloud.widgets.CTableTree
 *******************************************************************************/
package org.eclipse.swt.nebula.nebface.ctabletreeviewer.ccontainerviewer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.nebula.widgets.ctabletree.CContainer;
import org.eclipse.swt.nebula.widgets.ctabletree.CContainerCell;
import org.eclipse.swt.nebula.widgets.ctabletree.CContainerItem;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

/**
 * <p>
 * NOTE:  THIS VIEWER AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p>
 */
public abstract class CContainerViewer extends StructuredViewer {
	protected CContainer container;
	protected ICContainerCellProvider cellProvider;
	protected String[] columnProperties;
	
	/**
	 * Creates a container viewer on the given container control.
	 * The viewer has no input, no content provider, a default label provider, 
	 * no sorter, and no filters.
	 *
	 * @param container the Container control
	 */
	public CContainerViewer(CContainer container) {
		this.container = container;
		hookControl(container);
	}
	
	/**
	 * Adds the given element to this list viewer.
	 * If this viewer does not have a sorter, the element is added at the end;
	 * otherwise the element is inserted at the appropriate position.
	 * <p>
	 * This method should be called (by the content provider) when a single element 
	 * has been added to the model, in order to cause the viewer to accurately
	 * reflect the model. This method only affects the viewer, not the model.
	 * Note that there is another method for efficiently processing the simultaneous
	 * addition of multiple elements.
	 * </p>
	 *
	 * @param element the element
	 */
	public void add(Object element) {
		add(new Object[] { element });
	}
	
	/**
	 * Adds the given elements to this table viewer. If this viewer does not
	 * have a sorter, the elements are added at the end in the order given;
	 * otherwise the elements are inserted at appropriate positions.
	 * <p>
	 * This method should be called (by the content provider) when elements have
	 * been added to the model, in order to cause the viewer to accurately
	 * reflect the model. This method only affects the viewer, not the model.
	 * </p>
	 * 
	 * @param elements
	 *            the elements to add
	 */
	public void add(Object[] elements) {
		assertElementsNotNull(elements);
		Object[] filtered = filter(elements);
		
		for (int i = 0; i < filtered.length; i++) {
			Object element = filtered[i];
			int index = indexForElement(element);
			createItem(element, index);
		}
	}
	
	/**
	 * Convenience method; use when using CTable as a List
	 * @see org.aspencloud.viewers.CCalendarViewer#setCellProvider(Class[])
	 */
	public void setCellProvider(Class clazz) {
		final Class[] classes = new Class[] { clazz };
		this.cellProvider = new ICContainerCellProvider() {
			public Class[] getCellClasses(Object element) {
				return classes;
			}
		};
	}
	
	/**
	 * 
	 * @param cellClasses
	 */
	public void setCellProvider(Class[] cellClasses) {
		final Class[] classes = cellClasses;
		this.cellProvider = new ICContainerCellProvider() {
			public Class[] getCellClasses(Object element) {
				return classes;
			}
		};
	}
	
	public void setCellProvider(ICContainerCellProvider provider) {
		this.cellProvider = provider;
	}
	
	/**
	 * 
	 * @param element
	 * @param index
	 */
	protected abstract void createItem(Object element, int index);
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.jface.viewers.StructuredViewer#doFindInputItem(java.lang.Object)
	 */
	protected Widget doFindInputItem(Object element) {
		if (equals(element, getRoot()))
			return getContainer();
		return null;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.jface.viewers.StructuredViewer#doFindItem(java.lang.Object)
	 */
	protected Widget doFindItem(Object element) {
		CContainerItem[] children = container.getItems();
		for(int i = 0; i < children.length; i++) {
			CContainerItem item = children[i];
			Object data = item.getData();
			if(data != null && equals(data, element))
				return item;
		}
		return null;
	}
	
	protected void associate(Object element, Widget item) {
		Object data = item.getData();
		if (data != element) {
			if (data != null) {
				disassociate(item);
			}
			item.setData(element);
		}
		// Always map the element, even if data == element,
		// since unmapAllElements() can leave the map inconsistent
		// See bug 2741 for details.
		mapElement(element, item);
	}

	protected void doUpdateCell(int index, CContainerCell cell, Object element, String[] properties) {
		IBaseLabelProvider prov = (IBaseLabelProvider) getLabelProvider();
		if(prov != null) {
			Color background = null;
			Color foreground = null;
			if(prov instanceof ITableColorProvider) {
				background = ((ITableColorProvider) prov).getBackground(element, index);
				foreground = ((ITableColorProvider) prov).getForeground(element, index);
			} else if(prov instanceof IColorProvider) {
				background = ((IColorProvider) prov).getBackground(element);
				foreground = ((IColorProvider) prov).getForeground(element);
			}
			if(background != null) cell.setCellBackground(background);
			if(foreground != null) cell.setCellForeground(foreground);
			
			// TODO doUpdateItem - set fonts...
		}
	}
		
	protected void doUpdateItem(Widget widget, Object element, boolean fullMap) {
		if(widget instanceof CContainerItem) {
			final CContainerItem item = (CContainerItem) widget;
			
			// remember element we are showing
			if (fullMap) {
				associate(element, item);
			} else {
				item.setData(element);
				mapElement(element, item);
			}
			
			CContainerCell[] cells = item.getCells();
			for(int i = 0; i < cells.length; i++) {
				CContainerCell cell = (CContainerCell) cells[i];
				if(cell.update(element, getStringColumnProperties())) {
					doUpdateCell(i, cell, element, getStringColumnProperties());
				}
			}
		}
	}
	
	public CContainer getContainer() {
		return container;
	}
	
	/**
	 * Returns the column properties of this table viewer. The properties must
	 * correspond with the columns of the table control. They are used to
	 * identify the column in a cell modifier.
	 * 
	 * @return the list of column properties
	 */
	public Object[] getColumnProperties() {
		return columnProperties;
	}

	public String[] getStringColumnProperties() {
		return columnProperties;
	}

	/* (non-Javadoc)
	 * Method declared on Viewer.
	 */
	public Control getControl() {
		return container;
	}
	
	/**
	 * Returns the element with the given index from this table viewer. Returns
	 * <code>null</code> if the index is out of range.
	 * <p>
	 * This method is internal to the framework.
	 * </p>
	 * 
	 * @param index
	 *            the zero-based index
	 * @return the element at the given index, or <code>null</code> if the
	 *         index is out of range
	 */
	public Object getElementAt(int index) {
		if(index >= 0 && index < container.getItemCount()) {
			CContainerItem i = container.getItem(index);
			if(i != null)
				return i.getData();
		}
		return null;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.jface.viewers.StructuredViewer#getSelectionFromWidget()
	 */
	protected List getSelectionFromWidget() {
		Widget[] items = container.getSelection();
		ArrayList list = new ArrayList(items.length);
		for (int i = 0; i < items.length; i++) {
			Widget item = items[i];
			Object e = item.getData();
			if (e != null)
				list.add(e);
		}
		return list;
	}
	
	/*
	 * Returns the index where the item should be inserted.
	 */
	protected int indexForElement(Object element) {
		ViewerSorter sorter = getSorter();
		if (sorter == null)
			return container.getItemCount();
		int count = container.getItemCount();
		int min = 0, max = count - 1;
		while(min <= max) {
			int mid = (min + max) / 2;
			Object data = container.getItem(mid).getData();
			int compare = sorter.compare(this, data, element);
			if(compare == 0) {
				// find first item > element
				while(compare == 0) {
					++mid;
					if(mid >= count) break;
					data = container.getItem(mid).getData();
					compare = sorter.compare(this, data, element);
				}
				return mid;
			}
			if(compare < 0) {
				min = mid + 1;
			} else {
				max = mid - 1;
			}
		}
		return min;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.jface.viewers.Viewer#inputChanged(java.lang.Object, java.lang.Object)
	 */
	protected void inputChanged(Object input, Object oldInput) {
		getControl().setRedraw(false);
		try {
			// refresh() attempts to preserve selection, which we want here
			refresh();
		} finally {
			getControl().setRedraw(true);
		}
	}
	
	/**
	 * Inserts the given element into this table viewer at the given position.
	 * If this viewer has a sorter, the position is ignored and the element is
	 * inserted at the correct position in the sort order.
	 * <p>
	 * This method should be called (by the content provider) when elements have
	 * been added to the model, in order to cause the viewer to accurately
	 * reflect the model. This method only affects the viewer, not the model.
	 * </p>
	 * 
	 * @param element
	 *            the element
	 * @param position
	 *            a 0-based position relative to the model, or -1 to indicate
	 *            the last position
	 */
	public void insert(Object element, int position) {
		if (getSorter() != null || hasFilters()) {
			add(element);
			return;
		}
		if (position == -1)
			position = container.getItemCount();
		
		createItem(element,position);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.jface.viewers.StructuredViewer#internalRefresh(java.lang.Object)
	 */
	protected void internalRefresh(Object element) {
		internalRefresh(element, true);
	}
	
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.jface.viewers.StructuredViewer#internalRefresh(java.lang.Object, boolean)
	 */
	protected void internalRefresh(Object element, boolean updateLabels) {
		if (element == null || equals(element, getRoot())) {
			internalRefreshAll(updateLabels);
		} else {
			Widget w = findItem(element);
			if (w != null) {
				updateItem(w, element);
			}
		}
	}
	
	/**
	 * Refresh all of the elements of the table. update the
	 * labels if updatLabels is true;
	 * @param updateLabels
	 * 
	 * @since 3.1
	 */
	protected void internalRefreshAll(boolean updateLabels) {
		Object[] children = getSortedChildren(getRoot());
		CContainerItem[] items = getContainer().getItems();
		List ci = Arrays.asList(children);
		List li = Arrays.asList(items);
		for(int i = 0; i < items.length; i++) {
			if(!items[i].isDisposed()) {
				Object data = items[i].getData();
				if(!ci.contains(items[i].getData())) {
					if(data != null) {
						disassociate(items[i]);
					}
					items[i].dispose();
				}
			}
		}
		for(int i = 0; i < children.length; i++) {
			Object item = findItem(children[i]);
			if(item == null) { // item does not exist for the child, insert a new one into the list
				insert(children[i], i);
			} else if(li.contains(item)) {
				if(item != container.getItem(i)) {
					container.move((CContainerItem) item, i);
				}
				if(updateLabels) {
					refresh(children[i], true);
				}
			}
		}
	}

	/**
	 * Removes the given elements from this table viewer.
	 * 
	 * @param elements
	 *            the elements to remove
	 */
	private void internalRemove(final Object[] elements) {
		Object input = getInput();
		for(int i = 0; i < elements.length; ++i) {
			if (equals(elements[i], input)) {
				setInput(null);
				return;
			}
		}
		List l = new ArrayList();
		for(int i = 0; i < elements.length; ++i) {
			Widget w = findItem(elements[i]);
			if(w instanceof CContainerItem) {
				CContainerItem item = (CContainerItem) w;
				disassociate(item);
				l.add(item);
			}
		}
		CContainerItem[] ia = l.isEmpty() ? new CContainerItem[0] : (CContainerItem[]) l.toArray(new CContainerItem[l.size()]);
		container.remove(ia);
	}
	
	protected void disassociate(Widget item) {
		Object element = item.getData();
		Assert.isNotNull(element);
		//Clear the map before we clear the data
		unmapElement(element, item);
		item.setData(null);
	}

	/**
	 * Removes the given element from this table viewer. The selection is
	 * updated if necessary.
	 * <p>
	 * This method should be called (by the content provider) when a single
	 * element has been removed from the model, in order to cause the viewer to
	 * accurately reflect the model. This method only affects the viewer, not
	 * the model. Note that there is another method for efficiently processing
	 * the simultaneous removal of multiple elements.
	 * </p>
	 * <strong>NOTE:</strong> removing an object from a virtual
	 * table will decrement the itemCount.
	 * 
	 * @param element
	 *            the element
	 */
	public void remove(Object element) {
		remove(new Object[] { element });
	}
	
	
	/**
	 * Removes the given elements from this table viewer. The selection is
	 * updated if required.
	 * <p>
	 * This method should be called (by the content provider) when elements have
	 * been removed from the model, in order to cause the viewer to accurately
	 * reflect the model. This method only affects the viewer, not the model.
	 * </p>
	 * 
	 * @param elements
	 *            the elements to remove
	 */
	public void remove(final Object[] elements) {
		assertElementsNotNull(elements);
		if (elements.length == 0) {
			return;
		}
		preservingSelection(new Runnable() {
			public void run() {
				internalRemove(elements);
			}
		});
	}
	
	/**
	 * Replace the entries starting at index with elements.
	 * This method assumes all of these values are correct
	 * and will not call the content provider to verify.
	 * <strong>Note that this method will create a TableItem
	 * for all of the elements provided</strong>.
	 * @param element
	 * @param index
	 * @see ILazyContentProvider
	 * 
	 * @since 3.1
	 */
	public void replace(Object element, int index){
		CContainerItem item = container.getItem(index);
		refreshItem(item, element);
	}
	
	/**
	 * @see org.eclipse.jface.viewers.StructuredViewer#reveal(java.lang.Object)
	 */
	public void reveal(Object element) {
		Assert.isNotNull(element);
		Widget w = findItem(element);
		if(w instanceof CContainerItem) {
			container.showItem((CContainerItem) w);
		}
	}
	
	/**
	 * Sets the column properties of this table viewer. The properties must
	 * correspond with the columns of the table control. They are used to
	 * identify the column in a cell modifier.
	 * 
	 * @param columnProperties
	 *            the list of column properties
	 */
	public void setColumnProperties(String[] columnProperties) {
		this.columnProperties = columnProperties;
	}
	
	/**
	 * The table viewer implementation of this <code>Viewer</code> framework
	 * method ensures that the given label provider is an instance of either
	 * <code>ITableLabelProvider</code> or <code>ILabelProvider</code>. If
	 * it is an <code>ITableLabelProvider</code>, then it provides a separate
	 * label text and image for each column. If it is an
	 * <code>ILabelProvider</code>, then it provides only the label text and
	 * image for the first column, and any remaining columns are blank.
	 */
	public void setLabelProvider(IBaseLabelProvider labelProvider) {
		Assert.isTrue(labelProvider instanceof ITableLabelProvider || labelProvider instanceof ILabelProvider);
		super.setLabelProvider(labelProvider);
	}
	
	/**
	 * @see org.eclipse.jface.viewers.StructuredViewer#setSelectionToWidget(java.util.List, boolean)
	 */
	protected void setSelectionToWidget(List list, boolean reveal) {
		if(list == null) {
			container.deselectAll();
			return;
		}
		
		int size = list.size();
		CContainerItem[] items = new CContainerItem[size];
		int count = 0;
		for(int i = 0; i < size; ++i) {
			Object o = list.get(i);
			Widget w = findItem(o);
			if(w instanceof CContainerItem) {
				CContainerItem item = (CContainerItem) w;
				items[count++] = item;
			}
		}
		if (count < size) {
			System.arraycopy(items, 0, items = new CContainerItem[count], 0, count);
		}
		container.setSelection(items);
		
		if(reveal) {
			container.showSelection();
		}
	}
	
	/**
	 * not yet implemented
	 * <p>post a feature request if you need it enabled</p>
	 */
	public void cancelEditing() {
		// TODO Auto-generated method stub
	}

	/**
	 * not yet implemented
	 * <p>post a feature request if you need it enabled</p>
	 */
	public void editElement(Object element, int column) {
		// TODO Auto-generated method stub
	}

	/**
	 * @return the Cell Provider, if there is one, null otherwise
	 */
	public ICContainerCellProvider getCellProvider() {
		return cellProvider;
	}
	
	/**
	 * not yet implemented
	 * <p>post a feature request if you need it enabled</p>
	 */
	public CellEditor[] getCellEditors() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * not yet implemented
	 * <p>post a feature request if you need it enabled</p>
	 */
	public ICellModifier getCellModifier() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * not yet implemented
	 * <p>post a feature request if you need it enabled</p>
	 */
	public boolean isCellEditorActive() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * not yet implemented
	 * <p>post a feature request if you need it enabled</p>
	 */
	public void setCellEditors(CellEditor[] editors) {
		// TODO Auto-generated method stub
	}

	/**
	 * not yet implemented
	 * <p>post a feature request if you need it enabled</p>
	 */
	public void setCellModifier(ICellModifier modifier) {
		// TODO Auto-generated method stub
	}
}