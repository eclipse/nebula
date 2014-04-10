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
	 * @param gridItem
	 * @param index
	 * @return {@link Color}
	 */
	Color getBackground(GridItem gridItem, int index);

	/**
	 * Method substitute GridItem method
	 * @param gridItem
	 * @param i
	 * @return boolean
	 */
	boolean getChecked(GridItem gridItem, int i);

	/**
	 * Method substitute GridItem method
	 * @param gridItem
	 * @param index
	 * @return int
	 */
	int getColumnSpan(GridItem gridItem, int index);

	/**
	 * Method substitute GridItem method
	 * @param gridItem
	 * @param index
	 * @return int
	 */
	int getRowSpan(GridItem gridItem, int index);

	/**
	 * Method substitute GridItem method
	 * @param gridItem
	 * @param index
	 * @return {@link Font}
	 */
	Font getFont(GridItem gridItem, int index);

	/**
	 * Method substitute GridItem method
	 * @param gridItem
	 * @param index
	 * @return {@link Color}
	 */
	Color getForeground(GridItem gridItem, int index);

	/**
	 * Method substitute GridItem method
	 * @param gridItem
	 * @param index
	 * @return boolean
	 */
	boolean getGrayed(GridItem gridItem, int index);

	/**
	 * Method substitute GridItem method
	 * @param gridItem
	 * @param i
	 * @return {@link Image}
	 */
	Image getImage(GridItem gridItem, int i);

	/**
	 * Method substitute GridItem method
	 * @param gridItem
	 * @param i
	 * @return {@link String}
	 */
	String getText(GridItem gridItem, int i);

	/**
	 * Method substitute GridItem method
	 * @param gridItem
	 * @param index
	 * @param color
	 */
	void setBackground(GridItem gridItem, int index, Color color);

	/**
	 * Method substitute GridItem method
	 * @param gridItem
	 * @param column
	 * @param checked
	 */
	void setChecked(GridItem gridItem, int column, boolean checked);

	/**
	 * Method substitute GridItem method
	 * @param gridItem
	 * @param index
	 * @param span
	 */
	void setColumnSpan(GridItem gridItem, int index, int span);

	/**
	 * Method substitute GridItem method
	 * @param gridItem
	 * @param index
	 * @param span
	 */
	void setRowSpan(GridItem gridItem, int index, int span);

	/**
	 * Method substitute GridItem method
	 * @param gridItem
	 * @param index
	 * @param font
	 */
	void setFont(GridItem gridItem, int index, Font font);

	/**
	 * Method substitute GridItem method
	 * @param gridItem
	 * @param index
	 * @param foreground
	 */
	void setForeground(GridItem gridItem, int index, Color foreground);

	/**
	 * Method substitute GridItem method
	 * @param gridItem
	 * @param i
	 * @param grayed
	 */
	void setGrayed(GridItem gridItem, int i, boolean grayed);

	/**
	 * Method substitute GridItem method
	 * @param gridItem
	 * @param i
	 * @param image
	 */
	void setImage(GridItem gridItem, int i, Image image);

	/**
	 * Method substitute GridItem method
	 * @param gridItem
	 * @param i
	 * @param text
	 */
	void setText(GridItem gridItem, int i, String text);

	/**
	 * Insert a column on DataVisualizer
	 * @param column is the column index
	 */
	void addColumn(int column);

	/**
	 * Method substitute GridItem method
	 * @param gridItem
	 * @param index
	 * @return boolean
	 */
	boolean getCheckable(GridItem gridItem, int index);

	/**
	 * Method substitute GridItem method
	 * @param gridItem
	 * @param index
	 * @param checked
	 */
	void setCheckable(GridItem gridItem, int index, boolean checked);

	/**
	 * Method substitute GridItem method
	 * @param gridItem
	 * @param index
	 * @return String
	 */
	String getToolTipText(GridItem gridItem, int index);

	/**
	 * Method substitute GridItem method
	 * @param gridItem
	 * @param index
	 * @param tooltip
	 */
	void setToolTipText(GridItem gridItem, int index, String tooltip);

	/**
	 * Clear rows dataVisualizer values 
	 * @param gridItem 
	 */
	void clearRow(GridItem gridItem);

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
	 * set default background
	 */
	void setDefaultBackground(Color defaultBackground);

	/**
	 * set default foreground
	 */
	void setDefaultForeground(Color defaultForeground);

	/**
	 * set default font
	 */
	void setDefaultFont(Font defaultFont);
	
	/**
	 * Clear all data on {@link DataVisualizer}
	 */
	void clearAll();

}
