/*******************************************************************************
 * Copyright (c) 2015 Giovanni Cimmino and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
