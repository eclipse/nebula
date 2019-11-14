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

import org.eclipse.nebula.widgets.compositetable.timeeditor.CalendarableItem;

/**
 * Instances of this class are sent as a result of
 * events such insertion, deletion, or disposal of 
 * CalendarableItem objects.

 * @since 3.2
 *
 */
public class CalendarableItemEvent {
	/**
	 * Set to true by default.  Setting this field to false aborts the 
	 * requested operation, as appropriate.  For example, setting this field
	 * to false in a deletion operation cancels the delete operation.
	 */
	public boolean doit = true;
	
	/**
	 * The CalenderableItem that should be processed in this operation.
	 */
	public CalendarableItem calendarableItem;
	
	/**
	 * Returns the result of the operation.  In the case of an edit operation,
	 * this must contain (oldStartTime, oldEndTime, newStartTime, newEndTime)
	 * if any of (startTime, endTime) have changed.
	 */
	public Object result = null;
	
	public String toString() {
		return "calendarableItem = " + calendarableItem;
	}
}
