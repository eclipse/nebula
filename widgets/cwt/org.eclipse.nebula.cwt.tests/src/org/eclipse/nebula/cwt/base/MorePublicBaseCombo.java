/****************************************************************************
 * Copyright (c) 2013, Red Hat Inc.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * 	 Mickael Istria (Red Hat Inc.) - extended class for test purpose
 *****************************************************************************/
package org.eclipse.nebula.cwt.base;

import org.eclipse.nebula.cwt.v.VButton;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * This class makes some methods and fields more public
 * for internal tests purpose.
 * It doesn't change behaviour, just visibility.
 * @author mistria
 */
public class MorePublicBaseCombo extends BaseCombo {

	public MorePublicBaseCombo(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected boolean setContentFocus() {
		return true;
	}

	// Make public for the test
	@Override
	public void setContent(Control control) {
		super.setContent(control);
	}

	public VButton getButton() {
		return super.button;
	}

	@Override
	public void setOpen(boolean open, Runnable runnable) {
		super.setOpen(open, runnable);
	}

	@Override
	public void setOpen(boolean open) {
		super.setOpen(open);
	}

}
