package org.eclipse.nebula.widgets.oscilloscope.example;

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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.nebula.examples.AbstractExampleTab;
import org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope;
import org.eclipse.nebula.widgets.oscilloscope.multichannel.OscilloscopeDispatcher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

/**
 * Demonstrates the Nebula Oscilloscope
 * 
 * @author Wim Jongman
 */
public class OscilloscopeExampleTab extends AbstractExampleTab {

	private static final String BUNDLE = "org.eclipse.nebula.widgets.oscilloscope.example";
	private Button serviceActive;
	private Spinner pulse;
	private Spinner delay;
	private Spinner lineWidth;
	private Button steady;
	private Spinner steadyPosition;
	private Oscilloscope oscilloscope;
	private Button scale;
	private Button mustFade;
	private Spinner fadeSpinner;
	private Button sound;
	private Button tailsizeMax;
	private Button connect;
	private Spinner tailSize;
	private Button tailsizeDefault;
	private Button tailsizeFill;
	private Combo imageCombo;
	private Combo activeSoundCombo;
	private Combo inactiveSoundCombo;
	private Button btnRandomSpikeEvery;
	private Button btnHeartbeatEveryPulse;
	private Button btnSine;
	private Button btnSquareWave;
	private Spinner progressionSpinner;
	private Button btnFollowProgression;
	private Button btnFollowProgression_1;
	private Spinner baseOffsetSpinner;

	@Override
	public String[] createLinks() {
		String[] links = new String[0];
		// links[0] =
		// "<a href=\"http://www.eclipse.org/nebula/widgets/gallery/gallery.php\">Gallery Home Page (includes Animation)</a>";
		return links;
	}

