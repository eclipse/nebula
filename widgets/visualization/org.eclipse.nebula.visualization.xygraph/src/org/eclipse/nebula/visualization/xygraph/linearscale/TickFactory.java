/*******************************************************************************
 * Copyright (c) 2012, 2017 Diamond Light Source Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.linearscale;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.nebula.visualization.internal.xygraph.utils.LargeNumberUtils;

/**
 * Tick factory produces the different axis ticks. When specifying a format and
 * given the screen size parameters and range it will return a list of Ticks
 */
public class TickFactory {
	/**
	 * tick formatting modes
	 *
	 */
	public enum TickFormatting {
		/**
		 * Automatically adjust precision
		 */
		autoMode,
		/**
		 * Rounded or chopped to the nearest decimal
		 */
		roundAndChopMode,
		/**
		 * Use Exponent
		 */
		useExponent,
		/**
		 * Use SI units (k,M,G,etc.)
		 */
		useSIunits,
		/**
		 * Use external scale provider
		 */
		useCustom;
	}

	private TickFormatting formatOfTicks;
	private final static BigDecimal EPSILON = new BigDecimal("1.0E-20");
	/**
	 * limit for number of digits to display left of decimal point
	 */
	private static final int DIGITS_UPPER_LIMIT = 6;
	/**
	 * limit for number of zeros to display right of decimal point
	 */
	private static final int DIGITS_LOWER_LIMIT = -6;
	/**
	 * fraction of denominator to round to
	 */
	private static final double ROUND_FRACTION = 2e-6;
	private static final BigDecimal BREL_ERROR = new BigDecimal("1e-15");
	private static final double REL_ERROR = BREL_ERROR.doubleValue();

	private double graphMin;
	private double graphMax;
	private String tickFormat;
	private IScaleProvider scale;
	private int numberOfIntervals;
	private boolean isReversed;

	/**
	 * @param format
	 */
	public TickFactory(IScaleProvider scale) {
		this(TickFormatting.useCustom, scale);
	}

	/**
	 * @param format
	 */
	public TickFactory(TickFormatting format, IScaleProvider scale) {
		formatOfTicks = format;
		this.scale = scale;
	}

	private String getTickString(double value) {

		if (scale != null)
			value = scale.getLabel(value);

		String returnString = "";
		if (Double.isNaN(value))
			return returnString;

		switch (formatOfTicks) {
		case autoMode:
			returnString = String.format(tickFormat, value);
			break;
		case useExponent:
			returnString = String.format(tickFormat, value);
			break;
		case roundAndChopMode:
			returnString = String.format("%d", Math.round(value));
			break;
		case useSIunits:
			double absValue = Math.abs(value);
			if (absValue == 0.0) {
				returnString = String.format("%6.2f", value);
			} else if (absValue <= 1E-15) {
				returnString = String.format("%6.2ff", value * 1E15);
			} else if (absValue <= 1E-12) {
				returnString = String.format("%6.2fp", value * 1E12);
			} else if (absValue <= 1E-9) {
				returnString = String.format("%6.2fn", value * 1E9);
			} else if (absValue <= 1E-6) {
				returnString = String.format("%6.2fµ", value * 1E6);
			} else if (absValue <= 1E-3) {
				returnString = String.format("%6.2fm", value * 1E3);
			} else if (absValue < 1E3) {
				returnString = String.format("%6.2f", value);
			} else if (absValue < 1E6) {
				returnString = String.format("%6.2fk", value * 1E-3);
			} else if (absValue < 1E9) {
				returnString = String.format("%6.2fM", value * 1E-6);
			} else if (absValue < 1E12) {
				returnString = String.format("%6.2fG", value * 1E-9);
			} else if (absValue < 1E15) {
				returnString = String.format("%6.2fT", value * 1E-12);
			} else if (absValue < 1E18)
				returnString = String.format("%6.2fP", value * 1E-15);
			break;
		case useCustom:
			returnString = scale.format(value);
			break;
		}
		return returnString;
	}

	private void createFormatString(final int precision, final boolean useExponent) {
		switch (formatOfTicks) {
		case autoMode:
			tickFormat = useExponent ? String.format("%%.%de", precision) : String.format("%%.%df", precision);
			break;
		case useExponent:
			tickFormat = String.format("%%.%de", precision);
			break;
		default:
			tickFormat = null;
			break;
		}
	}

