package org.eclipse.nebula.snippets.datechooser;

import org.eclipse.nebula.widgets.datechooser.DateChooser;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Snippet 1 for DateChooser : all default settings.
 */
public class DateChooserSnippet1 {
	public static void main(String[] args) {
		Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new GridLayout());

    new DateChooser(shell, SWT.NONE);

    shell.open();
    while ( ! shell.isDisposed() ) {
    	if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
	}
}
