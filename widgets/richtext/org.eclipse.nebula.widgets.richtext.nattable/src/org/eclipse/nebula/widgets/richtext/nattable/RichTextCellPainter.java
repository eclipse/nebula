/*****************************************************************************
 * Copyright (c) 2015 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *		Dirk Fauth <dirk.fauth@googlemail.com> - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.nebula.widgets.richtext.nattable;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.cell.CellDisplayConversionUtils;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.AbstractCellPainter;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.CellStyleUtil;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.richtext.RichTextPainter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

// TODO add support for automatic size calculation
public class RichTextCellPainter extends AbstractCellPainter {

	private RichTextPainter richTextPainter;

	public RichTextCellPainter() {
		this(true);
	}

	public RichTextCellPainter(boolean wordWrap) {
		this.richTextPainter = new RichTextPainter(wordWrap);
	}

	@Override
	public void paintCell(ILayerCell cell, GC gc, Rectangle bounds, IConfigRegistry configRegistry) {
		IStyle cellStyle = CellStyleUtil.getCellStyle(cell, configRegistry);
		setupGCFromConfig(gc, cellStyle);

		bounds.x += CellStyleUtil.getHorizontalAlignmentPadding(cellStyle, bounds, getPreferredWidth(cell, gc, configRegistry));
		bounds.y += CellStyleUtil.getVerticalAlignmentPadding(cellStyle, bounds, getPreferredHeight(cell, gc, configRegistry));

		String htmlText = CellDisplayConversionUtils.convertDataType(cell, configRegistry);
		this.richTextPainter.paintHTML(htmlText, gc, bounds);
	}

	@Override
	public int getPreferredWidth(ILayerCell cell, GC gc, IConfigRegistry configRegistry) {
		setupGCFromConfig(gc, CellStyleUtil.getCellStyle(cell, configRegistry));
		String htmlText = CellDisplayConversionUtils.convertDataType(cell, configRegistry);

		// using a zero size rectangle for calculation results in a content related preferred size
		this.richTextPainter.preCalculate(htmlText, gc, new Rectangle(0, 0, 0, 0));
		return this.richTextPainter.getPreferredSize().x;
	}

	@Override
	public int getPreferredHeight(ILayerCell cell, GC gc, IConfigRegistry configRegistry) {
		setupGCFromConfig(gc, CellStyleUtil.getCellStyle(cell, configRegistry));
		String htmlText = CellDisplayConversionUtils.convertDataType(cell, configRegistry);

		// using a zero size rectangle for calculation results in a content related preferred size
		this.richTextPainter.preCalculate(htmlText, gc, new Rectangle(0, 0, 0, 0));
		return this.richTextPainter.getPreferredSize().y;
	}

	/**
	 * Setup the GC by the values defined in the given cell style.
	 *
	 * @param gc
	 * @param cellStyle
	 */
	public void setupGCFromConfig(GC gc, IStyle cellStyle) {
		Color fg = cellStyle.getAttributeValue(CellStyleAttributes.FOREGROUND_COLOR);
		Color bg = cellStyle.getAttributeValue(CellStyleAttributes.BACKGROUND_COLOR);
		Font font = cellStyle.getAttributeValue(CellStyleAttributes.FONT);

		gc.setAntialias(GUIHelper.DEFAULT_ANTIALIAS);
		gc.setTextAntialias(GUIHelper.DEFAULT_TEXT_ANTIALIAS);
		gc.setFont(font);
		gc.setForeground(fg != null ? fg : GUIHelper.COLOR_LIST_FOREGROUND);
		gc.setBackground(bg != null ? bg : GUIHelper.COLOR_LIST_BACKGROUND);
	}

}
