/*******************************************************************************
 * Copyright (c) 2014 Mirko Paturzo (Exeura srl).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mirko Paturzo - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * This class can be used to provide general visualization values for various aspects 
 * of the GridItem like background, font and text. Your own subclass of this class 
 * could be used in combination with the JFace LabelProvider if you use the GridViewer.
 * This dataVisualizer implementation replace the current paradigma of GridItem with 
 * memory less.
 * This implementation is preferable in the case where there are more rows than columns.
 * 
 * This datavisualizer kind is default for Grid. 
 * 
 * @author Mirko Paturzo <mirko.paturzo@exeura.eu>
 *
 */
public class ColumnRowBigDataVisualizer implements DataVisualizer {
	
	/**
	 * Create {@link ColumnRowBigDataVisualizer} with default value
	 * @param defaultBackground
	 * @param defaultForeground
	 * @param defaultFont
	 */
	public ColumnRowBigDataVisualizer(Color defaultBackground, Color defaultForeground, Font defaultFont) {
		this.defaultBackground = defaultBackground;
		this.defaultForeground = defaultForeground;
		this.defaultFont = defaultFont;
	}

	private final Map<Integer, List<Color>> backgrounds = new HashMap<Integer, List<Color>>();
	private final Map<Integer, List<Color>> foregrounds = new HashMap<Integer, List<Color>>();
	private final Map<Integer, List<Boolean>> checkables = new HashMap<Integer, List<Boolean>>();
	private final Map<Integer, List<Boolean>> checkeds = new HashMap<Integer, List<Boolean>>();
	private final Map<Integer, List<Boolean>> grayeds = new HashMap<Integer, List<Boolean>>();
	private final Map<Integer, List<Integer>> columnSpans = new HashMap<Integer, List<Integer>>();
	private final Map<Integer, List<Integer>> rowSpans = new HashMap<Integer, List<Integer>>();
	private final Map<Integer, List<Font>> fonts = new HashMap<Integer, List<Font>>();
	private final Map<Integer, List<Image>> images = new HashMap<Integer, List<Image>>();
	private final Map<Integer, List<String>> texts = new HashMap<Integer, List<String>>();
	private final Map<Integer, List<String>> toolTipTexts = new HashMap<Integer, List<String>>();
	
