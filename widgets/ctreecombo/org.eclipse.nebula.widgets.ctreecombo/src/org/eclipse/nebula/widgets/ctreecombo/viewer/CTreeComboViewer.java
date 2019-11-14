/*******************************************************************************
 * Copyright (c) 2019 Thomas Schindl & Laurent Caron.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * - Thomas Schindl (tom dot schindl at bestsolution dot at) - initial API
 * and implementation
 * - Laurent Caron (laurent dot caron at gmail dot com) - Integration to Nebula,
 * code cleaning and documentation
 *******************************************************************************/
package org.eclipse.nebula.widgets.ctreecombo.viewer;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.util.Policy;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.ILazyTreePathContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.nebula.widgets.ctreecombo.CTreeCombo;
import org.eclipse.nebula.widgets.ctreecombo.CTreeComboItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;

/**
 * A concrete viewer based either on an SWT <code>CTreeCombo</code> control.
 */
public class CTreeComboViewer extends AbstractTreeViewer {

	private static final String VIRTUAL_DISPOSE_KEY = Policy.JFACE + ".DISPOSE_LISTENER"; //$NON-NLS-1$

	/**
	 * This viewer's control.
	 */
	private CTreeCombo tree;

	/**
	 * Flag for whether the tree has been disposed of.
	 */
	private boolean treeIsDisposed = false;

	private boolean contentProviderIsLazy;

	private boolean contentProviderIsTreeBased;

	/**
	 * The row object reused
	 */
	private CTreeComboViewerRow cachedRow;

	/**
	 * true if we are inside a preservingSelection() call
	 */
	private boolean preservingSelection;

