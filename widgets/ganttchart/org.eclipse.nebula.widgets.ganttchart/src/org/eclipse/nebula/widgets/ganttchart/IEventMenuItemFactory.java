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
