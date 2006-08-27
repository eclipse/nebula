package org.eclipse.swt.nebula.snippets.ctabletree;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.nebula.widgets.ctabletree.CTableTreeCell;
import org.eclipse.swt.nebula.widgets.ctabletree.ccontainer.CContainerItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * A Sample custom cell with no styles set (a "base" cell).
 * Everything is drawn custom and the toggle's visibility 
 * is set on and off dynamically.
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
	
	protected List getColorManagedControls() {
		List list = super.getColorManagedControls();
		list.remove(text);
		return list;
	}
	
	protected List getEventManagedControls() {
		List list = super.getEventManagedControls();
		list.remove(text);
		return list;
	}
	
	public void setBounds(Rectangle bounds) {
		super.setBounds(bounds);
		if(!open) {
			boolean needsToggle = text.computeSize(getClientArea().width, SWT.DEFAULT).y > getClientArea().height;
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
