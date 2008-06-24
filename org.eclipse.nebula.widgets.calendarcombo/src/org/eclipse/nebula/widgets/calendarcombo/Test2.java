package org.eclipse.nebula.widgets.calendarcombo;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class Test2 {

	public static void main(String[] args) {
        Display display = new Display ();
        Shell shell = new Shell (display);
        shell.setText("Button Composite Tester");
        shell.setSize(200, 400);
        shell.setLayout(new FillLayout());
       
        // allow other date formats than default
        class Settings extends DefaultSettings {
            public List getAdditionalDateFormats() {
                List additional = new ArrayList();
                additional.add("MMddyy");
                return additional;
            }           
        }
       
        Composite inner = new Composite(shell, SWT.None);
        GridLayout gl = new GridLayout(1, true);       
        inner.setLayout(gl);

        Label foo = new Label(inner, SWT.NONE);
        foo.setText("Test");
        new CalendarCombo(inner, SWT.NONE, new Settings(), null);
   
        shell.open();

        while (!shell.isDisposed ()) {
            if (!display.readAndDispatch ()) display.sleep ();
        }
        display.dispose ();
    }

}
