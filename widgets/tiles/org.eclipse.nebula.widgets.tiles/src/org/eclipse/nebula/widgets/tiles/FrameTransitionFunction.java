/*****************************************************************************
 * Copyright (c) 2014, 2021 Fabian Prasser, Laurent Caron
 *
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Arian Stolwijk - Initial API and implementation
 * Fabian Prasser - Port to Java
 * Laurent Caron <laurent dot caron at gmail dot com> - Integration into the Nebula Project
 *****************************************************************************/
package org.eclipse.nebula.widgets.tiles;


/**
 * A class that models a Cubic-Bezier curve.
 *
 * CSS: Ease-in (0.42,0,1,1) (slow start)
 * CSS: Ease-out (0,0,0.58,1) (slow end)
 * CSS: Ease-in-out (0.42,0,0.58,1) (slow start & end)
 *
 * Ported from: https://github.com/arian/cubic-bezier/blob/master/index.js
 */
class FrameTransitionFunction {

	/** Parameter of the bezier curve*/
	private final double x1;
	/** Parameter of the bezier curve*/
	private final double y1;
	/** Parameter of the bezier curve*/
	private final double x2;
	/** Parameter of the bezier curve*/
	private final double y2;

	/**
	 * Creates a new instance
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	FrameTransitionFunction(final double x1, final double y1, final double x2, final double y2) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}

	/**
	 * Computes the position on the bezier curve
	 * @param t
	 * @param duration
	 * @return
	 */
	public double bezier(final double t, final double duration) {

		final double x = t;
		double t0 = 0, t1 = 0, t2 = 0, x2 = 0, d2 = 0, i = 0;

		// Compute epsilon as proposed at https://github.com/arian/cubic-bezier
		final double epsilon = 1000d / 60d / duration / 4d;

		// First try a few iterations of Newton's method -- normally very fast.
		for (t2 = x, i = 0; i < 8; i++) {
			x2 = curveX(t2) - x;
			if (Math.abs(x2) < epsilon) {
				return curveY(t2);
			}
			d2 = derivativeCurveX(t2);
			if (Math.abs(d2) < 1e-6) {
				break;
			}
			t2 = t2 - x2 / d2;
		}

		t0 = 0;
		t1 = 1;
		t2 = x;

		if (t2 < t0) {
			return curveY(t0);
		}
		if (t2 > t1) {
			return curveY(t1);
		}

		// Fallback to the bisection method for reliability.
		while (t0 < t1) {
			x2 = curveX(t2);
			if (Math.abs(x2 - x) < epsilon) {
				return curveY(t2);
			}
			if (x > x2) {
				t0 = t2;
			} else {
				t1 = t2;
			}
			t2 = (t1 - t0) * .5 + t0;
		}

		// Failure
		return curveY(t2);
	}

	/**
	 * Helper
	 * @param t
	 * @return
	 */
	private double curveX(final double t) {
		final double v = 1 - t;
		return 3 * v * v * t * x1 + 3 * v * t * t * x2 + t * t * t;
	}

	/**
	 * Helper
	 * @param t
	 * @return
	 */
	private double curveY(final double t) {
		final double v = 1 - t;
		return 3 * v * v * t * y1 + 3 * v * t * t * y2 + t * t * t;
	}

	/**
	 * Helper
	 * @param t
	 * @return
	 */
	private double derivativeCurveX(final double t) {
		final double v = 1 - t;
		return 3 * (2 * (t - 1) * t + v * v) * x1 + 3 *
				(-t * t * t + 2 * v * t) * x2;
	};
}
