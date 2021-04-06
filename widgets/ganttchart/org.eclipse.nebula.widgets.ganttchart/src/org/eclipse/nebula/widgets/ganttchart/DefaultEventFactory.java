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

import java.util.Calendar;

public class DefaultEventFactory implements IEventFactory {

	public GanttEvent createGanttEvent(
			GanttChart parent, 
			GanttSection gs, 
			String name, 
			Calendar start, 
			Calendar end) {
		
    	GanttEvent newEvent = new GanttEvent(parent, name, start, end, 0);
    	newEvent.setStatusColor(ColorCache.getColor(24, 43, 69));
    	
    	//add event to section
    	if (gs != null) {
    		gs.addGanttEvent(newEvent);
    	}
    	
    	return newEvent;
	}

}
