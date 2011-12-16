package org.eclipse.nebula.snippets.datechooser;

import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Snippet 2 for DateChooserCombo : show button on focus.
 */
public class DateChooserComboSnippet2 {
	public static void main(String[] args) {
		Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new GridLayout());
    shell.setSize(300, 200);

    DateChooserCombo combo1 = new DateChooserCombo(shell, SWT.BORDER);
    GridData data = new GridData();
		data.widthHint = 110;
		combo1.setLayoutData(data);

		DateChooserCombo combo2 = new DateChooserCombo(shell, SWT.BORDER);
		data = new GridData();
		data.widthHint = 110;
		combo2.setLayoutData(data);
		combo2.setShowButtonOnFocus(true);

		shell.open();
    while ( ! shell.isDisposed() ) {
    	if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
	}
}
