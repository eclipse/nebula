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

package org.eclipse.nebula.snippets.tablecombo;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.tablecombo.TableCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * Show basic features of TableCombo
 *
 */
public class TableComboSnippet1 {

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
		shell.setText("TableCombo Snippet 1");
		shell.setSize(600, 400);
		shell.setLayout(new GridLayout());
		
		// create group
		Group group = new Group(shell, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
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
		TableCombo tc = new TableCombo(group, SWT.BORDER | SWT.READ_ONLY);
		tc.setLayoutData(new GridData(125, SWT.DEFAULT));

		// load the dataset.
		loadSingleDataset(tc.getTable());

		// add listener
		tc.addSelectionListener(new ItemSelected(tc, "Sample1"));
		
		////////////////////////////////////////////////////////////////////////
		// Sample #2
		////////////////////////////////////////////////////////////////////////
		
		label = new Label(group, SWT.NONE);
		label.setText("Single Column (With Images)");
		
		// create TableCombo
		tc = new TableCombo(group, SWT.BORDER | SWT.READ_ONLY);
		tc.setLayoutData(new GridData(125, SWT.DEFAULT));

		// load the dataset.
		loadSingleDatasetWithImages(tc.getTable());
		
		// add listener
		tc.addSelectionListener(new ItemSelected(tc, "Sample2"));
		////////////////////////////////////////////////////////////////////////
		// Sample #3
		////////////////////////////////////////////////////////////////////////
		// create label
		label = new Label(group, SWT.NONE);
		label.setText("Two Columns:");
		
		// create TableCombo
		tc = new TableCombo(group, SWT.BORDER | SWT.READ_ONLY);
		tc.setLayoutData(new GridData(125, SWT.DEFAULT));

		// tell the TableCombo that I want 2 blank columns auto sized.
		tc.defineColumns(2);
		
		// set which column will be used for the selected item.
		tc.setDisplayColumnIndex(1);
		
		tc.setToolTipText("This is a tool tip.");
		
		// load the dataset.
		loadTwoColumnDataset(tc.getTable());
		
		// add listener
		tc.addSelectionListener(new ItemSelected(tc, "Sample3"));

		////////////////////////////////////////////////////////////////////////
		// Sample #4
		////////////////////////////////////////////////////////////////////////
		// create label
		label = new Label(group, SWT.NONE);
		label.setText("Two Columns (With Colors && Fonts):");
		
		// create TableCombo
		tc = new TableCombo(group, SWT.BORDER | SWT.READ_ONLY);
		tc.setLayoutData(new GridData(125, SWT.DEFAULT));

		// tell the TableCombo that I want 2 blank columns auto sized.
		tc.defineColumns(2);
		
		// set which column will be used for the selected item.
		tc.setDisplayColumnIndex(1);
		
		// load the dataset.
		loadTwoColumnDatasetWithColorsAndFonts(tc.getTable());
		
		// add listener
		tc.addSelectionListener(new ItemSelected(tc, "Sample4"));
		
		////////////////////////////////////////////////////////////////////////
		// Sample #5
		////////////////////////////////////////////////////////////////////////
		// create label
		label = new Label(group, SWT.NONE);
		label.setText("Three Columns (With Colors && Fonts && Header):");
		
		// create TableCombo
		tc = new TableCombo(group, SWT.BORDER | SWT.READ_ONLY);
		tc.setLayoutData(new GridData(125, SWT.DEFAULT));

		// tell the TableCombo that I want 3 columns autosized with the following column headers.
		tc.defineColumns(new String[] { "Id", "Description", "Computed"});
		
		// set which column will be used for the selected item.
		tc.setDisplayColumnIndex(2);
		
		// turn on the table header.
		tc.setShowTableHeader(true);
		
		// load the dataset.
		loadThreeColumnDatasetWithColorsAndFonts(tc.getTable());
		
		// add listener
		tc.addSelectionListener(new ItemSelected(tc, "Sample5"));
		
		////////////////////////////////////////////////////////////////////////
		// Sample #6
		////////////////////////////////////////////////////////////////////////
		// create label
		label = new Label(group, SWT.NONE);
		label.setText("Three Columns (First Column, Fixed Width):");
		
		// create TableCombo
		tc = new TableCombo(group, SWT.BORDER | SWT.READ_ONLY);
		tc.setLayoutData(new GridData(125, SWT.DEFAULT));

		// tell the TableCombo that I want 3 columns autosized with the following column headers.
		tc.defineColumns(new String[] { "Id", "Description", "Computed"}, 
			new int[] { 50 , SWT.DEFAULT, SWT.DEFAULT});
		
		// set which column will be used for the selected item.
		tc.setDisplayColumnIndex(2);
		
		// turn on the table header.
		tc.setShowTableHeader(true);
		
		// load the dataset.
		loadThreeColumnDatasetWithColorsAndFonts(tc.getTable());
		
		// add listener
		tc.addSelectionListener(new ItemSelected(tc, "Sample6"));
		
		////////////////////////////////////////////////////////////////////////
		// Sample #7
		////////////////////////////////////////////////////////////////////////
		// create label
		label = new Label(group, SWT.NONE);
		label.setText("Three Columns (With Table Width 75%):");
		
		// create TableCombo
		tc = new TableCombo(group, SWT.BORDER | SWT.READ_ONLY);
		tc.setLayoutData(new GridData(125, SWT.DEFAULT));

		// tell the TableCombo that I want 3 columns autosized with the following column headers.
		tc.defineColumns(new String[] { "Id", "Description", "Computed"});
		
		// set which column will be used for the selected item.
		tc.setDisplayColumnIndex(2);
		
		// turn on the table header.
		tc.setShowTableHeader(true);

		// load the dataset.
		loadThreeColumnDatasetWithColorsAndFonts(tc.getTable());
		
		// set the table width % to 75%
		tc.setTableWidthPercentage(75);
		
		// add listener
		tc.addSelectionListener(new ItemSelected(tc, "Sample7"));
		
		// open the shell.
		shell.open();

		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		
		// dispose of the font
		boldFont.dispose();
		
		// dispose images
		testImage.dispose();
		test2Image.dispose();
		test3Image.dispose();
		
		// dispose colors
		darkRed.dispose();
		darkBlue.dispose();
		darkGreen.dispose();
		
		// dispose display
		display.dispose ();
	}
	
