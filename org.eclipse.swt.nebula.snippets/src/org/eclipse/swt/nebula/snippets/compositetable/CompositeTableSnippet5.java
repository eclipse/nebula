package org.eclipse.swt.nebula.snippets.compositetable;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.nebula.widgets.compositetable.AbstractSelectableRow;
import org.eclipse.swt.nebula.widgets.compositetable.AbstractSortableHeader;
import org.eclipse.swt.nebula.widgets.compositetable.CompositeTable;
import org.eclipse.swt.nebula.widgets.compositetable.IRowContentProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * A CompositeTable listing first/last name pairs, allowing sorting by 
 * clicking columns.
 * 
 * @author djo
 */
public class CompositeTableSnippet5 {
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
	
	static final Comparator firstNameAscending = new Comparator() {
		public int compare(Object o1, Object o2) {
			Name name1 = (Name) o1;
			Name name2 = (Name) o2;
			return name1.first.compareToIgnoreCase(name2.first);
		};
	};

	static final Comparator firstNameDescending = new Comparator() {
		public int compare(Object o1, Object o2) {
			Name name1 = (Name) o1;
			Name name2 = (Name) o2;
			return -1 * name1.first.compareToIgnoreCase(name2.first);
		};
	};

	static final Comparator lastNameAscending = new Comparator() {
		public int compare(Object o1, Object o2) {
			Name name1 = (Name) o1;
			Name name2 = (Name) o2;
			return name1.last.compareToIgnoreCase(name2.last);
		};
	};

	static final Comparator lastNameDescending = new Comparator() {
		public int compare(Object o1, Object o2) {
			Name name1 = (Name) o1;
			Name name2 = (Name) o2;
			return -1 * name1.last.compareToIgnoreCase(name2.last);
		};
	};

	// Define our header and row objects.  For convenience, we use
	// abstract classes provided along with CompositeTable that make it
	// easy to add common behaviors to our header and row objects.

	private static class Header extends AbstractSortableHeader {
		public Header(Composite parent, int style) {
			super(parent, style);
			setLabelStrings(new String[] {"First name", "Last name"});
		}
		
		protected void sortOnColumn(int column, boolean sortDescending) {
			Comparator comparator = null;
			if (column == 0) {
				if (sortDescending) {
					comparator = firstNameDescending;
				} else {
					comparator = firstNameAscending;
				}
			} else {
				if (sortDescending) {
					comparator = lastNameDescending;
				} else {
					comparator = lastNameAscending;
				}
			}
			Arrays.sort(swtCommitters, comparator);
			table.refreshAllRows();
		}
	}
	
	private static class Row extends AbstractSelectableRow {
		public Row(Composite parent, int style) {
			super(parent, style);
			setColumnCount(2);
		}
	}
	
	private static CompositeTable table;


	// Where it all starts...
	
	public static void main (String [] args) {
	    Display display = new Display ();
	    Shell shell = new Shell (display);
	    shell.setText("CompositeTable Snippet 4 -- List first/last names with sortable columns");
	    shell.setLayout(new FillLayout());

	    table = new CompositeTable(shell, SWT.NULL);
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
