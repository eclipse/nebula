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

public class VerticalDragModes {

    /**
     * Vertical dragging is disabled
     */
    public static final int NO_VERTICAL_DRAG      = 0;
    
    /**
     * Vertical dragging is enabled for any type of chart
     */
    public static final int ANY_VERTICAL_DRAG     = 1;
    
    /**
     * Vertical dragging is only enabled between two different {@link GanttSection}s
     */
    public static final int CROSS_SECTION_VERTICAL_DRAG = 2;

}
