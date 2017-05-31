/*******************************************************************************
 * Copyright (c) 2010, 2017 Oak Ridge National Laboratory and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.nebula.visualization.internal.xygraph.undo;

import org.eclipse.nebula.visualization.xygraph.figures.IXYGraph;
import org.eclipse.nebula.visualization.xygraph.figures.XYGraph;

/**
 * The command for graph configuration.
 * 
 * @author Xihui Chen
 *
 */
public class XYGraphConfigCommand implements IUndoableCommand {

	private IXYGraph xyGraph;
	private XYGraphMemento previousXYGraphMem, afterXYGraphMem;

	public XYGraphConfigCommand(IXYGraph xyGraph) {
		this((XYGraph) xyGraph);
	}

	/**
	 * Use {@link #XYGraphConfigCommand(IXYGraph)} instead
	 * 
	 * @param xyGraph
	 */
	@Deprecated
	public XYGraphConfigCommand(XYGraph xyGraph) {
		this.xyGraph = xyGraph;
		previousXYGraphMem = new XYGraphMemento();
		afterXYGraphMem = new XYGraphMemento();

		for (int i = 0; i < xyGraph.getPlotArea().getAnnotationList().size(); i++) {
			previousXYGraphMem.addAnnotationMemento(new AnnotationMemento());
			afterXYGraphMem.addAnnotationMemento(new AnnotationMemento());
		}

		for (int i = 0; i < xyGraph.getAxisList().size(); i++) {
			previousXYGraphMem.addAxisMemento(new AxisMemento());
			afterXYGraphMem.addAxisMemento(new AxisMemento());
		}

		for (int i = 0; i < xyGraph.getPlotArea().getTraceList().size(); i++) {
			previousXYGraphMem.addTraceMemento(new TraceMemento());
			afterXYGraphMem.addTraceMemento(new TraceMemento());
		}

	}

	public void redo() {
		XYGraphMementoUtil.restoreXYGraphPropsFromMemento(xyGraph, afterXYGraphMem);
	}

	public void undo() {
		XYGraphMementoUtil.restoreXYGraphPropsFromMemento(xyGraph, previousXYGraphMem);
	}

	public void savePreviousStates() {
		XYGraphMementoUtil.saveXYGraphPropsToMemento(xyGraph, previousXYGraphMem);
	}

	public void saveAfterStates() {
		XYGraphMementoUtil.saveXYGraphPropsToMemento(xyGraph, afterXYGraphMem);
	}

	/**
	 * Returns the previous graph memento
	 *
	 * @return previous graph memento
	 */
	public XYGraphMemento getPreviousXYGraphMemento() {
		return previousXYGraphMem;
	}

	/**
	 * Returns the after graph memento
	 *
	 * @return after graph memento
	 */
	public XYGraphMemento getAfterXYGraphMemento() {
		return afterXYGraphMem;
	}

	@Override
	public String toString() {
		return "Configure XYGraph Settings";
	}

}
