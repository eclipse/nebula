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

package org.eclipse.swt.nebula.widgets.ctree;

import java.util.Iterator;

import org.eclipse.swt.SWT;

/**
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 */
class CTreeLayout extends AbstractContainerLayout {

	private CTree ctree;
	private int rowHeight = 0;
	private boolean fixedHeight = true;
	
	public CTreeLayout(CTree ctree) {
		super(ctree);
		this.ctree = ctree;
	}
	
	protected void computeContentHeight() {
		if(ctree.visibleItems.isEmpty()) {
			contentHeight = 0;
		} else if(fixedHeight) {
			contentHeight = getRowHeight() * ctree.visibleItems.size();
		} else {
			contentHeight = 0;
			for(Iterator i = ctree.visibleItems.iterator(); i.hasNext(); ) {
				contentHeight += ((CTreeItem) i.next()).computeHeight();
			}
		}
		if(ctree.getGridLineWidth() > 0) {
			contentHeight += (ctree.visibleItems.size() - 1) * ctree.getGridLineWidth();
		}
	}

	protected void computeContentHeight(int eventType, AbstractCell cell) {
		if(SWT.Collapse == eventType) {
			if(fixedHeight) {
				contentHeight -= (getRowHeight() * ctree.getItems(cell.item, false).size());
			} else {
				// TODO
			}
		} else if(SWT.Expand == eventType) {
			if(fixedHeight) {
				contentHeight += (getRowHeight() * ctree.getItems(cell.item, false).size());
			} else {
				// TODO
			}
		}
	}

	protected void computeContentHeight(int eventType, AbstractColumn column) {
		if(!fixedHeight) {
			computeContentHeight();
		}
	}

	protected void computeContentHeight(int eventType, AbstractItem item) {
		// TODO Auto-generated method stub
		
	}

	private int getRowHeight() {
		return (rowHeight > 0) ? rowHeight : ((CTreeItem) ctree.visibleItems.get(0)).computeHeight();
	}
	
	protected void layoutContent() {
		int gridLine = ctree.getGridLineWidth();
		int top = headerSize.y;
		int height = fixedHeight ? getRowHeight() : 0;
		for(Iterator iter = ctree.visibleItems.iterator(); iter.hasNext(); ) {
			CTreeItem item = (CTreeItem) iter.next();
			if(!fixedHeight) {
				height = item.getSize().y;
				if(height <= 0) {
					height = item.computeHeight();
				}
			}
			item.setBounds(-1, top, -1, height);
			top += height + gridLine;
		}
	}
	
	protected void layoutContent(int eventType, AbstractCell cell) {
		if(SWT.Collapse == eventType || SWT.Expand == eventType) {
			if(SWT.Collapse == eventType && 
					ctree.visibleItems.get(ctree.visibleItems.size()-1) == cell.item) {
				return;
			}
			int gridLine = ctree.getGridLineWidth();
			int top = cell.bounds.y + cell.bounds.height + gridLine;
			int height = fixedHeight ? getRowHeight() : 0;
			CTreeItem[] items = ctree.getVisibleItems();
			for(int i = ctree.visibleItems.indexOf(cell.item)+1; i < items.length; i++) {
				if(!fixedHeight) {
					height = items[i].getSize().y;
					if(height <= 0) {
						height = items[i].computeHeight();
					}
				}
				items[i].setBounds(-1, top, -1, height);
				top += height + gridLine;
			}
		}
	}

	protected void layoutContent(int eventType, AbstractColumn column) {
		for(Iterator iter = ctree.visibleItems.iterator(); iter.hasNext(); ) {
			CTreeItem item = (CTreeItem) iter.next();
			item.setBounds(1,-1,1,-1);
		}
	}

	protected void layoutContent(int eventType, AbstractItem item) {
		// TODO Auto-generated method stub
		
	}
}