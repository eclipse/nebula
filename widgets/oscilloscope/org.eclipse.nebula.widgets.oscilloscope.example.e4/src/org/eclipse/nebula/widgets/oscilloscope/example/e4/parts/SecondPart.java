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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope;
import org.eclipse.nebula.widgets.oscilloscope.multichannel.OscilloscopeDispatcher;
import org.eclipse.nebula.widgets.oscilloscope.multichannel.OscilloscopeStackAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class SecondPart {

	private static final String CSS_ID = "org.eclipse.e4.ui.css.id";
	private Oscilloscope scope;

	@PostConstruct
	public void createComposite(final Composite parent) {
		parent.setLayout(new FillLayout());

		// Create a single channel scope
		final OscilloscopeDispatcher dsp = new OscilloscopeDispatcher() {

			@Override
			public void hookBeforeDraw(final Oscilloscope oscilloscope, final int counter) {
			}

			@Override
			public void hookAfterDraw(final Oscilloscope oscilloscope, final int counter) {
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
		scope = new Oscilloscope(10, dsp, parent, SWT.NONE);
		scope.setData(CSS_ID, "two");

		scope.addListener(SWT.Resize, e -> {
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
		});

		scope.addStackListener(0, getStackAdapter());
		scope.setForeground(0, Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		scope.addStackListener(1, getStackAdapter());
		scope.setForeground(1, Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		scope.addStackListener(2, getStackAdapter());
		scope.setForeground(2, Display.getDefault().getSystemColor(SWT.COLOR_DARK_BLUE));

		scope.addStackListener(3, getStackAdapter());
		scope.setForeground(3, Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));

		scope.addStackListener(4, getStackAdapter());
		scope.setForeground(4, Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN));

		scope.addStackListener(5, getStackAdapter());
		scope.setForeground(5, Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED));

		scope.addStackListener(6, getStackAdapter());
		scope.setForeground(6, Display.getDefault().getSystemColor(SWT.COLOR_DARK_YELLOW));

		scope.addStackListener(7, getStackAdapter());
		scope.setForeground(7, Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED));

		scope.addStackListener(8, getStackAdapter());
		scope.setForeground(8, Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		scope.addStackListener(9, getStackAdapter());
		scope.setForeground(9, Display.getDefault().getSystemColor(SWT.COLOR_BLUE));

		scope.getDispatcher(0).dispatch();

	}

	private static OscilloscopeStackAdapter getStackAdapter() {

		return new OscilloscopeStackAdapter() {
			double value = Math.PI;
			double counter;
			boolean init = false;

			@Override
			public void stackEmpty(final Oscilloscope scope, final int channel) {

				if (!init) {
					init = true;
					counter = (double) (channel + 10) / 100;
					scope.setBaseOffset(channel, channel * 10);
				}

				final int[] values = new int[scope.getProgression(channel)];

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

	@Focus
	public void setFocus() {
		scope.forceFocus();
	}

	@PreDestroy
	private void dispose() {
	}

}