	/**
	 * Round numerator down to multiples of denominators
	 *
	 * @param numerator
	 * @param denominator
	 * @return rounded down value
	 */
	protected static double roundDown(BigDecimal numerator, BigDecimal denominator) {
		final int ns = numerator.signum();
		if (ns == 0)
			return 0;
		final int ds = denominator.signum();
		if (ds == 0) {
			throw new IllegalArgumentException("Zero denominator is not allowed");
		}

		numerator = numerator.abs();
		denominator = denominator.abs();
		final BigDecimal[] x = numerator.divideAndRemainder(denominator);
		double rx = x[1].doubleValue();
		if (rx > (1 - ROUND_FRACTION) * denominator.doubleValue()) {
			// trim up if close to denominator
			x[1] = BigDecimal.ZERO;
			x[0] = x[0].add(BigDecimal.ONE);
		} else if (rx < ROUND_FRACTION * denominator.doubleValue()) {
			x[1] = BigDecimal.ZERO;
		}
		final int xs = x[1].signum();
		if (xs == 0) {
			return ns != ds ? -x[0].multiply(denominator).doubleValue() : x[0].multiply(denominator).doubleValue();
		} else if (xs < 0) {
			throw new IllegalStateException("Cannot happen!");
		}

		if (ns != ds)
			return x[0].signum() == 0 ? -denominator.doubleValue() : -x[0].add(BigDecimal.ONE).multiply(denominator).doubleValue();

		return x[0].multiply(denominator).doubleValue();
	}

	/**
	 * Round numerator up to multiples of denominators
	 *
	 * @param numerator
	 * @param denominator
	 * @return rounded up value
	 */
	protected static double roundUp(BigDecimal numerator, BigDecimal denominator) {
		final int ns = numerator.signum();
		if (ns == 0)
			return 0;
		final int ds = denominator.signum();
		if (ds == 0) {
			throw new IllegalArgumentException("Zero denominator is not allowed");
		}

		numerator = numerator.abs();
		denominator = denominator.abs();
		final BigDecimal[] x = numerator.divideAndRemainder(denominator);
		double rx = x[1].doubleValue();
		if (rx != 0) {
			if (rx < ROUND_FRACTION * denominator.doubleValue()) {
				// trim down if close to zero
				x[1] = BigDecimal.ZERO;
			} else if (rx > (1 - ROUND_FRACTION) * denominator.doubleValue()) {
				x[1] = BigDecimal.ZERO;
				x[0] = x[0].add(BigDecimal.ONE);
			}
		}
		final int xs = x[1].signum();
		if (xs == 0) {
			return ns != ds ? -x[0].multiply(denominator).doubleValue() : x[0].multiply(denominator).doubleValue();
		} else if (xs < 0) {
			throw new IllegalStateException("Cannot happen!");
		}

		if (ns != ds)
			return x[0].signum() == 0 ? 0 : -x[0].multiply(denominator).doubleValue();

		return x[0].add(BigDecimal.ONE).multiply(denominator).doubleValue();
	}

	/**
	 * @param x
	 * @return floor of log 10
	 */
	private static int log10(BigDecimal x) {
		int c = x.compareTo(BigDecimal.ONE);
		int e = 0;
		while (c < 0) {
			e--;
			x = x.scaleByPowerOfTen(1);
			c = x.compareTo(BigDecimal.ONE);
		}

		c = x.compareTo(BigDecimal.TEN);
		while (c >= 0) {
			e++;
			x = x.scaleByPowerOfTen(-1);
			c = x.compareTo(BigDecimal.TEN);
		}

		return e;
	}

	private static BigDecimal divide(BigDecimal dividend, int divisor) {
		return dividend.divide(BigDecimal.valueOf(divisor), dividend.scale() + 2, RoundingMode.DOWN);
	}

