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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;

/**
 * <b>GanttChart - SWT Widget - 2005-2008. Version 2.0 &copy; Emil Crumhorn - emil dot crumhorn at gmail dot com.</b>
 * <p>
 * <b>Website</b><br>
 * If you want more info or more documentation, please visit: <a href="http://www.hexapixel.com/">http://www.hexapixel.com/</a>
 * <p>
 * <b>Description</b><br>
 * The GANTT widget has taken hints from Microsoft Project as far as arrow styles and overall look and feel goes. There are features such as dependencies, checkpoints, revised
 * dates, and much more. Nearly everything is customizable and you can zoom in to detailed day views all the way out to yearly overviews (12 zoom levels). Events can be resized,
 * dragged and dropped and various other things.
 * <p>
 * The widget is extremely simple to use for those wishing a basic implementation, or you can customize everything down to the pixel level if you so wish. Basically, if you don't
 * like something, change it!
 * <p>
 * Important note: This class exposes a few select methods from the actual GanttComposite (where all the magic happens). If you are looking for a specific method and can't find it
 * here, do a call to getGanttComposite() and see if the method you are looking for is located there.
 * <p>
 * Example creation code:
 * <p>
 * <code>
 * GanttChart ganttChart = new GanttChart(parentComposite, SWT.NONE);
 * <br><br>
 * Calendar calStart = Calendar.getInstance();<br>
 * Calendar calEnd = Calendar.getInstance();<br>
 * calEnd.add(Calendar.DATE, 10);<br>
 * // set the data object to null, and percentage complete to 50<br>
 * GanttEvent event = new GanttEvent(ganttChart, "Project X", calStart, calEnd, 50);<br>
 * </code>
 * <p>
 * <b>Customizing</b><br>
 * As you may wish to customize the widget beyond the capabilities it already has, there are a few ways you may basically take control over as much or little as you please. First,
 * there are four interfaces that are of importance. They are the IPaintManager, the IColorManager, the ISettings and the ILanguageManager. Let's start with the IColorManager.
 * <p>
 * <b>IColorManager</b><br>
 * If you don't specify a color manager, the DefaultColorManager will be used. The color manager's job is to return colors to the method that is painting everything that is visual
 * in the chart. The colors that are returned from the ColorManager will determine everything as far as looks go.
 * <p>
 * <b>IPaintManager</b><br>
 * This interface is one you want to use if you want to dig really deep into how things are drawn. This class by default controls exactly how an event is represented visually,
 * pixel by pixel. If you don't like the look of a certain object on the chart, this is the interface you will want to implement.
 * <p>
 * <b>ISettings</b><br>
 * This interface is probably the most likely that you will be implementing on your own. Mainly this interface controls pixel values (widths, heights, multipliers) and various
 * boolean flags (if events can be moved, resized, etc).
 * <p>
 * <b>ILanguageManager</b><br>
 * Should you wish to use a different language than English, this is the interface to implement where you can override all the English text strings with whatever you wish.
 * 
 * @author Emil Crumhorn <a href="mailto:emil.crumhorn@gmail.com">emil.crumhorn@gmail.com</a>
 * @version 2.0
 * 
 */
public class GanttChart extends Composite implements IGanttFlags {

	private GanttComposite				mGanttComposite;
	private int							mStyle;
	private ISettings					mSettings;
	private IColorManager				mColorManager;
	private IPaintManager				mPaintManager;
	private ILanguageManager			mLanguageManager;

	/**
	 * Constructs a new GANTT chart widget. For styles, please see {@link IGanttFlags}.
	 * 
	 * @param parent Parent composite
	 * @param style Widget style
	 * @throws IllegalArgumentException <ul>
	 *             <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *             </ul>
	 * @throws org.eclipse.swt.SWTException <ul>
	 *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li> <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed
	 *             subclass</li>
	 *             </ul>
	 */
	public GanttChart(Composite parent, int style) {
		this(parent, style, null, null, null, null);
	}

