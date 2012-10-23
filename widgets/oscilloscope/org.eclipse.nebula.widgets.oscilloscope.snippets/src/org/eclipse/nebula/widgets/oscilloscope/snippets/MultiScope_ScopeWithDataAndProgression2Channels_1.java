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
public class MultiScope_ScopeWithDataAndProgression2Channels_1 {

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
		final Oscilloscope scope = new Oscilloscope(2, shell, SWT.NONE);
		scope.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				scope.setProgression(0, ((Oscilloscope) e.widget).getSize().x);
				scope.setProgression(1, ((Oscilloscope) e.widget).getSize().x);
			}
		});
		
		//scope.setSteady();

		OscilloscopeStackAdapter stackAdapter = getStackAdapter();
		scope.addStackListener(0, stackAdapter);
		scope.addStackListener(1, stackAdapter);

		scope.getDispatcher(0).dispatch();

	}

	private static OscilloscopeStackAdapter getStackAdapter() {

		return new OscilloscopeStackAdapter() {
			private int oldp;
			private int[] ints;

			@Override
			public void stackEmpty(Oscilloscope scope, int channel) {
				Random random = new Random();

				if (channel == 0) {

					if (oldp != scope.getProgression(channel)) {
						oldp = scope.getProgression(channel);
						ints = new int[oldp];
						for (int i = 0; i < ints.length - 8; i++) {
							int inti = 20 - random.nextInt(40);
							ints[i++] = inti;
							ints[i++] = inti;
							ints[i++] = inti;
							ints[i++] = inti;
							ints[i++] = inti;
							ints[i++] = inti;
							ints[i++] = inti;
							ints[i++] = inti;
						}
					} else {
						for (int i = 0; i < ints.length - 8; i++) {
							int inti = 2  - random.nextInt(5);
							ints[i] = ints[i++] + inti;
							ints[i] = ints[i++] + inti;
							ints[i] = ints[i++] + inti;
							ints[i] = ints[i++] + inti;
							ints[i] = ints[i++] + inti;
							ints[i] = ints[i++] + inti;
							ints[i] = ints[i++] + inti;
							ints[i] = ints[i++] + inti;
						}

					}
					scope.setValues(channel, ints);
				}

				else {
					int[] onts = new int[ints.length];
					for (int i = 0; i < ints.length; i++) {
						onts[i] = -1 * ints[i];
					}
					scope.setValues(channel, onts);
				}
			}
		};

		// TODO Auto-generated method stub
	}
}
