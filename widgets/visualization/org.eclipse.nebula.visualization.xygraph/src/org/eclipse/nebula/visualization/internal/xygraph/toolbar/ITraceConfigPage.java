/*******************************************************************************
 * Copyright (c) 2017 Diamond Light Source Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
