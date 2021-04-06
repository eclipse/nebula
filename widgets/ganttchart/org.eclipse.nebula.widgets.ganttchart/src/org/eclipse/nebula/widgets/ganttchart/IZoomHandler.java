/*******************************************************************************
 * Copyright (c) 2013 Dirk Fauth and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Dirk Fauth <dirk.fauth@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.ganttchart;

import org.eclipse.swt.graphics.Point;

public interface IZoomHandler {

	/**
     * Zooms in. If zooming is disabled, does nothing.
     */
    public void zoomIn();
    
    public void zoomIn(boolean fromMouseWheel, Point mouseLoc);
    
	/**
     * Zooms out. If zooming is disabled, does nothing.
     */
    public void zoomOut();
    
    public void zoomOut(boolean fromMouseWheel, Point mouseLoc);
    
	/**
     * Resets the zoom level to that set in the settings.
     */
    public void resetZoom();
}
