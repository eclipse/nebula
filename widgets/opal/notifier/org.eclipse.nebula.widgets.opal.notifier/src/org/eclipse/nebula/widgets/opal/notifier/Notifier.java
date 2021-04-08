/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API
 * and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.notifier;

import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.nebula.widgets.opal.notifier.NotifierColorsFactory.NotifierTheme;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * This class provides a notifier window, which is a window that appears in the
 * bottom of the screen and slides.
 */
public class Notifier {
	private static final int FONT_SIZE = 10;
	private static final int MAX_DURATION_FOR_OPENING = 500;
	private static final int DISPLAY_TIME = 4500;

	private static final int FADE_TIMER = 50;
	private static final int FADE_OUT_STEP = 8;

	private static final int STEP = 5;

	/**
	 * Starts a notification. A window will appear in the bottom of the screen, then
	 * will disappear after 4.5 s
	 *
	 * @param title the title of the popup window
	 * @param text  the text of the notification
	 *
	 */
	public static void notify(final String title, final String text) {
		notify(null, title, text, NotifierTheme.YELLOW_THEME);
	}

	/**
	 * Starts a notification. A window will appear in the bottom of the screen, then
	 * will disappear after 4.5 s
	 *
	 * @param image the image to display (if <code>null</code>, a default image is
	 *              displayed)
	 * @param title the title of the popup window
	 * @param text  the text of the notification
	 *
	 */
	public static void notify(final Image image, final String title, final String text) {
		notify(image, title, text, NotifierTheme.YELLOW_THEME);

	}

	/**
	 * Starts a notification. A window will appear in the bottom of the screen, then
	 * will disappear after 4.5 s
	 *
	 * @param title the title of the popup window
	 * @param text  the text of the notification
	 * @param theme the graphical theme. If <code>null</code>, the yellow theme is
	 *              used
	 *
	 * @see NotifierTheme
	 */
	public static void notify(final String title, final String text, final NotifierTheme theme) {
		notify(null, title, text, theme);
	}

	/**
	 * Starts a notification. A window will appear in the bottom of the screen, then
	 * will disappear after 4.5 s
	 *
	 * @param image the image to display (if <code>null</code>, a default image is
	 *              displayed)
	 * @param title the title of the popup window
	 * @param text  the text of the notification
	 * @param theme the graphical theme. If <code>null</code>, the yellow theme is
	 *              used
	 *
	 * @see NotifierTheme
	 */
	public static void notify(final Image image, final String title, final String text, final NotifierTheme theme) {
		final Shell shell = createNotificationWindow(image, title, text,
				NotifierColorsFactory.getColorsForTheme(theme));
		makeShellAppears(shell);
	}

	/**
	 * Creates a notification window
	 *
	 * @param image  image. If <code>null</code>, a default image is used
	 * @param title  title, the title of the window
	 * @param text   text of the window
	 * @param colors color set
	 * @return the notification window as a shell object
	 */
	protected static Shell createNotificationWindow(final Image image, final String title, final String text,
			final NotifierColors colors) {
		final Shell shell = new Shell(Display.getDefault().getActiveShell(), SWT.NO_TRIM | SWT.NO_FOCUS | SWT.ON_TOP);
		shell.setLayout(new GridLayout(2, false));
		shell.setBackgroundMode(SWT.INHERIT_FORCE);

		createTitle(shell, title, colors);
		createImage(shell, image);
		createText(shell, text, colors);
		createBackground(shell, colors);
		createCloseAction(shell);

		shell.addListener(SWT.Dispose, event -> {
			colors.dispose();
		});

		shell.pack();
		shell.setMinimumSize(320, 100);
		return shell;
	}

	/**
	 * Creates the title part of the window
	 *
	 * @param shell  the window
	 * @param title  the title
	 * @param colors the color set
	 */
	private static void createTitle(final Shell shell, final String title, final NotifierColors colors) {
		final Label titleLabel = new Label(shell, SWT.NONE);
		final GridData gdLabel = new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false, 2, 1);
		gdLabel.horizontalIndent = 40;
		titleLabel.setLayoutData(gdLabel);
		final Color titleColor = colors.titleColor;
		titleLabel.setForeground(titleColor);

