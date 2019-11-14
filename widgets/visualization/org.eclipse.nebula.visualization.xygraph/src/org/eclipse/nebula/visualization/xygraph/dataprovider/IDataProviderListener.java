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
package org.eclipse.nebula.visualization.xygraph.dataprovider;

/**
 * A listener on data provider data change.
 * 
 * @author Xihui Chen
 *
 */
public interface IDataProviderListener {

	/**
	 * This method will be notified by data provider whenever the data changed
	 * in data provider
	 */
	public void dataChanged(IDataProvider dataProvider);

}
