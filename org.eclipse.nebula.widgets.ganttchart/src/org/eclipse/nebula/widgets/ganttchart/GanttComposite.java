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

import org.eclipse.nebula.widgets.ganttchart.dnd.VerticalDragDropManager;
import org.eclipse.nebula.widgets.ganttchart.undoredo.GanttUndoRedoManager;
import org.eclipse.nebula.widgets.ganttchart.undoredo.commands.ClusteredCommand;
import org.eclipse.nebula.widgets.ganttchart.undoredo.commands.IUndoRedoCommand;
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
import org.eclipse.swt.graphics.Cursor;
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
// -- BUGS
// TODO: if planned dates are showing we need to check event visibilities differently
// -- FEATURES
// TODO: allow zoom-out for D-day charts (we need to flip it from ISettings.D_DAY to a bool as we should draw d-day headers in each respective zoom-level head instead)
// TODO: millisecond view
public final class GanttComposite extends Canvas implements MouseListener, MouseMoveListener, MouseTrackListener, KeyListener, IGanttFlags {
    private static final Cursor        CURSOR_NONE                  = CursorCache.getCursor(SWT.NONE);
    private static final Cursor        CURSOR_SIZEE                 = CursorCache.getCursor(SWT.CURSOR_SIZEE);
    private static final Cursor        CURSOR_SIZEW                 = CursorCache.getCursor(SWT.CURSOR_SIZEW);
    private static final Cursor        CURSOR_SIZEALL               = CursorCache.getCursor(SWT.CURSOR_SIZEALL);
    private static final Cursor        CURSOR_HAND                  = CursorCache.getCursor(SWT.CURSOR_HAND);

    // connecting line drawing, internal
    private static final int           BEND_RIGHT_UP                = 1;
    private static final int           BEND_RIGHT_DOWN              = 2;
    private static final int           BEND_LEFT_UP                 = 3;
    private static final int           BEND_LEFT_DOWN               = 4;

    // scrolling directions, internal
    private static final int           DIRECTION_LEFT               = 1;
    private static final int           DIRECTION_RIGHT              = 2;
    private static final int           DIRECTION_UP                 = 3;
    private static final int           DIRECTION_DOWN               = 4;

    // out of bounds sides, internal
    private static final int           VISIBILITY_VISIBLE           = 1;
    private static final int           VISIBILITY_OOB_LEFT          = 2;
    private static final int           VISIBILITY_OOB_RIGHT         = 3;
    private static final int           VISIBILITY_NOT_VISIBLE       = 4;
    private static final int           VISIBILITY_OOB_HEIGHT_TOP    = 5;
    private static final int           VISIBILITY_OOB_HEIGHT_BOTTOM = 6;

    // resize info, internal
    public static final int            TYPE_RESIZE_LEFT             = 1;
    public static final int            TYPE_RESIZE_RIGHT            = 2;
    public static final int            TYPE_MOVE                    = 3;

    private static final int           TIMER_INTERVAL               = 25;

    // what operating system we're on
    public static final int            OS_OTHER                     = 0;
    public static final int            OS_WINDOWS                   = 1;
    public static final int            OS_MAC                       = 2;
    public static final int            OS_LINUX                     = 3;

    public static int                  osType                       = OS_OTHER;
    
    // current auto scroll direction
    private int                        _autoScrollDirection         = SWT.NULL;

    // keeps track of when to show or hide the zoom-helper area, go Clippy!
    private boolean                    _showZoomHelper;
    private Rectangle                  _zoomLevelHelpArea;

    // start level
    private int                        _zoomLevel;
    private boolean                    _zoomLevelChanged;

    private boolean                    _forceScrollbarsUpdate;

    // current view
    private int                        _currentView;

    // 3D looking events
    private boolean                    _threeDee;

    // show a little plaque telling the days between start and end on the chart,
    // only draws if it fits
    private boolean                    _showNumberOfDaysOnChart;

    // draw the revised dates as a very thin small black |------| on the chart
    private boolean                    _showPlannedDates;

    // how many days offset to start drawing the calendar at.. useful for not
    // having the first day be today
    private int                        _startCalendarAtOffset;

    // how many pixels from each event border to ignore as a move.. mostly to
    // help resizing
    private int                        _moveAreaInsets;

    // year stuff
    private int                        _yearDayWidth;

    //private int							mYearMonthWeekWidth;

    //private int							mYearMonthWidth;

    // month stuff
    private int                        _monthDayWidth;

    private int                        _monthWeekWidth;

    // day stuff
    // width of one day
    private int                        _dayWidth;

    // one week width, usually 7 days * width of one day
    private int                        _weekWidth;

    private int                        _bottomMostY;

    // various colors used.. all set in initColors()
    private Color                      _lineTodayColor;

    private Color                      _lineColor;

    private Color                      _lineWeekDividerColor;

    private Color                      _textColor;

    private Color                      _saturdayBackgroundColorTop;
    private Color                      _saturdayBackgroundColorBottom;

    private Color                      _weekdayTextColor;
    private Color                      _saturdayTextColor;
    private Color                      _sundayTextColor;

    private Color                      _sundayBackgroundColorTop;
    private Color                      _sundayBackgroundColorBottom;

    private Color                      _weekdayBackgroundColorTop;
    private Color                      _weekdayBackgroundColorBottom;

    private Color                      _textHeaderBackgroundColorTop;
    private Color                      _textHeaderBackgroundColorBottom;
    private Color                      _timeHeaderBackgroundColorTop;
    private Color                      _timeHeaderBackgroundColorBottom;

    private Color                      _phaseHeaderBackgroundColorTop;
    private Color                      _phaseHeaderBackgroundColorBottom;

    private Color                      _arrowColor;
    private Color                      _reverseArrowColor;

    private Color                      _todayBGColorTop;
    private Color                      _todayBGColorBottom;

    // currently selected event
    private List                       _selectedEvents;

    // the calendar that the current view is on, will always be on the first
    // date of the visible area (the date on the far left)
    // it should never change from internal functions. Where date changes, a
    // clone is made. It is critical it is never modified unless there is a reason for it.
    private Calendar                   _mainCalendar;

    // end calendar, is only used for reference internally
    private Calendar                   _endCalendar;

    // the number of days that will be visible in the current area. Is set after
    // we're done drawing the chart
    private int                        _daysVisible;
    private int                        _hoursVisible;

    // all events
    private List                       _ganttEvents;

    // all connections between events
    private List                       _ganttConnections;

    // a cache for re-used string extents
    private HashMap                    _dayLetterStringExtentMap;

    // various variables for resize and drag/drop
    private boolean                    _dragging                    = false;

    private boolean                    _resizing                    = false;

    private Point                      _dragStartLocation;

    private boolean                    _freeFloatDragging           = false;
    private int                        _verticalDraggingDirection   = SWT.NONE;

    private int                        _lastX;
    private int                        _lastY;

    private int                        _cursor;

    private List                       _dragEvents;

    private GanttPhase                 _dragPhase;

    private boolean                    _lastLeft                    = false;

    private Calendar                   _dragStartDate               = null;

    private boolean                    _justStartedMoveOrResize     = false;

    private int                        _initialHoursDragOffset      = 0;

    private ISettings                  _settings;

    private IColorManager              _colorManager;

    private IPaintManager              _paintManager;

    private ILanguageManager           _languageManager;

    private List                       _eventListeners;

    private boolean                    _mouseIsDown;

    private Point                      _mouseDragStartLocation;

    private List                       _ganttGroups;

    private List                       _ganttSections;

    // menu used at right click events
    private Menu                       _rightClickMenu;

    // whether advanced tooltips are on or not
    private boolean                    _useAdvancedTooltips;

    // whether alpha drawing is enabled or not
    private boolean                    _useAlpha;

    private Rectangle                  _mainBounds;

    // bounds that are currently visible (changes when scrolling vertically)
    private Rectangle                  _visibleBounds;

    private Locale                     _defaultLocale;

    private int                        _eventHeight;

    private boolean                    _reCalculateScopes           = true;
    private boolean                    _reCalculateSectionBounds    = true;

    private HashSet                    _allEventsCombined;                                                      // convenience list for all events regardless if they're in
    // sections, in groups, or single

    private List                       _verticalLineLocations;                                                  // fast cache for where the vertical lines go, much much

    // faster
    // than
    // re-calculating over and over
    private HashSet                    _verticalWeekDividerLineLocations;

    // keeps track of hidden layers
    private HashSet                    _hiddenLayers;
    // keeps track of layer opacities
    private HashMap                    _layerOpacityMap;

    // debug variables
    long                               _total                       = 0;
    int                                _redrawCount                 = 0;

    // only settings variables we allow direct override on
    private int                        _eventSpacer;
    private int                        _fixedRowHeight;
    private boolean                    _drawVerticalLines;
    private boolean                    _drawHorizontalLines;
    // end

    private int                        _lockedHeaderY;

    private int                        _verticalScrollPosition;

    private final Point                _origin                      = new Point(0, 0);
    private int                        _lastVerticalScrollPosition;

    private ScrollBar                  _verticalScrollBar;

    private List                       _selectedHeaderDates;

    private int                        _style;

    private Tracker                    _tracker;
    private boolean                    _multiSelect;

    private Calendar                   _dDayCalendar;

    private HorizontalScrollbarHandler _hScrollHandler;
    private ViewPortHandler            _viewPortHandler;

    private boolean                    _savingChartImage            = false;                                     ;

    private List                       _ganttPhases;

    private List                       _ganttSpecialDateRanges;

    // cache for "string-extented" strings for much faster fetching 
    private Map                        _stringWidthCache;

    // the total of all non-hidden events that are GanttEvents 
    private int                        _totalVisibleGanttEvents;

    private VerticalDragDropManager    _verticalDragDropManager;

    private GanttUndoRedoManager       _undoRedoManager;

    static {
        String osProperty = System.getProperty("os.name");
        if (osProperty != null) {
            String osName = osProperty.toUpperCase();

            if (osName.indexOf("WINDOWS") > -1) {
                osType = OS_WINDOWS;
            } else if (osName.indexOf("MAC") > -1) {
                osType = OS_MAC;
            } else if (osName.indexOf("NIX") > -1 || osName.indexOf("NUX") > -1) {
                osType = OS_LINUX;
            }
        }
    }

