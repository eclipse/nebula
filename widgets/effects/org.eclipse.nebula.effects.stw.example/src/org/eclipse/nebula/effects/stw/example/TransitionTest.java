/*******************************************************************************
 * Copyright (c) 2010 Ahmed Mahran and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html 
 *
 * Contributors:
 *     Ahmed Mahran - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.effects.stw.example;

import org.eclipse.nebula.effects.stw.TransitionManager;
import org.eclipse.nebula.effects.stw.Transitionable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


/**
 * @author Ahmed Mahran (ahmahran@gmail.com)
 */
public class TransitionTest extends AbstractSTWDemoFrame {

	private Button button = null;

    @Override
    public void init() {
        
        _containerComposite.setLayout(new FillLayout());
        button = new Button(_containerComposite, SWT.NONE);
        button.setText("I'm a button!");
        
        _tm = new TransitionManager (new Transitionable() {
            public void setSelection(int index) {
            }
            public int getSelection() {
                return 0;
            }
            public Control getControl(int index) {
                return button;
            }
            public Composite getComposite() {
                return _containerComposite;
            }
            public double getDirection(int toIndex, int fromIndex) {
                return getSelectedDirection(toIndex, fromIndex);
            }
            public void addSelectionListener(final SelectionListener listener) {
                button.addSelectionListener(listener);
            }
        });
        
    }
    
}
