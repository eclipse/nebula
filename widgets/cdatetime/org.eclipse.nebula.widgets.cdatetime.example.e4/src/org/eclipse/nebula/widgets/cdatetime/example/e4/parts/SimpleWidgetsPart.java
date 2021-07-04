/*******************************************************************************
 * Copyright (c) 2020 Laurent Caron
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Laurent Caron <laurent dot caron at gmail dot com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.cdatetime.example.e4.parts;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class SimpleWidgetsPart {
	private static final String CSS_ID = "org.eclipse.e4.ui.css.id";
	private CDateTime cdt1;

	@PostConstruct
	public void createComposite(final Composite parent) {
		// create group
		final Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		group.setText("Widgets");
		group.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		// First
		Label label = new Label(group, SWT.NONE);
		label.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		label.setText("Simple date widget");

		cdt1 = new CDateTime(group, CDT.BORDER | CDT.SPINNER);
		final GridData gd1 = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		gd1.widthHint = 150;
		cdt1.setLayoutData(gd1);
		cdt1.setData(CSS_ID, "one");

		// Second
		label = new Label(group, SWT.NONE);
		label.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		label.setText("Date widget with pop-up shell");

		final CDateTime cdt2 = new CDateTime(group, CDT.BORDER | CDT.COMPACT | CDT.DROP_DOWN | CDT.DATE_LONG | CDT.TIME_MEDIUM);
		final GridData gd2 = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		gd2.widthHint = 150;
		cdt2.setLayoutData(gd2);
		cdt2.setSelection(new Date());
		cdt2.setData(CSS_ID, "two");

		// Third
		label = new Label(group, SWT.NONE);
		label.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		label.setText("Time widget with pop-up shell");

		final CDateTime cdt3 = new CDateTime(group, CDT.BORDER | CDT.DROP_DOWN | CDT.TIME_SHORT | CDT.CLOCK_DISCRETE);
		final GridData gd3 = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		gd3.widthHint = 70;
		cdt3.setLayoutData(gd3);
		cdt3.setSelection(new Date());
		cdt3.setData(CSS_ID, "three");

		// Fourth
		label = new Label(group, SWT.NONE);
		label.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		label.setText("Day/Month/Year widget with pop-up shell");

		final Composite comp = new Composite(group, SWT.NONE);
		final GridData gdComp = new GridData(SWT.FILL, SWT.CENTER, true, false);
		comp.setLayoutData(gdComp);
		comp.setLayout(new GridLayout(3, true));

		final CDateTime date = new CDateTime(comp, CDT.BORDER | CDT.DROP_DOWN);
		date.setNullText("<day>");
		date.setPattern("dd");
		date.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		date.setData(CSS_ID, "four");

		final CDateTime month = new CDateTime(comp, CDT.BORDER | CDT.DROP_DOWN);
		month.setNullText("<month>");
		month.setPattern("MMMM");
		month.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		month.setData(CSS_ID, "five");

		final CDateTime year = new CDateTime(comp, CDT.BORDER | CDT.DROP_DOWN);
		year.setNullText("<year>");
		year.setPattern("yyyy");
		year.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		year.setData(CSS_ID, "six");

		// Last
		label = new Label(group, SWT.NONE);
		label.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		label.setText("Date widget with pop-up shell\n(custom ok/cancel/clear button");

		final CDateTime cdt6 = new CDateTime(group, CDT.BORDER | CDT.COMPACT | CDT.DROP_DOWN | CDT.DATE_LONG | CDT.TIME_MEDIUM);
		final GridData gd6 = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		gd6.widthHint = 150;
		cdt6.setLayoutData(gd6);
		cdt6.setSelection(new Date());
		cdt6.setData(CSS_ID, "okcancelclear");
		
	}

	@Focus
	public void setFocus() {
		cdt1.setFocus();
	}

	@PreDestroy
	private void dispose() {
	}

}