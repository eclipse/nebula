/****************************************************************************
 * Copyright (c) 2006 Jeremy Dowdall
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/
package org.eclipse.swt.nebula.snippets.ctabletree;

import java.text.BreakIterator;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.nebula.widgets.ctabletree.CContainer;
import org.eclipse.swt.nebula.widgets.ctabletree.CContainerItem;
import org.eclipse.swt.nebula.widgets.ctabletree.CTableTreeCell;

/**
 * A Sample custom cell with no styles set (a "base" cell).
 * Everything is drawn custom and the toggle's visibility 
 * is set on and off dynamically.
 */
public class MultiLineCell extends CTableTreeCell {

	private String text;
	
	public MultiLineCell(CContainerItem item, int style) {
		super(item, style);
	}

	private String format(String text) {
		if(text == null || text.length() == 0) return "";
		
		int maxLen = getTitleClientArea().width;
		
		if((!toggleVisible && CContainer.staticGC.stringExtent(text).x < maxLen) ||
				(toggleVisible && CContainer.staticGC.stringExtent(text).x < maxLen+toggleBounds.width)) {
			if(open) open = false;
			if(toggleVisible) setToggleVisible(false);
			return text;
		}
		
		if(!open) {
			if(!toggleVisible) setToggleVisible(true);
			String str = text.substring(0, 1) + "...";
			for(int i = 2; i < text.length() && CContainer.staticGC.stringExtent(str).x < maxLen; i++) {
				str = text.substring(0, i) + "...";
			}
			return str;
		} else {
		    BreakIterator bi = BreakIterator.getLineInstance();
		    bi.setText(text);
		    int start = bi.first();
		    int end = bi.next();
		    int lineLen = 0;
	
		    String str = "";
		    while(end != BreakIterator.DONE) {
				String word = text.substring(start,end);
				lineLen = lineLen + CContainer.staticGC.stringExtent(word).x;
				if(lineLen >= maxLen && str.length() > 0) {
					str += "\n";
				    lineLen = CContainer.staticGC.stringExtent(word).x;
				}
				str += word;
				start = end;
				end = bi.next();
		    	}
		    return str;
		}
	}
	
	public void setBounds(Rectangle bounds) {
		super.setBounds(bounds);
		if(text != null) setText(text);
	}
	
	public void setOpen(boolean open) {
		super.setOpen(open);
		setText(text);
	}
	
	public void setText(String string) {
		text = string;
		super.setText(format(text));
	}
}
