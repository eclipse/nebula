/*******************************************************************************
 * Copyright (c) 2010, 2012 Weltevree Beheer BV, Remain Software & Industrial-TSI
 * Copyright (c) 2020 Laurent CARON
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Laurent Caron <laurent dot caron at gmail dot com> - initial API and implementation
 * Wim S. Jongman - Widget snippets
 ******************************************************************************/
package org.eclipse.nebula.widgets.oscilloscope.example.e4.parts;

import java.security.SecureRandom;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope;
import org.eclipse.nebula.widgets.oscilloscope.multichannel.OscilloscopeStackAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class ThirdPart {

	private static final String CSS_ID = "org.eclipse.e4.ui.css.id";
	private Oscilloscope scope;

	@PostConstruct
	public void createComposite(final Composite parent) {
		parent.setLayout(new FillLayout());

		// Create a single channel scope
		scope = new Oscilloscope(2, parent, SWT.NONE);
		scope.setData(CSS_ID, "three");

		scope.addListener(SWT.Resize, e -> {
			scope.setProgression(0, ((Oscilloscope) e.widget).getSize().x);
			scope.setProgression(1, ((Oscilloscope) e.widget).getSize().x);
		});

		final OscilloscopeStackAdapter stackAdapter = getStackAdapter();
		scope.addStackListener(0, stackAdapter);
		scope.addStackListener(1, stackAdapter);

		scope.getDispatcher(0).dispatch();

	}

	private static OscilloscopeStackAdapter getStackAdapter() {

		return new OscilloscopeStackAdapter() {
			private int oldp;
			private int[] ints;

			@Override
			public void stackEmpty(final Oscilloscope scope, final int channel) {
				final Random random = new SecureRandom();

				if (channel == 0) {

					if (oldp != scope.getProgression(channel)) {
						oldp = scope.getProgression(channel);
						ints = new int[oldp];
						for (int i = 0; i < ints.length - 8; i++) {
							final int inti = 20 - random.nextInt(40);
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
							final int inti = 2 - random.nextInt(5);
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
					final int[] onts = new int[ints.length];
					for (int i = 0; i < ints.length; i++) {
						onts[i] = -1 * ints[i];
					}
					scope.setValues(channel, onts);
				}
			}
		};

	}

	@Focus
	public void setFocus() {
		scope.forceFocus();
	}

	@PreDestroy
	private void dispose() {
	}


}