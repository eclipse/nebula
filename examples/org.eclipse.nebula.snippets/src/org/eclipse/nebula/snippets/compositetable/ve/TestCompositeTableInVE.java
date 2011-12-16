package org.eclipse.nebula.snippets.compositetable.ve;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.nebula.widgets.compositetable.CompositeTable;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TestCompositeTableInVE {

    private Shell sShell = null;  //  @jve:decl-index=0:visual-constraint="10,10"
    private CompositeTable compositeTable = null;
    private Header header = null;
    private Row row = null;
    /**
     * This method initializes compositeTable	
     *
     */
    private void createCompositeTable() {
        compositeTable = new CompositeTable(sShell, SWT.NONE);
        createHeader();
        createRow();
    }

    /**
     * This method initializes header	
     *
     */
    private void createHeader() {
        header = new Header(compositeTable, SWT.NONE);
    }

    /**
     * This method initializes row	
     *
     */
    private void createRow() {
        row = new Row(compositeTable, SWT.NONE);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        /* Before this is run, be sure to set up the launch configuration (Arguments->VM Arguments)
         * for the correct SWT library path in order to run with the SWT dlls. 
         * The dlls are located in the SWT plugin jar.  
         * For example, on Windows the Eclipse SWT 3.1 plugin jar is:
         *       installation_directory\plugins\org.eclipse.swt.win32_3.1.0.jar
         */
        Display display = Display.getDefault();
        TestCompositeTableInVE thisClass = new TestCompositeTableInVE();
        thisClass.createSShell();
        thisClass.sShell.open();
        while (!thisClass.sShell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }

    /**
     * This method initializes sShell
     */
    private void createSShell() {
        sShell = new Shell();
        sShell.setText("Shell");
        createCompositeTable();
        sShell.setSize(new Point(483, 279));
        sShell.setLayout(new FillLayout());
    }

}
