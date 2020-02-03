/*******************************************************************************
 * Copyright (c) 2020 Laurent CARON.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API and
 * implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.chips;

import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Snippet for the Stepbar widget
 */
public class ChipsSnippet {

	private static Shell shell;

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final Display display = new Display();
		shell = new Shell(display);
		shell.setText("Chips Snippet");
		shell.setLayout(new GridLayout(1, false));
		shell.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

		createColoredChipsArea();
		createCloseChipsArea();
		createCloseImageChipsArea();
		createCheckedChipsArea();
		createPushChipsArea();

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();

	}

	private static void createColoredChipsArea() {
		final Label lbl = new Label(shell, SWT.CENTER);
		lbl.setText("Colored chips (Eclipse Projects)");
		lbl.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		final Composite cmp = new Composite(shell, SWT.NONE);
		cmp.setLayoutData(new GridData(GridData.CENTER, GridData.FILL, false, false));
		cmp.setLayout(new GridLayout(5, false));
		cmp.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		final Chips chip1 = new Chips(cmp, SWT.NONE);
		chip1.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		chip1.setBackground(SWTGraphicUtil.getColorSafely(227, 22, 91));
		chip1.setText("Equinox");

		final Chips chip2 = new Chips(cmp, SWT.NONE);
		chip2.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		chip2.setBackground(SWTGraphicUtil.getColorSafely(3, 120, 213));
		chip2.setText("Nebula");

		final Chips chip3 = new Chips(cmp, SWT.NONE);
		chip3.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		chip3.setBackground(SWTGraphicUtil.getColorSafely(77, 132, 29));
		chip3.setText("XText");

		final Chips chip4 = new Chips(cmp, SWT.NONE);
		chip4.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		chip4.setBackground(SWTGraphicUtil.getColorSafely(214, 65, 19));
		chip4.setText("Wild Web Developer");

		final Chips chip5 = new Chips(cmp, SWT.NONE);
		chip5.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		chip5.setBackground(SWTGraphicUtil.getColorSafely(193, 87, 0));
		chip5.setText("JDT");
	}

	private static void createCloseChipsArea() {
		final Label lbl = new Label(shell, SWT.CENTER);
		lbl.setText("Close chips (Eclipse Fundation Members)");
		lbl.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		final Composite cmp = new Composite(shell, SWT.NONE);
		cmp.setLayoutData(new GridData(GridData.CENTER, GridData.FILL, false, false));
		cmp.setLayout(new GridLayout(4, false));
		cmp.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		final Color bgColor = SWTGraphicUtil.getColorSafely(224, 224, 244);

		final Chips chip1 = new Chips(cmp, SWT.CLOSE);
		chip1.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chip1.setBackground(bgColor);
		chip1.setText("IBM");

		final Chips chip2 = new Chips(cmp, SWT.CLOSE);
		chip2.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chip2.setBackground(bgColor);
		chip2.setText("Red Hat");

		final Chips chip3 = new Chips(cmp, SWT.CLOSE);
		chip3.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chip3.setBackground(bgColor);
		chip3.setText("Remain Software");

		final Chips chip4 = new Chips(cmp, SWT.CLOSE);
		chip4.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chip4.setBackground(bgColor);
		chip4.setText("Vogella");

	}

	private static void createCloseImageChipsArea() {
		final Label lbl = new Label(shell, SWT.CENTER);
		lbl.setText("Close & Images (Nebula Devs)");
		lbl.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		final Composite cmp = new Composite(shell, SWT.NONE);
		cmp.setLayoutData(new GridData(GridData.CENTER, GridData.FILL, false, false));
		cmp.setLayout(new GridLayout(5, false));
		cmp.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		final Color bgColor = SWTGraphicUtil.getColorSafely(224, 224, 244);

		final Chips chip1 = new Chips(cmp, SWT.CLOSE);
		chip1.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chip1.setBackground(bgColor);
		chip1.setHoverForeground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		chip1.setHoverBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chip1.setImage(loadImage("dirk.png"));
		chip1.setText("Dirk");

		final Chips chip2 = new Chips(cmp, SWT.CLOSE);
		chip2.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chip2.setBackground(bgColor);
		chip2.setHoverForeground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		chip2.setHoverBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chip2.setImage(loadImage("donald.png"));
		chip2.setText("Donald");

		final Chips chip3 = new Chips(cmp, SWT.CLOSE);
		chip3.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chip3.setBackground(bgColor);
		chip3.setHoverForeground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		chip3.setHoverBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chip3.setImage(loadImage("johannes.png"));
		chip3.setText("Johannes");

		final Chips chip4 = new Chips(cmp, SWT.CLOSE);
		chip4.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chip4.setBackground(bgColor);
		chip4.setHoverForeground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		chip4.setHoverBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chip4.setImage(loadImage("laurent.png"));
		chip4.setText("Laurent");

		final Chips chip5 = new Chips(cmp, SWT.CLOSE);
		chip5.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chip5.setBackground(bgColor);
		chip5.setHoverForeground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		chip5.setHoverBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chip5.setImage(loadImage("wim.png"));
		chip5.setText("Wim");

	}

	private static Image loadImage(final String img) {
		return new Image(shell.getDisplay(), ChipsSnippet.class.getResourceAsStream(img));
	}

	private static void createCheckedChipsArea() {
		final Label lbl = new Label(shell, SWT.CENTER);
		lbl.setText("Checked chips : Skills");
		lbl.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		final Composite cmp = new Composite(shell, SWT.NONE);
		cmp.setLayoutData(new GridData(GridData.CENTER, GridData.FILL, false, false));
		cmp.setLayout(new GridLayout(5, false));
		cmp.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		final Color bgColor = SWTGraphicUtil.getColorSafely(224, 224, 244);
		final Color checkColor = SWTGraphicUtil.getColorSafely(188, 188, 188);

		final Chips chip1 = new Chips(cmp, SWT.CHECK);
		chip1.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chip1.setBackground(bgColor);
		chip1.setPushedStateForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chip1.setPushedStateBackground(checkColor);
		chip1.setText("Java");

		final Chips chip2 = new Chips(cmp, SWT.CHECK);
		chip2.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chip2.setBackground(bgColor);
		chip2.setPushedStateForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chip2.setPushedStateBackground(checkColor);
		chip2.setText("SWT");

		final Chips chip3 = new Chips(cmp, SWT.CHECK);
		chip3.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chip3.setBackground(bgColor);
		chip3.setPushedStateForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chip3.setPushedStateBackground(checkColor);
		chip3.setText("JFace");

		final Chips chip4 = new Chips(cmp, SWT.CHECK);
		chip4.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chip4.setBackground(bgColor);
		chip4.setPushedStateForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chip4.setPushedStateBackground(checkColor);
		chip4.setText("EMF");

	}

	private static void createPushChipsArea() {
		final Label lbl = new Label(shell, SWT.CENTER);
		lbl.setText("Push chips");
		lbl.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		final Composite cmp = new Composite(shell, SWT.NONE);
		cmp.setLayoutData(new GridData(GridData.CENTER, GridData.FILL, false, false));
		cmp.setLayout(new GridLayout(4, false));
		cmp.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		final Color checkColor = SWTGraphicUtil.getColorSafely(227, 22, 91);

		final Chips chip1 = new Chips(cmp, SWT.PUSH);
		chip1.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chip1.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		chip1.setPushedStateForeground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		chip1.setPushedStateBackground(checkColor);
		chip1.setImage(loadImage("icons/bubble1_b.png"));
		chip1.setPushImage(loadImage("icons/bubble1_w.png"));
		chip1.setText("One");

		final Chips chip2 = new Chips(cmp, SWT.PUSH);
		chip2.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chip2.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		chip2.setPushedStateForeground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		chip2.setPushedStateBackground(checkColor);
		chip2.setImage(loadImage("icons/bubble2_b.png"));
		chip2.setPushImage(loadImage("icons/bubble2_w.png"));
		chip2.setText("Two");

		final Chips chip3 = new Chips(cmp, SWT.PUSH);
		chip3.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chip3.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		chip3.setPushedStateForeground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		chip3.setPushedStateBackground(checkColor);
		chip3.setImage(loadImage("icons/bubble3_b.png"));
		chip3.setPushImage(loadImage("icons/bubble3_w.png"));
		chip3.setText("Three");

		final Chips chip4 = new Chips(cmp, SWT.PUSH);
		chip4.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		chip4.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		chip4.setPushedStateForeground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		chip4.setPushedStateBackground(checkColor);
		chip4.setImage(loadImage("icons/email_b.png"));
		chip4.setPushImage(loadImage("icons/email_w.png"));
		chip4.setText("Mail");

	}

}