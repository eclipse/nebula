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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TreeItem;

/**
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 */
public class CTreeCell extends AbstractCell {

	private CTree ctree;
	private Image[] images;
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
	
	public void clear() {
		text = null;
		images = null;
	}
	
	public Point computeClientSize(int wHint, int hHint) {
		Point size = new Point(0,0);
		
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
			iBounds = new Rectangle(0,0,0,0);
		}
		
		Point tSize = computeTextSize(wHint-iBounds.width, -1);

		size.x = iBounds.width + ((iBounds.width > 0 && tSize.x > 0) ? horizontalSpacing : 0) + tSize.x;
		size.y = Math.max(iBounds.height, tSize.y);

		if(open) {
			Point cSize = computeControlSize(wHint-size.x, -1);
			size.x += cSize.x;
			size.y = Math.max(size.y, cSize.y);
		}

		return size;
	}

	public Point computeSize(int wHint, int hHint) {
		if(wHint == 0 || hHint == 0) return new Point(0,0);
		
		Point size = new Point(marginLeft+marginWidth+marginWidth+marginRight, marginTop+marginHeight+marginHeight+marginBottom);
		if(toggleVisible || ghostToggle) size.x += toggleWidth;
		
		Point clientSize = computeClientSize(wHint-size.x, hHint-size.y);
		if(clientSize != null) {
			size.x += clientSize.x;
			size.y += clientSize.y;
		}
		
		Point childSize = computeChildSize(wHint-size.x, hHint-size.y);
		if(childSize != null) {
			size.x += (childSize.x + (childBounds.x - bounds.x));
			size.y += (childSize.y + (childBounds.y - bounds.y));
		}
		
		if(wHint != SWT.DEFAULT) {
			size.x = Math.min(size.x, wHint);
		}
		if(hHint != SWT.DEFAULT) {
			size.y = Math.min(size.y, hHint);
		}
		
		return size;
	}
	
	protected Point computeTextSize(int wHint, int hHint) {
		return (text != null && text.length() > 0) ?
				ctree.internalGC.textExtent(text, SWT.DRAW_DELIMITER | SWT.DRAW_TAB | SWT.DRAW_TRANSPARENT) : 
					new Point(0,0);
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

	void internalFirstPainting() {
		if(isCheckCell()) setCheck(true);
		if(isTreeCell()) {
			if(((CTreeItem) item).hasParentItem()) {
				setIndent(((CTreeItem) item).getParentIndent() + ctree.getTreeIndent());
			}
			setToggleVisible(((CTreeItem) item).hasItems(), true);
		}
	}

//	private boolean isClear() {
//		return (text == null || text.length() == 0) && (images == null || images.length == 0);
//	}

	public boolean isCheckCell() {
		return ((CTreeItem) item).getCheckCell() == this;
	}
	
	public boolean isTreeCell() {
		return ((CTreeItem) item).getTreeCell() == this;
	}
	
	protected void layout(Control control) {
		Rectangle area = getClientArea();
		Point size = control.computeSize(-1, -1);
		size.x = (hAlign == SWT.FILL) ? area.width : Math.min(area.width, size.x);
		size.y = (vAlign == SWT.FILL) ? area.height : Math.min(area.height, size.y);
		control.setSize(size.x, size.y);
		locate(control);
	}
	
	final protected void layoutInternal() {
		if(ghostToggle || toggleVisible) {
			toggleBounds.x = indent;
			toggleBounds.y = 0;
		}
		toggleBounds.width = Math.min(bounds.width, toggleWidth);
		toggleBounds.height = bounds.height;

		Rectangle clientBounds = getClientArea();
		Point clientSize = computeClientSize(clientBounds.width, clientBounds.height);

		if(images == null) {
			iBounds = new Rectangle[0];
		} else {
			iBounds = new Rectangle[images.length];
			for(int i = 0; i < iBounds.length; i++) {
				iBounds[i] = (images[i] != null && !images[i].isDisposed()) ? images[i].getBounds() : new Rectangle(0,0,1,1);
			}
		}

		int x;
		if((style & SWT.CENTER) != 0) {
			x = clientBounds.x+((clientBounds.width-clientSize.x)/2);
		} else if((style & SWT.RIGHT) != 0) {
			x = clientBounds.x+clientBounds.width-clientSize.x;
		} else { // defaults to LEFT
			x = clientBounds.x;
		}

		for(int i = 0; i < iBounds.length; i++) {
			iBounds[i].x = x;
			x += iBounds[i].width;
			if((i != iBounds.length-1) || (text.length() > 0)) x += horizontalSpacing;
		}
		
		tBounds.x = x;

		Point tSize = computeTextSize(clientBounds.width-tBounds.x, bounds.height);
		tBounds.width = tSize.x;
		tBounds.height = tSize.y;

		if((open) || (style & SWT.TOP) != 0) {
			for(int i = 0; i < iBounds.length; i++) {
				iBounds[i].y = clientBounds.y;
			}
			tBounds.y = clientBounds.y;
		} else if((style & SWT.BOTTOM) != 0) {
			toggleBounds.y = clientBounds.y + clientBounds.height - toggleBounds.height;
			for(int i = 0; i < iBounds.length; i++) {
				iBounds[i].y = clientBounds.y + clientBounds.height - iBounds[i].height;
			}
			tBounds.y = clientBounds.y + clientBounds.height - tBounds.height;
		} else { // defaults to CENTER
			toggleBounds.y = clientBounds.y + (clientBounds.height-toggleBounds.height)/2;
			for(int i = 0; i < iBounds.length; i++) {
				iBounds[i].y = clientBounds.y + (clientBounds.height-iBounds[i].height)/2;
			}
			tBounds.y = clientBounds.y + (clientBounds.height-tBounds.height)/2;
		}
	}

	protected void locate(Control control) {
		Point scroll = container.getScrollPosition();
		Rectangle area = getClientArea();
		Point loc = new Point(bounds.x+area.x-scroll.x, bounds.y+area.y-scroll.y);
		Point size = control.getSize();
		if(hAlign == SWT.RIGHT) loc.x += (area.width-size.x);
		else if(hAlign == SWT.CENTER) loc.x += ((area.width-size.x)/2);
		if(vAlign == SWT.BOTTOM) loc.y += (area.height-size.y);
		else if(vAlign == SWT.CENTER) loc.y += ((area.height-size.y)/2);
		control.setLocation(loc);
	}

	protected void locateCheck(Button check) {
		Point scroll = container.getScrollPosition();
		Rectangle area = getClientArea();
		check.setLocation(
				bounds.x+area.x-scroll.x,
				bounds.y+-scroll.y+(bounds.height-check.computeSize(-1, -1).y)/2
				);
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
