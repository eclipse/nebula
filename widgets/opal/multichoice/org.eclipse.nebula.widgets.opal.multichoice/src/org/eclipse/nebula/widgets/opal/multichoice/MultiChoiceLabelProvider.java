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
 * Classes which implement this interface provide methods that determine what to show in a MultiChoice control.
 */
public interface MultiChoiceLabelProvider {
	/**
	 * @param element the element for which to provide the label text
	 * @return the text string used to label the element, or "" if there is no text label for the given object
	 */
	String getText(Object element);
}
