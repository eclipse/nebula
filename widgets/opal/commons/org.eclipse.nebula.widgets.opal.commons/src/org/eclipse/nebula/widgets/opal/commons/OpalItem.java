/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.commons;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * Instances of this object are items manipulated by the widgets of the Opal Project. These items are highly customizable, you can set :
 * <ul>
 * <li>Background and foreground colors,
 * <li>Font
 * <li>Image
 * <li>Text
 * <li>Height
 * </ul>
 * You can also store data using the <code>setData<code> methods.
 * 
 */
public abstract class OpalItem {

	private final Map<String, Object> data = new HashMap<String, Object>();
	private Object datum;
	private Color background;
	private Font font;
	private Color foreground;
	private Image image;
	private String text;
	private int height = -1;

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
	 * @return the height of the item
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * @return the image stored in this item
	 */
	public Image getImage() {
		return this.image;
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
	public void setBackground(final Color background) {
		this.background = background;
	}

	/**
	 * @param font set the font of this item
	 */
	public void setFont(final Font font) {
		this.font = font;
	}

	/**
	 * @param foreground set the foreground color of this item
	 */
	public void setForeground(final Color foreground) {
		this.foreground = foreground;
	}

	/**
	 * @param height set the height of this item
	 */
	public void setHeight(final int height) {
		this.height = height;
	}

	/**
	 * @param image set the image of this item
	 */
	public void setImage(final Image image) {
		this.image = image;
	}

	/**
	 * @param text set the text of this item
	 */
	public void setText(final String text) {
		this.text = text;
	}

	/**
	 * @param data set the data stored in this item
	 */
	public void setData(final Object data) {
		this.datum = data;
	}

	/**
	 * Store a data associated to a given key in this item
	 * 
	 * @param key key
	 * @param value value associated to this key
	 */
	public void setData(final String key, final Object value) {
		this.data.put(key, value);
	}

}
