/****************************************************************************
* Copyright (c) 2007 Peter Centgraf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     IBM Corporation - JFace's ComboBoxCellEditor was relied upon _heavily_ for example and reference
*     Peter Centgraf <peter@centgraf.net> - initial API and implementation
*****************************************************************************/

package org.eclipse.nebula.widgets.cdatetime;

import java.text.MessageFormat;
import java.util.Date;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * CellEditor implementation using a CDateTime for GUI and a Date for the value.
 * 
 * @author Peter Centgraf <peter@centgraf.net>
 * @since Jul 13, 2007
 */
public class CDateTimeCellEditor extends CellEditor {

    /**
     * Default ComboBoxCellEditor style
     */
    private static final int defaultStyle = SWT.NONE;
    
    /**
     * The CDateTime control GUI; initially <code>null</code>.
     */
    protected CDateTime dateTime;
    
    /**
     * Internal listener updates this class when the CDateTime value changes.
     */
    private SelectionListener selectionListener;

	/**
	 * Default constructor -- configures a cell editor with default style.
	 */
	public CDateTimeCellEditor() {
		setStyle(defaultStyle);
	}

	/**
	 * Constructor -- configures a cell editor with default style and creates the
	 * CDateTime GUI with the given parent.
	 * 
	 * @param parent the SWT parent Composite for the CDateTime
	 */
	public CDateTimeCellEditor(Composite parent) {
		super(parent);
	}

	/**
	 * Constructor -- configures a cell editor with the given style and creates the
	 * CDateTime GUI with the given parent.
	 * 
	 * @param parent the SWT parent Composite for the CDateTime
	 * @param style the SWT style bits for the CDateTime
	 */
	public CDateTimeCellEditor(Composite parent, int style) {
		super(parent, style);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.CellEditor#createControl(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createControl(Composite parent) {
		dateTime = new CDateTime(parent, getStyle());

		// Basic visual properties
        dateTime.setFont(parent.getFont());
        dateTime.setBackground(parent.getBackground());
        dateTime.setForeground(parent.getForeground());
        
        // Listeners
        dateTime.addSelectionListener(getSelectionListener());

        dateTime.addKeyListener(new KeyAdapter() {
            // hook key pressed - see PR 14201  
            public void keyPressed(KeyEvent e) {
                keyReleaseOccured(e);
            }
        });

        dateTime.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_ESCAPE
                        || e.detail == SWT.TRAVERSE_RETURN) {
                    e.doit = false;
                }
            }
        });

        dateTime.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                CDateTimeCellEditor.this.focusLost();
            }
        });
        
        return dateTime;
	}
	
    /**
     * This implementation is the same as the one for {@link ComboBoxCellEditor}.
     */
    public LayoutData getLayoutData() {
        LayoutData layoutData = super.getLayoutData();
        if ((dateTime == null) || dateTime.isDisposed()) {
			layoutData.minimumWidth = 60;
		} else {
            // make the comboBox 10 characters wide
            GC gc = new GC(dateTime);
            layoutData.minimumWidth = (gc.getFontMetrics()
                    .getAverageCharWidth() * 10) + 10;
            gc.dispose();
        }
        return layoutData;
    }

    /**
     * Return the internal selection listener.
     */
    private SelectionListener getSelectionListener() {
        if (selectionListener == null) {
        	selectionListener = new SelectionListener() {
                /**
                 * Listen to changes to update validation state.
                 */
        		public void widgetSelected(SelectionEvent e) {
        			editOccured(e);
        		}
        		/**
        		 * Listen for value commit to apply value and deactivate.
        		 */
        		public void widgetDefaultSelected(SelectionEvent e) {
        			fireApplyEditorValue();
        			deactivate();
        		}
            };
        }
        return selectionListener;
    }

    /**
     * Processes a modify event that occurred in this text cell editor.
     * This framework method performs validation and sets the error message
     * accordingly, and then reports a change via <code>fireEditorValueChanged</code>.
     * Subclasses should call this method at appropriate times. Subclasses
     * may extend or reimplement.
     *
     * @param e the SWT modify event
     */
    protected void editOccured(SelectionEvent e) {
        Date value = dateTime.getSelection();
        boolean oldValidState = isValueValid();
        boolean newValidState = isCorrect(value);
        if (!newValidState) {
            // try to insert the current value into the error message.
            setErrorMessage(MessageFormat.format(getErrorMessage(),
                    new Object[] { value }));
        }
        valueChanged(oldValidState, newValidState);
    }

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.CellEditor#doGetValue()
	 */
	protected Object doGetValue() {
		Assert.isNotNull(dateTime);
		return dateTime.getSelection();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.CellEditor#doSetFocus()
	 */
	protected void doSetFocus() {
		Assert.isNotNull(dateTime);
		dateTime.setFocus();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.CellEditor#doSetValue(java.lang.Object)
	 */
	protected void doSetValue(Object value) {
		Assert.isNotNull(dateTime);
        Assert.isTrue(value == null || (value instanceof Date));
        dateTime.setSelection((Date) value);
	}
}
