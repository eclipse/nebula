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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;

public class CursorCache {

    private static Map _cache = new HashMap();

    /**
     * Returns a cursor that is also cached as to not create more handles for each time the cursor type is fetched.
     * 
     * @param type Cursor Type to fetch
     * @return Cursor
     */
    public static Cursor getCursor(final int type) {
        if (_cache.get(new Integer(type)) == null) {
            final Cursor cursor = new Cursor(Display.getDefault(), type);
            _cache.put(new Integer(type), cursor); // NOPMD
            return cursor;
        } else {
            return (Cursor) _cache.get(new Integer(type)); // NOPMD
        }
    }

    /**
     * Disposes all cursors held in the cache.
     * <p>
     * <b>IMPORTANT: ONLY CALL WHEN YOU WANT TO DISPOSE ALL CACHED CURSORS!</b>
     *
     */
    public static void dispose() {
        if (_cache != null && _cache.keySet() != null) {
            final Iterator keys = _cache.keySet().iterator();
            while (keys.hasNext()) {
                final Object key = keys.next();
                ((Cursor) _cache.get(key)).dispose();
            }
        }
        _cache.clear();
    }
}
