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
package org.eclipse.nebula.widgets.pagination.renderers.navigation;

import org.eclipse.nebula.widgets.pagination.PageableController;
import org.eclipse.nebula.widgets.pagination.renderers.ICompositeRendererFactory;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.graphics.BlackNavigationPageGraphicsConfigurator;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.graphics.BlueNavigationPageGraphicsConfigurator;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.graphics.GreenNavigationPageGraphicsConfigurator;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.graphics.INavigationPageGraphicsConfigurator;
import org.eclipse.swt.widgets.Composite;

/**
 * Renderer factory to create instance of
 * {@link ResultAndNavigationPageGraphicsRenderer}.
 * 
 */
public class ResultAndNavigationPageGraphicsRendererFactory implements
		ICompositeRendererFactory {

	private static final ICompositeRendererFactory BLUE_FACTORY = new ResultAndNavigationPageGraphicsRendererFactory(
			BlueNavigationPageGraphicsConfigurator.getInstance());

	private static final ICompositeRendererFactory GREEN_FACTORY = new ResultAndNavigationPageGraphicsRendererFactory(
			GreenNavigationPageGraphicsConfigurator.getInstance());

	private static final ICompositeRendererFactory BLACK_FACTORY = new ResultAndNavigationPageGraphicsRendererFactory(
			BlackNavigationPageGraphicsConfigurator.getInstance());

	public static ICompositeRendererFactory getBlueFactory() {
		return BLUE_FACTORY;
	}

	public static ICompositeRendererFactory getGreenFactory() {
		return GREEN_FACTORY;
	}

	public static ICompositeRendererFactory getBlackFactory() {
		return BLACK_FACTORY;
	}

	private final INavigationPageGraphicsConfigurator configurator;

	public ResultAndNavigationPageGraphicsRendererFactory(
			INavigationPageGraphicsConfigurator configurator) {
		this.configurator = configurator;
	}

	public Composite createComposite(Composite parent, int style,
			PageableController controller) {
		return new ResultAndNavigationPageGraphicsRenderer(parent, style,
				controller, configurator);
	}
}
