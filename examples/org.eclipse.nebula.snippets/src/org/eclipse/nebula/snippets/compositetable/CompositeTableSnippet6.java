package org.eclipse.nebula.snippets.compositetable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.nebula.widgets.compositetable.CompositeTable;
import org.eclipse.nebula.widgets.compositetable.IRowContentProvider;
import org.eclipse.nebula.widgets.compositetable.RowFocusAdapter;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A CompositeTable editing a name/address block
 * 
 * @author djo
 */
public class CompositeTableSnippet6 {
	// First some data to edit...
	
	private static class Address {
		public String name;
		public String address1;
		public String address2;
		public String city;
		public String state;
		public String postalCode;
		public Address(String name, String address1, String address2, String city,
				String state, String postalCode) {
			this.name = name;
			this.address1 = address1;
			this.address2 = address2;
			this.city = city;
			this.state = state;
			this.postalCode = postalCode;
		}
	}
	
	protected static Address[] addressList = new Address[] {
		new Address("John Wright", "810 Trail's End", "", "Glen Ellyn", "Illinois", "60017"),
		new Address("Anna Smith", "600 Milky Spore Rd", "", "Addison", "Illinois", "60010"),
		new Address("Andy Taylor", "1913 North Path", "", "Wheaton", "Illinois", "60187"),
		new Address("George Owen", "1624 Highwood", "", "Glenbrook", "Illinois", "60085"),
	};
	
	
	// Now define the row object.  It's just a regular Composite like you 
	// might use to edit an Address anywhere else.
	
	private static class AddressEditor extends Composite {
		
		public AddressEditor(Composite parent, int style) {
			super(parent, style | SWT.BORDER);
			
			setLayout(new GridLayout(2, true));
			new Label(this, SWT.NULL).setText("Name:");
			new Label(this, SWT.NULL);
			
			name = new Text(this, SWT.BORDER);
			name.setLayoutData(spanGD());
			
			new Label(this, SWT.NULL).setText("Address:");
			new Label(this, SWT.NULL);
			
			address1 = new Text(this, SWT.BORDER);
			address1.setLayoutData(spanGD());
			address2 = new Text(this, SWT.BORDER);
			address2.setLayoutData(spanGD());
			
			new Label(this, SWT.NULL).setText("City:");
			new Label(this, SWT.NULL).setText("State:");
			
			city = new Text(this, SWT.BORDER);
			city.setLayoutData(fillGD());
			state = new Combo(this, SWT.BORDER);
			state.setLayoutData(fillGD());
			
			new Label(this, SWT.NULL).setText("Zip:");
			new Label(this, SWT.NULL);
			
			postalCode = new Text(this, SWT.BORDER);
			postalCode.setLayoutData(spanGD());
		}
		
		private GridData spanGD() {
			GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
			gd.horizontalSpan=2;
			return gd;
		}
		
		private GridData fillGD() {
			GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
			return gd;
		}
		
		public final Text name;
		public final Text address1;
		public final Text address2;
		public final Text city;
		public final Combo state;
		public final Text postalCode;
	}
	
	// Where it all starts...
	
	public static void main (String [] args) {
	    Display display = new Display ();
	    Shell shell = new Shell (display);
	    shell.setText("CompositeTable Snippet 6 -- Edit name/address block");
	    shell.setLayout(new FillLayout());

	    CompositeTable table = new CompositeTable(shell, SWT.NULL);
	    AddressEditor row = new AddressEditor(table, SWT.NULL);  // No header?  No problem.
	    table.setRunTime(true);
	    table.setNumRowsInCollection(addressList.length);
	    
	    // Note the JFace-like virtual table API
	    table.addRowContentProvider(new IRowContentProvider() {
			public void refresh(CompositeTable sender, int currentObjectOffset, Control rowControl) {
				AddressEditor row = (AddressEditor) rowControl;
				row.name.setText(addressList[currentObjectOffset].name);
				row.address1.setText(addressList[currentObjectOffset].address1);
				row.address2.setText(addressList[currentObjectOffset].address2);
				row.city.setText(addressList[currentObjectOffset].city);
				row.state.setText(addressList[currentObjectOffset].state);
				row.postalCode.setText(addressList[currentObjectOffset].postalCode);
			}
	    });
	    
	    table.addRowFocusListener(new RowFocusAdapter() {
			public void depart(CompositeTable sender, int currentObjectOffset, Control rowControl) {
				AddressEditor row = (AddressEditor) rowControl;
				addressList[currentObjectOffset].name = row.name.getText();
				addressList[currentObjectOffset].address1 = row.address1.getText();
				addressList[currentObjectOffset].address2 = row.address2.getText();
				addressList[currentObjectOffset].city = row.city.getText();
				addressList[currentObjectOffset].state = row.state.getText();
				addressList[currentObjectOffset].postalCode = row.postalCode.getText();
			}
	    });

	    Point preferredSize = row.computeSize(SWT.DEFAULT, SWT.DEFAULT);
	    shell.setSize(500, 2*preferredSize.y+35);
	    shell.open ();
	    while (!shell.isDisposed()) {
	        if (!display.readAndDispatch ()) display.sleep ();
	    }
	    display.dispose ();
	}
}
