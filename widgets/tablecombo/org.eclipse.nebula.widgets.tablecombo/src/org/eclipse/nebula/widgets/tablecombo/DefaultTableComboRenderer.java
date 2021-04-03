/*******************************************************************************
 * Copyright (c) 2011-2021 Nebula Team.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.tablecombo;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * Default table combo renderer
 */
public class DefaultTableComboRenderer implements TableComboRenderer {

	private Table table;
	private TableCombo tableCombo;

	/**
	 * @param tableCombo
	 */
	public DefaultTableComboRenderer(TableCombo tableCombo) {
		this.tableCombo = tableCombo;
		this.table = tableCombo.getTable();
	}

	/**
	 * @see org.eclipse.nebula.widgets.tablecombo.TableComboRenderer#getLabel(int)
	 */
	@Override
	public String getLabel(int selectionIndex) {
		final TableItem tableItem = table.getItem(selectionIndex);
		return tableItem.getText(tableCombo.getDisplayColumnIndex());
	}

	/**
	 * @see org.eclipse.nebula.widgets.tablecombo.TableComboRenderer#getImage(int)
	 */
	@Override
	public Image getImage(int selectionIndex) {
		final TableItem tableItem = table.getItem(selectionIndex);
		return tableItem.getImage(tableCombo.getDisplayColumnIndex());
	}

	/**
	 * @see org.eclipse.nebula.widgets.tablecombo.TableComboRenderer#getBackground(int)
	 */
	@Override
	public Color getBackground(int selectionIndex) {
		final TableItem tableItem = table.getItem(selectionIndex);
		return tableItem.getBackground(tableCombo.getDisplayColumnIndex());

	}

	/**
	 * @see org.eclipse.nebula.widgets.tablecombo.TableComboRenderer#getForeground(int)
	 */
	@Override
	public Color getForeground(int selectionIndex) {
		final TableItem tableItem = table.getItem(selectionIndex);
		return tableItem.getForeground(tableCombo.getDisplayColumnIndex());
	}

	/**
	 * @see org.eclipse.nebula.widgets.tablecombo.TableComboRenderer#getFont(int)
	 */
	@Override
	public Font getFont(int selectionIndex) {
		final TableItem tableItem = table.getItem(selectionIndex);
		return tableItem.getFont(tableCombo.getDisplayColumnIndex());

	}

}
