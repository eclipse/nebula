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
	private List<GanttEvent> mUsedUp;
	private HashMap<GanttEvent, ArrayList<GanttEvent>> mMap;

    public GanttMap() {
        mMap = new HashMap<GanttEvent, ArrayList<GanttEvent>>();
        mUsedUp = new ArrayList<GanttEvent>();
    }

    public void put(GanttEvent value, GanttEvent key) {
        if (mUsedUp.contains(value)) {
            ArrayList<GanttEvent> v = (ArrayList<GanttEvent>) mMap.get(value);
            if (!v.contains(key)) {
                v.add(key);
            }
            
            mMap.put(value, v);
        } 
        else {
            ArrayList<GanttEvent> v = new ArrayList<GanttEvent>();
            v.add(key);
            
            mMap.put(value, v);
            mUsedUp.add(value);
        }
    }
    
    public ArrayList<GanttEvent> get(GanttEvent obj) {
    	return mMap.get(obj);
    }

    public void clear() {
        mUsedUp.clear();
        mMap.clear();
    }

}
