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


/**
 * Convenience class for creating a checkpoint instead of using the constructors on GanttEvent.
 * 
 */
public class GanttCheckpoint extends GanttEvent {

	/**
	 * {@inheritDoc}
	 */
	public GanttCheckpoint(final GanttChart parent, final String name, final Calendar date) {
		super(parent, null, name, date);
	}

	/**
	 * {@inheritDoc}
	 */
	public GanttCheckpoint(final GanttChart parent, final Object data, final String name, final Calendar date) {
		super(parent, data, name, date);
	}
	
}
