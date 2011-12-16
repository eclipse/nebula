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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;

/*
 * Creates a PGroup.
 *
 * For a list of all Nebula PGroup example snippets see
 * http://www.eclipse.org/nebula/widgets/pgroup/snippets.php
 */
public class PGroupSnippet1 {

public static void main (String [] args) {
    Display display = new Display ();
    Shell shell = new Shell (display);
    shell.setLayout(new GridLayout());

    PGroup group = new PGroup(shell, SWT.SMOOTH);
    group.setText("Example");
    
//  Optionally, change strategy and toggle
//  group.setStrategy(new FormGroupStrategy());
//  group.setToggleRenderer(new TwisteToggleRenderer());
    
    group.setLayout(new GridLayout());
    
    Label label = new Label(group,SWT.NONE);
    label.setText("Contents");
    Button button = new Button(group,SWT.PUSH);
    button.setText("Contents");
    Scale scale = new Scale(group,SWT.HORIZONTAL);
    
    
    shell.setSize(200,200);
    shell.open ();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch ()) display.sleep ();
    }
    display.dispose ();
}
} 