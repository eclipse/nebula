/*******************************************************************************
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    jdowdall - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.cdatetime.example;

import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.nebula.examples.AbstractExampleTab;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class CDateTimeExampleTab extends AbstractExampleTab {

	private CDateTime cdc;
	private int style = CDT.BORDER;
	private int format;
	private String pattern;
	private Locale locale;
	private Button simple;
	private Button drop;
	private Button hour12;
	private Button hour24;
	private Combo tzCombo;

	public CDateTimeExampleTab() {
		// TODO Auto-generated constructor stub
	}

	public Control createControl(Composite parent) {
		cdc = new CDateTime(parent, style);
		if (locale != null) {
			cdc.setLocale(locale);
		}
		if (format > 0) {
			cdc.setFormat(format);
		} else if (pattern != null) {
			setPattern();
		}
		return cdc;
	}

	public String[] createLinks() {
		return new String[] {
				"<a href=\"http://www.eclipse.org/nebula/widgets/cdatetime/cdatetime.php\">CDateTime Home Page</a>",
				"<a href=\"http://www.eclipse.org/nebula/widgets/cdatetime/snippets.php\">Snippets</a>",
				"<a href=\"https://bugs.eclipse.org/bugs/buglist.cgi?query_format=advanced&short_desc_type=allwordssubstr&short_desc=&classification=Technology&product=Nebula&component=CDateTime&long_desc_type=allwordssubstr&long_desc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&status_whiteboard_type=allwordssubstr&status_whiteboard=&keywords_type=allwords&keywords=&emailtype1=substring&email1=&emailtype2=substring&email2=&bugidtype=include&bug_id=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&cmdtype=doit&order=Reuse+same+sort+as+last+time&field0-0-0=noop&type0-0-0=noop&value0-0-0=\">Bugs</a>" };
	}

	public void createParameters(Composite parent) {
		parent.setLayout(new GridLayout());

		Group g = new Group(parent, SWT.BORDER);
		g.setText("Styles:");
		g.setLayout(new GridLayout());
		g.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		simple = new Button(g, SWT.CHECK);
		simple.setText("Simple");
		simple.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (simple.getSelection()) {
					style |= CDT.SIMPLE;
					style &= ~CDT.DROP_DOWN;
					drop.setSelection(false);
				} else {
					style &= ~CDT.SIMPLE;
				}
				recreateAndLayout();
			}
		});

		drop = new Button(g, SWT.CHECK);
		drop.setText("Drop Down");
		drop.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (((Button) e.widget).getSelection()) {
					style |= CDT.DROP_DOWN;
					style &= ~CDT.SIMPLE;
					simple.setSelection(false);
				} else {
					style &= ~CDT.DROP_DOWN;
				}
				recreateAndLayout();
			}
		});

		Button b = new Button(g, SWT.CHECK);
		b.setText("Border");
		b.setSelection(true);
		b.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (((Button) e.widget).getSelection()) {
					style |= CDT.BORDER;
				} else {
					style &= ~CDT.BORDER;
				}
				recreateAndLayout();
			}
		});

		b = new Button(g, SWT.CHECK);
		b.setText("Compact");
		b.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (((Button) e.widget).getSelection()) {
					style |= CDT.COMPACT;
				} else {
					style &= ~CDT.COMPACT;
				}
				recreateAndLayout();
			}
		});

		b = new Button(g, SWT.CHECK);
		b.setText("Spinner");
		b.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (((Button) e.widget).getSelection()) {
					style |= CDT.SPINNER;
				} else {
					style &= ~CDT.SPINNER;
				}
				recreateAndLayout();
			}
		});

		b = new Button(g, SWT.RADIO);
		b.setText("Analog Clock");
		b.setSelection(true);

		b = new Button(g, SWT.RADIO);
		b.setText("Discrete Clock");
		b.setSelection(false);

		final Composite dc = new Composite(g, SWT.NONE);
		GridData data = new GridData();
		data.exclude = true;
		dc.setLayoutData(data);
		dc.setLayout(new GridLayout(2, true));

		b.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (((Button) e.widget).getSelection()) {
					style |= CDT.CLOCK_DISCRETE;
					((GridData) dc.getLayoutData()).exclude = false;
					dc.setVisible(true);
				} else {
					style &= ~CDT.CLOCK_DISCRETE;
					((GridData) dc.getLayoutData()).exclude = true;
					dc.setVisible(false);
				}
				recreateAndLayout();
				dc.getParent().getParent().layout(true);
			}
		});

		b = new Button(dc, SWT.RADIO);
		b.setText("Horizontal");
		b.setSelection(false);
		data = new GridData();
		data.horizontalIndent = 20;
		b.setLayoutData(data);

		b = new Button(dc, SWT.RADIO);
		b.setText("Vertical");
		b.setSelection(true);
		b.setLayoutData(new GridData());
		b.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (((Button) e.widget).getSelection()) {
					style &= ~CDT.HORIZONTAL;
					style |= CDT.VERTICAL;
				} else {
					style &= ~CDT.VERTICAL;
					style |= CDT.HORIZONTAL;
				}
				recreateAndLayout();
			}
		});

		hour12 = new Button(g, SWT.CHECK);
		hour12.setText("Force 12 Hour Clock");
		hour12.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (hour12.getSelection()) {
					style |= CDT.CLOCK_12_HOUR;
					style &= ~CDT.CLOCK_24_HOUR;
					hour24.setSelection(false);
				} else {
					style &= ~CDT.CLOCK_12_HOUR;
				}
				recreateAndLayout();
			}
		});

		hour24 = new Button(g, SWT.CHECK);
		hour24.setText("Force 24 Hour Clock");
		hour24.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (hour24.getSelection()) {
					style |= CDT.CLOCK_24_HOUR;
					style &= ~CDT.CLOCK_12_HOUR;
					hour12.setSelection(false);
				} else {
					style &= ~CDT.CLOCK_24_HOUR;
				}
				recreateAndLayout();
			}
		});

		g = new Group(parent, SWT.BORDER);
		g.setText("Settings:");
		g.setLayout(new GridLayout());
		g.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		final Combo combo1 = new Combo(g, SWT.BORDER | SWT.RIGHT | SWT.SINGLE);
		combo1.add("Type in a custom pattern");
		combo1.add("MM/dd/yyyy HH:mm.ss z");
		combo1.add("dd/MM/yy HH:mm.ss");
		combo1.add("dd/MM/yy HH:mm.ss.SSS");
		combo1.add("DATE_SHORT");
		combo1.add("DATE_SHORT");
		combo1.add("DATE_MEDIUM");
		combo1.add("DATE_LONG");
		combo1.add("TIME_SHORT");
		combo1.add("TIME_MEDIUM");
		combo1.add("DATE_SHORT | TIME_SHORT");
		combo1.add("DATE_MEDIUM | TIME_MEDIUM");
		combo1.add("DATE_LONG | TIME_MEDIUM");
		combo1.select(1);
		data = new GridData(SWT.FILL, SWT.FILL, false, false);
		data.horizontalSpan = 2;
		combo1.setLayoutData(data);

		b = new Button(g, SWT.PUSH);
		b.setText("Set Format");
		data = new GridData(SWT.RIGHT, SWT.FILL, false, false);
		data.horizontalSpan = 2;
		b.setLayoutData(data);
		b.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int index = combo1.getSelectionIndex();
				if (index < 3) {
					pattern = combo1.getText();
					format = -1;
					setPattern();

				} else {
					pattern = null;
					switch (index) {
					case 3:
						format = CDT.DATE_SHORT;
						break;
					case 4:
						format = CDT.DATE_MEDIUM;
						break;
					case 5:
						format = CDT.DATE_LONG;
						break;
					case 6:
						format = CDT.TIME_SHORT;
						break;
					case 7:
						format = CDT.TIME_MEDIUM;
						break;
					case 8:
						format = CDT.DATE_SHORT | CDT.TIME_SHORT;
						break;
					case 9:
						format = CDT.DATE_MEDIUM | CDT.TIME_MEDIUM;
						break;
					case 10:
						format = CDT.DATE_LONG | CDT.TIME_MEDIUM;
						break;
					}
					cdc.setFormat(format);
				}
				relayoutExample();
			}
		});

		data = new GridData(SWT.FILL, SWT.FILL, false, false);
		data.horizontalSpan = 2;
		Button clear = new Button(g, SWT.PUSH);
		clear.setText("Selection to Null");
		clear.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cdc.setSelection(null);
				relayoutExample();
			}
		});
		clear.setLayoutData(data);

		final Combo localeCombo = new Combo(g, SWT.DROP_DOWN | SWT.READ_ONLY);
		localeCombo.setLayoutData(data);
		final Locale[] la = Locale.getAvailableLocales();
		for (int i = 0; i < la.length; i++) {
			localeCombo.add(la[i].getDisplayName());
		}

		String[] items = localeCombo.getItems();
		Arrays.sort(items);
		localeCombo.setItems(items);
		Locale local = Locale.getDefault();
		localeCombo.select(Arrays.binarySearch(items, local.getDisplayName()));

		localeCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				locale = Locale.getAvailableLocales()[localeCombo
						.getSelectionIndex()];
				cdc.setLocale(locale);
				relayoutExample();
			}
		});

		Label label2 = new Label(g, SWT.NONE);
		label2.setLayoutData(data);
		label2.setText("Comma separated timezones to roll");

		tzCombo = new Combo(g, SWT.BORDER);
		tzCombo.setLayoutData(data);
		String[] zones = TimeZone.getAvailableIDs();
		Arrays.sort(zones);
		tzCombo.add("UTC, CET, CAT, EAT");
		for (int i = 0; i < zones.length; i++) {
			tzCombo.add(zones[i]);
		}
		tzCombo.setText("UTC, CET, CAT, EAT");

	}

	protected void setPattern() {
		if (pattern.endsWith("z")) {
			String[] zones = tzCombo.getText().split(",");
			TimeZone[] tZones = new TimeZone[zones.length];
			for (int i = 0; i < zones.length; i++) {
				tZones[i] = TimeZone.getTimeZone(zones[i].trim());
			}
			cdc.setPattern(pattern, tZones);
			cdc.setTimeZone(tZones[0]);
		} else {
			cdc.setPattern(pattern);
			cdc.setTimeZone((String) null);
		}

	}

	private void recreateAndLayout() {
		recreateExample();
		relayoutExample();
	}
}
