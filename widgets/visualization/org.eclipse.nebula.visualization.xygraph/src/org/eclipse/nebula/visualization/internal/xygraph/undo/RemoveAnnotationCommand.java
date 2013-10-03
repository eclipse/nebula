/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.nebula.visualization.internal.xygraph.undo;

import org.eclipse.nebula.visualization.xygraph.figures.Annotation;
import org.eclipse.nebula.visualization.xygraph.figures.XYGraph;

/**The undoable command to remove an annotation.
 * @author Xihui Chen
 *
 */
public class RemoveAnnotationCommand implements IUndoableCommand {
	
	private XYGraph xyGraph;
	private Annotation annotation;
	
	public RemoveAnnotationCommand(XYGraph xyGraph, Annotation annotation) {
		this.xyGraph = xyGraph;
		this.annotation = annotation;
	}

	public void redo() {
		xyGraph.removeAnnotation(annotation);
	}

	public void undo() {		
		xyGraph.addAnnotation(annotation);
	}
	
	@Override
	public String toString() {
		return "Remove Annotation";
	}

}
