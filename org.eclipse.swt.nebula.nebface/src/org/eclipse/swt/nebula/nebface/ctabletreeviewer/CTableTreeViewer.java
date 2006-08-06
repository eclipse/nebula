/****************************************************************************
* Copyright (c) 2006 Jeremy Dowdall
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
*****************************************************************************/

package org.eclipse.swt.nebula.nebface.ctabletreeviewer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.nebula.nebface.ctabletreeviewer.ccontainerviewer.CContainerViewer;
import org.eclipse.swt.nebula.nebface.ctabletreeviewer.ccontainerviewer.ICContainerLabelProvider;
import org.eclipse.swt.nebula.widgets.ctabletree.CTableTree;
import org.eclipse.swt.nebula.widgets.ctabletree.CTableTreeCell;
import org.eclipse.swt.nebula.widgets.ctabletree.CTableTreeItem;
import org.eclipse.swt.nebula.widgets.ctabletree.ccontainer.CContainerCell;
import org.eclipse.swt.nebula.widgets.ctabletree.ccontainer.CContainerItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

/**
 * A concrete viewer based on an SWT <code>List</code> control.
 * <p>
 * This class is not intended to be subclassed. It is designed to be
 * instantiated with a pre-existing SWT <code>List</code> control and configured
 * with a domain-specific content provider, label provider, element filter (optional),
 * and element sorter (optional).
 * <p>
 * Note that the SWT <code>List</code> control only supports the display of strings, not icons.
 * If you need to show icons for items, use <code>TableViewer</code> instead.
 * </p>
 * 
 * @see TableViewer
 */
public class CTableTreeViewer extends CContainerViewer {
	private int treeColumn = -1;
	
	/**
	 * Creates a CTableTree viewer on the given CTableTree control.
	 * The viewer has no input, no content provider, a default label provider, 
	 * no sorter, and no filters.
	 *
	 * @param list the list control
	 */
	public CTableTreeViewer(CTableTree cTableTree) {
		super(cTableTree);
	}
	
