/*******************************************************************************
 * Copyright (c) 2006 BestSolution.at and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl<tom.schindl@bestsolution.at> - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.snippets.pgroup;

import org.eclipse.nebula.widgets.pgroup.PGroup;
import org.eclipse.nebula.widgets.pgroup.PGroupToolItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;

/*
 * Creates a PGroup.
 *
 * For a list of all Nebula PGroup example snippets see
 * http://www.eclipse.org/nebula/widgets/pgroup/snippets.php
 */
public class PGroupSnippet4 {
	private static Font FONT;
	private static FontData FONT_DATA;

	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		final PGroup group = new PGroup(shell, SWT.SMOOTH);
		group.setText("Example");
		group.setImagePosition(SWT.TOP);

		// Optionally, change strategy and toggle
		// group.setStrategy(new FormGroupStrategy());
		// group.setToggleRenderer(new TwisteToggleRenderer());

		group.setLayout(new GridLayout());

		final Label label = new Label(group, SWT.NONE);
		FONT_DATA = label.getFont().getFontData()[0];
		FONT_DATA.setHeight(12);
		FONT = new Font(display, FONT_DATA);
		label.setText("Contents");
		Button button = new Button(group, SWT.PUSH);
		button.setText("Contents");
		Scale scale = new Scale(group, SWT.HORIZONTAL);

		PGroupToolItem toolItem = new PGroupToolItem(group, SWT.RADIO);
		toolItem.setImage(new Image(display, PGroupSnippet4.class.getResourceAsStream("shield_add.png")));
		toolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox box = new MessageBox(shell,SWT.ICON_INFORMATION);
				box.setText("Activated Protection");
				box.setMessage("Activated Protection");
				box.open();
			}
		});
		toolItem.setToolTipText ("A Test Text");

		toolItem = new PGroupToolItem(group, SWT.RADIO);
		toolItem.setImage(new Image(display, PGroupSnippet4.class.getResourceAsStream("shield_delete.png")));
		toolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox box = new MessageBox(shell,SWT.ICON_INFORMATION);
				box.setText("Item 1 Selection");
				box.setMessage("Item 1 Selection!");
				box.open();
			}
		});


		shell.setSize(200, 200);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		FONT.dispose();
		display.dispose();
	}
}