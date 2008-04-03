package org.eclipse.nebula.widgets.calendarcombo;

import java.util.Calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class Tester {

	public static void main(String[] args) {
		Display display = new Display ();
		Shell shell = new Shell (display);
		shell.setText("Button Composite Tester");
		shell.setSize(200, 400);
		
		shell.setLayout(new FillLayout());
		Composite inner = new Composite(shell, SWT.None);
		GridLayout gl = new GridLayout(1, true);		
		inner.setLayout(gl);

		Label foo = new Label(inner, SWT.NONE);
		foo.setText("Whatever date is set on this one...");
		final CalendarCombo one = new CalendarCombo(inner, SWT.READ_ONLY);
		Label foo2 = new Label(inner, SWT.NONE);
		foo2.setText("Will be the start for this one...");
		CalendarCombo two = new CalendarCombo(inner, SWT.READ_ONLY, "", true, one);
		
		new Label(inner, SWT.NONE);
		CalendarCombo three = new CalendarCombo(inner, SWT.NONE);
		CalendarCombo four = new CalendarCombo(inner, SWT.NONE, "Disabled", false);
		
		one.addCalendarListener(new ICalendarListener() {
			public void popupClosed() {
			}

			public void dateChanged(Calendar date) {
				if (date != null) {
					System.err.println("Date in getDate() " + one.getDate().getTime());
					System.err.println("Date as event " + date.getTime().toString());
				}
				else
					System.err.println("null");
			}	
			
			public void dateRangeChanged(Calendar start, Calendar end) {
				System.err.println("Range from " + start.getTime() + " to " + end.getTime());
			}
		});
		
		//Utils.centerDialogOnScreen(shell);
		shell.open();

		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
}
