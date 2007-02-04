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
package org.eclipse.swt.nebula.snippets.ctree;

import java.text.BreakIterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.nebula.widgets.ctree.AbstractItem;
import org.eclipse.swt.nebula.widgets.ctree.CTreeCell;

/**
 * A Sample custom cell with no styles set (a "base" cell).
 * Everything is drawn custom and the toggle's visibility 
 * is set on and off dynamically.
 */
public class MultiLineCell extends CTreeCell {

	private String originalText;
	
	public MultiLineCell(AbstractItem item, int style) {
		super(item, style);
	}

	private String format(String text, int width) {
		if(text == null || text.length() == 0) return "";
		
		if((!toggleVisible && internalGC().stringExtent(text).x < width) ||
				(toggleVisible && internalGC().stringExtent(text).x < width+toggleBounds.width)) {
			if(open) open = false;
			if(toggleVisible) setToggleVisible(false);
			return text;
		}
		
		if(!open) {
			if(!toggleVisible) setToggleVisible(true);
			String str = text.substring(0, 1) + "...";
			for(int i = 2; i < text.length() && internalGC().stringExtent(str).x < width; i++) {
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
				lineLen = lineLen + internalGC().stringExtent(word).x;
				if(lineLen >= width && str.length() > 0) {
					str += "\n";
				    lineLen = internalGC().stringExtent(word).x;
				}
				str += word;
				start = end;
				end = bi.next();
		    	}
		    return str;
		}
	}

	protected void layout() {
		int width = getClientArea().width;
		super.setText(format(originalText, width));
	}

	public Point computeTextSize(int wHint, int hHint) {
		if(originalText != null) {
			String str = format(originalText, wHint);
			return internalGC().textExtent(str);
		} else {
			return new Point(0,0);
		}
	}
	
	public void setText(String string) {
		originalText = string;
		redraw();
	}
}
