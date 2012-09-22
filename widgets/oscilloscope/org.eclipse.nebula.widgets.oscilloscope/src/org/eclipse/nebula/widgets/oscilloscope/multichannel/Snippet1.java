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

import java.io.File;
import java.util.Random;

import org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author jongw
 * 
 */
public class Snippet1 {

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
			Snippet1 window = new Snippet1();
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
		shell.setSize(600, 800);
		shell.setText("SWT Application");
		shell.setLayout(new GridLayout(1, false));

		int counter = 2;

		for (int i = 0; i < counter; i++) {

			new SnippetDispatcher() {

				double value[];
				double counter = .1;

				@Override
				public void init() {

					value = new double[getOscilloscope().getChannels()];
					for (int j = 0; j < getOscilloscope().getChannels(); j++) {
						value[j] = j;
					}

					pulse.setSelection(1);
				};

				@Override
				public void hookSetValues(int v) {
					for (int j = 0; j < getOscilloscope().getChannels(); j++) {
						value[j] += counter;
						if (value[j] > 2 * Math.PI) {
							value[j] = 0;
						}

						int intValue = (int) (Math.sin(value[j]) * 100);
						if (intValue == 99)
							if (sound.getSelection()) {
								clipper.playClip(BEEPFILE, 0);
							}

						getOscilloscope().setValue(j, intValue);
					}
				}
				
				

			}.dispatch(shell);

		}
	}
}
