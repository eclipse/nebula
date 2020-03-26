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
 * Laurent Caron <laurent dot caron at gmail dot com> - Initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.grid.example.e4.parts;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class FirstPart {

	private static final String CSS_ID = "org.eclipse.e4.ui.css.id";
	private Grid grid;

	@PostConstruct
	public void createComposite(final Composite parent) {
		parent.setLayout(new FillLayout());

		grid = new Grid(parent,SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
	    grid.setHeaderVisible(true);
	    GridColumn column = new GridColumn(grid,SWT.NONE);
	    column.setTree(true);
	    column.setText("Column 1");
	    column.setWidth(100);
	    GridItem item1 = new GridItem(grid,SWT.NONE);
	    item1.setText("Root Item");
	    GridItem item2 = new GridItem(item1,SWT.NONE);
	    item2.setText("Second item");
	    GridItem item3 = new GridItem(item2,SWT.NONE);
	    item3.setText("Third Item");

		grid.setData(CSS_ID, "one");

	}

	@Focus
	public void setFocus() {
		grid.forceFocus();
	}

	@PreDestroy
	private void dispose() {
	}


}