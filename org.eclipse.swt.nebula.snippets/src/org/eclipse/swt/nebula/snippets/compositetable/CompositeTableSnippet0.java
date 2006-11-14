package org.eclipse.swt.nebula.snippets.compositetable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.nebula.widgets.compositetable.CompositeTable;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A CompositeTable displaying a header and some row controls but no data
 * 
 * @author djo
 */
public class CompositeTableSnippet0 {
	// Define the table's header and row objects
	//
	// A tabular layout is desired, so no layout manager is needed on the header 
	// or row.  CompositeTable will handle the layout automatically.  However,
	// if you supply a layout manager, CompositeTable will respect and use it.
	
	private static class Header extends Composite {
		public Header(Composite parent, int style) {
			super(parent, style);
			new Label(this, SWT.NULL).setText("First Name");
			new Label(this, SWT.NULL).setText("Last Name");
		}
	}
	
	private static class Row extends Composite {
		public Row(Composite parent, int style) {
			super(parent, style);
			firstName = new Text(this, SWT.NULL);
			lastName = new Text(this, SWT.NULL);
		}
		public final Text firstName;
		public final Text lastName;
	}
	
	// Where it all starts...
	
	public static void main (String [] args) {
	    Display display = new Display ();
	    Shell shell = new Shell (display);
	    shell.setText("CompositeTable Snippet 0 -- Display a header and some rows");
	    shell.setLayout(new FillLayout());

	    CompositeTable table = new CompositeTable(shell, SWT.NULL);
	    new Header(table, SWT.NULL); // Just drop the Header and Row in that order...
	    new Row(table, SWT.NULL);
	    table.setRunTime(true);
	    table.setNumRowsInCollection(40); // some arbitrary number for now
	    
	    shell.setSize(500, 150);
	    shell.open ();
	    while (!shell.isDisposed()) {
	        if (!display.readAndDispatch ()) display.sleep ();
	    }
	    display.dispose ();
	}
}
