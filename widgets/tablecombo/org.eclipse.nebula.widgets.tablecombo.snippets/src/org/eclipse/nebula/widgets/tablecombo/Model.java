/****************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *	Marty Jones <martybjones@gmail.com> - initial API and implementation
 *  Enrico Schnepel <enrico.schnepel@randomice.net> - clear selectedImage bug 297209
 *  Enrico Schnepel <enrico.schnepel@randomice.net> - disable selectedImage bug 297327
 *  Wolfgang Schramm <wschramm@ch.ibm.com> - added vertical alignment of text for selected table item.
 *  Enrico Schnepel <enrico.schnepel@randomice.net> - help event listener bug 326285
 *  Andreas Ehret <> - patch for bug 334786
 *  Thorsten Hake <mail@thorsten-hake.com> - fix for bug 415868
 *  Stefan Dirix <sdirix@eclipsesource.com> - offer popup to keep open bug 480298
 *****************************************************************************/
package org.eclipse.nebula.widgets.tablecombo;

/**
 * Model for TableCombo
 */
public class Model {

	private int id;
	private String description;

	public Model(int id, String description) {
		this.id = id;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Model other = (Model) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Model [id=" + id + ", description=" + description + "]";
	}
	
	

}
