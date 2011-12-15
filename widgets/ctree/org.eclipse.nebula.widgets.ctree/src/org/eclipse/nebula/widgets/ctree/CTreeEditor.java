package org.eclipse.nebula.widgets.ctree;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

/**
 * A CTableTreeEditor is a manager for a Control that appears above a cell in a CTableTree and tracks with the
 * moving and resizing of that cell.  It can be used to display a text widget above a cell
 * in a CTableTree so that the user can edit the contents of that cell.  It can also be used to display
 * a button that can launch a dialog for modifying the contents of the associated cell.
 */
public class CTreeEditor extends ControlEditor {
	CTree tree;
	CTreeItem item;
	int column = 0;
	ControlListener columnListener;
	TreeListener treeListener;

	/**
	 * Creates a CTableTreeEditor for the specified CTableTree.
	 *
	 * @param tree the CTableTree Control above which this editor will be displayed
	 *
	 */
	public CTreeEditor (CTree tree) {
		super(tree);
		this.tree = tree;

		columnListener = new ControlListener() {
			public void controlMoved(ControlEvent e){
				layout();
			}
			public void controlResized(ControlEvent e){
				layout();
			}
		};
		treeListener = new TreeListener () {
			final Runnable runnable = new Runnable() {
				public void run() {
					Control editor = getEditor();
					if (editor == null || editor.isDisposed()) return;
					if (CTreeEditor.this.tree.isDisposed()) return;
					layout();
					editor.setVisible(true);
				}
			};
			public void treeCollapsed(TreeEvent e) {
				Control editor = getEditor();
				if (editor == null || editor.isDisposed ()) return;
				editor.setVisible(false);
				e.display.asyncExec(runnable);
			}
			public void treeExpanded(TreeEvent e) {
				Control editor = getEditor();
				if (editor == null || editor.isDisposed ()) return;
				editor.setVisible(false);
				e.display.asyncExec(runnable);
			}
		};
		tree.addTreeListener(treeListener);

		// To be consistent with older versions of SWT, grabVertical defaults to true
		grabVertical = true;
	}

	/**
	 * Removes all associations between the CTableTreeEditor and the row in the tree.  The
	 * tree and the editor Control are <b>not</b> disposed.
	 */
	public void dispose () {
		if (this.column > -1 && this.column < tree.getColumnCount()){
			CTreeColumn treeColumn = tree.getColumn(this.column);
			treeColumn.removeControlListener(columnListener);
		}
		columnListener = null;
		if (treeListener != null) 
			tree.removeTreeListener(treeListener);
		treeListener = null;
		tree = null;
		item = null;
		column = 0;
		super.dispose();
	}

	/**
	 * Returns the zero based index of the column of the cell being tracked by this editor.
	 *
	 * @return the zero based index of the column of the cell being tracked by this editor
	 *
	 * @since 3.1
	 */
	public int getColumn () {
		return column;
	}

	/**
	 * Returns the CTableTreeItem for the row of the cell being tracked by this editor.
	 *
	 * @return the CTableTreeItem for the row of the cell being tracked by this editor
	 */
	public CTreeItem getItem () {
		return item;
	}

