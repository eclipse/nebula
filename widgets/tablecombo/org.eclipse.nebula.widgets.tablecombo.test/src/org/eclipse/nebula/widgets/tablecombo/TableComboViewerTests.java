/*******************************************************************************
 * Copyright (c) 2017 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.eclipse.nebula.widgets.tablecombo;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.nebula.jface.tablecomboviewer.TableComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotArrowButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.junit.Before;
import org.junit.Test;

public class TableComboViewerTests {

	private final static Logger LOG = Logger.getLogger(TableComboViewerTests.class.getName());

	private Shell shell;

	@Before
	public final void setupTableComboViewerTests() {
		shell = new Shell(Display.getDefault());
		shell.setLayout(new GridLayout());
	}

	/**
	 * Ensures that the items set as input to the {@link TableComboViewer} are
	 * correctly displayed in the table widget.
	 */
	@Test
	public void testDisplayOfItems() {

		/*
		 * GIVEN a TableComboViewer with two columns and a number of items set as input
		 * of the TableComboViewer.
		 */
		createTableComboViewer(shell, 5);

		/*
		 * WHEN the TableComboViewer gets displayed
		 */
		shell.open();

		/*
		 * THEN the number of rows displayed in the table widget of the TableComboViewer
		 * must match the number of items.
		 */
		final SWTBot shellBot = new SWTBot(shell);
		final TableCombo tableComboWidget = shellBot.widget(WidgetMatcherFactory.widgetOfType(TableCombo.class));
		final SWTBotTable tableBot = new SWTBotTable(tableComboWidget.getTable());

		assertEquals(
				"Expected the number of items in the table widget of the TableCombo to be the same as the number of items added to the TableComboViewer",
				5, tableBot.rowCount());

	}

	/**
	 * Ensures that the properties of the items for the {@link TableComboViewer} are
	 * correctly displayed in the cells of the table widget.
	 */
	@Test
	public void testDisplayOfCellValues() {

		/*
		 * GIVEN a TableComboViewer with two columns and a number of items set as input
		 * of the TableComboViewer.
		 */
		createTableComboViewer(shell, 5);

		/*
		 * WHEN the TableComboViewer gets displayed
		 */
		shell.open();

		/*
		 * THEN the number of columns displayed in the table widget of the
		 * TableComboViewer must match the number of columns defined for the
		 * TableComboViewer and the cell values must reflect the according property
		 * values of the item in the row at hand.
		 */
		final SWTBot shellBot = new SWTBot(shell);
		final TableCombo tableComboWidget = shellBot.widget(WidgetMatcherFactory.widgetOfType(TableCombo.class));
		final SWTBotTable tableBot = new SWTBotTable(tableComboWidget.getTable());

		assertEquals(
				"Expected the number of columns in the table widget of the TableComboViewer to be the one as defined for the TableComboViewer",
				2, tableBot.columnCount());
		assertEquals(
				"Expected the value in the cell [0,1] in the table widget of the TableComboViewer to correspond to the description of first item set as input for the TableComboViewer",
				"Description 0", tableBot.cell(0, 1));
		assertEquals(
				"Expected the value in the cell [1,0] in the table widget of the TableComboViewer to correspond to the name of second item set as input for the TableComboViewer",
				"Item 1", tableBot.cell(1, 0));

	}

	/**
	 * Regression test for #514731. Ensures that the TableComboViewer does not emit
	 * a selection change event on opening of the drop down table of the
	 * TableCombo.<br>
	 * TODO This test will fail until the bug is actually fixed.
	 */
	@Test
	public void testThatNoSelectionEventIsFiredOnDropDown() {

		final AtomicInteger selectionEventCounter = new AtomicInteger(0);

		/*
		 * GIVEN a TableComboViewer with a selection change listener registered. two
		 * columns and a number of items set as
		 */
		final TableComboViewer viewer = createTableComboViewer(shell, 5);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				LOG.warning("Received unexpected selection change event " + event);
				selectionEventCounter.getAndIncrement();
			}
		});
		shell.open();

		/*
		 * WHEN the drop down table of the TableCombo gets opened by a click on the
		 * arrow button
		 */
		final SWTBot shellBot = new SWTBot(shell);
		final SWTBotArrowButton dropDownTriggerBot = shellBot.arrowButton();
		dropDownTriggerBot.click();

		consumeEvents();

		/*
		 * THEN no selection change event must have been fired.
		 */
		assertEquals(
				"Expected no selection change event to have been fired on drop down of the table of the TableCombo", 0,
				selectionEventCounter.get());
	}

	private TableComboViewer createTableComboViewer(final Composite parent, final int noOfItems) {
		final TableComboViewer result = new TableComboViewer(parent, SWT.NONE);
		result.getTableCombo().setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));

		result.setContentProvider(ArrayContentProvider.getInstance());
		result.setLabelProvider(new TestLabelProvider());

		// create columns
		final String[] columns = new String[] { "Name", "Description" };
		result.getTableCombo().defineColumns(columns);

		// set input data
		final TestItem[] testItems = new TestItem[noOfItems];
		for (int i = 0; i < noOfItems; i++) {
			testItems[i] = new TestItem("Item " + i, "Description " + i);
		}
		result.setInput(testItems);

		return result;

	}

	private void consumeEvents() {
		while (Display.getCurrent().readAndDispatch()) {
			// loop through events
		}
	}

	private class TestLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final TestItem item = (TestItem) element;
			return columnIndex == 0 ? item.name : item.description;
		}

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}
	}

	private class TestItem {
		String name;
		String description;

		TestItem(final String name, final String description) {
			this.name = name;
			this.description = description;
		}

	}
}
