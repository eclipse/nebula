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
 *    ziogiannigmail.com - Bug 464509 - Minute View Implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.ganttchart;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.prefs.Preferences;

import org.eclipse.nebula.widgets.ganttchart.themes.ColorThemeGrayBlue;
import org.eclipse.nebula.widgets.ganttchart.themes.ColorThemeHighContrastBlack;
import org.eclipse.nebula.widgets.ganttchart.themes.ColorThemeSilver;
import org.eclipse.nebula.widgets.ganttchart.themes.ColorThemeWindowsBlue;
import org.eclipse.nebula.widgets.ganttchart.undoredo.UndoRedoListenerAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class GanttTester {

    private GanttChart          _ganttChart;
    private GanttComposite      _ganttComposite;

    private final ViewForm      _vfChart;

    private Text                _timerText;

    private Button              _bUseSections;
    private Button              _bUseSectionsRight;
    private Button              _bUseSectionsLeft;
    private Button              _bDrawHorizontalLines;
    private Button              _bIncreaseDates;
    private Button              _bCreate;
    private Button              _bCreatePlannedDates;
    private Button              _bShowHolidays;
    private Button              _bUndo;
    private Button              _bRedo;
    private Button              _bRandomEventLength;
    private Button              _bConnectEvents;
    private Button              _bRandomColors;
    private Button              _bRandomEventColors;
    private Button              _bDNDLimits;
    private Button              _bRandomPercentCompletes;
    private Button              _bGanttPhases;
    private Button              _bSpecialDateRange;
    private Button              _bRandomRowHeights;
    private Button              _bRandomEventVLoc;
    private Button              _bRandomEventTextHLocation;
    private Button              _bRandomEventTextVLocation;
    private Button              _bUseDDay;
    private Button              _bLockHeader;
    private Button              _bMoveOnlyLaterLinkedEvents;
    private Button              _bClear;
    private Button              _bRedraw;
    private Button              _bHeavyRedraw;
    private Button              _bSaveFull;
    private Button				_bRandomImage;

    private Combo               _vDNDCombo;
    private Combo               _eventCountCombo;
    private Combo               _themeCombo;
    private Combo               _scrollCombo;
    private Combo               _selCombo;
    private Combo               _localeCombo;

    private Spinner             _sMaxSections;
    private Spinner             _sConnectionCountNumber;

    // advanced tab
    private Button              _bEnableAutoScroll;
    private Button              _bEventResizing;
    private Button              _bEventDND;
    private Button              _bAdjustForLetters;
    private Combo               _bConnectionLineStyle;
    private Button              _bShowArrows;
    private Button              _bShowBoldScopeText;
    private Button              _bShowGradientEventBars;
    private Button              _bShowOnlyDependenciesForSelectedItems;
    private Button              _bShowTooltips;
    private Button              _bShowAdvancedTooltips;
    private Button              _bEnableZooming;
    private Button              _bShowZoomLevelBox;
    private Button              _bAllowBlankAreaDragAndDropToMoveDates;
    private Button				_bAllowVerticalBlankDnd;
    private Button              _bFlipBlankAreaDragDirection;
    private Button              _bDrawSelectionMarkerAroundSelectedEvent;
    private Button              _bAllowCheckpointResizing;
    private Button              _bStartCalendarOnFirstDayOfWeek;
    private Button              _bDrawFullPercentageBar;
    private Button              _bDrawLockedDateMarks;
    private Button              _bShowDateTipsOnScrolling;
    private Button              _bZoomToMousePointerDateOnWheelZooming;
    private Button              _bScaleImageToDay;
    private Button				_bAllowArrowKeysToMoveChart;
    private Button				_bCreateSpecialRangesWithAllowNoEvents;
    
    private Table                _tEventLog;

    private Listener            _undoRedoListener;

    private Preferences         _prefs = Preferences.systemNodeForPackage(GanttTester.class);
    private static final String KEY    = "prefKey";

    /**
     * @param args
     */
    public static void main(final String[] args) {
        new GanttTester();
    }

    public GanttTester() {
        final Display display = Display.getDefault(); //new Display();
        final Monitor m = display.getMonitors()[0];
        final Shell shell = new Shell(display);
        shell.setText("GanttChart Test Application");
        shell.setLayout(new FillLayout());

        final SashForm sfVSplit = new SashForm(shell, SWT.VERTICAL);
        final SashForm sfHSplit = new SashForm(sfVSplit, SWT.HORIZONTAL);

        final ViewForm vfBottom = new ViewForm(sfVSplit, SWT.NONE);
        _vfChart = new ViewForm(sfHSplit, SWT.NONE);
        final ViewForm rightForm = new ViewForm(sfHSplit, SWT.NONE);

        final ScrolledComposite sc = new ScrolledComposite(rightForm, SWT.V_SCROLL | SWT.H_SCROLL);
        rightForm.setContent(sc);
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);
        sc.getVerticalBar().setPageIncrement(150);

        final Composite rightComposite = new Composite(sc, SWT.NONE);
        final GridLayout gl = new GridLayout();
        gl.marginLeft = 0;
        gl.marginTop = 0;
        gl.horizontalSpacing = 0;
        gl.verticalSpacing = 0;
        gl.marginBottom = 0;
        gl.marginHeight = 0;
        gl.marginWidth = 0;
        rightComposite.setLayout(gl);

        sc.setContent(rightComposite);

        rightComposite.addListener(SWT.Resize, new Listener() {

            public void handleEvent(final Event event) {
                sc.setMinSize(rightComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            }

        });

        sfVSplit.setWeights(new int[] { 91, 9 });
        sfHSplit.setWeights(new int[] { 70, 30 });

        // top left side
        _ganttChart = new GanttChart(_vfChart, SWT.MULTI);
        _vfChart.setContent(_ganttChart);
        _ganttComposite = _ganttChart.getGanttComposite();

        final TabFolder tfRight = new TabFolder(rightComposite, SWT.BORDER);
        final TabItem tiGeneral = new TabItem(tfRight, SWT.NONE);
        tiGeneral.setText("Creation");

        final TabItem tiAdvanced = new TabItem(tfRight, SWT.NONE);
        tiAdvanced.setText("Advanced");

        final TabItem tiEventLog = new TabItem(tfRight, SWT.NONE);
        tiEventLog.setText("Event Log");

        final Composite bottom = new Composite(rightComposite, SWT.NONE);
        bottom.setLayout(new GridLayout());
        createCreateButtons(bottom);

        vfBottom.setContent(createBottom(vfBottom));
        tiGeneral.setControl(createCreationTab(tfRight)); // NOPMD
        tiAdvanced.setControl(createAdvancedTab(tfRight));
        tiEventLog.setControl(createEventLogTab(tfRight));

        shell.setMaximized(true);
        // uncomment to put on right-hand-side monitor
        shell.setLocation(new Point(m.getClientArea().x, 0));

        sc.setMinSize(rightComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        shell.open();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.removeListener(SWT.KeyDown, _undoRedoListener);

        shell.dispose();
    }

    private Composite createEventLogTab(final Composite parent) {
        final ScrolledComposite sc = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);
        sc.getHorizontalBar().setPageIncrement(100);
        sc.getVerticalBar().setPageIncrement(100);

        final Composite comp = new Composite(sc, SWT.NONE);
        sc.setContent(comp);
        comp.setLayout(new GridLayout(1, true));

        sc.addListener(SWT.Resize, new Listener() {

            public void handleEvent(final Event event) {
                sc.setMinSize(comp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            }

        });

        final Group group = new Group(comp, SWT.NONE);
        group.setText("Event Log");
        group.setLayout(new FillLayout());
        group.setLayoutData(new GridData(GridData.FILL_BOTH));

        _tEventLog = new Table(group, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
        _tEventLog.setBackground(ColorCache.getWhite());
        
        Menu m = new Menu(_tEventLog);
        MenuItem mClear = new MenuItem(m, SWT.NONE);
        mClear.setText("Clear Log");
        mClear.addListener(SWT.Selection, new Listener() {

            public void handleEvent(Event event) {
                _tEventLog.removeAll();
            }
            
        });
        
        _tEventLog.setMenu(m);
        

        return sc;
    }

    private Composite createAdvancedTab(final Composite parent) {
        final ScrolledComposite sc = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);
        sc.getHorizontalBar().setPageIncrement(100);
        sc.getVerticalBar().setPageIncrement(100);

        final Composite comp = new Composite(sc, SWT.NONE);
        sc.setContent(comp);
        comp.setLayout(new GridLayout(1, true));

        sc.addListener(SWT.Resize, new Listener() {

            public void handleEvent(final Event event) {
                sc.setMinSize(comp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            }

        });

        final Group group = new Group(comp, SWT.NONE);
        group.setText("Advanced");
        group.setLayout(new GridLayout(2, true));
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        GridData oneRow = new GridData(GridData.FILL_HORIZONTAL);
        oneRow.horizontalSpan = 2;
        oneRow.grabExcessHorizontalSpace = true;

        _bEnableAutoScroll = new Button(group, SWT.CHECK);
        _bEnableAutoScroll.setText("Enable Auto Scroll");
        _bEnableAutoScroll.setToolTipText("Causes chart to auto scroll when using DND. On by default.");
        _bEnableAutoScroll.setSelection(true);
        _bEnableAutoScroll.setLayoutData(oneRow);
        _bEnableAutoScroll.setData(KEY, "enableAutoScroll");
        prefLoad(_bEnableAutoScroll);
        prefHook(_bEnableAutoScroll);

        _bEventResizing = new Button(group, SWT.CHECK);
        _bEventResizing.setText("Allow Event Resizing");
        _bEventResizing.setToolTipText("Allows for resizing of events. On by default.");
        _bEventResizing.setSelection(true);
        _bEventResizing.setLayoutData(oneRow);
        _bEventResizing.setData(KEY, "bEventResizing");
        prefLoad(_bEventResizing);
        prefHook(_bEventResizing);

        _bEventDND = new Button(group, SWT.CHECK);
        _bEventDND.setText("Allow Event Drag n Drop (DND)");
        _bEventDND.setToolTipText("Allows for dragging of events. On by default.");
        _bEventDND.setSelection(true);
        _bEventDND.setLayoutData(oneRow);
        _bEventDND.setData(KEY, "bEventDND");
        prefLoad(_bEventDND);
        prefHook(_bEventDND);

        _bAdjustForLetters = new Button(group, SWT.CHECK);
        _bAdjustForLetters.setText("Adjust Spacing For Letters");
        _bAdjustForLetters.setToolTipText("When letters are drawn anywhere in the header they are auto-adjusted to fit in nicely. On by default.");
        _bAdjustForLetters.setSelection(true);
        _bAdjustForLetters.setLayoutData(oneRow);
        _bAdjustForLetters.setData(KEY, "bAdjustForLetters");
        prefLoad(_bAdjustForLetters);
        prefHook(_bAdjustForLetters);

        final Label lConn = new Label(group, SWT.NONE);
        lConn.setText("Connecting Line Style");

        _bConnectionLineStyle = new Combo(group, SWT.READ_ONLY);
        _bConnectionLineStyle.add("Right to Left");
        _bConnectionLineStyle.add("Right to Top");
        _bConnectionLineStyle.add("MS Project Style");
        _bConnectionLineStyle.add("Birds Path");
        _bConnectionLineStyle.select(2);
        _bConnectionLineStyle.setData(KEY, "bConnectionLineStyle");
        _bConnectionLineStyle.setToolTipText("How the connecting line is drawn. Default is MS Project Style");
        prefLoad(_bConnectionLineStyle);
        prefHook(_bConnectionLineStyle);

        _bShowArrows = new Button(group, SWT.CHECK);
        _bShowArrows.setText("Show Arrows");
        _bShowArrows.setToolTipText("Arrowheads on/off. Default is on.");
        _bShowArrows.setSelection(true);
        _bShowArrows.setLayoutData(oneRow);
        _bShowArrows.setData(KEY, "bShowArrows");
        prefLoad(_bShowArrows);
        prefHook(_bShowArrows);

        _bShowBoldScopeText = new Button(group, SWT.CHECK);
        _bShowBoldScopeText.setText("Show Bold Scope Text");
        _bShowBoldScopeText.setToolTipText("Scope Text is Bold. Default is on.");
        _bShowBoldScopeText.setSelection(true);
        _bShowBoldScopeText.setLayoutData(oneRow);
        _bShowBoldScopeText.setData(KEY, "bShowBoldScopeText");
        prefLoad(_bShowBoldScopeText);
        prefHook(_bShowBoldScopeText);

        _bShowGradientEventBars = new Button(group, SWT.CHECK);
        _bShowGradientEventBars.setText("Show Gradient Event Bars");
        _bShowGradientEventBars.setToolTipText("Gradient Events On/Off. Default is on. (If off only 1 color is used)");
        _bShowGradientEventBars.setSelection(true);
        _bShowGradientEventBars.setLayoutData(oneRow);
        _bShowGradientEventBars.setData(KEY, "bShowGradientEventBars");
        prefLoad(_bShowGradientEventBars);
        prefHook(_bShowGradientEventBars);

        _bShowOnlyDependenciesForSelectedItems = new Button(group, SWT.CHECK);
        _bShowOnlyDependenciesForSelectedItems.setText("Show Only Dependencies for Selected Items");
        _bShowOnlyDependenciesForSelectedItems.setToolTipText("Shows only dependency arrows when items are selected and the selected event has connections/dependencies to other events. Default is off.");
        _bShowOnlyDependenciesForSelectedItems.setSelection(false);
        _bShowOnlyDependenciesForSelectedItems.setLayoutData(oneRow);
        _bShowOnlyDependenciesForSelectedItems.setData(KEY, "bShowOnlyDependenciesForSelectedItems");
        prefLoad(_bShowOnlyDependenciesForSelectedItems);
        prefHook(_bShowOnlyDependenciesForSelectedItems);

        _bShowTooltips = new Button(group, SWT.CHECK);
        _bShowTooltips.setText("Show Tooltips");
        _bShowTooltips.setToolTipText("Tooltips on/off. Default is on.");
        _bShowTooltips.setSelection(true);
        _bShowTooltips.setLayoutData(oneRow);
        _bShowTooltips.setData(KEY, "bShowTooltips");
        prefLoad(_bShowTooltips);
        prefHook(_bShowTooltips);

        _bShowAdvancedTooltips = new Button(group, SWT.CHECK);
        _bShowAdvancedTooltips.setText("Use Advanced Tooltips");
        _bShowAdvancedTooltips.setToolTipText("Advanced Tooltips on/off. Default is on.");
        _bShowAdvancedTooltips.setSelection(true);
        _bShowAdvancedTooltips.setLayoutData(oneRow);
        _bShowAdvancedTooltips.setData(KEY, "bShowAdvancedTooltips");
        prefLoad(_bShowAdvancedTooltips);
        prefHook(_bShowAdvancedTooltips);

        _bEnableZooming = new Button(group, SWT.CHECK);
        _bEnableZooming.setText("Enable Zooming");
        _bEnableZooming.setToolTipText("Whether chart can be zoomed / zoomed out. Default is on.");
        _bEnableZooming.setSelection(true);
        _bEnableZooming.setLayoutData(oneRow);
        _bEnableZooming.setData(KEY, "bEnableZooming");
        prefLoad(_bEnableZooming);
        prefHook(_bEnableZooming);

        _bShowZoomLevelBox = new Button(group, SWT.CHECK);
        _bShowZoomLevelBox.setText("Show Zoom-Level Box When Zooming");
        _bShowZoomLevelBox.setToolTipText("Box that shows what zoom level is currently being zoomed to on/off. Default is on.");
        _bShowZoomLevelBox.setSelection(true);
        _bShowZoomLevelBox.setLayoutData(oneRow);
        _bShowZoomLevelBox.setData(KEY, "bShowZoomLevelBox");
        prefLoad(_bShowZoomLevelBox);
        prefHook(_bShowZoomLevelBox);

        _bZoomToMousePointerDateOnWheelZooming = new Button(group, SWT.CHECK);
        _bZoomToMousePointerDateOnWheelZooming.setText("Zoom In/Out To Mouse Pointer");
        _bZoomToMousePointerDateOnWheelZooming.setToolTipText("When zooming the chart will try to keep the focus on the date where the mouse pointer is when the zoom happens. Default is on.");
        _bZoomToMousePointerDateOnWheelZooming.setSelection(true);
        _bZoomToMousePointerDateOnWheelZooming.setLayoutData(oneRow);
        _bZoomToMousePointerDateOnWheelZooming.setData(KEY, "bZoomToMousePointerDateOnWheelZooming");
        prefLoad(_bZoomToMousePointerDateOnWheelZooming);
        prefHook(_bZoomToMousePointerDateOnWheelZooming);

        _bAllowBlankAreaDragAndDropToMoveDates = new Button(group, SWT.CHECK);
        _bAllowBlankAreaDragAndDropToMoveDates.setText("Allow Horizontal Blank Area DND to Move Chart");
        _bAllowBlankAreaDragAndDropToMoveDates.setToolTipText("When you grab a blank area of the chart and drag it, the chart will move in the drag direction horizontally. Default is on.");
        _bAllowBlankAreaDragAndDropToMoveDates.setSelection(true);
        _bAllowBlankAreaDragAndDropToMoveDates.setLayoutData(oneRow);
        _bAllowBlankAreaDragAndDropToMoveDates.setData(KEY, "bAllowBlankAreaDragAndDropToMoveDates");
        prefLoad(_bAllowBlankAreaDragAndDropToMoveDates);
        prefHook(_bAllowBlankAreaDragAndDropToMoveDates);
        
        _bAllowVerticalBlankDnd = new Button(group, SWT.CHECK);
        _bAllowVerticalBlankDnd.setText("Allow Vertical Blank Area DND to Move Chart)");
        _bAllowVerticalBlankDnd.setToolTipText("When you grab a blank area of the chart and drag it, the chart will move in the drag direction vertically. Default is off.");
        _bAllowVerticalBlankDnd.setSelection(false);
        _bAllowVerticalBlankDnd.setLayoutData(oneRow);
        _bAllowVerticalBlankDnd.setData(KEY, "bAllowVerticalBlankDnd");
        prefLoad(_bAllowVerticalBlankDnd);
        prefHook(_bAllowVerticalBlankDnd);

        _bFlipBlankAreaDragDirection = new Button(group, SWT.CHECK);
        _bFlipBlankAreaDragDirection.setText("Flip Blank Area DND Direction");
        _bFlipBlankAreaDragDirection.setToolTipText("This will invert the X-axis of the direction the chart is scrolling when blank-area drag and dropping (natural to most people). Default is on.");
        _bFlipBlankAreaDragDirection.setSelection(true);
        _bFlipBlankAreaDragDirection.setLayoutData(oneRow);
        _bFlipBlankAreaDragDirection.setData(KEY, "bFlipBlankAreaDragDirection");
        prefLoad(_bFlipBlankAreaDragDirection);
        prefHook(_bFlipBlankAreaDragDirection);

        _bDrawSelectionMarkerAroundSelectedEvent = new Button(group, SWT.CHECK);
        _bDrawSelectionMarkerAroundSelectedEvent.setText("Draw Selection Marker Around Selected Events");
        _bDrawSelectionMarkerAroundSelectedEvent.setToolTipText("This will draw a selection marker (dotted line) around selected events. Default is on.");
        _bDrawSelectionMarkerAroundSelectedEvent.setSelection(true);
        _bDrawSelectionMarkerAroundSelectedEvent.setLayoutData(oneRow);
        _bDrawSelectionMarkerAroundSelectedEvent.setData(KEY, "bDrawSelectionMarkerAroundSelectedEvent");
        prefLoad(_bDrawSelectionMarkerAroundSelectedEvent);
        prefHook(_bDrawSelectionMarkerAroundSelectedEvent);

        _bAllowCheckpointResizing = new Button(group, SWT.CHECK);
        _bAllowCheckpointResizing.setText("Allow Checkpoint Resizing");
        _bAllowCheckpointResizing.setToolTipText("This will allow for resizing of events marked as checkpoints. Default is false.");
        _bAllowCheckpointResizing.setSelection(false);
        _bAllowCheckpointResizing.setLayoutData(oneRow);
        _bAllowCheckpointResizing.setData(KEY, "bAllowCheckpointResizing");
        prefLoad(_bAllowCheckpointResizing);
        prefHook(_bAllowCheckpointResizing);

        _bStartCalendarOnFirstDayOfWeek = new Button(group, SWT.CHECK);
        _bStartCalendarOnFirstDayOfWeek.setText("Start Calendar on First Day of Week");
        _bStartCalendarOnFirstDayOfWeek.setToolTipText("This will force calendar to start on the first day of the week of whatever the root calendar is. Default is false.");
        _bStartCalendarOnFirstDayOfWeek.setSelection(false);
        _bStartCalendarOnFirstDayOfWeek.setLayoutData(oneRow);
        _bStartCalendarOnFirstDayOfWeek.setData(KEY, "bStartCalendarOnFirstDayOfWeek");
        prefLoad(_bStartCalendarOnFirstDayOfWeek);
        prefHook(_bStartCalendarOnFirstDayOfWeek);

        _bDrawFullPercentageBar = new Button(group, SWT.CHECK);
        _bDrawFullPercentageBar.setText("Draw Full Percentage Bar");
        _bDrawFullPercentageBar.setToolTipText("Percentage bar is filled in beyond the percentage complete value (assuming it's less than 100%) to fill out the entire event. Default is on.");
        _bDrawFullPercentageBar.setSelection(true);
        _bDrawFullPercentageBar.setLayoutData(oneRow);
        _bDrawFullPercentageBar.setData(KEY, "bDrawFullPercentageBar");
        prefLoad(_bDrawFullPercentageBar);
        prefHook(_bDrawFullPercentageBar);

        _bDrawLockedDateMarks = new Button(group, SWT.CHECK);
        _bDrawLockedDateMarks.setText("Draw Locked Date Marks");
        _bDrawLockedDateMarks.setToolTipText("Whether locked date-range events (min / max dates) draw a bounding area or markers showing where the span starts/ends. Default is on.");
        _bDrawLockedDateMarks.setSelection(true);
        _bDrawLockedDateMarks.setLayoutData(oneRow);
        _bDrawLockedDateMarks.setData(KEY, "bDrawLockedDateMarks");
        prefLoad(_bDrawLockedDateMarks);
        prefHook(_bDrawLockedDateMarks);

        _bShowDateTipsOnScrolling = new Button(group, SWT.CHECK);
        _bShowDateTipsOnScrolling.setText("Show Date Tips on Scrolling");
        _bShowDateTipsOnScrolling.setToolTipText("Whether date tips (tooltips) are shown when scrolling around to indicate what dates the user is viewing. Default is on.");
        _bShowDateTipsOnScrolling.setSelection(true);
        _bShowDateTipsOnScrolling.setLayoutData(oneRow);
        _bShowDateTipsOnScrolling.setData(KEY, "bShowDateTipsOnScrolling");
        prefLoad(_bShowDateTipsOnScrolling);
        prefHook(_bShowDateTipsOnScrolling);
        
        _bScaleImageToDay = new Button(group, SWT.CHECK);
        _bScaleImageToDay.setText("Scale Images To (Minimum) Width Of One Day");
        _bScaleImageToDay.setToolTipText("Whether images should be scaled to the width of one day or if they can exceed one day. Default is on.");
        _bScaleImageToDay.setSelection(true);
        _bScaleImageToDay.setLayoutData(oneRow);
        _bScaleImageToDay.setData(KEY, "bScaleImageToDay");
        prefLoad(_bScaleImageToDay);
        prefHook(_bScaleImageToDay);

        _bAllowArrowKeysToMoveChart = new Button(group, SWT.CHECK);
        _bAllowArrowKeysToMoveChart.setText("Allow Arrow Keys To Scroll Chart");
        _bAllowArrowKeysToMoveChart.setToolTipText("Whether arrow keys can scroll the chart left/right/up/down (like navigating with scrollbars). Default is off.");
        _bAllowArrowKeysToMoveChart.setSelection(false);
        _bAllowArrowKeysToMoveChart.setLayoutData(oneRow);
        _bAllowArrowKeysToMoveChart.setData(KEY, "bAllowArrowKeysToMoveChart");
        prefLoad(_bAllowArrowKeysToMoveChart);
        prefHook(_bAllowArrowKeysToMoveChart);
                        
        _bCreateSpecialRangesWithAllowNoEvents = new Button(group, SWT.CHECK);
        _bCreateSpecialRangesWithAllowNoEvents.setText("Don't Allow Events on Special Date Ranges");
        _bCreateSpecialRangesWithAllowNoEvents.setToolTipText("Whether special date ranges will not allow drag and drop or resizing over them. Default is off.");
        _bCreateSpecialRangesWithAllowNoEvents.setSelection(false);
        _bCreateSpecialRangesWithAllowNoEvents.setLayoutData(oneRow);
        _bCreateSpecialRangesWithAllowNoEvents.setData(KEY, "bCreateSpecialRangesWithAllowNoEvents");
        prefLoad(_bCreateSpecialRangesWithAllowNoEvents);
        prefHook(_bCreateSpecialRangesWithAllowNoEvents);
       
        return sc;
    }

    private Composite createCreationTab(final Composite parent) {
        final ScrolledComposite sc = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);
        sc.getHorizontalBar().setPageIncrement(100);
        sc.getVerticalBar().setPageIncrement(100);

        final Composite comp = new Composite(sc, SWT.NONE);
        sc.setContent(comp);
        comp.setLayout(new GridLayout(1, true));

        sc.addListener(SWT.Resize, new Listener() {

            public void handleEvent(final Event event) {
                sc.setMinSize(comp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            }

        });

        final Group gGeneral = new Group(comp, SWT.NONE);
        gGeneral.setText("General");
        gGeneral.setLayout(new GridLayout(2, true));
        gGeneral.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        final Label lEvents = new Label(gGeneral, SWT.NONE);
        lEvents.setText("Number of Events");

        _eventCountCombo = new Combo(gGeneral, SWT.NONE);
        _eventCountCombo.add("2");
        _eventCountCombo.add("10");
        _eventCountCombo.add("20");
        _eventCountCombo.add("50");
        _eventCountCombo.add("100");
        _eventCountCombo.add("300");
        _eventCountCombo.add("600");
        _eventCountCombo.add("1000");
        _eventCountCombo.add("2000");
        _eventCountCombo.add("3000");
        _eventCountCombo.add("4000");
        _eventCountCombo.add("5000");
        _eventCountCombo.select(3);
        _eventCountCombo.setToolTipText("Number of events to create");
        _eventCountCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        _eventCountCombo.setData(KEY, "eventCountCombo");
        prefLoad(_eventCountCombo);
        prefHook(_eventCountCombo);

        final Label lTheme = new Label(gGeneral, SWT.NONE);
        lTheme.setText("Color Theme");

        _themeCombo = new Combo(gGeneral, SWT.READ_ONLY);
        _themeCombo.add("Blue");
        _themeCombo.add("Silver");
        _themeCombo.add("Gray Blue");
        _themeCombo.add("High Contrast (Black)");
        _themeCombo.select(0);
        _themeCombo.setData(KEY, "themeCombo");
        prefLoad(_themeCombo);
        prefHook(_themeCombo);

        final Label lLocale = new Label(gGeneral, SWT.NONE);
        lLocale.setText("Locale");

        _localeCombo = new Combo(gGeneral, SWT.READ_ONLY);
        Locale[] all = Locale.getAvailableLocales();
        int sel = 0;
        for (int i = 0; i < all.length; i++) {
            _localeCombo.add(all[i].toString());
            if (all[i].equals(Locale.getDefault())) {
                sel = i;
            }
        }
        _localeCombo.select(sel);
        _localeCombo.setData(KEY, "localeCombo");
        prefLoad(_localeCombo);
        prefHook(_localeCombo);

        final Group gLeft = new Group(comp, SWT.NONE);
        gLeft.setLayout(new GridLayout(1, true));
        gLeft.setText("Styles and Options");
        gLeft.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        final Composite flagComp1 = new Composite(gLeft, SWT.NONE);
        final GridLayout gl1 = new GridLayout(1, true);
        gl1.marginWidth = 0;
        flagComp1.setLayout(gl1);

        _scrollCombo = new Combo(flagComp1, SWT.READ_ONLY);
        _scrollCombo.add("No H Scrollbar (H_SCROLL_NONE)");
        _scrollCombo.add("Fixed H Scrollbar (H_SCROLL_FIXED)");
        _scrollCombo.add("Infinite H Scrollbar (H_SCROLL_INFINITE)");
        _scrollCombo.select(1);
        _scrollCombo.setData(KEY, "scrollCombo");
        prefLoad(_scrollCombo);
        prefHook(_scrollCombo);

        _selCombo = new Combo(flagComp1, SWT.READ_ONLY);
        _selCombo.add("Single select (SWT.SINGLE)");
        _selCombo.add("Multi select (SWT.MULTI)");
        _selCombo.select(1);
        _selCombo.setData(KEY, "selCombo");
        prefLoad(_selCombo);
        prefHook(_selCombo);

        _vDNDCombo = new Combo(flagComp1, SWT.READ_ONLY);
        _vDNDCombo.add("Vertical DND - Off");
        _vDNDCombo.add("Vertical DND - Any");
        _vDNDCombo.add("Vertical DND - Between Sections Only");
        _vDNDCombo.select(1);
        _vDNDCombo.setData(KEY, "vDNDCombo");
        prefLoad(_vDNDCombo);
        prefHook(_vDNDCombo);

        _bIncreaseDates = new Button(gLeft, SWT.CHECK);
        _bIncreaseDates.setText("Increase Dates");
        _bIncreaseDates.setSelection(true);
        _bIncreaseDates.setToolTipText("Automatically increases the date for each subesquently created event");
        _bIncreaseDates.setData(KEY, "bIncreaseDates");
        prefLoad(_bIncreaseDates);
        prefHook(_bIncreaseDates);

        _bCreatePlannedDates = new Button(gLeft, SWT.CHECK);
        _bCreatePlannedDates.setText("Create Planned Dates");
        _bCreatePlannedDates.setSelection(true);
        _bCreatePlannedDates.setToolTipText("Creates planned dates for each event");
        _bCreatePlannedDates.setData(KEY, "bCreatePlannedDates");
        prefLoad(_bCreatePlannedDates);
        prefHook(_bCreatePlannedDates);

        _bDrawHorizontalLines = new Button(gLeft, SWT.CHECK);
        _bDrawHorizontalLines.setText("Draw Horizontal Event Divider Lines");
        _bDrawHorizontalLines.setToolTipText("Draws horizontal lines between events at the location of the height of the row where the event lives");
        _bDrawHorizontalLines.setData(KEY, "bDrawHorizontalLines");
        prefLoad(_bDrawHorizontalLines);
        prefHook(_bDrawHorizontalLines);
        
        _bShowHolidays = new Button(gLeft, SWT.CHECK);
        _bShowHolidays.setText("Show Holidays");
        _bShowHolidays.setSelection(false);
        _bShowHolidays.setToolTipText("Draw Holidays in Chart");
        _bShowHolidays.setData(KEY, "bShowHolidays");
        prefLoad(_bShowHolidays);
        prefHook(_bShowHolidays);


        final Group sections = new Group(gLeft, SWT.NONE);
        sections.setText("Use GanttSections");
        final GridLayout sectionLayout = new GridLayout(2, true);
        sections.setLayout(sectionLayout);

        _bUseSections = new Button(sections, SWT.CHECK);
        _bUseSections.setText("Use GanttSections");
        _bUseSections.setData(KEY, "bUseSections");
        _bUseSections.setSelection(true);
        prefLoad(_bUseSections);
        prefHook(_bUseSections);
        final GridData oneRow = new GridData();
        oneRow.horizontalSpan = 2;
        _bUseSections.setLayoutData(oneRow);

        _bUseSectionsLeft = new Button(sections, SWT.RADIO);
        _bUseSectionsLeft.setText("Left Side");
        _bUseSectionsLeft.setToolTipText("Creates the section bar on the left");
        _bUseSectionsLeft.setSelection(true);
        _bUseSectionsLeft.setData(KEY, "bUseSectionsLeft");
        prefLoad(_bUseSectionsLeft);
        prefHook(_bUseSectionsLeft);

        _bUseSectionsRight = new Button(sections, SWT.RADIO);
        _bUseSectionsRight.setText("Right Side");
        _bUseSectionsRight.setToolTipText("Creates the section bar on the right");
        _bUseSectionsRight.setData(KEY, "bUseSectionsRight");
        prefLoad(_bUseSectionsRight);
        prefHook(_bUseSectionsRight);

        final Label lMaxSections = new Label(gLeft, SWT.LEFT);
        lMaxSections.setText("Max number of sections (0 = infinite)");

        _sMaxSections = new Spinner(gLeft, SWT.BORDER);

        _bRandomEventLength = new Button(gLeft, SWT.CHECK);
        _bRandomEventLength.setText("Random event length (+2-10 days)");
        _bRandomEventLength.setToolTipText("Makes events take a random length");
        _bRandomEventLength.setData(KEY, "bRandomEventLength");
        prefLoad(_bRandomEventLength);
        prefHook(_bRandomEventLength);

        _bConnectEvents = new Button(gLeft, SWT.CHECK);
        _bConnectEvents.setText("Connect events");
        _bConnectEvents.setSelection(true);
        _bConnectEvents.setText("Connects events up to the number defined in the spinner below");
        _bConnectEvents.setData(KEY, "bConnectEvents");
        prefLoad(_bConnectEvents);
        prefHook(_bConnectEvents);

        _sConnectionCountNumber = new Spinner(gLeft, SWT.BORDER);
        _sConnectionCountNumber.setSelection(50);
        _sConnectionCountNumber.setMaximum(10000);
        _sConnectionCountNumber.setToolTipText("Number of events that should be connected\n(Very large numbers is not suggested)");
        _sConnectionCountNumber.setData(KEY, "sConnectionCountNumber");
        prefLoad(_sConnectionCountNumber);
        prefHook(_sConnectionCountNumber);

        _bRandomColors = new Button(gLeft, SWT.CHECK);
        _bRandomColors.setText("Random Connection Colors");
        _bRandomColors.setToolTipText("Uses random colors for connecting events");
        _bRandomColors.setData(KEY, "bRandomColors");
        prefLoad(_bRandomColors);
        prefHook(_bRandomColors);

        _bRandomEventColors = new Button(gLeft, SWT.CHECK);
        _bRandomEventColors.setText("Random Event Colors");
        _bRandomEventColors.setToolTipText("Uses random colors for filling events");
        _bRandomEventColors.setData(KEY, "bRandomEventColors");
        prefLoad(_bRandomEventColors);
        prefHook(_bRandomEventColors);

        _bDNDLimits = new Button(gLeft, SWT.CHECK);
        _bDNDLimits.setText("Random Date Range Limitations");
        _bDNDLimits.setSelection(true);
        _bDNDLimits.setToolTipText("Creates limits that dates cannot be resized/dragged beyond");
        _bDNDLimits.setData(KEY, "bDNDLimits");
        prefLoad(_bDNDLimits);
        prefHook(_bDNDLimits);

        _bRandomPercentCompletes = new Button(gLeft, SWT.CHECK);
        _bRandomPercentCompletes.setText("Random Percent Completes");
        _bRandomPercentCompletes.setToolTipText("Creates random percent completes from 0 to 100 on each event");
        _bRandomPercentCompletes.setSelection(true);
        _bRandomPercentCompletes.setData(KEY, "bRandomPercentCompletes");
        prefLoad(_bRandomPercentCompletes);
        prefHook(_bRandomPercentCompletes);

        /*        final Button bRandomEventHeights = new Button(gLeft, SWT.CHECK);
                bRandomEventHeights.setText("Random Event Heights (eventHeight to 100)");
                bRandomEventHeights.setToolTipText("Creates random event heights for each event between the range of [defaultEventHeight] to 100");
        */
        _bGanttPhases = new Button(gLeft, SWT.CHECK);
        _bGanttPhases.setText("Gantt Phases");
        _bGanttPhases.setToolTipText("Creates some GanttPhase examples");
        _bGanttPhases.setSelection(true);
        _bGanttPhases.setData(KEY, "bGanttPhases");
        prefLoad(_bGanttPhases);
        prefHook(_bGanttPhases);

        _bSpecialDateRange = new Button(gLeft, SWT.CHECK);
        _bSpecialDateRange.setText("Special Date Range (random colors)");
        _bSpecialDateRange.setToolTipText("Creates one Special Date Range as Example (with random colors)");
        _bSpecialDateRange.setSelection(false);
        _bSpecialDateRange.setData(KEY, "bSpecialDateRange");
        prefLoad(_bSpecialDateRange);
        prefHook(_bSpecialDateRange);

        _bRandomImage = new Button(gLeft, SWT.CHECK);
        _bRandomImage.setText("Random Images");
        _bRandomImage.setToolTipText("Create some events with image");
        _bRandomImage.setSelection(false);
        _bRandomImage.setData(KEY, "bRandomImage");
        prefLoad(_bRandomImage);
        prefHook(_bRandomImage);
        
        final Group internal = new Group(gLeft, SWT.CHECK);
        internal.setLayout(new GridLayout(1, false));

        _bRandomRowHeights = new Button(internal, SWT.CHECK);
        _bRandomRowHeights.setText("Random Row Heights (rowHeight to 100)");
        _bRandomRowHeights.setToolTipText("Creates random row heights for each event between the range of [defaultEventHeight] to 100");
        _bRandomRowHeights.setData(KEY, "bRandomRowHeights");
        prefLoad(_bRandomRowHeights);
        prefHook(_bRandomRowHeights);

        _bRandomEventVLoc = new Button(internal, SWT.CHECK);
        _bRandomEventVLoc.setText("Random Event Vertical Location");
        _bRandomEventVLoc.setToolTipText("Creates random location for each event one of (SWT.TOP, SWT.CENTER, SWT.BOTTOM)");
        _bRandomEventVLoc.setEnabled(false);
        _bRandomEventVLoc.setData(KEY, "bRandomEventVLoc");
        prefLoad(_bRandomEventVLoc);
        prefHook(_bRandomEventVLoc);

        _bRandomEventTextHLocation = new Button(internal, SWT.CHECK);
        _bRandomEventTextHLocation.setText("Random Event Horizontal Text Location");
        _bRandomEventTextHLocation.setToolTipText("Creates random event Text location for each event one of (SWT.LEFT, SWT.CENTER, SWT.RIGHT)");
        _bRandomEventTextHLocation.setEnabled(false);
        _bRandomEventTextHLocation.setData(KEY, "bRandomEventTextHLocation");
        prefLoad(_bRandomEventTextHLocation);
        prefHook(_bRandomEventTextHLocation);

        _bRandomEventTextVLocation = new Button(internal, SWT.CHECK);
        _bRandomEventTextVLocation.setText("Random Event Vertical Text Location");
        _bRandomEventTextVLocation.setToolTipText("Creates random event Text location for each event one of (SWT.TOP, SWT.CENTER, SWT.BOTTOM)");
        _bRandomEventTextVLocation.setEnabled(false);
        _bRandomEventTextVLocation.setData(KEY, "bRandomEventTextVLocation");
        prefLoad(_bRandomEventTextVLocation);
        prefHook(_bRandomEventTextVLocation);

        _bRandomRowHeights.addListener(SWT.Selection, new Listener() {

            public void handleEvent(final Event event) {
                _bRandomEventVLoc.setEnabled(_bRandomRowHeights.getSelection());
                _bRandomEventTextHLocation.setEnabled(_bRandomRowHeights.getSelection());
                _bRandomEventTextVLocation.setEnabled(_bRandomRowHeights.getSelection());
            }

        });

        _bUseDDay = new Button(gLeft, SWT.CHECK);
        _bUseDDay.setText("D-Day chart");
        _bUseDDay.setData(KEY, "bUseDDay");
        prefLoad(_bUseDDay);
        prefHook(_bUseDDay);

        _bLockHeader = new Button(gLeft, SWT.CHECK);
        _bLockHeader.setText("Lock Header");
        _bLockHeader.setToolTipText("Locks the header so that it is always shown regardless of vertical scroll");
        _bLockHeader.setData(KEY, "bLockHeader");
        prefLoad(_bLockHeader);
        prefHook(_bLockHeader);

        _bMoveOnlyLaterLinkedEvents = new Button(gLeft, SWT.CHECK);
        _bMoveOnlyLaterLinkedEvents.setSelection(true);
        _bMoveOnlyLaterLinkedEvents.setText("Move/Resize Only 'Older' Linked Events");
        _bMoveOnlyLaterLinkedEvents.setToolTipText("When moving/resizing linked events, this flag ensures that only older events than the ones being dragged are moved/resized");
        _bMoveOnlyLaterLinkedEvents.setData(KEY, "bMoveOnlyLaterLinkedEvents");
        prefLoad(_bMoveOnlyLaterLinkedEvents);
        prefHook(_bMoveOnlyLaterLinkedEvents);

        _bConnectEvents.addListener(SWT.Selection, new Listener() {

            public void handleEvent(final Event event) {
                _sConnectionCountNumber.setEnabled(_bConnectEvents.getSelection());
                _bRandomColors.setEnabled(_bConnectEvents.getSelection());
            }

        });

        Group timerGroup = new Group(comp, SWT.NONE);
        timerGroup.setLayout(new GridLayout(1, true));
        timerGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        timerGroup.setText("Redraw Stats");
        _timerText = new Text(timerGroup, SWT.BORDER | SWT.READ_ONLY);
        _timerText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        _timerText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        _undoRedoListener = new Listener() {
            public void handleEvent(final Event event) {
                if ((event.stateMask & SWT.MOD1) != 0 && (event.keyCode == 'z' || event.keyCode == 'Z')) {
                    _ganttComposite.getUndoRedoManager().undo();
                }
                if ((event.stateMask & SWT.MOD1) != 0 && (event.keyCode == 'y' || event.keyCode == 'Y')) {
                    _ganttComposite.getUndoRedoManager().redo();
                }
            }
        };

        Display.getDefault().addFilter(SWT.KeyDown, _undoRedoListener);

        return sc;
    }

    private void createButtonClicked() {
        final long time1 = System.currentTimeMillis();

        _ganttComposite.getVerticalBar().setPageIncrement(200);

        _ganttComposite.getUndoRedoManager().clear();

        int numberEvents = 0;
        try {
            numberEvents = Integer.parseInt(_eventCountCombo.getText());
        } catch (NumberFormatException nfe) {
            return;
        }

        // _ganttComposite.clearChart();

        _ganttChart.dispose();
        int flags = 0;

        switch (_scrollCombo.getSelectionIndex()) {
            case 0:
                flags |= GanttFlags.H_SCROLL_NONE;
                break;
            case 1:
                flags |= GanttFlags.H_SCROLL_FIXED_RANGE;
                break;
            case 2:
                flags |= GanttFlags.H_SCROLL_INFINITE;
                break;
            default:
                break;
        }

        switch (_selCombo.getSelectionIndex()) {
            case 0:
                flags |= SWT.SINGLE;
                break;
            case 1:
                flags |= SWT.MULTI;
                break;
            default:
                break;
        }

        IColorManager color = new ColorThemeWindowsBlue();
        if (_themeCombo.getSelectionIndex() == 1) {
            color = new ColorThemeSilver();
        } else if (_themeCombo.getSelectionIndex() == 2) {
            color = new ColorThemeGrayBlue();
        } else if (_themeCombo.getSelectionIndex() == 3) { 
            color = new ColorThemeHighContrastBlack();
        }
        	
        

        
        final ISettings toUse = new TestSettings();

        _ganttChart = new GanttChart(_vfChart, flags, toUse, color);
        _ganttComposite = _ganttChart.getGanttComposite();

        _ganttComposite.getUndoRedoManager().addUndoRedoListener(new UndoRedoListenerAdapter() {

            public void canRedoChanged(final boolean canRedo) {
                _bRedo.setEnabled(canRedo);
            }

            public void canUndoChanged(final boolean canUndo) {
                _bUndo.setEnabled(canUndo);
            }

        });

        _vfChart.setContent(_ganttChart);
        _ganttChart.addGanttEventListener(new IGanttEventListener() {
			public void eventDoubleClicked(final GanttEvent event, final MouseEvent me) {
                eventLog("Doubleclicked '" + event + "'");
                final Shell shell = new Shell(Display.getDefault(), SWT.TITLE | SWT.CLOSE | SWT.MIN | SWT.ON_TOP | SWT.APPLICATION_MODAL);
                shell.setLocation(Display.getDefault().getBounds().width * 1 / 5, Display.getDefault().getBounds().height * 1 / 3);
                shell.setSize(350, 200);
                final String dialogTitle = event.getName();
                shell.setText(dialogTitle);
                shell.open();
            }

            public void eventHeaderSelected(Calendar newlySelectedDate, List allSelectedDates) {
                eventLog("Header date selected: " + newlySelectedDate.getTime() + ". Now a total of " + allSelectedDates.size() + " header date(s) selected.");
            }

            public void eventMovedToNewSection(GanttEvent event, GanttSection oldSection, GanttSection newSection) {
                eventLog("Eveent'" + event + "' was moved from GanttSection '" + oldSection.getName() + "' to '" +newSection.getName() +"'");
            }

            public void eventPropertiesSelected(List events) {
                eventLog("Properties was selected on '" + events + "'");
            }

            public void eventReordered(GanttEvent event) {
                eventLog("Event '" + event + "' was reordered");
            }

            public void eventsDeleteRequest(List events, MouseEvent mouseEvent) {
                eventLog("Events '" + events + "' were requested to be deleted");
                for (int i = 0; i < events.size(); i++) {
                	((GanttEvent)events.get(i)).dispose();
                }
            }

            public void eventSelected(GanttEvent event, List allSelectedEvents, MouseEvent mouseEvent) {
                eventLog("Event '" + event + "' was selected. " + allSelectedEvents.size() + " event(s) are now selected in total.");
            }

            public void eventsMoved(List events, MouseEvent mouseEvent) {
                eventLog("Events '" + events + "' were moved");
            }

            public void eventsMoveFinished(List events, MouseEvent mouseEvent) {
                eventLog("Events '" + events + "' were finished moving");
            }

            public void eventsResized(List events, MouseEvent mouseEvent) {
                eventLog("Events '" + events + "' were resized");
            }

            public void eventsResizeFinished(List events, MouseEvent mouseEvent) {
                eventLog("Events '" + events + "' were finished resizing");
            }

            public void lastDraw(GC gc) {
            }

            public void phaseMoved(GanttPhase phase, MouseEvent mouseEvent) {
                eventLog("Phase '" + phase.getTitle() + "' was moved");
            }

            public void phaseMoveFinished(GanttPhase phase, MouseEvent mouseEvent) {
                eventLog("Phase '" + phase.getTitle() + "' finished moving");
            }

            public void phaseResized(GanttPhase phase, MouseEvent mouseEvent) {
                eventLog("Phase '" + phase.getTitle() + "' was resized");
            }

            public void phaseResizeFinished(GanttPhase phase, MouseEvent mouseEvent) {
                eventLog("Phase '" + phase.getTitle() + "' finished resizing");
            }

            public void zoomedIn(int newZoomLevel) {
                eventLog("Zoomed in, zoom level is now: " + newZoomLevel);
            }

            public void zoomedOut(int newZoomLevel) {
                eventLog("Zoomed out, zoom level is now: " + newZoomLevel);
            }

            public void zoomReset() {
                eventLog("Zoomed level was reset");
            }

            public void eventsDroppedOrResizedOntoUnallowedDateRange(List events, GanttSpecialDateRange range) {
            	eventLog("Events '" + events + "' were dropped or resized over special range '" + range + "' that does not allow events on its dates");
				
			}
        });

        final Random r = new Random();

        GanttSection parent = null;
        int sectionCount = 1;
        int lastSectionEventCount = 0;
        if (_bUseSections.getSelection() && (_bUseSectionsLeft.getSelection() || _bUseSectionsRight.getSelection())) {
            parent = new GanttSection(_ganttChart, "Section " + sectionCount);
        }

        if (_bGanttPhases.getSelection()) {
            Calendar phaseRoot = Calendar.getInstance();
            if (_bUseDDay.getSelection()) {
                phaseRoot = toUse.getDDayRootCalendar();
            }

            final Calendar x = (Calendar) phaseRoot.clone();
            x.add(Calendar.DATE, 10);
            new GanttPhase(_ganttChart, phaseRoot, x, "Testing");

            x.add(Calendar.DATE, 10);
            final Calendar x2 = (Calendar) phaseRoot.clone();
            x2.add(Calendar.DATE, 30);
            new GanttPhase(_ganttChart, x, x2, "Something Much Longer");
        }

        if (_bSpecialDateRange.getSelection()) {
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            if (_bUseDDay.getSelection()) {
                start = (Calendar) toUse.getDDayRootCalendar().clone();
                end = (Calendar) toUse.getDDayRootCalendar().clone();
            }

            end.add(Calendar.DATE, 50);
            final GanttSpecialDateRange range = new GanttSpecialDateRange(_ganttChart, start, end);
            range.setFrequency(_bUseDDay.getSelection() ? GanttSpecialDateRange.REPEAT_DDAY : GanttSpecialDateRange.REPEAT_WEEKLY);
            if (_bUseDDay.getSelection()) {
                range.setDDayRepeatInterval(10);
            } else {
                range.addRecurDay(Calendar.WEDNESDAY);
            }
            range.setRecurCount(50);
            range.setBackgroundColorTop(ColorCache.getRandomColor());
            range.setBackgroundColorBottom(ColorCache.getRandomColor());
            if (_bCreateSpecialRangesWithAllowNoEvents.getSelection()) {
            	range.setAllowEventsOnDates(false);
            }
        }

        for (int i = 0; i < numberEvents; i++) {
            Calendar cal = Calendar.getInstance();

            if (_bUseDDay.getSelection()) {
                cal = (Calendar) toUse.getDDayRootCalendar().clone();
            }

            GanttEvent predEvent = null;
            if (i != 0) {
                predEvent = ((GanttEvent) _ganttComposite.getEvents().get(i - 1));
                cal = predEvent.getActualEndDate();
            }
            final Calendar cStartDate = cal;
            if (_bRandomEventLength.getSelection()) {
                cStartDate.add(Calendar.DATE, r.nextInt(5));
            }
            final Calendar cEndDate = (Calendar) cStartDate.clone();
            if (_bIncreaseDates.getSelection()) {
                cEndDate.add(Calendar.DATE, _bRandomEventLength.getSelection() ? r.nextInt(10) + 1 : 1);
            }

            GanttEvent ganttEvent = null;
            if (_bCreatePlannedDates.getSelection()) {
                final Calendar plannedStart = (Calendar) cStartDate.clone();
                plannedStart.add(Calendar.DATE, _bRandomEventLength.getSelection() ? -(r.nextInt(10) + 1) : -10);
                final Calendar plannedEnd = (Calendar) cEndDate.clone();
                plannedEnd.add(Calendar.DATE, _bRandomEventLength.getSelection() ? (r.nextInt(10) + 1) : 10);

                ganttEvent = new GanttEvent(_ganttChart, null, "Event " + (i + 1), plannedStart, plannedEnd, cStartDate, cEndDate, 0);
            } else {
                ganttEvent = new GanttEvent(_ganttChart, null, "Event " + (i + 1), cStartDate, cEndDate, 0);
            }

            if (_bRandomPercentCompletes.getSelection()) {
                ganttEvent.setPercentComplete(r.nextInt(100));
            }

            if (_bUseSections.getSelection() && (_bUseSectionsLeft.getSelection() || _bUseSectionsRight.getSelection())) {
                boolean reachedMax = false;
                if (_sMaxSections.getSelection() != 0) {
                    reachedMax = sectionCount >= _sMaxSections.getSelection();
                }

                if (lastSectionEventCount != 0 && !reachedMax) {
                    if (r.nextInt(2) == 1) {
                        sectionCount++;
                        parent = new GanttSection(_ganttChart, "Section " + sectionCount);
                        parent.addGanttEvent(ganttEvent);
                        lastSectionEventCount = 0;
                    } else {
                        parent.addGanttEvent(ganttEvent);
                        lastSectionEventCount++;
                    }
                } else {
                    parent.addGanttEvent(ganttEvent);
                    lastSectionEventCount++;
                }
            }

            if (_bRandomEventColors.getSelection()) {
                ganttEvent.setStatusColor(ColorCache.getColor(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
                ganttEvent.setGradientStatusColor(ColorCache.getColor(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
            }

            if (_bRandomRowHeights.getSelection()) {
                int height = r.nextInt(100);
                if (height < _ganttChart.getSettings().getEventHeight()) {
                    height = _ganttChart.getSettings().getEventHeight();
                }
                ganttEvent.setFixedRowHeight(height);

                if (_bRandomEventTextHLocation.getSelection()) {
                    int loc = -1;
                    switch (r.nextInt(3)) {
                        case 0:
                            loc = SWT.LEFT;
                            break;
                        case 1:
                            loc = SWT.CENTER;
                            break;
                        case 2:
                            loc = SWT.RIGHT;
                            break;
                        default:
                            break;
                    }

                    ganttEvent.setHorizontalTextLocation(loc);
                }
                if (_bRandomEventTextVLocation.getSelection()) {
                    int loc = -1;
                    switch (r.nextInt(3)) {
                        case 0:
                            loc = SWT.TOP;
                            break;
                        case 1:
                            loc = SWT.CENTER;
                            break;
                        case 2:
                            loc = SWT.BOTTOM;
                            break;
                        default:
                            break;
                    }

                    ganttEvent.setVerticalTextLocation(loc);
                }
                if (_bRandomEventVLoc.getSelection()) {
                    int loc = -1;
                    switch (r.nextInt(3)) {
                        case 0:
                            loc = SWT.TOP;
                            break;
                        case 1:
                            loc = SWT.CENTER;
                            break;
                        case 2:
                            loc = SWT.BOTTOM;
                            break;
                        default:
                            break;
                    }

                    ganttEvent.setVerticalEventAlignment(loc);
                }

            }

            if (_bRandomImage.getSelection() && r.nextInt(5) == 0) {
            	PaletteData palette = new PaletteData (new RGB [] {Display.getDefault().getSystemColor(SWT.COLOR_WHITE).getRGB(), Display.getDefault().getSystemColor(SWT.COLOR_BLUE).getRGB()});
            	ImageData sourceData = new ImageData (16, 16, 1, palette);
            	sourceData.transparentPixel = 0;
            	final Image image = new Image(Display.getDefault(), sourceData);
            	final Rectangle rect = image.getBounds();
            	GC gc = new GC(image);
            	gc.setAntialias(SWT.ON);
            	gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
            	gc.fillOval(rect.x, rect.y, rect.width, rect.height);
            	gc.dispose();
            	ganttEvent.setImage(true);
            	ganttEvent.setPicture(image);
            	ganttEvent.setEndDate(ganttEvent.getStartDate());
            }            
            
            if (_bDNDLimits.getSelection()) {
                final Calendar noBefore = Calendar.getInstance();
                noBefore.setTime(cStartDate.getTime());
                noBefore.add(Calendar.DATE, -(r.nextInt(10) + 2));
                if (r.nextInt(2) == 1) {
                    ganttEvent.setNoMoveBeforeDate(noBefore);
                }

                final Calendar noBeyond = Calendar.getInstance();
                noBeyond.setTime(cEndDate.getTime());
                noBeyond.add(Calendar.DATE, r.nextInt(10) + 2);

                if (r.nextInt(2) == 1) {
                    ganttEvent.setNoMoveAfterDate(noBeyond);
                }
            }

            if (_bConnectEvents.getSelection() && i < _sConnectionCountNumber.getSelection()) {
                if (_bRandomColors.getSelection()) {
                    _ganttComposite.addConnection(predEvent, ganttEvent, ColorCache.getColor(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
                } else {
                    _ganttComposite.addConnection(predEvent, ganttEvent);
                }
            }
        }
        
        if(_bShowHolidays.getSelection()) {
        	Calendar start = Calendar.getInstance();
        	Calendar end = Calendar.getInstance();
        	
        	for(int i = 0; i < _ganttComposite.getEvents().size(); i++) {
        		GanttEvent event = (GanttEvent) _ganttComposite.getEvents().get(i);
        		if(event.getActualStartDate().before(start)) {
        			start = event.getActualStartDate();
        		}
        		if(event.getActualEndDate().after(end)) {
        			end = event.getActualEndDate();
        		}
        	}
        	List<Holiday> holidays = new ArrayList<Holiday>();
        	Calendar day = Calendar.getInstance();
        	day.setTime(start.getTime());
        	
        	int i = 1;
        	while(day.before(end)) {
        		day.add(Calendar.DATE, 5 + r.nextInt(5));
        		int dow = day.get(Calendar.DAY_OF_WEEK);
				
				if (dow != Calendar.SATURDAY && dow != Calendar.SUNDAY) {
					Calendar date = (Calendar) day.clone();
					holidays.add(new Holiday(date, "Special free Day " + i++));
				}
        	}
			_ganttComposite.setHolidays(holidays.toArray(new Holiday[holidays.size()]));
        }

        final long time2 = System.currentTimeMillis();
        _timerText.setText("Initial chart creation took " + (time2 - time1) + " ms");
        eventLog("Initial chart creation took " + (time2 - time1) + " ms");
        
        
        moveFocus();

        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                if (_ganttChart.isDisposed()) { return; }
                _ganttChart.getVerticalBar().setPageIncrement(350);
            }

        });
    }

    private void createCreateButtons(final Composite parent) {
        final Group buttons = new Group(parent, SWT.NONE);
        buttons.setText("Any changes to above requires a new 'Create'");
        buttons.setLayout(new GridLayout(2, true));
        final GridData buttonData = new GridData();
        buttonData.grabExcessHorizontalSpace = true;
        buttons.setLayoutData(buttonData);

        _bCreate = new Button(buttons, SWT.PUSH);
        _bCreate.setText("&Create");
        _bCreate.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        _bCreate.setToolTipText("Creates a new chart");

        _bClear = new Button(buttons, SWT.PUSH);
        _bClear.setText("Clear");
        _bClear.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        _bClear.setToolTipText("Clears all events from the chart");

        _bRedraw = new Button(buttons, SWT.PUSH);
        _bRedraw.setText("Normal Redraw");
        _bRedraw.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        _bRedraw.setToolTipText("Forces a normal redraw which the chart does whenever something minor has changed");

        _bHeavyRedraw = new Button(buttons, SWT.PUSH);
        _bHeavyRedraw.setText("Heavy Redraw");
        _bHeavyRedraw.setToolTipText("You should never have to use a heavy redraw. If you need to because of something not doing what it is supposed to it is most likely a bug\n\nAlso note that the results of using this may not be the same as when the chart calls this method itself");
        _bHeavyRedraw.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        _bSaveFull = new Button(buttons, SWT.PUSH);
        _bSaveFull.setText("Save Full Image");
        _bSaveFull.setToolTipText("Saves an image of the chart, the entire chart, and nothing but the chart");
        _bSaveFull.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        _bSaveFull.addListener(SWT.Selection, new Listener() {

            public void handleEvent(final Event event) {
                final Image full = _ganttComposite.getFullImage();

                final FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
                fd.setFilterExtensions(new String[] { ".jpg" });
                fd.setFilterNames(new String[] { "JPG File" });
                fd.setFileName("img.jpg");
                final String path = fd.open();
                if (path == null) { return; }

                final ImageLoader imageLoader = new ImageLoader();
                imageLoader.data = new ImageData[] { full.getImageData() };
                imageLoader.save(path, SWT.IMAGE_JPEG);
            }

        });

        _bHeavyRedraw.addListener(SWT.Selection, new Listener() {

            public void handleEvent(final Event event) {
                final long time1 = System.currentTimeMillis();
                //long nano1 = System.nanoTime();
                _ganttComposite.heavyRedraw();
                final long time2 = System.currentTimeMillis();
                //long nano2 = System.nanoTime();

                _timerText.setText("Heavy redraw took " + (time2 - time1) + " ms");
                eventLog("Heavy redraw took " + (time2 - time1) + " ms");
            }

        });

        _bCreate.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                createButtonClicked();
            }

        });

        _bClear.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                _ganttComposite.clearChart();
                moveFocus();
            }
        });

        _bRedraw.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                final long time1 = System.currentTimeMillis();
                //long nano1 = System.nanoTime();
                _ganttComposite.refresh();
                final long time2 = System.currentTimeMillis();
                //long nano2 = System.nanoTime();
                _timerText.setText("Redraw took " + (time2 - time1) + " ms");
                eventLog("Redraw took " + (time2 - time1) + " ms");
                moveFocus();
            }
        });

    }

    /**
     * Hooks a control to have state saved automatically when it changes
     * 
     * @param ctrl Control to state save
     */
    private void prefHook(final Control ctrl) {
        final Object obj = ctrl.getData(KEY);
        if (obj == null) {
            System.err.println("Control " + ctrl + " does not have a key set on it!"); // NOPMD
            return;
        }
        final String keyName = obj.toString();
        if (ctrl instanceof Spinner) {
            ((Spinner) ctrl).addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    _prefs.putInt(keyName, ((Spinner) ctrl).getSelection());
                }
            });
        } else if (ctrl instanceof Button) {
            ((Button) ctrl).addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    _prefs.put(keyName, ((Button) ctrl).getSelection() ? "true" : "false");
                }
            });
        } else if (ctrl instanceof Text) {
            ((Text) ctrl).addListener(SWT.Modify, new Listener() {
                public void handleEvent(Event event) {
                    _prefs.put(keyName, ((Text) ctrl).getText());
                }
            });
        } else if (ctrl instanceof Combo) {
            ((Combo) ctrl).addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    _prefs.putInt(keyName, ((Combo) ctrl).getSelectionIndex());
                }
            });
        }
    }

    /**
     * Loads a saved preference onto a control
     * 
     * @param ctrl Control to load preference onto
     */
    private void prefLoad(final Control ctrl) {
        final Object obj = ctrl.getData(KEY);
        if (obj == null) { return; }
        final String keyName = obj.toString();

        if (ctrl instanceof Spinner) {
            int old = _prefs.getInt(keyName, -1);
            if (old == -1) { return; }

            ((Spinner) ctrl).setSelection(old);
        } else if (ctrl instanceof Button) {
            String old = _prefs.get(keyName, null);
            if (old == null) { return; }
            ((Button) ctrl).setSelection(Boolean.valueOf(old).booleanValue());
        } else if (ctrl instanceof Text) {
            String old = _prefs.get(keyName, null);
            if (old == null) { return; }

            ((Text) ctrl).setText(old);
        } else if (ctrl instanceof Combo) {
            int old = _prefs.getInt(keyName, -1);
            if (old == -1) { return; }

            ((Combo) ctrl).select(old);
        }

    }

    public int getVerticalDNDStyle() {
        switch (_vDNDCombo.getSelectionIndex()) {
            case 0:
                return VerticalDragModes.NO_VERTICAL_DRAG;
            case 1:
                return VerticalDragModes.ANY_VERTICAL_DRAG;
            case 2:
                return VerticalDragModes.CROSS_SECTION_VERTICAL_DRAG;
            default:
                break;
        }

        return VerticalDragModes.NO_VERTICAL_DRAG;
    }

    private Composite createBottom(final Composite parent) {
        final ScrolledComposite sc = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);

        final Composite outer = new Composite(sc, SWT.NONE);
        sc.setContent(outer);
        outer.setLayout(new GridLayout(1, true));

        sc.addListener(SWT.Resize, new Listener() {

            public void handleEvent(final Event event) {
                sc.setMinSize(outer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            }

        });

        final Group comp = new Group(outer, SWT.NONE);
        comp.setText("Gantt Chart Operations");
        comp.setLayoutData(new GridData(GridData.FILL_BOTH));
        comp.setLayout(new GridLayout(10, false));

        final Button bJumpEarliest = new Button(comp, SWT.PUSH);
        bJumpEarliest.setText("Jump to earliest event");

        final Button bJumpLatest = new Button(comp, SWT.PUSH);
        bJumpLatest.setText("Jump to latest event");

        final Button bSelectFirstEvent = new Button(comp, SWT.PUSH);
        bSelectFirstEvent.setText("Show first event");

        final Button bSelectMidEvent = new Button(comp, SWT.PUSH);
        bSelectMidEvent.setText("Show middle event");

        final Button bSelectLastEvent = new Button(comp, SWT.PUSH);
        bSelectLastEvent.setText("Show last event");

        final Button bJumpToCurrentTimeLeft = new Button(comp, SWT.PUSH);
        bJumpToCurrentTimeLeft.setText("Today [Left]");
        final Button bJumpToCurrentTimeCenter = new Button(comp, SWT.PUSH);
        bJumpToCurrentTimeCenter.setText("Today [Center]");
        final Button bJumpToCurrentTimeRight = new Button(comp, SWT.PUSH);
        bJumpToCurrentTimeRight.setText("Today [Right]");

        final Button bMoveEventsLeft = new Button(comp, SWT.PUSH);
        bMoveEventsLeft.setText("Move -1");
        final Button bMoveEventsRight = new Button(comp, SWT.PUSH);
        bMoveEventsRight.setText("Move +1");

        final Button zIn = new Button(comp, SWT.PUSH);
        final Button zOut = new Button(comp, SWT.PUSH);
        zIn.setText("Zoom In");
        zOut.setText("Zoom Out");

        final Button bShowPlanned = new Button(comp, SWT.PUSH);
        bShowPlanned.setText("Toggle Planned Dates");

        final Button bShowDays = new Button(comp, SWT.PUSH);
        bShowDays.setText("Toggle Dates On Events");

        final Button bSetDate = new Button(comp, SWT.PUSH);
        bSetDate.setText("Set Date Randomly (+-10)");
        bSetDate.setToolTipText("Sets the date randomly to a date +-10 days from the leftmost date of the chart");

        _bUndo = new Button(comp, SWT.PUSH);
        _bUndo.setText("Undo (CTRL+z)");
        _bUndo.setEnabled(false);
        _bUndo.addListener(SWT.Selection, new Listener() {

            public void handleEvent(final Event event) {
                _ganttComposite.getUndoRedoManager().undo();
                eventLog("Undo was pressed");
            }

        });

        _bRedo = new Button(comp, SWT.PUSH);
        _bRedo.setText("Redo (CTRL+y)");
        _bRedo.setEnabled(false);
        _bRedo.addListener(SWT.Selection, new Listener() {

            public void handleEvent(final Event event) {
                _ganttComposite.getUndoRedoManager().redo();
                eventLog("Redo was pressed");
            }

        });

        bMoveEventsLeft.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
            	if (_ganttComposite.getCurrentView() == ISettings.VIEW_MINUTE){
            		moveAllEvents(Calendar.MINUTE, -1);
                    moveFocus();
            	}
            		
            	else {
                 moveAllEvents(Calendar.DATE, -1);
                 moveFocus();
            	}
            }
        });

        bMoveEventsRight.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
            	if (_ganttComposite.getCurrentView() == ISettings.VIEW_MINUTE){
            		moveAllEvents(Calendar.MINUTE, 1);
                    moveFocus();
            	}
            		
            	else {
                 moveAllEvents(Calendar.DATE, 1);
                 moveFocus();
            	}
            }
        });

        bJumpEarliest.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                _ganttComposite.jumpToEarliestEvent();
                moveFocus();
            }
        });

        bJumpLatest.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                _ganttComposite.jumpToLatestEvent();
                moveFocus();
            }
        });

        bSelectFirstEvent.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                if (_ganttComposite.getEvents().size() == 0) { return; }

                _ganttComposite.setTopItem((GanttEvent) _ganttComposite.getEvents().get(0), SWT.CENTER);
                moveFocus();
            }
        });

        bSelectLastEvent.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                if (_ganttComposite.getEvents().size() == 0) { return; }

                _ganttComposite.setTopItem((GanttEvent) _ganttComposite.getEvents().get(_ganttComposite.getEvents().size() - 1), SWT.CENTER);
                moveFocus();
            }
        });

        bSelectMidEvent.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                if (_ganttComposite.getEvents().size() < 2) { return; }

                final GanttEvent ge = (GanttEvent) _ganttComposite.getEvents().get(_ganttComposite.getEvents().size() / 2);
                _ganttComposite.setTopItem(ge, SWT.CENTER);
                moveFocus();
            }
        });

        bJumpToCurrentTimeLeft.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                final Calendar currentDate = Calendar.getInstance();

                _ganttComposite.setDate(currentDate, SWT.LEFT);
                moveFocus();
            }
        });
        bJumpToCurrentTimeCenter.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                final Calendar currentDate = Calendar.getInstance();

                _ganttComposite.setDate(currentDate, SWT.CENTER);
                moveFocus();
            }
        });
        bJumpToCurrentTimeRight.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                final Calendar currentDate = Calendar.getInstance();

                _ganttComposite.setDate(currentDate, SWT.RIGHT);
                moveFocus();
            }
        });

        zIn.addListener(SWT.Selection, new Listener() {

            public void handleEvent(final Event event) {
                _ganttComposite.zoomIn();
                moveFocus();
            }

        });

        zOut.addListener(SWT.Selection, new Listener() {

            public void handleEvent(final Event event) {
                _ganttComposite.zoomOut();
                moveFocus();
            }

        });

        bShowPlanned.addListener(SWT.Selection, new Listener() {

            public void handleEvent(final Event event) {
                _ganttComposite.setShowPlannedDates(!_ganttComposite.isShowingPlannedDates());
                moveFocus();
            }

        });

        bShowDays.addListener(SWT.Selection, new Listener() {

            public void handleEvent(final Event event) {
                _ganttComposite.setShowDaysOnEvents(!_ganttComposite.isShowingDaysOnEvents());
                moveFocus();
            }

        });

        bSetDate.addListener(SWT.Selection, new Listener() {

            public void handleEvent(final Event event) {
                final Calendar cal = _ganttComposite.getDate();
                final Random r = new Random();
                int add = r.nextInt(10) + 1;
                if (r.nextInt(2) == 1) {
                    add = -add;
                }

                cal.add(Calendar.DATE, add);
                _ganttComposite.setDate(cal);
                moveFocus();
            }

        });

        return sc;
    }

    private void moveAllEvents(final int calendarObj, final int amount) {
        if (amount == 0) { return; }

        final List events = _ganttComposite.getEvents();
        for (int i = 0; i < events.size(); i++) {
            final GanttEvent ge = (GanttEvent) events.get(i);
            final Calendar start = ge.getActualStartDate();
            final Calendar end = ge.getActualEndDate();

            if (amount > 0) {
                end.add(calendarObj, amount);
                start.add(calendarObj, amount);

                ge.setRevisedDates(start, end, SWT.RIGHT_TO_LEFT);
            } else {
                start.add(calendarObj, amount);
                end.add(calendarObj, amount);
                ge.setRevisedDates(start, end, SWT.LEFT_TO_RIGHT);
            }

            ge.update(false);
        }

        _ganttComposite.heavyRedraw();

    }

    private void moveFocus() {
        _ganttComposite.setFocus();
    }

    private Locale getSelectedLocale() {
        final Locale[] all = Locale.getAvailableLocales();
        return all[_localeCombo.getSelectionIndex()];
    }

    private void eventLog(String txt) {
        TableItem ti = new TableItem(_tEventLog, SWT.NONE, 0);
        ti.setText(txt);
        System.out.println(txt);
    }

    class TestSettings extends AbstractSettings {
        public boolean lockHeaderOnVerticalScroll() {
            return _bLockHeader.getSelection();
        }

        public boolean drawHeader() {
            return true;
        }

        public int getSectionSide() {
            return _bUseSectionsRight.getSelection() ? SWT.RIGHT : SWT.LEFT;
        }

        public boolean drawHorizontalLines() {
            return _bDrawHorizontalLines.getSelection();
        }

        public int getVerticalEventDragging() {
            return getVerticalDNDStyle();
        }

        public int getInitialView() {
            if (_bUseDDay.getSelection()) { return ISettings.VIEW_D_DAY; }

            return super.getInitialView();
        }

        public boolean moveLinkedEventsWhenEventsAreMoved() {
            return true;
        }

        public boolean moveAndResizeOnlyDependentEventsThatAreLaterThanLinkedMoveEvent() {
            return true;
        }

        public Locale getDefaultLocale() {
            return getSelectedLocale();
        }

        public boolean enableAutoScroll() {
            return _bEnableAutoScroll.getSelection();
        }

        public boolean enableResizing() {
            return _bEventResizing.getSelection();
        }

        public boolean enableDragAndDrop() {
            return _bEventDND.getSelection();
        }

        public boolean adjustForLetters() {
            return _bAdjustForLetters.getSelection();
        }

        public int getArrowConnectionType() {
            switch (_bConnectionLineStyle.getSelectionIndex()) {
                case 0:
                    return ISettings.CONNECTION_ARROW_RIGHT_TO_LEFT;
                case 1:
                    return ISettings.CONNECTION_ARROW_RIGHT_TO_TOP;
                default:
                case 2:
                    return ISettings.CONNECTION_MS_PROJECT_STYLE;
                case 3:
                    return ISettings.CONNECTION_BIRDS_FLIGHT_PATH;
            }
        }

        public boolean showArrows() {
            return _bShowArrows.getSelection();
        }

        public boolean showBoldScopeText() {
            return _bShowBoldScopeText.getSelection();
        }

        public boolean showToolTips() {
            return _bShowTooltips.getSelection();
        }

        public boolean showGradientEventBars() {
            return _bShowGradientEventBars.getSelection();
        }

        public boolean showOnlyDependenciesForSelectedItems() {
            return _bShowOnlyDependenciesForSelectedItems.getSelection();
        }

        public boolean showZoomLevelBox() {
            return _bShowZoomLevelBox.getSelection();
        }

        public boolean allowBlankAreaDragAndDropToMoveDates() {
            return _bAllowBlankAreaDragAndDropToMoveDates.getSelection();
        }

        public boolean flipBlankAreaDragDirection() {
            return _bFlipBlankAreaDragDirection.getSelection();
        }

        public boolean drawSelectionMarkerAroundSelectedEvent() {
            return _bDrawSelectionMarkerAroundSelectedEvent.getSelection();
        }

        public boolean allowCheckpointResizing() {
            return _bAllowCheckpointResizing.getSelection();
        }

        public boolean startCalendarOnFirstDayOfWeek() {
            return _bStartCalendarOnFirstDayOfWeek.getSelection();
        }

        public boolean enableZooming() {
            return _bEnableZooming.getSelection();
        }

        public boolean drawFullPercentageBar() {
            return _bDrawFullPercentageBar.getSelection();
        }

        public boolean getUseAdvancedTooltips() {
            return _bShowAdvancedTooltips.getSelection();
        }

        public boolean drawLockedDateMarks() {
            return _bDrawLockedDateMarks.getSelection();
        }

        public boolean showDateTipsOnScrolling() {
            return _bShowDateTipsOnScrolling.getSelection();
        }

        public boolean zoomToMousePointerDateOnWheelZooming() {
            return _bZoomToMousePointerDateOnWheelZooming.getSelection();
        }

        public boolean allowBlankAreaVerticalDragAndDropToMoveChart() {
			return _bAllowVerticalBlankDnd.getSelection();
		}

		public boolean scaleImageToDayWidth() {
        	return _bScaleImageToDay.getSelection();
        }

		public boolean allowArrowKeysToScrollChart() {
			return _bAllowArrowKeysToMoveChart.getSelection();
		}
		
		@Override
		public boolean showHolidayToolTips() {
			return _bShowHolidays.getSelection();
		}
    }

}
