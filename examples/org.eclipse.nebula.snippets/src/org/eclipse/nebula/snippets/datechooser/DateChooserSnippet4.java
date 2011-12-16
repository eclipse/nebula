package org.eclipse.nebula.snippets.datechooser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
 * Snippet 4 for DateChooser : navigation controlled by program.
 */
public class DateChooserSnippet4 {
	static DateChooser cal;
	static List monthsList;
	static Date[] months;

	public static void main(String[] args) {
		Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new GridLayout());

    cal = new DateChooser(shell, SWT.BORDER);
    cal.setNavigationEnabled(false);

    monthsList = new List(shell, SWT.BORDER);
    GridData data = new GridData();
    data.widthHint  = 100;
    data.heightHint = 170;
    monthsList.setLayoutData(data);
    monthsList.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent event) {
			}

			public void widgetSelected(SelectionEvent event) {
				int i = monthsList.getSelectionIndex();
				cal.setCurrentMonth(months[i]);
			}
    });

    SimpleDateFormat sdf = new SimpleDateFormat("MMMM");
    months = new Date[12];
    Calendar c = Calendar.getInstance();
    c.setTime(new Date());
    for (int i = 0; i < 12; i++) {
    	c.set(Calendar.MONTH, i);
    	months[i] = c.getTime();
    	monthsList.add(sdf.format(months[i]));
    }

    shell.open();
    while ( ! shell.isDisposed() ) {
    	if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
	}
}
