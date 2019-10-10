/*******************************************************************************
 * Copyright (c) 2013 Laurent CARON.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Laurent CARON (laurent.caron@gmail.com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.multichoice;

/**
 * Default MultiChoiceLabelProvider that uses the toString() method to determine the content of a given element
 */
public class MultiChoiceDefaultLabelProvider implements MultiChoiceLabelProvider {

	/**
	 * @see org.eclipse.nebula.widgets.opal.multichoice.MultiChoiceLabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(final Object element) {
		return element == null ? "" : element.toString();
	}
}
