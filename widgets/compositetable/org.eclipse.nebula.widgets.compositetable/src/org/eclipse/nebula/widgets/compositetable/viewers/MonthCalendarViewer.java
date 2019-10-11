/*******************************************************************************
 * Copyright (c) 2008 Trevor S. Kaufman and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Trevor S. Kaufman - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.compositetable.viewers;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.compositetable.month.MonthCalendar;
import org.eclipse.nebula.widgets.compositetable.month.MonthCalendarSelectedDay;
import org.eclipse.nebula.widgets.compositetable.timeeditor.CalendarableItem;
import org.eclipse.nebula.widgets.compositetable.timeeditor.EventContentProvider;
import org.eclipse.nebula.widgets.compositetable.timeeditor.EventCountProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class MonthCalendarViewer extends ContentViewer {

	MonthCalendar calendar = null;
	private IStructuredSelection selection = StructuredSelection.EMPTY;

	private MouseListener mouseListener = new MouseAdapter() {

		public void mouseDown(MouseEvent e) {
			System.out.println("mouseDown: MouseListener: " + e);
			changeSelection();
		}
	};

	public MonthCalendarViewer(Composite parent, int style) {
		this(new MonthCalendar(parent, style));
	}

	public MonthCalendarViewer(MonthCalendar monthCalendar) {
		if (monthCalendar == null) {
			throw new IllegalArgumentException("MonthCalendar cannot be null");
		}

		calendar = monthCalendar;

		calendar.setStartDate(new Date());

		super.hookControl(calendar);

		calendar.addMouseListener(mouseListener);
	}

	public Control getControl() {
		return getMonthCalendar();
	}

	public MonthCalendar getMonthCalendar() {
		return calendar;
	}

	public ISelection getSelection() {
		return selection;
	}

	public void refresh() {
		calendar.refresh();
	}

	protected void inputChanged(Object input, Object oldInput) {
		refresh();
	}

	public void setSelection(ISelection selection, boolean reveal) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			if (this.selection.equals(structuredSelection)) {
				return;
			}

			Date selectedDate = null;

			if (structuredSelection.getFirstElement() instanceof MonthCalendarSelectedDay) {
				selectedDate = ((MonthCalendarSelectedDay) structuredSelection.getFirstElement()).date;
			} else if (structuredSelection.getFirstElement() instanceof Date) {
				selectedDate = (Date) structuredSelection.getFirstElement();
			} else {
				return;
			}
			
			System.out.println("setSelection: selectedDate " + selectedDate);

			if (calendar.getSelectedDay().date.equals(selectedDate)) {
				return;
			}

			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(selectedDate);
			Point monthPosition = new Point(cal.get(Calendar.DAY_OF_WEEK), cal.get(Calendar.WEEK_OF_MONTH));
			this.selection = new StructuredSelection(new MonthCalendarSelectedDay(selectedDate, monthPosition));
			
			calendar.select(selectedDate);

			this.fireSelectionChanged(new SelectionChangedEvent(this,
					this.selection));
		}

	}

	protected void changeSelection() {
		MonthCalendarSelectedDay selectedDay = calendar.getSelectedDay();
		StructuredSelection sel = new StructuredSelection(selectedDay);
		this.selection = sel;
		this.fireSelectionChanged(new SelectionChangedEvent(this,
				this.selection));
	}

	public void setContentProvider(IContentProvider contentProvider) {
		if (!(contentProvider instanceof IMonthCalendarContentProvider)) {
			throw new IllegalArgumentException(
					"content provider must be of type IMonthCalendarContentProvider");
		}
		super.setContentProvider(contentProvider);
		calendar.setEventContentProvider(eventContentProvider);
		calendar.setEventCountProvider(eventCountProvider);
	}

	public void setLabelProvider(IBaseLabelProvider labelProvider) {
		if (!(labelProvider instanceof ILabelProvider)) {
			throw new IllegalArgumentException(
					"label provider must be of type ILabelProvider");
		}
		super.setLabelProvider(labelProvider);
	}

	private EventContentProvider eventContentProvider = new EventContentProvider() {

		public void refresh(Date day, CalendarableItem[] controls) {
			Object[] elements = ((IMonthCalendarContentProvider) getContentProvider())
					.getElements(day, getInput());
			int count = Math.min(elements.length, controls.length);
			for (int i = 0; i < count; i++) {
				if (controls[i] == null)
					continue;
				controls[i].setText(((ILabelProvider) getLabelProvider())
						.getText(elements[i]));
				controls[i].setImage(((ILabelProvider) getLabelProvider())
						.getImage(elements[i]));

				// Not sure how to do this properly..
				// It appears that none of these fields matter for
				// the display of the MonthCalendar..
				controls[i].setStartTime(day);
				controls[i].setEndTime(day);
				controls[i].setAllDayEvent(false);
				controls[i].setContinued(SWT.NONE);
			}
		}
	};

	private EventCountProvider eventCountProvider = new EventCountProvider() {

		public int getNumberOfEventsInDay(Date day) {
			return ((IMonthCalendarContentProvider) getContentProvider())
					.getElements(day, getInput()).length;
		}

	};

}
