package org.eclipse.nebula.snippets.datechooser;

import org.eclipse.nebula.widgets.datechooser.DateChooser;
import org.eclipse.nebula.widgets.datechooser.DateChooserTheme;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Snippet 5 for DateChooser : use a custom default theme. 
 */
public class DateChooserSnippet5 {
	public static void main(String[] args) {
		Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new GridLayout());

    // Create a new default theme
    DateChooserTheme theme = new DateChooserTheme();
    theme.setFont(new Font(display, "Arial", 10, SWT.BOLD));
    theme.setGridVisible(false);
    theme.setCellPadding(3);
    theme.setFocusColor(display.getSystemColor(SWT.COLOR_BLUE));
    theme.setWeekendForeground(display.getSystemColor(SWT.COLOR_DARK_GREEN));
    DateChooserTheme.setDefaultTheme(theme);

    new DateChooser(shell, SWT.BORDER);

    shell.open();
    while ( ! shell.isDisposed() ) {
    	if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
	}
}
