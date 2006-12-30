package org.eclipse.swt.nebula.snippets.cdatetime;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.nebula.widgets.cdatetime.CDT;
import org.eclipse.swt.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class CDTSnippet01 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("CDC");
		shell.setLayout(new GridLayout());

		GridLayout layout = new GridLayout(2, true);
		shell.setLayout(layout);

		final CDateTime cdc1 = new CDateTime(shell, CDT.BORDER | CDT.DROP_DOWN);
		cdc1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Combo combo = new Combo(shell, SWT.BORDER);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		shell.pack();
		Point size = shell.getSize();
		Rectangle screen = display.getMonitors()[0].getBounds();
		shell.setBounds(
				(screen.width-size.x)/2,
				(screen.height-size.y)/2,
				size.x,
				size.y
		);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
