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

/**
 * @author Xihui Chen
 *
 */
public interface IUndoableCommand {

	/**
	 * Restore the state of the target to the state before this command has been
	 * executed.
	 */
	public void undo();

	/**
	 * Restore the state of the target to the state after this command has been
	 * executed.
	 */
	public void redo();

	// toString() is used to obtain the text that's used
	// when displaying this command in the GUI
}
