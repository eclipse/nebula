/*****************************************************************************
 * Copyright (c) 2015 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *		Dirk Fauth <dirk.fauth@googlemail.com> - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.nebula.widgets.richtext.painter;

import java.util.Arrays;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

//TODO javadoc

public class ResourceHelper {

	private ResourceHelper() {
	}

	public static Color getColor(String rgbString) {
		if (!JFaceResources.getColorRegistry().hasValueFor(rgbString)) {
			// rgb string in format rgb(r, g, b)
			String rgbValues = rgbString.substring(rgbString.indexOf('(') + 1, rgbString.lastIndexOf(')'));
			String[] values = rgbValues.split(",");
			try {
				int red = Integer.valueOf(values[0].trim());
				int green = Integer.valueOf(values[1].trim());
				int blue = Integer.valueOf(values[2].trim());

				JFaceResources.getColorRegistry().put(rgbString, new RGB(red, green, blue));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return JFaceResources.getColorRegistry().get(rgbString);
	}

	public static Font getFont(FontData... fontDatas) {
		StringBuilder keyBuilder = new StringBuilder();
		for (FontData fontData : fontDatas) {
			keyBuilder.append(fontData.toString());
		}
		String key = keyBuilder.toString();

		if (!JFaceResources.getFontRegistry().hasValueFor(key)) {
			JFaceResources.getFontRegistry().put(key, fontDatas);
		}
		return JFaceResources.getFont(key);
	}

	public static Font getFont(Font currentFont, String name, Integer size) {
		FontData[] original = currentFont.getFontData();
		FontData[] fontData = Arrays.copyOf(original, original.length);
		for (FontData data : fontData) {
			if (name != null) {
				data.setName(name);
			}
			if (size != null) {
				data.setHeight(size);
			}
		}

		return getFont(fontData);
	}

	public static Font getBoldFont(Font currentFont) {
		FontData[] original = currentFont.getFontData();
		FontData[] fontData = Arrays.copyOf(original, original.length);
		for (FontData data : fontData) {
			data.setStyle(data.getStyle() | SWT.BOLD);
		}

		return getFont(fontData);
	}

	public static Font getItalicFont(Font currentFont) {
		FontData[] original = currentFont.getFontData();
		FontData[] fontData = Arrays.copyOf(original, original.length);
		for (FontData data : fontData) {
			data.setStyle(data.getStyle() | SWT.ITALIC);
		}

		return getFont(fontData);
	}

	public static String ltrim(String s) {
		int i = 0;
		while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
			i++;
		}
		return s.substring(i);
	}

	public static String rtrim(String s) {
		int i = s.length() - 1;
		while (i >= 0 && Character.isWhitespace(s.charAt(i))) {
			i--;
		}
		return s.substring(0, i + 1);
	}

}