    public GanttComposite(final GanttChart parent, int style, ISettings settings, IColorManager colorManager, IPaintManager paintManager, ILanguageManager languageManager) {
        super(parent, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED | SWT.V_SCROLL | SWT.H_SCROLL);

        // d-day calendar can be anything, but it needs to be something, and for convenience, lets use 2000 as base year
        if (settings.getInitialView() == ISettings.VIEW_D_DAY) {
            _dDayCalendar = (Calendar) settings.getDDayRootCalendar().clone();
        }

        // midnight "thread" (we need to redraw the screen once at Midnight as the date line will otherwise be incorrect)
        // this is truly only necessary if someone were to be staring at the GANTT chart as midnight happens, so it's
        // incredibly minor (any redraw would draw the right date lines), but nevertheless...
        parent.getDisplay().timerExec(60000, new Runnable() {

            public void run() {
                try {
                    checkWidget();

                    if (isDisposed()) { return; }

                    Calendar cal = Calendar.getInstance(_settings.getDefaultLocale());
                    // Midnight, redraw
                    if (cal.get(Calendar.HOUR_OF_DAY) == 0 && cal.get(Calendar.MINUTE) == 0) {
                        redraw();
                    }

                    if (parent.getDisplay() != null && !parent.getDisplay().isDisposed()) {
                        parent.getDisplay().timerExec(60000, this);
                    }
                } catch (Exception err) {

                }
            }

        });

        _style = style;
        _multiSelect = (_style & SWT.MULTI) != 0;

        _settings = settings;
        _colorManager = colorManager;
        _paintManager = paintManager;
        _languageManager = languageManager;

        _ganttConnections = new ArrayList();
        _dragEvents = new ArrayList();
        _eventListeners = new ArrayList();
        _ganttEvents = new ArrayList();
        _ganttGroups = new ArrayList();
        _ganttSections = new ArrayList();
        _verticalLineLocations = new ArrayList();
        _verticalWeekDividerLineLocations = new HashSet();
        _hiddenLayers = new HashSet();
        _allEventsCombined = new HashSet();
        _dayLetterStringExtentMap = new HashMap();
        _layerOpacityMap = new HashMap();
        _selectedHeaderDates = new ArrayList();
        _selectedEvents = new ArrayList();
        _ganttPhases = new ArrayList();
        _ganttSpecialDateRanges = new ArrayList();

        _stringWidthCache = new HashMap();

        _defaultLocale = _settings.getDefaultLocale();
        _useAdvancedTooltips = _settings.getUseAdvancedTooltips();

        _currentView = _settings.getInitialView();
        _zoomLevel = _settings.getInitialZoomLevel();
        _showNumberOfDaysOnChart = _settings.showNumberOfDaysOnBars();
        _showPlannedDates = _settings.showPlannedDates();
        _threeDee = _settings.showBarsIn3D();
        _yearDayWidth = _settings.getYearMonthDayWidth();
        _monthDayWidth = _settings.getMonthDayWidth();
        _monthWeekWidth = _monthDayWidth * 7;
        _dayWidth = _settings.getDayWidth();
        _weekWidth = _dayWidth * 7;
        _startCalendarAtOffset = _settings.getCalendarStartupDateOffset();
        _mainCalendar = _settings.getStartupCalendarDate();
        _moveAreaInsets = _settings.getMoveAreaNegativeSensitivity();
        _eventSpacer = _settings.getEventSpacer();
        _eventHeight = _settings.getEventHeight();

        _drawHorizontalLines = _settings.drawHorizontalLines();
        _drawVerticalLines = _settings.drawVerticalLines();

        _verticalDragDropManager = new VerticalDragDropManager();
        _undoRedoManager = new GanttUndoRedoManager(this, GanttUndoRedoManager.DEFAULT_STACK_SIZE);

        updateZoomLevel();

        if (_currentView == ISettings.VIEW_D_DAY) {
            _mainCalendar = (Calendar) _dDayCalendar.clone();
        }

        // by default, set today's date
        setDate(_mainCalendar, true, false);

        // we don't really need a layout, but set one in any case  
        setLayout(new FillLayout());

        initColors();
        initListeners();

        // last but not least, update the scrollbars post-first-draw (otherwise we don't know jack about nothing as far as client area etc goes)
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                if (isDisposed()) return;

                updateVerticalScrollBar(false);
                updateHorizontalScrollbar();
            }
        });
    }

    private void initListeners() {
        // without this there'd be nothing!
        addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent event) {
                GanttComposite.this.repaint(event);
            }
        });

        addMouseListener(this);
        addMouseMoveListener(this);
        addMouseTrackListener(this);
        addKeyListener(this);

        Listener mMouseWheelListner = new Listener() {
            public void handleEvent(Event event) {
                if (!_settings.enableZooming()) { return; }

                if (event.stateMask == _settings.getZoomWheelModifierKey()) {
                    _showZoomHelper = _settings.showZoomLevelBox();

                    if (event.count > 0) {
                        zoomIn(_settings.showZoomLevelBox(), true, new Point(event.x, event.y));
                    } else {
                        zoomOut(_settings.showZoomLevelBox(), true, new Point(event.x, event.y));
                    }
                } else {
                    // Linux doesn't honor scrollwheel vs. scrollbar in the same way that OS X / Windows does
                    // so we actually need to force it manually. I have no idea why, but it works fine this way.
                    if (osType == OS_LINUX || _settings.forceMouseWheelVerticalScroll()) {
                        vScroll(event);
                    }
                }
            }
        };

        addListener(SWT.MouseWheel, mMouseWheelListner);
        // on Windows we want to kill any tooltip when alt is pressed, as it may
        // be an indicator we're switching
        // programs, and tooltips usually die on MouseMove or MouseUp
        addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent event) {
                if (event.stateMask == SWT.ALT || event.keyCode == SWT.ALT) {
                    killDialogs();
                }
            }

            public void keyReleased(KeyEvent event) {
                if (_tracker != null) {
                    _tracker.dispose();
                    _tracker = null;
                    _mouseIsDown = false;
                }
                if (_showZoomHelper) {
                    _showZoomHelper = false;
                    // redraw the area where it was
                    if (_zoomLevelHelpArea != null) {
                        redraw(_zoomLevelHelpArea.x, _zoomLevelHelpArea.y, _zoomLevelHelpArea.width + 1, _zoomLevelHelpArea.height + 1, false);
                    }
                }
            }
        });

        // viewport handler must be created before scrollbar handler
        _viewPortHandler = new ViewPortHandler(this);
        // horizontal scrollbar handler, deals with all scrollbar movements, scrollbar types, etc
        _hScrollHandler = new HorizontalScrollbarHandler(this, getHorizontalBar(), _style);

        _verticalScrollBar = getVerticalBar();
        _verticalScrollBar.setPageIncrement(_settings.getEventHeight() + _settings.getEventSpacer());
        _verticalScrollBar.setIncrement(_settings.getEventHeight());
        _verticalScrollBar.setVisible(false);
        _verticalScrollBar.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                vScroll(e);
            }
        });

        addListener(SWT.Resize, new Listener() {
            public void handleEvent(Event e) {
                handleResize(false);
                updateHorizontalScrollbar();

                // redraw last
                Display.getDefault().asyncExec(new Runnable() {
                    public void run() {
                        if (isDisposed()) { return; }

                        redraw();
                    }
                });
            }
        });

    }

    private void vScroll(Event e) {
        // end of drag, kill dialogs
        if (e != null && e.detail == 0) {
            killDialogs();
            return;
        }

        // this has got to be a SWT bug, a non-visible scrollbar can report scroll events!
        if (!_verticalScrollBar.isVisible()) {
            _verticalScrollPosition = 0;
            return;
        }

        int vSelection = _verticalScrollBar.getSelection();

        _verticalScrollPosition = vSelection;
        int diff = _verticalScrollPosition - _lastVerticalScrollPosition;
        _lastVerticalScrollPosition = _verticalScrollPosition;

        // move all events the fast way. There is truly no reason to recalculate bounds for any event, all we need to do is move them
        // according to the scroll bar, which is way way faster than recalculating.
        moveYBounds(diff);
        showVscrollInfo();

        _reCalculateSectionBounds = true;
        redraw();
    }

    void updateVerticalScrollBar(boolean redraw) {
        handleResize(redraw);
    }

    void handleResize(boolean redraw) {
        Rectangle rect = getBounds();
        Rectangle client = getClientArea();
        _verticalScrollBar.setMaximum(rect.height);
        _verticalScrollBar.setPageIncrement(15);
        _verticalScrollBar.setThumb(Math.min(rect.height, client.height));
        int vPage = rect.height - client.height;
        int vSelection = _verticalScrollBar.getSelection();
        if (vSelection >= vPage) {
            if (vPage <= 0) vSelection = 0;
            _origin.y = -vSelection;
        }

        // TODO: Do fancy thing where client area moves with the resize.. (low priority)

        if (_bottomMostY < getClientArea().height) {
            // if we reached the end, make sure we're back at the top
            if (_verticalScrollBar.isVisible()) {
                _verticalScrollBar.setVisible(false);
                if (_verticalScrollPosition != 0) {
                    moveYBounds(-_verticalScrollPosition);
                    _verticalScrollPosition = 0;
                }
            }
        } else {
            _verticalScrollBar.setVisible(true);
        }

        if (_endCalendar != null) {
            updateEventVisibilities(client);
        }

        _reCalculateSectionBounds = true;

        if (redraw) {
            redraw();
        }
    }

    private void initColors() {
        _lineTodayColor = _colorManager.getTodayLineColor();
        _lineColor = _colorManager.getLineColor();
        _textColor = _colorManager.getTextColor();
        _saturdayTextColor = _colorManager.getSaturdayTextColor();

        _saturdayBackgroundColorTop = _colorManager.getSaturdayBackgroundColorTop();
        _saturdayBackgroundColorBottom = _colorManager.getSaturdayBackgroundColorBottom();
        _sundayBackgroundColorTop = _colorManager.getSundayBackgroundColorTop();
        _sundayBackgroundColorBottom = _colorManager.getSundayBackgroundColorBottom();

        _weekdayTextColor = _colorManager.getWeekdayTextColor();
        _sundayTextColor = _colorManager.getSundayTextColor();
        _saturdayTextColor = _colorManager.getSaturdayTextColor();
        _weekdayBackgroundColorTop = _colorManager.getWeekdayBackgroundColorTop();
        _weekdayBackgroundColorBottom = _colorManager.getWeekdayBackgroundColorBottom();
        _lineWeekDividerColor = _colorManager.getWeekDividerLineColor();
        _textHeaderBackgroundColorTop = _colorManager.getTextHeaderBackgroundColorTop();
        _textHeaderBackgroundColorBottom = _colorManager.getTextHeaderBackgroundColorBottom();
        _timeHeaderBackgroundColorTop = _colorManager.getTimeHeaderBackgroundColorTop();
        _timeHeaderBackgroundColorBottom = _colorManager.getTimeHeaderBackgroundColorBottom();
        _phaseHeaderBackgroundColorTop = _colorManager.getPhaseHeaderBackgroundColorTop();
        _phaseHeaderBackgroundColorBottom = _colorManager.getPhaseHeaderBackgroundColorBottom();
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
        return (Calendar) _mainCalendar.clone();
    }

    Calendar getRootCalendar() {
        return _mainCalendar;
    }

    Calendar getRootEndCalendar() {
        return _endCalendar;
    }

    /**
     * Hides all layers of the given value and redraws the event area.
     * 
     * @param layer Layer to hide.
     */
    public void hideLayer(int layer) {
        if (!_hiddenLayers.contains(new Integer(layer))) {
            _hiddenLayers.add(new Integer(layer));
        }
    }

    /**
     * Shows all layers of the given value and redraws the event area.
     * 
     * @param layer Layer to show.
     */
    public void showLayer(int layer) {
        boolean removed = _hiddenLayers.remove(new Integer(layer));
        if (removed) {
            redrawEventsArea();
        }
    }

    // why don't controls have this already? such a simple method
    void redraw(Rectangle rect) {
        redraw(rect.x, rect.y, rect.width, rect.height, false);
    }

    /**
     * Shows all layers and redraws the event area.
     */
    public void showAllLayers() {
        if (_hiddenLayers.size() == 0) return;

        _hiddenLayers.clear();
        redrawEventsArea();
    }

    /**
     * Hides all layers and redraws the event area.
     */
    public void hideAllLayers() {
        for (int i = 0; i < _ganttEvents.size(); i++) {
            GanttEvent ge = (GanttEvent) _ganttEvents.get(i);
            if (!_hiddenLayers.contains(new Integer(ge.getLayer()))) _hiddenLayers.add(new Integer(ge.getLayer()));
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
    public void setLayerOpacity(int layer, int opacity) {

        int toSet = opacity;

        if (toSet >= 255) {
            _layerOpacityMap.remove(new Integer(layer));
            return;
        }

        if (toSet < 0) toSet = 0;

        _layerOpacityMap.put(new Integer(layer), new Integer(toSet));
        redrawEventsArea();
    }

    /**
     * Returns the layer opacity for a layer.
     * 
     * @param layer Layer to get opacity for
     * @return Layer opacity, -1 if layer has no opacity set.
     */
    public int getLayerOpacity(int layer) {
        if (_layerOpacityMap.get(new Integer(layer)) == null) return -1;

        return ((Integer) _layerOpacityMap.get(new Integer(layer))).intValue();
    }

    /**
     * Setting a fixed row height override causes all rows to be the set height regardless of individual row heights set
     * on items themselves and all settings.
     * 
     * @param height Height to set. Set to zero to turn off.
     */
    public void setFixedRowHeightOverride(int height) {
        _fixedRowHeight = height;
    }

    /**
     * Setting a fixed event spacer overrides all individual event space settings on chart items and all settings.
     * 
     * @param height Height to set. Set to zero to turn off.
     */
    public void setEventSpacerOverride(int height) {
        _eventSpacer = height;
    }

    /**
     * Setting this to true will force horizontal lines to draw despite what may be set in the settings.
     * 
     * @param drawHorizontal true to draw horizontal lines.
     */
    public void setDrawHorizontalLinesOverride(boolean drawHorizontal) {
        _drawHorizontalLines = drawHorizontal;
    }

    /**
     * Setting this to true will force vertical lines to draw despite what may be set in the settings.
     * 
     * @param drawVertical true to draw vertical lines.
     */
    public void setDrawVerticalLinesOverride(boolean drawVertical) {
        _drawVerticalLines = drawVertical;
    }

    /**
     * Sets the selection to be a specific GanttEvent. This method will cause a redraw.
     * 
     * @param ge GanttEvent to select
     */
    public void setSelection(GanttEvent ge) {
        _selectedEvents.clear();
        _selectedEvents.add(ge);
        redrawEventsArea();
    }

    /**
     * Sets the selection to be a set of GanttEvents. If the chart is set to <code>SWT.SINGLE</code> you should be using
     * {@link #setSelection(GanttEvent)} as this method will do nothing. This method will cause a redraw.
     * 
     * @param list List of GanttEvents to select
     */
    public void setSelection(ArrayList list) {
        if (!_multiSelect) return;

        _selectedEvents.clear();
        _selectedEvents.addAll(list);
        redrawEventsArea();
    }

    /**
     * Adds a GanttGroup to the chart.
     * 
     * @param group Group to add
     */
    public void addGroup(GanttGroup group) {
        if (!_ganttGroups.contains(group)) _ganttGroups.add(group);

        eventNumbersChanged();
    }

    /**
     * Returns a list of all GanttGroups.
     * 
     * @return List of GanttGroups
     */
    public List getGroups() {
        return _ganttGroups;
    }

    /**
     * Removes a GanttGroup from the chart.
     * 
     * @param group Group to remove
     */
    public void removeGroup(GanttGroup group) {
        _ganttGroups.remove(group);

        eventNumbersChanged();
    }

    /**
     * Adds a GanttSection to the chart.
     * 
     * @param section Section to add
     */
    public void addSection(GanttSection section) {
        if (!_ganttSections.contains(section)) _ganttSections.add(section);

        eventNumbersChanged();
    }

    /**
     * Removes a GanttSection from the chart.
     * 
     * @param section Section to remove
     */
    public void removeSection(GanttSection section) {
        _ganttSections.remove(section);

        eventNumbersChanged();
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
        if (_settings.showDateTips() && _settings.showDateTipsOnScrolling()) {
            String str = DateHelper.getDate(_mainCalendar, _currentView == ISettings.VIEW_DAY ? _settings.getHourDateFormat() : _settings.getDateFormat(), _defaultLocale);
            GC gc = new GC(this);
            Point ext = gc.stringExtent(str);
            gc.dispose();

            int bottomY = _mainBounds.height - ext.y - 12;
            int bottomX = _mainBounds.x + (_mainBounds.width / 2) - ext.x;

            Point p = toDisplay(bottomX, bottomY);
            GanttDateTip.makeDialog(_colorManager, str, p, bottomY);
        }
    }

    // vertical scrollbar info
    private void showVscrollInfo() {
        // needs work
        if (true) return;
        /*
        		if (mSettings.showDateTips() && mSettings.showDateTipsOnScrolling()) {
        			// TODO: This doesn't take groups into event, we should get the top/bottom from all sorts of events
        			GanttEvent topEvent = _getTopEvent();
        			GanttEvent bottomEvent = _getBottomEvent();
        			if (topEvent == null) {
        				GanttDateTip.kill();
        				return;
        			}

        			StringBuffer buf = new StringBuffer();
        			buf.append(topEvent.getName());
        			if (bottomEvent != null) {
        				buf.append(" - ");
        				buf.append(bottomEvent.getName());
        			}

        			GC gc = new GC(this);
        			Point ext = gc.stringExtent(buf.toString());
        			gc.dispose();

        			int bottomY = toControl(getDisplay().getCursorLocation()).y;
        			int bottomX = getClientArea().width - ext.x - 12;

        			Point p = toDisplay(bottomX, bottomY);
        			GanttDateTip.makeDialog(mColorManager, buf.toString(), p, bottomY);
        		}*/
    }

  /*  private GanttEvent _getTopEvent() {
        for (int i = 0; i < mGanttEvents.size(); i++) {
            GanttEvent ge = (GanttEvent) mGanttEvents.get(i);
            if (ge.isBoundsSet() && ge.getY() - mVerticalScrollPosition >= getHeaderHeight() - mSettings.getEventsTopSpacer()) { return ge; }
        }

        return null;
    }

    private GanttEvent _getBottomEvent() {
        for (int i = 0; i < mGanttEvents.size(); i++) {
            GanttEvent ge = (GanttEvent) mGanttEvents.get(i);
            if (ge.getY() > mVerticalScrollPosition + mBounds.height - getHeaderHeight() - mSettings.getEventsTopSpacer()) return ge;
        }

        return null;
    }
*/
    /**
     * Returns the topmost visible event in the current view of the chart.
     * 
     * @return GanttEvent or null
     */
    public GanttEvent getTopEvent() {
        for (int i = 0; i < _ganttEvents.size(); i++) {
            GanttEvent ge = (GanttEvent) _ganttEvents.get(i);

            if (ge.getVisibility() == VISIBILITY_VISIBLE) return ge;
        }

        return null;
    }

    /**
     * Returns the bottom most visible event in the current view of the chart.
     * 
     * @return GanttEvent or null
     */
    public GanttEvent getBottomEvent() {
        GanttEvent top = getTopEvent();
        if (top == null) return null;

        boolean start = false;

        for (int i = 0; i < _ganttEvents.size(); i++) {
            GanttEvent ge = (GanttEvent) _ganttEvents.get(i);
            if (i < _ganttEvents.size() - 1) {
                GanttEvent nextEvent = (GanttEvent) _ganttEvents.get(i + 1);

                if (ge == top) {
                    start = true;
                    continue;
                }

                if (!start) continue;

                if (nextEvent.getVisibility() == VISIBILITY_OOB_HEIGHT_BOTTOM) return ge;
            } else {
                return ge;
            }
        }

        return null;
    }

    void setReCaclulateScopes(boolean recalc) {
        _reCalculateScopes = recalc;
    }

    void setReCalculateSectionBounds(boolean recalc) {
        _reCalculateSectionBounds = recalc;
    }

    void killDialogs() {
        if (_settings.showToolTips()) {
            GanttToolTip.kill();
            GanttDateTip.kill();
            AdvancedTooltipDialog.kill();
        }
    }

    // the repaint event, whenever the composite needs to refresh the contents
    private void repaint(PaintEvent event) {
        _paintManager.redrawStarting();
        GC gc = event.gc;
        drawChartOntoGC(gc, null);
    }

    // draws the actual chart.. separated from the repaint event as the
    // getImage() call uses it on a different GC
    // Note: things are in a certain order, as some stuff draws on top of
    // others, so don't move them around unless you want a different effect
    private void drawChartOntoGC(GC gc, Rectangle boundsOverride) {
        // long totaltime1 = System.currentTimeMillis();
        boolean drawSections = hasGanttSections();

        // only reset bottom y if we recalculate it, or we'll lose the vertical scrollbar among other things that update on all redraws
        if (_reCalculateScopes || drawSections) {
            _bottomMostY = 0;
        }

        Rectangle bounds = super.getClientArea();
        if (boundsOverride != null) {
            bounds = boundsOverride;
        }

        // the actual visible bounds counting any vertical scroll offset (includes header height)
        _visibleBounds = new Rectangle(bounds.x, bounds.y + _verticalScrollPosition, bounds.width, bounds.height);

        // if we use sections, our bounds for everything will be the size of the client area minus the section bar on the left
        if (drawSections) {
            if (_settings.getSectionSide() == SWT.LEFT) {
                bounds = new Rectangle(_settings.getSectionBarWidth(), bounds.y, bounds.width - _settings.getSectionBarWidth(), bounds.height);
            } else {
                bounds = new Rectangle(0, bounds.y, bounds.width - _settings.getSectionBarWidth(), bounds.height);
            }
        }

        _mainBounds = new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
        _lockedHeaderY = _mainBounds.y;
        _mainBounds.y -= _verticalScrollPosition;

        // header
        if (_settings.drawHeader()) {
            // we need to draw the header regardless of locked state as it updates the location of the 
            // vertical lines
            drawHeader(gc);
        } else {
            // we need the mDaysVisible value which is normally calculated when we draw boxes, as the header is not drawn here, we need to calculate it manually
            calculateDaysVisible(bounds);
        }

        updateEventVisibilities(_visibleBounds);

        // section drawing needs special treatment as we need to give sub-bounds to the various drawing methods
        if (drawSections) {
            if (_reCalculateSectionBounds) {
                calculateSectionBounds(gc, bounds);
            }

            // if we fill the bottom then fill it!
            if (_settings.drawFillsToBottomWhenUsingGanttSections()) {
                Rectangle extraBounds = new Rectangle(_mainBounds.x, _mainBounds.y + getHeaderHeight() - _verticalScrollPosition, _mainBounds.x + _mainBounds.width, _mainBounds.y + _mainBounds.height - getHeaderHeight() + _verticalScrollPosition);
                drawFills(gc, extraBounds);
                drawVerticalLines(gc, extraBounds, false);
            }

            for (int i = 0; i < _ganttSections.size(); i++) {
                GanttSection gs = (GanttSection) _ganttSections.get(i);
                Rectangle gsBounds = gs.getBounds();

                if (boundsOverride != null) {
                    gsBounds.width = boundsOverride.width;
                }

                if (_reCalculateScopes) {
                    calculateAllScopes(gsBounds, gs);
                }

                drawFills(gc, gsBounds, gs);

                if (hasGanttPhases()) {
                    drawGanttPhases(gc, gsBounds, false, gs);
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
                    drawGanttSpecialDateRanges(gc, gsBounds, gs);
                }

                // draw events
                drawEvents(gc, gsBounds, gs);
            }

            // before we drew connections inside the loop below, which was totally pointless. We only need to draw connections once for the visible area,
            // not once per section. This is way faster, connection drawing is not 0ms
            drawConnections(gc, _mainBounds);

            // just because I have the feeling some user will want cross-section connections, we allow it by
            // drawing the connecting lines _last_. Why? because the event bounds are not calculated until the event is drawn, and if we have
            // a connection to a group/event that hasn't been drawn yet, it would draw an arrow into space..
            for (int i = 0; i < _ganttSections.size(); i++) {
                GanttSection gs = (GanttSection) _ganttSections.get(i);
                _bottomMostY = Math.max(gs.getBounds().y + gs.getBounds().height, _bottomMostY);
            }

            // if we zoom in / out in the middle of a scroll we need to add on where we are to the bottom most y
            //  mBottomMostY += mVerticalScrollPosition;

        } else {
            bounds = new Rectangle(bounds.x, getHeaderHeight(), bounds.width, bounds.height);

            // long start = System.currentTimeMillis();
            if (_reCalculateScopes) {
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
            drawConnections(gc, bounds);
        }

        if (drawSections) {
            drawSectionColumn(gc, bounds, false, false, false, false);
        }

        // if we lock the header, we unfortunately need to draw it again on top of everything else. Down the road this should be optimized of course,
        // but there's so many necessary calculations done in the header drawing that we need for later that it's a bit of work
        if (_settings.lockHeaderOnVerticalScroll() && _settings.drawHeader()) {
            drawHeader(gc);
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
                IGanttEventListener listener = (IGanttEventListener) _eventListeners.get(i);
                listener.lastDraw(gc);
            }
        }

        // by default these are on, we flag them off when we know for sure we don't need to recalculate bounds
        _reCalculateScopes = false;
        _reCalculateSectionBounds = false;

        if (_zoomLevelChanged) {
            _zoomLevelChanged = false;
            updateHorizontalScrollbar();

            // on zoom level change we update the position, as otherwise the next prev/next horizontal bar click will make it check against
            // the previous zoom level value, which is usually way off, and the entire chart jumps a huge distance which is obviously really bad.
            // as the zoom level has changed, all we do is to say "update the scrollbar, set the new selection position to what it is now after the update"
            // which solves the issue
            _hScrollHandler.resetScrollPosition();
        }

        if (_forceScrollbarsUpdate) {
            updateVerticalScrollBar(true);
            updateHorizontalScrollbar();
            _forceScrollbarsUpdate = false;
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
    public void setShowPlannedDates(boolean showPlanned) {
        _showPlannedDates = showPlanned;
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
    public void setShowDaysOnEvents(boolean showDates) {
        _showNumberOfDaysOnChart = showDates;
        redraw();
    }

    /**
     * Returns whether event day number drawing is currently on or off.
     * 
     * @return true if on
     */
    public boolean isShowingDaysOnEvents() {
        return _showNumberOfDaysOnChart;
    }

    /**
     * Whether the chart has gantt sections or not
     * 
     * @return true if has gantt sections
     */
    public boolean isShowingGanttSections() {
        return !_ganttSections.isEmpty();
    }

    private void drawHeader(GC gc) {
        _verticalLineLocations.clear();
        _verticalWeekDividerLineLocations.clear();

        Rectangle headerBounds = new Rectangle(_mainBounds.x, _mainBounds.y, _mainBounds.width, _mainBounds.height);
        if (_settings.lockHeaderOnVerticalScroll()) {
            headerBounds.y = _lockedHeaderY;
        }

        // draw phases header (but not above normal header)
        if (hasGanttPhases()) {
            drawGanttPhases(gc, headerBounds, true, null);
        }

        if (_currentView == ISettings.VIEW_DAY) {
            // draw the day at the top
            drawHourTopBoxes(gc, headerBounds);
            // draw the hour ticks below
            drawHourBottomBoxes(gc, headerBounds);
        } else if (_currentView == ISettings.VIEW_WEEK) {
            // draw the week boxes
            drawWeekTopBoxes(gc, headerBounds);
            // draw the day boxes
            drawWeekBottomBoxes(gc, headerBounds);
        } else if (_currentView == ISettings.VIEW_MONTH) {
            // draws the month boxes at the top
            drawMonthTopBoxes(gc, headerBounds);
            // draws the monthly "week" boxes
            drawMonthBottomBoxes(gc, headerBounds);
        } else if (_currentView == ISettings.VIEW_YEAR) {
            // draws the years at the top
            drawYearTopBoxes(gc, headerBounds);
            // draws the month boxes
            drawYearBottomBoxes(gc, headerBounds);
        } else if (_currentView == ISettings.VIEW_D_DAY) {
            // draw D-day stuff, for Darren and anyone else who needs it
            drawDDayTopBoxes(gc, headerBounds);
            // draw the bottom d-day
            drawDDayBottomBoxes(gc, headerBounds);
        }

        // draws the horizontal (usually black) lines at the top to make
        // things stand out a little
        drawTopHorizontalLines(gc, headerBounds);
    }

    private void showMenu(int x, int y, GanttEvent event, final MouseEvent me) {
        if (!_settings.showMenuItemsOnRightClick()) return;

        killDialogs();

        // destroy old menu as to not leak memory or at least GC faster
        if (_rightClickMenu != null && !_rightClickMenu.isDisposed()) _rightClickMenu.dispose();

        _rightClickMenu = new Menu(Display.getDefault().getActiveShell(), SWT.POP_UP);

        if (event != null) {
            // We can't use JFace actions.. so we need to make copies.. Dirty
            // but at least not reinventing a wheel (as much)
            Menu eventMenu = event.getMenu();
            MenuItem[] items = eventMenu.getItems();

            if (items != null) {
                for (int i = 0; i < items.length; i++) {
                    final MenuItem mItem = items[i];

                    MenuItem copy = new MenuItem(_rightClickMenu, SWT.PUSH);
                    copy.setText(mItem.getText());
                    copy.setImage(mItem.getImage());
                    copy.setEnabled(mItem.getEnabled());
                    copy.setAccelerator(mItem.getAccelerator());
                    copy.setData(mItem.getData());
                    copy.setSelection(mItem.getSelection());
                    copy.addListener(SWT.Selection, new Listener() {
                        public void handleEvent(Event event) {
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

            if (items != null && items.length > 0) new MenuItem(_rightClickMenu, SWT.SEPARATOR);
        }

        if (_settings.enableZooming()) {
            // new MenuItem(menu, SWT.SEPARATOR);
            MenuItem zoomIn = new MenuItem(_rightClickMenu, SWT.PUSH);
            zoomIn.setText(_languageManager.getZoomInMenuText());
            MenuItem zoomOut = new MenuItem(_rightClickMenu, SWT.PUSH);
            zoomOut.setText(_languageManager.getZoomOutMenuText());
            MenuItem zoomReset = new MenuItem(_rightClickMenu, SWT.PUSH);
            zoomReset.setText(_languageManager.getZoomResetMenuText());

            zoomIn.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    zoomIn(_settings.showZoomLevelBox());
                }
            });
            zoomOut.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    zoomOut(_settings.showZoomLevelBox());
                }
            });
            zoomReset.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    resetZoom();
                }
            });

            new MenuItem(_rightClickMenu, SWT.SEPARATOR);
        }

        final MenuItem showPlaque = new MenuItem(_rightClickMenu, SWT.CHECK);
        showPlaque.setText(_languageManager.getShowNumberOfDaysOnEventsMenuText());
        showPlaque.setSelection(_showNumberOfDaysOnChart);
        showPlaque.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                _showNumberOfDaysOnChart = !_showNumberOfDaysOnChart;
                showPlaque.setSelection(_showNumberOfDaysOnChart);
                redraw();
            }
        });

        new MenuItem(_rightClickMenu, SWT.SEPARATOR);

        final MenuItem showEsts = new MenuItem(_rightClickMenu, SWT.CHECK);
        showEsts.setText(_languageManager.getShowPlannedDatesMenuText());
        showEsts.setSelection(_showPlannedDates);
        showEsts.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                _showPlannedDates = !_showPlannedDates;
                showEsts.setSelection(_showPlannedDates);
                redraw();
            }
        });

        new MenuItem(_rightClickMenu, SWT.SEPARATOR);

        final MenuItem showThreeDee = new MenuItem(_rightClickMenu, SWT.CHECK);
        showThreeDee.setText(_languageManager.get3DMenuText());
        showThreeDee.setSelection(_threeDee);
        showThreeDee.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                _threeDee = !_threeDee;
                redraw();
            }
        });

        if (_settings.showDeleteMenuOption() && event != null) {
            new MenuItem(_rightClickMenu, SWT.SEPARATOR);

            MenuItem delete = new MenuItem(_rightClickMenu, SWT.PUSH);
            delete.setText(_languageManager.getDeleteMenuText());
            delete.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event e) {
                    if (_selectedEvents.size() > 0) {
                        for (int i = 0; i < _eventListeners.size(); i++) {
                            IGanttEventListener listener = (IGanttEventListener) _eventListeners.get(i);
                            listener.eventsDeleteRequest(_selectedEvents, me);
                        }
                    }
                }
            });
        }

        if (_settings.showPropertiesMenuOption() && _selectedEvents.size() > 0) {
            new MenuItem(_rightClickMenu, SWT.SEPARATOR);

            MenuItem properties = new MenuItem(_rightClickMenu, SWT.PUSH);
            properties.setText(_languageManager.getPropertiesMenuText());
            properties.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    if (_selectedEvents.size() > 0) {
                        for (int i = 0; i < _eventListeners.size(); i++) {
                            IGanttEventListener listener = (IGanttEventListener) _eventListeners.get(i);
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
    public List getSelectedEvents() {
        checkWidget();
        return _selectedEvents;
    }

    /**
     * Sets the top visible item in the chart and scrolls to show it.
     * 
     * @param ge Event to show
     * @param yOffset y offset modifier
     * @param side one of <code>SWT.LEFT</code>, <code>SWT.CENTER</code>, <code>SWT.RIGHT</code>
     */
    public void setTopItem(GanttEvent ge, int yOffset, int side) {
        // TODO: Take fixed header into account
        vScrollToY(ge.getY() + yOffset, false);
        internalSetDate(ge.getActualStartDate(), side, true, false);
        redraw();
    }

    /**
     * Sets the top visible item in the chart and scrolls to show it.
     * 
     * @param ge Event to show
     * @param side one of <code>SWT.LEFT</code>, <code>SWT.CENTER</code>, <code>SWT.RIGHT</code>
     */
    public void setTopItem(GanttEvent ge, int side) {
        setTopItem(ge, 0, side);
    }

    /**
     * Scrolls the chart to the selected item regardless if it is visible or not.
     * 
     * @param ge GanttEvent to scroll to.
     * @param side one of <code>SWT.LEFT</code>, <code>SWT.CENTER</code>, <code>SWT.RIGHT</code>
     */
    public void showEvent(GanttEvent ge, int side) {
        // TODO: Side doesn't work.. missing something
        if (ge.getActualStartDate() == null) return;

        vScrollToY(ge.getY(), false);
        internalSetDate(ge.getActualStartDate(), side, true, false);
        _reCalculateScopes = true;
        _reCalculateSectionBounds = true;
        redraw();
    }

    // moves the chart to the given y position, takes various spacers into account
    private void vScrollToY(int y, boolean redraw) {
        if (_settings.lockHeaderOnVerticalScroll()) {
            y -= getHeaderHeight();
        } else {
            y -= (_mainBounds.y - getHeaderHeight());
        }

        // we need to take the "previous" scroll location into account
        y += _verticalScrollPosition;

        // y -= mLastVerticalScrollPosition;

        int max = _verticalScrollBar.getMaximum() - _verticalScrollBar.getThumb();

        if (y < 0) y = 0;
        if (y > max) y = max;

        _verticalScrollPosition = y;
        _lastVerticalScrollPosition = _verticalScrollPosition;
        _verticalScrollBar.setSelection(y);

        // moveYBounds(y);

        _reCalculateSectionBounds = true;
        flagForceFullUpdate();

        if (redraw) redraw();
    }

    public Rectangle getBounds() {
        if (_mainBounds == null) return super.getBounds();

        return new Rectangle(0, 0, super.getBounds().width, _bottomMostY);
    }

    // calculates all gantt section bounds
    private void calculateSectionBounds(GC gc, Rectangle bounds) {
        int lineLoc = getHeaderHeight() == 0 ? bounds.y : (bounds.y + getHeaderHeight());
        int yStart = lineLoc;

        for (int i = 0; i < _ganttSections.size(); i++) {
            GanttSection gs = (GanttSection) _ganttSections.get(i);

            Point extent = null;
            if (gs.needsNameUpdate() || gs.getNameExtent() == null) {
                extent = gc.stringExtent(gs.getName());
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
            int gsHeight = gs.getEventsHeight(_settings);
            int height = Math.max(gsHeight, strHeight);

            // if the name of the section is so large that it basically defines the size of the section, space out the text slightly
            if (strHeight > gsHeight) {
                height += _settings.getSectionTextSpacer();
            }

            Rectangle gsBounds = new Rectangle(bounds.x, yStart, bounds.width, height);
            gs.setBounds(gsBounds);

            yStart += height - 1;
            yStart += _settings.getSectionBarDividerHeight();
        }
    }

    // calculates days visible
    private void calculateDaysVisible(final Rectangle bounds) {
        Calendar temp = Calendar.getInstance(_defaultLocale);
        temp.setTime(_mainCalendar.getTime());

        int current = 0;

        while (true) {
            temp.add(Calendar.DATE, 1);
            current += _dayWidth;

            _daysVisible++;

            if (current > bounds.width) {
                break;
            }
        }
    }

    // the height of the header or zero if not visible
    private int getHeaderHeight() {
        if (!_settings.drawHeader()) {
            if (hasGanttPhases()) { return _settings.getPhasesHeaderHeight(); }
            return 0;
        }

        // we account for the drawTopLines bottom line which is at the bottom of the header, which increases our height by 1 pixel
        int ret = _settings.getHeaderDayHeight() + _settings.getHeaderMonthHeight() + 1;
        if (hasGanttPhases()) {
            ret += _settings.getPhasesHeaderHeight();
        }

        return ret;
    }

    private void drawFills(GC gc, Rectangle bounds, GanttSection gs) {
        internalDrawFills(gc, bounds, gs);
    }

    // background fills
    private void drawFills(GC gc, Rectangle bounds) {
        internalDrawFills(gc, bounds, null);
    }

    private void internalDrawFills(GC gc, Rectangle bounds, GanttSection gs) {
        int maxX = bounds.width;
        int startX = bounds.x;

        int offset = 0;
        if (gs == null) {
            offset = _verticalScrollPosition;
            if (offset > getHeaderHeight()) offset = getHeaderHeight();
        } else {
            offset = _verticalScrollPosition;
        }

        int startY = bounds.y - offset;// getHeaderHeight() == 0 ? bounds.y : bounds.y + getHeaderHeight() + 1;
        int heightY = bounds.height + offset;

        boolean drawFills = true;
        /*		if (gs != null) {
        			drawFills = !gs.isInheritBackgroud();
        		}
        */
        // fill all of it, then we just have to worry about weekends, much
        // faster in terms of drawing time
        // only two views have weekend colors, week and month view. Hour and
        // year view don't.
        if (drawFills) {
            switch (_currentView) {
                case ISettings.VIEW_DAY:
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
            }
        }

        Calendar temp = Calendar.getInstance(_defaultLocale);
        temp.setTime(_mainCalendar.getTime());

        // fill all of it first, then do weekends
        if (drawFills) {
            gc.setForeground(getDayBackgroundGradient(Calendar.MONDAY, true, gs));
            gc.setBackground(getDayBackgroundGradient(Calendar.MONDAY, false, gs));

            gc.fillGradientRectangle(startX, startY, maxX, heightY, true);
        }

        int dayWidth = (_currentView == ISettings.VIEW_WEEK || _currentView == ISettings.VIEW_D_DAY ? _dayWidth : _monthDayWidth);

        while (true) {
            int day = temp.get(Calendar.DAY_OF_WEEK);

            if (drawFills) {
                if (_selectedHeaderDates.contains(temp)) {
                    gc.setForeground(_colorManager.getSelectedDayColorTop());
                    gc.setBackground(_colorManager.getSelectedDayColorBottom());
                    gc.fillGradientRectangle(startX, startY, dayWidth, heightY, true);
                } else {
                    if (_currentView != ISettings.VIEW_D_DAY) {
                        if ((day == Calendar.SATURDAY || day == Calendar.SUNDAY)) {
                            gc.setForeground(getDayBackgroundGradient(day, true, gs));
                            gc.setBackground(getDayBackgroundGradient(day, false, gs));

                            // fill the whole thing all the way down
                            gc.fillGradientRectangle(startX, startY, dayWidth, heightY, true);
                        }

                        if (DateHelper.isToday(temp, _defaultLocale)) {
                            gc.setForeground(_todayBGColorTop);
                            gc.setBackground(_todayBGColorBottom);
                            gc.fillGradientRectangle(startX + 1, startY, dayWidth - 1, heightY, true);
                        }
                    }
                }
            }

            startX += dayWidth;

            temp.add(Calendar.DATE, 1);

            if (startX > maxX) break;
        }

    }

    // draws the zoom level box in the corner, only shown when zooming
    private void drawZoomLevel(final GC gc) {
        int helpHeight = 19;

        int bottomLeftY = _mainBounds.height - helpHeight - 3;
        int bottomLeftX = _mainBounds.x + 3;

        // if there is no sections where we'd normally draw the zoom box, we can move it in to the left, otherwise we leave it as it should not cover the section bar
        if (_ganttSections.size() > 0 && _bottomMostY < bottomLeftY) bottomLeftX = 3;

        String qText = _languageManager.getZoomLevelText() + ": " + (_zoomLevel == ISettings.MIN_ZOOM_LEVEL ? _languageManager.getZoomMaxText() : (_zoomLevel == ISettings.MAX_ZOOM_LEVEL ? _languageManager.getZoomMinText() : "" + _zoomLevel));
        Point point = gc.stringExtent(qText);

        int helpWidth = point.x + 5;

        Rectangle outer = new Rectangle(bottomLeftX, bottomLeftY, helpWidth, helpHeight);
        Rectangle inner = new Rectangle(bottomLeftX + 1, bottomLeftY + 1, helpWidth - 1, helpHeight - 1);

        _zoomLevelHelpArea = outer;

        gc.setForeground(_colorManager.getZoomBackgroundColorTop());
        gc.setBackground(_colorManager.getZoomBackgroundColorBottom());
        gc.fillGradientRectangle(inner.x, inner.y, inner.width, inner.height, true);

        gc.setForeground(_colorManager.getZoomBorderColor());
        gc.drawRectangle(outer);

        gc.setForeground(_colorManager.getZoomTextColor());
        gc.drawString(qText, bottomLeftX + 4, bottomLeftY + 3, true);
    }

    // draws the section column on left or right side
    private void drawSectionColumn(GC gc, Rectangle bounds, boolean columnOnly, boolean forceUsageOfBounds, boolean drawCornerOnly, boolean force) {
        if (_ganttSections.size() == 0) { return; }

        boolean rightSide = (_settings.getSectionSide() == SWT.RIGHT);

        // don't draw this, it will be force-drawn at the end.. this is a hack to get around the bar
        // drawing in the middle, also speeds it up as we only draw it once
        if (_savingChartImage && rightSide && !force) { return; }

        int xMax = _settings.getSectionBarWidth() - 1;

        int horiSpacer = 3;

        // calculate max width if any section is horizontal
        for (int i = 0; i < _ganttSections.size(); i++) {
            GanttSection gs = (GanttSection) _ganttSections.get(i);
            if (gs.getTextOrientation() == SWT.HORIZONTAL) {
                Point p = null;
                if (gs.needsNameUpdate() || gs.getNameExtent() == null) {
                    p = gc.stringExtent(gs.getName());
                    gs.setNameExtent(p);
                    gs.setNeedsNameUpdate(false);
                } else {
                    p = gs.getNameExtent();
                }

                xMax = Math.max(xMax, p.x + (horiSpacer * 2));
            }
        }

        int lineLoc = getHeaderHeight() == 0 ? bounds.y : (bounds.y + getHeaderHeight());
        int yStart = lineLoc;
        yStart -= _verticalScrollPosition;

        int x = 0;

        if (rightSide) {
            if (forceUsageOfBounds) {
                x = bounds.width - xMax;
            } else {
                x = super.getClientArea().width - xMax;
            }
        } else {
            x = 0;
        }

        int neg = 0;
        if (rightSide) {
            neg = -_settings.getSectionBarWidth();
        }

        GanttSection bottomSection = (GanttSection) _ganttSections.get(_ganttSections.size() - 1);

        if (drawCornerOnly) {
            lineLoc += _verticalScrollPosition;
        }

        // top corner
        gc.setForeground(_colorManager.getNonActiveSessionBarColorLeft());
        gc.setBackground(_colorManager.getNonActiveSessionBarColorRight());
        
        int bottomPos = _settings.drawGanttSectionBarToBottom() ? _mainBounds.y + _mainBounds.height : lineLoc;
        // if we're only drawing a corner, do not draw to the bottom regardless of settings or we'll paint over the text and section markers etc.
        // this fix to bugzilla #304804 - Thanks Wim!
        if (drawCornerOnly) {
            bottomPos = lineLoc;
        }
        
        gc.fillGradientRectangle(x, 0, xMax + 1, bottomPos, false);

        gc.setForeground(_colorManager.getTopHorizontalLinesColor());
        // vertical
        gc.drawLine(x + xMax + neg, 0, x + xMax + neg, bottomSection.getBounds().y + bottomSection.getBounds().height - 1);

        if (!drawCornerOnly) {

            for (int i = 0; i < _ganttSections.size(); i++) {
                GanttSection gs = (GanttSection) _ganttSections.get(i);

                int gsHeight = gs.getBounds().height;

                gc.setForeground(_colorManager.getActiveSessionBarColorLeft());
                gc.setBackground(_colorManager.getActiveSessionBarColorRight());
                gc.fillGradientRectangle(x, yStart, xMax, gsHeight, false);

                gc.setForeground(_textColor);

                if (gs.getTextOrientation() == SWT.VERTICAL) {
                    // draw vertical text (I tried using Transform.rotate stuff here earlier, but it's so incredibly slow that we can't use it)
                    // and not only that, but the kerning on vertical text is completely whacked with letter overlapping, so right now it's unusable
                    Image image = null;
                    int xStart = (xMax / 2);

                    // only create the images if we don't have one from before, or if the name has changed since last time
                    if (gs.getNameImage() == null || gs.needsNameUpdate()) {

                        Point extent = null;
                        if (gs.needsNameUpdate() || gs.getNameExtent() == null) {
                            extent = gc.stringExtent(gs.getName());
                            gs.setNameExtent(extent);
                            gs.setNeedsNameUpdate(false);
                        } else {
                            extent = gs.getNameExtent();
                        }

                        // zero length name causes exceptions, set it to 1
                        if (extent.x == 0) {
                            extent.x = 1;
                        }
                        Image textImage = new Image(getDisplay(), extent.x, xMax - 2);
                        GC gcTemp = new GC(textImage);

                        if (rightSide) {
                            gcTemp.setForeground(_colorManager.getActiveSessionBarColorRight());
                            gcTemp.setBackground(_colorManager.getActiveSessionBarColorLeft());
                        } else {
                            gcTemp.setForeground(_colorManager.getActiveSessionBarColorLeft());
                            gcTemp.setBackground(_colorManager.getActiveSessionBarColorRight());
                        }
                        gcTemp.fillGradientRectangle(0, 0, extent.x, xMax - 2, true);
                        gcTemp.setForeground(_textColor);
                        gcTemp.drawString(gs.getName(), 0, 0, true);
                        gcTemp.dispose();

                        ImageData id = textImage.getImageData();
                        image = new Image(getDisplay(), rotate(id, rightSide ? SWT.RIGHT : SWT.LEFT));
                        gs.setNameImage(image);
                    } else {
                        image = gs.getNameImage();
                    }

                    xStart -= (image.getBounds().width / 2) - 2;
                    int textLocY = (gsHeight / 2) - (image.getBounds().height / 2);

                    gc.drawImage(image, x + xStart - 1, yStart + textLocY);
                } else if (gs.getTextOrientation() == SWT.HORIZONTAL) {
                    gc.drawString(gs.getName(), horiSpacer, yStart + (gsHeight / 2) - (gs.getNameExtent().y / 2), true);
                }

                yStart += gsHeight - 1;

                int width = bounds.x + bounds.width;
                if (rightSide && !forceUsageOfBounds) {
                    width = super.getClientArea().width;
                }

                // draw center divider
                if (!columnOnly) {
                    gc.setForeground(_colorManager.getTopHorizontalLinesColor());

                    //System.out.println(bounds + " " + width);

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
                        gc.drawLine(0, yStart, width, yStart);
                        yStart += _settings.getSectionBarDividerHeight();
                    }
                }
            }

        }

        gc.setForeground(_colorManager.getTopHorizontalLinesColor());
        // horizontal
        if (_settings.drawHeader()) {
            gc.drawLine(x, bounds.y, x + xMax, bounds.y);
        }

        gc.drawLine(x, lineLoc - _verticalScrollPosition, x + xMax, lineLoc - _verticalScrollPosition);

    }

    private void drawHorizontalLines(GC gc, Rectangle bounds) {
        gc.setForeground(_lineColor);

        List usedGroups = new ArrayList();

        for (int i = 0; i < _ganttEvents.size(); i++) {
            GanttEvent ge = (GanttEvent) _ganttEvents.get(i);
            // could be a feature, although a lame one, if this is enabled, only visible events horizontal lines will be drawn
            // if (!ge.getBounds().intersects(bounds))
            // continue;

            if (ge.isHidden()) {
                continue;
            }

            int yExtra = ge.isAutomaticRowHeight() ? 0 : ge.getFixedRowHeight();
            yExtra -= _verticalScrollPosition;

            if (ge.getGanttGroup() != null) {
                if (usedGroups.contains(ge.getGanttGroup())) {
                    continue;
                } else {
                    usedGroups.add(ge.getGanttGroup());
                }
            }

            if (ge.isAutomaticRowHeight()) {
                yExtra += (_eventSpacer / 2);
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
    private void drawVerticalLines(GC gc, Rectangle bounds, boolean applyVscroll) {
        // int xMax = bounds.width + bounds.x;
        // space it out 1 or more or else it will draw over the bottom horizontal line of the header
        int yStart = bounds.y - (applyVscroll ? _verticalScrollPosition : 0);
        int height = bounds.height + yStart - 1 + (applyVscroll ? _verticalScrollPosition : 0);

        if (_currentView == ISettings.VIEW_WEEK || _currentView == ISettings.VIEW_MONTH || _currentView == ISettings.VIEW_DAY || _currentView == ISettings.VIEW_D_DAY) {
            // normal day lines
            gc.setForeground(_lineColor);
            for (int i = 0; i < _verticalLineLocations.size(); i++) {
                int current = ((Integer) _verticalLineLocations.get(i)).intValue();

                gc.drawLine(current, yStart, current, height);
            }

            // "weekend" lines
            gc.setForeground(_lineWeekDividerColor);
            Object[] weekLocs = _verticalWeekDividerLineLocations.toArray();

            if (_useAlpha) {
                gc.setAlpha(_colorManager.getWeekDividerAlpha());
            }
            for (int i = 0; i < weekLocs.length; i++) {
                int current = ((Integer) weekLocs[i]).intValue();
                gc.drawLine(current, yStart, current, height);
            }
            if (_useAlpha) {
                gc.setAlpha(255);
                gc.setAdvanced(false);
            }

            Calendar today = Calendar.getInstance(_defaultLocale);
            drawTodayLine(gc, bounds, getStartingXfor(today), today.get(Calendar.DAY_OF_WEEK));
        } else if (_currentView == ISettings.VIEW_YEAR) {
            for (int i = 0; i < _verticalLineLocations.size(); i++) {
                gc.setForeground(_lineWeekDividerColor);

                int x = ((Integer) _verticalLineLocations.get(i)).intValue();
                gc.drawLine(x, yStart, x, height);
            }

            Calendar today = Calendar.getInstance(_defaultLocale);
            drawTodayLine(gc, bounds, getStartingXfor(today), today.get(Calendar.DAY_OF_WEEK));
        }
    }

    // year
    private void drawYearBottomBoxes(GC gc, Rectangle bounds) {
        int xMax = bounds.width + bounds.x;
        int current = bounds.x;
        int topY = bounds.y + _settings.getHeaderMonthHeight();
        int heightY = _settings.getHeaderDayHeight();
        _daysVisible = 0;

        Calendar temp = Calendar.getInstance(_defaultLocale);
        temp.setTime(_mainCalendar.getTime());

        temp.set(Calendar.DAY_OF_MONTH, 1);

        gc.setForeground(_textHeaderBackgroundColorTop);
        gc.setBackground(_textHeaderBackgroundColorBottom);
        gc.fillGradientRectangle(0, topY, xMax, heightY, true);

        gc.setBackground(_weekdayBackgroundColorTop);

        while (true) {
            if (temp.get(Calendar.DATE) == 1) {
                gc.setForeground(_colorManager.getYearTimeDividerColor());
                gc.drawLine(current, topY, current, topY + heightY);
                _verticalLineLocations.add(new Integer(current));

                gc.setForeground(_textColor);
                gc.drawString(getDateString(temp, false), current + 4, topY + 3, true);

            }

            int monthMax = temp.getActualMaximum(Calendar.DATE);
            _daysVisible += monthMax;
            temp.add(Calendar.MONTH, 1);
            current += (_yearDayWidth * monthMax);

            if (current > xMax) {
                _endCalendar = temp;
                break;
            }
        }
    }

    // year
    private void drawYearTopBoxes(GC gc, Rectangle bounds) {
        int xMax = bounds.width + bounds.x;
        int current = bounds.x;
        int topY = bounds.y;
        int bottomY = _settings.getHeaderMonthHeight();

        Calendar temp = Calendar.getInstance(_defaultLocale);
        temp.setTime(_mainCalendar.getTime());

        temp.set(Calendar.DAY_OF_MONTH, 1);

        gc.setForeground(_textHeaderBackgroundColorTop);
        gc.setBackground(_textHeaderBackgroundColorBottom);
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

            int monthMax = temp.getActualMaximum(Calendar.DAY_OF_MONTH);
            _daysVisible += monthMax;
            temp.add(Calendar.DATE, monthMax);
            current += (_yearDayWidth * monthMax);

            if (current > xMax) {
                break;
            }
        }
    }

    // month
    private void drawMonthBottomBoxes(GC gc, Rectangle bounds) {
        int xMax = bounds.width + bounds.x;
        int current = bounds.x;
        int topY = bounds.y + _settings.getHeaderMonthHeight();
        int heightY = _settings.getHeaderDayHeight();

        // get the offset to draw things at
        Calendar temp = Calendar.getInstance(_defaultLocale);
        temp.setTime(_mainCalendar.getTime());
        Calendar temp2 = Calendar.getInstance(_defaultLocale);
        temp2.setTime(_mainCalendar.getTime());
        temp2.set(Calendar.DAY_OF_WEEK, temp.getFirstDayOfWeek());
        int days = (int) DateHelper.daysBetween(temp, temp2, _defaultLocale);
        current += days * _monthDayWidth;

        temp.set(Calendar.DAY_OF_WEEK, temp.getFirstDayOfWeek());

        while (true) {
            int curDay = temp.get(Calendar.DAY_OF_WEEK);

            _verticalLineLocations.add(new Integer(current));
            // only change dates when week changes, as we don't change date
            // string for every different day
            if (curDay == temp.getFirstDayOfWeek()) {
                _verticalWeekDividerLineLocations.add(new Integer(current));
                gc.setForeground(_colorManager.getMonthTimeDividerColor());
                gc.drawRectangle(current, topY, _monthWeekWidth, heightY);
                gc.setForeground(_textHeaderBackgroundColorTop);
                gc.setBackground(_textHeaderBackgroundColorBottom);
                gc.fillGradientRectangle(current + 1, topY + 1, _monthWeekWidth - 1, heightY - 1, true);
                gc.setForeground(_textColor);

                gc.drawString(getDateString(temp, false), current + 4, topY + 3, true);
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
    private void drawMonthTopBoxes(GC gc, Rectangle bounds) {
        int xMax = bounds.width + bounds.x;
        int current = bounds.x;
        int topY = bounds.y;
        int bottomY = _settings.getHeaderMonthHeight();
        _daysVisible = 0;

        gc.setForeground(_textHeaderBackgroundColorTop);
        gc.setBackground(_textHeaderBackgroundColorBottom);
        gc.fillGradientRectangle(current, topY + 1, xMax, bottomY - 1, true);

        // get the offset to draw things at
        Calendar temp = Calendar.getInstance(_defaultLocale);
        temp.setTime(_mainCalendar.getTime());
        Calendar temp2 = Calendar.getInstance(_defaultLocale);
        temp2.setTime(_mainCalendar.getTime());
        temp2.set(Calendar.DAY_OF_WEEK, temp.getFirstDayOfWeek());
        int days = (int) DateHelper.daysBetween(temp, temp2, _defaultLocale);
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
    private void drawWeekTopBoxes(GC gc, Rectangle bounds) {
        int xMax = bounds.width + bounds.x;
        int current = bounds.x;
        int topY = bounds.y;
        int bottomY = _settings.getHeaderMonthHeight();

        Calendar temp = Calendar.getInstance(_defaultLocale);
        temp.setTime(_mainCalendar.getTime());

        // if we're not on a sunday, the weekbox will be shorter, so check how
        // much shorter
        // sun = 1, mon = 2, tue = 3, wed = 4, thu = 5, fri = 6, sat = 7
        int day = temp.get(Calendar.DAY_OF_WEEK);
        int firstDayOfWeek = temp.getFirstDayOfWeek();
        int dayOffset = firstDayOfWeek - day;

        // BUGFIX Sep 12/07:
        // On international dates that have getFirstDayOfWeek() on other days
        // than Sundays, we need to do some magic to these
        // or the dates printed will be wrong. As such, if the offset is
        // positive, we simply toss off 7 days to get the correct number.
        // we want a negative value in the end.
        if (dayOffset > 0) dayOffset -= 7;

        int toTakeOff = (dayOffset * _dayWidth);

        current += toTakeOff;

        // move us to the first day of the week, as the date shouldn't change
        // when scrolling
        temp.set(Calendar.DAY_OF_WEEK, temp.getFirstDayOfWeek());

        while (true) {
            gc.setForeground(_lineColor);
            gc.drawRectangle(current, topY, _weekWidth, bottomY);

            gc.setForeground(_textHeaderBackgroundColorTop);
            gc.setBackground(_textHeaderBackgroundColorBottom);
            gc.fillGradientRectangle(current < bounds.x ? bounds.x : current, topY + 1, _weekWidth, bottomY - 1, true);

            gc.setForeground(_colorManager.getTickMarkColor());
            gc.drawLine(current, topY + _settings.getVerticalTickMarkOffset(), current, topY + _settings.getHeaderDayHeight());

            gc.setForeground(_textColor);
            gc.drawString(getDateString(temp, true), current + 4, topY + 3, true);

            // only change dates on the first day of the week, as we don't
            // change date string for every different day
            if (temp.get(Calendar.DAY_OF_WEEK) == temp.getFirstDayOfWeek()) temp.add(Calendar.DATE, 7);

            current += _weekWidth;

            if (current > xMax) {
                break;
            }
        }
    }

    // days
    private void drawWeekBottomBoxes(GC gc, Rectangle bounds) {
        int xMax = bounds.width + bounds.x;
        int current = bounds.x;
        int topY = bounds.y + _settings.getHeaderMonthHeight();
        int heightY = _settings.getHeaderDayHeight();

        int day = _mainCalendar.get(Calendar.DAY_OF_WEEK);
        _daysVisible = 0;

        Calendar temp = Calendar.getInstance(_defaultLocale);
        temp.setTime(_mainCalendar.getTime());

        while (true) {
            if (_selectedHeaderDates.contains(temp)) {
                gc.setForeground(_colorManager.getSelectedDayHeaderColorTop());
                gc.setBackground(_colorManager.getSelectedDayHeaderColorBottom());
            } else {
                gc.setForeground(_timeHeaderBackgroundColorTop);
                gc.setBackground(_timeHeaderBackgroundColorBottom);
            }
            gc.fillGradientRectangle(current + 1, topY + 1, _dayWidth - 1, heightY - 1, true);

            _verticalLineLocations.add(new Integer(current));
            if (temp.get(Calendar.DAY_OF_WEEK) == _mainCalendar.getFirstDayOfWeek()) {
                _verticalWeekDividerLineLocations.add(new Integer(current));
            }

            gc.setForeground(_colorManager.getWeekTimeDividerColor());
            gc.drawRectangle(current, topY, _dayWidth, heightY);

            int hSpacer = _settings.getDayHorizontalSpacing();
            int vSpacer = _settings.getDayVerticalSpacing();

            String dayLetter = getDateString(temp, false);

            if (_settings.adjustForLetters()) {
                Point extent = null;
                if (!_dayLetterStringExtentMap.containsKey(dayLetter)) {
                    extent = gc.stringExtent(dayLetter);
                    _dayLetterStringExtentMap.put(dayLetter, extent);
                } else extent = (Point) _dayLetterStringExtentMap.get(dayLetter);

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
                }
            }

            gc.setForeground(getDayTextColor(day));
            gc.drawString(dayLetter, current + hSpacer, topY + vSpacer, true);

            temp.add(Calendar.DATE, 1);
            // fixes some odd red line bug, not sure why
            gc.setForeground(_lineColor);

            current += _dayWidth;

            day++;
            if (day > 7) day = 1;

            _daysVisible++;

            if (current > xMax) {
                _endCalendar = temp;
                break;
            }
        }
    }

    // d-days
    private void drawDDayTopBoxes(GC gc, Rectangle bounds) {
        int xMax = bounds.width + bounds.x;
        int current = bounds.x;
        int topY = bounds.y;
        int bottomY = _settings.getHeaderMonthHeight();
        int splitEvery = _settings.getDDaySplitCount();

        Calendar temp = Calendar.getInstance(_defaultLocale);
        temp.setTime(_mainCalendar.getTime());
        int startX = getXForDate(_dDayCalendar);
        int daysFromStartOffset = startX / _dayWidth;
        int dayOffset = 0;
        int letter = 0;

        // find the start date by splitting it down and checking offsets of where we are
        float count = (float) daysFromStartOffset / (float) splitEvery;
        count = (float) Math.floor(count);
        int takeOff = (int) Math.ceil(count * splitEvery);
        dayOffset = daysFromStartOffset - takeOff;
        letter = -takeOff;

        // set current to the location of where "0" starts for each set
        current += (dayOffset * _dayWidth);
        int dDayWeekWidth = _dayWidth * splitEvery;

        // if we're to the right of 0 we need to start drawing one "set" of days prior to the left or we'll have a blak space
        if (current > 0) {
            current -= dDayWeekWidth;
            letter -= splitEvery;
        }

        while (true) {
            gc.setForeground(_lineColor);
            gc.drawRectangle(current, topY, dDayWeekWidth, bottomY);

            gc.setForeground(_textHeaderBackgroundColorTop);
            gc.setBackground(_textHeaderBackgroundColorBottom);
            gc.fillGradientRectangle(current < bounds.x ? bounds.x : current, topY + 1, dDayWeekWidth, bottomY - 1, true);

            gc.setForeground(_colorManager.getTickMarkColor());
            // draws the little line that starts a group i.e a week or in this case every ten days
            gc.drawLine(current, topY + _settings.getVerticalTickMarkOffset(), current, topY + _settings.getHeaderDayHeight());

            gc.setForeground(_textColor);
            gc.drawString("" + letter, current + 4, topY + 3, true);
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
    private void drawDDayBottomBoxes(GC gc, Rectangle bounds) {
        int xMax = bounds.width + bounds.x;
        int current = bounds.x;

        int topY = bounds.y + _settings.getHeaderMonthHeight();
        int heightY = _settings.getHeaderDayHeight();

        int startX = getXForDate(_dDayCalendar);
        int daysFromStartOffset = startX / _dayWidth;
        int dayOffset = 0;
        int splitEvery = _settings.getDDaySplitCount();
        int letter = 0;

        // find the start date by splitting it down and checking offsets of where we are
        float count = (float) daysFromStartOffset / (float) splitEvery;
        count = (float) Math.floor(count);
        int takeOff = (int) Math.ceil(count * splitEvery);
        dayOffset = daysFromStartOffset - takeOff;
        letter = splitEvery - Math.abs(dayOffset);
        if (letter >= splitEvery) {
            letter -= splitEvery;
        }

        _daysVisible = 0;

        Calendar temp = Calendar.getInstance(_defaultLocale);
        temp.setTime(_mainCalendar.getTime());

        while (true) {
            if (_selectedHeaderDates.contains(temp)) {
                gc.setForeground(_colorManager.getSelectedDayHeaderColorTop());
                gc.setBackground(_colorManager.getSelectedDayHeaderColorBottom());
            } else {
                gc.setForeground(_timeHeaderBackgroundColorTop);
                gc.setBackground(_timeHeaderBackgroundColorBottom);
            }
            gc.fillGradientRectangle(current + 1, topY + 1, _dayWidth - 1, heightY - 1, true);

            _verticalLineLocations.add(new Integer(current));

            gc.setForeground(_colorManager.getWeekTimeDividerColor());
            gc.drawRectangle(current, topY, _dayWidth, heightY);

            int hSpacer = _settings.getDayHorizontalSpacing();
            int vSpacer = _settings.getDayVerticalSpacing();

            String dayLetter = "" + letter;

            if (_settings.adjustForLetters()) {
                Point extent = null;
                if (!_dayLetterStringExtentMap.containsKey(dayLetter)) {
                    extent = gc.stringExtent(dayLetter);
                    _dayLetterStringExtentMap.put(dayLetter, extent);
                } else extent = (Point) _dayLetterStringExtentMap.get(dayLetter);

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
                }
            }

            // d-days don't have weekends, so we stick to a non-weekend date
            gc.setForeground(getDayTextColor(Calendar.TUESDAY));

            gc.drawString(dayLetter, current + hSpacer, topY + vSpacer, true);

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
    private void drawHourTopBoxes(GC gc, Rectangle bounds) {
        int xMax = bounds.width + bounds.x;
        int current = bounds.x;
        int topY = bounds.y;
        int bottomY = _settings.getHeaderMonthHeight();

        Calendar temp = Calendar.getInstance(_defaultLocale);
        temp.setTime(_mainCalendar.getTime());

        // if we're not on hour zero, the box will be shorter, so check how much
        int hour = temp.get(Calendar.HOUR_OF_DAY);

        int toTakeOff = hour * _dayWidth;

        int wWidth = _weekWidth;
        current -= toTakeOff;

        boolean once = false;

        while (true) {
            gc.setForeground(_textHeaderBackgroundColorTop);
            gc.setBackground(_textHeaderBackgroundColorBottom);
            gc.fillGradientRectangle(current, topY + 1, wWidth, bottomY - 1, true);

            gc.setForeground(_colorManager.getTickMarkColor());
            gc.drawLine(current, topY + _settings.getVerticalTickMarkOffset(), current, topY + _settings.getHeaderDayHeight());

            gc.setForeground(_textColor);

            String dString = getDateString(temp, true);

            // TODO: This needs to be a different fetch for dates that are not
            // right now, so to speak.
            // so if the leftmost date is a full date/time, the "next" date
            // needs to be without the time
            // should make it a method call
            if (current >= bounds.x) gc.drawString(dString, current + 4, topY + 3, true);

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
    private void drawHourBottomBoxes(GC gc, Rectangle bounds) {
        int xMax = bounds.width + bounds.x;
        int current = bounds.x;
        int topY = bounds.y + _settings.getHeaderMonthHeight();
        int heightY = _settings.getHeaderDayHeight();

        Calendar temp = Calendar.getInstance(_defaultLocale);
        temp.setTime(_mainCalendar.getTime());

        boolean first = true;

        _hoursVisible = 0;

        while (true) {
            gc.setForeground(_colorManager.getHourTimeDividerColor());
            int spacer = 1;

            _verticalLineLocations.add(new Integer(current));

            // this weird code here checks if it's a gantt section on the left and we're drawing the first iteration.
            // as sections draw their own line on the inner-facing border, and so does this, that'd be two lines in a row
            // which makes the section at the top look rather "thick". As we don't want that, we do a quick check and skip the drawing.
            // the "spacer" does the same thing for the gradient below
            if (_ganttSections.size() > 0 && first) {
                gc.drawRectangle(current - 1, topY, _dayWidth + 1, heightY);
                spacer = 0;
            } else gc.drawRectangle(current, topY, _dayWidth, heightY);

            gc.setForeground(_timeHeaderBackgroundColorTop);
            gc.setBackground(_timeHeaderBackgroundColorBottom);
            gc.fillGradientRectangle(current + spacer, topY + 1, _dayWidth - spacer, heightY - 1, true);

            int hSpacer = _settings.getDayHorizontalSpacing();
            int vSpacer = _settings.getDayVerticalSpacing();

            gc.setForeground(_textColor);
            gc.drawString(getDateString(temp, false), current + hSpacer, topY + vSpacer, true);

            // fixes some odd red line bug, not sure why
            // gc.setForeground(mLineColor);

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
        int hr = (_mainBounds.width - _mainBounds.x) / _dayWidth;

        // hour pixels visible
        int total = hr * _dayWidth;
        float ppm = 60f / _dayWidth;

        // whatever pixels don't account for a full hour that are left over
        int extra = _mainBounds.width - _mainBounds.x - total;

        // total visible minutes
        int totalMinutes = (int) (((float) total + (float) extra) * ppm);

        // set our temporary end calendar to the start date of the calendar
        Calendar fakeEnd = Calendar.getInstance(_defaultLocale);
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

    private void drawGanttSpecialDateRanges(GC gc, Rectangle bounds, GanttSection gs) {
        for (int i = 0; i < _ganttSpecialDateRanges.size(); i++) {
            GanttSpecialDateRange range = (GanttSpecialDateRange) _ganttSpecialDateRanges.get(i);

            // fastest check
            if (!range.isUseable()) {
                continue;
            }

            // slower check
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
                offset = _verticalScrollPosition;
                if (offset > getHeaderHeight()) {
                    offset = getHeaderHeight();
                }
            } else {
                offset = _verticalScrollPosition;
            }

            int yStart = bounds.y - offset;
            int yHeight = bounds.height + offset;

            int extra = 0;
            if (!_ganttSections.isEmpty()) {
                if (_settings.getSectionSide() == SWT.LEFT) {
                    extra += _settings.getSectionBarWidth();
                }
            }

            yHeight -= offset;

            List toDraw = range.getBlocks(_mainCalendar, _endCalendar);
            for (int x = 0; x < toDraw.size(); x++) {
                List cals = (List) toDraw.get(x);
                // start and end are in pos 0 and 1
                Calendar eStart = (Calendar) cals.get(0);
                Calendar eEnd = (Calendar) cals.get(1);

                // push it over the edge to the next day or we'll have a gap of ~1px as we deal with nearly-next-day timestamps
                if (_currentView == ISettings.VIEW_DAY) {
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

    private void drawGanttPhases(GC gc, Rectangle bounds, boolean header, GanttSection gs) {

        int pHeight = _settings.getPhasesHeaderHeight();

        int extra = 0;
        if (!_ganttSections.isEmpty()) {
            if (_settings.getSectionSide() == SWT.LEFT) {
                extra += _settings.getSectionBarWidth();
            }
        }

        // first of all, fill a full background of the header
        if (header) {
            gc.setBackground(_phaseHeaderBackgroundColorBottom);
            gc.setForeground(_phaseHeaderBackgroundColorTop);

            gc.fillGradientRectangle(bounds.x, getHeaderHeight() - pHeight - _verticalScrollPosition, bounds.width, pHeight, true);

            gc.setForeground(_colorManager.getTopHorizontalLinesColor());
            gc.drawLine(bounds.x, getHeaderHeight() - 1 - _verticalScrollPosition, bounds.width + extra, getHeaderHeight() - 1 - _verticalScrollPosition);
        }

        for (int i = 0; i < _ganttPhases.size(); i++) {
            GanttPhase phase = (GanttPhase) _ganttPhases.get(i);

            // don't draw hidden phases
            if (phase.isHidden()) {
                continue;
            }

            // phase is missing data, can't draw it
            if (!phase.isDisplayable()) {
                continue;
            }

            // get values we'll need
            int xStart = getStartingXfor(phase.getStartDate());
            int xEnd = getXForDate(phase.getEndDate());

            xEnd += extra;

            int offset = 0;
            if (gs == null) {
                offset = _verticalScrollPosition;
                if (offset > getHeaderHeight()) {
                    offset = getHeaderHeight();
                }
            } else {
                offset = _verticalScrollPosition;
            }

            int yStart = bounds.y - offset;
            int yHeight = bounds.height + offset;

            yHeight -= offset;

            // alpha
            if (phase.getAlpha() != 255) {
                gc.setAlpha(phase.getAlpha());
            } else {
                gc.setAlpha(255);
                gc.setAdvanced(false);
            }

            if (header) {
                yStart = getHeaderHeight() - pHeight - _verticalScrollPosition;
                // do fills first
                gc.setBackground(phase.getHeaderBackgroundColor());
                gc.setForeground(phase.getHeaderForegroundColor());
                gc.fillGradientRectangle(xStart, yStart, xEnd - xStart, pHeight, true);

                Rectangle hBounds = new Rectangle(xStart, yStart, xEnd - xStart, pHeight - 1);

                if (phase.getTitle() != null) {
                    // font first or string extent will be off
                    if (phase.getHeaderFont() != null) {
                        gc.setFont(phase.getHeaderFont());
                    }

                    String str = getStringToDisplay(gc, hBounds, phase.getTitle());

                    Point p = gc.stringExtent(str);
                    int textX = ((xEnd - xStart) / 2) - (p.x / 2) + 1;
                    int textY = yStart + (pHeight / 2) - (p.y / 2) - 2;

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
                yStart = getHeaderHeight() - pHeight - _verticalScrollPosition;
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
    private String getStringToDisplay(GC gc, Rectangle area, String text) {
        Point p = null;
        if (_stringWidthCache.containsKey(text)) {
            p = (Point) _stringWidthCache.get(text);
        } else {
            p = gc.stringExtent(text);
            _stringWidthCache.put(text, p);
        }

        StringBuffer buf = new StringBuffer();
        String line = text;
        if (p.x > area.width) {
            int textLen = line.length();
            for (int i = textLen - 1; i >= 0; i--) {
                String temp = line.substring(0, i) + "...";
                int width = 0;
                if (_stringWidthCache.containsKey(temp)) {
                    width = ((Point) _stringWidthCache.get(temp)).x;
                } else {
                    Point p2 = gc.stringExtent(temp);
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

    private Color getDayBackgroundGradient(int day, boolean top, GanttSection gs) {
        return internalGetDayBackgroundGradient(day, top, gs);
    }

    /*
     * private Color getDayBackgroundGradient(int day, boolean top) { return _getDayBackgroundGradient(day, top, null); }
     */

    private Color internalGetDayBackgroundGradient(int day, boolean top, GanttSection gs) {
        switch (day) {
            case Calendar.SATURDAY:
                if (top) {
                    if (gs == null) return _saturdayBackgroundColorTop;
                    else return gs.getSaturdayBackgroundColorTop();
                } else {
                    if (gs == null) return _saturdayBackgroundColorBottom;
                    else return gs.getSaturdayBackgroundColorBottom();
                }
            case Calendar.SUNDAY:
                if (top) {
                    if (gs == null) return _sundayBackgroundColorTop;
                    else return gs.getSundayBackgroundColorTop();
                } else {
                    if (gs == null) return _sundayBackgroundColorBottom;
                    else return gs.getSundayBackgroundColorBottom();
                }
            default:
                if (top) {
                    if (gs == null) return _weekdayBackgroundColorTop;
                    else return gs.getWeekdayBackgroundColorTop();
                } else {
                    if (gs == null) return _weekdayBackgroundColorBottom;
                    else return gs.getWeekdayBackgroundColorBottom();
                }
        }
    }

    private Color getDayTextColor(int day) {
        switch (day) {
            case Calendar.SATURDAY:
                return _saturdayTextColor;
            case Calendar.SUNDAY:
                return _sundayTextColor;
            default:
                return _weekdayTextColor;
        }
    }

    private void drawEvents(GC gc, Rectangle bounds, GanttSection gs) {
        internalDrawEvents(gc, bounds, gs);
    }

    private void drawEvents(GC gc, Rectangle bounds) {
        internalDrawEvents(gc, bounds, null);
    }

    private void internalDrawEvents(GC gc, Rectangle bounds, GanttSection gs) {
        if (_ganttEvents.isEmpty()) { return; }

        HashSet eventsAlreadyDrawn = new HashSet();

        List events = _ganttEvents;
        if (gs != null) {
            events = gs.getEvents();
        }

        List correctOrder = new ArrayList();
        for (int i = 0; i < events.size(); i++) {
            IGanttChartItem event = (IGanttChartItem) events.get(i);
            if (event instanceof GanttGroup) {
                GanttGroup gg = (GanttGroup) event;
                ArrayList children = gg.getEventMembers();
                correctOrder.addAll(children);
                continue;
            } else {
                correctOrder.add(event);
            }
        }

        _totalVisibleGanttEvents = 0;

        // add events that need to be drawn but aren't actually part of the section (vertical DND)
        if (gs != null) {
            correctOrder.addAll(gs.getDNDGanttEvents());
        }

        for (int i = 0; i < correctOrder.size(); i++) {
            GanttEvent ge = (GanttEvent) correctOrder.get(i);

            if (ge.isHidden()) continue;

            if (eventsAlreadyDrawn.contains(ge)) continue;

            // all events that are non-hidden are counted, in visible bounds or not
            _totalVisibleGanttEvents++;

            // don't draw out of bounds events
            if (ge.getVisibility() != VISIBILITY_VISIBLE) {
                // still calculate name extent, we need it to determine correct scrollbars for fixed scrollbars among other things
                if (ge.getNameExtent() == null || ge.isNameChanged()) {
                    String toDraw = getStringForEvent(ge);
                    ge.setNameExtent(gc.stringExtent(toDraw));
                    ge.setParsedString(toDraw);
                    ge.setNameChanged(false);
                }
                continue;
            }

            // at this point it will be drawn
            eventsAlreadyDrawn.add(ge);

            // draw it
            drawOneEvent(gc, ge, bounds);
        }
    }

    // draws one event onto the chart (or rather, delegates to the correct drawing method)
    private void drawOneEvent(GC gc, GanttEvent ge, Rectangle boundsToUse) {
        int xStart = ge.getX();
        int xEventWidth = ge.getWidth();

        // clone the bounds, we don't want any sub-method messing with them
        Rectangle bounds = new Rectangle(boundsToUse.x, boundsToUse.y, boundsToUse.width, boundsToUse.height);

        Color cEvent = ge.getStatusColor();
        Color gradient = ge.getGradientStatusColor();

        if (cEvent == null) cEvent = _settings.getDefaultEventColor();
        if (gradient == null) gradient = _settings.getDefaultGradientEventColor();

        int yDrawPos = ge.getY();

        int dw = getDayWidth();

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
            _paintManager.drawEvent(this, _settings, _colorManager, ge, gc, (_selectedEvents.size() > 0 && _selectedEvents.contains(ge)), _threeDee, dw, xStart, yDrawPos, xEventWidth, bounds);
        }
        if (ge.hasMovementConstraints()) {
            int startStart = -1, endStart = -1;
            if (ge.getNoMoveBeforeDate() != null) {
                startStart = getStartingXfor(ge.getNoMoveBeforeDate());
            }
            if (ge.getNoMoveAfterDate() != null) {
                endStart = getStartingXfor(ge.getNoMoveAfterDate());
            }

            _paintManager.drawLockedDateRangeMarker(this, _settings, _colorManager, ge, gc, _threeDee, dw, yDrawPos, startStart, endStart, bounds);
        }

        // draws |---------| lines to show revised dates, if any
        if (_showPlannedDates) {
            _paintManager.drawPlannedDates(this, _settings, _colorManager, ge, gc, _threeDee, xStart, yDrawPos, xEventWidth, bounds);
        }

        // draw a little plaque saying how many days that this event is long
        if (_showNumberOfDaysOnChart) {
            long days = DateHelper.daysBetween(ge.getActualStartDate(), ge.getActualEndDate(), _defaultLocale) + 1;
            _paintManager.drawDaysOnChart(this, _settings, _colorManager, ge, gc, _threeDee, xStart, yDrawPos, xEventWidth, (int) days, bounds);
        }

        // fetch current font
        Font oldFont = gc.getFont();

        // if the name has changed, update various cached variables
        if (ge.isNameChanged()) {
            String toDraw = getStringForEvent(ge);
            ge.setNameExtent(gc.stringExtent(toDraw));
            ge.setParsedString(toDraw);
            ge.setNameChanged(false);
        }

        // draw the text if any, o
        if (ge.getParsedString() != null) _paintManager.drawEventString(this, _settings, _colorManager, ge, gc, ge.getParsedString(), _threeDee, xStart, yDrawPos, xEventWidth, bounds);

        // reset font
        gc.setFont(oldFont);

        if (advanced) {
            gc.setAdvanced(false);
        }
    }

    // updates all event visibilities, the bounds is the currently visible bounds, not the bounds that should be calculated
    private void updateEventVisibilities(Rectangle bounds) {
        Object[] all = _allEventsCombined.toArray();
        for (int i = 0; i < all.length; i++) {
            GanttEvent ge = (GanttEvent) all[i];
            ge.setVisibility(getEventVisibility(ge, bounds));
        }
    }

    private void calculateAllScopes(Rectangle bounds, GanttSection gs) {
        if (_ganttEvents.size() == 0) return;

        int yStart = bounds.y + _settings.getEventsTopSpacer();// - mVerticalScrollPosition;
        //System.err.println(yStart);

        HashSet allEventsInGroups = new HashSet();
        for (int i = 0; i < _ganttGroups.size(); i++) {
            allEventsInGroups.addAll(((GanttGroup) _ganttGroups.get(i)).getEventMembers());
        }

        boolean lastLoopWasGroup = false;
        //GanttGroup lastGroup = null;
        Map groupLocations = new HashMap();

        List events = _ganttEvents;
        if (gs != null) events = gs.getEvents();

        List correctOrder = new ArrayList();
        for (int i = 0; i < events.size(); i++) {
            IGanttChartItem event = (IGanttChartItem) events.get(i);
            if (event instanceof GanttGroup) {
                GanttGroup gg = (GanttGroup) event;
                ArrayList children = gg.getEventMembers();
                correctOrder.addAll(children);
                continue;
            } else {
                correctOrder.add(event);
            }
        }

        for (int i = 0; i < correctOrder.size(); i++) {
            IGanttChartItem event = (IGanttChartItem) correctOrder.get(i);

            GanttEvent ge = (GanttEvent) event;

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

            int xStart = getStartingXforEvent(ge);
            int xEventWidth = getXLengthForEvent(ge);

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
            int yDrawPos = yStart - _verticalScrollPosition;

            // if it's a grouped event, get the location from our map to where it's drawn
            if (groupedEvent && groupLocations.containsKey(ge.getGanttGroup())) {
                yDrawPos = ((Integer) groupLocations.get(ge.getGanttGroup())).intValue();
            }

            int fixedRowHeight = _fixedRowHeight;
            int verticalAlignment = ge.getVerticalEventAlignment();

            if (ge.getGanttGroup() != null) {
                verticalAlignment = ge.getGanttGroup().getVerticalEventAlignment();
                if (!ge.getGanttGroup().isAutomaticRowHeight()) {
                    fixedRowHeight = ge.getGanttGroup().getFixedRowHeight();
                }
            } else {
                if (!ge.isAutomaticRowHeight()) {
                    fixedRowHeight = ge.getFixedRowHeight();
                }
            }

            boolean fixedHeight = (fixedRowHeight > 0);

            ge.setHorizontalLineTopY(yStart + _verticalScrollPosition);

            if (fixedHeight) {
                yStart += fixedRowHeight;

                int extra = 0;

                switch (verticalAlignment) {
                    case SWT.BOTTOM:
                        extra = fixedRowHeight - _eventHeight;
                        break;
                    case SWT.CENTER:
                        extra = ((fixedRowHeight / 2) - (_eventHeight / 2));
                        break;
                    case SWT.NONE:
                    case SWT.TOP:
                        extra = _eventSpacer - _eventHeight;
                        break;
                }

                if (extra < 0) extra = 0;

                yDrawPos += extra;

            }

            // sub-events in a grouped event type where the group has a fixed row height, we just set the yStart to the last yStart, which actually
            // got through the above switch statement and had its start position calculated
            if (!newGroup && groupedEvent) {
                yDrawPos = ((Integer) groupLocations.get(ge.getGanttGroup())).intValue();
            }

            if (!fixedHeight) {
                ge.setHorizontalLineBottomY(yDrawPos + _eventHeight + _verticalScrollPosition);
            } else {
                ge.setHorizontalLineBottomY(yDrawPos - _eventHeight + _verticalScrollPosition);
            }

            // set event bounds
            ge.setBounds(new Rectangle(xStart, yDrawPos, xEventWidth, _eventHeight));

            // update the actual width of the event
            ge.updateActualWidth();

            if (!groupedEvent) {
                // space them out
                if (!fixedHeight) {
                    yStart += _eventHeight + _eventSpacer;
                    _bottomMostY = yStart + _eventHeight;
                }
                lastLoopWasGroup = false;
            } else {
                lastLoopWasGroup = true;
            }

            _bottomMostY = Math.max(_bottomMostY, yStart + _eventHeight);

        }

        // take off the last iteration, easier here than an if check for each iteration
        _bottomMostY -= _eventSpacer;
    }

    // string processing for display text beyond event
    private String getStringForEvent(GanttEvent ge) {
        String toUse = ge.getTextDisplayFormat();
        if (toUse == null) toUse = _settings.getTextDisplayFormat();
        if (toUse == null) return "";

        String dateFormat = (_currentView == ISettings.VIEW_DAY ? _settings.getHourDateFormat() : _settings.getDateFormat());

        String toReturn = toUse;

        // we do indexOf on all events, as blind replaceAll is slow when repeatedly called (which we are). It's faster to indexOf and then replace if need be.
        // quick testing shows we (randomly) save about 15ms per large redraw, which is huge.
        // String operations are normally slow
        if (toReturn.indexOf("#name#") > -1) {
            if (ge.getName() != null) toReturn = toReturn.replaceAll("#name#", ge.getName());
            else toReturn = toReturn.replaceAll("#name#", "");
        }

        if (toReturn.indexOf("#pc#") > -1) toReturn = toReturn.replaceAll("#pc#", "" + ge.getPercentComplete());

        if (toReturn.indexOf("#sd#") > -1) {
            if (ge.getStartDate() != null) toReturn = toReturn.replaceAll("#sd#", "" + DateHelper.getDate(ge.getStartDate(), dateFormat, _defaultLocale));
            else toReturn = toReturn.replaceAll("#sd#", "");
        }

        if (toReturn.indexOf("#ed#") > -1) {
            if (ge.getEndDate() != null) toReturn = toReturn.replaceAll("#ed#", "" + DateHelper.getDate(ge.getEndDate(), dateFormat, _defaultLocale));
            else toReturn = toReturn.replaceAll("#ed", "");
        }

        if (toReturn.indexOf("#rs#") > -1) {
            if (ge.getRevisedStart() != null) toReturn = toReturn.replaceAll("#rs#", "" + DateHelper.getDate(ge.getRevisedStart(), dateFormat, _defaultLocale));
            else toReturn = toReturn.replaceAll("#rs#", "");
        }

        if (toReturn.indexOf("#re#") > -1) {
            if (ge.getRevisedEnd() != null) toReturn = toReturn.replaceAll("#re#", "" + DateHelper.getDate(ge.getRevisedEnd(), dateFormat, _defaultLocale));
            else toReturn = toReturn.replaceAll("#re#", "");
        }

        if (toReturn.indexOf("#days#") > 1) {
            if (ge.getStartDate() != null && ge.getEndDate() != null) {
                long days = DateHelper.daysBetween(ge.getStartDate(), ge.getEndDate(), _defaultLocale);
                toReturn = toReturn.replaceAll("#days#", "" + days);
            } else toReturn = toReturn.replaceAll("#days#", "");
        }

        if (toReturn.indexOf("#reviseddays#") > -1) {
            if (ge.getRevisedStart() != null && ge.getRevisedEnd() != null) {
                long days = DateHelper.daysBetween(ge.getRevisedStart(), ge.getRevisedEnd(), _defaultLocale);
                toReturn = toReturn.replaceAll("#reviseddays#", "" + days);
            } else toReturn = toReturn.replaceAll("#reviseddays#", "");
        }

        return toReturn;
    }

    // draws the three top horizontal lines
    private void drawTopHorizontalLines(GC gc, Rectangle bounds) {
        int yStart = bounds.y;
        int width = bounds.x + bounds.width;
        int xStart = bounds.x;

        int monthHeight = bounds.y + _settings.getHeaderMonthHeight();

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
    public void addDependency(GanttEvent source, GanttEvent target) {
        addDependency(source, target, null);
    }

    /**
     * Adds a connection between two GanttEvents. <code>Source</code> will connect to <code>Target</code>.
     * 
     * @param source Source event
     * @param target Target event
     * @param Color to use to draw connection. Set null to use default color from Settings.
     */
    public void addDependency(GanttEvent source, GanttEvent target, Color color) {
        checkWidget();
        if (source == null || target == null) return;

        GanttConnection con = new GanttConnection(source, target, color);
        if (!_ganttConnections.contains(con)) _ganttConnections.add(con);
    }

    // connection is added from a GanttConnection class
    void connectionAdded(GanttConnection conn) {
        checkWidget();

        addDependency(conn.getSource(), conn.getTarget(), conn.getColor());
    }

    void connectionRemoved(GanttConnection conn) {
        checkWidget();

        _ganttConnections.remove(conn);
    }

    /**
     * Returns true if the given event is connected to another.
     * 
     * @param ge GanttEvent to check
     * @return true if the GanttEvent is connected
     */
    public boolean isConnected(GanttEvent ge) {
        for (int i = 0; i < _ganttConnections.size(); i++) {
            GanttConnection gc = (GanttConnection) _ganttConnections.get(i);
            if (gc.getSource().equals(ge) || gc.getTarget().equals(ge)) { return true; }
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
    public boolean isConnected(GanttEvent source, GanttEvent target) {
        for (int i = 0; i < _ganttConnections.size(); i++) {
            GanttConnection gc = (GanttConnection) _ganttConnections.get(i);
            if (gc.getSource().equals(source) && gc.getTarget().equals(target)) return true;
        }

        return false;
    }

    /**
     * Same as addDependency().
     * 
     * @param source Source event
     * @param target Target event
     */
    public void addConnection(GanttEvent source, GanttEvent target) {
        addDependency(source, target, null);
    }

    /**
     * Same as addDependency().
     * 
     * @param source Source event
     * @param target Target event
     * @param Color to use to draw connection. Set null to use defaults.
     */
    public void addConnection(GanttEvent source, GanttEvent target, Color color) {
        addDependency(source, target, color);
    }

    // the stub that is the first section drawn from the end of an event
    private Rectangle getFirstStub(GanttConnection con) {
        GanttEvent ge1 = con.getSource();

        // draw the few pixels right after the box, but don't draw over text
        int xStart = ge1.getX() + ge1.getWidth();
        int yStart = ge1.getY() + (_eventHeight / 2);

        return new Rectangle(xStart, yStart, 4, _eventHeight);
    }

    // draws the lines and arrows between events
    private void drawConnections(GC gc, Rectangle bounds) {

        int dw = getDayWidth();

        for (int i = 0; i < _ganttConnections.size(); i++) {
            GanttConnection connection = (GanttConnection) _ganttConnections.get(i);

            GanttEvent ge1 = connection.getSource();
            GanttEvent ge2 = connection.getTarget();

            if (ge1 == null || ge2 == null) continue;

            if (ge1.equals(ge2)) continue;

            if (ge1.getX() == 0 && ge2.getY() == 0) continue;

            // don't draw hidden events, nor connections to them
            if (ge1.isHidden() || ge2.isHidden()) continue;

            // use connection color if set, otherwise use arrow color
            gc.setForeground(connection.getColor() == null ? _arrowColor : connection.getColor());

            // same deal but with hidden layers
            if (_hiddenLayers.size() > 0) {
                if (_hiddenLayers.contains(ge1.getLayerInt()) || _hiddenLayers.contains(ge2.getLayerInt())) continue;
            }

            boolean sourceIsOutOfBounds = ge1.getVisibility() != VISIBILITY_VISIBLE;
            boolean targetIsOutOfBounds = ge2.getVisibility() != VISIBILITY_VISIBLE;

            // save precious cycles by not drawing what we don't have to draw (where both events are oob in a way that it's certain no "lines" will cross our view)
            if (sourceIsOutOfBounds && targetIsOutOfBounds) {
                if (ge1.getVisibility() == VISIBILITY_OOB_LEFT && ge2.getVisibility() == VISIBILITY_OOB_LEFT) continue;

                if (ge1.getVisibility() == VISIBILITY_OOB_RIGHT && ge2.getVisibility() == VISIBILITY_OOB_RIGHT) continue;

                if (ge1.getVisibility() == VISIBILITY_OOB_HEIGHT_TOP && ge2.getVisibility() == VISIBILITY_OOB_HEIGHT_TOP) continue;

                if (ge1.getVisibility() == VISIBILITY_OOB_HEIGHT_BOTTOM && ge2.getVisibility() == VISIBILITY_OOB_HEIGHT_BOTTOM) continue;
            }

            // don't draw if we can't see it, this is rather buggy, but that's
            // stated in the feature
            if (_settings.consumeEventWhenOutOfRange()) {
                if (ge1.getVisibility() == VISIBILITY_NOT_VISIBLE && ge2.getVisibility() == VISIBILITY_NOT_VISIBLE) continue;
            }

            if (_settings.showOnlyDependenciesForSelectedItems()) {
                if (_selectedEvents.size() == 0) { return; }

                if (!_selectedEvents.contains(connection.getSource())) {
                    continue;
                }
            }

            if (_settings.getArrowConnectionType() != ISettings.CONNECTION_MS_PROJECT_STYLE) {
                if (_settings.getArrowConnectionType() == ISettings.CONNECTION_BIRDS_FLIGHT_PATH) {
                    if (ge1.getX() < ge2.getX()) gc.drawLine(ge1.getXEnd(), ge1.getBottomY(), ge2.getX(), ge2.getY());
                    else gc.drawLine(ge1.getX(), ge1.getY(), ge2.getXEnd(), ge2.getBottomY());
                    continue;
                }

                // draw the stub.. [event]-- .. -- is the stub
                Rectangle rect = getFirstStub(connection);
                gc.drawLine(rect.x + 1, rect.y, rect.x + rect.width - 1, rect.y);

                // draw down some, (start at the end of stub and draw down
                // remaining height of event box, + half of the event spacer)
                // --
                // | <-- that part
                Rectangle down = new Rectangle(rect.x + rect.width, rect.y, rect.width, (rect.height / 2) + (_eventSpacer / 2));
                gc.drawLine(down.x, down.y, rect.x + down.width, rect.y + down.height);

                // get the top left corner of the target area, then draw a line
                // out to it, only along the x axis
                Rectangle rGe2 = new Rectangle(ge2.getX() - _settings.getArrowHeadEventSpacer(), ge2.getY() + _settings.getArrowHeadVerticalAdjuster(), ge2.getWidth(), ge2.getHeight());

                boolean goingUp = false;
                boolean goingLeft = false;
                if (rect.y > rGe2.y) goingUp = true;

                if (rect.x > rGe2.x) goingLeft = true;

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
                    int offset = 10;

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
                Rectangle rect = getFirstStub(connection);
                int x = rect.x;
                int y = rect.y;

                boolean aboveUs = ge2.getY() < ge1.getY();
                boolean belowUs = ge2.getY() > ge1.getY();
                boolean sameRow = ge2.getY() == ge1.getY();
                boolean targetIsOnLeft = ge2.getXEnd() < ge1.getX();
                boolean targetIsOnRight = ge2.getX() > ge1.getXEnd();

                Rectangle bounds1 = ge1.getBounds();
                Rectangle bounds2 = ge2.getBounds();
                // fake same line
                bounds1.y = bounds2.y;
                boolean eventsOverlap = bounds1.intersects(bounds2);
                boolean targetIsOnLeftBorder = ge2.getXEnd() == ge1.getX();
                boolean targetIsOnRightBorder = ge2.getX() == ge1.getXEnd();

                int neg = 8;

                Point xy = null;
                boolean isLinux = (osType == OS_LINUX);

                if (belowUs) {
                    gc.setForeground(connection.getColor() == null ? _arrowColor : connection.getColor());

                    // draw first stub
                    gc.drawLine(x, y, x + rect.width, y);
                    x += rect.width;

                    xy = drawBend(gc, BEND_RIGHT_DOWN, x - (isLinux ? 1 : 0), y, true);
                    x = xy.x;
                    y = xy.y;

                    if (targetIsOnRight) {
                        // #1 vert line
                        int yTarget = ge2.getY() + (ge2.getHeight() / 2);
                        gc.drawLine(x, y, x, yTarget - (isLinux ? 1 : 2)); // minus 2 as we need another bend
                        y = yTarget - 2;

                        // #2 bend
                        xy = drawBend(gc, BEND_RIGHT_DOWN, x, y, true);
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
                        int yDest = ge1.getBottomY() + (_eventSpacer / 2);
                        gc.drawLine(x, y, x, yDest);
                        y = yDest;// ge1.getY() + ge1.getHeight() + yDiff - 2;

                        if (isLinux) y -= 1;

                        // #2 bend
                        xy = drawBend(gc, BEND_LEFT_DOWN, x, y, true);
                        x = xy.x;
                        y = xy.y;

                        // #3 line
                        gc.drawLine(x, y, ge2.getX() - neg, y);
                        x = ge2.getX() - neg;

                        // #4 bend
                        xy = drawBend(gc, BEND_LEFT_DOWN, x, y, true);
                        x = xy.x;
                        y = xy.y;

                        // #5 vert line
                        int yTarget = ge2.getY() + (ge2.getHeight() / 2);
                        gc.drawLine(x, y, x, yTarget - (isLinux ? 1 : 2)); // minus 2 as we need another bend
                        y = yTarget - 2;

                        // #6 bend
                        xy = drawBend(gc, BEND_RIGHT_DOWN, x, y, true);
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
                        xy = drawBend(gc, BEND_RIGHT_DOWN, x, y, true);
                        x = xy.x;
                        y = xy.y;

                        // #1 vertical
                        int yDest = ge1.getBottomY() + (_eventSpacer / 2);
                        gc.drawLine(x, y, x, yDest);
                        y = yDest;

                        // #2 bend
                        xy = drawBend(gc, BEND_LEFT_DOWN, x, y, true);
                        x = xy.x;
                        y = xy.y;

                        // #3 line (not -8 as we don't want line overlap for events that draw down and use -8)
                        gc.drawLine(x, y, ge2.getX() - neg - _settings.getReverseDependencyLineHorizontalSpacer(), y);
                        x = ge2.getX() - neg - _settings.getReverseDependencyLineHorizontalSpacer();

                        // #4 bend
                        xy = drawBend(gc, BEND_LEFT_UP, x, y, true);
                        x = xy.x;
                        y = xy.y;

                        // #5 vert up
                        if (_settings.useSplitArrowConnections()) {
                            gc.drawLine(x, y, x, ge2.getY() + ge2.getHeight());
                            y = ge2.getY() + ge2.getHeight();
                        } else {
                            gc.drawLine(x, y, x, ge2.getY() + (ge2.getHeight() / 2) + 2);
                            y = ge2.getY() + (ge2.getHeight() / 2) + 2;
                        }

                        // #6 bend
                        xy = drawBend(gc, BEND_RIGHT_UP, x, y, true);
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
                        xy = drawBend(gc, BEND_RIGHT_UP, x, y, true);
                        x = xy.x;
                        y = xy.y;

                        // #2 vert up
                        if (_settings.useSplitArrowConnections()) {
                            gc.drawLine(x, y, x, ge2.getY() + ge2.getHeight());
                            y = ge2.getY() + ge2.getHeight();
                        } else {
                            gc.drawLine(x, y, x, ge2.getY() + (ge2.getHeight() / 2) + 2);
                            y = ge2.getY() + (ge2.getHeight() / 2) + 2;
                        }

                        // #3 bend
                        xy = drawBend(gc, BEND_RIGHT_UP, x, y, true);
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
                        xy = drawBend(gc, BEND_RIGHT_DOWN, x, y, true);
                        x = xy.x;
                        y = xy.y;

                        // #2 vert down
                        if (_settings.useSplitArrowConnections()) {
                            gc.drawLine(x, y, x, ge2.getY() + ge2.getHeight() + (_eventSpacer / 2));
                            y = ge2.getY() + ge2.getHeight();
                        } else {
                            gc.drawLine(x, y, x, ge2.getY() + (ge2.getHeight() / 2) + 2 + (_eventSpacer / 2));
                            y = ge2.getY() + (ge2.getHeight() / 2) + 2;
                        }
                        y += (_eventSpacer / 2);

                        // #1 bend
                        xy = drawBend(gc, BEND_LEFT_DOWN, x, y, true);
                        x = xy.x;
                        y = xy.y;

                        // #4 horizontal
                        gc.drawLine(x, y, ge2.getX() - neg - (neg / 2), y);
                        x = ge2.getX() - neg - (neg / 2);

                        // #5 bend
                        xy = drawBend(gc, BEND_LEFT_UP, x, y, true);
                        x = xy.x;
                        y = xy.y;

                        // #6 vert up
                        if (_settings.useSplitArrowConnections()) {
                            gc.drawLine(x, y, x, ge2.getY() + (ge2.getHeight() / 2) + neg - _settings.getReverseDependencyLineHorizontalSpacer());
                            y = ge2.getY() + (ge2.getHeight() / 2) + neg - _settings.getReverseDependencyLineHorizontalSpacer();
                        } else {
                            gc.drawLine(x, y, x, ge2.getY() + (ge2.getHeight() / 2) + neg - _settings.getReverseDependencyLineHorizontalSpacer());
                            y = ge2.getY() + (ge2.getHeight() / 2) + neg - _settings.getReverseDependencyLineHorizontalSpacer();
                        }

                        // #7 bend
                        xy = drawBend(gc, BEND_RIGHT_UP, x, y, true);
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
                        if ((ge2.getX() - ge1.getXEnd()) <= dw) {
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
                        xy = drawBend(gc, BEND_RIGHT_DOWN, x, y, true);
                        x = xy.x;
                        y = xy.y;

                        // #2 vert down
                        if (_settings.useSplitArrowConnections()) {
                            gc.drawLine(x, y, x, ge2.getY() + ge2.getHeight() + (_eventSpacer / 2));
                            y = ge2.getY() + ge2.getHeight();
                        } else {
                            gc.drawLine(x, y, x, ge2.getY() + (ge2.getHeight() / 2) + 2 + (_eventSpacer / 2));
                            y = ge2.getY() + (ge2.getHeight() / 2) + 2;
                        }
                        y += (_eventSpacer / 2);

                        // #3 bend
                        xy = drawBend(gc, BEND_RIGHT_DOWN, x, y, true);
                        x = xy.x;
                        y = xy.y;

                        // #4 horizontal
                        gc.drawLine(x, y, ge2.getX() - neg - (neg / 2), y);
                        x = ge2.getX() - neg - (neg / 2);

                        // #5 bend
                        xy = drawBend(gc, BEND_RIGHT_UP, x, y, true);
                        x = xy.x;
                        y = xy.y;

                        // #6 vert down
                        if (_settings.useSplitArrowConnections()) {
                            gc.drawLine(x, y, x, ge2.getY() + (ge2.getHeight() / 2) + 2);
                            y = ge2.getY() + (ge2.getHeight() / 2) + 2;
                        } else {
                            gc.drawLine(x, y, x, ge2.getY() + (ge2.getHeight() / 2) + 2);
                            y = ge2.getY() + (ge2.getHeight() / 2) + 2;
                        }

                        // #7 bend
                        xy = drawBend(gc, BEND_RIGHT_UP, x, y, true);
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

    private Point drawBend(GC gc, int style, int x, int y, boolean rounded) {
        Point xy = new Point(0, 0);
        int bonus = (osType == OS_LINUX ? 1 : 0);
        if (rounded) {
            switch (style) {
                case BEND_RIGHT_UP:
                    gc.drawLine(x + 1, y - 1, x + 1 + bonus, y - 1 - bonus);
                    gc.drawLine(x + 2, y - 2, x + 2 + bonus, y - 2 - bonus);
                    xy.x = x + 2;
                    xy.y = y - 2;
                    break;
                case BEND_RIGHT_DOWN:
                    gc.drawLine(x + 1, y + 1, x + 1 + bonus, y + 1 + bonus);
                    gc.drawLine(x + 2, y + 2, x + 2 + bonus, y + 2 + bonus);
                    xy.x = x + 2;
                    xy.y = y + 2;
                    break;
                case BEND_LEFT_DOWN:
                    gc.drawLine(x - 1, y + 1, x - 1 + bonus, y + 1 + bonus);
                    gc.drawLine(x - 2, y + 2, x - 2 + bonus, y + 2 + bonus);
                    xy.x = x - 2;
                    xy.y = y + 2;
                    break;
                case BEND_LEFT_UP:
                    gc.drawLine(x - 1, y - 1, x - 1 + bonus, y - 1 - bonus);
                    gc.drawLine(x - 2, y - 2, x - 2 + bonus, y - 2 - bonus);
                    xy.x = x - 2;
                    xy.y = y - 2;
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
    private void drawTodayLine(GC gc, Rectangle bounds, int x, int dayOfWeek) {
        // d-day has no today
        if (_currentView == ISettings.VIEW_D_DAY) { return; }

        int xStart = x;

        gc.setForeground(_lineTodayColor);
        gc.setLineWidth(_settings.getTodayLineWidth());
        gc.setLineStyle(_settings.getTodayLineStyle());

        // to not clash with the week divider lines move dotted line 1 pixel left on new weeks
        if (_currentView == ISettings.VIEW_WEEK || _currentView == ISettings.VIEW_MONTH) {
            if (dayOfWeek == _mainCalendar.getFirstDayOfWeek()) xStart--;
        }

        int vOffset = _settings.getTodayLineVerticalOffset();
        int yStart = bounds.y - _settings.getHeaderMonthHeight();
        if (!_settings.drawHeader()) vOffset = 0;
        yStart += vOffset;
        if (!_settings.drawHeader()) yStart = bounds.y;

        yStart -= _verticalScrollPosition;

        if (_useAlpha) gc.setAlpha(_colorManager.getTodayLineAlpha());

        gc.drawLine(xStart, yStart, xStart, bounds.height + yStart + _verticalScrollPosition);
        if (_useAlpha) {
            gc.setAlpha(0);
            gc.setAdvanced(false);
        }

        // reset lines etc
        gc.setLineWidth(1);
        gc.setLineStyle(SWT.LINE_SOLID);
    }

    // the Date string that is displayed at the very top
    private String getDateString(Calendar cal, boolean top) {
        if (top) {
            switch (_currentView) {
                case ISettings.VIEW_WEEK:
                    return DateHelper.getDate(cal, _settings.getWeekHeaderTextDisplayFormatTop(), _defaultLocale);
                case ISettings.VIEW_MONTH:
                    return DateHelper.getDate(cal, _settings.getMonthHeaderTextDisplayFormatTop(), _defaultLocale);
                case ISettings.VIEW_DAY:
                    return DateHelper.getDate(cal, _settings.getDayHeaderTextDisplayFormatTop(), _defaultLocale);
                case ISettings.VIEW_YEAR:
                    return DateHelper.getDate(cal, _settings.getYearHeaderTextDisplayFormatTop(), _defaultLocale);
            }
        } else {
            switch (_currentView) {
                case ISettings.VIEW_WEEK:
                    return DateHelper.getDate(cal, _settings.getWeekHeaderTextDisplayFormatBottom(), _defaultLocale).substring(0, 1);
                case ISettings.VIEW_MONTH:
                    return DateHelper.getDate(cal, _settings.getMonthHeaderTextDisplayFormatBottom(), _defaultLocale);
                case ISettings.VIEW_DAY:
                    return DateHelper.getDate(cal, _settings.getDayHeaderTextDisplayFormatBottom(), _defaultLocale);
                case ISettings.VIEW_YEAR:
                    return DateHelper.getDate(cal, _settings.getYearHeaderTextDisplayFormatBottom(), _defaultLocale);
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
    public void setDate(Calendar date) {
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
    public void jumpToToday(int side) {
        checkWidget();

        Calendar cal = Calendar.getInstance(_defaultLocale);

        if (_currentView == ISettings.VIEW_DAY) {
            // round it down
            internalSetDate(cal, side, true, true);
        } else setDate(cal, side, false);
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
        /*        Calendar cal = Calendar.getInstance(mDefaultLocale);
                GanttEvent latest = getEvent(false, false);
                if (latest == null) return;

                flagForceFullUpdate();
                cal.setTime(latest.getActualStartDate().getTime());

                if (mCurrentView == ISettings.VIEW_DAY) {
                    internalSetDate(cal, SWT.LEFT, true, true);
                } else {
                    setDate(cal, false);
                }*/
        jumpToEvent(false);
    }

    private void jumpToEvent(boolean earliestEvent) {
        Calendar cal = Calendar.getInstance(_defaultLocale);
        Date earliest = getEventDate(earliestEvent);
        if (earliest == null) { return; }

        flagForceFullUpdate();
        cal.setTime(earliest);

        if (_currentView == ISettings.VIEW_DAY) {
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
    public void jumpToEvent(GanttEvent event, boolean start, int side) {
        Calendar cal = Calendar.getInstance(_defaultLocale);

        if (start) cal.setTime(event.getActualStartDate().getTime());
        else cal.setTime(event.getActualEndDate().getTime());

        internalSetDate(cal, side, true, true);
    }

    GanttEvent getEvent(boolean earliest, boolean pixelComparison) {
        Calendar cal = null;
        GanttEvent retEvent = null;

        for (int i = 0; i < _ganttEvents.size(); i++) {
            GanttEvent ge = (GanttEvent) _ganttEvents.get(i);
            if (earliest) {
                if (cal == null) {
                    cal = ge.getEarliestStartDate();
                    retEvent = ge;
                    continue;
                }

                if (pixelComparison) {
                    Rectangle r = ge.getActualBounds();
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
                    Rectangle r = ge.getActualBounds();
                    Rectangle other = retEvent.getActualBounds();
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

    private Date getEventDate(boolean earliest) {
        Calendar ret = null;

        for (int i = 0; i < _ganttEvents.size(); i++) {
            GanttEvent ge = (GanttEvent) _ganttEvents.get(i);
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
    public void setDate(Calendar date, int side, boolean clearMinutes) {
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

    private void internalSetDateAtX(int x, Calendar preZoomDate, boolean clearMinutes, boolean redraw, boolean zoomIn) {
        Calendar target = getDateAt(x);

        if (_currentView == ISettings.VIEW_DAY) {
            int hours = DateHelper.hoursBetween(target, preZoomDate, _defaultLocale, false);
            Calendar toSet = (Calendar) _mainCalendar.clone();
            if (zoomIn) {
                hours = Math.abs(hours);
            }

            toSet.add(Calendar.HOUR_OF_DAY, hours);

            internalSetDate(toSet, SWT.LEFT, clearMinutes, redraw);
        } else {
            int days = (int) DateHelper.daysBetween(target, preZoomDate, _defaultLocale);
            if (zoomIn) {
                days = Math.abs(days);
            }

            Calendar toSet = (Calendar) _mainCalendar.clone();
            toSet.add(Calendar.DATE, days);
            internalSetDate(toSet, SWT.LEFT, clearMinutes, redraw);
        }
    }

    private void internalSetDate(final Calendar date, final int side, final boolean clearMinutes, final boolean redraw) {
        checkWidget();

        // set the date regardless, thus the below "jump" will be minor
        _mainCalendar = (Calendar) date.clone();
        if (clearMinutes) {
            _mainCalendar.set(Calendar.MINUTE, 0);
            _mainCalendar.set(Calendar.SECOND, 0);
            _mainCalendar.set(Calendar.MILLISECOND, 0);
        }

        // create a copy, don't modify the original
        Calendar copy = (Calendar) date.clone();

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
                }
                break;
            case SWT.CENTER:
                if (_currentView == ISettings.VIEW_DAY) {
                    int middleHours = _hoursVisible / 2;
                    copy.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY));
                    copy.add(Calendar.HOUR_OF_DAY, -middleHours);
                } else {
                    int middle = _daysVisible / 2;
                    copy.add(Calendar.DATE, -middle);
                }
                break;
            case SWT.RIGHT:
                if (_currentView == ISettings.VIEW_DAY) {
                    copy.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY));
                    copy.add(Calendar.HOUR_OF_DAY, -_hoursVisible);
                } else {
                    copy.add(Calendar.DATE, -_daysVisible + 1);
                }
                break;
        }

        _mainCalendar = copy;
        _reCalculateScopes = true;
        _reCalculateSectionBounds = true;

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
    public void setDate(Calendar date, boolean applyOffset) {
        setDate(date, applyOffset, true);
    }

    private void setDate(Calendar date, boolean applyOffset, boolean redraw) {
        checkWidget();
        // create a copy, don't modify the original
        Calendar copy = (Calendar) date.clone();

        if (applyOffset) {
            copy.add(Calendar.DATE, _startCalendarAtOffset);
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
    public void reindex(GanttEvent event, int newIndex) {
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
    public void reindex(GanttSection section, int newIndex) {
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
    public void reindex(GanttGroup group, int newIndex) {
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
    public void addPhase(GanttPhase phase) {
        checkWidget();
        addPhase(phase, true);
    }

    /**
     * Removes a GanttPhase from the chart.
     * 
     * @param phase GanttPhase to remove
     */
    public void removePhase(GanttPhase phase) {
        removePhase(phase, true);
    }

    /**
     * Adds a {@link GanttSpecialDateRange} to the chart.
     * 
     * @param range {@link GanttSpecialDateRange} to add.
     */
    public void addSpecialDateRange(GanttSpecialDateRange range) {
        addSpecialDateRange(range, true);
    }

    /**
     * Removes a {@link GanttSpecialDateRange} to the chart and redraws.
     * 
     * @param range {@link GanttSpecialDateRange} to remove
     */
    public void removeSpecialDateRange(GanttSpecialDateRange range) {
        removeSpecialDateRange(range, true);
    }

    /**
     * Removes a {@link GanttSpecialDateRange} to the chart and redraws.
     * 
     * @param range {@link GanttSpecialDateRange} to remove
     * @param redraw true to redraw
     */
    public void removeSpecialDateRange(GanttSpecialDateRange range, boolean redraw) {
        checkWidget();
        _ganttSpecialDateRanges.remove(range);

        if (redraw) {
            redraw();
        }
    }

    /**
     * Removes all {@link GanttSpecialDateRange}s and redraws.
     */
    public void clearSpecialDateRanges() {
        checkWidget();
        _ganttSpecialDateRanges.clear();
        redraw();
    }

    /**
     * Adds a {@link GanttSpecialDateRange} and optionally redraws.
     * 
     * @param range {@link GanttSpecialDateRange} to add
     * @param redraw true to redraw
     */
    public void addSpecialDateRange(GanttSpecialDateRange range, boolean redraw) {
        checkWidget();
        if (!_ganttSpecialDateRanges.contains(range)) {
            _ganttSpecialDateRanges.add(range);
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
    public void addPhase(GanttPhase phase, boolean redraw) {
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
    public void removePhase(GanttPhase phase, boolean redraw) {
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
    public void addEvent(GanttEvent event) {
        checkWidget();
        addEvent(event, true);
    }

    /**
     * Adds an event at a given index.
     * 
     * @param event GanttEvent
     * @param index index
     */
    public void addEvent(GanttEvent event, int index) {
        checkWidget();
        if (!_ganttEvents.contains(event)) {
            _ganttEvents.add(index, event);
        }

        eventNumbersChanged();

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
    public void addEvent(GanttEvent event, boolean redraw) {
        checkWidget();
        if (!_ganttEvents.contains(event)) {
            _ganttEvents.add(event);
        }

        eventNumbersChanged();

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
     */
    public void removeEvent(GanttEvent event) {
        checkWidget();
        _ganttEvents.remove(event);
        ArrayList toRemove = new ArrayList();
        for (int i = 0; i < _ganttConnections.size(); i++) {
            GanttConnection con = (GanttConnection) _ganttConnections.get(i);

            if (con.getSource().equals(event) || con.getTarget().equals(event)) {
                toRemove.add(con);
            }

        }
        for (int i = 0; i < toRemove.size(); i++) {
            _ganttConnections.remove(toRemove.get(i));
        }

        eventNumbersChanged();

        redrawEventsArea();
    }

    /**
     * Returns all currently connected events as a list of {@link GanttConnection} objects.
     * 
     * @return List of connections.
     */
    public List getGanttConnections() {
        return _ganttConnections;
    }

    void eventDatesChanged(GanttEvent ge, boolean redraw) {
        int newStartX = getXForDate(ge.getActualStartDate());
        int newEndX = getXForDate(ge.getActualEndDate());

        // if we're zoomed in to see hours, we don't modify the end date
        if (_currentView != ISettings.VIEW_DAY) {
            newEndX += getDayWidth();
        }

        ge.updateX(newStartX);
        ge.updateWidth(newEndX - newStartX);
        if (redraw) redraw();
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
        _forceScrollbarsUpdate = true;
        _verticalScrollPosition = 0;
        _lastVerticalScrollPosition = 0;
        _verticalScrollBar.setSelection(0);
        redraw();
    }

    /**
     * Clears all GanttGroups from the chart.
     */
    public void clearGanttGroups() {
        checkWidget();
        _ganttGroups.clear();
        eventNumbersChanged();
        _forceScrollbarsUpdate = true;
        _verticalScrollPosition = 0;
        _lastVerticalScrollPosition = 0;
        _verticalScrollBar.setSelection(0);
        redraw();
    }

    /**
     * Clears all GanttSections from the chart.
     */
    public void clearGanttSections() {
        checkWidget();
        _ganttSections.clear();
        eventNumbersChanged();
        _forceScrollbarsUpdate = true;
        _verticalScrollPosition = 0;
        _lastVerticalScrollPosition = 0;
        _verticalScrollBar.setSelection(0);
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
        // mGmap.clear();
        eventNumbersChanged();
        _forceScrollbarsUpdate = true;
        _verticalScrollPosition = 0;
        _lastVerticalScrollPosition = 0;
        _verticalScrollBar.setSelection(0);
        flagForceFullUpdate();
        redraw();
    }

    /**
     * Checks whether the chart has a given event.
     * 
     * @param event GanttEvent
     * @return true if event exists
     */
    public boolean hasEvent(GanttEvent event) {
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

    // used in useFastDraw
    void setNoRecalc() {
        _reCalculateScopes = false;
        _reCalculateSectionBounds = false;
    }

    private void eventNumbersChanged() {
        _allEventsCombined.clear();

        for (int i = 0; i < _ganttEvents.size(); i++) {
            Object obj = _ganttEvents.get(i);
            if (obj instanceof GanttEvent) {
                _allEventsCombined.add(obj);
            } else if (obj instanceof GanttGroup) {
                _allEventsCombined.addAll(((GanttGroup) obj).getEventMembers());
            }
        }
        for (int i = 0; i < _ganttGroups.size(); i++) {
            GanttGroup gg = (GanttGroup) _ganttGroups.get(i);
            _allEventsCombined.addAll(gg.getEventMembers());
        }

        flagForceFullUpdate();
    }

    // moves the x bounds of all events one day width left or right
    void moveXBounds(boolean positive) {
        Object[] objs = _allEventsCombined.toArray();

        int dw = getDayWidth();
        if (_currentView == ISettings.VIEW_YEAR) {
            Calendar temp = Calendar.getInstance(_defaultLocale);
            temp.setTime(_mainCalendar.getTime());
            temp.add(Calendar.MONTH, positive ? 1 : -1);

            long days = DateHelper.daysBetween(_mainCalendar, temp, _defaultLocale);
            dw = Math.abs((int) days * dw);
        }

        for (int i = 0; i < _allEventsCombined.size(); i++) {
            GanttEvent ge = (GanttEvent) objs[i];
            ge.updateX(ge.getX() + (positive ? dw : -dw));
        }
    }

    /*private void moveXBounds(int move) {
    	Object[] objs = mAllEventsCombined.toArray();

    	for (int i = 0; i < objs.length; i++) {
    		GanttEvent ge = (GanttEvent) objs[i];
    		ge.updateX(ge.getX() - move);
    	}

    	updateEventVisibilities(mVisibleBounds);
    }*/

    private void moveYBounds(int move) {
        Object[] objs = _allEventsCombined.toArray();

        for (int i = 0; i < objs.length; i++) {
            GanttEvent ge = (GanttEvent) objs[i];
            ge.updateY(ge.getY() - move);
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
    public boolean isEventVisible(GanttEvent event, Rectangle bounds) {
        return (getEventVisibility(event, bounds) == VISIBILITY_VISIBLE);
    }

    // checks whether an event is visible in the current date range that is
    // displayed on the screen
    private int getEventVisibility(GanttEvent event, Rectangle bounds) {
        // if we're saving the chart as an image, everything is visible unless it's truly hidden
        if (_savingChartImage) {
            if (!event.isHidden()) { return VISIBILITY_VISIBLE; }
        }

        // fastest checks come first, if it's not a visible layer, it's not visible
        if (_hiddenLayers.contains(new Integer(event.getLayer()))) return VISIBILITY_NOT_VISIBLE;

        // if event is missing dates, don't let it show, fix to #281983
        if (event.getActualStartDate() == null || event.getActualEndDate() == null) { return VISIBILITY_NOT_VISIBLE; }

        // our second check is the check whether it's out of bounds vertically, if so we can return right away (and scope calculation
        // takes the special OOB_HEIGHT into account when counting the vertical offset

        // as we offset the entire view area when scrolling vertically by moving the events up or down vertically
        // we need to check the offset as if they were still in their original position, which we do by taking their y location
        // and adding on the vertical scroll position. Once we have those fake bounds, we simply check it against the visual area
        // and if they're not inside, they're out!
        Rectangle fakeBounds = new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight());
        fakeBounds.y += _verticalScrollPosition;

        // first draw everything is zero, ignore that one
        if (event.getY() != 0) {
            if (fakeBounds.y > _visibleBounds.y + _visibleBounds.height) { return VISIBILITY_OOB_HEIGHT_BOTTOM; }
            if ((fakeBounds.y + fakeBounds.height) < _visibleBounds.y) { return VISIBILITY_OOB_HEIGHT_TOP; }
        }

        Calendar sCal = event.getActualStartDate();
        Calendar eCal = event.getActualEndDate();
        if (sCal == null) sCal = event.getRevisedStart();
        if (eCal == null) eCal = event.getRevisedEnd();

        // scope checking
        if (event.isScope()) {
            GanttEvent earliest = event.getEarliestScopeEvent();
            GanttEvent latest = event.getLatestScopeEvent();
            if (earliest != null) {
                sCal = earliest.getActualStartDate();
            }
            if (latest != null) {
                eCal = latest.getActualEndDate();
            }

            if (sCal == null || eCal == null) { return VISIBILITY_NOT_VISIBLE; }
        }

        // if an event has movement constraints we draw a marker around it to display this fact, thus,
        // if that marker expands beyond the size of the event, we need to assume that the event visibility is actually
        // the size between the constraints and not just the event itself. If we were not to do this calculation
        // the boundary box would not be drawn when the event was not in visible range, which would be very odd to the user
        // as it would suddenly appear when the event became visible, but they could not view how far it expanded without zooming out.
        if (event.hasMovementConstraints()) {
            if (event.getNoMoveBeforeDate() != null && event.getNoMoveBeforeDate().before(sCal)) sCal = event.getNoMoveBeforeDate();
            if (event.getNoMoveAfterDate() != null && event.getNoMoveAfterDate().after(eCal)) eCal = event.getNoMoveAfterDate();
        }

        // if we don't have width, check using dates, this happens on the
        // initial draw and when events are outside of the picture
        if (event.getWidthWithText() == 0) {
            Date eStart = sCal.getTime();
            Date eEnd = eCal.getTime();
            Calendar temp = Calendar.getInstance(_defaultLocale);
            temp.setTime(_mainCalendar.getTime());

            long calStart = temp.getTimeInMillis();
            if (_daysVisible != 0) temp.add(Calendar.DATE, _daysVisible);
            else temp.setTime(_endCalendar.getTime());

            long calEnd = temp.getTimeInMillis();

            if (eStart.getTime() >= calStart && eStart.getTime() <= calEnd) return VISIBILITY_VISIBLE;

            if (eEnd.getTime() >= calStart && eEnd.getTime() <= calEnd) return VISIBILITY_VISIBLE;

            // event spans entire screen, also fix to Bugzilla bug #236846 - https://bugs.eclipse.org/bugs/show_bug.cgi?id=236846
            if (eStart.getTime() <= calStart && eEnd.getTime() >= calEnd) return VISIBILITY_VISIBLE;

        } else {
            int xStart = getStartingXfor(sCal);
            int xEnd = getXForDate(eCal);

            int buffer = _settings.getArrowHeadEventSpacer();
            xEnd += buffer;
            xStart -= buffer;

            // account for the actual text
            // TODO: we should account for other text locations
            if (event.getHorizontalTextLocation() == SWT.RIGHT) {
                xEnd += event.getNameExtent().x;
            }

            if (xEnd < 0) { return VISIBILITY_OOB_LEFT; }

            if (xStart > bounds.width) { return VISIBILITY_OOB_RIGHT; }

            // if the event was OOB before, height-wise, but no longer is, and we flagged it as bounds are not set
            // (which we do when the event will have moved due to zoom or other)
            // we need to force an update of the bounds. Since we need the xStart/xEnd anyway, we set them here as it's one method call less
            if (event.getVisibility() == VISIBILITY_OOB_HEIGHT_TOP || event.getVisibility() == VISIBILITY_OOB_HEIGHT_BOTTOM) {
                if (!event.isBoundsSet()) {
                    event.updateX(xStart);
                    event.updateWidth(xEnd - xStart + getDayWidth());
                }
            }

            return VISIBILITY_VISIBLE;
        }

        return VISIBILITY_NOT_VISIBLE;
    }

    // gets the x position for where the event bar should start
    private int getStartingXforEvent(GanttEvent event) {
        if (_currentView == ISettings.VIEW_DAY) {
            return getStartingXforEventHours(event);
        } else {
            return getStartingXfor(event.getActualStartDate());
        }
    }

    /**
     * Returns the starting x position for a given date in the current view.
     * 
     * @param date Date
     * @return x position, -1 should it for some reason not be found
     */
    public int getStartingXforEventDate(Calendar date) {
        return getStartingXfor(date);
    }

    private int getStartingXforEventHours(GanttEvent event) {
        return getStartingXForEventHours(event.getActualStartDate());
    }

    private int getStartingXForEventHours(Calendar start) {
        Calendar temp = Calendar.getInstance(_defaultLocale);
        temp.setTime(_mainCalendar.getTime());

        // some stuff we know, (to help program this)
        // 1 dayWidth is one working hour, thus, 1 dayWidth / 60 = 1 minute
        // 1 day is the same as the week width

        int dw = getDayWidth();
        int daysBetween = (int) DateHelper.daysBetween(temp, start, _defaultLocale);
        int ret = daysBetween * _weekWidth;
        ret += _mainBounds.x;

        // days is ok, now deal with hours
        float hoursBetween = DateHelper.hoursBetween(temp, start, _defaultLocale, true);
        float minutesBetween = DateHelper.minutesBetween(temp.getTime(), start.getTime(), _defaultLocale, true, false);

        float minPixels = 0;

        // now deal with minutes, if settings say so
        if (!_settings.roundHourlyEventsOffToNearestHour()) {
            float remainderMins = minutesBetween - (hoursBetween * 60);
            float oneMinWidth = ((float) dw) / 60;
            minPixels = oneMinWidth * remainderMins;
        }

        ret += (hoursBetween * dw) + minPixels;

        return ret;
    }

    /**
     * Returns the starting x for a given date.
     * 
     * @param date Calendar date
     * @return x position or -1 if it for some reason should not be found
     */
    public int getStartingXfor(Calendar date) {
        checkWidget();

        if (_currentView == ISettings.VIEW_DAY) { return getStartingXForEventHours(date); }

        if (date == null) { return _mainBounds.x; }

        Calendar temp = Calendar.getInstance(_defaultLocale);
        temp.setTime(_mainCalendar.getTime());
        if (_currentView == ISettings.VIEW_YEAR) {
            temp.set(Calendar.DAY_OF_MONTH, 1);
        }

        long daysBetween = DateHelper.daysBetween(temp, date, _defaultLocale);
        int dw = getDayWidth();

        int extra = 0;
        if (_settings.drawEventsDownToTheHourAndMinute()) {
            if (_currentView != ISettings.VIEW_DAY) {
                float ppm = 60f / _dayWidth;

                int mins = date.get(Calendar.HOUR_OF_DAY);
                extra = (int) (mins * ppm);
            }
        }

        return _mainBounds.x + ((int) daysBetween * dw) + extra;
    }

    private int getXLengthForEventHours(GanttEvent event) {
        return getXForDate(event.getActualEndDate()) - getXForDate(event.getActualStartDate());
    }

    // gets the x position for where the event bar should end
    private int getXLengthForEvent(GanttEvent event) {
        if (_currentView == ISettings.VIEW_DAY) { return getXLengthForEventHours(event); }

        // +1 as it's the end date and we include the last day
        return (event.getDaysBetweenStartAndEnd() + 1) * getDayWidth();
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

        return dw;
    }

    // checks whether an x/y position is inside the given rectangle
    private boolean isInside(int x, int y, Rectangle rect) {
        if (rect == null) { return false; }

        return x >= rect.x && y >= rect.y && x <= (rect.x + rect.width) && y <= (rect.y + rect.height);
    }

    // open edit dialogs
    public void mouseDoubleClick(MouseEvent me) {
        // if we only listen to selected events we won't catch locked or
        // otherwise disabled events, up to the user in the end, not us.
        killDialogs();

        // BUG FIX: If a dialog/shell is opened by the user on a double click event, the mouse incorrectly still thinks
        // it's in mouse-down mode, so we need to force it to not thinking that
        killMouseState();

        for (int i = 0; i < _ganttEvents.size(); i++) {
            GanttEvent event = (GanttEvent) _ganttEvents.get(i);

            if (isInside(me.x, me.y, new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight()))) {
                for (int j = 0; j < _eventListeners.size(); j++) {
                    ((IGanttEventListener) _eventListeners.get(j)).eventDoubleClicked(event, me);
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
        return _selectedHeaderDates;
    }

    /**
     * Clears all selected headers and redraws the chart.
     */
    public void clearAllSelectedHeaders() {
        _selectedHeaderDates.clear();
        redraw();
    }

    /**
     * Sets a list of header dates that should be the selected dates. This list must be a list of Calendars.
     * 
     * @param dates List of Calendar objects representing selected header dates.
     */
    public void setSelectedHeaderDates(List dates) {
        _selectedHeaderDates = dates;
        redraw();
    }

    /**
     * Selects a region of events.
     * 
     * @param rect Rectangle of region to select
     * @param me
     */
    private void doMultiSelect(Rectangle rect, MouseEvent me) {
        if (rect == null) return;

        for (int i = 0; i < _ganttEvents.size(); i++) {
            GanttEvent ge = (GanttEvent) _ganttEvents.get(i);
            if (ge.isScope()) continue;

            if (ge.getBounds().intersects(rect)) _selectedEvents.add(ge);
        }

        for (int x = 0; x < _eventListeners.size(); x++) {
            IGanttEventListener listener = (IGanttEventListener) _eventListeners.get(x);
            listener.eventSelected(null, _selectedEvents, me);
        }

        redraw();
    }

    public void mouseDown(MouseEvent me) {
        if (me.button == 1) {
            _mouseIsDown = true;
        }

        Point ctrlPoint = toDisplay(new Point(me.x, me.y));

        if (_settings.allowBlankAreaDragAndDropToMoveDates() && me.button == 1) setCursor(CURSOR_HAND);

        // remove old dotted border, we used to just create a new GC and clear it, but to be honest, it was a bit of a hassle with some calculations
        // and left over cheese, and a redraw is just faster. It's rare enough anyway, and we don't redraw more than a small area
        if (_selectedEvents.size() != 0 && _settings.drawSelectionMarkerAroundSelectedEvent()) {
            for (int x = 0; x < _selectedEvents.size(); x++) {
                GanttEvent selEvent = (GanttEvent) _selectedEvents.get(x);
                redraw(selEvent.getX() - 2, selEvent.getY() - 2, selEvent.getWidth() + 4, selEvent.getHeight() + 4, false);
            }
        }

        // header clicks, if it's visible
        if (_settings.drawHeader() && _settings.allowHeaderSelection()) {
            Rectangle headerBounds = new Rectangle(_mainBounds.x, _settings.getHeaderMonthHeight() + _verticalScrollPosition, _mainBounds.width, _settings.getHeaderDayHeight());

            if (isInside(me.x, me.y, headerBounds)) {
                // we account for section bar width
                Calendar cal = getDateAt(me.x - _mainBounds.x);

                if (me.stateMask == SWT.MOD1 || me.stateMask == SWT.SHIFT || me.stateMask == (SWT.MOD1 | SWT.SHIFT)) {
                    if (me.stateMask == SWT.MOD1) {
                        if (_selectedHeaderDates.contains(cal)) _selectedHeaderDates.remove(cal);
                        else _selectedHeaderDates.add(cal);
                    } else {
                        // shift or ctrl + shift
                        Calendar last = (Calendar) _selectedHeaderDates.get(_selectedHeaderDates.size() - 1);
                        long days = 0;
                        boolean reverse = false;
                        if (last.before(cal)) {
                            days = DateHelper.daysBetween(last, cal, _defaultLocale);
                        } else {
                            days = DateHelper.daysBetween(cal, last, _defaultLocale);
                            reverse = true;
                        }

                        // all dates
                        Calendar temp = Calendar.getInstance(_defaultLocale);
                        temp.setTime(last.getTime());
                        for (int i = 0; i < (int) days; i++) {
                            temp.add(Calendar.DATE, reverse ? -1 : 1);
                            Calendar temp2 = Calendar.getInstance(_defaultLocale);
                            temp2.setTime(temp.getTime());
                            _selectedHeaderDates.add(temp2);
                        }
                    }
                } else {
                    if (_selectedHeaderDates.contains(cal)) {
                        _selectedHeaderDates.clear();
                    } else {
                        _selectedHeaderDates.clear();
                        _selectedHeaderDates.add(cal);
                    }
                }

                for (int i = 0; i < _eventListeners.size(); i++) {
                    ((IGanttEventListener) _eventListeners.get(i)).eventHeaderSelected(cal, _selectedHeaderDates);
                }

                redraw();
                return;
            }

        }

        // deal with selection
        for (int i = 0; i < _ganttEvents.size(); i++) {
            GanttEvent event = (GanttEvent) _ganttEvents.get(i);

            if (event.isScope() && !_settings.allowScopeMenu()) continue;

            if (isInside(me.x, me.y, new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight()))) {
                GC gc = new GC(this);

                // if it's a scope and menu is allowed, we can finish right here
                if (me.button == 3 && event.isScope()) {
                    showMenu(ctrlPoint.x, ctrlPoint.y, event, me);
                    // dispose or we have a memory leak, as we're returning!
                    gc.dispose();
                    return;
                }

                if (me.stateMask == 0 || !_multiSelect) _selectedEvents.clear();

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
                if ((_selectedEvents.size() > 0)) {// && !mSelectedEvents.contains(event)) || mSelectedEvents.size() == 0) {
                    for (int x = 0; x < _eventListeners.size(); x++) {
                        IGanttEventListener listener = (IGanttEventListener) _eventListeners.get(x);
                        listener.eventSelected(event, _selectedEvents, me);
                    }
                }

                for (int x = 0; x < _selectedEvents.size(); x++) {
                    GanttEvent selEvent = (GanttEvent) _selectedEvents.get(x);
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
                if (me.button == 3 && _selectedEvents.size() > 0) showMenu(ctrlPoint.x, ctrlPoint.y, (GanttEvent) _selectedEvents.get(_selectedEvents.size() - 1), me);

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
            setCursor(CURSOR_NONE);
            _tracker = new Tracker(this, SWT.RESIZE);
            _tracker.setCursor(CURSOR_NONE);
            _tracker.setStippled(true);
            _tracker.setRectangles(new Rectangle[] { new Rectangle(me.x, me.y, 1, 1) });
            // this blocks until the Tracker is disposed, so on success, we multiselect
            if (_tracker.open()) {
                doMultiSelect(_tracker.getRectangles()[0], me);
            }
            return;
        }

        if (me.stateMask == 0) _selectedEvents.clear();

        if (_settings.showOnlyDependenciesForSelectedItems()) {
            redrawEventsArea();
        }

    }

    private void drawSelectionAroundEvent(GC gc, GanttEvent ge, int x, int y, int eventWidth, Rectangle bounds) {
        if (!_settings.drawSelectionMarkerAroundSelectedEvent()) return;

        gc.setLineStyle(SWT.LINE_DOT);

        // this is _extremely_ slow to draw, so we need to check bounds here, which is probably a good idea anyway
        boolean oobLeft = (x < bounds.x);
        boolean oobRight = (x + eventWidth > bounds.width);

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
    public void mouseUp(MouseEvent event) {
        _mouseIsDown = false;

        boolean doRedraw = false;

        if (_tracker != null) {
            _tracker.dispose();
            _tracker = null;
        }

        if (_dragEvents.size() > 0) {
            if (_resizing) {
                for (int i = 0; i < _eventListeners.size(); i++) {
                    ((IGanttEventListener) _eventListeners.get(i)).eventsResizeFinished(_dragEvents, event);
                }
            }
            if (_dragging) {
                for (int i = 0; i < _eventListeners.size(); i++) {
                    ((IGanttEventListener) _eventListeners.get(i)).eventsMoveFinished(_dragEvents, event);
                }
            }
           
        }

        // vertical dragging "end"
        if (_freeFloatDragging) {
            // clear any temporary DND events held in the sections
            for (int i = 0; i < _ganttSections.size(); i++) {
                GanttSection gs = (GanttSection) _ganttSections.get(i);
                gs.clearDNDGanttEvents();
            }

            // dragging is done
            _freeFloatDragging = false;
            _verticalDraggingDirection = SWT.NONE;

            // we now need to [potentially] re-align the vertical event so it's not offset by some pixels 
            // this deals with reordering events etc after a DND
            handlePostVerticalDragDrop();

            // done with it, clear it
            _verticalDragDropManager.clear();
        }
        
        // put all undo/redo commands into one command as any user would expect a multi-DND to undo with all events, not just one at a time
        ClusteredCommand cc = new ClusteredCommand();
        
        // undo/redo handling
        for (int i = 0; i < _dragEvents.size(); i++) {
            GanttEvent ge = (GanttEvent) _dragEvents.get(i);
            ge.moveFinished();
            // the event knows if it's resized or moved and will return the correct event accordingly
            IUndoRedoCommand undoCommand = ge.getPostMoveOrResizeUndoCommand();
            cc.addCommand(undoCommand);
        }
        if (cc.size() > 0) {
            _undoRedoManager.record(cc);
        }

        if (_dragPhase != null) {
            boolean notify = true;

            // if we don't allow overlaps, we need to check if we're about to drag or resize onto
            // an existing phase
            if (!_settings.allowPhaseOverlap()) {
                // check if we overlap
                for (int i = 0; i < _ganttPhases.size(); i++) {
                    GanttPhase gp = (GanttPhase) _ganttPhases.get(i);
                    if (gp == _dragPhase) {
                        continue;
                    }

                    // if we overlap, undo the last DND
                    if (gp.getBounds().intersects(_dragPhase.getBounds())) {
                        _dragPhase.undoLastDragDrop();
                        notify = false;
                    }
                }
            }

            if (notify) {
                if (_resizing) {
                    for (int i = 0; i < _eventListeners.size(); i++) {
                        ((IGanttEventListener) _eventListeners.get(i)).phaseResizeFinished(_dragPhase, event);
                    }
                }
                if (_dragging) {
                    for (int i = 0; i < _eventListeners.size(); i++) {
                        ((IGanttEventListener) _eventListeners.get(i)).phaseMoveFinished(_dragPhase, event);
                    }
                }
            } else {
                redraw();
            }
        }

        endEverything();
        updateHorizontalScrollbar();

        if (doRedraw) {
            redraw();
        }
    }

    /**
     * This method deals with moving events around so that vertical gaps are filled where they left holes after a
     * vertical drag/drop
     */
    private void handlePostVerticalDragDrop() {
        if (_dragEvents.size() == 0) {
            redraw();
            return;
        }

        int vDragMode = _settings.getVerticalEventDragging();

        // loop all dragged events
        for (int i = 0; i < _dragEvents.size(); i++) {
            GanttEvent ge = (GanttEvent) _dragEvents.get(i);

            GanttEvent top = _verticalDragDropManager.getTopEvent();
            GanttEvent bottom = _verticalDragDropManager.getBottomEvent();
            GanttSection targetSection = _verticalDragDropManager.getTargetSection();

            //System.err.println(ge + " is getting retargeted from " + ge.getGanttSection() + " to " + targetSection + " between " + top + " and " + bottom);

            // only allow DND across sections?
            if (ge.getGanttSection() != null && targetSection != null && ge.getGanttSection() == targetSection && vDragMode == IVerticalDragModes.CROSS_SECTION_VERTICAL_DRAG) {
                ge.undoVerticalDragging();
                continue;
            }

            // notify listeners
            if (ge.getGanttSection() != targetSection && hasGanttSections()) {
                for (int x = 0; x < _eventListeners.size(); x++) {
                    ((IGanttEventListener) _eventListeners.get(x)).eventMovedToNewSection(ge, ge.getGanttSection(), targetSection);
                }
            } else {
                for (int x = 0; x < _eventListeners.size(); x++) {
                    ((IGanttEventListener) _eventListeners.get(x)).eventReordered(ge);
                }
            }

            if (top == null && bottom == null) {
                // it'll be alone in a section
                if (targetSection != null) {
                    ge.reparentToNewGanttSection(0, targetSection);
                }

                continue;
            }

            int index = 0;

            // event was moved nowhere vertically, same place
            if (ge.getY() == ge.getPreVerticalDragBounds().y) {
                continue;
            }

            if (!hasGanttSections()) {
                if (top != null) {
                    index = _ganttEvents.indexOf(top);
                }
                if (ge.wasVerticallyMovedUp()) {
                    index++;
                }

                // get the index of the top event
                _ganttEvents.remove(ge);
                _ganttEvents.add(index, ge);
            } else {
                GanttSection fromSection = ge.getGanttSection();
                //List fromEvents = fromSection.getEvents();
                if (targetSection == null) {
                    // TODO: This seems to happen when user drops event on border between two, uncomment line below and do a drop on border, see if we can handle differently
                    targetSection = fromSection;
                }
                List toEvents = targetSection.getEvents();

                if (top == null) {
                    index = 0;
                }
                if (bottom == null) {
                    index = toEvents.size();
                }

                if (top != null && bottom != null) {
                    index = toEvents.indexOf(top);
                }

                // if moved up we need to push the index by 1, unless it was moved to the top of a section, then we don't push it or it'd never go to the top
                if (ge.wasVerticallyMovedUp() && ge.getGanttSection() == targetSection) {
                    if (top != null) {
                        index++;
                    }
                }
                // moved up from a section to inbetween two events (not at top)
                else if (ge.wasVerticallyMovedUp() && ge.getGanttSection() != targetSection) {
                    if (top != null) {
                        index++;
                    }
                }

                // moved down from one section to another, push it down (unless at the top of the new section, same reasoning as before)
                if (!ge.wasVerticallyMovedUp() && ge.getGanttSection() != targetSection) {
                    if (top != null) {
                        index++;
                    }
                }

                if (index < 0) {
                    index = 0;
                }

                //System.err.println("Index " + index);

                fromSection.removeGanttEvent(ge);
                targetSection.addGanttEvent(index, ge);
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
     *        section, if user is between two sections this may play a part
     * @return Section it is over or null if none
     */
    private GanttSection getSectionForVerticalDND(GanttEvent event, boolean accountForVerticalDragDirection) {
        if (!hasGanttSections()) { return null; }

        for (int i = 0; i < _ganttSections.size(); i++) {
            GanttSection gs = (GanttSection) _ganttSections.get(i);

            // must account for scroll position as the event itself has no clue there's a scrollbar and obviously doesn't account for it
            if (gs.getBounds().contains(event.getX(), event.getY() + _verticalScrollPosition)) { return gs; }
        }

        // if we get here, we had a null hit, which means we might be between two sections, make it easy, check if we're oob first
        if (event.getY() < getHeaderHeight()) {
            // we could return null but that's not a good idea, rather, lets tell the target it's still in the same section
            return event.getGanttSection();
        }

        // ok, we're not oob at the top, so we're probably between two, easy check
        for (int i = 0; i < _ganttSections.size(); i++) {
            GanttSection gs = (GanttSection) _ganttSections.get(i);
            GanttSection next = null;
            if (i != _ganttSections.size() - 1) {
                next = (GanttSection) _ganttSections.get(i + 1);

                if (next != null) {
                    if (event.getY() >= (gs.getBounds().y + gs.getBounds().height) && event.getY() <= next.getBounds().y) {
                        if (accountForVerticalDragDirection) {
                            if (_verticalDraggingDirection == SWT.UP) {
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
     * Returns a list of all surrounding vertical events to a given event. If a GanttSection is given, only the events
     * in that section will be used in calculating.
     * 
     * @param event Event to get surrounding events for
     * @param section GanttSection optional (pass null for using all events)
     * @return 2-size array with entry 0 being the top event and entry 1 being the bottom event. Any or all can be null.
     */
    private List getSurroundingVerticalEvents(GanttEvent event, GanttSection section) {

        final List allEvents = section == null ? _ganttEvents : section.getEvents();

        List ret = new ArrayList();

        // no events
        if (allEvents.isEmpty()) {
            ret.add(null);
            ret.add(null);
            return ret;
        }

        List sorted = new ArrayList();
        for (int i = 0; i < allEvents.size(); i++) {
            IGanttChartItem ge = (IGanttChartItem) allEvents.get(i);
            if (ge instanceof GanttEvent) {
                if (((GanttEvent) ge).isHidden()) {
                    continue;
                }

                sorted.add(ge);
            }
        }

        Collections.sort(sorted, new Comparator() {

            public int compare(Object one, Object two) {
                GanttEvent ge1 = (GanttEvent) one;
                GanttEvent ge2 = (GanttEvent) two;

                Integer i1 = new Integer(ge1.getY());
                Integer i2 = new Integer(ge2.getY());

                return i1.compareTo(i2);
            }

        });

        GanttEvent nearestUp = null;
        GanttEvent nearestDown = null;

        for (int i = 0; i < sorted.size(); i++) {
            GanttEvent cur = (GanttEvent) sorted.get(i);
            //if (i == sorted.size() - 1) {
            //  break;
            //}
            GanttEvent next = null;
            if (i < sorted.size() - 1) {
                next = (GanttEvent) sorted.get(i + 1);
            }
            //System.err.println("Next: " + next + " " + event.getY());
            /*            if (next != null) {
                            System.err.println(cur + " || - if ("+event.getY()+ " < " + cur.getY()+" && " + next.getY() + " > " + event.getY() + ")");
                        }
                        else {
                            System.err.println(cur + " || - if ("+event.getY()+ " < " + cur.getY()+")");
                        }
            */
            if (event.getY() < cur.getY() && (next == null || (next != null && next.getY() > event.getY()))) {
                if (nearestDown != null) {
                    // if it's closer to us than the last one we registered, use it, otherwise ignore it
                    if (cur.getY() < nearestDown.getY()) {
                        nearestDown = cur;
                    }
                } else {
                    nearestDown = cur;
                }

                int x = i;
                while (true) {
                    if (x < 0) {
                        break;
                    }
                    GanttEvent ge = (GanttEvent) sorted.get(x);
                    if (ge.getY() < nearestDown.getY() && ge != event) {
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
        }
        if (first == event && sorted.size() > 1) {
            first = (GanttEvent) sorted.get(1);
        }

        int topMostY = first.getY();
        int botMostY = ((GanttEvent) sorted.get(sorted.size() - 1)).getY();

        if (event.getY() < topMostY) {
            if (section != null) {
                nearestUp = null;
                nearestDown = (GanttEvent) allEvents.get(0);
            } else {
                nearestUp = null;
            }
        }
        if ((event.getY() + event.getHeight()) > botMostY) {
            if (section != null) {
                nearestUp = (GanttEvent) allEvents.get(allEvents.size() - 1);
                nearestDown = null;
            } else {
                nearestDown = null;
            }
        }

        /*        if (section != null) {
                    if (nearestDown == null) {
                        nearestDown = (GanttEvent) allEvents.get(allEvents.size()-1);
                    }
                }
        */
        //        System.err.println("Up: " + nearestUp);
        //        System.err.println("Down: " + nearestDown);
        ret.add(nearestUp);
        ret.add(nearestDown);

        return ret;
    }

    public void keyPressed(KeyEvent e) {
        // an attempt at ending everything
        if (e.keyCode == SWT.ESC) {

            List scopeEventsToUpdate = new ArrayList();

            // if we were in the middle of a drag or resize, we cancel the move and reset the dates back to before the move started
            if (_dragEvents.size() > 0) {
                for (int i = 0; i < _dragEvents.size(); i++) {
                    GanttEvent ge = (GanttEvent) _dragEvents.get(i);
                    ge.moveCancelled();
                    if (ge.getScopeParent() != null) {
                        if (!scopeEventsToUpdate.contains(ge.getScopeParent())) scopeEventsToUpdate.add(ge.getScopeParent());
                    }
                }
            }

            // update any scope events that we found during the cancel or they won't redraw in their right spots
            for (int i = 0; i < scopeEventsToUpdate.size(); i++) {
                updateScopeXY(((GanttEvent) scopeEventsToUpdate.get(i)));
            }

            _dragPhase = null;
            _dragEvents.clear();
            endEverything();
            _selectedEvents.clear();
            killDialogs();
            if (_freeFloatDragging) {
                heavyRedraw();
            } else {
                redraw();
            }
            setCursor(CURSOR_NONE);
        }
    }

    public void keyReleased(KeyEvent e) {
        checkWidget();
        endEverything();
    }

    private void endEverything() {
        if (_tracker != null) {
            _tracker.dispose();
            _tracker = null;
            _mouseIsDown = false;
        }

        // oh, not quite yet
        if (_mouseIsDown) { return; }

        _initialHoursDragOffset = 0;
        _justStartedMoveOrResize = false;

        _mouseIsDown = false;
        _mouseDragStartLocation = null;
        _dragStartDate = null;

        // kill auto scroll
        endAutoScroll();

        _showZoomHelper = false;
        if (_zoomLevelHelpArea != null) {
            redraw(_zoomLevelHelpArea.x, _zoomLevelHelpArea.y, _zoomLevelHelpArea.width + 1, _zoomLevelHelpArea.height + 1, false);
        }

        _dragging = false;
        _dragStartLocation = null;
        _dragEvents.clear();
        _dragPhase = null;
        _resizing = false;
        this.setCursor(CURSOR_NONE);
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

            if (_tracker != null) { return; }

            String dateFormat = (_currentView == ISettings.VIEW_DAY ? _settings.getHourDateFormat() : _settings.getDateFormat());

            // if we moved mouse back in from out of bounds, kill auto scroll
            Rectangle area = super.getClientArea();
            if (me.x >= area.x && me.x < area.width) {
                endAutoScroll();
            }

            boolean dndOK = _settings.enableDragAndDrop();
            boolean resizeOK = _settings.enableResizing();

            // check if we need to auto-scroll
            if ((_dragging || _resizing) && _settings.enableAutoScroll()) {
                if (me.x < 0 || me.x > _mainBounds.width) {
                    doAutoScroll(me);
                }
                if (_freeFloatDragging) {
                    if (me.y > _mainBounds.height || me.y < 0) {
                        doAutoScroll(me);
                    }
                }
            }

            // handle cross-section DND's, events need to be temporarily added to whatever
            // section they are being dragged over or they will not be rendered.
            if (_freeFloatDragging && _dragging && !_ganttSections.isEmpty() && !_dragEvents.isEmpty()) {
                GanttEvent drag = (GanttEvent) _dragEvents.get(0);
                for (int i = 0; i < _ganttSections.size(); i++) {
                    GanttSection gs = (GanttSection) _ganttSections.get(i);
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

            // phase move/resize (check first as it's faster than looping events)
            if (_dragPhase != null && (_resizing || _dragging)) {
                AdvancedTooltipDialog.kill();
                GanttToolTip.kill();
                if (_dragging && dndOK) {
                    handlePhaseMove(me, _dragPhase, TYPE_MOVE, true);
                }
                if (_resizing && resizeOK && (_cursor == SWT.CURSOR_SIZEE || _cursor == SWT.CURSOR_SIZEW)) {
                    handlePhaseMove(me, _dragPhase, _lastLeft ? TYPE_RESIZE_LEFT : TYPE_RESIZE_RIGHT, true);
                }
                return;
            }

            // move
            if (_dragging && dndOK) {
                AdvancedTooltipDialog.kill();
                GanttToolTip.kill();
                for (int x = 0; x < _selectedEvents.size(); x++) {
                    handleMove(me, (GanttEvent) _selectedEvents.get(x), TYPE_MOVE, x == 0);
                }
                return;
            }

            // resize
            if (_resizing && resizeOK && (_cursor == SWT.CURSOR_SIZEE || _cursor == SWT.CURSOR_SIZEW)) {
                for (int x = 0; x < _selectedEvents.size(); x++) {
                    handleMove(me, (GanttEvent) _selectedEvents.get(x), _lastLeft ? TYPE_RESIZE_LEFT : TYPE_RESIZE_RIGHT, x == 0);
                }
                return;
            }

            // when we move a mouse really fast at the beginning of a move or a
            // drag, we need to catch that seperately
            // as "isInside()" will not have a chance to see if it's true or not
            // (below)
            if (!_resizing && !_dragging && dndOK && resizeOK && me.stateMask != 0 && _cursor != SWT.NONE) {
                if (_selectedEvents.size() > 0) {
                    if ((_cursor == SWT.CURSOR_SIZEE || _cursor == SWT.CURSOR_SIZEW)) {
                        // handleResize(me, mSelectedEvent, mCursor == SWT.CURSOR_SIZEW);
                        for (int x = 0; x < _selectedEvents.size(); x++)
                            handleMove(me, (GanttEvent) _selectedEvents.get(x), _cursor == SWT.CURSOR_SIZEW ? TYPE_RESIZE_LEFT : TYPE_RESIZE_RIGHT, x == 0);
                        return;
                    } else if (_cursor == SWT.CURSOR_SIZEALL) {
                        for (int x = 0; x < _selectedEvents.size(); x++)
                            handleMove(me, (GanttEvent) _selectedEvents.get(x), TYPE_MOVE, x == 0);
                        return;
                    }
                }
            }

            if (me.stateMask == 0) {
                this.setCursor(CURSOR_NONE);
            }

            boolean insideAnyEvent = false;

            // check if the header is locked, if so we check if the mouse is in the header area, and if so, ignore any mouse move event from here on out.
            if (_settings.lockHeaderOnVerticalScroll()) {
                Rectangle headerBounds = new Rectangle(0, _verticalScrollPosition, super.getBounds().width, getHeaderHeight());
                if (isInside(me.x, me.y, headerBounds)) { return; }
            }

            final int x = me.x;
            final int y = me.y;

            // check if cursor is inside the area of an event
            for (int i = 0; i < _ganttEvents.size(); i++) {
                GanttEvent event = (GanttEvent) _ganttEvents.get(i);
                if (isInside(me.x, me.y, new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight()))) {
                    insideAnyEvent = true;

                    if (event.isScope()) {
                        continue;
                    }

                    if (_hiddenLayers.contains(event.getLayerInt())) {
                        continue;
                    }

                    Rectangle rect = new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight());

                    boolean onRightBorder = false;
                    boolean onLeftBorder = false;

                    // n pixels left or right of either border = show resize
                    // mouse cursor
                    if (x >= (rect.x + rect.width - _settings.getResizeBorderSensitivity()) && y >= rect.y && x <= (rect.x + rect.width + _settings.getResizeBorderSensitivity()) && y <= (rect.y + rect.height)) {
                        onRightBorder = true;
                    } else if (x >= (rect.x - _settings.getResizeBorderSensitivity()) && y >= rect.y && x <= (rect.x + _settings.getResizeBorderSensitivity()) && y <= (rect.y + rect.height)) {
                        onLeftBorder = true;
                    }

                    // right border resize cursor (not images!)
                    if (me.stateMask == 0 || me.stateMask == _settings.getDragAllModifierKey()) {
                        boolean resizeArea = false;
                        if (resizeOK && event.isResizable()) {
                            if ((event.isCheckpoint() && _settings.allowCheckpointResizing()) || !event.isCheckpoint() && !event.isImage()) {
                                if (onRightBorder) {
                                    this.setCursor(CURSOR_SIZEE);
                                    _cursor = SWT.CURSOR_SIZEE;
                                    return;
                                } else if (onLeftBorder) {
                                    this.setCursor(CURSOR_SIZEW);
                                    _cursor = SWT.CURSOR_SIZEW;
                                    return;
                                }
                            }
                        }
                        // move cursor
                        if ((dndOK && event.isMoveable()) && !resizeArea) {
                            this.setCursor(CURSOR_SIZEALL);
                            _cursor = SWT.CURSOR_SIZEALL;
                            return;
                        }
                    } else {
                        if ((dndOK || event.isMoveable()) && _cursor == SWT.CURSOR_SIZEALL) {
                            if (isInMoveArea(event, me.x)) {
                                handleMove(me, event, TYPE_MOVE, true);
                                return;
                            }
                        }

                        if (!event.isCheckpoint()) {
                            if ((resizeOK || event.isResizable()) && (_cursor == SWT.CURSOR_SIZEE || _cursor == SWT.CURSOR_SIZEW)) {
                                // handleResize(me, event, onLeftBorder);
                                handleMove(me, event, onLeftBorder ? TYPE_RESIZE_LEFT : TYPE_RESIZE_RIGHT, true);
                                return;
                            }
                        }
                    }
                    break;
                }
            }

            if (hasGanttPhases()) {
                for (int i = 0; i < _ganttPhases.size(); i++) {
                    GanttPhase phase = (GanttPhase) _ganttPhases.get(i);

                    // not visible, don't bother
                    if (phase.isHidden() || !phase.isDisplayable()) {
                        continue;
                    }

                    // uncalculated, bad
                    if (phase.getHeaderBounds() == null || phase.getBounds() == null) {
                        continue;
                    }

                    // don't bother with locked events or events that can't be adjusted
                    if ((!phase.isResizable() && !phase.isMoveable()) || phase.isLocked()) {
                        continue;
                    }

                    if (isInside(me.x, me.y, phase.getHeaderBounds())) {
                        boolean onRightBorder = false;
                        boolean onLeftBorder = false;

                        Rectangle rect = phase.getHeaderBounds();

                        // n pixels left or right of either border = show resize
                        // mouse cursor
                        if (x >= (rect.x + rect.width - _settings.getResizeBorderSensitivity()) && y >= rect.y && x <= (rect.x + rect.width + _settings.getResizeBorderSensitivity()) && y <= (rect.y + rect.height)) {
                            onRightBorder = true;
                        } else if (x >= (rect.x - _settings.getResizeBorderSensitivity()) && y >= rect.y && x <= (rect.x + _settings.getResizeBorderSensitivity()) && y <= (rect.y + rect.height)) {
                            onLeftBorder = true;
                        }

                        if (me.stateMask == 0) {
                            if (onRightBorder) {
                                this.setCursor(CURSOR_SIZEE);
                                _cursor = SWT.CURSOR_SIZEE;
                                return;
                            } else if (onLeftBorder) {
                                this.setCursor(CURSOR_SIZEW);
                                _cursor = SWT.CURSOR_SIZEW;
                                return;
                            } else {
                                if (phase.isMoveable()) {
                                    this.setCursor(CURSOR_SIZEALL);
                                    _cursor = SWT.CURSOR_SIZEALL;
                                    return;
                                }
                            }
                        } else {
                            if (onLeftBorder || onRightBorder) {
                                handlePhaseMove(me, phase, onLeftBorder ? TYPE_RESIZE_LEFT : TYPE_RESIZE_RIGHT, true);
                                return;
                            } else {
                                handlePhaseMove(me, phase, TYPE_MOVE, true);
                                return;
                            }
                        }
                    }
                }
            }

            // clear mouse cursor if need be
            if (!insideAnyEvent && !_dragging && !_resizing) {
                if (_cursor != SWT.NONE) {
                    this.setCursor(CURSOR_NONE);
                    _cursor = SWT.NONE;
                }
            }

            // blank area drag and drop
            if (_mouseIsDown && _settings.allowBlankAreaDragAndDropToMoveDates()) {
                // blank area drag
                if (_mouseDragStartLocation == null) _mouseDragStartLocation = new Point(me.x, me.y);

                int diff = me.x - _mouseDragStartLocation.x;

                boolean left = false;
                if (diff < 0) {
                    left = true;
                }

                if (_settings.flipBlankAreaDragDirection()) {
                    left = !left;
                }

                diff /= getDayWidth();

                diff = Math.abs(diff);

                if (diff > 0 && ISettings.VIEW_YEAR != _currentView) {
                    _mouseDragStartLocation = new Point(me.x, me.y);
                }

                // if it's year view, we wait until the diff matches the number of dates of the month to move
                if (ISettings.VIEW_YEAR == _currentView && diff < _mainCalendar.getActualMaximum(Calendar.DATE)) {
                    return;
                }
                // and again if it's year view, and we get here, we do the actual flip
                else if (ISettings.VIEW_YEAR == _currentView) {
                    _mouseDragStartLocation = new Point(me.x, me.y);

                    // fast drags get multiple moves..
                    // TODO: just do prevMonth(times) instead of looping, less redraw
                    int times = diff / _mainCalendar.getActualMaximum(Calendar.DATE);
                    if (times == 0) times = 1;

                    for (int i = 0; i < times; i++) {
                        if (left) {
                            prevMonth();
                        } else {
                            nextMonth();
                        }
                    }
                    return;
                }

                for (int i = 0; i < diff; i++) {
                    if (left) {
                        if (_currentView == ISettings.VIEW_DAY) {
                            prevHour();
                        } else {
                            prevDay();
                        }
                    } else {
                        if (_currentView == ISettings.VIEW_DAY) {
                            nextHour();
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
                if (_currentView == ISettings.VIEW_YEAR && diff > 7) {
                    _reCalculateScopes = true;
                    _reCalculateSectionBounds = true;
                    redraw();
                }

                Point loc = new Point(_mouseDragStartLocation.x + 10, _mouseDragStartLocation.y - 20);
                // don't show individual dates if we're on yearly view, they're
                // correct, it's just that
                // we move horizontally month-wise, so it doesn't quite make
                // sense.
                if (_zoomLevel >= ISettings.ZOOM_YEAR_MAX) {
                    Calendar temp = Calendar.getInstance(_defaultLocale);
                    temp.setTime(_mainCalendar.getTime());
                    temp.set(Calendar.DAY_OF_MONTH, 1);
                    GanttDateTip.makeDialog(_colorManager, DateHelper.getDate(temp, dateFormat, _defaultLocale), toDisplay(loc), _mainBounds.y);
                } else {
                    if (_currentView == ISettings.VIEW_D_DAY) {
                        GanttDateTip.makeDialog(_colorManager, getCurrentDDate() + "        ", toDisplay(loc), _mainBounds.y);
                    } else {
                        GanttDateTip.makeDialog(_colorManager, DateHelper.getDate(_mainCalendar, dateFormat, _defaultLocale), toDisplay(loc), _mainBounds.y);
                    }
                }
            }
        } catch (Exception error) {
            SWT.error(SWT.ERROR_UNSPECIFIED, error);
        }
    }

    public int getDaysVisible() {
        return _daysVisible;
    }

    private String getCurrentDDate() {
        int days = (int) DateHelper.daysBetween(_dDayCalendar, _mainCalendar, _defaultLocale);
        if (days > 0) return "+" + days;

        return "" + days;
    }

    private boolean isInMoveArea(GanttEvent event, int x) {
        Rectangle bounds = new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight());
        return x > (bounds.x + _moveAreaInsets) && x < (bounds.x + bounds.width - _moveAreaInsets);
    }

    /**
     * Deals with figuring out what direction we are auto-scrolling in, if any.
     * 
     * @param event MouseEvent
     */
    private void doAutoScroll(MouseEvent event) {
        if (event.x < _mainBounds.x) {
            doAutoScroll(DIRECTION_LEFT);
        } else if (event.x > _mainBounds.width) {
            doAutoScroll(DIRECTION_RIGHT);
        } else {
            if (_freeFloatDragging) {
                if (event.y < 0) {
                    doAutoScroll(DIRECTION_UP);
                } else if (event.y > _mainBounds.height) {
                    doAutoScroll(DIRECTION_DOWN);
                } else {
                    endAutoScroll();
                }
            } else {
                endAutoScroll();
            }
        }
    }

    private void doAutoScroll(int direction) {
        Runnable timer = null;

        // If we're already autoscrolling in the given direction do nothing
        if (_autoScrollDirection == direction) { return; }

        final Display display = getDisplay();

        if (direction == DIRECTION_LEFT) {
            timer = new Runnable() {
                public void run() {
                    if (_autoScrollDirection == DIRECTION_LEFT) {
                        if (_currentView == ISettings.VIEW_DAY) {
                            prevHour();
                        } else {
                            prevDay();
                        }

                        display.timerExec(TIMER_INTERVAL, this);
                    }
                }
            };
        } else if (direction == DIRECTION_RIGHT) {
            timer = new Runnable() {
                public void run() {
                    if (_autoScrollDirection == DIRECTION_RIGHT) {
                        if (_currentView == ISettings.VIEW_DAY) {
                            nextHour();
                        } else {
                            nextDay();
                        }

                        display.timerExec(TIMER_INTERVAL, this);
                    }
                }
            };
        } else if (direction == DIRECTION_DOWN) {
            timer = new Runnable() {
                public void run() {
                    if (_autoScrollDirection == DIRECTION_DOWN) {
                        _verticalScrollBar.setSelection(_verticalScrollBar.getSelection() + 10);
                        vScroll(null);
                        display.timerExec(TIMER_INTERVAL, this);
                    }
                }
            };
        } else if (direction == DIRECTION_UP) {
            timer = new Runnable() {
                public void run() {
                    if (_autoScrollDirection == DIRECTION_UP) {
                        _verticalScrollBar.setSelection(_verticalScrollBar.getSelection() - 10);
                        vScroll(null);
                        display.timerExec(TIMER_INTERVAL, this);
                    }
                }
            };
        }

        if (timer != null) {
            _autoScrollDirection = direction;
            display.timerExec(TIMER_INTERVAL, timer);
        }
    }

    boolean isChartReady() {
        return _mainBounds != null;
    }

    private void endAutoScroll() {
        _autoScrollDirection = SWT.NULL;
    }

    /**
     * Gets the X for a given date.
     * 
     * @param date Date
     * @return x position or -1 if date was not found
     */
    public int getXForDate(Date date) {
        checkWidget();
        Calendar cal = Calendar.getInstance(_defaultLocale);
        cal.setTime(date);

        return getXForDate(cal);
    }

    /**
     * Gets the x position where the given date starts in the current visible area.
     * 
     * @param cal Calendar
     * @return -1 if date was not found
     */
    public int getXForDate(Calendar cal) {
        checkWidget();

        if (_currentView == ISettings.VIEW_DAY) { return getStartingXForEventHours(cal); }

        // clone the "root" calendar (our leftmost date)
        Calendar temp = (Calendar) _mainCalendar.clone();

        int dw = getDayWidth();
        if (_currentView == ISettings.VIEW_YEAR) {
            // we draw years starting on the left for simplicity's sake
            temp.set(Calendar.DAY_OF_MONTH, 1);
        }

        long days = DateHelper.daysBetween(temp, cal, _defaultLocale);

        int extra = 0;
        if (_settings.drawEventsDownToTheHourAndMinute()) {
            if (_currentView != ISettings.VIEW_DAY) {
                float ppm = 60f / _dayWidth;
                int mins = cal.get(Calendar.HOUR_OF_DAY);
                extra = (int) (mins * ppm);
            }
        }

        // return mBounds.x + ((int) days * dw) + extra;
        // -- Emil: This was old, why we append mBounds.x is beyond me, it's wrong as the bounds.x starting 
        // position has nothing to do with the actual dates, we always calcualate from the calendar date
        // regardless of where the bounds start, as long as the start calendar represents what is visible
        // the extra buffering is NOT needed.
        return ((int) days * dw) + extra;
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
        Calendar temp = (Calendar) _mainCalendar.clone();

        int dw = getDayWidth();
        if (_currentView == ISettings.VIEW_YEAR) {
            temp.set(Calendar.DAY_OF_MONTH, 1);
        } else if (_currentView == ISettings.VIEW_DAY) {

            // pixels per minute
            float ppm = 60f / dw;

            // total minutes from left side
            xPosition -= _mainBounds.x;
            int totalMinutes = (int) (xPosition * ppm);

            // set our temporary end calendar to the start date of the calendar
            Calendar fakeEnd = Calendar.getInstance(_defaultLocale);
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
                if (xx < xPosition) break;

                xx -= dw;
            }
        } else {
            // loop from left to right
            while (true) {
                if (xx > xPosition) break;

                if (xx != 0) {
                    temp.add(Calendar.DATE, 1);
                }

                xx += dw;
            }

        }

        //System.err.println(x + " " + xx);

        return temp;
    }

    private boolean isNoOverlap(Calendar dat1, Calendar dat2) {
        if (_currentView == ISettings.VIEW_DAY) {
            long minutes = DateHelper.minutesBetween(dat1.getTime(), dat2.getTime(), _defaultLocale, false, false);
            return minutes >= 0;
        } else {
            long days = DateHelper.daysBetween(dat1, dat2, _defaultLocale);
            return days >= 0;
        }
    }

    /**
     * Draws a dotted vertical marker at the given date. It will get removed on repaint, so make sure it's drawn as
     * often as needed.
     * 
     * @param date Date to draw it at
     */
    public void drawMarker(Date date) {
        checkWidget();
        Calendar cal = Calendar.getInstance(_defaultLocale);
        cal.setTime(date);

        int x = getXForDate(cal);

        if (x == -1) return;

        GC gc = new GC(this);
        gc.setLineStyle(SWT.LINE_DOT);
        gc.drawRectangle(x, 0, x, _mainBounds.height);
        gc.setLineStyle(SWT.LINE_SOLID);
        gc.dispose();
    }

    private void handlePhaseMove(MouseEvent me, GanttPhase phase, int type, boolean showToolTip) {
        String dateFormat = (_currentView == ISettings.VIEW_DAY ? _settings.getHourDateFormat() : _settings.getDateFormat());

        Calendar mouseDateCal = getDateAt(me.x);

        // TODO: Code-reuse from normal event move, merge the two

        // drag start event, show cross hair arrow cursor and keep going
        if ((me.stateMask & SWT.BUTTON1) != 0 && !_dragging && !_resizing) {
            if (type == TYPE_MOVE) {
                _dragging = true;
                _resizing = false;
                this.setCursor(CURSOR_SIZEALL);
            } else {
                _dragging = false;
                _resizing = true;
                this.setCursor(type == TYPE_RESIZE_LEFT ? CURSOR_SIZEW : CURSOR_SIZEE);
                _lastLeft = (type == TYPE_RESIZE_LEFT);
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
        if ((me.stateMask & SWT.BUTTON1) != 0 && (_dragging || _resizing)) {
            if (me.x > _lastX || me.x < _lastX) {
                int diff = 0;
                if (_currentView == ISettings.VIEW_DAY) {
                    Calendar cal = _dragPhase.getStartDate();
                    if (_resizing && !_lastLeft) {
                        cal = _dragPhase.getEndDate();
                    }

                    diff = DateHelper.minutesBetween(mouseDateCal.getTime(), cal.getTime(), _defaultLocale, false, false);

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
                    diff = -(diff);
                } else {
                    if (me.x > _lastX) {
                        diff = (int) DateHelper.daysBetween(_dragStartDate, mouseDateCal, _defaultLocale);
                    } else {
                        diff = (int) DateHelper.daysBetween(mouseDateCal, _dragStartDate, _defaultLocale);
                    }

                    if (me.x < _lastX) {
                        diff = -diff;
                    }
                }

                _dragStartDate = mouseDateCal;

                int calMark = Calendar.DATE;

                if (_currentView == ISettings.VIEW_DAY) {
                    calMark = Calendar.MINUTE;
                }

                // move phase
                if (diff != 0) {
                    //System.err.println("MOVE! " + mDragPhase.getBounds() + " " + mGanttPhases.size());
                    boolean ok = true;

                    if (type == TYPE_MOVE) {
                        _dragPhase.move(calMark, diff);
                    } else {
                        if (!_settings.allowPhaseOverlap()) {
                            for (int i = 0; i < _ganttPhases.size(); i++) {
                                GanttPhase other = (GanttPhase) _ganttPhases.get(i);
                                if (other == _dragPhase) {
                                    continue;
                                }

                                if ((type == TYPE_RESIZE_LEFT && diff < 0) || (type == TYPE_RESIZE_RIGHT && diff > 0)) {
                                    if (_dragPhase.willOverlapResize(other, calMark, diff, type == TYPE_RESIZE_LEFT)) {
                                        ok = false;
                                        break;
                                    }
                                }
                            }
                        }

                        if (ok) {
                            if (type == TYPE_RESIZE_LEFT) {
                                _dragPhase.moveStart(calMark, diff);
                            } else if (type == TYPE_RESIZE_RIGHT) {
                                _dragPhase.moveEnd(calMark, diff);
                            }
                        }
                    }

                    // redraw to show update
                    redraw();

                    if (ok) {
                        // fire listeners
                        for (int x = 0; x < _eventListeners.size(); x++) {
                            IGanttEventListener listener = (IGanttEventListener) _eventListeners.get(x);
                            if (type == TYPE_MOVE) {
                                listener.phaseMoved(_dragPhase, me);
                            } else {
                                listener.phaseResized(_dragPhase, me);
                            }
                        }
                    }

                    if (_settings.showDateTips() && (_dragging || _resizing) && showToolTip) {
                        Rectangle eBounds = _dragPhase.getBounds();
                        Point eventOnDisplay = toDisplay(me.x, eBounds.y);

                        if (_currentView == ISettings.VIEW_D_DAY) {
                            StringBuffer buf = new StringBuffer();
                            buf.append(_dragPhase.getDDayStart());
                            buf.append(" - ");
                            buf.append(_dragPhase.getDDayEnd());
                            buf.append("       ");
                            GanttDateTip.makeDialog(_colorManager, buf.toString(), eventOnDisplay, _mainBounds.y);
                        } else {
                            StringBuffer buf = new StringBuffer();
                            buf.append(DateHelper.getDate(_dragPhase.getStartDate(), dateFormat, _defaultLocale));
                            buf.append(" - ");
                            buf.append(DateHelper.getDate(_dragPhase.getEndDate(), dateFormat, _defaultLocale));
                            GanttDateTip.makeDialog(_colorManager, buf.toString(), eventOnDisplay, _mainBounds.y);
                        }
                    }
                }
            }
        }
    }

    /**
     * Calculates where a vertically DND'd event will end up if dropped
     */
    private void calculateVerticalInsertLocations() {
        _verticalDragDropManager.clear();

        if (!_freeFloatDragging) { return; }

        if (_dragEvents.isEmpty()) { return; }

        // first drag event
        GanttEvent ge = (GanttEvent) _dragEvents.get(0);

        GanttSection dragOverSection = getSectionForVerticalDND(ge, true);

        //  System.err.println(dragOverSection);

        // get surrounding events of that event
        List surrounding = getSurroundingVerticalEvents(ge, dragOverSection);

        GanttEvent top = (GanttEvent) surrounding.get(0);
        GanttEvent bottom = (GanttEvent) surrounding.get(1);

        _verticalDragDropManager.setTopEvent(top);
        _verticalDragDropManager.setBottomEvent(bottom);
        _verticalDragDropManager.setTargetSection(dragOverSection);
        _verticalDragDropManager.setSurroundingEvents(surrounding);
    }

    private void drawVerticalInsertMarkers(GC gc) {
        if (!_freeFloatDragging) { return; }

        if (_dragEvents.isEmpty()) { return; }

        gc.setForeground(_colorManager.getOriginalLocationColor());
        gc.setLineWidth(1);
        gc.setAdvanced(false);

        // first drag event
        GanttEvent ge = (GanttEvent) _dragEvents.get(0);

        GanttEvent top = _verticalDragDropManager.getTopEvent();//(GanttEvent) surrounding.get(0);
        GanttEvent bottom = _verticalDragDropManager.getBottomEvent();//(GanttEvent) surrounding.get(1);

        //System.err.println("Top: " + top + ", Bottom: " + bottom);//, Over section " + dragOverSection);

        int xDrawStart = 0;
        int xDrawEnd = _mainBounds.width;
        if (hasGanttSections() && _settings.getSectionSide() == SWT.LEFT) {
            xDrawStart = _settings.getSectionBarWidth();
            xDrawEnd = _mainBounds.width + xDrawStart;
        }

        // check if floating event is over a gantt section, if it's in a new section, assume draw line always appears in that section

        if (ge.hasMovedVertically()) {
            if (top != null) {
                if (bottom != null) {
                    int midDiff = (bottom.getY() - top.getBottomY()) / 2;
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
            GanttEvent e = (GanttEvent) _dragEvents.get(i);
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
        if (event.isLocked()) { return; }
        
        if (!_settings.enableDragAndDrop() && type == TYPE_MOVE) { return; }

        if (!_settings.enableResizing() && (type == TYPE_RESIZE_LEFT || type == TYPE_RESIZE_RIGHT)) { return; }

        if (!_mouseIsDown) { return; }

        if (event == null) { return; }

        if (type == TYPE_MOVE && !event.isMoveable()) { return; }

        if ((type == TYPE_RESIZE_LEFT || type == TYPE_RESIZE_RIGHT) && !event.isResizable()) { return; }

        String dateFormat = (_currentView == ISettings.VIEW_DAY ? _settings.getHourDateFormat() : _settings.getDateFormat());

        Calendar mouseDateCal = getDateAt(me.x);

        // drag start event, show cross hair arrow cursor and keep going
        if ((me.stateMask & SWT.BUTTON1) != 0 && !_dragging && !_resizing) {
            if (type == TYPE_MOVE) {
                event.flagDragging();
                _dragging = true;
                _resizing = false;
                // save the location where we started the drag, can be used during the drag for calculating 
                // deltas for where we are now vs. where we started
                _dragStartLocation = new Point(me.x, me.y);
                this.setCursor(CURSOR_SIZEALL);
            } else {
                _dragging = false;
                _resizing = true;
                this.setCursor(type == TYPE_RESIZE_LEFT ? CURSOR_SIZEW : CURSOR_SIZEE);
                _lastLeft = (type == TYPE_RESIZE_LEFT);
            }

            _dragEvents.clear();
            _dragEvents.add(event);
            _lastX = me.x;
            _lastY = me.y;
            _dragStartDate = mouseDateCal;
            _justStartedMoveOrResize = true;
        }

        // vertical drag
        if (_settings.getVerticalEventDragging() != IVerticalDragModes.NO_VERTICAL_DRAG && _dragStartLocation != null) {
            int diff = _dragStartLocation.y - me.y;
            if (Math.abs(diff) > _settings.getVerticalDragResistance()) {
                if (!_freeFloatDragging) {
                    _freeFloatDragging = true;
                }
                event.updateY(me.y);
            } else {
                // this makes the event "snap" back to where it was before as we undo the vertical Y
                // if it's within range of our original position 
                if (_freeFloatDragging) {
                    event.undoVerticalDragging();
                    redraw();
                }
            }

            // check what vertical direction the user is dragging the event in
            if (me.y > _lastY) {
                _verticalDraggingDirection = SWT.DOWN;
            } else if (me.y < _lastY) {
                _verticalDraggingDirection = SWT.UP;
            }
        }

        // if we're dragging, with left mouse held down..
        if ((me.stateMask & SWT.BUTTON1) != 0 && (_dragging || _resizing)) {

            if (_freeFloatDragging) {
                // vertical insert markers will be drawin in the main redraw handler
                redraw();
            }

            // there's some math here to calculate how far between the drag
            // start date and the current mouse x position date we are at.
            // we move the event the amount of days of difference there is between those two. 
            // This way we get 2 wanted results:
            // 	1. The position where we grabbed the event will remain the same
            // 	   throughout the move
            //  2. The mouse cursor will move with the event and not skip ahead
            // 	   or behind
            // it's important we set the dragStartDate to the current mouse x
            // date once we're done with it, as well as set the last
            // x position to where the mouse was last            

            // left or right drag
            if (me.x > _lastX || me.x < _lastX) {
                int diff = 0;
                if (_currentView == ISettings.VIEW_DAY) {
                    Calendar cal = event.getActualStartDate();
                    if (_resizing && !_lastLeft) {
                        cal = event.getActualEndDate();
                    }

                    diff = DateHelper.minutesBetween(mouseDateCal.getTime(), cal.getTime(), _defaultLocale, false, false);

                    // there is some confusing math here that should get explaining.
                    // When the user starts dragging, we pick the distance from the event start time to the mouse and set that as our
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
                    diff = -(diff);
                } else {
                    if (me.x > _lastX) {
                        diff = (int) DateHelper.daysBetween(_dragStartDate, mouseDateCal, _defaultLocale);
                    } else {
                        diff = (int) DateHelper.daysBetween(mouseDateCal, _dragStartDate, _defaultLocale);
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
            /*
             * event.getScopeParent().calculateScope(); int newStartX = getXForDate(event.getScopeParent().getEarliestScopeEvent().getActualStartDate()); int newWidth =
             * getXLengthForEvent(event.getScopeParent()); event.getScopeParent().updateX(newStartX); event.getScopeParent().updateWidth(newWidth);
             */}

        // set new last x position to where mouse is now
        _lastX = me.x;
        _lastY = me.y;

        // TODO: On multi-drag events, one tooltip is a bit lame, can we do something cooler/nicer/more useful? (20 tooltips != useful, and it's slow)
        if (_settings.showDateTips() && (_dragging || _resizing) && showToolTip) {
            Rectangle eBounds = new Rectangle(event.getX(), event.getY() - 25, event.getWidth(), event.getHeight());
            Point eventOnDisplay = toDisplay(me.x, eBounds.y);
            if (event.isCheckpoint()) {
                long days = DateHelper.daysBetween(event.getActualStartDate(), event.getActualEndDate(), _defaultLocale);
                days++;

                if (days == 1) {
                    GanttDateTip.makeDialog(_colorManager, DateHelper.getDate(event.getActualStartDate(), dateFormat, _defaultLocale), eventOnDisplay, _mainBounds.y);
                } else {
                    StringBuffer buf = new StringBuffer();
                    if (_currentView == ISettings.VIEW_D_DAY) {
                        buf.append(event.getActualDDayStart());
                        buf.append(" - ");
                        buf.append(event.getActualDDayEnd());
                        GanttDateTip.makeDialog(_colorManager, buf.toString(), eventOnDisplay, _mainBounds.y);
                    } else {
                        buf.append(DateHelper.getDate(event.getActualStartDate(), dateFormat, _defaultLocale));
                        buf.append(" - ");
                        buf.append(DateHelper.getDate(event.getActualEndDate(), dateFormat, _defaultLocale));
                        GanttDateTip.makeDialog(_colorManager, buf.toString(), eventOnDisplay, _mainBounds.y);
                    }
                }
            } else {
                if (_currentView == ISettings.VIEW_D_DAY) {
                    StringBuffer buf = new StringBuffer();
                    buf.append(event.getActualDDayStart());
                    buf.append(" - ");
                    buf.append(event.getActualDDayEnd());
                    buf.append("       ");
                    GanttDateTip.makeDialog(_colorManager, buf.toString(), eventOnDisplay, _mainBounds.y);
                } else {
                    StringBuffer buf = new StringBuffer();
                    buf.append(DateHelper.getDate(event.getActualStartDate(), dateFormat, _defaultLocale));
                    buf.append(" - ");
                    buf.append(DateHelper.getDate(event.getActualEndDate(), dateFormat, _defaultLocale));
                    GanttDateTip.makeDialog(_colorManager, buf.toString(), eventOnDisplay, _mainBounds.y);
                }
            }
        }
    }

    // updates a scope's x/y position (literal scope event)
    private void updateScopeXY(GanttEvent ge) {
        // if the event is part of a scope, force the parent to recalculate it's size etc, thus we don't have to recalculate everything
        if (ge != null) {
            ge.calculateScope();
            int newStartX = getXForDate(ge.getEarliestScopeEvent().getActualStartDate());
            int newWidth = getXLengthForEvent(ge);
            ge.updateX(newStartX);
            ge.updateWidth(newWidth);
        }
    }

    // moves one event on the canvas. If SHIFT is held and linked events is true,
    // it moves all events linked to the moved event.
    private void moveEvent(GanttEvent ge, int diff, int stateMask, MouseEvent me, int type) {
        if (diff == 0) { return; }

        if (ge.isLocked()) { return; }

        ArrayList eventsMoved = new ArrayList();

        int calMark = Calendar.DATE;

        if (_currentView == ISettings.VIEW_DAY) {
            calMark = Calendar.MINUTE;
        }

        List toMove = new ArrayList();

        // multi move
        if ((stateMask & _settings.getDragAllModifierKey()) != 0 && _settings.moveLinkedEventsWhenEventsAreMoved()) {
            List conns = getEventsDependingOn(ge);

            List translated = new ArrayList();
            for (int x = 0; x < conns.size(); x++) {
                GanttEvent md = (GanttEvent) conns.get(x);

                if (md.isLocked()) {
                    continue;
                }

                // it's a checkpoint, we're resizing, and settings say checkpoints can't be resized, then we skip them even here
                if (md.isCheckpoint() && type != TYPE_MOVE && !_settings.allowCheckpointResizing()) {
                    continue;
                }

                translated.add(md);
            }

            // add all multiselected events too, if any
            if (_multiSelect) {
                for (int x = 0; x < _selectedEvents.size(); x++) {
                    GanttEvent selEvent = (GanttEvent) _selectedEvents.get(x);
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
            List toRemove = new ArrayList();
            for (int x = 0; x < toMove.size(); x++) {
                GanttEvent moveEvent = (GanttEvent) toMove.get(x);
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
            GanttEvent event = (GanttEvent) toMove.get(x);

            Calendar cal1 = Calendar.getInstance(_defaultLocale);
            Calendar cal2 = (Calendar) cal1.clone();
            cal1.setTime(event.getActualStartDate().getTime());
            cal2.setTime(event.getActualEndDate().getTime());

            if (type == TYPE_MOVE) {
                cal1.add(calMark, diff);
                cal2.add(calMark, diff);

                // check before date move as it's the start date
                if (event.getNoMoveBeforeDate() != null) {
                    // for start dates we need to actually set the date or we'll be 1 day short as we did the diff already
                    // if we didn't have this code, a fast move would never move the event to the end date as it would be beyond already
                    if (cal1.before(event.getNoMoveBeforeDate())) {
                        // flag first or dates won't be cloned before they changed
                        event.moveStarted(type);

                        long millis = event.getNoMoveBeforeDate().getTimeInMillis();
                        long milliDiff = Math.abs(event.getActualStartDate().getTimeInMillis() - millis);
                        event.setRevisedStart((Calendar) event.getNoMoveBeforeDate().clone(), false);

                        // we also move the end date the same amount of time in difference between the no move date and the start date
                        Calendar end = (Calendar) event.getActualEndDate().clone();
                        end.add(Calendar.MILLISECOND, (int) -milliDiff);
                        event.setRevisedEnd(end, false);
                        event.updateX(getStartingXfor(event.getRevisedStart()));
                        continue;
                    }
                }

                // remember, we check the start calendar on both occasions, as that's the mark that counts
                if (event.getNoMoveAfterDate() != null) {
                    if (cal2.after(event.getNoMoveAfterDate())) {
                        // flag first or dates won't be cloned before they changed
                        event.moveStarted(type);

                        // same deal for end date as for start date
                        long millis = event.getNoMoveAfterDate().getTimeInMillis();
                        long milliDiff = Math.abs(event.getActualEndDate().getTimeInMillis() - millis);
                        event.setRevisedEnd((Calendar) event.getNoMoveAfterDate().clone(), false);

                        // we also move the end date the same amount of time in difference between the no move date and the start date
                        Calendar start = (Calendar) event.getActualStartDate().clone();
                        start.add(Calendar.MILLISECOND, (int) milliDiff);
                        event.setRevisedStart(start, false);
                        event.updateX(getStartingXfor(event.getRevisedStart()));
                        continue;
                    }
                }

                event.moveStarted(type);

                // we already validated dates here, so we can just set them (Besides, dual validation is bad, as one date will be validated
                // before the next one is set, which can cause serious issues when we do drag and drops
                event.setRevisedStart(cal1, false);
                event.setRevisedEnd(cal2, false);

                // we move it by updating the x position to its new location
                event.updateX(getStartingXfor(cal1));

                // to move events vertically
                //event.updateY(toControl(me.x, me.y).);
            } else if (type == TYPE_RESIZE_LEFT) {
                cal1.add(calMark, diff);

                if (!isNoOverlap(cal1, event.getActualEndDate())) continue;

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
                event.updateX(getStartingXfor(cal1));
                event.updateWidth(getXLengthForEvent(event));
            } else if (type == TYPE_RESIZE_RIGHT) {
                cal2.add(calMark, diff);

                if (!isNoOverlap(event.getActualStartDate(), cal2)) continue;

                // remember, we check the start calendar on both occasions, as that's the mark that counts
                if (event.getNoMoveAfterDate() != null) {
                    if (cal2.after(event.getNoMoveAfterDate())) continue;
                }

                event.moveStarted(type);

                event.setRevisedEnd(cal2, false);

                // update size + location
                event.updateX(getStartingXfor(cal1));
                event.updateWidth(getXLengthForEvent(event));
            }

            if (!eventsMoved.contains(event)) eventsMoved.add(event);
        }

        if (!eventsMoved.contains(ge)) eventsMoved.add(ge);

        for (int x = 0; x < _eventListeners.size(); x++) {
            IGanttEventListener listener = (IGanttEventListener) _eventListeners.get(x);
            if (type == TYPE_MOVE) {
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
        if (_dragging || _resizing) { return; }

        // right now all we do here is tooltips
        if (!_settings.showToolTips()) return;

        // possible fix for mac's over-and-over display of hover tip
        if (GanttToolTip.isActive()) return;

        if (me.stateMask != 0) return;

        for (int i = 0; i < _ganttEvents.size(); i++) {
            GanttEvent event = (GanttEvent) _ganttEvents.get(i);
            if (isInside(me.x, me.y, new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight()))) {
                showTooltip(event, me);
                return;
            }
        }
    }

    private void showTooltip(GanttEvent event, MouseEvent me) {
        if (!_settings.showToolTips()) return;

        long days = DateHelper.daysBetween(event.getStartDate(), event.getEndDate(), _defaultLocale);
        days++;
        long revisedDays = 0;

        boolean dDay = _currentView == ISettings.VIEW_D_DAY;
        if (dDay) {
            days = event.getDDateRange();
        }

        // as the dialog is slightly bigger in many aspects, push it slightly
        // off to the right
        int xPlus = 0;
        if (isUseAdvancedTooltips() || event.getAdvancedTooltip() != null) xPlus = _settings.getAdvancedTooltipXOffset();

        Point displayLocation = super.toDisplay(new Point(me.x + xPlus, me.y));

        String dateFormat = (_currentView == ISettings.VIEW_DAY ? _settings.getHourDateFormat() : _settings.getDateFormat());

        String startDate = DateHelper.getDate(event.getStartDate(), dateFormat, _defaultLocale);
        String endDate = DateHelper.getDate(event.getEndDate(), dateFormat, _defaultLocale);

        Calendar revisedStart = event.getRevisedStart();
        Calendar revisedEnd = event.getRevisedEnd();
        StringBuffer extra = new StringBuffer();

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
            revisedStartText = dDay ? "" + event.getDDayRevisedStart() : DateHelper.getDate(revisedStart, dateFormat, _defaultLocale);
            extra.append(revisedStartText);
        }

        if (revisedEnd != null) {
            if (revisedStart == null) {
                revisedStart = event.getActualStartDate();
            }

            revisedEndText = dDay ? "" + event.getDDayRevisedEnd() : DateHelper.getDate(revisedEnd, dateFormat, _defaultLocale);
            revisedDays = DateHelper.daysBetween(revisedStart, revisedEnd, _defaultLocale);
            revisedDays++;
            extra.append(" - ");
            extra.append(revisedEndText);
            extra.append(" (");
            if (dDay) {
                revisedDays = event.getRevisedDDateRange();
            }
            extra.append(revisedDays);
            extra.append(" ");
            if (revisedDays == 1 || revisedDays == -1) {
                extra.append(_languageManager.getDaysText());
            } else {
                extra.append(_languageManager.getDaysPluralText());
            }
            extra.append(")");
        }

        AdvancedTooltip at = event.getAdvancedTooltip();
        if (at == null && isUseAdvancedTooltips()) {
            String ttText = _settings.getDefaultAdvancedTooltipTextExtended();
            if (event.isCheckpoint() || event.isImage() || event.isScope() || (revisedStartText == null && revisedEnd == null)) {
                ttText = _settings.getDefaultAdvancedTooltipText();
            }

            at = new AdvancedTooltip(_settings.getDefaultAdvancedTooltipTitle(), ttText, _settings.getDefaultAdvandedTooltipImage(), _settings.getDefaultAdvandedTooltipHelpImage(), _settings.getDefaultAdvancedTooltipHelpText());
        }

        if (at != null) {
            String title = fixTooltipString(at.getTitle(), event.getName(), startDate, endDate, revisedStartText, revisedEndText, days, revisedDays, event.getPercentComplete());
            String content = fixTooltipString(at.getContent(), event.getName(), startDate, endDate, revisedStartText, revisedEndText, days, revisedDays, event.getPercentComplete());
            String help = fixTooltipString(at.getHelpText(), event.getName(), startDate, endDate, revisedStartText, revisedEndText, days, revisedDays, event.getPercentComplete());

            AdvancedTooltipDialog.makeDialog(at, _colorManager, displayLocation, title, content, help);
        } else {
            if (extra.length() > 0) {
                StringBuffer buf = new StringBuffer();
                buf.append(_languageManager.getPlannedText());
                buf.append(": ");
                buf.append(dDay ? "" + dDayStart : startDate);
                buf.append(" - ");
                buf.append(dDay ? "" + dDayEnd : endDate);
                buf.append(" (");
                buf.append(days);
                buf.append(" ");
                buf.append((days == 1 || days == -1) ? _languageManager.getDaysText() : _languageManager.getDaysPluralText());
                buf.append(")");

                GanttToolTip.makeDialog(_colorManager, event.getName(), extra.toString(), buf.toString(), event.getPercentComplete() + _languageManager.getPercentCompleteText(), displayLocation);
            } else {
                StringBuffer buf = new StringBuffer();
                buf.append(dDay ? "" + dDayStart : startDate);
                buf.append(" - ");
                buf.append(dDay ? "" + dDayEnd : endDate);
                buf.append(" (");
                buf.append(days);
                buf.append(" ");
                buf.append((days == 1 || days == -1) ? _languageManager.getDaysText() : _languageManager.getDaysPluralText());
                buf.append(")");

                GanttToolTip.makeDialog(_colorManager, event.getName(), buf.toString(), event.getPercentComplete() + _languageManager.getPercentCompleteText(), displayLocation);
            }
        }
    }

    private String fixTooltipString(String str, String name, String startDate, String endDate, String plannedStart, String plannedEnd, long days, long plannedDays, int percentageComplete) {
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
        return getImage(_mainBounds);
    }

    public void foo() {
        setDate(getEvent(true, true).getEarliestStartDate(), false, true);
        //System.err.println(getEvent(true).getActualBounds());        
    }

    /**
     * Returns the image that is the entire chart, regardless of what is currently visible. If chart contains no events,
     * getImage() is called from within.
     * <p>
     * Do note that if the chart is "huge", you may need to increase your heap size. If you're zoomed in that's also
     * taken into account and you may need a massive heap to work with hours views as they are simply huge.
     * 
     * @return Image
     */
    public Image getFullImage() {
        checkWidget();

        // we need to pretend that we are at scroll position 0 along with that our bounds are as big as all visible events,
        // thus we save old values before so we can reset them at the end
        _savingChartImage = true;
        int oldVscroll = _verticalScrollPosition;
        _verticalScrollPosition = 0;
        moveYBounds(-oldVscroll);
        Rectangle oldBounds = _mainBounds;
        Calendar currentCalendar = (Calendar) _mainCalendar.clone();
        try {
            // as we may accidentally move the current chart when saving, don't show the user, we'll be drawing on a different canvas anyway
            setRedraw(false);

            GanttEvent geLeft = getEvent(true, true);
            Rectangle fullBounds = new Rectangle(0, 0, 0, 0);
            GanttEvent geRight = getEvent(false, true);

            if (geRight == null || geLeft == null) { return getImage(); }

            /*System.err.println();

            System.err.println("Now:  " + mCalendar.getTime() + " " + getXForDate(mCalendar));
            System.err.println("Left bounds: " + geLeft.getActualBounds());*/
            // set calendar to earliest date
            boolean drawSections = (_ganttSections.size() > 0);
            int extraX = 0;
            int extraW = 0;
            if (drawSections) {
                if (_settings.getSectionSide() == SWT.LEFT) {
                    extraX = -_settings.getSectionBarWidth();
                    extraW = _settings.getSectionBarWidth();
                } else {
                    extraW = _settings.getSectionBarWidth();
                }

            }

            _mainCalendar = getDateAt(geLeft.getActualBounds().x + extraX);//(Calendar) geLeft.getEarliestStartDate().clone();
            //mCalendar.add(Calendar.DATE, -(geLeft.getNameExtent().x + mSettings.getTextSpacerConnected() + getDayWidth()) / getDayWidth());
            //System.err.println("Post: " + mCalendar.getTime() + " Should be < " + geLeft.getEarliestStartDate().getTime());
            //System.err.println(" ---- " + geLeft.getEarliestStartX() + " " + geLeft.getActualBounds().x);

            //fullBounds.x = geLeft.getActualBounds().x;
            Rectangle rBounds = geRight.getActualBounds();

            //System.err.println(rBounds);

            // space it out 1 day
            if (drawSections && _settings.getSectionSide() == SWT.RIGHT) {
                extraW += getDayWidth();
            }

            fullBounds.width = rBounds.x + rBounds.width - geLeft.getActualBounds().x + extraW;
            fullBounds.height = _bottomMostY;

            //            System.err.println(fullBounds);
            //          System.err.println(getRightMostPixel() + " " + getLeftMostPixel());

            // set chart bounds to be the fake bounds
            _mainBounds = fullBounds;

            // forcing a full update or event visibilities will not change
            flagForceFullUpdate();

            Image buffer = new Image(Display.getDefault(), fullBounds);

            GC gc2 = new GC(buffer);
            drawChartOntoGC(gc2, fullBounds);
            drawHeader(gc2);

            // we don't draw this when saving an image until the very end as we push
            // bounds around differently and it gets drawn mis-aligned if we draw it
            // before
            if (drawSections && _settings.getSectionSide() == SWT.RIGHT) {
                drawSectionColumn(gc2, fullBounds, false, true, false, true);
            }

            gc2.dispose();

            return buffer;
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            // reset everything, including forcing a redraw and reset
            _verticalScrollPosition = oldVscroll;
            moveYBounds(_verticalScrollPosition);
            _savingChartImage = false;
            _mainBounds = oldBounds;
            _mainCalendar = currentCalendar;
            Display.getDefault().asyncExec(new Runnable() {
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

    /*private int getFullDrawSpan() {
        GanttEvent geRight = getEvent(false);
        GanttEvent geLeft = getEvent(true);
        
        if (geRight == null || geLeft == null) {
            return 0;
        }
        
        return geRight.getActualBounds().x + getRightMostPixel()
    }
    */
    /**
     * Returns the chart as an image for the given bounds.
     * 
     * @param bounds Rectangle bounds
     * @return Image of chart
     */
    public Image getImage(Rectangle bounds) {
        checkWidget();
        _savingChartImage = true;
        int oldVscroll = _verticalScrollPosition;
        _verticalScrollPosition = 0;
        try {
            Image buffer = new Image(Display.getDefault(), bounds);

            GC gc2 = new GC(buffer);
            drawChartOntoGC(gc2, null);
            gc2.dispose();
            return buffer;
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            _savingChartImage = false;
            _verticalScrollPosition = oldVscroll;
        }

        return null;
    }

    private List getEventsDependingOn(GanttEvent ge) {
        if (_ganttConnections.size() == 0) return new ArrayList();

        GanttMap gm = new GanttMap();

        for (int i = 0; i < _ganttConnections.size(); i++) {
            GanttConnection conn = (GanttConnection) _ganttConnections.get(i);
            gm.put(conn.getSource(), conn.getTarget());
            gm.put(conn.getTarget(), conn.getSource());
        }

        HashSet ret = _getEventsDependingOn(ge, gm, new HashSet());
        if (!ret.contains(ge)) ret.add(ge);

        List retList = new ArrayList();
        Iterator ite = ret.iterator();
        while (ite.hasNext()) {
            retList.add(ite.next());
        }

        return retList;
    }

    // get all chained events
    private HashSet _getEventsDependingOn(GanttEvent ge, GanttMap gm, HashSet ret) {
        List conns = gm.get(ge);
        if (conns != null && conns.size() > 0) {
            for (int i = 0; i < conns.size(); i++) {
                GanttEvent event = (GanttEvent) conns.get(i);
                if (ret.contains(event)) continue;
                else ret.add(event);

                HashSet more = _getEventsDependingOn(event, gm, ret);
                if (more.size() > 0) {
                    Iterator ite = more.iterator();
                    while (ite.hasNext()) {
                        Object obj = ite.next();
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
        int originalDayWidth = _settings.getDayWidth();
        int originalMonthWeekWidth = _settings.getMonthDayWidth();
        int originalYearMonthDayWidth = _settings.getYearMonthDayWidth();

        boolean dDay = false;
        if (_currentView == ISettings.VIEW_D_DAY) {
            dDay = true;
        }

        switch (_zoomLevel) {
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
        }

        _weekWidth = _dayWidth * 7;
        _monthWeekWidth = _monthDayWidth * 7;

        //mYearMonthWeekWidth = mYearDayWidth * 7;
        //mYearMonthWidth = mYearMonthWeekWidth * 4;

        // not a hack, we just re-use the same parameter name but for a
        // different purpose than the name itself, not exactly great logic
        // but it saves some recoding
        if (_zoomLevel == ISettings.ZOOM_HOURS_MAX || _zoomLevel == ISettings.ZOOM_HOURS_MEDIUM || _zoomLevel == ISettings.ZOOM_HOURS_NORMAL) {
            // how many hours are there really in our work day? we don't show
            // anything else!
            _weekWidth = _dayWidth * 24;
        }

        if (dDay) {
            _currentView = ISettings.VIEW_D_DAY;
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
        _reCalculateScopes = true;
        _reCalculateSectionBounds = true;

        for (int i = 0; i < _ganttEvents.size(); i++) {
            ((GanttEvent) _ganttEvents.get(i)).setBoundsSet(false);
        }

        _forceScrollbarsUpdate = true;
    }

    ViewPortHandler getViewPortHandler() {
        return _viewPortHandler;
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
            GanttEvent ge = (GanttEvent) _ganttEvents.get(i);
            Rectangle actualBounds = ge.getActualBounds();

            if (left) {
                if (actualBounds.x < max) {
                    max = actualBounds.x;
                }
            } else {
                int w = actualBounds.x + actualBounds.width;
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
        if (level == _zoomLevel) return;

        int toSet = level;

        if (toSet < ISettings.MIN_ZOOM_LEVEL) toSet = ISettings.MIN_ZOOM_LEVEL;
        if (toSet > ISettings.MAX_ZOOM_LEVEL) toSet = ISettings.MAX_ZOOM_LEVEL;

        int oldZoomLevel = _zoomLevel;
        _zoomLevel = toSet;

        updateZoomLevel();
        _zoomLevelChanged = true;

        forceFullUpdate();

        for (int i = 0; i < _eventListeners.size(); i++) {
            IGanttEventListener listener = (IGanttEventListener) _eventListeners.get(i);
            if (toSet < oldZoomLevel) {
                listener.zoomedOut(_zoomLevel);
            } else {
                listener.zoomedIn(_zoomLevel);
            }
        }
    }

    /**
     * Zooms in. If zooming is disabled, does nothing.
     * 
     * @param showHelper Whether to show the help area or not.
     */
    public void zoomIn(boolean showHelper) {
        zoomIn(showHelper, false, null);
    }

    private void zoomIn(boolean showHelper, boolean fromMouseWheel, Point mouseLoc) {
        checkWidget();
        if (!_settings.enableZooming()) return;
        /*
        if (mCurrentView == ISettings.VIEW_D_DAY)
        	return;*/

        _zoomLevel--;
        if (_zoomLevel < ISettings.MIN_ZOOM_LEVEL) {
            _zoomLevel = ISettings.MIN_ZOOM_LEVEL;
            return;
        }

        Calendar preZoom = null;
        if (fromMouseWheel && mouseLoc != null) preZoom = getDateAt(mouseLoc.x);

        updateZoomLevel();

        if (fromMouseWheel && _settings.zoomToMousePointerDateOnWheelZooming() && mouseLoc != null) internalSetDateAtX(mouseLoc.x, preZoom, true, false, true);

        _zoomLevelChanged = true;

        forceFullUpdate();

        for (int i = 0; i < _eventListeners.size(); i++) {
            IGanttEventListener listener = (IGanttEventListener) _eventListeners.get(i);
            listener.zoomedIn(_zoomLevel);
        }

    }

    /**
     * Zooms out. If zooming is disabled, does nothing.
     * 
     * @param showHelper Whether to show the help area or not.
     */
    public void zoomOut(boolean showHelper) {
        zoomOut(showHelper, false, null);
    }

    private void zoomOut(boolean showHelper, boolean fromMouseWheel, Point mouseLoc) {
        checkWidget();
        if (!_settings.enableZooming()) return;

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

        updateZoomLevel();

        if (fromMouseWheel && _settings.zoomToMousePointerDateOnWheelZooming() && mouseLoc != null) {
            internalSetDateAtX(mouseLoc.x, preZoom, true, false, false);
        }

        _zoomLevelChanged = true;
        forceFullUpdate();

        for (int i = 0; i < _eventListeners.size(); i++) {
            IGanttEventListener listener = (IGanttEventListener) _eventListeners.get(i);
            listener.zoomedOut(_zoomLevel);
        }
    }

    /**
     * Resets the zoom level to that set in the settings.
     */
    public void resetZoom() {
        checkWidget();
        if (!_settings.enableZooming()) return;

        _zoomLevel = _settings.getInitialZoomLevel();

        updateZoomLevel();

        _zoomLevelChanged = true;
        forceFullUpdate();

        for (int i = 0; i < _eventListeners.size(); i++) {
            IGanttEventListener listener = (IGanttEventListener) _eventListeners.get(i);
            listener.zoomReset();
        }
    }

    // override so we can tell paint manager to reset
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
        if (_eventListeners.contains(listener)) return;

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
        return _useAdvancedTooltips;
    }

    /**
     * Sets whether to use advanced tooltips or not. This method will override the settings implementation with the same
     * name.
     * 
     * @param useAdvancedTooltips true whether to use advanced tooltips.
     */
    public void setUseAdvancedTooltips(boolean useAdvancedTooltips) {
        _useAdvancedTooltips = useAdvancedTooltips;
    }

    /**
     * Returns (a clone) of the D-Day calendar
     * 
     * @return D-Day calendar
     */
    public Calendar getDDayCalendar() {
        return (Calendar) _dDayCalendar.clone();
    }

    /**
     * Returns the Undo/Redo manager.
     * 
     * @return
     */
    public GanttUndoRedoManager getUndoRedoManager() {
        return _undoRedoManager;
    }
    
    // as from:
    // http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet139.java?view=co
    static ImageData rotate(ImageData srcData, int direction) {
        int bytesPerPixel = srcData.bytesPerLine / srcData.width;
        int destBytesPerLine = (direction == SWT.DOWN) ? srcData.width * bytesPerPixel : srcData.height * bytesPerPixel;
        byte[] newData = new byte[srcData.data.length];
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
                }
                destIndex = (destY * destBytesPerLine) + (destX * bytesPerPixel);
                srcIndex = (srcY * srcData.bytesPerLine) + (srcX * bytesPerPixel);
                System.arraycopy(srcData.data, srcIndex, newData, destIndex, bytesPerPixel);
            }
        }
        // destBytesPerLine is used as scanlinePad to ensure that no padding is
        // required
        return new ImageData(width, height, srcData.depth, srcData.palette, destBytesPerLine, newData);
    }

    static ImageData flip(ImageData srcData, boolean vertical) {
        int bytesPerPixel = srcData.bytesPerLine / srcData.width;
        int destBytesPerLine = srcData.width * bytesPerPixel;
        byte[] newData = new byte[srcData.data.length];
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
                destIndex = (destY * destBytesPerLine) + (destX * bytesPerPixel);
                srcIndex = (srcY * srcData.bytesPerLine) + (srcX * bytesPerPixel);
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
        return !_ganttSpecialDateRanges.isEmpty();
    }
}
