/*******************************************************************************
 * Copyright (c) 2012 Laurent CARON.
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
package org.eclipse.nebula.widgets.opal.starrating.snippets;

import org.eclipse.nebula.widgets.opal.starrating.StarRating;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * A simple snippet for the StarRating component
 */
public class StarRatingSnippet {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("StarRating Snippet");

		shell.setLayout(new GridLayout(2, false));

		createHorizontal(shell, true);
		createHorizontal(shell, false);

		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new GridLayout(8, false));
		composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		createVertical(composite, true);
		createVertical(composite, false);

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();

	}

	private static void createHorizontal(final Shell shell, final boolean enabled) {
		for (final StarRating.SIZE size : StarRating.SIZE.values()) {
			final Label label = new Label(shell, SWT.NONE);
			label.setText("Horizontal " + (enabled ? "enabled" : "disabled") + " size=" + size.toString());
			label.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));

			final StarRating sr = new StarRating(shell, SWT.NONE);
			final GridData gd = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false);
			sr.setLayoutData(gd);
			sr.setSizeOfStars(size);
			sr.setEnabled(enabled);
			sr.setMaxNumberOfStars(5 + (enabled ? 1 : 0));
		}
	}

	private static void createVertical(final Composite composite, final boolean enabled) {
		for (final StarRating.SIZE size : StarRating.SIZE.values()) {
			final Label label = new Label(composite, SWT.NONE);
			label.setText("Vertical " + (enabled ? "enabled" : "disabled") + " size=" + size.toString());
			label.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));

			final StarRating sr = new StarRating(composite, SWT.VERTICAL | SWT.BORDER);
			sr.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
			sr.setSizeOfStars(size);
			sr.setEnabled(enabled);
			sr.setMaxNumberOfStars(5 + (enabled ? 1 : 0));
		}
	}

}
