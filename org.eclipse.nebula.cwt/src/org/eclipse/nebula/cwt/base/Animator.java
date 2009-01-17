/****************************************************************************
* Copyright (c) 2007-2009 Jeremy Dowdall
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
*****************************************************************************/

package org.eclipse.nebula.cwt.base;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

class Animator extends Thread {

	/**
	 * Style constant for an animation that will show and open the 
	 * control (value is 1&lt;&lt;1).
	 */
	public static final int OPEN	= 1 << 0;
	
	/**
	 * Style constant for an animation that will close and hide the 
	 * control (value is 1&lt;&lt;1).
	 */
	public static final int CLOSE	= 1 << 1;
	
	/**
	 * Style constant for an animation which will occur in an upwards 
	 * fashion (value is 1&lt;&lt;2).
	 */
	public static final int UP		= 1 << 2;
	
	/**
	 * Style constant for an animation which will occur in an downwards 
	 * fashion (value is 1&lt;&lt;3).
	 */
	public static final int DOWN	= 1 << 3;
	
	/**
	 * Style constant for a 'blind' style animation (value is 1&lt;&lt;2).
	 * Used in conjunction with a direction (UP or DOWN)
	 * and either an OPEN or CLOSE function. 
	 */
	public static final int BLIND	= 1 << 4;

	/**
	 * Style constant for a 'slide' style animation (value is 1&lt;&lt;2).
	 * Used in conjunction with a direction (UP or DOWN)
	 * and either an OPEN or CLOSE function. 
	 */
	public static final int SLIDE	= 1 << 5;

    /**
     * Style constant for a 'pop' style animation, which really, is not an
     * animation at all, but is included for convenience purposes
     * (value is 1&lt;&lt;2). Used in conjunction with a direction
     * (UP or DOWN) and either an OPEN or CLOSE function. 
     */
    public static final int POP     = 1 << 6;

    /**
	 * sets the default style for a given type
	 * @param type
	 * @return
	 */
	private static int type(int type) {
		if((type & UP) == 0) type |= DOWN;
		if((type & CLOSE) == 0) type |= OPEN;
		return type;
	}

	Composite controlParent;
	Control control;
	int type;
	int traveled = 0;
	double stepSize;
	int distance;
	int duration = 100;

	int interval = 10;

	private Runnable afterFinish;
	private Runnable afterInit;
	
	/**
	 * @param control
	 * @param type
	 */
	public Animator(Control control, int type) {
		this.controlParent = control.getParent();
		this.control = control;
		this.type = type(type);
		if(hasStyle(BLIND) || hasStyle(SLIDE)) {
		    distance = control.getSize().y - 1;
			stepSize = (((double) distance * (double) interval) / duration);
		} else if(hasStyle(POP)) {
            stepSize = distance = control.getSize().y - 1;
        }
	}

	void animate() {
        if(hasStyle(POP)) {
            traveled = distance;
        } else if(hasStyle(SLIDE)) {
        	if(checkWidgets()) {
				int step = (int) (((traveled+stepSize) > distance) ? (distance - traveled) : stepSize);
				if(hasStyle(CLOSE)) step *= -1;
				
				if(hasStyle(UP)) {
					Rectangle bounds = controlParent.getBounds();
					controlParent.setBounds(bounds.x, bounds.y - step, bounds.width, bounds.height + step);
				} else {
					Point size = controlParent.getSize();
					Point location = control.getLocation();
					controlParent.setSize(size.x, size.y + step);
					control.setLocation(location.x, location.y + step);
				}
				traveled += stepSize;
        	}
		} else if(hasStyle(BLIND)) {
			// todo
		}
	}
	private boolean checkWidgets() {
		return control != null && controlParent != null && !control.isDisposed() && !controlParent.isDisposed();
	}
	
	void finish() {
		if(hasStyle(CLOSE) && controlParent.isVisible()) {
        	if(checkWidgets()) {
        		controlParent.setVisible(false);
        	}
		}
	}
	
	private boolean hasStyle(int style) {
		return (type & style) != 0;
	}

	void init() {
    	if(checkWidgets()) {
	        if(isStyle(POP | OPEN | DOWN)) {
	            Point size = controlParent.getSize();
	            controlParent.setSize(new Point(size.x, distance + 1));
	            control.setLocation(0, 0);
	        }
	        if(isStyle(POP | OPEN | UP)) {
	            Rectangle bounds = controlParent.getBounds();
	            controlParent.setBounds(bounds.x, bounds.y, bounds.width, distance + 1);
	            control.setLocation(0, 0);
	        }
			if(isStyle(SLIDE | OPEN | DOWN)) {
				Point size = control.getSize();
				controlParent.setSize(new Point(size.x, 1));
				control.setLocation(new Point(0, -size.y+1));
			}
			if(isStyle(SLIDE | OPEN | UP)) {
				Rectangle bounds = controlParent.getBounds();
				controlParent.setBounds(bounds.x, bounds.y + bounds.height, bounds.width, 0);
			}
			if(hasStyle(OPEN) && !controlParent.isVisible()) {
				controlParent.setVisible(true);
			}
    	}
	}
	
	private boolean isStyle(int style) {
		return type == style;
	}
	
	public void run() {
		Display display = Display.getDefault();
		
		if(display != null && !display.isDisposed()) {
			display.syncExec(new Runnable() {
				public void run() {
					if(checkWidgets()) {
						controlParent.setRedraw(false);
						init();
						if(afterInit != null) {
							afterInit.run();
						}
						controlParent.setRedraw(true);
					}
				}
			});
		}
		
		while(traveled < distance) {
			try {
				sleep(interval);
				if(display != null && !display.isDisposed()) {
					display.syncExec(new Runnable() {
						public void run() {
							if(checkWidgets()) {
								controlParent.setRedraw(false);
								animate();
								controlParent.setRedraw(true);
							} else {
								traveled = distance; // widgets are disposed - exit the loop
							}
						}
					});
				} else {
					traveled = distance; // display is disposed - exit the loop
				}
			} catch (InterruptedException e) {
				traveled = distance; // something bad happened :) - exit the loop
				e.printStackTrace();
			}
		}
		
		if(display != null && !display.isDisposed()) {
			display.syncExec(new Runnable() {
				public void run() {
					if(checkWidgets()) {
						controlParent.setRedraw(false);
						finish();
						if(afterFinish != null) {
							afterFinish.run();
						}
						if(controlParent != null && !controlParent.isDisposed()) {
							controlParent.setRedraw(true);
						}
					}
				}
			});
		}
	}
	
	/**
	 * @param runnable
	 */
	public void setAfterFinish(Runnable runnable) {
		this.afterFinish = runnable;
	}
	
	/**
	 * @param runnable
	 */
	public void setAfterInit(Runnable runnable) {
		this.afterInit = runnable;
	}
	
}
