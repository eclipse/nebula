/*******************************************************************************
 * Copyright (C) 2011 Angelo Zerr <angelo.zerr@gmail.com>, Pascal Leclercq <pascal.leclercq@gmail.com>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo ZERR - initial API and implementation
 *     Pascal Leclercq - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.pagination.snippets.model;

/**
 * Person model.
 * 
 */
public class Person {

	private String name;
	private Address address;

	public Person(String name, String addressName) {
		this.name = name;
		if (addressName != null) {
			this.address = new Address(addressName);
		}
	}

	public String getName() {
		return name;
	}

	public Address getAddress() {
		return address;
	}

}
