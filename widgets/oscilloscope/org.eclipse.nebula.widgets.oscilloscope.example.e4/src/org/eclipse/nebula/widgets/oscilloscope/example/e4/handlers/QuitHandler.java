/*******************************************************************************
 * Copyright (c) 2020 Laurent Caron
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Laurent Caron <laurent dot caron at gmail dot com> - Initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.oscilloscope.example.e4.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class QuitHandler {
	@Execute
	public void execute(final IWorkbench workbench, final Shell shell) {
		if (MessageDialog.openConfirm(shell, "Confirmation", "Do you want to exit?")) {
			workbench.close();
		}
	}
}
