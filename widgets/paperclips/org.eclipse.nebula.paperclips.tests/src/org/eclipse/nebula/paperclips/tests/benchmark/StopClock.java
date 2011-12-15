/*
 * Copyright (c) 2007 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.tests.benchmark;

public class StopClock implements Clock {
	public StopClock() {
		this(0);
	}

	public StopClock(long initial) {
		this.time = initial;
	}

	public long time;

	public long getTime() {
		return time;
	}
}
