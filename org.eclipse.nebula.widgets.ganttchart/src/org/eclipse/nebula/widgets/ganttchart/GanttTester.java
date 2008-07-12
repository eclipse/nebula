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
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class GanttTester {

	private GanttChart		_ganttChart;
	private GanttComposite	_ganttComposite;

	private ViewForm		_vfChart;

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
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, true));

		Group general = new Group(comp, SWT.NONE);
		general.setText("General");
		general.setLayout(new GridLayout(1, true));
		general.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label lEvents = new Label(general, SWT.NONE);
		lEvents.setText("Number of Events");

		final Combo combo = new Combo(general, SWT.NONE);
		combo.add("2");
		combo.add("10");
		combo.add("20");
		combo.add("50");
		combo.add("100");
		combo.add("300");
		combo.add("600");
		combo.add("1000");
		combo.add("2000");
		combo.add("3000");
		combo.add("4000");
		combo.add("5000");
		combo.select(3);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Group left = new Group(comp, SWT.NONE);
		left.setLayout(new GridLayout(1, true));
		left.setText("Styles and Options");
		left.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Button noHbar = new Button(left, SWT.RADIO);
		noHbar.setText("No H Scrollbar (H_SCROLL_NONE)");

		final Button fixedHbar = new Button(left, SWT.RADIO);
		fixedHbar.setText("Fixed H Scrollbar (H_SCROLL_FIXED)");

		final Button infHbar = new Button(left, SWT.RADIO);
		infHbar.setText("Infinite H Scrollbar (H_SCROLL_INFINITE)");
		infHbar.setSelection(true);

		final Button increase = new Button(left, SWT.CHECK);
		increase.setText("Increase Dates");
		increase.setSelection(true);

		final Button plannedDates = new Button(left, SWT.CHECK);
		plannedDates.setText("Create Planned Dates");
		plannedDates.setSelection(true);

		final Button connEvents = new Button(left, SWT.CHECK);
		connEvents.setText("Connect events");
		connEvents.setSelection(true);

		final Spinner connNumber = new Spinner(left, SWT.BORDER);
		connNumber.setSelection(50);
		connNumber.setMaximum(4000);

		final Button rnd = new Button(left, SWT.CHECK);
		rnd.setText("Random Connection Colors");
		rnd.setSelection(true);

		final Button limits = new Button(left, SWT.CHECK);
		limits.setText("Create DND range limitations");
		limits.setSelection(true);

		final Button lockHeader = new Button(left, SWT.CHECK);
		lockHeader.setText("Lock Header");

		connEvents.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				connNumber.setEnabled(connEvents.getSelection());
				rnd.setEnabled(connEvents.getSelection());
			}

		});

		Group buttons = new Group(comp, SWT.NONE);
		buttons.setLayout(new GridLayout(3, true));
		buttons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button button = new Button(buttons, SWT.PUSH);
		button.setText("Create");
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button clear = new Button(buttons, SWT.PUSH);
		clear.setText("Clear");
		clear.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button redrawButton = new Button(buttons, SWT.PUSH);
		redrawButton.setText("Redraw");
		redrawButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				int numberEvents = 0;
				try {
					numberEvents = Integer.parseInt(combo.getText());
				} catch (NumberFormatException nfe) {
					return;
				}

				// _ganttChart.getGanttComposite().clearChart();

				_ganttChart.dispose();
				int flags = 0;

				if (noHbar.getSelection())
					flags |= IGanttFlags.H_SCROLL_NONE;
				if (fixedHbar.getSelection())
					flags |= IGanttFlags.H_SCROLL_FIXED_RANGE;
				if (infHbar.getSelection())
					flags |= IGanttFlags.H_SCROLL_INFINITE;

				ISettings toUse = null;
				if (lockHeader.getSelection())
					toUse = new FixedHeaderSettings();

				_ganttChart = new GanttChart(_vfChart, flags, toUse);
				_ganttComposite = _ganttChart.getGanttComposite();
				_vfChart.setContent(_ganttChart);

				for (int i = 0; i < numberEvents; i++) {
					Calendar cal = Calendar.getInstance();
					GanttEvent predEvent = null;
					if (i != 0) {
						predEvent = ((GanttEvent) _ganttChart.getGanttComposite().getEvents().get(i - 1));
						cal = predEvent.getActualEndDate();
					}
					Calendar cStartDate = cal;
					Calendar cEndDate = (Calendar) cStartDate.clone();
					if (increase.getSelection())
						cEndDate.add(Calendar.DATE, 1);

					GanttEvent ganttEvent = null;
					if (plannedDates.getSelection()) {
						Calendar plannedStart = (Calendar) cStartDate.clone();
						plannedStart.add(Calendar.DATE, -10);
						Calendar plannedEnd = (Calendar) cEndDate.clone();
						plannedEnd.add(Calendar.DATE, 10);

						ganttEvent = new GanttEvent(_ganttChart, null, "Event_" + (i + 1), plannedStart, plannedEnd, cStartDate, cEndDate, 0);
					} else {
						ganttEvent = new GanttEvent(_ganttChart, null, "Event_" + (i + 1), cStartDate, cEndDate, 0);
					}

					if (limits.getSelection()) {
						Calendar noBefore = Calendar.getInstance();
						noBefore.setTime(cStartDate.getTime());
						noBefore.add(Calendar.DATE, -10);
						if (i % 3 == 0 || i % 5 == 0)
							ganttEvent.setNoMoveBeforeDate(noBefore);

						Calendar noBeyond = Calendar.getInstance();
						noBeyond.setTime(cEndDate.getTime());
						noBeyond.add(Calendar.DATE, 10);

						if (i % 3 == 0 || i % 2 == 0)
							ganttEvent.setNoMoveAfterDate(noBeyond);
					}

					if (connEvents.getSelection()) {
						if (i < connNumber.getSelection()) {
							if (rnd.getSelection()) {
								Random r = new Random();
								_ganttChart.getGanttComposite().addConnection(predEvent, ganttEvent, ColorCache.getColor(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
							} else {
								_ganttChart.getGanttComposite().addConnection(predEvent, ganttEvent);
							}
						}
					}
				}

				moveFocus();
			}

		});

		clear.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				_ganttChart.getGanttComposite().clearChart();
				moveFocus();
			}
		});

		redrawButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				_ganttChart.getGanttComposite().refresh();
				moveFocus();
			}
		});

		return comp;
	}

	private Composite createBottom(Composite parent) {
		Composite outer = new Composite(parent, SWT.NONE);
		outer.setLayout(new GridLayout(1, true));

		Group comp = new Group(outer, SWT.NONE);
		comp.setText("Gantt Chart Operations");
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		comp.setLayout(new GridLayout(10, false));

		Button jumpEarliest = new Button(comp, SWT.PUSH);
		jumpEarliest.setText("Jump to earliest event");

		Button jumpLatest = new Button(comp, SWT.PUSH);
		jumpLatest.setText("Jump to latest event");

		Button selectFirstEvent = new Button(comp, SWT.PUSH);
		selectFirstEvent.setText("Show first event");

		Button selectMidEvent = new Button(comp, SWT.PUSH);
		selectMidEvent.setText("Show middle event");

		Button selectLastEvent = new Button(comp, SWT.PUSH);
		selectLastEvent.setText("Show last event");

		Button jumpToCurrentTimeLeft = new Button(comp, SWT.PUSH);
		jumpToCurrentTimeLeft.setText("Today [Left]");
		Button jumpToCurrentTimeCenter = new Button(comp, SWT.PUSH);
		jumpToCurrentTimeCenter.setText("Today [Center]");
		Button jumpToCurrentTimeRight = new Button(comp, SWT.PUSH);
		jumpToCurrentTimeRight.setText("Today [Right]");

		Button moveEventsLeft = new Button(comp, SWT.PUSH);
		moveEventsLeft.setText("Move -1");
		Button moveEventsRight = new Button(comp, SWT.PUSH);
		moveEventsRight.setText("Move +1");

		Button zIn = new Button(comp, SWT.PUSH);
		Button zOut = new Button(comp, SWT.PUSH);
		zIn.setText("Zoom In");
		zOut.setText("Zoom Out");

		Button showPlanned = new Button(comp, SWT.PUSH);
		showPlanned.setText("Toggle Planned Dates");

		Button showDays = new Button(comp, SWT.PUSH);
		showDays.setText("Toggle Dates On Events");

		moveEventsLeft.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveAllEvents(Calendar.DATE, -1);
				moveFocus();
			}
		});

		moveEventsRight.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveAllEvents(Calendar.DATE, 1);
				moveFocus();
			}
		});

		jumpEarliest.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				_ganttChart.getGanttComposite().jumpToEarliestEvent();
				moveFocus();
			}
		});

		jumpLatest.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				_ganttChart.getGanttComposite().jumpToLatestEvent();
				moveFocus();
			}
		});

		selectFirstEvent.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				_ganttChart.getGanttComposite().setTopItem((GanttEvent) _ganttChart.getGanttComposite().getEvents().get(0), SWT.CENTER);
				moveFocus();
			}
		});

		selectLastEvent.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				_ganttChart.getGanttComposite().setTopItem((GanttEvent) _ganttChart.getGanttComposite().getEvents().get(_ganttChart.getGanttComposite().getEvents().size() - 1), SWT.CENTER);
				moveFocus();
			}
		});

		selectMidEvent.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				if (_ganttChart.getGanttComposite().getEvents().size() < 2)
					return;
				GanttEvent ge = (GanttEvent) _ganttChart.getGanttComposite().getEvents().get(_ganttChart.getGanttComposite().getEvents().size() / 2);
				_ganttChart.getGanttComposite().setTopItem(ge, SWT.CENTER);
				moveFocus();
			}
		});

		jumpToCurrentTimeLeft.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				Calendar currentDate = Calendar.getInstance();

				_ganttChart.getGanttComposite().setDate(currentDate, SWT.LEFT);
				moveFocus();
			}
		});
		jumpToCurrentTimeCenter.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				Calendar currentDate = Calendar.getInstance();

				_ganttChart.getGanttComposite().setDate(currentDate, SWT.CENTER);
				moveFocus();
			}
		});
		jumpToCurrentTimeRight.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				Calendar currentDate = Calendar.getInstance();

				_ganttChart.getGanttComposite().setDate(currentDate, SWT.RIGHT);
				moveFocus();
			}
		});

		zIn.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				_ganttComposite.zoomIn(true);
			}

		});

		zOut.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				_ganttComposite.zoomOut(true);
			}

		});

		showPlanned.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				_ganttComposite.setShowPlannedDates(!_ganttComposite.isShowingPlannedDates());
			}

		});

		showDays.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				_ganttComposite.setShowDaysOnEvents(!_ganttComposite.isShowingDaysOnEvents());
			}

		});
		return outer;
	}

	private void moveAllEvents(int calendarObj, int amount) {
		List events = _ganttChart.getGanttComposite().getEvents();
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
