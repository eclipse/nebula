package org.eclipse.nebula.snippets.compositetable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.nebula.widgets.compositetable.AbstractSelectableRow;
import org.eclipse.nebula.widgets.compositetable.CompositeTable;
import org.eclipse.nebula.widgets.compositetable.GridRowLayout;
import org.eclipse.nebula.widgets.compositetable.IRowContentProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * A CompositeTable listing first/last name pairs, emulating SWT.FULL_SELECTION
 * on the row objects.
 * 
 * @author djo
 */
public class CompositeTableSnippet4 {
	// First some data...
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

	// Define our header and row objects.  For convenience, we use
	// abstract classes provided along with CompositeTable that make it
	// easy to add common behaviors to our header and row objects.
	
	private static class Header extends Composite {
		public Header(Composite parent, int style) {
			super(parent, style);
            setLayout(new GridRowLayout(new int[] { 160, 100 }, false));
			new Label(this, SWT.NULL).setText("First Name");
			new Label(this, SWT.NULL).setText("Last Name");
		}
	}
	
	private static class Row extends AbstractSelectableRow {
		public Row(Composite parent, int style) {
			super(parent, style);
			setLayout(new GridRowLayout(new int[] { 160, 100 }, false));
			super.setColumnCount(2);
		}
	}
	
	// Where it all starts...
	
	public static void main (String [] args) {
	    Display display = new Display ();
	    Shell shell = new Shell (display);
	    shell.setText("CompositeTable Snippet 4 -- List first/last names");
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
				Control[] children = row.getChildren();
				((Label)children[0]).setText(swtCommitters[currentObjectOffset].first);
				((Label)children[1]).setText(swtCommitters[currentObjectOffset].last);
			}
	    });
	    
	    shell.setSize(500, 150);
	    shell.open ();
	    while (!shell.isDisposed()) {
	        if (!display.readAndDispatch ()) display.sleep ();
	    }
	    display.dispose ();
	}
}