	private final Color defaultBackground;
	private final Color defaultForeground;
	private final Font defaultFont;

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getDefaultBackground()
	 */
	@Override
	public Color getDefaultBackground() {
		return defaultBackground;
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getDefaultForeground()
	 */
	@Override
	public Color getDefaultForeground() {
		return defaultForeground;
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getDefaultFont()
	 */
	@Override
	public Font getDefaultFont() {
		return defaultFont;
	}
	
	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getBackground(int, int)
	 */
	@Override
	public Color getBackground(int row, int column) {
		return getValueOrDefault(backgrounds, row, column, defaultBackground);
	}
	
	/**
	 * get value or default.. inverted index on {@link RowColumnBigDataVisualizer}
	 * @param map
	 * @param row
	 * @param column
	 * @param defaultValue
	 * @return T
	 */
	protected <T> T getValueOrDefault(Map<Integer, List<T>> map, int row, int column, T defaultValue) {
		List<T> list = map.get(column);
		if(list == null)
			return defaultValue;
		
		if(row >= list.size())
			return defaultValue;
		
		T t = list.get(row);
		
		if(t == null)
			return defaultValue;
		
		return t;
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getChecked(int, int)
	 */
	@Override
	public boolean getChecked(int row, int column) {
		return getValueOrDefault(checkeds, row, column, Boolean.FALSE);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getColumnSpan(int, int)
	 */
	@Override
	public int getColumnSpan(int row, int column) {
		return getValueOrDefault(columnSpans, row, column, 0);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getRowSpan(int, int)
	 */
	@Override
	public int getRowSpan(int row, int column) {
		return getValueOrDefault(rowSpans, row, column, 0);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getFont(int, int)
	 */
	@Override
	public Font getFont(int row, int column) {
		return getValueOrDefault(fonts, row, column, defaultFont);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getForeground(int, int)
	 */
	@Override
	public Color getForeground(int row, int column) {
		return getValueOrDefault(foregrounds, row, column, defaultForeground);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getGrayed(int, int)
	 */
	@Override
	public boolean getGrayed(int row, int column) {
		return getValueOrDefault(grayeds, row,column, Boolean.FALSE);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getImage(int, int)
	 */
	@Override
	public Image getImage(int row, int column) {
		return getValueOrDefault(images, row,column, null);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getText(int, int)
	 */
	@Override
	public String getText(int row, int column) {
		return getValueOrDefault(texts, row, column, "");
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setBackground(int, int, org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setBackground(int row, int column, Color color) {
		put(backgrounds, row, column, color);
	}

	/**
	 * put value on maps.. inverted index for {@link RowColumnBigDataVisualizer}
	 * @param map
	 * @param row
	 * @param column
	 * @param value
	 */
	protected <T> void put(Map<Integer, List<T>> map, int row, int column,
			T value) {
		List<T> list = map.get(column);
		if(list == null) {
			list = new ArrayList<T>();
			map.put(column, list);
		}
		while(row > list.size()) {
			list.add(null);
		}
		if(list.size() > row) {
			list.remove(row);
		}
		list.add(row, value);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setChecked(int, int, boolean)
	 */
	@Override
	public void setChecked(int row, int column, boolean checked) {
		put(checkeds, row, column, checked);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setColumnSpan(int, int, int)
	 */
	@Override
	public void setColumnSpan(int row, int column, int span) {
		put(columnSpans, row, column, span);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setRowSpan(int, int, int)
	 */
	@Override
	public void setRowSpan(int row, int column, int span) {
		put(rowSpans, row, column, span);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setFont(int, int, org.eclipse.swt.graphics.Font)
	 */
	@Override
	public void setFont(int row, int column, Font font) {
		put(fonts, row, column, font);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setForeground(int, int, org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setForeground(int row, int column, Color foreground) {
		put(foregrounds, row, column, foreground);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setGrayed(int, int, boolean)
	 */
	@Override
	public void setGrayed(int row, int column, boolean grayed) {
		put(grayeds, row, column, grayed);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setImage(int, int, org.eclipse.swt.graphics.Image)
	 */
	@Override
	public void setImage(int row, int column, Image image) {
		put(images, row, column, image);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setText(int, int, java.lang.String)
	 */
	@Override
	public void setText(int row, int column, String text) {
		put(texts, row, column, text);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getCheckable(int, int)
	 */
	@Override
	public boolean getCheckable(int row, int column) {
		return getValueOrDefault(checkables, row,column, Boolean.TRUE);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setCheckable(int, int, boolean)
	 */
	@Override
	public void setCheckable(int row, int column, boolean checked) {
		put(this.checkables, row, column, checked);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getToolTipText(int, int)
	 */
	@Override
	public String getToolTipText(int row, int column) {
		return getValueOrDefault(toolTipTexts, row,column, "");
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setToolTipText(int, int, java.lang.String)
	 */
	@Override
	public void setToolTipText(int row, int column, String tooltip) {
		put(texts, row, column, tooltip);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#removeIndex(int)
	 */
	@Override
	public void removeIndex(int index) {
		texts.remove(index);
		toolTipTexts.remove(index);
		foregrounds.remove(index);
		backgrounds.remove(index);
		images.remove(index);
		fonts.remove(index);
		rowSpans.remove(index);
		columnSpans.remove(index);
		grayeds.remove(index);
		checkables.remove(index);
		checkeds.remove(index);
	}

	/**
	 * In this implementation is too much expensive...
	 * Empty is Much better
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#clearRow(int)
	 */
	@Override
	public void clearRow(int row) {
		/*
		 * Is empty
		 */
	}
	
	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#clearColumn(int)
	 */
	@Override
	public void clearColumn(int column) {
		removeIndex(column);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#createAndGetChildenGridItemDataVisualizer()
	 */
	@Override
	public DataVisualizer createAndGetChildenGridItemDataVisualizer() {
		return new ColumnRowBigDataVisualizer(defaultBackground, defaultForeground, defaultFont);
	}

}
