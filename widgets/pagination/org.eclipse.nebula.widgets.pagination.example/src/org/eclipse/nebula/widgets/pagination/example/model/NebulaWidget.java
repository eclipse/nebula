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
package org.eclipse.nebula.widgets.pagination.example.model;

/**
 * Widget model.
 * 
 */
public class NebulaWidget {

	private final String name;
	private final String description;
	private final Person committer;

	public NebulaWidget(String name, String description, Person committer) {
		this.name = name;
		this.description = description;
		this.committer = committer;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Person getCommitter() {
		return committer;
	}
}
