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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.nebula.widgets.ctabletree.ccontainer.CContainerCell;
import org.eclipse.swt.nebula.widgets.ctabletree.ccontainer.CContainerItem;


public class CTableTreeCell extends CContainerCell {

	private Image[] images = new Image[0];
	private String text;
	
	private Rectangle[] iBounds = new Rectangle[0];
	private Rectangle tBounds = new Rectangle(0,0,0,0);
	

	public CTableTreeCell(CContainerItem item, int style) {
		super(item, style);
	}
	
	
	public Point computeSize(int wHint, int hHint) {
		Point size = new Point(0,0);
		if((getStyle() & SWT.SIMPLE) != 0) {
			size = titleArea.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		} else {
			Image image = getImage();
			Rectangle iBounds = (image != null && !image.isDisposed()) ? image.getBounds() : new Rectangle(0,0,1,1);
			
			String text = getText();
			GC gc = CTableTree.staticGC;
			Point tSize = (text.length() > 0) ? gc.textExtent(text) : new Point(0,0);
			
			size.x = 	marginLeft +
			marginWidth +
			iBounds.width +
			((iBounds.width > 0 && tSize.x > 0) ? horizontalSpacing : 0) +
			tSize.x +
			marginWidth +
			marginRight;
			
			size.y = (item.useFixedTitleHeight) ? item.getFixedTitleHeight() :
				marginTop +
				marginHeight +
				Math.max(iBounds.height, tSize.y) +
				marginHeight +
				marginBottom;
		}
		
		if(open && childArea != null) {
			Point childSize = childArea.computeSize(SWT.DEFAULT,SWT.DEFAULT);
			size.x = marginLeft+marginWidth+toggleWidth+Math.max(size.x, childSize.x)+marginWidth+marginRight;
			size.y += (childSpacing + childSize.y + childSpacing);
		}
		
		if(wHint != SWT.DEFAULT) {
			size.x = Math.min(size.x, wHint);
		}
		if(hHint != SWT.DEFAULT) {
			size.y = Math.min(size.y, hHint);
		}
		
		return size;
	}
	
	public int computeTitleHeight(int hHint) {
		if((getStyle() & SWT.SIMPLE) != 0) {
			titleHeight = titleArea.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		} else {
			int height = 0;
			for(int i = 0; i < iBounds.length; i++) {
				height = Math.max(height, iBounds[i].height);
			}
			String text = getText();
			if(text != null) {
				GC gc = CTableTree.staticGC;
				height = Math.max(height, gc.textExtent(text).y);
			}
			titleHeight = marginTop + marginHeight + height + marginHeight + marginBottom;
		}
		if(hHint != SWT.DEFAULT) {
			titleHeight = Math.min(titleHeight, hHint);
		}
		return titleHeight;
	}
	
	public Image getImage() {
		if(images.length > 0) return images[0];
		return null;
	}
	
	public Image[] getImages() {
		return images;
	}
	
	public String getText() {
		return (text == null) ? "" : text;
	}
	
	protected void layout() {
		if(item.useFixedTitleHeight) titleHeight = item.getFixedTitleHeight();
		
		toggleBounds.width = Math.min(bounds.width, toggleWidth);
		toggleBounds.height = Math.min(bounds.height, titleHeight);
		
		iBounds = new Rectangle[images.length];
		for(int i = 0; i < iBounds.length; i++) {
			iBounds[i] = (images[i] != null && !images[i].isDisposed()) ? images[i].getBounds() : new Rectangle(0,0,1,1);
		}
		String text = getText();
		GC gc = CTableTree.staticGC;
		Point tSize = (text.length() > 0) ? gc.textExtent(text, SWT.DRAW_DELIMITER | SWT.DRAW_TAB | SWT.DRAW_TRANSPARENT) : new Point(0,0);
//		gc.dispose();
		tBounds.width = tSize.x;
		tBounds.height = tSize.y;
		
		int x = bounds.x, y = bounds.y, width = 0, height = 0;
		
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
		if((horizontalAlignment & SWT.CENTER) != 0) {
			x += ((bounds.width-(indent+marginLeft+marginWidth+width+marginWidth+marginRight))/2);
		} else if((horizontalAlignment & SWT.RIGHT) != 0) {
			x += (bounds.width-(width+marginWidth+marginRight));
		} else { // defaults to LEFT
			x += (indent+marginLeft+marginWidth);
		}

//		int toggleClippingWidth = -1;
		if(ghostToggle || toggleVisible) {
			toggleBounds.x = x - 1;
			x += toggleBounds.width;
//			if(toggleVisible && (x-bounds.x) > bounds.width) {
//				toggleClippingWidth = Math.max(0, bounds.width-(toggleBounds.x-bounds.x));
//			}
		}
		for(int i = 0; i < iBounds.length; i++) {
			iBounds[i].x = x;
			x += iBounds[i].width;
			if((i != iBounds.length-1) || (text.length() > 0)) x += horizontalSpacing;
		}
		tBounds.x = x;
		
		if((open && childArea != null) || (verticalAlignment & SWT.TOP) != 0) {
			toggleBounds.y = y; // toggle centers itself, so don't add margins to it
			y += marginTop+marginHeight;
			for(int i = 0; i < iBounds.length; i++) {
				iBounds[i].y = y;
			}
			tBounds.y = y;
		} else if((verticalAlignment & SWT.BOTTOM) != 0) {
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
		if(open && childArea != null) {
			childArea.setBounds(
					bounds.x+marginWidth+toggleBounds.width,
					bounds.y+titleHeight+childSpacing,
					bounds.width-(marginWidth+toggleBounds.width+rightChildIndent),
					bounds.height-(titleHeight+childSpacing+childSpacing)
			);
		} else if(childArea != null) {
			childArea.setBounds(0, 0, 0, 0);
		}
		
		needsLayout = false;
	}
	
	public void paint(GC gc, Rectangle eventBounds) {
		if((getStyle() & SWT.SIMPLE) != 0) return;
		if((bounds.width <= 0) || bounds.height <= 0) return;
		
		if(needsLayout) {
			layout();
		}
		
		if(background != null) gc.setBackground(background);
		if(foreground != null) gc.setForeground(foreground);

		// background
		gc.fillRectangle(
				bounds.x-eventBounds.x,
				bounds.y-eventBounds.y,
				bounds.width,
				open ? titleHeight : bounds.height
		);

		// images
		for(int i = 0; i < iBounds.length; i++) {
			if(!images[i].isDisposed()) {
				gc.drawImage(images[i], iBounds[i].x-eventBounds.x, iBounds[i].y-eventBounds.y);
			}
		}

		// text
		if(getText().length() > 0) {
			gc.drawText(getText(), tBounds.x-eventBounds.x, tBounds.y-eventBounds.y);
		}

		// toggle (it changes the colors again so paint it last...)
		if(toggleVisible) {
			paintToggle(gc, eventBounds);
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
		container.paint(getItem());
	}
	
	public void setText(String string) {
		if(string != null && !string.equals(getText())) {
			text = string;
			update = true;
			container.paint(getItem());
		}
	}
	
}
