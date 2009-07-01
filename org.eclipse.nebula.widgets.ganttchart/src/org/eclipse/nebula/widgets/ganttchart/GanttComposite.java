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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
// -- FEATURES
// TODO: Custom headers
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

    // out of bounds sides, internal
    private static final int           VISIBILITY_VISIBLE           = 1;
    private static final int           VISIBILITY_OOB_LEFT          = 2;
    private static final int           VISIBILITY_OOB_RIGHT         = 3;
    private static final int           VISIBILITY_NOT_VISIBLE       = 4;
    private static final int           VISIBILITY_OOB_HEIGHT_TOP    = 5;
    private static final int           VISIBILITY_OOB_HEIGHT_BOTTOM = 6;

    // resize info, internal
    private static final int           TYPE_RESIZE_LEFT             = 1;
    private static final int           TYPE_RESIZE_RIGHT            = 2;
    private static final int           TYPE_MOVE                    = 3;

    private static final int           TIMER_INTERVAL               = 25;

    // current auto scroll direction
    private int                        mAutoScrollDirection         = SWT.NULL;

    // keeps track of when to show or hide the zoom-helper area, go Clippy!
    private boolean                    mShowZoomHelper;
    private Rectangle                  mZoomLevelHelpArea;

    // start level
    private int                        mZoomLevel;
    private boolean                    mZoomLevelChanged;

    private boolean                    mForceScrollbarsUpdate;

    // current view
    private int                        mCurrentView;

    // 3D looking events
    private boolean                    mThreeDee;

    // show a little plaque telling the days between start and end on the chart,
    // only draws if it fits
    private boolean                    mShowNumberOfDaysOnChart;

    // draw the revised dates as a very thin small black |------| on the chart
    private boolean                    mShowPlannedDates;

    // how many days offset to start drawing the calendar at.. useful for not
    // having the first day be today
    private int                        mStartCalendarAtOffset;

    // how many pixels from each event border to ignore as a move.. mostly to
    // help resizing
    private int                        mMoveAreaInsets;

    // year stuff
    private int                        mYearDayWidth;

    //private int							mYearMonthWeekWidth;

    //private int							mYearMonthWidth;

    // month stuff
    private int                        mMonthDayWidth;

    private int                        mMonthWeekWidth;

    // day stuff
    // width of one day
    private int                        mDayWidth;

    // one week width, usually 7 days * width of one day
    private int                        mWeekWidth;

    private int                        mBottomMostY;

    // various colors used.. all set in initColors()
    private Color                      mLineTodayColor;

    private Color                      mLineColor;

    private Color                      mLineWeekDividerColor;

    private Color                      mTextColor;

    private Color                      mSaturdayBackgroundColorTop;
    private Color                      mSaturdayBackgroundColorBottom;

    private Color                      mWeekdayTextColor;
    private Color                      mSaturdayTextColor;
    private Color                      mSundayTextColor;

    private Color                      mSundayBackgroundColorTop;
    private Color                      mSundayBackgroundColorBottom;

    private Color                      mWeekdayBackgroundColorTop;
    private Color                      mWeekdayBackgroundColorBottom;

    private Color                      mTextHeaderBackgroundColorTop;
    private Color                      mTextHeaderBackgroundColorBottom;
    private Color                      mTimeHeaderBackgroundColorTop;
    private Color                      mTimeHeaderBackgroundColorBottom;

    private Color                      mArrowColor;
    private Color                      mReverseArrowColor;

    private Color                      mTodayBGColorTop;
    private Color                      mTodayBGColorBottom;

    // currently selected event
    private List                       mSelectedEvents;

    // the calendar that the current view is on, will always be on the first
    // date of the visible area (the date on the far left)
    // it should never change from internal functions. Where date changes, a
    // clone is made. It is critical it is never modified unless there is a reason for it.
    private Calendar                   mCalendar;

    // end calendar, is only used for reference internally
    private Calendar                   mEndCalendar;

    // the number of days that will be visible in the current area. Is set after
    // we're done drawing the chart
    private int                        mDaysVisible;
    private int                        mHoursVisible;

    // all events
    private List                       mGanttEvents;

    // all connections between events
    private List                       mGanttConnections;

    // a cache for re-used string extents
    private HashMap                    mDayLetterStringExtentMap;

    // various variables for resize and drag/drop
    private boolean                    mDragging                    = false;

    private boolean                    mResizing                    = false;

    private int                        mLastX;

    private int                        mCursor;

    private List                       mDragEvents;

    private boolean                    mLastLeft                    = false;

    private Calendar                   mDragStartDate               = null;

    private boolean                    mJustStartedMoveOrResize     = false;

    private int                        mInitialHoursDragOffset      = 0;

    // what operating system we're on
    public static final int            OS_OTHER                     = 0;

    public static final int            OS_WINDOWS                   = 1;

    public static final int            OS_MAC                       = 2;

    public static final int            OS_LINUX                     = 3;

    public static int                  osType                       = OS_OTHER;

    private ISettings                  mSettings;

    private IColorManager              mColorManager;

    private IPaintManager              mPaintManager;

    private ILanguageManager           mLanguageManager;

    private List                       mEventListeners;

    private boolean                    mMouseIsDown;

    private Point                      mMouseDragStartLocation;

    private List                       mGanttGroups;

    private List                       mGanttSections;

    // menu used at right click events
    private Menu                       mRightClickMenu;

    // whether advanced tooltips are on or not
    private boolean                    mUseAdvancedTooltips;

    // whether alpha drawing is enabled or not
    private boolean                    mUseAlpha;

    private Rectangle                  mBounds;

    // bounds that are currently visible (changes when scrolling vertically)
    private Rectangle                  mVisibleBounds;

    private Locale                     mDefaultLocale;

    private int                        mEventHeight;

    private boolean                    mReCalculateScopes           = true;
    private boolean                    mReCalculateSectionBounds    = true;

    private HashSet                    mAllEventsCombined;                                                      // convenience list for all events regardless if they're in
    // sections, in groups, or single

    // it's been so long since this was off that I say we permanently set it to true
    private boolean                    mUseFastDrawing              = true;

    private List                       mVerticalLineLocations;                                                  // fast cache for where the vertical lines go, much much

    // faster
    // than
    // re-calculating over and over
    private HashSet                    mVerticalWeekDividerLineLocations;

    // keeps track of hidden layers
    private HashSet                    mHiddenLayers;
    // keeps track of layer opacities
    private HashMap                    mLayerOpacityMap;

    // debug variables
    long                               mTotal                       = 0;
    int                                mRedrawCount                 = 0;

    // only settings variables we allow direct override on
    private int                        mEventSpacer;
    private int                        mFixedRowHeight;
    private boolean                    mDrawVerticalLines;
    private boolean                    mDrawHorizontalLines;
    // end

    private int                        mLockedHeaderY;

    private int                        mVerticalScrollPosition;

    private final Point                mOrigin                      = new Point(0, 0);
    private int                        mLastVerticalScrollPosition;

    private ScrollBar                  mVerticalScrollBar;

    private List                       mSelectedHeaderDates;

    private int                        mStyle;

    private Tracker                    mTracker;
    private boolean                    mMultiSelect;

    private Calendar                   mDDayCalendar;

    private HorizontalScrollbarHandler _hScrollHandler;
    private ViewPortHandler            _viewPortHandler;

    private boolean                    mSavingChartImage            = false;                                     ;

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

    public GanttComposite(GanttChart parent, int style, ISettings settings, IColorManager colorManager, IPaintManager paintManager, ILanguageManager languageManager) {
        super(parent, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED | SWT.V_SCROLL | SWT.H_SCROLL);

        // d-day calendar can be anything, but it needs to be something, and for convenience, lets use 2000 as base year
        if (settings.getInitialView() == ISettings.VIEW_D_DAY) {
            mDDayCalendar = (Calendar) settings.getDDayRootCalendar().clone();            
        }

        mStyle = style;
        mMultiSelect = (mStyle & SWT.MULTI) != 0;

        mSettings = settings;
        mColorManager = colorManager;
        mPaintManager = paintManager;
        mLanguageManager = languageManager;

        mGanttConnections = new ArrayList();
        mDragEvents = new ArrayList();
        mEventListeners = new ArrayList();
        mGanttEvents = new ArrayList();
        mGanttGroups = new ArrayList();
        mGanttSections = new ArrayList();
        mVerticalLineLocations = new ArrayList();
        mVerticalWeekDividerLineLocations = new HashSet();
        mHiddenLayers = new HashSet();
        mAllEventsCombined = new HashSet();
        mDayLetterStringExtentMap = new HashMap();
        mLayerOpacityMap = new HashMap();
        mSelectedHeaderDates = new ArrayList();
        mSelectedEvents = new ArrayList();

        mDefaultLocale = mSettings.getDefaultLocale();
        mUseAdvancedTooltips = mSettings.getUseAdvancedTooltips();

        mCurrentView = mSettings.getInitialView();
        mZoomLevel = mSettings.getInitialZoomLevel();
        mShowNumberOfDaysOnChart = mSettings.showNumberOfDaysOnBars();
        mShowPlannedDates = mSettings.showPlannedDates();
        mThreeDee = mSettings.showBarsIn3D();
        mYearDayWidth = mSettings.getYearMonthDayWidth();
        mMonthDayWidth = mSettings.getMonthDayWidth();
        mMonthWeekWidth = mMonthDayWidth * 7;
        mDayWidth = mSettings.getDayWidth();
        mWeekWidth = mDayWidth * 7;
        mStartCalendarAtOffset = mSettings.getCalendarStartupDateOffset();
        mCalendar = mSettings.getStartupCalendarDate();
        mMoveAreaInsets = mSettings.getMoveAreaNegativeSensitivity();
        mEventSpacer = mSettings.getEventSpacer();
        mEventHeight = mSettings.getEventHeight();

        mDrawHorizontalLines = mSettings.drawHorizontalLines();
        mDrawVerticalLines = mSettings.drawVerticalLines();

        updateZoomLevel();

        if (mCurrentView == ISettings.VIEW_D_DAY) {
            mCalendar = (Calendar) mDDayCalendar.clone();
        }

        // by default, set today's date
        setDate(mCalendar, true, false);

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
                if (!mSettings.enableZooming()) { return; }

                if (event.stateMask == mSettings.getZoomWheelModifierKey()) {
                    mShowZoomHelper = mSettings.showZoomLevelBox();

                    if (event.count > 0) {
                        zoomIn(mSettings.showZoomLevelBox(), true, new Point(event.x, event.y));
                    } else {
                        zoomOut(mSettings.showZoomLevelBox(), true, new Point(event.x, event.y));
                    }
                } else {
                    // Linux doesn't honor scrollwheel vs. scrollbar in the same way that OS X / Windows does
                    // so we actually need to force it manually. I have no idea why, but it works fine this way.
                    if (osType == OS_LINUX || mSettings.forceMouseWheelVerticalScroll()) {
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
                if (mTracker != null) {
                    mTracker.dispose();
                    mTracker = null;
                    mMouseIsDown = false;
                }
                if (mShowZoomHelper) {
                    mShowZoomHelper = false;
                    // redraw the area where it was
                    if (mZoomLevelHelpArea != null) {
                        redraw(mZoomLevelHelpArea.x, mZoomLevelHelpArea.y, mZoomLevelHelpArea.width + 1, mZoomLevelHelpArea.height + 1, false);
                    }
                }
            }
        });

        // viewport handler must be created before scrollbar handler
        _viewPortHandler = new ViewPortHandler(this);
        // horizontal scrollbar handler, deals with all scrollbar movements, scrollbar types, etc
        _hScrollHandler = new HorizontalScrollbarHandler(this, getHorizontalBar(), mStyle);

        mVerticalScrollBar = getVerticalBar();
        mVerticalScrollBar.setPageIncrement(mSettings.getEventHeight() + mSettings.getEventSpacer());
        mVerticalScrollBar.setIncrement(mSettings.getEventHeight());
        mVerticalScrollBar.setVisible(false);
        mVerticalScrollBar.addListener(SWT.Selection, new Listener() {
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
        if (e.detail == 0) {
            killDialogs();
            return;
        }

        // this has got to be a SWT bug, a non-visible scrollbar can report scroll events!
        if (!mVerticalScrollBar.isVisible()) {
            mVerticalScrollPosition = 0;
            return;
        }

        int vSelection = mVerticalScrollBar.getSelection();

        mVerticalScrollPosition = vSelection;
        int diff = mVerticalScrollPosition - mLastVerticalScrollPosition;
        mLastVerticalScrollPosition = mVerticalScrollPosition;

        // move all events the fast way. There is truly no reason to recalculate bounds for any event, all we need to do is move them
        // according to the scroll bar, which is way way faster than recalculating.
        moveYBounds(diff);
        showVscrollInfo();

        mReCalculateSectionBounds = true;
        redraw();
    }

    void updateVerticalScrollBar(boolean redraw) {
        handleResize(redraw);
    }

    void handleResize(boolean redraw) {
        Rectangle rect = getBounds();
        Rectangle client = getClientArea();
        mVerticalScrollBar.setMaximum(rect.height);
        mVerticalScrollBar.setPageIncrement(15);
        mVerticalScrollBar.setThumb(Math.min(rect.height, client.height));
        int vPage = rect.height - client.height;
        int vSelection = mVerticalScrollBar.getSelection();
        if (vSelection >= vPage) {
            if (vPage <= 0) vSelection = 0;
            mOrigin.y = -vSelection;
        }

        // TODO: Do fancy thing where client area moves with the resize.. (low priority)

        if (mBottomMostY < getClientArea().height) {
            // if we reached the end, make sure we're back at the top
            if (mVerticalScrollBar.isVisible()) {
                mVerticalScrollBar.setVisible(false);
                if (mVerticalScrollPosition != 0) {
                    moveYBounds(-mVerticalScrollPosition);
                    mVerticalScrollPosition = 0;
                }
            }
        } else {
            mVerticalScrollBar.setVisible(true);
        }

        if (mEndCalendar != null) {
            updateEventVisibilities(client);
        }

        mReCalculateSectionBounds = true;

        if (redraw) {
            redraw();
        }
    }

    private void initColors() {
        mLineTodayColor = mColorManager.getTodayLineColor();
        mLineColor = mColorManager.getLineColor();
        mTextColor = mColorManager.getTextColor();
        mSaturdayTextColor = mColorManager.getSaturdayTextColor();

        mSaturdayBackgroundColorTop = mColorManager.getSaturdayBackgroundColorTop();
        mSaturdayBackgroundColorBottom = mColorManager.getSaturdayBackgroundColorBottom();
        mSundayBackgroundColorTop = mColorManager.getSundayBackgroundColorTop();
        mSundayBackgroundColorBottom = mColorManager.getSundayBackgroundColorBottom();

        mWeekdayTextColor = mColorManager.getWeekdayTextColor();
        mSundayTextColor = mColorManager.getSundayTextColor();
        mSaturdayTextColor = mColorManager.getSaturdayTextColor();
        mWeekdayBackgroundColorTop = mColorManager.getWeekdayBackgroundColorTop();
        mWeekdayBackgroundColorBottom = mColorManager.getWeekdayBackgroundColorBottom();
        mLineWeekDividerColor = mColorManager.getWeekDividerLineColor();
        mTextHeaderBackgroundColorTop = mColorManager.getTextHeaderBackgroundColorTop();
        mTextHeaderBackgroundColorBottom = mColorManager.getTextHeaderBackgroundColorBottom();
        mTimeHeaderBackgroundColorTop = mColorManager.getTimeHeaderBackgroundColorTop();
        mTimeHeaderBackgroundColorBottom = mColorManager.getTimeHeaderBackgroundColorBottom();
        mArrowColor = mColorManager.getArrowColor();
        mReverseArrowColor = mColorManager.getReverseArrowColor();
        mTodayBGColorTop = mColorManager.getTodayBackgroundColorTop();
        mTodayBGColorBottom = mColorManager.getTodayBackgroundColorBottom();
        mUseAlpha = mColorManager.useAlphaDrawing();
    }

    /**
     * Returns the current date (left-most date).
     * 
     * @return
     */
    public Calendar getDate() {
        return (Calendar) mCalendar.clone();
    }

    Calendar getRootCalendar() {
        return mCalendar;
    }

    Calendar getRootEndCalendar() {
        return mEndCalendar;
    }

    /**
     * Hides all layers of the given value and redraws the event area.
     * 
     * @param layer Layer to hide.
     */
    public void hideLayer(int layer) {
        if (!mHiddenLayers.contains(new Integer(layer))) {
            mHiddenLayers.add(new Integer(layer));
        }
    }

    /**
     * Shows all layers of the given value and redraws the event area.
     * 
     * @param layer Layer to show.
     */
    public void showLayer(int layer) {
        boolean removed = mHiddenLayers.remove(new Integer(layer));
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
        if (mHiddenLayers.size() == 0) return;

        mHiddenLayers.clear();
        redrawEventsArea();
    }

    /**
     * Hides all layers and redraws the event area.
     */
    public void hideAllLayers() {
        for (int i = 0; i < mGanttEvents.size(); i++) {
            GanttEvent ge = (GanttEvent) mGanttEvents.get(i);
            if (!mHiddenLayers.contains(new Integer(ge.getLayer()))) mHiddenLayers.add(new Integer(ge.getLayer()));
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
            mLayerOpacityMap.remove(new Integer(layer));
            return;
        }

        if (toSet < 0) toSet = 0;

        mLayerOpacityMap.put(new Integer(layer), new Integer(toSet));
        redrawEventsArea();
    }

    /**
     * Returns the layer opacity for a layer.
     * 
     * @param layer Layer to get opacity for
     * @return Layer opacity, -1 if layer has no opacity set.
     */
    public int getLayerOpacity(int layer) {
        if (mLayerOpacityMap.get(new Integer(layer)) == null) return -1;

        return ((Integer) mLayerOpacityMap.get(new Integer(layer))).intValue();
    }

    /**
     * Setting a fixed row height override causes all rows to be the set height regardless of individual row heights set
     * on items themselves and all settings.
     * 
     * @param height Height to set. Set to zero to turn off.
     */
    public void setFixedRowHeightOverride(int height) {
        mFixedRowHeight = height;
    }

    /**
     * Setting a fixed event spacer overrides all individual event space settings on chart items and all settings.
     * 
     * @param height Height to set. Set to zero to turn off.
     */
    public void setEventSpacerOverride(int height) {
        mEventSpacer = height;
    }

    /**
     * Setting this to true will force horizontal lines to draw despite what may be set in the settings.
     * 
     * @param drawHorizontal true to draw horizontal lines.
     */
    public void setDrawHorizontalLinesOverride(boolean drawHorizontal) {
        mDrawHorizontalLines = drawHorizontal;
    }

    /**
     * Setting this to true will force vertical lines to draw despite what may be set in the settings.
     * 
     * @param drawVertical true to draw vertical lines.
     */
    public void setDrawVerticalLinesOverride(boolean drawVertical) {
        mDrawVerticalLines = drawVertical;
    }

    /**
     * Sets the selection to be a specific GanttEvent. This method will cause a redraw.
     * 
     * @param ge GanttEvent to select
     */
    public void setSelection(GanttEvent ge) {
        mSelectedEvents.clear();
        mSelectedEvents.add(ge);
        redrawEventsArea();
    }

    /**
     * Sets the selection to be a set of GanttEvents. If the chart is set to <code>SWT.SINGLE</code> you should be using
     * {@link #setSelection(GanttEvent)} as this method will do nothing. This method will cause a redraw.
     * 
     * @param list List of GanttEvents to select
     */
    public void setSelection(ArrayList list) {
        if (!mMultiSelect) return;

        mSelectedEvents.clear();
        mSelectedEvents.addAll(list);
        redrawEventsArea();
    }

    /**
     * Adds a GanttGroup to the chart.
     * 
     * @param group Group to add
     */
    public void addGroup(GanttGroup group) {
        if (!mGanttGroups.contains(group)) mGanttGroups.add(group);

        eventNumbersChanged();
    }

    /**
     * Returns a list of all GanttGroups.
     * 
     * @return List of GanttGroups
     */
    public List getGroups() {
        return mGanttGroups;
    }

    /**
     * Removes a GanttGroup from the chart.
     * 
     * @param group Group to remove
     */
    public void removeGroup(GanttGroup group) {
        mGanttGroups.remove(group);

        eventNumbersChanged();
    }

    /**
     * Adds a GanttSection to the chart.
     * 
     * @param section Section to add
     */
    public void addSection(GanttSection section) {
        if (!mGanttSections.contains(section)) mGanttSections.add(section);

        eventNumbersChanged();
    }

    /**
     * Removes a GanttSection from the chart.
     * 
     * @param section Section to remove
     */
    public void removeSection(GanttSection section) {
        mGanttSections.remove(section);

        eventNumbersChanged();
    }

    /**
     * Returns a list of all GanttSections.
     * 
     * @return List of GanttSections.
     */
    public List getGanttSections() {
        return mGanttSections;
    }

    /*
    	void scrollingLeft(int diff) {
    		checkWidget();

    		showScrollDate();

    		if (diff == 1) {
    			if (mCurrentView == ISettings.VIEW_YEAR) {
    				prevMonth();
    				return;
    			}
    			else {
    				if (mCurrentView == ISettings.VIEW_DAY) {
    					prevHour();
    					return;
    				}
    				else {
    					prevDay();
    					return;
    				}
    			}
    		}
    		else {
    			if (diff == 0) {
    				if (mCurrentView == ISettings.VIEW_YEAR) {
    					prevWeek();
    					return;
    				}
    				else {
    					if (mCurrentView == ISettings.VIEW_DAY) {
    						prevHour();
    						return;
    					}
    					else {
    						prevDay();
    						return;
    					}
    				}
    			}

    			// year scrolls so fast that the events actually lose position, this is no good, so we need to break the speed
    			if (mCurrentView == ISettings.VIEW_YEAR) {
    				if (diff > 7)
    					diff = 7;
    			}

    			for (int i = 0; i < diff; i++) {
    				if (mCurrentView == ISettings.VIEW_YEAR) {
    					prevMonth();
    				}
    				else {
    					if (mCurrentView == ISettings.VIEW_DAY) {
    						prevHour();
    					}
    					else {
    						prevDay();
    					}
    				}
    			}
    		}
    	}

    	void scrollingRight(int diff) {
    		checkWidget();

    		showScrollDate();

    		if (diff == 1) {
    			if (mCurrentView == ISettings.VIEW_YEAR) {
    				nextMonth();
    				return;
    			}
    			else {
    				if (mCurrentView == ISettings.VIEW_DAY) {
    					nextHour();
    					return;
    				}
    				else {
    					nextDay();
    					return;
    				}
    			}
    		}
    		else {
    			// far right
    			if (diff == 0) {
    				if (mCurrentView == ISettings.VIEW_YEAR) {
    					nextWeek();
    					return;
    				}
    				else {
    					if (mCurrentView == ISettings.VIEW_DAY) {
    						nextHour();
    						return;
    					}
    					else {
    						nextDay();
    						return;
    					}
    				}
    			}

    			// year scrolls so fast that the events actually lose position, this is no good, so we need to break the speed
    			// TODO: look into this a bit more, as it's quite odd (same for scrollingLeft)
    			if (mCurrentView == ISettings.VIEW_YEAR) {
    				if (diff > 7)
    					diff = 7;
    			}

    			for (int i = 0; i < diff; i++) {
    				if (mCurrentView == ISettings.VIEW_YEAR) {
    					nextMonth();
    				}
    				else {
    					if (mCurrentView == ISettings.VIEW_DAY) {
    						nextHour();
    					}
    					else {
    						nextDay();
    					}
    				}
    			}
    		}
    	}
    */
    // horizontal scrollbar info
    void showScrollDate() {
        if (mSettings.showDateTips() && mSettings.showDateTipsOnScrolling()) {
            String str = DateHelper.getDate(mCalendar, mCurrentView == ISettings.VIEW_DAY ? mSettings.getHourDateFormat() : mSettings.getDateFormat(), mDefaultLocale);
            GC gc = new GC(this);
            Point ext = gc.stringExtent(str);
            gc.dispose();

            int bottomY = mBounds.height - ext.y - 12;
            int bottomX = mBounds.x + (mBounds.width / 2) - ext.x;

            Point p = toDisplay(bottomX, bottomY);
            GanttDateTip.makeDialog(mColorManager, str, p, bottomY);
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

    private GanttEvent _getTopEvent() {
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

    /**
     * Returns the topmost visible event in the current view of the chart.
     * 
     * @return GanttEvent or null
     */
    public GanttEvent getTopEvent() {
        for (int i = 0; i < mGanttEvents.size(); i++) {
            GanttEvent ge = (GanttEvent) mGanttEvents.get(i);

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

        for (int i = 0; i < mGanttEvents.size(); i++) {
            GanttEvent ge = (GanttEvent) mGanttEvents.get(i);
            if (i < mGanttEvents.size() - 1) {
                GanttEvent nextEvent = (GanttEvent) mGanttEvents.get(i + 1);

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
        mReCalculateScopes = recalc;
    }

    void setReCalculateSectionBounds(boolean recalc) {
        mReCalculateSectionBounds = recalc;
    }

    void killDialogs() {
        if (mSettings.showToolTips()) {
            GanttToolTip.kill();
            GanttDateTip.kill();
            AdvancedTooltipDialog.kill();
        }
    }

    // the repaint event, whenever the composite needs to refresh the contents
    private void repaint(PaintEvent event) {
        GC gc = event.gc;
        drawChartOntoGC(gc, null);
    }

    // draws the actual chart.. separated from the repaint event as the
    // getImage() call uses it on a different GC
    // Note: things are in a certain order, as some stuff draws on top of
    // others, so don't move them around unless you want a different effect
    private void drawChartOntoGC(GC gc, Rectangle boundsOverride) {
        // long totaltime1 = System.currentTimeMillis();
        boolean drawSections = (mGanttSections.size() > 0);

        // only reset bottom y if we recalculate it, or we'll lose the vertical scrollbar among other things that update on all redraws
        if (mReCalculateScopes || drawSections) {
            mBottomMostY = 0;
        }

        Rectangle bounds = super.getClientArea();
        if (boundsOverride != null) {
            bounds = boundsOverride;
        }

        // the actual visible bounds counting any vertical scroll offset (includes header height)
        mVisibleBounds = new Rectangle(bounds.x, bounds.y + mVerticalScrollPosition, bounds.width, bounds.height);

        // if we use sections, our bounds for everything will be the size of the client area minus the section bar on the left
        if (drawSections) {
            if (mSettings.getSectionSide() == SWT.LEFT) {
                bounds = new Rectangle(mSettings.getSectionBarWidth(), bounds.y, bounds.width - mSettings.getSectionBarWidth(), bounds.height);
            } else {
                bounds = new Rectangle(0, bounds.y, bounds.width - mSettings.getSectionBarWidth(), bounds.height);
            }
        }

        mBounds = bounds;
        mLockedHeaderY = mBounds.y;
        mBounds.y -= mVerticalScrollPosition;

        // header
        if (mSettings.drawHeader()) {
            // we need to draw the header regardless of locked state as it updates the location of the 
            // vertical lines
            drawHeader(gc);
        } else {
            // we need the mDaysVisible value which is normally calculated when we draw boxes, as the header is not drawn here, we need to calculate it manually
            calculateDaysVisible(bounds);
        }

        updateEventVisibilities(mVisibleBounds);

        // section drawing needs special treatment as we need to give sub-bounds to the various drawing methods
        if (drawSections) {
            if (!mUseFastDrawing || mReCalculateSectionBounds) {
                calculateSectionBounds(gc, bounds);
            }

            // if we fill the bottom then fill it!
            if (mSettings.drawFillsToBottomWhenUsingGanttSections()) {
                Rectangle extraBounds = new Rectangle(mBounds.x, mBounds.y + getHeaderHeight() - mVerticalScrollPosition, mBounds.x + mBounds.width, mBounds.y + mBounds.height - getHeaderHeight() + mVerticalScrollPosition);
                drawFills(gc, extraBounds);
                drawVerticalLines(gc, extraBounds, false);
            }

            for (int i = 0; i < mGanttSections.size(); i++) {
                GanttSection gs = (GanttSection) mGanttSections.get(i);
                Rectangle gsBounds = gs.getBounds();

                if (boundsOverride != null) {
                    gsBounds.width = boundsOverride.width;
                }

                if (!mUseFastDrawing || mReCalculateScopes) {
                    calculateAllScopes(gsBounds, gs);
                }

                drawFills(gc, gsBounds, gs);

                // draw vertical lines
                if (mDrawVerticalLines) {
                    drawVerticalLines(gc, gsBounds, false);
                }

                // more lines
                if (mDrawHorizontalLines) {
                    drawHorizontalLines(gc, bounds);
                }

                // draw events
                drawEvents(gc, gsBounds, gs);
            }

            // before we drew connections inside the loop below, which was totally pointless. We only need to draw connections once for the visible area,
            // not once per section. This is way faster, connection drawing is not 0ms
            drawConnections(gc, mBounds);

            // just because I have the feeling some user will want cross-section connections, we allow it by
            // drawing the connecting lines _last_. Why? because the event bounds are not calculated until the event is drawn, and if we have
            // a connection to a group/event that hasn't been drawn yet, it would draw an arrow into space..
            for (int i = 0; i < mGanttSections.size(); i++) {
                GanttSection gs = (GanttSection) mGanttSections.get(i);
                mBottomMostY = Math.max(gs.getBounds().y + gs.getBounds().height, mBottomMostY);
            }

            // if we zoom in / out in the middle of a scroll we need to add on where we are to the bottom most y
            mBottomMostY += mVerticalScrollPosition;

        } else {
            bounds = new Rectangle(bounds.x, getHeaderHeight(), bounds.width, bounds.height);

            // long start = System.currentTimeMillis();
            if (mReCalculateScopes) {
                calculateAllScopes(bounds, null);
            }

            // draw fills
            drawFills(gc, bounds);

            // draws vertical lines all over the chart
            if (mDrawVerticalLines) {
                drawVerticalLines(gc, bounds, true);
            }

            if (mDrawHorizontalLines) {
                drawHorizontalLines(gc, bounds);
            }

            // draw the events
            drawEvents(gc, bounds);

            // draw the connecting arrows
            drawConnections(gc, bounds);
        }

        if (drawSections) {
            drawSectionColumn(gc, bounds, false, false, false);
        }

        // if we lock the header, we unfortunately need to draw it again on top of everything else. Down the road this should be optimized of course,
        // but there's so many necessary calculations done in the header drawing that we need for later that it's a bit of work
        if (mSettings.lockHeaderOnVerticalScroll() && mSettings.drawHeader()) {
            drawHeader(gc);
            // draw corner again            
            drawSectionColumn(gc, bounds, true, false, true);           
        }

        // zoom
        if (mShowZoomHelper && mSettings.showZoomLevelBox()) {
            drawZoomLevel(gc);
        }

        // last draw
        if (mSettings.enableLastDraw()) {
            for (int i = 0; i < mEventListeners.size(); i++) {
                IGanttEventListener listener = (IGanttEventListener) mEventListeners.get(i);
                listener.lastDraw(gc);
            }
        }

        // by default these are on, we flag them off when we know for sure we don't need to recalculate bounds
        mReCalculateScopes = false;
        mReCalculateSectionBounds = false;

        if (mZoomLevelChanged) {
            mZoomLevelChanged = false;
            updateHorizontalScrollbar();

            // on zoom level change we update the position, as otherwise the next prev/next horizontal bar click will make it check against
            // the previous zoom level value, which is usually way off, and the entire chart jumps a huge distance which is obviously really bad.
            // as the zoom level has changed, all we do is to say "update the scrollbar, set the new selection position to what it is now after the update"
            // which solves the issue
            _hScrollHandler.resetScrollPosition();
        }

        if (mForceScrollbarsUpdate) {
            updateVerticalScrollBar(true);
            updateHorizontalScrollbar();
            mForceScrollbarsUpdate = false;
        }

        // long totaltime2 = System.currentTimeMillis();
        // String redraw = "redraw: " + (totaltime2 - totaltime1);

        // mRedrawCount++;
        // total += (time2 - time1);
        // System.err.println(redraw + " avg: " + (float) total / (float) redrawCount);
        // System.err.println(redraw);

    }

    /**
     * Flag whether to show planned dates or not. This will override any settings value and will cause a redraw.
     * 
     * @param showPlanned true to show planned dates
     */
    public void setShowPlannedDates(boolean showPlanned) {
        mShowPlannedDates = showPlanned;
        redraw();
    }

    /**
     * Returns whether planned date drawing is currently on or off.
     * 
     * @return true if on
     */
    public boolean isShowingPlannedDates() {
        return mShowPlannedDates;
    }

    /**
     * Flag whether to show the number of days on events. This will override any settings value and will cause a redraw.
     * 
     * @param showDates
     */
    public void setShowDaysOnEvents(boolean showDates) {
        mShowNumberOfDaysOnChart = showDates;
        redraw();
    }

    /**
     * Returns whether event day number drawing is currently on or off.
     * 
     * @return true if on
     */
    public boolean isShowingDaysOnEvents() {
        return mShowNumberOfDaysOnChart;
    }

    private void drawHeader(GC gc) {
        mVerticalLineLocations.clear();
        mVerticalWeekDividerLineLocations.clear();

        Rectangle headerBounds = new Rectangle(mBounds.x, mBounds.y, mBounds.width, mBounds.height);
        if (mSettings.lockHeaderOnVerticalScroll()) {
            headerBounds.y = mLockedHeaderY;
        }

        if (mCurrentView == ISettings.VIEW_DAY) {
            // draw the day at the top
            drawHourTopBoxes(gc, headerBounds);
            // draw the hour ticks below
            drawHourBottomBoxes(gc, headerBounds);
        } else if (mCurrentView == ISettings.VIEW_WEEK) {
            // draw the week boxes
            drawWeekTopBoxes(gc, headerBounds);
            // draw the day boxes
            drawWeekBottomBoxes(gc, headerBounds);
        } else if (mCurrentView == ISettings.VIEW_MONTH) {
            // draws the month boxes at the top
            drawMonthTopBoxes(gc, headerBounds);
            // draws the monthly "week" boxes
            drawMonthBottomBoxes(gc, headerBounds);
        } else if (mCurrentView == ISettings.VIEW_YEAR) {
            // draws the years at the top
            drawYearTopBoxes(gc, headerBounds);
            // draws the month boxes
            drawYearBottomBoxes(gc, headerBounds);
        } else if (mCurrentView == ISettings.VIEW_D_DAY) {
            // draw D-day stuff, for Darren and anyone else who needs it
            drawDDayTopBoxes(gc, headerBounds);
            // draw the bottom d-day
            drawDDayBottomBoxes(gc, headerBounds);
        }

        // draws the 2 horizontal (usually black) lines at the top to make
        // things stand out a little
        drawTopHorizontalLines(gc, headerBounds);
    }

    private void showMenu(int x, int y, GanttEvent event, final MouseEvent me) {
        if (!mSettings.showMenuItemsOnRightClick()) return;

        killDialogs();

        // destroy old menu as to not leak memory or at least GC faster
        if (mRightClickMenu != null && !mRightClickMenu.isDisposed()) mRightClickMenu.dispose();

        mRightClickMenu = new Menu(Display.getDefault().getActiveShell(), SWT.POP_UP);

        if (event != null) {
            // We can't use JFace actions.. so we need to make copies.. Dirty
            // but at least not reinventing a wheel (as much)
            Menu eventMenu = event.getMenu();
            MenuItem[] items = eventMenu.getItems();

            if (items != null) {
                for (int i = 0; i < items.length; i++) {
                    final MenuItem mItem = items[i];

                    MenuItem copy = new MenuItem(mRightClickMenu, SWT.PUSH);
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
            if (!mSettings.showDefaultMenuItemsOnEventRightClick()) {
                mRightClickMenu.setLocation(x, y);
                mRightClickMenu.setVisible(true);
                return;
            }

            if (items != null && items.length > 0) new MenuItem(mRightClickMenu, SWT.SEPARATOR);
        }

        if (mSettings.enableZooming()) {
            // new MenuItem(menu, SWT.SEPARATOR);
            MenuItem zoomIn = new MenuItem(mRightClickMenu, SWT.PUSH);
            zoomIn.setText(mLanguageManager.getZoomInMenuText());
            MenuItem zoomOut = new MenuItem(mRightClickMenu, SWT.PUSH);
            zoomOut.setText(mLanguageManager.getZoomOutMenuText());
            MenuItem zoomReset = new MenuItem(mRightClickMenu, SWT.PUSH);
            zoomReset.setText(mLanguageManager.getZoomResetMenuText());

            zoomIn.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    zoomIn(mSettings.showZoomLevelBox());
                }
            });
            zoomOut.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    zoomOut(mSettings.showZoomLevelBox());
                }
            });
            zoomReset.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    resetZoom();
                }
            });

            new MenuItem(mRightClickMenu, SWT.SEPARATOR);
        }

        final MenuItem showPlaque = new MenuItem(mRightClickMenu, SWT.CHECK);
        showPlaque.setText(mLanguageManager.getShowNumberOfDaysOnEventsMenuText());
        showPlaque.setSelection(mShowNumberOfDaysOnChart);
        showPlaque.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                mShowNumberOfDaysOnChart = !mShowNumberOfDaysOnChart;
                showPlaque.setSelection(mShowNumberOfDaysOnChart);
                redraw();
            }
        });

        new MenuItem(mRightClickMenu, SWT.SEPARATOR);

        final MenuItem showEsts = new MenuItem(mRightClickMenu, SWT.CHECK);
        showEsts.setText(mLanguageManager.getShowPlannedDatesMenuText());
        showEsts.setSelection(mShowPlannedDates);
        showEsts.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                mShowPlannedDates = !mShowPlannedDates;
                showEsts.setSelection(mShowPlannedDates);
                redraw();
            }
        });

        new MenuItem(mRightClickMenu, SWT.SEPARATOR);

        final MenuItem showThreeDee = new MenuItem(mRightClickMenu, SWT.CHECK);
        showThreeDee.setText(mLanguageManager.get3DMenuText());
        showThreeDee.setSelection(mThreeDee);
        showThreeDee.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                mThreeDee = !mThreeDee;
                redraw();
            }
        });

        if (mSettings.showDeleteMenuOption() && event != null) {
            new MenuItem(mRightClickMenu, SWT.SEPARATOR);

            MenuItem delete = new MenuItem(mRightClickMenu, SWT.PUSH);
            delete.setText(mLanguageManager.getDeleteMenuText());
            delete.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event e) {
                    if (mSelectedEvents.size() > 0) {
                        for (int i = 0; i < mEventListeners.size(); i++) {
                            IGanttEventListener listener = (IGanttEventListener) mEventListeners.get(i);
                            listener.eventsDeleteRequest(mSelectedEvents, me);
                        }
                    }
                }
            });
        }

        if (mSettings.showPropertiesMenuOption() && mSelectedEvents.size() > 0) {
            new MenuItem(mRightClickMenu, SWT.SEPARATOR);

            MenuItem properties = new MenuItem(mRightClickMenu, SWT.PUSH);
            properties.setText(mLanguageManager.getPropertiesMenuText());
            properties.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    if (mSelectedEvents.size() > 0) {
                        for (int i = 0; i < mEventListeners.size(); i++) {
                            IGanttEventListener listener = (IGanttEventListener) mEventListeners.get(i);
                            listener.eventPropertiesSelected(mSelectedEvents);
                        }
                    }
                }
            });
        }

        mRightClickMenu.setLocation(x, y);
        mRightClickMenu.setVisible(true);
    }

    /**
     * Returns the a list of all currently selected events, or an emtpy list if none.
     * 
     * @return GanttEvent or null
     */
    public List getSelectedEvents() {
        checkWidget();
        return mSelectedEvents;
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
        // jumpToEvent(ge, true, side);
        vScrollToY(ge.getY(), false);
        internalSetDate(ge.getActualStartDate(), side, true, false);
        redraw();
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
        mReCalculateScopes = true;
        mReCalculateSectionBounds = true;
        redraw();
    }

    // moves the chart to the given y position, takes various spacers into account
    private void vScrollToY(int y, boolean redraw) {
        if (mSettings.lockHeaderOnVerticalScroll()) {
            y -= getHeaderHeight();
        }
        else {
            y -= (mBounds.y - getHeaderHeight());
        }

        // we need to take the "previous" scroll location into account
        y += mVerticalScrollPosition;

        // y -= mLastVerticalScrollPosition;

        int max = mVerticalScrollBar.getMaximum() - mVerticalScrollBar.getThumb();

        if (y < 0) y = 0;
        if (y > max) y = max;

        mVerticalScrollPosition = y;
        mLastVerticalScrollPosition = mVerticalScrollPosition;
        mVerticalScrollBar.setSelection(y);

        // moveYBounds(y);

        mReCalculateSectionBounds = true;
        flagForceFullUpdate();

        if (redraw) redraw();
    }

    public Rectangle getBounds() {
        if (mBounds == null) return super.getBounds();

        return new Rectangle(0, 0, super.getBounds().width, mBottomMostY);
    }

    // calculates all gantt section bounds
    private void calculateSectionBounds(GC gc, Rectangle bounds) {
        int lineLoc = getHeaderHeight() == 0 ? bounds.y : (bounds.y + getHeaderHeight());
        int yStart = lineLoc;

        for (int i = 0; i < mGanttSections.size(); i++) {
            GanttSection gs = (GanttSection) mGanttSections.get(i);

            Point extent = null;
            if (gs.needsNameUpdate() || gs.getNameExtent() == null) {
                extent = gc.stringExtent(gs.getName());
                gs.setNameExtent(extent);
                gs.setNeedsNameUpdate(false);
            } else {
                extent = gs.getNameExtent();
            }

            int strLen = extent.x;
            int gsHeight = gs.getEventsHeight(mSettings);
            int height = Math.max(gsHeight, strLen);

            // if the name of the section is so large that it basically defines the size of the section, space out the text slightly
            if (strLen > gsHeight) {
                height += mSettings.getSectionTextSpacer();
            }

            Rectangle gsBounds = new Rectangle(bounds.x, yStart, bounds.width, height);
            gs.setBounds(gsBounds);

            yStart += height - 1;
            yStart += mSettings.getSectionBarDividerHeight();
        }
    }

    // calculates days visible
    private void calculateDaysVisible(Rectangle bounds) {
        Calendar temp = Calendar.getInstance(mDefaultLocale);
        temp.setTime(mCalendar.getTime());

        int current = 0;

        while (true) {
            temp.add(Calendar.DATE, 1);
            current += mDayWidth;

            mDaysVisible++;

            if (current > bounds.width) {
                break;
            }
        }
    }

    // the height of the header or zero if not visible
    private int getHeaderHeight() {
        if (!mSettings.drawHeader()) return 0;

        // we account for the drawTopLines bottom line which is at the bottom of the header, which increases our height by 1 pixel
        return mSettings.getHeaderDayHeight() + mSettings.getHeaderMonthHeight() + 1;
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
            offset = mVerticalScrollPosition;
            if (offset > getHeaderHeight()) offset = getHeaderHeight();
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
            switch (mCurrentView) {
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

        Calendar temp = Calendar.getInstance(mDefaultLocale);
        temp.setTime(mCalendar.getTime());

        // fill all of it first, then do weekends
        if (drawFills) {
            gc.setForeground(getDayBackgroundGradient(Calendar.MONDAY, true, gs));
            gc.setBackground(getDayBackgroundGradient(Calendar.MONDAY, false, gs));

            gc.fillGradientRectangle(startX, startY, maxX, heightY, true);
        }

        int dayWidth = (mCurrentView == ISettings.VIEW_WEEK || mCurrentView == ISettings.VIEW_D_DAY ? mDayWidth : mMonthDayWidth);

        while (true) {
            int day = temp.get(Calendar.DAY_OF_WEEK);

            if (drawFills) {
                if (mSelectedHeaderDates.contains(temp)) {
                    gc.setForeground(mColorManager.getSelectedDayColorTop());
                    gc.setBackground(mColorManager.getSelectedDayColorBottom());
                    gc.fillGradientRectangle(startX, startY, dayWidth, heightY, true);
                } else {
                    if ((day == Calendar.SATURDAY || day == Calendar.SUNDAY) && mCurrentView != ISettings.VIEW_D_DAY) {
                        gc.setForeground(getDayBackgroundGradient(day, true, gs));
                        gc.setBackground(getDayBackgroundGradient(day, false, gs));

                        // fill the whole thing all the way down
                        gc.fillGradientRectangle(startX, startY, dayWidth, heightY, true);
                    }

                    if (DateHelper.isToday(temp, mDefaultLocale)) {
                        gc.setForeground(mTodayBGColorTop);
                        gc.setBackground(mTodayBGColorBottom);
                        gc.fillGradientRectangle(startX + 1, startY, dayWidth - 1, heightY, true);
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

        int bottomLeftY = mBounds.height - helpHeight - 3;
        int bottomLeftX = mBounds.x + 3;

        // if there is no sections where we'd normally draw the zoom box, we can move it in to the left, otherwise we leave it as it should not cover the section bar
        if (mGanttSections.size() > 0 && mBottomMostY < bottomLeftY) bottomLeftX = 3;

        String qText = mLanguageManager.getZoomLevelText() + ": " + (mZoomLevel == ISettings.MIN_ZOOM_LEVEL ? mLanguageManager.getZoomMaxText() : (mZoomLevel == ISettings.MAX_ZOOM_LEVEL ? mLanguageManager.getZoomMinText() : "" + mZoomLevel));
        Point point = gc.stringExtent(qText);

        int helpWidth = point.x + 5;

        Rectangle outer = new Rectangle(bottomLeftX, bottomLeftY, helpWidth, helpHeight);
        Rectangle inner = new Rectangle(bottomLeftX + 1, bottomLeftY + 1, helpWidth - 1, helpHeight - 1);

        mZoomLevelHelpArea = outer;

        gc.setForeground(mColorManager.getZoomBackgroundColorTop());
        gc.setBackground(mColorManager.getZoomBackgroundColorBottom());
        gc.fillGradientRectangle(inner.x, inner.y, inner.width, inner.height, true);

        gc.setForeground(mColorManager.getZoomBorderColor());
        gc.drawRectangle(outer);

        gc.setForeground(mColorManager.getZoomTextColor());
        gc.drawString(qText, bottomLeftX + 4, bottomLeftY + 3, true);
    }

    // draws the section column on left or right side
    private void drawSectionColumn(GC gc, Rectangle bounds, boolean columnOnly, boolean forceUsageOfBounds, boolean drawCornerOnly) {
        if (mGanttSections.size() == 0) { return; }

        int xMax = mSettings.getSectionBarWidth() - 1;

        int horiSpacer = 3;

        // calculate max width if any section is horizontal
        for (int i = 0; i < mGanttSections.size(); i++) {
            GanttSection gs = (GanttSection) mGanttSections.get(i);
            if (gs.getTextOrientation() == SWT.HORIZONTAL) {
                Point p = null;
                if (gs.needsNameUpdate() || gs.getNameExtent() == null) {
                    p = gc.stringExtent(gs.getName());
                    gs.setNameExtent(p);
                    gs.setNeedsNameUpdate(false);
                } else p = gs.getNameExtent();

                xMax = Math.max(xMax, p.x + (horiSpacer * 2));
            }
        }

        int lineLoc = getHeaderHeight() == 0 ? bounds.y : (bounds.y + getHeaderHeight());
        int yStart = lineLoc;

        int x = 0;

        boolean rightSide = (mSettings.getSectionSide() == SWT.RIGHT);

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
            neg = -mSettings.getSectionBarWidth();
        }

        GanttSection bottomSection = (GanttSection) mGanttSections.get(mGanttSections.size() - 1);

        if (drawCornerOnly) {
            lineLoc += mVerticalScrollPosition;
        }

        // top corner
        gc.setForeground(mColorManager.getNonActiveSessionBarColorLeft());
        gc.setBackground(mColorManager.getNonActiveSessionBarColorRight());
        gc.fillGradientRectangle(x, 0, xMax + 1, mSettings.drawGanttSectionBarToBottom() ? mBounds.y + mBounds.height : lineLoc, false);

        gc.setForeground(mColorManager.getTopHorizontalLinesColor());
        // vertical
        gc.drawLine(x + xMax + neg, 0, x + xMax + neg, bottomSection.getBounds().y + bottomSection.getBounds().height - 1);

        if (!drawCornerOnly) {

            for (int i = 0; i < mGanttSections.size(); i++) {
                GanttSection gs = (GanttSection) mGanttSections.get(i);

                int gsHeight = gs.getBounds().height;

                gc.setForeground(mColorManager.getActiveSessionBarColorLeft());
                gc.setBackground(mColorManager.getActiveSessionBarColorRight());
                gc.fillGradientRectangle(x, yStart, xMax, gsHeight, false);

                gc.setForeground(mTextColor);

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
                            gcTemp.setForeground(mColorManager.getActiveSessionBarColorRight());
                            gcTemp.setBackground(mColorManager.getActiveSessionBarColorLeft());
                        } else {
                            gcTemp.setForeground(mColorManager.getActiveSessionBarColorLeft());
                            gcTemp.setBackground(mColorManager.getActiveSessionBarColorRight());
                        }
                        gcTemp.fillGradientRectangle(0, 0, extent.x, xMax - 2, true);
                        gcTemp.setForeground(mTextColor);
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
                if (rightSide) {
                    width = super.getClientArea().width;
                }

                // draw center divider
                if (!columnOnly) {
                    gc.setForeground(mColorManager.getTopHorizontalLinesColor());

                    if (i != mGanttSections.size() - 1 && mSettings.getSectionBarDividerHeight() != 0) {
                        gc.setForeground(mColorManager.getSessionBarDividerColorLeft());
                        gc.setBackground(mColorManager.getSessionBarDividerColorRight());
                        gc.fillGradientRectangle(0, yStart, width, mSettings.getSectionBarDividerHeight(), false);

                        gc.setForeground(mColorManager.getTopHorizontalLinesColor());
                        gc.drawLine(0, yStart, width, yStart);
                        yStart += mSettings.getSectionBarDividerHeight();
                        gc.drawLine(0, yStart - 1, width, yStart - 1);
                    } else {
                        // the last line
                        gc.drawLine(0, yStart, width, yStart);
                        yStart += mSettings.getSectionBarDividerHeight();
                    }
                }
            }

        }

        gc.setForeground(mColorManager.getTopHorizontalLinesColor());
        // horizontal
        if (mSettings.drawHeader()) {
            gc.drawLine(x, bounds.y, x + xMax, bounds.y);
        }

        gc.drawLine(x, lineLoc, x + xMax, lineLoc);

    }

    private void drawHorizontalLines(GC gc, Rectangle bounds) {
        gc.setForeground(mLineColor);

        List usedGroups = new ArrayList();

        for (int i = 0; i < mGanttEvents.size(); i++) {
            GanttEvent ge = (GanttEvent) mGanttEvents.get(i);
            // could be a feature, although a lame one, if this is enabled, only visible events horizontal lines will be drawn
            // if (!ge.getBounds().intersects(bounds))
            // continue;

            if (ge.isHidden()) {
                continue;
            }

            int yExtra = ge.isAutomaticRowHeight() ? 0 : ge.getFixedRowHeight();
            yExtra -= mVerticalScrollPosition;

            if (ge.getGanttGroup() != null) {
                if (usedGroups.contains(ge.getGanttGroup())) {
                    continue;
                } else {
                    usedGroups.add(ge.getGanttGroup());
                }
            }

            yExtra += (mEventSpacer / 2);

            // top line. we don't need to check for fixed row heights as it'll always be correct at the top
            gc.drawLine(bounds.x, ge.getHorizontalLineBottomY() + yExtra, bounds.width, ge.getHorizontalLineBottomY() + yExtra);

            // last event, draw bottom line as well
            if (i == mGanttEvents.size() - 1) {
                int y = ge.getHorizontalLineBottomY() - mEventSpacer;
                if (!ge.isAutomaticRowHeight()) {
                    y += ge.getFixedRowHeight();
                }
                gc.drawLine(bounds.x, y, bounds.width, y);
            }
        }
    }

    // draws vertical lines for separating days, hours, months, years etc
    private void drawVerticalLines(GC gc, Rectangle bounds, boolean applyVscroll) {
        // int xMax = bounds.width + bounds.x;
        // space it out 1 or more or else it will draw over the bottom horizontal line of the header
        int yStart = bounds.y - (applyVscroll ? mVerticalScrollPosition : 0);
        int height = bounds.height + yStart - 1 + (applyVscroll ? mVerticalScrollPosition : 0);

        if (mCurrentView == ISettings.VIEW_WEEK || mCurrentView == ISettings.VIEW_MONTH || mCurrentView == ISettings.VIEW_DAY || mCurrentView == ISettings.VIEW_D_DAY) {
            // normal day lines
            gc.setForeground(mLineColor);
            for (int i = 0; i < mVerticalLineLocations.size(); i++) {
                int current = ((Integer) mVerticalLineLocations.get(i)).intValue();

                gc.drawLine(current, yStart, current, height);
            }
            
            // "weekend" lines
            gc.setForeground(mLineWeekDividerColor);
            Object [] weekLocs = mVerticalWeekDividerLineLocations.toArray();

            if (mUseAlpha) {
                gc.setAlpha(mColorManager.getWeekDividerAlpha());
            }
            for (int i = 0; i < weekLocs.length; i++) {
                int current = ((Integer)weekLocs[i]).intValue();
                gc.drawLine(current, yStart, current, height);
            }
            if (mUseAlpha) {
                gc.setAlpha(255);
                gc.setAdvanced(false);
            }                

            Calendar today = Calendar.getInstance(mDefaultLocale);
            drawTodayLine(gc, bounds, getStartingXfor(today), today.get(Calendar.DAY_OF_WEEK));
        } else if (mCurrentView == ISettings.VIEW_YEAR) {
            for (int i = 0; i < mVerticalLineLocations.size(); i++) {
                gc.setForeground(mLineWeekDividerColor);

                int x = ((Integer) mVerticalLineLocations.get(i)).intValue();
                gc.drawLine(x, yStart, x, height);
            }

            Calendar today = Calendar.getInstance(mDefaultLocale);
            drawTodayLine(gc, bounds, getStartingXfor(today), today.get(Calendar.DAY_OF_WEEK));
        }
    }

    // year
    private void drawYearBottomBoxes(GC gc, Rectangle bounds) {
        int xMax = bounds.width + bounds.x;
        int current = bounds.x;
        int topY = bounds.y + mSettings.getHeaderMonthHeight();
        int heightY = mSettings.getHeaderDayHeight();
        mDaysVisible = 0;

        Calendar temp = Calendar.getInstance(mDefaultLocale);
        temp.setTime(mCalendar.getTime());

        temp.set(Calendar.DAY_OF_MONTH, 1);

        gc.setForeground(mTextHeaderBackgroundColorTop);
        gc.setBackground(mTextHeaderBackgroundColorBottom);
        gc.fillGradientRectangle(0, topY, xMax, heightY, true);

        gc.setBackground(mWeekdayBackgroundColorTop);

        while (true) {
            if (temp.get(Calendar.DATE) == 1) {
                gc.setForeground(mColorManager.getYearTimeDividerColor());
                gc.drawLine(current, topY, current, topY + heightY);
                mVerticalLineLocations.add(new Integer(current));

                gc.setForeground(mTextColor);
                gc.drawString(getDateString(temp, false), current + 4, topY + 3, true);

            }

            int monthMax = temp.getActualMaximum(Calendar.DATE);
            mDaysVisible += monthMax;
            temp.add(Calendar.MONTH, 1);
            current += (mYearDayWidth * monthMax);

            if (current > xMax) {
                mEndCalendar = temp;
                break;
            }
        }
    }

    // year
    private void drawYearTopBoxes(GC gc, Rectangle bounds) {
        int xMax = bounds.width + bounds.x;
        int current = bounds.x;
        int topY = bounds.y;
        int bottomY = mSettings.getHeaderMonthHeight();

        Calendar temp = Calendar.getInstance(mDefaultLocale);
        temp.setTime(mCalendar.getTime());

        temp.set(Calendar.DAY_OF_MONTH, 1);

        gc.setForeground(mTextHeaderBackgroundColorTop);
        gc.setBackground(mTextHeaderBackgroundColorBottom);
        gc.fillGradientRectangle(current, topY + 1, xMax, bottomY - 1, true);

        int lastYear = -1;
        while (true) {
            // draw month at beginning of month
            if (temp.get(Calendar.YEAR) != lastYear) {
                gc.setForeground(mColorManager.getTickMarkColor());
                gc.drawLine(current, topY + mSettings.getVerticalTickMarkOffset(), current, topY + mSettings.getHeaderDayHeight());
                gc.setForeground(mTextColor);
                gc.drawString(getDateString(temp, true), current + 4, topY + 3, true);
            }

            lastYear = temp.get(Calendar.YEAR);

            int monthMax = temp.getActualMaximum(Calendar.DAY_OF_MONTH);
            mDaysVisible += monthMax;
            temp.add(Calendar.DATE, monthMax);
            current += (mYearDayWidth * monthMax);

            if (current > xMax) {
                break;
            }
        }
    }

    // month
    private void drawMonthBottomBoxes(GC gc, Rectangle bounds) {
        int xMax = bounds.width + bounds.x;
        int current = bounds.x;
        int topY = bounds.y + mSettings.getHeaderMonthHeight();
        int heightY = mSettings.getHeaderDayHeight();

        // get the offset to draw things at
        Calendar temp = Calendar.getInstance(mDefaultLocale);
        temp.setTime(mCalendar.getTime());
        Calendar temp2 = Calendar.getInstance(mDefaultLocale);
        temp2.setTime(mCalendar.getTime());
        temp2.set(Calendar.DAY_OF_WEEK, temp.getFirstDayOfWeek());
        int days = (int) DateHelper.daysBetween(temp, temp2, mDefaultLocale);
        current += days * mMonthDayWidth;

        temp.set(Calendar.DAY_OF_WEEK, temp.getFirstDayOfWeek());

        while (true) {
            int curDay = temp.get(Calendar.DAY_OF_WEEK);

            mVerticalLineLocations.add(new Integer(current));
            // only change dates when week changes, as we don't change date
            // string for every different day
            if (curDay == temp.getFirstDayOfWeek()) {
                mVerticalWeekDividerLineLocations.add(new Integer(current));
                gc.setForeground(mColorManager.getMonthTimeDividerColor());
                gc.drawRectangle(current, topY, mMonthWeekWidth, heightY);
                gc.setForeground(mTextHeaderBackgroundColorTop);
                gc.setBackground(mTextHeaderBackgroundColorBottom);
                gc.fillGradientRectangle(current + 1, topY + 1, mMonthWeekWidth - 1, heightY - 1, true);
                gc.setForeground(mTextColor);

                gc.drawString(getDateString(temp, false), current + 4, topY + 3, true);
            }

            temp.add(Calendar.DATE, 1);

            current += mMonthDayWidth;

            if (current > xMax) {
                mEndCalendar = temp;
                break;
            }
        }
    }

    // month
    private void drawMonthTopBoxes(GC gc, Rectangle bounds) {
        int xMax = bounds.width + bounds.x;
        int current = bounds.x;
        int topY = bounds.y;
        int bottomY = mSettings.getHeaderMonthHeight();
        mDaysVisible = 0;

        gc.setForeground(mTextHeaderBackgroundColorTop);
        gc.setBackground(mTextHeaderBackgroundColorBottom);
        gc.fillGradientRectangle(current, topY + 1, xMax, bottomY - 1, true);

        // get the offset to draw things at
        Calendar temp = Calendar.getInstance(mDefaultLocale);
        temp.setTime(mCalendar.getTime());
        Calendar temp2 = Calendar.getInstance(mDefaultLocale);
        temp2.setTime(mCalendar.getTime());
        temp2.set(Calendar.DAY_OF_WEEK, temp.getFirstDayOfWeek());
        int days = (int) DateHelper.daysBetween(temp, temp2, mDefaultLocale);
        current += days * mMonthDayWidth;

        // move us to sunday, as the date shouldn't change when scrolling
        temp.set(Calendar.DAY_OF_WEEK, temp.getFirstDayOfWeek());

        while (true) {
            // draw month at beginning of month
            if (temp.get(Calendar.DAY_OF_MONTH) == 1) {
                gc.setForeground(mColorManager.getTickMarkColor());
                gc.drawLine(current, topY + mSettings.getVerticalTickMarkOffset(), current, topY + mSettings.getHeaderDayHeight());
                gc.setForeground(mTextColor);
                gc.drawString(getDateString(temp, true), current + 4, topY + 3, true);
            }

            temp.add(Calendar.DATE, 1);
            mDaysVisible++;

            current += mMonthDayWidth;

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
        int bottomY = mSettings.getHeaderMonthHeight();

        Calendar temp = Calendar.getInstance(mDefaultLocale);
        temp.setTime(mCalendar.getTime());

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

        int toTakeOff = (dayOffset * mDayWidth);

        current += toTakeOff;

        // move us to the first day of the week, as the date shouldn't change
        // when scrolling
        temp.set(Calendar.DAY_OF_WEEK, temp.getFirstDayOfWeek());

        while (true) {
            gc.setForeground(mLineColor);
            gc.drawRectangle(current, topY, mWeekWidth, bottomY);

            gc.setForeground(mTextHeaderBackgroundColorTop);
            gc.setBackground(mTextHeaderBackgroundColorBottom);
            gc.fillGradientRectangle(current < bounds.x ? bounds.x : current, topY + 1, mWeekWidth, bottomY - 1, true);

            gc.setForeground(mColorManager.getTickMarkColor());
            gc.drawLine(current, topY + mSettings.getVerticalTickMarkOffset(), current, topY + mSettings.getHeaderDayHeight());

            gc.setForeground(mTextColor);
            gc.drawString(getDateString(temp, true), current + 4, topY + 3, true);

            // only change dates on the first day of the week, as we don't
            // change date string for every different day
            if (temp.get(Calendar.DAY_OF_WEEK) == temp.getFirstDayOfWeek()) temp.add(Calendar.DATE, 7);

            current += mWeekWidth;

            if (current > xMax) {
                break;
            }
        }
    }

    // days
    private void drawWeekBottomBoxes(GC gc, Rectangle bounds) {
        int xMax = bounds.width + bounds.x;
        int current = bounds.x;
        int topY = bounds.y + mSettings.getHeaderMonthHeight();
        int heightY = mSettings.getHeaderDayHeight();

        int day = mCalendar.get(Calendar.DAY_OF_WEEK);
        mDaysVisible = 0;

        Calendar temp = Calendar.getInstance(mDefaultLocale);
        temp.setTime(mCalendar.getTime());

        while (true) {
            if (mSelectedHeaderDates.contains(temp)) {
                gc.setForeground(mColorManager.getSelectedDayHeaderColorTop());
                gc.setBackground(mColorManager.getSelectedDayHeaderColorBottom());
            } else {
                gc.setForeground(mTimeHeaderBackgroundColorTop);
                gc.setBackground(mTimeHeaderBackgroundColorBottom);
            }
            gc.fillGradientRectangle(current + 1, topY + 1, mDayWidth - 1, heightY - 1, true);

            mVerticalLineLocations.add(new Integer(current));
            if (temp.get(Calendar.DAY_OF_WEEK) == mCalendar.getFirstDayOfWeek()) {
                mVerticalWeekDividerLineLocations.add(new Integer(current));
            }

            gc.setForeground(mColorManager.getWeekTimeDividerColor());
            gc.drawRectangle(current, topY, mDayWidth, heightY);

            int hSpacer = mSettings.getDayHorizontalSpacing();
            int vSpacer = mSettings.getDayVerticalSpacing();

            String dayLetter = getDateString(temp, false);

            if (mSettings.adjustForLetters()) {
                Point extent = null;
                if (!mDayLetterStringExtentMap.containsKey(dayLetter)) {
                    extent = gc.stringExtent(dayLetter);
                    mDayLetterStringExtentMap.put(dayLetter, extent);
                } else extent = (Point) mDayLetterStringExtentMap.get(dayLetter);

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
            gc.setForeground(mLineColor);

            current += mDayWidth;

            day++;
            if (day > 7) day = 1;

            mDaysVisible++;

            if (current > xMax) {
                mEndCalendar = temp;
                break;
            }
        }
    }

    // d-days
    private void drawDDayTopBoxes(GC gc, Rectangle bounds) {
        int xMax = bounds.width + bounds.x;
        int current = bounds.x;
        int topY = bounds.y;
        int bottomY = mSettings.getHeaderMonthHeight();
        int splitEvery = mSettings.getDDaySplitCount();

        Calendar temp = Calendar.getInstance(mDefaultLocale);
        temp.setTime(mCalendar.getTime());
        int startX = getXForDate(mDDayCalendar);
        int daysFromStartOffset = startX / mDayWidth;
        int dayOffset = 0;
        int letter = 0;
        
        // find the start date by splitting it down and checking offsets of where we are
        float count = (float) daysFromStartOffset / (float) splitEvery;
        count = (float) Math.floor(count);
        int takeOff = (int) Math.ceil(count * splitEvery);
        dayOffset = daysFromStartOffset - takeOff;
        letter = -takeOff;

        // set current to the location of where "0" starts for each set
        current += (dayOffset * mDayWidth);
        int dDayWeekWidth = mDayWidth * splitEvery;

        // if we're to the right of 0 we need to start drawing one "set" of days prior to the left or we'll have a blak space
        if (current > 0) {
            current -= dDayWeekWidth;
            letter -= splitEvery;
        }
        
        while (true) {
            gc.setForeground(mLineColor);
            gc.drawRectangle(current, topY, dDayWeekWidth, bottomY);

            gc.setForeground(mTextHeaderBackgroundColorTop);
            gc.setBackground(mTextHeaderBackgroundColorBottom);
            gc.fillGradientRectangle(current < bounds.x ? bounds.x : current, topY + 1, dDayWeekWidth, bottomY - 1, true);

            gc.setForeground(mColorManager.getTickMarkColor());
            // draws the little line that starts a group i.e a week or in this case every ten days
            gc.drawLine(current, topY + mSettings.getVerticalTickMarkOffset(), current, topY + mSettings.getHeaderDayHeight());

            gc.setForeground(mTextColor);
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
        
        int topY = bounds.y + mSettings.getHeaderMonthHeight();
        int heightY = mSettings.getHeaderDayHeight();

        int startX = getXForDate(mDDayCalendar);
        int daysFromStartOffset = startX / mDayWidth;
        int dayOffset = 0;
        int splitEvery = mSettings.getDDaySplitCount();
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
                
        mDaysVisible = 0;

        Calendar temp = Calendar.getInstance(mDefaultLocale);
        temp.setTime(mCalendar.getTime());

        while (true) {
            if (mSelectedHeaderDates.contains(temp)) {
                gc.setForeground(mColorManager.getSelectedDayHeaderColorTop());
                gc.setBackground(mColorManager.getSelectedDayHeaderColorBottom());
            } else {
                gc.setForeground(mTimeHeaderBackgroundColorTop);
                gc.setBackground(mTimeHeaderBackgroundColorBottom);
            }
            gc.fillGradientRectangle(current + 1, topY + 1, mDayWidth - 1, heightY - 1, true);

            mVerticalLineLocations.add(new Integer(current));

            gc.setForeground(mColorManager.getWeekTimeDividerColor());
            gc.drawRectangle(current, topY, mDayWidth, heightY);

            int hSpacer = mSettings.getDayHorizontalSpacing();
            int vSpacer = mSettings.getDayVerticalSpacing();

            String dayLetter = "" + letter;

            if (mSettings.adjustForLetters()) {
                Point extent = null;
                if (!mDayLetterStringExtentMap.containsKey(dayLetter)) {
                    extent = gc.stringExtent(dayLetter);
                    mDayLetterStringExtentMap.put(dayLetter, extent);
                } else extent = (Point) mDayLetterStringExtentMap.get(dayLetter);

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
            gc.setForeground(mLineColor);

            current += mDayWidth;

            mDaysVisible++;

            letter++;
            if (letter % splitEvery == 0) {
                mVerticalWeekDividerLineLocations.add(new Integer(current));
                letter = 0;
            }

            if (current > xMax) {
                mEndCalendar = temp;
                break;
            }
        }
    }

    // days (hours)
    private void drawHourTopBoxes(GC gc, Rectangle bounds) {
        int xMax = bounds.width + bounds.x;
        int current = bounds.x;
        int topY = bounds.y;
        int bottomY = mSettings.getHeaderMonthHeight();

        Calendar temp = Calendar.getInstance(mDefaultLocale);
        temp.setTime(mCalendar.getTime());

        // if we're not on hour zero, the box will be shorter, so check how much
        int hour = temp.get(Calendar.HOUR_OF_DAY);

        int toTakeOff = hour * mDayWidth;

        int wWidth = mWeekWidth;
        current -= toTakeOff;

        boolean once = false;

        while (true) {
            gc.setForeground(mTextHeaderBackgroundColorTop);
            gc.setBackground(mTextHeaderBackgroundColorBottom);
            gc.fillGradientRectangle(current, topY + 1, wWidth, bottomY - 1, true);

            gc.setForeground(mColorManager.getTickMarkColor());
            gc.drawLine(current, topY + mSettings.getVerticalTickMarkOffset(), current, topY + mSettings.getHeaderDayHeight());

            gc.setForeground(mTextColor);

            String dString = getDateString(temp, true);

            // TODO: This needs to be a different fetch for dates that are not
            // right now, so to speak.
            // so if the leftmost date is a full date/time, the "next" date
            // needs to be without the time
            // should make it a method call
            if (current >= bounds.x) gc.drawString(dString, current + 4, topY + 3, true);

            // as ranges here can be massive width-wise, always ensure there's a
            // date visible at the top
            if (!once && mCalendar.get(Calendar.HOUR_OF_DAY) != 0) {
                gc.drawString(dString, bounds.x + 4, topY + 3, true);
                once = true;
            }

            temp.add(Calendar.DATE, 1);
            current += mWeekWidth;

            if (current > xMax) {
                break;
            }
        }
    }

    // days
    private void drawHourBottomBoxes(GC gc, Rectangle bounds) {
        int xMax = bounds.width + bounds.x;
        int current = bounds.x;
        int topY = bounds.y + mSettings.getHeaderMonthHeight();
        int heightY = mSettings.getHeaderDayHeight();

        Calendar temp = Calendar.getInstance(mDefaultLocale);
        temp.setTime(mCalendar.getTime());

        boolean first = true;

        mHoursVisible = 0;

        while (true) {
            gc.setForeground(mColorManager.getHourTimeDividerColor());
            int spacer = 1;

            mVerticalLineLocations.add(new Integer(current));

            // this weird code here checks if it's a gantt section on the left and we're drawing the first iteration.
            // as sections draw their own line on the inner-facing border, and so does this, that'd be two lines in a row
            // which makes the section at the top look rather "thick". As we don't want that, we do a quick check and skip the drawing.
            // the "spacer" does the same thing for the gradient below
            if (mGanttSections.size() > 0 && first) {
                gc.drawRectangle(current - 1, topY, mDayWidth + 1, heightY);
                spacer = 0;
            } else gc.drawRectangle(current, topY, mDayWidth, heightY);

            gc.setForeground(mTimeHeaderBackgroundColorTop);
            gc.setBackground(mTimeHeaderBackgroundColorBottom);
            gc.fillGradientRectangle(current + spacer, topY + 1, mDayWidth - spacer, heightY - 1, true);

            int hSpacer = mSettings.getDayHorizontalSpacing();
            int vSpacer = mSettings.getDayVerticalSpacing();

            gc.setForeground(mTextColor);
            gc.drawString(getDateString(temp, false), current + hSpacer, topY + vSpacer, true);

            // fixes some odd red line bug, not sure why
            // gc.setForeground(mLineColor);

            current += mDayWidth;

            if (current > xMax) {
                current -= mDayWidth;
                mEndCalendar = temp;
                break;
            }

            mHoursVisible++;
            temp.add(Calendar.HOUR_OF_DAY, 1);
            if (temp.get(Calendar.HOUR_OF_DAY) == 0) {
                mVerticalWeekDividerLineLocations.add(new Integer(current));
                // temp.add(Calendar.DATE, 1);
                // temp.set(Calendar.HOUR_OF_DAY, workDayStartHour);
            }

            first = false;
        }

        // calculate the exact end time down to the minute

        // hours in the day
        int hr = (mBounds.width - mBounds.x) / mDayWidth;

        // hour pixels visible
        int total = hr * mDayWidth;
        float ppm = 60f / mDayWidth;

        // whatever pixels don't account for a full hour that are left over
        int extra = mBounds.width - mBounds.x - total;

        // total visible minutes
        int totalMinutes = (int) (((float) total + (float) extra) * ppm);

        // set our temporary end calendar to the start date of the calendar
        Calendar fakeEnd = Calendar.getInstance(mDefaultLocale);
        fakeEnd.setTime(mCalendar.getTime());

        // loop it, but take work days/hours into account as usual
        for (int i = 0; i < totalMinutes; i++) {
            fakeEnd.add(Calendar.MINUTE, 1);
            if (temp.get(Calendar.HOUR_OF_DAY) >= 24) {
                temp.add(Calendar.DATE, 1);
                temp.set(Calendar.HOUR_OF_DAY, 0);
            }
        }

        // what we're left with is the exact end date down to the minute
        mEndCalendar = fakeEnd;
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
                    if (gs == null) return mSaturdayBackgroundColorTop;
                    else return gs.getSaturdayBackgroundColorTop();
                } else {
                    if (gs == null) return mSaturdayBackgroundColorBottom;
                    else return gs.getSaturdayBackgroundColorBottom();
                }
            case Calendar.SUNDAY:
                if (top) {
                    if (gs == null) return mSundayBackgroundColorTop;
                    else return gs.getSundayBackgroundColorTop();
                } else {
                    if (gs == null) return mSundayBackgroundColorBottom;
                    else return gs.getSundayBackgroundColorBottom();
                }
            default:
                if (top) {
                    if (gs == null) return mWeekdayBackgroundColorTop;
                    else return gs.getWeekdayBackgroundColorTop();
                } else {
                    if (gs == null) return mWeekdayBackgroundColorBottom;
                    else return gs.getWeekdayBackgroundColorBottom();
                }
        }
    }

    private Color getDayTextColor(int day) {
        switch (day) {
            case Calendar.SATURDAY:
                return mSaturdayTextColor;
            case Calendar.SUNDAY:
                return mSundayTextColor;
            default:
                return mWeekdayTextColor;
        }
    }

    private void drawEvents(GC gc, Rectangle bounds, GanttSection gs) {
        internalDrawEvents(gc, bounds, gs);
    }

    private void drawEvents(GC gc, Rectangle bounds) {
        internalDrawEvents(gc, bounds, null);
    }

    private void internalDrawEvents(GC gc, Rectangle bounds, GanttSection gs) {
        if (mGanttEvents.size() == 0) return;

        HashSet eventsAlreadyDrawn = new HashSet();

        List events = mGanttEvents;
        if (gs != null) events = gs.getEvents();

        List correctOrder = new ArrayList();
        for (int i = 0; i < events.size(); i++) {
            IGanttChartItem event = (IGanttChartItem) events.get(i);
            if (event instanceof GanttGroup) {
                GanttGroup gg = (GanttGroup) event;
                ArrayList children = gg.getEventMembers();
                correctOrder.addAll(children);
                continue;
            } else correctOrder.add(event);
        }

        for (int i = 0; i < correctOrder.size(); i++) {
            GanttEvent ge = (GanttEvent) correctOrder.get(i);

            if (ge.isHidden()) continue;

            if (eventsAlreadyDrawn.contains(ge)) continue;

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

        if (cEvent == null) cEvent = mSettings.getDefaultEventColor();
        if (gradient == null) gradient = mSettings.getDefaultGradientEventColor();

        int yDrawPos = ge.getY();

        int dw = getDayWidth();

        boolean advanced = false;
        if (getLayerOpacity(ge.getLayer()) != -1) {
            advanced = true;
            gc.setAlpha(getLayerOpacity(ge.getLayer()));
        }

        if (ge.isCheckpoint()) {
            mPaintManager.drawCheckpoint(this, mSettings, mColorManager, ge, gc, mThreeDee, dw, xStart, yDrawPos, bounds);
        } else if (ge.isImage()) {
            mPaintManager.drawImage(this, mSettings, mColorManager, ge, gc, ge.getPicture(), mThreeDee, dw, xStart, yDrawPos, bounds);
        } else if (ge.isScope()) {
            mPaintManager.drawScope(this, mSettings, mColorManager, ge, gc, mThreeDee, dw, xStart, yDrawPos, xEventWidth, bounds);
        } else {
            mPaintManager.drawEvent(this, mSettings, mColorManager, ge, gc, (mSelectedEvents.size() > 0 && mSelectedEvents.contains(ge)), mThreeDee, dw, xStart, yDrawPos, xEventWidth, bounds);
        }
        if (ge.hasMovementConstraints()) {
            int startStart = -1, endStart = -1;
            if (ge.getNoMoveBeforeDate() != null) startStart = getStartingXfor(ge.getNoMoveBeforeDate());
            if (ge.getNoMoveAfterDate() != null) endStart = getXForDate(ge.getNoMoveAfterDate());

            mPaintManager.drawLockedDateRangeMarker(this, mSettings, mColorManager, ge, gc, mThreeDee, dw, yDrawPos, startStart, endStart, bounds);
        }

        // draws |---------| lines to show revised dates, if any
        if (mShowPlannedDates) mPaintManager.drawPlannedDates(this, mSettings, mColorManager, ge, gc, mThreeDee, xStart, yDrawPos, xEventWidth, bounds);

        // draw a little plaque saying how many days that this event is long
        if (mShowNumberOfDaysOnChart) {
            long days = DateHelper.daysBetween(ge.getActualStartDate(), ge.getActualEndDate(), mDefaultLocale) + 1;
            mPaintManager.drawDaysOnChart(this, mSettings, mColorManager, ge, gc, mThreeDee, xStart, yDrawPos, xEventWidth, (int) days, bounds);
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
        if (ge.getParsedString() != null) mPaintManager.drawEventString(this, mSettings, mColorManager, ge, gc, ge.getParsedString(), mThreeDee, xStart, yDrawPos, xEventWidth, bounds);

        // reset font
        gc.setFont(oldFont);

        if (advanced) gc.setAdvanced(false);
    }

    // updates all event visibilities, the bounds is the currently visible bounds, not the bounds that should be calculated
    private void updateEventVisibilities(Rectangle bounds) {
        Object[] all = mAllEventsCombined.toArray();
        for (int i = 0; i < all.length; i++) {
            GanttEvent ge = (GanttEvent) all[i];
            ge.setVisibility(getEventVisibility(ge, bounds));
        }
    }

    private void calculateAllScopes(Rectangle bounds, GanttSection gs) {
        if (mGanttEvents.size() == 0) return;

        int yStart = bounds.y + mSettings.getEventsTopSpacer();// - mVerticalScrollPosition;

        HashSet allEventsInGroups = new HashSet();
        for (int i = 0; i < mGanttGroups.size(); i++) {
            allEventsInGroups.addAll(((GanttGroup) mGanttGroups.get(i)).getEventMembers());
        }

        boolean lastLoopWasGroup = false;
        //GanttGroup lastGroup = null;
        Map groupLocations = new HashMap();
        
        List events = mGanttEvents;
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
            if (mFixedRowHeight != 0) {
                ge.setFixedRowHeight(mFixedRowHeight);
                if (ge.getGanttGroup() != null) {
                    ge.getGanttGroup().setFixedRowHeight(mFixedRowHeight);
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
                        yStart += mEventHeight + mEventSpacer;
                    }
                    groupLocations.put(ge.getGanttGroup(), new Integer(yStart));
                }
            }

            // event just after a group
            if (lastLoopWasGroup && !groupedEvent) {
                yStart += mEventHeight + mEventSpacer;
            }

            // position event will be drawn at vertically
            int yDrawPos = yStart;

            // if it's a grouped event, get the location from our map to where it's drawn
            if (groupedEvent && groupLocations.containsKey(ge.getGanttGroup())) {
                yDrawPos = ((Integer) groupLocations.get(ge.getGanttGroup())).intValue();
            }

            int fixedRowHeight = mFixedRowHeight;
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

            ge.setHorizontalLineTopY(yStart);

            if (fixedHeight) {
                yStart += fixedRowHeight;

                int extra = 0;

                switch (verticalAlignment) {
                    case SWT.BOTTOM:
                        extra = fixedRowHeight - mEventHeight;
                        break;
                    case SWT.CENTER:
                        extra = ((fixedRowHeight / 2) - (mEventHeight / 2));
                        break;
                    case SWT.NONE:
                    case SWT.TOP:
                        extra = mEventSpacer - mEventHeight;
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
                ge.setHorizontalLineBottomY(yDrawPos + mEventHeight);
            } else {
                ge.setHorizontalLineBottomY(yDrawPos - mEventHeight);
            }

            // set event bounds
            Rectangle eventBounds = new Rectangle(xStart, yDrawPos, xEventWidth, mEventHeight);
            ge.setBounds(eventBounds.x, eventBounds.y, eventBounds.width, eventBounds.height);

            if (!groupedEvent) {
                // space them out
                if (!fixedHeight) {
                    yStart += mEventHeight + mEventSpacer;
                    mBottomMostY = yStart + mEventHeight;
                }
                lastLoopWasGroup = false;
            } else {
                lastLoopWasGroup = true;
            }

            mBottomMostY = Math.max(mBottomMostY, yStart + mEventHeight);
        }

        // take off the last iteration, easier here than an if check for each iteration
        mBottomMostY -= mEventSpacer;
    }

    // string processing for display text beyond event
    private String getStringForEvent(GanttEvent ge) {
        String toUse = ge.getTextDisplayFormat();
        if (toUse == null) toUse = mSettings.getTextDisplayFormat();
        if (toUse == null) return "";

        String dateFormat = (mCurrentView == ISettings.VIEW_DAY ? mSettings.getHourDateFormat() : mSettings.getDateFormat());

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
            if (ge.getStartDate() != null) toReturn = toReturn.replaceAll("#sd#", "" + DateHelper.getDate(ge.getStartDate(), dateFormat, mDefaultLocale));
            else toReturn = toReturn.replaceAll("#sd#", "");
        }

        if (toReturn.indexOf("#ed#") > -1) {
            if (ge.getEndDate() != null) toReturn = toReturn.replaceAll("#ed#", "" + DateHelper.getDate(ge.getEndDate(), dateFormat, mDefaultLocale));
            else toReturn = toReturn.replaceAll("#ed", "");
        }

        if (toReturn.indexOf("#rs#") > -1) {
            if (ge.getRevisedStart() != null) toReturn = toReturn.replaceAll("#rs#", "" + DateHelper.getDate(ge.getRevisedStart(), dateFormat, mDefaultLocale));
            else toReturn = toReturn.replaceAll("#rs#", "");
        }

        if (toReturn.indexOf("#re#") > -1) {
            if (ge.getRevisedEnd() != null) toReturn = toReturn.replaceAll("#re#", "" + DateHelper.getDate(ge.getRevisedEnd(), dateFormat, mDefaultLocale));
            else toReturn = toReturn.replaceAll("#re#", "");
        }

        if (toReturn.indexOf("#days#") > 1) {
            if (ge.getStartDate() != null && ge.getEndDate() != null) {
                long days = DateHelper.daysBetween(ge.getStartDate(), ge.getEndDate(), mDefaultLocale);
                toReturn = toReturn.replaceAll("#days#", "" + days);
            } else toReturn = toReturn.replaceAll("#days#", "");
        }

        if (toReturn.indexOf("#reviseddays#") > -1) {
            if (ge.getRevisedStart() != null && ge.getRevisedEnd() != null) {
                long days = DateHelper.daysBetween(ge.getRevisedStart(), ge.getRevisedEnd(), mDefaultLocale);
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

        int monthHeight = bounds.y + mSettings.getHeaderMonthHeight();

        gc.setForeground(mColorManager.getTopHorizontalLinesColor());

        gc.drawLine(xStart, yStart, width, yStart);
        gc.drawLine(xStart, monthHeight, width, monthHeight);
        gc.drawLine(xStart, monthHeight + mSettings.getHeaderDayHeight(), width, monthHeight + mSettings.getHeaderDayHeight());
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
        if (!mGanttConnections.contains(con)) mGanttConnections.add(con);
    }

    // connection is added from a GanttConnection class
    void connectionAdded(GanttConnection conn) {
        checkWidget();

        addDependency(conn.getSource(), conn.getTarget(), conn.getColor());
    }

    void connectionRemoved(GanttConnection conn) {
        checkWidget();

        mGanttConnections.remove(conn);
    }

    /**
     * Returns true if the given event is connected to another.
     * 
     * @param ge GanttEvent to check
     * @return true if the GanttEvent is connected
     */
    public boolean isConnected(GanttEvent ge) {
        for (int i = 0; i < mGanttConnections.size(); i++) {
            GanttConnection gc = (GanttConnection) mGanttConnections.get(i);
            if (gc.getSource().equals(ge) || gc.getTarget().equals(ge)) return true;
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
        for (int i = 0; i < mGanttConnections.size(); i++) {
            GanttConnection gc = (GanttConnection) mGanttConnections.get(i);
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
        int yStart = ge1.getY() + (mEventHeight / 2);

        return new Rectangle(xStart, yStart, 4, mEventHeight);
    }

    // draws the lines and arrows between events
    private void drawConnections(GC gc, Rectangle bounds) {

        int dw = getDayWidth();

        for (int i = 0; i < mGanttConnections.size(); i++) {
            GanttConnection connection = (GanttConnection) mGanttConnections.get(i);

            GanttEvent ge1 = connection.getSource();
            GanttEvent ge2 = connection.getTarget();

            // use connection color if set, otherwise use arrow color
            gc.setForeground(connection.getColor() == null ? mArrowColor : connection.getColor());

            if (ge1 == null || ge2 == null) continue;

            if (ge1.equals(ge2)) continue;

            if (ge1.getX() == 0 && ge2.getY() == 0) continue;

            // don't draw hidden events, nor connections to them
            if (ge1.isHidden() || ge2.isHidden()) continue;

            // same deal but with hidden layers
            if (mHiddenLayers.size() > 0) {
                if (mHiddenLayers.contains(ge1.getLayerInt()) || mHiddenLayers.contains(ge2.getLayerInt())) continue;
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
            if (mSettings.consumeEventWhenOutOfRange()) {
                if (ge1.getVisibility() == VISIBILITY_NOT_VISIBLE && ge2.getVisibility() == VISIBILITY_NOT_VISIBLE) continue;
            }

            if (mSettings.showOnlyDependenciesForSelectedItems()) {
                if (mSelectedEvents.size() == 0) { return; }

                if (!mSelectedEvents.contains(connection.getSource())) {
                    continue;
                }
            }

            if (mSettings.getArrowConnectionType() != ISettings.CONNECTION_MS_PROJECT_STYLE) {
                if (mSettings.getArrowConnectionType() == ISettings.CONNECTION_BIRDS_FLIGHT_PATH) {
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
                Rectangle down = new Rectangle(rect.x + rect.width, rect.y, rect.width, (rect.height / 2) + (mEventSpacer / 2));
                gc.drawLine(down.x, down.y, rect.x + down.width, rect.y + down.height);

                // get the top left corner of the target area, then draw a line
                // out to it, only along the x axis
                Rectangle rGe2 = new Rectangle(ge2.getX() - mSettings.getArrowHeadEventSpacer(), ge2.getY() + mSettings.getArrowHeadVerticalAdjuster(), ge2.getWidth(), ge2.getHeight());

                boolean goingUp = false;
                boolean goingLeft = false;
                if (rect.y > rGe2.y) goingUp = true;

                if (rect.x > rGe2.x) goingLeft = true;

                if (mSettings.getArrowConnectionType() == ISettings.CONNECTION_ARROW_RIGHT_TO_TOP) {

                    // draw the line
                    gc.drawLine(down.x, rect.y + down.height, rGe2.x, rect.y + down.height);

                    // draw the last snippet
                    if (goingLeft) {
                        if (goingUp) {
                            gc.drawLine(rGe2.x, rect.y + down.height, rGe2.x, rGe2.y + mEventHeight + 1);
                        } else {
                            gc.drawLine(rGe2.x, rect.y + down.height, rGe2.x, rGe2.y);
                        }
                    } else {
                        gc.drawLine(rGe2.x, rect.y + down.height, rGe2.x, rGe2.y);
                    }

                    if (mSettings.showArrows()) {
                        if (goingUp) {
                            mPaintManager.drawArrowHead(rGe2.x, rGe2.y + mEventHeight / 2 + 4, SWT.UP, gc);
                        } else {
                            mPaintManager.drawArrowHead(rGe2.x, rGe2.y - mEventHeight / 2 - 1, SWT.DOWN, gc);
                        }
                    }

                } else if (mSettings.getArrowConnectionType() == ISettings.CONNECTION_ARROW_RIGHT_TO_LEFT) {
                    int offset = 10;

                    // first of all, draw a bit further of the line we just
                    // created
                    gc.drawLine(down.x, rect.y + down.height, rGe2.x - offset, rect.y + down.height);

                    gc.drawLine(rGe2.x - offset, rect.y + down.height, rGe2.x - offset, rGe2.y + mEventHeight / 2);

                    // draw the last snippet
                    gc.drawLine(rGe2.x - offset, rGe2.y + mEventHeight / 2, rGe2.x, rGe2.y + mEventHeight / 2);

                    if (mSettings.showArrows()) {
                        if (goingUp) {
                            mPaintManager.drawArrowHead(rGe2.x - 7, rGe2.y + mEventHeight / 2, SWT.LEFT, gc);
                        } else {
                            mPaintManager.drawArrowHead(rGe2.x - 7, rGe2.y + mEventHeight / 2, SWT.RIGHT, gc);
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
                    gc.setForeground(connection.getColor() == null ? mArrowColor : connection.getColor());

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
                        if (mSettings.showArrows()) {
                            x = ge2.getX() - 8;
                            mPaintManager.drawArrowHead(x, y, SWT.RIGHT, gc);
                        }
                    } else if (targetIsOnLeft || eventsOverlap || targetIsOnLeftBorder || targetIsOnRightBorder) {
                        // for left side we draw the vertical line down to the middle between the events
                        // int yDiff = (ge2.getY() - (ge1.getY() + ge1.getHeight())) / 2;
                        // gc.drawLine(x, y, x, ge1.getY() + ge1.getHeight() + yDiff - 2);
                        int yDest = ge1.getBottomY() + (mEventSpacer / 2);
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
                        if (mSettings.showArrows()) {
                            x = ge2.getX() - 8;
                            mPaintManager.drawArrowHead(x, y, SWT.RIGHT, gc);
                        }

                    }
                } else if (aboveUs) {
                    gc.setForeground(connection.getColor() == null ? mReverseArrowColor : connection.getColor());

                    // draw first stub
                    gc.drawLine(x, y, x + rect.width, y);
                    x += rect.width;

                    if (targetIsOnLeft || eventsOverlap || targetIsOnRightBorder || targetIsOnLeftBorder) {
                        // #0 bend
                        xy = drawBend(gc, BEND_RIGHT_DOWN, x, y, true);
                        x = xy.x;
                        y = xy.y;

                        // #1 vertical
                        int yDest = ge1.getBottomY() + (mEventSpacer / 2);
                        gc.drawLine(x, y, x, yDest);
                        y = yDest;

                        // #2 bend
                        xy = drawBend(gc, BEND_LEFT_DOWN, x, y, true);
                        x = xy.x;
                        y = xy.y;

                        // #3 line (not -8 as we don't want line overlap for events that draw down and use -8)
                        gc.drawLine(x, y, ge2.getX() - neg - mSettings.getReverseDependencyLineHorizontalSpacer(), y);
                        x = ge2.getX() - neg - mSettings.getReverseDependencyLineHorizontalSpacer();

                        // #4 bend
                        xy = drawBend(gc, BEND_LEFT_UP, x, y, true);
                        x = xy.x;
                        y = xy.y;

                        // #5 vert up
                        if (mSettings.useSplitArrowConnections()) {
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
                        if (mSettings.showArrows()) {
                            x = ge2.getX() - 8;
                            mPaintManager.drawArrowHead(x, y, SWT.RIGHT, gc);
                        }
                    } else if (targetIsOnRight) {
                        // #1 bend
                        xy = drawBend(gc, BEND_RIGHT_UP, x, y, true);
                        x = xy.x;
                        y = xy.y;

                        // #2 vert up
                        if (mSettings.useSplitArrowConnections()) {
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
                        if (mSettings.showArrows()) {
                            x = ge2.getX() - 8;
                            mPaintManager.drawArrowHead(x, y, SWT.RIGHT, gc);
                        }
                    }
                } else if (sameRow) {
                    if (targetIsOnLeft || eventsOverlap || targetIsOnRightBorder || targetIsOnLeftBorder) {
                        gc.setForeground(connection.getColor() == null ? mReverseArrowColor : connection.getColor());

                        // draw first stub
                        gc.drawLine(x, y, x + rect.width, y);
                        x += rect.width;

                        // #1 bend
                        xy = drawBend(gc, BEND_RIGHT_DOWN, x, y, true);
                        x = xy.x;
                        y = xy.y;

                        // #2 vert down
                        if (mSettings.useSplitArrowConnections()) {
                            gc.drawLine(x, y, x, ge2.getY() + ge2.getHeight() + (mEventSpacer / 2));
                            y = ge2.getY() + ge2.getHeight();
                        } else {
                            gc.drawLine(x, y, x, ge2.getY() + (ge2.getHeight() / 2) + 2 + (mEventSpacer / 2));
                            y = ge2.getY() + (ge2.getHeight() / 2) + 2;
                        }
                        y += (mEventSpacer / 2);

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
                        if (mSettings.useSplitArrowConnections()) {
                            gc.drawLine(x, y, x, ge2.getY() + (ge2.getHeight() / 2) + neg - mSettings.getReverseDependencyLineHorizontalSpacer());
                            y = ge2.getY() + (ge2.getHeight() / 2) + neg - mSettings.getReverseDependencyLineHorizontalSpacer();
                        } else {
                            gc.drawLine(x, y, x, ge2.getY() + (ge2.getHeight() / 2) + neg - mSettings.getReverseDependencyLineHorizontalSpacer());
                            y = ge2.getY() + (ge2.getHeight() / 2) + neg - mSettings.getReverseDependencyLineHorizontalSpacer();
                        }

                        // #7 bend
                        xy = drawBend(gc, BEND_RIGHT_UP, x, y, true);
                        x = xy.x;
                        y = xy.y;

                        // #8 last straight
                        gc.drawLine(x, y, ge2.getX(), y);

                        if (mSettings.showArrows()) {
                            x = ge2.getX() - 8;
                            mPaintManager.drawArrowHead(x, y, SWT.RIGHT, gc);
                        }
                    } else if (targetIsOnRight) {
                        gc.setForeground(connection.getColor() == null ? mArrowColor : connection.getColor());

                        // if distance between left and right is smaller than the width of a day small, we draw it differently or we'll get a funny bend
                        if ((ge2.getX() - ge1.getXEnd()) <= dw) {
                            gc.drawLine(ge1.getXEnd(), y, ge2.getX(), y);

                            if (mSettings.showArrows()) {
                                x = ge2.getX() - 8;
                                mPaintManager.drawArrowHead(x, y, SWT.RIGHT, gc);
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
                        if (mSettings.useSplitArrowConnections()) {
                            gc.drawLine(x, y, x, ge2.getY() + ge2.getHeight() + (mEventSpacer / 2));
                            y = ge2.getY() + ge2.getHeight();
                        } else {
                            gc.drawLine(x, y, x, ge2.getY() + (ge2.getHeight() / 2) + 2 + (mEventSpacer / 2));
                            y = ge2.getY() + (ge2.getHeight() / 2) + 2;
                        }
                        y += (mEventSpacer / 2);

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
                        if (mSettings.useSplitArrowConnections()) {
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

                        if (mSettings.showArrows()) {
                            x = ge2.getX() - 8;
                            mPaintManager.drawArrowHead(x, y, SWT.RIGHT, gc);
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

        int xStart = x;

        gc.setForeground(mLineTodayColor);
        gc.setLineWidth(mSettings.getTodayLineWidth());
        gc.setLineStyle(mSettings.getTodayLineStyle());

        // to not clash with the week divider lines move dotted line 1 pixel left on new weeks
        if (mCurrentView == ISettings.VIEW_WEEK || mCurrentView == ISettings.VIEW_MONTH) {
            if (dayOfWeek == mCalendar.getFirstDayOfWeek()) xStart--;
        }

        int vOffset = mSettings.getTodayLineVerticalOffset();
        int yStart = bounds.y - mSettings.getHeaderMonthHeight();
        if (!mSettings.drawHeader()) vOffset = 0;
        yStart += vOffset;
        if (!mSettings.drawHeader()) yStart = bounds.y;

        yStart -= mVerticalScrollPosition;

        if (mUseAlpha) gc.setAlpha(mColorManager.getTodayLineAlpha());

        gc.drawLine(xStart, yStart, xStart, bounds.height + yStart + mVerticalScrollPosition);
        if (mUseAlpha) {
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
            switch (mCurrentView) {
                case ISettings.VIEW_WEEK:
                    return DateHelper.getDate(cal, mSettings.getWeekHeaderTextDisplayFormatTop(), mDefaultLocale);
                case ISettings.VIEW_MONTH:
                    return DateHelper.getDate(cal, mSettings.getMonthHeaderTextDisplayFormatTop(), mDefaultLocale);
                case ISettings.VIEW_DAY:
                    return DateHelper.getDate(cal, mSettings.getDayHeaderTextDisplayFormatTop(), mDefaultLocale);
                case ISettings.VIEW_YEAR:
                    return DateHelper.getDate(cal, mSettings.getYearHeaderTextDisplayFormatTop(), mDefaultLocale);
            }
        } else {
            switch (mCurrentView) {
                case ISettings.VIEW_WEEK:
                    return DateHelper.getDate(cal, mSettings.getWeekHeaderTextDisplayFormatBottom(), mDefaultLocale).substring(0, 1);
                case ISettings.VIEW_MONTH:
                    return DateHelper.getDate(cal, mSettings.getMonthHeaderTextDisplayFormatBottom(), mDefaultLocale);
                case ISettings.VIEW_DAY:
                    return DateHelper.getDate(cal, mSettings.getDayHeaderTextDisplayFormatBottom(), mDefaultLocale);
                case ISettings.VIEW_YEAR:
                    return DateHelper.getDate(cal, mSettings.getYearHeaderTextDisplayFormatBottom(), mDefaultLocale);
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

        Calendar cal = Calendar.getInstance(mDefaultLocale);

        if (mCurrentView == ISettings.VIEW_DAY) {
            // round it down
            internalSetDate(cal, side, true, true);
        } else setDate(cal, side, false);
    }

    /**
     * Moves the calendar to the earliest event date.
     */
    public void jumpToEarliestEvent() {
        Calendar cal = Calendar.getInstance(mDefaultLocale);
        Date earliest = getEventDate(true);
        if (earliest == null) return;

        flagForceFullUpdate();
        cal.setTime(earliest);

        if (mCurrentView == ISettings.VIEW_DAY) {
            // round it down
            internalSetDate(cal, SWT.LEFT, true, true);
        } else setDate(cal, false);
    }

    /**
     * Moves the calendar to the latest event date.
     */
    // Thanks Wim!
    public void jumpToLatestEvent() {
        Calendar cal = Calendar.getInstance(mDefaultLocale);
        GanttEvent latest = getEvent(false);
        if (latest == null) return;

        flagForceFullUpdate();
        cal.setTime(latest.getActualStartDate().getTime());

        if (mCurrentView == ISettings.VIEW_DAY) {
            internalSetDate(cal, SWT.LEFT, true, true);
        } else setDate(cal, false);
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
        Calendar cal = Calendar.getInstance(mDefaultLocale);

        if (start) cal.setTime(event.getActualStartDate().getTime());
        else cal.setTime(event.getActualEndDate().getTime());

        internalSetDate(cal, side, true, true);
    }

    GanttEvent getEvent(boolean earliest) {
        Calendar cal = null;
        GanttEvent retEvent = null;

        for (int i = 0; i < mGanttEvents.size(); i++) {
            GanttEvent ge = (GanttEvent) mGanttEvents.get(i);
            if (earliest) {
                if (cal == null) {
                    cal = ge.getActualStartDate();
                    retEvent = ge;
                    continue;
                }

                if (ge.getActualStartDate().before(cal)) {
                    cal = ge.getActualStartDate();
                    retEvent = ge;
                }
            } else {
                if (cal == null) {
                    cal = ge.getActualEndDate();
                    retEvent = ge;
                    continue;
                }

                if (ge.getActualEndDate().after(cal)) {
                    cal = ge.getActualEndDate();
                    retEvent = ge;
                }
            }
        }

        return retEvent;
    }

    private Date getEventDate(boolean earliest) {
        Calendar ret = null;

        for (int i = 0; i < mGanttEvents.size(); i++) {
            GanttEvent ge = (GanttEvent) mGanttEvents.get(i);
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

        if (mCurrentView == ISettings.VIEW_DAY) {
            int hours = DateHelper.hoursBetween(target, preZoomDate, mDefaultLocale, false);
            Calendar toSet = (Calendar) mCalendar.clone();
            if (zoomIn) {
                hours = Math.abs(hours);
            }

            toSet.add(Calendar.HOUR_OF_DAY, hours);

            internalSetDate(toSet, SWT.LEFT, clearMinutes, redraw);
        } else {
            int days = (int) DateHelper.daysBetween(target, preZoomDate, mDefaultLocale);
            if (zoomIn) {
                days = Math.abs(days);
            }

            Calendar toSet = (Calendar) mCalendar.clone();
            toSet.add(Calendar.DATE, days);
            internalSetDate(toSet, SWT.LEFT, clearMinutes, redraw);
        }
    }

    private void internalSetDate(final Calendar date, final int side, final boolean clearMinutes, final boolean redraw) {
        checkWidget();
        
        // set the date regardless, thus the below "jump" will be minor
        mCalendar = (Calendar) date.clone();
        if (clearMinutes) {
            mCalendar.set(Calendar.MINUTE, 0);
            mCalendar.set(Calendar.SECOND, 0);
            mCalendar.set(Calendar.MILLISECOND, 0);
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
                if (mCurrentView == ISettings.VIEW_DAY) {
                    copy.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY));
                }
                break;
            case SWT.CENTER:
                if (mCurrentView == ISettings.VIEW_DAY) {
                    int middleHours = mHoursVisible / 2;
                    copy.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY));
                    copy.add(Calendar.HOUR_OF_DAY, -middleHours);
                } else {
                    int middle = mDaysVisible / 2;
                    copy.add(Calendar.DATE, -middle);
                }
                break;
            case SWT.RIGHT:
                if (mCurrentView == ISettings.VIEW_DAY) {
                    copy.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY));
                    copy.add(Calendar.HOUR_OF_DAY, -mHoursVisible);
                } else {
                    copy.add(Calendar.DATE, -mDaysVisible + 1);
                }
                break;
        }

        mCalendar = copy;
        mReCalculateScopes = true;
        mReCalculateSectionBounds = true;

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
            copy.add(Calendar.DATE, mStartCalendarAtOffset);
        }

        // not for D-Days, there is no such thing as start of week there
        if (mSettings.startCalendarOnFirstDayOfWeek() && mCurrentView != ISettings.VIEW_D_DAY) {
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
        mGanttEvents.remove(event);
        mGanttEvents.add(newIndex, event);
        redrawEventsArea();
    }

    /**
     * Re-indexes a GanttSection to a new index.
     * 
     * @param section GanttSection to reindex
     * @param newIndex new index
     */
    public void reindex(GanttSection section, int newIndex) {
        mGanttSections.remove(section);
        mGanttSections.add(newIndex, section);
        redrawEventsArea();
    }

    /**
     * Re-indexes a GanttGroup to a new index.
     * 
     * @param group GanttGroup to reindex
     * @param newIndex new index
     */
    public void reindex(GanttGroup group, int newIndex) {
        mGanttGroups.remove(group);
        mGanttGroups.add(newIndex, group);
        redrawEventsArea();
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
        if (!mGanttEvents.contains(event)) {
            mGanttEvents.add(index, event);
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
        if (!mGanttEvents.contains(event)) {
            mGanttEvents.add(event);
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
        mGanttEvents.remove(event);
        ArrayList toRemove = new ArrayList();
        for (int i = 0; i < mGanttConnections.size(); i++) {
            GanttConnection con = (GanttConnection) mGanttConnections.get(i);

            if (con.getSource().equals(event) || con.getTarget().equals(event)) {
                toRemove.add(con);
            }

        }
        for (int i = 0; i < toRemove.size(); i++) {
            mGanttConnections.remove(toRemove.get(i));
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
        return mGanttConnections;
    }

    void eventDatesChanged(GanttEvent ge, boolean redraw) {
        int newStartX = getXForDate(ge.getActualStartDate());
        int newEndX = getXForDate(ge.getActualEndDate());

        // if we're zoomed in to see hours, we don't modify the end date
        if (mCurrentView != ISettings.VIEW_DAY) {
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
        mGanttEvents.clear();
        mGanttConnections.clear();
        // mGmap.clear();
        eventNumbersChanged();
        mForceScrollbarsUpdate = true;
        mVerticalScrollPosition = 0;
        mLastVerticalScrollPosition = 0;
        mVerticalScrollBar.setSelection(0);
        redraw();
    }

    /**
     * Clears all GanttGroups from the chart.
     */
    public void clearGanttGroups() {
        checkWidget();
        mGanttGroups.clear();
        eventNumbersChanged();
        mForceScrollbarsUpdate = true;
        mVerticalScrollPosition = 0;
        mLastVerticalScrollPosition = 0;
        mVerticalScrollBar.setSelection(0);
        redraw();
    }

    /**
     * Clears all GanttSections from the chart.
     */
    public void clearGanttSections() {
        checkWidget();
        mGanttSections.clear();
        eventNumbersChanged();
        mForceScrollbarsUpdate = true;
        mVerticalScrollPosition = 0;
        mLastVerticalScrollPosition = 0;
        mVerticalScrollBar.setSelection(0);
        redraw();
    }

    /**
     * Clears the entire chart of everything (all types of events) and leaves the chart blank.
     */
    public void clearChart() {
        checkWidget();
        mGanttEvents.clear();
        mGanttConnections.clear();
        mGanttSections.clear();
        mGanttGroups.clear();
        // mGmap.clear();
        eventNumbersChanged();
        mForceScrollbarsUpdate = true;
        mVerticalScrollPosition = 0;
        mLastVerticalScrollPosition = 0;
        mVerticalScrollBar.setSelection(0);
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
        return mGanttEvents.contains(event);
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
        if (!mUseFastDrawing) return;

        mReCalculateScopes = false;
        mReCalculateSectionBounds = false;
    }

    private void eventNumbersChanged() {
        if (!mUseFastDrawing) return;

        mAllEventsCombined.clear();

        for (int i = 0; i < mGanttEvents.size(); i++) {
            Object obj = mGanttEvents.get(i);
            if (obj instanceof GanttEvent) {
                mAllEventsCombined.add(obj);
            } else if (obj instanceof GanttGroup) {
                mAllEventsCombined.addAll(((GanttGroup) obj).getEventMembers());
            }
        }
        for (int i = 0; i < mGanttGroups.size(); i++) {
            GanttGroup gg = (GanttGroup) mGanttGroups.get(i);
            mAllEventsCombined.addAll(gg.getEventMembers());
        }

        flagForceFullUpdate();
    }

    // moves the x bounds of all events one day width left or right
    void moveXBounds(boolean positive) {
        if (!mUseFastDrawing) return;

        Object[] objs = mAllEventsCombined.toArray();

        int dw = getDayWidth();
        if (mCurrentView == ISettings.VIEW_YEAR) {
            Calendar temp = Calendar.getInstance(mDefaultLocale);
            temp.setTime(mCalendar.getTime());
            temp.add(Calendar.MONTH, positive ? 1 : -1);

            long days = DateHelper.daysBetween(mCalendar, temp, mDefaultLocale);
            dw = Math.abs((int) days * dw);
        }

        for (int i = 0; i < mAllEventsCombined.size(); i++) {
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
        Object[] objs = mAllEventsCombined.toArray();

        for (int i = 0; i < objs.length; i++) {
            GanttEvent ge = (GanttEvent) objs[i];
            ge.updateY(ge.getY() - move);
        }

        mVisibleBounds.y += move;

        updateEventVisibilities(mVisibleBounds);
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
        if (mSavingChartImage) {
            if (!event.isHidden()) { return VISIBILITY_VISIBLE; }
        }

        // fastest checks come first, if it's not a visible layer, it's not visible
        if (mHiddenLayers.contains(new Integer(event.getLayer()))) return VISIBILITY_NOT_VISIBLE;

        // if event is missing dates, don't let it show, fix to #281983
        if (event.getActualStartDate() == null || event.getActualEndDate() == null) { return VISIBILITY_NOT_VISIBLE; }

        // our second check is the check whether it's out of bounds vertically, if so we can return right away (and scope calculation
        // takes the special OOB_HEIGHT into account when counting the vertical offset

        // as we offset the entire view area when scrolling vertically by moving the events up or down vertically
        // we need to check the offset as if they were still in their original position, which we do by taking their y location
        // and adding on the vertical scroll position. Once we have those fake bounds, we simply check it against the visual area
        // and if they're not inside, they're out!
        Rectangle fakeBounds = new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight());
        fakeBounds.y += mVerticalScrollPosition;

        // first draw everything is zero, ignore that one
        if (event.getY() != 0) {
            if (fakeBounds.y > mVisibleBounds.y + mVisibleBounds.height) { return VISIBILITY_OOB_HEIGHT_BOTTOM; }
            if ((fakeBounds.y + fakeBounds.height) < mVisibleBounds.y) { return VISIBILITY_OOB_HEIGHT_TOP; }
        }

        Calendar sCal = event.getActualStartDate();
        Calendar eCal = event.getActualEndDate();
        if (sCal == null) sCal = event.getRevisedStart();
        if (eCal == null) eCal = event.getRevisedEnd();

        // scope checking
        if (event.isScope()) {
            GanttEvent earliest = event.getEarliestScopeEvent();
            GanttEvent latest = event.getLatestScopeEvent();
            if (earliest != null) sCal = earliest.getActualStartDate();
            if (latest != null) eCal = latest.getActualEndDate();

            if (sCal == null || eCal == null) return VISIBILITY_NOT_VISIBLE;
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
            Calendar temp = Calendar.getInstance(mDefaultLocale);
            temp.setTime(mCalendar.getTime());

            long calStart = temp.getTimeInMillis();
            if (mDaysVisible != 0) temp.add(Calendar.DATE, mDaysVisible);
            else temp.setTime(mEndCalendar.getTime());

            long calEnd = temp.getTimeInMillis();

            if (eStart.getTime() >= calStart && eStart.getTime() <= calEnd) return VISIBILITY_VISIBLE;

            if (eEnd.getTime() >= calStart && eEnd.getTime() <= calEnd) return VISIBILITY_VISIBLE;

            // event spans entire screen, also fix to Bugzilla bug #236846 - https://bugs.eclipse.org/bugs/show_bug.cgi?id=236846
            if (eStart.getTime() <= calStart && eEnd.getTime() >= calEnd) return VISIBILITY_VISIBLE;

        } else {
            int xStart = getStartingXfor(sCal);
            int xEnd = getXForDate(eCal);

            int buffer = mSettings.getArrowHeadEventSpacer();
            xEnd += buffer;
            xStart -= buffer;

            // account for the actual text
            // TODO: we should account for other text locations
            if (event.getHorizontalTextLocation() == SWT.RIGHT) xEnd += event.getNameExtent().x;

            if (xEnd < 0) { return VISIBILITY_OOB_LEFT; }

            if (xStart > bounds.width) return VISIBILITY_OOB_RIGHT;

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
        if (mCurrentView == ISettings.VIEW_DAY) {
            return getStartingXforEventHours(event);
        }
        else {
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
        Calendar temp = Calendar.getInstance(mDefaultLocale);
        temp.setTime(mCalendar.getTime());

        // some stuff we know, (to help program this)
        // 1 dayWidth is one working hour, thus, 1 dayWidth / 60 = 1 minute
        // 1 day is the same as the week width

        int dw = getDayWidth();
        int daysBetween = (int) DateHelper.daysBetween(temp, start, mDefaultLocale);
        int ret = daysBetween * mWeekWidth;
        ret += mBounds.x;

        // days is ok, now deal with hours
        float hoursBetween = DateHelper.hoursBetween(temp, start, mDefaultLocale, true);
        float minutesBetween = DateHelper.minutesBetween(temp.getTime(), start.getTime(), mDefaultLocale, true, false);

        float minPixels = 0;

        // now deal with minutes, if settings say so
        if (!mSettings.roundHourlyEventsOffToNearestHour()) {
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

        if (mCurrentView == ISettings.VIEW_DAY) {
            return getStartingXForEventHours(date);
        }

        if (date == null) {
            return mBounds.x;
        }

        Calendar temp = Calendar.getInstance(mDefaultLocale);
        temp.setTime(mCalendar.getTime());
        if (mCurrentView == ISettings.VIEW_YEAR) {
            temp.set(Calendar.DAY_OF_MONTH, 1);
        }

        long daysBetween = DateHelper.daysBetween(temp, date, mDefaultLocale);
        int dw = getDayWidth();

        int extra = 0;
        if (mSettings.drawEventsDownToTheHourAndMinute()) {
            if (mCurrentView != ISettings.VIEW_DAY) {
                float ppm = 60f / mDayWidth;

                int mins = date.get(Calendar.HOUR_OF_DAY);
                extra = (int) (mins * ppm);
            }
        }

        return mBounds.x + ((int) daysBetween * dw) + extra;
    }

    private int getXLengthForEventHours(GanttEvent event) {
        return getXForDate(event.getActualEndDate()) - getXForDate(event.getActualStartDate());
    }

    // gets the x position for where the event bar should end
    private int getXLengthForEvent(GanttEvent event) {
        if (mCurrentView == ISettings.VIEW_DAY) {
            return getXLengthForEventHours(event);
        }

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
        int dw = mDayWidth;
        if (mCurrentView == ISettings.VIEW_MONTH) {
            dw = mMonthDayWidth;
        } else if (mCurrentView == ISettings.VIEW_YEAR) {
            dw = mYearDayWidth;
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

        for (int i = 0; i < mGanttEvents.size(); i++) {
            GanttEvent event = (GanttEvent) mGanttEvents.get(i);

            if (isInside(me.x, me.y, new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight()))) {
                for (int j = 0; j < mEventListeners.size(); j++) {
                    ((IGanttEventListener) mEventListeners.get(j)).eventDoubleClicked(event, me);
                }

                return;
            }
        }
    }

    /**
     * Returns a list of all selected header dates (Calendar).
     * 
     * @return List of calendars or empty list if none
     */
    public List getAllSelectedHeaderDates() {
        return mSelectedHeaderDates;
    }

    /**
     * Clears all selected headers and redraws the chart.
     */
    public void clearAllSelectedHeaders() {
        mSelectedHeaderDates.clear();
        redraw();
    }

    /**
     * Sets a list of header dates that should be the selected dates. This list must be a list of Calendars.
     * 
     * @param dates List of Calendar objects representing selected header dates.
     */
    public void setSelectedHeaderDates(List dates) {
        mSelectedHeaderDates = dates;
        redraw();
    }

    private void doMultiSelect(Rectangle rect, MouseEvent me) {
        if (rect == null) return;

        for (int i = 0; i < mGanttEvents.size(); i++) {
            GanttEvent ge = (GanttEvent) mGanttEvents.get(i);
            if (ge.isScope()) continue;

            if (ge.getBounds().intersects(rect)) mSelectedEvents.add(ge);
        }

        for (int x = 0; x < mEventListeners.size(); x++) {
            IGanttEventListener listener = (IGanttEventListener) mEventListeners.get(x);
            listener.eventSelected(null, mSelectedEvents, me);
        }

        redraw();
    }

    public void mouseDown(MouseEvent me) {
        if (me.button == 1) mMouseIsDown = true;

        Point ctrlPoint = toDisplay(new Point(me.x, me.y));

        if (mSettings.allowBlankAreaDragAndDropToMoveDates() && me.button == 1) setCursor(CURSOR_HAND);

        // remove old dotted border, we used to just create a new GC and clear it, but to be honest, it was a bit of a hassle with some calculations
        // and left over cheese, and a redraw is just faster. It's rare enough anyway, and we don't redraw more than a small area
        if (mSelectedEvents.size() != 0 && mSettings.drawSelectionMarkerAroundSelectedEvent()) {
            for (int x = 0; x < mSelectedEvents.size(); x++) {
                GanttEvent selEvent = (GanttEvent) mSelectedEvents.get(x);
                redraw(selEvent.getX() - 2, selEvent.getY() - 2, selEvent.getWidth() + 4, selEvent.getHeight() + 4, false);
            }
        }

        // header clicks, if it's visible
        if (mSettings.drawHeader() && mSettings.allowHeaderSelection()) {
            Rectangle headerBounds = new Rectangle(mBounds.x, mSettings.getHeaderMonthHeight() + mVerticalScrollPosition, mBounds.width, mSettings.getHeaderDayHeight());

            if (isInside(me.x, me.y, headerBounds)) {
                // we account for section bar width
                Calendar cal = getDateAt(me.x - mBounds.x);

                if (me.stateMask == SWT.MOD1 || me.stateMask == SWT.SHIFT || me.stateMask == (SWT.MOD1 | SWT.SHIFT)) {
                    if (me.stateMask == SWT.MOD1) {
                        if (mSelectedHeaderDates.contains(cal)) mSelectedHeaderDates.remove(cal);
                        else mSelectedHeaderDates.add(cal);
                    } else {
                        // shift or ctrl + shift
                        Calendar last = (Calendar) mSelectedHeaderDates.get(mSelectedHeaderDates.size() - 1);
                        long days = 0;
                        boolean reverse = false;
                        if (last.before(cal)) {
                            days = DateHelper.daysBetween(last, cal, mDefaultLocale);
                        } else {
                            days = DateHelper.daysBetween(cal, last, mDefaultLocale);
                            reverse = true;
                        }

                        // all dates
                        Calendar temp = Calendar.getInstance(mDefaultLocale);
                        temp.setTime(last.getTime());
                        for (int i = 0; i < (int) days; i++) {
                            temp.add(Calendar.DATE, reverse ? -1 : 1);
                            Calendar temp2 = Calendar.getInstance(mDefaultLocale);
                            temp2.setTime(temp.getTime());
                            mSelectedHeaderDates.add(temp2);
                        }
                    }
                } else {
                    if (mSelectedHeaderDates.contains(cal)) {
                        mSelectedHeaderDates.clear();
                    } else {
                        mSelectedHeaderDates.clear();
                        mSelectedHeaderDates.add(cal);
                    }
                }

                for (int i = 0; i < mEventListeners.size(); i++) {
                    ((IGanttEventListener) mEventListeners.get(i)).eventHeaderSelected(cal, mSelectedHeaderDates);
                }

                redraw();
                return;
            }

        }

        // deal with selection
        for (int i = 0; i < mGanttEvents.size(); i++) {
            GanttEvent event = (GanttEvent) mGanttEvents.get(i);

            if (event.isScope() && !mSettings.allowScopeMenu()) continue;

            if (isInside(me.x, me.y, new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight()))) {
                GC gc = new GC(this);

                // if it's a scope and menu is allowed, we can finish right here
                if (me.button == 3 && event.isScope()) {
                    showMenu(ctrlPoint.x, ctrlPoint.y, event, me);
                    // dispose or we have a memory leak, as we're returning!
                    gc.dispose();
                    return;
                }

                if (me.stateMask == 0 || !mMultiSelect) mSelectedEvents.clear();

                // if we're multi selecting
                if (mMultiSelect) {
                    // ctrl select + deselect
                    if (mSelectedEvents.contains(event) && me.stateMask == SWT.MOD1) mSelectedEvents.remove(event);
                    else mSelectedEvents.add(event);
                } else mSelectedEvents.add(event);

                // fire selection changed
                if ((mSelectedEvents.size() > 0)) {// && !mSelectedEvents.contains(event)) || mSelectedEvents.size() == 0) {
                    for (int x = 0; x < mEventListeners.size(); x++) {
                        IGanttEventListener listener = (IGanttEventListener) mEventListeners.get(x);
                        listener.eventSelected(event, mSelectedEvents, me);
                    }
                }

                for (int x = 0; x < mSelectedEvents.size(); x++) {
                    GanttEvent selEvent = (GanttEvent) mSelectedEvents.get(x);
                    if (!selEvent.isCheckpoint() && !selEvent.isScope() && !selEvent.isImage() && mSettings.drawSelectionMarkerAroundSelectedEvent()) {
                        gc.setForeground(ColorCache.getWhite());
                        drawSelectionAroundEvent(gc, event, event.getX(), event.getY(), event.getWidth(), mBounds);
                    }
                }

                gc.dispose();

                if (mSettings.showOnlyDependenciesForSelectedItems()) {
                    redrawEventsArea();
                }

                // show menu on the last selected event if there are multiple
                if (me.button == 3 && mSelectedEvents.size() > 0) showMenu(ctrlPoint.x, ctrlPoint.y, (GanttEvent) mSelectedEvents.get(mSelectedEvents.size() - 1), me);

                return;
            }
        }

        if (me.button == 3) {
            mSelectedEvents.clear();
            showMenu(ctrlPoint.x, ctrlPoint.y, null, me);
            return;
        }

        // after selection, we only allow Trackers in blank areas, it's too much of a clash with other listeners and events otherwise
        if (mMultiSelect && me.button == 1 && me.stateMask == SWT.MOD1) {
            setCursor(CURSOR_NONE);
            mTracker = new Tracker(this, SWT.RESIZE);
            mTracker.setCursor(CURSOR_NONE);
            mTracker.setStippled(true);
            mTracker.setRectangles(new Rectangle[] { new Rectangle(me.x, me.y, 1, 1) });
            // this blocks until the Tracker is disposed, so on success, we multiselect
            if (mTracker.open()) {
                doMultiSelect(mTracker.getRectangles()[0], me);
            }
            return;
        }

        if (me.stateMask == 0) mSelectedEvents.clear();

        if (mSettings.showOnlyDependenciesForSelectedItems()) {
            redrawEventsArea();
        }

    }

    private void drawSelectionAroundEvent(GC gc, GanttEvent ge, int x, int y, int eventWidth, Rectangle bounds) {
        if (!mSettings.drawSelectionMarkerAroundSelectedEvent()) return;

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
            gc.drawRectangle(x, y, eventWidth, mEventHeight);
        }

        gc.setLineStyle(SWT.LINE_SOLID);
    }

    // whenever the mouse button is let up, we reset all drag and resize things,
    // as they only happen when the mouse
    // button is down and we resize or drag & drop
    public void mouseUp(MouseEvent event) {
        mMouseIsDown = false;

        if (mTracker != null) {
            // mTracker.close();
            mTracker.dispose();
            mTracker = null;
        }

        if (mDragEvents.size() > 0) {
            if (mResizing) {
                for (int i = 0; i < mEventListeners.size(); i++) {
                    ((IGanttEventListener) mEventListeners.get(i)).eventsResizeFinished(mDragEvents, event);
                }
            }
            if (mDragging) {
                for (int i = 0; i < mEventListeners.size(); i++) {
                    ((IGanttEventListener) mEventListeners.get(i)).eventsMoveFinished(mDragEvents, event);
                }
            }

            for (int i = 0; i < mDragEvents.size(); i++) {
                ((GanttEvent) mDragEvents.get(i)).moveFinished();
            }
        }

        endEverything();
        updateHorizontalScrollbar();
    }

    public void keyPressed(KeyEvent e) {
        // an attempt at ending everything
        if (e.keyCode == SWT.ESC) {

            List scopeEventsToUpdate = new ArrayList();

            // if we were in the middle of a drag or resize, we cancel the move and reset the dates back to before the move started
            if (mDragEvents.size() > 0) {
                for (int i = 0; i < mDragEvents.size(); i++) {
                    GanttEvent ge = (GanttEvent) mDragEvents.get(i);
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

            mDragEvents.clear();
            endEverything();
            mSelectedEvents.clear();
            killDialogs();
            redraw();
            setCursor(CURSOR_NONE);
        }
    }

    public void keyReleased(KeyEvent e) {
        checkWidget();
        endEverything();
    }

    private void endEverything() {
        if (mTracker != null) {
            mTracker.dispose();
            mTracker = null;
            mMouseIsDown = false;
        }

        // oh, not quite yet
        if (mMouseIsDown) { return; }

        mInitialHoursDragOffset = 0;
        mJustStartedMoveOrResize = false;

        mMouseIsDown = false;
        mMouseDragStartLocation = null;
        mDragStartDate = null;

        // kill auto scroll
        endAutoScroll();

        mShowZoomHelper = false;
        if (mZoomLevelHelpArea != null) {
            redraw(mZoomLevelHelpArea.x, mZoomLevelHelpArea.y, mZoomLevelHelpArea.width + 1, mZoomLevelHelpArea.height + 1, false);
        }

        mDragging = false;
        mDragEvents.clear();
        mResizing = false;
        this.setCursor(CURSOR_NONE);
        mLastX = 0;
        mCursor = SWT.NONE;
        killDialogs();
    }

    public void mouseMove(final MouseEvent me) {
        try {
            // kill dialogs if no mouse button is held down etc, otherwise we just move the dialog with updated text if it's a tooltip etc
            // and pre-killing will cause flicker
            if (me.stateMask == 0) {
                killDialogs();
            }

            if (mTracker != null) { return; }

            String dateFormat = (mCurrentView == ISettings.VIEW_DAY ? mSettings.getHourDateFormat() : mSettings.getDateFormat());

            // if we moved mouse back in from out of bounds, kill auto scroll
            Rectangle area = super.getClientArea();
            if (me.x >= area.x && me.x < area.width) {
                endAutoScroll();
            }

            boolean dndOK = mSettings.enableDragAndDrop();
            boolean resizeOK = mSettings.enableResizing();

            // check if we need to auto-scroll
            if ((mDragging || mResizing) && mSettings.enableAutoScroll()) {
                if (me.x < 0) {
                    doAutoScroll(me);
                }

                if (me.x > mBounds.width) {
                    doAutoScroll(me);
                }
            }

            // move
            if (mDragging && dndOK) {
                AdvancedTooltipDialog.kill();
                GanttToolTip.kill();
                for (int x = 0; x < mSelectedEvents.size(); x++) {
                    handleMove(me, (GanttEvent) mSelectedEvents.get(x), TYPE_MOVE, x == 0);
                }
                return;
            }

            // resize
            if (mResizing && resizeOK && (mCursor == SWT.CURSOR_SIZEE || mCursor == SWT.CURSOR_SIZEW)) {
                for (int x = 0; x < mSelectedEvents.size(); x++) {
                    handleMove(me, (GanttEvent) mSelectedEvents.get(x), mLastLeft ? TYPE_RESIZE_LEFT : TYPE_RESIZE_RIGHT, x == 0);
                }
                return;
            }

            // when we move a mouse really fast at the beginning of a move or a
            // drag, we need to catch that seperately
            // as "isInside()" will not have a chance to see if it's true or not
            // (below)
            if (!mResizing && !mDragging && dndOK && resizeOK && me.stateMask != 0 && mCursor != SWT.NONE) {
                if (mSelectedEvents.size() > 0) {
                    if ((mCursor == SWT.CURSOR_SIZEE || mCursor == SWT.CURSOR_SIZEW)) {
                        // handleResize(me, mSelectedEvent, mCursor == SWT.CURSOR_SIZEW);
                        for (int x = 0; x < mSelectedEvents.size(); x++)
                            handleMove(me, (GanttEvent) mSelectedEvents.get(x), mCursor == SWT.CURSOR_SIZEW ? TYPE_RESIZE_LEFT : TYPE_RESIZE_RIGHT, x == 0);
                        return;
                    } else if (mCursor == SWT.CURSOR_SIZEALL) {
                        for (int x = 0; x < mSelectedEvents.size(); x++)
                            handleMove(me, (GanttEvent) mSelectedEvents.get(x), TYPE_MOVE, x == 0);
                        return;
                    }
                }
            }

            if (me.stateMask == 0) {
                this.setCursor(CURSOR_NONE);
            }

            boolean insideAnyEvent = false;

            // check if the header is locked, if so we check if the mouse is in the header area, and if so, ignore any mouse move event from here on out.
            if (mSettings.lockHeaderOnVerticalScroll()) {
                Rectangle headerBounds = new Rectangle(0, mVerticalScrollPosition, super.getBounds().width, getHeaderHeight());
                if (isInside(me.x, me.y, headerBounds)) { return; }
            }

            // check if cursor is inside the area of an event
            for (int i = 0; i < mGanttEvents.size(); i++) {
                GanttEvent event = (GanttEvent) mGanttEvents.get(i);
                if (isInside(me.x, me.y, new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight()))) {
                    insideAnyEvent = true;

                    if (event.isScope()) {
                        continue;
                    }

                    if (mHiddenLayers.contains(event.getLayerInt())) {
                        continue;
                    }

                    int x = me.x;
                    int y = me.y;
                    Rectangle rect = new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight());

                    boolean onRightBorder = false;
                    boolean onLeftBorder = false;

                    // n pixels left or right of either border = show resize
                    // mouse cursor
                    if (x >= (rect.x + rect.width - mSettings.getResizeBorderSensitivity()) && y >= rect.y && x <= (rect.x + rect.width + mSettings.getResizeBorderSensitivity()) && y <= (rect.y + rect.height)) {
                        onRightBorder = true;
                    } else if (x >= (rect.x - mSettings.getResizeBorderSensitivity()) && y >= rect.y && x <= (rect.x + mSettings.getResizeBorderSensitivity()) && y <= (rect.y + rect.height)) {
                        onLeftBorder = true;
                    }

                    // right border resize cursor (not images!)
                    if (me.stateMask == 0 || me.stateMask == mSettings.getDragAllModifierKey()) {
                        boolean resizeArea = false;
                        if (resizeOK && event.isResizable()) {
                            if ((event.isCheckpoint() && mSettings.allowCheckpointResizing()) || !event.isCheckpoint() && !event.isImage()) {
                                if (onRightBorder) {
                                    this.setCursor(CURSOR_SIZEE);
                                    mCursor = SWT.CURSOR_SIZEE;
                                    return;
                                } else if (onLeftBorder) {
                                    this.setCursor(CURSOR_SIZEW);
                                    mCursor = SWT.CURSOR_SIZEW;
                                    return;
                                }
                            }
                        }
                        // move cursor
                        if ((dndOK && event.isMoveable()) && !resizeArea) {
                            this.setCursor(CURSOR_SIZEALL);
                            mCursor = SWT.CURSOR_SIZEALL;
                            return;
                        }
                    } else {
                        if ((dndOK || event.isMoveable()) && mCursor == SWT.CURSOR_SIZEALL) {
                            if (isInMoveArea(event, me.x)) {
                                handleMove(me, event, TYPE_MOVE, true);
                                return;
                            }
                        }

                        if (!event.isCheckpoint()) {
                            if ((resizeOK || event.isResizable()) && (mCursor == SWT.CURSOR_SIZEE || mCursor == SWT.CURSOR_SIZEW)) {
                                // handleResize(me, event, onLeftBorder);
                                handleMove(me, event, onLeftBorder ? TYPE_RESIZE_LEFT : TYPE_RESIZE_RIGHT, true);
                                return;
                            }
                        }
                    }
                    break;
                }
            }

            // clear mouse cursor if need be
            if (!insideAnyEvent && !mDragging && !mResizing) {
                if (mCursor != SWT.NONE) {
                    this.setCursor(CURSOR_NONE);
                    mCursor = SWT.NONE;
                }
            }

            // blank area drag and drop
            if (mMouseIsDown && mSettings.allowBlankAreaDragAndDropToMoveDates()) {
                // blank area drag
                if (mMouseDragStartLocation == null) mMouseDragStartLocation = new Point(me.x, me.y);

                int diff = me.x - mMouseDragStartLocation.x;

                boolean left = false;
                if (diff < 0) {
                    left = true;
                }

                if (mSettings.flipBlankAreaDragDirection()) {
                    left = !left;
                }

                diff /= getDayWidth();

                diff = Math.abs(diff);

                if (diff > 0 && ISettings.VIEW_YEAR != mCurrentView) {
                    mMouseDragStartLocation = new Point(me.x, me.y);
                }

                // if it's year view, we wait until the diff matches the number of dates of the month to move
                if (ISettings.VIEW_YEAR == mCurrentView && diff < mCalendar.getActualMaximum(Calendar.DATE)) {
                    return;
                }
                // and again if it's year view, and we get here, we do the actual flip
                else if (ISettings.VIEW_YEAR == mCurrentView) {
                    mMouseDragStartLocation = new Point(me.x, me.y);

                    // fast drags get multiple moves..
                    // TODO: just do prevMonth(times) instead of looping, less redraw
                    int times = diff / mCalendar.getActualMaximum(Calendar.DATE);
                    if (times == 0) times = 1;

                    for (int i = 0; i < times; i++) {
                        if (left) prevMonth();
                        else nextMonth();
                    }
                    return;
                }

                for (int i = 0; i < diff; i++) {
                    if (left) {
                        if (mCurrentView == ISettings.VIEW_DAY) {
                            prevHour();
                        } else {
                            prevDay();
                        }
                    } else {
                        if (mCurrentView == ISettings.VIEW_DAY) {
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
                if (mUseFastDrawing && mCurrentView == ISettings.VIEW_YEAR && diff > 7) {
                    mReCalculateScopes = true;
                    mReCalculateSectionBounds = true;
                    redraw();
                }

                Point loc = new Point(mMouseDragStartLocation.x + 10, mMouseDragStartLocation.y - 20);
                // don't show individual dates if we're on yearly view, they're
                // correct, it's just that
                // we move horizontally month-wise, so it doesn't quite make
                // sense.
                if (mZoomLevel >= ISettings.ZOOM_YEAR_MAX) {
                    Calendar temp = Calendar.getInstance(mDefaultLocale);
                    temp.setTime(mCalendar.getTime());
                    temp.set(Calendar.DAY_OF_MONTH, 1);
                    GanttDateTip.makeDialog(mColorManager, DateHelper.getDate(temp, dateFormat, mDefaultLocale), toDisplay(loc), mBounds.y);
                } else {
                    if (mCurrentView == ISettings.VIEW_D_DAY) {
                        GanttDateTip.makeDialog(mColorManager, getCurrentDDate() + "        ", toDisplay(loc), mBounds.y);
                    } else {
                        GanttDateTip.makeDialog(mColorManager, DateHelper.getDate(mCalendar, dateFormat, mDefaultLocale), toDisplay(loc), mBounds.y);
                    }
                }
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public int getDaysVisible() {
        return mDaysVisible;
    }

    private String getCurrentDDate() {
        int days = (int) DateHelper.daysBetween(mDDayCalendar, mCalendar, mDefaultLocale);
        if (days > 0) return "+" + days;

        return "" + days;
    }

    private boolean isInMoveArea(GanttEvent event, int x) {
        Rectangle bounds = new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight());
        return x > (bounds.x + mMoveAreaInsets) && x < (bounds.x + bounds.width - mMoveAreaInsets);
    }

    private void doAutoScroll(MouseEvent event) {
        // Rectangle area = super.getClientArea();

        if (event.x < mBounds.x) {
            doAutoScroll(DIRECTION_LEFT);
        } else if (event.x > mBounds.width) {
            doAutoScroll(DIRECTION_RIGHT);
        } else {
            endAutoScroll();
        }
    }

    private void doAutoScroll(int direction) {
        Runnable timer = null;

        // If we're already autoscrolling in the given direction do nothing
        if (mAutoScrollDirection == direction) { return; }

        final Display display = getDisplay();

        if (direction == DIRECTION_LEFT) {
            timer = new Runnable() {
                public void run() {
                    if (mAutoScrollDirection == DIRECTION_LEFT) {
                        if (mCurrentView == ISettings.VIEW_DAY) prevHour();
                        else prevDay();

                        display.timerExec(TIMER_INTERVAL, this);
                    }
                }
            };
        } else if (direction == DIRECTION_RIGHT) {
            timer = new Runnable() {
                public void run() {
                    if (mAutoScrollDirection == DIRECTION_RIGHT) {
                        if (mCurrentView == ISettings.VIEW_DAY) nextHour();
                        else nextDay();

                        display.timerExec(TIMER_INTERVAL, this);
                    }
                }
            };
        }
        if (timer != null) {
            mAutoScrollDirection = direction;
            display.timerExec(TIMER_INTERVAL, timer);
        }
    }

    boolean isChartReady() {
        return mBounds != null;
    }

    private void endAutoScroll() {
        mAutoScrollDirection = SWT.NULL;
    }

    /**
     * Gets the X for a given date.
     * 
     * @param date Date
     * @return x position or -1 if date was not found
     */
    public int getXForDate(Date date) {
        checkWidget();
        Calendar cal = Calendar.getInstance(mDefaultLocale);
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

        if (mCurrentView == ISettings.VIEW_DAY) { return getStartingXForEventHours(cal); }

        // clone the "root" calendar (our leftmost date)
        Calendar temp = (Calendar) mCalendar.clone();

        int dw = getDayWidth();
        if (mCurrentView == ISettings.VIEW_YEAR) {
            // we draw years starting on the left for simplicity's sake
            temp.set(Calendar.DAY_OF_MONTH, 1);
        }

        long days = DateHelper.daysBetween(temp, cal, mDefaultLocale);

        int extra = 0;
        if (mSettings.drawEventsDownToTheHourAndMinute()) {
            if (mCurrentView != ISettings.VIEW_DAY) {
                float ppm = 60f / mDayWidth;
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
     * Gets the date where the current x position is.
     * 
     * @param x x location
     * @return Calendar of date
     */
    public Calendar getDateAt(int x) {

        int xPosition = x;

        checkWidget();
        Calendar temp = Calendar.getInstance(mDefaultLocale);
        temp.setTime(mCalendar.getTime());

        int dw = getDayWidth();
        if (mCurrentView == ISettings.VIEW_YEAR) temp.set(Calendar.DAY_OF_MONTH, 1);
        else if (mCurrentView == ISettings.VIEW_DAY) {

            // pixels per minute
            float ppm = 60f / dw;

            // total minutes from left side
            xPosition -= mBounds.x;
            int totalMinutes = (int) (xPosition * ppm);

            // set our temporary end calendar to the start date of the calendar
            Calendar fakeEnd = Calendar.getInstance(mDefaultLocale);
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

        // loop from left to right
        int xx = 0;
        while (true) {
            if (xx > xPosition) break;

            if (xx != 0) {
                temp.add(Calendar.DATE, 1);
            }

            xx += dw;
        }

        return temp;
    }

    private boolean isNoOverlap(Calendar dat1, Calendar dat2) {
        if (mCurrentView == ISettings.VIEW_DAY) {
            long minutes = DateHelper.minutesBetween(dat1.getTime(), dat2.getTime(), mDefaultLocale, false, false);
            return minutes >= 0;
        } else {
            long days = DateHelper.daysBetween(dat1, dat2, mDefaultLocale);
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
        Calendar cal = Calendar.getInstance(mDefaultLocale);
        cal.setTime(date);

        int x = getXForDate(cal);

        if (x == -1) return;

        GC gc = new GC(this);
        gc.setLineStyle(SWT.LINE_DOT);
        gc.drawRectangle(x, 0, x, mBounds.height);
        gc.setLineStyle(SWT.LINE_SOLID);
        gc.dispose();
    }

    /**
     * Handles the actual moving of an event.
     */
    private void handleMove(MouseEvent me, GanttEvent event, int type, boolean showToolTip) {
        if (!mSettings.enableDragAndDrop() && type == TYPE_MOVE) return;

        if (!mSettings.enableResizing() && (type == TYPE_RESIZE_LEFT || type == TYPE_RESIZE_RIGHT)) return;

        if (!mMouseIsDown) return;

        if (event == null) return;

        if (type == TYPE_MOVE && !event.isMoveable()) return;

        if ((type == TYPE_RESIZE_LEFT || type == TYPE_RESIZE_RIGHT) && !event.isResizable()) return;

        String dateFormat = (mCurrentView == ISettings.VIEW_DAY ? mSettings.getHourDateFormat() : mSettings.getDateFormat());

        Calendar mouseDateCal = getDateAt(me.x);

        // drag start event, show cross hair arrow cursor and keep going
        if ((me.stateMask & SWT.BUTTON1) != 0 && !mDragging && !mResizing) {
            if (type == TYPE_MOVE) {
                mDragging = true;
                mResizing = false;
                this.setCursor(CURSOR_SIZEALL);
            } else {
                mDragging = false;
                mResizing = true;
                this.setCursor(type == TYPE_RESIZE_LEFT ? CURSOR_SIZEW : CURSOR_SIZEE);
                mLastLeft = (type == TYPE_RESIZE_LEFT);
            }

            mDragEvents.clear();
            mDragEvents.add(event);
            mLastX = me.x;
            mDragStartDate = mouseDateCal;
            mJustStartedMoveOrResize = true;
        }

        // if we're dragging, with left mouse held down..
        if ((me.stateMask & SWT.BUTTON1) != 0 && (mDragging || mResizing)) {
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
            if (me.x > mLastX || me.x < mLastX) {
                int diff = 0;
                if (mCurrentView == ISettings.VIEW_DAY) {
                    Calendar cal = event.getActualStartDate();
                    if (mResizing && !mLastLeft) {
                        cal = event.getActualEndDate();
                    }

                    diff = DateHelper.minutesBetween(mouseDateCal.getTime(), cal.getTime(), mDefaultLocale, false, false);

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
                    if (mJustStartedMoveOrResize) {
                        if (mDragging) {
                            mInitialHoursDragOffset = diff;
                        }

                        mJustStartedMoveOrResize = false;
                        return;
                    } else {
                        if (mDragging) {
                            diff -= mInitialHoursDragOffset;
                        }
                    }

                    mJustStartedMoveOrResize = false;

                    // I need to figure out this weirdness at some point
                    diff = -(diff);
                } else {
                    if (me.x > mLastX) {
                        diff = (int) DateHelper.daysBetween(mDragStartDate, mouseDateCal, mDefaultLocale);
                    } else {
                        diff = (int) DateHelper.daysBetween(mouseDateCal, mDragStartDate, mDefaultLocale);
                    }

                    if (me.x < mLastX) {
                        diff = -diff;
                    }
                }

                mDragStartDate = mouseDateCal;

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
        mLastX = me.x;

        // TODO: On multi-drag events, one tooltip is a bit lame, can we do something cooler/nicer/more useful? (20 tooltips != useful, and it's slow)
        if (mSettings.showDateTips() && (mDragging || mResizing) && showToolTip) {
            Rectangle eBounds = new Rectangle(event.getX(), event.getY() - 25, event.getWidth(), event.getHeight());
            Point eventOnDisplay = toDisplay(me.x, eBounds.y);
            if (event.isCheckpoint()) {
                long days = DateHelper.daysBetween(event.getActualStartDate(), event.getActualEndDate(), mDefaultLocale);
                days++;

                if (days == 1) {
                    GanttDateTip.makeDialog(mColorManager, DateHelper.getDate(event.getActualStartDate(), dateFormat, mDefaultLocale), eventOnDisplay, mBounds.y);
                } else {
                    StringBuffer buf = new StringBuffer();
                    if (mCurrentView == ISettings.VIEW_D_DAY) {
                        buf.append(event.getActualDDayStart());
                        buf.append(" - ");
                        buf.append(event.getActualDDayEnd());
                        GanttDateTip.makeDialog(mColorManager, buf.toString(), eventOnDisplay, mBounds.y);
                    } else {
                        buf.append(DateHelper.getDate(event.getActualStartDate(), dateFormat, mDefaultLocale));
                        buf.append(" - ");
                        buf.append(DateHelper.getDate(event.getActualEndDate(), dateFormat, mDefaultLocale));
                        GanttDateTip.makeDialog(mColorManager, buf.toString(), eventOnDisplay, mBounds.y);
                    }
                }
            } else {
                if (mCurrentView == ISettings.VIEW_D_DAY) {
                    StringBuffer buf = new StringBuffer();
                    buf.append(event.getActualDDayStart());
                    buf.append(" - ");
                    buf.append(event.getActualDDayEnd());
                    buf.append("       ");
                    GanttDateTip.makeDialog(mColorManager, buf.toString(), eventOnDisplay, mBounds.y);
                } else {
                    StringBuffer buf = new StringBuffer();
                    buf.append(DateHelper.getDate(event.getActualStartDate(), dateFormat, mDefaultLocale));
                    buf.append(" - ");
                    buf.append(DateHelper.getDate(event.getActualEndDate(), dateFormat, mDefaultLocale));
                    GanttDateTip.makeDialog(mColorManager, buf.toString(), eventOnDisplay, mBounds.y);
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

        if (mCurrentView == ISettings.VIEW_DAY) {
            calMark = Calendar.MINUTE;
        }

        List toMove = new ArrayList();

        // multi move
        if ((stateMask & mSettings.getDragAllModifierKey()) != 0 && mSettings.moveLinkedEventsWhenEventsAreMoved()) {
            List conns = getEventsDependingOn(ge);

            List translated = new ArrayList();
            for (int x = 0; x < conns.size(); x++) {
                GanttEvent md = (GanttEvent) conns.get(x);

                if (md.isLocked()) {
                    continue;
                }

                // it's a checkpoint, we're resizing, and settings say checkpoints can't be resized, then we skip them even here
                if (md.isCheckpoint() && type != TYPE_MOVE && !mSettings.allowCheckpointResizing()) {
                    continue;
                }

                translated.add(md);
            }

            // add all multiselected events too, if any
            if (mMultiSelect) {
                for (int x = 0; x < mSelectedEvents.size(); x++) {
                    GanttEvent selEvent = (GanttEvent) mSelectedEvents.get(x);
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
        if (mSettings.moveAndResizeOnlyDependentEventsThatAreLaterThanLinkedMoveEvent()) {
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

        mDragEvents = new ArrayList(toMove);

        for (int x = 0; x < toMove.size(); x++) {
            GanttEvent event = (GanttEvent) toMove.get(x);

            Calendar cal1 = Calendar.getInstance(mDefaultLocale);
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
                        long millis = event.getNoMoveBeforeDate().getTimeInMillis();
                        long milliDiff = Math.abs(event.getActualStartDate().getTimeInMillis() - millis);
                        event.setRevisedStart((Calendar) event.getNoMoveBeforeDate().clone(), false);

                        // we also move the end date the same amount of time in difference between the no move date and the start date
                        Calendar end = (Calendar) event.getActualEndDate().clone();
                        end.add(Calendar.MILLISECOND, (int) -milliDiff);
                        event.setRevisedEnd(end, false);
                        event.updateX(getStartingXfor(event.getRevisedStart()));
                        //event.updateY(me.y);
                        event.moveStarted();
                        continue;
                    }
                }

                // remember, we check the start calendar on both occasions, as that's the mark that counts
                if (event.getNoMoveAfterDate() != null) {
                    if (cal2.after(event.getNoMoveAfterDate())) {
                        // same deal for end date as for start date
                        long millis = event.getNoMoveAfterDate().getTimeInMillis();
                        long milliDiff = Math.abs(event.getActualEndDate().getTimeInMillis() - millis);
                        event.setRevisedEnd((Calendar) event.getNoMoveAfterDate().clone(), false);

                        // we also move the end date the same amount of time in difference between the no move date and the start date
                        Calendar start = (Calendar) event.getActualStartDate().clone();
                        start.add(Calendar.MILLISECOND, (int) milliDiff);
                        event.setRevisedStart(start, false);
                        event.updateX(getStartingXfor(event.getRevisedStart()));
                        event.moveStarted();
                        continue;
                    }
                }

                event.moveStarted();

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

                // check before date move as it's the start date
                if (event.getNoMoveBeforeDate() != null) {
                    if (cal1.before(event.getNoMoveBeforeDate())) {
                        // for start dates we need to actually set the date or we'll be 1 day short as we did the diff already
                        event.setRevisedStart(event.getNoMoveBeforeDate());
                        continue;
                    }
                }

                event.moveStarted();

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

                event.moveStarted();

                event.setRevisedEnd(cal2, false);

                // update size + location
                event.updateX(getStartingXfor(cal1));
                event.updateWidth(getXLengthForEvent(event));
            }

            if (!eventsMoved.contains(event)) eventsMoved.add(event);
        }

        if (!eventsMoved.contains(ge)) eventsMoved.add(ge);

        for (int x = 0; x < mEventListeners.size(); x++) {
            IGanttEventListener listener = (IGanttEventListener) mEventListeners.get(x);
            if (type == TYPE_MOVE) listener.eventsMoved(eventsMoved, me);
            else listener.eventsResized(eventsMoved, me);
        }

        // TODO: Granualize, if possible, quite hard with arrow-connections
        redrawEventsArea();
    }

    public void mouseEnter(MouseEvent event) {
    }

    public void mouseExit(MouseEvent event) {
    }

    public void mouseHover(MouseEvent me) {
        if (mDragging || mResizing) { return; }

        // right now all we do here is tooltips
        if (!mSettings.showToolTips()) return;

        // possible fix for mac's over-and-over display of hover tip
        if (GanttToolTip.isActive()) return;

        if (me.stateMask != 0) return;

        for (int i = 0; i < mGanttEvents.size(); i++) {
            GanttEvent event = (GanttEvent) mGanttEvents.get(i);
            if (isInside(me.x, me.y, new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight()))) {
                showTooltip(event, me);
                return;
            }
        }
    }

    private void showTooltip(GanttEvent event, MouseEvent me) {
        if (!mSettings.showToolTips()) return;

        long days = DateHelper.daysBetween(event.getStartDate(), event.getEndDate(), mDefaultLocale);
        days++;
        long revisedDays = 0;

        boolean dDay = mCurrentView == ISettings.VIEW_D_DAY;
        if (dDay) {
            days = event.getDDateRange();
        }

        // as the dialog is slightly bigger in many aspects, push it slightly
        // off to the right
        int xPlus = 0;
        if (isUseAdvancedTooltips() || event.getAdvancedTooltip() != null) xPlus = mSettings.getAdvancedTooltipXOffset();

        Point displayLocation = super.toDisplay(new Point(me.x + xPlus, me.y));

        String dateFormat = (mCurrentView == ISettings.VIEW_DAY ? mSettings.getHourDateFormat() : mSettings.getDateFormat());

        String startDate = DateHelper.getDate(event.getStartDate(), dateFormat, mDefaultLocale);
        String endDate = DateHelper.getDate(event.getEndDate(), dateFormat, mDefaultLocale);

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
            if (event.getDDayRevisedStart() == Integer.MAX_VALUE) revisedStart = null;
            if (event.getDDayRevisedEnd() == Integer.MAX_VALUE) revisedEnd = null;
        }

        if (revisedStart != null) {
            extra.append(mLanguageManager.getRevisedText());
            extra.append(": ");
            revisedStartText = dDay ? "" + event.getDDayRevisedStart() : DateHelper.getDate(revisedStart, dateFormat, mDefaultLocale);
            extra.append(revisedStartText);
        }

        if (revisedEnd != null) {
            if (revisedStart == null) {
                revisedStart = event.getActualStartDate();
            }

            revisedEndText = dDay ? "" + event.getDDayRevisedEnd() : DateHelper.getDate(revisedEnd, dateFormat, mDefaultLocale);
            revisedDays = DateHelper.daysBetween(revisedStart, revisedEnd, mDefaultLocale);
            revisedDays++;
            extra.append(" - ");
            extra.append(revisedEndText);
            extra.append(" (");
            if (dDay) {
                revisedDays = event.getRevisedDDateRange();
            }
            extra.append(revisedDays);
            extra.append(" ");
            if (revisedDays == 1 || revisedDays == -1) extra.append(mLanguageManager.getDaysText());
            else extra.append(mLanguageManager.getDaysPluralText());
            extra.append(")");
        }

        AdvancedTooltip at = event.getAdvancedTooltip();
        if (at == null && isUseAdvancedTooltips()) {
            String ttText = mSettings.getDefaultAdvancedTooltipTextExtended();
            if (event.isCheckpoint() || event.isImage() || event.isScope() || (revisedStartText == null && revisedEnd == null)) ttText = mSettings.getDefaultAdvancedTooltipText();

            at = new AdvancedTooltip(mSettings.getDefaultAdvancedTooltipTitle(), ttText, mSettings.getDefaultAdvandedTooltipImage(), mSettings.getDefaultAdvandedTooltipHelpImage(), mSettings.getDefaultAdvancedTooltipHelpText());
        }

        if (at != null) {
            String title = fixTooltipString(at.getTitle(), event.getName(), startDate, endDate, revisedStartText, revisedEndText, days, revisedDays, event.getPercentComplete());
            String content = fixTooltipString(at.getContent(), event.getName(), startDate, endDate, revisedStartText, revisedEndText, days, revisedDays, event.getPercentComplete());
            String help = fixTooltipString(at.getHelpText(), event.getName(), startDate, endDate, revisedStartText, revisedEndText, days, revisedDays, event.getPercentComplete());

            AdvancedTooltipDialog.makeDialog(at, mColorManager, displayLocation, title, content, help);
        } else {
            if (extra.length() > 0) {
                StringBuffer buf = new StringBuffer();
                buf.append(mLanguageManager.getPlannedText());
                buf.append(": ");
                buf.append(dDay ? "" + dDayStart : startDate);
                buf.append(" - ");
                buf.append(dDay ? "" + dDayEnd : endDate);
                buf.append(" (");
                buf.append(days);
                buf.append(" ");
                buf.append((days == 1 || days == -1) ? mLanguageManager.getDaysText() : mLanguageManager.getDaysPluralText());
                buf.append(")");

                GanttToolTip.makeDialog(mColorManager, event.getName(), extra.toString(), buf.toString(), event.getPercentComplete() + mLanguageManager.getPercentCompleteText(), displayLocation);
            } else {
                StringBuffer buf = new StringBuffer();
                buf.append(dDay ? "" + dDayStart : startDate);
                buf.append(" - ");
                buf.append(dDay ? "" + dDayEnd : endDate);
                buf.append(" (");
                buf.append(days);
                buf.append(" ");
                buf.append((days == 1 || days == -1) ? mLanguageManager.getDaysText() : mLanguageManager.getDaysPluralText());
                buf.append(")");

                GanttToolTip.makeDialog(mColorManager, event.getName(), buf.toString(), event.getPercentComplete() + mLanguageManager.getPercentCompleteText(), displayLocation);
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
        return mVisibleBounds;
    }

    /**
     * Returns the current view.
     * 
     * @return View
     */
    public int getCurrentView() {
        return mCurrentView;
    }

    /**
     * Sets the current view.
     * 
     * @param view View
     */
    public void setView(int view) {
        mCurrentView = view;
        // do stuff here
        redraw();
    }

    /**
     * Returns all events.
     * 
     * @return List of all events.
     */
    public List getEvents() {
        return mGanttEvents;
    }

    /**
     * Returns the current visible area of the chart as an image
     * 
     * @return Image of the chart
     */
    public Image getImage() {
        return getImage(mBounds);
    }

    /**
     * Returns the image that is the entire chart, regardless of what is currently visible. If chart contains no events,
     * getImage() is called from within.
     * <p>
     * Do note that if the chart is "huge", you may need to increase your heap size.
     * 
     * @return Image
     */
    public Image getFullImage() {
        checkWidget();

        // we need to pretend that we are at scroll position 0 along with that our bounds are as big as all visible events,
        // thus we save old values before so we can reset them at the end
        mSavingChartImage = true;
        int oldVscroll = mVerticalScrollPosition;
        mVerticalScrollPosition = 0;
        moveYBounds(-oldVscroll);
        Rectangle oldBounds = mBounds;
        try {
            Rectangle fullBounds = new Rectangle(0, 0, 0, 0);
            GanttEvent geRight = getEvent(false);

            if (geRight == null) { return getImage(); }

            // remember, we need the bounds here, not the location bounds, just the sizes, the rest will start filling at the right location
            fullBounds.width = fullBounds.x + geRight.getActualBounds().x + geRight.getActualBounds().width;
            fullBounds.height = mBottomMostY;

            // set chart bounds to be the fake bounds
            mBounds = fullBounds;

            // forcing a full update or event visibilities will not change
            forceFullUpdate();

            //System.err.println(fullBounds);

            boolean drawSections = (mGanttSections.size() > 0);
            if (mSettings.getSectionSide() == SWT.RIGHT) {
                fullBounds.width += mSettings.getSectionBarWidth();
            }

            Image buffer = new Image(Display.getDefault(), fullBounds);

            GC gc2 = new GC(buffer);
            drawChartOntoGC(gc2, fullBounds);
            drawHeader(gc2);

            if (drawSections && mSettings.getSectionSide() == SWT.RIGHT) {
                drawSectionColumn(gc2, fullBounds, false, true, false);
            }

            gc2.dispose();

            return buffer;
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            // reset everything, including forcing a redraw and reset
            mVerticalScrollPosition = oldVscroll;
            moveYBounds(mVerticalScrollPosition);
            mSavingChartImage = false;
            mBounds = oldBounds;
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    forceFullUpdate();
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
        mSavingChartImage = true;
        int oldVscroll = mVerticalScrollPosition;
        mVerticalScrollPosition = 0;
        try {
            Image buffer = new Image(Display.getDefault(), bounds);

            GC gc2 = new GC(buffer);
            drawChartOntoGC(gc2, null);
            gc2.dispose();
            return buffer;
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            mSavingChartImage = false;
            mVerticalScrollPosition = oldVscroll;
        }

        return null;
    }

    private List getEventsDependingOn(GanttEvent ge) {
        if (mGanttConnections.size() == 0) return new ArrayList();

        GanttMap gm = new GanttMap();

        for (int i = 0; i < mGanttConnections.size(); i++) {
            GanttConnection conn = (GanttConnection) mGanttConnections.get(i);
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
        int originalDayWidth = mSettings.getDayWidth();
        int originalMonthWeekWidth = mSettings.getMonthDayWidth();
        int originalYearMonthDayWidth = mSettings.getYearMonthDayWidth();

        boolean dDay = false;
        if (mCurrentView == ISettings.VIEW_D_DAY) {
            dDay = true;
        }

        switch (mZoomLevel) {
            // hour
            case ISettings.ZOOM_HOURS_MAX:
                mCurrentView = ISettings.VIEW_DAY;
                mDayWidth = originalDayWidth * 6;
                break;
            case ISettings.ZOOM_HOURS_MEDIUM:
                mCurrentView = ISettings.VIEW_DAY;
                mDayWidth = originalDayWidth * 4;
                break;
            case ISettings.ZOOM_HOURS_NORMAL:
                mCurrentView = ISettings.VIEW_DAY;
                mDayWidth = originalDayWidth * 2;
                break;
            // mDay
            case ISettings.ZOOM_DAY_MAX:
                mCurrentView = ISettings.VIEW_WEEK;
                mDayWidth = originalDayWidth * 3;
                break;
            case ISettings.ZOOM_DAY_MEDIUM:
                mCurrentView = ISettings.VIEW_WEEK;
                mDayWidth = originalDayWidth * 2;
                break;
            case ISettings.ZOOM_DAY_NORMAL:
                mCurrentView = ISettings.VIEW_WEEK;
                mDayWidth = originalDayWidth;
                break;
            case ISettings.ZOOM_MONTH_MAX:
                mCurrentView = ISettings.VIEW_MONTH;
                mMonthDayWidth = originalMonthWeekWidth + 6;
                break;
            case ISettings.ZOOM_MONTH_MEDIUM:
                mCurrentView = ISettings.VIEW_MONTH;
                mMonthDayWidth = originalMonthWeekWidth + 3;
                break;
            case ISettings.ZOOM_MONTH_NORMAL:
                mCurrentView = ISettings.VIEW_MONTH;
                mMonthDayWidth = originalMonthWeekWidth;
                break;
            case ISettings.ZOOM_YEAR_MAX:
                mCurrentView = ISettings.VIEW_YEAR;
                mYearDayWidth = originalYearMonthDayWidth + 3;
                break;
            case ISettings.ZOOM_YEAR_MEDIUM:
                mCurrentView = ISettings.VIEW_YEAR;
                mYearDayWidth = originalYearMonthDayWidth + 2;
                break;
            case ISettings.ZOOM_YEAR_NORMAL:
                mCurrentView = ISettings.VIEW_YEAR;
                mYearDayWidth = originalYearMonthDayWidth;
                break;
            case ISettings.ZOOM_YEAR_SMALL:
                mCurrentView = ISettings.VIEW_YEAR;
                mYearDayWidth = originalYearMonthDayWidth - 1;
                break;
            case ISettings.ZOOM_YEAR_VERY_SMALL:
                mCurrentView = ISettings.VIEW_YEAR;
                mYearDayWidth = originalYearMonthDayWidth - 2;
                break;
        }

        mWeekWidth = mDayWidth * 7;
        mMonthWeekWidth = mMonthDayWidth * 7;

        //mYearMonthWeekWidth = mYearDayWidth * 7;
        //mYearMonthWidth = mYearMonthWeekWidth * 4;

        // not a hack, we just re-use the same parameter name but for a
        // different purpose than the name itself, not exactly great logic
        // but it saves some recoding
        if (mZoomLevel == ISettings.ZOOM_HOURS_MAX || mZoomLevel == ISettings.ZOOM_HOURS_MEDIUM || mZoomLevel == ISettings.ZOOM_HOURS_NORMAL) {
            // how many hours are there really in our work day? we don't show
            // anything else!
            mWeekWidth = mDayWidth * 24;
        }

        if (dDay) {
            mCurrentView = ISettings.VIEW_D_DAY;
        }
    }

    /**
     * This will cause a full recaclulation of events and a lot of other things. Normally this is used internally when
     * there are zoom changes and/or other events that cause the chart to need a full recalculation. It is <b>NOT</b>
     * intended to be used outside of the chart, but is available as a workaround if there is a bug that you can't get
     * around and you need to force a full update. See this method as a temporary solution if you need to use it.
     */
    public void heavyRedraw() {
        mZoomLevelChanged = true;
        forceFullUpdate();
    }

    void forceFullUpdate() {
        flagForceFullUpdate();
        redraw();
    }

    void flagForceFullUpdate() {
        mReCalculateScopes = true;
        mReCalculateSectionBounds = true;

        for (int i = 0; i < mGanttEvents.size(); i++) {
            ((GanttEvent) mGanttEvents.get(i)).setBoundsSet(false);
        }

        mForceScrollbarsUpdate = true;
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

        for (int i = 0; i < mGanttEvents.size(); i++) {
            GanttEvent ge = (GanttEvent) mGanttEvents.get(i);
            Rectangle actualBounds = ge.getActualBounds();

            if (left) {
                if (actualBounds.x < max) {
                    max = actualBounds.x;
                }
            } else {
                if (actualBounds.x + actualBounds.width > max) {
                    max = actualBounds.x + actualBounds.width;
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
        if (level == mZoomLevel) return;

        int toSet = level;

        if (toSet < ISettings.MIN_ZOOM_LEVEL) toSet = ISettings.MIN_ZOOM_LEVEL;
        if (toSet > ISettings.MAX_ZOOM_LEVEL) toSet = ISettings.MAX_ZOOM_LEVEL;

        int oldZoomLevel = mZoomLevel;
        mZoomLevel = toSet;

        updateZoomLevel();
        mZoomLevelChanged = true;

        forceFullUpdate();

        for (int i = 0; i < mEventListeners.size(); i++) {
            IGanttEventListener listener = (IGanttEventListener) mEventListeners.get(i);
            if (toSet < oldZoomLevel) listener.zoomedOut(mZoomLevel);
            else listener.zoomedIn(mZoomLevel);
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
        if (!mSettings.enableZooming()) return;
        /*
        if (mCurrentView == ISettings.VIEW_D_DAY)
        	return;*/

        mZoomLevel--;
        if (mZoomLevel < ISettings.MIN_ZOOM_LEVEL) {
            mZoomLevel = ISettings.MIN_ZOOM_LEVEL;
            return;
        }

        Calendar preZoom = null;
        if (fromMouseWheel && mouseLoc != null) preZoom = getDateAt(mouseLoc.x);

        updateZoomLevel();

        if (fromMouseWheel && mSettings.zoomToMousePointerDateOnWheelZooming() && mouseLoc != null) internalSetDateAtX(mouseLoc.x, preZoom, true, false, true);

        mZoomLevelChanged = true;

        forceFullUpdate();

        for (int i = 0; i < mEventListeners.size(); i++) {
            IGanttEventListener listener = (IGanttEventListener) mEventListeners.get(i);
            listener.zoomedIn(mZoomLevel);
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
        if (!mSettings.enableZooming()) return;

        mZoomLevel++;
        if (mZoomLevel > ISettings.MAX_ZOOM_LEVEL) {
            mZoomLevel = ISettings.MAX_ZOOM_LEVEL;
            return;
        }

        if (mCurrentView == ISettings.VIEW_D_DAY) {
            if (mZoomLevel > ISettings.ZOOM_DAY_NORMAL) {
                mZoomLevel = ISettings.ZOOM_DAY_NORMAL;
                return;
            }
        }

        Calendar preZoom = null;
        if (fromMouseWheel && mouseLoc != null) preZoom = getDateAt(mouseLoc.x);

        updateZoomLevel();

        if (fromMouseWheel && mSettings.zoomToMousePointerDateOnWheelZooming() && mouseLoc != null) internalSetDateAtX(mouseLoc.x, preZoom, true, false, false);

        mZoomLevelChanged = true;
        forceFullUpdate();

        for (int i = 0; i < mEventListeners.size(); i++) {
            IGanttEventListener listener = (IGanttEventListener) mEventListeners.get(i);
            listener.zoomedOut(mZoomLevel);
        }
    }

    /**
     * Resets the zoom level to that set in the settings.
     */
    public void resetZoom() {
        checkWidget();
        if (!mSettings.enableZooming()) return;

        mZoomLevel = mSettings.getInitialZoomLevel();

        updateZoomLevel();

        mZoomLevelChanged = true;
        forceFullUpdate();

        for (int i = 0; i < mEventListeners.size(); i++) {
            IGanttEventListener listener = (IGanttEventListener) mEventListeners.get(i);
            listener.zoomReset();
        }
    }

    /**
     * Adds a listener that will be notified of Gantt events.
     * 
     * @param listener Listener
     */
    public void addGanttEventListener(IGanttEventListener listener) {
        checkWidget();
        if (mEventListeners.contains(listener)) return;

        mEventListeners.add(listener);
    }

    /**
     * Removes a listener from being notified of Gantt events.
     * 
     * @param listener Listener
     */
    public void removeGanttEventListener(IGanttEventListener listener) {
        checkWidget();
        mEventListeners.remove(listener);
    }

    private boolean isUseAdvancedTooltips() {
        return mUseAdvancedTooltips;
    }

    /**
     * Sets whether to use advanced tooltips or not. This method will override the settings implementation with the same
     * name.
     * 
     * @param useAdvancedTooltips true whether to use advanced tooltips.
     */
    public void setUseAdvancedTooltips(boolean useAdvancedTooltips) {
        mUseAdvancedTooltips = useAdvancedTooltips;
    }

    public Calendar getDDayCalendar() {
        return (Calendar) mDDayCalendar.clone();
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

}