	/**
	 * @param x
	 * @param round
	 *            if true, then round else take ceiling
	 * @return a nice number
	 */
	protected static BigDecimal nicenum(BigDecimal x, boolean round) {
		int expv; /* exponent of x */
		double f; /* fractional part of x */
		double nf; /* nice, rounded number */
		BigDecimal bf;

		boolean negative = x.signum() == -1;
		x = x.abs();
		expv = log10(x);
		bf = x.scaleByPowerOfTen(-expv);
		f = bf.doubleValue(); /* between 1 and 10 */
		if (round) {
			if (f < 1.5)
				nf = 1;
			else if (f < 2.25)
				nf = 2;
			else if (f < 3.25)
				nf = 2.5;
			else if (f < 7.5)
				nf = 5;
			else
				nf = 10;
		} else if (f <= 1.)
			nf = 1;
		else if (f <= 2.)
			nf = 2;
		else if (f <= 5.)
			nf = 5;
		else
			nf = 10;

		if (negative) {
			nf = -nf;
		}
		return BigDecimal.valueOf(nf).scaleByPowerOfTen(expv).stripTrailingZeros();
	}

	private double determineNumTicks(double min, double max, int maxTicks, boolean allowMinMaxOver) {
		BigDecimal bMin = BigDecimal.valueOf(min);
		BigDecimal bMax = BigDecimal.valueOf(max);
		BigDecimal bRange = bMax.subtract(bMin);
		if (bRange.signum() < 0) {
			BigDecimal bt = bMin;
			bMin = bMax;
			bMax = bt;
			bRange = bRange.negate();
			isReversed = true;
		} else {
			isReversed = false;
		}

		BigDecimal magnitude = BigDecimal.valueOf(Math.max(Math.abs(min), Math.abs(max)));
		// tick points too dense to do anything
		if (bRange.compareTo(EPSILON.multiply(magnitude)) < 0) {
			return 0;
		}

		// Important fix: This avoids tick labeller entering an infinite loop
		// for some plotting cases.
		try {
			if (magnitude.doubleValue() <= Double.MIN_VALUE) {
				return 0;
			}
		} catch (Throwable ne) {
			// Might be a big number that doubleValue() does not work on - carry
			// on!
		}

		bRange = nicenum(bRange, false);
		BigDecimal bUnit;
		int nTicks = maxTicks - 1;
		if (Math.signum(min) * Math.signum(max) < 0) {
			// straddle case
			nTicks++;
		}
		do {
			long n;
			do { // ensure number of ticks is less or equal to number requested
				bUnit = nicenum(divide(bRange, nTicks), true);
				n = bRange.divideToIntegralValue(bUnit).longValue();
			} while (n > maxTicks && --nTicks > 0);

			if (allowMinMaxOver) {
				graphMin = roundDown(bMin, bUnit);
				if (graphMin == 0) { // ensure positive zero
					graphMin = 0;
				} else if (Double.isInfinite(graphMin) && graphMin < 0) {
					// ensure graph minimum can be used as double
					graphMin = roundUp(bMin, bUnit);
					// it signals generateTicks to loosen its bounds
					// and use its input minimum (rather the first tick)
				}
				graphMax = roundUp(bMax, bUnit);
				if (graphMax == 0) {
					graphMax = 0;
				} else if (Double.isInfinite(graphMax) && graphMax > 0) {
					// ensure graph maximum can be used as double
					graphMax = roundDown(bMax, bUnit);
					// it signals generateTicks to loosen its bounds
					// and use its input maximum (rather the last tick)
				}
			} else {
				if (isReversed) {
					graphMin = max;
					graphMax = min;
				} else {
					graphMin = min;
					graphMax = max;
				}
			}
			if (bUnit.compareTo(BREL_ERROR.multiply(magnitude)) <= 0) {
				numberOfIntervals = -1; // signal that we hit the limit of precision
			} else {
				double factor = LargeNumberUtils.maxMagnitude(graphMin, graphMax);
				double tmp = graphMax/factor - graphMin/factor;
				numberOfIntervals = (int) Math.round(tmp / (bUnit.doubleValue() / factor));
			}
		} while (numberOfIntervals > maxTicks && --nTicks > 0);
		if (isReversed) {
			double t = graphMin;
			graphMin = graphMax;
			graphMax = t;
		}
		double tickUnit = isReversed ? -bUnit.doubleValue() : bUnit.doubleValue();

		/**
		 * We get the labelled max and min for determining the precision which
		 * the ticks should be shown at.
		 */
		int d = bUnit.scale() < 0 ? bUnit.precision() + bUnit.scale() - 1 : bUnit.scale();
		int p = (int) Math.max(Math.floor(Math.log10(Math.abs(graphMin))), Math.floor(Math.log10(Math.abs(graphMax))));
		if (p <= DIGITS_LOWER_LIMIT || p >= DIGITS_UPPER_LIMIT) {
			createFormatString(Math.max(d + p, 0), true);
		} else {
			createFormatString(Math.max(d, 0), false);
		}
		return tickUnit;
	}

