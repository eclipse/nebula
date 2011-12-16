/*******************************************************************************
 * Copyright (c) 2006-2007 Nicolas Richeton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors :
 *    Nicolas Richeton (nicolas.richeton@gmail.com) - initial implementation
 *******************************************************************************/
package org.eclipse.nebula.snippets.collapsiblebuttons;

import org.eclipse.nebula.widgets.collapsiblebuttons.CollapsibleButtons;
import org.eclipse.nebula.widgets.collapsiblebuttons.IColorManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Nicolas Richeton (nicolas.richeton@gmail.com)
 */

public class CollapsibleButtonsSnippet1 {

	public static void main(String[] args) {
		Display display = new Display();

		Image itemImage24 = new Image(display, Program
				.findProgram("jpg").getImageData().scaledTo(24, 24)); //$NON-NLS-1$
		Image itemImage16 = new Image(display, Program
				.findProgram("jpg").getImageData().scaledTo(16, 16)); //$NON-NLS-1$

		Shell shell = new Shell(display);
		shell.setSize(400, 400);
		shell.setLayout(new FillLayout());

		Composite inner = new Composite(shell, SWT.NONE);
		GridLayout gl = new GridLayout(1, true);
		gl.marginBottom = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		inner.setLayout(gl);

		CollapsibleButtons collapsibleButtons = new CollapsibleButtons(inner,
				SWT.NONE, IColorManager.SKIN_OFFICE_2007);
		collapsibleButtons.setLayoutData(new GridData(GridData.GRAB_VERTICAL
				| GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_END));

		for (int i = 0; i < 5; i++) {
			collapsibleButtons.addButton("Button "+i, "Tooltip "+i, itemImage24,
					itemImage16);
		}
		
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		display.dispose();
		itemImage16.dispose();
		itemImage24.dispose();
	}
}
