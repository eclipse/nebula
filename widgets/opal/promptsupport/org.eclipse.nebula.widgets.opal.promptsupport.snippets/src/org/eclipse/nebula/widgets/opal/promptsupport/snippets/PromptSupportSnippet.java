/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron@gmail.com) - initial API and
 * implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.promptsupport.snippets;

import org.eclipse.nebula.widgets.opal.promptsupport.PromptSupport;
import org.eclipse.nebula.widgets.opal.promptsupport.PromptSupport.FocusBehavior;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A simple snipper for the PromptSupport utilities
 *
 */
public class PromptSupportSnippet {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(2, true));

		createText(createGroup(shell));
		createStyledText(createGroup(shell));
		createCombo(createGroup(shell));
		createCCombo(createGroup(shell));

		shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();

	}

	private static Group createGroup(Shell shell) {
		final Group group = new Group(shell, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		return group;
	}

	private static void createText(final Group group) {
		group.setLayout(new GridLayout(2, false));
		group.setText("Text widget");

		final Label lbl0 = new Label(group, SWT.NONE);
		lbl0.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lbl0.setText("No prompt :");

		final Text txt0 = new Text(group, SWT.BORDER);
		txt0.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		final Label lbl1 = new Label(group, SWT.NONE);
		lbl1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lbl1.setText("Simple text prompt :");

		final Text txt1 = new Text(group, SWT.BORDER);
		txt1.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		PromptSupport.setPrompt("Type anything you want", txt1);

		final Label lbl2 = new Label(group, SWT.NONE);
		lbl2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lbl2.setText("Other style (bold) :");

		final Text txt2 = new Text(group, SWT.BORDER);
		txt2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		PromptSupport.setPrompt("Type anything you want in bold", txt2);
		PromptSupport.setFontStyle(SWT.BOLD, txt2);

		final Label lbl3 = new Label(group, SWT.NONE);
		lbl3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lbl3.setText("Behaviour highlight :");

		final Text txt3 = new Text(group, SWT.BORDER);
		txt3.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		PromptSupport.setPrompt("Type anything you want", txt3);
		PromptSupport.setFocusBehavior(FocusBehavior.HIGHLIGHT_PROMPT, txt3);

		final Label lbl4 = new Label(group, SWT.NONE);
		lbl4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lbl4.setText("Change colors :");

		final Text txt4 = new Text(group, SWT.BORDER);
		txt4.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		PromptSupport.setPrompt("Type anything you want", txt4);
		PromptSupport.setForeground(txt4.getDisplay().getSystemColor(SWT.COLOR_YELLOW), txt4);
		PromptSupport.setBackground(txt4.getDisplay().getSystemColor(SWT.COLOR_BLACK), txt4);

		final Label lbl5 = new Label(group, SWT.NONE);
		lbl5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lbl5.setText("Change when widget is initialized :");

		final Text txt5 = new Text(group, SWT.BORDER);
		txt5.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txt5.setText("Remove what is typed...");
		txt5.setBackground(txt4.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		txt5.setForeground(txt4.getDisplay().getSystemColor(SWT.COLOR_YELLOW));

		PromptSupport.setPrompt("Type anything you want", txt5);
		PromptSupport.setForeground(txt4.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE), txt5);
		PromptSupport.setBackground(txt4.getDisplay().getSystemColor(SWT.COLOR_WHITE), txt5);

	}

	private static void createStyledText(final Group group) {
		group.setLayout(new GridLayout(2, false));
		group.setText("StyledText widget");

		final Label lbl0 = new Label(group, SWT.NONE);
		lbl0.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lbl0.setText("No prompt :");

		final StyledText txt0 = new StyledText(group, SWT.BORDER);
		txt0.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		final Label lbl1 = new Label(group, SWT.NONE);
		lbl1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lbl1.setText("Simple text prompt :");

		final StyledText txt1 = new StyledText(group, SWT.BORDER);
		txt1.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		PromptSupport.setPrompt("Type anything you want", txt1);

		final Label lbl2 = new Label(group, SWT.NONE);
		lbl2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lbl2.setText("Other style (bold) :");

		final StyledText txt2 = new StyledText(group, SWT.BORDER);
		txt2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		PromptSupport.setPrompt("Type anything you want in bold", txt2);
		PromptSupport.setFontStyle(SWT.BOLD, txt2);

		final Label lbl3 = new Label(group, SWT.NONE);
		lbl3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lbl3.setText("Behaviour highlight :");

		final StyledText txt3 = new StyledText(group, SWT.BORDER);
		txt3.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		PromptSupport.setPrompt("Type anything you want", txt3);
		PromptSupport.setFocusBehavior(FocusBehavior.HIGHLIGHT_PROMPT, txt3);

		final Label lbl4 = new Label(group, SWT.NONE);
		lbl4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lbl4.setText("Change colors :");

		final StyledText txt4 = new StyledText(group, SWT.BORDER);
		txt4.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		PromptSupport.setPrompt("Type anything you want", txt4);
		PromptSupport.setForeground(txt4.getDisplay().getSystemColor(SWT.COLOR_YELLOW), txt4);
		PromptSupport.setBackground(txt4.getDisplay().getSystemColor(SWT.COLOR_BLACK), txt4);

		final Label lbl5 = new Label(group, SWT.NONE);
		lbl5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lbl5.setText("Change when widget is initialized :");

		final StyledText txt5 = new StyledText(group, SWT.BORDER);
		txt5.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txt5.setText("Remove what is typed...");
		txt5.setBackground(txt4.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		txt5.setForeground(txt4.getDisplay().getSystemColor(SWT.COLOR_YELLOW));

		PromptSupport.setPrompt("Type anything you want", txt5);
		PromptSupport.setForeground(txt4.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE), txt5);
		PromptSupport.setBackground(txt4.getDisplay().getSystemColor(SWT.COLOR_WHITE), txt5);

	}

	private static void createCombo(final Group group) {
		group.setLayout(new GridLayout(2, false));
		group.setText("Combo widget");

		final Label lbl0 = new Label(group, SWT.NONE);
		lbl0.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lbl0.setText("No prompt :");

		final Combo combo0 = new Combo(group, SWT.BORDER);
		combo0.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		final Label lbl1 = new Label(group, SWT.NONE);
		lbl1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lbl1.setText("Simple text prompt :");

		final Combo combo1 = new Combo(group, SWT.BORDER);
		combo1.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		PromptSupport.setPrompt("Type anything you want", combo1);

		final Label lbl2 = new Label(group, SWT.NONE);
		lbl2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lbl2.setText("Other style (bold) :");

		final Combo combo2 = new Combo(group, SWT.BORDER);
		combo2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		PromptSupport.setPrompt("Type anything you want in bold", combo2);
		PromptSupport.setFontStyle(SWT.BOLD, combo2);

		final Label lbl3 = new Label(group, SWT.NONE);
		lbl3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lbl3.setText("Behaviour highlight :");

		final Combo combo3 = new Combo(group, SWT.BORDER);
		combo3.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		PromptSupport.setPrompt("Type anything you want", combo3);
		PromptSupport.setFocusBehavior(FocusBehavior.HIGHLIGHT_PROMPT, combo3);

		final Label lbl4 = new Label(group, SWT.NONE);
		lbl4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lbl4.setText("Change colors :");

		final Combo combo4 = new Combo(group, SWT.BORDER);
		combo4.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		PromptSupport.setPrompt("Type anything you want", combo4);
		PromptSupport.setForeground(combo4.getDisplay().getSystemColor(SWT.COLOR_YELLOW), combo4);
		PromptSupport.setBackground(combo4.getDisplay().getSystemColor(SWT.COLOR_BLACK), combo4);

		final Label lbl5 = new Label(group, SWT.NONE);
		lbl5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lbl5.setText("Change when widget is initialized :");

		final Combo combo5 = new Combo(group, SWT.BORDER);
		combo5.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		combo5.setText("Remove what is typed...");
		combo5.setBackground(combo4.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		combo5.setForeground(combo4.getDisplay().getSystemColor(SWT.COLOR_YELLOW));

		PromptSupport.setPrompt("Type anything you want", combo5);
		PromptSupport.setForeground(combo4.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE), combo5);
		PromptSupport.setBackground(combo4.getDisplay().getSystemColor(SWT.COLOR_WHITE), combo5);

	}

	private static void createCCombo(final Group group) {
		group.setLayout(new GridLayout(2, false));
		group.setText("CCombo widget");

		final Label lbl0 = new Label(group, SWT.NONE);
		lbl0.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lbl0.setText("No prompt :");

		final CCombo combo0 = new CCombo(group, SWT.BORDER);
		combo0.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		final Label lbl1 = new Label(group, SWT.NONE);
		lbl1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lbl1.setText("Simple text prompt :");

		final CCombo txt1 = new CCombo(group, SWT.BORDER);
		txt1.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		PromptSupport.setPrompt("Type anything you want", txt1);

		final Label lbl2 = new Label(group, SWT.NONE);
		lbl2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lbl2.setText("Other style (bold) :");

		final CCombo txt2 = new CCombo(group, SWT.BORDER);
		txt2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		PromptSupport.setPrompt("Type anything you want in bold", txt2);
		PromptSupport.setFontStyle(SWT.BOLD, txt2);

		final Label lbl3 = new Label(group, SWT.NONE);
		lbl3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lbl3.setText("Behaviour highlight :");

		final CCombo txt3 = new CCombo(group, SWT.BORDER);
		txt3.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		PromptSupport.setPrompt("Type anything you want", txt3);
		PromptSupport.setFocusBehavior(FocusBehavior.HIGHLIGHT_PROMPT, txt3);

		final Label lbl4 = new Label(group, SWT.NONE);
		lbl4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lbl4.setText("Change colors :");

		final CCombo txt4 = new CCombo(group, SWT.BORDER);
		txt4.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		PromptSupport.setPrompt("Type anything you want", txt4);
		PromptSupport.setForeground(txt4.getDisplay().getSystemColor(SWT.COLOR_YELLOW), txt4);
		PromptSupport.setBackground(txt4.getDisplay().getSystemColor(SWT.COLOR_BLACK), txt4);

		final Label lbl5 = new Label(group, SWT.NONE);
		lbl5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lbl5.setText("Change when widget is initialized :");

		final CCombo txt5 = new CCombo(group, SWT.BORDER);
		txt5.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txt5.setText("Remove what is typed...");
		txt5.setBackground(txt4.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		txt5.setForeground(txt4.getDisplay().getSystemColor(SWT.COLOR_YELLOW));

		PromptSupport.setPrompt("Type anything you want", txt5);
		PromptSupport.setForeground(txt4.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE), txt5);
		PromptSupport.setBackground(txt4.getDisplay().getSystemColor(SWT.COLOR_WHITE), txt5);

	}

}