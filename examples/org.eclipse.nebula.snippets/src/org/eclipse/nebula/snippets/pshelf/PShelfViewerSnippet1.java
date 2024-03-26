package org.eclipse.nebula.snippets.pshelf;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.jface.pshelfviewer.IShelfViewerFactory;
import org.eclipse.nebula.jface.pshelfviewer.PShelfViewer;
import org.eclipse.nebula.widgets.pshelf.RedmondShelfRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class PShelfViewerSnippet1 {

	private MyTreeNode myModel;
	private MyContentProvider myContentProvider;
	private MyLabelProvider myLabelProvider;

	private class MyViewerFactory implements IShelfViewerFactory {

		public Viewer createViewerForContent(Composite parent, Object content) {
			TreeNode node = (TreeNode) content;
			StructuredViewer viewer;
			if (node.getValue().equals("List")) {
				viewer = new ListViewer(parent);
			} else {
				viewer = new TreeViewer(parent);
			}

			viewer.setContentProvider(myContentProvider);
			viewer.setLabelProvider(myLabelProvider);
			return viewer;
		}

	}

	private class MyContentProvider implements ITreeContentProvider {

		public Object[] getChildren(Object parentElement) {
			return ((TreeNode) parentElement).getChildren();
		}

		public Object getParent(Object element) {
			return ((TreeNode) element).getParent();
		}

		public boolean hasChildren(Object element) {
			return ((TreeNode) element).hasChildren();
		}

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Object[]) {
				// elements for ListViewer
				return (Object[]) inputElement;
			} else {
				// elements for PShelf
				Object[] children = getChildren(inputElement);
				// prepend "All" node to children
				Object[] elements = new Object[children.length + 1];
				elements[0] = myModel;
				System.arraycopy(children, 0, elements, 1, children.length);
				return elements;
			}
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	private class MyLabelProvider extends LabelProvider {

		public String getText(Object element) {
			return ((TreeNode) element).getValue().toString();
		}
	}

	private class MyTreeNode extends TreeNode {

		public MyTreeNode(Object value) {
			super(value);
		}

		public void setChildren(TreeNode[] children) {
			super.setChildren(children);
			for (int i = 0; i < children.length; i++) {
				children[i].setParent(this);
			}
		}

	}

	public PShelfViewerSnippet1(Shell shell) {
		PShelfViewer viewer = new PShelfViewer(shell, SWT.NONE,
				new MyViewerFactory());

		// Optionally, change the renderer
		viewer.getPShelf().setRenderer(new RedmondShelfRenderer());

		// Optionally, transfer selection between viewers
		viewer.setTransferSelection(true);

		myContentProvider = new MyContentProvider();
		viewer.setContentProvider(myContentProvider);
		myLabelProvider = new MyLabelProvider();
		viewer.setLabelProvider(myLabelProvider);
		createModel();
		viewer.setInput(myModel);
	}

	private void createModel() {
		myModel = new MyTreeNode("All");
		TreeNode list = new MyTreeNode("List");
		TreeNode tree = new MyTreeNode("Tree");
		myModel.setChildren(new TreeNode[] { list, tree });

		list.setChildren(new TreeNode[] {
			new MyTreeNode("List item 1"),	new MyTreeNode("List item 2")
		});

		MyTreeNode treeNode1 = new MyTreeNode("Tree item 1");
		tree.setChildren(new TreeNode[] { treeNode1, new MyTreeNode("Tree item 2") });
		treeNode1.setChildren(new TreeNode[] { new MyTreeNode("Sub item 1.1") });
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		new PShelfViewerSnippet1(shell);

		shell.setSize(200, 400);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
