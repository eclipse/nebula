/*******************************************************************************
 * Copyright (C) 2011 Angelo Zerr <angelo.zerr@gmail.com>, Pascal Leclercq <pascal.leclercq@gmail.com>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo ZERR - initial API and implementation
 *     Pascal Leclercq - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.pagination.snippets.tree;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.nebula.widgets.pagination.collections.PageResultLoaderList;
import org.eclipse.nebula.widgets.pagination.snippets.model.Address;
import org.eclipse.nebula.widgets.pagination.snippets.model.Person;
import org.eclipse.nebula.widgets.pagination.snippets.model.Team;
import org.eclipse.nebula.widgets.pagination.tree.PageableTree;
import org.eclipse.nebula.widgets.pagination.tree.SortTreeColumnSelectionListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * This sample display a list of model {@link Team} in a SWT Tree with
 * pagination banner displayed with Page Results+Page Links on the top of the
 * SWT Tree. The 2 columns which display the list of {@link Team} can be clicked
 * to sort the paginated list.
 * 
 */
public class ModelSortPageableTreeExample {

	public static void main(String[] args) {

		Display display = new Display();
		Shell shell = new Shell(display);
		GridLayout layout = new GridLayout(1, false);
		shell.setLayout(layout);

		final List<Team> items = createList();

		// 1) Create pageable tree with 10 items per page
		// This SWT Component create internally a SWT Tree+JFace TreeViewer
		int pageSize = 10;
		PageableTree pageableTree = new PageableTree(shell, SWT.BORDER,
				SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, pageSize);
		pageableTree.setLayoutData(new GridData(GridData.FILL_BOTH));

		// 2) Initialize the tree viewer + SWT Tree
		TreeViewer viewer = pageableTree.getViewer();
		viewer.setContentProvider(TeamContentProvider.getInstance());
		viewer.setLabelProvider(new LabelProvider());

		Tree tree = viewer.getTree();
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);

		// 3) Create Tree columns with sort of paginated list.
		createColumns(viewer);

		// 3) Set current page to 0 to refresh the tree
		pageableTree.setPageLoader(new PageResultLoaderList<Team>(items));
		pageableTree.setCurrentPage(0);

		shell.setSize(400, 250);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private static void createColumns(final TreeViewer viewer) {

		// First column is for the first name
		TreeViewerColumn col = createTreeViewerColumn(viewer, "Name", 150);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Team) {
					Team p = (Team) element;
					return p.getName();
				}
				Person p = (Person) element;
				return p.getName();
			}
		});
		col.getColumn().addSelectionListener(
				new SortTreeColumnSelectionListener("name"));

		// Second column is for the adress
		col = createTreeViewerColumn(viewer, "Adress", 150);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Person) {
					Person p = (Person) element;
					Address address = p.getAddress();
					if (address == null) {
						return "";
					}
					return address.getName();
				}
				return "";
			}
		});
		col.getColumn().addSelectionListener(
				new SortTreeColumnSelectionListener("address.name"));
	}

	private static List<Team> createList() {
		List<Team> teams = new ArrayList<Team>();
		Team team = null;
		for (int i = 1; i < 2012; i++) {
			team = new Team("Team" + i);
			teams.add(team);
			for (int j = 1; j < 5; j++) {
				team.addPerson(new Person("Name " + j, j < 100 ? "Adress "
						+ Math.random() : null));
			}
		}

		return teams;
	}

	private static TreeViewerColumn createTreeViewerColumn(TreeViewer viewer,
			String title, int bound) {
		final TreeViewerColumn viewerColumn = new TreeViewerColumn(viewer,
				SWT.NONE);
		final TreeColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

}
