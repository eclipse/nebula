/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.ganttchart;

/**
 * A convenience class for creating a GanttScope instead of using the specific constructors on the GanttEvent.
 *  
 */
public final class GanttScope extends GanttEvent {

	/**
	 * {@inheritDoc}
	 */
	public GanttScope(final GanttChart parent, final String name) {
		super(parent, null, name);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public GanttScope(final GanttChart parent, final Object data, final String name) {
		super(parent, data, name);
	}
	
}
