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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Random;

import org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope;
import org.eclipse.nebula.widgets.oscilloscope.multichannel.OscilloscopeDispatcher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * Used to provide values to the Oscilloscope and keep it alive.
 * 
 */
public abstract class SnippetDispatcher extends OscilloscopeDispatcher {

	public abstract void hookSetValues(int value);

	SoundClip clipper = new SoundClip();
	protected Oscilloscope gilloscope;
	protected Button serviceActive;
	protected Spinner pulse;
	protected Spinner delay;
	protected Button tailSizeMax;
	protected Spinner tailsize;
	protected Button sound;
	protected Button steady;
	protected Spinner steadyPosition;
	protected Button scale;
	protected Button mustFade;
	protected Spinner fadeSpinner;
	protected Button connectButton;
	private Spinner lineWidth;

	/**
	 * @wbp.parser.entryPoint
	 */
	public void dispatch(Shell shell) {

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(1, false));

		TabFolder tabFolder = new TabFolder(composite, SWT.BOTTOM);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Group group = new Group(tabFolder, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		group.setLayout(new GridLayout(1, false));

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Scope");
		tabItem.setControl(group);

		gilloscope = new Oscilloscope(group, SWT.None);
		gilloscope.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));

		byte[] bytes = new byte[BACKGROUND_MONITOR.length];
		for (int i = 0; i < BACKGROUND_MONITOR.length; i++)
			bytes[i] = (byte) BACKGROUND_MONITOR[i];

		gilloscope.setBackgroundImage(new Image(shell.getDisplay(),
				new ByteArrayInputStream(bytes)));

		if (new Random().nextInt(2) == 1)
			gilloscope.setTailSize(0,new Random().nextInt(200) + 1);
		else
			gilloscope.setTailSize(0,-1);

		TabItem tabItem2 = new TabItem(tabFolder, SWT.NONE);
		tabItem2.setText("Settings");

		tabItem2.setControl(getSettings(tabFolder));

		init();

		Runnable runnable = new Runnable() {
			int pulse = 0;

			public void run() {

				getOscilloscope().setPercentage(0, isPercentage());
				getOscilloscope().setTailSize(0,
						isTailSizeMax() ? Oscilloscope.TAILSIZE_MAX
								: getTailSize());
				getOscilloscope().setSteady(0,isSteady(), getSteadyPosition());
				getOscilloscope().setFade(0,getFade());
				getOscilloscope().setTailFade(0,getTailFade());
				getOscilloscope().setConnect(0,mustConnect());
				getOscilloscope().setLineWidth(0,getLineWidth());

				getOscilloscope().redraw();
				pulse++;

				if (pulse == getPulse()) {
					pulse = 0;
					if (isServiceActive()) {
						getOscilloscope().setForeground(
								getOscilloscope().getDisplay().getSystemColor(
										SWT.COLOR_GREEN));
						hookSetValues(pulse);
						if (isSoundRequired())
							clipper.playClip(getActiveSoundfile(), 0);
					} else {
						if (isSoundRequired())
							clipper.playClip(getInactiveSoundfile(), 0);
						getOscilloscope().setForeground(
								getOscilloscope().getDisplay().getSystemColor(
										SWT.COLOR_RED));
					}
				}
				getOscilloscope().getDisplay().timerExec(getDelayLoop(), this);
			}
		};
		getOscilloscope().getDisplay().timerExec(getDelayLoop(), runnable);

	}

	private Control getSettings(Composite parent) {
		ScrolledComposite group2 = new ScrolledComposite(parent, SWT.H_SCROLL
				| SWT.V_SCROLL);

		group2.setExpandVertical(true);
		group2.setExpandHorizontal(true);

		;
		// group2.setShowFocusedControl(true);
		// group2.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
		// group2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Composite group2a = new Composite(group2, SWT.None);
		// group2a.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		group2a.setLayout(new GridLayout(4, false));

		Label lblServiceActive = new Label(group2a, SWT.NONE);
		lblServiceActive.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblServiceActive.setText("Service Active");

		serviceActive = new Button(group2a, SWT.CHECK);
		serviceActive.setSelection(true);
		new Label(group2a, SWT.NONE);
		new Label(group2a, SWT.NONE);

		Label lblDrawEvery = new Label(group2a, SWT.NONE);
		lblDrawEvery.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblDrawEvery.setText("Draw every");

		pulse = new Spinner(group2a, SWT.BORDER);
		pulse.setMaximum(500);
		pulse.setMinimum(1);
		pulse.setIncrement(1);
		pulse.setSelection(new Random().nextInt(70) + 30);
		pulse.setToolTipText("Pulse");

		Label lblTicks = new Label(group2a, SWT.NONE);
		lblTicks.setText("ticks");
		new Label(group2a, SWT.NONE);

		Label lblRedrawDealy = new Label(group2a, SWT.NONE);
		lblRedrawDealy.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblRedrawDealy.setText("Redraw delay");

		delay = new Spinner(group2a, SWT.BORDER);
		delay.setMaximum(500);
		delay.setMinimum(1);
		delay.setIncrement(1);
		delay.setSelection(new Random().nextInt(40) + 5);
		delay.setToolTipText("redraw delay in ms");

		Label lblMs = new Label(group2a, SWT.NONE);
		lblMs.setText("ms");
		new Label(group2a, SWT.NONE);

		Label pixelSizeLabel = new Label(group2a, SWT.NONE);
		pixelSizeLabel.setText("line width");

		lineWidth = new Spinner(group2a, SWT.BORDER);
		lineWidth.setToolTipText("redraw delay in ms");
		lineWidth.setMaximum(500);
		lineWidth.setMinimum(1);
		lineWidth.setSelection(1);
		lineWidth.setIncrement(1);

		Label lblPixel = new Label(group2a, SWT.NONE);
		lblPixel.setText("pixel");
		new Label(group2a, SWT.NONE);

		Label lblTail = new Label(group2a, SWT.NONE);
		lblTail.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblTail.setText("Max tailsize");

		tailSizeMax = new Button(group2a, SWT.CHECK);
		tailSizeMax.setSelection(new Random().nextBoolean());

		Label lblTailsize = new Label(group2a, SWT.NONE);
		lblTailsize.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblTailsize.setText("Tailsize");
		tailsize = new Spinner(group2a, SWT.BORDER);
		tailsize.setMaximum(1000);
		tailsize.setMinimum(1);
		tailsize.setIncrement(10);
		tailsize.setSelection(new Random().nextInt(70) + 100);
		tailsize.setToolTipText("tail size");

		Label lblSound = new Label(group2a, SWT.NONE);
		lblSound.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblSound.setText("Sound");

		sound = new Button(group2a, SWT.CHECK);
		new Label(group2a, SWT.NONE);
		new Label(group2a, SWT.NONE);

		Label lblSteady = new Label(group2a, SWT.NONE);
		lblSteady.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblSteady.setText("Steady");

		steady = new Button(group2a, SWT.CHECK);
		steady.setSelection(new Random().nextBoolean());

		Label lblPosition = new Label(group2a, SWT.NONE);
		lblPosition.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblPosition.setText("Position");

		steadyPosition = new Spinner(group2a, SWT.BORDER);
		steadyPosition.setMaximum(1000);
		steadyPosition.setMinimum(-1);
		steadyPosition.setIncrement(10);
		steadyPosition.setSelection(gilloscope.getBounds().width - 1);
		steadyPosition.setToolTipText("steady position");

		Label lblScale = new Label(group2a, SWT.NONE);
		lblScale.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblScale.setText("Scale");

		scale = new Button(group2a, SWT.CHECK);
		scale.setSelection(new Random().nextBoolean());
		new Label(group2a, SWT.NONE);
		new Label(group2a, SWT.NONE);

		Label lblFade = new Label(group2a, SWT.NONE);
		lblFade.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblFade.setText("Fade");

		mustFade = new Button(group2a, SWT.CHECK);

		Label lblPercentage = new Label(group2a, SWT.NONE);
		lblPercentage.setText("Percentage");

		fadeSpinner = new Spinner(group2a, SWT.BORDER);
		fadeSpinner.setMinimum(1);
		fadeSpinner.setSelection(25);

		Label lblConnect = new Label(group2a, SWT.NONE);
		lblConnect.setText("Connect");

		connectButton = new Button(group2a, SWT.CHECK);
		connectButton.setToolTipText("connect head and tail");
		connectButton.setSelection(new Random().nextBoolean());
		new Label(group2a, SWT.NONE);
		new Label(group2a, SWT.NONE);

		group2.setContent(group2a);
		group2.setMinSize(group2a.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		return group2;

	}

	public int getLineWidth() {
		return lineWidth.getSelection();
	}

	public void init() {
	}

	public int getPulse() {
		return pulse.getSelection();
	}

	public File getActiveSoundfile() {
		return null;
	}

	public int getDelayLoop() {
		return delay.getSelection();
	}

	public org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope getOscilloscope() {
		return gilloscope;
	}

	public File getInactiveSoundfile() {
		return null;
	}

	public boolean isTailSizeMax() {
		return tailSizeMax.getSelection();
	}

	public boolean isPercentage() {
		return scale.getSelection();
	}

	public boolean isServiceActive() {
		return serviceActive.getSelection();
	}

	public boolean isSoundRequired() {
		return sound.getSelection();
	}

	public int getTailSize() {
		return tailsize.getSelection();
	}

	public boolean isSteady() {
		return steady.getSelection();
	}

	public int getSteadyPosition() {
		return steadyPosition.getSelection();
	}

	public boolean getFade() {
		return mustFade.getSelection();

	}

	public int getTailFade() {
		return fadeSpinner.getSelection();
	}

	public boolean mustConnect() {
		return connectButton.getSelection();
	}

}
