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

package org.eclipse.nebula.widgets.compositetable.timeeditor;

import java.util.Date;

/**
 * A strategy pattern object that returns the number of events to display for a 
 * specific time period.
 * 
 * @since 3.2
 */
public abstract class EventCountProvider {
	/**
	 * Returns the number of events to display on a specific day.
	 *  
	 * @param day The day to query.
	 * @return The number of events on the specified day.
	 */
	abstract public int getNumberOfEventsInDay(Date day);
}