	/**
	 * load a list of rows with a single column
	 * @return
	 */
	private static List loadSingleDataset(Table table) {
		List rowList = new ArrayList();
		
		int total = (modelList == null ? 0 : modelList.size());
		
		for (int index=0; index < total; index++) {
			TableItem ti = new TableItem(table, SWT.NONE);
			Model model = (Model)modelList.get(index);
			ti.setText(model.getDescription());
			rowList.add(ti);
		}
		
		return rowList;
	}
	
	/**
	 * load a list of rows with a single column that includes images
	 * @return
	 */
	private static List loadSingleDatasetWithImages(Table table) {
		List list = loadSingleDataset(table);
		
		int total = (list == null ? 0 : list.size());
		
		for (int index=0; index < total; index++) {
			TableItem ti = ((TableItem)(list.get(index)));
			
			if (index == 1 || index == 7 || index == 13 || index == 19) {
				ti.setImage(0, testImage);
			}
			else if (index == 3 || index == 9 || index == 15) {
				ti.setImage(0, test2Image);
			}
			else if (index == 5 || index == 11 || index == 17) {
				ti.setImage(0, test3Image);
			}
		}
		
		return list;
	}
	
	/**
	 * load a list of rows with 2 columns in each row.
	 * @return
	 */
	private static List loadTwoColumnDataset(Table table) {
		List rowList = new ArrayList();
		
		int total = (modelList == null ? 0 : modelList.size());
		
		for (int index=0; index < total; index++) {
			TableItem ti = new TableItem(table, SWT.NONE);
			Model model = (Model)modelList.get(index);
			ti.setText(new String[] { String.valueOf(model.getId()), model.getDescription() });
			rowList.add(ti);			
		}
		
		return rowList;
	}

	/**
	 * load a list of rows with 2 columns that includes colors and fonts.
	 * @return
	 */
	private static List loadTwoColumnDatasetWithColorsAndFonts(Table table) {
		List list = loadTwoColumnDataset(table);
		
		int total = (list == null ? 0 : list.size());
		
		for (int index=0; index < total; index++) {
			TableItem ti = ((TableItem)(list.get(index)));
			
			if (index == 0 || index == 14) {
				ti.setForeground(darkRed);
				ti.setFont(boldFont);
			}
			else if (index == 4 || index == 19) {
				ti.setForeground(darkBlue);
				ti.setFont(boldFont);
			}
			else if (index == 9) {
				ti.setForeground(darkGreen);
				ti.setFont(boldFont);
			}
		}
		
		return list;
	}
	
	/**
	 * load a list of rows with 3 columns
	 * @return
	 */
	private static List loadThreeColumnDataset(Table table) {
		List rowList = new ArrayList();
		
		int total = (modelList == null ? 0 : modelList.size());
		
		for (int index=0; index < total; index++) {
			TableItem ti = new TableItem(table, SWT.NONE);
			Model model = (Model)modelList.get(index);
			ti.setText(new String[] { String.valueOf(model.getId()), model.getDescription(), 
				String.valueOf(model.getId()) + " - " + model.getDescription() });
			rowList.add(ti);			
		}
		
		return rowList;
	}

	/**
	 * load a list of rows with 3 columns that includes colors and fonts.
	 * @return
	 */
	private static List loadThreeColumnDatasetWithColorsAndFonts(Table table) {
		List list = loadThreeColumnDataset(table);
		
		int total = (list == null ? 0 : list.size());
		
		for (int index=0; index < total; index++) {
			TableItem ti = ((TableItem)(list.get(index)));
			
			if (index == 0 || index == 14) {
				ti.setForeground(darkRed);
				ti.setFont(boldFont);
			}
			else if (index == 4 || index == 19) {
				ti.setForeground(darkBlue);
				ti.setFont(boldFont);
			}
			else if (index == 9) {
				ti.setForeground(darkGreen);
				ti.setFont(boldFont);
			}
		}
		
		return list;
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

	private static class ItemSelected extends SelectionAdapter {
		
		private TableCombo tc;
		private String text;
		
		public ItemSelected(TableCombo tc, String text) {
			this.tc = tc;
			this.text = text;
		}

		public void widgetSelected(SelectionEvent e) {
			listenerGroup.setText("Listener Results - (" + text + ")");
			listenerResults.setText(tc.getText() == null ? "null" : tc.getText());
		}
	}
}

