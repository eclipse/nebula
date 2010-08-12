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

import org.eclipse.nebula.effects.stw.Transition;
import org.eclipse.nebula.effects.stw.TransitionManager;
import org.eclipse.nebula.effects.stw.TransitionListener;
import org.eclipse.nebula.effects.stw.Transitionable;
import org.eclipse.nebula.effects.stw.transitions.FadeTransition;
import org.eclipse.nebula.effects.stw.transitions.SlideTransition;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


/**
 * @author Ahmed Mahran (ahmahran@gmail.com)
 */
public class TransitionTest {

	private Shell sShell = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private Button button = null;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/* Before this is run, be sure to set up the launch configuration (Arguments->VM Arguments)
		 * for the correct SWT library path in order to run with the SWT dlls. 
		 * The dlls are located in the SWT plugin jar.  
		 * For example, on Windows the Eclipse SWT 3.1 plugin jar is:
		 *       installation_directory\plugins\org.eclipse.swt.win32_3.1.0.jar
		 */
		Display display = Display.getDefault();
		TransitionTest thisClass = new TransitionTest();
		thisClass.createSShell();
		thisClass.sShell.open();

		while (!thisClass.sShell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	/**
	 * This method initializes sShell
	 */
	private void createSShell() {
		sShell = new Shell();
		sShell.setText("Shell");
		sShell.setSize(new Point(800, 600));
		sShell.setLayout(new FillLayout());
		button = new Button(sShell, SWT.NONE);
		TransitionManager tm = new TransitionManager (new Transitionable() {
			public void setSelection(int index) {
			}
			public int getSelection() {
				return 0;
			}
			public Control getControl(int index) {
				return button;
			}
			public Composite getComposite() {
				return sShell;
			}
			public double getDirection(int toIndex, int fromIndex) {
			    if(Math.random() > 0.5)
                    return Math.random() > 0.5 ? Transition.DIR_RIGHT : Transition.DIR_LEFT;
                else
                    return Math.random() > 0.5 ? Transition.DIR_DOWN : Transition.DIR_UP;
			}
			public void addSelectionListener(final SelectionListener listener) {
				button.addSelectionListener(listener);
			}
		});
		
		SlideTransition st = new SlideTransition(tm);
		tm.setTransition(st);
		
		tm.addTransitionListener(new TransitionListener() {
            public void transitionFinished(TransitionManager transition) {
                System.out.println("End Of Transition! current item: " 
                        + transition.getTransitionable().getSelection());
            }
        });
		
	}

}
