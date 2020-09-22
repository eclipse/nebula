/*******************************************************************************
 * Copyright (c) 2020 Laurent CARON.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Inspired by the Segmented Bar Control of the ControlsFXProjet
 * (https://controlsfx.bitbucket.io/org/controlsfx/control/SegmentedBar.html)
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API and
 * implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.segmentedbar;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

/**
 * Instances of this object are items manipulated by the Segmented Bar widget. These items are highly customizable, you can set :
 * <ul>
 * <li>Background and foreground colors,
 * <li>Font
 * <li>Image
 * <li>Text
 * <li>Tooltip
 * </ul>
 * You can also store data using the <code>setData<code> methods.
 * 
 */
public class Segment {
	private String text;
	private String tooltip;
	private Double value;
	private Font font;
	private final Map<String, Object> data = new HashMap<String, Object>();
	private Object datum;
	private Color background, foreground;

	/**
	 * @return the tooltip of this item
	 */
	public String getTooltip() {
		return tooltip;
	}

	/**
	 * Set the tooltip of this item
	 * 
	 * @param tooltip new value
	 */
	public Segment setTooltip(String tooltip) {
		this.tooltip = tooltip;
		return this;
	}

	/**
	 * @return the value associated to this item
	 */
	public Double getValue() {
		return value;
	}

	/**
	 * Set the value associated to this item
	 * 
	 * @param value the new value
	 */
	public Segment setValue(Double value) {
		this.value = value;
		return this;
	}

	/**
	 * @return the background color of the item
	 */
	public Color getBackground() {
		return this.background;
	}

	/**
	 * @return the the data stored in this item
	 */
	public Object getData() {
		return this.datum;
	}

	/**
	 * @param key a key
	 * @return the the data stored in this item associated to this key
	 */
	public Object getData(final String key) {
		return this.data.get(key);
	}

	/**
	 * @return the font of the item
	 */
	public Font getFont() {
		return this.font;
	}

	/**
	 * @return the foreground color of the item
	 */
	public Color getForeground() {
		return this.foreground;
	}

	/**
	 * @return the text stored in this item
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * @param background set the background color of this item
	 */
	public Segment setBackground(final Color background) {
		this.background = background;
		return this;
	}

	/**
	 * @param font set the font of this item
	 */
	public Segment setFont(final Font font) {
		this.font = font;
		return this;
	}

	/**
	 * @param foreground set the foreground color of this item
	 */
	public Segment setForeground(final Color foreground) {
		this.foreground = foreground;
		return this;
	}

	/**
	 * @param text set the text of this item
	 */
	public Segment setText(final String text) {
		this.text = text;
		return this;
	}

	/**
	 * @param data set the data stored in this item
	 */
	public Segment setData(final Object data) {
		this.datum = data;
		return this;
	}

	/**
	 * Store a data associated to a given key in this item
	 * 
	 * @param key key
	 * @param value value associated to this key
	 * @return 
	 */
	public Segment setData(final String key, final Object value) {
		this.data.put(key, value);
		return this;
	}

	int computeHeight() {
		if (text == null) {
			return 0;
		}
		return SWTGraphicUtil.computeSize(text, font).y + 4;
	}


}
