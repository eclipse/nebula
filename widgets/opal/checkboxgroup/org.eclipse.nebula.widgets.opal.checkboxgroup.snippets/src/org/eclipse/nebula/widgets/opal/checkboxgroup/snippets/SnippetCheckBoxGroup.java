/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API
 * and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.checkboxgroup.snippets;

import org.eclipse.nebula.widgets.opal.checkboxgroup.CheckBoxGroup;
import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * This snippet demonstrates the checkBoxGroup widget
 *
 */
public class SnippetCheckBoxGroup {
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setBackgroundMode(SWT.INHERIT_DEFAULT);
		final FillLayout layout1 = new FillLayout(SWT.VERTICAL);
		layout1.marginWidth = layout1.marginHeight = 10;
		shell.setLayout(layout1);

		// Displays the group
		final CheckBoxGroup group = new CheckBoxGroup(shell, SWT.NONE);
		group.setLayout(new GridLayout(4, false));
		group.setText("Use proxy server");

		final Composite content = group.getContent();

		final Label lblServer = new Label(content, SWT.NONE);
		lblServer.setText("Server:");
		lblServer.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		final Text txtServer = new Text(content, SWT.NONE);
		txtServer.setText("proxy.host.com");
		txtServer.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		final Label lblPort = new Label(content, SWT.NONE);
		lblPort.setText("Port:");
		lblPort.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		final Text txtPort = new Text(content, SWT.NONE);
		txtPort.setText("1234");
		txtPort.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		final Label lblUser = new Label(content, SWT.NONE);
		lblUser.setText("User ID:");
		lblUser.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		final Text txtUser = new Text(content, SWT.NONE);
		txtUser.setText("MyName");
		txtUser.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		final Label lblPassword = new Label(content, SWT.NONE);
		lblPassword.setText("Password:");
		lblPassword.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		final Text txtPassword = new Text(content, SWT.PASSWORD);
		txtPassword.setText("password");
		txtPassword.setEnabled(false);
		txtPassword.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		// Open the shell
		shell.setSize(640, 360);
		SWTGraphicUtil.centerShell(shell);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

}