	/**
	 * Creates a CTableTree viewer on a newly-created CTableTree control under the given parent.
	 * The CTableTree control is created using the SWT style bits <code>MULTI, H_SCROLL, V_SCROLL,</code> and <code>BORDER</code>.
	 * The viewer has no input, no content provider, a default label provider, 
	 * no sorter, and no filters.
	 *
	 * @param parent the parent control
	 */
	public CTableTreeViewer(Composite parent) {
		super(new CTableTree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER));
	}
	
	/**
	 * Creates a CTableTree viewer on a newly-created CTableTree control under the given parent.
	 * The CTableTree control is created using the given SWT style bits.
	 * The viewer has no input, no content provider, a default label provider, 
	 * no sorter, and no filters.
	 *
	 * @param parent the parent control
	 * @param style the SWT style bits
	 */
	public CTableTreeViewer(Composite parent, int style) {
		super(new CTableTree(parent, style));
	}
	
	/**
	 * 
	 * @param element
	 * @param index
	 */
	protected void createItem(Object element, int index) {
		Class[] classes = (cellProvider != null) ? cellProvider.getCellClasses(element) : null;
		CTableTreeItem item = null;
		
		IContentProvider cp = getContentProvider();
		if(cp != null && cp instanceof ITreeContentProvider) {
			Object parent = ((ITreeContentProvider) cp).getParent(element);
			if(parent != null) {
				Widget parentItem = findItem(parent);
				if(parentItem != null && parentItem instanceof CTableTreeItem) {
					item = new CTableTreeItem((CTableTreeItem) parentItem, SWT.NONE, index, classes);
				}
			}
		}
		if(item == null) item = new CTableTreeItem(getCTableTree(), SWT.NONE, index, classes);		
		updateItem(item, element);
	}

	protected void doUpdateCell(int index, CContainerCell cell, Object element, String[] properties) {
		super.doUpdateCell(index, cell, element, properties);
		
		IBaseLabelProvider prov = (IBaseLabelProvider) getLabelProvider();
		if(prov != null) {
			CTableTreeCell cttc = (CTableTreeCell) cell;
			String text = null;
			if(prov instanceof ICContainerLabelProvider) {
				cttc.setImages(((ICContainerLabelProvider) prov).getColumnImages(element, index));
				text = ((ITableLabelProvider) prov).getColumnText(element, index);
			} else if(prov instanceof ITableLabelProvider) {
				cttc.setImage(((ITableLabelProvider) prov).getColumnImage(element, index));
				text = ((ITableLabelProvider) prov).getColumnText(element, index);
			} else if(prov instanceof ILabelProvider) {
				cttc.setImage(((ILabelProvider) prov).getImage(element));
				text = ((ILabelProvider) prov).getText(element);
			}
			if(text == null) text = ""; //$NON-NLS-1$
			cttc.setText(text);
		}
	}
	
	public CTableTree getCTableTree() {
		return (CTableTree) container;
	}
	
	protected Object[] getRawChildren(Object parent) {
		Object[] result = null;
		if(parent != null) {
			IStructuredContentProvider cp = (IStructuredContentProvider) getContentProvider();
			if(cp != null) {
				if(cp instanceof ITreeContentProvider) {
					ITreeContentProvider tcp = (ITreeContentProvider) cp;
					// if Flat, must iteratively get all children and return them as one array
					//   so that the filters and sorters hit every element
					// if NOT Flat (Hierarchical) then only return the direct elements or children
					//   requested - the getSortedChildren method will compile all branches after
					//   being filtered
					if(getCTableTree().isFlat()) {
						Object[] oa;
						if(equals(parent, getRoot())) {
							oa = tcp.getElements(parent);
						} else {
							oa = tcp.getChildren(parent);
						}
						Set s = new HashSet(oa.length);
						for(int i = 0; i < oa.length; i++) {
							s.add(oa[i]);
							s.addAll(Arrays.asList(getRawChildren(oa[i])));
						}
						result = s.isEmpty() ? new Object[0] : s.toArray();
					} else {
						if(equals(parent, getRoot())) {
							result = tcp.getElements(parent);
						} else {
							result = tcp.getChildren(parent);
						}
					}
				} else {
					result = cp.getElements(parent);
				}
				assertElementsNotNull(result);
			}
		}
		return (result != null) ? result : new Object[0];
	}

	protected Object[] getSortedChildren(Object parent) {
		Object[] result = null;
		if (parent != null) {
			if(getCTableTree().isFlat()) {
				result = super.getSortedChildren(parent);
			} else {
				Object[] oa = getFilteredChildren(parent);
				if(getSorter() != null) {
					oa = (Object[]) oa.clone();
					getSorter().sort(this, oa);
				}
				List l = new ArrayList();
				for(int i = 0; i < oa.length; i++) {
					l.add(oa[i]);
					if(getContentProvider() instanceof ITreeContentProvider) {
						l.addAll(Arrays.asList(getSortedChildren(oa[i])));
					}
				}
				result = l.isEmpty() ? new Object[0] : l.toArray();
			}
		}
		return (result != null) ? result : new Object[0];
	}

	/**
	 * Refresh all of the elements of the table. update the
	 * labels if updatLabels is true;
	 * @param updateLabels
	 * 
	 * @since 3.1
	 */
	protected void internalRefreshAll(boolean updateLabels) {
		Object[] expanded = getExpandedElements(true);
		if(treeColumn == getCTableTree().getTreeColumn()) {
			super.internalRefreshAll(updateLabels);
		} else {
			Object[] children = getSortedChildren(getRoot());
			CContainerItem[] items = getCTableTree().getItems();
			treeColumn = getCTableTree().getTreeColumn();
			for(int i = 0; i < items.length; i++) {
				Object data = items[i].getData();
				if(data != null) {
					disassociate(items[i]);
				}
			}
			container.removeAll();
			add(children);
		}
		setExpandedElements(expanded, true);
	}

	/**
	 * not yet implemented
	 * <p>post a feature request if you need it enabled</p>
	 */
	public void setItemCount(int count) {
		// TODO Auto-generated method stub
	}

	/**
	 * not yet implemented
	 * <p>post a feature request if you need it enabled</p>
	 */
	public void clear(int index) {
		// TODO Auto-generated method stub
	}

	/**
	 * not yet implemented
	 * <p>post a feature request if you need it enabled</p>
	 */
	public void setChildCount(Object element, int count) {
		// TODO Auto-generated method stub
	}

	/**
	 * not yet implemented
	 * <p>post a feature request if you need it enabled</p>
	 */
	public void replace(Object parent, int index, Object element) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * not yet implemented
	 * <p>post a feature request if you need it enabled</p>
	 */
	public boolean isExpandable(Object element) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * not yet implemented
	 * <p>post a feature request if you need it enabled</p>
	 */
	public void add(Object parentElement, Object[] childElements) {
		// TODO Auto-generated method stub
	}

	/**
	 * not yet implemented
	 * <p>post a feature request if you need it enabled</p>
	 */
	public void add(Object parentElement, Object childElement) {
		// TODO Auto-generated method stub
	}

	/**
	 * not yet implemented
	 * <p>post a feature request if you need it enabled</p>
	 */
	public void addTreeListener(ITreeViewerListener listener) {
		// TODO Auto-generated method stub
	}

	public void collapseAll() {
		CContainerItem[] items = container.getItems();
		for(int i = 0; i < items.length; i++) {
			if(((CTableTreeItem) items[i]).getExpanded()) {
				((CTableTreeItem) items[i]).setExpanded(false);
			}
		}
	}

