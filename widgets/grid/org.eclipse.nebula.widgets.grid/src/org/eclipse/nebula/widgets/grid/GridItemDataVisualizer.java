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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * This implementation of {@link DataVisualizer} is the default for Grid when no
 * custom implementation has been provided.
 * 
 * This class is used to provide general visualization values for various
 * aspects of the GridItem like background, font and text. Your own subclass of
 * this class could be used in combination with the JFace LabelProvider if you
 * use the GridViewer. This implementation is preferable in the case where there
 * are more rows than columns.
 * 
 * @author Mirko Paturzo <mirko.paturzo@exeura.eu>
 *
 */
public class GridItemDataVisualizer implements DataVisualizer {

	/**
	 * Create {@link GridItemDataVisualizer} with default value
	 * 
	 * @param defaultBackground
	 * @param defaultForeground
	 * @param defaultFont
	 */
	public GridItemDataVisualizer(Color defaultBackground, Color defaultForeground, Font defaultFont) {
		this.defaultBackground = defaultBackground;
		this.defaultForeground = defaultForeground;
		this.defaultFont = defaultFont;
	}

	private final Map<GridItem, List<Color>> backgrounds = new HashMap<GridItem, List<Color>>();
	private final Map<GridItem, List<Color>> foregrounds = new HashMap<GridItem, List<Color>>();
	private final Map<GridItem, List<Boolean>> checkables = new HashMap<GridItem, List<Boolean>>();
	private final Map<GridItem, List<Boolean>> checkeds = new HashMap<GridItem, List<Boolean>>();
	private final Map<GridItem, List<Boolean>> grayeds = new HashMap<GridItem, List<Boolean>>();
	private final Map<GridItem, List<Integer>> columnSpans = new HashMap<GridItem, List<Integer>>();
	private final Map<GridItem, List<Integer>> rowSpans = new HashMap<GridItem, List<Integer>>();
	private final Map<GridItem, List<Font>> fonts = new HashMap<GridItem, List<Font>>();
	private final Map<GridItem, List<Image>> images = new HashMap<GridItem, List<Image>>();
	private final Map<GridItem, List<String>> texts = new HashMap<GridItem, List<String>>();
	private final Map<GridItem, List<String>> toolTipTexts = new HashMap<GridItem, List<String>>();

	private Color defaultBackground;
	private Color defaultForeground;
	private Font defaultFont;

	@Override
	public Color getDefaultBackground() {
		return defaultBackground;
	}

	@Override
	public Color getDefaultForeground() {
		return defaultForeground;
	}

	@Override
	public Font getDefaultFont() {
		return defaultFont;
	}

	@Override
	public Color getBackground(GridItem gridItem, int column) {
		return getValueOrDefault(backgrounds, gridItem, column, defaultBackground);
	}

	/**
	 * get value or default
	 * 
	 * @param map
	 * @param gridItem
	 * @param column
	 * @param defaultValue
	 * @return T
	 */
	protected <T> T getValueOrDefault(Map<GridItem, List<T>> map, GridItem gridItem, int column, T defaultValue) {

		if (column < 0)
			return defaultValue;

		List<T> list = map.get(gridItem);
		if (list == null || column >= list.size())
			return defaultValue;

		T t = list.get(column);

		if (t == null)
			return defaultValue;

		return t;
	}

	@Override
	public boolean getChecked(GridItem gridItem, int column) {
		return getValueOrDefault(checkeds, gridItem, column, Boolean.FALSE);
	}

	@Override
	public int getColumnSpan(GridItem gridItem, int column) {
		return getValueOrDefault(columnSpans, gridItem, column, 0);
	}

	@Override
	public int getRowSpan(GridItem gridItem, int column) {
		return getValueOrDefault(rowSpans, gridItem, column, 0);
	}

	@Override
	public Font getFont(GridItem gridItem, int column) {
		return getValueOrDefault(fonts, gridItem, column, defaultFont);
	}

	@Override
	public Color getForeground(GridItem gridItem, int column) {
		return getValueOrDefault(foregrounds, gridItem, column, defaultForeground);
	}

	@Override
	public boolean getGrayed(GridItem gridItem, int column) {
		return getValueOrDefault(grayeds, gridItem, column, Boolean.FALSE);
	}

	@Override
	public Image getImage(GridItem gridItem, int column) {
		return getValueOrDefault(images, gridItem, column, null);
	}

	@Override
	public String getText(GridItem gridItem, int column) {
		return getValueOrDefault(texts, gridItem, column, "");
	}

	@Override
	public void setBackground(GridItem gridItem, int column, Color color) {
		put(backgrounds, gridItem, column, color);
	}

	/**
	 * put value on maps
	 * 
	 * @param map
	 * @param gridItem
	 * @param column
	 * @param value
	 */
	protected <T> void put(Map<GridItem, List<T>> map, GridItem gridItem, int column, T value) {
		List<T> list = map.get(gridItem);
		if (list == null) {
			list = new ArrayList<T>();
			map.put(gridItem, list);
		}
		while (column > list.size()) {
			list.add(null);
		}
		if (list.size() > column) {
			list.remove(column);
		}
		list.add(column, value);
	}

