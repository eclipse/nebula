/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API
 * and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.titledseparator.snippets;

import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.nebula.widgets.opal.titledseparator.TitledSeparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This snippet demonstrates the TitledSeparator widget
 *
 */
public class TitledSeparatorSnippet {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, false));

		final Image icon = new Image(display, TitledSeparatorSnippet.class.getResourceAsStream("user.png"));
		final Font font = new Font(display, "Courier New", 18, SWT.BOLD | SWT.ITALIC);

		// Default separator (no image, title aligned on the left)
		final TitledSeparator sep1 = new TitledSeparator(shell, SWT.NONE);
		sep1.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		sep1.setText("Customer Info");

		// Separator with image
		final TitledSeparator sep2 = new TitledSeparator(shell, SWT.NONE);
		sep2.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		sep2.setText("Customer Info");
		sep2.setImage(icon);

		// Separator aligned on the right
		final TitledSeparator sep3 = new TitledSeparator(shell, SWT.NONE);
		sep3.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		sep3.setText("Customer Info");
		sep3.setAlignment(SWT.RIGHT);

		// Custom font & text color
		final TitledSeparator sep4 = new TitledSeparator(shell, SWT.NONE);
		sep4.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		sep4.setText("Customized Color and Font");
		sep4.setAlignment(SWT.CENTER);

		sep4.setForeground(display.getSystemColor(SWT.COLOR_DARK_RED));
		sep4.setFont(font);

		shell.setSize(640, 350);
		shell.pack();
		shell.open();
		SWTGraphicUtil.centerShell(shell);

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		icon.dispose();
		font.dispose();

		display.dispose();
	}

}
