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

/**
 * This interface is used to implement a listener that gets informed when the plus
 * icon in a section detail area is clicked to show more detail information.
 * Usually it is intended to open a subdialog to show more detail information.
 */
public interface ISectionDetailMoreClickListener {

	/**
	 * Make the advanced detail information of a GanttSection visible.
	 * Usually this should be done by opening a dialog that contains 
	 * that information.
	 * @param section The section whos detail informations should be showed.
	 */
	void openAdvancedDetails(GanttSection section);
}
