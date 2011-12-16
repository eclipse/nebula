package org.eclipse.nebula.snippets.datechooser;

import org.eclipse.nebula.widgets.datechooser.DateChooser;
import org.eclipse.nebula.widgets.datechooser.DateChooserTheme;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Snippet 2 for DateChooser : border, blue theme, week numbers and footer.
 */
public class DateChooserSnippet2 {
	public static void main(String[] args) {
		Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new GridLayout());

    DateChooser cal = new DateChooser(shell, SWT.BORDER);
    cal.setTheme(DateChooserTheme.BLUE);
    cal.setWeeksVisible(true);
    cal.setFooterVisible(true);

    shell.open();
    while ( ! shell.isDisposed() ) {
    	if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
	}
}
