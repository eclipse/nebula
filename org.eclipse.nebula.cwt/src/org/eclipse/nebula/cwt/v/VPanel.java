/****************************************************************************
* Copyright (c) 2008, 2009 Jeremy Dowdall
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
*****************************************************************************/

package org.eclipse.nebula.cwt.v;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;

public class VPanel extends VControl {

	Composite widget;
	List<VControl> children = new ArrayList<VControl>();
	private VLayout layout = null;
	private boolean isTopLevel = false;
	private Listener topLevelListener;
	
	public VPanel(Composite parent, int style) {
		this((VPanel) null, style & ~SWT.BORDER);

		isTopLevel = true;
		
		composite = parent;
		
		topLevelListener = new Listener() {
			public void handleEvent(Event event) {
				switch(event.type) {
				case SWT.Dispose:
					dispose(false);
					break;
//				case SWT.FocusIn:
//					setFocus();
//					break;
				case SWT.Paint:
					paintControl(event);
					break;
				}
			}
		};
		
		composite.addListener(SWT.Dispose, topLevelListener);
		composite.addListener(SWT.FocusIn, topLevelListener);
		composite.addListener(SWT.Paint, topLevelListener);

		composite.setLayout(new Layout() {
			protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
				return VPanel.this.computeSize(wHint, hHint, flushCache);
			}
			protected void layout(Composite composite, boolean flushCache) {
				VPanel.this.setBounds(composite.getClientArea());
				VPanel.this.layout(flushCache);
			}
		});

		VTracker.addTopLevelPanel(this);
		composite.setData("cwt_vcontrol", this);
	}

	public VPanel(VPanel panel, int style) {
		super(panel, style);
		marginTop = marginBottom = marginLeft = marginRight = 0;
		setLayout(new VGridLayout());
		setPainter(new VPanelPainter());
	}

	void addChild(VControl child) {
		if(!children.contains(child)) {
			children.add(child);
		}
	}
	
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return layout.computeSize(this, wHint, hHint, changed);
	}

	@Override
	public void dispose() {
		dispose(true);
	}
	
	/**
	 * If the dispose request comes from the Composite via the topLevelListener,
	 * then do not dispose the Composite again - controls recieving the
	 * Composite's dispose event after this VPanel will be in danger of dealing 
	 * with a disposed control before they are ready.
	 * @param disposeComposite
	 */
	private void dispose(boolean disposeComposite) {
		if(isTopLevel) {
			if(composite != null && !composite.isDisposed()) {
				composite.removeListener(SWT.Dispose, topLevelListener);
				composite.removeListener(SWT.FocusIn, topLevelListener);
				composite.removeListener(SWT.Paint, topLevelListener);
			}
		}
		for(VControl child : children.toArray(new VControl[children.size()])) {
			child.dispose();
		}
		super.dispose();
		if(isTopLevel && disposeComposite) {
			if(composite != null && !composite.isDisposed()) {
				composite.dispose();
			}
		}
	}
	
	public int getBorderWidth() {
		if(isTopLevel) {
			return composite.getBorderWidth();
		} else {
			return 1;
		}
	}
	
	public VControl[] getChildren() {
		return children.toArray(new VControl[children.size()]);
	}

	public VControl getControl(int x, int y) {
		return getControl(x, y, false);
	}

	public VControl getControl(int x, int y, boolean includePanels) {
		if(bounds.contains(x, y)) {
			for(ListIterator<VControl> iter = children.listIterator(children.size()); iter.hasPrevious(); ) {
				VControl child = iter.previous();
				if(child.getVisible() && child.getBounds().contains(x, y)) {
					if(includePanels && child instanceof VPanel) {
						return ((VPanel) child).getControl(x, y, true);
					} else {
						return child;
					}
				}
			}
			return this;
		}
		return null;
	}
	
	public VLayout getLayout() {
		return layout;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends VLayout> T getLayout(Class<T> clazz) {
		return (T) layout;
	}
	
	@Override
	public Type getType() {
		return VControl.Type.Panel;
	}
	
	@Override
	public Composite getWidget() {
		if(widget != null) {
			return widget;
		}
		if(parent != null) {
			return parent.getWidget();
		}
		return composite;
	}
	
	public void layout() {
		layout(true);
	}
	
	public void layout(boolean changed) {
		layout.layout(this, changed);
		redraw();
	}

	void move(VControl above, VControl below) {
		if(above == null) {
			children.remove(below);
			children.add(below);
		} else if(below == null) {
			children.remove(above);
			children.add(0, above);
		} else {
			int ix = children.indexOf(below);
			children.remove(above);
			children.add(ix, above);
		}
	}
	
	
	
	@Override
	protected boolean redrawOnActivate() {
		return false;
	}
	
	@Override
	protected boolean redrawOnDeactivate() {
		return false;
	}
	
	void removeChild(VControl child) {
		children.remove(child);
	}

	void removeVChild(VControl vchild) {
		children.remove(vchild);
	}

	public void setActivatable(boolean activatable) {
		super.setActivatable(activatable);
		for(VControl child : children) {
			child.setActivatable(activatable);
		}
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		layout();
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		for(VControl child : children) {
			child.setEnabled(enabled);
		}
	}
	
	@Override
	protected boolean setFocus(boolean focus) {
		if(focus) {
			for(VControl child : children) {
				if(VTracker.instance().setFocusControl(child)) {
					return true;
				}
			}
			return false;
		} else {
			for(VControl child : children) {
				child.setFocus(false);
			}
			return true;
		}
	}
	
	public void setLayout(VLayout layout) {
		this.layout = layout;
	}
	
	@Override
	public void setLayoutData(GridData data) {
		if(isTopLevel) {
			composite.setLayoutData(data);
		} else {
			super.setLayoutData(data);
		}
	}

	@Override
	public void setSize(Point size) {
		super.setSize(size);
		layout();
	}

	@Override
	public void setVisibility(int visibility) {
		for(VControl child : children) {
			child.setVisibility(visibility);
		}
		super.setVisibility(visibility);
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		for(VControl child : children) {
			if(child instanceof VNative) {
				child.setVisible(visible);
			}
		}
	}
	
	public void setWidget(Composite widget) {
		this.widget = widget;
		this.widget.addListener(SWT.KeyDown, new Listener() {
			public void handleEvent(Event event) {
			}
		});
//		this.widget.addListener(SWT.FocusIn, new Listener() {
//			public void handleEvent(Event event) {
//				VPanel.this.getWidget().setFocus();
//			}
//		});
	}

	public void sort(Comparator<VControl> comparator) {
		Collections.sort(children, comparator);
	}
	
}
