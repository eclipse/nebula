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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Slider;

/**
 * <b>GanttChart - SWT Widget - 2005-2007. Version 1.0 &copy; Emil Crumhorn - emil.crumhorn@gmail.com.</b>
 * <p>
 * <b>Website</b><br>
 * If you want more info or more documentation, please visit: <a href="http://www.hexapixel.com/">http://www.hexapixel.com</a>
 * <p>
 * <b>Description</b><br>
 * The GANTT widget has taken hints from Microsoft Project as far as arrow styles and overall look and feel goes. 
 * There are features such as dependencies, checkpoints, revised dates, and much more. Nearly everything is customizable 
 * and you can zoom in to detailed day views all the way out to yearly overviews (12 zoom levels). Events can be resized, 
 * dragged and dropped and various other things.
 * <p>
 * The widget is extremely simple to use for those wishing a basic implementation, or you can customize everything down to 
 * the pixel level if you so wish. Basically, if you don't like something, change it!
 * <p>
 * Example creation code:<p>
 * <code>
 * GanttChart ganttChart = new GanttChart(parentComposite, SWT.NONE);
 * <p>
 * Calendar calStart = Calendar.getInstance();<br>
 * Calendar calEnd = Calendar.getInstance();<br>
 * calEnd.add(Calendar.DATE, 10);<br>
 * // set the data object to null, and percentage complete to 50<br>
 * GanttEvent event = new GanttEvent(ganttChart, null, "Project X", calStart, calEnd, 50);<br>
 * </code>
 *<p>
 * <b>Customizing</b><br>
 * As you may wish to customize the widget beyond the capabilities it already has, there are a few ways you may basically take control over as much or little
 * as you please. First, there are three interfaces that are of importance, one is the IPaintManager, the IColorManager and the ISettings. Let's start
 * with the IColorManager.
 * <p>
 * <b>IColorManager</b><br>
 * If you don't specify a color manager, the DefaultColorManager will be used. The color manager's job is to return colors to the method that is painting everything
 * that is visual in the chart. The colors that are returned from the ColorManager will determine everything as far as looks go. 
 *<p>
 * <b>IPaintManager</b><br>
 * This interface is one you want to use if you want to dig really deep into how things are drawn. This class by default controls exactly how an 
 * event is represented visually, pixel by pixel. If you don't like the look of a certain object on the chart, this is the interface you will want to 
 * implement.
 * <p>
 * <b>ISettings</b><br>
 * This interface is probably the most likely that you will be implementing on your own. Mainly this interface controls pixel values (widths, heights, 
 * multipliers) and various boolean flags (if events can be moved, resized, etc). All in all there are about 30-40 methods.
 *  
 * @author Emil Crumhorn <a href="mailto:emil.crumhorn@gmail.com">emil.crumhorn@gmail.com</a>
 * @version 1.0
 *
 */
public class GanttChart extends Composite {


	private GanttChartScrolledWrapper mGc;
	private Slider mSlider;
	private Composite mSliderComp;
	private int mScrollPosition;
	private int mMinScrollRange = 0;
	private int mMaxScrollRange = 500;
	private int mCenter = mMaxScrollRange / 2;
	private SelectionListener mSelectionListener;
	private ISettings mSettings;
	private IColorManager mColorManager;
	private IPaintManager mPaintManager;
	private GanttComposite mGanttComposite;
	
	/**
	 * Constructs a new Gantt chart widget.
	 * 
	 * @param parent Parent composite
	 * @param style Widget style
	 */
	public GanttChart(Composite parent, int style) {
		super(parent, checkStyle(style));
		init();
	}

	/**
	 * Constructs a new Gantt chart widget with custom settings.
	 *  
	 * @param parent Parent composite
	 * @param style Widget style
	 * @param settings ISettings implementation
	 */
	public GanttChart(Composite parent, int style, ISettings settings) {
		super(parent, checkStyle(style));
		mSettings = settings;
		init();
	}
	