	@Override
	public Control createControl(Composite parent) {
		// Composite c = new Composite(parent, SWT.None);
		// c.setLayout(new RowLayout());

		oscilloscope = new Oscilloscope(parent, SWT.NONE);
		oscilloscope.setForeground(oscilloscope.getDisplay().getSystemColor(SWT.COLOR_RED));
		OscilloscopeDispatcher dispatcher = null;
		try {
			dispatcher = new OscilloscopeDispatcher() {

				private double value;
				private final double counter = .1;
				private final SoundClip clipper = new SoundClip();

				final String path = FileLocator.getBundleFile(Platform.getBundle(BUNDLE)).getPath();
				final String HEARTBEAT = path + "/Heartbeat.wav";
				final String FLATLINE = path + "/Flatline.wav";
				final String BEEP = path + "/Beep.wav";
				private Image image;
				private Image image1;

				@Override
				public void hookSetValues(int value) {

					if (btnSine.getSelection())
						setSineValue(value);
					if (btnSquareWave.getSelection())
						setSquareValue(value);
					if (btnRandomSpikeEvery.getSelection())
						setRandomPulse(value);
					if (btnHeartbeatEveryPulse.getSelection())
						setHeartBeat();

				}

				private void setHeartBeat() {
					if (sound.getSelection()) {
						clipper.playClip(getActiveSoundfile(), 0);
					}
					getOscilloscope().setValues(0, Oscilloscope.HEARTBEAT);
				}

				public void setRandomPulse(int v) {
					if (sound.getSelection()) {
						clipper.playClip(getActiveSoundfile(), 0);
					}
					getOscilloscope().setValue(0, 100 - new Random().nextInt(200));
				}

				public void setSineValue(int v) {
					for (int i = 0; i < getProgression(); i++) {

						value += counter;
						if (value > 2 * Math.PI) {
							value = 0;
						}

						int intValue = (int) (Math.sin(value) * 100);
						if (intValue == 99)
							if (sound.getSelection()) {
								clipper.playClip(getActiveSoundfile(), 0);
							}
						getOscilloscope().setValue(0, intValue);

						if (!btnFollowProgression.getSelection())
							return;
					}
				}

				public void setSquareValue(int v) {
					for (int i = 0; i < getProgression(); i++) {

						value += counter; // Math.PI /
											// getOscilloscope().getBounds().width;
						if (value > 2 * Math.PI) {
							value = 0;
						}

						int intValue = (int) ((Math.sin(value) + (Math.sin(3 * value) / 3) + (Math.sin(5 * value) / 5)
								+ (Math.sin(7 * value) / 7) + (Math.sin(9 * value) / 9) + (Math.sin(11 * value) / 11)
								+ (Math.sin(13 * value) / 13) + (Math.sin(15 * value) / 15) + (Math.sin(17 * value) / 17) + (Math
								.sin(19 * value) / 19)) * 100);
						// intValue += ;
						if (intValue >= 90)
							if (sound.getSelection()) {
								clipper.playClip(getActiveSoundfile(), 0);
							}
						getOscilloscope().setValue(0, intValue);

						if (!btnFollowProgression_1.getSelection())
							return;
					}
				}

				@Override
				public Oscilloscope getOscilloscope() {
					return oscilloscope;
				}

				@Override
				public int getLineWidth() {
					return lineWidth.getSelection();
				}

				@Override
				public int getProgression() {
					return progressionSpinner.getSelection();

				}

				@Override
				public int getBaseOffset() {
					return baseOffsetSpinner.getSelection();
				}

				@Override
				public void init() {
				}

				@Override
				public int getPulse() {
					return pulse.getSelection();
				}

				@Override
				public File getActiveSoundfile() {
					if (activeSoundCombo.getSelectionIndex() == -1)
						return null;
					else if (activeSoundCombo.getItem(activeSoundCombo.getSelectionIndex()).equals("Heartbeat")) {
						return new File(HEARTBEAT);
					} else if (activeSoundCombo.getItem(activeSoundCombo.getSelectionIndex()).equals("Beep"))
						return new File(BEEP);
					return new File(FLATLINE);
				}

				@Override
				public Color getActiveForegoundColor() {
					return getOscilloscope().getForeground();
				}

				@Override
				public int getDelayLoop() {
					return delay.getSelection();
				}

				@Override
				public File getInactiveSoundfile() {
					if (inactiveSoundCombo.getSelectionIndex() == -1)
						return null;
					else if (inactiveSoundCombo.getItem(inactiveSoundCombo.getSelectionIndex()).equals("Heartbeat"))
						return new File(HEARTBEAT);
					else if (inactiveSoundCombo.getItem(inactiveSoundCombo.getSelectionIndex()).equals("Beep"))
						return new File(BEEP);
					return new File(FLATLINE);
				}

				@Override
				public boolean isTailSizeMax() {
					return tailsizeMax.getSelection();
				}

				@Override
				public Image getBackgroundImage() {

					if (image == null) {
						byte[] bytes = new byte[BACKGROUND_MONITOR.length];
						for (int i = 0; i < BACKGROUND_MONITOR.length; i++)
							bytes[i] = (byte) BACKGROUND_MONITOR[i];
						image = new Image(null, new ByteArrayInputStream(bytes));
					}
					if (image1 == null) {
						byte[] bytes = new byte[BACKGROUND_MONITOR_SMALL.length];
						for (int i = 0; i < BACKGROUND_MONITOR_SMALL.length; i++)
							bytes[i] = (byte) BACKGROUND_MONITOR_SMALL[i];
						image1 = new Image(null, new ByteArrayInputStream(bytes));
					}

					if (imageCombo.getItem(imageCombo.getSelectionIndex()).equals("None"))
						return null;
					else if (imageCombo.getItem(imageCombo.getSelectionIndex()).equals("SMALL RASTER"))
						return image1;

					return image;
				}

				@Override
				public boolean isPercentage() {
					return scale.getSelection();
				}

				@Override
				public boolean isServiceActive() {
					return serviceActive.getSelection();
				}

				@Override
				public boolean isSoundRequired() {

					// active sounds are handled by the signal providers.

					if (!isServiceActive())
						return sound.getSelection();
					return false;
				}

				@Override
				public int getTailSize() {
					if (tailsizeMax.getSelection())
						return Oscilloscope.TAILSIZE_MAX;

					if (tailsizeFill.getSelection())
						return Oscilloscope.TAILSIZE_FILL;

					if (tailsizeDefault.getSelection())
						return Oscilloscope.TAILSIZE_DEFAULT;

					return tailSize.getSelection();
				}

				@Override
				public boolean isSteady() {
					return steady.getSelection();
				}

				@Override
				public int getSteadyPosition() {
					return steadyPosition.getSelection();
				}

				@Override
				public boolean getFade() {
					return mustFade.getSelection();

				}

				@Override
				public int getTailFade() {
					return fadeSpinner.getSelection();
				}

				@Override
				public boolean mustConnect() {
					return connect.getSelection();
				}

			};
		} catch (IOException e) {
			e.printStackTrace();
		}

		dispatcher.dispatch();

		return oscilloscope;
	}