//	/**
//	 * If allCells is true, it affects all the item's cells, not just the Tree Cell
//	 * <p>If allCells is false, it simply call collapseAll()</p>
//	 * @param allCells
//	 * @see org.eclipse.jface.viewers.TreeViewer#collapseAll()
//	 */
//	public void collapseAll(boolean allCells) {
//		if(allCells) {
//			CTableTreeItem[] items = cTableTree.getItems();
//			for(int i = 0; i < items.length; i++) {
//				if(items[i].getExpanded(allCells)) 
//					items[i].setExpanded(false, allCells);
//			}
//		} else {
//			collapseAll();
//		}
//	}

	/**
	 * not yet implemented
	 * <p>post a feature request if you need it enabled</p>
	 */
	public void collapseToLevel(Object element, int level) {
		// TODO Auto-generated method stub
	}

	public void expandAll() {
		expandAll(false);
	}

	/**
	 * If allCells is true, it affects all the item's cells, not just the Tree Cell
	 * <p>If allCells is false, it simply call expandAll()</p>
	 * @param allCells
	 * @see org.eclipse.jface.viewers.TreeViewer#expandAll()
	 */
	public void expandAll(boolean allCells) {
		if(allCells) {
			CContainerItem[] items = container.getItems();
			for(int i = 0; i < items.length; i++) {
//				if(!items[i].getExpanded(allCells)) 
//					items[i].setExpanded(true, allCells);
			}
		} else {
			expandAll();
		}
	}

	/**
	 * not yet implemented
	 * <p>post a feature request if you need it enabled</p>
	 */
	public void expandToLevel(int level) {
		// TODO Auto-generated method stub
	}

	/**
	 * not yet implemented
	 * <p>post a feature request if you need it enabled</p>
	 */
	public void expandToLevel(Object element, int level) {
		// TODO Auto-generated method stub
	}

	/**
	 * not yet implemented
	 * <p>post a feature request if you need it enabled</p>
	 */
	public int getAutoExpandLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Object[] getExpandedElements() {
		return getExpandedElements(false);
	}
	/**
	 * If allCells is true, it affects all the item's cells, not just the Tree Cell
	 * <p>If allCells is false, it is the same as getExpandedState(Object)</p>
	 * @param element
	 * @param allCells
	 * @return
	 * @see org.eclipse.jface.viewers.TreeViewer#getExpandedState(Object)
	 */
	public Object[] getExpandedElements(boolean allCells) {
		List l = new ArrayList(Arrays.asList(container.getItems()));
//		for(Iterator i = l.iterator(); i.hasNext(); ) {
//			CTableTreeItem item = ((CTableTreeItem) i.next());
//			if(!item.getExpanded(allCells)) i.remove();
//		}
		Object[] elements = new Object[l.size()];
		for(int i = 0; i < elements.length; i++) {
			elements[i] = ((CTableTreeItem) l.get(i)).getData();
		}
		return elements;
	}

	public boolean getExpandedState(Object element) {
		return getExpandedState(element, false);
	}
	/**
	 * If allCells is true, it affects all the item's cells, not just the Tree Cell
	 * <p>If allCells is false, it is the same as getExpandedState(Object)</p>
	 * @param element
	 * @param allCells
	 * @return
	 * @see org.eclipse.jface.viewers.TreeViewer#getExpandedState(Object)
	 */
	public boolean getExpandedState(Object element, boolean allCells) {
//		CTableTreeItem item = (CTableTreeItem) findItem(element);
//		if(item != null) return item.getExpanded(allCells);
		return false;
	}

	/**
	 * not yet implemented
	 * <p>post a feature request if you need it enabled</p>
	 */
	public void removeTreeListener(ITreeViewerListener listener) {
		// TODO Auto-generated method stub
	}

	/**
	 * not yet implemented
	 * <p>post a feature request if you need it enabled</p>
	 */
	public void setAutoExpandLevel(int level) {
		// TODO Auto-generated method stub
	}

	public void setExpandedElements(Object[] elements) {
		setExpandedElements(elements, false);
	}
	public void setExpandedElements(Object[] elements, boolean allCells) {
//		CTableTreeItem[] items = cTableTree.getItems();
//		for(int i = 0; i < items.length; i++) {
//			items[i].setExpanded(false, allCells);
//		}
//		for(int i = 0; i < elements.length; i++) {
//			CTableTreeItem item = (CTableTreeItem) findItem(elements[i]);
//			if(item != null) item.setExpanded(true, allCells);
//		}
	}

	public void setExpandedState(Object element, boolean expanded) {
		setExpandedState(element, expanded, false);
	}

	public void setExpandedState(Object element, boolean expanded, boolean allCells) {
//		CTableTreeItem item = (CTableTreeItem) findItem(element);
//		if(item != null) item.setExpanded(expanded, allCells);
	}

	public Object[] getVisibleExpandedElements() {
		List l = new ArrayList(Arrays.asList(container.getItems()));
		for(Iterator i = l.iterator(); i.hasNext(); ) {
			CTableTreeItem item = ((CTableTreeItem) i.next());
			if(!getCTableTree().isVisible(item)) i.remove();
		}
		Object[] elements = new Object[l.size()];
		for(int i = 0; i < elements.length; i++) {
			elements[i] = ((CTableTreeItem) l.get(i)).getData();
		}
		return elements;
	}
}

