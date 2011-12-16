package org.eclipse.nebula.snippets.formattedtext;

import org.eclipse.nebula.widgets.formattedtext.DateFormatter;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Snippet 2 for DateFormatter : fixed mask yyyy/MM/dd, no default value.
 */
public class DateFormatterSnippet2 {
	public static void main(String[] args) {
		Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new GridLayout());

    FormattedText text = new FormattedText(shell, SWT.BORDER);
    text.setFormatter(new DateFormatter("yyyy/MM/dd"));
    GridData data = new GridData();
    data.widthHint = 70;
    text.getControl().setLayoutData(data);

    shell.open();
    while ( ! shell.isDisposed() ) {
    	if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
	}
}
