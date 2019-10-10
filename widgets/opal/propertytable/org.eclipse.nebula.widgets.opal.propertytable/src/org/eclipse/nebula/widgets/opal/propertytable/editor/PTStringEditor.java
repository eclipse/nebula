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
package org.eclipse.nebula.widgets.opal.propertytable.editor;

import org.eclipse.swt.SWT;

/**
 * This editor is used to edit string values
 */
public class PTStringEditor extends PTBaseTextEditor {

	@Override
	public void addVerifyListeners() {
	}

	@Override
	public Object convertValue() {
		return this.text.getText();
	}

	@Override
	public int getStyle() {
		return SWT.NONE;
	}

}
