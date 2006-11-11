/****************************************************************************
* Copyright (c) 2005-2006 Jeremy Dowdall
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
*****************************************************************************/

package org.eclipse.swt.nebula.widgets.ctabletree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 */
class CTableTreeLayout extends CContainerLayout {

	CTableTreeLayout(CTableTree tree) {
		super(tree);
	}

	private List fillers = new ArrayList();
	protected Point computeBodySize() {
		CContainerItem[] items = container.getVisibleItems();
		if(items.length > 0) {
			int gridLine = container.getGridLineWidth();

			CContainerItem dirtyItem = container.getDirtyItem();
			if(container.isOperation(CTableTree.OP_ADD)) {
				if(dirtyItem == null || !dirtyItem.isVisible()) return bodySize; // nothing to do
				if(!dirtyItem.isAutoHeight()) {
					Point[] sa = dirtyItem.computeSize(columnWidths, -1);
					dirtyItem.setSize(sa);
					Point p = dirtyItem.getUnifiedSize();
					bodySize.x = Math.max(bodySize.x, p.x);
					bodySize.y += (p.y + gridLine);
				} else if(!fillers.contains(dirtyItem)) {
					fillers.add(dirtyItem);
				}
			} else if(container.isOperation(CTableTree.OP_REMOVE)) {
				for(int i = 0; i < items.length; i++) {
					bodySize.x = Math.max(bodySize.x, items[i].getUnifiedSize().x);
				}
				CContainerItem[] ritems = container.getRemovedItems();
				for(int i = 0; i < ritems.length; i++) {
					if(!ritems[i].isAutoHeight()) {
						bodySize.y -= (container.getRemovedItems()[i].getUnifiedSize().y + gridLine);
					} else {
						fillers.remove(ritems[i]);
					}
				}
			} else if(container.isOperation(CTableTree.OP_CELL_COLLAPSE) || container.isOperation(CTableTree.OP_CELL_EXPAND)) {
				if(!dirtyItem.isAutoHeight()) {
					if(dirtyItem == null || !dirtyItem.isVisible()) return bodySize; // nothing to do
					Point oldS = dirtyItem.getUnifiedSize();
					Point[] sa = dirtyItem.computeSize(columnWidths, -1);
					dirtyItem.setSize(sa);
					Point newS = dirtyItem.getUnifiedSize();
					for(int i = 0; i < items.length; i++) {
						bodySize.x = Math.max(bodySize.x, items[i].getUnifiedSize().x);
					}
					bodySize.y = bodySize.y - oldS.y + newS.y;
				}
			} else {
				Point p;
				bodySize.x = bodySize.y = 0;
				fillers.clear();
				for(int i = 0; i < items.length; i++) {
					if(!items[i].isAutoHeight()) {
						Point[] sa = items[i].computeSize(columnWidths, -1);
						items[i].setSize(sa);
						p = items[i].getUnifiedSize();
						bodySize.x = Math.max(bodySize.x, p.x);
						bodySize.y += (p.y + gridLine);
					} else if(!fillers.contains(items[i])) {
						fillers.add(items[i]);
					}
				}
			}
		} else {
			bodySize.x = bodySize.y = 0;
			fillers.clear();
		}

		return bodySize;
	}

	private int oldHeight = -1;
	protected void layoutItems(Rectangle r) {
		r.x = container.marginWidth;
		r.y = 0;
		int gridLine = container.getGridLineWidth();
		int fillerHeight0 = fillers.isEmpty() ? 1 : (r.height - bodySize.y) / fillers.size();
		int fillerHeight1 = fillers.isEmpty() ? 1 : r.height - bodySize.y - (fillerHeight0 * (fillers.size() - 1));
		if(fillerHeight0 == 0) fillerHeight0 = 1;
		if(fillerHeight1 == 0) fillerHeight1 = 1;
		Object lastFiller = fillers.isEmpty() ? null : fillers.get(fillers.size()-1);
		
		CContainerItem[] items = container.getVisibleItems();
		int start = Arrays.asList(items).indexOf(container.getDirtyItem());
		if(items.length > 0 && start < 0) {
			start = 0;
		}
		if(oldHeight != r.height && !fillers.isEmpty()) {
			oldHeight = r.height;
			start = 0;
		}
		if(start >= 0 && start < items.length) {
			if(start > 0) {
				Rectangle ubounds = items[start-1].getUnifiedBounds();
				r.y += (ubounds.y + ubounds.height) + gridLine;
			}
			Point location = new Point(r.x, r.y);
			Rectangle[] bounds = new Rectangle[columnWidths.length];
			for(int i = start; i < items.length; i++) {
				location.x = r.x;
				for(int j = 0; j < columnWidths.length; j++) {
					bounds[j] = new Rectangle(
							location.x,
							location.y,
							columnWidths[j],
							items[i].isAutoHeight() ?
									(items[i] != lastFiller ? fillerHeight0 : fillerHeight1) :
										items[i].getSize()[j].y
							);
					location.x += columnWidths[j];
				}
				items[i].setBounds(bounds);
				location.y += items[i].getUnifiedBounds().height + gridLine;
			}
		}
	}
}