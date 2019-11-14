/*******************************************************************************
 * Copyright (c) 2019 Laurent CARON
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
package org.eclipse.nebula.widgets.ctreecombo.snippets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.ctreecombo.CTreeCombo;
import org.eclipse.nebula.widgets.ctreecombo.CTreeComboItem;
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
import org.eclipse.swt.widgets.Text;

/**
 * This snippet demonstrates the CTreeComboSnippet widget
 *
 */
public class CTreeComboSnippet {
	private static class ItemSelected extends SelectionAdapter {

		private final CTreeCombo ctc;
		private final String text;

		public ItemSelected(CTreeCombo ctc, String text) {
			this.ctc = ctc;
			this.text = text;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			listenerGroup.setText("Listener Results - (" + text + ")");
			listenerResults.setText(ctc.getText() == null ? "null" : ctc.getText());
		}
	}

	private static Font boldFont;
	private static Map<String, Image> images = new HashMap<>();
	private static Color darkRed;
	private static Color darkBlue;
	private static Color darkGreen;
	private static List<Country> modelList;
	private static Text listenerResults;

	private static Group listenerGroup;

	private static List<Country> loadModel() {
		final List<Country> items = new ArrayList<Country>();
		items.add(new Country("Austria", "Thomas Schindl"));
		items.add(new Country("France", "Laurent Caron", "Nicolas Richeton"));
		items.add(new Country("Germany", "Dirk Fauth", "Johannes Faltermeier"));
		items.add(new Country("Italy", "Mirko Paturzo"));
		items.add(new Country("Netherlands", "Wim Jongman"));
		items.add(new Country("Norway", "Hallvard Traetteberg"));
		items.add(new Country("UK", "Jonah Graham", "Matthew Gerring", "Peter Chang"));
		items.add(new Country("USA", "Donald Dunne"));
		return items;
	}

	private static void loadSingleDataset(CTreeCombo ctc) {
		for (final Country country : modelList) {
			final CTreeComboItem item = new CTreeComboItem(ctc, SWT.NONE);
			item.setText(country.getName());

			for (final String commiter : country.getCommiters()) {
				final CTreeComboItem commiterItem = new CTreeComboItem(item, SWT.NONE);
				commiterItem.setText(commiter);
			}
		}
	}

	private static void loadSingleDatasetWithColorsAndFonts(CTreeCombo ctc) {

		int index = 0;
		for (final Country country : modelList) {
			final CTreeComboItem item = new CTreeComboItem(ctc, SWT.NONE);
			item.setText(country.getName());

			if (index % 3 == 0) {
				item.setForeground(darkBlue);
			} else if (index % 3 == 1) {
				item.setForeground(darkGreen);
			} else {
				item.setForeground(darkRed);
			}

			for (int j = 0; j < country.getCommiters().size(); j++) {
				final String commiter = country.getCommiters().get(j);
				final CTreeComboItem commiterItem = new CTreeComboItem(item, SWT.NONE);
				commiterItem.setText(commiter);
				if (j % 2 == 0) {
					commiterItem.setFont(boldFont);
				}
			}
			index++;
		}
	}

	private static void loadSingleDatasetWithImages(CTreeCombo ctc) {
		for (final Country country : modelList) {
			final CTreeComboItem item = new CTreeComboItem(ctc, SWT.NONE);
			item.setText(country.getName());
			final String key = country.getName().toLowerCase();
			item.setImage(images.get(key));

			for (final String commiter : country.getCommiters()) {
				final CTreeComboItem commiterItem = new CTreeComboItem(item, SWT.NONE);
				commiterItem.setText(commiter);
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		// get display.
		final Display display = new Display();

		// create bold and italic font.
		boldFont = new Font(display, "Arial", 8, SWT.BOLD | SWT.ITALIC);

		// create images
		final String[] countries = new String[] { "austria", "france", "germany", "italy", "netherlands", "norway", "unitedkingdom", "usa" };
		for (final String country : countries) {
			final Image img = ImageDescriptor.createFromFile(CTreeComboSnippet.class, "flags/" + country + ".png").createImage();
			images.put(country, img);
		}

		// create colors
		darkRed = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED);
		darkBlue = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE);
		darkGreen = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);

		// load the model list.
		modelList = loadModel();

		// create a new shell.
		final Shell shell = new Shell(display);
		shell.setText("CTreeCombo Snippet (pure SWT)");
		shell.setSize(600, 400);
		shell.setLayout(new GridLayout());

		// create group
		final Group group = new Group(shell, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		group.setText("Sample Group");

		// create group
		listenerGroup = new Group(shell, SWT.NONE);
		listenerGroup.setLayout(new GridLayout(1, false));
		listenerGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		listenerGroup.setText("Listener Results");

		listenerResults = new Text(listenerGroup, SWT.BORDER | SWT.MULTI);
		final GridData gd = new GridData(SWT.FILL, SWT.DEFAULT, true, false);
		gd.heightHint = 30;
		listenerResults.setLayoutData(gd);

		////////////////////////////////////////////////////////////////////////
		// Sample #1
		////////////////////////////////////////////////////////////////////////
		Label label = new Label(group, SWT.NONE);
		label.setText("No image, no font:");

		// create TableCombo
		CTreeCombo ctc = new CTreeCombo(group, SWT.BORDER | SWT.READ_ONLY);
		ctc.setLayoutData(new GridData(200, SWT.DEFAULT));

		// load the dataset.
		loadSingleDataset(ctc);

		// add listener
		ctc.addSelectionListener(new ItemSelected(ctc, "Sample1"));

		////////////////////////////////////////////////////////////////////////
		// Sample #2
		////////////////////////////////////////////////////////////////////////

		label = new Label(group, SWT.NONE);
		label.setText("Combo with images");

		// create TableCombo
		ctc = new CTreeCombo(group, SWT.BORDER | SWT.READ_ONLY);
		ctc.setLayoutData(new GridData(200, SWT.DEFAULT));

		// load the dataset.
		loadSingleDatasetWithImages(ctc);

		// add listener
		ctc.addSelectionListener(new ItemSelected(ctc, "Sample2"));

		////////////////////////////////////////////////////////////////////////
		// Sample #3
		////////////////////////////////////////////////////////////////////////
		// create label
		label = new Label(group, SWT.NONE);
		label.setText("Colors && Fonts :");

		// create TableCombo
		ctc = new CTreeCombo(group, SWT.BORDER | SWT.READ_ONLY);
		ctc.setLayoutData(new GridData(200, SWT.DEFAULT));

		// load the dataset.
		loadSingleDatasetWithColorsAndFonts(ctc);

		// add listener
		ctc.addSelectionListener(new ItemSelected(ctc, "Sample3"));

		// open the shell.
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		// dispose of the font
		boldFont.dispose();

		// dispose images
		for (final Image img : images.values()) {
			img.dispose();
		}

		// dispose colors
		darkRed.dispose();
		darkBlue.dispose();
		darkGreen.dispose();

		// dispose display
		display.dispose();
	}
}
