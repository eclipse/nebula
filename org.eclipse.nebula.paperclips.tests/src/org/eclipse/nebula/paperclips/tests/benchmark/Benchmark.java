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

import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.Locale;

public class Benchmark {
	private int runCount = 1;
	private Clock clock = getDefaultClock();
	private String name = "Unnamed benchmark";
	private PrintStream printStream = System.out;

	public Benchmark() {
	}

	public Benchmark setName(String name) {
		this.name = name;
		return this;
	}

	public Benchmark setClock(Clock clock) {
		this.clock = clock;
		return this;
	}

	public Benchmark setRunCount(int runCount) {
		this.runCount = runCount;
		return this;
	}

	public Benchmark setPrintStream(PrintStream printStream) {
		this.printStream = printStream;
		return this;
	}

	public long execute(Runnable runnable) {
		long total = 0;
		printStream.println("Benchmarking '" + name + "':");
		for (int i = 0; i < runCount; i++) {
			long time = time(clock, runnable);
			printStream.println("\tRun " + (i + 1) + "/" + runCount + ":\t"
					+ time + "ms");
			total += time;
		}
		printStream.println("Total:  \t" + total + "ms");
		printStream.println("Average:\t"
				+ getNumberFormat().format((float) total / (float) runCount)
				+ "ms");
		return total;
	}

	private NumberFormat getNumberFormat() {
		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
		numberFormat.setMinimumFractionDigits(1);
		numberFormat.setMaximumFractionDigits(1);
		return numberFormat;
	}

	public static long time(Runnable runnable) {
		return time(getDefaultClock(), runnable);
	}

	private static SystemClock getDefaultClock() {
		return new SystemClock();
	}

	public static long time(Clock clock, Runnable runnable) {
		long before = clock.getTime();
		runnable.run();
		long after = clock.getTime();
		return after - before;
	}
}
