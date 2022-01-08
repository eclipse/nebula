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

import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;

/**
 * Reacts to a resize with a delay. Inspired by
 * http://stackoverflow.com/questions/2074966/detecting-when-a-user-is-finished-resizing-swt-shell
 *
 * @author Fabian Prasser
 */
abstract class ResizeListener extends ControlAdapter implements Runnable {

	/** Time offset*/
	private static final int OFFSET    = 500;
	/** Timestamp*/
	private long             timestamp = 0;
	/** Tiles*/
	private final Tiles<?>         tiles;

	/**
	 * Constructor
	 * @param tiles
	 */
	ResizeListener(final Tiles<?> tiles){
		this.tiles = tiles;
	}

	/**
	 * Resize
	 */
	@Override
	public void controlResized(final ControlEvent e) {
		timestamp = System.currentTimeMillis();
		tiles.getDisplay().timerExec(OFFSET, this);
	}

	/**
	 * Run
	 */
	@Override
	public void run() {
		if (timestamp + OFFSET < System.currentTimeMillis()) {
			tiles.getDisplay().timerExec(-1, this);
			controlResized();
		} else {
			tiles.getDisplay().timerExec(500, this);
		}
	}

	/**
	 * Implement this to listen for resize events
	 */
	protected abstract void controlResized();
}