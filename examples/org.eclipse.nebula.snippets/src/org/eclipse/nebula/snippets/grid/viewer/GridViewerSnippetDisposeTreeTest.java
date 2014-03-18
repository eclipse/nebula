/*******************************************************************************
 * Copyright (c) 2006 Tom Schindl and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl - initial API and implementation
 *     Mirko Paturzo - adding dispose functions example and defaults properties change
 *******************************************************************************/

package org.eclipse.nebula.snippets.grid.viewer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.jface.gridviewer.GridTreeViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.nebula.widgets.grid.GridUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * A simple TreeViewer for verify dispose improvement
 * 
 * @author Tom Schindl <tom.schindl@bestsolution.at>
 * @author Mirko Paturzo <mirko.paturzo@exeura.eu>
 * 
 */
public class GridViewerSnippetDisposeTreeTest
{
	private class MyContentProvider implements ITreeContentProvider
	{

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			return ((MyModel) inputElement).child.toArray();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose()
		{

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
		 *      java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object parentElement)
		{
			return getElements(parentElement);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		public Object getParent(Object element)
		{
			if (element == null)
			{
				return null;
			}

			return ((MyModel) element).parent;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		public boolean hasChildren(Object element)
		{
			return ((MyModel) element).child.size() > 0;
		}

	}

	public class MyModel
	{
		public MyModel parent;

		public ArrayList child = new ArrayList();

		public int counter;

		public MyModel(int counter, MyModel parent)
		{
			this.parent = parent;
			this.counter = counter;
		}

		@Override
		public String toString()
		{
			String rv = "Item ";
			if (parent != null)
			{
				rv = parent.toString() + ".";
			}

			rv += counter;

			return rv;
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

	public GridViewerSnippetDisposeTreeTest(final Shell shell)
	{
		final GridTreeViewer v = new GridTreeViewer(shell);

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
					new MessageDialog(shell, "IndexOutOfBound error", null, "Restore the grid with button \"" + restore
							+ "\"", MessageDialog.WARNING, new String[] { "Ok" }, 0).open();
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
					new MessageDialog(shell, "IndexOutOfBound error", null, "Restore the grid with button \"" + restore
							+ "\"", MessageDialog.WARNING, new String[] { "Ok" }, 0).open();
				}

				shell.layout();
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
					new MessageDialog(shell, "IndexOutOfBound error", null, "Restore the grid with button \"" + restore
							+ "\"", MessageDialog.WARNING, new String[] { "Ok" }, 0).open();
				}

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
							new MessageDialog(shell, "Success", null, "Exported in " + open, MessageDialog.INFORMATION,
									new String[] { "Great!" }, 1).open();
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
		final Button changeFont = new Button(buttons, SWT.NONE);
		changeFont.setText("Change Default");
		changeFont.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				v.getGrid().setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
				v.getGrid().setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
				v.getGrid()
						.setFont(registry.getItalic(Display.getCurrent().getSystemFont().getFontData()[0].getName()));
				shell.layout();
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
				revertGrid(v);
				shell.layout();
			}
		});
		revertGrid(v);
	}

	private void revertGrid(final GridTreeViewer v)
	{
		GridViewerColumn column = new GridViewerColumn(v, SWT.NONE);
		column.getColumn().setWidth(200);
		column.getColumn().setText("Column 1");
		column.getColumn().setTree(true);

		column = new GridViewerColumn(v, SWT.NONE);
		column.getColumn().setWidth(200);
		column.getColumn().setText("Column 2");

		v.setLabelProvider(new MyLabelProvider());
		v.setContentProvider(new MyContentProvider());
		v.setInput(createModel());
	}

	private MyModel createModel()
	{

		MyModel root = new MyModel(0, null);
		root.counter = 0;

		MyModel tmp;
		for (int i = 1; i < 10; i++)
		{
			tmp = new MyModel(i, root);
			root.child.add(tmp);
			for (int j = 1; j < i; j++)
			{
				tmp.child.add(new MyModel(j, tmp));
			}
		}

		return root;
	}

	public static void main(String[] args)
	{
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		new GridViewerSnippetDisposeTreeTest(shell);
		shell.open();

		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}

		display.dispose();
	}

	private static void createColumn(final GridTreeViewer v, String name)
	{
		GridColumn column = new GridColumn(v.getGrid(), SWT.NONE);
		column.setWidth(200);
		column.setText(name);
	}
}
