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

public class BigWidgetsPart {
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
		label.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, false, false));
		label.setText("Date Selector widget");

		cdt1 = new CDateTime(group, CDT.BORDER | CDT.COMPACT | CDT.SIMPLE);
		final GridData gd1 = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd1.widthHint = 200;
		gd1.heightHint = 200;
		cdt1.setLayoutData(gd1);
		cdt1.setData(CSS_ID, "big_one");

		// Second
		label = new Label(group, SWT.NONE);
		label.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, false, false));
		label.setText("Hour Selector widget");

		final CDateTime cdt2 = new CDateTime(group, CDT.TIME_MEDIUM | CDT.SIMPLE);
		final GridData gd2 = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd2.widthHint = 200;
		cdt2.setLayoutData(gd2);
		cdt2.setData(CSS_ID, "big_two");

	}

	@Focus
	public void setFocus() {
		cdt1.setFocus();
	}

	@PreDestroy
	private void dispose() {
	}

}