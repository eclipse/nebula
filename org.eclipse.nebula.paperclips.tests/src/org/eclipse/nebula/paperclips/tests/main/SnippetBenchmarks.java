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
package org.eclipse.nebula.paperclips.tests.main;

import java.text.NumberFormat;

import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.PrintJob;
import org.eclipse.nebula.paperclips.snippets.Snippet2;
import org.eclipse.nebula.paperclips.snippets.Snippet3;
import org.eclipse.nebula.paperclips.snippets.Snippet4;
import org.eclipse.nebula.paperclips.snippets.Snippet5;
import org.eclipse.nebula.paperclips.snippets.Snippet6;
import org.eclipse.nebula.paperclips.snippets.Snippet7;
import org.eclipse.nebula.paperclips.snippets.Snippet8;
import org.eclipse.nebula.paperclips.tests.benchmark.Benchmark;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;

/**
 * Benchmarks the time required to layout the various snippets.
 * 
 * @author Matthew
 */
public class SnippetBenchmarks {
	/**
	 * Executes the benchmark.
	 * 
	 * @param args
	 *            command-line args.
	 */
	public static void main(String[] args) {
		// Bug in SWT--a Display must be instantiated under Linux in order to
		// use printer
		Display.getDefault();

		benchmarkSnippet8();
	}

	private static void benchmarkSnippet8() {
		final Printer printer = new Printer();
		printer.startJob("benchmarkSnippet8");

		final PrintJob job = new PrintJob("Snippet8", Snippet8.createPrint());

		final GC gc = new GC(printer);
		new Benchmark().setName("getPageEnumeration").setRunCount(10).execute(
				new Runnable() {
					public void run() {
						PaperClips.getPageEnumeration(job, printer, gc)
								.nextPage();
					}
				});
		gc.dispose();

		new Benchmark().setName("getPages").setRunCount(10).execute(
				new Runnable() {
					public void run() {
						PaperClips.getPages(job, printer);
					}
				});

		printer.cancelJob();
		printer.dispose();
	}

	static void benchmarkSnippets() {
		String[] names = {"Snippet2", "Snippet3", "Snippet4", "Snippet5",
				"Snippet6", "Snippet7"};
		Print[] documents = {Snippet2.createPrint(), Snippet3.createPrint(),
				Snippet4.createPrint(), Snippet5.createPrint(),
				Snippet6.createPrint(), Snippet7.createPrint()};
		final Printer printer = new Printer();
		printer.startJob("benchmarkSnippets");
		final int RUN_COUNT = 100;

		long total = 0;
		for (int i = 0; i < documents.length; i++) {
			final PrintJob job = new PrintJob(names[i], documents[i])
					.setMargins(108);
			total += new Benchmark().setRunCount(RUN_COUNT).setName(names[i])
					.execute(new Runnable() {
						public void run() {
							PaperClips.getPages(job, printer);
						}
					});
		}

		printer.cancelJob();
		printer.dispose();

		printFinalResult(total, total / (double) (RUN_COUNT * documents.length));
	}

	private static void printFinalResult(long total, final double average) {
		System.out.println();
		System.out.println("Grand total:\t" + total + "ms");
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setMinimumFractionDigits(1);
		format.setMaximumFractionDigits(1);
		System.out.println("Average:    \t" + format.format(average) + "ms");
	}
}
