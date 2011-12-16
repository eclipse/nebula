package org.eclipse.nebula.snippets.formattedtext;

import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.NumberFormatter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Snippet 3 for NumberFormatter : default Locale and mask, variable length in the
 * integer part, value is provided.
 */
public class NumberFormatterSnippet3 {
	public static void main(String[] args) {
		Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new GridLayout());

    FormattedText text = new FormattedText(shell, SWT.BORDER | SWT.RIGHT);
    NumberFormatter formatter = new NumberFormatter();
    formatter.setFixedLengths(false, true);
    text.setFormatter(formatter);
    text.setValue(new Double(123.4));
    GridData data = new GridData();
    data.widthHint = 100;
    text.getControl().setLayoutData(data);

    shell.open();
    while ( ! shell.isDisposed() ) {
    	if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
	}
}
