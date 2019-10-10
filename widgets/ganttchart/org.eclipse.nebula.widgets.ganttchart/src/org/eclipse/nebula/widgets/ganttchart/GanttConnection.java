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

import org.eclipse.swt.graphics.Color;

/**
 * This class represents one connection between two events in one direction. You may create a connection by using this class, or do it the slightly easier way by calling
 * {@link GanttChart#addConnection(GanttEvent, GanttEvent)} on the {@link GanttChart}.
 * 
 */
public class GanttConnection implements Cloneable {

	private GanttEvent		_source;
	private GanttEvent		_target;
	private Color			_color;
	private GanttComposite	_parent;
	
	GanttConnection() {
	    super();
	}

	GanttConnection(final GanttEvent source, final GanttEvent target, final Color color) {
	    this(null, source, target, color);
	}
	
	/**
	 * Creates a new connection between two events.
	 * 
	 * @param parent Gantt Chart parent
	 * @param source Source event
	 * @param target Target event
	 */
	public GanttConnection(final GanttChart parent, final GanttEvent source, final GanttEvent target) {
	    this(parent, source, target, null);
	}

	/**
	 * Creates a new connection between two events and gives the connecting line a specific color.
	 * 
	 * @param parent Gantt Chart parent 
	 * @param source Source event
	 * @param target Target event
	 * @param lineColor Color of line and arrowhead drawn between the events
	 */
	public GanttConnection(final GanttChart parent, final GanttEvent source, final GanttEvent target, final Color lineColor) {
		_source = source;
		_target = target;
		_color = lineColor;
		if (parent != null) {
		    _parent = parent.getGanttComposite();
	        _parent.connectionAdded(this);
		}
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
	public void setSource(final GanttEvent source) {
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
	public void setTarget(final GanttEvent target) {
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
	public void setColor(final Color color) {
		this._color = color;
	}

	/**
	 * Disposes this connection
	 */
	public void dispose() {
		_parent.connectionRemoved(this);
	}
	
	
	/**
	 * Set the parent composite
	 * @param _parent
	 */
	void setParentComposite(GanttComposite _parent) {
		this._parent = _parent;
	}

	/**
	 * Clones the GanttConnection (and adds the clone to the parent)
	 */
	public Object clone() throws CloneNotSupportedException { // NOPMD
		final GanttConnection clone = new GanttConnection();
		clone._parent = _parent;
		clone._color = _color;
		clone._source = _source;
		clone._target = _target;
		
		_parent.connectionAdded(clone);
		return clone;
	}

	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final GanttConnection other = (GanttConnection) obj;
		if (_source == null) {
			if (other._source != null) {
				return false;
			}
		} else if (!_source.equals(other._source)) {
			return false;
		}
		if (_target == null) {
			if (other._target != null) {
				return false;
			}
		} else if (!_target.equals(other._target)) {
			return false;
		}
		
		return true;
	}

}
