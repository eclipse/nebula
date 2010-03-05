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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class GanttMap {
	private HashMap	_map;

	public GanttMap() {
		_map = new HashMap();
	}
	
	public void put(GanttEvent key, List connections) {
		_map.put(key, connections);
	}

	public void put(GanttEvent value, GanttEvent key) {
		if (_map.containsKey(value)) {
			List v = (List) _map.get(value);
			if (!v.contains(key)) {
				v.add(key);
			}

			_map.put(value, v);
		} else {
			List v = new ArrayList();
			v.add(key);

			_map.put(value, v);
		}
	}

	public void remove(GanttEvent key) {
		_map.remove(key);
	}
	
	public List get(GanttEvent obj) {
		return (List) _map.get(obj);
	}

	public void clear() {
		_map.clear();
	}

}