	/**
	 * Constructs a new Gantt chart widget with custom settings and a custom color manager.
	 * 
	 * @param parent Parent composite
	 * @param style Widget style
	 * @param settings ISettings implementation
	 * @param colorManager IColorManager implementation
	 */
	public GanttChart(Composite parent, int style, ISettings settings, IColorManager colorManager) {
		super(parent, checkStyle(style));
		mSettings = settings;
		mColorManager = colorManager;
		init();
	}

	/**
	 * Constructs a new Gantt chart widget with custom settings, custom color manager and a custom paint manager.
	 * 
	 * @param parent Parent composite
	 * @param style Widget style
	 * @param settings ISettings implementation
	 * @param colorManager IColorManager implementation
	 * @param paintManager IPaintManager implementation
	 */
	public GanttChart(Composite parent, int style, ISettings settings, IColorManager colorManager, IPaintManager paintManager) {
		super(parent, checkStyle(style));
		mSettings = settings;
		mColorManager = colorManager;
		mPaintManager = paintManager;
		init();
	}

	private static int checkStyle(int style) {
		int mask = SWT.BORDER | SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT | SWT.H_SCROLL | SWT.V_SCROLL
        | SWT.SINGLE | SWT.MULTI | SWT.NO_FOCUS | SWT.CHECK | SWT.VIRTUAL;
        int newStyle = style & mask;
		return newStyle;
	}
	
	/**
	 * Adds a GanttGroup. A Gantt Group represents a collection of events that should all draw on the same line.
	 * 
	 * @param group GanttGroup to add
	 */
	public void addGroup(GanttGroup group) {
		mGanttComposite.addGroup(group);
	}
	
	/**
	 * Removes a GanttGroup.
	 * 
	 * @param group GanttGroup to remove
	 */
	public void removeGroup(GanttGroup group) {
		mGanttComposite.removeGroup(group);
	}
	
