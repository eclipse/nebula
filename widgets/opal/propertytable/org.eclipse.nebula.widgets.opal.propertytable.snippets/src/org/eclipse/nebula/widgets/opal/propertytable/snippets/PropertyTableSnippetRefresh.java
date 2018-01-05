/*******************************************************************************
 * Copyright (c) 2012 Laurent CARON All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API
 * and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.propertytable.snippets;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This snippet demonstrates the PropertyTable widget
 *
 */
public class PropertyTableSnippetRefresh {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		Locale.setDefault(Locale.ENGLISH);

		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("PropertyTable snippet");
		shell.setLayout(new GridLayout(2, true));

		final Button button1 = new Button(shell, SWT.PUSH);
		button1.setText("First set of values");
		button1.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		final Button button2 = new Button(shell, SWT.PUSH);
		button2.setText("Second set of values");
		button2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		final PropertyTable table = buildPropertyTable(shell);
		table.setLayoutData(new GridData(GridData.FILL, GridData.FILL, //
				true, true, 2, 1));

		button1.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Map<String, Object> map = table.getProperties();
				map.put("id", "My id");
				map.put("text", "blahblah...");
				map.put("url", "http://www.google.com");
				map.put("password", "password");
				map.put("int", "123");
				map.put("float", "123.45");
				map.put("spinner", null);
				map.put("directory", null);
				map.put("file", null);
				map.put("comboReadOnly", null);
				map.put("combo", null);
				map.put("cb", Boolean.FALSE);
				map.put("color", null);
				map.put("font", null);
				map.put("dimension", null);
				map.put("rectangle", null);
				map.put("inset", null);
				map.put("date", null);
				table.setProperties(map);
			}
		});

		button2.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Map<String, Object> map = table.getProperties();
				map.put("id", "(2)My id");
				map.put("text", "(2)blahblah...");
				map.put("url", "(2)http://www.google.com");
				map.put("password", "(2)password");
				map.put("int", "1234");
				map.put("float", "1234.56");
				map.put("spinner", 12);
				map.put("directory", "C:/temp");
				map.put("file", "C:/temp/temp.txt");
				map.put("comboReadOnly", "Summer");
				map.put("combo", "Value 2");
				map.put("cb", Boolean.TRUE);
				map.put("color", null);
				map.put("font", null);
				map.put("dimension", null);
				map.put("rectangle", null);
				map.put("inset", null);
				map.put("date", new Date());
				table.setProperties(map);
			}
		});

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
	 */
	private static PropertyTable buildPropertyTable(final Composite composite) {
		final PropertyTable table = new PropertyTable(composite, SWT.NONE);

		table.showButtons();
		table.viewAsCategories();
		table.showDescription();

		table.addProperty(new PTProperty("id", "Identifier", "Description for identifier", "My id"))
				.setCategory("General");
		table.addProperty(new PTProperty("text", "Description", "Description for the description field", "blahblah..."))
				.setCategory("General");
		table.addProperty(new PTProperty("url", "URL:", "This is a nice <b>URL</b>", "http://www.google.com")
				.setCategory("General")).setEditor(new PTURLEditor());
		table.addProperty(
				new PTProperty("password", "Password", "Enter your <i>password</i> and keep it secret...", "password"))
				.setCategory("General").setEditor(new PTPasswordEditor());

		table.addProperty(new PTProperty("int", "An integer", "Type any integer", "123")).setCategory("Number")
				.setEditor(new PTIntegerEditor());
		table.addProperty(new PTProperty("float", "A float", "Type any float", "123.45")).setCategory("Number")
				.setEditor(new PTFloatEditor());
		table.addProperty(new PTProperty("spinner", "Another integer", "Use a spinner to enter an integer"))
				.setCategory("Number").setEditor(new PTSpinnerEditor(0, 100));

		table.addProperty(new PTProperty("directory", "Directory", "Select a directory")).setCategory("Directory/File")
				.setEditor(new PTDirectoryEditor());
		table.addProperty(new PTProperty("file", "File", "Select a file")).setCategory("Directory/File")
				.setEditor(new PTFileEditor());

		table.addProperty(new PTProperty("comboReadOnly", "Combo (read-only)", "A simple combo with seasons"))
				.setCategory("Combo")
				.setEditor(new PTComboEditor(true, new Object[] { "Spring", "Summer", "Autumn", "Winter" }));
		table.addProperty(new PTProperty("combo", "Combo", "A combo that is not read-only")).setCategory("Combo")
				.setEditor(new PTComboEditor("Value 1", "Value 2", "Value 3"));

		table.addProperty(new PTProperty("cb", "Checkbox", "A checkbox")).setCategory("Checkbox")
				.setEditor(new PTCheckboxEditor()).setCategory("Checkbox");
		table.addProperty(new PTProperty("cb2", "Checkbox (disabled)", "A disabled checkbox..."))
				.setEditor(new PTCheckboxEditor()).setCategory("Checkbox").setEnabled(false);

		table.addProperty(new PTProperty("color", "Color", "Pick it !")).setCategory("Misc")
				.setEditor(new PTColorEditor());
		table.addProperty(new PTProperty("font", "Font", "Pick again my friend")).setEditor(new PTFontEditor())
				.setCategory("Misc");
		table.addProperty(new PTProperty("dimension", "Dimension", "A dimension is composed of a width and a height"))
				.setCategory("Misc").setEditor(new PTDimensionEditor());
		table.addProperty(new PTProperty("rectangle", "Rectangle",
				"A rectangle is composed of a position (x,y) and a dimension(width,height)")).setCategory("Misc")
				.setEditor(new PTRectangleEditor());
		table.addProperty(
				new PTProperty("inset", "Inset", "An inset is composed of the following fields:top,left,bottom,right)"))
				.setCategory("Misc").setEditor(new PTInsetsEditor());
		table.addProperty(new PTProperty("date", "Date", "Well, is there something more to say ?")).setCategory("Misc")
				.setEditor(new PTDateEditor());

		return table;
	}

}
