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
 * Valid styles outside of this class are as follows:
 * <p>
 * <code>SWT.SINGLE</code> - Single event selection in the chart<br>
 * <code>SWT.MULTI</code> - Multi event selection in the chart<br>
 * <p>
 * Any other styles not listed here will be ignored.
 */
public class GanttFlags {

	// SWT.MULTI is taken (1 << 1)
	// SWT.SINGLE is taken (1 << 2)
	// SWT.BORDER is taken (1 << 11)
	
	/**
	 * Creates the chart with an infinite scrollbar. This is the default unless one is set explicitly.
	 */
	public static final int	H_SCROLL_INFINITE		= 1 << 3;

	/**
	 * Creates the chart with a scrollbar fixed to the range of the dates on the chart.
	 */
	public static final int	H_SCROLL_FIXED_RANGE	= 1 << 4;

	/**
	 * Creates the chart with no horizontal scrollbar. It's up to you to handle any horizontal scrolling manually.
	 */
	public static final int	H_SCROLL_NONE			= 1 << 5;

}
