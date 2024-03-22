package org.eclipse.nebula.widgets.formattedtext;

import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.IPAddressFormatter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class IPAddressFormatterSnippet {
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout());

		final FormattedText text = new FormattedText(shell, SWT.BORDER);
		text.setFormatter(new IPAddressFormatter());
		text.setValue("192.192.92.192");
		GridData data = new GridData();
		data.widthHint = 100;
		text.getControl().setLayoutData(data);

		Button btn = new Button(shell, SWT.PUSH);
		btn.setText("ok");
		btn.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				System.out.println(text.getValue());
			}
		});

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