		final Font titleFont = SWTGraphicUtil.buildFontFrom(titleLabel, SWT.BOLD, FONT_SIZE);
		titleLabel.setFont(titleFont);
		titleLabel.setText(title);
		SWTGraphicUtil.addDisposer(shell, titleFont);
	}

	/**
	 * Creates the image part of the window
	 *
	 * @param shell the window
	 * @param image the image
	 */
	private static void createImage(final Shell shell, final Image image) {
		final Label labelImage = new Label(shell, SWT.NONE);
		final GridData gdImage = new GridData(GridData.CENTER, GridData.BEGINNING, false, true);
		gdImage.horizontalIndent = 10;
		labelImage.setLayoutData(gdImage);
		if (image == null) {
			final Image temp = SWTGraphicUtil.createImageFromFile("images/information.png");
			labelImage.setImage(temp);
			SWTGraphicUtil.addDisposer(shell, temp);
		} else {
			labelImage.setImage(image);
		}

	}

	/**
	 * Creates the text part of the window
	 *
	 * @param shell  the window
	 * @param text   the text
	 * @param colors the color set
	 */
	private static void createText(final Shell shell, final String text, final NotifierColors colors) {
		final StyledText textLabel = new StyledText(shell, SWT.WRAP | SWT.READ_ONLY);
		final GridData gdText = new GridData(GridData.FILL, GridData.FILL, true, true);
		gdText.horizontalIndent = 15;
		textLabel.setLayoutData(gdText);
		textLabel.setEnabled(false);
		final Font textFont = SWTGraphicUtil.buildFontFrom(textLabel, SWT.NONE, 10);
		textLabel.setFont(textFont);

		final Color textColor = colors.textColor;
		textLabel.setForeground(textColor);
		final Color fillColor = colors.rightColor;
		textLabel.setBackground(fillColor);

		textLabel.setText(text);
		SWTGraphicUtil.applyHTMLFormating(textLabel);

		SWTGraphicUtil.addDisposer(shell, textFont);
	}

	/**
	 * Creates the background of the window
	 *
	 * @param shell  the window
	 * @param colors the color set of the window
	 */
	private static void createBackground(final Shell shell, final NotifierColors colors) {
		shell.addListener(SWT.Resize, event -> {
			final Rectangle rect = shell.getClientArea();
			final Image newImage = new Image(Display.getDefault(), Math.max(1, rect.width), rect.height);
			final GC gc = new GC(newImage);
			gc.setAntialias(SWT.ON);

			final Color borderColor = colors.borderColor;
			final Color fillColor1 = colors.leftColor;
			final Color fillColor2 = colors.rightColor;

			gc.setBackground(borderColor);
			gc.fillRoundRectangle(0, 0, rect.width, rect.height, 8, 8);

			gc.setBackground(fillColor1);
			gc.fillRoundRectangle(1, 1, rect.width - 2, rect.height - 2, 8, 8);

			gc.setBackground(fillColor2);
			gc.fillRoundRectangle(30, 1, rect.width - 32, rect.height - 2, 8, 8);
			gc.fillRectangle(30, 1, 10, rect.height - 2);

			final Image closeImage = SWTGraphicUtil.createImageFromFile("images/close.png");
			gc.drawImage(closeImage, rect.width - 21, 13);

			gc.dispose();
			closeImage.dispose();
			SWTGraphicUtil.addDisposer(shell, newImage);

			shell.setBackgroundImage(newImage);
		});

	}

	/**
	 * @param shell shell that will appear
	 */
	protected static void makeShellAppears(final Shell shell) {
		if (shell == null || shell.isDisposed()) {
			return;
		}

		final Rectangle clientArea = Display.getDefault().getPrimaryMonitor().getClientArea();
		final int startX = clientArea.x + clientArea.width - shell.getSize().x;

		final int stepForPosition = MAX_DURATION_FOR_OPENING / shell.getSize().y * STEP;
		final int stepForAlpha = STEP * 255 / shell.getSize().y;

		final int lastPosition = clientArea.y + clientArea.height - shell.getSize().y;

		shell.setAlpha(0);
		shell.setLocation(startX, clientArea.y + clientArea.height);
		shell.open();

		shell.getDisplay().timerExec(stepForPosition, new Runnable() {

			@Override
			public void run() {

				if (shell == null || shell.isDisposed()) {
					return;
				}

				shell.setLocation(startX, shell.getLocation().y - STEP);
				shell.setAlpha(shell.getAlpha() + stepForAlpha);
				if (shell.getLocation().y >= lastPosition) {
					shell.getDisplay().timerExec(stepForPosition, this);
				} else {
					shell.setAlpha(255);
					Display.getDefault().timerExec(DISPLAY_TIME, fadeOut(shell, false));
				}
			}
		});

	}

	/**
	 * @param shell shell that will disappear
	 * @param fast  if true, the fading is much faster
	 * @return a runnable
	 */
	private static Runnable fadeOut(final Shell shell, final boolean fast) {
		return new Runnable() {

			@Override
			public void run() {
				if (shell == null || shell.isDisposed()) {
					return;
				}

				int currentAlpha = shell.getAlpha();
				currentAlpha -= FADE_OUT_STEP * (fast ? 8 : 1);

				if (currentAlpha <= 0) {
					shell.setAlpha(0);
					shell.dispose();
					return;
				}

				shell.setAlpha(currentAlpha);

				Display.getDefault().timerExec(FADE_TIMER, this);

			}

		};
	}

	/**
	 * Add a listener to the shell in order to handle the clicks on the close button
	 *
	 * @param shell associated shell
	 */
	private static void createCloseAction(final Shell shell) {
		shell.addListener(SWT.MouseUp, event -> {
			final Rectangle rect = shell.getClientArea();
			final int xUpperLeftCorner = rect.width - 21;
			final int yUpperLeftCorner = 13;

			if (event.x >= xUpperLeftCorner && event.x <= xUpperLeftCorner + 8 && event.y >= yUpperLeftCorner
					&& event.y <= yUpperLeftCorner + 8) {
				Display.getDefault().timerExec(0, fadeOut(shell, true));
			}
		});
	}
}
