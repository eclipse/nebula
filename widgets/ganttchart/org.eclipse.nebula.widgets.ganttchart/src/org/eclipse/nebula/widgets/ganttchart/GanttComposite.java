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
 * emil.crumhorn@gmail.com - initial API and implementation
 * ziogiannigmail.com - Bug 462855 - Zoom Minimum Depth increased up to 6 levels deeper than initial implementation (-6,-5,-4,-3,-2,-1)
 * ziogiannigmail.com - Bug 464509 - Minute View Implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.ganttchart;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;

import org.eclipse.nebula.widgets.ganttchart.dnd.VerticalDragDropManager;
import org.eclipse.nebula.widgets.ganttchart.undoredo.GanttUndoRedoManager;
import org.eclipse.nebula.widgets.ganttchart.undoredo.commands.ClusteredCommand;
import org.eclipse.nebula.widgets.ganttchart.undoredo.commands.EventMoveCommand;
import org.eclipse.nebula.widgets.ganttchart.undoredo.commands.IUndoRedoCommand;
import org.eclipse.nebula.widgets.ganttchart.utils.TextPainterHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Tracker;

/**
 * The GanttComposite is the workhorse of the GANTT chart. It contains a few public methods available for use, but most
 * of the functionality is private. <br>
 * <br>
 * There is a serious amount of calculation done in this chart, it's about 80% calculation and 20% drawing. In fact,
 * most of the drawing is delegated to other classes. <br>
 * <br>
 * A lot of settings method calls are set as class variables, but some are called straight off the settings object. The
 * logic isn't that deep, it's mostly just the over-and-over used variables that get class members. Slow stuff is
 * cached, such as the use of <code>gc.stringExtent()</code>, image rotation and so on. Anything that is slow _should_
 * be cached as it is a slowdown to the chart. A redraw should be as fast as possible and whenever possible should be
 * specific to certain bounds instead of a full redraw.<br>
 * <br>
 * This class may not be subclassed.
 */
// -- FEATURES
// TODO: allow zoom-out for D-day charts (we need to flip it from ISettings.D_DAY to a bool as we should draw d-day headers in each respective zoom-level head instead)
// TODO: millisecond view
public final class GanttComposite extends Canvas implements MouseListener, MouseMoveListener, MouseTrackListener, KeyListener, IZoomHandler {

	public static int _osType = Constants.OS_OTHER;

	final private GanttChart _parentChart;

	// current auto scroll direction
	private int _autoScrollDir = SWT.NULL;

	// keeps track of when to show or hide the zoom-helper area, go Clippy!
	private boolean _showZoomHelper;
	private Rectangle _zoomLevelArea;

	// start level
	private int _zoomLevel;
	private boolean _zoomLevelChanged;

	private boolean _forceSBUpdate;

	// current view
	private int _currentView;

	// 3D looking events
	private boolean _threeDee;

	// show a little plaque telling the days between start and end on the chart,
	// only draws if it fits
	private boolean _showNumDays;

	// draw the revised dates as a very thin small black |------| on the chart
	private boolean _showPlannedDates;

	// how many days offset to start drawing the calendar at.. useful for not
	// having the first day be today
	private final int _calStartOffset;

	// how many pixels from each event border to ignore as a move.. mostly to
	// help resizing
	private final int _moveAreaInsets;

	// year stuff
	private int _yearDayWidth;

	// month stuff
	private int _monthDayWidth;

	private int _monthWeekWidth;

	private int _minuteDayWidth;

	// day stuff
	// width of one day
	private int _dayWidth;

	// one week width, usually 7 days * width of one day
	private int _weekWidth;

	private int _bottomMostY;

	// various colors used.. all set in initColors()
	private Color _lineTodayColor;

	private Color _linePeriodColor;

	private Color _lineColor;

	private Color _lineWkDivColor;

	private Color _textColor;

	private Color _satBGColorTop;
	private Color _satBGColorBottom;

	private Color _weekdayTextColor;
	private Color _satTextColor;
	private Color _sunTextColor;

	private Color _sunBGColorTop;
	private Color _sunBGColorBottom;

	private Color _holidayBGColorTop;
	private Color _holidayBGColorBottom;

	private Color _wkBGColorTop;
	private Color _wkBGColorBottom;

	private Color _txtHeaderBGColorTop;
	private Color _txtHeaderBGColorBottom;
	private Color _timeHeaderBGColorTop;
	private Color _timeHeaderBGColorBottom;

	private Color _phaseHeaderBGColorTop;
	private Color _phaseHeaderBGColorBottom;

	private Color _arrowColor;
	private Color _reverseArrowColor;

	private Color _todayBGColorTop;
	private Color _todayBGColorBottom;

	// currently selected event
	private final List<Object> _selectedEvents;

	// the calendar that the current view is on, will always be on the first
	// date of the visible area (the date on the far left)
	// it should never change from internal functions. Where date changes, a
	// clone is made. It is critical it is never modified unless there is a reason for it.
	private Calendar _mainCalendar;

	// start calendar, is only used for reference internally
	private Calendar _startCalendar;
	
	// end calendar, is only used for reference internally
	private Calendar _endCalendar;

	// the number of days that will be visible in the current area. Is set after
	// we're done drawing the chart
	private int _daysVisible;
	private int _hoursVisible;

	// all events
	private final List<GanttEvent> _ganttEvents;

	// all connections between events
	private final List<GanttConnection> _ganttConnections;

	// a cache for re-used string extents
	private final Map<String, Point> _dayLetterStringExtentMap;

	// various variables for resize and drag/drop
	private boolean _dragging = false;

	private boolean _resizing = false;

	private Point _dragStartLoc;

	private boolean _freeDragging = false;
	private int _vDragDir = SWT.NONE;

	private int _lastX;
	private int _lastY;

	private int _cursor;

	private List<GanttEvent> _dragEvents;

	private GanttPhase _dragPhase;

	private boolean _lastLeft = false;

	private Calendar _dragStartDate = null;

	private boolean _justStartedMoveOrResize = false;

	private int _initialHoursDragOffset = 0;

	private final ISettings _settings;

	private final IColorManager _colorManager;

	private final IPaintManager _paintManager;

	private final ILanguageManager _languageManager;

	private final List<IGanttEventListener> _eventListeners;

	private boolean _mouseIsDown;

	private Point _mouseDragStartLocation;

	private final List<GanttGroup> _ganttGroups;

	private final List<GanttSection> _ganttSections;

	// menu used at right click events
	private Menu _rightClickMenu;

	// whether advanced tooltips are on or not
	private boolean _useAdvTooltips;

	// whether alpha drawing is enabled or not
	private boolean _useAlpha;

	private Rectangle _mainBounds;

	// bounds that are currently visible (changes when scrolling vertically)
	private Rectangle _visibleBounds;

	private final Locale _defaultLocale;

	private final int _eventHeight;

	private boolean _recalcScopes = true;
	private boolean _recalcSecBounds = true;

	private final Set<Object> _allEventsCombined;
	// sections, in groups, or single

	private final List<Integer> _verticalLineLocations;

	// faster
	// than
	// re-calculating over and over
	private final Set<Integer> _verticalWeekDividerLineLocations;

	// keeps track of hidden layers
	private final Set<Integer> _hiddenLayers;
	// keeps track of layer opacities
	private final Map<Integer, Integer> _layerOpacityMap;

	// only settings variables we allow direct override on
	private int _eventSpacer;
	private int _fixedRowHeight;
	private boolean _drawVerticalLines;
	private boolean _drawHorizontalLines;
	// end

	private int _lockedHeaderY;

	private int _vScrollPos;

	private final Point _origin = new Point(0, 0);
	private int _lastVScrollPos;

	private ScrollBar _vScrollBar;

	private List<Calendar> _selHeaderDates;

	private final int _style;

	private Tracker _tracker;
	private final boolean _multiSelect;

	private Calendar _dDayCalendar;

	private HorizontalScrollbarHandler _hScrollHandler;
	private IViewPortHandler2 _viewPortHandler;

	private boolean _savingChartImage = false;

	private final List<GanttPhase> _ganttPhases;

	final private List<GanttSpecialDateRange> _specDateRanges;

	// cache for "string-extented" strings for much faster fetching
	final private Map<String, Point> _stringWidthCache;

	// the total of all non-hidden events that are GanttEvents
	private int _totVisEventCnt;

	final private VerticalDragDropManager _vDNDManager;

	final private GanttUndoRedoManager _undoRedoManager;

	private boolean _drawToMinute;

	private IEventFactory eventFactory = new DefaultEventFactory();
	private IEventMenuItemFactory eventMenuItemFactory;
	private IMenuItemFactory menuItemFactory;

	private Holiday[] holidays;

	private IZoomHandler zoomHandler;

	private Map<GanttSection, Rectangle> sectionDetailMoreIcons = null;

	private final List<ISectionDetailMoreClickListener> sectionDetailMoreClickListener = new ArrayList<>();

	static {
		final String osProperty = System.getProperty("os.name");
		if (osProperty != null) {
			final String osName = osProperty.toUpperCase(Locale.getDefault());

			if (osName.indexOf("WINDOWS") > -1) {
				_osType = Constants.OS_WINDOWS;
			} else if (osName.indexOf("MAC") > -1) {
				_osType = Constants.OS_MAC;
			} else if (osName.indexOf("NIX") > -1 || osName.indexOf("NUX") > -1) {
				_osType = Constants.OS_LINUX;
			}
		}
	}

	public GanttComposite(final GanttChart parent, final int style, final ISettings settings, final IColorManager colorManager, final IPaintManager paintManager, final ILanguageManager languageManager) {
		this(parent, style, settings, colorManager, paintManager, languageManager, null);
	}

