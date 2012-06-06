/*******************************************************************************
 * Copyright (c) 2012 Hallvard Tr¾tteberg.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Hallvard Tr¾tteberg - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.geomap.jface;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;

/**
 * ImageDescriptor that downloads the image data from https://chart.googleapis.com/
 * Typically used by a LabelProvider with an ImageRegistry to provide map icons.
 * @see LabelProvider
 * @see ImageRegistry
 * @author hal
 *
 */
public class GoogleIconDescriptor extends ImageDescriptor {

	public static class Options {

		private String iconClass, iconName, style;
		private boolean hasShadow;
		private String text;
		private RGB fillColor, textColor;

		public Options(String iconClass, String iconName,
				String style, boolean hasShadow, String text, RGB fillColor,
				RGB textColor) {
			super();
			setOptions(iconClass, iconName, style, hasShadow, text, fillColor,
					textColor);
		}

		private void setOptions(String iconClass, String iconName,
				String style, boolean hasShadow, String text, RGB fillColor,
				RGB textColor) {
			this.iconClass = iconClass;
			this.iconName = iconName;
			this.style = style;
			this.hasShadow = hasShadow;
			this.text = text;
			this.fillColor = fillColor;
			this.textColor = textColor;
		}

		public Options(Options options) {
			setOptions(options.iconClass, options.iconName, options.style, options.hasShadow, options.text, options.fillColor, options.textColor);
		}
	}
	
	private Options options;

	public GoogleIconDescriptor(Options options) {
		this.options = options;
	}
	
	private static String baseUrl = "https://chart.googleapis.com/chart?";
	private static String argsSep = "|";
	
	@Override
	public String toString() {
		return "[GoogleIconDescriptor @ " + getUrlString() + "]";
	}
	
	public String getUrlString() {
		String chst = options.iconClass;
		if (options.hasShadow) {
			chst += "_withshadow";
		}
		Object[] args = {options.iconName, options.style, options.text, toHex(options.fillColor), toHex(options.textColor)};
		StringBuilder chld = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			if (args[i] != null) {
				if (chld.length() > 0) {
					chld.append(argsSep);
				}
				try {
					chld.append(URLEncoder.encode(args[i].toString(), "utf-8"));
				} catch (UnsupportedEncodingException e) {
				}
			}
		}
		return baseUrl + "chst=" + chst + "&chld=" + chld;
	}

	@Override
	public ImageData getImageData() {
		try {
			return getImageData(getUrlString());
		} catch (Exception e) {
			return null;
		}
	}
	
	private String toHex(RGB rgb) {
		return String.format("%02x%02x%02x", rgb.red, rgb.green, rgb.blue);
	}

	public ImageData getImageData(String urlString) {
		InputStream inputStream = null;
		try {
			inputStream = new URL(urlString).openStream();
			return new ImageData(inputStream);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	//
	
	public final static String icon_map_pin_letter = "d_map_pin_letter";
	public final static String icon_bubble_text_small = "d_bubble_text_small";
	public final static String icon_bubble_icon_text_small = "d_bubble_icon_text_small";
	
	public final static String icon_style_pin = "pin";
	public final static String icon_style_pin_star = "pin_star";
	public final static String icon_style_pin_sleft = "pin_sleft";
	public final static String icon_style_pin_sright = "pin_sright";
	
	public final static String frame_style_bb = "bb";
	public final static String frame_style_bbtl = "bbtl";
	public final static String frame_style_bbtr = "bbtr";
	public final static String frame_style_bbbr = "bbbr";
	public final static String frame_style_bbT = "bbT";

	public final static String frame_style_edge_bl = "edge_bl";
	public final static String frame_style_edge_bc = "edge_bc";
	public final static String frame_style_edge_br = "edge_br";
	public final static String frame_style_edge_tl = "edge_tl";
	public final static String frame_style_edge_tc = "edge_tc";
	public final static String frame_style_edge_tr = "edge_tr";
	public final static String frame_style_edge_lt = "edge_lt";
	public final static String frame_style_edge_lc = "edge_lc";
	public final static String frame_style_edge_lb = "edge_lb";
	public final static String frame_style_edge_rt = "edge_rt";
	public final static String frame_style_edge_rc = "edge_rc";
	public final static String frame_style_edge_rb = "edge_rb";
	
	public static GoogleIconDescriptor letterPin(char c, boolean hasShadow, RGB fillColor, RGB textColor)
	{ return new GoogleIconDescriptor(new Options(icon_map_pin_letter, null, null, hasShadow, String.valueOf(c), fillColor, textColor));}

	public static GoogleIconDescriptor textBubble(String s, boolean hasShadow, RGB fillColor, RGB textColor)
	{ return new GoogleIconDescriptor(new Options(icon_bubble_text_small, null, frame_style_bb, hasShadow, s, fillColor, textColor));}
}