	public void layout() {
		if (tree.isDisposed()) return;
		if (item == null || item.isDisposed()) return;	
		int columnCount = tree.getColumnCount();
		if (columnCount == 0 && column != 0) return;
		if (columnCount > 0 && (column < 0 || column >= columnCount)) return;
		Control editor = getEditor();
		if(editor == null || editor.isDisposed()) return;

		Rectangle cell = item.getCell(column).getBounds();
		Rectangle ca = item.getCell(column).getClientArea();
		cell.x += ca.x;
		cell.width = ca.width - 1;
		cell.y += 1;
		cell.height -= 2;
		int bwidth = getEditor().getBorderWidth();
		cell.x -= bwidth;
		cell.y -= bwidth;
		cell.width -= 2*bwidth;
		cell.height -= 2*bwidth;
		Image[] images = ((CTreeCell) item.getCell(column)).getImages();
		if(images.length > 0) {
			Image img = images[images.length-1];
			Rectangle rect = img == null ? new Rectangle(cell.x,cell.y,0,0) : img.getBounds();
			cell.x = rect.x + rect.width;
			cell.width -= rect.width;
		}
		Rectangle area = tree.getClientArea();
		if (cell.x < area.x + area.width) {
			if (cell.x + cell.width > area.x + area.width) {
				cell.width = area.x + area.width - cell.x;
			}
		}
		Rectangle editorRect = new Rectangle(cell.x, cell.y, minimumWidth, minimumHeight);

		if (grabHorizontal) {
			if (tree.getColumnCount() == 0) {
				// Bounds of tree item only include the text area - stretch out to include 
				// entire client area
				cell.width = area.x + area.width - cell.x;
			}
			editorRect.width = Math.max(cell.width, minimumWidth);
		}

		if (grabVertical) {
			editorRect.height = Math.max(cell.height, minimumHeight);
		}

		if (horizontalAlignment == SWT.RIGHT) {
			editorRect.x += cell.width - editorRect.width;
		} else if (horizontalAlignment == SWT.LEFT) {
			// do nothing - cell.x is the right answer
		} else { // default is CENTER
			editorRect.x += (cell.width - editorRect.width)/2;
		}
		// don't let the editor overlap with the +/- of the tree
		editorRect.x = Math.max(cell.x, editorRect.x);

		if (verticalAlignment == SWT.BOTTOM) {
			editorRect.y += cell.height - editorRect.height;
		} else if (verticalAlignment == SWT.TOP) {
			// do nothing - cell.y is the right answer
		} else { // default is CENTER
			editorRect.y += (cell.height - editorRect.height)/2;
		}

		if(editor == null || editor.isDisposed()) return;
		boolean hadFocus = editor.getVisible () && editor.isFocusControl();
		// this doesn't work because
		// resizing the column takes the focus away
		// before we get here
		editor.setBounds (editorRect);
		if(hadFocus) {
			if (editor == null || editor.isDisposed()) return;
			editor.setFocus ();
		}

		editor.moveAbove(null);
	}

	/**
	 * Sets the zero based index of the column of the cell being tracked by this editor.
	 * 
	 * @param column the zero based index of the column of the cell being tracked by this editor
	 *
	 * @since 3.1
	 */
	public void setColumn(int column) {
		int columnCount = tree.getColumnCount();
		// Separately handle the case where the tree has no CTableTreeColumns.
		// In this situation, there is a single default column.
		if (columnCount == 0) {
			this.column = (column == 0) ? 0 : -1;
			layout();
			return;
		}
		if (this.column > -1 && this.column < columnCount){
			CTreeColumn treeColumn = tree.getColumn(this.column);
			treeColumn.removeControlListener(columnListener);
			this.column = -1;
		}

		if (column < 0  || column >= tree.getColumnCount()) return;	

		this.column = column;
		CTreeColumn treeColumn = tree.getColumn(this.column);
		treeColumn.addControlListener(columnListener);
		layout();
	}

	/**
	 * Specify the Control that is to be displayed and the cell in the tree that it is to be positioned above.
	 *
	 * <p>Note: The Control provided as the editor <b>must</b> be created with its parent being the CTableTree control
	 * specified in the CTableTreeEditor constructor.
	 * 
	 * @param editor the Control that is displayed above the cell being edited
	 * @param item the CTableTreeItem for the row of the cell being tracked by this editor
	 */
	public void setEditor (Control editor, CTreeItem item) {
		setItem(item);
		setEditor(editor);
	}
	/**
	 * Specify the Control that is to be displayed and the cell in the tree that it is to be positioned above.
	 *
	 * <p>Note: The Control provided as the editor <b>must</b> be created with its parent being the CTableTree control
	 * specified in the CTableTreeEditor constructor.
	 * 
	 * @param editor the Control that is displayed above the cell being edited
	 * @param item the CTableTreeItem for the row of the cell being tracked by this editor
	 * @param column the zero based index of the column of the cell being tracked by this editor
	 *
	 * @since 3.1
	 */
	public void setEditor (Control editor, CTreeItem item, int column) {
		setItem(item);
		setColumn(column);
		setEditor(editor);
	}

	public void setItem (CTreeItem item) {
		this.item = item;
		layout();
	}
}
