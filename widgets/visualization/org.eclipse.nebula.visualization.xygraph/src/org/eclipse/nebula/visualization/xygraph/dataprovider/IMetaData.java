/*******************************************************************************
 * Copyright (c) 2016 Bernhard Wedl and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Bernhard Wedl - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.visualization.xygraph.dataprovider;

import java.util.Map;

/**
 * Store additional data and information associated with an object.
 * 
 *
 */
public interface IMetaData {

	/**
	 * Get meta data stored with the object.
	 *
	 * @return containing meta data or <code>null</code>
	 */
	public Object getData();

	/**
	 * Store meta data with the object.
	 * 
	 * @param data
	 *            data to be stored with the object
	 */
	public void setData(Object data);

	/**
	 * Returns the value to which the specified key is mapped, or
	 * <code>null</code> if this map contains no mapping for the key.
	 *
	 * @param key
	 *            key associated with the data
	 * 
	 * @return associated meta data or <code>null</code>
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>if the key is null</li>
	 *                </ul>
	 */
	public Object getData(String key);

	/**
	 * Store meta data mapped with the key. To remove an existing entry, set the
	 * data to <code>null</code>.
	 *
	 * @param key
	 *            key to be associated with the data or <code>null</code> to
	 *            remove
	 * @param data
	 *            data to be stored with the object
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>if the key is null</li>
	 *                </ul>
	 */
	public void setData(String key, Object data);

	/**
	 * Get the available meta data entries as a immutable {@link #Map}.
	 *
	 * @return immutable map of the stored data
	 */
	public Map<String, Object> getDataCollection();

}
