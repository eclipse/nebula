/*******************************************************************************
 * Copyright (c) 2009 Marty Jones
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marty Jones <martybjones@gmail.com> - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.snippets.tablecombo.viewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.nebula.jface.tablecomboviewer.TableComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.nebula.snippets.tablecombo.Model;
import org.eclipse.nebula.snippets.tablecombo.TableComboSnippet1;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * Shows basic features of TableComboViewer
 *
 */
public class TableComboViewerSnippet1 {

	private static Font boldFont;
	private static Image testImage;
	private static Image test2Image;
	private static Image test3Image;
	private static Color darkRed;
	private static Color darkBlue;
	private static Color darkGreen;
	private static List modelList;
	private static Text listenerResults;
	private static Group listenerGroup;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// get display.
		Display display = new Display ();
		
		// create bold and italic font.
		boldFont = new Font(display,"Arial",8, SWT.BOLD | SWT.ITALIC);
		
		// create images
		testImage = ImageDescriptor.createFromFile(TableComboSnippet1.class, 
			"in_ec_ov_success_16x16.gif").createImage();
		test2Image = ImageDescriptor.createFromFile(TableComboSnippet1.class, 
			"in_ec_ov_warning_16x16.gif").createImage();
		test3Image = ImageDescriptor.createFromFile(TableComboSnippet1.class, 
			"invalid_build_tool_16x16.gif").createImage();
		
