/*******************************************************************************
 * Copyright (C) 2011 Angelo Zerr <angelo.zerr@gmail.com>, Pascal Leclercq <pascal.leclercq@gmail.com>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo ZERR - initial API and implementation
 *     Pascal Leclercq - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.pagination.renderers.navigation.graphics;

/**
 * This interface is used to configure an instance of
 * {@link NavigationPageGraphics} to customize color, background color and
 * border color of the navigation page.
 * 
 */
public interface INavigationPageGraphicsConfigurator {

	/**
	 * Configure the current navigation page.
	 * 
	 * @param page
	 */
	void configure(NavigationPageGraphics page);

}
