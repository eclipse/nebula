/*****************************************************************************
 * Copyright (c) 2014, 2021 Fabian Prasser, Laurent Caron
 *
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Fabian Prasser - Initial API and implementation
 * Laurent Caron <laurent dot caron at gmail dot com> - Integration into the Nebula Project
 *****************************************************************************/
package org.eclipse.nebula.widgets.tiles;

/**
 * A decorator listener. Implement this and attach it to decorators, to dispose the
 * associated resources if requested and required.
 *
 * @author Fabian Prasser
 */
public interface DecoratorListener {

	/**
	 * Called when the decorator is disposed
	 */
	public abstract void disposed();
}
