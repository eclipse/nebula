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
package org.eclipse.nebula.widgets.datechooser.example;

import java.util.Locale;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.nebula.widgets.datechooser.DateChooser;
import org.eclipse.nebula.widgets.datechooser.DateChooserTheme;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.nebula.examples.AbstractExampleTab;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

public class DateChooserExampleTab extends AbstractExampleTab {
	public static final String GRAY_THEME = "GRAY (default)";
	public static final String BLUE_THEME = "BLUE";
	public static final String YELLOW_THEME = "YELLOW";

	private DateChooser chooser = null;

	// Style group
	private Button borderStyle;
	private Button multiStyle;

	// GUI settings group
	private Combo themes;
	private Locale locale;
	private Button gridVisible;
	private Button weeksVisible;
	private Button footerVisible;
	private Button navigationEnabled;

	// Selection settings group
	private Button autoChangeOnAdjacent;
	private Button autoSelectOnFooter;

	private Listener recreateListener = new Listener() {
		public void handleEvent(Event event) {
			recreateExample();
		}
	};

	private void applyTheme() {
		switch ( themes.getSelectionIndex() ) {
			case 0:
				chooser.setTheme(DateChooserTheme.GRAY);
				break;
			case 1:
				chooser.setTheme(DateChooserTheme.BLUE);
				break;
			case 2:
				chooser.setTheme(DateChooserTheme.YELLOW);
				break;
		} 
	}

	private Button createCheckBox2(Composite parent, String text, boolean selected) {
		Button cb = new Button(parent, SWT.CHECK);
		cb.setText(text);
		cb.setSelection(selected);
		GridData data = new GridData();
		data.horizontalSpan = 2;
		cb.setLayoutData(data);
		return cb;
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public Control createControl(Composite parent) {
		int style = SWT.None;
		if ( borderStyle.getSelection() ) {
			style |= SWT.BORDER;
		}
		if ( multiStyle.getSelection() ) {
			style |= SWT.MULTI;
		}
		chooser = new DateChooser(parent, style);

		applyTheme();
		chooser.setGridVisible(gridVisible.getSelection());
		chooser.setWeeksVisible(weeksVisible.getSelection());
		chooser.setFooterVisible(footerVisible.getSelection());
		chooser.setNavigationEnabled(navigationEnabled.getSelection());

		chooser.setAutoChangeOnAdjacent(autoChangeOnAdjacent.getSelection());
		chooser.setAutoSelectOnFooter(autoSelectOnFooter.getSelection());

		return chooser;
	}

	public String[] createLinks() {
		return null;
	}

	private void createGUISettingsGroup(Composite parent) {
		Group gp = new Group(parent, SWT.NONE);
		gp.setText("GUI Settings");
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(gp);
		gp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		new Label(gp, SWT.NONE).setText("Themes:");
		themes = new Combo(gp, SWT.BORDER);
		themes.setItems(new String[] {GRAY_THEME , BLUE_THEME, YELLOW_THEME});
		themes.select(0);
		themes.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				applyTheme();
			}			
		});

		new Label(gp, SWT.NONE).setText("Locale:");
		final Combo localeCombo = new Combo(gp, SWT.BORDER);
		final Locale[] locales = Locale.getAvailableLocales();
		Locale dl = Locale.getDefault();
		for(int i = 0; i < locales.length; i++) {
			localeCombo.add(locales[i].getDisplayName());
			if ( locales[i].equals(dl) ) {
				localeCombo.select(i);
			}
		}
		localeCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				locale = locales[localeCombo.getSelectionIndex()];
				chooser.setLocale(locale);
			}
		});

		gridVisible = createCheckBox2(gp, "GridVisible", true);
		gridVisible.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				chooser.setGridVisible(gridVisible.getSelection());
			}
		});

		weeksVisible = createCheckBox2(gp, "WeeksVisible", false);
		weeksVisible.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				chooser.setWeeksVisible(weeksVisible.getSelection());
				chooser.getParent().layout();
			}
		});

		footerVisible = createCheckBox2(gp, "FooterVisible", false);
		footerVisible.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				chooser.setFooterVisible(footerVisible.getSelection());
				chooser.getParent().layout();
			}
		});

		navigationEnabled = createCheckBox2(gp, "NavigationEnabled", true);
		navigationEnabled.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				chooser.setNavigationEnabled(navigationEnabled.getSelection());
			}
		});
	}

	private void createStyleGroup(Composite parent) {
		Group gp = new Group(parent, SWT.NONE);
		gp.setText("Style");
		gp.setLayout(new RowLayout());
		GridData data = new GridData(SWT.FILL, SWT.FILL, false, false);
		data.horizontalSpan = 2;
		gp.setLayoutData(data);

		borderStyle = new Button(gp, SWT.CHECK);
		borderStyle.setText("SWT.BORDER");
		borderStyle.addListener(SWT.Selection, recreateListener);

		multiStyle = new Button(gp, SWT.CHECK);
		multiStyle.setText("SWT.MULTI");
		multiStyle.addListener(SWT.Selection, recreateListener);
	}

	public void createParameters(Composite parent) {
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(2)
										 .applyTo(parent);
		createStyleGroup(parent);
		createGUISettingsGroup(parent);
		createSelectionSettingsGroup(parent);
	}

	private void createSelectionSettingsGroup(Composite parent) {
		Group gp = new Group(parent, SWT.NONE);
		gp.setText("Selection Settings");
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(gp);
		gp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		autoChangeOnAdjacent = createCheckBox2(gp, "AutoChangeOnAdjacent", true);
		autoChangeOnAdjacent.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				chooser.setAutoChangeOnAdjacent(autoChangeOnAdjacent.getSelection());
			}
		});

		autoSelectOnFooter = createCheckBox2(gp, "AutoSelectOnFooter", false);
		autoSelectOnFooter.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				chooser.setAutoSelectOnFooter(autoSelectOnFooter.getSelection());
			}
		});
	}
}
