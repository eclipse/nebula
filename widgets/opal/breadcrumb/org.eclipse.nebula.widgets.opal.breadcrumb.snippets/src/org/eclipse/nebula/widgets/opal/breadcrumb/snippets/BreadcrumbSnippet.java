/*******************************************************************************
 * Copyright (c) 2012 Laurent CARON. All rights reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API
 * and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.breadcrumb.snippets;

import org.eclipse.nebula.widgets.opal.breadcrumb.Breadcrumb;
import org.eclipse.nebula.widgets.opal.breadcrumb.BreadcrumbItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * A simple snippet for the breadcrumb Widget
 */
public class BreadcrumbSnippet {

	private static Image[] images;

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("BreakCrumb Snippet");
		shell.setLayout(new GridLayout(2, false));

		createImages();

		createLabelsBreadCrumb(shell);
		createButtonsBreadCrumb(shell);
		createButtonsIconsBreadCrumb(shell);
		createToggleButtonsBreadCrumb(shell);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();

	}

	private static void createImages() {
		images = new Image[5];
		final String[] fileNames = new String[] { "add.png", "bell.png", "feed.png", "house.png", "script.png" };
		for (int i = 0; i < 5; i++) {
			final Image image = new Image(Display.getCurrent(), BreadcrumbSnippet.class.getResourceAsStream(fileNames[i]));
			images[i] = image;
		}
	}

	private static void createLabelsBreadCrumb(final Shell shell) {
		final Label label = new Label(shell, SWT.NONE);
		label.setText("Label breadcrumb:");
		label.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		createBreadcrumb(shell, SWT.BORDER, SWT.CENTER, false);
		new Label(shell, SWT.NONE);

		createBreadcrumb(shell, SWT.NONE, SWT.CENTER, false);
	}

	private static void createBreadcrumb(final Shell shell, final int breadCrumbArgument, final int itemArgument, final boolean showImages) {
		final Breadcrumb bc = new Breadcrumb(shell, breadCrumbArgument);
		bc.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		for (int i = 1; i < 5; i++) {
			final BreadcrumbItem item = new BreadcrumbItem(bc, itemArgument);
			item.setText("Label " + String.valueOf(i));
			if (showImages) {
				item.setImage(images[i]);
				item.setSelectionImage(images[i]);
			}
			item.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					System.out.println("Click !");
				}

			});
		}
	}

	private static void createButtonsBreadCrumb(final Shell shell) {
		final Label label = new Label(shell, SWT.NONE);
		label.setText("Buttons breadcrumb:");
		label.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		createBreadcrumb(shell, SWT.BORDER, SWT.CENTER | SWT.PUSH, false);
		new Label(shell, SWT.NONE);

		createBreadcrumb(shell, SWT.NONE, SWT.CENTER | SWT.PUSH, false);
	}

	private static void createButtonsIconsBreadCrumb(final Shell shell) {
		final Label label = new Label(shell, SWT.NONE);
		label.setText("Buttons breadcrumb:");
		label.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		createBreadcrumb(shell, SWT.BORDER, SWT.CENTER | SWT.PUSH, true);
		new Label(shell, SWT.NONE);

		createBreadcrumb(shell, SWT.NONE, SWT.CENTER | SWT.PUSH, true);
	}

	private static void createToggleButtonsBreadCrumb(final Shell shell) {
		final Label label = new Label(shell, SWT.NONE);
		label.setText("Toggle buttons breadcrumb:");
		label.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		createBreadcrumb(shell, SWT.BORDER, SWT.CENTER | SWT.TOGGLE, false);
		new Label(shell, SWT.NONE);

		createBreadcrumb(shell, SWT.NONE, SWT.CENTER | SWT.TOGGLE, false);
	}

}
