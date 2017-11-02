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
   * Ensures that the elements set as items for the the {@link TableCombo} are
   * correctly displayed in the table widget.
   */
  @Test
  public void testDisplayOfItems() {

    /*
     * GIVEN a TableCombo with one column and a number of TableItems assigned to
     * the TableCombo.
     */
    final TableCombo tableCombo = new TableCombo(shell, SWT.NONE);
    tableCombo.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));

    tableCombo.defineColumns(new String[] {
        "Id"
    });

    final int numberOfTableItems = 5;
    for (int i = 0; i < numberOfTableItems; i++) {
      createTableItem(tableCombo, "item " + i);
    }

    /*
     * WHEN the TableCombo gets displayed
     */
    shell.open();
    consumeEvents();

    /*
     * THEN the number of rows displayed in the table widget of the TableCombo
     * must match the numer of TableItems.
     */
    final SWTBot shellBot = new SWTBot(shell);
    final TableCombo tableComboWidget = shellBot
        .widget(WidgetMatcherFactory.widgetOfType(TableCombo.class));
    final SWTBotTable tableBot = new SWTBotTable(tableComboWidget.getTable());

    assertEquals("Expected the number of items in the table widget of the TableCombo to be the same as the number of TableItems added to the TableCombo", numberOfTableItems, tableBot
        .rowCount());

  }

  private void createTableItem(final TableCombo tableCombo, final String text) {
    final TableItem tableItem = new TableItem(tableCombo.getTable(), SWT.NONE);
    tableItem.setText(text);
  }

  private void consumeEvents() {
    while (shell.getDisplay().readAndDispatch()) {
    }
  }
}
