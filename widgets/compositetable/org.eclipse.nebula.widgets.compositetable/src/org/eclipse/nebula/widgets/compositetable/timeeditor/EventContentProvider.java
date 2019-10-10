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
 * A strategy pattern object that can refresh all of the event controls
 * for a given day.
 * 
 * @since 3.2
 */
public abstract class EventContentProvider {
	/**
	 * Refreshes the contents of the specified event controls for the specified
	 * day.
	 * 
	 * @param day The day
	 * @param controls The controls to refresh
	 */
	abstract public void refresh(Date day, CalendarableItem[] controls);
}
