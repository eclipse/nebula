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

import org.eclipse.nebula.widgets.opal.commons.AdvancedPath;
import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Instances of this object are items manipulated by the Segmented Bar widget. These items are highly customizable, you can set :
 * <ul>
 * <li>Background and foreground colors
 * <li>Font
 * <li>Image
 * <li>Text
 * <li>Tooltip
 * </ul>
 * You can also store data using the <code>setData<code> methods.
 * 
 */
public class Segment {
	static final int CORNER_RADIUS = 10;
	private String text;
	private String tooltip;
	private Double value;
	private Font font;
	private final Map<String, Object> data = new HashMap<String, Object>();
	private Object datum;
	private Color background, foreground;
	private SegmentedBar parent;
	Rectangle drawingArea;

	/**
	 * @return the tooltip of this item
	 */
	public String getTooltip() {
		return tooltip;
	}

	/**
	 * Set the tooltip of this item
	 * 
	 * @param tooltip the new value
	 * @return the current segment
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
	 * @return the current segment
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
	 * Set the background color of this item
	 * 
	 * @param background the new value
	 * @return the current segment
	 */
	public Segment setBackground(final Color background) {
		this.background = background;
		return this;
	}

	/**
	 * Set the font of this item
	 * 
	 * @param font new value
	 * @return the current segment
	 */
	public Segment setFont(final Font font) {
		this.font = font;
		return this;
	}

	/**
	 * Set the foreground color of this item
	 * 
	 * @param foreground the new value
	 * @return the current segment
	 */
	public Segment setForeground(final Color foreground) {
		this.foreground = foreground;
		return this;
	}

	/**
	 * Set the text of this item
	 * 
	 * @param text the new value
	 * @return the current segment
	 */
	public Segment setText(final String text) {
		this.text = text;
		return this;
	}

	/**
	 * Set the data stored in this item
	 * 
	 * @param data the new value
	 * @return the current segment
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
	 * @return the current segment
	 */
	public Segment setData(final String key, final Object value) {
		this.data.put(key, value);
		return this;
	}

	int computeHeight() {
		if (text == null) {
			return 30;
		}
		return SWTGraphicUtil.computeSize(text, font).y + 4 + 2 * CORNER_RADIUS;
	}

	void setParent(SegmentedBar parent) {
		this.parent = parent;
	}

	void draw(int segmentSize) {
		final GC gc = parent.gc;
		final Color previousForeground = gc.getForeground();
		final Color previousBackground = gc.getBackground();
		final Font previousFont = gc.getFont();
		
		
		final AdvancedPath path = new AdvancedPath(parent.getDisplay());
		int height = parent.getSize().y;
		drawingArea = new Rectangle(parent.currentX, 0, segmentSize, height);
		if (parent.isFirstItem) {
			path.addRoundRectangleStraightRight(parent.currentX, 0, segmentSize, height, CORNER_RADIUS, CORNER_RADIUS);
		} else if (parent.isLastItem) {
			path.addRoundRectangleStraightLeft(parent.currentX, 0, segmentSize - CORNER_RADIUS, height, CORNER_RADIUS, CORNER_RADIUS);
			drawingArea.width -= CORNER_RADIUS;
		} else {
			path.addRectangle(parent.currentX, 0, segmentSize, height);
		}

		if (background == null) {
			gc.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_GRAY));
		} else {
			gc.setBackground(background);
		}

		gc.setClipping(path);
		gc.fillRectangle(parent.currentX, 0, segmentSize, height);

		gc.setClipping((Rectangle) null);
		path.dispose();

		if (text != null && !text.trim().equals("")) {
			drawText(segmentSize);
		}
		
		gc.setBackground(previousBackground);
		gc.setForeground(previousForeground);
		gc.setFont(previousFont);
	}

	private void drawText(int segmentSize) {
		GC gc = parent.gc;
		if (font != null) {
			gc.setFont(font);
		}
		Point textSize = gc.textExtent(text, SWT.DRAW_TRANSPARENT);
		int height = parent.getSize().y;
		boolean tooLarge = (textSize.x > (segmentSize - 6));
		boolean tooHigh = (textSize.y > (height - 2));
		if (tooLarge || tooHigh) {
			return;
		}
		if (foreground != null) {
			gc.setForeground(foreground);
		}
		gc.drawText(text, parent.currentX + 3, (height - textSize.y) / 2, true);
	}

	public static Segment create() {
		return new Segment();
	}

}
