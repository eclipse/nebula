package org.eclipse.nebula.widgets.roundedcheckbox.snippets;

/*******************************************************************************
 * Copyright (c) 2018 Laurent CARON All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API
 * and implementation
 *******************************************************************************/

import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * This snippet demonstrates the TitledSeparator widget
 *
 */
public class Prototype {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, false));

		final StringBuilder sb = new StringBuilder();
		final Random random = new Random(2546);
		for (int i = 0; i < 200; i++) {
			sb.append("Very very long text about ").append(random.nextInt(2000)).append("\t");
			if (i % 10 == 0) {
				sb.append("\n");
			}
		}

		// H SCROLL
		final Label lbl1 = new Label(shell, SWT.NONE);
		lbl1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		lbl1.setText("Horizontal Scroll");

		final StyledText txt1 = new StyledText(shell, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL);
		txt1.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		txt1.setText(sb.toString());
		new MouseNavigator(txt1);

		// V_SCROLL
		final Label lbl2 = new Label(shell, SWT.NONE);
		lbl2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		lbl2.setText("Vertical Scroll");

		final StyledText txt2 = new StyledText(shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		txt2.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		txt2.setText(sb.toString());
		new MouseNavigator(txt2);

		// H SCROLL & V_SCROLL
		final Label lbl3 = new Label(shell, SWT.NONE);
		lbl3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		lbl3.setText("Horizontal and Vertical Scroll");

		final StyledText txt3 = new StyledText(shell, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		txt3.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		txt3.setBackground(txt3.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		txt3.setForeground(txt3.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		txt3.setText(sb.toString());
		new MouseNavigator(txt3);

		// Disabled Scroll at start
		final Label lbl4 = new Label(shell, SWT.NONE);
		lbl4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		lbl4.setText("No scroll at start");

		final StyledText txt4 = new StyledText(shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		final GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true);
		gd.minimumHeight = 100;
		txt4.setLayoutData(gd);

		txt4.setText("Disabled scroll");
		new MouseNavigator(txt4);

		// Disabled Scroll
		final Label lbl5 = new Label(shell, SWT.NONE);
		lbl5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		lbl5.setText("No scroll");

		final StyledText txt5 = new StyledText(shell, SWT.MULTI | SWT.BORDER);
		final GridData gd5 = new GridData(GridData.FILL, GridData.FILL, true, true);
		gd5.minimumHeight = 100;
		txt5.setLayoutData(gd5);

		txt5.setText("No scroll");
		new MouseNavigator(txt5);

		shell.setSize(800, 600);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();
	}

}
