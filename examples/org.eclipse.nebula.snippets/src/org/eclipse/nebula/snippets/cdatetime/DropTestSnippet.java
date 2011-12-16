package org.eclipse.nebula.snippets.cdatetime;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


class MyDialog extends Dialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Shell shell;

	public MyDialog (Shell parent, int style) {
		super(parent, style);
	}
	public void open() {
		Shell parent = getParent();
		final Shell shell = new Shell(parent, getStyle());
		shell.setText("Testing");
		shell.setLayout(new GridLayout());

		final CDateTime cdc2 = new CDateTime(shell, CDT.BORDER | CDT.DROP_DOWN);
		cdc2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Display display = parent.getDisplay();
		shell.layout(true, true);
		shell.pack();
		int x = shell.getSize().x;
		int y = shell.getSize().y;
		shell.setLocation(
				(display.getBounds().width-x)/2,
				(display.getBounds().height-y)/2
		);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
	}

	public void close() {
		shell.close();
	}
}

public class DropTestSnippet {

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

		final CDateTime cdc1 = new CDateTime(shell, CDT.BORDER);
		cdc1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		final CDateTime cdc2 = new CDateTime(shell, CDT.BORDER | CDT.DROP_DOWN);
		cdc2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));


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
