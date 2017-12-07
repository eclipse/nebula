/*******************************************************************************
 * Copyright (c) 2017 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.eclipse.nebula.widgets.tablecombo;

import static org.junit.Assert.assertEquals;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.junit.Before;
import org.junit.Test;

public class TableComboTests {

	private Shell shell;

	@Before
	public final void setupTableComboTests() {
		shell = new Shell(Display.getDefault());
		shell.setLayout(new GridLayout());
	}

	/**
	 * Ensures that the items in the {@link TableCombo} are correctly displayed in
	 * the table widget.
	 */
	@Test
	public void testDisplayOfItems() {

		/*
		 * GIVEN a TableCombo with one column and a number of TableItems assigned to the
		 * TableCombo.
		 */
		createTableCombo(shell, 1, 5);

		/*
		 * WHEN the TableCombo gets displayed
		 */
		shell.open();

		/*
		 * THEN the number of rows displayed in the table widget of the TableCombo must
		 * match the number of TableItems.
		 */
		final SWTBot shellBot = new SWTBot(shell);
		final TableCombo tableComboWidget = shellBot.widget(WidgetMatcherFactory.widgetOfType(TableCombo.class));
		final SWTBotTable tableBot = new SWTBotTable(tableComboWidget.getTable());

		assertEquals(
				"Expected the number of items in the table widget of the TableCombo to be the same as the number of TableItems added to the TableCombo",
				5, tableBot.rowCount());

	}

	/**
	 * Ensures that the columns defined for a {@link TableCombo} are correctly
	 * displayed in the table widget.
	 */
	@Test
	public void testDisplayOfColumns() {

		/*
		 * GIVEN a TableCombo with a number of columns and a number of TableItems
		 * assigned to the TableCombo.
		 */
		createTableCombo(shell, 3, 5);

		/*
		 * WHEN the TableCombo gets displayed
		 */
		shell.open();

		/*
		 * THEN the number of columns displayed in the table widget of the TableCombo
		 * must match the configured number
		 */
		final SWTBot shellBot = new SWTBot(shell);
		final TableCombo tableComboWidget = shellBot.widget(WidgetMatcherFactory.widgetOfType(TableCombo.class));
		final SWTBotTable tableBot = new SWTBotTable(tableComboWidget.getTable());

		assertEquals(
				"Expected the number of columns in the table widget of the TableCombo to be the same as the number of columns configured in the TableCombo",
				3, tableBot.columnCount());

	}

	/**
	 * Ensures that the text displayed in tableCombo text widget corresponds to the
	 * item selected in the tableCombo.
	 */
	@Test
	public void testSelectionOfSingleItem() {

		/*
		 * GIVEN a TableCombo with one column and a number of TableItems assigned to the
		 * TableCombo.
		 */
		createTableCombo(shell, 1, 5);

		/*
		 * WHEN the TableCombo gets displayed and a single item is selected in the table
		 * combo
		 */
		shell.open();

		final SWTBot shellBot = new SWTBot(shell);
		final TableCombo tableComboWidget = shellBot.widget(WidgetMatcherFactory.widgetOfType(TableCombo.class));
		tableComboWidget.select(0);

		/*
		 * THEN the text displayed in tableCombo text widget must correspond to the item
		 * selected in the tableCombo.
		 */
		assertEquals(
				"Expected the text displayed in the tableCombo text widget to correspond to the item selected in the tableCombo",
				"Item 0", tableComboWidget.getText());

	}

	private TableCombo createTableCombo(final Composite parent, final int noOfColumns, final int noOfItems) {
		final TableCombo result = new TableCombo(parent, SWT.NONE);
		result.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));

		// create columns
		final String[] columns = new String[noOfColumns];
		for (int i = 0; i < noOfColumns; i++) {
			columns[i] = "Column " + i;
		}
		result.defineColumns(columns);

		// create table items
		for (int i = 0; i < noOfItems; i++) {
			createTableItem(result, "Item " + i);
		}
		return result;
	}

	private void createTableItem(final TableCombo tableCombo, final String text) {
		final TableItem tableItem = new TableItem(tableCombo.getTable(), SWT.NONE);
		tableItem.setText(text);
	}

}
