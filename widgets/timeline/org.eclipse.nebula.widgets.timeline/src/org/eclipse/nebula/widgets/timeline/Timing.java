/*******************************************************************************
 * Copyright (c) 2020 Christian Pontesegger and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Christian Pontesegger - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.timeline;

public class Timing {

	private double fTimestamp;
	private double fDuration;

	public Timing(double timestamp, double duration) {
		fTimestamp = timestamp;
		fDuration = duration;
	}

	public Timing(double timestamp) {
		this(timestamp, 0);
	}

	public double getTimestamp() {
		return fTimestamp;
	}

	public double getDuration() {
		return fDuration;
	}

	public double left() {
		return getTimestamp();
	}

	public double right() {
		return getTimestamp() + getDuration();
	}

	public Timing copy() {
		return new Timing(getTimestamp(), getDuration());
	}

	public void union(Timing timing) {
		final double left = Math.min(left(), timing.left());
		final double right = Math.max(right(), timing.right());

		fTimestamp = left;
		fDuration = right - left;
	}

	public void scale(double scaleFactor) {
		fTimestamp *= scaleFactor;
		fDuration *= scaleFactor;
	}

	public void translate(double offset) {
		fTimestamp += offset;
	}

	public boolean isEmpty() {
		return fDuration == 0;
	}

	@Override
	public String toString() {
		return fTimestamp + " (" + fDuration + ")";
	}
}
