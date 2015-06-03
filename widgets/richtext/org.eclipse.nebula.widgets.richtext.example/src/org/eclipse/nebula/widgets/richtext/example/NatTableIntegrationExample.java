/*****************************************************************************
 * Copyright (c) 2015 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *		Dirk Fauth <dirk.fauth@googlemail.com> - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.nebula.widgets.richtext.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.EditableRule;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ReflectiveColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultBooleanDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.CheckBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.extension.nebula.richtext.MarkupDisplayConverter;
import org.eclipse.nebula.widgets.nattable.extension.nebula.richtext.RichTextCellEditor;
import org.eclipse.nebula.widgets.nattable.extension.nebula.richtext.RichTextCellPainter;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultGridLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.cell.BackgroundPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.PaddingDecorator;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.theme.ModernNatTableThemeConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class NatTableIntegrationExample {

	public static void main(String[] args) {
		Display display = new Display();

		final Shell shell = new Shell(display);
		shell.setText("Rich Text Editor JFace viewer integration example");
		shell.setSize(800, 600);

		shell.setLayout(new GridLayout(1, true));

		NatTableIntegrationExample example = new NatTableIntegrationExample();
		example.createControls(shell);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	public void createControls(Composite parent) {
		parent.setLayout(new FillLayout(SWT.VERTICAL));

		String[] propertyNames = new String[] { "firstName", "lastName", "gender", "married", "description" };

		Map<String, String> propertyToLabelMap = new HashMap<>();
		propertyToLabelMap.put("firstName", "Firstname");
		propertyToLabelMap.put("lastName", "Lastname");
		propertyToLabelMap.put("gender", "Gender");
		propertyToLabelMap.put("married", "Married");
		propertyToLabelMap.put("description", "Description");

		IColumnAccessor<Person> columnAccessor = new ReflectiveColumnPropertyAccessor<>(propertyNames);
		List<Person> persons = PersonService.getPersons(10);
		IDataProvider bodyDataProvider = new ListDataProvider<>(persons, columnAccessor);

		DefaultColumnHeaderDataProvider columnHeaderDataProvider = new DefaultColumnHeaderDataProvider(propertyNames, propertyToLabelMap);

		DefaultGridLayer gridLayer = new DefaultGridLayer(bodyDataProvider, columnHeaderDataProvider);
		((AbstractLayer) gridLayer.getBodyDataLayer()).setConfigLabelAccumulator(new ColumnLabelAccumulator());

		NatTable natTable = new NatTable(parent, gridLayer, false);

		natTable.addConfiguration(new DefaultNatTableStyleConfiguration());

		// add custom painter and editor configuration
		natTable.addConfiguration(new AbstractRegistryConfiguration() {

			@Override
			public void configureRegistry(IConfigRegistry configRegistry) {

				// configure converter

				MarkupDisplayConverter markupConverter = new MarkupDisplayConverter();
				markupConverter.registerMarkup("Simpson", "<em>", "</em>");
				markupConverter.registerMarkup("Smithers",
						"<span style=\"background-color:rgb(255, 0, 0)\"><strong><s><u>",
						"</u></s></strong></span>");

				// register markup display converter for normal displaymode
				configRegistry.registerConfigAttribute(
						CellConfigAttributes.DISPLAY_CONVERTER,
						markupConverter,
						DisplayMode.NORMAL,
						ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 1);
				// register default display converter for editing, so there is no markup in the
				// editor
				configRegistry.registerConfigAttribute(
						CellConfigAttributes.DISPLAY_CONVERTER,
						new DefaultDisplayConverter(),
						DisplayMode.EDIT,
						ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 1);

				configRegistry.registerConfigAttribute(
						CellConfigAttributes.DISPLAY_CONVERTER,
						new DefaultBooleanDisplayConverter(),
						DisplayMode.NORMAL,
						ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 3);

				// configure cell painter

				configRegistry.registerConfigAttribute(
						CellConfigAttributes.CELL_PAINTER,
						new BackgroundPainter(new PaddingDecorator(new RichTextCellPainter(), 2, 5, 2, 5)),
						DisplayMode.NORMAL,
						ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 1);

				configRegistry.registerConfigAttribute(
						CellConfigAttributes.CELL_PAINTER,
						new CheckBoxPainter(),
						DisplayMode.NORMAL,
						ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 3);

				configRegistry.registerConfigAttribute(
						CellConfigAttributes.CELL_PAINTER,
						new BackgroundPainter(new PaddingDecorator(new RichTextCellPainter(), 2, 5, 2, 5)),
						DisplayMode.NORMAL,
						ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 4);

				// configure editing

				configRegistry.registerConfigAttribute(
						EditConfigAttributes.CELL_EDITABLE_RULE,
						EditableRule.ALWAYS_EDITABLE);

				configRegistry.registerConfigAttribute(
						EditConfigAttributes.CELL_EDITOR,
						new CheckBoxCellEditor(),
						DisplayMode.NORMAL,
						ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 3);

				configRegistry.registerConfigAttribute(
						EditConfigAttributes.CELL_EDITOR,
						new RichTextCellEditor(),
						DisplayMode.NORMAL,
						ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 4);
			}
		});

		natTable.configure();

		natTable.setTheme(new ModernNatTableThemeConfiguration());
	}

}
