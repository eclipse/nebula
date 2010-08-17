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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * This class keeps the oscilloscope animation running and is used to set
 * various attributes of the scope.
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

		public void playClip(File file, int loop) {

			if (file == null)
				return;

			try {

				if (clip == null || !file.getAbsolutePath().equals(oldFile)) {
					oldFile = file.getAbsolutePath();
					clip = AudioSystem.getClip();
					clip.open(AudioSystem.getAudioInputStream(file));
				}
				if (clip.isActive())
					return;
				// clip.stop(); << Alternative

				clip.setFramePosition(0);
				clip.loop(loop);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public abstract void setValue(int value);

	private PlayClip clipper = new PlayClip();

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

	/**
	 * Contains a small image that can serve as the background of the scope.
	 */
	final public static int[] BACKGROUND_MONITOR_SMALL = new int[] { 255, 216,
			255, 224, 0, 16, 74, 70, 73, 70, 0, 1, 1, 1, 0, 72, 0, 72, 0, 0,
			255, 254, 0, 20, 67, 114, 101, 97, 116, 101, 100, 32, 119, 105,
			116, 104, 32, 71, 73, 77, 80, 0, 255, 219, 0, 67, 0, 2, 1, 1, 2, 1,
			1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 5, 3, 3, 3, 3, 3, 6, 4, 4, 3, 5, 7,
			6, 7, 7, 7, 6, 7, 7, 8, 9, 11, 9, 8, 8, 10, 8, 7, 7, 10, 13, 10,
			10, 11, 12, 12, 12, 12, 7, 9, 14, 15, 13, 12, 14, 11, 12, 12, 12,
			255, 219, 0, 67, 1, 2, 2, 2, 3, 3, 3, 6, 3, 3, 6, 12, 8, 7, 8, 12,
			12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12,
			12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12,
			12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 255,
			192, 0, 17, 8, 0, 10, 0, 10, 3, 1, 34, 0, 2, 17, 1, 3, 17, 1, 255,
			196, 0, 31, 0, 0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0,
			1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 255, 196, 0, 181, 16, 0, 2, 1,
			3, 3, 2, 4, 3, 5, 5, 4, 4, 0, 0, 1, 125, 1, 2, 3, 0, 4, 17, 5, 18,
			33, 49, 65, 6, 19, 81, 97, 7, 34, 113, 20, 50, 129, 145, 161, 8,
			35, 66, 177, 193, 21, 82, 209, 240, 36, 51, 98, 114, 130, 9, 10,
			22, 23, 24, 25, 26, 37, 38, 39, 40, 41, 42, 52, 53, 54, 55, 56, 57,
			58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90,
			99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119,
			120, 121, 122, 131, 132, 133, 134, 135, 136, 137, 138, 146, 147,
			148, 149, 150, 151, 152, 153, 154, 162, 163, 164, 165, 166, 167,
			168, 169, 170, 178, 179, 180, 181, 182, 183, 184, 185, 186, 194,
			195, 196, 197, 198, 199, 200, 201, 202, 210, 211, 212, 213, 214,
			215, 216, 217, 218, 225, 226, 227, 228, 229, 230, 231, 232, 233,
			234, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 255, 196, 0,
			31, 1, 0, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 2, 3,
			4, 5, 6, 7, 8, 9, 10, 11, 255, 196, 0, 181, 17, 0, 2, 1, 2, 4, 4,
			3, 4, 7, 5, 4, 4, 0, 1, 2, 119, 0, 1, 2, 3, 17, 4, 5, 33, 49, 6,
			18, 65, 81, 7, 97, 113, 19, 34, 50, 129, 8, 20, 66, 145, 161, 177,
			193, 9, 35, 51, 82, 240, 21, 98, 114, 209, 10, 22, 36, 52, 225, 37,
			241, 23, 24, 25, 26, 38, 39, 40, 41, 42, 53, 54, 55, 56, 57, 58,
			67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99,
			100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120,
			121, 122, 130, 131, 132, 133, 134, 135, 136, 137, 138, 146, 147,
			148, 149, 150, 151, 152, 153, 154, 162, 163, 164, 165, 166, 167,
			168, 169, 170, 178, 179, 180, 181, 182, 183, 184, 185, 186, 194,
			195, 196, 197, 198, 199, 200, 201, 202, 210, 211, 212, 213, 214,
			215, 216, 217, 218, 226, 227, 228, 229, 230, 231, 232, 233, 234,
			242, 243, 244, 245, 246, 247, 248, 249, 250, 255, 218, 0, 12, 3, 1,
			0, 2, 17, 3, 17, 0, 63, 0, 248, 10, 210, 13, 31, 254, 17, 29, 65,
			22, 238, 252, 192, 110, 160, 44, 198, 221, 119, 3, 178, 92, 12,
			110, 233, 215, 244, 245, 227, 48, 89, 104, 35, 143, 183, 106, 60,
			127, 211, 170, 127, 241, 84, 182, 31, 242, 35, 106, 63, 245, 247,
			111, 255, 0, 160, 203, 88, 213, 143, 41, 162, 143, 153, 255, 217 };

	public static final int NO_PULSE = -1;

	private Image image;

	/**
	 * This class calls all methods to set various attributes of the scope. It
	 * will redraw the scope and then place a new request in the display thread.
	 * Please read each method's doc to know how to control the widget.
	 * 
	 * @wbp.parser.entryPoint
	 */
	public void dispatch() {

		init();

		Runnable runnable = new Runnable() {

			private int pulse;

			public void run() {

				getOscilloscope().setPercentage(isPercentage());
				getOscilloscope().setTailSize(
						isTailSizeMax() ? Oscilloscope.TAILSIZE_MAX
								: getTailSize());
				getOscilloscope().setSteady(isSteady(), getSteadyPosition());
				getOscilloscope().setFade(getFade());
				getOscilloscope().setTailFade(getTailFade());
				getOscilloscope().setConnect(mustConnect());
				getOscilloscope().setLineWidth(getLineWidth());
				getOscilloscope().setBackgroundImage(getBackgroundImage());
				getOscilloscope().redraw();

				pulse++;

				if (getPulse() != NO_PULSE)
					if (pulse >= getPulse()) {
						pulse = 0;
						if (isServiceActive()) {
							getOscilloscope().setForeground(
									getActiveForegoundColor());

							setValue(pulse);
							if (isSoundRequired())
								getClipper().playClip(getActiveSoundfile(), 0);
						} else {
							if (isSoundRequired())
								getClipper()
										.playClip(getInactiveSoundfile(), 0);
							getOscilloscope().setForeground(
									getInactiveForegoundColor());
						}
					}

				getOscilloscope().getDisplay().timerExec(getDelayLoop(), this);
			}
		};
		getOscilloscope().getDisplay().timerExec(getDelayLoop(), runnable);

	}

	public int getLineWidth() {
		return 1;
	}

	/**
	 * Is used to get the color of the foreground when the thing that the scope
	 * is measuring is still alive. The aliveness of the thing that is being
	 * measured is returned by the {@link #isServiceActive()} method. The result
	 * of this method will be used in the
	 * {@link Oscilloscope#setForeground(Color)} method.
	 * 
	 * @return the color
	 * 
	 * @see #getInactiveForegoundColor()
	 * @see Oscilloscope#setForeground(Color)
	 */
	public Color getActiveForegoundColor() {
		return getOscilloscope().getForeground();
	}

	/**
	 * Is used to get the color of the foreground when the thing that the scope
	 * is measuring is not active. The aliveness of the thing that is being
	 * measured is returned by the {@link #isServiceActive()} method. The result
	 * of this method will be used in the
	 * {@link Oscilloscope#setForeground(Color)} method.
	 * 
	 * @return the color
	 * 
	 * @see #getActiveForegoundColor()
	 * @see Oscilloscope#setForeground(Color)
	 */
	public Color getInactiveForegoundColor() {
		return getOscilloscope().getForeground();
	}

	/**
	 * 
	 * @return
	 */
	public Image getBackgroundImage() {

		if (image == null) {
			byte[] bytes = new byte[BACKGROUND_MONITOR.length];
			for (int i = 0; i < BACKGROUND_MONITOR.length; i++)
				bytes[i] = (byte) BACKGROUND_MONITOR[i];
			image = new Image(null, new ByteArrayInputStream(bytes));
		}
		return image;
	}

	/**
	 * Will be called only once.
	 */
	public void init() {
	}

	public int getPulse() {
		return 40;
	}

	public File getActiveSoundfile() {
		return null;
	}

	public int getDelayLoop() {
		return 80;
	}

	public abstract Oscilloscope getOscilloscope();

	public File getInactiveSoundfile() {
		return null;
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

	public PlayClip getClipper() {
		return clipper;
	}
}
