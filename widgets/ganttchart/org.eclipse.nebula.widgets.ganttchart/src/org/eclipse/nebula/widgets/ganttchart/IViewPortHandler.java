/*******************************************************************************
 * Copyright (c) 2013 Dirk Fauth and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Dirk Fauth <dirk.fauth@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.ganttchart;

public interface IViewPortHandler {

	void scrollingLeft(final int diffCount);
	
	void scrollingRight(final int diffCount);

	/**
	 * Jumps to the next month.
	 */
	public void nextMonth();

	/**
	 * Jumps to the previous month.
	 */
	public void prevMonth();

	/**
	 * Jumps one week forward.
	 */
	public void nextWeek();

	/**
	 * Jumps one week backwards.
	 */
	public void prevWeek();

	/**
	 * Jumps to the next hour.
	 */
	public void nextHour();

	/**
	 * Jumps to the previous hour.
	 */
	public void prevHour();

	/**
	 * Jumps one day forward.
	 */
	void nextDay();

	/**
	 * Jumps one day backwards.
	 */
	void prevDay();
}
