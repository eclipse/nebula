/*******************************************************************************
 * Copyright (c) 2012 Laurent CARON.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.roundedtoolbar.snippets;

import org.eclipse.nebula.widgets.opal.roundedtoolbar.RoundedToolItem;
import org.eclipse.nebula.widgets.opal.roundedtoolbar.RoundedToolbar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * A simple snippet for the Rounded Toolbar Widget
 */
public class RoundedToolbarSnippet {

	private static Color grey1;
	private static Color grey2;
	private static Image iconBubble1b;
	private static Image iconBubble1w;
	private static Image iconBubble2b;
	private static Image iconBubble2w;
	private static Image iconBubble3b;
	private static Image iconBubble3w;
	private static Image emailb;
	private static Image emailw;

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("RoundedToolbar Snippet");
		final GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 20;
		shell.setLayout(gridLayout);

		grey1 = new Color(display, 211, 211, 211);
		grey2 = new Color(display, 255, 250, 250);

		iconBubble1b = new Image(display, RoundedToolbarSnippet.class.getResourceAsStream("icons/bubble1_b.png"));
		iconBubble1w = new Image(display, RoundedToolbarSnippet.class.getResourceAsStream("icons/bubble1_w.png"));

		iconBubble2b = new Image(display, RoundedToolbarSnippet.class.getResourceAsStream("icons/bubble2_b.png"));
		iconBubble2w = new Image(display, RoundedToolbarSnippet.class.getResourceAsStream("icons/bubble2_w.png"));

		iconBubble3b = new Image(display, RoundedToolbarSnippet.class.getResourceAsStream("icons/bubble3_b.png"));
		iconBubble3w = new Image(display, RoundedToolbarSnippet.class.getResourceAsStream("icons/bubble3_w.png"));

		emailb = new Image(display, RoundedToolbarSnippet.class.getResourceAsStream("icons/email_b.png"));
		emailw = new Image(display, RoundedToolbarSnippet.class.getResourceAsStream("icons/email_w.png"));

		createFirstToolbar(shell);

		createSecondToolbar(shell, false);

		final RoundedToolbar toolbar = createSecondToolbar(shell, true);
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.widthHint = SWT.DEFAULT;
		gd.heightHint = 100;
		toolbar.setLayoutData(gd);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		grey1.dispose();
		grey2.dispose();

		iconBubble1b.dispose();
		iconBubble1w.dispose();
		iconBubble2b.dispose();
		iconBubble2w.dispose();
		iconBubble3b.dispose();
		iconBubble3w.dispose();
		emailb.dispose();
		emailw.dispose();

		display.dispose();

	}

	private static void createFirstToolbar(final Shell shell) {
		final RoundedToolbar roundedToolBar = new RoundedToolbar(shell, SWT.NONE);

		roundedToolBar.setMultiselection(true);
		roundedToolBar.setBackground(grey1);
		roundedToolBar.setCornerRadius(6);

		roundedToolBar.addControlListener(new ControlListener() {

			@Override
			public void controlResized(final ControlEvent e) {
				System.out.println(roundedToolBar.getSize());

			}

			@Override
			public void controlMoved(final ControlEvent e) {
				// TODO Auto-generated method stub

			}
		});

		final RoundedToolItem item1 = new RoundedToolItem(roundedToolBar);
		item1.setSelection(true);
		item1.setTooltipText("Multiple ballons");
		item1.setWidth(40);
		item1.setSelectionImage(iconBubble3w);
		item1.setImage(iconBubble3b);

		final RoundedToolItem item2 = new RoundedToolItem(roundedToolBar);
		item2.setTooltipText("Simple item");
		item2.setSelectionImage(iconBubble1w);
		item2.setImage(iconBubble1b);
		item2.setWidth(40);

		final RoundedToolItem item3 = new RoundedToolItem(roundedToolBar);
		item3.setTooltipText("Lot of lines\r\n\r\nThis item has a line-break");
		item3.setSelectionImage(iconBubble2w);
		item3.setImage(iconBubble2b);
		item3.setWidth(40);
	}

	private static RoundedToolbar createSecondToolbar(final Shell shell, final boolean verticalAlignment) {
		final RoundedToolbar roundedToolBar2 = new RoundedToolbar(shell, SWT.NONE);
		roundedToolBar2.setCornerRadius(8);
		roundedToolBar2.setBackground(grey1);

		final RoundedToolItem mailItem = new RoundedToolItem(roundedToolBar2);
		mailItem.setSelectionImage(emailw);
		mailItem.setImage(emailb);
		mailItem.setWidth(32);
		mailItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				System.out.println("Bar2/Button 1");
			}
		});
		if (verticalAlignment) {
			mailItem.setVerticalAlignment(SWT.TOP);
		}

		final RoundedToolItem mailItemWithText = new RoundedToolItem(roundedToolBar2);
		mailItemWithText.setTextColorSelected(grey2);
		mailItemWithText.setText("Mails");
		mailItemWithText.setSelectionImage(emailw);
		mailItemWithText.setImage(emailb);
		mailItemWithText.setWidth(65);
		mailItemWithText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				System.out.println("Bar2/Button 2");
			}
		});
		if (verticalAlignment) {
			mailItemWithText.setVerticalAlignment(SWT.CENTER);
		}

		final RoundedToolItem itemJustText = new RoundedToolItem(roundedToolBar2);
		itemJustText.setTextColorSelected(grey2);
		itemJustText.setText("Just text");
		itemJustText.setWidth(100);
		itemJustText.setAlignment(SWT.RIGHT);
		itemJustText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				System.out.println("Bar2/Button 3");
			}
		});

		if (verticalAlignment) {
			itemJustText.setVerticalAlignment(SWT.BOTTOM);
		}
		return roundedToolBar2;

	}
}
