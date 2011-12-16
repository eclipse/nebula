package org.eclipse.nebula.snippets.datechooser;

import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;

import org.eclipse.nebula.widgets.datechooser.DateChooser;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

/**
 * Snippet 3 for DateChooser : multi selection, selection listener.
 */
public class DateChooserSnippet3 {
	static DateChooser cal;
	static List selectedDates;
	static DateFormat df;

	public static void main(String[] args) {
		Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new GridLayout());

    cal = new DateChooser(shell, SWT.BORDER | SWT.MULTI);
    cal.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent event) {
			}

			public void widgetSelected(SelectionEvent event) {
				selectedDates.removeAll();
				for (Iterator it = cal.getSelectedDates().iterator(); it.hasNext(); ) {
					Date d = (Date) it.next();
					selectedDates.add(df.format(d));
				}
			}
    });

    df = DateFormat.getDateInstance(DateFormat.MEDIUM);
    selectedDates = new List(shell, SWT.BORDER);
    GridData data = new GridData();
    data.widthHint  = 100;
    data.heightHint = 100;
    selectedDates.setLayoutData(data);

    shell.open();
    while ( ! shell.isDisposed() ) {
    	if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
	}
}
