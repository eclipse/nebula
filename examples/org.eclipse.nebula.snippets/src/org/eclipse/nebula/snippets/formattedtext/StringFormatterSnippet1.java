package org.eclipse.nebula.snippets.formattedtext;

import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.StringFormatter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Snippet 1 for StringFormatter.
 */
public class StringFormatterSnippet1 {
	public static void main(String[] args) {
		Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new GridLayout());
    shell.setSize(300, 200);

    FormattedText text = new FormattedText(shell, SWT.BORDER);
    text.setFormatter(new StringFormatter());
    GridData data = new GridData();
    data.widthHint = 200;
    text.getControl().setLayoutData(data);

    shell.open();
    while ( ! shell.isDisposed() ) {
    	if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
	}
}
