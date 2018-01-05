/*******************************************************************************
 * Copyright (c) 2012 Laurent CARON
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation 
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.propertytable;

/**
 * Classes which implement this interface provide methods that deal with the
 * events that are generated when value of the property is changed
 * 
 */
public interface PTPropertyChangeListener {
	/**
	 * Sent when value is changed in a property.
	 * 
	 * @param property property which value has changed
	 */
	void propertyHasChanged(PTProperty property);
}
