package org.eclipse.nebula.snippets.tablecombo;

import java.util.Arrays;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.nebula.jface.tablecomboviewer.TableComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class Bug385585 {

	public static void main(String[] args) {

		// get display.
		Display display = new Display();

		// create a new visible shell.
		final Shell shell = new Shell(display);
		shell.setText("Test");
		shell.setSize(600, 400);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(shell);

		Button button = new Button(shell, SWT.PUSH);
		button.setText("Click me");
		GridDataFactory.fillDefaults().grab(false, false).applyTo(button);

		final Label label = new Label(shell, SWT.NONE);
		label.setText("Combo will appear here");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(label);

		// create a new "background" shell
		Shell limbo = new Shell(display, SWT.NONE);
		limbo.setLocation(0, 10000);
		limbo.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		limbo.setBackgroundMode(SWT.INHERIT_FORCE);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(limbo);

		final TableComboViewer comboViewer = new TableComboViewer(limbo);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(comboViewer.getControl());
		comboViewer.getTableCombo().defineColumns(1);
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return (String) element;
			}
		});
		comboViewer.setInput(Arrays.asList("One", "Two", "Three"));

		// move combo
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				label.dispose();
				comboViewer.getTableCombo().setParent(shell);
				shell.layout(true);
			}
		});

		// open the shell.
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		// dispose display
		display.dispose();
	}
}
