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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

/**
 * Navigation page configurator to configure navigation page with blue theme.
 * 
 */
public class BlueNavigationPageGraphicsConfigurator implements INavigationPageGraphicsConfigurator {

	private final static INavigationPageGraphicsConfigurator INSTANCE = new BlueNavigationPageGraphicsConfigurator();

	private static final RGB DARK_BLUE = new RGB(0,49,82);
	private static final RGB BLUE = new RGB(148, 148, 231);	
	private static final RGB LIGHT_BLUE = new RGB(222, 239, 247);
	private static final RGB WHITE = new RGB(255, 255, 255);
	private static final RGB ORANGE = new RGB(236, 82, 16);
	private static final RGB GRAY = new RGB(239,237,247);	
	private static final RGB LIGHT_GRAY = new RGB(134, 134, 134);
	
	public static INavigationPageGraphicsConfigurator getInstance() {
		return INSTANCE;
	}

	public void configure(NavigationPageGraphics page) {

		page.setBackground(page.getDisplay().getSystemColor(
				SWT.COLOR_WIDGET_BACKGROUND));
		// page.setBackground(Resources.getColor(LIGHT_BLUE));

		// Selected item styles
		page.setSelectedItemBorderColor(Resources.getColor(ORANGE));
		page.setSelectedItemBackground(Resources.getColor(ORANGE));
		page.setSelectedItemForeground(Resources.getColor(WHITE));

		// Item styles
		page.setItemBorderColor(Resources.getColor(BLUE));
		page.setItemBackground(Resources.getColor(LIGHT_BLUE));
		page.setItemForeground(Resources.getColor(DARK_BLUE));

		// Disabled
		page.setDisabledItemBorderColor(Resources.getColor(LIGHT_GRAY));
		page.setDisabledItemForeground(Resources.getColor(LIGHT_GRAY));
		page.setDisabledItemBackground(Resources.getColor(GRAY));
		
		page.setRound(5);
	}

}
