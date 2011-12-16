/*******************************************************************************
 * Copyright (c) 2006-2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    ewuillai - initial implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.tablecombo.example;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.nebula.widgets.tablecombo.TableCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.nebula.examples.AbstractExampleTab;
import org.eclipse.nebula.examples.ButtonFactory;
import org.eclipse.nebula.examples.ExamplesView;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TableItem;

public class TableComboExampleTab extends AbstractExampleTab {
	private TableCombo tableCombo = null;

	// Style group
	private Button borderStyle;
	private Button readOnlyStyle;
	private Button flatStyle;

	// GUI settings group
	private Button showGrid;
	private Button showHeader;
	private Button showImageInSelection;
	private Button showCustomFontInSelection;
	private Spinner tableWidthPct;
	private Spinner numOfColumnsToDisplaySpinner;
	private Spinner columnIndexToDisplayWhenSelected;
	private Spinner numOfRowsDisplayed;
	private Button  showImageInCombo;
	private Button  showCustomFontInCombo;
	
	private static Font boldFont;
	private static Image testImage;
	private static Image test2Image;
	private static Image test3Image;
	private static Color darkRed;
	private static Color darkBlue;
	private static Color darkGreen;
	
	// create listener for recreating the control.
	private Listener recreateListener = new Listener() {
		public void handleEvent(Event event) {
			recreateExample();
		}
	};
	
	public TableComboExampleTab() {
		super();
		
		// create bold and italic font.
		ExamplesView.setFont("tableComboCustFont", new FontData[]{
			new FontData("Arial", 8, SWT.BOLD | SWT.ITALIC)});
		boldFont = ExamplesView.getFont("tableComboCustFont"); 
		
		// create images
		testImage = ExamplesView.getImage("icons/in_ec_ov_success_16x16.gif"); 
		test2Image = ExamplesView.getImage("icons/in_ec_ov_warning_16x16.gif");
		test3Image = ExamplesView.getImage("icons/invalid_build_tool_16x16.gif");
		
		// create colors
		darkRed = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED);
		darkBlue = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE);
		darkGreen = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
	}

	/** 
	 * {@inheritDoc}
	 */
	public Control createControl(Composite parent) {
		
		// set the style.
		int style = SWT.BORDER | SWT.READ_ONLY;
		if ( borderStyle.getSelection() ) {
			style |= SWT.BORDER;
		}
		if ( readOnlyStyle.getSelection() ) {
			style |= SWT.READ_ONLY;
		}
		if ( flatStyle.getSelection() ) {
			style |= SWT.FLAT;
		}
		
		// create table combo instance.
		tableCombo = new TableCombo(parent, style);

		// set options.
		tableCombo.setShowTableLines(showGrid.getSelection());
		tableCombo.setShowTableHeader(showHeader.getSelection());
		tableCombo.setDisplayColumnIndex(columnIndexToDisplayWhenSelected.getSelection() - 1);
		tableCombo.setShowImageWithinSelection(showImageInSelection.getSelection());
		tableCombo.setShowColorWithinSelection(showCustomFontInSelection.getSelection());
		tableCombo.setShowFontWithinSelection(showCustomFontInSelection.getSelection());
		tableCombo.setTableWidthPercentage(tableWidthPct.getSelection());
		tableCombo.setVisibleItemCount(numOfRowsDisplayed.getSelection());
		
		// load the model and data.
		loadData(loadModel(), tableCombo);

		return tableCombo;
	}

	/** 
	 * Creates the Parameters Group.
	 * {@inheritDoc}
	 */
	public void createParameters(Composite parent) {
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(2).applyTo(parent);
		createStyleGroup(parent);
		createOptionsGroup(parent);
		createSampleDataGroup(parent);
	}
	
	/** 
	 * generate the links for the example.
	 * {@inheritDoc}
	 */
	public String[] createLinks() {
        String[] links = new String[3];
        
        links[0] = "<a href=\"http://www.eclipse.org/nebula/widgets/tablecombo/tablecombo.php\">TableCombo Home Page</a>";
        links[1] = "<a href=\"http://www.eclipse.org/nebula/snippets.php#TableCombo\">Snippets</a>";
        links[2] = "<a href=\"https://bugs.eclipse.org/bugs/buglist.cgi?query_format=advanced&short_desc_type=allwordssubstr&short_desc=&classification=Technology&product=Nebula&component=TableCombo&long_desc_type=allwordssubstr&long_desc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&status_whiteboard_type=allwordssubstr&status_whiteboard=&keywords_type=allwords&keywords=&emailtype1=substring&email1=&emailtype2=substring&email2=&bugidtype=include&bug_id=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&cmdtype=doit&order=Reuse+same+sort+as+last+time&field0-0-0=noop&type0-0-0=noop&value0-0-0=\">Bugs</a>";
        
        return links;
	}

	/**
	 * Creates the Options Group
	 * @param parent
	 */
	private void createOptionsGroup(Composite parent) {
		Group gp = new Group(parent, SWT.NONE);
		gp.setText("Options");
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(gp);
		gp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		GridData gd = new GridData(SWT.FILL, SWT.DEFAULT, true, false);
		gd.horizontalSpan = 2;

		// create Show Header checkbox.
		showHeader = ButtonFactory.create(gp,SWT.CHECK, "Show Header", recreateListener , false);
		showHeader.setToolTipText("Hides or displays the table column headers.");
		showHeader.setLayoutData(gd);
		
		// create show Grid checkbox
		showGrid = ButtonFactory.create(gp,SWT.CHECK, "Show Grid Lines", recreateListener , false);
		showGrid.setToolTipText("Hides or displays grid lines in the table.");
		showGrid.setLayoutData(gd);
		
		// create showImageInSelection checkbox
		showImageInSelection = ButtonFactory.create(gp,SWT.CHECK, "Show Image In Selection", recreateListener , true);
		showImageInSelection.setLayoutData(gd);
		showImageInSelection.setToolTipText("Sets whether or not to display the selected table row item's image.");
		
		// create showCustomFontInSelection checkbox
		showCustomFontInSelection = ButtonFactory.create(gp,SWT.CHECK, "Show Custom Font In Selection", recreateListener , true);
		showCustomFontInSelection.setLayoutData(gd);
		showCustomFontInSelection.setToolTipText("Sets whether or not to display custom fonts in the selected text.");
		
		// create table width percentage composite.
        Label l = new Label(gp,SWT.NONE);
        l.setText("Table Width Pct:"); 
        gd = new GridData(25, SWT.DEFAULT);
        tableWidthPct = new Spinner(gp,SWT.BORDER);
        tableWidthPct.setValues(100,25,100,0,1,1);
        tableWidthPct.addListener(SWT.Selection, recreateListener);
        tableWidthPct.setLayoutData(gd);
        tableWidthPct.setToolTipText("Is the percentage of the total table width to display.");
        
        // create number of rows returned
        l = new Label(gp,SWT.NONE);
        l.setText("Num Rows Displayed:"); 
        numOfRowsDisplayed = new Spinner(gp,SWT.BORDER);
        numOfRowsDisplayed.setValues(7,1,20,0,1,1);
        numOfRowsDisplayed.addListener(SWT.Selection, recreateListener);      
        numOfRowsDisplayed.setLayoutData(gd);
        numOfRowsDisplayed.setToolTipText("The number of viewable rows displayed in the table.");
	}
	
	/**
	 * Creates the Sample Data Group
	 * @param parent
	 */
	private void createSampleDataGroup(Composite parent) {
		
		// create Sample Group
		Group sampleGroup = new Group(parent, SWT.NONE);
		sampleGroup.setText("Sample Data");
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(sampleGroup);
		sampleGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		GridData gd = new GridData(15, SWT.DEFAULT);
		
		Label l = new Label(sampleGroup, SWT.NONE);
		l.setText("# Columns:");
		l.setToolTipText("Number of Columns To Display In Table.");
		numOfColumnsToDisplaySpinner = new Spinner(sampleGroup,SWT.BORDER);
		numOfColumnsToDisplaySpinner.setValues(1,1,3,0,1,1);
		numOfColumnsToDisplaySpinner.setToolTipText("Number of Columns To Display In Table.");
		numOfColumnsToDisplaySpinner.setLayoutData(gd);
		numOfColumnsToDisplaySpinner.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
            	if (numOfColumnsToDisplaySpinner.getSelection() == 1) {
            		columnIndexToDisplayWhenSelected.setSelection(1);
            		columnIndexToDisplayWhenSelected.setEnabled(false);
            	}
            	else {
            		columnIndexToDisplayWhenSelected.setEnabled(true);
            		columnIndexToDisplayWhenSelected.setMaximum(
            			numOfColumnsToDisplaySpinner.getSelection());
            	}
            	recreateExample();
            }
        });  

		// create selected column
		l = new Label(sampleGroup, SWT.NONE);
		l.setText("Selected Value:");
		l.setToolTipText("Column to be displayed when selected");
		
		columnIndexToDisplayWhenSelected = new Spinner(sampleGroup, SWT.BORDER);
		columnIndexToDisplayWhenSelected.setValues(1,1,3,0,1,1);
		columnIndexToDisplayWhenSelected.addListener(SWT.Selection, recreateListener);
		columnIndexToDisplayWhenSelected.setEnabled(false);
		columnIndexToDisplayWhenSelected.setToolTipText("Column to be displayed when selected");
		columnIndexToDisplayWhenSelected.setLayoutData(gd);
		
		showImageInCombo = ButtonFactory.create(sampleGroup,SWT.CHECK, "Include Images", recreateListener , false);
		showImageInCombo.setToolTipText("Sets whether or not to include sample images.");
		gd = new GridData(SWT.FILL, SWT.DEFAULT, true, false);
		gd.horizontalSpan = 2;
		showImageInCombo.setLayoutData(gd);
		
		showCustomFontInCombo = ButtonFactory.create(sampleGroup,SWT.CHECK, "Include Fonts", recreateListener , false);
		showCustomFontInCombo.setToolTipText("Sets whether or not to include sample fonts.");
		showCustomFontInCombo.setLayoutData(gd);
	}

	/**
	 * @param parent
	 */
	private void createStyleGroup(Composite parent) {
		Group other = new Group(parent, SWT.NONE);
		other.setText("Style");
        other.setLayout(new GridLayout());
        other.setLayoutData(new GridData(GridData.FILL_VERTICAL));

		borderStyle = new Button(other, SWT.CHECK);
		borderStyle.setText("SWT.BORDER");
		borderStyle.setSelection(true);
		borderStyle.addListener(SWT.Selection, recreateListener);

		readOnlyStyle = new Button(other, SWT.CHECK);
		readOnlyStyle.setText("SWT.READ_ONLY");
		readOnlyStyle.setSelection(true);
		readOnlyStyle.addListener(SWT.Selection, recreateListener);

		flatStyle = new Button(other, SWT.CHECK);
		flatStyle.setText("SWT.FLAT");
		flatStyle.addListener(SWT.Selection, recreateListener);
	}

	/**
	 * @param modelList
	 * @param tc
	 * @return
	 */
	private void loadData(List modelList, TableCombo tc) {
		// get the number of columns to build in the table
		int numCols = numOfColumnsToDisplaySpinner.getSelection();

		// define the columns
		if (numCols == 1) {
			tc.defineColumns(new String[] {"Id"});
		}
		else if (numCols == 2) {
			tc.defineColumns(new String[] {"Id", "Description"});
		}
		else {
			tc.defineColumns(new String[] {"Id", "Description", "Computed"});	
		}
		
		int total = (modelList == null ? 0 : modelList.size());
		
		// now create the table items
		for (int index=0; index < total; index++) {
			TableItem ti = new TableItem(tc.getTable(), SWT.NONE);
			Model model = (Model)modelList.get(index);
			
			// set the column text
			if (numCols == 1) {
				ti.setText(0, model.getDescription());
			}
			else {
				ti.setText(0, String.valueOf(model.getId()));
			}
			
			if (numCols >= 2) {
				ti.setText(1, model.getDescription());
			}
			
			if (numCols == 3) {
				ti.setText(2, model.getId() + " - " + model.getDescription());
			}
			
			// add images if needed.
			if (showImageInCombo.getSelection()) {
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
			
			if (showCustomFontInCombo.getSelection()) {
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
		}
	}
	
	/**
	 * Loads the sample model.
	 * @return
	 */
	private List loadModel() {
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
	
	/**
	 * Model class
	 *
	 * @author martyj
	 */
	private class Model {
		private int id;
		private String description;
		
		public Model(int id, String description) {
			this.id = id;
			this.description = description;
		}

		public int getId() {
			return id;
		}

		public String getDescription() {
			return description;
		}

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((description == null) ? 0 : description.hashCode());
			result = prime * result + id;
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Model other = (Model) obj;
			if (description == null) {
				if (other.description != null)
					return false;
			} else if (!description.equals(other.description))
				return false;
			if (id != other.id)
				return false;
			return true;
		}

		public String toString() {
			return "[id=" + id + "] [description=" + description + "]";
		}
	}
}