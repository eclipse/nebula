/*******************************************************************************
 * Copyright (c) 2014 Kristine Jetzke and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kristine Jetzke - initial implementation
 *******************************************************************************/
package org.eclipse.nebula.snippets.grid.viewer;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.nebula.jface.gridviewer.GridColumnLayout;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Demonstrates usage of {@link GridColumnLayout}.
 */
public class GridViewerSnippetGridColumnLayout {

	public static void main(String[] args) {

		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout(SWT.VERTICAL));

		// 1st table: Columns with column weight

		final Label labelWeight = new Label(shell, SWT.NONE);
		labelWeight.setText("Grid Column Layout with weights from 1 to 10");
		Composite compWeight = new Composite(shell, SWT.NONE);

		GridColumnLayout layoutWeight = new GridColumnLayout();
		compWeight.setLayout(layoutWeight);
		final GridTableViewer viewerWeight = new GridTableViewer(compWeight,
				SWT.BORDER | SWT.FULL_SELECTION);

		viewerWeight.getGrid().setHeaderVisible(true);
		viewerWeight.getGrid().setLinesVisible(true);

		for (int i = 1; i < 11; i++) {
			GridViewerColumn column = new GridViewerColumn(viewerWeight,
					SWT.NONE);
			// Set column data with column weight
			layoutWeight.setColumnData(column.getColumn(),
					new ColumnWeightData(i));
			column.getColumn().setText("Weight: " + i);
			column.setLabelProvider(new ColumnLabelProvider());
		}

		viewerWeight.setContentProvider(ArrayContentProvider.getInstance());
		viewerWeight.setInput(new String[0]);

		// 2nd table: Columns with fixed width in pixel

		final Label labelFixedWidth = new Label(shell, SWT.NONE);
		labelFixedWidth
				.setText("Grid Column Layout with widths from 10px to 100 px");
		Composite compFixedWidth = new Composite(shell, SWT.NONE);

		GridColumnLayout layoutFixedWidth = new GridColumnLayout();
		compFixedWidth.setLayout(layoutFixedWidth);
		final GridTableViewer viewerFixedWidth = new GridTableViewer(
				compFixedWidth, SWT.BORDER | SWT.FULL_SELECTION);

		viewerFixedWidth.getGrid().setHeaderVisible(true);
		viewerFixedWidth.getGrid().setLinesVisible(true);

		for (int i = 1; i < 11; i++) {
			GridViewerColumn column = new GridViewerColumn(viewerFixedWidth,
					SWT.NONE);
			// Set column data with fixed width in pixel
			layoutWeight.setColumnData(column.getColumn(), new ColumnPixelData(
					10 * i));
			column.getColumn().setText(10 * i + "px");
			column.setLabelProvider(new ColumnLabelProvider());
		}

		viewerFixedWidth.setContentProvider(ArrayContentProvider.getInstance());
		viewerFixedWidth.setInput(new String[0]);

		shell.setSize(600, 200);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
