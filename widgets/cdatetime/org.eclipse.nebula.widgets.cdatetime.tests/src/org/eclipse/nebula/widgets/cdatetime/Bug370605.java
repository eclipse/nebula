/****************************************************************************
 * Copyright (c) 2012 Scott Klein
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Scott Klein <scott.klein@goldenhour.com> - initial API and implementation
 *****************************************************************************/

package org.eclipse.nebula.widgets.cdatetime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.nebula.cwt.test.AbstractVTestCase;
import org.eclipse.swt.SWT;

public class Bug370605 extends AbstractVTestCase {

	private static final String FALL_EPOCH = "11/04/2012 00:00.00 PDT";

	private static Date now = new Date();

	private static final String PATTERN_TIMESTAMP = "MM/dd/yyyy HH:mm.ss z";

	private static final String SPRING_EPOCH = "03/11/2012 01:00.00 PST";

	private static final TimeZone TIMEZONE_DST = TimeZone
			.getTimeZone("US/Pacific");

	private static final TimeZone TIMEZONE_INITIAL = TimeZone
			.getTimeZone("Pacific/Kiritimati");

	private static Collection<TimeZone> timezones = new ArrayList<TimeZone>();

	private static final String[] TIMEZONES = { "UTC", "MIT", "HST", "AST",
			"PST", "MST", "CST", "EST", "PRT", "AGT", "BET", "CET", "EET",
			"NET", "IST", "VST", "JST", "NST" };

	private CdtTester tester;

	/**
	 * Using traverse arrow keys, move through each field of the control
	 *
	 * Stop traversing if we have cycled back to the starting field, or we reach
	 * the time zone field
	 *
	 * @return true, if the time zone field has been found. false otherwise.
	 */
	private boolean moveToTimeZoneField(int calendarFieldSelection) {
		CDateTime cdt = tester.getCDateTime();
		int initialField = cdt.getCalendarField();

		int currentField = -1;
		do {
			keyPress(SWT.ARROW_RIGHT);
			keyPress('\r');
			currentField = cdt.getCalendarField();
		} while (currentField != initialField
				&& currentField != calendarFieldSelection);

		return currentField != Calendar.ZONE_OFFSET;
	}

	public void setUp() throws Exception {
		timezones.clear();
		for (final String timezone : TIMEZONES) {
			timezones.add(TimeZone.getTimeZone(timezone));
		}
		tester = new CdtTester(getShell(), CDT.BORDER | CDT.TAB_FIELDS
				| CDT.CLOCK_24_HOUR | CDT.DATE_LONG | CDT.TIME_MEDIUM);

	}

	/**
	 * This uses a single test with a known DST zone (PST).
	 *
	 * <ol>
	 * Test Plans
	 * <li>roll over the epoch time and ensure we roll over both 1 AM times
	 * (Daylight and Standard).</li>
	 * <li>roll backwards and ensure we can do that also</li>
	 * <li>manually type in the epoch time and ensure we get the Java default
	 * time</li>
	 * </ol>
	 *
	 * @throws Exception
	 */
	public void testDaylightSavingsFallRoll() throws Exception {
		TimeZone.setDefault(TIMEZONE_DST);
		SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_TIMESTAMP);
		Date fallEpochDate = sdf.parse(FALL_EPOCH);
		Calendar fallEpochCal = Calendar.getInstance();
		fallEpochCal.setTime(fallEpochDate);

		tester.setPattern(PATTERN_TIMESTAMP);
		tester.setTimeZone(TIMEZONE_DST);
		tester.setSelection(fallEpochDate);

		click(tester.getTextWidget());

		moveToTimeZoneField(Calendar.HOUR_OF_DAY);

		// VERIFY: Move from 12 AM to 1 AM
		// Ignoring time zone DST during this test
		keyPress(SWT.ARROW_UP);
		keyPress('\r');

		Date modifiedDate = tester.getCDateTime().getCalendarTime();
		Calendar modifiedCal = Calendar.getInstance();
		modifiedCal.setTime(modifiedDate);

		assertEquals(fallEpochCal.get(Calendar.HOUR_OF_DAY) + 1,
				modifiedCal.get(Calendar.HOUR_OF_DAY));

		// VERFIY: Move from 1 AM to 1 AM
		// Ignoring time zone DST during this test
		keyPress(SWT.ARROW_UP);
		keyPress('\r');

