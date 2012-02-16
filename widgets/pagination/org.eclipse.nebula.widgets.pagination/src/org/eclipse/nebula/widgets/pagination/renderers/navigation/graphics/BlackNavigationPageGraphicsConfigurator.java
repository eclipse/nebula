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

import org.eclipse.nebula.widgets.pagination.Resources;
import org.eclipse.swt.graphics.RGB;

/**
 * Navigation page configurator to configure navigation page with black theme.
 * 
 */
public class BlackNavigationPageGraphicsConfigurator implements INavigationPageGraphicsConfigurator {

	private final static INavigationPageGraphicsConfigurator INSTANCE = new BlackNavigationPageGraphicsConfigurator();

	private static final RGB ORANGE = new RGB(236, 82, 16);
	private static final RGB WHITE = new RGB(255, 255, 255);
	private static final RGB BIG_DARK_GRAY = new RGB(49, 49, 49);
	private static final RGB DARK_GRAY = new RGB(62, 62, 62);
	private static final RGB LIGHT_GRAY = new RGB(134, 134, 134);

	public static INavigationPageGraphicsConfigurator getInstance() {
		return INSTANCE;
	}

	public void configure(NavigationPageGraphics page) {

		page.setBackground(Resources.getColor(BIG_DARK_GRAY));

		// Selected item styles
		page.setSelectedItemBorderColor(Resources.getColor(BIG_DARK_GRAY));
		page.setSelectedItemBackground(Resources.getColor(ORANGE));
		page.setSelectedItemForeground(Resources.getColor(WHITE));

		// Item styles
		page.setItemBorderColor(Resources.getColor(DARK_GRAY));
		page.setItemBackground(Resources.getColor(DARK_GRAY));
		page.setItemForeground(Resources.getColor(WHITE));

		// Disabled
		page.setDisabledItemForeground(Resources.getColor(LIGHT_GRAY));
		page.setDisabledItemBorderColor(Resources.getColor(DARK_GRAY));
		page.setDisabledItemBackground(Resources.getColor(DARK_GRAY));

	}

}
