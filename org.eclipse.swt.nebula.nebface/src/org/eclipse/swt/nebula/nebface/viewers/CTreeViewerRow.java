/*******************************************************************************
 * Copyright (c) 2006 Tom Schindl and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl <tom.schindl@bestsolution.at> - initial API and implementation
 *     IBM Corporation
 *******************************************************************************/
package org.eclipse.swt.nebula.nebface.viewers;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.nebula.widgets.ctree.CTree;
import org.eclipse.swt.nebula.widgets.ctree.CTreeItem;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;

public class CTreeViewerRow extends ViewerRow {
	private CTreeItem item;

	public CTreeViewerRow(CTreeItem item) {
		super(item);
		this.item = item;
	}

	public void clear() {
		item.setText("");
		if (getColumnCount() == 0) {
			item.setImage(null);
		} else {
			for (int i = 0; i < getColumnCount(); i++) {
				item.setImage(i, null);
			}
		}
	}

	public Color getBackground(int columnIndex) {
		return item.getBackground(columnIndex);
	}

	public Rectangle getBounds(int columnIndex) {
		return item.getBounds(columnIndex);
	}

	public Rectangle getBounds() {
		return item.getBounds();
	}

	public int getColumnCount() {
		return item.getParent().getColumnCount();
	}

	public Font getFont(int columnIndex) {
		return item.getFont(columnIndex);
	}

	public Color getForeground(int columnIndex) {
		return item.getForeground(columnIndex);
	}

	public Image getImage(int columnIndex) {
		return item.getImage(columnIndex);
	}

	public Item getItem() {
		return item;
	}

	public String getText(int columnIndex) {
		return item.getText(columnIndex);
	}

	public void setBackground(int columnIndex, Color color) {
		item.setBackground(columnIndex, color);
	}

	public void setFont(int columnIndex, Font font) {
		item.setFont(columnIndex, font);
	}

	public void setForeground(int columnIndex, Color color) {
		item.setForeground(columnIndex, color);
	}

	public void setImage(int columnIndex, Image image) {
		item.setImage(columnIndex, image);
	}

	public void setText(int columnIndex, String text) {
		item.setText(columnIndex, text);
	}

	public Control getControl() {
		return item.getParent();
	}

	public ViewerRow getNeighbor(int direction, boolean sameLevel) {
		if(sameLevel) {
			if(item.hasParentItem()) {
				CTreeItem pitem = item.getParentItem();
				int ix = pitem.getIndexOf(item);
				if(ViewerRow.ABOVE == direction && ix > 0) {
					return new CTreeViewerRow(pitem.getItems()[ix-1]);
				} else if(ViewerRow.BELOW == direction && ix < pitem.getItemCount()) {
					return new CTreeViewerRow(pitem.getItems()[ix+1]);
				}
			} else {
				CTree tree = item.getParent();
				int ix = tree.getIndexOf(item);
				if(ViewerRow.ABOVE == direction && ix > 0) {
					return new CTreeViewerRow(tree.getItems()[ix-1]);
				} else if(ViewerRow.BELOW == direction && ix < tree.getItemCount()) {
					return new CTreeViewerRow(tree.getItems()[ix+1]);
				}
			}
		} else {
			List l = Arrays.asList(item.getParent().getVisibleItems());
			int ix = l.indexOf(item);
			if(ViewerRow.ABOVE == direction && ix > 0) {
				return new CTreeViewerRow((CTreeItem) l.get(ix-1));
			} else if(ViewerRow.BELOW == direction && ix < l.size()) {
				return new CTreeViewerRow((CTreeItem) l.get(ix+1));
			}
		}
		return null;
	}
}