		modifiedDate = tester.getCDateTime().getCalendarTime();
		modifiedCal = Calendar.getInstance();
		modifiedCal.setTime(modifiedDate);

		assertEquals(fallEpochCal.get(Calendar.HOUR_OF_DAY) + 1,
				modifiedCal.get(Calendar.HOUR_OF_DAY));

		// VERIFY: Move from 1 AM to 2 AM
		keyPress(SWT.ARROW_UP);
		keyPress('\r');

		modifiedDate = tester.getCDateTime().getCalendarTime();
		modifiedCal = Calendar.getInstance();
		modifiedCal.setTime(modifiedDate);

		assertEquals(fallEpochCal.get(Calendar.HOUR_OF_DAY) + 2,
				modifiedCal.get(Calendar.HOUR_OF_DAY));

		// VERIFY: Move backwards from 2 AM to 1 AM
		keyPress(SWT.ARROW_DOWN);
		keyPress('\r');

		modifiedDate = tester.getCDateTime().getCalendarTime();
		modifiedCal = Calendar.getInstance();
		modifiedCal.setTime(modifiedDate);

		assertEquals(fallEpochCal.get(Calendar.HOUR_OF_DAY) + 1,
				modifiedCal.get(Calendar.HOUR_OF_DAY));

		// VERIFY: Move backwards from 1 AM to 1 AM
		keyPress(SWT.ARROW_DOWN);
		keyPress('\r');

		modifiedDate = tester.getCDateTime().getCalendarTime();
		modifiedCal = Calendar.getInstance();
		modifiedCal.setTime(modifiedDate);

		assertEquals(fallEpochCal.get(Calendar.HOUR_OF_DAY) + 1,
				modifiedCal.get(Calendar.HOUR_OF_DAY));

		// VERIFY: Move backwards from 1 AM to 12 AM
		keyPress(SWT.ARROW_DOWN);
		keyPress('\r');

		modifiedDate = tester.getCDateTime().getCalendarTime();
		modifiedCal = Calendar.getInstance();
		modifiedCal.setTime(modifiedDate);

		assertEquals(fallEpochCal.get(Calendar.HOUR_OF_DAY),
				modifiedCal.get(Calendar.HOUR_OF_DAY));

		// VERIFY: Manually type in 1 AM
		keyPress(SWT.KEYPAD_0);
		keyPress(SWT.KEYPAD_1);
		keyPress('\r');

		modifiedDate = tester.getCDateTime().getCalendarTime();
		modifiedCal = Calendar.getInstance();
		modifiedCal.setTime(modifiedDate);

