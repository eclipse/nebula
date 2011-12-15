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

package org.eclipse.nebula.widgets.collapsiblebuttons;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;

public class CursorCache {

    private static HashMap map = new HashMap();

    /**
     * Returns a cursor that is also cached as to not create more handles for each time the cursor type is fetched.
     * 
     * @param type Cursor Type to fetch
     * @return Cursor
     */
    public static Cursor getCursor(int type) {
        if (map.get(new Integer(type)) != null) {
            return (Cursor) map.get(new Integer(type));
        } else {
            Cursor c = new Cursor(Display.getDefault(), type);
            map.put(new Integer(type), c);
            return c;
        }
    }

    /**
     * Disposes all cursors held in the cache.
     * <p>
     * <b>IMPORTANT: ONLY CALL WHEN YOU WANT TO DISPOSE ALL CACHED CURSORS!</b>
     *
     */
    public static void dispose() {
        if (map != null && map.keySet() != null) {
            Iterator keys = map.keySet().iterator();
            while (keys.hasNext()) {
                Object key = keys.next();
                ((Cursor) map.get(key)).dispose();
            }
        }
        map = null;
    }
}
