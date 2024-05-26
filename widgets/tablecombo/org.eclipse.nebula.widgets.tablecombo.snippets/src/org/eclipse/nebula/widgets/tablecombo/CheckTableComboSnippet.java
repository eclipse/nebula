/*******************************************************************************
 * Copyright (c) 2011-2024 Nebula Team.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Laurent CARON (laurent.caron at gmail dot com) - creation of this snippet
 *******************************************************************************/

package org.eclipse.nebula.widgets.tablecombo;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.checktablecombo.CheckTableCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * Show basic features of CheckTableCombo
 *
 */
public class CheckTableComboSnippet {

	private static List<Model> modelList;
	private static Text listenerResults;
	private static Group listenerGroup;

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		// get display.
		final Display display = new Display();


		// load the model list.
		modelList = loadModel();

		// create a new shell.
		final Shell shell = new Shell(display);
		shell.setText("CheckTableCombo Snippet");
		shell.setSize(600, 400);
		shell.setLayout(new GridLayout());

		// create group
		final Group group = new Group(shell, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		group.setText("Sample Group");

		// create group
		listenerGroup = new Group(shell, SWT.NONE);
		listenerGroup.setLayout(new GridLayout(1, false));
		listenerGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		listenerGroup.setText("Listener Results");

		listenerResults = new Text(listenerGroup, SWT.BORDER | SWT.MULTI);
		final GridData gd = new GridData(SWT.FILL, SWT.DEFAULT, true, false);
		gd.heightHint = 30;
		listenerResults.setLayoutData(gd);

		////////////////////////////////////////////////////////////////////////
		// Sample #1
		////////////////////////////////////////////////////////////////////////
		final Label label = new Label(group, SWT.NONE);
		label.setText("Single Column :");

		// create TableCombo
		final CheckTableCombo tc = new CheckTableCombo(group, SWT.BORDER | SWT.READ_ONLY);
		tc.setLayoutData(new GridData(125, SWT.DEFAULT));

		// load the dataset.
		loadSingleDataset(tc.getTable());

		// add listener
		tc.addSelectionListener(new ItemSelected(tc, "Sample1"));


		// open the shell.
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		// dispose display
		display.dispose();
	}

	/**
	 * load a list of rows with a single column
	 *
	 * @return
	 */
	private static List<TableItem> loadSingleDataset(final Table table) {
		final List<TableItem> rowList = new ArrayList<>();

		final int total = modelList == null ? 0 : modelList.size();

		for (int index = 0; index < total; index++) {
			final TableItem ti = new TableItem(table, SWT.NONE);
			final Model model = modelList.get(index);
			ti.setText(model.getDescription());
			rowList.add(ti);
		}

		return rowList;
	}



	/**
	 * load the Model data.
	 *
	 * @return
	 */
	private static List<Model> loadModel() {
		final List<Model> items = new ArrayList<>();
		items.add(new Model(1, "One"));
		items.add(new Model(2, "Two"));
		items.add(new Model(3, "Three"));
		items.add(new Model(4, "Four"));
		items.add(new Model(5, "Five"));
		items.add(new Model(6, "Six"));
		items.add(new Model(7, "Seven"));
		items.add(new Model(8, "Eight"));
		items.add(new Model(9, "Nine"));
		items.add(new Model(10, "Ten"));
		items.add(new Model(11, "Eleven"));
		items.add(new Model(12, "Twelve"));
		items.add(new Model(13, "Thirteen"));
		items.add(new Model(14, "Fourteen"));
		items.add(new Model(15, "Fiveteen"));
		items.add(new Model(16, "Sixteen"));
		items.add(new Model(17, "Seventeen"));
		items.add(new Model(18, "Eighteen"));
		items.add(new Model(19, "Nineteen"));
		items.add(new Model(20, "Twenty"));

		return items;
	}


	private static class ItemSelected extends SelectionAdapter {

		private final CheckTableCombo tc;
		private final String text;

		public ItemSelected(final CheckTableCombo tc, final String text) {
			this.tc = tc;
			this.text = text;
		}

		@Override
		public void widgetSelected(final SelectionEvent e) {
			listenerGroup.setText("Listener Results - (" + text + ")");
			listenerResults.setText(tc.getText() == null ? "null" : tc.getText());
		}
	}
}
