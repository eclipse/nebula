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

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 */
public class CTreeCell extends AbstractCell {

	private CTree ctree;
	private Image[] images = new Image[0];
	private String text;

	private int horizontalSpacing = 2;
	private Rectangle[] iBounds = new Rectangle[0];
	private Rectangle tBounds = new Rectangle(0,0,0,0);

	private int[] childSpan = new int[] { -1, 1 };	// default setting keeps the child area
													// within the same column as the title area

	public CTreeCell(AbstractItem item, int style) {
		super(item, style);
		ctree = (CTree) container;
	}

	public Point computeSize(int wHint, int hHint) {
		Point size = new Point(0,0);
//		if(titleArea != null) {
//			titleSize.y += titleArea.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
//		} else {
			Image[] images = getImages();
			Rectangle iBounds = null;
			if(images != null && images.length > 0) {
				iBounds = images[0].getBounds();
				for(int i = 1; i < images.length; i++) {
					Rectangle ib = images[i].getBounds();
					iBounds.width += horizontalSpacing + ib.width;
					iBounds.height = Math.max(iBounds.height, ib.height);
				}
			} else {
				iBounds = new Rectangle(0,0,1,1);
			}
			
			String text = getText();
			GC gc = ctree.internalGC;
			Point tSize = (text.length() > 0) ? gc.textExtent(text) : new Point(0,0);

			size.x += iBounds.width + ((iBounds.width > 0 && tSize.x > 0) ? horizontalSpacing : 0) + tSize.x;
			size.y += Math.max(iBounds.height, tSize.y);
//		}
		
//		if(open) {
//			childSize = childArea.computeSize(SWT.DEFAULT,SWT.DEFAULT);
//			titleSize.x = Math.max(titleSize.x, childSize.x);
//		} else {
//			Point childSize = new Point(0,0);
//		}

		if(toggleVisible || ghostToggle) size.x += toggleWidth;
		size.x += marginLeft + marginWidth + marginWidth + marginRight;
		size.y += marginTop + marginHeight + marginHeight + marginBottom;

//		Point returnSize = new Point(size.x, size.y);
//		if(childSize.y > 0) {
//			returnSize.y += (childBounds.y + childSize.y + childBounds.y);
//		}

		if(wHint != SWT.DEFAULT) {
			size.x = Math.min(size.x, wHint);
		}
		if(hHint != SWT.DEFAULT) {
			size.y = Math.min(size.y, hHint);
		}
		
		return size;
	}
	
//	/**
//	 * Compute the preferred size of the cell's Title Area, similar to the way it would be done 
//	 * in a regular SWT layout.
//	 * <p>Implementations are to implement this themselves, though if the the cell's style is 
//	 * SWT.TITLE then most likely they will simply return the computed height as found by
//	 * titleArea.computeSize(int, -1).y</p> 
//	 * @param wHint
//	 * @return an int representing the preferred height of the cell
//	 */
//	public int computeTitleHeight(int hHint) {
//		titleHeight = marginTop + marginHeight + marginHeight + marginBottom;
//		if(titleArea != null) {
//			titleHeight += titleArea.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
//		} else {
//			int height = 0;
//			for(int i = 0; i < iBounds.length; i++) {
//				height = Math.max(height, iBounds[i].height);
//			}
//			String text = getText();
//			if(text != null) {
//				GC gc = CTree.staticGC;
//				height = Math.max(height, gc.textExtent(text).y);
//			}
//			titleHeight += height;
//		}
//		if(hHint != SWT.DEFAULT) {
//			titleHeight = Math.min(titleHeight, hHint);
//		}
//		return titleHeight;
//	}
	
	/**
	 * Get information on if and how the child area of this CTableTree will span
	 * the columns.
	 * 
	 * @return an int[] with a length of 2: int[0] represents the starting
	 *         column, and int[1] represents the number of columns, from the
	 *         start, to span.
	 * @see #setChildSpan(int, int)
	 */
	public int[] getChildSpan() {
		return childSpan;
	}

	public Image getImage() {
		if(images.length > 0) return images[0];
		return null;
	}
	
	public Image[] getImages() {
		return images == null ? new Image[0] : images;
	}
	
	public String getText() {
		return (text == null) ? "" : text;
	}
	
