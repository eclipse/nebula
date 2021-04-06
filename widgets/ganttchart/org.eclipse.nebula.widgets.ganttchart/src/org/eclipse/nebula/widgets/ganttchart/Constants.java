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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;

public final class Constants {

    private Constants() {
    }

    public static final Cursor CURSOR_NONE       = CursorCache.getCursor(SWT.NONE);
    public static final Cursor CURSOR_SIZEE      = CursorCache.getCursor(SWT.CURSOR_SIZEE);
    public static final Cursor CURSOR_SIZEW      = CursorCache.getCursor(SWT.CURSOR_SIZEW);
    public static final Cursor CURSOR_SIZEALL    = CursorCache.getCursor(SWT.CURSOR_SIZEALL);
    public static final Cursor CURSOR_HAND       = CursorCache.getCursor(SWT.CURSOR_HAND);

    // connecting line drawing, internal
    public static final int    BEND_RIGHT_UP     = 1;
    public static final int    BEND_RIGHT_DOWN   = 2;
    public static final int    BEND_LEFT_UP      = 3;
    public static final int    BEND_LEFT_DOWN    = 4;

    // scrolling directions, internal
    public static final int    DIRECTION_LEFT    = 1;
    public static final int    DIRECTION_RIGHT   = 2;
    public static final int    DIRECTION_UP      = 3;
    public static final int    DIRECTION_DOWN    = 4;

    // out of bounds sides, internal
    public static final int    EVENT_VISIBLE     = 1;
    public static final int    EVENT_OOB_LEFT    = 2;
    public static final int    EVENT_OOB_RIGHT   = 3;
    public static final int    EVENT_NOT_VISIBLE = 4;
    public static final int    EVENT_OOB_TOP     = 5;
    public static final int    EVENT_OOB_BOTTOM  = 6;

    // resize info, internal
    public static final int    TYPE_RESIZE_LEFT  = 1;
    public static final int    TYPE_RESIZE_RIGHT = 2;
    public static final int    TYPE_MOVE         = 3;

    public static final int    TIMER_INTERVAL    = 25;

    public static final String STR_NAME          = "#name#";
    public static final String STR_PC            = "#pc#";
    public static final String STR_ED            = "#ed#";
    public static final String STR_SD            = "#sd#";
    public static final String STR_RS            = "#rs#";
    public static final String STR_RE            = "#re#";
    public static final String STR_DAYS          = "#days#";
    public static final String STR_REV_DAYS      = "#reviseddays#";
    
    public static final String STR_DASH = " - ";

    // what operating system we're on
    public static final int    OS_OTHER          = 0;
    public static final int    OS_WINDOWS        = 1;
    public static final int    OS_MAC            = 2;
    public static final int    OS_LINUX          = 3;

    public static final int    HELP_HEIGHT       = 19;
}