	/**
	 * Creates a tree viewer on a newly-created tree control under the given
	 * parent. The tree control is created using the SWT style bits
	 * <code>MULTI, H_SCROLL, V_SCROLL,</code> and <code>BORDER</code>. The
	 * viewer has no input, no content provider, a default label provider, no
	 * sorter, and no filters.
	 * 
	 * @param parent
	 *            the parent control
	 */
	public CTreeComboViewer(Composite parent) {
		this(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
	}

	/**
	 * Creates a CTreeComboViewer viewer on a newly-created tree control under the given
	 * parent. The CTreeCombo control is created using the given SWT style bits. The
	 * viewer has no input, no content provider, a default label provider, no
	 * sorter, and no filters.
	 * 
	 * @param parent
	 *            the parent control
	 * @param style
	 *            the SWT style bits used to create the tree.
	 */
	public CTreeComboViewer(Composite parent, int style) {
		this(new CTreeCombo(parent, style));
	}

	/**
	 * Creates a CTreeComboViewer viewer on the given tree control. The viewer has no input,
	 * no content provider, a default label provider, no sorter, and no filters.
	 * 
	 * @param tree
	 *            the tree control
	 */
	public CTreeComboViewer(CTreeCombo tree) {
		super();
		this.tree = tree;
		hookControl(tree);
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#addTreeListener(org.eclipse.swt.widgets.Control, org.eclipse.swt.events.TreeListener)
	 */
	protected void addTreeListener(Control c, TreeListener listener) {
		((CTreeCombo) c).addTreeListener(listener);
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#getColumnViewerOwner(int)
	 */
	protected Widget getColumnViewerOwner(int columnIndex) {
		if (columnIndex < 0 || (columnIndex > 0 && columnIndex >= getTree().getColumnCount())) {
			return null;
		}

		if (getTree().getColumnCount() == 0)
			return getTree();

		return getTree().getColumn(columnIndex);
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#getChildren(org.eclipse.swt.widgets.Widget)
	 */
	protected Item[] getChildren(Widget o) {
		if (o instanceof CTreeComboItem) {
			return ((CTreeComboItem) o).getItems();
		}
		if (o instanceof CTreeCombo) {
			return ((CTreeCombo) o).getItems();
		}
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.Viewer#getControl()
	 */
	public Control getControl() {
		return tree;
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#getExpanded(org.eclipse.swt.widgets.Item)
	 */
	protected boolean getExpanded(Item item) {
		return ((CTreeComboItem) item).getExpanded();
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#getItemAt(org.eclipse.swt.graphics.Point)
	 */
	protected Item getItemAt(Point p) {
		CTreeComboItem[] selection = tree.getSelection();

		if (selection.length == 1) {
			int columnCount = tree.getColumnCount();

			for (int i = 0; i < columnCount; i++) {
				if (selection[0].getBounds(i).contains(p)) {
					return selection[0];
				}
			}
		}

		return getTree().getItem(p);
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#getItemCount(org.eclipse.swt.widgets.Control)
	 */
	protected int getItemCount(Control widget) {
		return ((CTreeCombo) widget).getItemCount();
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#getItemCount(org.eclipse.swt.widgets.Item)
	 */
	protected int getItemCount(Item item) {
		return ((CTreeComboItem) item).getItemCount();
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#getItems(org.eclipse.swt.widgets.Item)
	 */
	protected Item[] getItems(Item item) {
		return ((CTreeComboItem) item).getItems();
	}

	/**
	 * @see org.eclipse.jface.viewers.ContentViewer#getLabelProvider()
	 */
	public IBaseLabelProvider getLabelProvider() {
		return super.getLabelProvider();
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#getParentItem(org.eclipse.swt.widgets.Item)
	 */
	protected Item getParentItem(Item item) {
		return ((CTreeComboItem) item).getParentItem();
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#getSelection(org.eclipse.swt.widgets.Control)
	 */
	protected Item[] getSelection(Control widget) {
		return ((CTreeCombo) widget).getSelection();
	}

	/**
	 * Returns this CTreeCombo viewer's tree control.
	 * 
	 * @return the ctreecombo control
	 */
	public CTreeCombo getTree() {
		return tree;
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#hookControl(org.eclipse.swt.widgets.Control)
	 */
	protected void hookControl(Control control) {
		super.hookControl(control);
		CTreeCombo treeControl = (CTreeCombo) control;

		if ((treeControl.getStyle() & SWT.VIRTUAL) != 0) {
			treeControl.addListener(SWT.Dispose, (e) -> {
				treeIsDisposed = true;
				unmapAllElements();
			});
			treeControl.addListener(SWT.SetData, (event) -> {
				if (contentProviderIsLazy) {
					CTreeComboItem item = (CTreeComboItem) event.item;
					CTreeComboItem parentItem = item.getParentItem();
					int index = event.index;
					virtualLazyUpdateWidget(parentItem == null ? (Widget) getTree() : parentItem, index);
				}
			});
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#createViewerEditor()
	 */
	protected ColumnViewerEditor createViewerEditor() {
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#newItem(org.eclipse.swt.widgets.Widget, int, int)
	 */
	protected Item newItem(Widget parent, int flags, int ix) {
		CTreeComboItem item;

		if (parent instanceof CTreeComboItem) {
			item = (CTreeComboItem) createNewRowPart(getViewerRowFromItem(parent), flags, ix).getItem();
		} else {
			item = (CTreeComboItem) createNewRowPart(null, flags, ix).getItem();
		}

		return item;
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#removeAll(org.eclipse.swt.widgets.Control)
	 */
	protected void removeAll(Control widget) {
		((CTreeCombo) widget).removeAll();
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#setExpanded(org.eclipse.swt.widgets.Item, boolean)
	 */
	protected void setExpanded(Item node, boolean expand) {
		((CTreeComboItem) node).setExpanded(expand);
		if (contentProviderIsLazy) {
			// force repaints to happen
			getControl().update();
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#setSelection(java.util.List)
	 */
	protected void setSelection(List<Item> items) {
		Item[] current = getSelection(getTree());

		// Don't bother resetting the same selection
		if (isSameSelection(items, current)) {
			return;
		}

		CTreeComboItem[] newItems = new CTreeComboItem[items.size()];
		items.toArray(newItems);
		getTree().setSelection(newItems);
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#showItem(org.eclipse.swt.widgets.Item)
	 */
	protected void showItem(Item item) {
		getTree().showItem((CTreeComboItem) item);
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#getChild(org.eclipse.swt.widgets.Widget, int)
	 */
	protected Item getChild(Widget widget, int index) {
		if (widget instanceof CTreeComboItem) {
			return ((CTreeComboItem) widget).getItem(index);
		}
		if (widget instanceof CTreeCombo) {
			return ((CTreeCombo) widget).getItem(index);
		}
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#assertContentProviderType(org.eclipse.jface.viewers.IContentProvider)
	 */
	protected void assertContentProviderType(IContentProvider provider) {
		if (provider instanceof ILazyTreeContentProvider || provider instanceof ILazyTreePathContentProvider) {
			return;
		}
		super.assertContentProviderType(provider);
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#getRawChildren(java.lang.Object)
	 */
	protected Object[] getRawChildren(Object parent) {
		if (contentProviderIsLazy) {
			return new Object[0];
		}
		return super.getRawChildren(parent);
	}

	/**
	 * For a CTreeComboViewer with a ctreecombo with the VIRTUAL style bit set, set the
	 * number of children of the given element or tree path. To set the number
	 * of children of the invisible root of the tree, you can pass the input
	 * object or an empty tree path.
	 * 
	 * @param elementOrTreePath
	 *            the element, or tree path
	 * @param count
	 */
	public void setChildCount(final Object elementOrTreePath, final int count) {
		if (checkBusy())
			return;
		preservingSelection(new Runnable() {
			public void run() {
				if (internalIsInputOrEmptyPath(elementOrTreePath)) {
					getTree().setItemCount(count);
					return;
				}
				Widget[] items = internalFindItems(elementOrTreePath);
				for (int i = 0; i < items.length; i++) {
					CTreeComboItem treeItem = (CTreeComboItem) items[i];
					treeItem.setItemCount(count);
				}
			}
		});
	}

	/**
	 * For a CTreeComboViewer with a ctreecombo with the VIRTUAL style bit set, replace the
	 * given parent's child at index with the given element. If the given parent
	 * is this viewer's input or an empty tree path, this will replace the root
	 * element at the given index.
	 * <p>
	 * This method should be called by implementers of ILazyTreeContentProvider
	 * to populate this viewer.
	 * </p>
	 * 
	 * @param parentElementOrTreePath
	 *            the parent of the element that should be updated, or the tree
	 *            path to that parent
	 * @param index
	 *            the index in the parent's children
	 * @param element
	 *            the new element
	 * 
	 * @see #setChildCount(Object, int)
	 * @see ILazyTreeContentProvider
	 * @see ILazyTreePathContentProvider
	 */
	public void replace(final Object parentElementOrTreePath, final int index, final Object element) {
		if (checkBusy())
			return;
		Item[] selectedItems = getSelection(getControl());
		TreeSelection selection = (TreeSelection) getSelection();
		Widget[] itemsToDisassociate;
		if (parentElementOrTreePath instanceof TreePath) {
			TreePath elementPath = ((TreePath) parentElementOrTreePath).createChildPath(element);
			itemsToDisassociate = internalFindItems(elementPath);
		} else {
			itemsToDisassociate = internalFindItems(element);
		}
		if (internalIsInputOrEmptyPath(parentElementOrTreePath)) {
			if (index < tree.getItemCount()) {
				CTreeComboItem item = tree.getItem(index);
				selection = adjustSelectionForReplace(selectedItems, selection, item, element, getRoot());
				// disassociate any different item that represents the
				// same element under the same parent (the tree)
				for (int i = 0; i < itemsToDisassociate.length; i++) {
					if (itemsToDisassociate[i] instanceof CTreeComboItem) {
						CTreeComboItem itemToDisassociate = (CTreeComboItem) itemsToDisassociate[i];
						if (itemToDisassociate != item && itemToDisassociate.getParentItem() == null) {
							int indexToDisassociate = getTree().indexOf(itemToDisassociate);
							disassociate(itemToDisassociate);
							getTree().clear(indexToDisassociate, true);
						}
					}
				}
				Object oldData = item.getData();
				updateItem(item, element);
				if (!CTreeComboViewer.this.equals(oldData, element)) {
					item.clearAll(true);
				}
			}
		} else {
			Widget[] parentItems = internalFindItems(parentElementOrTreePath);
			for (int i = 0; i < parentItems.length; i++) {
				CTreeComboItem parentItem = (CTreeComboItem) parentItems[i];
				if (index < parentItem.getItemCount()) {
					CTreeComboItem item = parentItem.getItem(index);
					selection = adjustSelectionForReplace(selectedItems, selection, item, element, parentItem.getData());
					// disassociate any different item that represents the
					// same element under the same parent (the tree)
					for (int j = 0; j < itemsToDisassociate.length; j++) {
						if (itemsToDisassociate[j] instanceof CTreeComboItem) {
							CTreeComboItem itemToDisassociate = (CTreeComboItem) itemsToDisassociate[j];
							if (itemToDisassociate != item && itemToDisassociate.getParentItem() == parentItem) {
								int indexToDisaccociate = parentItem.indexOf(itemToDisassociate);
								disassociate(itemToDisassociate);
								parentItem.clear(indexToDisaccociate, true);
							}
						}
					}
					Object oldData = item.getData();
					updateItem(item, element);
					if (!CTreeComboViewer.this.equals(oldData, element)) {
						item.clearAll(true);
					}
				}
			}
		}
		// Restore the selection if we are not already in a nested
		// preservingSelection:
		if (!preservingSelection) {
			setSelectionToWidget(selection, false);
			// send out notification if old and new differ
			ISelection newSelection = getSelection();
			if (!newSelection.equals(selection)) {
				handleInvalidSelection(selection, newSelection);
			}
		}
	}

	private TreeSelection adjustSelectionForReplace(Item[] selectedItems, TreeSelection selection, CTreeComboItem item, Object element, Object parentElement) {
		if (item.getData() != null || selectedItems.length == selection.size() || parentElement == null) {
			// Don't do anything - we are not seeing an instance of bug 185673
			return selection;
		}
		for (int i = 0; i < selectedItems.length; i++) {
			if (item == selectedItems[i]) {
				// The current item was selected, but its data is null.
				// The data will be replaced by the given element, so to keep
				// it selected, we have to add it to the selection.
				TreePath[] originalPaths = selection.getPaths();
				int length = originalPaths.length;
				TreePath[] paths = new TreePath[length + 1];
				System.arraycopy(originalPaths, 0, paths, 0, length);
				// set the element temporarily so that we can call
				// getTreePathFromItem
				item.setData(element);
				paths[length] = getTreePathFromItem(item);
				item.setData(null);
				return new TreeSelection(paths, selection.getElementComparer());
			}
		}
		// The item was not selected, return the given selection
		return selection;
	}

	/** 
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#isExpandable(java.lang.Object)
	 */
	public boolean isExpandable(Object element) {
		if (contentProviderIsLazy) {
			CTreeComboItem treeItem = (CTreeComboItem) internalExpand(element, false);
			if (treeItem == null) {
				return false;
			}
			virtualMaterializeItem(treeItem);
			return treeItem.getItemCount() > 0;
		}
		return super.isExpandable(element);
	}

	/** 
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#getParentElement(java.lang.Object)
	 */
	protected Object getParentElement(Object element) {
		boolean oldBusy = isBusy();
		setBusy(true);
		try {
			if (contentProviderIsLazy && !contentProviderIsTreeBased && !(element instanceof TreePath)) {
				ILazyTreeContentProvider lazyTreeContentProvider = (ILazyTreeContentProvider) getContentProvider();
				return lazyTreeContentProvider.getParent(element);
			}
			if (contentProviderIsLazy && contentProviderIsTreeBased && !(element instanceof TreePath)) {
				ILazyTreePathContentProvider lazyTreePathContentProvider = (ILazyTreePathContentProvider) getContentProvider();
				TreePath[] parents = lazyTreePathContentProvider.getParents(element);
				if (parents != null && parents.length > 0) {
					return parents[0];
				}
			}
			return super.getParentElement(element);
		} finally {
			setBusy(oldBusy);
		}
	}

	/** 
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#createChildren(org.eclipse.swt.widgets.Widget)
	 */
	protected void createChildren(Widget widget) {
		if (contentProviderIsLazy) {
			Object element = widget.getData();
			if (element == null && widget instanceof CTreeComboItem) {
				// parent has not been materialized
				virtualMaterializeItem((CTreeComboItem) widget);
				// try getting the element now that updateElement was called
				element = widget.getData();
			}
			if (element == null) {
				// give up because the parent is still not materialized
				return;
			}
			Item[] children = getChildren(widget);
			if (children.length == 1 && children[0].getData() == null) {
				// found a dummy node
				virtualLazyUpdateChildCount(widget, children.length);
				children = getChildren(widget);
			}
			// touch all children to make sure they are materialized
			for (int i = 0; i < children.length; i++) {
				if (children[i].getData() == null) {
					virtualLazyUpdateWidget(widget, i);
				}
			}
			return;
		}
		super.createChildren(widget);
	}

	/** 
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#internalAdd(org.eclipse.swt.widgets.Widget, java.lang.Object, java.lang.Object[])
	 */
	protected void internalAdd(Widget widget, Object parentElement, Object[] childElements) {
		if (contentProviderIsLazy) {
			if (widget instanceof CTreeComboItem) {
				CTreeComboItem ti = (CTreeComboItem) widget;
				int count = ti.getItemCount() + childElements.length;
				ti.setItemCount(count);
				ti.clearAll(false);
			} else {
				CTreeCombo t = (CTreeCombo) widget;
				t.setItemCount(t.getItemCount() + childElements.length);
				t.clearAll(false);
			}
			return;
		}
		super.internalAdd(widget, parentElement, childElements);
	}

	private void virtualMaterializeItem(CTreeComboItem treeItem) {
		if (treeItem.getData() != null) {
			// already materialized
			return;
		}
		if (!contentProviderIsLazy) {
			return;
		}
		int index;
		Widget parent = treeItem.getParentItem();
		if (parent == null) {
			parent = treeItem.getParent();
		}
		Object parentElement = parent.getData();
		if (parentElement != null) {
			if (parent instanceof CTreeCombo) {
				index = ((CTreeCombo) parent).indexOf(treeItem);
			} else {
				index = ((CTreeComboItem) parent).indexOf(treeItem);
			}
			virtualLazyUpdateWidget(parent, index);
		}
	}

	/** 
	 * @see org.eclipse.jface.viewers.StructuredViewer#mapElement(java.lang.Object, org.eclipse.swt.widgets.Widget)
	 */
	protected void mapElement(Object element, final Widget item) {
		super.mapElement(element, item);
		// make sure to unmap elements if the tree is virtual
		if ((getTree().getStyle() & SWT.VIRTUAL) != 0) {
			// only add a dispose listener if item hasn't already on assigned
			// because it is reused
			if (item.getData(VIRTUAL_DISPOSE_KEY) == null) {
				item.setData(VIRTUAL_DISPOSE_KEY, Boolean.TRUE);
				item.addDisposeListener(new DisposeListener() {
					public void widgetDisposed(DisposeEvent e) {
						if (!treeIsDisposed) {
							Object data = item.getData();
							if (usingElementMap() && data != null) {
								unmapElement(data, item);
							}
						}
					}
				});
			}
		}
	}

	/** 
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#getViewerRowFromItem(org.eclipse.swt.widgets.Widget)
	 */
	protected ViewerRow getViewerRowFromItem(Widget item) {
		if (cachedRow == null) {
			cachedRow = new CTreeComboViewerRow((CTreeComboItem) item);
		} else {
			cachedRow.setItem((CTreeComboItem) item);
		}

		return cachedRow;
	}

	/**
	 * Create a new ViewerRow at rowIndex
	 * 
	 * @param parent
	 * @param style
	 * @param rowIndex
	 * @return ViewerRow
	 */
	private ViewerRow createNewRowPart(ViewerRow parent, int style, int rowIndex) {
		if (parent == null) {
			if (rowIndex >= 0) {
				return getViewerRowFromItem(new CTreeComboItem(tree, style, rowIndex));
			}
			return getViewerRowFromItem(new CTreeComboItem(tree, style));
		}

		if (rowIndex >= 0) {
			return getViewerRowFromItem(new CTreeComboItem((CTreeComboItem) parent.getItem(), SWT.NONE, rowIndex));
		}

		return getViewerRowFromItem(new CTreeComboItem((CTreeComboItem) parent.getItem(), SWT.NONE));
	}

	/** 
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#internalInitializeTree(org.eclipse.swt.widgets.Control)
	 */
	protected void internalInitializeTree(Control widget) {
		if (contentProviderIsLazy) {
			if (widget instanceof CTreeCombo && widget.getData() != null) {
				virtualLazyUpdateChildCount(widget, 0);
				return;
			}
		}
		super.internalInitializeTree(tree);
	}

	/** 
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#updatePlus(org.eclipse.swt.widgets.Item, java.lang.Object)
	 */
	protected void updatePlus(Item item, Object element) {
		if (contentProviderIsLazy) {
			Object data = item.getData();
			int itemCount = 0;
			if (data != null) {
				// item is already materialized
				itemCount = ((CTreeComboItem) item).getItemCount();
			}
			virtualLazyUpdateHasChildren(item, itemCount);
		} else {
			super.updatePlus(item, element);
		}
	}

	/**
	 * Removes the element at the specified index of the parent. The selection
	 * is updated if required.
	 * 
	 * @param parentOrTreePath
	 *            the parent element, the input element, or a tree path to the
	 *            parent element
	 * @param index
	 *            child index
	 */
	public void remove(final Object parentOrTreePath, final int index) {
		if (checkBusy())
			return;
		final List<TreePath> oldSelection = new LinkedList<TreePath>(Arrays.asList(((TreeSelection) getSelection()).getPaths()));
		preservingSelection(new Runnable() {
			public void run() {
				TreePath removedPath = null;
				if (internalIsInputOrEmptyPath(parentOrTreePath)) {
					CTreeCombo tree = (CTreeCombo) getControl();
					if (index < tree.getItemCount()) {
						CTreeComboItem item = tree.getItem(index);
						if (item.getData() != null) {
							removedPath = getTreePathFromItem(item);
							disassociate(item);
						}
						item.dispose();
					}
				} else {
					Widget[] parentItems = internalFindItems(parentOrTreePath);
					for (int i = 0; i < parentItems.length; i++) {
						CTreeComboItem parentItem = (CTreeComboItem) parentItems[i];
						if (parentItem.isDisposed())
							continue;
						if (index < parentItem.getItemCount()) {
							CTreeComboItem item = parentItem.getItem(index);
							if (item.getData() != null) {
								removedPath = getTreePathFromItem(item);
								disassociate(item);
							}
							item.dispose();
						}
					}
				}
				if (removedPath != null) {
					boolean removed = false;
					for (Iterator<TreePath> it = oldSelection.iterator(); it.hasNext();) {
						TreePath path = (TreePath) it.next();
						if (path.startsWith(removedPath, getComparer())) {
							it.remove();
							removed = true;
						}
					}
					if (removed) {
						setSelection(new TreeSelection((TreePath[]) oldSelection.toArray(new TreePath[oldSelection.size()]), getComparer()), false);
					}

				}
			}
		});
	}

	/** 
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#handleTreeExpand(org.eclipse.swt.events.TreeEvent)
	 */
	protected void handleTreeExpand(TreeEvent event) {
		if (contentProviderIsLazy) {
			if (event.item.getData() != null) {
				Item[] children = getChildren(event.item);
				if (children.length == 1 && children[0].getData() == null) {
					// we have a dummy child node, ask for an updated child
					// count
					virtualLazyUpdateChildCount(event.item, children.length);
				}
				fireTreeExpanded(new TreeExpansionEvent(this, event.item.getData()));
			}
			return;
		}
		super.handleTreeExpand(event);
	}

	/** 
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#setContentProvider(org.eclipse.jface.viewers.IContentProvider)
	 */
	public void setContentProvider(IContentProvider provider) {
		contentProviderIsLazy = (provider instanceof ILazyTreeContentProvider) || (provider instanceof ILazyTreePathContentProvider);
		contentProviderIsTreeBased = provider instanceof ILazyTreePathContentProvider;
		super.setContentProvider(provider);
	}

	/**
	 * For a TreeViewer with a tree with the VIRTUAL style bit set, inform the
	 * viewer about whether the given element or tree path has children. Avoid
	 * calling this method if the number of children has already been set.
	 * 
	 * @param elementOrTreePath
	 *            the element, or tree path
	 * @param hasChildren
	 * 
	 */
	public void setHasChildren(final Object elementOrTreePath, final boolean hasChildren) {
		if (checkBusy())
			return;
		preservingSelection(new Runnable() {
			public void run() {
				if (internalIsInputOrEmptyPath(elementOrTreePath)) {
					if (hasChildren) {
						virtualLazyUpdateChildCount(getTree(), getChildren(getTree()).length);
					} else {
						setChildCount(elementOrTreePath, 0);
					}
					return;
				}
				Widget[] items = internalFindItems(elementOrTreePath);
				for (int i = 0; i < items.length; i++) {
					CTreeComboItem item = (CTreeComboItem) items[i];
					if (!hasChildren) {
						item.setItemCount(0);
					} else {
						if (!item.getExpanded()) {
							item.setItemCount(1);
							CTreeComboItem child = item.getItem(0);
							if (child.getData() != null) {
								disassociate(child);
							}
							item.clear(0, true);
						} else {
							virtualLazyUpdateChildCount(item, item.getItemCount());
						}
					}
				}
			}
		});
	}

	/**
	 * Update the widget at index.
	 * 
	 * @param widget
	 * @param index
	 */
	private void virtualLazyUpdateWidget(Widget widget, int index) {
		boolean oldBusy = isBusy();
		setBusy(false);
		try {
			if (contentProviderIsTreeBased) {
				TreePath treePath;
				if (widget instanceof Item) {
					if (widget.getData() == null) {
						return;
					}
					treePath = getTreePathFromItem((Item) widget);
				} else {
					treePath = TreePath.EMPTY;
				}
				((ILazyTreePathContentProvider) getContentProvider()).updateElement(treePath, index);
			} else {
				((ILazyTreeContentProvider) getContentProvider()).updateElement(widget.getData(), index);
			}
		} finally {
			setBusy(oldBusy);
		}
	}

	/**
	 * Update the child count
	 * 
	 * @param widget
	 * @param currentChildCount
	 */
	private void virtualLazyUpdateChildCount(Widget widget, int currentChildCount) {
		boolean oldBusy = isBusy();
		setBusy(false);
		try {
			if (contentProviderIsTreeBased) {
				TreePath treePath;
				if (widget instanceof Item) {
					treePath = getTreePathFromItem((Item) widget);
				} else {
					treePath = TreePath.EMPTY;
				}
				((ILazyTreePathContentProvider) getContentProvider()).updateChildCount(treePath, currentChildCount);
			} else {
				((ILazyTreeContentProvider) getContentProvider()).updateChildCount(widget.getData(), currentChildCount);
			}
		} finally {
			setBusy(oldBusy);
		}
	}

	/**
	 * Update the item with the current child count.
	 * 
	 * @param item
	 * @param currentChildCount
	 */
	private void virtualLazyUpdateHasChildren(Item item, int currentChildCount) {
		boolean oldBusy = isBusy();
		setBusy(false);
		try {
			if (contentProviderIsTreeBased) {
				TreePath treePath;
				treePath = getTreePathFromItem(item);
				if (currentChildCount == 0) {
					// item is not expanded (but may have a plus currently)
					((ILazyTreePathContentProvider) getContentProvider()).updateHasChildren(treePath);
				} else {
					((ILazyTreePathContentProvider) getContentProvider()).updateChildCount(treePath, currentChildCount);
				}
			} else {
				((ILazyTreeContentProvider) getContentProvider()).updateChildCount(item.getData(), currentChildCount);
			}
		} finally {
			setBusy(oldBusy);
		}
	}

	/** 
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#disassociate(org.eclipse.swt.widgets.Item)
	 */
	protected void disassociate(Item item) {
		if (contentProviderIsLazy) {
			// avoid causing a callback:
			item.setText(" "); //$NON-NLS-1$
		}
		super.disassociate(item);
	}

	/** 
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#doGetColumnCount()
	 */
	protected int doGetColumnCount() {
		return tree.getColumnCount();
	}

	/**
	 * Sets a new selection for this viewer and optionally makes it visible.
	 * <p>
	 * <b>Currently the <code>reveal</code> parameter is not honored because
	 * {@link Tree} does not provide an API to only select an item without
	 * scrolling it into view</b>
	 * </p>
	 * 
	 * @param selection
	 *            the new selection
	 * @param reveal
	 *            <code>true</code> if the selection is to be made visible, and
	 *            <code>false</code> otherwise
	 */
	public void setSelection(ISelection selection, boolean reveal) {
		super.setSelection(selection, reveal);
	}

	/** 
	 * @see org.eclipse.jface.viewers.ColumnViewer#editElement(java.lang.Object, int)
	 */
	public void editElement(Object element, int column) {
		if (element instanceof TreePath) {
			try {
				getControl().setRedraw(false);
				setSelection(new TreeSelection((TreePath) element));
				CTreeComboItem[] items = tree.getSelection();

				if (items.length == 1) {
					ViewerRow row = getViewerRowFromItem(items[0]);

					if (row != null) {
						ViewerCell cell = row.getCell(column);
						if (cell != null) {
							triggerEditorActivationEvent(new ColumnViewerEditorActivationEvent(cell));
						}
					}
				}
			} finally {
				getControl().setRedraw(true);
			}
		} else {
			super.editElement(element, column);
		}
	}
}
