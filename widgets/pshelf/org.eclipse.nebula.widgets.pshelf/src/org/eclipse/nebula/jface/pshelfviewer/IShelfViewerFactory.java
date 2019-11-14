/*******************************************************************************
 * Copyright (c) 2007 Richard Michalsky.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors :
 *    kralikX@gmail.com (Richard Michalsky) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.jface.pshelfviewer;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

/**
 * Interface for creating sub-viewers within PShelf viewer.
 */
public interface IShelfViewerFactory {

	/**
	 * Creates and returns viewer for given content and parent.
	 *
	 * The viewer must be suitable for viewing the content,
	 * the <b>children</b> of the content will be
	 * passed into viewer's <code>setInput</code> method. Method
	 * must also connect label and content providers before returning
	 * the viewer.
	 *
	 * @param content Input provider for the created viewer
	 * @param parent Parent to created viewer's widget
	 *
	 * @return new viewer
	 */
	Viewer createViewerForContent(Composite parent, Object content);
}
