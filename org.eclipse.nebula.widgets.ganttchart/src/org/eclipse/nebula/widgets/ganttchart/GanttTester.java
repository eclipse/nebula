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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class GanttTester {

	private GanttChart _ganttChart;
	private GanttComposite _ganttComposite;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new GanttTester();
	}
	
	public GanttTester() {
		Display disp = new Display();
		Shell shell = new Shell(disp);
		shell.setText("Gantt Tester");
		GridLayout lay = new GridLayout();
		lay.marginHeight = lay.marginWidth = 1;
		shell.setLayout(lay);

		Composite comp = new Composite(shell, SWT.NONE);
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comp.setLayout(new GridLayout(10, false));

		final Combo combo = new Combo(comp, SWT.NONE);
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
		combo.select(6);

		Button button = new Button(comp, SWT.PUSH);
		button.setText("Create");
		
		final Button increase = new Button(comp, SWT.CHECK);
		increase.setText("Increase Dates");
		increase.setSelection(true);

		final Button rnd = new Button(comp, SWT.CHECK);
		rnd.setText("Random Conn Colors");
		rnd.setSelection(true);
		
		Button clear = new Button(comp, SWT.PUSH);
		clear.setText("Clear");
		
		Button redrawButton = new Button(comp, SWT.PUSH);
		redrawButton.setText("Redraw");

		Button jumpEarliest = new Button(comp, SWT.PUSH);
		jumpEarliest.setText("Jump to earliest event");

		Button jumpLatest= new Button(comp, SWT.PUSH);
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

		_ganttChart = new GanttChart(shell, SWT.MULTI);
		_ganttChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		_ganttComposite = _ganttChart.getGanttComposite();
		

		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				int numberEvents = 0;
				try {
					numberEvents = Integer.parseInt(combo.getText());
				} catch (NumberFormatException nfe) {
					return;
				}

				_ganttChart.getGanttComposite().clearChart();
				for (int i = 0; i < numberEvents; i++) {
					Calendar cal = Calendar.getInstance();
					GanttEvent predEvent = null;
					if (i != 0) {
						predEvent = ((GanttEvent) _ganttChart.getGanttComposite().getEvents().get(i - 1));
						cal = predEvent.getEndDate();
					}
					Calendar cStartDate = cal;
					Calendar cEndDate = (Calendar) cStartDate.clone();
					if (increase.getSelection())
						cEndDate.add(Calendar.DATE, 1);

					GanttEvent ganttEvent = new GanttEvent(_ganttChart, null, "Event_" + i, cStartDate, cEndDate, 0);
					Calendar noBefore = Calendar.getInstance();
					noBefore.setTime(cStartDate.getTime());
					noBefore.add(Calendar.DATE, -20);
					if (i % 3 == 0 || i % 5 == 0)
						ganttEvent.setNoMoveBeforeDate(noBefore);
					
					Calendar noBeyond = Calendar.getInstance();
					noBeyond.setTime(cEndDate.getTime());
					noBeyond.add(Calendar.DATE, 20);
					
					if (i % 3 == 0 || i % 2 == 0)
						ganttEvent.setNoMoveAfterDate(noBeyond);

					if (i != 0) {
						if (rnd.getSelection()) {
							Random r = new Random();						
							_ganttChart.getGanttComposite().addConnection(predEvent, ganttEvent, ColorCache.getColor(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
						}
						else {
							_ganttChart.getGanttComposite().addConnection(predEvent, ganttEvent);
						}
					}
				}
				
				moveFocus();
			}

		});
		
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

		//shell.setSize(1800, 1000);		
		//shell.setLocation(new Point(100, 100));
		shell.setMaximized(true);
		shell.open();
		while (!shell.isDisposed()) {
			if (!disp.readAndDispatch())
				disp.sleep();
		}
		disp.dispose();
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
			}
			else {
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
}
