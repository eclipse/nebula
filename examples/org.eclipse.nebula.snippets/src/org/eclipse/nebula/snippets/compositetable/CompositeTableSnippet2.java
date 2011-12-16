package org.eclipse.nebula.snippets.compositetable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.nebula.widgets.compositetable.CompositeTable;
import org.eclipse.nebula.widgets.compositetable.GridRowLayout;
import org.eclipse.nebula.widgets.compositetable.IRowContentProvider;
import org.eclipse.nebula.widgets.compositetable.RowFocusAdapter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A CompositeTable editing first/last name pairs
 * 
 * @author djo
 */
public class CompositeTableSnippet2 {
	// First some data to edit...
	private static class Name {
		public String first;
		public String last;
		public Name(String first, String last) {
			this.first = first;
			this.last = last;
		}
	}
	
	static Name[] swtCommitters = new Name[] {
		new Name("Grant", "Gayed"),
		new Name("Veronika", "Irvine"),
		new Name("Steve", "Northover"),
		new Name("Mike", "Wilson"),
		new Name("Christophe", "Cornu"),
		new Name("Lynne", "Kues"),
		new Name("Silenio", "Quarti"),
		new Name("Tod", "Creasey"),
		new Name("Felipe", "Heidrich"),
		new Name("Billy", "Biggs"),
		new Name("B", "Shingar")
	};

	// Now, define the table's header and row objects
	//
	// A tabular layout is desired, so no layout manager is needed on the header 
	// or row.  CompositeTable will handle the layout automatically.  However,
	// if you supply a layout manager, CompositeTable will respect and use it.
	
	private static class Header extends Composite {
		public Header(Composite parent, int style) {
			super(parent, style);
            setLayout(new GridRowLayout(new int[] { 160, 100 }, false));
			new Label(this, SWT.NULL).setText("First Name");
			new Label(this, SWT.NULL).setText("Last Name");
		}
	}
	
	private static class Row extends Composite {
		public Row(Composite parent, int style) {
			super(parent, style);
            setLayout(new GridRowLayout(new int[] { 160, 100 }, false));
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
	    shell.setText("CompositeTable Snippet 2 -- Edit first/last name");
	    shell.setLayout(new FillLayout());

	    CompositeTable table = new CompositeTable(shell, SWT.NULL);
	    new Header(table, SWT.NULL); // Just drop the Header and Row in that order...
	    new Row(table, SWT.NULL);
	    table.setRunTime(true);
	    table.setNumRowsInCollection(swtCommitters.length);
	    
	    // Note the JFace-like virtual table API
	    table.addRowContentProvider(new IRowContentProvider() {
			public void refresh(CompositeTable sender, int currentObjectOffset, Control rowControl) {
				Row row = (Row) rowControl;
				row.firstName.setText(swtCommitters[currentObjectOffset].first);
				row.lastName.setText(swtCommitters[currentObjectOffset].last);
			}
	    });
	    
	    table.addRowFocusListener(new RowFocusAdapter() {
			public void depart(CompositeTable sender, int currentObjectOffset, Control rowControl) {
				Row row = (Row) rowControl;
				swtCommitters[currentObjectOffset].first = row.firstName.getText();
				swtCommitters[currentObjectOffset].last = row.lastName.getText();
			}
	    });
	    
	    shell.setSize(500, 150);
	    shell.open ();
	    while (!shell.isDisposed()) {
	        if (!display.readAndDispatch ()) display.sleep ();
	    }
	    display.dispose ();

	    // Print the results
	    for (int i = 0; i < swtCommitters.length; i++) {
			System.out.println(swtCommitters[i].first + " " + swtCommitters[i].last);
		}
	}
}
