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
package org.eclipse.nebula.widgets.oscilloscope;

import java.io.ByteArrayInputStream;
import java.io.File;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.eclipse.nebula.widgets.oscilloscope.Oscilloscope;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;


/**
 * Dispatch an osgilloscope.
 * 
 */
public abstract class OscilloscopeDispatcher {

	/**
	 * Play a sound clip.
	 * 
	 */
	public class PlayClip {
		Clip clip = null;
		String oldFile = "";

		public void playClip(String file, int loop) {
			try {

				if (clip == null || !file.equals(oldFile)) {
					oldFile = file;
					clip = AudioSystem.getClip();
					clip.open(AudioSystem.getAudioInputStream(new File(file)));
				}
				if (clip.isActive())
					return;
				// clip.stop(); << Alternative

				clip.setFramePosition(0);
				clip.loop(loop);

			} catch (Exception e) {
			}
		}
	}

	public abstract void setValue(int value);

	PlayClip clipper = new PlayClip();
	/**
	 * Contains a small image that can serve as the background of the scope.
	 */
	final public static int[] BACKGROUND_MONITOR = new int[] { 255, 216, 255,
			224, 0, 16, 74, 70, 73, 70, 0, 1, 1, 1, 0, 72, 0, 72, 0, 0, 255,
			254, 0, 19, 67, 114, 101, 97, 116, 101, 100, 32, 119, 105, 116,
			104, 32, 71, 73, 77, 80, 255, 219, 0, 67, 0, 5, 3, 4, 4, 4, 3, 5,
			4, 4, 4, 5, 5, 5, 6, 7, 12, 8, 7, 7, 7, 7, 15, 11, 11, 9, 12, 17,
			15, 18, 18, 17, 15, 17, 17, 19, 22, 28, 23, 19, 20, 26, 21, 17, 17,
			24, 33, 24, 26, 29, 29, 31, 31, 31, 19, 23, 34, 36, 34, 30, 36, 28,
			30, 31, 30, 255, 219, 0, 67, 1, 5, 5, 5, 7, 6, 7, 14, 8, 8, 14, 30,
			20, 17, 20, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
			30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
			30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
			30, 30, 255, 192, 0, 17, 8, 0, 20, 0, 20, 3, 1, 34, 0, 2, 17, 1, 3,
			17, 1, 255, 196, 0, 23, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 4, 5, 7, 255, 196, 0, 34, 16, 0, 2, 2, 0, 5, 5, 1, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 4, 2, 20, 33, 52, 84, 17, 115,
			145, 178, 209, 97, 255, 196, 0, 23, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 2, 0, 1, 3, 255, 196, 0, 27, 17, 0, 2, 2, 3,
			1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 17, 33, 49, 81, 18,
			255, 218, 0, 12, 3, 1, 0, 2, 17, 3, 17, 0, 63, 0, 225, 37, 87, 118,
			245, 59, 79, 217, 140, 212, 60, 24, 60, 226, 250, 83, 110, 196, 74,
			10, 205, 211, 133, 245, 141, 180, 155, 122, 106, 255, 0, 78, 77,
			187, 88, 4, 164, 237, 96, 204, 5, 89, 168, 120, 48, 121, 197, 244,
			10, 223, 5, 233, 240, 148, 170, 238, 222, 167, 105, 251, 48, 9,
			237, 20, 182, 137, 64, 6, 140, 255, 217 };
	private Image image;

	/**
	 * @wbp.parser.entryPoint
	 */
	public void dispatch() {

		init();

		Runnable runnable = new Runnable() {

			private int pulse;

			public void run() {

				getOSGilloscope().setPercentage(isPercentage());
				getOSGilloscope().setTailSize(
						isTailSizeMax() ? Oscilloscope.TAILSIZE_MAX
								: getTailSize());
				getOSGilloscope().setSteady(isSteady(), getSteadyPosition());
				getOSGilloscope().setFade(getFade());
				getOSGilloscope().setTailFade(getTailFade());
				getOSGilloscope().setConnect(mustConnect());
				getOSGilloscope().setBackgroundImage(getBackgroundImage());
				if (isServiceActive())
					getOSGilloscope().setForeground(
							getOSGilloscope().getDisplay().getSystemColor(
									SWT.COLOR_GREEN));
				else
					getOSGilloscope().setForeground(
							getOSGilloscope().getDisplay().getSystemColor(
									SWT.COLOR_RED));
				getOSGilloscope().redraw();
				
				pulse++;

				if (pulse == getPulse()) {
					pulse = 0;
					if (isServiceActive()) {
						getOSGilloscope().setForeground(
								getOSGilloscope().getDisplay().getSystemColor(
										SWT.COLOR_GREEN));
						setValue(pulse);
						if (isSoundRequired())
							clipper.playClip(getActiveSoundfile(), 0);
					} else {
						if (isSoundRequired())
							clipper.playClip(getInactiveSoundfile(), 0);
						getOSGilloscope().setForeground(
								getOSGilloscope().getDisplay().getSystemColor(
										SWT.COLOR_RED));
					}
				}

				getOSGilloscope().getDisplay().timerExec(getDelayloop(), this);
			}
		};
		getOSGilloscope().getDisplay().timerExec(getDelayloop(), runnable);

	}

	public Image getBackgroundImage() {

		if (image == null) {
			byte[] bytes = new byte[BACKGROUND_MONITOR.length];
			for (int i = 0; i < BACKGROUND_MONITOR.length; i++)
				bytes[i] = (byte) BACKGROUND_MONITOR[i];
			image = new Image(null, new ByteArrayInputStream(bytes));
		}
		return image;
	}

	public void init() {
	}

	public int getPulse() {
		return 40;
	}

	public String getActiveSoundfile() {
		return "";
	}

	public int getDelayloop() {
		return 80;
	}

	public abstract Oscilloscope getOSGilloscope();

	public String getInactiveSoundfile() {
		return "";
	}

	public boolean isTailSizeMax() {
		return false;
	}

	public boolean isPercentage() {
		return true;
	}

	public boolean isServiceActive() {
		return true;
	}

	public boolean isSoundRequired() {
		return false;
	}

	public int getTailSize() {
		return Oscilloscope.TAILSIZE_MAX;
	}

	public boolean isSteady() {
		return false;
	}

	public int getSteadyPosition() {
		return 200;
	}

	public boolean getFade() {
		return true;

	}

	public int getTailFade() {
		return Oscilloscope.DEFAULT_TAILFADE;
	}

	public boolean mustConnect() {
		return false;
	}

	protected void finalize() throws Throwable {
		if (image != null && !image.isDisposed())
			image.dispose();
	}
}
