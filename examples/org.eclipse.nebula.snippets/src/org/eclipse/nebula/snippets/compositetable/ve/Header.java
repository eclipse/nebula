package org.eclipse.nebula.snippets.compositetable.ve;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.nebula.widgets.compositetable.GridRowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class Header extends Composite {

    private Label label = null;
    private Label label1 = null;
    private Label label2 = null;

    public Header(Composite parent, int style) {
        super(parent, style);
        initialize();
        setLayout(new GridRowLayout());
    }

    private void initialize() {
        label = new Label(this, SWT.NONE);
        label.setText("Name");
        label.setBounds(new Rectangle(5, 5, 27, 13));
        label1 = new Label(this, SWT.NONE);
        label1.setText("Department");
        label1.setBounds(new Rectangle(37, 5, 57, 13));
        label2 = new Label(this, SWT.NONE);
        label2.setText("Job");
        label2.setBounds(new Rectangle(99, 5, 17, 13));
        this.setLayout(null);
        this.setSize(new Point(178, 33));
    }

}  //  @jve:decl-index=0:visual-constraint="10,10"