		assertEquals(1, modifiedCal.get(Calendar.HOUR_OF_DAY));

	}

	/**
	 * This uses a single test with a known DST zone (PST).
	 *
	 * <ol>
	 * Test Plans
	 * <li>roll over the epoch time and ensure we roll over 1 AM directly into 3
	 * AM</li>
	 * <li>roll backwards and ensure we can do that also</li>
	 * <li>manually type in the epoch time and ensure we get the Java default
	 * time</li>
	 * </ol>
	 *
	 * @throws Exception
	 */
	public void testDaylightSavingsSpringRoll() throws Exception {
		TimeZone.setDefault(TIMEZONE_DST);
		SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_TIMESTAMP);
		Date springEpochDate = sdf.parse(SPRING_EPOCH);
		Calendar springEpochCal = Calendar.getInstance();
		springEpochCal.setTime(springEpochDate);

		tester.setPattern(PATTERN_TIMESTAMP);
		tester.setTimeZone(TIMEZONE_DST);
		tester.setSelection(springEpochDate);

		click(tester.getTextWidget());

		moveToTimeZoneField(Calendar.HOUR_OF_DAY);

		// VERIFY: Spring from 1 AM to 3 AM using arrow keys
		keyPress(SWT.ARROW_UP);
		keyPress('\r');

		Date modifiedDate = tester.getCDateTime().getCalendarTime();
		Calendar modifiedCal = Calendar.getInstance();
		modifiedCal.setTime(modifiedDate);

		assertEquals(springEpochCal.get(Calendar.HOUR_OF_DAY) + 2,
				modifiedCal.get(Calendar.HOUR_OF_DAY));

		// VERIFY: Spring backwards from 3 AM to 1 AM using arrow keys
		keyPress(SWT.ARROW_DOWN);
		keyPress('\r');

		modifiedDate = tester.getCDateTime().getCalendarTime();
		modifiedCal = Calendar.getInstance();
		modifiedCal.setTime(modifiedDate);

		assertEquals(springEpochCal.get(Calendar.HOUR_OF_DAY),
				modifiedCal.get(Calendar.HOUR_OF_DAY));

		// VERIFY: Manully enter 2 AM and verify it reverts to 3 AM
		keyPress(SWT.KEYPAD_0);
		keyPress(SWT.KEYPAD_2);

		modifiedDate = tester.getCDateTime().getCalendarTime();
		modifiedCal = Calendar.getInstance();
		modifiedCal.setTime(modifiedDate);

		assertEquals(3, modifiedCal.get(Calendar.HOUR_OF_DAY));

	}

	/**
	 * Using a CDateTime that is not configured for altering time zone, verify
	 * that the time zone field is simply not able to be traversed into.
	 *
	 * @throws Exception
	 */
	public void testNoTimeZoneSelection() throws Exception {
		tester.setPattern(PATTERN_TIMESTAMP);
		tester.setTimeZone(TIMEZONE_INITIAL);
		tester.setSelection(now);

		click(tester.getTextWidget());

		// VERIFY: TimeZone is set to the initial time zone
		final TimeZone wtzOriginal = tester.getCDateTime().getTimeZone();
		assertEquals(TIMEZONE_INITIAL.getDisplayName(),
				wtzOriginal.getDisplayName());

		// VERIFY: Move over to time zone, but this should not be
		// possible, so the utility method should return false
		assertTrue(moveToTimeZoneField(Calendar.ZONE_OFFSET));
	}

	/**
	 * Using a CDateTime that is configured for altering the time zone, verify
	 * that our original time zone that we set to the control is still in the
	 * control on instantiation. Here we use a time zone that is *not* in the
	 * list to ensure we do no alter the programatic expectation.
	 *
	 * We then step over to the time zone field and verify that the controls
	 * time zone is altered in accordance with our list of time zones that we
	 * set into the control
	 *
	 * @throws Exception
	 */
	public void testTimeZoneSelection() throws Exception {
		tester.setPattern(PATTERN_TIMESTAMP, timezones.toArray(new TimeZone[0]));
		tester.setTimeZone(TIMEZONE_INITIAL);
		tester.setSelection(now);

		// VERIFY: TimeZone is set to the initial time zone
		final TimeZone wtzOriginal = tester.getCDateTime().getTimeZone();
		assertEquals(TIMEZONE_INITIAL.getDisplayName(),
				wtzOriginal.getDisplayName());

		// Move to the TimeZone field
		click(tester.getTextWidget());
		moveToTimeZoneField(Calendar.ZONE_OFFSET);

		SimpleDateFormat sdf = new SimpleDateFormat("z");

		// VERIFY: Flip forward through each available timezone and that it
		// changes in the control
		for (String timezone : TIMEZONES) {
			keyPress(SWT.ARROW_UP);
			keyPress('\r');

			final TimeZone actualTimeZone = tester.getCDateTime().getTimeZone();
			final TimeZone expectedTimeZone = TimeZone.getTimeZone(timezone);

			// get the actual timezone format we expect, and the actual one
			sdf.setTimeZone(actualTimeZone);
			final String actualTimeZoneDisplay = sdf.format(new Date());
			sdf.setTimeZone(expectedTimeZone);
			final String expectedTimeZoneDisplay = sdf.format(new Date());

			assertEquals(actualTimeZoneDisplay, expectedTimeZoneDisplay);
		}

		// Get back to the "1st" time zone in the list
		keyPress(SWT.ARROW_UP);
		keyPress('\r');

		// VERIFY: Flip backward through each available timezone and that it
		// changes in the control
		for (int idx = TIMEZONES.length - 1; idx >= 0; idx--) {
			keyPress(SWT.ARROW_DOWN);
			keyPress('\r');

			final TimeZone actualTimeZone = tester.getCDateTime().getTimeZone();
			final TimeZone expectedTimeZone = TimeZone
					.getTimeZone(TIMEZONES[idx]);

			// get the actual timezone format we expect, and the actual one
			sdf.setTimeZone(actualTimeZone);
			final String actualTimeZoneDisplay = sdf.format(new Date());
			sdf.setTimeZone(expectedTimeZone);
			final String expectedTimeZoneDisplay = sdf.format(new Date());

			assertEquals(actualTimeZoneDisplay, expectedTimeZoneDisplay);

		}
	}
}