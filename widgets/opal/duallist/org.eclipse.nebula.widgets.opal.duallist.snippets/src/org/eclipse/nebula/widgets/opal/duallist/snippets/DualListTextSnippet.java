/*******************************************************************************
 * Copyright (c) 2011-2021 Laurent CARON.
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
package org.eclipse.nebula.widgets.opal.duallist.snippets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.nebula.widgets.opal.duallist.DLConfiguration;
import org.eclipse.nebula.widgets.opal.duallist.DLItem;
import org.eclipse.nebula.widgets.opal.duallist.DualList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * A simple snipper for the DualList Widget
 *
 */
public class DualListTextSnippet {
	private static final String DOUBLE_DOWN_IMAGE = "double_down.png";
	private static final String DOUBLE_UP_IMAGE = "double_up.png";
	private static final String DOUBLE_LEFT_IMAGE = "double_left.png";
	private static final String DOUBLE_RIGHT_IMAGE = "double_right.png";
	private static final String ARROW_DOWN_IMAGE = "arrow_down.png";
	private static final String ARROW_LEFT_IMAGE = "arrow_left.png";
	private static final String ARROW_UP_IMAGE = "arrow_up.png";
	private static final String ARROW_RIGHT_IMAGE = "arrow_right.png";
	private static DualList dl;

	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("Dual List Snippet");
		shell.setSize(600, 600);
		shell.setLayout(new GridLayout(1, false));

		dl = new DualList(shell, SWT.NONE);
		dl.setItems(createItems(shell));
		dl.addListener(SWT.Selection, e -> {
			System.out.println("Selection Listener called");
		});

		dl.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		GridData gdButtons = new GridData(GridData.END, GridData.FILL, true, false);
		gdButtons.widthHint = 150;

		Button changeConfiguration = new Button(shell, SWT.PUSH);
		changeConfiguration.setText("Change Configuration");
		changeConfiguration.setLayoutData(gdButtons);
		changeConfiguration.addListener(SWT.Selection, e -> dl.setConfiguration(createConfiguration()));

		Button hideButtons = new Button(shell, SWT.PUSH);
		hideButtons.setText("Hide buttons");
		hideButtons.setLayoutData(gdButtons);
		hideButtons.addListener(SWT.Selection, e -> {
			DLConfiguration config = new DLConfiguration();
			config.setDoubleDownVisible(false).setDoubleUpVisible(false).//
			setDoubleRightVisible(false).setDoubleLeftVisible(false).//
			setDownVisible(false).setUpVisible(false);
			dl.setConfiguration(config);
		});

		Button resetConfiguration = new Button(shell, SWT.PUSH);
		resetConfiguration.setText("Reset Configuration");
		resetConfiguration.setLayoutData(gdButtons);
		resetConfiguration.addListener(SWT.Selection, e -> dl.setConfiguration(null));

		shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();

	}

	private static List<DLItem> createItems(final Shell shell) {
		final List<DLItem> list = new ArrayList<DLItem>();

		String defaultFontName = null;
		int defaultHeight = -1;
		for (final FontData fontData : shell.getFont().getFontData()) {
			if (defaultFontName == null) {
				defaultFontName = fontData.getName();
			}
			if (defaultHeight == -1) {
				defaultHeight = fontData.getHeight();
			}
		}

		final Font font = new Font(shell.getDisplay(), defaultFontName, defaultHeight, SWT.BOLD);

		list.add(new DLItem("Austria"));
		list.add(new DLItem("Belgium"));
		list.add(new DLItem("Bulgaria"));
		list.add(new DLItem("Cyprus"));
		list.add(new DLItem("Czech Republic"));
		list.add(new DLItem("Denmark"));
		list.add(new DLItem("Estonia"));
		list.add(new DLItem("Finland"));
		list.add(new DLItem("France").setSelected(true));
		list.add(new DLItem("Germany"));
		list.add(new DLItem("Greece"));
		list.add(new DLItem("Hungary").setSelected(true));
		list.add(new DLItem("Ireland"));
		list.add(new DLItem("Italy"));
		list.add(new DLItem("Latvia"));
		list.add(new DLItem("Lithuania").setSelected(true));
		list.add(new DLItem("Luxembourg"));
		list.add(new DLItem("Malta"));
		list.add(new DLItem("Netherlands"));
		list.add(new DLItem("Poland").setSelected(true));
		list.add(new DLItem("Portugal"));
		list.add(new DLItem("Romania"));
		list.add(new DLItem("Slovakia"));
		list.add(new DLItem("Slovenia"));
		list.add(new DLItem("Spain"));
		list.add(new DLItem("Sweden"));
		list.add(new DLItem("United Kingdom"));

		shell.addDisposeListener(e -> {
			font.dispose();
		});

		return list;
	}

	private static DLConfiguration createConfiguration() {
		DLConfiguration config = new DLConfiguration();
		Display display = Display.getCurrent();
		// Change colors for both panels
		config.setItemsBackgroundColor(display.getSystemColor(SWT.COLOR_BLACK)).//
				setItemsForegroundColor(display.getSystemColor(SWT.COLOR_WHITE)).//
				setItemsOddLinesColor(display.getSystemColor(SWT.COLOR_GRAY));
		config.setSelectionBackgroundColor(display.getSystemColor(SWT.COLOR_DARK_GREEN)).//
				setSelectionForegroundColor(display.getSystemColor(SWT.COLOR_YELLOW)).//
				setSelectionOddLinesColor(display.getSystemColor(SWT.COLOR_RED));

		// Change text alignment
		config.setItemsTextAlignment(SWT.RIGHT).setSelectionTextAlignment(SWT.CENTER);

		// Change buttons
		config.setDownImage(createImage(ARROW_DOWN_IMAGE)).setUpImage(createImage(ARROW_UP_IMAGE)).//
				setRightImage(createImage(ARROW_RIGHT_IMAGE)).setLeftImage(createImage(ARROW_LEFT_IMAGE)).//
				setDoubleDownImage(createImage(DOUBLE_DOWN_IMAGE)).setDoubleUpImage(createImage(DOUBLE_UP_IMAGE)).//
				setDoubleLeftImage(createImage(DOUBLE_LEFT_IMAGE)).setDoubleRightImage(createImage(DOUBLE_RIGHT_IMAGE));

		return config;
	}

	private static Image createImage(String fileName) {
		Image image = new Image(Display.getCurrent(), //
				DualListTextSnippet.class.getResourceAsStream("arrows/" + fileName));
		SWTGraphicUtil.addDisposer(dl, image);
		return image;
	}
}