	private boolean inRange(double x, double min, double max) {
		if (isReversed) {
			return x >= max && x <= min;
		}
		return x >= min && x <= max;
	}

	/**
	 * Generate a list of ticks that span range given by min and max. The
	 * maximum number of ticks is exceed by one in the case where the range
	 * straddles zero.
	 *
	 * @param min
	 * @param max
	 * @param maxTicks
	 * @param allowMinMaxOver
	 *            allow min/maximum overwrite
	 * @param tight
	 *            if true then remove ticks outside range
	 * @return a list of the ticks for the axis
	 */
	public List<Tick> generateTicks(double min, double max, int maxTicks, boolean allowMinMaxOver,
			final boolean tight) {
		List<Tick> ticks = new ArrayList<Tick>();
		double tickUnit = determineNumTicks(min, max, maxTicks, allowMinMaxOver);
		if (tickUnit == 0) {
			return ticks;
		}

		double tmin = graphMin / tickUnit;
		for (int i = 0; i <= numberOfIntervals; i++) {
			double p = (tmin + i) * tickUnit;
			if (Math.abs(p / tickUnit) < REL_ERROR) {
				p = 0; // ensure positive zero
			} else if (Double.isInfinite(p)) {
				continue;
			}

			if (!tight || inRange(p, min, max)) {
				Tick newTick = new Tick();
				newTick.setValue(p);
				newTick.setText(getTickString(p));
				ticks.add(newTick);
			}
		}

		int imax = ticks.size();
		if (imax > 1) {
			if (!tight && allowMinMaxOver) {
				Tick t = ticks.get(imax - 1);
				if (!isReversed && t.getValue() < max) { // last is >= max
					t.setValue(graphMax);
					t.setText(getTickString(graphMax));
				}
			}
		} else if (maxTicks > 1) {
			if (imax == 0) {
				imax++;
				Tick newTick = new Tick();
				newTick.setValue(graphMin);
				newTick.setText(getTickString(graphMin));
				ticks.add(newTick);
			}
			if (imax == 1) {
				Tick t = ticks.get(0);
				Tick newTick = new Tick();
				if (t.getText().equals(getTickString(graphMax))) {
					newTick.setValue(graphMin);
					newTick.setText(getTickString(graphMin));
					ticks.add(0, newTick);
				} else {
					newTick.setValue(graphMax);
					newTick.setText(getTickString(graphMax));
					ticks.add(newTick);
				}
				imax++;
			}
		}

		// override tight flag in cases where graph ends have been
		// modified (in determineNumTicks) to be representable as doubles
		double lo = tight || compare(isReversed, min, graphMin) ? min : ticks.get(0).getValue();
		double hi = tight || compare(!isReversed, max, graphMax) ? max : (imax > 1 ? ticks.get(imax - 1).getValue() : lo);
		double factor = LargeNumberUtils.maxMagnitude(lo, hi);
		lo /= factor;
		hi /= factor;
		double range = imax > 1 ? hi - lo : 1;

		for (Tick t : ticks) {
			t.setPosition((t.getValue()/factor - lo) / range);
		}

		return ticks;
	}

	private static boolean compare(boolean isGreaterThan, double a, double b) {
		if (isGreaterThan) {
			return a > b;
		}
		return a < b;
	}

	private static final DecimalFormat INDEX_FORMAT = new DecimalFormat("0");

