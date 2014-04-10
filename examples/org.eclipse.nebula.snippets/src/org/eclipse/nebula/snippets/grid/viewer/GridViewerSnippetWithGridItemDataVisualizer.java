package org.eclipse.nebula.snippets.grid.viewer;

/*******************************************************************************
 * Copyright (c) 2006 Tom Schindl and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl - initial API and implementation
 *     Mirko Paturzo - improve Grid Export, dispose, fonts and background
 *     				   functional changes
 *******************************************************************************/


import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerEditor;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Example usage of none mandatory interfaces of ITableFontProvider and
 * ITableColorProvider
 * 
 * @author Tom Schindl <tom.schindl@bestsolution.at>
 * @author Mirko Paturzo <mirko.paturzo@exeura.eu>
 * 
 * Original example, with something else (NUM_COLUMNS, NUM_MODELS)
 * Using {@link ColumnRowBigDataVisualizer}
 * 
 */
public class GridViewerSnippetWithGridItemDataVisualizer {

	private static final int NUM_COLUMNS = 1000;
	private static final int NUM_MODELS = 1000;

	private class MyContentProvider implements IStructuredContentProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			return (MyModel[]) inputElement;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
		 *      java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}

	}

	public static boolean flag = true;
	
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

	public class MyLabelProvider extends LabelProvider implements
			ITableLabelProvider, ITableFontProvider, ITableColorProvider {
		FontRegistry registry = new FontRegistry();

		
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		
		public String getColumnText(Object element, int columnIndex) {
			return "Column " + columnIndex + " => " + element.toString();
		}

		
		public Font getFont(Object element, int columnIndex) {
			if (((MyModel) element).counter % 2 == 0) {
				return registry.getBold(Display.getCurrent().getSystemFont()
						.getFontData()[0].getName());
			}
			return null;
		}

		
		public Color getBackground(Object element, int columnIndex) {
			if (((MyModel) element).counter % 2 == 0) {
				return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
			}
			return null;
		}

		
		public Color getForeground(Object element, int columnIndex) {
			if (((MyModel) element).counter % 2 == 1) {
				return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
			}
			return null;
		}

	}

	public GridViewerSnippetWithGridItemDataVisualizer(Shell shell) {
		final GridTableViewer v = new GridTableViewer(shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		v.setLabelProvider(new MyLabelProvider());
		v.setContentProvider(new MyContentProvider());
		v.getGrid().setCellSelectionEnabled(true);
				
		v.setCellEditors(new CellEditor[] { new TextCellEditor(v.getGrid()), new TextCellEditor(v.getGrid()) });
		v.setCellModifier(new ICellModifier() {

			
			public boolean canModify(Object element, String property) {
				return true;
			}

			
			public Object getValue(Object element, String property) {
				return "Column " + property + " => " + element.toString();
			}

			
			public void modify(Object element, String property, Object value) {
				
			}
			
		});
		
		v.setColumnProperties(new String[] {"1","2"});
		
		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(v) {
			
			@Override
			protected boolean isEditorActivationEvent(
					ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
						|| (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.CR);
			}
		};
		
		GridViewerEditor.create(v, actSupport, ColumnViewerEditor.TABBING_HORIZONTAL
				| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
				| ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION);
		
		for(int i = 0; i < NUM_COLUMNS; i++) {
			createColumn(v, "Column " + i);
		}

		MyModel[] model = createModel();
		v.setInput(model);
		v.getGrid().setLinesVisible(true);
		v.getGrid().setHeaderVisible(true);
	}

	private void createColumn(final GridTableViewer v, String name) {
		GridColumn column = new GridColumn(v.getGrid(), SWT.NONE);
		column.setWidth(200);
		column.setText(name);
	}

	private MyModel[] createModel() {
		MyModel[] elements = new MyModel[NUM_MODELS];

		for (int i = 0; i < NUM_MODELS; i++) {
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
		new GridViewerSnippetWithGridItemDataVisualizer(shell);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		display.dispose();

	}

}
