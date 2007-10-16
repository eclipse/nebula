/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/ 

package org.eclipse.nebula.widgets.collapsiblebuttons;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ButtonsTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = new Display ();
		Shell shell = new Shell (display);
		shell.setText("Buttons Widget Tester");
		shell.setSize(200, 400);
		
		shell.setLayout(new FillLayout());
		Composite inner = new Composite(shell, SWT.None);
		GridLayout gl = new GridLayout(1, true);
		gl.marginBottom = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		inner.setLayout(gl);

		CollapsibleButtons bc = new CollapsibleButtons(inner, SWT.NONE, IColorManager.SKIN_OFFICE_2007);
		bc.setLayoutData(new GridData(GridData.GRAB_VERTICAL | GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_END));
		bc.addButton("Synchronize", null, ImageCache.getImage("selection_recycle_24.png"), ImageCache.getImage("selection_recycle_16.gif"));
		bc.addButton("Folders", null, ImageCache.getImage("folder.png"), ImageCache.getImage("folder_16.gif"));
		bc.addButton("Preferences", null, ImageCache.getImage("notebook_preferences.png"), ImageCache.getImage("notebook_preferences_16.gif"));
		bc.addButton("Syntax Check", null, ImageCache.getImage("gear_ok.png"), ImageCache.getImage("gear_ok_16.gif"));
		bc.addButton("Library ", null, ImageCache.getImage("books.png"), ImageCache.getImage("books_16.gif"));
		bc.addButtonListener(new IButtonListener() {
			public void buttonClicked(CustomButton button, MouseEvent e) {
				System.err.println("You clicked " + button);
			}

			public void buttonEnter(CustomButton button, MouseEvent e) {
			}

			public void buttonExit(CustomButton button, MouseEvent e) {
			}

			public void buttonHover(CustomButton button, MouseEvent e) {
			}			
		});
		
		shell.open();

		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}

}
