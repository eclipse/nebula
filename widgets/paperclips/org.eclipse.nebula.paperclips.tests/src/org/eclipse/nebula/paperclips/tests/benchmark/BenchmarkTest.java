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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;

public class BenchmarkTest extends TestCase {
	StopClock clock;
	ByteArrayOutputStream output;
	Benchmark benchmark;
	RunnableStub runnable;
	int executionTime;

	protected void setUp() throws Exception {
		super.setUp();
		clock = new StopClock(0);
		output = new ByteArrayOutputStream();
		final PrintStream printStream = new PrintStream(output);
		benchmark = new Benchmark().setName(getName()).setClock(clock)
				.setPrintStream(printStream);
		runnable = new RunnableStub();
		executionTime = 100;
	}

	protected void tearDown() throws Exception {
		output.close();
	}

	public void testTime() {
		assertEquals(executionTime, Benchmark.time(clock, runnable));
		assertEquals(1, runnable.callbackCount);
	}

	public void testExecute() {
		assertEquals(executionTime, benchmark.execute(runnable));
	}

	public void testSetRunCount() {
		int runCount = 10;
		assertEquals(runCount * executionTime, benchmark.setRunCount(10)
				.execute(runnable));
		assertEquals(runCount, runnable.callbackCount);
	}

	public void testOutput() throws Exception {
		String newline = System.getProperty("line.separator");
		String expected = "Benchmarking 'testOutput':" + newline
				+ "\tRun 1/3:\t10ms" + newline + "\tRun 2/3:\t20ms" + newline
				+ "\tRun 3/3:\t30ms" + newline + "Total:  \t60ms" + newline
				+ "Average:\t20.0ms" + newline;

		final int[] runTimes = { 10, 20, 30 };
		benchmark.setRunCount(runTimes.length).execute(new Runnable() {
			int runIndex = 0;

			public void run() {
				clock.time += runTimes[runIndex++];
			}
		});

		assertEquals(expected, output.toString());
	}

	class RunnableStub implements Runnable {
		int callbackCount = 0;

		public void run() {
			callbackCount++;
			clock.time += executionTime;
		}
	}
}
