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

class GanttMap {	
	private ArrayList mUsedUp;
	private HashMap mMap;

    public GanttMap() {
        mMap = new HashMap();
        mUsedUp = new ArrayList();
    }

    public void put(GanttEvent value, GanttEvent key) {
        if (mUsedUp.contains(value)) {
            ArrayList v = (ArrayList) mMap.get(value);
            if (!v.contains(key)) {
                v.add(key);
            }
            
            mMap.put(value, v);
        } 
        else {
            ArrayList v = new ArrayList();
            v.add(key);
            
            mMap.put(value, v);
            mUsedUp.add(value);
        }
    }
    
    public ArrayList get(GanttEvent obj) {
    	return (ArrayList)mMap.get(obj);
    }

    public void clear() {
        mUsedUp.clear();
        mMap.clear();
    }

}
