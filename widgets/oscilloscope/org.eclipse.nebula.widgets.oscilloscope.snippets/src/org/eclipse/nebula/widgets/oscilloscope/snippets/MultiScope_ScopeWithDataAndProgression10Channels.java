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

import org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope;
import org.eclipse.nebula.widgets.oscilloscope.multichannel.OscilloscopeDispatcher;
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
public class MultiScope_ScopeWithDataAndProgression10Channels {

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
		shell.setSize(400, 800);
		shell.setText("Nebula Oscilloscope");
		shell.setLayout(new FillLayout());

		// Create a single channel scope
		OscilloscopeDispatcher dsp = new OscilloscopeDispatcher() {

			@Override
			public void hookBeforeDraw(Oscilloscope oscilloscope, int counter) {
			}

			@Override
			public void hookAfterDraw(Oscilloscope oscilloscope, int counter) {
				// System.out.println(System.nanoTime() - timer);
			}

			@Override
			public int getDelayLoop() {
				return 0;
			}

			@Override
			public boolean getFade() {
				return false;
			}
			
		

			@Override
			public int getTailSize() {
				return Oscilloscope.TAILSIZE_MAX;
			}
		};
		final Oscilloscope scope = new Oscilloscope(10, dsp, shell, SWT.NONE);

		scope.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				scope.setProgression(0, ((Oscilloscope) e.widget).getSize().x);
				scope.setProgression(1, ((Oscilloscope) e.widget).getSize().x);
				scope.setProgression(2, ((Oscilloscope) e.widget).getSize().x);
				scope.setProgression(3, ((Oscilloscope) e.widget).getSize().x);
				scope.setProgression(4, ((Oscilloscope) e.widget).getSize().x);
				scope.setProgression(5, ((Oscilloscope) e.widget).getSize().x);
				scope.setProgression(6, ((Oscilloscope) e.widget).getSize().x);
				scope.setProgression(7, ((Oscilloscope) e.widget).getSize().x);
				scope.setProgression(8, ((Oscilloscope) e.widget).getSize().x);
				scope.setProgression(9, ((Oscilloscope) e.widget).getSize().x);
			}
		});

		scope.addStackListener(0, getStackAdapter());
		scope.setForeground(0,
				Display.getDefault().getSystemColor(SWT.COLOR_BLUE));

		scope.addStackListener(1, getStackAdapter());
		scope.setForeground(1,
				Display.getDefault().getSystemColor(SWT.COLOR_CYAN));

		scope.addStackListener(2, getStackAdapter());
		scope.setForeground(2,
				Display.getDefault().getSystemColor(SWT.COLOR_DARK_BLUE));

		scope.addStackListener(3, getStackAdapter());
		scope.setForeground(3,
				Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));

		scope.addStackListener(4, getStackAdapter());
		scope.setForeground(4,
				Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN));

		scope.addStackListener(5, getStackAdapter());
		scope.setForeground(5,
				Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED));

		scope.addStackListener(6, getStackAdapter());
		scope.setForeground(6,
				Display.getDefault().getSystemColor(SWT.COLOR_DARK_YELLOW));

		scope.addStackListener(7, getStackAdapter());
		scope.setForeground(7,
				Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED));

		scope.addStackListener(8, getStackAdapter());
		scope.setForeground(8,
				Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		scope.addStackListener(9, getStackAdapter());
		scope.setForeground(9,
				Display.getDefault().getSystemColor(SWT.COLOR_BLUE));

		scope.getDispatcher(0).dispatch();

	}

	private static OscilloscopeStackAdapter getStackAdapter() {

		return new OscilloscopeStackAdapter() {
			double value = Math.PI;
			double counter;
			boolean init = false;

			@Override
			public void stackEmpty(Oscilloscope scope, int channel) {

				if (!init) {
					init = true;
					counter = (double) (channel + 10) / 100;
					scope.setBaseOffset(channel, channel * 10);
				}

				int[] values = new int[scope.getProgression(channel)];

				for (int i = 0; i < values.length; i++) {

					value += counter;
					if (value > 2 * Math.PI) {
						value = 0;
					}

					values[i] = (int) (Math.sin(value) * 10);
				}

				scope.setValues(channel, values);

			}
		};
	}
}
