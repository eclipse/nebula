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
 * Marty Jones <martybjones at gmail dot com> - Base code for a sample of using TableCombo
 *******************************************************************************/
package org.eclipse.nebula.widgets.roundedswitch.example.e4.parts;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.nebula.widgets.roundedswitch.RoundedSwitch;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class SamplePart {

	private static final String CSS_CLASS_NAME = "org.eclipse.e4.ui.css.CssClassName";
	private RoundedSwitch button1;

	@PostConstruct
	public void createComposite(final Composite parent) {
		
		parent.setLayout(new GridLayout());
		
		createDefaultGroup(parent);
		createCustomizedGroup(parent);
		
	}

	private void createDefaultGroup(final Composite parent) {
		final Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		group.setText("Default design");
		group.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		
		final Label lbl1 = new Label(group, SWT.NONE);
		lbl1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		lbl1.setText("Enabled checked");

		button1 = new RoundedSwitch(group, SWT.NONE);
		button1.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		button1.setEnabled(true);
		button1.setSelection(true);

		final Label lbl2 = new Label(group, SWT.NONE);
		lbl2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		lbl2.setText("Enabled unchecked");

		final RoundedSwitch button2 = new RoundedSwitch(group, SWT.NONE);
		button2.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		button2.setEnabled(true);
		button2.setSelection(false);

		final Label lbl3 = new Label(group, SWT.NONE);
		lbl3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		lbl3.setText("Disabled checked");

		final RoundedSwitch button3 = new RoundedSwitch(group, SWT.NONE);
		button3.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		button3.setEnabled(false);
		button3.setSelection(true);

		final Label lbl4 = new Label(group, SWT.NONE);
		lbl4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		lbl4.setText("Disabled unchecked");

		final RoundedSwitch button4 = new RoundedSwitch(group, SWT.NONE);
		button4.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		button4.setEnabled(false);
		button4.setSelection(false);
	}

	private void createCustomizedGroup(final Composite parent) {
		final Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		group.setText("Customized design");
		group.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		
		final Label lbl1 = new Label(group, SWT.NONE);
		lbl1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		lbl1.setText("Enabled checked");

		RoundedSwitch button1 = new RoundedSwitch(group, SWT.NONE);
		button1.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		button1.setEnabled(true);
		button1.setSelection(true);
		button1.setData(CSS_CLASS_NAME, "custom");
		
		final Label lbl2 = new Label(group, SWT.NONE);
		lbl2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		lbl2.setText("Enabled unchecked");

		final RoundedSwitch button2 = new RoundedSwitch(group, SWT.NONE);
		button2.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		button2.setEnabled(true);
		button2.setSelection(false);
		button2.setData(CSS_CLASS_NAME, "custom");
		
		final Label lbl3 = new Label(group, SWT.NONE);
		lbl3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		lbl3.setText("Disabled checked");

		final RoundedSwitch button3 = new RoundedSwitch(group, SWT.NONE);
		button3.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		button3.setEnabled(false);
		button3.setSelection(true);
		button3.setData(CSS_CLASS_NAME, "custom");
		
		final Label lbl4 = new Label(group, SWT.NONE);
		lbl4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		lbl4.setText("Disabled unchecked");

		final RoundedSwitch button4 = new RoundedSwitch(group, SWT.NONE);
		button4.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		button4.setEnabled(false);
		button4.setSelection(false);
		button4.setData(CSS_CLASS_NAME, "custom");
	}
	
	@Focus
	public void setFocus() {
		button1.forceFocus();
	}

	@PreDestroy
	private void dispose() {
	}

}