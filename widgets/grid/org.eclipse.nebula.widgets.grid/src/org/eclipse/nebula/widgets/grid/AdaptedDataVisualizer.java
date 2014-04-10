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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * A basic implementation of the DataVisualizer interface. This class can be used
 * to provide general visualization values for various aspects of the GridItem like
 * background, font and text.
 * Scope of this implementation: reduce memory usage by avoid duplication of 
 * visualization data like string, fonts, and others.
 * In this example, DataVisualizer is customized on Object named MyModel: LabelProvider 
 * is not required. 
 * 
 * 
 * <pre>
 * class MyOwnDataVisualizer extends AdaptedDataVisualizer {
 *		FontRegistry registry = new FontRegistry();
 *		
 *		private final MyModel models[];
 *		
 *		public MyOwnDataVisualizer(MyModel models[]) {
 *			this.models = models;
 *		}
 *		
 *		@Override
 *		public Image getImage(GridItem gridItem, int columnIndex) {
 *			return null;
 *		}
 *
 *		@Override
 *		public String getText(GridItem gridItem, int columnIndex) {
 *			return "Column " + columnIndex + " => " + models[gridItem.getRowIndex()].toString();
 *		}
 *
 *		@Override
 *		public Font getFont(GridItem gridItem, int columnIndex) {
 *			if ((models[gridItem.getRowIndex()]).counter % 2 == 0) {
 *				return registry.getBold(Display.getCurrent().getSystemFont()
 *						.getFontData()[0].getName());
 *			}
 *			return null;
 *		}
 *	}
 * </pre>
 * 
 * @author Mirko Paturzo <mirko.paturzo@exeura.eu>
 *
 */
public class AdaptedDataVisualizer implements DataVisualizer {

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setBackground(GridItem, int, org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setBackground(GridItem gridItem, int index, Color color) {
		/**
		 * Is empty
		 */
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setChecked(GridItem, int, boolean)
	 */
	@Override
	public void setChecked(GridItem gridItem, int i, boolean checked) {
		/**
		 * Is empty
		 */
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setColumnSpan(GridItem, int, int)
	 */
	@Override
	public void setColumnSpan(GridItem gridItem, int index, int span) {
		/**
		 * Is empty
		 */
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setRowSpan(GridItem, int, int)
	 */
	@Override
	public void setRowSpan(GridItem gridItem, int index, int span) {
		/**
		 * Is empty
		 */
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setFont(GridItem, int, org.eclipse.swt.graphics.Font)
	 */
	@Override
	public void setFont(GridItem gridItem, int index, Font font) {
		/**
		 * Is empty
		 */
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setForeground(GridItem, int, org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setForeground(GridItem gridItem, int index, Color foreground) {
		/**
		 * Is empty
		 */
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setGrayed(GridItem, int, boolean)
	 */
	@Override
	public void setGrayed(GridItem gridItem, int i, boolean grayed) {
		/**
		 * Is empty
		 */
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setImage(GridItem, int, org.eclipse.swt.graphics.Image)
	 */
	@Override
	public void setImage(GridItem gridItem, int i, Image image) {
		/**
		 * Is empty
		 */
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setText(GridItem, int, java.lang.String)
	 */
	@Override
	public void setText(GridItem gridItem, int i, String text) {
		/**
		 * Is empty
		 */
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setCheckable(GridItem, int, boolean)
	 */
	@Override
	public void setCheckable(GridItem gridItem, int index, boolean checked) {
		/**
		 * Is empty
		 */
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setToolTipText(GridItem, int, java.lang.String)
	 */
	@Override
	public void setToolTipText(GridItem gridItem, int index, String tooltip) {
		/**
		 * Is empty
		 */
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getBackground(GridItem, int)
	 */
	@Override
	public Color getBackground(GridItem gridItem, int index) {
		return getDefaultBackground();
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getChecked(GridItem, int)
	 */
	@Override
	public boolean getChecked(GridItem gridItem, int i) {
		return false;
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getColumnSpan(GridItem, int)
	 */
	@Override
	public int getColumnSpan(GridItem gridItem, int index) {
		return 0;
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getRowSpan(GridItem, int)
	 */
	@Override
	public int getRowSpan(GridItem gridItem, int index) {
		return 0;
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getFont(GridItem, int)
	 */
	@Override
	public Font getFont(GridItem gridItem, int index) {
		return getDefaultFont();
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getForeground(GridItem, int)
	 */
	@Override
	public Color getForeground(GridItem gridItem, int index) {
		return getDefaultForeground();
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getGrayed(GridItem, int)
	 */
	@Override
	public boolean getGrayed(GridItem gridItem, int index) {
		return false;
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getImage(GridItem, int)
	 */
	@Override
	public Image getImage(GridItem gridItem, int i) {
		return null;
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getText(GridItem, int)
	 */
	@Override
	public String getText(GridItem gridItem, int i) {
		return null;
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getCheckable(GridItem, int)
	 */
	@Override
	public boolean getCheckable(GridItem gridItem, int index) {
		return false;
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getToolTipText(GridItem, int)
	 */
	@Override
	public String getToolTipText(GridItem gridItem, int index) {
		return null;
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#clearRow(GridItem)
	 */
	@Override
	public void clearRow(GridItem gridItem) {
		/**
		 * Is empty
		 */
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#clearColumn(int)
	 */
	@Override
	public void clearColumn(int column) {
		/**
		 * Is empty
		 */
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getDefaultBackground()
	 */
	@Override
	public Color getDefaultBackground() {
		return Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getDefaultForeground()
	 */
	@Override
	public Color getDefaultForeground() {
		return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getDefaultFont()
	 */
	@Override
	public Font getDefaultFont() {
		return null;
	}

	/** (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#addColumn(int)
	 */
	@Override
	public void addColumn(int column) {
		/**
		 * Is empty
		 */
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setDefaultBackground(org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setDefaultBackground(Color defaultBackground) {
		/**
		 * Is empty
		 */
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setDefaultForeground(org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setDefaultForeground(Color defaultForeground) {
		/**
		 * Is empty
		 */
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setDefaultFont(org.eclipse.swt.graphics.Font)
	 */
	@Override
	public void setDefaultFont(Font defaultFont) {
		/**
		 * Is empty
		 */
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#clearAll()
	 */
	@Override
	public void clearAll()
	{
		/**
		 * Is empty
		 */
	}

}
