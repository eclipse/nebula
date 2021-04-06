/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.compositetable.day;


/**
 * Abstract class CalendarableItemEventHandler.  An abstract class defining
 * the API for objects that implement strategy pattern services such as
 * insert, edit, and delete from a DayEditor object.
 * 
 * @since 3.2
 */
public abstract class CalendarableItemEventHandler {
	/**
	 * Process this CalenderableItemEvent, please.
	 * 
	 * @param e
	 *            The CalendarableItemEvent to process.
	 */
	public void handleRequest(CalendarableItemEvent e) {
		// NOOP by default
	}
	
	/**
	 * This CalendarableItemEvent has been handled.
	 * 
	 * @param e
	 *            The CalendarableItemEvent that was processed, including the
	 *            results of processing the event.
	 */
	public void requestHandled(CalendarableItemEvent e) {
		// NOOP by default
	}

}
