package org.eclipse.nebula.widgets.cdatetime.snippets;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class CDateTimeSnippetBug527399 {
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("CDateTime");
		shell.setLayout(new GridLayout(2, false));

		CDateTime cdt = new CDateTime(shell, CDT.BORDER);
		String pattern = "dd.MM.yyyy HH:mm";
		cdt.setPattern(pattern);
		cdt.setSelection(new Date());
		cdt.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Label output = new Label(shell, SWT.NONE);
		output.setText("<press enter to see output value>");

		cdt.addSelectionListener(new SelectionAdapter() {
			SimpleDateFormat format = new SimpleDateFormat(pattern);

			@Override
			public void widgetSelected(SelectionEvent e) {
				String result = format.format(cdt.getSelection());
				output.setText(result);
				System.out.println(result);
			}
		});

		shell.pack();
		Point size = shell.getSize();
		Rectangle screen = display.getMonitors()[0].getBounds();
		shell.setBounds( (screen.width - size.x) / 2, (screen.height - size.y) / 2, size.x, size.y);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}
