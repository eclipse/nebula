/*******************************************************************************
 * Copyright (c) 2012 Laurent CARON
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.propertytable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;

import org.eclipse.nebula.widgets.opal.propertytable.editor.PTEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Instances of this class are property stored in a PropertyTableWidget
 *
 */
public class PTProperty {

	private final String name;
	private final String displayName;
	private final String description;
	private Object value;
	private Object originalValue;
	private String category;
	private boolean enabled = true;
	private PTEditor editor;
	private PropertyTable parentTable;
	private Item associatedItem;

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
		originalValue = clone(value);
	}

	private Object clone(final Object object) {
		if (object == null) {
			return null;
		}
		try {
			// First serializing the object and its state to memory using ByteArrayOutputStream instead of FileOutputStream.
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			final ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(object);

			// And then deserializing it from memory using ByteArrayOutputStream instead of FileInputStream,
			// Deserialization process will create a new object with the same state as in the serialized object.
			final ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
			final ObjectInputStream in = new ObjectInputStream(bis);
			return in.readObject();
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * @return the category of the property
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @return the description of the property
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the displayed name of the property
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @return the editor associated to this property
	 */
	public PTEditor getEditor() {
		return editor;
	}

	/**
	 * @return the name of the property
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the value of the property
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @return <code>true</code> if the property is enabled, <code>false</code>
	 *         otherwise
	 */
	public boolean isEnabled() {
		return enabled;
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
	 * @param item item (TableItem or TreeItem) associated to this property
	 */
	void setAssociatedItem(final Item item) {
		associatedItem = item;
	}

	/**
	 * Change the font used to display the "display name" in the table
	 *
	 * @param font new font
	 */
	public void changeFont(final Font font) {
		if (font == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		if (font.isDisposed()) {
			SWT.error(SWT.ERROR_WIDGET_DISPOSED);
		}
		if (associatedItem instanceof TableItem) {
			((TableItem) associatedItem).setFont(0, font);
		} else {
			((TreeItem) associatedItem).setFont(0, font);
		}
	}

	/**
	 * Change the background color used to display the "display name" in the table
	 *
	 * @param color new background color
	 */
	public void changeBackgroundColor(final Color color) {
		if (color == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		if (color.isDisposed()) {
			SWT.error(SWT.ERROR_WIDGET_DISPOSED);
		}
		if (associatedItem instanceof TableItem) {
			((TableItem) associatedItem).setBackground(0, color);
		} else {
			((TreeItem) associatedItem).setBackground(0, color);
		}
	}

	/**
	 * Change the foreground color used to display the "display name" in the table
	 *
	 * @param color new foreground color
	 */
	public void changeForegroundColor(final Color color) {
		if (color == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		if (color.isDisposed()) {
			SWT.error(SWT.ERROR_WIDGET_DISPOSED);
		}
		if (associatedItem instanceof TableItem) {
			((TableItem) associatedItem).setForeground(0, color);
		} else {
			((TreeItem) associatedItem).setForeground(0, color);
		}
	}

	/**
	 * @param value the new value of the property
	 * @return the property
	 */
	public PTProperty setValue(final Object value) {
		this.value = value;
		parentTable.firePTPropertyChangeListeners(this);
		return this;
	}


	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(name);
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
		if (!Objects.equals(name, other.name)) {
			return false;
		}
		return true;
	}

	/**
	 * Discard any change and set the value to the original value
	 */
	public void discardChange() {
		value = originalValue;
		parentTable.rebuild();
	}

}
