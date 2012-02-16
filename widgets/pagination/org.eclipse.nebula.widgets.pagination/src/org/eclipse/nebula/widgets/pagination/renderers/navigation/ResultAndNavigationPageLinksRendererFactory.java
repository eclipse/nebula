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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Renderer factory to create instance of {@link ResultAndNavigationPageLinksRenderer}.
 * 
 */
public class ResultAndNavigationPageLinksRendererFactory implements ICompositeRendererFactory {

	private static final ICompositeRendererFactory FACTORY = new ResultAndNavigationPageLinksRendererFactory();

	public static ICompositeRendererFactory getFactory() {
		return FACTORY;
	}

	public Composite createComposite(Composite parent, int style,
			PageableController controller) {
		return new ResultAndNavigationPageLinksRenderer(parent, SWT.NONE, controller);
	}

}
