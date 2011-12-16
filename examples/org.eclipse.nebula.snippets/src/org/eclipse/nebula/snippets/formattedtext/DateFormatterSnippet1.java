package org.eclipse.nebula.snippets.formattedtext;

import java.util.Locale;

import org.eclipse.nebula.widgets.formattedtext.DateFormatter;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Snippet 1 for DateFormatter : default Locale US, default mask, no
 * default value.
 */
public class DateFormatterSnippet1 {
	public static void main(String[] args) {
		Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new GridLayout());

    Locale.setDefault(Locale.US);
    FormattedText text = new FormattedText(shell, SWT.BORDER);
    text.setFormatter(new DateFormatter());
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
