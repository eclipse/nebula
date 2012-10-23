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
package org.eclipse.nebula.widgets.oscilloscope.snippets;

import java.io.File;

import org.eclipse.nebula.widgets.oscilloscope.Oscilloscope;
import org.eclipse.nebula.widgets.oscilloscope.OscilloscopeDispatcher;
import org.eclipse.nebula.widgets.oscilloscope.OscilloscopeStackAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Uses the stack listener. Every time the stack is empty, the stack listener is
 * activated to request more values.
 * 
 * @author Wim Jongman
 * 
 */
public class Snippet4 {

	final String HEARTBEAT = "Heartbeat.wav";
	final String FLATLINE = "Flatline.wav";
	final String BEEP = "Beep.wav";
	protected final File BEEPFILE = new File(BEEP);

	protected Shell shell;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Snippet4 window = new Snippet4();
			window.open();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
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
	protected void createContents() {

		shell = new Shell();
		shell.setImage(null);
		shell.setSize(600, 190);
		shell.setText("SWT Application");
		shell.setLayout(new GridLayout(1, false));

		final Oscilloscope scope = new Oscilloscope(shell, SWT.NONE);

		scope.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		new OscilloscopeDispatcher() {

			@Override
			public int getDelayLoop() {
				return 40;
			}

			public void init() {
				super.init();

				OscilloscopeStackAdapter listener = new OscilloscopeStackAdapter() {
					@Override
					public void stackEmpty(Oscilloscope scope) {

						if (isSoundRequired()) {
							getClipper().playClip(getActiveSoundfile(), 0);
						}
						

						// Calculate a full sine circle
						double value = 0;
						while (value < 2 * Math.PI) {

							value += .1;

							int intValue = (int) (Math.sin(value) * 100);
							getOscilloscope().setValue(intValue);
						}
					}
				};

				scope.addStackListener(listener);
			}

			@Override
			public boolean getFade() {
				return false;
			}
			

			public int getPulse() {
				return NO_PULSE;
			};

			public int getTailSize() {
				return Oscilloscope.TAILSIZE_DEFAULT;
			};

			@Override
			public void hookSetValues(int value) {
				scope.setValues(Oscilloscope.HEARTBEAT);
			}

			public File getActiveSoundfile() {
				return new File(HEARTBEAT); // add to classpath
			};

			@Override
			public boolean isSoundRequired() {
				return true;
			}

			@Override
			public Oscilloscope getOscilloscope() {
				return scope;
			}
		}.dispatch();

	}
}
