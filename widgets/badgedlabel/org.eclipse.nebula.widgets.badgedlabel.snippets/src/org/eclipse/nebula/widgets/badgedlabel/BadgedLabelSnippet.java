/*******************************************************************************
 * Copyright (c) 2019 Akuiteo (http://www.akuiteo.com). All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API and
 * implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.badgedlabel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Snippet for the BadgedLabel widget
 */
public class BadgedLabelSnippet {
	private static Shell shell;
	private static Image icon;

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final Display display = new Display();
		shell = new Shell(display);
		shell.setText("BadgedLabel Snippet");
		shell.setLayout(new GridLayout(5, false));
		shell.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

		icon = new Image(display, BadgedLabelSnippet.class.getClassLoader()
				.getResourceAsStream("org/eclipse/nebula/widgets/badgedlabel/user.png"));

		createButtons("Blue :", SWT.COLOR_BLUE, SWT.TOP | SWT.LEFT);
		createButtons("Grey:", SWT.COLOR_GRAY, SWT.TOP | SWT.RIGHT);
		createButtons("Green:", SWT.COLOR_GREEN, SWT.BOTTOM | SWT.LEFT);
		createButtons("Red:", SWT.COLOR_RED, SWT.BOTTOM | SWT.RIGHT);
		createButtons("Yellow:", SWT.COLOR_YELLOW, SWT.TOP | SWT.RIGHT);
		createButtons("Cyan:", SWT.COLOR_CYAN, SWT.TOP | SWT.LEFT);
		createButtons("Black:", SWT.COLOR_BLACK, SWT.BOTTOM | SWT.RIGHT);

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		icon.dispose();
		display.dispose();

	}

	private static void createButtons(final String text, int color, int location) {
		final Label label = new Label(shell, SWT.NONE);
		label.setBackground(label.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		String locationText;
		if (location == (SWT.BOTTOM | SWT.LEFT)) {
			locationText = "Bottom left";
		} else if (location == (SWT.BOTTOM | SWT.RIGHT)) {
			locationText = "Bottom right";
		} else if (location == (SWT.TOP | SWT.LEFT)) {
			locationText = "Top left";
		} else {
			locationText = "Top right";
		}

		label.setText(text + " " + locationText);
		label.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		final BadgedLabel button1 = new BadgedLabel(shell, location);
		button1.setText("Notification");
		final GridData gd = new GridData(GridData.FILL, GridData.CENTER, false, false);
		gd.widthHint = 200;
		gd.heightHint = 100;
		button1.setLayoutData(gd);
		button1.setBadgeValue("1");
		button1.setPredefinedColor(color);
		button1.setBackground(label.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		final BadgedLabel button2 = new BadgedLabel(shell, location);
		button2.setText("Text & image");
		button2.setImage(icon);
		button2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		button2.setPredefinedColor(color);
		button2.setBadgeValue("2");
		button2.setBackground(label.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		final BadgedLabel button3 = new BadgedLabel(shell, location);
		button3.setImage(icon);
		button3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		button3.setPredefinedColor(color);
		button3.setBadgeValue("99+");
		button3.setBackground(label.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		final BadgedLabel button4 = new BadgedLabel(shell, location);
		button4.setText("Disabled");
		button4.setEnabled(false);
		button4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		button4.setPredefinedColor(color);
		button4.setBadgeValue("New");
		button4.setBackground(label.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}
}
