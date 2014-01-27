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
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

/**
 * This class can be used to provide general visualization values for various aspects 
 * of the GridItem like background, font and text. Your own subclass of this class 
 * could be used in combination with the JFace LabelProvider if you use the GridViewer.
 * This dataVisualizer implementation replace the current paradigma of GridItem with 
 * memory less.
 * This implementation is preferable in the case where there are more columns than rows.
 * 
 * @author Mirko Paturzo <mirko.paturzo@exeura.eu>
 *
 */
public class RowColumnBigDataVisualizer extends ColumnRowBigDataVisualizer{
	
	
	/**
	 * Create {@link RowColumnBigDataVisualizer} with default value
	 * @param defaultBackground
	 * @param defaultForeground
	 * @param defaultFont
	 */
	public RowColumnBigDataVisualizer (Color defaultBackground, Color defaultForeground, Font defaultFont) {
		super(defaultBackground, defaultForeground, defaultFont);
	}
	
	
	/**
	 * @see org.eclipse.nebula.widgets.grid.ColumnRowBigDataVisualizer#clearRow(int)
	 */
	@Override
	public void clearRow(int row) {
		removeIndex(row);
	}
	/**
	 * put value on maps.. inverted index from {@link ColumnRowBigDataVisualizer}
	 * @param map
	 * @param row
	 * @param column
	 * @param value
	 */
	@Override
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
		list.add(row, value);
	}
	/**
	 * get value or default.. inverted index on {@link ColumnRowBigDataVisualizer}
	 * @param map
	 * @param row
	 * @param column
	 * @param defaultValue
	 * @return T
	 */
	@Override
	protected <T> T getValueOrDefault(Map<Integer, List<T>> map, int row, int column, T defaultValue) {
		List<T> list = map.get(column);
		if(list == null)
			return defaultValue;
		
		T t = list.get(row);
		
		if(t == null)
			return defaultValue;
		
		return t;
	}
	
	/**
	 * @see org.eclipse.nebula.widgets.grid.ColumnRowBigDataVisualizer#createAndGetChildenGridItemDataVisualizer()
	 */
	@Override
	public DataVisualizer createAndGetChildenGridItemDataVisualizer() {
		return new RowColumnBigDataVisualizer(getDefaultBackground(), getDefaultForeground(), getDefaultFont());
	}
}
