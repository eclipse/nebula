package org.eclipse.nebula.snippets.formattedtext;

import java.util.Locale;

import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.NumberFormatter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Snippet 2 for NumberFormatter : Locale FR, default fixed mask, no
 * default value.
 */
public class NumberFormatterSnippet2 {
	public static void main(String[] args) {
		Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new GridLayout());

    FormattedText text = new FormattedText(shell, SWT.BORDER | SWT.RIGHT);
    text.setFormatter(new NumberFormatter(Locale.FRENCH));
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
