package org.eclipse.nebula.snippets.compositetable;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.nebula.widgets.compositetable.AbstractNativeHeader;
import org.eclipse.nebula.widgets.compositetable.AbstractSelectableRow;
import org.eclipse.nebula.widgets.compositetable.CompositeTable;
import org.eclipse.nebula.widgets.compositetable.IRowContentProvider;
import org.eclipse.nebula.widgets.compositetable.ResizableGridRowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * A CompositeTable listing first/last name pairs, utilizing a native header
 * control, allowing sorting by clicking columns and allowing columns to be 
 * resized by dragging in the header.
 * 
 * @author djo
 */
public class CompositeTableSnippet5 {
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
	
	static class Opposite implements Comparator {
		private Comparator c;
		public Opposite(Comparator c) {
			this.c = c;
		}
		public int compare(Object o1, Object o2) {
			return -1 * c.compare(o1, o2);
		}
	}
	
	static final Comparator firstNameAscending = new Comparator() {
		public int compare(Object o1, Object o2) {
			Name name1 = (Name) o1;
			Name name2 = (Name) o2;
			return name1.first.compareToIgnoreCase(name2.first);
		};
	};
	static final Comparator firstNameDescending = new Opposite(firstNameAscending);

	static final Comparator lastNameAscending = new Comparator() {
		public int compare(Object o1, Object o2) {
			Name name1 = (Name) o1;
			Name name2 = (Name) o2;
			return name1.last.compareToIgnoreCase(name2.last);
		};
	};
	static final Comparator lastNameDescending = new Opposite(lastNameAscending);

    /*
 	 * Define our header and row objects.  For convenience, we use
	 * abstract classes provided along with CompositeTable that make it
	 * easy to add common behaviors to our header and row objects.
     * 
     * Notice that since AbstractNativeHeader is a native header control, it
     * doesn't make sense to use a layout manager, but it has methods similar 
     * to the GridRowLayout layout manager.
     */

	private static class Header extends AbstractNativeHeader {
		public Header(Composite parent, int style) {
			super(parent, style);
            setWeights(new int[] { 160, 100 });
			setColumnText(new String[] {"First name", "Last name"});
		}
		
		protected boolean sortOnColumn(int column, int sortDirection) {
			Comparator comparator = null;
            
			if (column == 0) {
				if (sortDirection == SWT.DOWN) {
					comparator = firstNameDescending;
				} else {
					comparator = firstNameAscending;
				}
			} else {
				if (sortDirection == SWT.DOWN) {
					comparator = lastNameDescending;
				} else {
					comparator = lastNameAscending;
				}
			}
            
			Arrays.sort(swtCommitters, comparator);
			table.refreshAllRows();
            
            return true;
		}
	}
	
    /*
     * So that the row's columns size with the header, we use a
     * ResizableGridRowLayout here.  The value of weights and fittingHorizontally
     * is gotten from the header, so we don't need to set anything else here.
     */
	private static class Row extends AbstractSelectableRow {
		public Row(Composite parent, int style) {
			super(parent, style);
            setLayout(new ResizableGridRowLayout());
			setColumnCount(2);
		}
	}
	
	private static CompositeTable table;


	// Where it all starts...
	
	public static void main (String [] args) {
	    Display display = new Display ();
	    Shell shell = new Shell (display);
	    shell.setText("CompositeTable Snippet 5 -- List first/last names with sortable, movable, resizable columns and a native header");
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
