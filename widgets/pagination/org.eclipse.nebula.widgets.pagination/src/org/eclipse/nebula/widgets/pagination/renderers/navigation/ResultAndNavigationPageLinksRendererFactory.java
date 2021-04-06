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
