/*******************************************************************************
 * Copyright (c) 2013 Laurent CARON.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Laurent CARON (laurent.caron@gmail.com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.multichoice;

/**
 * Default MultiChoiceLabelProvider that uses the toString() method to determine the content of a given element
 */
public class MultiChoiceDefaultLabelProvider implements MultiChoiceLabelProvider {

	/**
	 * @see org.mihalis.opal.multiChoice.MultiChoiceLabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(final Object element) {
		return element == null ? "" : element.toString();
	}

}
