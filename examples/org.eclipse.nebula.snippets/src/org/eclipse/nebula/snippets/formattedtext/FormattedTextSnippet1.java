package org.eclipse.nebula.snippets.formattedtext;

import java.util.Date;
import java.util.Locale;

import org.eclipse.nebula.widgets.formattedtext.DateFormatter;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Snippet 1 for FormattedText : ModifyListener.
 */
public class FormattedTextSnippet1 {
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

    final Text lsnrInfo = new Text(shell, SWT.NONE);
    data = new GridData();
    data.widthHint = 200;
    lsnrInfo.setLayoutData(data);
    lsnrInfo.setEditable(false);

    text.getControl().addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				lsnrInfo.setText(new Date().toString());
      }
    });

    shell.open();
    while ( ! shell.isDisposed() ) {
    	if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
	}
}