		// create colors
		darkRed = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED);
		darkBlue = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE);
		darkGreen = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
		
		// load the model list.
		modelList = loadModel();
		
		// create a new shell.
		Shell shell = new Shell (display);
		shell.setText("TableComboViewer Snippet 1");
		shell.setSize(500, 400);
		shell.setLayout(new GridLayout());
		
		// create group
		Group group = new Group(shell, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		group.setText("Sample Group");
		
		// create group
		listenerGroup = new Group(shell, SWT.NONE);
		listenerGroup.setLayout(new GridLayout(1, false));
		listenerGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		listenerGroup.setText("Listener Results");
		
		listenerResults = new Text(listenerGroup, SWT.BORDER | SWT.MULTI);
		GridData gd = new GridData(SWT.FILL, SWT.DEFAULT, true, false);
		gd.heightHint = 30;
		listenerResults.setLayoutData(gd);
		
		////////////////////////////////////////////////////////////////////////
		// Sample #1
		////////////////////////////////////////////////////////////////////////
		
		Label label = new Label(group, SWT.NONE);
		label.setText("Single Column (Mimics Normal Combo Field):");
		
		// create TableCombo
		TableComboViewer tcv = new TableComboViewer(group, SWT.READ_ONLY | SWT.BORDER);
		tcv.getTableCombo().setLayoutData(new GridData(125, SWT.DEFAULT));

		// set the content provider
		tcv.setContentProvider(ArrayContentProvider.getInstance());
		
		// set the label provider
		tcv.setLabelProvider(new SingleItemLabelProvider());

		// load the data
		tcv.setInput(modelList);
		
		// add listener
		tcv.addSelectionChangedListener(new ItemSelected("Sample1"));
		
		////////////////////////////////////////////////////////////////////////
		// Sample #2
		////////////////////////////////////////////////////////////////////////
		// create label
		label = new Label(group, SWT.NONE);
		label.setText("Single Column (With Images):");
		
		// create TableCombo
		tcv = new TableComboViewer(group, SWT.READ_ONLY | SWT.BORDER);
		tcv.getTableCombo().setLayoutData(new GridData(125, SWT.DEFAULT));

		// set the content provider
		tcv.setContentProvider(ArrayContentProvider.getInstance());
		
		// set the label provider
		tcv.setLabelProvider(new SingleImageItemLabelProvider());
		
		// load the data
		tcv.setInput(modelList);
		
		// add listener
		tcv.addSelectionChangedListener(new ItemSelected("Sample2"));
		
		////////////////////////////////////////////////////////////////////////
		// Sample #3
		////////////////////////////////////////////////////////////////////////
		// create label
		label = new Label(group, SWT.NONE);
		label.setText("Two Columns:");
		
		// create TableCombo
		tcv = new TableComboViewer(group, SWT.READ_ONLY | SWT.BORDER);
		tcv.getTableCombo().setLayoutData(new GridData(125, SWT.DEFAULT));
		
		// set the content provider
		tcv.setContentProvider(ArrayContentProvider.getInstance());
		
		// set the label provider
		tcv.setLabelProvider(new MultipleLabelProvider());

		// tell the TableCombo that I want 2 blank columns auto sized.
		tcv.getTableCombo().defineColumns(2);
		
		// set which column index will be used to display the selected item.
		tcv.getTableCombo().setDisplayColumnIndex(1);
		
		// load the data
		tcv.setInput(modelList);
		
		// add listener
		tcv.addSelectionChangedListener(new ItemSelected("Sample3"));

		////////////////////////////////////////////////////////////////////////
		// Sample #4
		////////////////////////////////////////////////////////////////////////
		// create label
		label = new Label(group, SWT.NONE);
		label.setText("Two Columns (With Colors && Fonts):");
		
		// create TableCombo
		tcv = new TableComboViewer(group, SWT.READ_ONLY | SWT.BORDER);
		tcv.getTableCombo().setLayoutData(new GridData(125, SWT.DEFAULT));
		tcv.getTableCombo().setVisibleItemCount(10);

		// set the content provider
		tcv.setContentProvider(ArrayContentProvider.getInstance());
		
		// set the label provider
		tcv.setLabelProvider(new MultipleColorLabelProvider());

		// tell the TableCombo that I want 2 blank columns auto sized.
		tcv.getTableCombo().defineColumns(2);
		
		// set which column index will be used to display the selected item.
		tcv.getTableCombo().setDisplayColumnIndex(1);
		
		// load the data
		tcv.setInput(modelList);
		
		// add listener
		tcv.addSelectionChangedListener(new ItemSelected("Sample4"));

		////////////////////////////////////////////////////////////////////////
		// Sample #5
		////////////////////////////////////////////////////////////////////////
		// create label
		label = new Label(group, SWT.NONE);
		label.setText("Three Columns (With Colors && Fonts && Header):");
		
		// create TableCombo
		tcv = new TableComboViewer(group, SWT.READ_ONLY | SWT.BORDER);
		tcv.getTableCombo().setLayoutData(new GridData(125, SWT.DEFAULT));
		tcv.getTableCombo().setShowTableHeader(true);

		// set the content provider
		tcv.setContentProvider(ArrayContentProvider.getInstance());
		
		// set the label provider
		tcv.setLabelProvider(new ThreeLabelProvider());
		
		// tell the TableCombo that I want 3 columns autosized with the following column headers.
		tcv.getTableCombo().defineColumns(new String[] { "Id", "Description", "Computed"});
		
		// set which column index will be used to display the selected item.
		tcv.getTableCombo().setDisplayColumnIndex(2);
		
		// load the data
		tcv.setInput(modelList);
		
		// add listener
		tcv.addSelectionChangedListener(new ItemSelected("Sample5"));
		
		////////////////////////////////////////////////////////////////////////
		// Sample #6
		////////////////////////////////////////////////////////////////////////
		// create label
		label = new Label(group, SWT.NONE);
		label.setText("Three Columns (First Column, Fixed Width):");
		
		// create TableCombo
		tcv = new TableComboViewer(group, SWT.READ_ONLY | SWT.BORDER);
		tcv.getTableCombo().setLayoutData(new GridData(125, SWT.DEFAULT));		
		tcv.getTableCombo().setShowTableHeader(true);

		// set the content provider
		tcv.setContentProvider(ArrayContentProvider.getInstance());
		
		// set the label provider
		tcv.setLabelProvider(new ThreeLabelProvider());
		
		// tell the TableCombo that I want 3 columns autosized with the following column headers.
		tcv.getTableCombo().defineColumns(new String[] { "Id", "Description", "Computed"}, 
			new int[] { 50 , SWT.DEFAULT, SWT.DEFAULT});
		
		// set which column index will be used to display the selected item.
		tcv.getTableCombo().setDisplayColumnIndex(2);
		
		// load the data
		tcv.setInput(modelList);
		
		// add listener
		tcv.addSelectionChangedListener(new ItemSelected("Sample6"));
		
		////////////////////////////////////////////////////////////////////////
		// Sample #7
		////////////////////////////////////////////////////////////////////////
		// create label
		label = new Label(group, SWT.NONE);
		label.setText("Three Columns (With Table Width 75%):");
		
		// create TableCombo
		tcv = new TableComboViewer(group, SWT.READ_ONLY | SWT.BORDER);
		tcv.getTableCombo().setLayoutData(new GridData(125, SWT.DEFAULT));		
		tcv.getTableCombo().setShowTableHeader(true);

		// set the content provider
		tcv.setContentProvider(ArrayContentProvider.getInstance());
		
		// set the label provider
		tcv.setLabelProvider(new ThreeLabelProvider());
		
		// tell the TableCombo that I want 3 columns autosized with the following column headers.
		tcv.getTableCombo().defineColumns(new String[] { "Id", "Description", "Computed"});
		
		// set which column index will be used to display the selected item.
		tcv.getTableCombo().setDisplayColumnIndex(2);
		
		// set the table width percentage to 75%
		tcv.getTableCombo().setTableWidthPercentage(75);
		
		// load the data
		tcv.setInput(modelList);
		
		// add listener
		tcv.addSelectionChangedListener(new ItemSelected("Sample7"));
		
		
		shell.open();

		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		
		// dispose of the font
		boldFont.dispose();
		
		display.dispose ();
	}

	private static class SingleItemLabelProvider extends LabelProvider implements ITableLabelProvider {
		/**
		 * We return null, because we don't support images yet.
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage (Object element, int columnIndex) {
			return null;
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText (Object element, int columnIndex) {
			
			Model item = (Model)element;
			
			switch (columnIndex) {
				case 0: return item.getDescription();
			}
			return "";
		}
	}

	private static class SingleImageItemLabelProvider extends LabelProvider implements ITableLabelProvider {
		/**
		 * We return null, because we don't support images yet.
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage (Object element, int columnIndex) {
			
			Model item = (Model)element;
			
			if (columnIndex == 0) {
				int itemId = item.getId();
				
				if (itemId == 1 || itemId == 7 || itemId == 13 || itemId == 19) {
					return testImage;
				}
				else if (itemId == 3 || itemId == 9 || itemId == 15) {
					return test2Image;
				}
				else if (itemId == 5 || itemId == 11 || itemId == 17) {
					return test3Image;
				}
			}
			
			return null;
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText (Object element, int columnIndex) {
			
			Model item = (Model)element;
			
			switch (columnIndex) {
				case 0: return item.getDescription();
			}
			return "";
		}
	}
	

	
	private static class MultipleLabelProvider extends LabelProvider implements ITableLabelProvider {
		/**
		 * We return null, because we don't support images yet.
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage (Object element, int columnIndex) {
			return null;
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText (Object element, int columnIndex) {
			
			Model model = (Model)element;
			
			switch (columnIndex) {
			case 0: return String.valueOf(model.getId());
			case 1: return model.getDescription();
			}
			return "";
		}
	}

	private static class ThreeLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider, ITableFontProvider {
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage (Object element, int columnIndex) {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText (Object element, int columnIndex) {
			
			Model model = (Model)element;
			
			switch (columnIndex) {
			case 0: return String.valueOf(model.getId());
			case 1: return model.getDescription();
			case 2: return model.getId() + " - " + model.getDescription();
			}
			return "";
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object, int)
		 */
		public Color getBackground(Object arg0, int arg1) {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object, int)
		 */
		public Color getForeground(Object element, int columnIndex) {
			Model item = (Model)element;
			
			if (item.getId() == 1 || item.getId() == 15) {
				return darkRed;
			}
			else if (item.getId() == 5 || item.getId() == 20) {
				return darkBlue;
			}
			else if (item.getId() == 10) {
				return darkGreen;
			}
			else {
				return null;
			}
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableFontProvider#getFont(java.lang.Object, int)
		 */
		public Font getFont(Object element, int index) {
			Model item = (Model)element;
			
			if (item.getId() == 1 || item.getId() == 15) {
				return boldFont;
			}
			else if (item.getId() == 5 || item.getId() == 20) {
				return boldFont;
			}
			else if (item.getId() == 10) {
				return boldFont;
			}
			else {
				return null;
			}			
		}		
	}
	
	private static class MultipleColorLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider, ITableFontProvider {
		/**
		 * We return null, because we don't support images yet.
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage (Object element, int columnIndex) {
			return null;
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText (Object element, int columnIndex) {
			
			Model model = (Model)element;
			
			switch (columnIndex) {
			case 0: return String.valueOf(model.getId());
			case 1: return model.getDescription();
			}
			return "";
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object, int)
		 */
		public Color getBackground(Object element, int columnIndex) {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object, int)
		 */
		public Color getForeground(Object element, int columnIndex) {
			Model item = (Model)element;
			
			if (item.getId() == 1 || item.getId() == 15) {
				return darkRed;
			}
			else if (item.getId() == 5 || item.getId() == 20) {
				return darkBlue;
			}
			else if (item.getId() == 10) {
				return darkGreen;
			}
			else {
				return null;
			}
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableFontProvider#getFont(java.lang.Object, int)
		 */
		public Font getFont(Object element, int index) {
			Model item = (Model)element;
			
			if (item.getId() == 1 || item.getId() == 15 || 
				item.getId() == 5 || item.getId() == 20 ||
				item.getId() == 10) {
				return boldFont;
			}
			else {
				return null;
			}			
		}		
	}
	
	/**
	 * load the Model data.
	 * @return
	 */
	private static List loadModel() {
		List items = new ArrayList();
		items.add(new Model(1, "One"));
		items.add(new Model(2, "Two"));
		items.add(new Model(3, "Three"));
		items.add(new Model(4, "Four"));
		items.add(new Model(5, "Five"));
		items.add(new Model(6, "Six"));
		items.add(new Model(7, "Seven"));
		items.add(new Model(8, "Eight"));
		items.add(new Model(9, "Nine"));
		items.add(new Model(10, "Ten"));
		items.add(new Model(11, "Eleven"));
		items.add(new Model(12, "Twelve"));
		items.add(new Model(13, "Thirteen"));
		items.add(new Model(14, "Fourteen"));
		items.add(new Model(15, "Fiveteen"));
		items.add(new Model(16, "Sixteen"));
		items.add(new Model(17, "Seventeen"));
		items.add(new Model(18, "Eighteen"));
		items.add(new Model(19, "Nineteen"));
		items.add(new Model(20, "Twenty"));		
		
		return items;
	}	
	
	private static class ItemSelected implements ISelectionChangedListener {
		
		private String text;
		
		public ItemSelected(String text) {
			this.text = text;
		}

		public void selectionChanged(SelectionChangedEvent event) {
			Model model = (Model)((IStructuredSelection)event.getSelection()).getFirstElement();

			listenerGroup.setText("Listener Results - (" + text + ")");
			listenerResults.setText(model.toString());
		}
	}
}

