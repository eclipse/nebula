/*******************************************************************************
 * Copyright (c) 2010, 2017 Oak Ridge National Laboratory and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.nebula.visualization.internal.xygraph.undo;

import org.eclipse.nebula.visualization.xygraph.Messages;
import org.eclipse.nebula.visualization.xygraph.figures.Annotation;
import org.eclipse.nebula.visualization.xygraph.figures.IXYGraph;

/**
 * The undoable command to add an annotation.
 * 
 * @author Xihui Chen
 *
 */
public class AddAnnotationCommand implements IUndoableCommand {

	private IXYGraph xyGraph;
	private Annotation annotation;

	/**
	 * Construct an "Add annotation command"
	 *
	 * @param xyGraph
	 *         the graph
	 * @param annotation
	 *         annotation to be added
	 */
	public AddAnnotationCommand(IXYGraph xyGraph, Annotation annotation) {
		this.xyGraph = xyGraph;
		this.annotation = annotation;
	}

	public void redo() {
		xyGraph.addAnnotation(annotation);
	}

	public void undo() {
		xyGraph.removeAnnotation(annotation);
	}

	@Override
	public String toString() {
		return Messages.Annotation_Add;
	}

}
