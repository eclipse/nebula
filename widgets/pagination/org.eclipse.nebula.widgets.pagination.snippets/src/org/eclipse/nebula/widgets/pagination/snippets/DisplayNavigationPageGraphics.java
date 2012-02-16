package org.eclipse.nebula.widgets.pagination.snippets;

import org.eclipse.nebula.widgets.pagination.PaginationHelper;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.graphics.NavigationPageGraphics;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.graphics.NavigationPageGraphicsItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class DisplayNavigationPageGraphics {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Drawing Example");

		int[] indexes = PaginationHelper.getPageIndexes(1, 20, 10);

		NavigationPageGraphics canvas = new NavigationPageGraphics(shell, SWT.BORDER) {
			@Override
			protected void handleSelection(NavigationPageGraphicsItem pageItem) {
				System.err.println(pageItem);
			}
		};

		// PageItemsThemesHelper.setGreenTheme(canvas);

		canvas.update(indexes, 1);

		// canvas.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// System.err.println(e);
		// }
		// });

		canvas.setSize(150, 150);
		canvas.setLocation(20, 20);
		shell.open();
		shell.setSize(200, 220);
		//
		// GC gc = new GC(canvas);
		// gc.setForeground(new Color(display, 100, 100, 200));
		// gc.drawRectangle(10, 10, 40, 45);
		// gc.drawString("1", 15, 14);
		//
		// gc.dispose();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