	@Override
	public void createParameters(Composite parent) {
		parent.setLayout(new RowLayout());
		getSettings(parent);
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private Control getSettings(Composite parent) {
		ScrolledComposite group2 = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);

		group2.setExpandVertical(true);
		group2.setExpandHorizontal(true);
		// group2.setShowFocusedControl(true);
		// group2.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
		// group2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Composite Inner = new Composite(group2, SWT.None);
		// group2a.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		Inner.setLayout(new GridLayout(1, false));

		{
			Group grpSpeed = new Group(Inner, SWT.NONE);
			grpSpeed.setText("Speed");
			grpSpeed.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
			grpSpeed.setLayout(new GridLayout(4, false));

			Label lblServiceActive = new Label(grpSpeed, SWT.NONE);
			lblServiceActive.setText("Service Active");

			serviceActive = new Button(grpSpeed, SWT.CHECK);
			serviceActive.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (serviceActive.getSelection())
						oscilloscope.setForeground(oscilloscope.getDisplay().getSystemColor(SWT.COLOR_GREEN));
					else
						oscilloscope.setForeground(oscilloscope.getDisplay().getSystemColor(SWT.COLOR_RED));
				}
			});
			serviceActive.setSelection(false);
			new Label(grpSpeed, SWT.NONE);
			new Label(grpSpeed, SWT.NONE);

			Label lblDrawEvery = new Label(grpSpeed, SWT.NONE);
			lblDrawEvery.setText("Pulse");

			pulse = new Spinner(grpSpeed, SWT.BORDER);
			pulse.setMaximum(500);
			pulse.setMinimum(1);
			pulse.setIncrement(1);
			pulse.setSelection(60);
			pulse.setToolTipText("Pulse");

			Label lblTicks = new Label(grpSpeed, SWT.NONE);
			lblTicks.setText("ticks");
			new Label(grpSpeed, SWT.NONE);

			Label lblRedrawDealy = new Label(grpSpeed, SWT.NONE);
			lblRedrawDealy.setText("Redraw delay");

			delay = new Spinner(grpSpeed, SWT.BORDER);
			delay.setMaximum(500);
			delay.setMinimum(1);
			delay.setIncrement(1);
			delay.setSelection(20);
			delay.setToolTipText("redraw delay in ms");

			Label lblMs = new Label(grpSpeed, SWT.NONE);
			lblMs.setText("ms");
			new Label(grpSpeed, SWT.NONE);

			Label lblProgression = new Label(grpSpeed, SWT.NONE);
			lblProgression.setText("Progression");

			progressionSpinner = new Spinner(grpSpeed, SWT.BORDER);
			progressionSpinner.setMaximum(100);
			progressionSpinner.setMinimum(1);
			progressionSpinner.setSelection(1);

			Label lblSteps = new Label(grpSpeed, SWT.NONE);
			lblSteps.setText("steps");
			new Label(grpSpeed, SWT.NONE);
		}

		{
			Group grpSignal = new Group(Inner, SWT.NONE);
			grpSignal.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			grpSignal.setText("Signal");
			grpSignal.setLayout(new GridLayout(2, false));

			btnRandomSpikeEvery = new Button(grpSignal, SWT.RADIO);
			btnRandomSpikeEvery.setText("Random spike every pulse");
			new Label(grpSignal, SWT.NONE);

			btnHeartbeatEveryPulse = new Button(grpSignal, SWT.RADIO);
			btnHeartbeatEveryPulse.setSelection(false);
			btnHeartbeatEveryPulse.setText("Heartbeat every pulse");
			btnHeartbeatEveryPulse.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (btnHeartbeatEveryPulse.getSelection()) {
						pulse.setSelection(60);
					}
				}
			});
			new Label(grpSignal, SWT.NONE);

			btnSine = new Button(grpSignal, SWT.RADIO);
			btnSine.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (btnSine.getSelection()) {
						pulse.setSelection(1);
						scale.setSelection(true);
					}
				}
			});
			btnSine.setText("Round Wave (Sine)");

			btnFollowProgression = new Button(grpSignal, SWT.CHECK);
			btnFollowProgression.setText("follow progression");

			btnSquareWave = new Button(grpSignal, SWT.RADIO);
			btnSquareWave.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (btnSquareWave.getSelection()) {
						pulse.setSelection(1);
						scale.setSelection(true);
					}
				}
			});
			btnSquareWave.setText("Square Wave");

			btnFollowProgression_1 = new Button(grpSignal, SWT.CHECK);
			btnFollowProgression_1.setText("follow progression");
		}

		{
			Group grpGraph = new Group(Inner, SWT.NONE);
			grpGraph.setText("Graph");
			grpGraph.setLayout(new GridLayout(2, false));

			Label pixelSizeLabel = new Label(grpGraph, SWT.NONE);
			pixelSizeLabel.setText("Line width");

			lineWidth = new Spinner(grpGraph, SWT.BORDER);
			lineWidth.setMaximum(500);
			lineWidth.setMinimum(1);
			lineWidth.setSelection(1);
			lineWidth.setIncrement(1);

			Label lblSteady = new Label(grpGraph, SWT.NONE);
			lblSteady.setText("Steady");

			steady = new Button(grpGraph, SWT.CHECK);
			steady.setSelection(false);

			Label lblPosition = new Label(grpGraph, SWT.NONE);
			lblPosition.setText("Steady Position");

			steadyPosition = new Spinner(grpGraph, SWT.BORDER);
			steadyPosition.setMaximum(1000);
			steadyPosition.setMinimum(-1);
			steadyPosition.setIncrement(10);
			steadyPosition.setSelection(140);
			steadyPosition.setToolTipText("steady position");

			Label lblBaseOffsetIn = new Label(grpGraph, SWT.NONE);
			lblBaseOffsetIn.setText("Base Offset in %");

			baseOffsetSpinner = new Spinner(grpGraph, SWT.BORDER);
			baseOffsetSpinner.setTextLimit(3);
			baseOffsetSpinner.setSelection(50);
			baseOffsetSpinner.setPageIncrement(1);

			Label lblScale = new Label(grpGraph, SWT.NONE);
			lblScale.setText("Scale");

			scale = new Button(grpGraph, SWT.CHECK);
			scale.setToolTipText("If set to true then the values are treated as percentages of the\r\navailable space rather than absolute values. This will scale the\r\namplitudes if the control is resized.");
			scale.setSelection(false);

			Label lblBackgroundImage = new Label(grpGraph, SWT.NONE);
			lblBackgroundImage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lblBackgroundImage.setText("Background Image");

			imageCombo = new Combo(grpGraph, SWT.NONE);
			imageCombo.setItems(new String[] { "None", "RASTER", "SMALL RASTER" });
			imageCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			imageCombo.select(1);
		}

		{
			Group grpTail = new Group(Inner, SWT.NONE);
			grpTail.setText("Tail");
			grpTail.setLayout(new GridLayout(4, false));

			Group grpTailsize = new Group(grpTail, SWT.NONE);
			grpTailsize.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1));
			grpTailsize.setText("Tail size");
			grpTailsize.setLayout(new GridLayout(2, false));

			Label lblTailsizedefault = new Label(grpTailsize, SWT.NONE);
			lblTailsizedefault.setText("TAILSIZE_DEFAULT");

			tailsizeDefault = new Button(grpTailsize, SWT.RADIO);
			tailsizeDefault.setSelection(true);

			Label lblTailsizemax = new Label(grpTailsize, SWT.NONE);
			lblTailsizemax.setText("TAILSIZE_MAX");

			tailsizeMax = new Button(grpTailsize, SWT.RADIO);
			tailsizeMax.setSelection(false);

			Label lblTailsizeFill = new Label(grpTailsize, SWT.NONE);
			lblTailsizeFill.setText("TAILSIZE_FILL");

			tailsizeFill = new Button(grpTailsize, SWT.RADIO);

			Button label_1 = new Button(grpTailsize, SWT.RADIO);
			label_1.setText("Tailsize");

			tailSize = new Spinner(grpTailsize, SWT.BORDER);
			tailSize.setToolTipText("tail size");
			tailSize.setMaximum(1000);
			tailSize.setMinimum(1);
			tailSize.setSelection(200);
			tailSize.setIncrement(10);

			Label label_2 = new Label(grpTail, SWT.NONE);
			label_2.setSize(45, 15);
			label_2.setText("Connect");

			connect = new Button(grpTail, SWT.CHECK);
			connect.setSize(13, 16);
			connect.setToolTipText("connect head and tail \r\nif tailsize_max, steady and no fade");
			connect.setSelection(true);

			Label lblIfTailsizemaxAnd = new Label(grpTail, SWT.NONE);
			lblIfTailsizemaxAnd.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
			Label lblFade = new Label(grpTail, SWT.NONE);
			lblFade.setText("Fade");

			mustFade = new Button(grpTail, SWT.CHECK);

			Label lblPercentage = new Label(grpTail, SWT.NONE);
			lblPercentage.setText("Percentage");

			fadeSpinner = new Spinner(grpTail, SWT.BORDER);
			fadeSpinner.setMinimum(1);
			fadeSpinner.setSelection(25);

		}

		{
			Group grpSound = new Group(Inner, SWT.NONE);
			grpSound.setText("Sound");
			grpSound.setLayout(new GridLayout(2, false));

			Label lblSound_2 = new Label(grpSound, SWT.NONE);
			lblSound_2.setText("Sound");

			sound = new Button(grpSound, SWT.CHECK);
			sound.setAlignment(SWT.RIGHT);

			Label lblSound_1 = new Label(grpSound, SWT.NONE);
			lblSound_1.setBounds(0, 0, 55, 15);
			lblSound_1.setText("Active Sound");

			activeSoundCombo = new Combo(grpSound, SWT.NONE);
			activeSoundCombo.setItems(new String[] { "Heartbeat", "Beep" });
			activeSoundCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

			Label lblInactiveSound = new Label(grpSound, SWT.NONE);
			lblInactiveSound.setText("Inactive Sound");

			inactiveSoundCombo = new Combo(grpSound, SWT.NONE);
			inactiveSoundCombo.setItems(new String[] { "Heartbeat", "Beep", "Flatline" });
			inactiveSoundCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		}

		group2.setContent(Inner);
		group2.setMinSize(Inner.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		return group2;

	}
}
