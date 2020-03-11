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

import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope;
import org.eclipse.nebula.widgets.oscilloscope.multichannel.OscilloscopeStackAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class FirstPart {

	private static final String CSS_ID = "org.eclipse.e4.ui.css.id";
	private Oscilloscope scope;

	@PostConstruct
	public void createComposite(final Composite parent) {
		parent.setLayout(new FillLayout());

		// Create a single channel scope
		scope = new Oscilloscope(parent, SWT.NONE);
		scope.setData(CSS_ID, "one");

		scope.addListener(SWT.Resize, e -> scope.setProgression(0, ((Oscilloscope) e.widget).getSize().x));

		scope.addStackListener(0, new OscilloscopeStackAdapter() {
			private int oldp;
			private int[] ints;

			@Override
			public void stackEmpty(final Oscilloscope scope, final int channel) {
				final Random random = new Random();
				if (oldp != scope.getProgression(0)) {
					oldp = scope.getProgression(0);
					ints = new int[oldp];
					for (int i = 0; i < ints.length; i++) {
						final int inti = 10 - random.nextInt(20);
						ints[i++] = inti;
					}
				} else {
					for (int i = 0; i < ints.length; i++) {
						final int inti = 2 - random.nextInt(5);
						ints[i] = ints[i++] + inti;
					}

				}

				scope.setValues(0, ints);
			}
		});

		scope.getDispatcher(0).dispatch();

	}

	@Focus
	public void setFocus() {
		scope.forceFocus();
	}

	@PreDestroy
	private void dispose() {
	}


}