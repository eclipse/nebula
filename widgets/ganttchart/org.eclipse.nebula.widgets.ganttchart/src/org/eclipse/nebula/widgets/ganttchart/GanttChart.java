/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.ganttchart;

import java.util.Calendar;
import java.util.Random;

import org.eclipse.nebula.widgets.ganttchart.print.GanttChartPrinter;
import org.eclipse.nebula.widgets.ganttchart.undoredo.GanttUndoRedoManager;
import org.eclipse.nebula.widgets.ganttchart.undoredo.IUndoRedoListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;

/**
 * <b>GanttChart - SWT Widget - 2005-2011. Version 2.0 &copy; Emil Crumhorn - emil dot crumhorn at gmail dot com.</b>
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
public class GanttChart extends Composite {

	private GanttComposite				_ganttComposite;
	private int							_style;
	private ISettings					_settings;
	private IColorManager				_colorManager;
	private IPaintManager				_paintManager;
	private ILanguageManager			_languageManager;

	private Holiday[] 					_holidays;
	
	private GanttChartPrinter			_printer;

	/**
	 * Constructs a new GANTT chart widget. For styles, please see {@link GanttFlags}.
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
	public GanttChart(final Composite parent, final int style) {
		this(parent, style, null);
	}

	/**
	 * Constructs a new GANTT chart widget with custom settings {@link ISettings}. For styles, please see {@link GanttFlags}.
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
	public GanttChart(final Composite parent, final int style, final ISettings settings) {
		this(parent, style, settings, null);
	}

	/**
	 * Constructs a new GANTT chart widget with custom settings and a custom color manager {@link IColorManager}. For styles, please see {@link GanttFlags}.
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
	public GanttChart(final Composite parent, final int style, final ISettings settings, final IColorManager colorManager) {
		this(parent, style, settings, colorManager, null, null, new Calendar[]{});
	}

	/**
	 * Constructs a new GANTT chart widget with custom settings, custom color manager {@link IColorManager}, a custom paint manager {@link IPaintManager} and a custom language
	 * manager {@link ILanguageManager}. If any of the managers is set to null the default manager using that implementation will be used. For styles, please see {@link GanttFlags}.
	 * 
	 * @param parent Parent composite
	 * @param style Widget style
	 * @param settings ISettings implementation or null
	 * @param colorManager IColorManager implementation or null
	 * @param paintManager IPaintManager implementation or null
	 * @param languageManager {@link ILanguageManager} implementation or null
	 * @param holidays Calendar objects specifying holidays
	 * @throws IllegalArgumentException <ul>
	 *             <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *             </ul>
	 * @throws org.eclipse.swt.SWTException <ul>
	 *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li> <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed
	 *             subclass</li>
	 *             </ul>
	 */
	public GanttChart(final Composite parent, final int style, final ISettings settings, final IColorManager colorManager, final IPaintManager paintManager, final ILanguageManager languageManager, Calendar... holidays) {
		this(parent, style, settings, colorManager, paintManager, languageManager, convertToHolidays(holidays));
	}

	/**
	 * Constructs a new GANTT chart widget with custom settings, custom color manager {@link IColorManager}, a custom paint manager
	 * {@link IPaintManager} and a custom language manager {@link ILanguageManager}. If any of the managers is set to null the
	 * default manager using that implementation will be used. For styles, please see {@link GanttFlags}.
	 * 
	 * @param parent Parent composite
	 * @param style Widget style
	 * @param settings ISettings implementation or null
	 * @param colorManager IColorManager implementation or null
	 * @param paintManager IPaintManager implementation or null
	 * @throws IllegalArgumentException
	 *             <ul>
	 *             <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *             </ul>
	 * @throws org.eclipse.swt.SWTException
	 *             <ul>
	 *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
	 *             <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
	 *             </ul>
	 */
	public GanttChart(final Composite parent, final int style,
			final ISettings settings, final IColorManager colorManager,
			final IPaintManager paintManager,
			final ILanguageManager languageManager) {
		super(parent, SWT.NONE);

		int styleToUse = style;

		// if no scrollbar is set, set one
		if ((style & GanttFlags.H_SCROLL_FIXED_RANGE) == 0
				&& (style & GanttFlags.H_SCROLL_NONE) == 0
				&& (style & GanttFlags.H_SCROLL_INFINITE) == 0) {
			styleToUse |= GanttFlags.H_SCROLL_INFINITE;
		}

		_style = styleToUse;
		_settings = settings;
		_colorManager = colorManager;
		_paintManager = paintManager;
		_languageManager = languageManager;
		init();
	}
	
	/**
	 * Constructs a new GANTT chart widget with custom settings, custom color manager {@link IColorManager}, a custom paint manager {@link IPaintManager} and a custom language
	 * manager {@link ILanguageManager}. If any of the managers is set to null the default manager using that implementation will be used. For styles, please see {@link GanttFlags}.
	 * 
	 * @param parent Parent composite
	 * @param style Widget style
	 * @param settings ISettings implementation or null
	 * @param colorManager IColorManager implementation or null
	 * @param paintManager IPaintManager implementation or null
	 * @param languageManager {@link ILanguageManager} implementation or null
	 * @param holidays Holiday objects specifying holidays
	 * @throws IllegalArgumentException <ul>
	 *             <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *             </ul>
	 * @throws org.eclipse.swt.SWTException <ul>
	 *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li> <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed
	 *             subclass</li>
	 *             </ul>
	 */
	public GanttChart(final Composite parent, final int style, final ISettings settings, final IColorManager colorManager, final IPaintManager paintManager, final ILanguageManager languageManager, Holiday... holidays) {
		super(parent, SWT.NONE);
		
		int styleToUse = style;
		
		// if no scrollbar is set, set one
		if ((style & GanttFlags.H_SCROLL_FIXED_RANGE) == 0 && (style & GanttFlags.H_SCROLL_NONE) == 0 && (style & GanttFlags.H_SCROLL_INFINITE) == 0) {
		    styleToUse |= GanttFlags.H_SCROLL_INFINITE;
		}
		
		_style = styleToUse;
		_settings = settings;
		_colorManager = colorManager;
		_paintManager = paintManager;
		_languageManager = languageManager;
		
		_holidays = holidays;
		
		_printer = new GanttChartPrinter(this);
		
		init();
	}

	/**
	 * Adds a GanttGroup. A GanttGroup represents a collection of events that should all draw on the same line.
	 * 
	 * @param group GanttGroup to add
	 */
	public void addGroup(final GanttGroup group) {
		_ganttComposite.addGroup(group);
	}

	/**
	 * Removes a GanttGroup.
	 * 
	 * @param group GanttGroup to remove
	 */
	public void removeGroup(final GanttGroup group) {
		_ganttComposite.removeGroup(group);
	}

	private void init() {
	    final GridLayout layout = new GridLayout(1, true);
		layout.marginBottom = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		setLayout(layout);

		if (_settings == null) {
			_settings = new DefaultSettings();
		}

		if (_colorManager == null) {
			_colorManager = new DefaultColorManager();
		}

		if (_paintManager == null) {
			_paintManager = new DefaultPaintManager();
		}

		if (_languageManager == null) {
			_languageManager = new DefaultLanguageManager();
		}

		_ganttComposite = new GanttComposite(this, _style, _settings, _colorManager, _paintManager, _languageManager, _holidays);
		_ganttComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	private static Holiday[] convertToHolidays(Calendar[] holidays) {
		Holiday[] list = new Holiday[holidays.length];
		
		for (int i = 0; i < holidays.length; i++) {
			list[i] = new Holiday(holidays[i]);
		}
		return list;
	}


	/**
	 * Adds a dependency between two events.
	 * 
	 * @param source GanttEvent source
	 * @param target GanttEvent target
	 */
	public void addConnection(final GanttEvent source, final GanttEvent target) {
		checkWidget();
		_ganttComposite.addDependency(source, target);
	}

	/**
	 * Same as addConnection().
	 * 
	 * @param source GanttEvent source
	 * @param target GanttEvent target
	 */
	public void addDependency(final GanttEvent source, final GanttEvent target) {
		checkWidget();
		addConnection(source, target);
	}

	/**
	 * Adds an event listener.
	 * 
	 * @param listener IGanttEventListener
	 */
	public void addGanttEventListener(final IGanttEventListener listener) {
		checkWidget();
		_ganttComposite.addGanttEventListener(listener);
	}

	/**
	 * Removes and event listener.
	 * 
	 * @param listener IGanttEventListener
	 */
	public void removeGanttEventListener(final IGanttEventListener listener) {
		checkWidget();
		_ganttComposite.removeGanttEventListener(listener);
	}

	/**
	 * Returns the widget that is the actual GANTT widget at the root level.
	 * 
	 * @return GanttComposite
	 */
	public GanttComposite getGanttComposite() {
		checkWidget();
		return _ganttComposite;
	}

	/**
	 * Redraws the GanttChart. Call this if an update to the chart has been made that did not cause an automatic redrawing of the chart.
	 * 
	 */
	public void redrawGanttChart() {
		_ganttComposite.redraw();
	}

	/**
	 * Re-indexes an event to show at a new location.
	 * 
	 * @param event GanttEvent to re-index
	 * @param newIndex new index position
	 */
	public void reindex(final GanttEvent event, final int newIndex) {
		_ganttComposite.reindex(event, newIndex);
	}

	/**
	 * Returns the currently set settings implementor.
	 * 
	 * @return Settings
	 */
	public ISettings getSettings() {
		return _settings;
	}

	/**
	 * Returns the currently set color manager.
	 * 
	 * @return Color manager
	 */
	public IColorManager getColorManager() {
		return _colorManager;
	}

	/**
	 * Returns the currently set paint manager.
	 * 
	 * @return Paint manager
	 */
	public IPaintManager getPaintManager() {
		return _paintManager;
	}

	/**
	 * Returns the currently set language manager.
	 * 
	 * @return Language manager
	 */
	public ILanguageManager getLanguageManger() {
		return _languageManager;
	}

	public ScrollBar getVerticalBar() {
		return _ganttComposite.getVerticalBar();
	}

	public ScrollBar getHorizontalBar() {
		return _ganttComposite.getHorizontalBar();
	}
	
	   /**
     * Adds a listener to be notified when undo/redo possibilities change
     * 
     * @param listener
     */
    public void addUndoRedoListener(final IUndoRedoListener listener) {
        _ganttComposite.getUndoRedoManager().addUndoRedoListener(listener);
    }
    
    /**
     * Removes a listener from being notified when undo/redo possibilities change
     * 
     * @param listener
     */
    public void removeUndoRedoListener(final IUndoRedoListener listener) {
        _ganttComposite.getUndoRedoManager().removeUndoRedoListener(listener);
    }
    
    /**
     * Returns the Undo/Redo manager
     * 
     * @return {@link GanttUndoRedoManager}
     */
    public GanttUndoRedoManager getUndoRedoManager() {
        return _ganttComposite.getUndoRedoManager();
    }
    
	
	/**
	 * Returns a random GanttEvent, useful for testing.
	 * 
	 * @return GanttEvent
	 */
	public GanttEvent getRandomEvent() {
	    final Calendar cal = Calendar.getInstance(_settings.getDefaultLocale());
	    final Calendar cal2 = Calendar.getInstance(_settings.getDefaultLocale());
		
	    final Random random = new Random();
		cal.add(Calendar.DATE, -random.nextInt(10));
		cal2.add(Calendar.DATE, random.nextInt(10)+1);
	
		return new GanttEvent(this, "Random Event", cal, cal2, random.nextInt(100)); //$NON-NLS-1$
	}

	/**
	 * Set a different GanttChartPrinter that should be used to print this GanttChart.
	 * @param printer The GanttChartPrinter that should be used to print this GanttChart.
	 */
	public void setGanttChartPrinter(GanttChartPrinter printer) {
		this._printer = printer;
	}
	
	/**
	 * Will print the GanttChart based on the settings made in the PrintDialog.
	 */
	public void print() {
		_printer.print();
	}
}
