/*******************************************************************************
 * Copyright (c) 2015 Giovanni Cimmino and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    ziogiannigmail.com - Bug 464509 - Minute View Implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.ganttchart;

public interface IViewPortHandler2 extends IViewPortHandler {	
	
	/**
	 * Jumps to the next minute.
	 */
	public void nextMinute();

	/**
	 * Jumps to the previous hour.
	 */
	public void prevMinute();

}
