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

import java.util.Random;

import org.eclipse.nebula.widgets.oscilloscope.Oscilloscope;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


/**
 * @author jongw
 * 
 */
public class Snippet2 {

	final String HEARTBEAT = "74829__jobro__Heartbeat.wav";
	final String FLATLINE = "Beep EKG Flatline 1.WAV";
	final String BEEP = "25882__acclivity__Beep1000.wav";

	protected Shell shell;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Snippet2 window = new Snippet2();
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
		shell.setLayout(new GridLayout(3, false));

		int counter = 9;

		for (int i = 0; i < counter; i++) {

			int dice = new Random().nextInt(4) + 1;

			if (dice == 1) {
				new SnippetDispatcher() {
					public void setValue(int value) {
						getOSGilloscope().setValues(Oscilloscope.HEARTBEAT);
					}

					public String getActiveSoundfile() {
						return HEARTBEAT;
					}

				}.dispatch(shell);
			}

			if (dice == 2) {
				new SnippetDispatcher() {

					double value = 0;
					double counter = .1;

					@Override
					public void init() {
						pulse.setSelection(1);
					};

					@Override
					public void setValue(int v) {
						value += counter;
						if (value > 2 * Math.PI) {
							value = 0;
						}

						int intValue = (int) (Math.sin(value) * 100);
						if (intValue == 99)
							if (sound.getSelection()) {
								clipper.playClip(BEEP, 0);
							}
						getOSGilloscope().setValue(intValue);
					}

					@Override
					public boolean isSoundRequired() {
						if (sound.getSelection()) {
							int intValue = (int) (Math.sin(value) * 100);
							if (intValue == 99)
								return true;
						}
						return false;
					}

				}.dispatch(shell);
			}

			if (dice == 3) {
				new SnippetDispatcher() {

					double value = 0;
					double counter = .1;

					@Override
					public void init() {
						pulse.setSelection(1);
					};

					@Override
					public void setValue(int v) {
						value += counter;
						if (value > 2 * Math.PI) {
							value = 0;
						}

						int intValue = (int) ((Math.sin(value) + (Math
								.sin(3 * value) / 3)) * 100);
						// intValue += ;
						if (intValue == 99)
							if (sound.getSelection()) {
								clipper.playClip(BEEP, 0);
							}
						getOSGilloscope().setValue(intValue);
					}

					@Override
					public boolean isSoundRequired() {
						if (sound.getSelection()) {
							int intValue = (int) (Math.sin(value) * 100);
							if (intValue == 99)
								return true;
						}
						return false;
					}

				}.dispatch(shell);
			}

			if (dice == 4) {
				new SnippetDispatcher() {

					double value = 0;

					@Override
					public void init() {
						pulse.setSelection(1);
					};

					@Override
					public void setValue(int v) {
						value += Math.PI / gilloscope.getBounds().width;
						if (value > 2 * Math.PI) {
							value = 0;
						}

						int intValue = (int) ((Math.sin(value)
								+ (Math.sin(3 * value) / 3)
								+ (Math.sin(5 * value) / 5)
								+ (Math.sin(7 * value) / 7)
								+ (Math.sin(9 * value) / 9)
								+ (Math.sin(11 * value) / 11)
								+ (Math.sin(13 * value) / 13)
								+ (Math.sin(15 * value) / 15)
								+ (Math.sin(17 * value) / 17) + (Math
								.sin(19 * value) / 19)) * 100);
						// intValue += ;
						if (intValue == 99)
							if (sound.getSelection()) {
								clipper.playClip(BEEP, 0);
							}
						getOSGilloscope().setValue(intValue);
					}

					@Override
					public boolean isSoundRequired() {
						if (sound.getSelection()) {
							int intValue = (int) (Math.sin(value) * 100);
							if (intValue == 99)
								return true;
						}
						return false;
					}

				}.dispatch(shell);
			}

		}

	}

}
