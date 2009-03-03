package org.eclipse.nebula.jface.cdatetime;

import java.util.Date;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class CDateTimeCellEditor extends CellEditor {

	/**
	 * The CDateTime control; initial selection is the time of instantiation
	 */
	private CDateTime cdt;

    /**
     * Creates a new date/time cell editor parented under the given control.
     *
     * @param parent the parent control
     */
    public CDateTimeCellEditor(Composite parent) {
        this(parent, CDT.DROP_DOWN);
    }

    /**
     * Creates a new date/time cell editor parented under the given control.
     *
     * @param parent the parent control
     * @param style the style bits
     */
    public CDateTimeCellEditor(Composite parent, int style) {
        super(parent, style);
    }

    /**
     * Creates a new date/time cell editor parented under the given control.
     *
     * @param parent the parent control
     * @param style the style bits
     */
    public CDateTimeCellEditor(Composite parent, String pattern) {
        super(parent, CDT.DROP_DOWN);
        cdt.setPattern(pattern);
    }

    protected Control createControl(Composite parent) {
		cdt = new CDateTime(parent, CDT.DROP_DOWN | getStyle());
		cdt.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
		        fireApplyEditorValue();
		        deactivate();
			}
		});
        cdt.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if(e.detail == SWT.TRAVERSE_ESCAPE) {
                    deactivate();
                }
            }
        });
		cdt.setFont(parent.getFont());
		cdt.setBackground(parent.getBackground());
		return cdt;
	}

	protected Object doGetValue() {
		return cdt.getSelection();
	}

	protected void doSetFocus() {
		cdt.setFocus();
	}

	protected void doSetValue(Object value) {
        cdt.setSelection((Date) value);
	}

}
