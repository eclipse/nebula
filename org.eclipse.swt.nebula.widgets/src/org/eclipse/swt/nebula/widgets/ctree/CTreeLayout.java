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
	int itemHeight = CTree.gtk ? 25 : 20;
	private boolean itemHeightSet = false;
	private boolean fixedHeight = false;
	
	public CTreeLayout(CTree ctree) {
		super(ctree);
		this.ctree = ctree;
	}
	
	protected void computeContentHeight() {
		if(ctree.isEmpty()) {
			contentHeight = 0;
		} else if(fixedHeight) {
			int count = ctree.items(false).size();
			contentHeight = getItemHeight() * count;
			if(ctree.getGridLineWidth() > 1) {
				contentHeight += (count - 1) * ctree.getGridLineWidth();
			}
		} else {
			contentHeight = 0;
			for(Iterator i = ctree.items(false).iterator(); i.hasNext(); ) {
				contentHeight += ((CTreeItem) i.next()).computeHeight();
				contentHeight += ctree.getGridLineWidth();
			}
			contentHeight -= ctree.getGridLineWidth();
		}
	}

	protected void computeContentHeight(int eventType, AbstractCell cell) {
		if(SWT.Collapse == eventType) {
			if(fixedHeight) {
				contentHeight -= (getItemHeight() * ctree.getItems((CTreeItem) cell.item, false).size());
			} else {
				contentHeight -= ((CTreeItem) cell.item).computeHeight();
			}
		} else if(SWT.Expand == eventType) {
			if(fixedHeight) {
				contentHeight += (getItemHeight() * ctree.getItems((CTreeItem) cell.item, false).size());
			} else {
				contentHeight += ((CTreeItem) cell.item).computeHeight();
			}
		}
	}

	protected void computeContentHeight(int eventType, AbstractColumn column) {
		if(!fixedHeight) {
			computeContentHeight();
		}
	}

	protected void computeContentHeight(int eventType, AbstractItem item) {
		if(SWT.Show == eventType || SWT.Hide == eventType) {
			int height = itemHeight;
			if(!fixedHeight) {
				height = ((CTreeItem) item).computeHeight();
			}
			contentHeight += (SWT.Show == eventType) ? height : -height;
		}
	}

	int getItemHeight() {
		if(itemHeightSet) return itemHeight;
		if(ctree.isEmpty()) return itemHeight;
		itemHeight = ((CTreeItem) ctree.itemList.get(0)).computeHeight();
		itemHeightSet = true;
		return itemHeight;
	}

	void setItemHeight(int height) {
		itemHeight = height;
		itemHeightSet = true;
	}
	
	protected void layoutContent() {
		int gridLine = ctree.getGridLineWidth();
		int top = 0;
		for(Iterator i = ctree.items(false).iterator(); i.hasNext(); ) {
			CTreeItem item = (CTreeItem) i.next();
			item.setTop(top);
			item.setHeight(fixedHeight ? getItemHeight() : item.computeHeight());
			top += item.getHeight() + gridLine;
		}
	}
	
	protected void layoutContent(int eventType, AbstractCell cell) {
		if(SWT.Collapse == eventType || SWT.Expand == eventType) {
		} else if(SWT.Resize == eventType) {
		}
	}

	protected void layoutContent(int eventType, AbstractColumn column) {
//		just redraw...
//		if(SWT.Move == eventType) {
//		} else if(SWT.Resize == eventType) {
//		}
	}

	protected void layoutContent(int eventType, AbstractItem item) {
//		layoutContent();
	}
}