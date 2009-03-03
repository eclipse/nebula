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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class VTracker implements DisposeListener {

	private static VTracker tracker;

	static void addTopLevelPanel(VPanel panel) {
		tracker = instance();
		tracker.panels.put(panel.composite, panel);
		if(!tracker.listening) {
			tracker.listening = true;
			Display.getDefault().addFilter(SWT.FocusIn, tracker.filter);
			Display.getDefault().addFilter(SWT.MouseMove, tracker.filter);
			Display.getDefault().addFilter(SWT.MouseDown, tracker.filter);
			Display.getDefault().addFilter(SWT.MouseUp, tracker.filter);
			Display.getDefault().addFilter(SWT.Traverse, tracker.filter);
		}
		panel.composite.addDisposeListener(tracker);
	}
	
	public static void deactivate() {
		instance().deactivate(getActiveControl());
	}

	public static int getLastTraverse() {
		return instance().lastTraverse;
	}
	
	public static int getMouseDownButton() {
		return instance().mouseButton;
	}
	
	public static Point getMouseDownLocation() {
		return instance().mouseDown;
	}
	
	public static boolean isMouseDown() {
		return instance().mouseDown != null;
	}
	
	public static VControl getFocusControl() {
		return instance().focusControl;
	}
	
	public static VControl getActiveControl() {
		return instance().activeControl;
	}

	private static boolean setFocusFromPrev(Control control) {
		Control c = null;
		Composite parent = control.getParent();
		if(parent == null) {
			c = control;
		} else {
			Control[] ca = parent.getTabList();
			for(int i = 0; i < ca.length; i++) {
				if(ca[i] == control) {
					if(i == ca.length-1) {
						c = ca[0];
					} else {
						c = ca[i+1];
					}
					break;
				}
			}
		}
		if(control != null) {
			for(VControl vc : instance().panels.values()) {
				Composite widget = vc.getWidget();
				if(widget == c) {
					return widget.setFocus();
				}
			}
		}
		return false;
	}
	
	private static boolean setFocusFromNext(Control control) {
		Control c = null;
		Composite parent = control.getParent();
		if(parent == null) {
			c = control;
		} else {
			Control[] ca = parent.getTabList();
			for(int i = 0; i < ca.length; i++) {
				if(ca[i] == control) {
					if(i == 0) {
						c = ca[ca.length-1];
					} else {
						c = ca[i-1];
					}
					break;
				}
			}
		}
		if(c != null) {
			for(VControl vc : instance().panels.values()) {
				Composite widget = vc.getWidget();
				if(widget == c) {
					return widget.setFocus();
				}
			}
		}
		return false;
	}
	
	private static void setFocusToNext(Composite comp) {
		if(comp != null) {
			Composite parent = comp.getParent();
			Control[] controls = parent.getTabList();
			if(parent instanceof Shell) {
				for(int i = 0; i < controls.length; i++) {
					if(controls[i] == comp) {
						for(int j = 0; j < controls.length; j++) {
							i++;
							if(i > controls.length-1) {
								i = 0;
							}
							if(controls[i].setFocus()) {
								return;
							}
						}
					}
				}
			} else {
				for(int i = 0; i < controls.length; i++) {
					if(controls[i] == comp) {
						for( ; i < controls.length-1; i++) {
							if(controls[i+1].setFocus()) {
								return;
							}
						}
						setFocusToNext(comp.getParent());
					}
				}
			}
		}
	}
	
	private static void setFocusToPrev(Composite comp) {
		if(comp != null) {
			Composite parent = comp.getParent();
			Control[] controls = parent.getTabList();
			if(parent instanceof Shell) {
				for(int i = 0; i < controls.length; i++) {
					if(controls[i] == comp) {
						for(int j = 0; j < controls.length; j++) {
							i--;
							if(i < 0) {
								i = controls.length-1;
							}
							if(controls[i].setFocus()) {
								return;
							}
						}
					}
				}
			} else {
				for(int i = 0; i < controls.length; i++) {
					if(controls[i] == comp) {
						for( ; i > 0; i--) {
							if(controls[i-1].setFocus()) {
								return;
							}
						}
						setFocusToPrev(comp.getParent());
					}
				}
			}
		}
	}

	static VTracker instance() {
		if(tracker == null) {
			tracker = new VTracker();
		}
		return tracker;
	}

	private Map<Composite, VPanel> panels;

	private VControl activeControl = null;
	
	private Listener filter = new Listener() {
		public void handleEvent(Event event) {
			switch (event.type){
			case SWT.Traverse:
				lastTraverse = event.detail;
				if(SWT.TRAVERSE_TAB_NEXT == event.detail || SWT.TRAVERSE_TAB_PREVIOUS == event.detail) {
					event.doit = true;
					if(focusControl != null) {
						focusControl.handleEvent(event);
						if(event.doit) {
							Composite comp = focusControl.getWidget();
							if(SWT.TRAVERSE_TAB_NEXT == event.detail) {
								setFocusToNext(comp);
							} else {
								setFocusToPrev(comp);
							}
						}
					} else if(event.widget instanceof Control) {
						System.out.println(event.doit);
						if(SWT.TRAVERSE_TAB_NEXT == event.detail) {
							if(setFocusFromPrev((Control) event.widget)){
								event.type = SWT.None;
								event.doit = false;
							}
						} else {
							if(setFocusFromNext((Control) event.widget)){
								event.type = SWT.None;
								event.doit = false;
							}
						}
					}
				}
				break;
			case SWT.FocusIn:
				if(event.widget instanceof Shell) {
					Control[] ca = ((Shell) event.widget).getTabList();
					if(ca.length > 0 && ca[0] instanceof VWidget) {
						setFocusControl(((VWidget) ca[0]).getPanel());
					}
				}
				if(focusControl == null) {
					if(event.widget instanceof VWidget) {
						setFocusControl(((VWidget) event.widget).getPanel());
					}
				} else if(!focusControl.isSameWidgetAs(event.widget)) {
					setFocusControl(null);
				}
				break;
			case SWT.MouseDown:
				mouseButton = event.button;
				mouseDown = new Point(event.x, event.y);
				if(activeControl != null && activeControl.setState(VControl.STATE_MOUSE_DOWN, true)) {
					activeControl.redraw();
				}
				break;
			case SWT.MouseMove:
				if(panels.containsKey(event.widget)) {
					VControl vcontrol = panels.get(event.widget).getControl(event.x, event.y, true);
					if(vcontrol != activeControl && (vcontrol == null || vcontrol.isEnabled())) {
						activate(vcontrol);
					}
				} else if(activeControl != null && event.widget != activeControl.getControl()) {
					activeControl.deactivate();
					activeControl = null;
				}
				break;
			case SWT.MouseUp:
				mouseButton = -1;
				mouseDown = null;
				if(activeControl != null && activeControl.setState(VControl.STATE_MOUSE_DOWN, false)) {
					activeControl.redraw();
				}
				break;
			}
		}
	};

	private VControl focusControl = null;

	private boolean listening = false;
	
	private int mouseButton = -1;
	
	private Point mouseDown = null;
	
	private int lastTraverse = -1;
	
	private VTracker() {
		panels = new HashMap<Composite, VPanel>();
	}

	void activate(VControl vcontrol) {
		if(activeControl != null && !activeControl.isDisposed()) {
			activeControl.deactivate();
		}
		activeControl = vcontrol;
		if(activeControl != null) {
			activeControl.activate();
		}
	}
	
	void deactivate(VControl vcontrol) {
		if(vcontrol != null) {
			if(!vcontrol.isDisposed()) {
				vcontrol.deactivate();
			}
			if(activeControl == vcontrol) {
				activeControl = null;
			}
		}
	}

	boolean setFocusControl(VControl control) {
		if(control == focusControl) {
			return true;
		}
		if(control instanceof VPanel) {
			return control.setFocus(true);
		} else {
			VControl old = focusControl;
			if(old != null && !old.isDisposed()) {
				old.setFocus(false);
			}
			if(control != null) {
				if(!control.isDisposed() && control.setFocus(true)) {
					control.getControl().forceFocus();
				} else {
					return false;
				}
			}
			focusControl = control;
			if(control != null) {
				control.redraw();
			}
			if(old != null && !old.isDisposed()) {
				old.redraw();
			}
			return true;
		}
	}
	
	public void widgetDisposed(DisposeEvent e) {
		instance().panels.remove(e.widget);
		if(instance().panels.isEmpty()) {
			Display.getDefault().removeFilter(SWT.FocusIn, tracker.filter);
			Display.getDefault().removeFilter(SWT.MouseMove, tracker.filter);
			Display.getDefault().removeFilter(SWT.MouseDown, tracker.filter);
			Display.getDefault().removeFilter(SWT.MouseUp, tracker.filter);
			Display.getDefault().removeFilter(SWT.Traverse, tracker.filter);
			tracker.listening = false;
			if(activeControl != null) {
				Control control = activeControl.getControl();
				if(control != null && !control.isDisposed()) {
					control.dispose();
				}
			}
		}
	}

}