	/**
	 * Generate a list of ticks that span range given by min and max.
	 *
	 * @param min
	 * @param max
	 * @param maxTicks
	 * @param tight
	 *            if true then remove ticks outside range (ignored)
	 * @return a list of the ticks for the axis
	 */
	public List<Tick> generateIndexBasedTicks(double min, double max, int maxTicks, boolean tight) {
		isReversed = min > max;
		if (isReversed) {
			double t = max;
			max = min;
			min = t;
		}

		List<Tick> ticks = new ArrayList<Tick>();
		double gRange = nicenum(BigDecimal.valueOf(max - min), false).doubleValue();
		double tickUnit = 1;
		numberOfIntervals = 0;
		int it = maxTicks - 1;
		while (numberOfIntervals < 1) {
			tickUnit = Math.max(1, nicenum(BigDecimal.valueOf(gRange / it++), true).doubleValue());
			tickUnit = Math.floor(tickUnit); // make integer
			graphMin = Math.ceil(Math.ceil(min / tickUnit) * tickUnit);
			graphMax = Math.floor(Math.floor(max / tickUnit) * tickUnit);
			numberOfIntervals = (int) Math.floor((graphMax - graphMin) / tickUnit);
			if (tickUnit == 1) {
				break;
			}
		}

		switch (formatOfTicks) {
		case autoMode:
			tickFormat = "%g";
			break;
		case useExponent:
			tickFormat = "%e";
			break;
		default:
			tickFormat = null;
			break;
		}

		for (int i = 0; i <= numberOfIntervals; i++) {
			double p = graphMin + i * tickUnit;
			Tick newTick = new Tick();
			newTick.setValue(p);
			newTick.setText(getTickString(p));
			ticks.add(newTick);
		}

		int imax = ticks.size();
		double range = imax > 1 ? max - min : 1;
		for (Tick t : ticks) {
			t.setPosition((t.getValue() - min) / range);
		}
		if (isReversed) {
			Collections.reverse(ticks);
		}

		if (formatOfTicks == TickFormatting.autoMode) { // override labels
			if (scale != null && scale.isLabelCustomised()) {
				double vmin = Double.POSITIVE_INFINITY;
				double vmax = Double.NEGATIVE_INFINITY;
				boolean allInts = true;
				for (Tick t : ticks) {
					double v = Math.abs(scale.getLabel(t.getValue()));
					if (Double.isNaN(v))
						continue;
					if (allInts) {
						allInts = Math.abs(v - Math.floor(v)) == 0;
					}
					v = Math.abs(v);
					if (v < vmin && v > 0)
						vmin = v;
					if (v > vmax)
						vmax = v;
				}
				if (allInts) {
					for (Tick t : ticks) {
						double v = scale.getLabel(t.getValue());
						if (!Double.isNaN(v))
							t.setText(INDEX_FORMAT.format(v));
					}
				} else if (Math.log10(vmin) >= DIGITS_LOWER_LIMIT || Math.log10(vmax) <= DIGITS_UPPER_LIMIT) {
					for (Tick t : ticks) {
						double v = scale.getLabel(t.getValue());
						if (!Double.isNaN(v))
							t.setText(scale.format(v));
					}
				}
			} else {
				for (Tick t : ticks) {
					t.setText(INDEX_FORMAT.format(t.getValue()));
				}
			}
		}
		return ticks;
	}

	private final static int LOWEST_LOG_10 = -323; // sub-normal value 4.9e-324
	private final static int LOWER_LOG_10 = -311; // where Math.log10 becomes inaccurate
	private final static int HIGHEST_LOG_10 = 308; // 1.80e308

	private int determineNumLogTicks(double min, double max, int maxTicks, boolean allowMinMaxOver) {
		isReversed = min > max;
		if (isReversed) {
			double t = min;
			min = max;
			max = t;
		}

		graphMin = Math.log10(min);
		graphMax = Math.log10(max);
		int loDecade = (int) Math.floor(graphMin); // lowest decade (or power of ten)
		if (loDecade < LOWEST_LOG_10) {
			loDecade = LOWEST_LOG_10;
		} else if (loDecade < LOWER_LOG_10) {
			BigDecimal bd = BigDecimal.valueOf(min).scaleByPowerOfTen(-loDecade);
			if (bd.doubleValue() >= 10) { // should be less than 10
				loDecade++;
			}
		}
		int hiDecade = (int) Math.ceil(graphMax);
		if (hiDecade > HIGHEST_LOG_10) {
			hiDecade = HIGHEST_LOG_10;
		}

		int decades = hiDecade - loDecade;

		int unit = (decades + maxTicks - 1) / maxTicks;

		if (allowMinMaxOver) {
			graphMin = loDecade;
			numberOfIntervals = (decades + unit - 1) / unit; // ceiling of units in decades
			if (hiDecade < HIGHEST_LOG_10) {
				graphMax = numberOfIntervals * unit + loDecade;
			} else if (loDecade > LOWEST_LOG_10) {
				graphMax = hiDecade;
			} else { // bound on both ends
				graphMax = hiDecade;
				unit = decades / numberOfIntervals; // trim units to ensure graphMin >= loDecades
				graphMin = hiDecade - numberOfIntervals * unit;
			}
		} else {
			numberOfIntervals = (int) Math.floor(graphMax - graphMin) / unit;
		}

		if (isReversed) {
			double t = graphMin;
			graphMin = graphMax;
			graphMax = t;
		}

		if (loDecade < -3 || hiDecade > 3 || decades > 6) {
			createFormatString(0, true);
		} else {
			createFormatString(Math.max(-loDecade, 0), false);
		}
		return isReversed ? -unit : unit;
	}

