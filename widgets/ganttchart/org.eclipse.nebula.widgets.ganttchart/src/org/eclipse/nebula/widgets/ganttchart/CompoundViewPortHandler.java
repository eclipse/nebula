/*******************************************************************************
 * Copyright (c) 2013 Dirk Fauth and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Dirk Fauth <dirk.fauth@gmail.com> - initial API and implementation
 *    ziogiannigmail.com - Bug 464509 - Minute View Implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.ganttchart;

import java.util.ArrayList;
import java.util.List;


public class CompoundViewPortHandler implements IViewPortHandler2 {

	private List<IViewPortHandler> handler = new ArrayList<IViewPortHandler>();
	
	public void addHandler(IViewPortHandler handler) {
		this.handler.add(handler);
	}
	
	public void removeHandler(IViewPortHandler handler) {
		this.handler.remove(handler);
	}
	
	public void scrollingLeft(int diffCount) {
		for (IViewPortHandler vph : this.handler) {
			vph.scrollingLeft(diffCount);
		}
	}

	public void scrollingRight(int diffCount) {
		for (IViewPortHandler vph : this.handler) {
			vph.scrollingRight(diffCount);
		}
	}

	public void nextMonth() {
		for (IViewPortHandler vph : this.handler) {
			vph.nextMonth();
		}
	}

	public void prevMonth() {
		for (IViewPortHandler vph : this.handler) {
			vph.prevMonth();
		}
	}

	public void nextWeek() {
		for (IViewPortHandler vph : this.handler) {
			vph.nextWeek();
		}
	}

	public void prevWeek() {
		for (IViewPortHandler vph : this.handler) {
			vph.prevWeek();
		}
	}

	public void nextHour() {
		for (IViewPortHandler vph : this.handler) {
			vph.nextHour();
		}
	}

	public void prevHour() {
		for (IViewPortHandler vph : this.handler) {
			vph.prevHour();
		}
	}
	
	public void nextMinute() {
		for (IViewPortHandler vph : this.handler) {
			if(vph instanceof IViewPortHandler2)
				  ((IViewPortHandler2) vph).nextMinute();
		}
	}

	public void prevMinute() {
		for (IViewPortHandler vph : this.handler) {
			if(vph instanceof IViewPortHandler2)
				  ((IViewPortHandler2) vph).prevMinute();
		}
	}

	public void nextDay() {
		for (IViewPortHandler vph : this.handler) {
			vph.nextDay();
		}
	}

	public void prevDay() {
		for (IViewPortHandler vph : this.handler) {
			vph.prevDay();
		}
	}

}
