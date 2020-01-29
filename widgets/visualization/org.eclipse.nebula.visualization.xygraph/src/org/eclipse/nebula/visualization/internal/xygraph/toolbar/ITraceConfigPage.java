/*******************************************************************************
 * Copyright (c) 2017 Diamond Light Source Ltd.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.nebula.visualization.internal.xygraph.toolbar;

import org.eclipse.swt.widgets.Composite;

/**
 * Interface used to implements a custom Trace config page
 *
 * @author Baha El-Kassaby - first commit
 *
 */
public interface ITraceConfigPage {

	/**
	 * Apply changes to page
	 */
	public void applyChanges();

	/**
	 *
	 * @return the composite
	 */
	public Composite getComposite();

	/**
	 * Create the page
	 *
	 * @param composite
	 */
	public void createPage(Composite composite);

}
