/*******************************************************************************
 * Copyright (c) 2020 Laurent Caron
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Laurent Caron <laurent dot caron at gmail dot com> - initial API and implementation
 * Marty Jones <martybjones at gmail dot com> - Base code for a sample of using TableCombo
 *******************************************************************************/
package org.eclipse.nebula.widgets.tablecombo.example.e4.parts;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.nebula.widgets.tablecombo.TableCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class SamplePart {
	private static List<Model> modelList;
	private static Text listenerResults;
	private static Group listenerGroup;
	private TableCombo tc;

	private static final String CSS_ID = "org.eclipse.e4.ui.css.id";

	@PostConstruct
	public void createComposite(final Composite parent) {
		// load the model list.
		modelList = loadModel();

		parent.setLayout(new GridLayout());

		// create group
		final Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		group.setText("Sample Group");
		group.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		// create group
		listenerGroup = new Group(parent, SWT.NONE);
		listenerGroup.setLayout(new GridLayout(1, false));
		listenerGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		listenerGroup.setText("Listener Results");
		listenerGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		listenerResults = new Text(listenerGroup, SWT.BORDER | SWT.MULTI);
		final GridData gd = new GridData(SWT.FILL, SWT.DEFAULT, true, false);
		gd.heightHint = 30;
		listenerResults.setLayoutData(gd);

		////////////////////////////////////////////////////////////////////////
		// Sample #1
		////////////////////////////////////////////////////////////////////////
		Label label = new Label(group, SWT.NONE);
		label.setText("No customization:");

		// create TableCombo
		tc = new TableCombo(group, SWT.BORDER | SWT.READ_ONLY);
		tc.setLayoutData(new GridData(125, SWT.DEFAULT));
		tc.setData(CSS_ID, "one");

		// load the dataset.
		loadSingleDataset(tc.getTable());

		// add listener
		tc.addSelectionListener(new ItemSelected(tc, "Sample1"));

		////////////////////////////////////////////////////////////////////////
		// Sample #2
		////////////////////////////////////////////////////////////////////////

		label = new Label(group, SWT.NONE);
		label.setText("Widget CSS Customization");

		// create TableCombo
		tc = new TableCombo(group, SWT.BORDER | SWT.READ_ONLY);
		tc.setLayoutData(new GridData(125, SWT.DEFAULT));
		tc.setData(CSS_ID, "two");
		tc.defineColumns(new String[] { "Data" });

		// load the dataset.
		loadSingleDataset(tc.getTable());

		// add listener
		tc.addSelectionListener(new ItemSelected(tc, "Sample2"));

		////////////////////////////////////////////////////////////////////////
		// Sample #3
		////////////////////////////////////////////////////////////////////////
		// create label
		label = new Label(group, SWT.NONE);
		label.setText("Text CSS Customization");

		// create TableCombo
		tc = new TableCombo(group, SWT.BORDER | SWT.READ_ONLY);
		tc.setLayoutData(new GridData(125, SWT.DEFAULT));
		tc.setData(CSS_ID, "three");

		// load the dataset.
		loadSingleDataset(tc.getTable());

		// add listener
		tc.addSelectionListener(new ItemSelected(tc, "Sample3"));

		////////////////////////////////////////////////////////////////////////
		// Sample #4
		////////////////////////////////////////////////////////////////////////
		// create label
		label = new Label(group, SWT.NONE);
		label.setText("Button CSS Customization (hint):");

		// create TableCombo
		tc = new TableCombo(group, SWT.BORDER | SWT.READ_ONLY);
		tc.setLayoutData(new GridData(125, SWT.DEFAULT));
		tc.setData(CSS_ID, "four");

		// load the dataset.
		loadSingleDataset(tc.getTable());

		// add listener
		tc.addSelectionListener(new ItemSelected(tc, "Sample4"));

		////////////////////////////////////////////////////////////////////////
		// Sample #5
		////////////////////////////////////////////////////////////////////////
		// create label
		label = new Label(group, SWT.NONE);
		label.setText("Table CSS Customization:");

		// create TableCombo
		tc = new TableCombo(group, SWT.BORDER | SWT.READ_ONLY);
		tc.setLayoutData(new GridData(125, SWT.DEFAULT));
		tc.setData(CSS_ID, "five");

		// load the dataset.
		loadSingleDataset(tc.getTable());

		// add listener
		tc.addSelectionListener(new ItemSelected(tc, "Sample5"));

		////////////////////////////////////////////////////////////////////////
		// Sample #6
		////////////////////////////////////////////////////////////////////////
		// create label
		label = new Label(group, SWT.NONE);
		label.setText("Odd/Even Lines Customisation:");

		// create TableCombo
		tc = new TableCombo(group, SWT.BORDER | SWT.READ_ONLY);
		tc.setLayoutData(new GridData(125, SWT.DEFAULT));
		tc.setData(CSS_ID, "six");

		// load the dataset.
		loadSingleDataset(tc.getTable());

		// add listener
		tc.addSelectionListener(new ItemSelected(tc, "Sample6"));

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

	@Focus
	public void setFocus() {
		tc.forceFocus();
	}

	@PreDestroy
	private void dispose() {
	}

	private static class ItemSelected extends SelectionAdapter {

		private final TableCombo tc;
		private final String text;

		public ItemSelected(final TableCombo tc, final String text) {
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