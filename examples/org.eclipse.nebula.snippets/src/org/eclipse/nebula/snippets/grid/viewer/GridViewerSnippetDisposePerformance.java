package org.eclipse.nebula.snippets.grid.viewer;

/*******************************************************************************
 * Copyright (c) 2006 Tom Schindl and others. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Tom Schindl - initial API and implementation
 * 				 Mirko Paturzo - adding dispose functions example and 
 * 								 defaults properties change
 *******************************************************************************/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.nebula.widgets.grid.GridUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Example usage of none mandatory interfaces of ITableFontProvider and
 * ITableColorProvider
 * 
 * @author Tom Schindl <tom.schindl@bestsolution.at>
 * @author Mirko Paturzo <mirko.paturzo@exeura.eu>
 * 
 *         Original example, with something else (NUM_COLUMNS, NUM_MODELS) Using
 *         {@link ColumnRowBigDataVisualizer}
 * 
 *         Suggestions: Take heap dump by jvisualvm and analyze it with
 *         MemoryAnalyzer
 * 
 */
public class GridViewerSnippetDisposePerformance
{

	private static final int NUM_COLUMNS = 10;
	private static final int NUM_MODELS = 10;

	private class MyContentProvider implements IStructuredContentProvider
	{

		/*
		 * (non-Javadoc)
		 * @see
		 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(
		 * java.lang.Object)
		 */

