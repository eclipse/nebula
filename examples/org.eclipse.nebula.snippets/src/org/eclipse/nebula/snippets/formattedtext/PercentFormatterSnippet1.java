package org.eclipse.nebula.snippets.formattedtext;

import java.util.Locale;

import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.PercentFormatter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Snippet 1 for PercentFormatter : default Locale US, , default fixed mask, no
 * default value.
 */
public class PercentFormatterSnippet1 {
	public static void main(String[] args) {
		Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new GridLayout());

    Locale.setDefault(Locale.US);
    final FormattedText text = new FormattedText(shell, SWT.BORDER | SWT.SINGLE);
    text.setFormatter(new PercentFormatter());
    GridData data = new GridData();
    data.widthHint = 200;
    text.getControl().setLayoutData(data);

    final Label result = new Label(shell, SWT.NONE);
    data = new GridData();
    data.widthHint = 200;
    result.setLayoutData(data);

    Button button = new Button(shell, SWT.PUSH);
    button.setText("Get value");
    button.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
      }

			public void widgetSelected(SelectionEvent e) {
				Object value = text.getValue();
				result.setText(value != null ? value.toString() : "null");
      }
    });

    shell.open();
    while ( ! shell.isDisposed() ) {
    	if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
	}
}
