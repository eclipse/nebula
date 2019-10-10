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
package org.eclipse.nebula.visualization.widgets.datadefinition;

/**A wrapper that wraps primary data types array.
 *  This allows clients to accept all primary data types array without converting the array type.
 * @author Xihui Chen
 *
 */
public interface IPrimaryArrayWrapper {

	/**Get the array element value at index i. 
	 * @param i index
	 * @return the value at index i.
	 */
	public double get(int i);
	
	/**Get size of the array.
	 * @return size of the array.
	 */
	public int getSize();
	
	
}
