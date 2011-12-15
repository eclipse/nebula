/*******************************************************************************
* Copyright (c) 2010, Lukasz Milewski and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     Lukasz Milewski <lukasz.milewski@gmail.com> - Initial API and implementation
*******************************************************************************/
package org.eclipse.nebula.widgets.nebulatoolbar.test;

import org.eclipse.nebula.widgets.nebulatoolbar.NebulaToolbar;
import org.eclipse.nebula.widgets.nebulatoolbar.ToolbarItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class NebulaToolbarTest
{

	public static void main(String[] args)
	{
		Display display = Display.getDefault();

		final Shell shell = new Shell(display, SWT.SHELL_TRIM);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		shell.setLayout(gridLayout);

		GridData toolbarData = new GridData();
		toolbarData.heightHint = 31;
		toolbarData.grabExcessHorizontalSpace = true;
		toolbarData.horizontalAlignment = SWT.FILL;

		final NebulaToolbar toolbar = new NebulaToolbar(shell, 0);
		toolbar.setLayoutData(toolbarData);

		ToolbarItem item = new ToolbarItem(toolbar, 0);
		item.setText("Close application");
		item.setSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				shell.dispose();
			}
		});

		ToolbarItem itemVista = new ToolbarItem(toolbar, 0);
		itemVista.setText("Vista style");
		itemVista.setSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				toolbar.setMode(NebulaToolbar.MODE_VISTA);
			}
		});

		ToolbarItem itemSeven = new ToolbarItem(toolbar, 0);
		itemSeven.setText("Seven style");
		itemSeven.setSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				toolbar.setMode(NebulaToolbar.MODE_SEVEN);
			}
		});

		shell.layout();
		shell.open();

		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}

		display.dispose();
	}

}
