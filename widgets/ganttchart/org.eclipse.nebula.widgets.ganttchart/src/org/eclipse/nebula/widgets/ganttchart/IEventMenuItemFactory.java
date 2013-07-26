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

import org.eclipse.swt.widgets.Menu;

/**
 * Interface for a factory that creates menu items in the context menu of a 
 * GanttEvent in the GanttChart.
 */
public interface IEventMenuItemFactory {

	/**
	 * Adds new custom menu items to the context menu of a GanttEvent
	 * in the GanttChart.
	 * @param menu The menu to add the custom actions to.
	 * @param ganttEvent The GanttEvent for which the menu is opened
	 */
	void addCustomMenuItems(final Menu menu, final GanttEvent ganttEvent);
}
