/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.ganttchart;

import org.eclipse.swt.graphics.Color;

/**
 * This class represents one connection between two events in one direction. You may create a connection by using this class, or do it the slightly easier way by calling
 * {@link GanttChart#addConnection(GanttEvent, GanttEvent)} on the {@link GanttChart}.
 * 
 */
public class GanttConnection {

	private GanttEvent		_source;
	private GanttEvent		_target;
	private Color			_color;
	private GanttComposite	_parent;
	
	GanttConnection() {
	}

	GanttConnection(GanttEvent source, GanttEvent target, Color color) {
		this._source = source;
		this._target = target;
		this._color = color;
	}
	
	/**
	 * Creates a new connection between two events.
	 * 
	 * @param parent Gantt Chart parent
	 * @param source Source event
	 * @param target Target event
	 */
	public GanttConnection(GanttChart parent, GanttEvent source, GanttEvent target) {
		this._source = source;
		this._target = target;
		this._parent = parent.getGanttComposite();
		_parent.connectionAdded(this);
	}

	/**
	 * Creates a new connection between two events and gives the connecting line a specific color.
	 * 
	 * @param parent Gantt Chart parent 
	 * @param source Source event
	 * @param target Target event
	 * @param lineColor Color of line and arrowhead drawn between the events
	 */
	public GanttConnection(GanttChart parent, GanttEvent source, GanttEvent target, Color lineColor) {
		this._source = source;
		this._target = target;
		this._color = lineColor;
		this._parent = parent.getGanttComposite();
		_parent.connectionAdded(this);
	}

	/**
	 * Returns the source event of this connection.
	 * 
	 * @return Source event
	 */
	public GanttEvent getSource() {
		return _source;
	}

	/**
	 * Sets the source event of this connection.
	 * 
	 * @param source Source event
	 */
	public void setSource(GanttEvent source) {
		this._source = source;
	}

	/**
	 * Returns the target event of this connection.
	 * 
	 * @return Target event
	 */
	public GanttEvent getTarget() {
		return _target;
	}

	/**
	 * Sets the target event of this connection.
	 * 
	 * @param target Target event
	 */
	public void setTarget(GanttEvent target) {
		this._target = target;
	}

	/**
	 * Returns the color of the line drawn in the connection.
	 * 
	 * @return Color
	 */
	public Color getColor() {
		return _color;
	}

	/**
	 * Sets the color of the line drawn in the connection.
	 * 
	 * @param color Color or null for default color.
	 */
	public void setColor(Color color) {
		this._color = color;
	}

	/**
	 * Disposes this connection
	 */
	public void dispose() {
		_parent.connectionRemoved(this);
	}
	
	/**
	 * Clones the GanttConnection (and adds the clone to the parent)
	 */
	public Object clone() throws CloneNotSupportedException {
		GanttConnection clone = new GanttConnection();
		clone._parent = _parent;
		clone._color = _color;
		clone._source = _source;
		clone._target = _target;
		
		_parent.connectionAdded(clone);
		return clone;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GanttConnection other = (GanttConnection) obj;
		if (_source == null) {
			if (other._source != null)
				return false;
		} else if (!_source.equals(other._source))
			return false;
		if (_target == null) {
			if (other._target != null)
				return false;
		} else if (!_target.equals(other._target))
			return false;
		return true;
	}

}
