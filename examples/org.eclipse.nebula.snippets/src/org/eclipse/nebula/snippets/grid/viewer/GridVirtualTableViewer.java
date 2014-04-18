package org.eclipse.nebula.snippets.grid.viewer;

/*******************************************************************************
 * Copyright (c) 2014 Mirko Paturzo (Exeura srl).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mirko Paturzo - realize example
 *******************************************************************************/

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * A simple TableViewer to demonstrate the usage of a lazy content provider
 * with a virtual table
 */
public class GridVirtualTableViewer {

	private static final int ROWS = 1000000;
	private static final int COLUMNS = 1000;

	private class MyLazyContentProvider implements ILazyContentProvider {
		  private final GridTableViewer viewer;
		  private MyModel[] elements;

		  public MyLazyContentProvider(GridTableViewer viewer) {
		    this.viewer = viewer;
		  }

		  public void dispose() {
		  }

		  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		    this.elements = (MyModel[]) newInput;
		  }

		  public void updateElement(int index) {
		    viewer.replace(elements[index], index);
		  }
		} 
	public class MyModel {
		public int counter;

		public MyModel(int counter) {
			this.counter = counter;
		}

		@Override
		public String toString() {
			return "Item " + this.counter;
		}
	}

	public GridVirtualTableViewer(Shell shell) {
		LabelProvider labelProvider = new LabelProvider();
		final GridTableViewer v = new GridTableViewer(shell, SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL);
		
		v.setLabelProvider(labelProvider);
		v.setContentProvider(new MyLazyContentProvider(v));
		v.setUseHashlookup(true);
		for (int i = 0; i < COLUMNS; i++)
		{
			createColumn(v, "Column");
		}
		
		MyModel[] model = createModel();
		v.setInput(model);
		v.getGrid().setItemCount(model.length);

		v.getGrid().setLinesVisible(true);
		v.getGrid().setHeaderVisible(true);
	}
	private void createColumn(final GridTableViewer v, String name) {
		GridColumn column = new GridColumn(v.getGrid(), SWT.NONE);
		column.setWidth(200);
		column.setText(name);
	}
	private MyModel[] createModel() {
		MyModel[] elements = new MyModel[ROWS];

		for (int i = 0; i < ROWS; i++) {
			elements[i] = new MyModel(i);
		}

		return elements;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		new GridVirtualTableViewer(shell);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		display.dispose();

	}

}