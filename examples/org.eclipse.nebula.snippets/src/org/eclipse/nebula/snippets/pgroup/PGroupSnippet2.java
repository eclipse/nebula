/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.snippets.pgroup;

import org.eclipse.nebula.widgets.pgroup.PGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;

/*
 * Create a group with rounded corners that correctly shows
 * the parent's background.
 *
 * For a list of all Nebula PGroup example snippets see
 * http://www.eclipse.org/nebula/widgets/pgroup/snippets.php
 */
public class PGroupSnippet2 {

public static void main (String [] args) {
    Display display = new Display ();
    Shell shell = new Shell (display);
    shell.setLayout(new GridLayout());

    //Set the shell background to something different
    shell.setBackground(display.getSystemColor(SWT.COLOR_RED));
    
    //Tell the shell to give its children the same background color or image
    shell.setBackgroundMode(SWT.INHERIT_DEFAULT);
    
    //Optionally trying creating a patterned image for the shell background
//    final Image backImage = new Image(display,10,10);
//    GC gc = new GC(backImage);
//    gc.drawLine(0,0,9,9);
//    gc.dispose();
//    shell.addDisposeListener(new DisposeListener() {
//		public void widgetDisposed(DisposeEvent e) {
//			backImage.dispose();
//		}	
//	});
//    shell.setBackgroundImage(backImage);
    
    PGroup group = new PGroup(shell, SWT.SMOOTH);
    group.setText("Example");
    group.setLayout(new FillLayout());    
    group.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
    
    Composite groupClient = new Composite(group,SWT.NONE);
    groupClient.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
    
    groupClient.setLayout(new GridLayout());
    
    Label label = new Label(groupClient,SWT.NONE);
    label.setText("Contents");
    Button button = new Button(groupClient,SWT.PUSH);
    button.setText("Contents");
    Scale scale = new Scale(groupClient,SWT.HORIZONTAL);
    
    
    shell.setSize(200,200);
    shell.open ();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch ()) display.sleep ();
    }
    display.dispose ();
}
} 