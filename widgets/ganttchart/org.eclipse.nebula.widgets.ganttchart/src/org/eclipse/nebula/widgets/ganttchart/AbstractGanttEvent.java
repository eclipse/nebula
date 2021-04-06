/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.ganttchart;

abstract class AbstractGanttEvent {

	private int _layer;
	
	/**
	 * Returns the layer of this event. By default all events are on layer zero.
	 * 
	 * @return Layer
	 */
	public int getLayer() {
		return _layer;
	}
	
	Integer getLayerInt() {
		return new Integer(_layer);
	}
	
	/**
	 * Sets the layer of this event. Layers can be used for showing/hiding multiple events at the same time. 
	 * <br><br>
	 * Do note that layers are not layers in the sense of Photoshop layers or such where one layer is above another. 
	 * Layers are simply an identifier to let you hide/show/alpha blend an entire set of objects with one method call. 
	 * 
	 * @param layer What layer this item belongs to.
	 * @see GanttComposite#hideLayer(int)
	 * @see GanttComposite#showLayer(int)
	 * @see GanttComposite#setLayerOpacity(int, int)
	 */
	public void setLayer(int layer) {
		_layer = layer;
	}

	
}
