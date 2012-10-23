/*******************************************************************************
 *  Copyright (c) 2010,2012 Weltevree Beheer BV, Remain Software & Industrial-TSI
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
public class MultiScope_ScopeWithDataAndProgression2Channels_2 {

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

		// OscilloscopeStackAdapter stackAdapter = getStackAdapter();
		scope.addStackListener(0, getStackAdapter());
		scope.addStackListener(1, getStackAdapter());

		scope.getDispatcher(0).dispatch();

	}

	private static OscilloscopeStackAdapter getStackAdapter() {

		return new OscilloscopeStackAdapter() {
			double[] value;
			double[] counter;
			boolean init = false;

			@Override
			public void stackEmpty(Oscilloscope scope, int channel) {

				if (!init) {
					init = true;
					value = new double[scope.getChannels()];
					counter = new double[scope.getChannels()];
					for (int i = 0; i < scope.getChannels(); i++) {
						counter[i] = (double) (i + 10) / 100;
						System.out.println(counter[i]);
						value[i] = new Random().nextInt((int) (200 * Math.PI)) / 100;
					}
				}

				value[channel] += counter[channel];
				if (value[channel] > 2 * Math.PI) {
					value[channel] = 0;
				}

				int intValue = (int) (Math.sin(value[channel]) * 100);
				scope.setValue(channel, intValue);
			}
		};
	}
}
