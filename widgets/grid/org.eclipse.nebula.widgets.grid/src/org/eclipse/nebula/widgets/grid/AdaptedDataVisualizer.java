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
 *		public Image getImage(int row, int columnIndex) {
 *			return null;
 *		}
 *
 *		@Override
 *		public String getText(int row, int columnIndex) {
 *			return "Column " + columnIndex + " => " + models[row].toString();
 *		}
 *
 *		@Override
 *		public Font getFont(int row, int columnIndex) {
 *			if ((models[row]).counter % 2 == 0) {
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
public class AdaptedDataVisualizer implements DataVisualizer{
	

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setBackground(int, int, org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setBackground(int row, int index, Color color) {
		/**
		 * Is empty
		 */
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setChecked(int, int, boolean)
	 */
	@Override
	public void setChecked(int row, int i, boolean checked) {
		/**
		 * Is empty
		 */
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setColumnSpan(int, int, int)
	 */
	@Override
	public void setColumnSpan(int row, int index, int span) {
		/**
		 * Is empty
		 */
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setRowSpan(int, int, int)
	 */
	@Override
	public void setRowSpan(int row, int index, int span) {
		/**
		 * Is empty
		 */
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setFont(int, int, org.eclipse.swt.graphics.Font)
	 */
	@Override
	public void setFont(int row, int index, Font font) {
		/**
		 * Is empty
		 */
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setForeground(int, int, org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setForeground(int row, int index, Color foreground) {
		/**
		 * Is empty
		 */
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setGrayed(int, int, boolean)
	 */
	@Override
	public void setGrayed(int row, int i, boolean grayed) {
		/**
		 * Is empty
		 */
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setImage(int, int, org.eclipse.swt.graphics.Image)
	 */
	@Override
	public void setImage(int row, int i, Image image) {
		/**
		 * Is empty
		 */
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setText(int, int, java.lang.String)
	 */
	@Override
	public void setText(int row, int i, String text) {
		/**
		 * Is empty
		 */
	}


	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setCheckable(int, int, boolean)
	 */
	@Override
	public void setCheckable(int row, int index, boolean checked) {
		/**
		 * Is empty
		 */
	}


	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#setToolTipText(int, int, java.lang.String)
	 */
	@Override
	public void setToolTipText(int row, int index, String tooltip) {
		/**
		 * Is empty
		 */
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#removeIndex(int)
	 */
	@Override
	public void removeIndex(int row) {
		/**
		 * Is empty
		 */
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getBackground(int, int)
	 */
	@Override
	public Color getBackground(int row, int index) {
		return getDefaultBackground();
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getChecked(int, int)
	 */
	@Override
	public boolean getChecked(int row, int i) {
		return false;
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getColumnSpan(int, int)
	 */
	@Override
	public int getColumnSpan(int row, int index) {
		return 0;
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getRowSpan(int, int)
	 */
	@Override
	public int getRowSpan(int row, int index) {
		return 0;
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getFont(int, int)
	 */
	@Override
	public Font getFont(int row, int index) {
		return getDefaultFont();
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getForeground(int, int)
	 */
	@Override
	public Color getForeground(int row, int index) {
		return getDefaultForeground();
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getGrayed(int, int)
	 */
	@Override
	public boolean getGrayed(int row, int index) {
		return false;
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getImage(int, int)
	 */
	@Override
	public Image getImage(int row, int i) {
		return null;
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getText(int, int)
	 */
	@Override
	public String getText(int row, int i) {
		return null;
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getCheckable(int, int)
	 */
	@Override
	public boolean getCheckable(int row, int index) {
		return false;
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#getToolTipText(int, int)
	 */
	@Override
	public String getToolTipText(int row, int index) {
		return null;
	}

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#clearRow(int)
	 */
	@Override
	public void clearRow(int row) {
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

	/**
	 * @see org.eclipse.nebula.widgets.grid.DataVisualizer#createAndGetChildenGridItemDataVisualizer()
	 */
	@Override
	public DataVisualizer createAndGetChildenGridItemDataVisualizer() {
		throw new UnsupportedOperationException("GridItem Children dataVisualizer is not supported in AdaptedDataVisualizer");
	}

}
