/*******************************************************************************
 *  Copyright (c) 2010 Weltevree Beheer BV, Remain Software & Industrial-TSI
 * 
 * All rights reserved. 
 * This program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Wim S. Jongman - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.oscilloscope.multichannel;

import java.util.Random;

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
public class Snippet5_ScopeWithDataAndProgression2Channels {

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

		// scope.setSteady();

	//	OscilloscopeStackAdapter stackAdapter = getStackAdapter();
		scope.addStackListener(0, getStackAdapter());
		scope.addStackListener(1, getStackAdapter());

		scope.getDispatcher(0).dispatch();

	}

	private static OscilloscopeStackAdapter getStackAdapter() {

		return new OscilloscopeStackAdapter() {
			double value = Math.PI;
			double counter = .1;

			@Override
			public void stackEmpty(Oscilloscope scope, int channel) {

				if (channel == 0) {

					value += counter;
					if (value > 2 * Math.PI) {
						value = 0;
					}

					int intValue = (int) (Math.sin(value) * 100);
					scope.setValue(channel, intValue);

				}

				if (channel == 1) {
					
					value += counter;
					if (value > 2 * Math.PI) {
						value = 0;
					}
					
					int intValue = (int) (Math.cos(value) * 40);
					scope.setValue(channel, intValue);
					
				}
			}
		};

	}
}
