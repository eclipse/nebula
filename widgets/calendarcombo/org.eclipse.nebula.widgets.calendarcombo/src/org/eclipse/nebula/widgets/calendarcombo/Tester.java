package org.eclipse.nebula.widgets.calendarcombo;

import java.util.Calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class Tester {

	public static void main(String[] args) {
		Display display = new Display ();
		Shell shell = new Shell (display);
		shell.setText("Calendar Combo Tester");
		shell.setSize(200, 400);
		
		// allow other date formats than default
		class Settings extends DefaultSettings {
			
/*			public Locale getLocale() {
				//return Locale.GERMAN;
			}
*/
			public boolean keyboardNavigatesCalendar() {
				return false;
			}

			
		}
		
		shell.setLayout(new FillLayout());
		Composite inner = new Composite(shell, SWT.None);
		GridLayout gl = new GridLayout(1, true);		
		inner.setLayout(gl);

		Label foo = new Label(inner, SWT.NONE);
		foo.setText("Test");
		final CalendarCombo cc = new CalendarCombo(inner, SWT.NONE, new Settings(), null);
		cc.addCalendarListener(new ICalendarListener() {

			public void dateChanged(Calendar date) {
				if (date == null) {
					System.err.println("Date changed to null");
				}
				else {
					System.err.println("Date changed " + date.getTime());
				}
			}

			public void dateRangeChanged(Calendar start, Calendar end) {
			}

			public void popupClosed() {
			}
			
		});

		final CalendarCombo cc2 = new CalendarCombo(inner, SWT.READ_ONLY, new Settings(), null);

		Button b = new Button(inner, SWT.PUSH);
		b.setText("Check date");
		b.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				System.err.println(cc.getDate().getTime());
			}
			
		});
	
		shell.open();

		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
}