	private boolean inRangeLog(double x, double min, double max) {
		if (isReversed) {
			max -= BREL_ERROR.doubleValue();
			return x >= max && x <= min;
		}
		min -= BREL_ERROR.doubleValue();
		return x >= min && x <= max;
	}

	/**
	 * @param min
	 *            (must be >0)
	 * @param max
	 *            (must be >0)
	 * @param maxTicks
	 * @param allowMinMaxOver
	 *            allow min/maximum overwrite
	 * @param tight
	 *            if true then remove ticks outside range
	 * @return a list of the ticks for the axis
	 */
	public List<Tick> generateLogTicks(double min, double max, int maxTicks, boolean allowMinMaxOver,
			final boolean tight) {
		if (min <= 0 || max <= 0) {
			throw new IllegalArgumentException("Non-positive minimum and maximum values are not allowed");
		}

		List<Tick> ticks = new ArrayList<Tick>();
		int tickUnit = determineNumLogTicks(min, max, maxTicks, allowMinMaxOver);

		double p = graphMin;
		for (int i = 0; i <= numberOfIntervals; i++) {
			double x = Math.pow(10, p);
			boolean r = inRangeLog(x, min, max);
			if (!tight || r) {
				Tick newTick = new Tick();
				newTick.setValue(x);
				newTick.setText(getTickString(x));
				ticks.add(newTick);
			}
			p += tickUnit;
		}

		int imax = ticks.size();
		if (imax < numberOfIntervals) {
			double x = Math.pow(10, p);
			boolean r = inRangeLog(x, min, max);
			if (!tight || r) {
				Tick newTick = new Tick();
				newTick.setValue(x);
				newTick.setText(getTickString(x));
				ticks.add(newTick);
				imax++;
			}
		}

		if (imax > 1) {
			if (!tight && allowMinMaxOver) {
				Tick t = ticks.get(imax - 1);
				if (!isReversed && t.getValue() < max) { // last is >= max
					double x = Math.pow(10, graphMax);
					t.setValue(x);
					t.setText(getTickString(x));
				}
			}
		} else if (maxTicks > 1) {
			if (imax == 0) {
				imax++;
				Tick newTick = new Tick();
				double x = Math.pow(10, isReversed ? graphMax : graphMin);
				newTick.setValue(x);
				newTick.setText(getTickString(x));
				ticks.add(newTick);
			}
			if (imax == 1) {
				if (!tight && allowMinMaxOver) {
					Tick t = ticks.get(0);
					Tick newTick = new Tick();
					double x = Math.pow(10, graphMax);
					if (t.getText().equals(getTickString(x))) {
						x = Math.pow(10, graphMin);
						newTick.setValue(x);
						newTick.setText(getTickString(x));
						ticks.add(0, newTick);
					} else {
						newTick.setValue(x);
						newTick.setText(getTickString(x));
						ticks.add(newTick);
					}
					imax++;
				}
			}
		}

		if (tickUnit > 0) {
			double lo = Math.log(tight ? min : ticks.get(0).getValue());
			double hi = Math.log(tight ? max : ticks.get(imax - 1).getValue());
			double range = hi - lo;
			for (Tick t : ticks) {
				t.setPosition((Math.log(t.getValue()) - lo) / range);
			}
		} else {
			double lo = Math.log(tight ? max : ticks.get(0).getValue());
			double hi = Math.log(tight ? min : ticks.get(imax - 1).getValue());
			double range = hi - lo;
			for (Tick t : ticks) {
				t.setPosition(1 - (Math.log(t.getValue()) - lo) / range);
			}
		}
		return ticks;
	}
}
