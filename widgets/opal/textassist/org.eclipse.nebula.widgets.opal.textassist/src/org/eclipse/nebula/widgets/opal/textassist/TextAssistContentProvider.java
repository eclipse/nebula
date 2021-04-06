/*******************************************************************************
 * Copyright (c) 2011-2020 Laurent CARON.
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
