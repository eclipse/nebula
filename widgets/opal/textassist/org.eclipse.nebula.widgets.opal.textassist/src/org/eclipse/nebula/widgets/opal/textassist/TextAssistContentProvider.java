/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Laurent CARON (laurent.caron@gmail.com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.textassist;

import java.util.List;

/**
 * This class is a content provider for the TextAssist widget. When the user
 * types something, an instance returns an arraylist of proposition based on the
 * typed text.
 */
public abstract class TextAssistContentProvider {
	private TextAssist textAssist;

	/**
	 * Provides the content
	 * 
	 * @param entry text typed by the user
	 * @return an array list of String that contains propositions for the entry
	 *         typed by the user
	 */
	public abstract List<String> getContent(final String entry);

	/**
	 * @param textAssist the textAssist to set
	 */
	protected void setTextAssist(final TextAssist textAssist) {
		this.textAssist = textAssist;
	}

	/**
	 * @return the max number of propositions.
	 */
	protected int getMaxNumberOfLines() {
		return this.textAssist.getNumberOfLines();
	}
}
