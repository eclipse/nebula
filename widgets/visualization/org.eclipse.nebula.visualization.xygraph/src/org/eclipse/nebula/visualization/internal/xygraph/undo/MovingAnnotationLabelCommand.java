/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.nebula.visualization.internal.xygraph.undo;

import org.eclipse.nebula.visualization.xygraph.figures.Annotation;

/**
 * The command moving an annotation label.
 * 
 * @author Xihui Chen
 *
 */
public class MovingAnnotationLabelCommand implements IUndoableCommand {

	private Annotation annotation;

	private double beforeDx, beforeDy, afterDx, afterDy;

	public MovingAnnotationLabelCommand(Annotation annotation) {
		this.annotation = annotation;
	}

	public void redo() {
		annotation.setdxdy(afterDx, afterDy);
	}

	public void undo() {
		annotation.setdxdy(beforeDx, beforeDy);
	}

	public void setBeforeMovingDxDy(double dx, double dy) {
		beforeDx = dx;
		beforeDy = dy;
	}

	public void setAfterMovingDxDy(double dx, double dy) {
		afterDx = dx;
		afterDy = dy;
	}

	@Override
	public String toString() {
		return "Move Annotation Label";
	}
}
