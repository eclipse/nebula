/*******************************************************************************
 * Copyright (c) 2013 Dirk Fauth and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Dirk Fauth <dirk.fauth@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.ganttchart;

import java.util.Calendar;

public interface IEventFactory {
	
	/**
	 * Creates a new GanttEvent and adds it to the given GanttChart.
	 * If a GanttSection is specified, it is directly added to the
	 * GanttSection aswell.
	 * @param parent The GanttChart to add the GanttEvent to.
	 * @param gs The GanttSection to add the GanttEvent to.
	 * 			Can be <code>null</code>.
	 * @param name The name that should be set to the GanttEvent.
	 * @param start The start date of the GanttEvent.
	 * @param end The end date of the GanttEvent.
	 * @return The newly created GanttEvent that was added to the 
	 * 			GanttChart.
	 */
	GanttEvent createGanttEvent(
			GanttChart parent, 
			GanttSection gs, 
			String name, 
			Calendar start, 
			Calendar end);
}
