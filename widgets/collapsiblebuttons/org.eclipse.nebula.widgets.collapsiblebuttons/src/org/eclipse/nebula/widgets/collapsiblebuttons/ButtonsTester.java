/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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

		System.err.println(ButtonsTester.class.getResource("icons/file_foo_16.png"));
		
		System.err.println(ImageCache.getImage("file_foo_16.png"));

		final CollapsibleButtons bc = new CollapsibleButtons(inner, SWT.NONE, IColorManager.SKIN_OFFICE_2007);
		bc.setLayoutData(new GridData(GridData.GRAB_VERTICAL | GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_END));
		bc.addButton("Synchronize", null, ImageCache.getImage("selection_recycle_24.png"), ImageCache.getImage("selection_recycle_16.gif"));
		bc.addButton("Folders", null, ImageCache.getImage("folder.png"), ImageCache.getImage("folder_16.gif"));
		bc.addButton("Preferences", null, ImageCache.getImage("notebook_preferences.png"), ImageCache.getImage("notebook_preferences_16.gif"));
		bc.addButton("Syntax Check", null, ImageCache.getImage("gear_ok.png"), ImageCache.getImage("gear_ok_16.gif"));
				
		bc.addButton("Library ", "Test", ImageCache.getImage("icons/arrows.gif"), ImageCache.getImage("icons/arrows.gif"));
		bc.addButtonListener(new IButtonListener() {
			public void buttonClicked(CustomButton button, MouseEvent e) {
				if (button.getText().equals("Library ")) {
					bc.removeAllButtons();		
					bc.addButton("Test", null, ImageCache.getImage("icons/arrows.gif"), null);
				}
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
		/*
		Display display = new Display ();
		Shell shell = new Shell (display);
		shell.setText("Buttons Widget Tester");
		shell.setSize(200, 400);

		shell.setLayout(new FillLayout());
		Composite inner = new Composite(shell, SWT.None);
		inner.setLayout(new FillLayout());

		SashForm sash = new SashForm(inner, SWT.VERTICAL);
		Composite top = new Composite(sash, SWT.NONE);
		top.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		top.setLayout( new FillLayout());
		Composite bottom = new Composite(sash, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		layout.marginBottom = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginWidth = 0;
		bottom.setLayout( layout );

		sash.setWeights( new int[] { 20, 80 } );

		CollapsibleButtons bc = new CollapsibleButtons(bottom, SWT.NONE, IColorManager.SKIN_AUTO_DETECT);
		//bc.setLayoutData(new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_END));
		bc.setLayoutData(new GridData(GridData.GRAB_VERTICAL | GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_END));
		bc.addButton("Synchronize", "Synchronize", ImageCache.getImage("selection_recycle_24.png"), ImageCache.getImage("selection_recycle_16.gif"));
		bc.addButton("Folders", "Folders", ImageCache.getImage("folder.png"), ImageCache.getImage("folder_16.gif"));
		bc.addButton("Preferences", "Preferences", ImageCache.getImage("notebook_preferences.png"), ImageCache.getImage("notebook_preferences_16.gif"));
		bc.addButton("Syntax Check", "Syntax Check", ImageCache.getImage("gear_ok.png"), ImageCache.getImage("gear_ok_16.gif"));
		bc.addButton("Library ", "Library ", ImageCache.getImage("books.png"), ImageCache.getImage("books_16.gif"));

		//Add three buttons, setVisible to false, and add them to toolbar
		Image image = new Image(display, "icons/arrows.gif");
		ToolbarComposite toolbar = bc.getToolbarComposite();
		CustomButton cb1 = bc.addButton("A", "A", image, image);
		cb1.setVisible(false);
		toolbar.addItem(cb1);

		CustomButton cb2 =bc.addButton("B", "B", image, image);
		cb2.setVisible(false);
		toolbar.addItem(cb2);

		CustomButton cb3 =bc.addButton("C", "C ", image, image );
		cb3.setVisible(false);
		toolbar.addItem(cb3);

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
*/
		shell.open();

		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}

}