	protected void layout() {
		toggleBounds.width = Math.min(bounds.width, toggleWidth);
		toggleBounds.height = bounds.height;
		
		iBounds = new Rectangle[images.length];
		for(int i = 0; i < iBounds.length; i++) {
			iBounds[i] = (images[i] != null && !images[i].isDisposed()) ? images[i].getBounds() : new Rectangle(0,0,1,1);
		}
		String text = getText();
		Point tSize = (text.length() > 0) ?
				ctree.internalGC.textExtent(text, SWT.DRAW_DELIMITER | SWT.DRAW_TAB | SWT.DRAW_TRANSPARENT) : new Point(0,0);
		tBounds.width = tSize.x;
		tBounds.height = tSize.y;
		
		int x = 0, y = 0, width = 0, height = 0;
		
		// indent
		width = indent;
		
		// toggle
		if(ghostToggle || toggleVisible) {
			width += toggleBounds.width;
		}
		
		// images
		for(int i = 0; i < iBounds.length; i++) {
			width += iBounds[i].width; 
			if((i != iBounds.length-1) || (text.length() > 0)) width += horizontalSpacing;
			height = Math.max(height, iBounds[i].height);
		}
		
		// text
		width += tSize.x;
		height += tSize.y;

	// calculate positions
		if((style & SWT.CENTER) != 0) {
			x += ((bounds.width-(indent+marginLeft+marginWidth+width+marginWidth+marginRight))/2);
		} else if((style & SWT.RIGHT) != 0) {
			x += (bounds.width-(width+marginWidth+marginRight));
		} else { // defaults to LEFT
			x += (indent+marginLeft+marginWidth);
		}

		if(ghostToggle || toggleVisible) {
			toggleBounds.x = x - 1;
			x += toggleBounds.width;
		}
		for(int i = 0; i < iBounds.length; i++) {
			iBounds[i].x = x;
			x += iBounds[i].width;
			if((i != iBounds.length-1) || (text.length() > 0)) x += horizontalSpacing;
		}
		tBounds.x = x;
		
		if((open) || (style & SWT.TOP) != 0) {
			toggleBounds.y = y; // toggle centers itself, so don't add margins to it
			y += marginTop+marginHeight;
			for(int i = 0; i < iBounds.length; i++) {
				iBounds[i].y = y;
			}
			tBounds.y = y;
		} else if((style & SWT.BOTTOM) != 0) {
			toggleBounds.y = y + bounds.height-(toggleBounds.y+marginHeight+marginBottom);
			for(int i = 0; i < iBounds.length; i++) {
				iBounds[i].y = y + bounds.height-(iBounds[i].y+marginHeight+marginBottom);
			}
			tBounds.y = y + bounds.height-(tBounds.y+marginHeight+marginBottom);
		} else { // defaults to CENTER
			toggleBounds.y = y + (bounds.height-toggleBounds.height)/2;
			for(int i = 0; i < iBounds.length; i++) {
				iBounds[i].y = y + (bounds.height-iBounds[i].height)/2;
			}
			tBounds.y = y + (bounds.height-tBounds.height)/2;
		}

	// set positions of self drawing components
//		if(titleArea != null) {
//			Rectangle ca = getClientArea();
//			titleArea.setBounds(bounds.x + ca.x, bounds.y + ca.y, ca.width, ca.height);
//			titleArea.layout(true, true);
//		}
		
//		if(childArea != null) {
//			if(open && childSpan[0] < ctt.getColumnCount()) {
//				int s0 = childSpan[0] < 0 ? item.getCellIndex(this) : childSpan[0];
//				int s1 = childSpan[1] < 0 ?
//						ctt.getColumnCount() - 1 :
//							s0 + childSpan[1] - 1;
//				if(s1 >= ctt.getColumnCount()) s1 = ctt.getColumnCount() - 1;
//
//				int cx = childSpan[0] < 0 ?
//						bounds.x+marginWidth+toggleBounds.width :
//							ctt.internalGetColumn(s0).getLeft();
//				int cw = ctt.internalGetColumn(s1).getRight() - cx - rightChildIndent;
//
//				childArea.setBounds(
//						cx,
//						bounds.y+titleSize.y+childBounds.y,
//						cw,
//						bounds.height-(titleSize.y+childBounds.y+childBounds.y)
//						);
//			} else {
//				childArea.setBounds(0, 0, 0, 0);
//			}
//		}
	}
	
	protected void locate() {
		// TODO: locate
	}
	
	public void doPaint(GC gc, Point offset) {
		if(activeBackground != null) gc.setBackground(activeBackground);
		if(activeForeground != null) gc.setForeground(activeForeground);

		// background
		gc.fillRectangle(
				offset.x,
				offset.y,
				bounds.width,
				bounds.height
		);

		// images
		for(int i = 0; i < iBounds.length; i++) {
			if(!images[i].isDisposed()) {
				gc.drawImage(images[i], offset.x+iBounds[i].x, offset.y+iBounds[i].y);
			}
		}

		// text
		if(getText().length() > 0) {
			Font bf = gc.getFont();
			if(getFont() != null) gc.setFont(getFont());
			gc.drawText(getText(), offset.x+tBounds.x, offset.y+tBounds.y);
			if(getFont() != null) gc.setFont(bf);
		}

		if(((CTreeItem) item).getTreeCell() == this) {
			paintChildLines(gc, offset);
		}
		
		// toggle (it changes the colors again so paint it last...)
		if(toggleVisible) {
			paintToggle(gc, offset);
		}
	}
	
	public boolean isTreeCell() {
		return ((CTreeItem) item).getTreeCell() == this;
	}
	
