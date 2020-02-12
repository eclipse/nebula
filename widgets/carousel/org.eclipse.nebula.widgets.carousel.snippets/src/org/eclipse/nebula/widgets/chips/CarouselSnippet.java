/*******************************************************************************
 * Copyright (c) 2020 Laurent CARON.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API and
 * implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.chips;

import org.eclipse.nebula.widgets.carousel.Carousel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * Snippet for the Carousel widget
 */
public class CarouselSnippet {

	private static Shell shell;

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final Display display = new Display();
		shell = new Shell(display);
		shell.setText("Carousel Snippet");
		shell.setLayout(new GridLayout(1, false));
		shell.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

		final Carousel carousel = new Carousel(shell, SWT.NONE);
		carousel.addImage(loadImage("images/first.png"));
		carousel.addImage(loadImage("images/second.jpg"));
		carousel.addImage(loadImage("images/third.png"));

		final Listener listener = event -> {
			System.out.println("Click on " + carousel.getSelection());
		};
		carousel.addListener(SWT.Selection, listener);

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();

	}

	private static Image loadImage(final String img) {
		return new Image(shell.getDisplay(), CarouselSnippet.class.getResourceAsStream(img));
	}

}