		public Object[] getElements(Object inputElement)
		{
			return (MyModel[]) inputElement;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */

		public void dispose()
		{

		}

		/*
		 * (non-Javadoc)
		 * @see
		 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse
		 * .jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{

		}

	}

	public static boolean flag = true;

	public class MyModel
	{
		public int counter;

		public MyModel(int counter)
		{
			this.counter = counter;
		}

		@Override
		public String toString()
		{
			return "Item " + this.counter;
		}
	}

	final FontRegistry registry = new FontRegistry();

	public class MyLabelProvider extends LabelProvider implements ITableLabelProvider, ITableFontProvider,
			ITableColorProvider
	{

		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			return "Column " + columnIndex + " => " + element.toString();
		}

		public Font getFont(Object element, int columnIndex)
		{
			if (((MyModel) element).counter % 2 == 0)
			{
				return registry.getBold(Display.getCurrent().getSystemFont().getFontData()[0].getName());
			}
			return null;
		}

		public Color getBackground(Object element, int columnIndex)
		{
			if (((MyModel) element).counter % 2 == 0)
			{
				return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
			}
			return null;
		}

		public Color getForeground(Object element, int columnIndex)
		{
			if (((MyModel) element).counter % 2 == 1)
			{
				return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
			}
			return null;
		}

	}
	
	// field for disable/enable default key listener button
	private boolean enable = true;
	
	public GridViewerSnippetDisposePerformance(final Shell shell, boolean createButtons)
	{
		final GridTableViewer v = new GridTableViewer(new Grid(shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL) {
			@Override
			protected void onKeyDown(Event e)
			{
				super.onKeyDown(e);
				System.out.println(e.keyCode + " Called");
			}
		});
		v.setLabelProvider(new MyLabelProvider());
		v.setContentProvider(new MyContentProvider());
		v.getGrid().setCellSelectionEnabled(true);

		v.setCellEditors(new CellEditor[] { new TextCellEditor(v.getGrid()), new TextCellEditor(v.getGrid()) });
		v.setCellModifier(new ICellModifier()
		{

			public boolean canModify(Object element, String property)
			{
				return true;
			}

			public Object getValue(Object element, String property)
			{
				if (element == null)
					return "Element is null";
				return "Column " + property + " => " + element.toString();
			}

			public void modify(Object element, String property, Object value)
			{

			}

		});

		v.setColumnProperties(new String[] { "1", "2" });

		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(v)
		{

			@Override
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event)
			{
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
						|| (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.CR);
			}
		};

		GridViewerEditor.create(v, actSupport, ColumnViewerEditor.TABBING_HORIZONTAL
				| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL
				| ColumnViewerEditor.KEYBOARD_ACTIVATION);

		for (int i = 0; i < NUM_COLUMNS; i++)
		{
			createColumn(v, "Column " + i);
		}
		if (createButtons)
		{
			Composite buttons = new Composite(shell, SWT.NONE);
			buttons.setLayout(new RowLayout());

			final Button remove3 = new Button(buttons, SWT.NONE);
			remove3.setText("remove row with index 3");
			final String restore = "restore grid";
			remove3.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseDown(MouseEvent e)
				{
					//					button.dispose();
					try
					{
						v.getGrid().remove(3);
					}
					catch (java.lang.IllegalArgumentException ie)
					{
						new MessageDialog(shell, "IndexOutOfBound error", null, "Restore the grid with button \""
								+ restore + "\"", MessageDialog.WARNING, new String[] { "Ok" }, 0).open();
					}

					shell.layout();
				}
			});

			final Button add3 = new Button(buttons, SWT.NONE);
			add3.setText("add row with index 3");
			add3.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseDown(MouseEvent e)
				{
					GridItem gridItem = new GridItem(v.getGrid(), SWT.NONE, 3);
					gridItem.setText(0, "Added in 3");
					shell.layout();
				}
			});
			final Button addColumn1 = new Button(buttons, SWT.NONE);
			addColumn1.setText("add column in 1");
			addColumn1.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseDown(MouseEvent e)
				{
					new GridColumn(v.getGrid(), SWT.NONE, 1);
					shell.layout();
				}
			});
			final Button removeColumn = new Button(buttons, SWT.NONE);
			removeColumn.setText("remove first column");
			removeColumn.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseDown(MouseEvent e)
				{
					//					button.dispose();
					try
					{
						v.getGrid().getColumn(0).dispose();
						v.getGrid().redraw();
						v.getGrid().layout();
					}
					catch (java.lang.IllegalArgumentException ie)
					{
						new MessageDialog(shell, "IndexOutOfBound error", null, "Restore the grid with button \""
								+ restore + "\"", MessageDialog.WARNING, new String[] { "Ok" }, 0).open();
					}

					shell.layout();
				}
			});
			final Button hideColumn = new Button(buttons, SWT.NONE);
			hideColumn.setText("hide first column");
			hideColumn.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseDown(MouseEvent e)
				{
					try
					{
						GridColumn[] columns = v.getGrid().getColumns();
						for (GridColumn gridColumn : columns)
						{
							if (gridColumn.isVisible())
							{
								gridColumn.setVisible(false);
								break;
							}
						}
					}
					catch (java.lang.IllegalArgumentException ie)
					{
						new MessageDialog(shell, "IndexOutOfBound error", null, "Restore the grid with button \""
								+ restore + "\"", MessageDialog.WARNING, new String[] { "Ok" }, 0).open();
					}
					shell.layout();
				}
			});
			final Button manualDeselect = new Button(buttons, SWT.NONE);
			manualDeselect.setText("Manual Deselect");
			manualDeselect.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseDown(MouseEvent e)
				{
					Grid grid = v.getGrid();
					Point focus = grid.getFocusCell();
					int[] selected = grid.getSelectionIndices();
					grid.deselect(selected);
					grid.remove(selected);
					while (focus.y >= grid.getItemCount())
						--focus.y;
					if (focus.y >= 0)
					{
						grid.setFocusItem(grid.getItem(focus.y));
						grid.setFocusColumn(grid.getColumn(focus.x));
						grid.setCellSelection(focus);
					}
				}
			});
			final Button addColumn = new Button(buttons, SWT.NONE);
			addColumn.setText("add column");
			addColumn.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseDown(MouseEvent e)
				{
					//					button.dispose();
					try
					{
						createColumn(v, "Added");
					}
					catch (java.lang.IllegalArgumentException ie)
					{
						new MessageDialog(shell, "IndexOutOfBound error", null, "Restore the grid with button \""
								+ restore + "\"", MessageDialog.WARNING, new String[] { "Ok" }, 0).open();
					}

					shell.layout();
				}
			});
			final Button changeFont = new Button(buttons, SWT.NONE);
			changeFont.setText("Change Default");
			changeFont.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseDown(MouseEvent e)
				{
					v.getGrid().setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
					v.getGrid().setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
					v.getGrid().setFont(
							registry.getItalic(Display.getCurrent().getSystemFont().getFontData()[0].getName()));
					shell.layout();
				}
			});
			Button exportGrid = new Button(buttons, SWT.NONE);
			exportGrid.setText("Export Grid");
			exportGrid.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseDown(MouseEvent e)
				{
					try
					{
						if (v.getGrid().isDisposed())
							return;
						FileDialog fileDialog = new FileDialog(shell);
						fileDialog.setFilterExtensions(new String[] { "*.xml" });
						String open = fileDialog.open();
						if (open != null)
						{
							open = open.endsWith(".xml") ? open : open + ".xml";
							FileOutputStream outputStream = new FileOutputStream(new File(open));
							try
							{
								GridUtils.gridToXml(v.getGrid(), outputStream);
								new MessageDialog(shell, "Success", null, "Exported in " + open,
										MessageDialog.INFORMATION, new String[] { "Great!" }, 1).open();
							}
							catch (ParserConfigurationException e1)
							{
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							catch (TransformerException e1)
							{
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							finally
							{
								try
								{
									outputStream.close();
								}
								catch (IOException e1)
								{
									e1.printStackTrace();
								}
							}
						}
					}
					catch (FileNotFoundException e1)
					{
						e1.printStackTrace();
					}
				}
			});
			final Button restoreButton = new Button(buttons, SWT.NONE);
			restoreButton.setText(restore);
			restoreButton.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseDown(MouseEvent e)
				{
					GridColumn[] columns = v.getGrid().getColumns();
					for (GridColumn gridColumn : columns)
					{
						gridColumn.dispose();
					}
					for (int i = 0; i < NUM_COLUMNS; i++)
					{
						createColumn(v, "Column " + i);
					}
					v.setInput(createModel());
					v.getGrid().setItemCount(10);
					v.getGrid().redraw();
					v.getGrid().layout();
					shell.layout();
				}
			});
			final Button enableDisableKeyListenerButton = new Button(buttons, SWT.NONE);
			enableDisableKeyListenerButton.setText("Disable default key listener");
			enableDisableKeyListenerButton.addMouseListener(new MouseAdapter()
			{
				

				@Override
				public void mouseDown(MouseEvent e)
				{
					if (enable)
					{
						v.getGrid().disableDefaultKeyListener();
						enable = false;
						enableDisableKeyListenerButton.setText("Enable default key listener");
					}
					else
					{
						v.getGrid().enableDefaultKeyListener();
						enable = true;
						enableDisableKeyListenerButton.setText("Disable default key listener");
					}
				}
			});
			Button gcButton = new Button(buttons, SWT.NONE);
			gcButton.setText("gc");
			gcButton.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseDown(MouseEvent e)
				{
					System.gc();
				}
			});

		}
		MyModel[] model = createModel();
		v.setInput(model);
		v.getGrid().setLinesVisible(true);
		v.getGrid().setHeaderVisible(true);
	}

	private void createColumn(final GridTableViewer v, String name)
	{
		GridColumn column = new GridColumn(v.getGrid(), SWT.NONE);
		column.setWidth(200);
		column.setText(name);
	}

	private MyModel[] createModel()
	{
		MyModel[] elements = new MyModel[NUM_MODELS];

		for (int i = 0; i < NUM_MODELS; i++)
		{
			elements[i] = new MyModel(i);
		}

		return elements;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Display display = new Display();

		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		new GridViewerSnippetDisposePerformance(shell, true);
		shell.open();

		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}

		display.dispose();

	}

}
