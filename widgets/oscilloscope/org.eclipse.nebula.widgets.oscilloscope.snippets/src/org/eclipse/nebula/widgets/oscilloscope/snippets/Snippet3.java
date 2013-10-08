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

import org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope;
import org.eclipse.nebula.widgets.oscilloscope.multichannel.OscilloscopeDispatcher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author jongw
 * 
 */
public class Snippet3 {

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
			Snippet3 window = new Snippet3();
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

			@Override
			public boolean getFade() {
				return false;
			}

			public int getPulse() {
				return 60;
			};

			public int getTailSize() {
				return Oscilloscope.TAILSIZE_DEFAULT;
			};

			@Override
			public void hookSetValues(int value) {
				scope.setValues(0,Oscilloscope.HEARTBEAT);
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
