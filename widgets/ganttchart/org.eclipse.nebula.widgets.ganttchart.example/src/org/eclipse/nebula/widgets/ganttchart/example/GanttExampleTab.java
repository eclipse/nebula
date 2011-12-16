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
package org.eclipse.nebula.widgets.ganttchart.example;

import org.eclipse.nebula.widgets.ganttchart.GanttTester;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.nebula.examples.AbstractExampleTab;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class GanttExampleTab extends AbstractExampleTab {

	/**
	 * @wbp.parser.entryPoint
	 */
	public Control createControl(Composite parent) {
		int style = SWT.None;
		Button button = new Button(parent, style);
		button.setText("Run Extended Gantt Example");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				new GanttTester();
			}
		});

		return button;
	}

	public String[] createLinks() {
		return null;
	}

	public void createParameters(Composite parent) {

	}

}
