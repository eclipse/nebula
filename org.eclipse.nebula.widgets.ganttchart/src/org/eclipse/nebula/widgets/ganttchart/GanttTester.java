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

import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class GanttTester {

	private GanttChart		_ganttChart;
	private GanttComposite	_ganttComposite;

	private ViewForm		_vfChart;

	private Text			_timerText;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new GanttTester();
	}

	public GanttTester() {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Gantt Tester");
		shell.setLayout(new FillLayout());

		SashForm sfVSplit = new SashForm(shell, SWT.VERTICAL);
		SashForm sfHSplit = new SashForm(sfVSplit, SWT.HORIZONTAL);

		ViewForm vfBottom = new ViewForm(sfVSplit, SWT.NONE);
		_vfChart = new ViewForm(sfHSplit, SWT.NONE);
		ViewForm vfRight = new ViewForm(sfHSplit, SWT.NONE);

		sfVSplit.setWeights(new int[] { 91, 9 });
		sfHSplit.setWeights(new int[] { 80, 20 });

		// top left side
		_ganttChart = new GanttChart(_vfChart, SWT.MULTI);
		_vfChart.setContent(_ganttChart);
		_ganttComposite = _ganttChart.getGanttComposite();

		TabFolder tfRight = new TabFolder(vfRight, SWT.BORDER);
		TabItem tiGeneral = new TabItem(tfRight, SWT.NONE);
		tiGeneral.setText("Creation");
		vfRight.setContent(tfRight);

		vfBottom.setContent(createBottom(vfBottom));
		tiGeneral.setControl(createCreationTab(tfRight));

		// shell.setSize(1800, 1000);
		// shell.setLocation(new Point(100, 100));
		shell.setMaximized(true);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private Composite createCreationTab(Composite parent) {
		final ScrolledComposite sc = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
	    sc.setExpandHorizontal(true);
	    sc.setExpandVertical(true);

		final Composite comp = new Composite(sc, SWT.NONE);
		sc.setContent(comp);
		comp.setLayout(new GridLayout(1, true));
				
		sc.addListener(SWT.Resize, new Listener() {

			public void handleEvent(Event event) {
				sc.setMinSize(comp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}
			
		});


		Group gGeneral = new Group(comp, SWT.NONE);
		gGeneral.setText("General");
		gGeneral.setLayout(new GridLayout(1, true));
		gGeneral.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label lEvents = new Label(gGeneral, SWT.NONE);
		lEvents.setText("Number of Events");

		final Combo eventCountCombo = new Combo(gGeneral, SWT.NONE);
		eventCountCombo.add("2");
		eventCountCombo.add("10");
		eventCountCombo.add("20");
		eventCountCombo.add("50");
		eventCountCombo.add("100");
		eventCountCombo.add("300");
		eventCountCombo.add("600");
		eventCountCombo.add("1000");
		eventCountCombo.add("2000");
		eventCountCombo.add("3000");
		eventCountCombo.add("4000");
		eventCountCombo.add("5000");
		eventCountCombo.select(3);
		eventCountCombo.setToolTipText("Number of events to create");
		eventCountCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Group gLeft = new Group(comp, SWT.NONE);
		gLeft.setLayout(new GridLayout(1, true));
		gLeft.setText("Styles and Options");
		gLeft.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite flagComp0 = new Composite(gLeft, SWT.NONE);
		GridLayout gl0 = new GridLayout(1, true);
		gl0.marginWidth = 0;
		flagComp0.setLayout(gl0);

		final Button bNoHbar = new Button(flagComp0, SWT.RADIO);
		bNoHbar.setText("No H Scrollbar (H_SCROLL_NONE)");

		final Button bFixedHbar = new Button(flagComp0, SWT.RADIO);
		bFixedHbar.setText("Fixed H Scrollbar (H_SCROLL_FIXED)");

		final Button bInfHbar = new Button(flagComp0, SWT.RADIO);
		bInfHbar.setText("Infinite H Scrollbar (H_SCROLL_INFINITE)");
		bInfHbar.setSelection(true);

		Composite flagComp1 = new Composite(gLeft, SWT.NONE);
		GridLayout gl1 = new GridLayout(1, true);
		gl1.marginWidth = 0;
		flagComp1.setLayout(gl1);

		final Button bFlagSingle = new Button(flagComp1, SWT.RADIO);
		bFlagSingle.setText("Single select (SWT.SINGLE)");

		final Button bFlagMulti = new Button(flagComp1, SWT.RADIO);
		bFlagMulti.setText("Multi select (SWT.MULTI)");
		bFlagMulti.setSelection(true);

		bFlagSingle.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
			}

		});

		bFlagMulti.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
			}

		});

		final Button bIncreaseDates = new Button(gLeft, SWT.CHECK);
		bIncreaseDates.setText("Increase Dates");
		bIncreaseDates.setSelection(true);
		bIncreaseDates.setToolTipText("Automatically increases the date for each subesquently created event");

		final Button bCreatePlannedDates = new Button(gLeft, SWT.CHECK);
		bCreatePlannedDates.setText("Create Planned Dates");
		bCreatePlannedDates.setSelection(true);
		bCreatePlannedDates.setToolTipText("Creates planned dates for each event");

		final Button bUseSections = new Button(gLeft, SWT.CHECK);
		bUseSections.setText("Use GanttSections");
		bUseSections.setToolTipText("Creates GanttSections assigns a random number of events to those sections");

		Label lMaxSections = new Label(gLeft, SWT.LEFT);
		lMaxSections.setText("Max number of sections (0 = infinite)");

		final Spinner sMaxSections = new Spinner(gLeft, SWT.BORDER);

		final Button bConnectEvents = new Button(gLeft, SWT.CHECK);
		bConnectEvents.setText("Connect events");
		bConnectEvents.setSelection(true);
		bConnectEvents.setText("Connects events up to the number defined in the spinner below");

		final Button bRandomEventLength = new Button(gLeft, SWT.CHECK);
		bRandomEventLength.setText("Random event length (+2-10 days)");
		bRandomEventLength.setToolTipText("Makes events take a random length");

		final Spinner sConnectionCountNumber = new Spinner(gLeft, SWT.BORDER);
		sConnectionCountNumber.setSelection(50);
		sConnectionCountNumber.setMaximum(10000);
		sConnectionCountNumber.setToolTipText("Number of events that should be connected\n(Very large numbers is not suggested)");

		final Button bRandomColors = new Button(gLeft, SWT.CHECK);
		bRandomColors.setText("Random Connection Colors");
		bRandomColors.setToolTipText("Uses random colors for connecting events");

		final Button bRandomEventColors = new Button(gLeft, SWT.CHECK);
		bRandomEventColors.setText("Random Event Colors");
		bRandomEventColors.setToolTipText("Uses random colors for filling events");

		final Button bDNDLimits = new Button(gLeft, SWT.CHECK);
		bDNDLimits.setText("Random Date Range Limitations");
		bDNDLimits.setSelection(true);
		bDNDLimits.setToolTipText("Creates limits that dates cannot be resized/dragged beyond");

		final Button bRandomEventHeights = new Button(gLeft, SWT.CHECK);
		bRandomEventHeights.setText("Random Event Heights (eventHeight-100)");
		bRandomEventHeights.setToolTipText("Creates random heights for each event between the range of [eventHeight] to 100");

		final Button bRandomEventVLoc = new Button(gLeft, SWT.CHECK);
		bRandomEventVLoc.setText("Random Event Vertical Location");
		bRandomEventVLoc.setToolTipText("Creates random location for each event one of (SWT.TOP, SWT.CENTER, SWT.BOTTOM)");
		bRandomEventVLoc.setEnabled(false);

		final Button bRandomEventTextHLocation = new Button(gLeft, SWT.CHECK);
		bRandomEventTextHLocation.setText("Random Event Horizontal Text Location");
		bRandomEventTextHLocation.setToolTipText("Creates random event Text location for each event one of (SWT.LEFT, SWT.CENTER, SWT.RIGHT)");
		bRandomEventTextHLocation.setEnabled(false);

		final Button bRandomEventTextVLocation = new Button(gLeft, SWT.CHECK);
		bRandomEventTextVLocation.setText("Random Event Vertical Text Location");
		bRandomEventTextVLocation.setToolTipText("Creates random event Text location for each event one of (SWT.TOP, SWT.CENTER, SWT.BOTTOM)");
		bRandomEventTextVLocation.setEnabled(false);

		bRandomEventHeights.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				bRandomEventVLoc.setEnabled(bRandomEventHeights.getSelection());
				bRandomEventTextHLocation.setEnabled(bRandomEventHeights.getSelection());
				bRandomEventTextVLocation.setEnabled(bRandomEventHeights.getSelection());
			}

		});

		final Button bLockHeader = new Button(gLeft, SWT.CHECK);
		bLockHeader.setText("Lock Header");
		bLockHeader.setToolTipText("Locks the header so that it is always shown regardless of vertical scroll");

		bConnectEvents.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				sConnectionCountNumber.setEnabled(bConnectEvents.getSelection());
				bRandomColors.setEnabled(bConnectEvents.getSelection());
			}

		});

		Group buttons = new Group(comp, SWT.NONE);
		buttons.setText("Any changes to above requires a new 'Create'");
		buttons.setLayout(new GridLayout(2, true));
		buttons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button bCreate = new Button(buttons, SWT.PUSH);
		bCreate.setText("Create");
		bCreate.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		bCreate.setToolTipText("Creates a new chart");

		Button bClear = new Button(buttons, SWT.PUSH);
		bClear.setText("Clear");
		bClear.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		bClear.setToolTipText("Clears all events from the chart");

		Button bRedraw = new Button(buttons, SWT.PUSH);
		bRedraw.setText("Normal Redraw");
		bRedraw.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		bRedraw.setToolTipText("Forces a normal redraw which the chart does whenever something minor has changed");

		Button bHeavyRedraw = new Button(buttons, SWT.PUSH);
		bHeavyRedraw.setText("Heavy Redraw");
		bHeavyRedraw.setToolTipText("You should never have to use a heavy redraw. If you need to because of something not doing what it is supposed to it is most likely a bug\n\nAlso note that the results of using this may not be the same as when the chart calls this method itself");
		bHeavyRedraw.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button bSaveFull = new Button(buttons, SWT.PUSH);
		bSaveFull.setText("Save Full Image");
		bSaveFull.setToolTipText("Saves an image of the chart, the entire chart, and nothing but the chart");
		bSaveFull.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		bSaveFull.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				Image full = _ganttComposite.getFullImage();
				
				FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
				fd.setFilterExtensions(new String [] { ".jpg" });
				fd.setFilterNames(new String [] { "JPG File" });
				fd.setFileName("img.jpg");
				String path = fd.open();
				if (path == null)
					return;
				
				ImageLoader imageLoader = new ImageLoader();
				imageLoader.data = new ImageData[] {full.getImageData()};
				imageLoader.save(path, SWT.IMAGE_JPEG); 
			}
			
		});	
		
		Group timerGroup = new Group(comp, SWT.NONE);
		timerGroup.setLayout(new GridLayout(1, true));
		timerGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		timerGroup.setText("Redraw Results");
		_timerText = new Text(timerGroup, SWT.BORDER | SWT.READ_ONLY);
		_timerText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		_timerText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		bHeavyRedraw.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				long time1 = System.currentTimeMillis();
				_ganttComposite.heavyRedraw();
				long time2 = System.currentTimeMillis();
				_timerText.setText("Heavy redraw took " + (time2 - time1) + " ms");
			}

		});

		bCreate.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				long time1 = System.currentTimeMillis();

				int numberEvents = 0;
				try {
					numberEvents = Integer.parseInt(eventCountCombo.getText());
				} catch (NumberFormatException nfe) {
					return;
				}

				// _ganttComposite.clearChart();

				_ganttChart.dispose();
				int flags = 0;

				if (bNoHbar.getSelection())
					flags |= IGanttFlags.H_SCROLL_NONE;
				if (bFixedHbar.getSelection())
					flags |= IGanttFlags.H_SCROLL_FIXED_RANGE;
				if (bInfHbar.getSelection())
					flags |= IGanttFlags.H_SCROLL_INFINITE;
				if (bFlagMulti.getSelection())
					flags |= SWT.MULTI;
				if (bFlagSingle.getSelection())
					flags |= SWT.SINGLE;

				ISettings toUse = null;
				if (bLockHeader.getSelection())
					toUse = new FixedHeaderSettings();
				
				class Foo extends AbstractSettings {

					public boolean moveLinkedEventsWhenEventsAreMoved() {
						return true;
					}
					
					
					
				}

				toUse = new Foo();
				
				_ganttChart = new GanttChart(_vfChart, flags, toUse);
				_ganttComposite = _ganttChart.getGanttComposite();
				_vfChart.setContent(_ganttChart);

				Random r = new Random();

				GanttSection parent = null;
				int sectionCount = 1;
				int lastSectionEventCount = 0;
				if (bUseSections.getSelection()) {
					parent = new GanttSection(_ganttChart, "Section " + sectionCount);
				}

				for (int i = 0; i < numberEvents; i++) {
					Calendar cal = Calendar.getInstance();
					GanttEvent predEvent = null;
					if (i != 0) {
						predEvent = ((GanttEvent) _ganttComposite.getEvents().get(i - 1));
						cal = predEvent.getActualEndDate();
					}
					Calendar cStartDate = cal;
					if (bRandomEventLength.getSelection())
						cStartDate.add(Calendar.DATE, r.nextInt(5));
					Calendar cEndDate = (Calendar) cStartDate.clone();
					if (bIncreaseDates.getSelection())
						cEndDate.add(Calendar.DATE, bRandomEventLength.getSelection() ? r.nextInt(10) + 1 : 1);

					GanttEvent ganttEvent = null;
					if (bCreatePlannedDates.getSelection()) {
						Calendar plannedStart = (Calendar) cStartDate.clone();
						plannedStart.add(Calendar.DATE, bRandomEventLength.getSelection() ? -(r.nextInt(10) + 1) : -10);
						Calendar plannedEnd = (Calendar) cEndDate.clone();
						plannedEnd.add(Calendar.DATE, bRandomEventLength.getSelection() ? (r.nextInt(10) + 1) : 10);

						ganttEvent = new GanttEvent(_ganttChart, null, "Event_" + (i + 1), plannedStart, plannedEnd, cStartDate, cEndDate, 0);
					} else {
						ganttEvent = new GanttEvent(_ganttChart, null, "Event_" + (i + 1), cStartDate, cEndDate, 0);
					}

					if (bUseSections.getSelection()) {
						boolean reachedMax = false;
						if (sMaxSections.getSelection() != 0)
							reachedMax = sectionCount >= sMaxSections.getSelection();
						
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

					if (bRandomEventColors.getSelection()) {
						ganttEvent.setStatusColor(ColorCache.getColor(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
						ganttEvent.setGradientStatusColor(ColorCache.getColor(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
					}

					if (bRandomEventHeights.getSelection()) {
						int height = r.nextInt(100);
						while (height < _ganttChart.getSettings().getEventHeight())
							height = _ganttChart.getSettings().getEventHeight();
						ganttEvent.setFixedRowHeight(height);

						if (bRandomEventTextHLocation.getSelection()) {
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
							}

							ganttEvent.setHorizontalTextLocation(loc);
						}
						if (bRandomEventTextVLocation.getSelection()) {
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
							}

							ganttEvent.setVerticalTextLocation(loc);
						}
						if (bRandomEventVLoc.getSelection()) {
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
							}

							ganttEvent.setVerticalEventAlignment(loc);
						}

					}

					if (bDNDLimits.getSelection()) {
						Calendar noBefore = Calendar.getInstance();
						noBefore.setTime(cStartDate.getTime());
						noBefore.add(Calendar.DATE, -(r.nextInt(10) + 2));
						if (r.nextInt(2) == 1)
							ganttEvent.setNoMoveBeforeDate(noBefore);

						Calendar noBeyond = Calendar.getInstance();
						noBeyond.setTime(cEndDate.getTime());
						noBeyond.add(Calendar.DATE, r.nextInt(10) + 2);

						if (r.nextInt(2) == 1)
							ganttEvent.setNoMoveAfterDate(noBeyond);
					}

					if (bConnectEvents.getSelection()) {
						if (i < sConnectionCountNumber.getSelection()) {
							if (bRandomColors.getSelection()) {
								_ganttComposite.addConnection(predEvent, ganttEvent, ColorCache.getColor(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
							} else {
								_ganttComposite.addConnection(predEvent, ganttEvent);
							}
						}
					}
				}

				long time2 = System.currentTimeMillis();
				_timerText.setText("Initial chart creation took " + (time2 - time1) + " ms");

				moveFocus();
			}

		});

		bClear.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				_ganttComposite.clearChart();
				moveFocus();
			}
		});

		bRedraw.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				long time1 = System.currentTimeMillis();
				_ganttComposite.refresh();
				long time2 = System.currentTimeMillis();
				_timerText.setText("Redraw took " + (time2 - time1) + " ms");
				moveFocus();
			}
		});

		return sc;
	}

	private Composite createBottom(Composite parent) {
		final ScrolledComposite sc = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
	    sc.setExpandHorizontal(true);
	    sc.setExpandVertical(true);
		
		final Composite outer = new Composite(sc, SWT.NONE);
		sc.setContent(outer);
		outer.setLayout(new GridLayout(1, true));
		
		sc.addListener(SWT.Resize, new Listener() {

			public void handleEvent(Event event) {
				sc.setMinSize(outer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}
			
		});

		Group comp = new Group(outer, SWT.NONE);
		comp.setText("Gantt Chart Operations");
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		comp.setLayout(new GridLayout(10, false));

		Button bJumpEarliest = new Button(comp, SWT.PUSH);
		bJumpEarliest.setText("Jump to earliest event");

		Button bJumpLatest = new Button(comp, SWT.PUSH);
		bJumpLatest.setText("Jump to latest event");

		Button bSelectFirstEvent = new Button(comp, SWT.PUSH);
		bSelectFirstEvent.setText("Show first event");

		Button bSelectMidEvent = new Button(comp, SWT.PUSH);
		bSelectMidEvent.setText("Show middle event");

		Button bSelectLastEvent = new Button(comp, SWT.PUSH);
		bSelectLastEvent.setText("Show last event");

		Button bJumpToCurrentTimeLeft = new Button(comp, SWT.PUSH);
		bJumpToCurrentTimeLeft.setText("Today [Left]");
		Button bJumpToCurrentTimeCenter = new Button(comp, SWT.PUSH);
		bJumpToCurrentTimeCenter.setText("Today [Center]");
		Button bJumpToCurrentTimeRight = new Button(comp, SWT.PUSH);
		bJumpToCurrentTimeRight.setText("Today [Right]");

		Button bMoveEventsLeft = new Button(comp, SWT.PUSH);
		bMoveEventsLeft.setText("Move -1");
		Button bMoveEventsRight = new Button(comp, SWT.PUSH);
		bMoveEventsRight.setText("Move +1");

		Button zIn = new Button(comp, SWT.PUSH);
		Button zOut = new Button(comp, SWT.PUSH);
		zIn.setText("Zoom In");
		zOut.setText("Zoom Out");

		Button bShowPlanned = new Button(comp, SWT.PUSH);
		bShowPlanned.setText("Toggle Planned Dates");

		Button bShowDays = new Button(comp, SWT.PUSH);
		bShowDays.setText("Toggle Dates On Events");
		
		Button bSetDate = new Button(comp, SWT.PUSH);
		bSetDate.setText("Set Date Randomly (+-10)");
		bSetDate.setToolTipText("Sets the date randomly to a date +-10 days from the leftmost date of the chart");

		bMoveEventsLeft.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveAllEvents(Calendar.DATE, -1);
				moveFocus();
			}
		});

		bMoveEventsRight.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveAllEvents(Calendar.DATE, 1);
				moveFocus();
			}
		});

		bJumpEarliest.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				_ganttComposite.jumpToEarliestEvent();
				moveFocus();
			}
		});

		bJumpLatest.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				_ganttComposite.jumpToLatestEvent();
				moveFocus();
			}
		});

		bSelectFirstEvent.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				if (_ganttComposite.getEvents().size() == 0)
					return;

				_ganttComposite.setTopItem((GanttEvent) _ganttComposite.getEvents().get(0), SWT.CENTER);
				moveFocus();
			}
		});

		bSelectLastEvent.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				if (_ganttComposite.getEvents().size() == 0)
					return;

				_ganttComposite.setTopItem((GanttEvent) _ganttComposite.getEvents().get(_ganttComposite.getEvents().size() - 1), SWT.CENTER);
				moveFocus();
			}
		});

		bSelectMidEvent.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				if (_ganttComposite.getEvents().size() < 2)
					return;

				GanttEvent ge = (GanttEvent) _ganttComposite.getEvents().get(_ganttComposite.getEvents().size() / 2);
				_ganttComposite.setTopItem(ge, SWT.CENTER);
				moveFocus();
			}
		});

		bJumpToCurrentTimeLeft.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				Calendar currentDate = Calendar.getInstance();

				_ganttComposite.setDate(currentDate, SWT.LEFT);
				moveFocus();
			}
		});
		bJumpToCurrentTimeCenter.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				Calendar currentDate = Calendar.getInstance();

				_ganttComposite.setDate(currentDate, SWT.CENTER);
				moveFocus();
			}
		});
		bJumpToCurrentTimeRight.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				Calendar currentDate = Calendar.getInstance();

				_ganttComposite.setDate(currentDate, SWT.RIGHT);
				moveFocus();
			}
		});

		zIn.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				_ganttComposite.zoomIn(true);
				moveFocus();
			}

		});

		zOut.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				_ganttComposite.zoomOut(true);
				moveFocus();
			}

		});

		bShowPlanned.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				_ganttComposite.setShowPlannedDates(!_ganttComposite.isShowingPlannedDates());
				moveFocus();
			}

		});

		bShowDays.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				_ganttComposite.setShowDaysOnEvents(!_ganttComposite.isShowingDaysOnEvents());
				moveFocus();
			}

		});
		
		bSetDate.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				Calendar cal = _ganttComposite.getDate();
				Random r = new Random();
				int add = r.nextInt(10)+1;
				if (r.nextInt(2) == 1)
					add = -add;
				
				cal.add(Calendar.DATE, add);
				_ganttComposite.setDate(cal);
				moveFocus();
			}
		
		});
		
		return sc;
	}

	private void moveAllEvents(int calendarObj, int amount) {
		List events = _ganttComposite.getEvents();
		for (int i = 0; i < events.size(); i++) {
			GanttEvent ge = (GanttEvent) events.get(i);
			Calendar start = ge.getActualStartDate();
			Calendar end = ge.getActualEndDate();

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

		_ganttComposite.redraw();

	}

	private void moveFocus() {
		_ganttComposite.setFocus();
	}

	class FixedHeaderSettings extends AbstractSettings {
		public boolean lockHeaderOnVerticalScroll() {
			return true;
		}
	}
}