	/**
	 * Constructs a new GANTT chart widget with custom settings {@link ISettings}. For styles, please see {@link IGanttFlags}.
	 * 
	 * @param parent Parent composite
	 * @param style Widget style
	 * @param settings ISettings implementation or null
	 * @throws IllegalArgumentException <ul>
	 *             <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *             </ul>
	 * @throws org.eclipse.swt.SWTException <ul>
	 *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li> <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed
	 *             subclass</li>
	 *             </ul>
	 */
	public GanttChart(Composite parent, int style, ISettings settings) {
		this(parent, style, settings, null, null, null);
	}

	/**
	 * Constructs a new GANTT chart widget with custom settings and a custom color manager {@link IColorManager}. For styles, please see {@link IGanttFlags}.
	 * 
	 * @param parent Parent composite
	 * @param style Widget style
	 * @param settings ISettings implementation or null
	 * @param colorManager IColorManager implementation
	 * @throws IllegalArgumentException <ul>
	 *             <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *             </ul>
	 * @throws org.eclipse.swt.SWTException <ul>
	 *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li> <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed
	 *             subclass</li>
	 *             </ul>
	 */
	public GanttChart(Composite parent, int style, ISettings settings, IColorManager colorManager) {
		this(parent, style, settings, colorManager, null, null);
	}

	/**
	 * Constructs a new GANTT chart widget with custom settings, custom color manager {@link IColorManager}, a custom paint manager {@link IPaintManager} and a custom language
	 * manager {@link ILanguageManager}. If any of the managers is set to null the default manager using that implementation will be used. For styles, please see {@link IGanttFlags}.
	 * 
	 * @param parent Parent composite
	 * @param style Widget style
	 * @param settings ISettings implementation or null
	 * @param colorManager IColorManager implementation or null
	 * @param paintManager IPaintManager implementation or null
	 * @throws IllegalArgumentException <ul>
	 *             <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *             </ul>
	 * @throws org.eclipse.swt.SWTException <ul>
	 *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li> <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed
	 *             subclass</li>
	 *             </ul>
	 */
	public GanttChart(Composite parent, int style, ISettings settings, IColorManager colorManager, IPaintManager paintManager, ILanguageManager languageManager) {
		super(parent, SWT.NONE);
		
		// if no scrollbar is set, set one
		if ((style & H_SCROLL_FIXED_RANGE) == 0 && (style & H_SCROLL_NONE) == 0 && (style & H_SCROLL_INFINITE) == 0)
			style |= H_SCROLL_INFINITE;
		
		mStyle = style;
		mSettings = settings;
		mColorManager = colorManager;
		mPaintManager = paintManager;
		mLanguageManager = languageManager;
		init();
	}

	/**
	 * Adds a GanttGroup. A GanttGroup represents a collection of events that should all draw on the same line.
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

		if (mLanguageManager == null)
			mLanguageManager = new DefaultLanguageManager();

		mGanttComposite = new GanttComposite(this, mStyle, mSettings, mColorManager, mPaintManager, mLanguageManager);
		mGanttComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
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

	/**
	 * Returns the currently set settings implementor.
	 * 
	 * @return Settings
	 */
	public ISettings getSettings() {
		return mSettings;
	}

	/**
	 * Returns the currently set color manager.
	 * 
	 * @return Color manager
	 */
	public IColorManager getColorManager() {
		return mColorManager;
	}

	/**
	 * Returns the currently set paint manager.
	 * 
	 * @return Paint manager
	 */
	public IPaintManager getPaintManager() {
		return mPaintManager;
	}

	/**
	 * Returns the currently set language manager.
	 * 
	 * @return Language manager
	 */
	public ILanguageManager getLanguageManger() {
		return mLanguageManager;
	}

	public ScrollBar getVerticalBar() {
		return mGanttComposite.getVerticalBar();
	}

	public ScrollBar getHorizontalBar() {
		return mGanttComposite.getHorizontalBar();
	}

}
