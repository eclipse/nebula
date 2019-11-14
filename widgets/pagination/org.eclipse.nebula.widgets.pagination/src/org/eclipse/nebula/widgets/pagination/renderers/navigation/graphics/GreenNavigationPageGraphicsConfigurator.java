/*******************************************************************************
 * Copyright (C) 2011 Angelo Zerr <angelo.zerr@gmail.com>, Pascal Leclercq <pascal.leclercq@gmail.com>
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Angelo ZERR - initial API and implementation
 *     Pascal Leclercq - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.pagination.renderers.navigation.graphics;

import org.eclipse.nebula.widgets.pagination.Resources;
import org.eclipse.swt.graphics.RGB;

/**
 * Navigation page configurator to configure navigation page with green theme.
 * 
 */
public class GreenNavigationPageGraphicsConfigurator implements
		INavigationPageGraphicsConfigurator {

	private final static INavigationPageGraphicsConfigurator INSTANCE = new GreenNavigationPageGraphicsConfigurator();

	private static final RGB DARK_GREEN = new RGB(100, 126, 51);
	private static final RGB LIGHT_GREEN = new RGB(134, 167, 54);
	private static final RGB GREEN = new RGB(121, 152, 55);
	private static final RGB WHITE = new RGB(255, 255, 255);
	private static final RGB ORANGE = new RGB(228, 158, 22);

	public static INavigationPageGraphicsConfigurator getInstance() {
		return INSTANCE;
	}

	public void configure(NavigationPageGraphics page) {
		page.setBackground(Resources.getColor(DARK_GREEN));

		// Selected item styles
		page.setSelectedItemBorderColor(Resources.getColor(GREEN));
		page.setSelectedItemBackground(Resources.getColor(ORANGE));
		page.setSelectedItemForeground(Resources.getColor(WHITE));

		// Item styles
		page.setItemBorderColor(Resources.getColor(LIGHT_GREEN));
		page.setItemBackground(Resources.getColor(LIGHT_GREEN));
		page.setItemForeground(Resources.getColor(WHITE));

		// Disabled
		page.setDisabledItemForeground(Resources.getColor(GREEN));
		page.setDisabledItemBorderColor(Resources.getColor(GREEN));
		page.setDisabledItemBackground(Resources.getColor(LIGHT_GREEN));
	}

}
