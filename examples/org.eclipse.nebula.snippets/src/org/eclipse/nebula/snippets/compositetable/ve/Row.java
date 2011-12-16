package org.eclipse.nebula.snippets.compositetable.ve;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.nebula.widgets.compositetable.GridRowLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.graphics.Rectangle;

public class Row extends Composite {

    private Text text = null;
    private Text text1 = null;
    private Text text2 = null;

    public Row(Composite parent, int style) {
        super(parent, style);
        initialize();
        setLayout(new GridRowLayout());
    }

    private void initialize() {
        text = new Text(this, SWT.NONE);
        text.setBounds(new Rectangle(5, 5, 70, 13));
        text1 = new Text(this, SWT.NONE);
        text1.setBounds(new Rectangle(80, 5, 70, 13));
        text2 = new Text(this, SWT.NONE);
        text2.setBounds(new Rectangle(155, 5, 70, 13));
        this.setLayout(null);
        this.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        setSize(new Point(307, 46));
    }

}  //  @jve:decl-index=0:visual-constraint="10,10"
