/*******************************************************************************
 * Copyright (c) 2011-2021 Laurent CARON
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Laurent CARON (laurent.caron at gmail dot com) - Initial implementation and API
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.launcher;

import org.eclipse.swt.graphics.Image;

/**
 * Instances of this class are POJO to store information handled by the Launcher
 */
class LauncherItem {
	String title;
	Image image;
	LauncherLabel label;

	/**
	 * Constructor
	 *
	 * @param title text associated to the item
	 * @param image image associated to the item
	 */
	LauncherItem(final String title, final Image image) {
		this.title = title;
		this.image = image;
	}
}