	@Override
	public void setChecked(GridItem gridItem, int column, boolean checked) {
		put(checkeds, gridItem, column, checked);
	}

	@Override
	public void setColumnSpan(GridItem gridItem, int column, int span) {
		put(columnSpans, gridItem, column, span);
	}

	@Override
	public void setRowSpan(GridItem gridItem, int column, int span) {
		put(rowSpans, gridItem, column, span);
	}

	@Override
	public void setFont(GridItem gridItem, int column, Font font) {
		put(fonts, gridItem, column, font);
	}

	@Override
	public void setForeground(GridItem gridItem, int column, Color foreground) {
		put(foregrounds, gridItem, column, foreground);
	}

	@Override
	public void setGrayed(GridItem gridItem, int column, boolean grayed) {
		put(grayeds, gridItem, column, grayed);
	}

	@Override
	public void setImage(GridItem gridItem, int column, Image image) {
		put(images, gridItem, column, image);
	}

	@Override
	public void setText(GridItem gridItem, int column, String text) {
		put(texts, gridItem, column, text);
	}

	@Override
	public boolean getCheckable(GridItem gridItem, int column) {
		return getValueOrDefault(checkables, gridItem, column, Boolean.TRUE);
	}

	@Override
	public void setCheckable(GridItem gridItem, int column, boolean checked) {
		put(this.checkables, gridItem, column, checked);
	}

	@Override
	public String getToolTipText(GridItem gridItem, int column) {
		return getValueOrDefault(toolTipTexts, gridItem, column, "");
	}

	@Override
	public void setToolTipText(GridItem gridItem, int column, String tooltip) {
		put(toolTipTexts, gridItem, column, tooltip);
	}

	/**
	 * In this implementation is too much expensive... Empty is Much better
	 * 
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#clearRow(GridItem)
	 */
	@Override
	public void clearRow(GridItem gridItem) {
		texts.remove(gridItem);
		toolTipTexts.remove(gridItem);
		foregrounds.remove(gridItem);
		backgrounds.remove(gridItem);
		images.remove(gridItem);
		fonts.remove(gridItem);
		rowSpans.remove(gridItem);
		columnSpans.remove(gridItem);
		grayeds.remove(gridItem);
		checkables.remove(gridItem);
		checkeds.remove(gridItem);
	}

	/**
	 * Remove in revertIndex
	 * 
	 * @param map
	 * @param column
	 */
	protected <T> void removeInvertIndex(Map<GridItem, List<T>> map, int column) {
		for (List<T> list : map.values()) {
			if (column >= 0 && column < list.size()) {
				list.remove(column);
			}
		}
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#clearColumn(int)
	 */
	@Override
	public void clearColumn(int column) {
		removeInvertIndex(texts, column);
		removeInvertIndex(toolTipTexts, column);
		removeInvertIndex(foregrounds, column);
		removeInvertIndex(backgrounds, column);
		removeInvertIndex(images, column);
		removeInvertIndex(fonts, column);
		removeInvertIndex(rowSpans, column);
		removeInvertIndex(columnSpans, column);
		removeInvertIndex(grayeds, column);
		removeInvertIndex(checkables, column);
		removeInvertIndex(checkeds, column);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#addColumn(int)
	 */
	@Override
	public void addColumn(int column) {
		addColumnToMap(texts, column);
		addColumnToMap(toolTipTexts, column);
		addColumnToMap(foregrounds, column);
		addColumnToMap(backgrounds, column);
		addColumnToMap(images, column);
		addColumnToMap(fonts, column);
		addColumnToMap(rowSpans, column);
		addColumnToMap(columnSpans, column);
		addColumnToMap(grayeds, column);
		addColumnToMap(checkables, column);
		addColumnToMap(checkeds, column);
	}

	private <T> void addColumnToMap(Map<GridItem, List<T>> map, int column) {
		Collection<List<T>> collections = map.values();

		if (collections != null) {
			for (List<T> list : collections) {
				if (!list.isEmpty()) {
					for (int i = list.size(); i < column; i++) {
						list.add(i, null);
					}
					list.add(column, null);
				}
			}
		}
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setDefaultBackground(org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setDefaultBackground(Color defaultBackground) {
		this.defaultBackground = defaultBackground;
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setDefaultForeground(org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setDefaultForeground(Color defaultForeground) {
		this.defaultForeground = defaultForeground;
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setDefaultFont(org.eclipse.swt.graphics.Font)
	 */
	@Override
	public void setDefaultFont(Font defaultFont) {
		this.defaultFont = defaultFont;
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#clearAll()
	 */
	@Override
	public void clearAll() {
		texts.clear();
		toolTipTexts.clear();
		foregrounds.clear();
		backgrounds.clear();
		images.clear();
		fonts.clear();
		rowSpans.clear();
		columnSpans.clear();
		grayeds.clear();
		checkables.clear();
		checkeds.clear();
	}

}
