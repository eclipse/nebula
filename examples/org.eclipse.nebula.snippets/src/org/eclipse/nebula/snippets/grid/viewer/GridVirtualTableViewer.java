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

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * A simple TableViewer to demonstrate the usage of a lazy content provider
 * with a virtual table
 */
public class GridVirtualTableViewer {

	private static final int ROWS = 1000000;
	private static final int COLUMNS = 10;

	private class MyContentProvider implements IStructuredContentProvider {
		public MyContentProvider(GridTableViewer viewer) {
			
		}
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
			
		}

		public Object[] getElements(Object inputElement) {
			return (Object[]) inputElement;
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
		v.setContentProvider(new MyContentProvider(v));
		v.setUseHashlookup(true);
		v.getGrid().setLinesVisible(true);
		v.getGrid().setHeaderVisible(true);
		v.getGrid().setVisibleLinesColumnPack(true);
//		v.getGrid().setRowHeaderVisible(true);
//		v.setRowHeaderLabelProvider(new ColumnLabelProvider() {
//			@Override
//			public String getText(Object element) {
//				return "xyz";
//			}
//		});
		v.getGrid().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		for (int i = 0; i < COLUMNS; i++)
		{
			createColumn(v, "Column");
		}
		
		MyModel[] model = createModel();
		v.setInput(model);
		
		Button b = new Button(shell, SWT.PUSH);
		b.setText("Filter items without 0");
		b.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent arg0) {
				v.addFilter(new ViewerFilter() {
					
					@Override
					public boolean select(Viewer viewer, Object parentElement, Object element) {
						return element.toString().contains("0");
					}
				});
			}
			
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
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
		shell.setLayout(new GridLayout());
		new GridVirtualTableViewer(shell);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		display.dispose();

	}

}