	private void paintChildLines(GC gc, Point offset) {
		if(win32) {
			int gline = ctree.getGridLineWidth();
			Rectangle ibounds = item.getBounds();
			ibounds.y++;
			Rectangle tbounds = getBounds();
			tbounds.y++;
			int x1 = 0, x2 = 0, y1 = 0, y2 = 0;
			int x = toggleBounds.x + (toggleBounds.width/2) - offset.x;
			int y = tbounds.y + (tbounds.height/2) - offset.y;
			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
			gc.setLineDash(new int[] { 1, 1 });
			x1 = x;
			x2 = x+toggleBounds.width/2;
			y1 = y;
			y2 = y;
			if(gline % 2 == 0) {
				if(y1 % 2 == 1) y1 -= 1;
				if(y2 % 2 == 1) y2 -= 1;
			}
			gc.drawLine(x1, y1, x2, y2);
			CTreeItem it = (CTreeItem) item;
			int index = Arrays.asList(
					it.hasParentItem() ? 
							it.getParentItem().getItems() :
								ctree.getItems()
								).indexOf(it);
			int count = it.hasParentItem() ? 
					it.getParentItem().getItemCount() : 
						ctree.getItemCount();
			if(index > 0 || it.hasParentItem()) {
				x1 = x;
				x2 = x;
				y1 = ibounds.y - offset.y;
				y2 = y;
				if(gline % 2 == 0) {
					if(y1 % 2 == 1) y1 -= 1;
					if(y2 % 2 == 1) y2 -= 1;
				}
				gc.drawLine(x1, y1, x2, y2);
			}
			if(index < count - 1) {
				x1 = x;
				x2 = x;
				y1 = y;
				y2 = (it.hasParentItem() ?
						it.getParentItem().getItem(index+1).getCellBounds()[0].y :
							ctree.getItem(index+1).getCellBounds()[0].y)
							- offset.y;
				if(gline % 2 == 0) {
					if(y1 % 2 == 1) y1 -= 1;
					if(y2 % 2 == 1) y2 -= 1;
				}
				gc.drawLine(x1, y1, x2, y2);
			}
			x1 = x;
			x2 = x;
			y1 = ibounds.y - offset.y;
			y2 = ibounds.y+ibounds.height - offset.y;
			if(gline % 2 == 0) {
				if(y1 % 2 == 1) y1 -= 1;
				if(y2 % 2 == 1) y2 -= 1;
			}
			while(it.hasParentItem()) {
				x1 = x2 -= ((CTree) ctree).getTreeIndent();
				it = it.getParentItem();
				index = Arrays.asList(
						it.hasParentItem() ? 
								it.getParentItem().getItems() :
									ctree.getItems()
									).indexOf(it);
				count = it.hasParentItem() ? 
						it.getParentItem().getItemCount() : 
							ctree.getItemCount();
				if(index < count - 1) {
					gc.drawLine(x1, y1, x2, y2);
				}
			}
			if(open && ((CTreeItem) item).hasItems()) {
				x1 = x2 = x + ((CTree) ctree).getTreeIndent();
				y1 = tbounds.y + tbounds.height - offset.y;
				y2 = ibounds.y + ibounds.height - offset.y;
				if(gline % 2 == 0) {
					if(y1 % 2 == 1) y1 -= 1;
					if(y2 % 2 == 1) y2 -= 1;
				}
				gc.drawLine(x1, y1, x2, y2);
			}
			gc.setLineDash(null);
		}
	}

	public void setImage(Image image) {
		if(image == null) images = new Image[0];
		else images = new Image[] { image };
	}
	
	public void setImages(Image[] images) {
		if(images == null) {
			this.images = new Image[0];
		}
		else {
			boolean doit = true;
			for(int i = 0; i < images.length; i++) {
				if(images[i] == null || images[i].isDisposed()) doit = false;
			}
			if(doit) this.images = images;
		}
		update = true;
		ctree.redraw(this);
	}

	/**
	 * Set which columns the Child Area of this CTableTreeCell will span.<br />
	 * The default setting is: start == -1 and len == 1. To span the entire
	 * CTableTree (all columns), then use <code>setChildSpan(0, -1)</code>.
	 * 
	 * @param start
	 *            the column in which the child area will begin. A value of -1
	 *            indicates that the child area should begin in the same column
	 *            as its title area (same cell).
	 * @param len
	 *            how many columns, starting with the one specified by 'start',
	 *            the child area will span. A value of '-1' indicates that the
	 *            child area should span all the way to the end of the last
	 *            column.
	 * @see #getChildSpan()
	 */
	public void setChildSpan(int start, int len) {
		childSpan[0] = start;
		childSpan[1] = len;
	}

//	public void setOpen(boolean open) {
//		if(((CTreeItem) item).getTreeCell() == this) {
//			if(this.open != open) {
//				this.open = open;
//				container.layout(open ? SWT.Expand : SWT.Collapse, item);
//			}
//		} else {
//			super.setOpen(open);
//		}
//	}
	
	public void setText(String string) {
		if(string != null && !string.equals(getText())) {
			text = string;
			update = true;
			ctree.redraw(this);
		}
	}

}
