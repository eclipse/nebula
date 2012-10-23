/*******************************************************************************
 *  Copyright (c) 2010, 2012 Weltevree Beheer BV, Remain Software & Industrial-TSI
 * 
 * All rights reserved. 
 * This program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Wim S. Jongman - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.oscilloscope.snippets;

import java.util.Random;

import org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope;
import org.eclipse.nebula.widgets.oscilloscope.multichannel.OscilloscopeStackAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This snippet demonstrates how to run the dispatcher in simple mode.
 * 
 */
public class MultiScope_ScopeWithDataAndProgression {

	protected static Shell shell;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected static void createContents() {
		shell = new Shell();
		shell.setText("Nebula Oscilloscope");
		shell.setLayout(new FillLayout());

		// Create a single channel scope
		final Oscilloscope scope = new Oscilloscope(shell, SWT.NONE);
		scope.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				scope.setProgression(0, ((Oscilloscope) e.widget).getSize().x);
			}
		});
		scope.addStackListener(0, new OscilloscopeStackAdapter() {
			private int oldp;
			private int[] ints;

			@Override
			public void stackEmpty(Oscilloscope scope, int channel) {
				Random random = new Random();
				if (oldp != scope.getProgression(0)) {
					oldp = scope.getProgression(0);
					ints = new int[oldp];
					for (int i = 0; i < ints.length; i++) {
						int inti = 10 - random.nextInt(20);
						ints[i++] = inti;
					}
				} else {
					for (int i = 0; i < ints.length; i++) {
						int inti = 2 - random.nextInt(5);
						ints[i] = ints[i++] + inti;
					}

				}
				
				scope.setValues(0, ints);
			}
		});
		
		scope.getDispatcher(0).dispatch();

	}
}