	private void init() {
		GridLayout gl = new GridLayout(1, true);
		gl.marginBottom = 0;
		gl.marginHeight = 0;
		gl.marginLeft = 0;
		gl.marginRight = 0;
		gl.marginTop = 0;
		gl.marginWidth = 0;
		gl.verticalSpacing = 0;
		gl.horizontalSpacing = 0;
		setLayout(gl);
		
		if (mSettings == null)
			mSettings = new DefaultSettings();
		
		if (mColorManager == null)
			mColorManager = new DefaultColorManager();

		if (mPaintManager == null)
			mPaintManager = new DefaultPaintManager();
		
		mGc = new GanttChartScrolledWrapper(this, SWT.NONE, mSettings, mColorManager, mPaintManager);
		mGanttComposite = mGc.getGanttComposite();
		mGc.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		mSliderComp = new Composite(this, SWT.NONE);
		mSliderComp.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_END | GridData.FILL_HORIZONTAL));
		
		// outer bottom composite layout
		GridLayout gl2 = new GridLayout(2, false);
		gl2.marginBottom = 0;
		gl2.marginHeight = 0;
		gl2.marginLeft = 0;
		gl2.marginRight = 0;
		gl2.marginTop = 0;
		gl2.marginWidth = 0;
		gl2.verticalSpacing = 0;
		gl2.horizontalSpacing = 0;		
		mSliderComp.setLayout(gl2);
		
		// inner bottom layout
		GridLayout gl3 = new GridLayout(2, false);
		gl3.marginBottom = 0;
		gl3.marginHeight = 0;
		gl3.marginLeft = 0;
		gl3.marginRight = 0;
		gl3.marginTop = 0;
		gl3.marginWidth = 0;
		gl3.verticalSpacing = 0;
		gl3.horizontalSpacing = 0;		
		
		mSlider = new Slider(mSliderComp, SWT.NONE);
		mSlider.setMaximum(mMaxScrollRange);
		mSlider.setSelection(mCenter);
		mSlider.setThumb(20);
		mSlider.setIncrement(1);
		mSlider.setPageIncrement(10);
		mSelectionListener = new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				scrolledHorizontally(e.detail);
			}			
		};		
		mSlider.addSelectionListener(mSelectionListener);
		
		Composite filler = new Composite(mSliderComp, SWT.NONE);
		filler.setLayout(gl3);
		Composite insideFiller = new Composite(filler, SWT.NONE);
		Point scSize = mGc.getVerticalBar().getSize();
		insideFiller.setLayoutData(new GridData(scSize.x, scSize.x));
		
		mSlider.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		filler.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		mScrollPosition = mSlider.getSelection();		
	}
		
	/**
	 * Adds a dependency between two events. 
	 * 
	 * @param source GanttEvent source
	 * @param target GanttEvent target
	 */
	public void addConnection(GanttEvent source, GanttEvent target) {
		checkWidget();
		mGanttComposite.addDependency(source, target);
	}
	
	/**
	 * Same as addConnection().
	 * 
	 * @param source GanttEvent source
	 * @param target GanttEvent target
	 */
	public void addDependency(GanttEvent source, GanttEvent target) {
		checkWidget();
		addConnection(source, target);
	}

	/**
	 * Adds an event listener.
	 * 
	 * @param listener IGanttEventListener
	 */
	public void addGanttEventListener(IGanttEventListener listener) {
		checkWidget();
		mGanttComposite.addGanttEventListener(listener);
	}
	
	/**
	 * Removes and event listener.
	 * 
	 * @param listener IGanttEventListener
	 */
	public void removeGanttEventListener(IGanttEventListener listener) {
		checkWidget();
		mGanttComposite.removeGanttEventListener(listener);
	}
	
	/**
	 * Clears all events from the chart.
	 */
	public void clearEvents() {
		checkWidget();
		mGanttComposite.clearEvents();
	}
	
	/**
	 * Returns the widget that is the actual GANTT widget at the root level.
	 *  
	 * @return GanttComposite
	 */
	public GanttComposite getGanttComposite() {
		checkWidget();
		return mGanttComposite;
	}	

	/**
	 * Redraws the GanttChart. Call this if an update to the chart has been made that did not cause an automatic redrawing of the chart.
	 * 
	 */
	public void redrawGanttChart() {
		mGanttComposite.redraw();
	}
	
	/**
	 * Re-indexes an event to show at a new location.
	 * 
	 * @param ge GanttEvent to re-index
	 * @param newIndex new index position
	 */
	public void reindex(GanttEvent ge, int newIndex) {
		mGanttComposite.reindex(ge, newIndex);
	}
	
	private synchronized void scrolledHorizontally(int detail) {
		if (detail == 0) {
			mScrollPosition = mCenter;
			mSlider.setSelection(mCenter);
			return;
		}
		
		int cur = mSlider.getSelection();
		int thumb = mSlider.getThumb();

		int diff = cur - mScrollPosition;
		if (diff < 0) {
			diff = mScrollPosition - cur;
		}

		// far right or far left
		if (diff == 0 && (mScrollPosition == mMinScrollRange || (mScrollPosition + thumb) == mMaxScrollRange)) {
			// far right
			if (mScrollPosition > mMinScrollRange) {
				mGc.scrollingRight(diff);
			}
			else {
				mGc.scrollingLeft(diff);
			}
		}
		else {
			// dragged and dropped thumb, we don't want to move any dates
			if (diff == 0) {
				return;
			}

			if (cur > mScrollPosition) {
				mGc.scrollingRight(diff);
			}
			else {
				mGc.scrollingLeft(diff);
			}
		}

		mScrollPosition = mSlider.getSelection();
		if (detail != 1) {
			mScrollPosition = mCenter;
			mSlider.setSelection(mCenter);
		}
		
	}}
