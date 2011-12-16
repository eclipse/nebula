package org.eclipse.nebula.snippets.formattedtext;

import org.eclipse.nebula.widgets.formattedtext.DateFormatter;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * FormattedText snippet : no beep sound.
 */
public class NoBeepSoundFormatterSnippet {
	public static void main(String[] args) {
		Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new GridLayout());

    final FormattedText text = new FormattedText(shell, SWT.BORDER);
    text.setFormatter(new DateFormatter());
    GridData data = new GridData();
    data.widthHint = 70;
    text.getControl().setLayoutData(data);

    Button toggleBeep = new Button(shell, SWT.PUSH);
    toggleBeep.setText("Toggle beep sound");
    toggleBeep.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FormattedText.setBeepSound(! FormattedText.isBeepSound());
				text.getControl().setFocus();
			}
    });

    shell.open();
    while ( ! shell.isDisposed() ) {
    	if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
	}
}