	public GanttComposite(final GanttChart parent, final int style, final ISettings settings, final IColorManager colorManager, final IPaintManager paintManager, final ILanguageManager languageManager, final Holiday[] holidays) {
		super(parent, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED | SWT.V_SCROLL | SWT.H_SCROLL);

		_parentChart = parent;

		// d-day calendar can be anything, but it needs to be something, and for convenience, lets use 2000 as base year
		if (settings.getInitialView() == ISettings.VIEW_D_DAY) {
			_dDayCalendar = DateHelper.getNewCalendar(settings.getDDayRootCalendar());
		}

		startMidnightThread();

		_style = style;
		_multiSelect = (_style & SWT.MULTI) != 0;

		_settings = settings;
		_colorManager = colorManager;
		_paintManager = paintManager;
		_languageManager = languageManager;

		_ganttConnections = new ArrayList<GanttConnection>();
		_dragEvents = new ArrayList<GanttEvent>();
		_eventListeners = new ArrayList<IGanttEventListener>();
		_ganttEvents = new ArrayList<GanttEvent>();
		_ganttGroups = new ArrayList<GanttGroup>();
		_ganttSections = new ArrayList<GanttSection>();
		_verticalLineLocations = new ArrayList<Integer>();
		_verticalWeekDividerLineLocations = new HashSet<Integer>();
		_hiddenLayers = new HashSet<Integer>();
		_allEventsCombined = new HashSet<Object>();
		_dayLetterStringExtentMap = new HashMap<String, Point>();
		_layerOpacityMap = new HashMap<Integer, Integer>();
		_selHeaderDates = new ArrayList<Calendar>();
		_selectedEvents = new ArrayList<Object>();
		_ganttPhases = new ArrayList<GanttPhase>();
		_specDateRanges = new ArrayList<GanttSpecialDateRange>();

		_stringWidthCache = new HashMap<String, Point>();

		_defaultLocale = _settings.getDefaultLocale();
		DateHelper.initialize(_defaultLocale);
		_useAdvTooltips = _settings.getUseAdvancedTooltips();

		_currentView = _settings.getInitialView();
		_zoomLevel = _settings.getInitialZoomLevel();
		_showNumDays = _settings.showNumberOfDaysOnBars();
		_showPlannedDates = _settings.showPlannedDates();
		_threeDee = _settings.showBarsIn3D();
		_yearDayWidth = _settings.getYearMonthDayWidth();
		_monthDayWidth = _settings.getMonthDayWidth();
		_monthWeekWidth = _monthDayWidth * 7;
		_dayWidth = _settings.getDayWidth();
		_weekWidth = _dayWidth * 7;
		_minuteDayWidth = _dayWidth * 1440;
		_calStartOffset = _settings.getCalendarStartupDateOffset();
		_mainCalendar = _settings.getStartupCalendarDate();
		_moveAreaInsets = _settings.getMoveAreaNegativeSensitivity();
		_eventSpacer = _settings.getEventSpacer();
		_eventHeight = _settings.getEventHeight();

		_drawHorizontalLines = _settings.drawHorizontalLines();
		_drawVerticalLines = _settings.drawVerticalLines();

		_vDNDManager = new VerticalDragDropManager();
		_undoRedoManager = new GanttUndoRedoManager(this, GanttUndoRedoManager.STACK_SIZE);
		_drawToMinute = _settings.drawEventsDownToTheHourAndMinute();

		this.holidays = holidays == null ? new Holiday[] {} : holidays;

		updateZoomLevel();

		if (_currentView == ISettings.VIEW_D_DAY) {
			_mainCalendar = DateHelper.getNewCalendar(_dDayCalendar);
		}

		// by default, set today's date
		setDate(_mainCalendar, true, false);
		_startCalendar = (Calendar) _mainCalendar.clone();

		// we don't really need a layout, but set one in any case
		setLayout(new FillLayout());

		initColors();
		initListeners();

		// last but not least, update the scrollbars post-first-draw (otherwise we don't know jack about nothing as far as client area etc goes)
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (isDisposed()) {
					return;
				}

				updateVerticalScrollBar(false);
				updateHorizontalScrollbar();
			}
		});

		final Thread t = new Thread() {// TodayLine is too sleepy, not updated properly by GUI, then it forces its update every X seconds
			volatile int delay = 2000;

			@Override
			public void run() {
				while (!isDisposed()) {
					try {
						Thread.sleep(delay);
						if (isDisposed()) {
							return;
						}
						if (_currentView == ISettings.VIEW_DAY || _currentView == ISettings.VIEW_MINUTE) {
							_parentChart.getDisplay().asyncExec(new Runnable() {
								public void run() {
									if (!isDisposed()) {
										refresh();
									}
								}
							});
						}
					} catch (final Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};

		if (_settings instanceof ISettings2 && ((ISettings2) _settings).enableTodayLineUpdater()) {
			t.start();
		}

		zoomHandler = this;

		if (_settings.showSectionDetailMore()) {
			sectionDetailMoreIcons = new HashMap<GanttSection, Rectangle>();
		}

	}

	// midnight "thread" (we need to redraw the screen once at Midnight as the date line will otherwise be incorrect)
	// this is truly only necessary if someone were to be staring at the GANTT chart as midnight happens, so it's
	// incredibly minor (any redraw would draw the right date lines), but nevertheless...
	private void startMidnightThread() {
		_parentChart.getDisplay().timerExec(60000, new Runnable() {

			public void run() {
				try {
					if (isDisposed()) {
						return;
					}

					final Calendar cal = Calendar.getInstance(_settings.getDefaultLocale());
					// Midnight, redraw
					if (cal.get(Calendar.HOUR_OF_DAY) == 0 && cal.get(Calendar.MINUTE) == 0) {
						redraw();
						DateHelper.initialize(_settings.getDefaultLocale());
					}

					if (getDisplay() != null && !getDisplay().isDisposed()) {
						getDisplay().timerExec(60000, this);
					}
				} catch (final Exception err) {
					SWT.error(SWT.ERROR_UNSPECIFIED, err);
				}
			}

		});
	}

	private void initListeners() {
		// without this there'd be nothing!
		addPaintListener(new PaintListener() {
			public void paintControl(final PaintEvent event) {
				GanttComposite.this.repaint(event);
			}
		});

		addMouseListener(this);
		addMouseMoveListener(this);
		addMouseTrackListener(this);
		addKeyListener(this);

		final Listener mouseWheelListner = new Listener() {
			public void handleEvent(final Event event) {
				if (!_settings.enableZooming()) {
					return;
				}

				if (event.stateMask == _settings.getZoomWheelModifierKey()) {
					_showZoomHelper = _settings.showZoomLevelBox();

					if (event.count > 0) {
						zoomHandler.zoomIn(true, new Point(event.x, event.y));
					} else {
						zoomHandler.zoomOut(true, new Point(event.x, event.y));
					}
				} else {
					// note to self: on SWT 3.5+ it seems we just force it, older versions may need to turn this off
					if (_settings.scrollChartVerticallyOnMouseWheel()) {
						vScroll(event);
					}
				}
			}
		};

		addListener(SWT.MouseWheel, mouseWheelListner);
		// on Windows we want to kill any tooltip when alt is pressed, as it may
		// be an indicator we're switching
		// programs, and tooltips usually die on MouseMove or MouseUp
		addKeyListener(new KeyListener() {
			public void keyPressed(final KeyEvent event) {
				if (event.stateMask == SWT.ALT || event.keyCode == SWT.ALT) {
					killDialogs();
				}
			}

			public void keyReleased(final KeyEvent event) {
				if (_tracker != null && !_tracker.isDisposed()) {
					_tracker.dispose();
					_mouseIsDown = false;
				}
				if (_showZoomHelper) {
					_showZoomHelper = false;
					// redraw the area where it was
					if (_zoomLevelArea != null) {
						redraw(_zoomLevelArea.x, _zoomLevelArea.y, _zoomLevelArea.width + 1, _zoomLevelArea.height + 1, false);
					}
				}
			}
		});

		// viewport handler must be created before scrollbar handler
		_viewPortHandler = new ViewPortHandler(this);
		// horizontal scrollbar handler, deals with all scrollbar movements, scrollbar types, etc
		_hScrollHandler = new HorizontalScrollbarHandler(this, getHorizontalBar(), _style);

		_vScrollBar = getVerticalBar();
		_vScrollBar.setPageIncrement(_settings.getEventHeight() + _settings.getEventSpacer());
		_vScrollBar.setIncrement(_settings.getEventHeight());
		_vScrollBar.setVisible(false);
		_vScrollBar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(final Event event) {
				vScroll(event);
			}
		});

		addListener(SWT.Resize, new Listener() {
			public void handleEvent(final Event event) {
				handleResize(false);
				updateHorizontalScrollbar();

				// redraw last
				getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed()) {
							return;
						}

						redraw();
					}
				});
			}
		});

	}

	public void vScroll() {
		// this has got to be a SWT bug, a non-visible scrollbar can report scroll events!
		if (!_vScrollBar.isVisible()) {
			_vScrollPos = 0;
			return;
		}

		final int vSelection = _vScrollBar.getSelection();

		_vScrollPos = vSelection;
		final int diff = _vScrollPos - _lastVScrollPos;
		_lastVScrollPos = _vScrollPos;

		if (diff != 0) {
			// move all events the fast way. There is truly no reason to recalculate bounds for any event, all we need to do is move them
			// according to the scroll bar, which is way way faster than recalculating.
			moveYBounds(diff);
			// showVscrollInfo();

			_recalcSecBounds = true;
			redraw();
		}
	}

	private void vScroll(final Event event) {
		// end of drag, kill dialogs
		if (event != null && event.detail == 0) {
			killDialogs();
			return;
		}

		// this has got to be a SWT bug, a non-visible scrollbar can report scroll events!
		if (!_vScrollBar.isVisible()) {
			_vScrollPos = 0;
			return;
		}

		final int vSelection = _vScrollBar.getSelection();

		_vScrollPos = vSelection;
		final int diff = _vScrollPos - _lastVScrollPos;

		_lastVScrollPos = _vScrollPos;

		// move all events the fast way. There is truly no reason to recalculate bounds for any event, all we need to do is move them
		// according to the scroll bar, which is way way faster than recalculating.
		moveYBounds(diff);
		// showVscrollInfo();

		_recalcSecBounds = true;
		redraw();
	}

	void updateVerticalScrollBar(final boolean redraw) {
		handleResize(redraw);
	}

	private void handleResize(final boolean redraw) {
		final Rectangle rect = getBounds();
		final Rectangle client = getClientArea();
		_vScrollBar.setMaximum(rect.height);
		_vScrollBar.setPageIncrement(15);
		_vScrollBar.setThumb(Math.min(rect.height, client.height));
		final int vPage = rect.height - client.height;
		int vSelection = _vScrollBar.getSelection();
		if (vSelection >= vPage) {
			if (vPage <= 0) {
				vSelection = 0;
			}
			_origin.y = -vSelection;
		}

		// TODO: Do fancy thing where client area moves with the resize.. (low priority)

		if (_bottomMostY < getClientArea().height) {
			// if we reached the end, make sure we're back at the top
			if (_vScrollBar.isVisible()) {
				_vScrollBar.setVisible(false);
				if (_vScrollPos != 0) {
					moveYBounds(-_vScrollPos);
					_vScrollPos = 0;
				}
			}
		} else {
			_vScrollBar.setVisible(true);
		}

		if (_endCalendar != null) {
			updateEventVisibilities(client);
		}

		_recalcSecBounds = true;

		if (redraw) {
			redraw();
		}
	}

	private void initColors() {
		_lineTodayColor = _colorManager.getTodayLineColor();
		_linePeriodColor = _colorManager.getPeriodLineColor();
		_lineColor = _colorManager.getLineColor();
		_textColor = _colorManager.getTextColor();
		_satTextColor = _colorManager.getSaturdayTextColor();

		_satBGColorTop = _colorManager.getSaturdayBackgroundColorTop();
		_satBGColorBottom = _colorManager.getSaturdayBackgroundColorBottom();
		_sunBGColorTop = _colorManager.getSundayBackgroundColorTop();
		_sunBGColorBottom = _colorManager.getSundayBackgroundColorBottom();
		_holidayBGColorTop = _colorManager.getHolidayBackgroundColorTop();
		_holidayBGColorBottom = _colorManager.getHolidayBackgroundColorBottom();

		_weekdayTextColor = _colorManager.getWeekdayTextColor();
		_sunTextColor = _colorManager.getSundayTextColor();
		_satTextColor = _colorManager.getSaturdayTextColor();
		_wkBGColorTop = _colorManager.getWeekdayBackgroundColorTop();
		_wkBGColorBottom = _colorManager.getWeekdayBackgroundColorBottom();
		_lineWkDivColor = _colorManager.getWeekDividerLineColor();
		_txtHeaderBGColorTop = _colorManager.getTextHeaderBackgroundColorTop();
		_txtHeaderBGColorBottom = _colorManager.getTextHeaderBackgroundColorBottom();
		_timeHeaderBGColorTop = _colorManager.getTimeHeaderBackgroundColorTop();
		_timeHeaderBGColorBottom = _colorManager.getTimeHeaderBackgroundColorBottom();
		_phaseHeaderBGColorTop = _colorManager.getPhaseHeaderBackgroundColorTop();
		_phaseHeaderBGColorBottom = _colorManager.getPhaseHeaderBackgroundColorBottom();
		_arrowColor = _colorManager.getArrowColor();
		_reverseArrowColor = _colorManager.getReverseArrowColor();
		_todayBGColorTop = _colorManager.getTodayBackgroundColorTop();
		_todayBGColorBottom = _colorManager.getTodayBackgroundColorBottom();
		_useAlpha = _colorManager.useAlphaDrawing();
	}

	/**
	 * Returns the current date (left-most date).
	 *
	 * @return
	 */
	public Calendar getDate() {
		return DateHelper.getNewCalendar(_mainCalendar);
	}

	/**
	 * Returns the non-cloned root calendar of the chart. DO NOT modify this outside of the chart. This method is
	 * considered internal public and will be removed later.
	 *
	 * @return Calendar
	 */
	public Calendar getRootCalendar() {
		return _mainCalendar;
	}

	/**
	 * Returns the non-cloned root end calendar of the chart. DO NOT modify this outside of the chart. This method is
	 * considered internal public and will be removed later.
	 *
	 * @return Calendar
	 */
	public Calendar getRootEndCalendar() {
		    	Calendar end = (Calendar) getRootCalendar().clone();
		    	for (int i = 0; i < _ganttEvents.size(); i++) {
		    		GanttEvent event = (GanttEvent) _ganttEvents.get(i);
					Calendar latestEndDate = event.getLatestEndDate();
					if (latestEndDate.after(end)) {
						end = latestEndDate;
					}
				}
		        return end;
	}
	
	/**
	 * Returns the non-cloned root start calendar of the chart. DO NOT modify this outside of the chart. This method is
	 * considered internal public and will be removed later.
	 *
	 * @return Calendar
	 */
	public Calendar getRootStartCalendar() {
		return _startCalendar;
	}

	/**
	 * Hides all layers of the given value and redraws the event area.
	 *
	 * @param layer Layer to hide.
	 */
	public void hideLayer(final int layer) {
		if (!_hiddenLayers.contains(new Integer(layer))) {
			_hiddenLayers.add(new Integer(layer));
		}
	}

	/**
	 * Shows all layers of the given value and redraws the event area.
	 *
	 * @param layer Layer to show.
	 */
	public void showLayer(final int layer) {
		final boolean removed = _hiddenLayers.remove(new Integer(layer));
		if (removed) {
			redrawEventsArea();
		}
	}

	// why don't controls have this already? such a simple method
	public void redraw(final Rectangle rect) {
		redraw(rect.x, rect.y, rect.width, rect.height, false);
	}

	/**
	 * Shows all layers and redraws the event area.
	 */
	public void showAllLayers() {
		if (_hiddenLayers.isEmpty()) {
			return;
		}

		_hiddenLayers.clear();
		redrawEventsArea();
	}

	/**
	 * Hides all layers and redraws the event area.
	 */
	public void hideAllLayers() {
		for (int i = 0; i < _ganttEvents.size(); i++) {
			final GanttEvent event = _ganttEvents.get(i);
			final Integer layer = new Integer(event.getLayer());
			if (!_hiddenLayers.contains(layer)) {
				_hiddenLayers.add(layer);
			}
		}

		redrawEventsArea();
	}

	/**
	 * Sets the drawing opacity for a layer. Do note that this may reduce the drawing speed of the chart by a lot. The
	 * opacity range is from 0 to 255. Note that if alpha settings are turned on in settings, those values will still be
	 * used, so it may be wise to turn them off if you are doing layer blending.
	 *
	 * @param layer Layer to set opacity on
	 * @param opacity Opacity between 0 and 255
	 */
	public void setLayerOpacity(final int layer, final int opacity) {

		int toSet = opacity;

		if (toSet >= 255) {
			_layerOpacityMap.remove(new Integer(layer));
			return;
		}

		if (toSet < 0) {
			toSet = 0;
		}

		_layerOpacityMap.put(new Integer(layer), new Integer(toSet));
		redrawEventsArea();
	}

	/**
	 * Returns the layer opacity for a layer.
	 *
	 * @param layer Layer to get opacity for
	 * @return Layer opacity, -1 if layer has no opacity set.
	 */
	public int getLayerOpacity(final int layer) {
		int ret = -1;
		if (_layerOpacityMap.containsKey(new Integer(layer))) {
			ret = _layerOpacityMap.get(new Integer(layer)).intValue();
		}
		return ret;
	}

	/**
	 * Setting a fixed row height override causes all rows to be the set height regardless of individual row heights set
	 * on items themselves and all settings.
	 *
	 * @param height Height to set. Set to zero to turn off.
	 */
	public void setFixedRowHeightOverride(final int height) {
		_fixedRowHeight = height;
	}

	/**
	 * Setting a fixed event spacer overrides all individual event space settings on chart items and all settings.
	 *
	 * @param height Height to set. Set to zero to turn off.
	 */
	public void setEventSpacerOverride(final int height) {
		_eventSpacer = height;
	}

	/**
	 * Setting this to true will force horizontal lines to draw despite what may be set in the settings.
	 *
	 * @param drawHorizontal true to draw horizontal lines.
	 */
	public void setDrawHorizontalLinesOverride(final boolean drawHorizontal) {
		_drawHorizontalLines = drawHorizontal;
	}

	/**
	 * Setting this to true will force vertical lines to draw despite what may be set in the settings.
	 *
	 * @param drawVertical true to draw vertical lines.
	 */
	public void setDrawVerticalLinesOverride(final boolean drawVertical) {
		_drawVerticalLines = drawVertical;
	}

	/**
	 * Sets the selection to be a specific GanttEvent. This method will cause a redraw.
	 *
	 * @param event GanttEvent to select
	 */
	public void setSelection(final GanttEvent event) {
		_selectedEvents.clear();
		_selectedEvents.add(event);
		redrawEventsArea();
	}

	/**
	 * Sets the selection to be a set of GanttEvents. If the chart is set to <code>SWT.SINGLE</code> you should be using
	 * {@link #setSelection(GanttEvent)} as this method will do nothing. This method will cause a redraw.
	 *
	 * @param list List of GanttEvents to select
	 */
	public void setSelection(final List list) {
		if (!_multiSelect) {
			return;
		}

		_selectedEvents.clear();
		_selectedEvents.addAll(list);
		redrawEventsArea();
	}

	/**
	 * Adds a GanttGroup to the chart.
	 *
	 * @param group Group to add
	 */
	public void addGroup(final GanttGroup group) {
		internalAddGroup(-1, group);
	}

	/**
	 * Returns a list of all GanttGroups.
	 *
	 * @return List of GanttGroups
	 */
	public List<GanttGroup> getGroups() {
		return _ganttGroups;
	}

	/**
	 * Removes a GanttGroup from the chart.
	 *
	 * @param group Group to remove
	 */
	public void removeGroup(final GanttGroup group) {
		internalRemoveGroup(group);
	}

	/**
	 * Adds a GanttSection to the chart.
	 *
	 * @param section Section to add
	 */
	public void addSection(final GanttSection section) {
		internalAddSection(-1, section);
	}

	/**
	 * Adds a GanttSection to the chart.
	 *
	 * @param section Section to add
	 * @param index the index to add the Section at
	 */
	public void addSection(final GanttSection section, int index) {
		internalAddSection(index, section);
	}

	/**
	 * Removes a GanttSection from the chart.
	 *
	 * @param section Section to remove
	 */
	public void removeSection(final GanttSection section) {
		internalRemoveSection(section);
	}

	/**
	 * Returns a list of all GanttSections.
	 *
	 * @return List of GanttSections.
	 */
	public List getGanttSections() {
		return _ganttSections;
	}

	// horizontal scrollbar info
	void showScrollDate() {
		if (_settings.showDateTips() && _settings.showDateTipsOnScrolling() && _mainBounds != null /* security question as in some unknown cases the _mainBounds is null */) {
			final String str = DateHelper.getDate(_mainCalendar, _currentView == ISettings.VIEW_MINUTE || _currentView == ISettings.VIEW_DAY ? _settings.getHourDateFormat() : _settings.getDateFormat());
			final GC gc = new GC(this);
			final Point ext = gc.stringExtent(str);
			gc.dispose();

			final int bottomY = _mainBounds.height - ext.y - 12;
			final int bottomX = _mainBounds.x + _mainBounds.width / 2 - ext.x;

			final Point point = toDisplay(bottomX, bottomY);
			GanttDateTip.makeDialog(_colorManager, str, point, bottomY);
		}
	}

	// vertical scrollbar info // TODO: Needs work
	/*
	 * private void showVscrollInfo() {
	 * if (true) return;
	 * if (mSettings.showDateTips() && mSettings.showDateTipsOnScrolling()) {
	 * // TODO: This doesn't take groups into event, we should get the top/bottom from all sorts of events
	 * GanttEvent topEvent = _getTopEvent();
	 * GanttEvent bottomEvent = _getBottomEvent();
	 * if (topEvent == null) {
	 * GanttDateTip.kill();
	 * return;
	 * }
	 *
	 * StringBuffer buf = new StringBuffer();
	 * buf.append(topEvent.getName());
	 * if (bottomEvent != null) {
	 * buf.append(Constants.STR_DASH);
	 * buf.append(bottomEvent.getName());
	 * }
	 *
	 * GC gc = new GC(this);
	 * Point ext = gc.stringExtent(buf.toString());
	 * gc.dispose();
	 *
	 * int bottomY = toControl(getDisplay().getCursorLocation()).y;
	 * int bottomX = getClientArea().width - ext.x - 12;
	 *
	 * Point p = toDisplay(bottomX, bottomY);
	 * GanttDateTip.makeDialog(mColorManager, buf.toString(), p, bottomY);
	 * }
	 * }
	 */
	/*
	 * private GanttEvent _getTopEvent() {
	 * for (int i = 0; i < mGanttEvents.size(); i++) {
	 * GanttEvent ge = (GanttEvent) mGanttEvents.get(i);
	 * if (ge.isBoundsSet() && ge.getY() - mVerticalScrollPosition >= getHeaderHeight() - mSettings.getEventsTopSpacer()) { return ge; }
	 * }
	 *
	 * return null;
	 * }
	 *
	 * private GanttEvent _getBottomEvent() {
	 * for (int i = 0; i < mGanttEvents.size(); i++) {
	 * GanttEvent ge = (GanttEvent) mGanttEvents.get(i);
	 * if (ge.getY() > mVerticalScrollPosition + mBounds.height - getHeaderHeight() - mSettings.getEventsTopSpacer()) return ge;
	 * }
	 *
	 * return null;
	 * }
	 */
	/**
	 * Returns the topmost visible event in the current view of the chart.
	 *
	 * @return GanttEvent or null
	 */
	public GanttEvent getTopEvent() {
		for (int i = 0; i < _ganttEvents.size(); i++) {
			final GanttEvent event = _ganttEvents.get(i);

			if (event.getVisibility() == Constants.EVENT_VISIBLE) {
				return event;
			}
		}

		return null;
	}

	/**
	 * Returns the bottom most visible event in the current view of the chart.
	 *
	 * @return GanttEvent or null
	 */
	public GanttEvent getBottomEvent() {
		final GanttEvent top = getTopEvent();
		if (top == null) {
			return null;
		}

		boolean start = false;

		for (int i = 0; i < _ganttEvents.size(); i++) {
			final GanttEvent event = _ganttEvents.get(i);
			if (i < _ganttEvents.size() - 1) {
				final GanttEvent nextEvent = _ganttEvents.get(i + 1);

				if (event.equals(top)) {
					start = true;
					continue;
				}

				if (!start) {
					continue;
				}

				if (nextEvent.getVisibility() == Constants.EVENT_OOB_BOTTOM) {
					return event;
				}
			} else {
				return event;
			}
		}

		return null;
	}

	void killDialogs() {
		if (_settings.showToolTips()) {
			GanttToolTip.kill();
			GanttDateTip.kill();
			AdvancedTooltipDialog.kill();
		}
	}

	// the repaint event, whenever the composite needs to refresh the contents
	private void repaint(final PaintEvent event) {
		_paintManager.redrawStarting();
		final GC gc = event.gc;
		drawChartOntoGC(gc, null);
	}

	// draws the actual chart.. separated from the repaint event as the
	// getImage() call uses it on a different GC
	// Note: things are in a certain order, as some stuff draws on top of
	// others, so don't move them around unless you want a different effect
	private void drawChartOntoGC(final GC gc, final Rectangle boundsOverride) {
		// long totaltime1 = System.currentTimeMillis();
		final boolean drawSections = hasGanttSections();

		// only reset bottom y if we recalculate it, or we'll lose the vertical scrollbar among other things that update on all redraws
		if (_recalcScopes || drawSections) {
			_bottomMostY = 0;
		}

		Rectangle bounds = super.getClientArea();
		if (boundsOverride != null) {
			bounds = boundsOverride;
		}

		// the actual visible bounds counting any vertical scroll offset (includes header height)
		_visibleBounds = new Rectangle(bounds.x, bounds.y + _vScrollPos, bounds.width, bounds.height);

		// if we use sections, our bounds for everything will be the size of the client area minus the section bar on the left
		if (drawSections) {
			int sectionWidth = 0;
			if (_settings.drawSectionBar()) {
				sectionWidth += _settings.getSectionBarWidth();
			}
			if (_settings.drawSectionDetails()) {
				sectionWidth += _settings.getSectionDetailWidth();
			}
			if (_settings.getSectionSide() == SWT.LEFT) {
				bounds = new Rectangle(sectionWidth, bounds.y, bounds.width - sectionWidth, bounds.height);
			} else {
				bounds = new Rectangle(0, bounds.y, bounds.width - sectionWidth, bounds.height);
			}
		}

		_mainBounds = new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
		_lockedHeaderY = _mainBounds.y;
		_mainBounds.y -= _vScrollPos;

		final boolean calcHeaderOnly = _settings.drawHeader() && _settings.lockHeaderOnVerticalScroll() || !_settings.drawHeader();

		drawHeader(gc, calcHeaderOnly);

		/*
		 * // header
		 * if (_settings.drawHeader()) {
		 * // if we're locking the header, it will be drawn last as it needs to draw on top of the chart, so in that case we just do the necessary calculations
		 * // and let it draw later, this saves a bunch of time on locked-header charts
		 * if (_settings.lockHeaderOnVerticalScroll()) {
		 * drawHeader(gc, true);
		 * } else {
		 * drawHeader(gc, false);
		 * }
		 * } else {
		 * // we need the mDaysVisible value which is normally calculated when we draw boxes, as the header is not drawn here, we need to calculate it manually
		 * //calculateDaysVisible(bounds);
		 * drawHeader(gc, false);
		 * }
		 */
		updateEventVisibilities(_visibleBounds);

		// section drawing needs special treatment as we need to give sub-bounds to the various drawing methods
		if (drawSections) {
			if (_recalcSecBounds) {
				calculateSectionBounds(gc, bounds);
			}

			// if we fill the bottom then fill it!
			if (_settings.drawFillsToBottomWhenUsingGanttSections()) {
				final Rectangle extraBounds = new Rectangle(_mainBounds.x, _mainBounds.y + getHeaderHeight() - _vScrollPos, _mainBounds.x + _mainBounds.width, _mainBounds.y + _mainBounds.height - getHeaderHeight() + _vScrollPos);
				drawFills(gc, extraBounds);
				drawVerticalLines(gc, extraBounds, false);
			} else {
				// draw the background for the bottom of the gantt chart as on Win7 it would be transparent
				final int dayWidth = _currentView == ISettings.VIEW_WEEK || _currentView == ISettings.VIEW_D_DAY ? _dayWidth : _monthDayWidth;
				int maxX = bounds.width + dayWidth; // we need to draw beyond 1 day as the days at the edge of the viewport also needs to be filled in case a half-day is visible there
				int startX = bounds.x;

				int offset = _vScrollPos;
				if (offset > getHeaderHeight()) {
					offset = getHeaderHeight();
				}

				if (hasGanttSections()) {
					if (_settings.drawSectionBar()) {
						startX -= _settings.getSectionBarWidth();
						maxX += _settings.getSectionBarWidth();
					}
					if (_settings.drawSectionDetails()) {
						startX -= _settings.getSectionDetailWidth();
						maxX += _settings.getSectionDetailWidth();
					}
				}

				final int startY = bounds.y + getHeaderHeight() - offset;
				final int heightY = bounds.height;

				gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
				gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

				gc.fillRectangle(startX, startY, maxX, heightY);
			}

			for (int i = 0; i < _ganttSections.size(); i++) {
				final GanttSection section = _ganttSections.get(i);
				final Rectangle gsBounds = section.getBounds();

				if (boundsOverride != null) {
					gsBounds.width = boundsOverride.width;
				}

				if (_recalcScopes) {
					calculateAllScopes(gsBounds, section);
				}

				drawFills(gc, gsBounds, section);

				if (hasGanttPhases()) {
					drawGanttPhases(gc, gsBounds, false, section);
				}

				// draw vertical lines
				if (_drawVerticalLines) {
					drawVerticalLines(gc, gsBounds, true);
				}

				// more lines
				if (_drawHorizontalLines) {
					drawHorizontalLines(gc, bounds);
				}

				if (hasSpecialDateRanges()) {
					drawGanttSpecialDateRanges(gc, gsBounds, section);
				}

				// draw events
				drawEvents(gc, gsBounds, section);
			}

			// before we drew connections inside the loop below, which was totally pointless. We only need to draw connections once for the visible area,
			// not once per section. This is way faster, connection drawing is not 0ms
			drawConnections(gc);

			// just because I have the feeling some user will want cross-section connections, we allow it by
			// drawing the connecting lines _last_. Why? because the event bounds are not calculated until the event is drawn, and if we have
			// a connection to a group/event that hasn't been drawn yet, it would draw an arrow into space..
			for (int i = 0; i < _ganttSections.size(); i++) {
				final GanttSection section = _ganttSections.get(i);
				_bottomMostY = Math.max(section.getBounds().y + section.getBounds().height, _bottomMostY);
			}
		} else {
			bounds = new Rectangle(bounds.x, getHeaderHeight(), bounds.width, bounds.height);

			// long start = System.currentTimeMillis();
			if (_recalcScopes) {
				calculateAllScopes(bounds, null);
			}

			// draw fills
			drawFills(gc, bounds);

			if (hasGanttPhases()) {
				drawGanttPhases(gc, bounds, false, null);
			}

			// draws vertical lines all over the chart
			if (_drawVerticalLines) {
				drawVerticalLines(gc, bounds, true);
			}

			if (_drawHorizontalLines) {
				drawHorizontalLines(gc, bounds);
			}

			if (hasSpecialDateRanges()) {
				drawGanttSpecialDateRanges(gc, bounds, null);
			}

			// draw the events
			drawEvents(gc, bounds);

			// draw the connecting arrows
			drawConnections(gc);
		}

		if (drawSections) {
			drawSectionColumn(gc, bounds, false, false, false, false);
		}

		// if we lock the header, we unfortunately need to draw it again on top of everything else. Down the road this should be optimized of course,
		// but there's so many necessary calculations done in the header drawing that we need for later that it's a bit of work
		if (_settings.lockHeaderOnVerticalScroll() && _settings.drawHeader()) {
			drawHeader(gc, false);
			// draw corner again
			drawSectionColumn(gc, bounds, true, false, true, false);
		}

		// zoom
		if (_showZoomHelper && _settings.showZoomLevelBox()) {
			drawZoomLevel(gc);
		}

		// last draw
		if (_settings.enableLastDraw()) {
			for (int i = 0; i < _eventListeners.size(); i++) {
				final IGanttEventListener listener = _eventListeners.get(i);
				listener.lastDraw(gc);
			}
		}

		// by default these are on, we flag them off when we know for sure we don't need to recalculate bounds
		_recalcScopes = false;
		_recalcSecBounds = false;

		if (_zoomLevelChanged) {
			_zoomLevelChanged = false;
			updateHorizontalScrollbar();

			// on zoom level change we update the position, as otherwise the next prev/next horizontal bar click will make it check against
			// the previous zoom level value, which is usually way off, and the entire chart jumps a huge distance which is obviously really bad.
			// as the zoom level has changed, all we do is to say "update the scrollbar, set the new selection position to what it is now after the update"
			// which solves the issue
			_hScrollHandler.resetScrollPosition();
		}

		if (_forceSBUpdate) {
			updateVerticalScrollBar(true);
			updateHorizontalScrollbar();
			_forceSBUpdate = false;
		}

		// long totaltime2 = System.currentTimeMillis();
		// String redraw = "redraw: " + (totaltime2 - totaltime1);

		// mRedrawCount++;
		// total += (time2 - time1);
		// System.err.println(redraw + " avg: " + (float) total / (float) redrawCount);
		// System.err.println(redraw);

		calculateVerticalInsertLocations();
		drawVerticalInsertMarkers(gc);
	}

	/**
	 * Flag whether to show planned dates or not. This will override any settings value and will cause a redraw.
	 *
	 * @param showPlanned true to show planned dates
	 */
	public void setShowPlannedDates(final boolean showPlanned) {
		if (_showPlannedDates == showPlanned) {
			return;
		}

		_showPlannedDates = showPlanned;

		// heavy as scrollbars need to update too
		flagForceFullUpdate();
		redraw();
	}

	/**
	 * Returns whether planned date drawing is currently on or off.
	 *
	 * @return true if on
	 */
	public boolean isShowingPlannedDates() {
		return _showPlannedDates;
	}

	/**
	 * Flag whether to show the number of days on events. This will override any settings value and will cause a redraw.
	 *
	 * @param showDates
	 */
	public void setShowDaysOnEvents(final boolean showDates) {
		_showNumDays = showDates;
		redraw();
	}

	/**
	 * Returns whether event day number drawing is currently on or off.
	 *
	 * @return true if on
	 */
	public boolean isShowingDaysOnEvents() {
		return _showNumDays;
	}

	/**
	 * Whether the chart has gantt sections or not
	 *
	 * @return true if has gantt sections
	 */
	public boolean isShowingGanttSections() {
		return !_ganttSections.isEmpty();
	}

	/*
	 * private void calculateVerticalLineLocations() {
	 * _verticalLineLocations.clear();
	 * _verticalWeekDividerLineLocations.clear();
	 *
	 * final Calendar temp = Calendar.getInstance(_defaultLocale);
	 * temp.setTime(_mainCalendar.getTime());
	 *
	 * switch (_currentView) {
	 * case ISettings.VIEW_DAY:
	 * break;
	 * case ISettings.VIEW_WEEK:
	 * break;
	 * case ISettings.VIEW_MONTH:
	 * break;
	 * case ISettings.VIEW_YEAR:
	 * break;
	 * case ISettings.VIEW_D_DAY:
	 * break;
	 * default:
	 * break;
	 * }
	 *
	 * while (true) {
	 * _verticalLineLocations.add(new Integer(current)); // NOPMD
	 * }
	 * }
	 */
	/**
	 * Draws the header but if calculateOnly is set it doesn't actually draw, it only calculates locations of things
	 */
	private void drawHeader(final GC gc, boolean calculateOnly) {
		_verticalLineLocations.clear();
		_verticalWeekDividerLineLocations.clear();

		final Rectangle headerBounds = new Rectangle(_mainBounds.x, _mainBounds.y, _mainBounds.width, _mainBounds.height);
		if (_settings.lockHeaderOnVerticalScroll()) {
			headerBounds.y = _lockedHeaderY;
		}

		// draw phases header (but not above normal header)
		if (hasGanttPhases() && !calculateOnly) {
			drawGanttPhases(gc, headerBounds, true, null);
		}

		if (_currentView == ISettings.VIEW_MINUTE) {
			// draw the day at the top
			if (!calculateOnly) {
				drawMinuteTopBoxes(gc, headerBounds);
			}
			// draw the hour ticks below
			drawMinuteBottomBoxes(gc, headerBounds, calculateOnly);
		} else if (_currentView == ISettings.VIEW_DAY) {
			// draw the day at the top
			if (!calculateOnly) {
				drawHourTopBoxes(gc, headerBounds);
			}
			// draw the hour ticks below
			drawHourBottomBoxes(gc, headerBounds, calculateOnly);
		} else if (_currentView == ISettings.VIEW_WEEK) {
			// draw the week boxes
			if (!calculateOnly) {
				drawWeekTopBoxes(gc, headerBounds);
			}
			// draw the day boxes
			drawWeekBottomBoxes(gc, headerBounds, calculateOnly);
		} else if (_currentView == ISettings.VIEW_MONTH) {
			// draws the month boxes at the top
			if (!calculateOnly) {
				drawMonthTopBoxes(gc, headerBounds);
			}
			// draws the monthly "week" boxes
			drawMonthBottomBoxes(gc, headerBounds, calculateOnly);
		} else if (_currentView == ISettings.VIEW_YEAR) {
			// draws the years at the top
			if (!calculateOnly) {
				drawYearTopBoxes(gc, headerBounds);
			}
			// draws the month boxes
			drawYearBottomBoxes(gc, headerBounds, calculateOnly);
		} else if (_currentView == ISettings.VIEW_D_DAY) {
			// draw D-day stuff, for Darren and anyone else who needs it
			if (!calculateOnly) {
				drawDDayTopBoxes(gc, headerBounds);
			}
			// draw the bottom d-day
			drawDDayBottomBoxes(gc, headerBounds, calculateOnly);
		}

		// draws the horizontal (usually black) lines at the top to make
		// things stand out a little
		if (!calculateOnly) {
			drawTopHorizontalLines(gc, headerBounds);
		}
	}

	private void showMenu(final int x, final int y, final GanttEvent event, final MouseEvent me) {
		if (!_settings.showMenuItemsOnRightClick()) {
			return;
		}

		killDialogs();

		// destroy old menu as to not leak memory or at least GC faster
		if (_rightClickMenu != null && !_rightClickMenu.isDisposed()) {
			_rightClickMenu.dispose();
		}

		_rightClickMenu = new Menu(getDisplay().getActiveShell(), SWT.POP_UP);

		// add new event
		if (event == null) {
			if (_settings.enableAddEvent()) {
				final MenuItem addEvent = new MenuItem(_rightClickMenu, SWT.PUSH);
				addEvent.setText(_languageManager.getAddEventMenuText());
				addEvent.addListener(SWT.Selection, new Listener() {
					public void handleEvent(final Event event) {
						// add event to chart
						final Calendar start = getDateAt(me.x);
						final Calendar end = getDateAt(me.x);
						end.add(Calendar.DATE, 1);

						addEvent(eventFactory.createGanttEvent(_parentChart, getSectionAt(me), _languageManager.getNewEventDefaultText(), start, end));
					}
				});

				new MenuItem(_rightClickMenu, SWT.SEPARATOR);
			}

			// add custom actions
			if (menuItemFactory != null) {
				menuItemFactory.addCustomMenuItems(_rightClickMenu);
				new MenuItem(_rightClickMenu, SWT.SEPARATOR);
			}
		}

		if (event != null) {
			// add custom actions
			if (eventMenuItemFactory != null) {
				eventMenuItemFactory.addCustomMenuItems(_rightClickMenu, event);
				new MenuItem(_rightClickMenu, SWT.SEPARATOR);
			}

			// We can't use JFace actions.. so we need to make copies.. Dirty
			// but at least not reinventing a wheel (as much)
			final Menu eventMenu = event.getMenu();
			final MenuItem[] items = eventMenu.getItems();

			if (items != null) {
				for (int i = 0; i < items.length; i++) {
					final MenuItem mItem = items[i];

					final MenuItem copy = new MenuItem(_rightClickMenu, SWT.PUSH);
					copy.setText(mItem.getText());
					copy.setImage(mItem.getImage());
					copy.setEnabled(mItem.getEnabled());
					copy.setAccelerator(mItem.getAccelerator());
					copy.setData(mItem.getData());
					copy.setSelection(mItem.getSelection());
					copy.addListener(SWT.Selection, new Listener() {
						public void handleEvent(final Event event) {
							// relay...
							mItem.notifyListeners(SWT.Selection, event);
						}
					});
				}
			}

			// if that's it, show the menu and exit
			if (!_settings.showDefaultMenuItemsOnEventRightClick()) {
				_rightClickMenu.setLocation(x, y);
				_rightClickMenu.setVisible(true);
				return;
			}

			if (items != null && items.length > 0) {
				new MenuItem(_rightClickMenu, SWT.SEPARATOR);
			}
		}

		if (_settings.enableZooming()) {
			// new MenuItem(menu, SWT.SEPARATOR);
			final MenuItem zoomIn = new MenuItem(_rightClickMenu, SWT.PUSH);
			zoomIn.setText(_languageManager.getZoomInMenuText());
			final MenuItem zoomOut = new MenuItem(_rightClickMenu, SWT.PUSH);
			zoomOut.setText(_languageManager.getZoomOutMenuText());
			final MenuItem zoomReset = new MenuItem(_rightClickMenu, SWT.PUSH);
			zoomReset.setText(_languageManager.getZoomResetMenuText());

			zoomIn.addListener(SWT.Selection, new Listener() {
				public void handleEvent(final Event event) {
					zoomHandler.zoomIn();
				}
			});
			zoomOut.addListener(SWT.Selection, new Listener() {
				public void handleEvent(final Event event) {
					zoomHandler.zoomOut();
				}
			});
			zoomReset.addListener(SWT.Selection, new Listener() {
				public void handleEvent(final Event event) {
					zoomHandler.resetZoom();
				}
			});

			new MenuItem(_rightClickMenu, SWT.SEPARATOR);
		}

		final MenuItem showPlaque = new MenuItem(_rightClickMenu, SWT.CHECK);
		showPlaque.setText(_languageManager.getShowNumberOfDaysOnEventsMenuText());
		showPlaque.setSelection(_showNumDays);
		showPlaque.addListener(SWT.Selection, new Listener() {
			public void handleEvent(final Event event) {
				_showNumDays = !_showNumDays; // NOPMD
				showPlaque.setSelection(_showNumDays);
				redraw();
			}
		});

		new MenuItem(_rightClickMenu, SWT.SEPARATOR);

		final MenuItem showEsts = new MenuItem(_rightClickMenu, SWT.CHECK);
		showEsts.setText(_languageManager.getShowPlannedDatesMenuText());
		showEsts.setSelection(_showPlannedDates);
		showEsts.addListener(SWT.Selection, new Listener() {
			public void handleEvent(final Event event) {
				setShowPlannedDates(!isShowingPlannedDates());
				showEsts.setSelection(_showPlannedDates);
				redraw();
			}
		});

		new MenuItem(_rightClickMenu, SWT.SEPARATOR);

		final MenuItem showThreeDee = new MenuItem(_rightClickMenu, SWT.CHECK);
		showThreeDee.setText(_languageManager.get3DMenuText());
		showThreeDee.setSelection(_threeDee);
		showThreeDee.addListener(SWT.Selection, new Listener() {
			public void handleEvent(final Event event) {
				_threeDee = !_threeDee; // NOPMD
				redraw();
			}
		});

		if (_settings.showDeleteMenuOption() && event != null) {
			new MenuItem(_rightClickMenu, SWT.SEPARATOR);

			final MenuItem delete = new MenuItem(_rightClickMenu, SWT.PUSH);
			delete.setText(_languageManager.getDeleteMenuText());
			delete.addListener(SWT.Selection, new Listener() {
				public void handleEvent(final Event e) {
					if (!_selectedEvents.isEmpty()) {
						for (int i = 0; i < _eventListeners.size(); i++) {
							final IGanttEventListener listener = _eventListeners.get(i);
							listener.eventsDeleteRequest(_selectedEvents, me);
						}
					}
				}
			});
		}

		if (_settings.showPropertiesMenuOption() && !_selectedEvents.isEmpty()) {
			new MenuItem(_rightClickMenu, SWT.SEPARATOR);

			final MenuItem properties = new MenuItem(_rightClickMenu, SWT.PUSH);
			properties.setText(_languageManager.getPropertiesMenuText());
			properties.addListener(SWT.Selection, new Listener() {
				public void handleEvent(final Event event) {
					if (!_selectedEvents.isEmpty()) {
						for (int i = 0; i < _eventListeners.size(); i++) {
							final IGanttEventListener listener = _eventListeners.get(i);
							listener.eventPropertiesSelected(_selectedEvents);
						}
					}
				}
			});
		}

		_rightClickMenu.setLocation(x, y);
		_rightClickMenu.setVisible(true);
	}

	/**
	 * Returns the a list of all currently selected events, or an emtpy list if none.
	 *
	 * @return GanttEvent or null
	 */
	public List<Object> getSelectedEvents() {
		checkWidget();
		return _selectedEvents;
	}

	/**
	 * Sets the top visible item in the chart and scrolls to show it. Passing
	 * SWT.NONE prevents any horizontal alignment from occurring.
	 *
	 * @param ge Event to show
	 * @param yOffset y offset modifier
	 * @param side one of <code>SWT.LEFT</code>, <code>SWT.CENTER</code>,
	 *            <code>SWT.RIGHT</code>, <code>SWT.NONE</code>
	 */
	public void setTopItem(final GanttEvent ge, final int yOffset, final int side) {

		// fix to issue where setting same event would cause chart to jump
		// vertically. Seems we need to handle locked vs. unlocked headers
		// differently due to changes made elsewhere
		if (_settings.lockHeaderOnVerticalScroll()) {
			vScrollToY(ge.getY() + yOffset, false);
		} else {
			final int takeOff = getHeaderHeight();
			vScrollToY(ge.getY() - _vScrollPos + yOffset - takeOff, false);
		}

		if (side != SWT.NONE) {
			internalSetDate(ge.getActualStartDate(), side, true, false);
		}
		redraw();
	}

	/**
	 * Sets the top visible item in the chart and scrolls to show it. Passing
	 * SWT.NONE prevents any horizontal alignment from occurring.
	 *
	 * @param ge Event to show
	 * @param side one of <code>SWT.LEFT</code>, <code>SWT.CENTER</code>,
	 *            <code>SWT.RIGHT</code>, <code>SWT.NONE</code>
	 */
	public void setTopItem(final GanttEvent ge, final int side) {
		setTopItem(ge, 0, side);
	}

	/**
	 * Scrolls the chart to the selected item regardless if it is visible or not.
	 *
	 * @param ge GanttEvent to scroll to.
	 * @param side one of <code>SWT.LEFT</code>, <code>SWT.CENTER</code>, <code>SWT.RIGHT</code>
	 */
	public void showEvent(final GanttEvent ge, final int side) {
		// TODO: Side doesn't work.. missing something
		if (ge.getActualStartDate() == null) {
			return;
		}

		vScrollToY(ge.getY(), false);
		internalSetDate(ge.getActualStartDate(), side, true, false);
		_recalcScopes = true;
		_recalcSecBounds = true;
		redraw();
	}

	// moves the chart to the given y position, takes various spacers into account
	private void vScrollToY(final int yPos, final boolean redraw) {
		int y = yPos;

		if (_settings.lockHeaderOnVerticalScroll()) {
			y -= getHeaderHeight();
		} else {
			y -= _mainBounds.y - getHeaderHeight();
		}

		// we need to take the "previous" scroll location into account
		y += _vScrollPos;

		final int max = _vScrollBar.getMaximum() - _vScrollBar.getThumb() + _eventHeight + _eventSpacer;

		if (y < 0) {
			y = 0;
		}
		if (y > max) {
			y = max;
		}

		_vScrollPos = y;
		_lastVScrollPos = _vScrollPos;
		_vScrollBar.setSelection(y);

		flagForceFullUpdate();

		if (redraw) {
			redraw();
		}
	}

	@Override
	public Rectangle getBounds() {
		if (_mainBounds == null) {
			return super.getBounds();
		}

		return new Rectangle(0, 0, super.getBounds().width, _bottomMostY);
	}

	// calculates all gantt section bounds
	private void calculateSectionBounds(final GC gc, final Rectangle bounds) {
		int yStart = getHeaderHeight() == 0 ? bounds.y : bounds.y + getHeaderHeight();

		for (int i = 0; i < _ganttSections.size(); i++) {
			final GanttSection gs = _ganttSections.get(i);

			Point extent = null;
			if (gs.needsNameUpdate() || gs.getNameExtent() == null) {
				if (_settings.drawSectionBar()) {
					extent = gc.textExtent(gs.getName(), SWT.DRAW_DELIMITER);
				} else {
					extent = new Point(0, 0);
				}
				gs.setNameExtent(extent);
				gs.setNeedsNameUpdate(false);
			} else {
				extent = gs.getNameExtent();
			}
			// set the base height depending on the text orientation
			int strHeight = extent.x;
			if (gs.getTextOrientation() == SWT.HORIZONTAL) {
				strHeight = extent.y;
			}

			final int gsHeight = gs.getEventsHeight(_settings);
			int height = Math.max(gsHeight, strHeight);

			// if the name of the section is so large that it basically defines the size of the section, space out the text slightly
			if (strHeight > gsHeight) {
				height += _settings.getSectionTextSpacer();
			}

			final Rectangle gsBounds = new Rectangle(bounds.x, yStart, bounds.width, height);
			gs.setBounds(gsBounds);

			yStart += height - 1;
			yStart += _settings.getSectionBarDividerHeight();
		}
	}

	// calculates days visible
	/*
	 * private void calculateDaysVisible(final Rectangle bounds) {
	 * final Calendar temp = Calendar.getInstance(_defaultLocale);
	 * temp.setTime(_mainCalendar.getTime());
	 *
	 * int current = 0;
	 *
	 * while (true) {
	 * temp.add(Calendar.DATE, 1);
	 * current += _dayWidth;
	 *
	 * _daysVisible++;
	 *
	 * if (current > bounds.width) {
	 * break;
	 * }
	 * }
	 * }
	 */

	// the height of the header or zero if not visible
	public int getHeaderHeight() {
		if (!_settings.drawHeader()) {
			return 0;
		}

		// we account for the drawTopLines bottom line which is at the bottom of the header, which increases our height by 1 pixel
		int ret = _settings.getHeaderDayHeight() + _settings.getHeaderMonthHeight() + 1;
		if (hasGanttPhases()) {
			ret += _settings.getPhasesHeaderHeight();
		}

		return ret;
	}

	private void drawFills(final GC gc, final Rectangle bounds, final GanttSection gs) {
		internalDrawFills(gc, bounds, gs);
	}

	// background fills
	private void drawFills(final GC gc, final Rectangle bounds) {
		internalDrawFills(gc, bounds, null);
	}

	private void internalDrawFills(final GC gc, final Rectangle bounds, final GanttSection gs) {
		final int dayWidth = _currentView == ISettings.VIEW_WEEK || _currentView == ISettings.VIEW_D_DAY ? _dayWidth : _monthDayWidth;
		final int maxX = bounds.width + dayWidth; // we need to draw beyond 1 day as the days at the edge of the viewport also needs to be filled in case a half-day is visible there
		int startX = bounds.x;

		int offset = 0;
		if (gs == null) {
			offset = _vScrollPos;
			if (offset > getHeaderHeight()) {
				offset = getHeaderHeight();
			}
		} else {
			offset = _vScrollPos;
		}

		final int startY = bounds.y - offset;// getHeaderHeight() == 0 ? bounds.y : bounds.y + getHeaderHeight() + 1;
		final int heightY = gs == null ? bounds.height : gs.getBounds().height;

		// System.err.println(startY + " " + heightY + " " + gs.getName() + " " + gs.getBounds());

		// fill all of it, then we just have to worry about weekends, much
		// faster in terms of drawing time
		// only two views have weekend colors, week and month view. Hour and
		// year view don't.
		switch (_currentView) {
			case ISettings.VIEW_DAY:
				gc.setForeground(getDayBackgroundGradient(Calendar.MONDAY, true, gs));
				gc.setBackground(getDayBackgroundGradient(Calendar.MONDAY, false, gs));

				gc.fillGradientRectangle(startX, startY, maxX, heightY, true);
				return;
			case ISettings.VIEW_MINUTE:
				gc.setForeground(getDayBackgroundGradient(Calendar.MONDAY, true, gs));
				gc.setBackground(getDayBackgroundGradient(Calendar.MONDAY, false, gs));

				gc.fillGradientRectangle(startX, startY, maxX, heightY, true);
				return;
			case ISettings.VIEW_WEEK:
				break;
			case ISettings.VIEW_MONTH:
				break;
			case ISettings.VIEW_YEAR:
				gc.setForeground(getDayBackgroundGradient(Calendar.MONDAY, true, gs));
				gc.setBackground(getDayBackgroundGradient(Calendar.MONDAY, false, gs));

				gc.fillGradientRectangle(startX, startY, maxX, heightY, true);
				return;
			default:
				break;
		}

		final Calendar temp = Calendar.getInstance(_defaultLocale);
		temp.setTime(_mainCalendar.getTime());

		// fill all of it first, then do weekends
		gc.setForeground(getDayBackgroundGradient(Calendar.MONDAY, true, gs));
		gc.setBackground(getDayBackgroundGradient(Calendar.MONDAY, false, gs));

		gc.fillGradientRectangle(startX, startY, maxX, heightY, true);

		while (true) {
			final int day = temp.get(Calendar.DAY_OF_WEEK);

			if (_selHeaderDates.contains(temp)) {
				gc.setForeground(_colorManager.getSelectedDayColorTop());
				gc.setBackground(_colorManager.getSelectedDayColorBottom());
				gc.fillGradientRectangle(startX, startY, dayWidth, heightY, true);
			} else {
				if (_currentView != ISettings.VIEW_D_DAY) {
					if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
						gc.setForeground(getDayBackgroundGradient(day, true, gs));
						gc.setBackground(getDayBackgroundGradient(day, false, gs));

						// fill the whole thing all the way down
						gc.fillGradientRectangle(startX, startY, dayWidth, heightY, true);
					}

					final Holiday h = getHoliday(temp);
					if (h != null) {
						gc.setForeground(getHolidayBackgroundGradient(true, gs));
						gc.setBackground(getHolidayBackgroundGradient(false, gs));

						// fill the whole thing all the way down
						gc.fillGradientRectangle(startX, startY, dayWidth, heightY, true);

						h.updateBounds(new Rectangle(startX, startY, dayWidth, heightY));
					}

					if (DateHelper.isToday(temp)) {
						gc.setForeground(_todayBGColorTop);
						gc.setBackground(_todayBGColorBottom);
						gc.fillGradientRectangle(startX + 1, startY, dayWidth - 1, heightY, true);
					}
				}
			}

			startX += dayWidth;

			temp.add(Calendar.DATE, 1);

			if (startX > maxX) {
				break;
			}
		}

	}

	private Holiday getHoliday(Calendar day) {
		if (holidays != null) {
			for (final Holiday h : holidays) {
				if (DateHelper.sameDate(day, h.getDate())) {
					return h;
				}
			}
		}
		return null;
	}

	// draws the zoom level box in the corner, only shown when zooming
	private void drawZoomLevel(final GC gc) {

		final int bottomLeftY = _mainBounds.height - Constants.HELP_HEIGHT - 3;
		int bottomLeftX = _mainBounds.x + 3;

		// if there is no sections where we'd normally draw the zoom box, we can move it in to the left, otherwise we leave it as it should not cover the section bar
		if (!_ganttSections.isEmpty() && _bottomMostY < bottomLeftY) {
			bottomLeftX = 3;
		}

		final StringBuffer buf = new StringBuffer();
		buf.append(_languageManager.getZoomLevelText());
		buf.append(": ");
		if (_zoomLevel == ISettings.MIN_ZOOM_LEVEL) {
			buf.append(_languageManager.getZoomMaxText());
		} else {
			if (_zoomLevel == ISettings.MAX_ZOOM_LEVEL) {
				buf.append(_languageManager.getZoomMinText());
			} else {
				buf.append(_zoomLevel);
			}
		}

		final String qText = buf.toString();
		final Point point = gc.stringExtent(qText);

		final int helpWidth = point.x + 5;

		final Rectangle outer = new Rectangle(bottomLeftX, bottomLeftY, helpWidth, Constants.HELP_HEIGHT);
		final Rectangle inner = new Rectangle(bottomLeftX + 1, bottomLeftY + 1, helpWidth - 1, Constants.HELP_HEIGHT - 1);

		_zoomLevelArea = outer;

		gc.setForeground(_colorManager.getZoomBackgroundColorTop());
		gc.setBackground(_colorManager.getZoomBackgroundColorBottom());
		gc.fillGradientRectangle(inner.x, inner.y, inner.width, inner.height, true);

		gc.setForeground(_colorManager.getZoomBorderColor());
		gc.drawRectangle(outer);

		gc.setForeground(_colorManager.getZoomTextColor());
		gc.drawString(qText, bottomLeftX + 4, bottomLeftY + 3, true);
	}

	// draws the section column on left or right side
	private void drawSectionColumn(final GC gc, final Rectangle bounds, final boolean columnOnly, final boolean forceUsageOfBounds, final boolean drawCornerOnly, final boolean force) {
		if (_ganttSections.isEmpty()) {
			return;
		}

		final boolean rightSide = _settings.getSectionSide() == SWT.RIGHT;

		// don't draw this, it will be force-drawn at the end.. this is a hack to get around the bar
		// drawing in the middle, also speeds it up as we only draw it once
		if (_savingChartImage && rightSide && !force) {
			return;
		}

		int barWidth = _settings.drawSectionBar() ? _settings.getSectionBarWidth() - 1 : 3;

		final int horiSpacer = 3;

		// calculate max width if any section is horizontal
		for (int i = 0; i < _ganttSections.size(); i++) {
			final GanttSection gs = _ganttSections.get(i);
			if (gs.getTextOrientation() == SWT.HORIZONTAL) {
				Point p = null;
				if (gs.needsNameUpdate() || gs.getNameExtent() == null) {
					if (_settings.drawSectionBar()) {
						p = gc.textExtent(gs.getName(), SWT.DRAW_DELIMITER);
					} else {
						p = new Point(0, 0);
					}
					gs.setNameExtent(p);
					gs.setNeedsNameUpdate(false);
				} else {
					p = gs.getNameExtent();
				}

				if (gs.getAdditionalImage() != null) {
					barWidth = Math.max(barWidth, p.x + horiSpacer * 2 + gs.getAdditionalImage().getBounds().width + 2 );
				} else {
					barWidth = Math.max(barWidth, p.x + horiSpacer * 2);
				}
			}
		}

		int xMax = barWidth;

		if (_settings.drawSectionDetails() && rightSide) {
			xMax += _settings.getSectionDetailWidth();
		}

		int lineLoc = getHeaderHeight() == 0 ? bounds.y : bounds.y + getHeaderHeight() - 1;
		int yStart = lineLoc;
		yStart -= _vScrollPos;

		int sectionBarX = 0;

		if (rightSide) {
			if (forceUsageOfBounds) {
				sectionBarX = bounds.width - xMax;
			} else {
				sectionBarX = super.getClientArea().width - xMax;
			}
		} else {
			sectionBarX = 0;
		}

		int neg = 0;
		if (rightSide) {
			neg = -xMax;
		}

		// take space for section details into account
		int sectionDetailX = 0;
		if (_settings.drawSectionDetails()) {
			if (rightSide) {
				sectionDetailX = sectionBarX + _settings.getSectionBarWidth();
			} else {
				sectionDetailX = sectionBarX;
				sectionBarX += _settings.getSectionDetailWidth();
			}
		}

		final int sectionStartXPos = (rightSide ? sectionBarX : sectionDetailX) - 1;

		final GanttSection bottomSection = _ganttSections.get(_ganttSections.size() - 1);

		if (drawCornerOnly) {
			lineLoc += _vScrollPos;
		}

		// top corner
		gc.setForeground(_colorManager.getNonActiveSessionBarColorLeft());
		gc.setBackground(_colorManager.getNonActiveSessionBarColorRight());

		int bottomPos = _settings.drawGanttSectionBarToBottom() ? _mainBounds.y + _mainBounds.height : lineLoc;

		// if we're only drawing a corner, do not draw to the bottom regardless of settings or we'll paint over the text and section markers etc.
		// this fix to bugzilla #304804 - Thanks Wim!
		if (drawCornerOnly) {
			bottomPos = getHeaderHeight();// lineLoc - _vScrollPos;
		}

		gc.fillGradientRectangle(sectionStartXPos, 0, xMax + 1 + sectionBarX, bottomPos, false);

		gc.setForeground(_colorManager.getTopHorizontalLinesColor());
		// vertical
		gc.drawLine(sectionBarX + xMax + neg, 0, sectionBarX + xMax + neg, bottomSection.getBounds().y + bottomSection.getBounds().height - 1);

		if (!drawCornerOnly) {

			for (int i = 0; i < _ganttSections.size(); i++) {
				final GanttSection gs = _ganttSections.get(i);

				final int gsHeight = gs.getBounds().height;

				gc.setForeground(_colorManager.getActiveSessionBarColorLeft());
				gc.setBackground(_colorManager.getActiveSessionBarColorRight());
				gc.fillGradientRectangle(sectionBarX, yStart, xMax, gsHeight, false);

				// draw section details
				if (_settings.drawSectionDetails()) {
					gc.setForeground(_colorManager.getSectionDetailAreaForegroundColor(gs));
					gc.setBackground(_colorManager.getSectionDetailAreaBackgroundColor(gs));

					gc.fillGradientRectangle(sectionDetailX, yStart, sectionDetailX + sectionBarX, gsHeight, _colorManager.drawSectionDetailGradientTopDown());

					drawSectionDetails(gc, sectionDetailX, yStart, gs);
				}

				gc.setForeground(_textColor);

				int xStart = barWidth / 2;

				Point extent = null;
				if (gs.needsNameUpdate() || gs.getNameExtent() == null) {
					extent = gc.textExtent(gs.getName(), SWT.DRAW_DELIMITER);
					gs.setNameExtent(extent);
					gs.setNeedsNameUpdate(false);
				} else {
					extent = gs.getNameExtent();
				}

				// zero length name causes exceptions, set it to 1
				if (extent.x == 0) {
					extent.x = 1;
				}

				if (gs.getTextOrientation() == SWT.VERTICAL) {
					// draw vertical text (I tried using Transform.rotate stuff here earlier, but it's so incredibly slow that we can't use it)
					// and not only that, but the kerning on vertical text is completely whacked with letter overlapping, so right now it's unusable
					Image image = null;

					// only create the images if we don't have one from before, or if the name has changed since last time
					if (gs.getNameImage() == null || gs.needsNameUpdate()) {

						final Image textImage = new Image(getDisplay(), extent.x, xMax - 2);
						final GC gcTemp = new GC(textImage);

						if (rightSide) {
							gcTemp.setForeground(_colorManager.getActiveSessionBarColorRight());
							gcTemp.setBackground(_colorManager.getActiveSessionBarColorLeft());
						} else {
							gcTemp.setForeground(_colorManager.getActiveSessionBarColorLeft());
							gcTemp.setBackground(_colorManager.getActiveSessionBarColorRight());
						}
						gcTemp.fillGradientRectangle(0, 0, extent.x, xMax - 2, true);
						gcTemp.setForeground(_textColor);
						gcTemp.drawText(gs.getName(), 0, gs.getAdditionalImage()!=null ? (gs.getAdditionalImage().getBounds().width) : 0 , true);
						gcTemp.dispose();

						final ImageData id = textImage.getImageData();
						image = new Image(getDisplay(), rotate(id, rightSide ? SWT.RIGHT : SWT.LEFT)); // NOPMD
                        if(gs.getAdditionalImage()!=null) {
                        	final GC gcAdditionalImage = new GC(image);
                        	gcAdditionalImage.drawImage(gs.getAdditionalImage(), 0, image.getBounds().height/2 - gs.getAdditionalImage().getBounds().height/2);
                        	gcAdditionalImage.dispose();
                        }
                        gs.setNameImage(image);						
					} else {
						image = gs.getNameImage();
					}

					xStart -= image.getBounds().width / 2 - 2;
					final int textLocY = gsHeight / 2 - image.getBounds().height / 2;

					gc.drawImage(image, sectionBarX + xStart - 1, yStart + textLocY);
				} else if (gs.getTextOrientation() == SWT.HORIZONTAL) {
					xStart -= extent.x / 2;
					if (gs.getAdditionalImage() != null) {
						gc.drawImage(gs.getAdditionalImage(), 2, yStart + (gsHeight / 2) - (gs.getAdditionalImage().getBounds().height / 2));
					}
					gc.drawText(gs.getName(), gs.getAdditionalImage() != null ? gs.getAdditionalImage().getBounds().width + 5 : horiSpacer, yStart + (gsHeight / 2) - (gs.getNameExtent().y / 2), true);
				}

				yStart += gsHeight - 1;

				int width = bounds.x + bounds.width;
				if (rightSide && !forceUsageOfBounds) {
					width = super.getClientArea().width;
				}

				// draw center divider
				if (!columnOnly) {
					gc.setForeground(_colorManager.getTopHorizontalLinesColor());

					if (i != _ganttSections.size() - 1 && _settings.getSectionBarDividerHeight() != 0) {
						gc.setForeground(_colorManager.getSessionBarDividerColorLeft());
						gc.setBackground(_colorManager.getSessionBarDividerColorRight());
						gc.fillGradientRectangle(0, yStart, width, _settings.getSectionBarDividerHeight(), false);

						gc.setForeground(_colorManager.getTopHorizontalLinesColor());
						gc.drawLine(0, yStart, width, yStart);
						yStart += _settings.getSectionBarDividerHeight();
						gc.drawLine(0, yStart - 1, width, yStart - 1);
					} else {
						// the last line
						yStart += 1;
						gc.drawLine(0, yStart, width, yStart);
						yStart += _settings.getSectionBarDividerHeight();
					}
				}
			}

		}

		gc.setForeground(_colorManager.getTopHorizontalLinesColor());
		// horizontal
		if (_settings.drawHeader()) {
			gc.drawLine(sectionStartXPos, bounds.y, sectionBarX + xMax, bounds.y);
		}

		gc.drawLine(sectionStartXPos, lineLoc - _vScrollPos, sectionBarX + xMax, lineLoc - _vScrollPos);

	}

	private void drawSectionDetails(GC gc, int x, int y, GanttSection section) {
		String title = _settings.getSectionDetailTitle();
		String content = _settings.getSectionDetailText();

		final ISectionDetailContentReplacer sdcr = _settings.getSectionDetailContentReplacer();
		if (sdcr != null) {
			title = sdcr.replaceSectionDetailPlaceHolder(section, title);
			content = sdcr.replaceSectionDetailPlaceHolder(section, content);
		} else {
			title = title != null ? title.replaceAll("#name#", section.getName()) : "";
			if (content != null) {
				content = content.replaceAll("#name#", section.getName());
				content = content.replaceAll("#ne#", "" + section.getEvents().size());
			}
		}

		int xPos = x + 4;
		int yPos = y + 2;
		Point titleEndPos = new Point(xPos, yPos);
		if (title != null && title.length() > 0) {
			titleEndPos = TextPainterHelper.drawText(gc, title, xPos, yPos);
		}

		if (content != null && content.length() > 0) {
			// first we space it vertically
			xPos += 8;

			yPos += titleEndPos.y * 1.5;

			final StringTokenizer tokenizer = new StringTokenizer(content, "\n"); //$NON-NLS-1$

			while (tokenizer.hasMoreTokens()) {
				final String token = tokenizer.nextToken();
				final Point extent = TextPainterHelper.drawText(gc, token, xPos, yPos);
				yPos += extent.y;
			}
		}

		if (_settings.showSectionDetailMore() && section.getData() != null) {
			final Point moreExtent = gc.textExtent(_languageManager.getSectionDetailMoreText());
			yPos += moreExtent.y * 0.3;
			gc.drawText(_languageManager.getSectionDetailMoreText(), xPos, yPos, true);

			final Rectangle plusRectangle = new Rectangle(xPos + moreExtent.x + 8, yPos + moreExtent.y / 2 - 3, 8, 8);

			sectionDetailMoreIcons.put(section, plusRectangle);
			gc.drawRectangle(plusRectangle);
			gc.drawLine(plusRectangle.x + 4, plusRectangle.y + 2, plusRectangle.x + 4, plusRectangle.y + 6);
			gc.drawLine(plusRectangle.x + 2, plusRectangle.y + 4, plusRectangle.x + 6, plusRectangle.y + 4);

		}
	}

	private void drawHorizontalLines(final GC gc, final Rectangle bounds) {
		gc.setForeground(_lineColor);

		final List<GanttGroup> usedGroups = new ArrayList<>();

		for (int i = 0; i < _ganttEvents.size(); i++) {
			final GanttEvent ge = _ganttEvents.get(i);
			// could be a feature, although a lame one, if this is enabled, only visible events horizontal lines will be drawn
			// if (!ge.getBounds().intersects(bounds))
			// continue;

			if (ge.isHidden()) {
				continue;
			}

			int yExtra = ge.isAutomaticRowHeight() ? 0 : ge.getFixedRowHeight();
			yExtra -= _vScrollPos;

			if (ge.getGanttGroup() != null) {
				if (usedGroups.contains(ge.getGanttGroup())) {
					continue;
				} else {
					usedGroups.add(ge.getGanttGroup());
				}
			}

			if (ge.isAutomaticRowHeight()) {
				yExtra += _eventSpacer / 2;
			}

			// top line. we don't need to check for fixed row heights as it'll always be correct at the top
			gc.drawLine(bounds.x, ge.getHorizontalLineBottomY() + yExtra, bounds.x + bounds.width, ge.getHorizontalLineBottomY() + yExtra);

			// last event, draw bottom line as well
			if (i == _ganttEvents.size() - 1) {
				int y = ge.getHorizontalLineBottomY() - _eventSpacer;
				if (y > ge.getHorizontalLineBottomY() + yExtra) {
					if (!ge.isAutomaticRowHeight()) {
						y += ge.getFixedRowHeight();
					}
					gc.drawLine(bounds.x, y, bounds.x + bounds.width, y);
				}
			}
		}
	}

	// draws vertical lines for separating days, hours, months, years etc
	private void drawVerticalLines(final GC gc, final Rectangle bounds, final boolean applyVscroll) {
		// int xMax = bounds.width + bounds.x;
		// space it out 1 or more or else it will draw over the bottom horizontal line of the header
		final int yStart = bounds.y - (applyVscroll ? _vScrollPos : 0);
		final int height = bounds.height + yStart - 1 + (applyVscroll ? _vScrollPos : 0);

		if (_currentView == ISettings.VIEW_MINUTE || _currentView == ISettings.VIEW_WEEK || _currentView == ISettings.VIEW_MONTH || _currentView == ISettings.VIEW_DAY || _currentView == ISettings.VIEW_D_DAY) {
			// normal day lines
			gc.setForeground(_lineColor);
			for (int i = 0; i < _verticalLineLocations.size(); i++) {
				final int current = _verticalLineLocations.get(i).intValue();
				gc.drawLine(current, yStart, current, height);
			}

			// "weekend" lines
			gc.setForeground(_lineWkDivColor);
			final Object[] weekLocs = _verticalWeekDividerLineLocations.toArray();

			if (_useAlpha) {
				gc.setAlpha(_colorManager.getWeekDividerAlpha());
			}
			for (int i = 0; i < weekLocs.length; i++) {
				final int current = ((Integer) weekLocs[i]).intValue();
				gc.drawLine(current, yStart, current, height);
			}
			if (_useAlpha) {
				gc.setAlpha(255);
				gc.setAdvanced(false);
			}

			final Calendar today = Calendar.getInstance(_defaultLocale);

			drawTodayLine(gc, bounds, getStartingXFor(today), today.get(Calendar.DAY_OF_WEEK), _lineTodayColor);

			if (_settings.getPeriodStart() != null) {
				drawTodayLine(gc, bounds, getStartingXFor(_settings.getPeriodStart()), _settings.getPeriodStart().get(Calendar.DAY_OF_WEEK), _linePeriodColor);
			}
			if (_settings.getPeriodEnd() != null) {
				drawTodayLine(gc, bounds, getStartingXFor(_settings.getPeriodEnd()), _settings.getPeriodEnd().get(Calendar.DAY_OF_WEEK), _linePeriodColor);
			}
		} else if (_currentView == ISettings.VIEW_YEAR) {
			for (int i = 0; i < _verticalLineLocations.size(); i++) {
				gc.setForeground(_lineWkDivColor);

				final int x = _verticalLineLocations.get(i).intValue();
				gc.drawLine(x, yStart, x, height);
			}

			final Calendar today = Calendar.getInstance(_defaultLocale);
			drawTodayLine(gc, bounds, getStartingXFor(today), today.get(Calendar.DAY_OF_WEEK), _lineTodayColor);

			if (_settings.getPeriodStart() != null) {
				drawTodayLine(gc, bounds, getStartingXFor(_settings.getPeriodStart()), _settings.getPeriodStart().get(Calendar.DAY_OF_WEEK), _linePeriodColor);
			}
			if (_settings.getPeriodEnd() != null) {
				drawTodayLine(gc, bounds, getStartingXFor(_settings.getPeriodEnd()), _settings.getPeriodEnd().get(Calendar.DAY_OF_WEEK), _linePeriodColor);
			}
		}
	}

	// year
	private void drawYearBottomBoxes(final GC gc, final Rectangle bounds, boolean calculateOnly) {
		final int xMax = bounds.width + bounds.x;
		int current = bounds.x;
		final int topY = bounds.y + _settings.getHeaderMonthHeight();
		final int heightY = _settings.getHeaderDayHeight();
		_daysVisible = 0;

		final Calendar temp = Calendar.getInstance(_defaultLocale);
		temp.setTime(_mainCalendar.getTime());

		temp.set(Calendar.DAY_OF_MONTH, 1);

		gc.setForeground(_txtHeaderBGColorTop);
		gc.setBackground(_txtHeaderBGColorBottom);
		gc.fillGradientRectangle(0, topY, xMax, heightY, true);

		gc.setBackground(_wkBGColorTop);

		while (true) {
			if (temp.get(Calendar.DATE) == 1) {
				_verticalLineLocations.add(new Integer(current));
				if (!calculateOnly) {
					gc.setForeground(_colorManager.getYearTimeDividerColor());
					gc.drawLine(current, topY, current, topY + heightY);
					gc.setForeground(_textColor);
					gc.drawString(getDateString(temp, false), current + 4, topY + 3, true);
				}
			}

			final int monthMax = temp.getActualMaximum(Calendar.DATE);
			_daysVisible += monthMax;
			temp.add(Calendar.MONTH, 1);
			current += _yearDayWidth * monthMax;

			if (current > xMax) {
				_endCalendar = temp;
				break;
			}
		}
	}

	// year
	private void drawYearTopBoxes(final GC gc, final Rectangle bounds) {
		final int xMax = bounds.width + bounds.x;
		int current = bounds.x;
		final int topY = bounds.y;
		final int bottomY = _settings.getHeaderMonthHeight();

		final Calendar temp = Calendar.getInstance(_defaultLocale);
		temp.setTime(_mainCalendar.getTime());

		temp.set(Calendar.DAY_OF_MONTH, 1);

		gc.setForeground(_txtHeaderBGColorTop);
		gc.setBackground(_txtHeaderBGColorBottom);
		gc.fillGradientRectangle(current, topY + 1, xMax, bottomY - 1, true);

		int lastYear = -1;
		while (true) {
			// draw month at beginning of month
			if (temp.get(Calendar.YEAR) != lastYear) {
				gc.setForeground(_colorManager.getTickMarkColor());
				gc.drawLine(current, topY + _settings.getVerticalTickMarkOffset(), current, topY + _settings.getHeaderDayHeight());
				gc.setForeground(_textColor);
				gc.drawString(getDateString(temp, true), current + 4, topY + 3, true);
			}

			lastYear = temp.get(Calendar.YEAR);

			final int monthMax = temp.getActualMaximum(Calendar.DAY_OF_MONTH);
			_daysVisible += monthMax;
			temp.add(Calendar.DATE, monthMax);
			current += _yearDayWidth * monthMax;

			if (current > xMax) {
				break;
			}
		}
	}

	// month
	private void drawMonthBottomBoxes(final GC gc, final Rectangle bounds, boolean calculateOnly) {
		final int xMax = bounds.width + bounds.x;
		int current = bounds.x;
		final int topY = bounds.y + _settings.getHeaderMonthHeight();
		final int heightY = _settings.getHeaderDayHeight();

		// get the offset to draw things at
		final Calendar temp = Calendar.getInstance(_defaultLocale);
		temp.setTime(_mainCalendar.getTime());
		final Calendar temp2 = Calendar.getInstance(_defaultLocale);
		temp2.setTime(_mainCalendar.getTime());
		temp2.set(Calendar.DAY_OF_WEEK, temp.getFirstDayOfWeek());
		final int days = (int) DateHelper.daysBetween(temp, temp2);
		current += days * _monthDayWidth;

		temp.set(Calendar.DAY_OF_WEEK, temp.getFirstDayOfWeek());

		while (true) {
			final int curDay = temp.get(Calendar.DAY_OF_WEEK);

			_verticalLineLocations.add(new Integer(current));
			// only change dates when week changes, as we don't change date
			// string for every different day
			if (curDay == temp.getFirstDayOfWeek()) {
				_verticalWeekDividerLineLocations.add(new Integer(current));
				if (!calculateOnly) {
					gc.setForeground(_colorManager.getMonthTimeDividerColor());
					gc.drawRectangle(current, topY, _monthWeekWidth, heightY);
					gc.setForeground(_txtHeaderBGColorTop);
					gc.setBackground(_txtHeaderBGColorBottom);
					gc.fillGradientRectangle(current + 1, topY + 1, _monthWeekWidth - 1, heightY - 1, true);
					gc.setForeground(_textColor);

					gc.drawString(getDateString(temp, false), current + 4, topY + 3, true);
				}
			}

			temp.add(Calendar.DATE, 1);

			current += _monthDayWidth;

			if (current > xMax) {
				_endCalendar = temp;
				break;
			}
		}
	}

	// month
	private void drawMonthTopBoxes(final GC gc, final Rectangle bounds) {
		final int xMax = bounds.width + bounds.x;
		int current = bounds.x;
		final int topY = bounds.y;
		final int bottomY = _settings.getHeaderMonthHeight();
		_daysVisible = 0;

		gc.setForeground(_txtHeaderBGColorTop);
		gc.setBackground(_txtHeaderBGColorBottom);
		gc.fillGradientRectangle(current, topY + 1, xMax, bottomY - 1, true);

		// get the offset to draw things at
		final Calendar temp = Calendar.getInstance(_defaultLocale);
		temp.setTime(_mainCalendar.getTime());
		final Calendar temp2 = Calendar.getInstance(_defaultLocale);
		temp2.setTime(_mainCalendar.getTime());
		temp2.set(Calendar.DAY_OF_WEEK, temp.getFirstDayOfWeek());
		final int days = (int) DateHelper.daysBetween(temp, temp2);
		current += days * _monthDayWidth;

		// move us to sunday, as the date shouldn't change when scrolling
		temp.set(Calendar.DAY_OF_WEEK, temp.getFirstDayOfWeek());

		while (true) {
			// draw month at beginning of month
			if (temp.get(Calendar.DAY_OF_MONTH) == 1) {
				gc.setForeground(_colorManager.getTickMarkColor());
				gc.drawLine(current, topY + _settings.getVerticalTickMarkOffset(), current, topY + _settings.getHeaderDayHeight());
				gc.setForeground(_textColor);
				gc.drawString(getDateString(temp, true), current + 4, topY + 3, true);
			}

			temp.add(Calendar.DATE, 1);
			_daysVisible++;

			current += _monthDayWidth;

			if (current > xMax) {
				break;
			}
		}
	}

	// days
	private void drawWeekTopBoxes(final GC gc, final Rectangle bounds) {
		final int xMax = bounds.width + bounds.x;
		int current = bounds.x;
		final int topY = bounds.y;
		final int bottomY = _settings.getHeaderMonthHeight();

		final Calendar temp = Calendar.getInstance(_defaultLocale);
		temp.setTime(_mainCalendar.getTime());

		// if we're not on a sunday, the weekbox will be shorter, so check how
		// much shorter
		// sun = 1, mon = 2, tue = 3, wed = 4, thu = 5, fri = 6, sat = 7
		final int day = temp.get(Calendar.DAY_OF_WEEK);
		final int firstDayOfWeek = temp.getFirstDayOfWeek();
		int dayOffset = firstDayOfWeek - day;

		// BUGFIX Sep 12/07:
		// On international dates that have getFirstDayOfWeek() on other days
		// than Sundays, we need to do some magic to these
		// or the dates printed will be wrong. As such, if the offset is
		// positive, we simply toss off 7 days to get the correct number.
		// we want a negative value in the end.
		if (dayOffset > 0) {
			dayOffset -= 7;
		}

		final int toTakeOff = dayOffset * _dayWidth;

		current += toTakeOff;

		// move us to the first day of the week, as the date shouldn't change
		// when scrolling
		temp.set(Calendar.DAY_OF_WEEK, temp.getFirstDayOfWeek());

		while (true) {
			gc.setForeground(_lineColor);
			gc.drawRectangle(current, topY, _weekWidth, bottomY);

			gc.setForeground(_txtHeaderBGColorTop);
			gc.setBackground(_txtHeaderBGColorBottom);
			gc.fillGradientRectangle(current < bounds.x ? bounds.x : current, topY + 1, _weekWidth, bottomY - 1, true);

			gc.setForeground(_colorManager.getTickMarkColor());
			gc.drawLine(current, topY + _settings.getVerticalTickMarkOffset(), current, topY + _settings.getHeaderDayHeight());

			gc.setForeground(_textColor);
			gc.drawString(getDateString(temp, true), current + 4, topY + 3, true);

			// only change dates on the first day of the week, as we don't
			// change date string for every different day
			if (temp.get(Calendar.DAY_OF_WEEK) == temp.getFirstDayOfWeek()) {
				temp.add(Calendar.DATE, 7);
			}

			current += _weekWidth;

			if (current > xMax) {
				break;
			}
		}
	}

	// days
	private void drawWeekBottomBoxes(final GC gc, final Rectangle bounds, boolean calculateOnly) {
		final int xMax = bounds.width + bounds.x;
		int current = bounds.x;
		final int topY = bounds.y + _settings.getHeaderMonthHeight();
		final int heightY = _settings.getHeaderDayHeight();

		int day = _mainCalendar.get(Calendar.DAY_OF_WEEK);
		_daysVisible = 0;

		final Calendar temp = Calendar.getInstance(_defaultLocale);
		temp.setTime(_mainCalendar.getTime());

		while (true) {
			if (!calculateOnly) {
				if (_selHeaderDates.contains(temp)) {
					gc.setForeground(_colorManager.getSelectedDayHeaderColorTop());
					gc.setBackground(_colorManager.getSelectedDayHeaderColorBottom());
				} else {
					gc.setForeground(_timeHeaderBGColorTop);
					gc.setBackground(_timeHeaderBGColorBottom);
				}
				gc.fillGradientRectangle(current + 1, topY + 1, _dayWidth - 1, heightY - 1, true);
			}

			_verticalLineLocations.add(new Integer(current));
			if (temp.get(Calendar.DAY_OF_WEEK) == _mainCalendar.getFirstDayOfWeek()) {
				_verticalWeekDividerLineLocations.add(new Integer(current));
			}

			if (!calculateOnly) {
				gc.setForeground(_colorManager.getWeekTimeDividerColor());
				gc.drawRectangle(current, topY, _dayWidth, heightY);

				int hSpacer = _settings.getDayHorizontalSpacing();
				final int vSpacer = _settings.getDayVerticalSpacing();

				final String dayLetter = getDateString(temp, false);

				if (_settings.adjustForLetters()) {
					Point extent = null;
					if (_dayLetterStringExtentMap.containsKey(dayLetter)) {
						extent = (Point) _dayLetterStringExtentMap.get(dayLetter);
					} else {
						extent = gc.stringExtent(dayLetter);
						_dayLetterStringExtentMap.put(dayLetter, extent);
					}

					switch (extent.x) {
						case 1:
							hSpacer += 5;
							break;
						case 2:
							hSpacer += 5;
							break;
						case 3:
							hSpacer += 4;
							break;
						case 4:
							hSpacer += 3;
							break;
						case 5:
							hSpacer += 3;
							break;
						case 6:
							hSpacer += 2;
							break;
						case 7:
							hSpacer += 1;
							break;
						case 8:
							hSpacer += 1;
							break;
						case 9:
							hSpacer -= 1;
							break;
						default:
							break;
					}
				}

				gc.setForeground(getDayTextColor(day));
				gc.drawString(dayLetter, current + hSpacer, topY + vSpacer, true);
				// fixes some odd red line bug, not sure why
				gc.setForeground(_lineColor);
			}

			temp.add(Calendar.DATE, 1);

			current += _dayWidth;

			day++;
			if (day > 7) {
				day = 1;
			}

			_daysVisible++;

			if (current > xMax) {
				_endCalendar = temp;
				break;
			}
		}
	}

	// d-days
	private void drawDDayTopBoxes(final GC gc, final Rectangle bounds) {
		final int xMax = bounds.width + bounds.x;
		int current = bounds.x;
		final int topY = bounds.y;
		final int bottomY = _settings.getHeaderMonthHeight();
		final int splitEvery = _settings.getDDaySplitCount();

		final Calendar temp = Calendar.getInstance(_defaultLocale);
		temp.setTime(_mainCalendar.getTime());
		final int startX = getXForDate(_dDayCalendar);
		final int daysFromStartOffset = startX / _dayWidth;
		int dayOffset = 0;
		int letter = 0;

		// find the start date by splitting it down and checking offsets of where we are
		float count = (float) daysFromStartOffset / (float) splitEvery;
		count = (float) Math.floor(count);
		final int takeOff = (int) Math.ceil(count * splitEvery);
		dayOffset = daysFromStartOffset - takeOff;
		letter = -takeOff;

		// set current to the location of where "0" starts for each set
		current += dayOffset * _dayWidth;
		final int dDayWeekWidth = _dayWidth * splitEvery;

		// if we're to the right of 0 we need to start drawing one "set" of days prior to the left or we'll have a blak space
		if (current > 0) {
			current -= dDayWeekWidth;
			letter -= splitEvery;
		}

		while (true) {
			gc.setForeground(_lineColor);
			gc.drawRectangle(current, topY, dDayWeekWidth, bottomY);

			gc.setForeground(_txtHeaderBGColorTop);
			gc.setBackground(_txtHeaderBGColorBottom);
			gc.fillGradientRectangle(current < bounds.x ? bounds.x : current, topY + 1, dDayWeekWidth, bottomY - 1, true);

			gc.setForeground(_colorManager.getTickMarkColor());
			// draws the little line that starts a group i.e a week or in this case every ten days
			gc.drawLine(current, topY + _settings.getVerticalTickMarkOffset(), current, topY + _settings.getHeaderDayHeight());

			gc.setForeground(_textColor);
			final StringBuffer buf = new StringBuffer();
			buf.append(letter);
			gc.drawString(buf.toString(), current + 4, topY + 3, true);
			letter += splitEvery;

			// move one split-width forward
			temp.add(Calendar.DATE, splitEvery);
			current += dDayWeekWidth;

			if (current > xMax) {
				break;
			}
		}
	}

	// days
	private void drawDDayBottomBoxes(final GC gc, final Rectangle bounds, boolean calculateOnly) {
		final int xMax = bounds.width + bounds.x;
		int current = bounds.x;

		final int topY = bounds.y + _settings.getHeaderMonthHeight();
		final int heightY = _settings.getHeaderDayHeight();

		final int startX = getXForDate(_dDayCalendar);
		final int daysFromStartOffset = startX / _dayWidth;
		int dayOffset = 0;
		final int splitEvery = _settings.getDDaySplitCount();
		int letter = 0;

		// find the start date by splitting it down and checking offsets of where we are
		float count = (float) daysFromStartOffset / (float) splitEvery;
		count = (float) Math.floor(count);
		final int takeOff = (int) Math.ceil(count * splitEvery);
		dayOffset = daysFromStartOffset - takeOff;
		letter = splitEvery - Math.abs(dayOffset);
		if (letter >= splitEvery) {
			letter -= splitEvery;
		}

		_daysVisible = 0;

		final Calendar temp = Calendar.getInstance(_defaultLocale);
		temp.setTime(_mainCalendar.getTime());

		while (true) {
			_verticalLineLocations.add(new Integer(current));
			if (!calculateOnly) {
				if (_selHeaderDates.contains(temp)) {
					gc.setForeground(_colorManager.getSelectedDayHeaderColorTop());
					gc.setBackground(_colorManager.getSelectedDayHeaderColorBottom());
				} else {
					gc.setForeground(_timeHeaderBGColorTop);
					gc.setBackground(_timeHeaderBGColorBottom);
				}
				gc.fillGradientRectangle(current + 1, topY + 1, _dayWidth - 1, heightY - 1, true);

				gc.setForeground(_colorManager.getWeekTimeDividerColor());
				gc.drawRectangle(current, topY, _dayWidth, heightY);

				int hSpacer = _settings.getDayHorizontalSpacing();
				final int vSpacer = _settings.getDayVerticalSpacing();

				final StringBuffer buf = new StringBuffer();
				buf.append(letter);
				final String dayLetter = buf.toString();

				if (_settings.adjustForLetters()) {
					Point extent = null;
					if (_dayLetterStringExtentMap.containsKey(dayLetter)) {
						extent = (Point) _dayLetterStringExtentMap.get(dayLetter);
					} else {
						extent = gc.stringExtent(dayLetter);
						_dayLetterStringExtentMap.put(dayLetter, extent);
					}

					switch (extent.x) {
						case 1:
						case 2:
							hSpacer += 5;
							break;
						case 3:
							hSpacer += 4;
							break;
						case 4:
						case 5:
							hSpacer += 3;
							break;
						case 6:
							hSpacer += 2;
							break;
						case 7:
						case 8:
							hSpacer += 1;
							break;
						case 9:
							hSpacer -= 1;
							break;
						default:
							break;
					}
				}

				// d-days don't have weekends, so we stick to a non-weekend date
				gc.setForeground(getDayTextColor(Calendar.TUESDAY));

				gc.drawString(dayLetter, current + hSpacer, topY + vSpacer, true);
			}

			temp.add(Calendar.DATE, 1);
			// fixes some odd red line bug, not sure why
			gc.setForeground(_lineColor);

			current += _dayWidth;

			_daysVisible++;

			letter++;
			if (letter % splitEvery == 0) {
				_verticalWeekDividerLineLocations.add(new Integer(current));
				letter = 0;
			}

			if (current > xMax) {
				_endCalendar = temp;
				break;
			}
		}
	}

	// days (hours)
	private void drawHourTopBoxes(final GC gc, final Rectangle bounds) {
		final int xMax = bounds.width + bounds.x;
		int current = bounds.x;
		final int topY = bounds.y;
		final int bottomY = _settings.getHeaderMonthHeight();

		final Calendar temp = Calendar.getInstance(_defaultLocale);
		temp.setTime(_mainCalendar.getTime());

		// if we're not on hour zero, the box will be shorter, so check how much
		final int hour = temp.get(Calendar.HOUR_OF_DAY);

		final int toTakeOff = hour * _dayWidth;

		final int wWidth = _weekWidth;
		current -= toTakeOff;

		boolean once = false;

		while (true) {
			gc.setForeground(_txtHeaderBGColorTop);
			gc.setBackground(_txtHeaderBGColorBottom);
			gc.fillGradientRectangle(current, topY + 1, wWidth, bottomY - 1, true);

			gc.setForeground(_colorManager.getTickMarkColor());
			gc.drawLine(current, topY + _settings.getVerticalTickMarkOffset(), current, topY + _settings.getHeaderDayHeight());

			gc.setForeground(_textColor);

			final String dString = getDateString(temp, true);

			// TODO: This needs to be a different fetch for dates that are not
			// right now, so to speak.
			// so if the leftmost date is a full date/time, the "next" date
			// needs to be without the time
			// should make it a method call
			if (current >= bounds.x) {
				gc.drawString(dString, current + 4, topY + 3, true);
			}

			// as ranges here can be massive width-wise, always ensure there's a
			// date visible at the top
			if (!once && _mainCalendar.get(Calendar.HOUR_OF_DAY) != 0) {
				gc.drawString(dString, bounds.x + 4, topY + 3, true);
				once = true;
			}

			temp.add(Calendar.DATE, 1);
			current += _weekWidth;

			if (current > xMax) {
				break;
			}
		}
	}

	// days
	private void drawHourBottomBoxes(final GC gc, final Rectangle bounds, boolean calculateOnly) {
		final int xMax = bounds.width + bounds.x;
		int current = bounds.x;
		final int topY = bounds.y + _settings.getHeaderMonthHeight();
		final int heightY = _settings.getHeaderDayHeight();

		final Calendar temp = Calendar.getInstance(_defaultLocale);
		temp.setTime(_mainCalendar.getTime());

		boolean first = true;

		_hoursVisible = 0;

		while (true) {
			gc.setForeground(_colorManager.getHourTimeDividerColor());
			int spacer = 1;

			_verticalLineLocations.add(new Integer(current));

			if (!calculateOnly) {
				// this weird code here checks if it's a gantt section on the left and we're drawing the first iteration.
				// as sections draw their own line on the inner-facing border, and so does this, that'd be two lines in a row
				// which makes the section at the top look rather "thick". As we don't want that, we do a quick check and skip the drawing.
				// the "spacer" does the same thing for the gradient below
				if (!_ganttSections.isEmpty() && first) {
					gc.drawRectangle(current - 1, topY, _dayWidth + 1, heightY);
					spacer = 0;
				} else {
					gc.drawRectangle(current, topY, _dayWidth, heightY);
				}

				gc.setForeground(_timeHeaderBGColorTop);
				gc.setBackground(_timeHeaderBGColorBottom);
				gc.fillGradientRectangle(current + spacer, topY + 1, _dayWidth - spacer, heightY - 1, true);

				final int hSpacer = _settings.getDayHorizontalSpacing();
				final int vSpacer = _settings.getDayVerticalSpacing();

				gc.setForeground(_textColor);
				gc.drawString(getDateString(temp, false), current + hSpacer, topY + vSpacer, true);

				// fixes some odd red line bug, not sure why
				// gc.setForeground(mLineColor);
			}

			current += _dayWidth;

			if (current > xMax) {
				current -= _dayWidth;
				_endCalendar = temp;
				break;
			}

			_hoursVisible++;
			temp.add(Calendar.HOUR_OF_DAY, 1);
			if (temp.get(Calendar.HOUR_OF_DAY) == 0) {
				_verticalWeekDividerLineLocations.add(new Integer(current));
				// temp.add(Calendar.DATE, 1);
				// temp.set(Calendar.HOUR_OF_DAY, workDayStartHour);
			}

			first = false;
		}

		// calculate the exact end time down to the minute

		// hours in the day
		final int hr = (_mainBounds.width - _mainBounds.x) / _dayWidth;

		// hour pixels visible
		final int total = hr * _dayWidth;
		final float ppm = 60f / _dayWidth;

		// whatever pixels don't account for a full hour that are left over
		final int extra = _mainBounds.width - _mainBounds.x - total;

		// total visible minutes
		final int totalMinutes = (int) (((float) total + (float) extra) * ppm);

		// set our temporary end calendar to the start date of the calendar
		final Calendar fakeEnd = Calendar.getInstance(_defaultLocale);
		fakeEnd.setTime(_mainCalendar.getTime());

		// loop it, but take work days/hours into account as usual
		for (int i = 0; i < totalMinutes; i++) {
			fakeEnd.add(Calendar.MINUTE, 1);
			if (temp.get(Calendar.HOUR_OF_DAY) >= 24) {
				temp.add(Calendar.DATE, 1);
				temp.set(Calendar.HOUR_OF_DAY, 0);
			}
		}

		// what we're left with is the exact end date down to the minute
		_endCalendar = fakeEnd;
	}

	// minutes
	private void drawMinuteTopBoxes(final GC gc, final Rectangle bounds) {
		final int xMax = bounds.width + bounds.x;
		int current = bounds.x;
		final int topY = bounds.y;
		final int bottomY = _settings.getHeaderMonthHeight();

		final Calendar temp = Calendar.getInstance(_defaultLocale);
		temp.setTime(_mainCalendar.getTime());

		// if we're not on hour zero, the box will be shorter, so check how much
		final int hour = temp.get(Calendar.HOUR_OF_DAY);

		final int toTakeOff = hour * _dayWidth;

		final int wWidth = _weekWidth;
		current -= toTakeOff;

		boolean once = false;

		while (true) {
			gc.setForeground(_txtHeaderBGColorTop);
			gc.setBackground(_txtHeaderBGColorBottom);
			gc.fillGradientRectangle(current, topY + 1, wWidth, bottomY - 1, true);

			gc.setForeground(_colorManager.getTickMarkColor());
			gc.drawLine(current, topY + _settings.getVerticalTickMarkOffset(), current, topY + _settings.getHeaderDayHeight());

			gc.setForeground(_textColor);

			final String dString = getDateString(temp, true);

			// TODO: This needs to be a different fetch for dates that are not
			// right now, so to speak.
			// so if the leftmost date is a full date/time, the "next" date
			// needs to be without the time
			// should make it a method call
			if (current >= bounds.x) {
				gc.drawString(dString, current + 4, topY + 3, true);
			}
			// as ranges here can be massive width-wise, always ensure there's a
			// date visible at the top
			if (!once) {
				gc.drawString(dString, bounds.x + 4, topY + 3, true);
				once = true;
			}

			temp.add(Calendar.MINUTE, 1);
			current += _weekWidth;

			if (current > xMax) {
				break;
			}
		}
	}

	// minutes
	private void drawMinuteBottomBoxes(final GC gc, final Rectangle bounds, boolean calculateOnly) {
		final int xMax = bounds.width + bounds.x;
		int current = bounds.x;
		final int topY = bounds.y + _settings.getHeaderMonthHeight();
		final int heightY = _settings.getHeaderDayHeight();

		final Calendar temp = Calendar.getInstance(_defaultLocale);
		temp.setTime(_mainCalendar.getTime());

		boolean first = true;

		_hoursVisible = 0;

		while (true) {
			gc.setForeground(_colorManager.getHourTimeDividerColor());
			int spacer = 1;

			_verticalLineLocations.add(new Integer(current));

			if (!calculateOnly) {
				// this weird code here checks if it's a gantt section on the left and we're drawing the first iteration.
				// as sections draw their own line on the inner-facing border, and so does this, that'd be two lines in a row
				// which makes the section at the top look rather "thick". As we don't want that, we do a quick check and skip the drawing.
				// the "spacer" does the same thing for the gradient below
				if (!_ganttSections.isEmpty() && first) {
					gc.drawRectangle(current - 1, topY, _dayWidth + 1, heightY);
					spacer = 0;
				} else {
					gc.drawRectangle(current, topY, _dayWidth, heightY);
				}

				gc.setForeground(_timeHeaderBGColorTop);
				gc.setBackground(_timeHeaderBGColorBottom);
				gc.fillGradientRectangle(current + spacer, topY + 1, _dayWidth - spacer, heightY - 1, true);

				final int hSpacer = _settings.getDayHorizontalSpacing();
				final int vSpacer = _settings.getDayVerticalSpacing();

				gc.setForeground(_textColor);
				gc.drawString(getDateString(temp, false), current + hSpacer, topY + vSpacer, true);

				// fixes some odd red line bug, not sure why
				// gc.setForeground(mLineColor);
			}

			current += _dayWidth;

			if (current > xMax) {
				current -= _dayWidth;
				_endCalendar = temp;
				break;
			}

			_hoursVisible++;
			temp.add(Calendar.MINUTE, 1);
			if (temp.get(Calendar.MINUTE) == 0) {
				_verticalWeekDividerLineLocations.add(new Integer(current));
				// temp.add(Calendar.DATE, 1);
				// temp.set(Calendar.HOUR_OF_DAY, workDayStartHour);
			}

			first = false;
		}

		// calculate the exact end time down to the minute

		// hours in the day
		final int hr = (_mainBounds.width - _mainBounds.x) / _dayWidth;

		// hour pixels visible
		final int total = hr * _dayWidth;
		final float ppm = 60f / _dayWidth;

		// whatever pixels don't account for a full hour that are left over
		final int extra = _mainBounds.width - _mainBounds.x - total;

		// total visible minutes
		final int totalMinutes = (int) (((float) total + (float) extra) * ppm);

		// set our temporary end calendar to the start date of the calendar
		final Calendar fakeEnd = Calendar.getInstance(_defaultLocale);
		fakeEnd.setTime(_mainCalendar.getTime());

		// loop it, but take work days/hours into account as usual
		for (int i = 0; i < totalMinutes; i++) {
			fakeEnd.add(Calendar.MINUTE, 1);
			if (temp.get(Calendar.MINUTE) >= 60) {
				temp.add(Calendar.HOUR_OF_DAY, 1);
				temp.set(Calendar.MINUTE, 0);
			}
		}

		// what we're left with is the exact end date down to the minute
		_endCalendar = fakeEnd;
	}

	@SuppressWarnings("unchecked")
	private void drawGanttSpecialDateRanges(final GC gc, final Rectangle bounds, final GanttSection gs) {
		for (int i = 0; i < _specDateRanges.size(); i++) {
			final GanttSpecialDateRange range = _specDateRanges.get(i);

			/*
			 * // fastest check
			 * if (!range.isUseable()) {
			 * continue;
			 * }
			 */

			// slower check (note to self: this only checks if any date is in range, doesn't mean we fill the entire screen with data)
			if (!range.isVisible(_mainCalendar, _endCalendar)) {
				continue;
			}

			if (range.getBackgroundColorTop() != null) {
				gc.setForeground(range.getBackgroundColorTop());
			}
			if (range.getBackgroundColorBottom() != null) {
				gc.setBackground(range.getBackgroundColorBottom());
			}

			int offset = 0;
			if (gs == null) {
				offset = _vScrollPos;
				if (offset > getHeaderHeight()) {
					offset = getHeaderHeight();
				}
			} else {
				offset = _vScrollPos;
			}

			final int yStart = bounds.y - offset;
			int yHeight = bounds.height + offset;

			int extra = 0;
			if (!_ganttSections.isEmpty() && _settings.getSectionSide() == SWT.LEFT) {
				int sectionWidth = 0;
				if (_settings.drawSectionBar()) {
					sectionWidth += _settings.getSectionBarWidth();
				}
				if (_settings.drawSectionDetails()) {
					sectionWidth += _settings.getSectionDetailWidth();
				}
				extra += sectionWidth;
			}

			yHeight -= offset;

			final List<?> toDraw = range.getBlocks(_mainCalendar, _endCalendar);
			for (int x = 0; x < toDraw.size(); x++) {
				final List<Calendar> cals = (List<Calendar>) toDraw.get(x);
				// start and end are in pos 0 and 1
				final Calendar eStart =  cals.get(0);
				final Calendar eEnd = cals.get(1);

				// push it over the edge to the next day or we'll have a gap of ~1px as we deal with nearly-next-day timestamps
				if (_currentView == ISettings.VIEW_MINUTE || _currentView == ISettings.VIEW_DAY) {
					eEnd.add(Calendar.MILLISECOND, 1);
				}

				int xStart = getXForDate(eStart);
				int xEnd = getXForDate(eEnd);

				xStart += extra;
				xEnd += extra;

				// 23:59:59 1 day events needs a push to the right or they won't draw in zoomed out views
				if (xStart == xEnd && _currentView >= ISettings.VIEW_WEEK) {
					xEnd += getDayWidth();
				}

				gc.fillGradientRectangle(xStart, yStart, xEnd - xStart, yHeight, true);
			}
		}
	}

	private void drawGanttPhases(final GC gc, final Rectangle bounds, final boolean header, final GanttSection gs) {
		final int pHeight = _settings.getPhasesHeaderHeight();

		int extra = 0;
		if (!_ganttSections.isEmpty() && _settings.getSectionSide() == SWT.LEFT) {
			int sectionWidth = 0;
			if (_settings.drawSectionBar()) {
				sectionWidth += _settings.getSectionBarWidth();
			}
			if (_settings.drawSectionDetails()) {
				sectionWidth += _settings.getSectionDetailWidth();
			}
			extra += sectionWidth;
		}

		int offset = 0;
		if (gs == null) {
			offset = _vScrollPos;
			if (offset > getHeaderHeight()) {
				offset = getHeaderHeight();
			}
		} else {
			offset = _vScrollPos;
		}

		// first of all, fill a full background of the header
		if (header) {
			int yLoc = pHeight;
			if (!_settings.lockHeaderOnVerticalScroll()) {
				yLoc += offset;
			}

			gc.setBackground(_phaseHeaderBGColorBottom);
			gc.setForeground(_phaseHeaderBGColorTop);

			gc.fillGradientRectangle(bounds.x, getHeaderHeight() - yLoc, bounds.width, yLoc, true);

			gc.setForeground(_colorManager.getTopHorizontalLinesColor());
			gc.drawLine(bounds.x, getHeaderHeight() - 1, bounds.width + extra, getHeaderHeight() - 1);
		}

		for (int i = 0; i < _ganttPhases.size(); i++) {
			final GanttPhase phase = _ganttPhases.get(i);

			// don't draw hidden phases
			if (phase.isHidden()) {
				continue;
			}

			// phase is missing data, can't draw it
			if (!phase.isDisplayable()) {
				continue;
			}

			// get values we'll need
			final int xStart = getStartingXFor(phase.getStartDate());
			int xEnd = getXForDate(phase.getEndDate());

			xEnd += extra;

			int yStart = bounds.y - offset;
			int yHeight = bounds.height + offset;

			yHeight -= offset;

			// alpha
			if (phase.getAlpha() == 255) {
				gc.setAlpha(255);
				gc.setAdvanced(false);
			} else {
				gc.setAlpha(phase.getAlpha());
			}

			if (header) {
				yStart = getHeaderHeight() - pHeight;
				if (!_settings.lockHeaderOnVerticalScroll()) {
					yStart -= offset;
				}
				// do fills first
				gc.setBackground(phase.getHeaderBackgroundColor());
				gc.setForeground(phase.getHeaderForegroundColor());
				gc.fillGradientRectangle(xStart, yStart, xEnd - xStart, pHeight, true);

				final Rectangle hBounds = new Rectangle(xStart, yStart, xEnd - xStart, pHeight - 1);

				if (phase.getTitle() != null) {
					// font first or string extent will be off
					if (phase.getHeaderFont() != null) {
						gc.setFont(phase.getHeaderFont());
					}

					final String str = getStringToDisplay(gc, hBounds, phase.getTitle());

					final Point p = gc.stringExtent(str);
					final int textX = (xEnd - xStart) / 2 - p.x / 2 + 1;
					final int textY = yStart + pHeight / 2 - p.y / 2 - 2;

					gc.setForeground(phase.getHeaderTextColor());

					// draw text
					gc.drawText(str, xStart + textX, textY, true);
				}

				// set header bounds
				phase.setHeaderBounds(hBounds);
			} else {
				// just the fills needed
				gc.setBackground(phase.getBodyTopColor());
				gc.setForeground(phase.getBodyBottomColor());
				gc.fillGradientRectangle(xStart, yStart, xEnd - xStart, yHeight, true);
				phase.setBounds(new Rectangle(xStart, yStart, xEnd - xStart, yHeight));
			}

			if (header && phase.isDrawBorders()) {
				yStart = getHeaderHeight() - pHeight;
				gc.setLineWidth(phase.getBorderWidth());
				gc.setForeground(phase.getBorderColor());

				gc.drawLine(xStart, yStart, xStart, yStart + pHeight);
				gc.drawLine(xEnd - 1, yStart, xEnd - 1, yStart + pHeight);
			}
		}

		gc.setAlpha(255);
		gc.setAdvanced(false);
	}

	/**
	 * Fetches a dot-concatenated string that will fit the given space. Newlines are not taken into account.
	 *
	 * @param gc GC
	 * @param area Area to fit text in
	 * @param text Text to fit in area
	 * @return String concatenated string with ellipsis at end
	 */
	private String getStringToDisplay(final GC gc, final Rectangle area, final String text) {
		Point p = null;
		if (_stringWidthCache.containsKey(text)) {
			p = _stringWidthCache.get(text);
		} else {
			p = gc.stringExtent(text);
			_stringWidthCache.put(text, p);
		}

		final StringBuffer buf = new StringBuffer();
		String line = text;
		if (p.x > area.width) {
			final int textLen = line.length();
			for (int i = textLen - 1; i >= 0; i--) {
				final String temp = line.substring(0, i) + "...";
				int width = 0;
				if (_stringWidthCache.containsKey(temp)) {
					width = _stringWidthCache.get(temp).x;
				} else {
					final Point p2 = gc.stringExtent(temp);
					width = p2.x;
					_stringWidthCache.put(temp, p2);
				}

				if (width < area.width) {
					line = temp;
					break;
				} else if (i == 0) {
					line = "";
				}
			}
			buf.append(line);
		} else {
			return text;
		}

		return buf.toString();
	}

	private Color getDayBackgroundGradient(final int day, final boolean top, final GanttSection gs) {
		return internalGetDayBackgroundGradient(day, top, gs);
	}

	private Color getHolidayBackgroundGradient(final boolean top, final GanttSection gs) {
		Color ret = null;
		if (top) {
			if (gs == null) {
				ret = _holidayBGColorTop;
			} else {
				ret = gs.getHolidayBackgroundColorTop();
			}
		} else {
			if (gs == null) {
				ret = _holidayBGColorBottom;
			} else {
				ret = gs.getHolidayBackgroundColorBottom();
			}
		}
		return ret;
	}

	private Color internalGetDayBackgroundGradient(final int day, final boolean top, final GanttSection gs) {
		Color ret = null;
		switch (day) {
			case Calendar.SATURDAY:
				if (top) {
					if (gs == null) {
						ret = _satBGColorTop;
						break;
					} else {
						ret = gs.getSaturdayBackgroundColorTop();
						break;
					}
				} else {
					if (gs == null) {
						ret = _satBGColorBottom;
						break;
					} else {
						ret = gs.getSaturdayBackgroundColorBottom();
						break;
					}
				}
			case Calendar.SUNDAY:
				if (top) {
					if (gs == null) {
						ret = _sunBGColorTop;
						break;
					} else {
						ret = gs.getSundayBackgroundColorTop();
						break;
					}
				} else {
					if (gs == null) {
						ret = _sunBGColorBottom;
						break;
					} else {
						ret = gs.getSundayBackgroundColorBottom();
						break;
					}
				}
			default:
				if (top) {
					if (gs == null) {
						ret = _wkBGColorTop;
						break;
					} else {
						ret = gs.getWeekdayBackgroundColorTop();
						break;
					}
				} else {
					if (gs == null) {
						ret = _wkBGColorBottom;
						break;
					} else {
						ret = gs.getWeekdayBackgroundColorBottom();
						break;
					}
				}
		}

		return ret;
	}

	private Color getDayTextColor(final int day) {
		switch (day) {
			case Calendar.SATURDAY:
				return _satTextColor;
			case Calendar.SUNDAY:
				return _sunTextColor;
			default:
				return _weekdayTextColor;
		}
	}

	private void drawEvents(final GC gc, final Rectangle bounds, final GanttSection gs) {
		internalDrawEvents(gc, bounds, gs);
	}

	private void drawEvents(final GC gc, final Rectangle bounds) {
		internalDrawEvents(gc, bounds, null);
	}

	private void internalDrawEvents(final GC gc, final Rectangle bounds, final GanttSection gs) {
		if (_ganttEvents.isEmpty()) {
			return;
		}

		final Set alreadyDrawn = new HashSet();

		List events = _ganttEvents;
		if (gs != null) {
			events = gs.getEvents();
		}

		final List correctOrder = new ArrayList();
		for (int i = 0; i < events.size(); i++) {
			final IGanttChartItem event = (IGanttChartItem) events.get(i);
			if (event instanceof GanttGroup) {
				final GanttGroup gg = (GanttGroup) event;
				final List children = gg.getEventMembers();
				correctOrder.addAll(children);
				continue;
			} else {
				correctOrder.add(event);
			}
		}

		_totVisEventCnt = 0;

		// add events that need to be drawn but aren't actually part of the section (vertical DND)
		if (gs != null) {
			correctOrder.addAll(gs.getDNDGanttEvents());
		}

		for (int i = 0; i < correctOrder.size(); i++) {
			final GanttEvent ge = (GanttEvent) correctOrder.get(i);

			if (ge.isHidden()) {
				continue;
			}

			if (alreadyDrawn.contains(ge)) {
				continue;
			}

			// all events that are non-hidden are counted, in visible bounds or not
			_totVisEventCnt++;

			// don't draw out of bounds events
			if (ge.getVisibility() != Constants.EVENT_VISIBLE) {
				// still calculate name extent, we need it to determine correct scrollbars for fixed scrollbars among other things
				if (ge.getNameExtent() == null || ge.isNameChanged()) {
					final String toDraw = getStringForEvent(ge);
					// ge.setNameExtent(gc.stringExtent(toDraw));
					ge.setNameExtent(gc.textExtent(toDraw));
					ge.setParsedString(toDraw);
					ge.setNameChanged(false);
				}
				continue;
			}

			// at this point it will be drawn
			alreadyDrawn.add(ge);

			// draw it
			drawOneEvent(gc, ge, bounds);
		}
	}

	// draws one event onto the chart (or rather, delegates to the correct drawing method)
	private void drawOneEvent(final GC gc, final GanttEvent ge, final Rectangle boundsToUse) {
		final int xStart = ge.getX();
		final int xEventWidth = ge.getWidth();

		// clone the bounds, we don't want any sub-method messing with them
		final Rectangle bounds = new Rectangle(boundsToUse.x, boundsToUse.y, boundsToUse.width, boundsToUse.height);

		Color cEvent = ge.getStatusColor();
		Color gradient = ge.getGradientStatusColor();

		if (cEvent == null) {
			cEvent = _settings.getDefaultEventColor();
		}
		if (gradient == null) {
			gradient = _settings.getDefaultGradientEventColor();
		}

		final int yDrawPos = ge.getY();

		final int dw = getDayWidth();

		boolean advanced = false;
		if (getLayerOpacity(ge.getLayer()) != -1) {
			advanced = true;
			gc.setAlpha(getLayerOpacity(ge.getLayer()));
		}

		if (ge.isCheckpoint()) {
			_paintManager.drawCheckpoint(this, _settings, _colorManager, ge, gc, _threeDee, dw, xStart, yDrawPos, bounds);
		} else if (ge.isImage()) {
			_paintManager.drawImage(this, _settings, _colorManager, ge, gc, ge.getPicture(), _threeDee, dw, xStart, yDrawPos, bounds);
		} else if (ge.isScope()) {
			_paintManager.drawScope(this, _settings, _colorManager, ge, gc, _threeDee, dw, xStart, yDrawPos, xEventWidth, bounds);
		} else {
			_paintManager.drawEvent(this, _settings, _colorManager, ge, gc, !_selectedEvents.isEmpty() && _selectedEvents.contains(ge), _threeDee, dw, xStart, yDrawPos, xEventWidth, bounds);
		}
		if (ge.hasMovementConstraints()) {
			int startStart = -1, endStart = -1;
			if (ge.getNoMoveBeforeDate() != null) {
				startStart = getStartingXFor(ge.getNoMoveBeforeDate());
			}
			if (ge.getNoMoveAfterDate() != null) {
				endStart = getStartingXFor(ge.getNoMoveAfterDate());
			}

			if (_settings.drawLockedDateMarks()) {
				_paintManager.drawLockedDateRangeMarker(this, _settings, _colorManager, ge, gc, _threeDee, dw, yDrawPos, startStart, endStart, bounds);
			}
		}

		// draws |---------| lines to show revised dates, if any
		if (_showPlannedDates) {
			_paintManager.drawPlannedDates(this, _settings, _colorManager, ge, gc, _threeDee, xStart, yDrawPos, xEventWidth, bounds);
		}

		// draw a little plaque saying how many days that this event is long
		if (_showNumDays) {
			final long days = DateHelper.daysBetween(ge.getActualStartDate(), ge.getActualEndDate()) + 1;
			_paintManager.drawDaysOnChart(this, _settings, _colorManager, ge, gc, _threeDee, xStart, yDrawPos, xEventWidth, (int) days, bounds);
		}

		// fetch current font
		final Font oldFont = gc.getFont();

		// if the name has changed, update various cached variables
		if (ge.isNameChanged()) {
			final String toDraw = getStringForEvent(ge);
			// ge.setNameExtent(gc.stringExtent(toDraw));
			ge.setNameExtent(gc.textExtent(toDraw));
			ge.setParsedString(toDraw);
			ge.setNameChanged(false);
		}

		// draw the text if any, o
		if (_settings.drawEventString() && ge.getParsedString() != null && ge.isShowText()) {
			_paintManager.drawEventString(this, _settings, _colorManager, ge, gc, ge.getParsedString(), _threeDee, xStart, yDrawPos, xEventWidth, bounds);
		}

		// reset font
		gc.setFont(oldFont);

		if (advanced) {
			gc.setAdvanced(false);
		}
	}

	// updates all event visibilities, the bounds is the currently visible bounds, not the bounds that should be calculated
	private void updateEventVisibilities(final Rectangle bounds) {
		final Object[] all = _allEventsCombined.toArray();
		for (int i = 0; i < all.length; i++) {
			final GanttEvent ge = (GanttEvent) all[i];
			ge.setVisibility(getEventVisibility(ge, bounds));
		}
	}

	private void calculateAllScopes(final Rectangle bounds, final GanttSection gs) {
		if (_ganttEvents.isEmpty()) {
			return;
		}

		int yStart = bounds.y + _settings.getEventsTopSpacer();// - mVerticalScrollPosition;

		final Set allEventsInGroups = new HashSet();
		for (int i = 0; i < _ganttGroups.size(); i++) {
			allEventsInGroups.addAll(_ganttGroups.get(i).getEventMembers());
		}

		boolean lastLoopWasGroup = false;
		// GanttGroup lastGroup = null;
		final Map groupLocations = new HashMap();

		List events = _ganttEvents;
		if (gs != null) {
			events = gs.getEvents();
		}

		final List correctOrder = new ArrayList();
		for (int i = 0; i < events.size(); i++) {
			final IGanttChartItem event = (IGanttChartItem) events.get(i);
			if (event instanceof GanttGroup) {
				final GanttGroup gg = (GanttGroup) event;
				final List children = gg.getEventMembers();
				correctOrder.addAll(children);
				continue;
			} else {
				correctOrder.add(event);
			}
		}

		for (int i = 0; i < correctOrder.size(); i++) {
			final IGanttChartItem event = (IGanttChartItem) correctOrder.get(i);

			final GanttEvent ge = (GanttEvent) event;

			// if the override is set, set it on events etc so it's used
			if (_fixedRowHeight != 0) {
				ge.setFixedRowHeight(_fixedRowHeight);
				if (ge.getGanttGroup() != null) {
					ge.getGanttGroup().setFixedRowHeight(_fixedRowHeight);
				}
			}

			boolean groupedEvent = false;
			boolean newGroup = false;

			// if events are not visible, we can save a lot of time by not drawing them
			ge.setVisibility(getEventVisibility(ge, bounds));

			if (ge.isHidden()) {
				continue;
			}

			if (ge.isScope()) {
				ge.calculateScope();
			}

			final int xStart = getStartingXFor(ge);
			final int xEventWidth = getXLengthForEvent(ge);

			// entire group if this element is part of a group
			if (allEventsInGroups.contains(ge)) {
				groupedEvent = true;

				// remember the location we draw this group at
				if (!groupLocations.containsKey(ge.getGanttGroup())) {
					newGroup = true;
					if (i != 0 && lastLoopWasGroup) {
						yStart += _eventHeight + _eventSpacer;
					}
					groupLocations.put(ge.getGanttGroup(), new Integer(yStart));
				}
			}

			// event just after a group
			if (lastLoopWasGroup && !groupedEvent) {
				yStart += _eventHeight + _eventSpacer;
			}

			// position event will be drawn at vertically
			int yDrawPos = yStart - _vScrollPos;

			// if it's a grouped event, get the location from our map to where it's drawn
			if (groupedEvent && groupLocations.containsKey(ge.getGanttGroup())) {
				yDrawPos = ((Integer) groupLocations.get(ge.getGanttGroup())).intValue() - _vScrollPos;
			}

			int fixedRowHeight = _fixedRowHeight;
			int verticalAlignment = ge.getVerticalEventAlignment();

			if (ge.getGanttGroup() == null) {
				if (!ge.isAutomaticRowHeight()) {
					fixedRowHeight = ge.getFixedRowHeight();
				}
			} else {
				verticalAlignment = ge.getGanttGroup().getVerticalEventAlignment();
				if (!ge.getGanttGroup().isAutomaticRowHeight()) {
					fixedRowHeight = ge.getGanttGroup().getFixedRowHeight();
				}
			}

			final boolean fixedHeight = fixedRowHeight > 0;

			ge.setHorizontalLineTopY(yStart + _vScrollPos);

			if (fixedHeight) {
				yStart += fixedRowHeight;

				int extra = 0;

				switch (verticalAlignment) {
					case SWT.BOTTOM:
						extra = fixedRowHeight - _eventHeight;
						break;
					case SWT.CENTER:
						extra = fixedRowHeight / 2 - _eventHeight / 2;
						break;
					case SWT.NONE:
					case SWT.TOP:
						extra = _eventSpacer - _eventHeight;
						break;
					default:
						break;
				}

				if (extra < 0) {
					extra = 0;
				}

				yDrawPos += extra;

			}

			// sub-events in a grouped event type where the group has a fixed row height, we just set the yStart to the last yStart, which actually
			// got through the above switch statement and had its start position calculated
			if (!newGroup && groupedEvent) {
				yDrawPos = ((Integer) groupLocations.get(ge.getGanttGroup())).intValue() - _vScrollPos;
			}

			if (fixedHeight) {
				ge.setHorizontalLineBottomY(yDrawPos - _eventHeight + _vScrollPos);
			} else {
				ge.setHorizontalLineBottomY(yDrawPos + _eventHeight + _vScrollPos);
			}

			// set event bounds
			ge.setBounds(new Rectangle(xStart, yDrawPos, xEventWidth, _eventHeight));

			// update the actual width of the event
			ge.updateActualWidth();

			if (groupedEvent) {
				lastLoopWasGroup = true;
			} else {
				// space them out
				if (!fixedHeight) {
					yStart += _eventHeight + _eventSpacer;
					_bottomMostY = yStart + _eventHeight;
				}
				lastLoopWasGroup = false;

			}

			_bottomMostY = Math.max(_bottomMostY, yStart + _eventHeight);

		}

		// take off the last iteration, easier here than an if check for each iteration
		_bottomMostY -= _eventSpacer;
	}

	// string processing for display text beyond event
	private String getStringForEvent(final GanttEvent ge) {
		String toUse = ge.getTextDisplayFormat();
		if (toUse == null) {
			toUse = _settings.getTextDisplayFormat();
		}
		if (toUse == null) {
			return "";
		}

		final String dateFormat = _currentView == ISettings.VIEW_MINUTE || _currentView == ISettings.VIEW_DAY ? _settings.getHourDateFormat() : _settings.getDateFormat();

		String toReturn = toUse;

		// we do indexOf on all events, as blind replaceAll is slow when repeatedly called (which we are). It's faster to indexOf and then replace if need be.
		// quick testing shows we (randomly) save about 15ms per large redraw, which is huge.
		// String operations are normally slow
		if (toReturn.indexOf(Constants.STR_NAME) > -1) {
			if (ge.getName() == null) {
				toReturn = toReturn.replaceAll(Constants.STR_NAME, "");
			} else {
				try {
					toReturn = toReturn.replaceAll(Constants.STR_NAME, ge.getName());
				} catch (final IllegalArgumentException e) {
					toReturn = toReturn.replaceAll(Constants.STR_NAME, Matcher.quoteReplacement(ge.getName()));
				}
			}
		}

		if (toReturn.indexOf(Constants.STR_PC) > -1) {
			final StringBuffer buf = new StringBuffer();
			buf.append(ge.getPercentComplete());
			toReturn = toReturn.replaceAll(Constants.STR_PC, buf.toString());
		}

		if (toReturn.indexOf(Constants.STR_SD) > -1) {
			if (ge.getStartDate() == null) {
				toReturn = toReturn.replaceAll(Constants.STR_SD, "");
			} else {
				toReturn = toReturn.replaceAll(Constants.STR_SD, DateHelper.getDate(ge.getStartDate(), dateFormat));
			}
		}

		if (toReturn.indexOf(Constants.STR_ED) > -1) {
			if (ge.getEndDate() == null) {
				toReturn = toReturn.replaceAll(Constants.STR_ED, "");
			} else {
				toReturn = toReturn.replaceAll(Constants.STR_ED, DateHelper.getDate(ge.getEndDate(), dateFormat));
			}
		}

		if (toReturn.indexOf(Constants.STR_RS) > -1) {
			if (ge.getRevisedStart() == null) {
				toReturn = toReturn.replaceAll(Constants.STR_RS, "");
			} else {
				toReturn = toReturn.replaceAll(Constants.STR_RS, DateHelper.getDate(ge.getRevisedStart(), dateFormat));
			}
		}

		if (toReturn.indexOf(Constants.STR_RE) > -1) {
			if (ge.getRevisedEnd() == null) {
				toReturn = toReturn.replaceAll(Constants.STR_RE, "");
			} else {
				toReturn = toReturn.replaceAll(Constants.STR_RE, DateHelper.getDate(ge.getRevisedEnd(), dateFormat));
			}
		}

		if (toReturn.indexOf(Constants.STR_DAYS) > 1) {
			if (ge.getStartDate() == null || ge.getEndDate() == null) {
				toReturn = toReturn.replaceAll(Constants.STR_DAYS, "");
			} else {
				final StringBuffer buf = new StringBuffer();
				final long days = DateHelper.daysBetween(ge.getStartDate(), ge.getEndDate());
				buf.append(days);
				toReturn = toReturn.replaceAll(Constants.STR_DAYS, buf.toString());
			}
		}

		if (toReturn.indexOf(Constants.STR_REV_DAYS) > -1) {
			if (ge.getRevisedStart() == null || ge.getRevisedEnd() == null) {
				toReturn = toReturn.replaceAll(Constants.STR_REV_DAYS, "");
			} else {
				final StringBuffer buf = new StringBuffer();
				final long days = DateHelper.daysBetween(ge.getRevisedStart(), ge.getRevisedEnd());
				buf.append(days);
				toReturn = toReturn.replaceAll(Constants.STR_REV_DAYS, buf.toString());
			}
		}

		return toReturn;
	}

	// draws the three top horizontal lines
	private void drawTopHorizontalLines(final GC gc, final Rectangle bounds) {
		final int yStart = bounds.y;
		final int width = bounds.x + bounds.width;
		final int xStart = bounds.x;

		final int monthHeight = bounds.y + _settings.getHeaderMonthHeight();

		gc.setForeground(_colorManager.getTopHorizontalLinesColor());

		gc.drawLine(xStart, yStart, width, yStart);
		gc.drawLine(xStart, monthHeight, width, monthHeight);
		gc.drawLine(xStart, monthHeight + _settings.getHeaderDayHeight(), width, monthHeight + _settings.getHeaderDayHeight());
	}

	/**
	 * Adds a connection between two GanttEvents. ge1 will connect to ge2.
	 *
	 * @param source Source event
	 * @param target Target event
	 */
	public void addDependency(final GanttEvent source, final GanttEvent target) {
		addDependency(source, target, null);
	}

	/**
	 * Adds a connection between two GanttEvents. <code>Source</code> will connect to <code>Target</code>.
	 *
	 * @param source Source event
	 * @param target Target event
	 * @param Color to use to draw connection. Set null to use default color from Settings.
	 */
	public void addDependency(final GanttEvent source, final GanttEvent target, final Color color) {
		checkWidget();
		if (source == null || target == null) {
			return;
		}

		final GanttConnection con = new GanttConnection(source, target, color);
		con.setParentComposite(this);
		if (!_ganttConnections.contains(con)) {
			_ganttConnections.add(con);
		}
	}

	// connection is added from a GanttConnection class
	void connectionAdded(final GanttConnection conn) {
		checkWidget();

		addDependency(conn.getSource(), conn.getTarget(), conn.getColor());
	}

	void connectionRemoved(final GanttConnection conn) {
		checkWidget();

		_ganttConnections.remove(conn);
	}

	/**
	 * Returns true if the given event is connected to another.
	 *
	 * @param ge GanttEvent to check
	 * @return true if the GanttEvent is connected
	 */
	public boolean isConnected(final GanttEvent ge) {
		for (int i = 0; i < _ganttConnections.size(); i++) {
			final GanttConnection gc = _ganttConnections.get(i);
			if (gc.getSource().equals(ge) || gc.getTarget().equals(ge)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Checks whether two events are connected to each other.
	 *
	 * @param source Source event
	 * @param target Target event
	 * @return true if a connection exists
	 */
	public boolean isConnected(final GanttEvent source, final GanttEvent target) {
		for (int i = 0; i < _ganttConnections.size(); i++) {
			final GanttConnection gc = _ganttConnections.get(i);
			if (gc.getSource().equals(source) && gc.getTarget().equals(target)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Same as addDependency().
	 *
	 * @param source Source event
	 * @param target Target event
	 */
	public void addConnection(final GanttEvent source, final GanttEvent target) {
		addDependency(source, target, null);
	}

	/**
	 * Same as addDependency().
	 *
	 * @param source Source event
	 * @param target Target event
	 * @param Color to use to draw connection. Set null to use defaults.
	 */
	public void addConnection(final GanttEvent source, final GanttEvent target, final Color color) {
		addDependency(source, target, color);
	}

	// the stub that is the first section drawn from the end of an event
	private Rectangle getFirstStub(final GanttConnection con) {
		final GanttEvent ge1 = con.getSource();

		// draw the few pixels right after the box, but don't draw over text
		final int xStart = ge1.getX() + ge1.getWidth();
		final int yStart = ge1.getY() + _eventHeight / 2;

		return new Rectangle(xStart, yStart, 4, _eventHeight);
	}

	// draws the lines and arrows between events
	private void drawConnections(final GC gc) {

		final int dw = getDayWidth();

		for (int i = 0; i < _ganttConnections.size(); i++) {
			final GanttConnection connection = _ganttConnections.get(i);

			final GanttEvent ge1 = connection.getSource();
			final GanttEvent ge2 = connection.getTarget();

			if (ge1 == null || ge2 == null) {
				continue;
			}

			if (ge1.equals(ge2)) {
				continue;
			}

			if (ge1.getX() == 0 && ge2.getY() == 0) {
				continue;
			}

			// don't draw hidden events, nor connections to them
			if (ge1.isHidden() || ge2.isHidden()) {
				continue;
			}

			// use connection color if set, otherwise use arrow color
			gc.setForeground(connection.getColor() == null ? _arrowColor : connection.getColor());

			// same deal but with hidden layers
			if (!_hiddenLayers.isEmpty() && (_hiddenLayers.contains(ge1.getLayerInt()) || _hiddenLayers.contains(ge2.getLayerInt()))) {
				continue;
			}

			final boolean sourceIsOutOfBounds = ge1.getVisibility() != Constants.EVENT_VISIBLE;
			final boolean targetIsOutOfBounds = ge2.getVisibility() != Constants.EVENT_VISIBLE;

			// save precious cycles by not drawing what we don't have to draw (where both events are oob in a way that it's certain no "lines" will cross our view)
			if (sourceIsOutOfBounds && targetIsOutOfBounds) {
				if (ge1.getVisibility() == Constants.EVENT_OOB_LEFT && ge2.getVisibility() == Constants.EVENT_OOB_LEFT) {
					continue;
				}

				if (ge1.getVisibility() == Constants.EVENT_OOB_RIGHT && ge2.getVisibility() == Constants.EVENT_OOB_RIGHT) {
					continue;
				}

				if (ge1.getVisibility() == Constants.EVENT_OOB_TOP && ge2.getVisibility() == Constants.EVENT_OOB_TOP) {
					continue;
				}

				if (ge1.getVisibility() == Constants.EVENT_OOB_BOTTOM && ge2.getVisibility() == Constants.EVENT_OOB_BOTTOM) {
					continue;
				}
			}

			if (_settings.showOnlyDependenciesForSelectedItems()) {
				if (_selectedEvents.isEmpty()) {
					return;
				}

				if (!_selectedEvents.contains(connection.getSource())) {
					continue;
				}
			}

			if (_settings.getArrowConnectionType() != ISettings.CONNECTION_MS_PROJECT_STYLE) {
				if (_settings.getArrowConnectionType() == ISettings.CONNECTION_BIRDS_FLIGHT_PATH) {
					if (ge1.getX() < ge2.getX()) {
						gc.drawLine(ge1.getXEnd(), ge1.getBottomY(), ge2.getX(), ge2.getY());
					} else {
						gc.drawLine(ge1.getX(), ge1.getY(), ge2.getXEnd(), ge2.getBottomY());
					}
					continue;
				}

				// draw the stub.. [event]-- .. -- is the stub
				final Rectangle rect = getFirstStub(connection);
				gc.drawLine(rect.x + 1, rect.y, rect.x + rect.width - 1, rect.y);

				// draw down some, (start at the end of stub and draw down
				// remaining height of event box, + half of the event spacer)
				// --
				// | <-- that part
				final Rectangle down = new Rectangle(rect.x + rect.width, rect.y, rect.width, rect.height / 2 + _eventSpacer / 2);
				gc.drawLine(down.x, down.y, rect.x + down.width, rect.y + down.height);

				// get the top left corner of the target area, then draw a line
				// out to it, only along the x axis
				final Rectangle rGe2 = new Rectangle(ge2.getX() - _settings.getArrowHeadEventSpacer(), ge2.getY() + _settings.getArrowHeadVerticalAdjuster(), ge2.getWidth(), ge2.getHeight());

				boolean goingUp = false;
				boolean goingLeft = false;
				if (rect.y > rGe2.y) {
					goingUp = true;
				}

				if (rect.x > rGe2.x) {
					goingLeft = true;
				}

				if (_settings.getArrowConnectionType() == ISettings.CONNECTION_ARROW_RIGHT_TO_TOP) {

					// draw the line
					gc.drawLine(down.x, rect.y + down.height, rGe2.x, rect.y + down.height);

					// draw the last snippet
					if (goingLeft) {
						if (goingUp) {
							gc.drawLine(rGe2.x, rect.y + down.height, rGe2.x, rGe2.y + _eventHeight + 1);
						} else {
							gc.drawLine(rGe2.x, rect.y + down.height, rGe2.x, rGe2.y);
						}
					} else {
						gc.drawLine(rGe2.x, rect.y + down.height, rGe2.x, rGe2.y);
					}

					if (_settings.showArrows()) {
						if (goingUp) {
							_paintManager.drawArrowHead(rGe2.x, rGe2.y + _eventHeight / 2 + 4, SWT.UP, gc);
						} else {
							_paintManager.drawArrowHead(rGe2.x, rGe2.y - _eventHeight / 2 - 1, SWT.DOWN, gc);
						}
					}

				} else if (_settings.getArrowConnectionType() == ISettings.CONNECTION_ARROW_RIGHT_TO_LEFT) {
					final int offset = 10;

					// first of all, draw a bit further of the line we just
					// created
					gc.drawLine(down.x, rect.y + down.height, rGe2.x - offset, rect.y + down.height);

					gc.drawLine(rGe2.x - offset, rect.y + down.height, rGe2.x - offset, rGe2.y + _eventHeight / 2);

					// draw the last snippet
					gc.drawLine(rGe2.x - offset, rGe2.y + _eventHeight / 2, rGe2.x, rGe2.y + _eventHeight / 2);

					if (_settings.showArrows()) {
						if (goingUp) {
							_paintManager.drawArrowHead(rGe2.x - 7, rGe2.y + _eventHeight / 2, SWT.LEFT, gc);
						} else {
							_paintManager.drawArrowHead(rGe2.x - 7, rGe2.y + _eventHeight / 2, SWT.RIGHT, gc);
						}
					}
				}
			} else {
				// MS Project style basically means as follows
				// 1. All corners are rounded
				// 2. Arrows go from event [above] to event [below] and connect
				// to [below] on the left middle side if [above] comes after
				// [below]
				// 3. Arrows connect from event [above] to event [below] to
				// [below]'s top if [below] is far enough away right-side wise
				// from [above]
				final Rectangle rect = getFirstStub(connection);
				int x = rect.x;
				int y = rect.y;
				
				// fix for bug 373090
				// minimum distance between two events to draw ms project style connections correctly
				final int deltaX = 15;

				final boolean aboveUs = ge2.getY() < ge1.getY();
				final boolean belowUs = ge2.getY() > ge1.getY();
				final boolean sameRow = ge2.getY() == ge1.getY();
				final boolean targetIsOnLeft = ge2.getXEnd() < ge1.getX();
				final boolean targetIsOnRight = ge2.getX() > ge1.getXEnd() + deltaX;

				final Rectangle bounds1 = ge1.getBounds();
				final Rectangle bounds2 = ge2.getBounds();
				// fake same line
				bounds1.y = bounds2.y;
				final boolean eventsOverlap = bounds1.intersects(bounds2);
				final boolean targetIsOnLeftBorder = ge2.getXEnd() == ge1.getX();
				final boolean targetIsOnRightBorder = ge2.getX() - ge1.getXEnd() < deltaX;

				final int neg = 8;

				Point xy = null;
				final boolean isLinux = _osType == Constants.OS_LINUX;

				if (belowUs) {
					gc.setForeground(connection.getColor() == null ? _arrowColor : connection.getColor());

					// draw first stub
					gc.drawLine(x, y, x + rect.width, y);
					x += rect.width;

					xy = drawBend(gc, Constants.BEND_RIGHT_DOWN, x - (isLinux ? 1 : 0), y, true);
					x = xy.x;
					y = xy.y;

					if (targetIsOnRight) {
						// #1 vert line
						final int yTarget = ge2.getY() + ge2.getHeight() / 2;
						gc.drawLine(x, y, x, yTarget - (isLinux ? 1 : 2)); // minus 2 as we need another bend
						y = yTarget - 2;

						// #2 bend
						xy = drawBend(gc, Constants.BEND_RIGHT_DOWN, x, y, true);
						x = xy.x;
						y = xy.y;

						// #3 line
						gc.drawLine(x, y, ge2.getX(), y);

						// #4 arrow
						if (_settings.showArrows()) {
							x = ge2.getX() - 8;
							_paintManager.drawArrowHead(x, y, SWT.RIGHT, gc);
						}
					} else if (targetIsOnLeft || eventsOverlap || targetIsOnLeftBorder || targetIsOnRightBorder) {
						// for left side we draw the vertical line down to the middle between the events
						// int yDiff = (ge2.getY() - (ge1.getY() + ge1.getHeight())) / 2;
						// gc.drawLine(x, y, x, ge1.getY() + ge1.getHeight() + yDiff - 2);
						final int yDest = ge1.getBottomY() + _eventSpacer / 2;
						gc.drawLine(x, y, x, yDest);
						y = yDest;// ge1.getY() + ge1.getHeight() + yDiff - 2;

						if (isLinux) {
							y -= 1;
						}

						// #2 bend
						xy = drawBend(gc, Constants.BEND_LEFT_DOWN, x, y, true);
						x = xy.x;
						y = xy.y;

						// #3 line
						gc.drawLine(x, y, ge2.getX() - neg, y);
						x = ge2.getX() - neg;

						// #4 bend
						xy = drawBend(gc, Constants.BEND_LEFT_DOWN, x, y, true);
						x = xy.x;
						y = xy.y;

						// #5 vert line
						final int yTarget = ge2.getY() + ge2.getHeight() / 2;
						gc.drawLine(x, y, x, yTarget - (isLinux ? 1 : 2)); // minus 2 as we need another bend
						y = yTarget - 2;

						// #6 bend
						xy = drawBend(gc, Constants.BEND_RIGHT_DOWN, x, y, true);
						x = xy.x;
						y = xy.y;

						// #7 line
						gc.drawLine(x, y, ge2.getX(), y);

						// #8 arrow
						if (_settings.showArrows()) {
							x = ge2.getX() - 8;
							_paintManager.drawArrowHead(x, y, SWT.RIGHT, gc);
						}

					}
				} else if (aboveUs) {
					gc.setForeground(connection.getColor() == null ? _reverseArrowColor : connection.getColor());

					// draw first stub
					gc.drawLine(x, y, x + rect.width, y);
					x += rect.width;

					if (targetIsOnLeft || eventsOverlap || targetIsOnRightBorder || targetIsOnLeftBorder) {
						// #0 bend
						xy = drawBend(gc, Constants.BEND_RIGHT_DOWN, x, y, true);
						x = xy.x;
						y = xy.y;

						// #1 vertical
						final int yDest = ge1.getBottomY() + _eventSpacer / 2;
						gc.drawLine(x, y, x, yDest);
						y = yDest;

						// #2 bend
						xy = drawBend(gc, Constants.BEND_LEFT_DOWN, x, y, true);
						x = xy.x;
						y = xy.y;

						// #3 line (not -8 as we don't want line overlap for events that draw down and use -8)
						gc.drawLine(x, y, ge2.getX() - neg - _settings.getReverseDependencyLineHorizontalSpacer(), y);
						x = ge2.getX() - neg - _settings.getReverseDependencyLineHorizontalSpacer();

						// #4 bend
						xy = drawBend(gc, Constants.BEND_LEFT_UP, x, y, true);
						x = xy.x;
						y = xy.y;

						// #5 vert up
						if (_settings.useSplitArrowConnections()) {
							gc.drawLine(x, y, x, ge2.getY() + ge2.getHeight());
							y = ge2.getY() + ge2.getHeight();
						} else {
							gc.drawLine(x, y, x, ge2.getY() + ge2.getHeight() / 2 + 2);
							y = ge2.getY() + ge2.getHeight() / 2 + 2;
						}

						// #6 bend
						xy = drawBend(gc, Constants.BEND_RIGHT_UP, x, y, true);
						x = xy.x;
						y = xy.y;

						// #7 horizontal
						gc.drawLine(x, y, ge2.getX(), y);

						// #8 arrow
						if (_settings.showArrows()) {
							x = ge2.getX() - 8;
							_paintManager.drawArrowHead(x, y, SWT.RIGHT, gc);
						}
					} else if (targetIsOnRight) {
						// #1 bend
						xy = drawBend(gc, Constants.BEND_RIGHT_UP, x, y, true);
						x = xy.x;
						y = xy.y;

						// #2 vert up
						if (_settings.useSplitArrowConnections()) {
							gc.drawLine(x, y, x, ge2.getY() + ge2.getHeight());
							y = ge2.getY() + ge2.getHeight();
						} else {
							gc.drawLine(x, y, x, ge2.getY() + ge2.getHeight() / 2 + 2);
							y = ge2.getY() + ge2.getHeight() / 2 + 2;
						}

						// #3 bend
						xy = drawBend(gc, Constants.BEND_RIGHT_UP, x, y, true);
						x = xy.x;
						y = xy.y;

						// #4 horizontal
						gc.drawLine(x, y, ge2.getX(), y);

						// #5 arrow
						if (_settings.showArrows()) {
							x = ge2.getX() - 8;
							_paintManager.drawArrowHead(x, y, SWT.RIGHT, gc);
						}
					}
				} else if (sameRow) {
					if (targetIsOnLeft || eventsOverlap || targetIsOnRightBorder || targetIsOnLeftBorder) {
						gc.setForeground(connection.getColor() == null ? _reverseArrowColor : connection.getColor());

						// draw first stub
						gc.drawLine(x, y, x + rect.width, y);
						x += rect.width;

						// #1 bend
						xy = drawBend(gc, Constants.BEND_RIGHT_DOWN, x, y, true);
						x = xy.x;
						y = xy.y;

						// #2 vert down
						if (_settings.useSplitArrowConnections()) {
							gc.drawLine(x, y, x, ge2.getY() + ge2.getHeight() + _eventSpacer / 2);
							y = ge2.getY() + ge2.getHeight();
						} else {
							gc.drawLine(x, y, x, ge2.getY() + ge2.getHeight() / 2 + 2 + _eventSpacer / 2);
							y = ge2.getY() + ge2.getHeight() / 2 + 2;
						}
						y += _eventSpacer / 2;

						// #1 bend
						xy = drawBend(gc, Constants.BEND_LEFT_DOWN, x, y, true);
						x = xy.x;
						y = xy.y;

						// #4 horizontal
						gc.drawLine(x, y, ge2.getX() - neg - neg / 2, y);
						x = ge2.getX() - neg - neg / 2;

						// #5 bend
						xy = drawBend(gc, Constants.BEND_LEFT_UP, x, y, true);
						x = xy.x;
						y = xy.y;

						// #6 vert up
						if (_settings.useSplitArrowConnections()) {
							gc.drawLine(x, y, x, ge2.getY() + ge2.getHeight() / 2 + neg - _settings.getReverseDependencyLineHorizontalSpacer());
							y = ge2.getY() + ge2.getHeight() / 2 + neg - _settings.getReverseDependencyLineHorizontalSpacer();
						} else {
							gc.drawLine(x, y, x, ge2.getY() + ge2.getHeight() / 2 + neg - _settings.getReverseDependencyLineHorizontalSpacer());
							y = ge2.getY() + ge2.getHeight() / 2 + neg - _settings.getReverseDependencyLineHorizontalSpacer();
						}

						// #7 bend
						xy = drawBend(gc, Constants.BEND_RIGHT_UP, x, y, true);
						x = xy.x;
						y = xy.y;

						// #8 last straight
						gc.drawLine(x, y, ge2.getX(), y);

						if (_settings.showArrows()) {
							x = ge2.getX() - 8;
							_paintManager.drawArrowHead(x, y, SWT.RIGHT, gc);
						}
					} else if (targetIsOnRight) {
						gc.setForeground(connection.getColor() == null ? _arrowColor : connection.getColor());

						// if distance between left and right is smaller than the width of a day small, we draw it differently or we'll get a funny bend
						if (ge2.getX() - ge1.getXEnd() <= dw) {
							gc.drawLine(ge1.getXEnd(), y, ge2.getX(), y);

							if (_settings.showArrows()) {
								x = ge2.getX() - 8;
								_paintManager.drawArrowHead(x, y, SWT.RIGHT, gc);
							}
							continue;
						}

						// draw first stub
						gc.drawLine(x, y, x + rect.width, y);
						x += rect.width;

						// #1 bend
						xy = drawBend(gc, Constants.BEND_RIGHT_DOWN, x, y, true);
						x = xy.x;
						y = xy.y;

						// #2 vert down
						if (_settings.useSplitArrowConnections()) {
							gc.drawLine(x, y, x, ge2.getY() + ge2.getHeight() + _eventSpacer / 2);
							y = ge2.getY() + ge2.getHeight();
						} else {
							gc.drawLine(x, y, x, ge2.getY() + ge2.getHeight() / 2 + 2 + _eventSpacer / 2);
							y = ge2.getY() + ge2.getHeight() / 2 + 2;
						}
						y += _eventSpacer / 2;

						// #3 bend
						xy = drawBend(gc, Constants.BEND_RIGHT_DOWN, x, y, true);
						x = xy.x;
						y = xy.y;

						// #4 horizontal
						gc.drawLine(x, y, ge2.getX() - neg - neg / 2, y);
						x = ge2.getX() - neg - neg / 2;

						// #5 bend
						xy = drawBend(gc, Constants.BEND_RIGHT_UP, x, y, true);
						x = xy.x;
						y = xy.y;

						// #6 vert down
						if (_settings.useSplitArrowConnections()) {
							gc.drawLine(x, y, x, ge2.getY() + ge2.getHeight() / 2 + 2);
							y = ge2.getY() + ge2.getHeight() / 2 + 2;
						} else {
							gc.drawLine(x, y, x, ge2.getY() + ge2.getHeight() / 2 + 2);
							y = ge2.getY() + ge2.getHeight() / 2 + 2;
						}

						// #7 bend
						xy = drawBend(gc, Constants.BEND_RIGHT_UP, x, y, true);
						x = xy.x;
						y = xy.y;

						// #8 last straight
						gc.drawLine(x, y, ge2.getX(), y);

						if (_settings.showArrows()) {
							x = ge2.getX() - 8;
							_paintManager.drawArrowHead(x, y, SWT.RIGHT, gc);
						}
					}
				}
			}
		}

		gc.setLineStyle(SWT.LINE_SOLID);
		gc.setLineWidth(1);
	}

	private Point drawBend(final GC gc, final int style, final int x, final int y, final boolean rounded) {
		final Point xy = new Point(0, 0);
		final int bonus = _osType == Constants.OS_LINUX ? 1 : 0;
		if (rounded) {
			switch (style) {
				case Constants.BEND_RIGHT_UP:
					gc.drawLine(x + 1, y - 1, x + 1 + bonus, y - 1 - bonus);
					gc.drawLine(x + 2, y - 2, x + 2 + bonus, y - 2 - bonus);
					xy.x = x + 2;
					xy.y = y - 2;
					break;
				case Constants.BEND_RIGHT_DOWN:
					gc.drawLine(x + 1, y + 1, x + 1 + bonus, y + 1 + bonus);
					gc.drawLine(x + 2, y + 2, x + 2 + bonus, y + 2 + bonus);
					xy.x = x + 2;
					xy.y = y + 2;
					break;
				case Constants.BEND_LEFT_DOWN:
					gc.drawLine(x - 1, y + 1, x - 1 + bonus, y + 1 + bonus);
					gc.drawLine(x - 2, y + 2, x - 2 + bonus, y + 2 + bonus);
					xy.x = x - 2;
					xy.y = y + 2;
					break;
				case Constants.BEND_LEFT_UP:
					gc.drawLine(x - 1, y - 1, x - 1 + bonus, y - 1 - bonus);
					gc.drawLine(x - 2, y - 2, x - 2 + bonus, y - 2 - bonus);
					xy.x = x - 2;
					xy.y = y - 2;
					break;
				default:
					break;
			}
		} else {
			xy.x = x;
			xy.y = y;
		}

		// non rounded will return 0,0 which is exactly correct
		return xy;
	}

	// draws the line showing where today's date is
	private void drawTodayLine(final GC gc, final Rectangle bounds, final int x, final int dayOfWeek, final Color lineColor) {
		// d-day has no today
		if (_currentView == ISettings.VIEW_D_DAY) {
			return;
		}

		int xStart = x;

		gc.setForeground(lineColor);
		gc.setLineWidth(_settings.getTodayLineWidth());
		gc.setLineStyle(_settings.getTodayLineStyle());

		// to not clash with the week divider lines move dotted line 1 pixel left on new weeks
		if ((_currentView == ISettings.VIEW_WEEK || _currentView == ISettings.VIEW_MONTH) && dayOfWeek == _mainCalendar.getFirstDayOfWeek()) {
			xStart--;
		}

		int vOffset = _settings.getTodayLineVerticalOffset();
		int yStart = bounds.y - _settings.getHeaderMonthHeight();
		if (!_settings.drawHeader()) {
			vOffset = 0;
		}
		yStart += vOffset;
		if (!_settings.drawHeader()) {
			yStart = bounds.y;
		}

		yStart -= _vScrollPos;

		if (_useAlpha) {
			gc.setAlpha(_colorManager.getTodayLineAlpha());
		}

		gc.drawLine(xStart, yStart, xStart, bounds.height + yStart + _vScrollPos);
		if (_useAlpha) {
			gc.setAlpha(0);
			gc.setAdvanced(false);
		}

		// reset lines etc
		gc.setLineWidth(1);
		gc.setLineStyle(SWT.LINE_SOLID);
	}

	// the Date string that is displayed at the very top
	private String getDateString(final Calendar cal, final boolean top) {
		if (top) {
			switch (_currentView) {
				case ISettings.VIEW_MINUTE:
					return DateHelper.getDate(cal, ((ISettings2) _settings).getMinuteHeaderTextDisplayFormatTop());
				case ISettings.VIEW_WEEK:
					return DateHelper.getDate(cal, _settings.getWeekHeaderTextDisplayFormatTop());
				case ISettings.VIEW_MONTH:
					return DateHelper.getDate(cal, _settings.getMonthHeaderTextDisplayFormatTop());
				case ISettings.VIEW_DAY:
					return DateHelper.getDate(cal, _settings.getDayHeaderTextDisplayFormatTop());
				case ISettings.VIEW_YEAR:
					return DateHelper.getDate(cal, _settings.getYearHeaderTextDisplayFormatTop());
				default:
					break;
			}
		} else {
			switch (_currentView) {
				case ISettings.VIEW_MINUTE:
					return DateHelper.getDate(cal, ((ISettings2) _settings).getMinuteHeaderTextDisplayFormatBottom());
				case ISettings.VIEW_WEEK:
					return DateHelper.getDate(cal, _settings.getWeekHeaderTextDisplayFormatBottom()).substring(0, 1);
				case ISettings.VIEW_MONTH:
					return DateHelper.getDate(cal, _settings.getMonthHeaderTextDisplayFormatBottom());
				case ISettings.VIEW_DAY:
					return DateHelper.getDate(cal, _settings.getDayHeaderTextDisplayFormatBottom());
				case ISettings.VIEW_YEAR:
					return DateHelper.getDate(cal, _settings.getYearHeaderTextDisplayFormatBottom());
				default:
					break;
			}
		}

		return cal.getTime().toString();
	}

	/**
	 * Sets the calendar date.
	 *
	 * @param date
	 * @see #setDate(Calendar, boolean)
	 * @see #setDate(Calendar, int)
	 * @see #setDate(Calendar, int, boolean)
	 */
	public void setDate(final Calendar date) {
		checkWidget();
		setDate(date, false);
	}

	/**
	 * Moves calendar to the current date/time.
	 */
	public void jumpToToday() {
		jumpToToday(SWT.LEFT);
	}

	/**
	 * Moves calendar to the current date/time.
	 *
	 * @param side one of <code>SWT.LEFT</code>, <code>SWT.CENTER</code>, <code>SWT.RIGHT</code>
	 */
	public void jumpToToday(final int side) {
		checkWidget();

		final Calendar cal = Calendar.getInstance(_defaultLocale);

		if (_currentView == ISettings.VIEW_MINUTE || _currentView == ISettings.VIEW_DAY) {
			// round it down
			internalSetDate(cal, side, true, true);
		} else {
			setDate(cal, side, false);
		}
	}

	/**
	 * Moves the calendar to the earliest event date.
	 */
	public void jumpToEarliestEvent() {
		jumpToEvent(true);
	}

	/**
	 * Moves the calendar to the latest event date.
	 */
	// Thanks Wim!
	public void jumpToLatestEvent() {
		/*
		 * Calendar cal = Calendar.getInstance(mDefaultLocale);
		 * GanttEvent latest = getEvent(false, false);
		 * if (latest == null) return;
		 *
		 * flagForceFullUpdate();
		 * cal.setTime(latest.getActualStartDate().getTime());
		 *
		 * if (mCurrentView == ISettings.VIEW_DAY) {
		 * internalSetDate(cal, SWT.LEFT, true, true);
		 * } else {
		 * setDate(cal, false);
		 * }
		 */
		jumpToEvent(false);
	}

	private void jumpToEvent(final boolean earliestEvent) {
		final Calendar cal = Calendar.getInstance(_defaultLocale);
		final Date earliest = getEventDate(earliestEvent);
		if (earliest == null) {
			return;
		}

		flagForceFullUpdate();
		cal.setTime(earliest);

		if (_currentView == ISettings.VIEW_MINUTE || _currentView == ISettings.VIEW_DAY) {
			// round it down
			internalSetDate(cal, SWT.LEFT, true, true);
		} else {
			setDate(cal, false);
		}
	}

	/**
	 * Moves the calendar to a particular event date horizontally. To move to an event completely, you may use
	 * {@link #setTopItem(GanttEvent)} or {@link #setTopItem(GanttEvent, int)}.
	 *
	 * @param event Event to move to
	 * @param start true if to jump to the start date, false if to jump to the end date.
	 * @param side one of <code>SWT.LEFT</code>, <code>SWT.CENTER</code>, <code>SWT.RIGHT</code>
	 */
	public void jumpToEvent(final GanttEvent event, final boolean start, final int side) {
		final Calendar cal = Calendar.getInstance(_defaultLocale);

		if (start) {
			cal.setTime(event.getActualStartDate().getTime());
		} else {
			cal.setTime(event.getActualEndDate().getTime());
		}

		internalSetDate(cal, side, true, true);
	}

	private GanttEvent getEvent(final boolean earliest, final boolean pixelComparison) {
		Calendar cal = null;
		GanttEvent retEvent = null;

		for (int i = 0; i < _ganttEvents.size(); i++) {
			final GanttEvent ge = _ganttEvents.get(i);
			if (earliest) {
				if (cal == null) {
					cal = ge.getEarliestStartDate();
					retEvent = ge;
					continue;
				}

				if (pixelComparison) {
					final Rectangle r = ge.getActualBounds();
					if (r.x < retEvent.getActualBounds().x) {
						cal = ge.getEarliestStartDate();
						retEvent = ge;
					}
				} else {
					if (ge.getEarliestStartDate().before(cal)) {
						cal = ge.getEarliestStartDate();
						retEvent = ge;
					}
				}
			} else {
				if (cal == null) {
					cal = ge.getLatestEndDate();
					retEvent = ge;
					continue;
				}

				if (pixelComparison) {
					final Rectangle r = ge.getActualBounds();
					final Rectangle other = retEvent.getActualBounds();
					if (r.x + r.width > other.x + other.width) {
						cal = ge.getLatestEndDate();
						retEvent = ge;
					}
				} else {
					if (ge.getLatestEndDate().after(cal)) {
						cal = ge.getLatestEndDate();
						retEvent = ge;
					}
				}
			}
		}

		return retEvent;
	}

	private Date getEventDate(final boolean earliest) {
		Calendar ret = null;

		for (int i = 0; i < _ganttEvents.size(); i++) {
			final GanttEvent ge = _ganttEvents.get(i);
			if (earliest) {
				if (ret == null) {
					ret = ge.getActualStartDate();
					continue;
				}

				if (ge.getActualStartDate().before(ret)) {
					ret = ge.getActualStartDate();
				}
			} else {
				if (ret == null) {
					ret = ge.getActualEndDate();
					continue;
				}

				if (ge.getActualEndDate().after(ret)) {
					ret = ge.getActualEndDate();
				}
			}
		}

		return ret == null ? null : ret.getTime();
	}

	/**
	 * Sets the calendar date to the given date and shows it on the chart. You may provide the side that the date is to
	 * be visible on. This method does not apply any offset or other settings-related magic, but sets the date "purely".
	 * This method will only clear minutes, seconds and milliseconds if the clearMinutes variable is set to true.
	 *
	 * @see GanttComposite#setDate(Calendar, int)
	 * @param date Date
	 * @param side one of <code>SWT.LEFT</code>, <code>SWT.CENTER</code>, <code>SWT.RIGHT</code>
	 * @param clearMinutes true if to clear minutes, seconds, milliseconds
	 */
	public void setDate(final Calendar date, final int side, final boolean clearMinutes) {
		internalSetDate(date, side, clearMinutes, true);
	}

	/**
	 * Sets the calendar date to the given date and shows it on the chart. You may provide the side that the date is to
	 * be visible on. This method does not apply any offset or other settings-related magic, but sets the date "purely".
	 * This method will clear minutes, seconds and milliseconds and set them to zero. If you do not wish this, use
	 * {@link #setDate(Calendar, int, boolean)}
	 *
	 * @param date Date
	 * @param side one of <code>SWT.LEFT</code>, <code>SWT.CENTER</code>, <code>SWT.RIGHT</code>
	 */
	public void setDate(final Calendar date, final int side) {
		internalSetDate(date, side, true, true);
	}

	private void internalSetDateAtX(final int x, final Calendar preZoomDate, final boolean clearMinutes, final boolean redraw, final boolean zoomIn) {
		final Calendar target = getDateAt(x);

		if (_currentView == ISettings.VIEW_MINUTE || _currentView == ISettings.VIEW_DAY) {
			int hours = DateHelper.hoursBetween(target, preZoomDate, false);
			final Calendar toSet = DateHelper.getNewCalendar(_mainCalendar);
			if (zoomIn) {
				hours = Math.abs(hours);
			}

			toSet.add(Calendar.HOUR_OF_DAY, hours);

			internalSetDate(toSet, SWT.LEFT, clearMinutes, redraw);
		} else {
			int days = (int) DateHelper.daysBetween(target, preZoomDate);
			if (zoomIn) {
				days = Math.abs(days);
			}

			final Calendar toSet = DateHelper.getNewCalendar(_mainCalendar);
			toSet.add(Calendar.DATE, days);
			internalSetDate(toSet, SWT.LEFT, clearMinutes, redraw);
		}
	}

	private void internalSetDate(final Calendar date, final int side, final boolean clearMinutes, final boolean redraw) {
		checkWidget();

		// set the date regardless, thus the below "jump" will be minor
		_mainCalendar = DateHelper.getNewCalendar(date);
		if (clearMinutes) {
			_mainCalendar.set(Calendar.MINUTE, 0);
			_mainCalendar.set(Calendar.SECOND, 0);
			_mainCalendar.set(Calendar.MILLISECOND, 0);
		}

		// create a copy, don't modify the original
		final Calendar copy = DateHelper.getNewCalendar(date);

		copy.set(Calendar.HOUR_OF_DAY, 0);
		if (clearMinutes) {
			copy.set(Calendar.MINUTE, 0);
			copy.set(Calendar.SECOND, 0);
			copy.set(Calendar.MILLISECOND, 0);
		}

		switch (side) {
			case SWT.LEFT:
				if (_currentView == ISettings.VIEW_DAY) {
					copy.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY));
				} else if (_currentView == ISettings.VIEW_MINUTE) {
					copy.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY));
					copy.set(Calendar.MINUTE, date.get(Calendar.MINUTE));
				}
				break;
			case SWT.CENTER:
				if (_currentView == ISettings.VIEW_MINUTE) {
					final int middleMinutes = _hoursVisible / 2;
					copy.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY));
					copy.set(Calendar.MINUTE, date.get(Calendar.MINUTE));
					copy.add(Calendar.MINUTE, -middleMinutes);
				} else if (_currentView == ISettings.VIEW_DAY) {
					final int middleHours = _hoursVisible / 2;
					copy.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY));
					copy.add(Calendar.HOUR_OF_DAY, -middleHours);
				} else {
					final int middle = _daysVisible / 2;
					copy.add(Calendar.DATE, -middle);
				}
				break;
			case SWT.RIGHT:
				if (_currentView == ISettings.VIEW_MINUTE) {
					copy.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY));
					copy.set(Calendar.MINUTE, date.get(Calendar.MINUTE));
					copy.add(Calendar.MINUTE, -_hoursVisible);
				} else if (_currentView == ISettings.VIEW_DAY) {
					copy.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY));
					copy.add(Calendar.HOUR_OF_DAY, -_hoursVisible);
				} else {
					copy.add(Calendar.DATE, -_daysVisible + 1);
				}
				break;
			default:
				break;
		}

		_mainCalendar = copy;
		_recalcScopes = true;
		_recalcSecBounds = true;

		if (redraw) {
			redraw();
		}
	}

	/**
	 * Sets the new date of the calendar and redraws. This method will apply any offsets and other date magic that is
	 * set in the Settings.
	 *
	 * @param date Date to set
	 * @param applyOffset whether to apply the settings offset
	 * @see #setDate(Calendar)
	 * @see #setDate(Calendar, int)
	 * @see #setDate(Calendar, int, boolean)
	 */
	public void setDate(final Calendar date, final boolean applyOffset) {
		setDate(date, applyOffset, true);
	}

	private void setDate(final Calendar date, final boolean applyOffset, final boolean redraw) {
		checkWidget();
		// create a copy, don't modify the original
		final Calendar copy = DateHelper.getNewCalendar(date);

		if (applyOffset) {
			copy.add(Calendar.DATE, _calStartOffset);
		}

		// not for D-Days, there is no such thing as start of week there
		if (_settings.startCalendarOnFirstDayOfWeek() && _currentView != ISettings.VIEW_D_DAY) {
			copy.set(Calendar.DAY_OF_WEEK, copy.getFirstDayOfWeek());
		}

		internalSetDate(copy, SWT.LEFT, true, redraw);
	}

	/**
	 * Re-indexes an event to a new index.
	 *
	 * @param event GanttEvent to reindex
	 * @param newIndex new index
	 */
	public void reindex(final GanttEvent event, final int newIndex) {
		_ganttEvents.remove(event);
		_ganttEvents.add(newIndex, event);
		redrawEventsArea();
	}

	/**
	 * Re-indexes a GanttSection to a new index.
	 *
	 * @param section GanttSection to reindex
	 * @param newIndex new index
	 */
	public void reindex(final GanttSection section, final int newIndex) {
		_ganttSections.remove(section);
		_ganttSections.add(newIndex, section);
		redrawEventsArea();
	}

	/**
	 * Re-indexes a GanttGroup to a new index.
	 *
	 * @param group GanttGroup to reindex
	 * @param newIndex new index
	 */
	public void reindex(final GanttGroup group, final int newIndex) {
		checkWidget();
		_ganttGroups.remove(group);
		_ganttGroups.add(newIndex, group);
		redrawEventsArea();
	}

	/**
	 * Adds a GanttPhase to the chart.
	 *
	 * @param phase GanttPhase to add
	 */
	public void addPhase(final GanttPhase phase) {
		checkWidget();
		addPhase(phase, true);
	}

	/**
	 * Removes a GanttPhase from the chart.
	 *
	 * @param phase GanttPhase to remove
	 */
	public void removePhase(final GanttPhase phase) {
		removePhase(phase, true);
	}

	/**
	 * Adds a {@link GanttSpecialDateRange} to the chart.
	 *
	 * @param range {@link GanttSpecialDateRange} to add.
	 */
	public void addSpecialDateRange(final GanttSpecialDateRange range) {
		addSpecialDateRange(range, true);
	}

	/**
	 * Removes a {@link GanttSpecialDateRange} to the chart and redraws.
	 *
	 * @param range {@link GanttSpecialDateRange} to remove
	 */
	public void removeSpecialDateRange(final GanttSpecialDateRange range) {
		removeSpecialDateRange(range, true);
	}

	/**
	 * Removes a {@link GanttSpecialDateRange} to the chart and redraws.
	 *
	 * @param range {@link GanttSpecialDateRange} to remove
	 * @param redraw true to redraw
	 */
	public void removeSpecialDateRange(final GanttSpecialDateRange range, final boolean redraw) {
		checkWidget();
		_specDateRanges.remove(range);

		if (redraw) {
			redraw();
		}
	}

	/**
	 * Removes all {@link GanttSpecialDateRange}s and redraws.
	 */
	public void clearSpecialDateRanges() {
		checkWidget();
		_specDateRanges.clear();
		redraw();
	}

	/**
	 * Adds a {@link GanttSpecialDateRange} and optionally redraws.
	 *
	 * @param range {@link GanttSpecialDateRange} to add
	 * @param redraw true to redraw
	 */
	public void addSpecialDateRange(final GanttSpecialDateRange range, final boolean redraw) {
		checkWidget();
		if (!_specDateRanges.contains(range)) {
			_specDateRanges.add(range);
		}

		if (redraw) {
			redraw();
		}
	}

	/**
	 * Adds a GanttPhase to the chart with optional redraw call.
	 *
	 * @param phase GanttPhase to add
	 * @param redraw true to redraw
	 */
	public void addPhase(final GanttPhase phase, final boolean redraw) {
		checkWidget();
		if (!_ganttPhases.contains(phase)) {
			_ganttPhases.add(phase);
		}

		if (redraw) {
			redraw();
		}
	}

	/**
	 * Removes a GanttPhase from the chart with optional redraw call.
	 *
	 * @param phase GanttPhase to remove
	 * @param redraw true to redraw
	 */
	public void removePhase(final GanttPhase phase, final boolean redraw) {
		checkWidget();
		_ganttPhases.remove(phase);

		if (redraw) {
			redraw();
		}
	}

	/**
	 * Removes all GanttPhases from the chart
	 */
	public void clearPhases() {
		_ganttPhases.clear();
		redraw();
	}

	/**
	 * Adds a GanttEvent to the chart.
	 *
	 * @param event GanttEvent
	 */
	public void addEvent(final GanttEvent event) {
		checkWidget();
		addEvent(event, true);
	}

	/**
	 * Adds an event at a given index.
	 *
	 * @param event GanttEvent
	 * @param index index
	 */
	public void addEvent(final GanttEvent event, final int index) {
		checkWidget();

		internalAddEvent(index, event);

		// full redraw, as event hasn't been added yet so bounds will not return
		// included new event
		redraw();
	}

	/**
	 * Adds an GanttEvent to the chart and redraws.
	 *
	 * @param event GanttEvent
	 * @param redraw true if to redraw chart
	 */
	public void addEvent(final GanttEvent event, final boolean redraw) {
		checkWidget();

		internalAddEvent(-1, event);

		// full redraw, as event hasn't been added yet so bounds will not return
		// included new event
		if (redraw) {
			redraw();
		}
	}

	/**
	 * Removes a GanttEvent from the chart.
	 *
	 * @param event GanttEvent to remove
	 * @return true if removed
	 */
	public boolean removeEvent(GanttEvent event) {
		if (event == null) {
			return false;
		}

		checkWidget();

		internalRemoveEvent(event);

		final List toRemove = new ArrayList();
		for (int i = 0; i < _ganttConnections.size(); i++) {
			final GanttConnection con = _ganttConnections.get(i);

			if (con.getSource().equals(event) || con.getTarget().equals(event)) {
				toRemove.add(con);
			}

		}
		for (int i = 0; i < toRemove.size(); i++) {
			_ganttConnections.remove(toRemove.get(i));
		}

		// eventNumbersChanged();
		if (event.getScopeParent() != null) {
			event.getScopeParent().removeScopeEvent(event);
		}
		if (event.getGanttSection() != null) {
			event.getGanttSection().removeGanttEvent(event);
		}
		if (event.getGanttGroup() != null) {
			event.getGanttGroup().removeEvent(event);
		}
		final boolean ret = _ganttEvents.remove(event);

		redrawEventsArea();

		return ret;
	}

	/**
	 * Returns all currently connected events as a list of {@link GanttConnection} objects.
	 *
	 * @return List of connections.
	 */
	public List getGanttConnections() {
		return _ganttConnections;
	}

	void eventDatesChanged(final GanttEvent ge, final boolean redraw) {
		final int newStartX = getStartingXFor(ge);
		final int newEndX = getXLengthForEvent(ge);

		ge.updateX(newStartX);
		ge.updateWidth(newEndX);
		if (redraw) {
			redraw();
		}
	}

	/**
	 * Clears all GanttEvents events from the chart.
	 */
	public void clearGanttEvents() {
		checkWidget();
		_ganttEvents.clear();
		_ganttConnections.clear();
		// mGmap.clear();
		eventNumbersChanged();
		_forceSBUpdate = true;
		_vScrollPos = 0;
		_lastVScrollPos = 0;
		_vScrollBar.setSelection(0);
		redraw();
	}

	/**
	 * Clears all GanttGroups from the chart.
	 */
	public void clearGanttGroups() {
		checkWidget();
		_ganttGroups.clear();
		eventNumbersChanged();
		_forceSBUpdate = true;
		_vScrollPos = 0;
		_lastVScrollPos = 0;
		_vScrollBar.setSelection(0);
		redraw();
	}

	/**
	 * Clears all GanttSections from the chart.
	 */
	public void clearGanttSections() {
		checkWidget();
		_ganttSections.clear();
		eventNumbersChanged();
		_forceSBUpdate = true;
		_vScrollPos = 0;
		_lastVScrollPos = 0;
		_vScrollBar.setSelection(0);
		redraw();
	}

	/**
	 * Clears the entire chart of everything (all types of events) and leaves the chart blank.
	 */
	public void clearChart() {
		checkWidget();
		_ganttEvents.clear();
		_ganttConnections.clear();
		_ganttSections.clear();
		_ganttGroups.clear();
		_ganttPhases.clear();
		_specDateRanges.clear();
		// mGmap.clear();
		eventNumbersChanged();
		_forceSBUpdate = true;
		_vScrollPos = 0;
		_lastVScrollPos = 0;
		_vScrollBar.setSelection(0);
		flagForceFullUpdate();
		redraw();
	}

	/**
	 * Checks whether the chart has a given event.
	 *
	 * @param event GanttEvent
	 * @return true if event exists
	 */
	public boolean hasEvent(final GanttEvent event) {
		checkWidget();
		return _ganttEvents.contains(event);
	}

	/**
	 * Jumps to the next month.
	 */
	public void nextMonth() {
		_viewPortHandler.nextMonth();
	}

	/**
	 * Jumps to the previous month.
	 */
	public void prevMonth() {
		_viewPortHandler.prevMonth();
	}

	/**
	 * Jumps one week forward.
	 */
	public void nextWeek() {
		_viewPortHandler.nextWeek();
	}

	/**
	 * Jumps one week backwards.
	 */
	public void prevWeek() {
		_viewPortHandler.prevWeek();
	}

	/**
	 * Jumps to the next hour.
	 */
	public void nextHour() {
		_viewPortHandler.nextHour();
	}

	/**
	 * Jumps to the previous minute.
	 */
	public void prevMinute() {
		_viewPortHandler.prevMinute();
	}

	/**
	 * Jumps to the next hour.
	 */
	public void nextMinute() {
		_viewPortHandler.nextMinute();
	}

	/**
	 * Jumps to the previous hour.
	 */
	public void prevHour() {
		_viewPortHandler.prevHour();
	}

	/**
	 * Jumps one day forward.
	 */
	public void nextDay() {
		_viewPortHandler.nextDay();
	}

	/**
	 * Jumps one day backwards.
	 */
	public void prevDay() {
		_viewPortHandler.prevDay();
	}

	void setNoRecalc() {
		_recalcScopes = false;
		_recalcSecBounds = false;
	}

	private void internalAddEvent(final int index, final GanttEvent event) {
		if (!_ganttEvents.contains(event)) {
			if (index == -1) {
				_ganttEvents.add(event);
			} else {
				_ganttEvents.add(index, event);
			}
		}

		_allEventsCombined.add(event);

		flagForceFullUpdate();
	}

	private void internalRemoveEvent(final GanttEvent event) {
		_ganttEvents.remove(event);

		_allEventsCombined.remove(event);

		flagForceFullUpdate();
	}

	private void internalAddGroup(final int index, final GanttGroup group) {
		if (!_ganttGroups.contains(group)) {
			if (index == -1) {
				_ganttGroups.add(group);
			} else {
				_ganttGroups.add(index, group);
			}
		}

		_allEventsCombined.addAll(group.getEventMembers());

		flagForceFullUpdate();
	}

	private void internalRemoveGroup(final GanttGroup group) {
		_ganttGroups.remove(group);

		_allEventsCombined.removeAll(group.getEventMembers());

		flagForceFullUpdate();
	}

	private void internalAddSection(final int index, final GanttSection section) {
		if (!_ganttSections.contains(section)) {
			if (index == -1) {
				_ganttSections.add(section);
			} else {
				_ganttSections.add(index, section);
			}
			// if section is added also add the events that belong to that section
			for (final Iterator it = section.getEvents().iterator(); it.hasNext();) {
				internalAddEvent(-1, (GanttEvent) it.next());
			}
		}

		flagForceFullUpdate();
	}

	private void internalRemoveSection(final GanttSection section) {
		// if section is removed also remove the events that belong to that section
		for (final Iterator it = section.getEvents().iterator(); it.hasNext();) {
			final Object item = it.next();
			if (item instanceof GanttEvent) {
				internalRemoveEvent((GanttEvent) item);
			} else if (item instanceof GanttGroup) {
				internalRemoveGroup((GanttGroup) item);
			}
		}

		_ganttSections.remove(section);

		flagForceFullUpdate();
	}

	private void eventNumbersChanged() {
		_allEventsCombined.clear();

		for (int i = 0; i < _ganttEvents.size(); i++) {
			final Object obj = _ganttEvents.get(i);
			if (obj instanceof GanttEvent) {
				_allEventsCombined.add(obj);
			} else if (obj instanceof GanttGroup) {
				_allEventsCombined.addAll(((GanttGroup) obj).getEventMembers());
			}
		}
		for (int i = 0; i < _ganttGroups.size(); i++) {
			_allEventsCombined.addAll(_ganttGroups.get(i).getEventMembers());
		}

		flagForceFullUpdate();
	}

	// moves the x bounds of all events one day width left or right
	void moveXBounds(final boolean positive) {
		final Object[] objs = _allEventsCombined.toArray();

		int dw = getDayWidth();
		if (_currentView == ISettings.VIEW_YEAR) {
			final Calendar temp = Calendar.getInstance(_defaultLocale);
			temp.setTime(_mainCalendar.getTime());
			temp.add(Calendar.MONTH, positive ? 1 : -1);

			final long days = DateHelper.daysBetween(_mainCalendar, temp);
			dw = Math.abs((int) days * dw);
		}

		for (int i = 0; i < _allEventsCombined.size(); i++) {
			final GanttEvent ge = (GanttEvent) objs[i];
			ge.updateX(ge.getX() + (positive ? dw : -dw));
		}

		for (int i = 0; i < holidays.length; i++) {
			final Holiday holiday = holidays[i];
			if (holiday.getBounds() != null) {
				holiday.updateX(holiday.getBounds().x + (positive ? dw : -dw));
			}
		}
	}

	/*
	 * private void moveXBounds(int move) {
	 * Object[] objs = mAllEventsCombined.toArray();
	 *
	 * for (int i = 0; i < objs.length; i++) {
	 * GanttEvent ge = (GanttEvent) objs[i];
	 * ge.updateX(ge.getX() - move);
	 * }
	 *
	 * updateEventVisibilities(mVisibleBounds);
	 * }
	 */

	private void moveYBounds(final int move) {
		final Object[] objs = _allEventsCombined.toArray();

		for (int i = 0; i < objs.length; i++) {
			final GanttEvent ge = (GanttEvent) objs[i];
			ge.updateY(ge.getY() - move);
		}

		for (int i = 0; i < holidays.length; i++) {
			final Holiday holiday = holidays[i];
			if (holiday.getBounds() != null) {
				holiday.updateY(holiday.getBounds().y - move);
			}
		}

		_visibleBounds.y += move;

		updateEventVisibilities(_visibleBounds);
	}

	/**
	 * Redraws the calendar should some event not do it automatically.
	 */
	public void refresh() {
		checkWidget();
		redraw();
	}

	// redraws only the area where the events are, only call when dates aren't changing
	private void redrawEventsArea() {
		redraw();
	}

	/**
	 * Checks whether a certain event is visible in the current bounds.
	 *
	 * @param event GanttEvent
	 * @param bounds Bounds
	 * @return true if event is visible
	 */
	public boolean isEventVisible(final GanttEvent event, final Rectangle bounds) {
		return getEventVisibility(event, bounds) == Constants.EVENT_VISIBLE;
	}

	// checks whether an event is visible in the current date range that is
	// displayed on the screen
	private int getEventVisibility(final GanttEvent event, final Rectangle bounds) {
		// if we're saving the chart as an image, everything is visible unless it's truly hidden
		if (_savingChartImage && !event.isHidden()) {
			return Constants.EVENT_VISIBLE;
		}

		// fastest checks come first, if it's not a visible layer, it's not visible
		if (_hiddenLayers.contains(new Integer(event.getLayer()))) {
			return Constants.EVENT_NOT_VISIBLE;
		}

		// if event is missing dates, don't let it show, fix to #281983
		if (event.getActualStartDate() == null || event.getActualEndDate() == null) {
			return Constants.EVENT_NOT_VISIBLE;
		}

		// our second check is the check whether it's out of bounds vertically, if so we can return right away (and scope calculation
		// takes the special OOB_HEIGHT into account when counting the vertical offset

		// as we offset the entire view area when scrolling vertically by moving the events up or down vertically
		// we need to check the offset as if they were still in their original position, which we do by taking their y location
		// and adding on the vertical scroll position. Once we have those fake bounds, we simply check it against the visual area
		// and if they're not inside, they're out!
		final Rectangle fakeBounds = new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight());
		fakeBounds.y += _vScrollPos;

		// first draw everything is zero, ignore that one
		if (event.getY() != 0) {
			if (fakeBounds.y > _visibleBounds.y + _visibleBounds.height) {
				return Constants.EVENT_OOB_BOTTOM;
			}
			if (fakeBounds.y + fakeBounds.height < _visibleBounds.y) {
				return Constants.EVENT_OOB_TOP;
			}
		}

		Calendar sCal = null;
		Calendar eCal = null;

		// bugfix #304819 - If planned dates are showing, visibility needs to take them into account and not just the normal dates
		// thus, the earliest start and latest end matter instead as we're showing "everything".
		if (isShowingPlannedDates()) {
			sCal = event.getEarliestStartDate();
			eCal = event.getLatestEndDate();
		} else {
			sCal = event.getActualStartDate();
			eCal = event.getActualEndDate();
			if (sCal == null) {
				sCal = event.getRevisedStart();
			}
			if (eCal == null) {
				eCal = event.getRevisedEnd();
			}
		}

		// scope checking
		if (event.isScope()) {
			final GanttEvent earliest = event.getEarliestScopeEvent();
			final GanttEvent latest = event.getLatestScopeEvent();
			if (earliest != null) {
				sCal = earliest.getActualStartDate();
			}
			if (latest != null) {
				eCal = latest.getActualEndDate();
			}

			if (sCal == null || eCal == null) {
				return Constants.EVENT_NOT_VISIBLE;
			}
		}

		// if an event has movement constraints we draw a marker around it to display this fact, thus,
		// if that marker expands beyond the size of the event, we need to assume that the event visibility is actually
		// the size between the constraints and not just the event itself. If we were not to do this calculation
		// the boundary box would not be drawn when the event was not in visible range, which would be very odd to the user
		// as it would suddenly appear when the event became visible, but they could not view how far it expanded without zooming out.
		if (event.hasMovementConstraints()) {
			if (event.getNoMoveBeforeDate() != null && event.getNoMoveBeforeDate().before(sCal)) {
				sCal = event.getNoMoveBeforeDate();
			}
			if (event.getNoMoveAfterDate() != null && event.getNoMoveAfterDate().after(eCal)) {
				eCal = event.getNoMoveAfterDate();
			}
		}

		// if we don't have width, check using dates, this happens on the
		// initial draw and when events are outside of the picture
		if (event.getWidthWithText() == 0) {
			final Date eventStart = sCal.getTime();
			final Date eventEnd = eCal.getTime();
			final Calendar temp = Calendar.getInstance(_defaultLocale);
			temp.setTime(_mainCalendar.getTime());

			final long viewPortStart = temp.getTimeInMillis();
			if (_daysVisible == 0) {
				if (_endCalendar == null) {
					// May happen when the widget is about to be created and not visible
					if (_endCalendar==null) {
						return Constants.EVENT_NOT_VISIBLE;
					}
				}
				temp.setTime(_endCalendar.getTime());
			} else {
				temp.add(Calendar.DATE, _daysVisible);
			}

			final long viewPortEnd = temp.getTimeInMillis();

			// inside
			// if (eventStart.getTime() >= viewPortStart && eventStart.getTime() <= viewPortEnd) { return Constants.EVENT_VISIBLE; }

			// if (eventEnd.getTime() >= viewPortStart && eventEnd.getTime() <= viewPortEnd) { return Constants.EVENT_VISIBLE; }

			// event starts before calendar-end-time and ends after calendar-start-time (inside)
			if (eventStart.getTime() <= viewPortEnd && eventEnd.getTime() >= viewPortStart) {
				return Constants.EVENT_VISIBLE;
			}

			// event spans entire screen, also fix to Bugzilla bug #236846 - https://bugs.eclipse.org/bugs/show_bug.cgi?id=236846
			if (eventStart.getTime() <= viewPortStart && eventEnd.getTime() >= viewPortEnd) {
				return Constants.EVENT_VISIBLE;
			}

			// event starts in screen and ends outside screen
			if (eventStart.getTime() >= viewPortStart && eventStart.getTime() <= viewPortEnd && eventEnd.getTime() >= viewPortEnd) {
				return Constants.EVENT_VISIBLE;
			}

		} else {
			int xStart = getStartingXFor(sCal);
			int xEnd = getXForDate(eCal);

			final int buffer = _settings.getArrowHeadEventSpacer();
			xEnd += buffer;
			xStart -= buffer;

			// account for the actual text
			// TODO: we should account for other text locations
			if (event.getHorizontalTextLocation() == SWT.RIGHT) {
				xEnd += event.getNameExtent().x;
			}

			if (xEnd < 0) {
				return Constants.EVENT_OOB_LEFT;
			}

			if (xStart > bounds.width) {
				return Constants.EVENT_OOB_RIGHT;
			}

			// if the event was OOB before, height-wise, but no longer is, and we flagged it as bounds are not set
			// (which we do when the event will have moved due to zoom or other)
			// we need to force an update of the bounds. Since we need the xStart/xEnd anyway, we set them here as it's one method call less
			if ((event.getVisibility() == Constants.EVENT_OOB_TOP || event.getVisibility() == Constants.EVENT_OOB_BOTTOM) && !event.isBoundsSet()) {
				event.updateX(xStart);
				event.updateWidth(xEnd - xStart + getDayWidth());
			}

			return Constants.EVENT_VISIBLE;
		}

		return Constants.EVENT_NOT_VISIBLE;
	}

	// gets the x position for where the event bar should start
	private int getStartingXFor(final GanttEvent event) {
		if (_currentView == ISettings.VIEW_MINUTE || _currentView == ISettings.VIEW_DAY) {
			return getStartingXForEventHours(event);
		} else {
			return getStartingXFor(event.getActualStartDate());
		}
	}

	/**
	 * Returns the starting x position for a given date in the current view.
	 *
	 * @param date Date
	 * @return x position, -1 should it for some reason not be found
	 */
	public int getStartingXForEventDate(final Calendar date) {
		return getStartingXFor(date);
	}

	private int getStartingXForEventHours(final GanttEvent event) {
		return getStartingXForEventHours(event.getActualStartDate());
	}

	private int getStartingXForEventHours(final Calendar start) {
		final Calendar temp = Calendar.getInstance(_defaultLocale);
		temp.setTime(_mainCalendar.getTime());

		// some stuff we know, (to help program this)
		// 1 dayWidth is one working hour, thus, 1 dayWidth / 60 = 1 minute
		// 1 day is the same as the week width

		final int dw = getDayWidth();
		final int daysBetween = (int) DateHelper.daysBetween(temp, start);
		final int minsBetween = DateHelper.minutesBetween(temp.getTime(), start.getTime(), true, false);
		int ret = 0;
		if (_currentView == ISettings.VIEW_MINUTE) { // TodayLine for View_Minute
			ret = daysBetween * _minuteDayWidth;
			ret += _mainBounds.x;
			final float minutesBetween = DateHelper.minutesBetween(temp.getTime(), start.getTime(), true, false);
			final float secondsBetween = DateHelper.secondsBetween(temp.getTime(), start.getTime(), true, false);

			float minPixels = 0;

			final float remainderMins = secondsBetween - minutesBetween * 60;
			final float oneMinWidth = (float) dw / 60;
			minPixels = oneMinWidth * remainderMins;

			ret += minutesBetween * dw + minPixels;

			return ret;
		} else {
			// days is ok, now deal with hours
			ret = daysBetween * _weekWidth;
			ret += _mainBounds.x;
			final float hoursBetween = DateHelper.hoursBetween(temp, start, true);
			final float minutesBetween = DateHelper.minutesBetween(temp.getTime(), start.getTime(), true, false);

			float minPixels = 0;

			// now deal with minutes, if settings say so
			if (!_settings.roundHourlyEventsOffToNearestHour()) {
				final float remainderMins = minutesBetween - hoursBetween * 60;
				final float oneMinWidth = (float) dw / 60;
				minPixels = oneMinWidth * remainderMins;
			}

			ret += hoursBetween * dw + minPixels;
		}
		return ret;
	}

	/**
	 * Returns the starting x for a given date.
	 *
	 * @param date Calendar date
	 * @return x position or -1 if it for some reason should not be found
	 */
	public int getStartingXFor(final Calendar date) {
		checkWidget();

		if (_currentView == ISettings.VIEW_DAY) {
			return getStartingXForEventHours(date);
		}

		if (_currentView == ISettings.VIEW_MINUTE) {
			return getStartingXForEventHours(date);
		}

		if (date == null) {
			return _mainBounds.x;
		}

		final Calendar temp = DateHelper.getNewCalendar(_mainCalendar);

		if (_currentView == ISettings.VIEW_YEAR) {
			temp.set(Calendar.DAY_OF_MONTH, 1);
		}

		final long secondsBetween = DateHelper.secondsBetween(temp.getTime(), date.getTime(), false, false);
		final int dw = getDayWidth();
		final float pps = dw / (24f * 60f * 60f);
		return _mainBounds.x + (int) (secondsBetween * pps);
	}

	private int getXLengthForEventHours(final GanttEvent event) {
		return getXForDate(event.getActualEndDate()) - getXForDate(event.getActualStartDate());
	}

	// gets the x position for where the event bar should end
	private int getXLengthForEvent(final GanttEvent event) {
		if (_currentView == ISettings.VIEW_DAY || _currentView == ISettings.VIEW_MINUTE) {
			return getXLengthForEventHours(event);
		}


		final int secondsBetweenStartAndEnd = DateHelper.secondsBetween(event.getActualStartDate().getTime(), event.getActualEndDate().getTime(), false, false);

		final int dw = this.getDayWidth();
		final float pps = dw / (24f * 60f * 60f);

		final int result = (int) (secondsBetweenStartAndEnd * pps);
		
		// ensure there is never a negative value
		return result > 0 ? result : 1;
	}

	/**
	 * Returns the width of one day in the current zoom level. Do note that "one day" refers to 1 tick mark, so it might
	 * not be one day in a smaller or larger zoom level.
	 *
	 * @return One tick mark width
	 */
	public int getDayWidth() {
		int dw = _dayWidth;
		if (_currentView == ISettings.VIEW_MONTH) {
			dw = _monthDayWidth;
		} else if (_currentView == ISettings.VIEW_YEAR) {
			dw = _yearDayWidth;
		}
		if (_currentView == ISettings.VIEW_MINUTE) {
			dw = _dayWidth;
		}

		return dw;
	}

	// checks whether an x/y position is inside the given rectangle
	private boolean isInside(final int x, final int y, final Rectangle rect) {
		if (rect == null) {
			return false;
		}

		return x >= rect.x && y >= rect.y && x <= rect.x + rect.width && y <= rect.y + rect.height;
	}

	// open edit dialogs
	public void mouseDoubleClick(final MouseEvent me) {
		// if we only listen to selected events we won't catch locked or
		// otherwise disabled events, up to the user in the end, not us.
		killDialogs();

		// BUG FIX: If a dialog/shell is opened by the user on a double click event, the mouse incorrectly still thinks
		// it's in mouse-down mode, so we need to force it to not thinking that
		killMouseState();

		for (int i = 0; i < _ganttEvents.size(); i++) {
			final GanttEvent event = _ganttEvents.get(i);

			if (isInside(me.x, me.y, new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight()))) {
				for (int j = 0; j < _eventListeners.size(); j++) {
					_eventListeners.get(j).eventDoubleClicked(event, me);
				}

				return;
			}
		}
	}

	// removes the chart from thinking that the mouse button was never released
	private void killMouseState() {
		_mouseIsDown = false;
	}

	/**
	 * Returns a list of all selected header dates (Calendar).
	 *
	 * @return List of calendars or empty list if none
	 */
	public List getAllSelectedHeaderDates() {
		return _selHeaderDates;
	}

	/**
	 * Clears all selected headers and redraws the chart.
	 */
	public void clearAllSelectedHeaders() {
		_selHeaderDates.clear();
		redraw();
	}

	/**
	 * Sets a list of header dates that should be the selected dates. This list must be a list of Calendars.
	 *
	 * @param dates List of Calendar objects representing selected header dates.
	 */
	public void setSelectedHeaderDates(List dates) {
		_selHeaderDates = dates;
		redraw();
	}

	/**
	 * Selects a region of events.
	 *
	 * @param rect Rectangle of region to select
	 * @param me
	 */
	private void doMultiSelect(final Rectangle rect, final MouseEvent me) {
		if (rect == null) {
			return;
		}

		for (int i = 0; i < _ganttEvents.size(); i++) {
			final GanttEvent ge = _ganttEvents.get(i);
			if (ge.isScope()) {
				continue;
			}

			if (ge.getBounds().intersects(rect)) {
				_selectedEvents.add(ge);
			}
		}

		for (int x = 0; x < _eventListeners.size(); x++) {
			final IGanttEventListener listener = _eventListeners.get(x);
			listener.eventSelected(null, _selectedEvents, me);
		}

		redraw();
	}

	public void mouseDown(final MouseEvent me) {
		if (me.button == 1) {
			_mouseIsDown = true;
		}

		final Point ctrlPoint = toDisplay(new Point(me.x, me.y));

		if (_settings.allowBlankAreaDragAndDropToMoveDates() && me.button == 1) {
			setCursor(Constants.CURSOR_HAND);
		}

		// remove old dotted border, we used to just create a new GC and clear it, but to be honest, it was a bit of a hassle with some calculations
		// and left over cheese, and a redraw is just faster. It's rare enough anyway, and we don't redraw more than a small area
		if (!_selectedEvents.isEmpty() && _settings.drawSelectionMarkerAroundSelectedEvent()) {
			for (int x = 0; x < _selectedEvents.size(); x++) {
				final GanttEvent selEvent = (GanttEvent) _selectedEvents.get(x);
				redraw(selEvent.getX() - 2, selEvent.getY() - 2, selEvent.getWidth() + 4, selEvent.getHeight() + 4, false);
			}
		}

		// header clicks, if it's visible
		if (_settings.drawHeader() && _settings.allowHeaderSelection() && me.button == 1) {
			final Rectangle headerBounds = new Rectangle(_mainBounds.x, _settings.getHeaderMonthHeight() + _vScrollPos, _mainBounds.width, _settings.getHeaderDayHeight());

			if (isInside(me.x, me.y, headerBounds)) {
				// we account for section bar width
				final Calendar cal = getDateAt(me.x - _mainBounds.x);

				if (me.stateMask == SWT.MOD1 || me.stateMask == SWT.SHIFT || me.stateMask == (SWT.MOD1 | SWT.SHIFT)) {
					if (me.stateMask == SWT.MOD1) {
						if (_selHeaderDates.contains(cal)) {
							_selHeaderDates.remove(cal);
						} else {
							_selHeaderDates.add(cal);
						}
					} else {
						// shift or ctrl + shift
						if (!_selHeaderDates.isEmpty()) {
							final Calendar last = _selHeaderDates.get(_selHeaderDates.size() - 1);
							long days = 0;
							boolean reverse = false;
							if (last.before(cal)) {
								days = DateHelper.daysBetween(last, cal);
							} else {
								days = DateHelper.daysBetween(cal, last);
								reverse = true;
							}

							// all dates
							final Calendar temp = Calendar.getInstance(_defaultLocale);
							temp.setTime(last.getTime());
							for (int i = 0; i < (int) days; i++) {
								temp.add(Calendar.DATE, reverse ? -1 : 1);
								final Calendar temp2 = Calendar.getInstance(_defaultLocale);
								temp2.setTime(temp.getTime());
								_selHeaderDates.add(temp2);
							}
						}
					}
				} else {
					if (_selHeaderDates.contains(cal)) {
						_selHeaderDates.clear();
					} else {
						_selHeaderDates.clear();
						_selHeaderDates.add(cal);
					}
				}

				for (int i = 0; i < _eventListeners.size(); i++) {
					_eventListeners.get(i).eventHeaderSelected(cal, _selHeaderDates);
				}

				redraw();
				return;
			}

		}

		// deal with selection
		for (int i = 0; i < _ganttEvents.size(); i++) {
			final GanttEvent event = _ganttEvents.get(i);

			if (event.isScope() && !_settings.allowScopeMenu()) {
				continue;
			}

			if (isInside(me.x, me.y, new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight()))) {
				final GC gc = new GC(this);

				// if it's a scope and menu is allowed, we can finish right here
				if (me.button == 3 && event.isScope()) {
					showMenu(ctrlPoint.x, ctrlPoint.y, event, me);
					// dispose or we have a memory leak, as we're returning!
					gc.dispose();
					return;
				}

				if (me.stateMask == 0 || !_multiSelect) {
					_selectedEvents.clear();
				}

				// if we're multi selecting
				if (_multiSelect) {
					// ctrl select + deselect
					if (_selectedEvents.contains(event) && me.stateMask == SWT.MOD1) {
						_selectedEvents.remove(event);
					} else {
						_selectedEvents.add(event);
					}
				} else {
					_selectedEvents.add(event);
				}

				// fire selection changed
				if (_settings.fireEmptyEventSelection() || !_selectedEvents.isEmpty()) {
					for (int x = 0; x < _eventListeners.size(); x++) {
						final IGanttEventListener listener = _eventListeners.get(x);
						listener.eventSelected(event, _selectedEvents, me);
					}
				}

				for (int x = 0; x < _selectedEvents.size(); x++) {
					final GanttEvent selEvent = (GanttEvent) _selectedEvents.get(x);
					if (!selEvent.isCheckpoint() && !selEvent.isScope() && !selEvent.isImage() && _settings.drawSelectionMarkerAroundSelectedEvent()) {
						gc.setForeground(ColorCache.getWhite());
						drawSelectionAroundEvent(gc, event, event.getX(), event.getY(), event.getWidth(), _mainBounds);
					}
				}

				gc.dispose();

				if (_settings.showOnlyDependenciesForSelectedItems()) {
					redrawEventsArea();
				}

				// show menu on the last selected event if there are multiple
				if (me.button == 3 && !_selectedEvents.isEmpty()) {
					showMenu(ctrlPoint.x, ctrlPoint.y, (GanttEvent) _selectedEvents.get(_selectedEvents.size() - 1), me);
				}

				return;
			}
		}

		if (me.button == 3) {
			_selectedEvents.clear();
			showMenu(ctrlPoint.x, ctrlPoint.y, null, me);
			return;
		}

		// after selection, we only allow Trackers in blank areas, it's too much of a clash with other listeners and events otherwise
		if (_multiSelect && me.button == 1 && me.stateMask == SWT.MOD1) {
			setCursor(Constants.CURSOR_NONE);
			_tracker = new Tracker(this, SWT.RESIZE);
			_tracker.setCursor(Constants.CURSOR_NONE);
			_tracker.setStippled(true);
			_tracker.setRectangles(new Rectangle[] { new Rectangle(me.x, me.y, 1, 1) });
			// this blocks until the Tracker is disposed, so on success, we multiselect
			if (_tracker.open()) {
				doMultiSelect(_tracker.getRectangles()[0], me);
			}
			return;
		}

		if (me.stateMask == 0) {
			_selectedEvents.clear();
		}

		if (_settings.showOnlyDependenciesForSelectedItems()) {
			redrawEventsArea();
		}

	}

	private void drawSelectionAroundEvent(final GC gc, final GanttEvent ge, final int xPos, final int yPos, final int eventWidth, final Rectangle bounds) {
		if (!_settings.drawSelectionMarkerAroundSelectedEvent()) {
			return;
		}

		gc.setLineStyle(SWT.LINE_DOT);

		final int y = yPos;
		int x = xPos;

		// this is _extremely_ slow to draw, so we need to check bounds here, which is probably a good idea anyway
		final boolean oobLeft = x < bounds.x;
		final boolean oobRight = x + eventWidth > bounds.width;

		if (oobLeft || oobRight) {
			if (!oobLeft || !oobRight) {
				if (oobLeft) {
					x = bounds.x;

					// left side out of bounds
					gc.drawLine(x, y, x + eventWidth, y);
					gc.drawLine(x + eventWidth, y, x + eventWidth, y + ge.getHeight());
					gc.drawLine(x, y + ge.getHeight(), x + eventWidth, y + ge.getHeight());
				} else {
					// right side out of bounds
					gc.drawLine(x, y, bounds.width, y);
					gc.drawLine(x, y, x, y + ge.getHeight());
					gc.drawLine(x, y + ge.getHeight(), bounds.width, y + ge.getHeight());
				}
			} else {
				// double out of bounds
				gc.drawLine(bounds.x, y, bounds.x + bounds.width, y);
				gc.drawLine(bounds.x, y + ge.getHeight(), bounds.x + bounds.width, y + ge.getHeight());
			}
		} else {
			gc.drawRectangle(x, y, eventWidth, _eventHeight);
		}

		gc.setLineStyle(SWT.LINE_SOLID);
	}

	// whenever the mouse button is let up, we reset all drag and resize things,
	// as they only happen when the mouse
	// button is down and we resize or drag & drop
	public void mouseUp(final MouseEvent event) {
		_mouseIsDown = false;

		boolean needsRedraw = false;

		if (_tracker != null && !_tracker.isDisposed()) {
			_tracker.dispose();
		}

		// vertical dragging "end"
		if (_freeDragging) {
			// clear any temporary DND events held in the sections
			for (int i = 0; i < _ganttSections.size(); i++) {
				final GanttSection gs = _ganttSections.get(i);
				gs.clearDNDGanttEvents();
			}

			// dragging is done
			_freeDragging = false;
			_vDragDir = SWT.NONE;

			// we now need to [potentially] re-align the vertical event so it's not offset by some pixels
			// this deals with reordering events etc after a DND
			handlePostVerticalDragDrop();

			// done with it, clear it
			_vDNDManager.clear();
		}

		if (!_dragEvents.isEmpty()) {
			if (_resizing) {
				for (int i = 0; i < _eventListeners.size(); i++) {
					_eventListeners.get(i).eventsResizeFinished(_dragEvents, event);
				}
			}
			if (_dragging) {
				for (int i = 0; i < _eventListeners.size(); i++) {
					_eventListeners.get(i).eventsMoveFinished(_dragEvents, event);
				}
			}
			needsRedraw = true;
		}

		// put all undo/redo commands into one command as any user would expect a multi-DND to undo with all events, not just one at a time
		final ClusteredCommand cc = new ClusteredCommand();

		// as this check is slow-ish, we do this last. If a user resizes or drops an event on top of a range that does not allow events, we undo the drag/resize
		// NOTE: we also remove it from the undo queue as it was never moved/resized in the first place
		if (hasSpecialDateRanges()) {
			for (int i = 0; i < _specDateRanges.size(); i++) {
				final GanttSpecialDateRange range = _specDateRanges.get(i);

				final ArrayList failedMoves = new ArrayList();

				// check if any of the moved/resized events overlap any of our date ranges
				for (int x = 0; x < _dragEvents.size(); x++) {
					final GanttEvent ge = (GanttEvent) _dragEvents.get(x);

					// event is not allowed to be on these dates, undo
					if (!range.canEventOccupy(ge.getActualStartDate(), ge.getActualEndDate())) {
						ge.moveCancelled();
						_dragEvents.remove(ge);
						needsRedraw = true;
						failedMoves.add(ge);
					}
				}

				if (!failedMoves.isEmpty()) {
					// notify listeners that some events didn't make it...
					for (int j = 0; j < _eventListeners.size(); j++) {
						_eventListeners.get(j).eventsDroppedOrResizedOntoUnallowedDateRange(failedMoves, range);
					}
				}
			}
		}

		// undo/redo handling
		for (int i = 0; i < _dragEvents.size(); i++) {
			final GanttEvent ge = (GanttEvent) _dragEvents.get(i);
			ge.moveFinished();
			// the event knows if it's resized or moved and will return the correct event accordingly
			final IUndoRedoCommand undoCommand = ge.getPostMoveOrResizeUndoCommand();
			cc.addCommand(undoCommand);
		}

		// sort the commands by index before to keep the stair effect
		Collections.sort(cc.getCommandList(), new Comparator<IUndoRedoCommand>() {

			public int compare(IUndoRedoCommand o1, IUndoRedoCommand o2) {
				if (o1 instanceof EventMoveCommand && o2 instanceof EventMoveCommand) {
					final int thisVal = ((EventMoveCommand) o1).getIndexBefore();
					final int anotherVal = ((EventMoveCommand) o2).getIndexBefore();
					return thisVal < anotherVal ? -1 : thisVal == anotherVal ? 0 : 1;
				}
				return 0;
			}
		});

		if (cc.size() > 0) {
			_undoRedoManager.record(cc);
		}

		if (_dragPhase != null) {
			boolean notifyListeners = true;

			// if we don't allow overlaps, we need to check if we're about to drag or resize onto
			// an existing phase
			if (!_settings.allowPhaseOverlap()) {
				// check if we overlap
				for (int i = 0; i < _ganttPhases.size(); i++) {
					final GanttPhase gp = _ganttPhases.get(i);
					if (gp.equals(_dragPhase)) {
						continue;
					}

					// if we overlap, undo the last DND
					if (gp.getBounds().intersects(_dragPhase.getBounds())) {
						_dragPhase.undoLastDragDrop();
						notifyListeners = false;
					}
				}
			}

			if (notifyListeners) {
				if (_resizing) {
					for (int i = 0; i < _eventListeners.size(); i++) {
						_eventListeners.get(i).phaseResizeFinished(_dragPhase, event);
					}
				}
				if (_dragging) {
					for (int i = 0; i < _eventListeners.size(); i++) {
						_eventListeners.get(i).phaseMoveFinished(_dragPhase, event);
					}
				}
			} else {
				needsRedraw = true;
			}
		}

		if (sectionDetailMoreIcons != null) {
			for (final Iterator it = sectionDetailMoreIcons.entrySet().iterator(); it.hasNext();) {
				final Map.Entry entry = (Map.Entry) it.next();
				if (isInside(event.x, event.y, (Rectangle) entry.getValue())) {
					for (final Iterator listenerIt = sectionDetailMoreClickListener.iterator(); listenerIt.hasNext();) {
						final ISectionDetailMoreClickListener listener = (ISectionDetailMoreClickListener) listenerIt.next();
						listener.openAdvancedDetails((GanttSection) entry.getKey());
					}
				}
			}
		}

		if (needsRedraw) {
			redraw();
		}

		endEverything();
		updateHorizontalScrollbar();
	}

	/**
	 * This method deals with moving events around so that vertical gaps are filled where they left holes after a
	 * vertical drag/drop
	 */
	private void handlePostVerticalDragDrop() {
		if (_dragEvents.isEmpty()) {
			redraw();
			return;
		}

		// sort the events descending dependent on their start date
		// this is necessary to keep the stair effect correctly on drag and drop
		Collections.sort(_dragEvents, new Comparator<GanttEvent>() {
			public int compare(GanttEvent o1, GanttEvent o2) {
				final Calendar compareDate1 = o1.getRevisedStart() != null ? o1.getRevisedStart() : o1.getStartDate();
				final Calendar compareDate2 = o2.getRevisedStart() != null ? o2.getRevisedStart() : o2.getStartDate();
				return compareDate2.compareTo(compareDate1);
			}
		});

		final int vDragMode = _settings.getVerticalEventDragging();

		final GanttEvent top = _vDNDManager.getTopEvent();
		final GanttEvent bottom = _vDNDManager.getBottomEvent();
		GanttSection targetSection = _vDNDManager.getTargetSection();

		// only allow DND across sections?
		final List ignoreDrag = new ArrayList();
		if (vDragMode == VerticalDragModes.CROSS_SECTION_VERTICAL_DRAG) {
			for (int i = 0; i < _dragEvents.size(); i++) {
				final GanttEvent ge = (GanttEvent) _dragEvents.get(i);
				if (ge.getGanttSection() != null && targetSection != null && ge.getGanttSection() == targetSection) {
					ge.undoVerticalDragging();
					ignoreDrag.add(ge);
				}
			}
		}

		if (top == null && bottom == null) {
			// it'll be alone in a section
			if (targetSection != null) {
				for (int i = 0; i < _dragEvents.size(); i++) {
					final GanttEvent ge = (GanttEvent) _dragEvents.get(i);
					final GanttSection fromSection = ge.getGanttSection();

					ge.reparentToNewGanttSection(0, targetSection);

					for (int x = 0; x < _eventListeners.size(); x++) {
						_eventListeners.get(x).eventMovedToNewSection(ge, fromSection, targetSection);
					}
				}
			}
		} else {
			// if we come here, do further processing
			// first remove the dragged events, so we don't have to deal with a changing
			// list order when adding them again
			for (int i = 0; i < _dragEvents.size(); i++) {
				final GanttEvent ge = (GanttEvent) _dragEvents.get(i);
				final GanttSection fromSection = ge.getGanttSection();

				if (hasGanttSections()) {
					fromSection.removeGanttEvent(ge);
				} else {
					_ganttEvents.remove(ge);
				}
			}

			// calculate the insertion index for the event in the drag events that was dragged
			// on multi select, only the event that was dragged has information about the drag information
			// the others should rely on that index instead of calculating it over and over again
			GanttEvent event = null;
			for (final GanttEvent ev : (List<GanttEvent>) _dragEvents) {
				if (ev.getPreVerticalDragBounds() != null) {
					event = ev;
				}
			}

			if (event == null) {
				// this should never happen, but who knows if there is still some bugs around
				event = (GanttEvent) _dragEvents.get(0);
			}

			final GanttSection fromSection = event.getGanttSection();
			int index = 0;

			if (hasGanttSections()) {
				// List fromEvents = fromSection.getEvents();
				if (targetSection == null) {
					// TODO: This seems to happen when user drops event on border between two, uncomment line below and do a drop on border, see if we can handle differently
					targetSection = fromSection;
				}
				final List toEvents = targetSection.getEvents();
				if (top != null) {
					index = toEvents.indexOf(top);
					index++;
				}

			} else {
				if (top != null) {
					index = _ganttEvents.indexOf(top);
				}
				if (event.wasVerticallyMovedUp()) {
					index++;
				}
			}

			// loop all dragged events
			for (int i = 0; i < _dragEvents.size(); i++) {
				final GanttEvent ge = (GanttEvent) _dragEvents.get(i);

				// if the GanttEvent was moved to a new section, remember the origin section for later use of the listeners
				// needed because the event should be fired at the end not at the beginning of the move
				final GanttSection originSection = ge.getGanttSection() != targetSection && hasGanttSections() ? ge.getGanttSection() : null;

				// event was moved nowhere vertically, same place
				if (ge.getPreVerticalDragBounds() != null) {
					if (ge.getY() == ge.getPreVerticalDragBounds().y) {
						continue;
					}
				}

				if (hasGanttSections()) {
					targetSection.addGanttEvent(index, ge);

				} else {
					_ganttEvents.add(index, ge);
				}

				// notify listeners
				if (originSection != null) {
					for (int x = 0; x < _eventListeners.size(); x++) {
						_eventListeners.get(x).eventMovedToNewSection(ge, originSection, targetSection);
					}
				} else {
					for (int x = 0; x < _eventListeners.size(); x++) {
						_eventListeners.get(x).eventReordered(ge);
					}
				}
			}
		}

		// forces vertical re-calculation
		heavyRedraw();
	}

	/**
	 * Returns a section that corresponds to where the given event is currently hovering over
	 *
	 * @param event Event to check
	 * @param accountForVerticalDragDirection whether to account for if user is dragging up or down when returning
	 *            section, if user is between two sections this may play a part
	 * @return Section it is over or null if none
	 */
	private GanttSection getSectionForVerticalDND(final GanttEvent event, final boolean accountForVerticalDragDirection) {
		if (!hasGanttSections()) {
			return null;
		}

		for (int i = 0; i < _ganttSections.size(); i++) {
			final GanttSection gs = _ganttSections.get(i);

			// must account for scroll position as the event itself has no clue there's a scrollbar and obviously doesn't account for it
			if (gs.getBounds().contains(event.getX(), event.getY() + _vScrollPos)) {
				return gs;
			}
		}

		// if we get here, we had a null hit, which means we might be between two sections, make it easy, check if we're oob first
		if (event.getY() < getHeaderHeight()) {
			// we could return null but that's not a good idea, rather, lets tell the target it's still in the same section
			return event.getGanttSection();
		}

		// ok, we're not oob at the top, so we're probably between two, easy check
		for (int i = 0; i < _ganttSections.size(); i++) {
			final GanttSection gs = _ganttSections.get(i);
			GanttSection next = null;
			if (i != _ganttSections.size() - 1) {
				next = _ganttSections.get(i + 1);

				if (next != null) {
					if (event.getY() >= gs.getBounds().y + gs.getBounds().height && event.getY() <= next.getBounds().y) {
						if (accountForVerticalDragDirection) {
							if (_vDragDir == SWT.UP) {
								return gs;
							} else {
								return next;
							}
						} else {
							return gs;
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * Returns a section that corresponds to where the given MouseEvent is fired
	 *
	 * @param event MouseEvent to check
	 * @return Section it is over or null if none
	 */
	public GanttSection getSectionAt(final MouseEvent me) {
		if (!hasGanttSections()) {
			return null;
		}

		for (int i = 0; i < _ganttSections.size(); i++) {
			final GanttSection gs = _ganttSections.get(i);

			// must account for scroll position as the event itself has no clue there's a scrollbar and obviously doesn't account for it
			if (gs.getBounds().contains(me.x, me.y + _vScrollPos)) {
				return gs;
			}
		}

		// if we get here, we had a null hit, which means we might be between two sections, make it easy, check if we're oob first
		if (me.y < getHeaderHeight()) {
			// we could return null but that's not a good idea, rather, lets tell the target it's in the first section
			return _ganttSections.get(0);
		}

		// ok, we're not oob at the top, so we're probably between two, easy check
		for (int i = 0; i < _ganttSections.size(); i++) {
			final GanttSection gs = _ganttSections.get(i);
			GanttSection next = null;
			if (i != _ganttSections.size() - 1) {
				next = _ganttSections.get(i + 1);

				if (next != null) {
					if (me.y >= gs.getBounds().y + gs.getBounds().height && me.y <= next.getBounds().y) {
						return gs;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Returns a list of all surrounding vertical events to a given event. If a GanttSection is given, only the events
	 * in that section will be used in calculating.
	 *
	 * @param event Event to get surrounding events for
	 * @param section GanttSection optional (pass null for using all events)
	 * @return 2-size array with entry 0 being the top event and entry 1 being the bottom event. Any or all can be null.
	 */
	private List getSurroundingVerticalEvents(final GanttEvent event, final GanttSection section) {

		final List<?> allEvents = section == null ? _ganttEvents : section.getEvents();

		final List<GanttEvent> ret = new ArrayList<>();

		// no events
		if (allEvents.isEmpty()) {
			ret.add(null);
			ret.add(null);
			return ret;
		}

		final List<IGanttChartItem> sorted = new ArrayList<>();
		for (int i = 0; i < allEvents.size(); i++) {
			final IGanttChartItem ge = (IGanttChartItem) allEvents.get(i);
			if (ge instanceof GanttEvent) {
				if (((GanttEvent) ge).isHidden()) {
					continue;
				}

				if (_selectedEvents.contains(ge)) {
					continue;
				}

				sorted.add(ge);
			}
		}

		Collections.sort(sorted, new Comparator<IGanttChartItem>() {

			public int compare(final IGanttChartItem one, final IGanttChartItem two) {
				final GanttEvent ge1 = (GanttEvent) one;
				final GanttEvent ge2 = (GanttEvent) two;

				final Integer i1 = new Integer(ge1.getY());
				final Integer i2 = new Integer(ge2.getY());

				return i1.compareTo(i2);
			}

		});

		GanttEvent nearestUp = null;
		GanttEvent nearestDown = null;

		for (int i = 0; i < sorted.size(); i++) {
			final GanttEvent cur = (GanttEvent) sorted.get(i);
			GanttEvent next = null;
			if (i < sorted.size() - 1) {
				next = (GanttEvent) sorted.get(i + 1);
			}

			if (event.getY() < cur.getY() && (next == null || next != null && next.getY() > event.getY())) {
				if (nearestDown == null) {
					nearestDown = cur;
				} else {
					// if it's closer to us than the last one we registered, use it, otherwise ignore it
					if (cur.getY() < nearestDown.getY()) {
						nearestDown = cur;
					}
				}

				int x = i;
				while (true) {
					if (x < 0) {
						break;
					}
					final GanttEvent ge = (GanttEvent) sorted.get(x);
					if (ge.getY() < nearestDown.getY() && !ge.equals(event)) {
						nearestUp = ge;
						break;
					}
					x--;
				}
			}

			if (nearestUp != null && nearestDown != null) {
				break;
			}
		}

		GanttEvent first = null;
		if (!sorted.isEmpty()) {
			first = (GanttEvent) sorted.get(0);
			if (first.equals(event) && sorted.size() > 1) {
				first = (GanttEvent) sorted.get(1);
			}

			final int topMostY = first.getY();
			final int botMostY = ((GanttEvent) sorted.get(sorted.size() - 1)).getY();

			if (event.getY() < topMostY) {
				if (section == null) {
					nearestUp = null;
				} else {
					nearestUp = null;
					nearestDown = (GanttEvent) allEvents.get(0);
				}
			}
			if (event.getY() + event.getHeight() > botMostY) {
				if (section == null) {
					nearestDown = null;
				} else {
					nearestUp = (GanttEvent) allEvents.get(allEvents.size() - 1);
					nearestDown = null;
				}
			}

		}
		ret.add(nearestUp);
		ret.add(nearestDown);

		return ret;
	}

	public void keyPressed(final KeyEvent e) {
		// an attempt at ending everything
		if (e.keyCode == SWT.ESC) {

			final List<GanttEvent> scopeEventsToUpdate = new ArrayList<>();

			// if we were in the middle of a drag or resize, we cancel the move and reset the dates back to before the move started
			if (!_dragEvents.isEmpty()) {
				for (int i = 0; i < _dragEvents.size(); i++) {
					final GanttEvent ge = (GanttEvent) _dragEvents.get(i);
					ge.moveCancelled();
					if (ge.getScopeParent() != null && !scopeEventsToUpdate.contains(ge.getScopeParent())) {
						scopeEventsToUpdate.add(ge.getScopeParent());
					}
				}
			}

			// update any scope events that we found during the cancel or they won't redraw in their right spots
			for (int i = 0; i < scopeEventsToUpdate.size(); i++) {
				updateScopeXY((GanttEvent) scopeEventsToUpdate.get(i));
			}

			_dragPhase = null;
			_dragEvents.clear();
			endEverything();
			_selectedEvents.clear();
			killDialogs();
			if (_freeDragging) {
				heavyRedraw();
			} else {
				redraw();
			}
			setCursor(Constants.CURSOR_NONE);
		}

		if (_settings.allowArrowKeysToScrollChart()) {
			if (e.keyCode == SWT.ARROW_LEFT) {
				if (_currentView == ISettings.VIEW_YEAR) {
					prevMonth();
				} else {
					if (_currentView == ISettings.VIEW_DAY) {
						prevHour();
					} else if (_currentView == ISettings.VIEW_MINUTE) {
						prevMinute();
					} else {
						prevDay();
					}
				}
			} else if (e.keyCode == SWT.ARROW_RIGHT) {
				if (_currentView == ISettings.VIEW_YEAR) {
					nextMonth();
				} else {
					if (_currentView == ISettings.VIEW_DAY) {
						nextHour();
					} else if (_currentView == ISettings.VIEW_MINUTE) {
						nextMinute();
					} else {
						nextDay();
					}
				}
			} else if (e.keyCode == SWT.ARROW_UP) {
				_vScrollBar.setSelection(_vScrollBar.getSelection() - 10);
				vScroll(null);
			} else if (e.keyCode == SWT.ARROW_DOWN) {
				_vScrollBar.setSelection(_vScrollBar.getSelection() + 10);
				vScroll(null);
			}
		}
	}

	public void keyReleased(final KeyEvent e) {
		checkWidget();
		endEverything();
	}

	private void endEverything() {
		if (_tracker != null && !_tracker.isDisposed()) {
			_tracker.dispose();
			_mouseIsDown = false;
		}

		// oh, not quite yet
		if (_mouseIsDown) {
			return;
		}

		_initialHoursDragOffset = 0;
		_justStartedMoveOrResize = false;

		_mouseIsDown = false;
		_mouseDragStartLocation = null;
		_dragStartDate = null;

		// kill auto scroll
		endAutoScroll();

		_showZoomHelper = false;
		if (_zoomLevelArea != null) {
			redraw(_zoomLevelArea.x, _zoomLevelArea.y, _zoomLevelArea.width + 1, _zoomLevelArea.height + 1, false);
		}

		_dragging = false;
		_dragStartLoc = null;
		_dragEvents.clear();
		_dragPhase = null;
		_resizing = false;
		setCursor(Constants.CURSOR_NONE);
		_lastX = 0;
		_cursor = SWT.NONE;
		killDialogs();
	}

	public void mouseMove(final MouseEvent me) {
		try {
			// kill dialogs if no mouse button is held down etc, otherwise we just move the dialog with updated text if it's a tooltip etc
			// and pre-killing will cause flicker
			if (me.stateMask == 0) {
				killDialogs();
			}

			// resize and move disabled for VIEW_MINUTE, left for a future enhancement
			if (_currentView == ISettings.VIEW_MINUTE || _tracker != null && !_tracker.isDisposed()) {
				if (_mouseIsDown && _settings.allowBlankAreaDragAndDropToMoveDates()) {
					final String dateFormat = ((ISettings2) _settings).getMinuteDateFormat();
					mouseMoveMinutes(dateFormat, me);
				}

				return;
			}

			final String dateFormat = _currentView == ISettings.VIEW_MINUTE || _currentView == ISettings.VIEW_DAY ? _settings.getHourDateFormat() : _settings.getDateFormat();

			// if we moved mouse back in from out of bounds, kill auto scroll
			final Rectangle area = super.getClientArea();
			if (me.x >= area.x && me.x < area.width) {
				endAutoScroll();
			}

			final boolean dndOK = _settings.enableDragAndDrop();
			final boolean resizeOK = _settings.enableResizing();

			// check if we need to auto-scroll
			if ((_dragging || _resizing) && _settings.enableAutoScroll()) {
				if (me.x < 0 || me.x > _mainBounds.width) {
					doAutoScroll(me);
				}
				if (_freeDragging && (me.y > _mainBounds.height || me.y < 0)) {
					doAutoScroll(me);
				}
			}

			// handle cross-section DND's, events need to be temporarily added to whatever
			// section they are being dragged over or they will not be rendered.
			if (_freeDragging && _dragging && !_ganttSections.isEmpty() && !_dragEvents.isEmpty()) {

				for (final Object dragObj : _dragEvents) {
					final GanttEvent drag = (GanttEvent) _dragEvents.get(0);
					for (int i = 0; i < _ganttSections.size(); i++) {
						final GanttSection gs = _ganttSections.get(i);
						if (drag.getGanttSection() == gs) {
							continue;
						}

						if (gs.getBounds().intersects(drag.getBounds())) {
							gs.addDNDGanttEvent(drag);
						} else {
							// clear any old section events
							gs.clearDNDGanttEvents();
						}
					}
				}
			}

			// phase move/resize (check first as it's faster than looping events)
			if (_dragPhase != null && (_resizing || _dragging)) {
				AdvancedTooltipDialog.kill();
				GanttToolTip.kill();
				if (_dragging && dndOK) {
					handlePhaseMove(me, _dragPhase, Constants.TYPE_MOVE, true);
				}
				if (_resizing && resizeOK && (_cursor == SWT.CURSOR_SIZEE || _cursor == SWT.CURSOR_SIZEW)) {
					handlePhaseMove(me, _dragPhase, _lastLeft ? Constants.TYPE_RESIZE_LEFT : Constants.TYPE_RESIZE_RIGHT, true);
				}
				return;
			}

			// move
			if (_dragging && dndOK) {
				AdvancedTooltipDialog.kill();
				GanttToolTip.kill();
				for (int x = 0; x < _selectedEvents.size(); x++) {
					handleMove(me, (GanttEvent) _selectedEvents.get(x), Constants.TYPE_MOVE, x == 0);
				}
				return;
			}

			// resize
			if (_resizing && resizeOK && (_cursor == SWT.CURSOR_SIZEE || _cursor == SWT.CURSOR_SIZEW)) {
				for (int x = 0; x < _selectedEvents.size(); x++) {
					handleMove(me, (GanttEvent) _selectedEvents.get(x), _lastLeft ? Constants.TYPE_RESIZE_LEFT : Constants.TYPE_RESIZE_RIGHT, x == 0);
				}
				return;
			}

			// when we move a mouse really fast at the beginning of a move or a
			// drag, we need to catch that seperately
			// as "isInside()" will not have a chance to see if it's true or not
			// (below)
			if (!_resizing && !_dragging && dndOK && resizeOK && me.stateMask != 0 && _cursor != SWT.NONE) {
				if (!_selectedEvents.isEmpty()) {
					if (_cursor == SWT.CURSOR_SIZEE || _cursor == SWT.CURSOR_SIZEW) {
						// handleResize(me, mSelectedEvent, mCursor == SWT.CURSOR_SIZEW);
						for (int x = 0; x < _selectedEvents.size(); x++) {
							handleMove(me, (GanttEvent) _selectedEvents.get(x), _cursor == SWT.CURSOR_SIZEW ? Constants.TYPE_RESIZE_LEFT : Constants.TYPE_RESIZE_RIGHT, x == 0);
						}
						return;
					} else if (_cursor == SWT.CURSOR_SIZEALL) {
						for (int x = 0; x < _selectedEvents.size(); x++) {
							handleMove(me, (GanttEvent) _selectedEvents.get(x), Constants.TYPE_MOVE, x == 0);
						}
						return;
					}
				}
			}

			if (me.stateMask == 0) {
				setCursor(Constants.CURSOR_NONE);
			}

			boolean insideAnyEvent = false;

			final int x = me.x;
			final int y = me.y;

			if (_mainBounds == null || me.x >= _mainBounds.x) {
				// check if cursor is inside the area of an event
				for (int i = 0; i < _ganttEvents.size(); i++) {
					final GanttEvent event = _ganttEvents.get(i);
					if (isInside(me.x, me.y, new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight()))) {
						insideAnyEvent = true;

						if (event.isScope()) {
							continue;
						}

						if (_hiddenLayers.contains(event.getLayerInt())) {
							continue;
						}

						final Rectangle rect = new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight());

						boolean onRightBorder = false;
						boolean onLeftBorder = false;

						// n pixels left or right of either border = show resize
						// mouse cursor
						if (x >= rect.x + rect.width - _settings.getResizeBorderSensitivity() && y >= rect.y && x <= rect.x + rect.width + _settings.getResizeBorderSensitivity() && y <= rect.y + rect.height) {
							onRightBorder = true;
						} else if (x >= rect.x - _settings.getResizeBorderSensitivity() && y >= rect.y && x <= rect.x + _settings.getResizeBorderSensitivity() && y <= rect.y + rect.height) {
							onLeftBorder = true;
						}

						// right border resize cursor (not images!)
						if (me.stateMask == 0 || me.stateMask == _settings.getDragAllModifierKey()) {
							if (resizeOK && event.isResizable()) {
								if (event.isCheckpoint() && _settings.allowCheckpointResizing() || !event.isCheckpoint() && !event.isImage()) {
									if (onRightBorder) {
										setCursor(Constants.CURSOR_SIZEE);
										_cursor = SWT.CURSOR_SIZEE;
										return;
									} else if (onLeftBorder) {
										setCursor(Constants.CURSOR_SIZEW);
										_cursor = SWT.CURSOR_SIZEW;
										return;
									}
								}
							}

							// move cursor
							if (dndOK && event.isMoveable()) {
								setCursor(Constants.CURSOR_SIZEALL);
								_cursor = SWT.CURSOR_SIZEALL;
								return;
							}
						} else {
							if ((dndOK || event.isMoveable()) && _cursor == SWT.CURSOR_SIZEALL && isInMoveArea(event, me.x)) {
								handleMove(me, event, Constants.TYPE_MOVE, true);
								return;
							}

							if (!event.isCheckpoint() && (resizeOK || event.isResizable()) && (_cursor == SWT.CURSOR_SIZEE || _cursor == SWT.CURSOR_SIZEW)) {
								// handleResize(me, event, onLeftBorder);
								handleMove(me, event, onLeftBorder ? Constants.TYPE_RESIZE_LEFT : Constants.TYPE_RESIZE_RIGHT, true);
								return;
							}
						}
						break;
					}
				}
			}

			if (hasGanttPhases()) {
				for (int i = 0; i < _ganttPhases.size(); i++) {
					final GanttPhase phase = _ganttPhases.get(i);

					// not visible, don't bother
					if (phase.isHidden() || !phase.isDisplayable()) {
						continue;
					}

					// uncalculated, bad
					if (phase.getHeaderBounds() == null || phase.getBounds() == null) {
						continue;
					}

					// don't bother with locked events or events that can't be adjusted
					if (!phase.isResizable() && !phase.isMoveable() || phase.isLocked()) {
						continue;
					}

					if (isInside(me.x, me.y, phase.getHeaderBounds())) {
						boolean onRightBorder = false;
						boolean onLeftBorder = false;

						final Rectangle rect = phase.getHeaderBounds();

						// n pixels left or right of either border = show resize
						// mouse cursor
						if (x >= rect.x + rect.width - _settings.getResizeBorderSensitivity() && y >= rect.y && x <= rect.x + rect.width + _settings.getResizeBorderSensitivity() && y <= rect.y + rect.height) {
							onRightBorder = true;
						} else if (x >= rect.x - _settings.getResizeBorderSensitivity() && y >= rect.y && x <= rect.x + _settings.getResizeBorderSensitivity() && y <= rect.y + rect.height) {
							onLeftBorder = true;
						}

						if (me.stateMask == 0) {
							if (onRightBorder) {
								setCursor(Constants.CURSOR_SIZEE);
								_cursor = SWT.CURSOR_SIZEE;
								return;
							} else if (onLeftBorder) {
								setCursor(Constants.CURSOR_SIZEW);
								_cursor = SWT.CURSOR_SIZEW;
								return;
							} else {
								if (phase.isMoveable()) {
									setCursor(Constants.CURSOR_SIZEALL);
									_cursor = SWT.CURSOR_SIZEALL;
									return;
								}
							}
						} else {
							if (onLeftBorder || onRightBorder) {
								handlePhaseMove(me, phase, onLeftBorder ? Constants.TYPE_RESIZE_LEFT : Constants.TYPE_RESIZE_RIGHT, true);
								return;
							} else {
								handlePhaseMove(me, phase, Constants.TYPE_MOVE, true);
								return;
							}
						}
					}
				}
			}

			boolean isInsideSectionDetailMore = false;
			if (sectionDetailMoreIcons != null) {
				for (final Iterator<Rectangle> it = sectionDetailMoreIcons.values().iterator(); it.hasNext();) {
					final Rectangle rect = (Rectangle) it.next();
					if (isInside(me.x, me.y, rect)) {
						isInsideSectionDetailMore = true;
						setCursor(Constants.CURSOR_HAND);
						_cursor = SWT.CURSOR_HAND;
					}
				}
			}

			// clear mouse cursor if need be
			if (!insideAnyEvent && !isInsideSectionDetailMore && !_dragging && !_resizing && _cursor != SWT.NONE) {
				setCursor(Constants.CURSOR_NONE);
				_cursor = SWT.NONE;
			}

			// blank area drag and drop
			if (_mouseIsDown && _settings.allowBlankAreaDragAndDropToMoveDates()) {
				// blank area drag
				if (_mouseDragStartLocation == null) {
					_mouseDragStartLocation = new Point(me.x, me.y);
				}

				int xDiff = me.x - _mouseDragStartLocation.x;
				int yDiff = me.y - _mouseDragStartLocation.y;

				boolean left = xDiff < 0;

				if (_settings.flipBlankAreaDragDirection()) {
					left = !left;
				}

				xDiff /= getDayWidth();
				xDiff = Math.abs(xDiff);

				if (xDiff > 0 && ISettings.VIEW_YEAR != _currentView) {
					_mouseDragStartLocation = new Point(me.x, me.y);
				}

				if (_settings.allowBlankAreaVerticalDragAndDropToMoveChart()) {
					_mouseDragStartLocation = new Point(me.x, me.y);
				}

				// do vertical drag first, it's the easiest
				if (yDiff != 0 && _settings.allowBlankAreaVerticalDragAndDropToMoveChart()) {
					yDiff *= 2; // otherwise it's a really small scroll
					// double speed if shift is held down
					if ((me.stateMask & SWT.SHIFT) != 0) {
						yDiff *= 2;
					}
					if (_settings.flipBlankAreaDragDirection()) {
						_vScrollBar.setSelection(_vScrollBar.getSelection() + -yDiff);
					} else {
						_vScrollBar.setSelection(_vScrollBar.getSelection() + yDiff);
					}
					vScroll(null);
				}

				// if it's year view, we wait until the diff matches the number of dates of the month to move
				if (ISettings.VIEW_YEAR == _currentView && xDiff < _mainCalendar.getActualMaximum(Calendar.DATE)) {
					return;
				}
				// and again if it's year view, and we get here, we do the actual flip
				else if (ISettings.VIEW_YEAR == _currentView) {
					_mouseDragStartLocation = new Point(me.x, me.y);

					// fast drags get multiple moves..
					// TODO: just do prevMonth(times) instead of looping, less redraw
					int times = xDiff / _mainCalendar.getActualMaximum(Calendar.DATE);
					if (times == 0) {
						times = 1;
					}

					for (int i = 0; i < times; i++) {
						if (left) {
							prevMonth();
						} else {
							nextMonth();
						}
					}
					return;
				}

				for (int i = 0; i < xDiff; i++) {
					if (left) {
						if (_currentView == ISettings.VIEW_DAY) {
							prevHour();
						} else if (_currentView == ISettings.VIEW_MINUTE) {
							prevMinute();
						} else {
							prevDay();
						}
					} else {
						if (_currentView == ISettings.VIEW_DAY) {
							nextHour();
						} else if (_currentView == ISettings.VIEW_MINUTE) {
							nextMinute();
						} else {
							nextDay();
						}
					}
				}

				updateHorizontalScrollbar();

				// mMouseDragStartLocation = new Point(me.x, me.y);

				// when using fast redraw, it can happen that a really fast drag will not be redrawn correctly due to us flagging it to off
				// and the next/prev day items don't catch that it's a new month. So, if the diff is big here, that means we're dragging and dropping pretty fast
				// and we simply force a redraw. The number is just a number, but 7 seems about right, and that's tested on a 1920x resolution at full screen.
				if (_currentView == ISettings.VIEW_YEAR && xDiff > 7) {
					_recalcScopes = true;
					_recalcSecBounds = true;
					redraw();
				}

				final Point loc = new Point(_mouseDragStartLocation.x + 10, _mouseDragStartLocation.y - 20);
				// don't show individual dates if we're on yearly view, they're
				// correct, it's just that
				// we move horizontally month-wise, so it doesn't quite make
				// sense.
				if (_zoomLevel >= ISettings.ZOOM_YEAR_MAX) {
					final Calendar temp = Calendar.getInstance(_defaultLocale);
					temp.setTime(_mainCalendar.getTime());
					temp.set(Calendar.DAY_OF_MONTH, 1);
					GanttDateTip.makeDialog(_colorManager, DateHelper.getDate(temp, dateFormat), toDisplay(loc), _mainBounds.y);
				} else {
					if (_currentView == ISettings.VIEW_D_DAY) {
						GanttDateTip.makeDialog(_colorManager, getCurrentDDate() + "        ", toDisplay(loc), _mainBounds.y);
					} else {
						GanttDateTip.makeDialog(_colorManager, DateHelper.getDate(_mainCalendar, dateFormat), toDisplay(loc), _mainBounds.y);
					}
				}

			}
		} catch (final Exception error) {
			SWT.error(SWT.ERROR_UNSPECIFIED, error);
		}
	}

	private void mouseMoveMinutes(String dateFormat, MouseEvent me) {
		// Date Scrolling + DateTip on VIEW_MINUTE

		if (_mouseDragStartLocation == null) {
			_mouseDragStartLocation = new Point(me.x, me.y);
		}

		int xDiff = me.x - _mouseDragStartLocation.x;
		final int yDiff = me.y - _mouseDragStartLocation.y;

		boolean left = xDiff < 0;
		final boolean up = yDiff < 0;

		if (_settings.flipBlankAreaDragDirection()) {
			left = !left;
		}

		xDiff /= getDayWidth();
		xDiff = Math.abs(xDiff);

		for (int i = 0; i < xDiff; i++) {
			if (left) {
				if (_currentView == ISettings.VIEW_MINUTE) {
					prevMinute();
				}
			} else {
				if (_currentView == ISettings.VIEW_MINUTE) {
					nextMinute();
				}
			}
		}

		updateHorizontalScrollbar();

		final Point loc = new Point(_mouseDragStartLocation.x + 10, _mouseDragStartLocation.y - 20);
		GanttDateTip.makeDialog(_colorManager, DateHelper.getDate(_mainCalendar, dateFormat), toDisplay(loc), _mainBounds.y);

	}

	public int getDaysVisible() {
		return _daysVisible;
	}

	private String getCurrentDDate() {
		final int days = (int) DateHelper.daysBetween(_dDayCalendar, _mainCalendar);
		if (days > 0) {
			return "+" + days;
		}

		final StringBuffer buf = new StringBuffer();
		buf.append(days);

		return buf.toString();
	}

	private boolean isInMoveArea(final GanttEvent event, final int x) {
		final Rectangle bounds = new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight());
		return x > bounds.x + _moveAreaInsets && x < bounds.x + bounds.width - _moveAreaInsets;
	}

	/**
	 * Deals with figuring out what direction we are auto-scrolling in, if any.
	 *
	 * @param event MouseEvent
	 */
	private void doAutoScroll(final MouseEvent event) {
		if (event.x < _mainBounds.x) {
			doAutoScroll(Constants.DIRECTION_LEFT);
		} else if (event.x > _mainBounds.width) {
			doAutoScroll(Constants.DIRECTION_RIGHT);
		} else {
			if (_freeDragging) {
				if (event.y < 0) {
					doAutoScroll(Constants.DIRECTION_UP);
				} else if (event.y > _mainBounds.height) {
					doAutoScroll(Constants.DIRECTION_DOWN);
				} else {
					endAutoScroll();
				}
			} else {
				endAutoScroll();
			}
		}
	}

	private void doAutoScroll(final int direction) {
		Runnable timer = null;

		// If we're already autoscrolling in the given direction do nothing
		if (_autoScrollDir == direction) {
			return;
		}

		final Display display = getDisplay();

		if (direction == Constants.DIRECTION_LEFT) {
			timer = new Runnable() {
				public void run() {
					if (_autoScrollDir == Constants.DIRECTION_LEFT) {
						if (_currentView == ISettings.VIEW_DAY) {
							prevHour();
						} else if (_currentView == ISettings.VIEW_MINUTE) {
							prevMinute();
						} else {
							prevDay();
						}

						display.timerExec(Constants.TIMER_INTERVAL, this);
					}
				}
			};
		} else if (direction == Constants.DIRECTION_RIGHT) {
			timer = new Runnable() {
				public void run() {
					if (_autoScrollDir == Constants.DIRECTION_RIGHT) {
						if (_currentView == ISettings.VIEW_DAY) {
							nextHour();
						} else if (_currentView == ISettings.VIEW_MINUTE) {
							nextMinute();
						} else {
							nextDay();
						}

						display.timerExec(Constants.TIMER_INTERVAL, this);
					}
				}
			};
		} else if (direction == Constants.DIRECTION_DOWN) {
			timer = new Runnable() {
				public void run() {
					if (_autoScrollDir == Constants.DIRECTION_DOWN) {
						_vScrollBar.setSelection(_vScrollBar.getSelection() + 10);
						vScroll(null);
						display.timerExec(Constants.TIMER_INTERVAL, this);
					}
				}
			};
		} else if (direction == Constants.DIRECTION_UP) {
			timer = new Runnable() {
				public void run() {
					if (_autoScrollDir == Constants.DIRECTION_UP) {
						_vScrollBar.setSelection(_vScrollBar.getSelection() - 10);
						vScroll(null);
						display.timerExec(Constants.TIMER_INTERVAL, this);
					}
				}
			};
		}

		if (timer != null) {
			_autoScrollDir = direction;
			display.timerExec(Constants.TIMER_INTERVAL, timer);
		}
	}

	boolean isChartReady() {
		return _mainBounds != null;
	}

	private void endAutoScroll() {
		_autoScrollDir = SWT.NULL;
	}

	/**
	 * Gets the X for a given date.
	 *
	 * @param date Date
	 * @return x position or -1 if date was not found
	 */
	public int getXForDate(final Date date) {
		checkWidget();
		final Calendar cal = Calendar.getInstance(_defaultLocale);
		cal.setTime(date);

		return getXForDate(cal);
	}

	/**
	 * Gets the x position where the given date starts in the current visible area.
	 *
	 * @param cal Calendar
	 * @return -1 if date was not found
	 */
	public int getXForDate(final Calendar cal) {
		checkWidget();

		if (_currentView == ISettings.VIEW_MINUTE || _currentView == ISettings.VIEW_DAY) {
			return getStartingXForEventHours(cal);
		}

		// clone the "root" calendar (our leftmost date)
		final Calendar temp = DateHelper.getNewCalendar(_mainCalendar);

		final int dw = getDayWidth();
		if (_currentView == ISettings.VIEW_YEAR) {
			// we draw years starting on the left for simplicity's sake
			temp.set(Calendar.DAY_OF_MONTH, 1);
		}

		final long days = DateHelper.daysBetween(temp, cal);

		int extra = 0;
		if (_drawToMinute && (_currentView != ISettings.VIEW_DAY || _currentView != ISettings.VIEW_MINUTE)) {
			extra = calculateMinuteAdjustment(cal);
		}

		// return mBounds.x + ((int) days * dw) + extra;
		// -- Emil: This was old, why we append mBounds.x is beyond me, it's wrong as the bounds.x starting
		// position has nothing to do with the actual dates, we always calcualate from the calendar date
		// regardless of where the bounds start, as long as the start calendar represents what is visible
		// the extra buffering is NOT needed.
		return (int) days * dw + extra;
	}

	private int calculateMinuteAdjustment(Calendar date) {
		final float ppm = getDayWidth() / (60f * 24f);

		final int mins = date.get(Calendar.HOUR_OF_DAY) * 60 + date.get(Calendar.MINUTE);

		return (int) (mins * ppm);
	}

	/**
	 * Gets the date for a given x position.
	 *
	 * @param x x location
	 * @return Calendar of date
	 */
	public Calendar getDateAt(final int x) {

		int xPosition = x;

		checkWidget();
		final Calendar temp = DateHelper.getNewCalendar(_mainCalendar);

		final int dw = getDayWidth();
		if (_currentView == ISettings.VIEW_YEAR) {
			temp.set(Calendar.DAY_OF_MONTH, 1);
		} else if (_currentView == ISettings.VIEW_MINUTE || _currentView == ISettings.VIEW_DAY) {

			// pixels per minute
			final float ppm = 60f / dw;

			// total minutes from left side
			xPosition -= _mainBounds.x;
			final int totalMinutes = (int) (xPosition * ppm);

			// set our temporary end calendar to the start date of the calendar
			final Calendar fakeEnd = Calendar.getInstance(_defaultLocale);
			fakeEnd.setTime(temp.getTime());

			for (int i = 0; i < totalMinutes; i++) {
				fakeEnd.add(Calendar.MINUTE, 1);
				if (temp.get(Calendar.HOUR_OF_DAY) >= 24) {
					temp.add(Calendar.DATE, 1);
					temp.set(Calendar.HOUR_OF_DAY, 0);
				}
			}

			return fakeEnd;
		}

		int xx = 0;

		if (xPosition < 0) {
			// loop from right to left
			while (true) {
				if (xx != 0) {
					temp.add(Calendar.DATE, -1);
				}

				// after adding, as it's a diff direction and we round "up" (which is left)
				if (xx < xPosition) {
					break;
				}

				xx -= dw;
			}
		} else {
			// loop from left to right
			while (true) {
				if (xx > xPosition) {
					break;
				}

				if (xx != 0) {
					temp.add(Calendar.DATE, 1);
				}

				xx += dw;
			}

		}

		// System.err.println(x + " " + xx);

		return temp;
	}

	private boolean isNoOverlap(final Calendar dat1, final Calendar dat2) {
		if (_currentView == ISettings.VIEW_MINUTE || _currentView == ISettings.VIEW_DAY) {
			return DateHelper.minutesBetween(dat1.getTime(), dat2.getTime(), false, false) >= 0;
		} else {
			return DateHelper.daysBetween(dat1, dat2) >= 0;
		}
	}

	/**
	 * Draws a dotted vertical marker at the given date. It will get removed on repaint, so make sure it's drawn as
	 * often as needed.
	 *
	 * @param date Date to draw it at
	 */
	public void drawMarker(final Date date) {
		checkWidget();
		final Calendar cal = Calendar.getInstance(_defaultLocale);
		cal.setTime(date);

		final int x = getXForDate(cal);

		if (x == -1) {
			return;
		}

		final GC gc = new GC(this);
		gc.setLineStyle(SWT.LINE_DOT);
		gc.drawRectangle(x, 0, x, _mainBounds.height);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.dispose();
	}

	private void handlePhaseMove(final MouseEvent me, final GanttPhase phase, final int type, final boolean showToolTip) {
		final String dateFormat = _currentView == ISettings.VIEW_MINUTE || _currentView == ISettings.VIEW_DAY ? _settings.getHourDateFormat() : _settings.getDateFormat();

		final Calendar mouseDateCal = getDateAt(me.x);

		// TODO: Code-reuse from normal event move, merge the two

		// drag start event, show cross hair arrow cursor and keep going
		if ((me.stateMask & SWT.BUTTON1) != 0 && !_dragging && !_resizing) {
			if (type == Constants.TYPE_MOVE) {
				_dragging = true;
				_resizing = false;
				setCursor(Constants.CURSOR_SIZEALL);
			} else {
				_dragging = false;
				_resizing = true;
				setCursor(type == Constants.TYPE_RESIZE_LEFT ? Constants.CURSOR_SIZEW : Constants.CURSOR_SIZEE);
				_lastLeft = type == Constants.TYPE_RESIZE_LEFT;
			}

			// clear normal drag events, this is different
			_dragEvents.clear();
			_dragPhase = phase;
			// mark it so we can undo it if need be
			_dragPhase.markDragStart();
			_lastX = me.x;
			_dragStartDate = mouseDateCal;
			_justStartedMoveOrResize = true;
		}

		// if we're dragging, with left mouse held down..
		if ((me.stateMask & SWT.BUTTON1) != 0 && (_dragging || _resizing) && me.x > _lastX || me.x < _lastX) {
			int diff = 0;
			if (_currentView == ISettings.VIEW_MINUTE || _currentView == ISettings.VIEW_DAY) {
				Calendar cal = _dragPhase.getStartDate();
				if (_resizing && !_lastLeft) {
					cal = _dragPhase.getEndDate();
				}

				diff = DateHelper.minutesBetween(mouseDateCal.getTime(), cal.getTime(), false, false);

				if (_justStartedMoveOrResize) {
					if (_dragging) {
						_initialHoursDragOffset = diff;
					}

					_justStartedMoveOrResize = false;
					return;
				} else {
					if (_dragging) {
						diff -= _initialHoursDragOffset;
					}
				}

				_justStartedMoveOrResize = false;

				// I need to figure out this weirdness at some point
				diff = -diff;
			} else {
				if (me.x > _lastX) {
					diff = (int) DateHelper.daysBetween(_dragStartDate, mouseDateCal);
				} else {
					diff = (int) DateHelper.daysBetween(mouseDateCal, _dragStartDate);
				}

				if (me.x < _lastX) {
					diff = -diff;
				}
			}

			_dragStartDate = mouseDateCal;

			int calMark = Calendar.DATE;

			if (_currentView == ISettings.VIEW_MINUTE || _currentView == ISettings.VIEW_DAY) {
				calMark = Calendar.MINUTE;
			}

			// move phase
			if (diff != 0) {
				// System.err.println("MOVE! " + mDragPhase.getBounds() + " " + mGanttPhases.size());
				boolean ok = true;

				if (type == Constants.TYPE_MOVE) {
					_dragPhase.move(calMark, diff);
				} else {
					if (!_settings.allowPhaseOverlap()) {
						for (int i = 0; i < _ganttPhases.size(); i++) {
							final GanttPhase other = _ganttPhases.get(i);
							if (other.equals(_dragPhase)) {
								continue;
							}

							if (type == Constants.TYPE_RESIZE_LEFT && diff < 0 || type == Constants.TYPE_RESIZE_RIGHT && diff > 0) {
								if (_dragPhase.willOverlapResize(other, calMark, diff, type == Constants.TYPE_RESIZE_LEFT)) {
									ok = false;
									break;
								}
							}
						}
					}

					if (ok) {
						if (type == Constants.TYPE_RESIZE_LEFT) {
							_dragPhase.moveStart(calMark, diff);
						} else if (type == Constants.TYPE_RESIZE_RIGHT) {
							_dragPhase.moveEnd(calMark, diff);
						}
					}
				}

				// redraw to show update
				redraw();

				if (ok) {
					// fire listeners
					for (int x = 0; x < _eventListeners.size(); x++) {
						final IGanttEventListener listener = _eventListeners.get(x);
						if (type == Constants.TYPE_MOVE) {
							listener.phaseMoved(_dragPhase, me);
						} else {
							listener.phaseResized(_dragPhase, me);
						}
					}
				}

				if (_settings.showDateTips() && (_dragging || _resizing) && showToolTip) {
					final Rectangle eBounds = _dragPhase.getBounds();
					final Point eventOnDisplay = toDisplay(me.x, eBounds.y);

					if (_currentView == ISettings.VIEW_D_DAY) {
						final StringBuffer buf = new StringBuffer();
						buf.append(_dragPhase.getDDayStart());
						buf.append(Constants.STR_DASH);
						buf.append(_dragPhase.getDDayEnd());
						buf.append("       ");
						GanttDateTip.makeDialog(_colorManager, buf.toString(), eventOnDisplay, _mainBounds.y);
					} else {
						final StringBuffer buf = new StringBuffer();
						buf.append(DateHelper.getDate(_dragPhase.getStartDate(), dateFormat));
						buf.append(Constants.STR_DASH);
						buf.append(DateHelper.getDate(_dragPhase.getEndDate(), dateFormat));
						GanttDateTip.makeDialog(_colorManager, buf.toString(), eventOnDisplay, _mainBounds.y);
					}
				}
			}
		}
	}

	/**
	 * Calculates where a vertically DND'd event will end up if dropped
	 */
	private void calculateVerticalInsertLocations() {
		_vDNDManager.clear();

		if (!_freeDragging) {
			return;
		}

		if (_dragEvents.isEmpty()) {
			return;
		}

		// first drag event
		final GanttEvent ge = (GanttEvent) _dragEvents.get(0);

		final GanttSection dragOverSection = getSectionForVerticalDND(ge, true);

		// System.err.println(dragOverSection);

		// get surrounding events of that event
		final List surrounding = getSurroundingVerticalEvents(ge, dragOverSection);

		final GanttEvent top = (GanttEvent) surrounding.get(0);
		final GanttEvent bottom = (GanttEvent) surrounding.get(1);

		_vDNDManager.setTopEvent(top);
		_vDNDManager.setBottomEvent(bottom);
		_vDNDManager.setTargetSection(dragOverSection);
		_vDNDManager.setSurroundingEvents(surrounding);
	}

	private void drawVerticalInsertMarkers(GC gc) {
		if (!_freeDragging) {
			return;
		}

		if (_dragEvents.isEmpty()) {
			return;
		}

		gc.setForeground(_colorManager.getOriginalLocationColor());
		gc.setLineWidth(1);
		gc.setAdvanced(false);

		// first drag event
		final GanttEvent ge = (GanttEvent) _dragEvents.get(0);

		final GanttEvent top = _vDNDManager.getTopEvent();// (GanttEvent) surrounding.get(0);
		final GanttEvent bottom = _vDNDManager.getBottomEvent();// (GanttEvent) surrounding.get(1);

		// System.err.println("Top: " + top + ", Bottom: " + bottom);//, Over section " + dragOverSection);

		int xDrawStart = 0;
		int xDrawEnd = _mainBounds.width;
		if (hasGanttSections() && _settings.getSectionSide() == SWT.LEFT) {
			int sectionWidth = 0;
			if (_settings.drawSectionBar()) {
				sectionWidth += _settings.getSectionBarWidth();
			}
			if (_settings.drawSectionDetails()) {
				sectionWidth += _settings.getSectionDetailWidth();
			}
			xDrawStart = sectionWidth;
			xDrawEnd = _mainBounds.width + xDrawStart;
		}

		// check if floating event is over a gantt section, if it's in a new section, assume draw line always appears in that section

		if (ge.hasMovedVertically()) {
			if (top != null) {
				if (bottom != null) {
					final int midDiff = (bottom.getY() - top.getBottomY()) / 2;
					gc.drawLine(xDrawStart, top.getBottomY() + midDiff, xDrawEnd, top.getBottomY() + midDiff);
				} else {
					// draw line at bottom
					gc.drawLine(xDrawStart, top.getBottomY() + 5, xDrawEnd, top.getBottomY() + 5);
				}
			} else {
				if (bottom == null) {
					gc.drawLine(xDrawStart, _mainBounds.height - 5, xDrawEnd, _mainBounds.height - 5);
					return;
				} else {
					// since there is no top, draw at the top of the bottom event
					gc.drawLine(xDrawStart, bottom.getY() - 5, xDrawEnd, bottom.getY() - 5);
				}
			}
		}

		gc.setLineStyle(SWT.LINE_DASH);
		for (int i = 0; i < _dragEvents.size(); i++) {
			final GanttEvent e = (GanttEvent) _dragEvents.get(i);
			if (e.getPreVerticalDragBounds() != null) {
				gc.drawRectangle(e.getPreVerticalDragBounds());
			}
		}
		gc.setLineStyle(SWT.LINE_SOLID);
	}

	/**
	 * Handles the actual moving of an event.
	 */
	private void handleMove(MouseEvent me, GanttEvent event, int type, boolean showToolTip) {
		if (event == null) {
			return;
		}

		if (event.isLocked()) {
			return;
		}

		if (!_settings.enableDragAndDrop() && type == Constants.TYPE_MOVE) {
			return;
		}

		if (!_settings.enableResizing() && (type == Constants.TYPE_RESIZE_LEFT || type == Constants.TYPE_RESIZE_RIGHT)) {
			return;
		}

		if (!_mouseIsDown) {
			return;
		}

		if (type == Constants.TYPE_MOVE && !event.isMoveable()) {
			return;
		}

		if ((type == Constants.TYPE_RESIZE_LEFT || type == Constants.TYPE_RESIZE_RIGHT) && !event.isResizable()) {
			return;
		}

		final String dateFormat = _currentView == ISettings.VIEW_MINUTE || _currentView == ISettings.VIEW_DAY ? _settings.getHourDateFormat() : _settings.getDateFormat();

		final Calendar mouseDateCal = getDateAt(me.x);

		// drag start event, show cross hair arrow cursor and keep going
		if ((me.stateMask & SWT.BUTTON1) != 0 && !_dragging && !_resizing) {
			if (type == Constants.TYPE_MOVE) {
				_dragging = true;
				_resizing = false;
				// save the location where we started the drag, can be used during the drag for calculating
				// deltas for where we are now vs. where we started
				_dragStartLoc = new Point(me.x, me.y);
				setCursor(Constants.CURSOR_SIZEALL);
			} else {
				_dragging = false;
				_resizing = true;
				setCursor(type == Constants.TYPE_RESIZE_LEFT ? Constants.CURSOR_SIZEW : Constants.CURSOR_SIZEE);
				_lastLeft = type == Constants.TYPE_RESIZE_LEFT;
			}

			_dragEvents.clear();

			if (_dragging) {
				event.flagDragging();
			}

			_dragEvents.add(event);
			_lastX = me.x;
			_lastY = me.y;
			_dragStartDate = mouseDateCal;
			_justStartedMoveOrResize = true;
		}

		// vertical drag
		if (_settings.getVerticalEventDragging() != VerticalDragModes.NO_VERTICAL_DRAG && _dragStartLoc != null) {
			final int diff = _dragStartLoc.y - me.y;
			if (Math.abs(diff) > _settings.getVerticalDragResistance()) {
				if (!_freeDragging) {
					_freeDragging = true;
				}
				event.updateY(me.y);
			} else {
				// this makes the event "snap" back to where it was before as we undo the vertical Y
				// if it's within range of our original position
				if (_freeDragging) {
					event.undoVerticalDragging();
					redraw();
				}
			}

			// check what vertical direction the user is dragging the event in
			if (me.y > _lastY) {
				_vDragDir = SWT.DOWN;
			} else if (me.y < _lastY) {
				_vDragDir = SWT.UP;
			}
		}

		// if we're dragging, with left mouse held down..
		if ((me.stateMask & SWT.BUTTON1) != 0 && (_dragging || _resizing)) {

			if (_freeDragging) {
				// vertical insert markers will be drawin in the main redraw handler
				redraw();
			}

			// There's some math here to calculate how far between the drag
			// start date and the current mouse x position date we are at.
			// -
			// We move the event the amount of days of difference there is between those two.
			// This way we get 2 wanted results:
			// 1. The position where we grabbed the event will remain the same
			// throughout the move
			// 2. The mouse cursor will move with the event and not skip ahead
			// or behind
			// it's important we set the dragStartDate to the current mouse x
			// date once we're done with it, as well as set the last
			// x position to where the mouse was last

			// left or right drag
			if (me.x > _lastX || me.x < _lastX) {
				int diff = 0;
				if (_currentView == ISettings.VIEW_MINUTE || _currentView == ISettings.VIEW_DAY) {
					Calendar cal = event.getActualStartDate();
					if (_resizing && !_lastLeft) {
						cal = event.getActualEndDate();
					}

					diff = DateHelper.minutesBetween(mouseDateCal.getTime(), cal.getTime(), false, false);

					// There is some confusing math here that should be explained.
					// -
					// When the user starts dragging, we pick the distance from the event start time to the mouse pointer and set that as our
					// offset. This offset will be used to move the event regardless of where the mouse was pushed down on the event and
					// makes the event look likes it sticks to the cursor no matter how fast it's moved.
					// For every subsequent move event (in the same move, meaning, the mouse hasn't been let go), we use that offset
					// as out modifier for the normal "diff" value (which is how far we moved since the last move-event).
					// When the mouse is let go, we reset everything so that it's good to go for the next move.
					// We only do this for hours as they move by the minute.
					// The only "side effect" this has is that in the most zoomed out hours view there may not be enough
					// pixels for a per-minute drag, so each step will be two minutes. The "solution" is for the user to zoom in.
					// There's no fix for this anyway as we can't split pixels (and if we did, I think even Einstein would be proud).
					if (_justStartedMoveOrResize) {
						if (_dragging) {
							_initialHoursDragOffset = diff;
						}

						_justStartedMoveOrResize = false;
						return;
					} else {
						if (_dragging) {
							diff -= _initialHoursDragOffset;
						}
					}

					_justStartedMoveOrResize = false;

					// I need to figure out this weirdness at some point
					diff = -diff;
				} else {
					if (me.x > _lastX) {
						diff = (int) DateHelper.daysBetween(_dragStartDate, mouseDateCal);
					} else {
						diff = (int) DateHelper.daysBetween(mouseDateCal, _dragStartDate);
					}

					if (me.x < _lastX) {
						diff = -diff;
					}
				}

				_dragStartDate = mouseDateCal;

				moveEvent(event, diff, me.stateMask, me, type);
			}
		}

		// if the event is part of a scope, force the parent to recalculate it's size etc, thus we don't have to recalculate everything
		if (event.getScopeParent() != null) {
			updateScopeXY(event.getScopeParent());
			// above isn't enough, also tell it to recalculate when redrawing
			_recalcScopes = true;
		}

		// set new last x position to where mouse is now
		_lastX = me.x;
		_lastY = me.y;

		// TODO: On multi-drag events, one tooltip is a bit lame, can we do something cooler/nicer/more useful? (20 tooltips != useful, and it's slow)
		if (_settings.showDateTips() && (_dragging || _resizing) && showToolTip) {
			final Rectangle eBounds = new Rectangle(event.getX(), event.getY() - 25, event.getWidth(), event.getHeight());
			final Point eventOnDisplay = toDisplay(me.x, eBounds.y);
			if (event.isCheckpoint()) {
				long days = DateHelper.daysBetween(event.getActualStartDate(), event.getActualEndDate());
				days++;

				if (days == 1) {
					GanttDateTip.makeDialog(_colorManager, DateHelper.getDate(event.getActualStartDate(), dateFormat), eventOnDisplay, _mainBounds.y);
				} else {
					final StringBuffer buf = new StringBuffer();
					if (_currentView == ISettings.VIEW_D_DAY) {
						buf.append(event.getActualDDayStart());
						buf.append(Constants.STR_DASH);
						buf.append(event.getActualDDayEnd());
						GanttDateTip.makeDialog(_colorManager, buf.toString(), eventOnDisplay, _mainBounds.y);
					} else {
						buf.append(DateHelper.getDate(event.getActualStartDate(), dateFormat));
						buf.append(Constants.STR_DASH);
						buf.append(DateHelper.getDate(event.getActualEndDate(), dateFormat));
						GanttDateTip.makeDialog(_colorManager, buf.toString(), eventOnDisplay, _mainBounds.y);
					}
				}
			} else {
				if (_currentView == ISettings.VIEW_D_DAY) {
					final StringBuffer buf = new StringBuffer();
					buf.append(event.getActualDDayStart());
					buf.append(Constants.STR_DASH);
					buf.append(event.getActualDDayEnd());
					buf.append("       ");
					GanttDateTip.makeDialog(_colorManager, buf.toString(), eventOnDisplay, _mainBounds.y);
				} else {
					final StringBuffer buf = new StringBuffer();
					buf.append(DateHelper.getDate(event.getActualStartDate(), dateFormat));
					buf.append(Constants.STR_DASH);
					buf.append(DateHelper.getDate(event.getActualEndDate(), dateFormat));
					GanttDateTip.makeDialog(_colorManager, buf.toString(), eventOnDisplay, _mainBounds.y);
				}
			}
		}
	}

	// updates a scope's x/y position (literal scope event)
	private void updateScopeXY(GanttEvent ge) {
		// if the event is part of a scope, force the parent to recalculate it's size etc, thus we don't have to recalculate everything
		if (ge != null) {
			// System.err.println(ge.getParentScopeChain());

			ge.calculateScope();
			final int newStartX = getXForDate(ge.getEarliestScopeEvent().getActualStartDate());
			final int newWidth = getXLengthForEvent(ge);
			ge.updateX(newStartX);
			ge.updateWidth(newWidth);
		}

		/*
		 * for (int i = 0; i < _ganttEvents.size(); i++) {
		 * GanttEvent e = (GanttEvent) _ganttEvents.get(i);
		 * if (e.getScopeEvents().size() > 0)
		 * e.calculateScope();
		 * }
		 * redraw();
		 */
	}

	// moves one event on the canvas. If SHIFT is held and linked events is true,
	// it moves all events linked to the moved event.
	private void moveEvent(GanttEvent ge, int diff, int stateMask, MouseEvent me, int type) {

		// diff only applies to horizontal drags, if user is dragging vertically we can't say "return" here, we must assume it's being moved
		if (diff == 0 && !_freeDragging) {
			return;
		}

		if (ge.isLocked()) {
			return;
		}

		final ArrayList eventsMoved = new ArrayList();

		int calMark = Calendar.DATE;

		if (_currentView == ISettings.VIEW_DAY) {
			calMark = Calendar.MINUTE;
		}

		if (_currentView == ISettings.VIEW_MINUTE) {
			calMark = Calendar.MILLISECOND;
		}

		final List toMove = new ArrayList();

		// multi move
		if ((stateMask & _settings.getDragAllModifierKey()) != 0 && _settings.moveLinkedEventsWhenEventsAreMoved() || _settings.alwaysDragAllEvents()) {
			final List conns = getEventsDependingOn(ge);

			final List translated = new ArrayList();
			for (int x = 0; x < conns.size(); x++) {
				final GanttEvent md = (GanttEvent) conns.get(x);

				if (md.isLocked()) {
					continue;
				}

				// it's a checkpoint, we're resizing, and settings say checkpoints can't be resized, then we skip them even here
				if (md.isCheckpoint() && type != Constants.TYPE_MOVE && !_settings.allowCheckpointResizing()) {
					continue;
				}

				translated.add(md);
			}

			// add all multiselected events too, if any
			if (_multiSelect) {
				for (int x = 0; x < _selectedEvents.size(); x++) {
					final GanttEvent selEvent = (GanttEvent) _selectedEvents.get(x);
					if (selEvent.isScope()) {
						continue;
					}

					if (!translated.contains(selEvent)) {
						translated.add(selEvent);
					}
				}
			}

			toMove.addAll(translated);
		}

		if (!toMove.contains(ge)) {
			toMove.add(ge);
		}

		// check if we only move events that are "later" than the source drag event
		if (_settings.moveAndResizeOnlyDependentEventsThatAreLaterThanLinkedMoveEvent()) {
			final List toRemove = new ArrayList();
			for (int x = 0; x < toMove.size(); x++) {
				final GanttEvent moveEvent = (GanttEvent) toMove.get(x);
				// skip ourselves
				if (moveEvent.equals(ge)) {
					continue;
				}

				// earlier? then it's a no go
				if (moveEvent.getActualEndDate().getTimeInMillis() < ge.getActualEndDate().getTimeInMillis()) {
					toRemove.add(moveEvent);
				}
			}

			// remove all events that need removing
			toMove.removeAll(toRemove);
		}

		_dragEvents = new ArrayList(toMove);

		for (int x = 0; x < toMove.size(); x++) {
			final GanttEvent event = (GanttEvent) toMove.get(x);

			final Calendar cal1 = Calendar.getInstance(_defaultLocale);
			final Calendar cal2 = Calendar.getInstance(_defaultLocale);
			cal1.setTime(event.getActualStartDate().getTime());
			cal2.setTime(event.getActualEndDate().getTime());

			if (type == Constants.TYPE_MOVE) {
				// flag first or dates won't be cloned before they changed
				event.moveStarted(type);
				cal1.add(calMark, diff);
				cal2.add(calMark, diff);

				// check before date move as it's the start date
				if (event.getNoMoveBeforeDate() != null) {
					// for start dates we need to actually set the date or we'll be 1 day short as we did the diff already
					// if we didn't have this code, a fast move would never move the event to the end date as it would be beyond already
					if (cal1.before(event.getNoMoveBeforeDate())) {

						final long millis = event.getNoMoveBeforeDate().getTimeInMillis();
						final long milliDiff = Math.abs(event.getActualStartDate().getTimeInMillis() - millis);
						event.setRevisedStart(DateHelper.getNewCalendar(event.getNoMoveBeforeDate()), false);

						// we also move the end date the same amount of time in difference between the no move date and the start date
						final Calendar end = DateHelper.getNewCalendar(event.getActualEndDate());
						end.add(Calendar.MILLISECOND, (int) -milliDiff);
						event.setRevisedEnd(end, false);
						event.updateX(getStartingXFor(event.getRevisedStart()));
						continue;
					}
				}

				// remember, we check the start calendar on both occasions, as that's the mark that counts
				if (event.getNoMoveAfterDate() != null) {
					if (cal2.after(event.getNoMoveAfterDate())) {

						// same deal for end date as for start date
						final long millis = event.getNoMoveAfterDate().getTimeInMillis();
						final long milliDiff = Math.abs(event.getActualEndDate().getTimeInMillis() - millis);
						event.setRevisedEnd(DateHelper.getNewCalendar(event.getNoMoveAfterDate()), false);

						// we also move the end date the same amount of time in difference between the no move date and the start date
						final Calendar start = DateHelper.getNewCalendar(event.getActualStartDate());
						start.add(Calendar.MILLISECOND, (int) milliDiff);
						event.setRevisedStart(start, false);
						event.updateX(getStartingXFor(event.getRevisedStart()));
						continue;
					}
				}

				// we already validated dates here, so we can just set them (Besides, dual validation is bad, as one date will be validated
				// before the next one is set, which can cause serious issues when we do drag and drops
				event.setRevisedStart(cal1, false);
				event.setRevisedEnd(cal2, false);

				// we move it by updating the x position to its new location
				event.updateX(getStartingXFor(cal1));

				// to move events vertically
				// event.updateY(toControl(me.x, me.y).);
			} else if (type == Constants.TYPE_RESIZE_LEFT) {
				cal1.add(calMark, diff);

				if (!isNoOverlap(cal1, event.getActualEndDate())) {
					continue;
				}

				event.moveStarted(type);

				// check before date move as it's the start date
				if (event.getNoMoveBeforeDate() != null) {
					if (cal1.before(event.getNoMoveBeforeDate())) {
						// for start dates we need to actually set the date or we'll be 1 day short as we did the diff already
						event.setRevisedStart(event.getNoMoveBeforeDate());
						continue;
					}
				}

				event.setRevisedStart(cal1, false);

				// update size + location
				event.updateX(getStartingXFor(cal1));
				event.updateWidth(getXLengthForEvent(event));
			} else if (type == Constants.TYPE_RESIZE_RIGHT) {
				cal2.add(calMark, diff);

				if (!isNoOverlap(event.getActualStartDate(), cal2)) {
					continue;
				}

				// remember, we check the start calendar on both occasions, as that's the mark that counts
				if (event.getNoMoveAfterDate() != null) {
					if (cal2.after(event.getNoMoveAfterDate())) {
						continue;
					}
				}

				event.moveStarted(type);

				event.setRevisedEnd(cal2, false);

				// update size + location
				event.updateX(getStartingXFor(cal1));
				event.updateWidth(getXLengthForEvent(event));
			}

			if (!eventsMoved.contains(event)) {
				eventsMoved.add(event);
			}
		}

		if (!eventsMoved.contains(ge)) {
			eventsMoved.add(ge);
		}

		for (int x = 0; x < _eventListeners.size(); x++) {
			final IGanttEventListener listener = _eventListeners.get(x);
			if (type == Constants.TYPE_MOVE) {
				listener.eventsMoved(eventsMoved, me);
			} else {
				listener.eventsResized(eventsMoved, me);
			}
		}

		// TODO: Granualize, if possible, quite hard with arrow-connections
		redrawEventsArea();
	}

	public void mouseEnter(MouseEvent event) {
	}

	public void mouseExit(MouseEvent event) {
	}

	public void mouseHover(MouseEvent me) {
		if (_dragging || _resizing) {
			return;
		}

		// right now all we do here is tooltips
		if (!_settings.showToolTips()) {
			return;
		}

		// possible fix for mac's over-and-over display of hover tip
		if (GanttToolTip.isActive()) {
			return;
		}

		if (me.stateMask != 0) {
			return;
		}

		if (_mainBounds == null || me.x >= _mainBounds.x) {
			for (int i = 0; i < _ganttEvents.size(); i++) {
				final GanttEvent event = _ganttEvents.get(i);
				if (!event.isHidden()) {
					if (isInside(me.x, me.y, new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight()))) {
						showTooltip(event, me);
						return;
					}

					if (_settings.showHolidayToolTips()) {
						for (final Holiday holiday : holidays) {
							if (holiday.hasTooltip() && isInside(me.x, me.y, holiday.getBounds())) {
								showTooltip(holiday, me);
								return;
							}
						}
					}
				}
			}
		}
	}

	private void showTooltip(GanttEvent event, MouseEvent me) {
		if (!_settings.showToolTips()) {
			return;
		}

		long days = DateHelper.daysBetween(event.getStartDate(), event.getEndDate());
		days++;
		long revisedDays = 0;

		final boolean dDay = _currentView == ISettings.VIEW_D_DAY;
		if (dDay) {
			days = event.getDDateRange();
		}

		// as the dialog is slightly bigger in many aspects, push it slightly
		// off to the right
		int xPlus = 0;
		if (isUseAdvancedTooltips() || event.getAdvancedTooltip() != null) {
			xPlus = _settings.getAdvancedTooltipXOffset();
		}

		final Point displayLocation = super.toDisplay(new Point(me.x + xPlus, me.y));

		final String dateFormat = _currentView == ISettings.VIEW_DAY ? _settings.getHourDateFormat() : _currentView == ISettings.VIEW_MINUTE ? ((ISettings2) _settings).getMinuteDateFormat() : _settings.getDateFormat();

		String startDate = DateHelper.getDate(event.getStartDate(), dateFormat);
		String endDate = DateHelper.getDate(event.getEndDate(), dateFormat);

		Calendar revisedStart = event.getRevisedStart();
		Calendar revisedEnd = event.getRevisedEnd();
		final StringBuffer extra = new StringBuffer();

		String revisedStartText = null;
		String revisedEndText = null;

		int dDayStart = 0;
		int dDayEnd = 0;
		if (dDay) {
			dDayStart = event.getDDayStart() + 1;
			dDayEnd = event.getDDayEnd();
			startDate = "" + dDayStart;
			endDate = "" + dDayEnd;
		}

		if (dDay) {
			if (event.getDDayRevisedStart() == Integer.MAX_VALUE) {
				revisedStart = null;
			}
			if (event.getDDayRevisedEnd() == Integer.MAX_VALUE) {
				revisedEnd = null;
			}
		}

		if (revisedStart != null) {
			extra.append(_languageManager.getRevisedText());
			extra.append(": ");
			revisedStartText = dDay ? "" + event.getDDayRevisedStart() : DateHelper.getDate(revisedStart, dateFormat);
			extra.append(revisedStartText);
		}

		if (revisedEnd != null) {
			if (revisedStart == null) {
				revisedStart = event.getActualStartDate();
			}

			revisedEndText = dDay ? "" + event.getDDayRevisedEnd() : DateHelper.getDate(revisedEnd, dateFormat);
			revisedDays = DateHelper.daysBetween(revisedStart, revisedEnd);
			revisedDays++;
			extra.append(Constants.STR_DASH);
			extra.append(revisedEndText);
			extra.append(" (");
			if (dDay) {
				revisedDays = event.getRevisedDDateRange();
			}
			extra.append(revisedDays);
			extra.append(' ');
			if (revisedDays == 1 || revisedDays == -1) {
				extra.append(_languageManager.getDaysText());
			} else {
				extra.append(_languageManager.getDaysPluralText());
			}
			extra.append(')');
		}

		AdvancedTooltip at = event.getAdvancedTooltip();
		if (at == null && isUseAdvancedTooltips()) {
			String ttText = _settings.getDefaultAdvancedTooltipTextExtended();
			if (event.isCheckpoint() || event.isImage() || event.isScope() || revisedStartText == null && revisedEnd == null) {
				ttText = _settings.getDefaultAdvancedTooltipText();
			}

			at = new AdvancedTooltip(_settings.getDefaultAdvancedTooltipTitle(), ttText, _settings.getDefaultAdvandedTooltipImage(), _settings.getDefaultAdvandedTooltipHelpImage(), _settings.getDefaultAdvancedTooltipHelpText());
		}

		if (at != null) {
			String title = null;
			String content = null;
			String help = null;

			final IToolTipContentReplacer ttcr = _settings.getToolTipContentReplacer();
			if (ttcr != null) {
				title = ttcr.replaceToolTipPlaceHolder(event, at.getTitle(), dateFormat);
				content = ttcr.replaceToolTipPlaceHolder(event, at.getContent(), dateFormat);
				help = ttcr.replaceToolTipPlaceHolder(event, at.getHelpText(), dateFormat);
			} else {
				title = fixTooltipString(at.getTitle(), event.getName(), startDate, endDate, revisedStartText, revisedEndText, days, revisedDays, event.getPercentComplete());
				content = fixTooltipString(at.getContent(), event.getName(), startDate, endDate, revisedStartText, revisedEndText, days, revisedDays, event.getPercentComplete());
				help = fixTooltipString(at.getHelpText(), event.getName(), startDate, endDate, revisedStartText, revisedEndText, days, revisedDays, event.getPercentComplete());
			}

			AdvancedTooltipDialog.makeDialog(at, _colorManager, displayLocation, title, content, help);
		} else {
			if (extra.length() > 0) {
				final StringBuffer buf = new StringBuffer();
				buf.append(_languageManager.getPlannedText());
				buf.append(": ");
				if (dDay) {
					buf.append(dDayStart);
				} else {
					buf.append(startDate);
				}
				buf.append(Constants.STR_DASH);
				if (dDay) {
					buf.append(dDayEnd);
				} else {
					buf.append(endDate);
				}
				buf.append(" (");
				buf.append(days);
				buf.append(' ');
				buf.append(days == 1 || days == -1 ? _languageManager.getDaysText() : _languageManager.getDaysPluralText());
				buf.append(')');

				GanttToolTip.makeDialog(_colorManager, event.getName(), extra.toString(), buf.toString(), event.getPercentComplete() + _languageManager.getPercentCompleteText(), displayLocation);
			} else {
				final StringBuffer buf = new StringBuffer();
				if (dDay) {
					buf.append(dDayStart);
				} else {
					buf.append(startDate);
				}
				buf.append(Constants.STR_DASH);
				if (dDay) {
					buf.append(dDayEnd);
				} else {
					buf.append(endDate);
				}
				buf.append(" (");
				buf.append(days);
				buf.append(' ');
				buf.append(days == 1 || days == -1 ? _languageManager.getDaysText() : _languageManager.getDaysPluralText());
				buf.append(')');

				GanttToolTip.makeDialog(_colorManager, event.getName(), buf.toString(), event.getPercentComplete() + _languageManager.getPercentCompleteText(), displayLocation);
			}
		}
	}

	private String fixTooltipString(final String input, String name, String startDate, String endDate, String plannedStart, String plannedEnd, long days, long plannedDays, int percentageComplete) {
		String str = input;
		if (str != null) {
			str = str.replaceAll("#name#", name);
			str = str.replaceAll("#sd#", startDate);
			str = str.replaceAll("#ed#", endDate);
			str = str.replaceAll("#rs#", plannedStart == null ? "n/a" : plannedStart);
			str = str.replaceAll("#re#", plannedEnd == null ? "n/a" : plannedEnd);
			str = str.replaceAll("#days#", "" + days);
			str = str.replaceAll("#reviseddays#", "" + plannedDays);
			str = str.replaceAll("#pc#", "" + percentageComplete);
			return str;
		}

		return null;
	}

	private void showTooltip(Holiday holiday, MouseEvent me) {
		final int xPlus = 0;

		final Point displayLocation = super.toDisplay(new Point(me.x + xPlus, me.y));
		GanttToolTip.makeDialog(_colorManager, _languageManager.getHolidayText(), holiday.getTooltip(), displayLocation);
	}

	/**
	 * Returns a rectangle with the bounds of what is actually visible inside the chart.
	 *
	 * @return Rectangle
	 */
	public Rectangle getVisibleBounds() {
		return _visibleBounds;
	}

	/**
	 * Returns the current view.
	 *
	 * @return View
	 */
	public int getCurrentView() {
		return _currentView;
	}

	/**
	 * Sets the current view.
	 *
	 * @param view View
	 */
	public void setView(int view) {
		_currentView = view;
		// do stuff here
		redraw();
	}

	/**
	 * Returns all events.
	 *
	 * @return List of all events.
	 */
	public List getEvents() {
		return _ganttEvents;
	}

	/**
	 * Returns the current visible area of the chart as an image
	 *
	 * @return Image of the chart
	 */
	public Image getImage() {
		return getImage(_visibleBounds);
	}

	/**
	 * Returns the chart as an image with the visible horizontal area
	 * but showing all information in the chart vertically.
	 *
	 * @return Image of the chart
	 */
	public Image getVerticallyFullImage() {
		final Rectangle verticallyFullBounds = new Rectangle(_visibleBounds.x, 0, _visibleBounds.width, _bottomMostY);
		return getImage(verticallyFullBounds);
	}

	/**
	 * Returns the image that is the entire chart, regardless of what is currently visible. If chart contains no events,
	 * {@link #getImage()} is called from within.
	 * <p>
	 * Do note that if the chart is "huge", you may need to increase your heap size. If you're zoomed in that's also
	 * taken into account and you may need a massive heap to work with hours views as they are simply huge in a pixel-size sense.
	 *
	 * @return Image
	 */
	public Image getFullImage() {
		checkWidget();

		// we need to pretend that we are at scroll position 0 along with that our bounds are as big as all visible events,
		// thus we save old values before so we can reset them at the end
		_savingChartImage = true;
		final int oldVscroll = _vScrollPos;
		_vScrollPos = 0;
		moveYBounds(-oldVscroll);
		final Rectangle oldBounds = _mainBounds;
		final Calendar currentCalendar = DateHelper.getNewCalendar(_mainCalendar);
		try {
			// as we may accidentally move the current chart when saving, don't show the user, we'll be drawing on a different canvas anyway
			setRedraw(false);

			final GanttEvent geLeft = getEvent(true, true);
			final Rectangle fullBounds = new Rectangle(0, 0, 0, 0);
			final GanttEvent geRight = getEvent(false, true);

			if (geRight == null || geLeft == null) {
				return getImage();
			}

			// set calendar to earliest date
			final boolean drawSections = hasGanttSections();
			int extraX = 0;
			int extraW = 0;
			if (drawSections) {
				if (_settings.drawSectionBar()) {
					if (_settings.getSectionSide() == SWT.LEFT) {
						extraX -= _settings.getSectionBarWidth();
					}
					extraW += _settings.getSectionBarWidth();
				}
				if (_settings.drawSectionDetails()) {
					if (_settings.getSectionSide() == SWT.LEFT) {
						extraX -= _settings.getSectionDetailWidth();
					} else {
						extraW += _settings.getSectionDetailWidth() / 2;
					}
				}
			}

			int leftBound = geLeft.getActualBounds().x;
			if (_settings.getPeriodStart() != null) {
				leftBound = Math.min(leftBound, getStartingXFor(_settings.getPeriodStart()));
			}

			// add spacing of 3 days to the left
			final int dw = getDayWidth();
			extraX -= dw * 3;

			_mainCalendar = getDateAt(leftBound + extraX);

			final Rectangle rBounds = geRight.getActualBounds();

			int rightBound = rBounds.x + rBounds.width + extraW;
			if (_settings.getPeriodEnd() != null) {
				rightBound = Math.max(rightBound, getStartingXFor(_settings.getPeriodEnd()));
			}

			// the zoom level has impact on the start position of the gantt rendering
			// therefore we need to take that into account
			if (_currentView == ISettings.VIEW_YEAR) {
				extraW += _mainCalendar.get(Calendar.DAY_OF_MONTH) * dw;
			}

			// add spacing of 3 days to the right
			extraW += dw * 3;

			fullBounds.width = rightBound - leftBound - extraX + extraW;
			fullBounds.height = _bottomMostY;

			// set chart bounds to be the fake bounds
			_mainBounds = fullBounds;

			// forcing a full update or event visibilities will not change
			flagForceFullUpdate();

			final Image buffer = new Image(getDisplay(), fullBounds);

			final GC gc2 = new GC(buffer);
			drawChartOntoGC(gc2, fullBounds);
			drawHeader(gc2, false);

			// we don't draw this when saving an image until the very end as we push
			// bounds around differently and it gets drawn mis-aligned if we draw it
			// before
			if (drawSections && _settings.getSectionSide() == SWT.RIGHT) {
				drawSectionColumn(gc2, fullBounds, false, true, false, true);
			}

			gc2.dispose();

			return buffer;
		} catch (final Exception err) {
			SWT.error(SWT.ERROR_UNSPECIFIED, err);
		} finally {
			// reset everything, including forcing a redraw and reset
			_vScrollPos = oldVscroll;
			moveYBounds(_vScrollPos);
			_savingChartImage = false;
			_mainBounds = oldBounds;
			_mainCalendar = currentCalendar;
			getDisplay().asyncExec(new Runnable() {
				public void run() {
					flagForceFullUpdate();
					// this ensures no event-flicker
					setRedraw(true);
					redraw();
				}
			});
		}

		return null;
	}

	/**
	 * Returns the chart as an image for the given bounds.
	 *
	 * @param bounds Rectangle bounds
	 * @return Image of chart
	 */
	public Image getImage(Rectangle bounds) {
		checkWidget();
		setRedraw(false);

		_savingChartImage = true;
		try {
			final Image buffer = new Image(getDisplay(), bounds);

			final GC gc2 = new GC(buffer);
			drawChartOntoGC(gc2, bounds);

			// we don't draw this when saving an image until the very end as we push
			// bounds around differently and it gets drawn mis-aligned if we draw it
			// before
			if (hasGanttSections() && _settings.getSectionSide() == SWT.RIGHT) {
				drawSectionColumn(gc2, bounds, false, true, false, true);
			}

			gc2.dispose();
			return buffer;
		} catch (final Exception err) {
			SWT.error(SWT.ERROR_UNSPECIFIED, err);
		} finally {
			_savingChartImage = false;

			setRedraw(true);
			redraw();
		}

		return null;
	}

	private List<Object> getEventsDependingOn(GanttEvent ge) {
		if (_ganttConnections.isEmpty()) {
			return new ArrayList<>();
		}

		final GanttMap gm = new GanttMap();

		for (int i = 0; i < _ganttConnections.size(); i++) {
			final GanttConnection conn = _ganttConnections.get(i);
			gm.put(conn.getSource(), conn.getTarget());
			gm.put(conn.getTarget(), conn.getSource());
		}

		final Set ret = recursiveGetEventsDependingOn(ge, gm, new HashSet());
		if (!ret.contains(ge)) {
			ret.add(ge);
		}

		final List retList = new ArrayList();
		final Iterator ite = ret.iterator();
		while (ite.hasNext()) {
			retList.add(ite.next());
		}

		return retList;
	}

	// get all chained events
	private Set recursiveGetEventsDependingOn(GanttEvent ge, GanttMap gm, Set ret) {
		final List conns = gm.get(ge);
		if (conns != null && conns.size() > 0) {
			for (int i = 0; i < conns.size(); i++) {
				final GanttEvent event = (GanttEvent) conns.get(i);
				if (ret.contains(event)) {
					continue;
				} else {
					ret.add(event);
				}

				final Set more = recursiveGetEventsDependingOn(event, gm, ret);
				if (more.size() > 0) {
					final Iterator ite = more.iterator();
					while (ite.hasNext()) {
						final Object obj = ite.next();
						if (!ret.contains(obj)) {
							ret.add(obj);
						}
					}
				}
			}
		}

		return ret;
	}

	private void updateZoomLevel() {
		final int originalDayWidth = _settings.getDayWidth();
		final int originalMonthWeekWidth = _settings.getMonthDayWidth();
		final int originalYearMonthDayWidth = _settings.getYearMonthDayWidth();

		boolean dDay = false;
		if (_currentView == ISettings.VIEW_D_DAY) {
			dDay = true;
		}

		switch (_zoomLevel) {
			// seconds
			case ISettings.ZOOM_SECONDS_MAX:
				_currentView = ISettings.VIEW_MINUTE;
				_dayWidth = originalDayWidth * 18;
				break;
			case ISettings.ZOOM_SECONDS_MEDIUM:
				_currentView = ISettings.VIEW_MINUTE;
				_dayWidth = originalDayWidth * 16;
				break;
			case ISettings.ZOOM_SECONDS_NORMAL:
				_currentView = ISettings.VIEW_MINUTE;
				_dayWidth = originalDayWidth * 14;
				break;
			// minutes
			case ISettings.ZOOM_MINUTES_MAX:
				_currentView = ISettings.VIEW_MINUTE;
				_dayWidth = originalDayWidth * 12;
				break;
			case ISettings.ZOOM_MINUTES_MEDIUM:
				_currentView = ISettings.VIEW_MINUTE;
				_dayWidth = originalDayWidth * 10;
				break;
			case ISettings.ZOOM_MINUTES_NORMAL:
				_currentView = ISettings.VIEW_MINUTE;
				_dayWidth = originalDayWidth * 8;
				break;
			// hour
			case ISettings.ZOOM_HOURS_MAX:
				_currentView = ISettings.VIEW_DAY;
				_dayWidth = originalDayWidth * 6;
				break;
			case ISettings.ZOOM_HOURS_MEDIUM:
				_currentView = ISettings.VIEW_DAY;
				_dayWidth = originalDayWidth * 4;
				break;
			case ISettings.ZOOM_HOURS_NORMAL:
				_currentView = ISettings.VIEW_DAY;
				_dayWidth = originalDayWidth * 2;
				break;
			// mDay
			case ISettings.ZOOM_DAY_MAX:
				_currentView = ISettings.VIEW_WEEK;
				_dayWidth = originalDayWidth * 3;
				break;
			case ISettings.ZOOM_DAY_MEDIUM:
				_currentView = ISettings.VIEW_WEEK;
				_dayWidth = originalDayWidth * 2;
				break;
			case ISettings.ZOOM_DAY_NORMAL:
				_currentView = ISettings.VIEW_WEEK;
				_dayWidth = originalDayWidth;
				break;
			case ISettings.ZOOM_MONTH_MAX:
				_currentView = ISettings.VIEW_MONTH;
				_monthDayWidth = originalMonthWeekWidth + 6;
				break;
			case ISettings.ZOOM_MONTH_MEDIUM:
				_currentView = ISettings.VIEW_MONTH;
				_monthDayWidth = originalMonthWeekWidth + 3;
				break;
			case ISettings.ZOOM_MONTH_NORMAL:
				_currentView = ISettings.VIEW_MONTH;
				_monthDayWidth = originalMonthWeekWidth;
				break;
			case ISettings.ZOOM_YEAR_MAX:
				_currentView = ISettings.VIEW_YEAR;
				_yearDayWidth = originalYearMonthDayWidth + 3;
				break;
			case ISettings.ZOOM_YEAR_MEDIUM:
				_currentView = ISettings.VIEW_YEAR;
				_yearDayWidth = originalYearMonthDayWidth + 2;
				break;
			case ISettings.ZOOM_YEAR_NORMAL:
				_currentView = ISettings.VIEW_YEAR;
				_yearDayWidth = originalYearMonthDayWidth;
				break;
			case ISettings.ZOOM_YEAR_SMALL:
				_currentView = ISettings.VIEW_YEAR;
				_yearDayWidth = originalYearMonthDayWidth - 1;
				break;
			case ISettings.ZOOM_YEAR_VERY_SMALL:
				_currentView = ISettings.VIEW_YEAR;
				_yearDayWidth = originalYearMonthDayWidth - 2;
				break;
			default:
				break;
		}

		_minuteDayWidth = _dayWidth * 1440;
		_weekWidth = _dayWidth * 7;
		_monthWeekWidth = _monthDayWidth * 7;

		// mYearMonthWeekWidth = mYearDayWidth * 7;
		// mYearMonthWidth = mYearMonthWeekWidth * 4;

		// not a hack, we just re-use the same parameter name but for a
		// different purpose than the name itself, not exactly great logic
		// but it saves some recoding
		if (_zoomLevel == ISettings.ZOOM_SECONDS_MAX || _zoomLevel == ISettings.ZOOM_SECONDS_MEDIUM || _zoomLevel == ISettings.ZOOM_SECONDS_NORMAL || _zoomLevel == ISettings.ZOOM_MINUTES_MAX || _zoomLevel == ISettings.ZOOM_MINUTES_MEDIUM
				|| _zoomLevel == ISettings.ZOOM_MINUTES_NORMAL || _zoomLevel == ISettings.ZOOM_HOURS_MAX || _zoomLevel == ISettings.ZOOM_HOURS_MEDIUM || _zoomLevel == ISettings.ZOOM_HOURS_NORMAL) {
			// how many hours are there really in our work day? we don't show
			// anything else!
			_weekWidth = _dayWidth * 24;
		}

		if (dDay) {
			_currentView = ISettings.VIEW_D_DAY;
		}

		for (final Holiday holiday : holidays) {
			holiday.resetBounds();
		}
	}

	/**
	 * This will cause a full recaclulation of events and a lot of other things. Normally this is used internally when
	 * there are zoom changes and/or other events that cause the chart to need a full recalculation. It is <b>NOT</b>
	 * intended to be used outside of the chart, but is available as a workaround if there is a bug that you can't get
	 * around and you need to force a full update. See this method as a temporary solution if you need to use it.
	 */
	public void heavyRedraw() {
		_zoomLevelChanged = true;
		forceFullUpdate();
	}

	void forceFullUpdate() {
		flagForceFullUpdate();
		redraw();
	}

	void flagForceFullUpdate() {
		_recalcScopes = true;
		_recalcSecBounds = true;

		for (int i = 0; i < _ganttEvents.size(); i++) {
			_ganttEvents.get(i).setBoundsSet(false);
		}

		_forceSBUpdate = true;
	}

	public IViewPortHandler getViewPortHandler() {
		return _viewPortHandler;
	}

	public void setViewPortHandler(IViewPortHandler2 vph) {
		_viewPortHandler = vph;
	}

	void updateHorizontalScrollbar() {
		_hScrollHandler.recalculate();
	}

	int getLeftMostPixel() {
		return getPixel(true);
	}

	int getRightMostPixel() {
		return getPixel(false);
	}

	private int getPixel(boolean left) {
		int max = left ? Integer.MAX_VALUE : Integer.MIN_VALUE;

		for (int i = 0; i < _ganttEvents.size(); i++) {
			final GanttEvent ge = _ganttEvents.get(i);
			final Rectangle actualBounds = ge.getActualBounds();

			if (left) {
				if (actualBounds.x < max) {
					max = actualBounds.x;
				}
			} else {
				final int w = actualBounds.x + actualBounds.width;
				if (w > max) {
					max = w;
				}
			}
		}

		return max;
	}

	/**
	 * Sets the zoom level. If the new level is zoomed in from the previous set zoom level a zoom in event will be
	 * reported, otherwise a zoom out.
	 *
	 * @param level Level to set
	 */
	public void setZoomLevel(int level) {
		if (level == _zoomLevel) {
			return;
		}

		int toSet = level;

		if (toSet < ISettings.MIN_ZOOM_LEVEL) {
			toSet = ISettings.MIN_ZOOM_LEVEL;
		}
		if (toSet > ISettings.MAX_ZOOM_LEVEL) {
			toSet = ISettings.MAX_ZOOM_LEVEL;
		}

		final int oldZoomLevel = _zoomLevel;
		_zoomLevel = toSet;

		if (_zoomLevel >= ISettings.ZOOM_HOURS_MAX) {
			final Calendar datetoSet = DateHelper.getNewCalendar(_mainCalendar);
			datetoSet.set(Calendar.MINUTE, 0);
			_mainCalendar = datetoSet;
		}

		updateZoomLevel();
		_zoomLevelChanged = true;

		forceFullUpdate();

		for (int i = 0; i < _eventListeners.size(); i++) {
			final IGanttEventListener listener = _eventListeners.get(i);
			if (toSet < oldZoomLevel) {
				listener.zoomedOut(_zoomLevel);
			} else {
				listener.zoomedIn(_zoomLevel);
			}
		}
	}

	/**
	 * Zooms in. If zooming is disabled, does nothing.
	 */
	public void zoomIn() {
		zoomIn(false, null);
	}

	public void zoomIn(boolean fromMouseWheel, Point mouseLoc) {
		checkWidget();
		if (!_settings.enableZooming()) {
			return;
			/*
			 * if (mCurrentView == ISettings.VIEW_D_DAY)
			 * return;
			 */
		}

		_zoomLevel--;
		if (_zoomLevel < _settings.getMinZoomLevel()) {
			_zoomLevel = _settings.getMinZoomLevel();
			return;
		}

		Calendar preZoom = null;
		if (fromMouseWheel && mouseLoc != null) {
			preZoom = getDateAt(mouseLoc.x);
		}

		updateZoomLevel();

		if (fromMouseWheel && _settings.zoomToMousePointerDateOnWheelZooming() && mouseLoc != null) {
			internalSetDateAtX(mouseLoc.x, preZoom, true, false, true);
		}

		_zoomLevelChanged = true;

		forceFullUpdate();

		for (int i = 0; i < _eventListeners.size(); i++) {
			final IGanttEventListener listener = _eventListeners.get(i);
			listener.zoomedIn(_zoomLevel);
		}

	}

	/**
	 * Zooms out. If zooming is disabled, does nothing.
	 */
	public void zoomOut() {
		zoomOut(false, null);
	}

	public void zoomOut(boolean fromMouseWheel, Point mouseLoc) {
		checkWidget();
		if (!_settings.enableZooming()) {
			return;
		}

		_zoomLevel++;
		if (_zoomLevel > ISettings.MAX_ZOOM_LEVEL) {
			_zoomLevel = ISettings.MAX_ZOOM_LEVEL;
			return;
		}

		if (_currentView == ISettings.VIEW_D_DAY) {
			if (_zoomLevel > ISettings.ZOOM_DAY_NORMAL) {
				_zoomLevel = ISettings.ZOOM_DAY_NORMAL;
				return;
			}
		}

		Calendar preZoom = null;
		if (fromMouseWheel && mouseLoc != null) {
			preZoom = getDateAt(mouseLoc.x);
		}

		if (_zoomLevel >= ISettings.ZOOM_HOURS_MAX) {
			final Calendar toSet = DateHelper.getNewCalendar(_mainCalendar);
			toSet.set(Calendar.MINUTE, 0);
			_mainCalendar = toSet;
		}

		updateZoomLevel();

		if (fromMouseWheel && _settings.zoomToMousePointerDateOnWheelZooming() && mouseLoc != null) {
			internalSetDateAtX(mouseLoc.x, preZoom, true, false, false);
		}

		_zoomLevelChanged = true;
		forceFullUpdate();

		for (int i = 0; i < _eventListeners.size(); i++) {
			final IGanttEventListener listener = _eventListeners.get(i);
			listener.zoomedOut(_zoomLevel);
		}
	}

	/**
	 * Resets the zoom level to that set in the settings.
	 */
	public void resetZoom() {
		checkWidget();
		if (!_settings.enableZooming()) {
			return;
		}

		_zoomLevel = _settings.getInitialZoomLevel();

		if (_zoomLevel >= ISettings.ZOOM_HOURS_MAX) {
			final Calendar toSet = DateHelper.getNewCalendar(_mainCalendar);
			toSet.set(Calendar.MINUTE, 0);
			_mainCalendar = toSet;
		}

		updateZoomLevel();

		_zoomLevelChanged = true;
		forceFullUpdate();

		for (int i = 0; i < _eventListeners.size(); i++) {
			final IGanttEventListener listener = _eventListeners.get(i);
			listener.zoomReset();
		}
	}

	// override so we can tell paint manager to reset
	@Override
	public void redraw() {
		_paintManager.redrawStarting();
		super.redraw();
	}

	/**
	 * Adds a listener that will be notified of Gantt events.
	 *
	 * @param listener Listener
	 */
	public void addGanttEventListener(IGanttEventListener listener) {
		checkWidget();
		if (_eventListeners.contains(listener)) {
			return;
		}

		_eventListeners.add(listener);
	}

	/**
	 * Removes a listener from being notified of Gantt events.
	 *
	 * @param listener Listener
	 */
	public void removeGanttEventListener(IGanttEventListener listener) {
		checkWidget();
		_eventListeners.remove(listener);
	}

	private boolean isUseAdvancedTooltips() {
		return _useAdvTooltips;
	}

	/**
	 * Sets whether to use advanced tooltips or not. This method will override the settings implementation with the same
	 * name.
	 *
	 * @param useAdvancedTooltips true whether to use advanced tooltips.
	 */
	public void setUseAdvancedTooltips(boolean useAdvancedTooltips) {
		_useAdvTooltips = useAdvancedTooltips;
	}

	/**
	 * Returns (a clone) of the D-Day calendar
	 *
	 * @return D-Day calendar
	 */
	public Calendar getDDayCalendar() {
		return DateHelper.getNewCalendar(_dDayCalendar);
	}

	/**
	 * Returns the Undo/Redo manager.
	 *
	 * @return {@link GanttUndoRedoManager}
	 */
	public GanttUndoRedoManager getUndoRedoManager() {
		return _undoRedoManager;
	}

	/**
	 * Returns the number of visible events on in the current viewport
	 *
	 * @return number of visible events on in the current viewport
	 */
	public int getTotalVisibileGanttEvents() {
		return _totVisEventCnt;
	}

	/**
	 * Selects all events
	 */
	public void selectAll() {
		_selectedEvents.clear();
		final Object[] all = _allEventsCombined.toArray();
		for (int i = 0; i < all.length; i++) {
			_selectedEvents.add(all[i]);
		}

		redraw();
	}

	/**
	 * Clears all selected events
	 */
	public void deselectAll() {
		_selectedEvents.clear();
		redraw();
	}

	// as from:
	// http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet139.java?view=co
	static ImageData rotate(ImageData srcData, int direction) {
		final int bytesPerPixel = srcData.bytesPerLine / srcData.width;
		final int destBytesPerLine = direction == SWT.DOWN ? srcData.width * bytesPerPixel : srcData.height * bytesPerPixel;
		final byte[] newData = new byte[srcData.data.length];
		int width = 0, height = 0;
		for (int srcY = 0; srcY < srcData.height; srcY++) {
			for (int srcX = 0; srcX < srcData.width; srcX++) {
				int destX = 0, destY = 0, destIndex = 0, srcIndex = 0;
				switch (direction) {
					case SWT.LEFT: // left 90 degrees
						destX = srcY;
						destY = srcData.width - srcX - 1;
						width = srcData.height;
						height = srcData.width;
						break;
					case SWT.RIGHT: // right 90 degrees
						destX = srcData.height - srcY - 1;
						destY = srcX;
						width = srcData.height;
						height = srcData.width;
						break;
					case SWT.DOWN: // 180 degrees
						destX = srcData.width - srcX - 1;
						destY = srcData.height - srcY - 1;
						width = srcData.width;
						height = srcData.height;
						break;
					default:
						break;
				}
				destIndex = destY * destBytesPerLine + destX * bytesPerPixel;
				srcIndex = srcY * srcData.bytesPerLine + srcX * bytesPerPixel;
				System.arraycopy(srcData.data, srcIndex, newData, destIndex, bytesPerPixel);
			}
		}
		// destBytesPerLine is used as scanlinePad to ensure that no padding is
		// required
		return new ImageData(width, height, srcData.depth, srcData.palette, destBytesPerLine, newData);
	}

	static ImageData flip(ImageData srcData, boolean vertical) {
		final int bytesPerPixel = srcData.bytesPerLine / srcData.width;
		final int destBytesPerLine = srcData.width * bytesPerPixel;
		final byte[] newData = new byte[srcData.data.length];
		for (int srcY = 0; srcY < srcData.height; srcY++) {
			for (int srcX = 0; srcX < srcData.width; srcX++) {
				int destX = 0, destY = 0, destIndex = 0, srcIndex = 0;
				if (vertical) {
					destX = srcX;
					destY = srcData.height - srcY - 1;
				} else {
					destX = srcData.width - srcX - 1;
					destY = srcY;
				}
				destIndex = destY * destBytesPerLine + destX * bytesPerPixel;
				srcIndex = srcY * srcData.bytesPerLine + srcX * bytesPerPixel;
				System.arraycopy(srcData.data, srcIndex, newData, destIndex, bytesPerPixel);
			}
		}
		// destBytesPerLine is used as scanlinePad to ensure that no padding is
		// required
		return new ImageData(srcData.width, srcData.height, srcData.depth, srcData.palette, destBytesPerLine, newData);
	}

	boolean hasGanttPhases() {
		return !_ganttPhases.isEmpty();
	}

	boolean hasGanttSections() {
		return !_ganttSections.isEmpty();
	}

	boolean hasSpecialDateRanges() {
		return !_specDateRanges.isEmpty();
	}

	boolean isDDayCalendar() {
		return _currentView == ISettings.VIEW_D_DAY;
	}

	public ISettings getSettings() {
		return _settings;
	}

	public void setEventFactory(IEventFactory factory) {
		eventFactory = factory;
	}

	public void setEventMenuItemFactory(IEventMenuItemFactory factory) {
		eventMenuItemFactory = factory;
	}

	public void setMenuItemFactory(IMenuItemFactory factory) {
		menuItemFactory = factory;
	}

	public void setZoomHandler(IZoomHandler zoomHandler) {
		this.zoomHandler = zoomHandler;
	}

	public void addSelectionDetailClickListener(ISectionDetailMoreClickListener listener) {
		sectionDetailMoreClickListener.add(listener);
	}

	public void removeSelectionDetailClickListener(ISectionDetailMoreClickListener listener) {
		sectionDetailMoreClickListener.remove(listener);
	}

	public void setHolidays(Holiday[] holidays) {
		this.holidays = holidays;
	}
}
