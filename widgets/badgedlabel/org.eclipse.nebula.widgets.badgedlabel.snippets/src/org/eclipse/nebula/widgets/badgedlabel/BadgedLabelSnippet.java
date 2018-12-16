/*******************************************************************************
 * Copyright (c) 2018 Akuiteo (http://www.akuiteo.com). All rights reserved. This program and the
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

		icon = new Image(display, BadgedLabelSnippet.class.getClassLoader().getResourceAsStream("org/eclipse/nebula/widgets/badgedlabel/user.png"));

		createButtons("Blue :", BADGE_COLOR.BLUE, BADGE_LOCATION.TOP_LEFT);
		createButtons("Grey:", BADGE_COLOR.GREY, BADGE_LOCATION.TOP_RIGHT);
		createButtons("Green:", BADGE_COLOR.GREEN, BADGE_LOCATION.BOTTOM_LEFT);
		createButtons("Red:", BADGE_COLOR.RED, BADGE_LOCATION.BOTTOM_RIGHT);
		createButtons("Yellow:", BADGE_COLOR.YELLOW, BADGE_LOCATION.TOP_RIGHT);
		createButtons("Cyan:", BADGE_COLOR.CYAN, BADGE_LOCATION.TOP_LEFT);
		createButtons("Black:", BADGE_COLOR.BLACK, BADGE_LOCATION.BOTTOM_RIGHT);

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

	private static void createButtons(final String text, BADGE_COLOR color, BADGE_LOCATION location) {
		final Label label = new Label(shell, SWT.NONE);
		label.setBackground(label.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		String locationText;
		switch (location) {
			case BOTTOM_LEFT:
				locationText = "Bottom left";
				break;
			case BOTTOM_RIGHT:
				locationText = "Bottom right";
				break;
			case TOP_LEFT:
				locationText = "Top left";
				break;
			default:
				locationText = "Top right";
		}
		label.setText(text + " " + locationText);
		label.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		final BadgedLabel button1 = new BadgedLabel(shell, SWT.NONE);
		button1.setText("Notification");
		final GridData gd = new GridData(GridData.FILL, GridData.CENTER, false, false);
		gd.widthHint = 200;
		gd.heightHint = 100;
		button1.setLayoutData(gd);
		button1.setBadgeLocation(location);
		button1.changeBadgeColor(color);
		button1.setBadgeValue("1");
		button1.setBackground(label.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		final BadgedLabel button2 = new BadgedLabel(shell, SWT.NONE);
		button2.setText("Text & image");
		button2.setImage(icon);
		button2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		button2.setBadgeLocation(location);
		button2.changeBadgeColor(color);
		button2.setBadgeValue("2");
		button2.setBackground(label.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		final BadgedLabel button3 = new BadgedLabel(shell, SWT.NONE);
		button3.setImage(icon);
		button3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		button3.setBadgeLocation(location);
		button3.changeBadgeColor(color);
		button3.setBadgeValue("99+");
		button3.setBackground(label.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		final BadgedLabel button4 = new BadgedLabel(shell, SWT.NONE);
		button4.setText("Disabled");
		button4.setEnabled(false);
		button4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		button4.setBadgeLocation(location);
		button4.changeBadgeColor(color);
		button4.setBadgeValue("New");
		button4.setBackground(label.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}
}
