/*******************************************************************************
 * Copyright (C) 2011 Angelo Zerr <angelo.zerr@gmail.com>, Pascal Leclercq <pascal.leclercq@gmail.com>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo ZERR - initial API and implementation
 *     Pascal Leclercq - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.picture.snippets;

import org.eclipse.nebula.widgets.picture.PictureControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Picture control with default image example.
 * 
 */
public class PictureControlWithDefaultImage {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		GridLayout layout = new GridLayout(2, false);
		shell.setLayout(layout);

		Label label = new Label(shell, SWT.NONE);
		label.setText("Photo:");
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.TOP;
		label.setLayoutData(gridData);

		Image defaultImage = new Image(display,
				PictureControlWithDefaultImage.class
						.getResourceAsStream("EmptyPhoto.jpg"));

		PictureControl photoControl = new PictureControl(shell);
		photoControl.setDefaultImage(defaultImage);
		photoControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		shell.setSize(200, 150);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		defaultImage.dispose();
		display.dispose();
	}
}
