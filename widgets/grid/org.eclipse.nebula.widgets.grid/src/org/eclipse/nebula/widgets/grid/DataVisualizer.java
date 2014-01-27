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

import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * Creation of a paradigm for the management of GridItem data. 
 * Via interface DataVisualizer it's possible create the own datavisualization
 * manager. Remove collections on {@link GridItem}, reduce memory leak risk 
 * and reduce memory usage.
 * 
 * @author Mirko Paturzo <mirko.paturzo@exeura.eu>
 *
 */
public interface DataVisualizer {

	/**
	 * Method substitute GridItem method
	 * @param row
	 * @param index
	 * @return {@link Color}
	 */
	Color getBackground(int row, int index);

	/**
	 * Method substitute GridItem method
	 * @param row
	 * @param i
	 * @return boolean
	 */
	boolean getChecked(int row, int i);

	/**
	 * Method substitute GridItem method
	 * @param row
	 * @param index
	 * @return int
	 */
	int getColumnSpan(int row, int index);

	/**
	 * Method substitute GridItem method
	 * @param row
	 * @param index
	 * @return int
	 */
	int getRowSpan(int row, int index);

	/**
	 * Method substitute GridItem method
	 * @param row
	 * @param index
	 * @return {@link Font}
	 */
	Font getFont(int row, int index);

	/**
	 * Method substitute GridItem method
	 * @param row
	 * @param index
	 * @return {@link Color}
	 */
	Color getForeground(int row, int index);

	/**
	 * Method substitute GridItem method
	 * @param row
	 * @param index
	 * @return boolean
	 */
	boolean getGrayed(int row, int index);

	/**
	 * Method substitute GridItem method
	 * @param row
	 * @param i
	 * @return {@link Image}
	 */
	Image getImage(int row, int i);

	/**
	 * Method substitute GridItem method
	 * @param row
	 * @param i
	 * @return {@link String}
	 */
	String getText(int row, int i);

	/**
	 * Method substitute GridItem method
	 * @param row
	 * @param index
	 * @param color
	 */
	void setBackground(int row, int index, Color color);

	/**
	 * Method substitute GridItem method
	 * @param row
	 * @param column
	 * @param checked
	 */
	void setChecked(int row, int column, boolean checked);

	/**
	 * Method substitute GridItem method
	 * @param row
	 * @param index
	 * @param span
	 */
	void setColumnSpan(int row, int index, int span);

	/**
	 * Method substitute GridItem method
	 * @param row
	 * @param index
	 * @param span
	 */
	void setRowSpan(int row, int index, int span);

	/**
	 * Method substitute GridItem method
	 * @param row
	 * @param index
	 * @param font
	 */
	void setFont(int row, int index, Font font);

	/**
	 * Method substitute GridItem method
	 * @param row
	 * @param index
	 * @param foreground
	 */
	void setForeground(int row, int index, Color foreground);

	/**
	 * Method substitute GridItem method
	 * @param row
	 * @param i
	 * @param grayed
	 */
	void setGrayed(int row, int i, boolean grayed);

	/**
	 * Method substitute GridItem method
	 * @param row
	 * @param i
	 * @param image
	 */
	void setImage(int row, int i, Image image);

	/**
	 * Method substitute GridItem method
	 * @param row
	 * @param i
	 * @param text
	 */
	void setText(int row, int i, String text);

	/**
	 * Method substitute GridItem method
	 * @param row
	 * @param index
	 * @return boolean
	 */
	boolean getCheckable(int row, int index);

	/**
	 * Method substitute GridItem method
	 * @param row
	 * @param index
	 * @param checked
	 */
	void setCheckable(int row, int index, boolean checked);

	/**
	 * Method substitute GridItem method
	 * @param row
	 * @param index
	 * @return String
	 */
	String getToolTipText(int row, int index);

	/**
	 * Method substitute GridItem method
	 * @param row
	 * @param index
	 * @param tooltip
	 */
	void setToolTipText(int row, int index, String tooltip);

	/**
	 * remove {@link Map} index (key)
	 * for {@link ColumnRowBigDataVisualizer} index is column
	 * for {@link RowColumnBigDataVisualizer} index is row
	 * @param index
	 */
	void removeIndex(int index);

	/**
	 * Clear rows dataVisualizer values 
	 * @param row 
	 */
	void clearRow(int row);

	/**
	 * Clear columns dataVisualizer values
	 * @param column 
	 */
	void clearColumn(int column);

	/**
	 * @return default background
	 */
	Color getDefaultBackground();

	/**
	 * @return default foreground
	 */
	Color getDefaultForeground();

	/**
	 * @return default font
	 */
	Font getDefaultFont();
	
	/**
	 * @return GridItem children dataVisualizer.
	 */
	DataVisualizer createAndGetChildenGridItemDataVisualizer();

}
