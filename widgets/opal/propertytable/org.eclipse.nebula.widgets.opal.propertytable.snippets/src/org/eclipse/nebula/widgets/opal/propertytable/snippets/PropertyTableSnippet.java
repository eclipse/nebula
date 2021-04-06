/*******************************************************************************
 * Copyright (c) 2012 Laurent CARON
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API
 * and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.propertytable.snippets;

import java.util.Locale;

import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.nebula.widgets.opal.propertytable.PTProperty;
import org.eclipse.nebula.widgets.opal.propertytable.PropertyTable;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTCheckboxEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTColorEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTComboEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTDateEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTDimensionEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTDirectoryEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTFileEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTFloatEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTFontEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTInsetsEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTIntegerEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTPasswordEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTRectangleEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTSpinnerEditor;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTURLEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * This snippet demonstrates the PropertyTable widget
 *
 */
public class PropertyTableSnippet {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		Locale.setDefault(Locale.ENGLISH);

		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("PropertyTable snippet");
		shell.setLayout(new FillLayout(SWT.VERTICAL));

		final TabFolder tabFolder = new TabFolder(shell, SWT.BORDER);

		final TabItem item1 = new TabItem(tabFolder, SWT.NONE);
		item1.setText("First");
		item1.setControl(buildPropertyTable(tabFolder, true, true, true));

		final TabItem item2 = new TabItem(tabFolder, SWT.NONE);
		item2.setText("Second");
		item2.setControl(buildPropertyTable(tabFolder, false, true, false));

		final TabItem item3 = new TabItem(tabFolder, SWT.NONE);
		item3.setText("Third");
		item3.setControl(buildPropertyTable(tabFolder, true, false, true));

		final TabItem item4 = new TabItem(tabFolder, SWT.NONE);
		item4.setText("Forth");
		item4.setControl(buildPropertyTable(tabFolder, true, false, false));

		shell.setSize(800, 600);
		shell.open();
		SWTGraphicUtil.centerShell(shell);

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();
	}

	/**
	 * Build a property table
	 *
	 * @param tabFolder tabFolder that holds the property table
	 * @param showButton if <code>true</code>, show buttons
	 * @param showAsCategory if <code>true</code>, show property as categories. If
	 *            <code>false</code>, show property as a flat list
	 * @param showDescription if <code>true</code>, show description
	 * @return a property table
	 */
	private static PropertyTable buildPropertyTable(final TabFolder tabFolder, final boolean showButton, final boolean showAsCategory, final boolean showDescription) {
		final PropertyTable table = new PropertyTable(tabFolder, SWT.NONE);

		if (showButton) {
			table.showButtons();
		} else {
			table.hideButtons();
		}

		if (showAsCategory) {
			table.viewAsCategories();
		} else {
			table.viewAsFlatList();
		}

		if (showDescription) {
			table.showDescription();
		} else {
			table.hideDescription();
		}
		table.addProperty(new PTProperty("id", "Identifier", "Description for identifier", "My id")).setCategory("General");
		table.addProperty(new PTProperty("text", "Description", "Description for the description field", "blahblah...")).setCategory("General");
		table.addProperty(new PTProperty("url", "URL:", "This is a nice <b>URL</b>", "http://www.google.com").setCategory("General")).setEditor(new PTURLEditor());
		table.addProperty(new PTProperty("password", "Password", "Enter your <i>password</i> and keep it secret...", "password")).setCategory("General").setEditor(new PTPasswordEditor());
		table.addProperty(new PTProperty("longText", "Long", "Description for the description field, which is soooooo long that you have to scroll to see eveything and this is not user-friendly obviously.\n" + //
				"I keep typing but I've no idea, I just want to make it as long as possible, but\nI've definitively no idea.\n" + //
				"I think this is the last line...\nDid you know that I'm a huge fan of Pink Floyd ?", //
				"too long...")).setCategory("General");

		table.addProperty(new PTProperty("int", "An integer", "Type any integer", "123")).setCategory("Number").setEditor(new PTIntegerEditor());
		table.addProperty(new PTProperty("float", "A float", "Type any float", "123.45")).setCategory("Number").setEditor(new PTFloatEditor());
		table.addProperty(new PTProperty("spinner", "Another integer", "Use a spinner to enter an integer")).setCategory("Number").setEditor(new PTSpinnerEditor(0, 100));

		table.addProperty(new PTProperty("directory", "Directory", "Select a directory")).setCategory("Directory/File").setEditor(new PTDirectoryEditor());
		table.addProperty(new PTProperty("file", "File", "Select a file")).setCategory("Directory/File").setEditor(new PTFileEditor());

		table.addProperty(new PTProperty("comboReadOnly", "Combo (read-only)", "A simple combo with seasons")).setCategory("Combo").setEditor(new PTComboEditor(true, new Object[] { "Spring", "Summer", "Autumn", "Winter" }));
		table.addProperty(new PTProperty("combo", "Combo", "A combo that is not read-only")).setCategory("Combo").setEditor(new PTComboEditor("Value 1", "Value 2", "Value 3"));

		table.addProperty(new PTProperty("cb", "Checkbox", "A checkbox")).setCategory("Checkbox").setEditor(new PTCheckboxEditor()).setCategory("Checkbox");
		table.addProperty(new PTProperty("cb2", "Checkbox (disabled)", "A disabled checkbox...")).setEditor(new PTCheckboxEditor()).setCategory("Checkbox").setEnabled(false);

		table.addProperty(new PTProperty("color", "Color", "Pick it !")).setCategory("Misc").setEditor(new PTColorEditor());
		table.addProperty(new PTProperty("font", "Font", "Pick again my friend")).setEditor(new PTFontEditor()).setCategory("Misc");
		table.addProperty(new PTProperty("dimension", "Dimension", "A dimension is composed of a width and a height")).setCategory("Misc").setEditor(new PTDimensionEditor());
		table.addProperty(new PTProperty("rectangle", "Rectangle", "A rectangle is composed of a position (x,y) and a dimension(width,height)")).setCategory("Misc").setEditor(new PTRectangleEditor());
		table.addProperty(new PTProperty("inset", "Inset", "An inset is composed of the following fields:top,left,bottom,right)")).setCategory("Misc").setEditor(new PTInsetsEditor());
		table.addProperty(new PTProperty("date", "Date", "Well, is there something more to say ?")).setCategory("Misc").setEditor(new PTDateEditor());

		return table;
	}

}
