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

import java.util.EventListener;

/**
 * Definition of listeners that react on string value change.
 * 
 * @author Xihui Chen
 * 
 */
public interface IManualStringValueChangeListener extends EventListener{
	/**
	 * React on a manual value changing.
	 * 
	 * @param newValue
	 *            The new value.
	 */
	void manualValueChanged(String newValue);
}
