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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.nebula.widgets.ctabletree.CContainerItem;
import org.eclipse.swt.nebula.widgets.ctabletree.CTableTreeCell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * A Sample custom cell of style TITLE.
 * The CContainerCell's toggle is set on and off dynamically, and contains a
 * native SWT Text box in its Title Area.
 * <p>Note that the style SWT.TOP is also set so that the toggle will be drawn
 * at the top of the cell when it is expanded (compare to MultiLineCell which
 * does not have this style set).</p>
 */
public class MultiLineTextCell extends CTableTreeCell {

	private Text text;
	
	public MultiLineTextCell(CContainerItem item, int style) {
		super(item, style | SWT.TITLE | SWT.TOP);
	}

	protected void createTitleContents(Composite contents, int style) {
		contents.setLayout(new FillLayout());
		text = new Text(contents, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		text.setText("This is a text box with multiple lines This is a text box with multiple lines");

		setExclusions(text);
	}
	
	public Point computeSize(int wHint, int hHint) {
		if(!open) {
			return super.computeSize(wHint, hHint);
		} else {
			int xtrim = marginLeft + marginWidth + marginWidth + marginRight + toggleWidth;
			Point size = text.computeSize(wHint < 1 ? -1 : wHint-xtrim, SWT.DEFAULT);
			size.x += xtrim;
			size.y += marginTop + marginHeight + marginHeight + marginBottom;
			
			if(wHint != SWT.DEFAULT) {
				size.x = Math.min(size.x, wHint);
			}
			if(hHint != SWT.DEFAULT) {
				size.y = Math.min(size.y, hHint);
			}
			
			return size;
		}
	}
	
	public void setBounds(Rectangle bounds) {
		super.setBounds(bounds);
		if(!open && text != null) {
			boolean needsToggle = text.computeSize(getTitleClientArea().width, SWT.DEFAULT).y > getTitleClientArea().height;
			if(!toggleVisible && needsToggle) {
				setToggleVisible(true, false);
				super.setBounds(bounds);
			} else if(toggleVisible && !needsToggle) {
				setToggleVisible(false, false);
				super.setBounds(bounds);
			}
		} else {
			bounds.height = computeSize(bounds.width, -1).y;
			super.setBounds(bounds);
		}
	}

	public void setOpen(boolean open) {
		super.setOpen(open);
		needsLayout = true;
	}
	
}
