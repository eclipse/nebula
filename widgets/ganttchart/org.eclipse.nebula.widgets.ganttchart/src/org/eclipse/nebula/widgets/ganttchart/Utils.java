/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/ 

package org.eclipse.nebula.widgets.ganttchart;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public class Utils {

	/**
	 * Takes a font and gives it a bold typeface.
	 * 
	 * @param font Font to modify
	 * @return Font with bold typeface 
	 */
	public static Font applyBoldFont(final Font font) {
		if (font == null) {
			return null;
		}

		final FontData[] fontDataArray = font.getFontData();
		if (fontDataArray == null) {
			return null;
		}
		for (int index = 0; index < fontDataArray.length; index++) {
		    final FontData fData = fontDataArray[index];
			fData.setStyle(SWT.BOLD);
		}

		return new Font(Display.getDefault(), fontDataArray);
	}

	/**
	 * Takes a font and gives it the typeface of the given style.
	 * 
	 * @param font Font to modify
	 * @param style the new style for the given font (e.g. SWT.BOLD|SWT.ITALIC)
	 * @param size New font size
	 * @return Font with the given typeface and size
	 */
	public static Font applyFontData(final Font font, int style, int size) {
		if (font == null) {
			return null;
		}

		final FontData[] fontDataArray = font.getFontData();
		if (fontDataArray == null) {
			return null;
		}
		for (int index = 0; index < fontDataArray.length; index++) {
		    final FontData fData = fontDataArray[index];
			fData.setStyle(style);
			fData.setHeight(size);
		}

		return new Font(Display.getDefault(), fontDataArray);
	}

	/**
	 * Applies a certain font size to a font.
	 * 
	 * @param font Font to modify
	 * @param size New font size
	 * @return Font with new font size
	 */
	public static Font applyFontSize(final Font font, final int size) {
		if (font == null) {
			return null;
		}

		final FontData[] fontDataArray = font.getFontData();
		if (fontDataArray == null) {
			return null;
		}
		for (int index = 0; index < fontDataArray.length; index++) {
		    final FontData fData = fontDataArray[index];
			fData.setHeight(size);
		}

		return new Font(Display.getDefault(), fontDataArray);
	}

	/**
	 * Centers a dialog (Shell) on the <b>primary</b> (active) display.
	 * 
	 * @param shell Shell to center on screen
	 * @see Shell
	 */
	public static void centerDialogOnScreen(final Shell shell) {
		// do it by monitor to support dual-head cards and still center things correctly onto the screen people are on.
	    final Monitor monitor = Display.getDefault().getPrimaryMonitor();
	    final Rectangle bounds = monitor.getBounds();

	    final int screen_x = bounds.width;
	    final int screen_y = bounds.height;

		shell.setLocation(screen_x / 2 - (shell.getBounds().width / 2), screen_y / 2 - (shell.getBounds().height / 2));
	}
}
