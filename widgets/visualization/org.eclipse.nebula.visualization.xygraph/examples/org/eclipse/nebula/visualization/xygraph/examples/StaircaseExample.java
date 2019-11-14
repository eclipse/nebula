/*******************************************************************************
 * Copyright (c) 2017 Oak Ridge National Laboratory and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.examples;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.nebula.visualization.xygraph.dataprovider.CircularBufferDataProvider;
import org.eclipse.nebula.visualization.xygraph.dataprovider.Sample;
import org.eclipse.nebula.visualization.xygraph.figures.Legend;
import org.eclipse.nebula.visualization.xygraph.figures.ToolbarArmedXYGraph;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.figures.XYGraph;
import org.eclipse.nebula.visualization.xygraph.figures.XYGraphFlags;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.TraceType;
import org.eclipse.nebula.visualization.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * JUnit demo of the plot with 'staircase' data, including gaps, as it can be
 * used for showing historic data.
 * 
 * @author Kay Kasemir
 * @author Baha El-Kassaby - Modify test to be a GUI example
 */
public class StaircaseExample {
	private static int next_x = 1;

	public static void main(final String[] args) {
		// Main window (shell)
		final Shell shell = new Shell();
		shell.setSize(800, 500);
		shell.open();

		// XYGraph
		final LightweightSystem lws = new LightweightSystem(shell);
		final ToolbarArmedXYGraph plot = new ToolbarArmedXYGraph(new XYGraph(),
				XYGraphFlags.SEPARATE_ZOOM | XYGraphFlags.STAGGER);
		final XYGraph xygraph = (XYGraph) plot.getXYGraph();
		xygraph.setTransparent(false);
		xygraph.setTitle("You should see a line. Zoom out to see more data");
		lws.setContents(plot);

		// Add data & trace
		final CircularBufferDataProvider data = new CircularBufferDataProvider(true);
		data.addSample(new Sample(next_x++, 1, 1, 1, 0, 0));
		data.addSample(new Sample(next_x++, 2, 1, 1, 0, 0));
		data.addSample(new Sample(next_x++, 3, 1, 1, 0, 0));
		data.addSample(new Sample(next_x++, 1, 1, 1, 0, 0));
		data.addSample(new Sample(next_x++, 1, 1, 1, 0, 0));
		data.addSample(new Sample(next_x++, 1, 1, 1, 0, 0));
		// Add Double.NaN gap, single point
		data.addSample(new Sample(next_x++, Double.NaN, 0, 0, 0, 0, "Disconnected"));
		data.addSample(new Sample(next_x++, 1, 0, 0, 0, 0));
		// Another gap, single point
		data.addSample(new Sample(next_x++, Double.NaN, 0, 0, 0, 0, "Disconnected"));
		data.addSample(new Sample(next_x++, 2, 0, 0, 0, 0));
		// Last value is valid 'forever'
		data.addSample(new Sample(Double.MAX_VALUE, 2, 0, 0, 0, 0));

		// Always looked OK with this range
		xygraph.getPrimaryXAxis().setRange(data.getXDataMinMax());
		xygraph.getPrimaryYAxis().setRange(data.getYDataMinMax());

		// With STEP_HORIZONTALLY this should have shown just a horizontal
		// line, but a bug resulted in nothing when both the 'start' and 'end'
		// point of the horizontal line were outside the plot range.
		// Similarly, using STEP_VERTICALLY failed to draw anything when
		// both end-points of the horizontal or vertical section of the step
		// were outside the plot. (fixed)
		//
		// There's still a question about handling 'YErrorInArea':
		// For now, the axis intersection removes x/y errors,
		// so when moving a sample with y error left or right outside
		// of the plot range, the error area suddenly shrinks when
		// the axis intersection is assumed to have +-0 y error.
		xygraph.getPrimaryXAxis().setRange(4.1, 4.9);

		// Gap, start of X range, sample @ x==8, gap @ 9, end of range.
		// Bug failed to show line from that sample up to gap @ 9.
		xygraph.getPrimaryXAxis().setRange(7.5, 9.5);

		final Trace trace = new Trace("Demo", xygraph.getPrimaryXAxis(), xygraph.getPrimaryYAxis(), data);
		trace.setTraceType(TraceType.STEP_HORIZONTALLY);
		// trace.setTraceType(TraceType.STEP_VERTICALLY);

		// // SOLID_LINE does not show individual points
		// trace.setTraceType(TraceType.SOLID_LINE);
		// trace.setPointStyle(PointStyle.CIRCLE);

		trace.setErrorBarEnabled(true);
		trace.setDrawYErrorInArea(true);
		xygraph.addTrace(trace);

		Font LEGEND_FONT = XYGraphMediaFactory.getInstance().getFont(new FontData("Lucida Sans", 11, SWT.BOLD));
		
		Legend legend = xygraph.getLegend(trace);
		legend.setDrawBorder(true);
		legend.setPreferredHeight(100);
		legend.setTextFont(LEGEND_FONT);
		
		// SWT main loop
		final Display display = Display.getDefault();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
}
