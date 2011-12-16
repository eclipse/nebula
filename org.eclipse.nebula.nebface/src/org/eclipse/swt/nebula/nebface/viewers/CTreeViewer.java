package org.eclipse.swt.nebula.nebface.viewers;

import java.util.List;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.AbstractViewerEditor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.nebula.widgets.ctree.AbstractColumn;
import org.eclipse.swt.nebula.widgets.ctree.CTree;
import org.eclipse.swt.nebula.widgets.ctree.CTreeColumn;
import org.eclipse.swt.nebula.widgets.ctree.CTreeEditor;
import org.eclipse.swt.nebula.widgets.ctree.CTreeItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Widget;

public class CTreeViewer extends AbstractTreeViewer {
	private CTree ctree;
	
	private CTreeEditor ctreeEditor;
	
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
	public CTreeViewer(Composite parent) {
		this(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
	}

	/**
	 * Creates a tree viewer on a newly-created tree control under the given
	 * parent. The tree control is created using the given SWT style bits. The
	 * viewer has no input, no content provider, a default label provider, no
	 * sorter, and no filters.
	 * 
	 * @param parent
	 *            the parent control
	 * @param style
	 *            the SWT style bits used to create the tree.
	 */
	public CTreeViewer(Composite parent, int style) {
		this(new CTree(parent, style));
	}

	/**
	 * Creates a tree viewer on the given tree control. The viewer has no input,
	 * no content provider, a default label provider, no sorter, and no filters.
	 * 
	 * @param tree
	 *            the tree control
	 */
	public CTreeViewer(CTree ctree) {
		super();
		this.ctree = ctree;
		ctreeEditor = new CTreeEditor(ctree);
		hookControl(ctree);
	}

	
	
	protected void addTreeListener(Control control, TreeListener listener) {
		((CTree)control).addTreeListener(listener);
	}

	protected Item[] getChildren(Widget o) {
		if (o instanceof CTreeItem) {
			return ((CTreeItem) o).getItems();
		}
		if (o instanceof CTree) {
			return ((CTree) o).getItems();
		}
		return null;
	}

	protected boolean getExpanded(Item item) {
		return ((CTreeItem) item).getExpanded();
	}

	protected int getItemCount(Control control) {
		return ctree.getItemCount();
	}

	protected int getItemCount(Item item) {
		return ((CTreeItem)item).getItems().length;
	}

	protected Item[] getItems(Item item) {
		return ((CTreeItem)item).getItems();
	}

	protected Item getParentItem(Item item) {
		return ((CTreeItem)item).getParentItem();
	}

	protected Item[] getSelection(Control control) {
		return ctree.getSelection();
	}

	protected Item newItem(Widget parent, int flags, int ix) {
		CTreeItem item;

		if (parent instanceof CTreeItem) {
			item = (CTreeItem) createNewRowPart(getViewerRowFromItem(parent),
					flags, ix).getItem();
		} else {
			item = (CTreeItem) createNewRowPart(null, flags, ix).getItem();
		}

		return item;
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
				return getViewerRowFromItem(new CTreeItem(ctree, style, rowIndex));
			}
			return getViewerRowFromItem(new CTreeItem(ctree, style));
		}

		if (rowIndex >= 0) {
			return getViewerRowFromItem(new CTreeItem((CTreeItem) parent.getItem(),
					SWT.NONE, rowIndex));
		}

		return getViewerRowFromItem(new CTreeItem((CTreeItem) parent.getItem(),
				SWT.NONE));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ColumnViewer#getRowPartFromItem(org.eclipse.swt.widgets.Widget)
	 */
	protected ViewerRow getViewerRowFromItem(Widget item) {
		ViewerRow part = (ViewerRow) item.getData(ViewerRow.ROWPART_KEY);

		if (part == null) {
			part = new CTreeViewerRow(((CTreeItem) item));
		}

		return part;
	}

	
	protected void removeAll(Control control) {
		ctree.removeAll();
	}

	protected void setExpanded(Item item, boolean expand) {
		((CTreeItem)item).setExpanded(expand);
	}

	protected void setSelection(List items) {
		Item[] current = getSelection(ctree);

		// Don't bother resetting the same selection
		if (isSameSelection(items, current)) {
			return;
		}

		CTreeItem[] newItems = new CTreeItem[items.size()];
		items.toArray(newItems);
		ctree.setSelection(newItems);
	}

	protected void showItem(Item item) {
		ctree.showItem((CTreeItem) item);
	}

	public Control getControl() {
		return ctree;
	}

	protected int doGetColumnCount() {
		return ctree.getColumnCount();
	}

	protected Item getChild(Widget widget, int index) {
		if (widget instanceof CTreeItem) {
			return ((CTreeItem) widget).getItem(index);
		}
		if (widget instanceof CTree) {
			return ctree.getItem(index);
		}
		return null;
	}

	protected Item getItemAt(Point p) {
		return ctree.getItem(p);
	}

	protected AbstractViewerEditor createViewerEditor() {
		return new AbstractViewerEditor(this) {

			protected StructuredSelection createSelection(Object element) {
				if (element instanceof TreePath) {
					return new TreeSelection((TreePath) element, getComparer());
				}

				return new StructuredSelection(element);
			}

			protected Item[] getSelection() {
				return ctree.getSelection();
			}

			protected void setEditor(Control w, Item item, int fColumnNumber) {
				ctreeEditor.setEditor(w, (CTreeItem) item, fColumnNumber);
			}

			protected void setLayoutData(LayoutData layoutData) {
				ctreeEditor.grabHorizontal = layoutData.grabHorizontal;
				ctreeEditor.horizontalAlignment = layoutData.horizontalAlignment;
				ctreeEditor.minimumWidth = layoutData.minimumWidth;
			}

			protected void showSelection() {
				ctree.showSelection();
			}

		};
	}

	public void remove(final Object parentOrTreePath, final int index) {
		preservingSelection(new Runnable() {
			public void run() {
				if (internalIsInputOrEmptyPath(parentOrTreePath)) {
					if (index < ctree.getItemCount()) {
						CTreeItem item = ctree.getItem(index);
						if (item.getData() != null) {
							disassociate(item);
						}
						item.dispose();
					}
				} else {
					Widget[] parentItems = internalFindItems(parentOrTreePath);
					for (int i = 0; i < parentItems.length; i++) {
						CTreeItem parentItem = (CTreeItem) parentItems[i];
						if (index < parentItem.getItems().length) {
							CTreeItem item = parentItem.getItem(index);
							if (item.getData() != null) {
								disassociate(item);
							}
							item.dispose();
						}
					}
				}
			}
		});
	}
	
	public CTree getCTree() {
		return ctree;
	}
	
	protected Widget getColumnViewerOwner(int columnIndex) {
		if (columnIndex < 0 || ( columnIndex > 0 && columnIndex >= ctree.getColumnCount() ) ) {
			return null;
		}

		AbstractColumn column = ctree.getColumn(columnIndex);
		if(column == null) return ctree; // Hang it off the table if necessary
		return column;
	}

}
