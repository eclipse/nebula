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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class GanttMap {
    private final Map _map;

    public GanttMap() {
        _map = new HashMap();
    }

    public void put(final GanttEvent key, final List connections) {
        _map.put(key, connections);
    }

    public void put(final GanttEvent value, final GanttEvent key) {
        if (_map.containsKey(value)) {
            final List vList = (List) _map.get(value);
            if (!vList.contains(key)) {
                vList.add(key);
            }

            _map.put(value, vList);
        } else {
            final List vList = new ArrayList();
            vList.add(key);

            _map.put(value, vList);
        }
    }

    public void remove(final GanttEvent key) {
        _map.remove(key);
    }

    public List get(final GanttEvent obj) {
        return (List) _map.get(obj);
    }

    public void clear() {
        _map.clear();
    }

}
