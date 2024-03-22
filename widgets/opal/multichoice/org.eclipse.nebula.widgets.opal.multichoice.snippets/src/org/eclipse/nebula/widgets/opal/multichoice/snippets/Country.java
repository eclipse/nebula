/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Laurent CARON (laurent.caron@gmail.com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.multichoice.snippets;

/**
 * This is a POJO that represents a country
 */
public class Country {
	private String name;
	private int population;
	private String code;

	public Country(final String name, final int population) {
		this.name = name;
		this.population = population;
	}

	public Country(final String name, final String code) {
		this.name = name;
		this.code = code;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public int getPopulation() {
		return this.population;
	}

	public void setPopulation(final int population) {
		this.population = population;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(final String code) {
		this.code = code;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.code == null ? 0 : this.code.hashCode());
		result = prime * result + (this.name == null ? 0 : this.name.hashCode());
		result = prime * result + this.population;
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
		final Country other = (Country) obj;
		if (this.code == null) {
			if (other.code != null) {
				return false;
			}
		} else if (!this.code.equals(other.code)) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.population != other.population) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
