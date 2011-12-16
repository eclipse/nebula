/*
 * Copyright (C) 2005 David Orme <djo@coconut-palm-software.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Orme     - Initial API and implementation
 */
package org.eclipse.nebula.snippets.compositetable;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.nebula.widgets.compositetable.month.MonthCalendar;
import org.eclipse.nebula.widgets.compositetable.timeeditor.CalendarableItem;
import org.eclipse.nebula.widgets.compositetable.timeeditor.EventContentProvider;
import org.eclipse.nebula.widgets.compositetable.timeeditor.EventCountProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class MonthCalendarSnippet {

    private Shell sShell = null; // @jve:decl-index=0:visual-constraint="10,10"

    private static class Event {
        public boolean allDay = false;

        public Date startTime;

        public Date endTime;

        public String description;

        public Event(Date startTime, Date endTime, String description) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.description = description;
        }

        public Event(String description) {
            this.allDay = true;
            this.description = description;
        }
    }

    private Date time(int hour, int minutes) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(new Date());
        gc.set(Calendar.HOUR_OF_DAY, hour);
        gc.set(Calendar.MINUTE, minutes);
        return gc.getTime();
    }

    private Date beginningOfMonth(Date input) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(input);
        gc.set(Calendar.DATE, 1);
        gc.set(Calendar.HOUR_OF_DAY, 0);
        gc.set(Calendar.MINUTE, 0);
        gc.set(Calendar.SECOND, 0);
        gc.set(Calendar.MILLISECOND, 0);
        return gc.getTime();
    }
    
    private Event[][] events = new Event[][] {
            { new Event(time(5, 45), time(9, 45), "Stand-up meeting"),
                    new Event(time(11, 00), time(12, 15), "Meet with customer") },
            {},
            {},
            { new Event("EclipseCon"),
                    new Event(time(7, 50), time(9, 00), "Stand-up meeting"),
                    new Event(time(10, 15), time(12, 00), "Work on prototype") },
            {
                    new Event("EclipseCon"),
                    new Event(time(8, 30), time(9, 30), "Stand-up meeting"),
                    new Event(time(10, 00), time(13, 15), "Meet with customer"),
                    new Event(time(12, 45), time(14, 15), "RC1 due"),
                    new Event(time(13, 45), time(14, 15), "Way too much work"),
                    new Event(time(10, 00), time(13, 30), "Europa meeting") },
            { new Event("EclipseCon") },
            {
                    new Event(time(8, 30), time(11, 30), "Stand-up meeting"),
                    new Event(time(10, 00), time(12, 15), "Meet with customer1"),
                    new Event(time(11, 45), time(12, 15), "Meet with customer2"),
                    new Event(time(11, 00), time(11, 15), "Meet with customer3") },
            {},
            { new Event(time(8, 50), time(9, 00), "Stand-up meeting"),
                    new Event(time(10, 15), time(12, 00), "Work on prototype") },
            { new Event(time(8, 45), time(9, 45), "Stand-up meeting"),
                    new Event(time(11, 00), time(12, 15), "Meet with customer") },
            {},
            {},
            { new Event(time(8, 12), time(9, 00), "Stand-up meeting"),
                    new Event(time(10, 15), time(12, 00), "Work on prototype") },
            {},
            {},
            {
                    new Event(time(8, 30), time(11, 30), "Stand-up meeting"),
                    new Event(time(10, 00), time(12, 15), "Meet with customer"),
                    new Event(time(11, 45), time(12, 15), "Meet with customer"),
                    new Event(time(11, 00), time(2, 45), "Meet with customer") },
            { new Event(time(9, 50), time(9, 00), "Stand-up meeting"),
                    new Event(time(10, 15), time(12, 00), "Work on prototype") },
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, };

    /**
     * This method initializes monthCalendar
     * 
     */
    private void createMonthCalendar() {
        MonthCalendar dayEditor = new MonthCalendar(sShell, SWT.NONE);
        dayEditor.setEventCountProvider(eventCountProvider);
        dayEditor.setEventContentProvider(eventContentProvider);
        dayEditor.setStartDate(beginningOfMonth(new Date()));
    }

    private EventCountProvider eventCountProvider = new EventCountProvider() {
        public int getNumberOfEventsInDay(Date day) {
            return events[getOffset(day)].length;
        }
    };

    private EventContentProvider eventContentProvider = new EventContentProvider() {
        public void refresh(Date day, CalendarableItem[] controls) {
            int dayOffset = getOffset(day);

            for (int event = 0; event < events[dayOffset].length; ++event) {
                fillEvent(controls[event], events[dayOffset][event]);
            }
        }

        private void fillEvent(CalendarableItem c, Event event) {
            if (event.allDay) {
                c.setAllDayEvent(true);
            } else {
                c.setStartTime(event.startTime);
                c.setEndTime(event.endTime);
            }
            c.setData(event);
            c.setText(event.description);
        }
    };

    protected int getOffset(Date day) {
        GregorianCalendar dateToFind = new GregorianCalendar();
        dateToFind.setTime(day);
        GregorianCalendar dateToTest = new GregorianCalendar();
        dateToTest.setTime(beginningOfMonth(new Date()));
        for (int i = 0; i < events.length; ++i) {
            if (dateToTest.get(Calendar.MONTH) == dateToFind.get(Calendar.MONTH)
                    && dateToTest.get(Calendar.DAY_OF_MONTH) == dateToFind.get(Calendar.DAY_OF_MONTH)
                    && dateToTest.get(Calendar.YEAR) == dateToFind.get(Calendar.YEAR)) {
                return i;
            }
            dateToTest.add(Calendar.DAY_OF_MONTH, 1);
        }
        return 1;   // Return a day that has no data
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        Display display = Display.getDefault();
        MonthCalendarSnippet thisClass = new MonthCalendarSnippet();
        thisClass.createSShell();
        thisClass.sShell.open();

        while (!thisClass.sShell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }

    /**
     * This method initializes sShell
     */
    private void createSShell() {
        sShell = new Shell();
        sShell.setText("Shell");
        sShell.setLayout(new FillLayout());
        createMonthCalendar();
        sShell.setSize(new org.eclipse.swt.graphics.Point(624, 578));
    }

}
