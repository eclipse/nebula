/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON
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
package org.eclipse.nebula.widgets.opal.tipoftheday.snippets;

import java.util.Locale;

import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.nebula.widgets.opal.tipoftheday.TipOfTheDay;
import org.eclipse.nebula.widgets.opal.tipoftheday.TipOfTheDay.TipStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This snippet demonstrates the Tip of the Day widget
 *
 */
public class TipOfTheDaySnippet {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		Locale.setDefault(Locale.ENGLISH);

		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("Tip of the Days snippet");
		shell.setLayout(new FillLayout(SWT.VERTICAL));

		final TipOfTheDay tip = new TipOfTheDay();
		tip.addTip("This is the first tip<br/> " + "<b>This is the first tip</b> " + "<u>This is the first tip</u> " + "<i>This is the first tip</i> " + "This is the first tip " + "This is the first tip<br/>" + "This is the first tip "
				+ "This is the first tip");
		tip.addTip("This is the second tip<br/> " + "<b>This is the second tip</b> " + "<u>This is the second tip</u> <br/>" + "<i>This is the second tip</i> " + "This is the second tip " + "This is the second tip <br/>" + "This is the second tip "
				+ "This is the second tip");

		tip.addTip("This is the third tip<br/> " + "<b>This is the third tip</b> " + "<u>This is the third tip</u> <br/>" + "<i>This is the third tip</i> ");

		final Button button1 = new Button(shell, SWT.PUSH);
		button1.setText("Open Tip of the Day dialog (default style)");

		button1.addListener(SWT.Selection, e -> {
			tip.setStyle(TipStyle.TWO_COLUMNS);
			tip.open(shell);
		});

		final Button button2 = new Button(shell, SWT.PUSH);
		button2.setText("Open Tip of the Day dialog (2 columns large)");

		button2.addListener(SWT.Selection, e -> {
			tip.setStyle(TipStyle.TWO_COLUMNS_LARGE);
			tip.open(shell);
		});

		final Button button3 = new Button(shell, SWT.PUSH);
		button3.setText("Open Tip of the Day dialog (header)");

		button3.addListener(SWT.Selection, e -> {
			tip.setStyle(TipStyle.HEADER);
			tip.open(shell);
		});

		shell.pack();
		shell.open();
		SWTGraphicUtil.centerShell(shell);

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();
	}

}
