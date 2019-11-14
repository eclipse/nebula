/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    chris.gross@us.ibm.com - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.grid.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;

/**
 * Utility class to provide common operations on strings not supported by the
 * base java API.
 *
 * @author chris.gross@us.ibm.com
 * @author Mirko Paturzo <mirko.paturzo@exeura.eu>
 * @author gongguangyong@live.cn
 *
 *         Mirko modified the pivot calculation for improve short text provider
 *         performance. The pivot number is calculate starting from the size of
 *         the cell provided
 *
 * @since 2.0.0
 */
public class TextUtils {

	/**
	 * Shortens a supplied string so that it fits within the area specified by the
	 * width argument. Strings that have been shorted have an "..." attached to the
	 * end of the string. The width is computed using the
	 * {@link GC#getCharWidth(char)}.
	 *
	 * @param gc
	 *            GC used to perform calculation.
	 * @param text
	 *            text to modify.
	 * @param width
	 *            Pixels to display.
	 * @param style
	 *            truncation style. see {@link SWT#LEFT}, {@link SWT#CENTER},
	 *            {@link SWT#RIGHT}
	 * @return shortened string that fits in area specified.
	 */
	public static String getShortStr(GC gc, String text, int width, int style) {
		if (text == null || text.equals("")) {
			return text;
		}

		if (width >= gc.stringExtent(text).x) {
			return text;
		}

		switch (style) {
		case SWT.LEFT:
			return getShortStringTruncatedInTheBeginning(gc, text, width);
		case SWT.RIGHT:
			return getShortStringTruncatedInTheEnd(gc, text, width);
		case SWT.CENTER:
			return getShortStringTruncatedInTheMiddle(gc, text, width);
		default:
			return text;
		}

	}

	private static String getShortStringTruncatedInTheBeginning(GC gc, String text, int width) {
		char[] chars = text.toCharArray();
		int calcWidth = gc.stringExtent("...").x;
		int index = chars.length - 1;
		while (calcWidth < width && index >= 0) {
			int step = gc.getCharWidth(chars[index]);
			calcWidth += step;
			if (calcWidth >= width) {
				break;
			}
			index--;
		}
		if (index <= 0) {
			return text;
		}
		StringBuilder sb = new StringBuilder(chars.length - index + 4);
		sb.append("...").append(text.substring(index));
		return sb.toString();
	}

	private static String getShortStringTruncatedInTheEnd(GC gc, String text, int width) {
		char[] chars = text.toCharArray();
		int calcWidth = gc.stringExtent("...").x;
		int index = 0;
		int length = chars.length;
		while (calcWidth < width && index < length) {
			int step = gc.getCharWidth(chars[index]);
			calcWidth += step;
			if (calcWidth >= width) {
				break;
			}
			index++;
		}
		if (index == length - 1) {
			return text;
		}
		StringBuilder sb = new StringBuilder(index + 4);
		if (index > 4) {
			sb.append(text.substring(0, index - 4));
		} else {
			sb.append(text.substring(0, 1));
		}
		sb.append("...");
		return sb.toString();
	}

	private static String getShortStringTruncatedInTheMiddle(GC gc, String text, int width) {
		char[] chars = text.toCharArray();
		int length = chars.length;
		int left = 0;
		int right = length - 1;
		int calcWidth = gc.stringExtent("...").x;

		while (left < right) {
			int step = gc.getCharWidth(chars[left]);
			calcWidth += step;
			if (calcWidth >= width) {
				break;
			}
			left++;

			step = gc.getCharWidth(chars[right]);
			calcWidth += step;
			if (calcWidth >= width) {
				break;
			}
			right--;
		}
		if (left >= right) {
			return text;
		}
		StringBuilder builder = new StringBuilder(left + length - right + 4);
		if (left == 0 || right == length - 1) {
			builder.append(chars[0]).append("...").append(chars[length - 1]);
		} else {
			int leftLen = left == 1 ? left : left - 1;
			builder.append(chars, 0, leftLen).append("...").append(chars, right + 1, length - right - 1);
		}
		return builder.toString();
	}

	/**
	 * private constructor to prevent instantiation.
	 */
	private TextUtils() {
		// is empty
	}
}
