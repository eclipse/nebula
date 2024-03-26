/*******************************************************************************
 *  Copyright (c) 2010, 2012 Weltevree Beheer BV, Remain Software & Industrial-TSI
 *
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Wim S. Jongman - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.oscilloscope.snippets;

import java.security.SecureRandom;
import java.util.Random;

import org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope;
import org.eclipse.nebula.widgets.oscilloscope.multichannel.OscilloscopeDispatcher;
import org.eclipse.nebula.widgets.oscilloscope.multichannel.OscilloscopeStackAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This snippet demonstrates how to run the dispatcher in simple mode.
 *
 */
public class MultiScope_ScopeWithDataAndProgression2Channels_3 {

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
		final Oscilloscope scope = new Oscilloscope(4, shell, SWT.NONE);
		scope.addListener(SWT.Resize, e -> {
			scope.setProgression(0, ((Oscilloscope) e.widget).getSize().x);
			scope.setProgression(1, ((Oscilloscope) e.widget).getSize().x);
			scope.setProgression(2, ((Oscilloscope) e.widget).getSize().x);
			scope.setProgression(3, ((Oscilloscope) e.widget).getSize().x);
		});

		OscilloscopeStackAdapter stackAdapter = getStackAdapter();
		scope.addStackListener(0, stackAdapter);
		scope.addStackListener(1, stackAdapter);
		scope.addStackListener(2, stackAdapter);
		scope.addStackListener(3, stackAdapter);

		scope.setBaseOffset(0, 50);
		scope.setBaseOffset(1, 50);
		scope.setBaseOffset(2, -50);
		scope.setBaseOffset(3, -50);

		scope.setDispatcher(0, new OscilloscopeDispatcher(scope) {

			@Override
			public void hookBeforeDraw(Oscilloscope oscilloscope, int counter) {
			}

			@Override
			public void hookAfterDraw(Oscilloscope oscilloscope, int counter) {
			}

			@Override
			public void hookChangeAttributes() {
				getOscilloscope().setBackgroundImage(getBackgroundImage());

				for (int i = 0; i < getOscilloscope().getChannels(); i++) {

					getOscilloscope().setPercentage(i, isPercentage());
					getOscilloscope().setTailSize(
							i,
							isTailSizeMax() ? Oscilloscope.TAILSIZE_MAX
									: getTailSize());
					getOscilloscope().setSteady(i, isSteady(),
							getSteadyPosition());
					getOscilloscope().setTailFade(i, getTailFade());
					getOscilloscope().setConnect(i, mustConnect());
					getOscilloscope().setLineWidth(i, getLineWidth());
				}
			}
		});

		scope.getDispatcher(0).dispatch();

	}

	private static OscilloscopeStackAdapter getStackAdapter() {

		return new OscilloscopeStackAdapter() {
			private int oldp;
			private int[] ints;

			@Override
			public void stackEmpty(Oscilloscope scope, int channel) {

				Random random = new SecureRandom();

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
							int inti = 2 - random.nextInt(5);
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
					scope.setValues(channel + 2, ints);
				}

				else if (channel == 1) {
					int[] onts = new int[ints.length];
					for (int i = 0; i < ints.length; i++) {
						onts[i] = -1 * ints[i];
					}
					scope.setValues(channel, onts);
					scope.setValues(channel + 2, onts);
				}
			}
		};
	}
}
