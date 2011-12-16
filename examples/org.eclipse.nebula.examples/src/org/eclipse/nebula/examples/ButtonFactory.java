/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    cgross - initial API and implementation
 *******************************************************************************/ 
package org.eclipse.nebula.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

/**
 * A factory class that allows for easier creation of <code>Button</code>s.
 *
 * @author cgross
 */
public class ButtonFactory
{

    /**
     * Creates a <code>Button</code>.  
     * 
     * @param parent the button's parent.
     * @param style the button's style.
     * @param text the button's text.
     * @param selectionListener a selection listener for the button.
     * @return the new button.
     */
    public static Button create(Composite parent, int style, String text, Listener selectionListener)
    {
        Button b = new Button(parent,style);
        b.setText(text);
        b.addListener(SWT.Selection,selectionListener);
        
        return b;
    }
    
    /**
     * Creates a <code>Button</code>.
     * 
     * @param parent the button's parent.
     * @param style the button's style.
     * @param text the button's text.
     * @param selectionListener a selection listener for the button.
     * @param selection the default selection state of the button (only applicable with SWT.CHECK or SWT.RADIO).
     * @return the new button.
     */
    public static Button create(Composite parent, int style, String text, Listener selectionListener, boolean selection)
    {
        Button b = create(parent, style, text, selectionListener);
        b.setSelection(selection);
        return b;
    }
    
}
