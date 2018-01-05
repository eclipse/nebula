/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API
 * and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.loginDialog.snippets;

import java.util.Locale;

import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.nebula.widgets.opal.loginDialog.LoginDialog;
import org.eclipse.nebula.widgets.opal.loginDialog.LoginDialogVerifier;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * This snippet demonstrates the Login Dialog widget
 *
 */
public class LoginDialogSnippet {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		Locale.setDefault(Locale.ENGLISH);

		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("Login dialog snippet");
		shell.setLayout(new GridLayout(2, false));

		// Basic Login dialog
		final Label label1 = new Label(shell, SWT.WRAP);
		label1.setText("This is the basic dialog box, \nwithout any customization");
		label1.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		final Button button1 = new Button(shell, SWT.PUSH);
		button1.setText("Open basic dialog");
		button1.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false));

		final LoginDialogVerifier verifier = new LoginDialogVerifier() {

			@Override
			public void authenticate(final String login, final String password) throws Exception {
				if ("".equals(login)) {
					throw new Exception("Please enter a login.");
				}

				if ("".equals(password)) {
					throw new Exception("Please enter a password.");
				}

				if (!login.equalsIgnoreCase("laurent")) {
					throw new Exception("Login unknown.");
				}

				if (!password.equalsIgnoreCase("laurent")) {
					throw new Exception("Authentication failed, please check your password.");
				}

			}
		};

		button1.addSelectionListener(new SelectionAdapter() {

			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(final SelectionEvent e) {

				final LoginDialog dialog = new LoginDialog();
				dialog.setVerifier(verifier);

				final boolean result = dialog.open();
				if (result) {
					System.out.println("Login confirmed : " + dialog.getLogin());
				} else {
					System.out.println("User canceled !");
				}
			}
		});

		final Label separator = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1));

		// Login dialog with image, description, default login, and no button
		// "remember my password"
		final Label label2 = new Label(shell, SWT.NONE);
		label2.setText(
				"This is a customized login (image, description, \ndefault login, multiple login values, \nno button 'remember my password'");
		label2.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		final Button button2 = new Button(shell, SWT.PUSH);
		button2.setText("Open customized dialog");
		button2.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));

		button2.addSelectionListener(new SelectionAdapter() {

			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(final SelectionEvent e) {

				final LoginDialog dialog = new LoginDialog();
				dialog.setImage(new Image(display, LoginDialogSnippet.class.getClassLoader()
						.getResourceAsStream("org/eclipse/nebula/opal/loginDialog/snippets/image.png")));
				dialog.setDescription(
						"Please login to our system...\nPlease remember that the password is the same as the login :)");
				dialog.setAutorizedLogin("Laurent", "Albert", "Erik", "Ulrich", "Luis");
				dialog.setLogin("Laurent");
				dialog.setDisplayRememberPassword(false);
				dialog.setVerifier(verifier);

				final boolean result = dialog.open();
				if (result) {
					System.out.println("Login confirmed : " + dialog.getLogin());
				} else {
					System.out.println("User canceled !");
				}
			}
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
