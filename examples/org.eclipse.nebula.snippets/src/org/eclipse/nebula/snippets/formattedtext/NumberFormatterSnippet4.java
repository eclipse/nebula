package org.eclipse.nebula.snippets.formattedtext;

import java.util.Locale;

import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.NumberFormatter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Snippet 4 for NumberFormatter : get and set value between to fields having
 * same edit and display masks for two differents Locale (US and FR).
 */
public class NumberFormatterSnippet4 {
	public static final String EDIT_MASK = "##,##0.00";
	public static final String DISPLAY_MASK = "##,##0.##";

	public static void main(String[] args) {
		Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new GridLayout(3, false));

    final FormattedText text1 = new FormattedText(shell, SWT.BORDER | SWT.RIGHT);
    text1.setFormatter(new NumberFormatter(EDIT_MASK, DISPLAY_MASK, Locale.US));
    GridData data = new GridData();
    data.widthHint = 100;
    text1.getControl().setLayoutData(data);

    Button button = new Button(shell, SWT.PUSH);
    button.setText("Copy");

    final FormattedText text2 = new FormattedText(shell, SWT.BORDER | SWT.RIGHT);
    text2.setFormatter(new NumberFormatter(EDIT_MASK, DISPLAY_MASK, Locale.FRENCH));
    data = new GridData();
    data.widthHint = 100;
    text2.getControl().setLayoutData(data);

    button.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent event) {
			}

			public void widgetSelected(SelectionEvent event) {
				text2.setValue(text1.getValue());
			}
    });

    shell.open();
    while ( ! shell.isDisposed() ) {
    	if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
	}
}
