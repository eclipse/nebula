package org.eclipse.nebula.widgets.ganttchart;

import java.util.HashMap;

abstract class AbstractGanttEvent {

	private int mLayer;
	
	/**
	 * Returns the layer of this event. By default all events are on layer zero.
	 * 
	 * @return Layer
	 */
	public int getLayer() {
		return mLayer;
	}
	
	Integer getLayerInt() {
		return new Integer(mLayer);
	}
	
	/**
	 * Sets the layer of this event. Layers can be used for showing/hiding multiple events at the same time.
	 * 
	 * @param layer What layer this item belongs to.
	 * @see GanttComposite#hideLayer(int)
	 * @see GanttComposite#showLayer(int)
	 */
	public void setLayer(int layer) {
		mLayer = layer;
	}

	
}
