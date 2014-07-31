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
public class GridItemDataVisualizer implements DataVisualizer {

	/**
	 * Create {@link GridItemDataVisualizer} with default value
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
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getBackground(GridItem, int)
	 */
	@Override
	public Color getBackground(GridItem gridItem, int column) {
		return getValueOrDefault(backgrounds, gridItem, column, defaultBackground);
	}

	/**
	 * get value or default
	 * @param map
	 * @param gridItem
	 * @param column
	 * @param defaultValue
	 * @return T
	 */
	protected <T> T getValueOrDefault(Map<GridItem, List<T>> map, GridItem gridItem, int column, T defaultValue) {
		
		if(column < 0)
			return defaultValue;
		
		List<T> list = map.get(gridItem);
		if (list == null || column >= list.size())
			return defaultValue;

		T t = list.get(column);

		if (t == null)
			return defaultValue;

		return t;
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getChecked(GridItem, int)
	 */
	@Override
	public boolean getChecked(GridItem gridItem, int column) {
		return getValueOrDefault(checkeds, gridItem, column, Boolean.FALSE);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getColumnSpan(GridItem, int)
	 */
	@Override
	public int getColumnSpan(GridItem gridItem, int column) {
		return getValueOrDefault(columnSpans, gridItem, column, 0);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getRowSpan(GridItem, int)
	 */
	@Override
	public int getRowSpan(GridItem gridItem, int column) {
		return getValueOrDefault(rowSpans, gridItem, column, 0);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getFont(GridItem, int)
	 */
	@Override
	public Font getFont(GridItem gridItem, int column) {
		return getValueOrDefault(fonts, gridItem, column, defaultFont);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getForeground(GridItem, int)
	 */
	@Override
	public Color getForeground(GridItem gridItem, int column) {
		return getValueOrDefault(foregrounds, gridItem, column, defaultForeground);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getGrayed(GridItem, int)
	 */
	@Override
	public boolean getGrayed(GridItem gridItem, int column) {
		return getValueOrDefault(grayeds, gridItem, column, Boolean.FALSE);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getImage(GridItem, int)
	 */
	@Override
	public Image getImage(GridItem gridItem, int column) {
		return getValueOrDefault(images, gridItem, column, null);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getText(GridItem, int)
	 */
	@Override
	public String getText(GridItem gridItem, int column) {
		return getValueOrDefault(texts, gridItem, column, "");
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setBackground(GridItem, int, org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setBackground(GridItem gridItem, int column, Color color) {
		put(backgrounds, gridItem, column, color);
	}

	/**
	 * put value on maps
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

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setChecked(GridItem, int, boolean)
	 */
	@Override
	public void setChecked(GridItem gridItem, int column, boolean checked) {
		put(checkeds, gridItem, column, checked);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setColumnSpan(GridItem, int, int)
	 */
	@Override
	public void setColumnSpan(GridItem gridItem, int column, int span) {
		put(columnSpans, gridItem, column, span);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setRowSpan(GridItem, int, int)
	 */
	@Override
	public void setRowSpan(GridItem gridItem, int column, int span) {
		put(rowSpans, gridItem, column, span);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setFont(GridItem, int, org.eclipse.swt.graphics.Font)
	 */
	@Override
	public void setFont(GridItem gridItem, int column, Font font) {
		put(fonts, gridItem, column, font);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setForeground(GridItem, int, org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setForeground(GridItem gridItem, int column, Color foreground) {
		put(foregrounds, gridItem, column, foreground);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setGrayed(GridItem, int, boolean)
	 */
	@Override
	public void setGrayed(GridItem gridItem, int column, boolean grayed) {
		put(grayeds, gridItem, column, grayed);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setImage(GridItem, int, org.eclipse.swt.graphics.Image)
	 */
	@Override
	public void setImage(GridItem gridItem, int column, Image image) {
		put(images, gridItem, column, image);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setText(GridItem, int, java.lang.String)
	 */
	@Override
	public void setText(GridItem gridItem, int column, String text) {
		put(texts, gridItem, column, text);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getCheckable(GridItem, int)
	 */
	@Override
	public boolean getCheckable(GridItem gridItem, int column) {
		return getValueOrDefault(checkables, gridItem, column, Boolean.TRUE);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setCheckable(GridItem, int, boolean)
	 */
	@Override
	public void setCheckable(GridItem gridItem, int column, boolean checked) {
		put(this.checkables, gridItem, column, checked);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getToolTipText(GridItem, int)
	 */
	@Override
	public String getToolTipText(GridItem gridItem, int column) {
		return getValueOrDefault(toolTipTexts, gridItem, column, "");
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setToolTipText(GridItem, int, java.lang.String)
	 */
	@Override
	public void setToolTipText(GridItem gridItem, int column, String tooltip) {
		put(toolTipTexts, gridItem, column, tooltip);
	}

	/**
	 * In this implementation is too much expensive...
	 * Empty is Much better
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
					for(int i = list.size(); i < column; i++) {
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
	public void clearAll()
	{
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
