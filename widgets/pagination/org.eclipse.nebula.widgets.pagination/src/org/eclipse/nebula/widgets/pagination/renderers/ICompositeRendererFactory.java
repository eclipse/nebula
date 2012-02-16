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
package org.eclipse.nebula.widgets.pagination.renderers;

import org.eclipse.nebula.widgets.pagination.PageableController;
import org.eclipse.swt.widgets.Composite;

/**
 * SWT {@link Composite} renderer factory.
 * 
 */
public interface ICompositeRendererFactory {

	/**
	 * Create SWT {@link Composite} that you can link to the given paination
	 * controller.
	 * 
	 * @param parent
	 *            a widget which will be the parent of the new instance (cannot
	 *            be null)
	 * @param style
	 *            the style of widget to construct
	 * @param controller
	 *            the pagination controller.
	 * @return
	 */
	Composite createComposite(Composite parent, int style,
			PageableController controller);
}
