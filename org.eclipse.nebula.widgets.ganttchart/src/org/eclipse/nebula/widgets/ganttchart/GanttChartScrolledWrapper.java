/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/ 

package org.eclipse.nebula.widgets.ganttchart;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

/**
 * The GanttChartScrolledWrapper is a middle-man between the GanttComposite and the GanttChart. All scrollbar magic is handled by this class so that the GanttComposite
 * does not have to worry about those things, the only thing it needs to do is to return the correct size when the wrapper asks for it. 
 *
 * This class may NOT be subclassed. 
 */
final class GanttChartScrolledWrapper extends ScrolledComposite {
	
	private GanttComposite mGc;
	
	public GanttChartScrolledWrapper(Composite parent, int style, ISettings settings, IColorManager colorManager, IPaintManager paintManager, ILanguageManager languageManager) {
		super(parent, style | SWT.V_SCROLL);
		setExpandHorizontal(true);
		setExpandVertical(true);
		getVerticalBar().setIncrement(15);
		getVerticalBar().setPageIncrement(160);

		mGc = new GanttComposite(this, SWT.NONE, settings, colorManager, paintManager, languageManager);
		
		addControlListener(new ControlListener() {

			public void controlMoved(ControlEvent e) {
			}

			public void controlResized(ControlEvent e) {
				forceUpdate();
			}
			
		});
		
		setContent(mGc);
		
		// force a scrollbar update post-widget creation
		parent.getDisplay().asyncExec(new Runnable() {
			public void run() {
				forceUpdate();
			}			
		});
	}
		
	public void scrollingLeft(int diff) {
		mGc.scrollingLeft(diff);
	}
	
	public void scrollingRight(int diff) {
		mGc.scrollingRight(diff);
	}
	
	public GanttComposite getGanttComposite() {
		return mGc;
	}
		
	public void forceUpdate() {
		Rectangle bounds = mGc.getBounds();
		setMinSize(SWT.DEFAULT, bounds.height);
	}

	protected void eventNumberModified() {
		forceUpdate();
	}

}
