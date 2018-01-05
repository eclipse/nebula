/*******************************************************************************
 * Copyright (c) 2012 Laurent CARON
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation 
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.propertytable;

import org.eclipse.nebula.widgets.opal.propertytable.editor.PTEditor;

/**
 * Instances of this class are property stored in a PropertyTableWidget
 * 
 */
public class PTProperty {

	private final String name;
	private final String displayName;
	private final String description;
	private Object value;
	private String category;
	private boolean enabled = true;
	private PTEditor editor;
	private PropertyTable parentTable;

	/**
	 * Constructor
	 * 
	 * @param name name of the property
	 * @param displayName Name of the property displayed in the widget
	 * @param description Description of the property displayed in the widget
	 */
	public PTProperty(final String name, final String displayName, final String description) {
		this.name = name;
		this.displayName = displayName;
		this.description = description;
	}

	/**
	 * Constructor
	 * 
	 * @param name name of the property
	 * @param displayName Name of the property displayed in the widget
	 * @param description Description of the property displayed in the widget
	 * @param value Initial value of the property
	 */
	public PTProperty(final String name, final String displayName, final String description, final Object value) {
		this(name, displayName, description);
		this.value = value;
	}

	/**
	 * @return the category of the property
	 */
	public String getCategory() {
		return this.category;
	}

	/**
	 * @return the description of the property
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @return the displayed name of the property
	 */
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * @return the editor associated to this property
	 */
	public PTEditor getEditor() {
		return this.editor;
	}

	/**
	 * @return the name of the property
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the value of the property
	 */
	public Object getValue() {
		return this.value;
	}

	/**
	 * @return <code>true</code> if the property is enabled, <code>false</code>
	 *         otherwise
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * @param category category associated to this property
	 * @return the property
	 */
	public PTProperty setCategory(final String category) {
		this.category = category;
		return this;
	}

	/**
	 * @param editor editor associated to this property
	 * @return the property
	 */
	public PTProperty setEditor(final PTEditor editor) {
		this.editor = editor;
		return this;
	}

	/**
	 * @param enabled if <code>true</code>, the property is enabled.
	 * @return the property
	 */
	public PTProperty setEnabled(final boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	/**
	 * @param parentTable the property table associated to this property
	 * @return the property
	 */
	public PTProperty setParentTable(final PropertyTable parentTable) {
		this.parentTable = parentTable;
		return this;
	}

	/**
	 * @param value the new value of the property
	 * @return the property
	 */
	public PTProperty setValue(final Object value) {
		this.value = value;
		this.parentTable.firePTPropertyChangeListeners(this);
		return this;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.name == null ? 0 : this.name.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
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
		final PTProperty other = (PTProperty) obj;
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

}
