/*******************************************************************************
 * Copyright (c) 2020 Patrik Dufresne (http://www.patrikdufresne.com/).
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Patrik Dufresne (info at patrikdufresne dot com) - initial API and
 * implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.fontawesome;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;

/**
 * Snippet for the FontAwesome widget
 */
public class FontAwesomeSnippet3 {

	private static Slider fSlider;
	private static int fFontSize = 22;
	private static ScrolledComposite fScrolledComposite;
	private static Label fFontLabel;

	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("FontAwesome Snippet");
		shell.setSize(1000, 600);
		shell.setLayout(new GridLayout(2, false));

		fScrolledComposite = new ScrolledComposite(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		fScrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		fScrolledComposite.setAlwaysShowScrollBars(false);
		fScrolledComposite.setExpandHorizontal(true);
		fScrolledComposite.setExpandVertical(true);

		Composite composite = new Composite(fScrolledComposite, SWT.NONE);
		fScrolledComposite.setContent(composite);

		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		rowLayout.marginTop = 5;
		rowLayout.marginRight = 5;
		rowLayout.marginLeft = 5;
		rowLayout.marginBottom = 5;
		rowLayout.pack = false;
		composite.setLayout(rowLayout);
		buildComposite(composite);
		calcMinsize(composite);
		shell.layout(true, true);

		fFontLabel = new Label(shell, SWT.NONE);
		fFontLabel.setText("Font size (22)");


		fSlider = new Slider(shell, SWT.NONE);
		fSlider.addListener(SWT.MouseUp, e -> {
			shell.setRedraw(false);
			Arrays.asList(composite.getChildren()).forEach(c -> c.dispose());
			fFontSize = fSlider.getSelection();
			buildComposite(composite);
			fFontLabel.setText("Font size (" + fFontSize + ")");
			shell.setRedraw(true);

			shell.layout(true, true);
			calcMinsize(composite);
		});
		fSlider.setPageIncrement(1);
		fSlider.setMaximum(150);
		fSlider.setMinimum(4);
		fSlider.setSelection(22);
		fSlider.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();

	}

	private static void calcMinsize(Composite composite) {
		Point computeSize = fScrolledComposite.getChildren()[0]
				.computeSize(fScrolledComposite.getShell().getClientArea().width - 50,
						-1);
		fScrolledComposite.setMinSize(computeSize);
	}

	private static void buildComposite(Composite composite) {
		try {

			Field[] fields = FontAwesome.class.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				if (!field.getType().equals(String.class)) {
					continue;
				}
				String value = (String) field.get(null);
				if (value.length() != 1) {
					continue;
				}
				Label text = new Label(composite, SWT.NONE);
				text.setFont(FontAwesome.getFont(fFontSize));
				text.setText(value);
				text.setToolTipText(field.getName());
			}
		} catch (Exception e) {
			// nop
		}
	}

}