/*
 * Copyright (C) 2005 David Orme <djo@coconut-palm-software.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Orme     - Initial API and implementation
 *     The Pampered Chef - Generalized
 */
package org.eclipse.swt.nebula.widgets.compositetable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;

/**
 * An abstract row class making it easy to implement a table where the whole
 * row is selected at once.
 *
 * @author djo
 */
public abstract class AbstractSelectableRow extends Composite implements
		TraverseListener, FocusListener, MouseListener, IRowFocusListener,
		IRowContentProvider, KeyListener {

	private Display display = Display.getCurrent();

	private Color LIST_BACKGROUND = display
			.getSystemColor(SWT.COLOR_LIST_BACKGROUND);

	private Color LIST_FOREGROUND = display
			.getSystemColor(SWT.COLOR_LIST_FOREGROUND);

	private Color LIST_SELECTION = display
			.getSystemColor(SWT.COLOR_LIST_SELECTION);

	private Color LIST_SELECTION_TEXT = display
			.getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT);

	private Color LIST_SELECTION_NOFOCUS = display
			.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);

	private Color LIST_SELECTION_TEXT_NOFOCUS = display
			.getSystemColor(SWT.COLOR_LIST_FOREGROUND);

	protected List labels;

	public AbstractSelectableRow(Composite parent, int style) {
		super(parent, style);
		addTraverseListener(this);
		addFocusListener(this);
		addMouseListener(this);
		addKeyListener(this);
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.labels = new ArrayList();
		for (int i = 0; i < getColumnCount(); i++) {
			Label label = new Label(this, SWT.NONE);
			this.labels.add(label);
			label.addMouseListener(this);
		}
	}

	public List getLabelsList() {
		return this.labels;
	}

	private void setRowColor(Color foreground, Color background) {
		setBackground(background);
		setForeground(foreground);
		Control[] children = getChildren();
		for (int i = 0; i < children.length; i++) {
			children[i].setBackground(background);
			children[i].setForeground(foreground);
		}
	}

	// Event handlers ----------------------------------------------------------

	public void keyTraversed(TraverseEvent e) {
		// NOOP: this just lets us receive focus from SWT
	}

	public void focusGained(FocusEvent e) {
		setRowColor(LIST_SELECTION_TEXT, LIST_SELECTION);
		selected = true;
		setSelection(model);
	}

	protected void setSelection(Object model) {
		// noop
	}

	private boolean selected = false;

	private boolean inactiveSelected = false;

	public void focusLost(FocusEvent e) {
		if (selected) {
			setRowColor(LIST_SELECTION_TEXT_NOFOCUS, LIST_SELECTION_NOFOCUS);
			inactiveSelected = true;
		}
	}

	public void depart(CompositeTable sender, int currentObjectOffset,
			Control row) {
		if(row == this && selected) {
			deselectRow();
		}
	}

	public void arrive(CompositeTable sender, int currentObjectOffset,
			Control newRow) {
		// NO OP
	}

	public void refresh(CompositeTable sender, int currentObjectOffset,
			Control row) {
		if (row == this && inactiveSelected) {
			deselectRow();
		}
	}

	private void deselectRow() {
		setRowColor(LIST_FOREGROUND, LIST_BACKGROUND);
		selected = false;
		inactiveSelected = false;
	}

	public boolean requestRowChange(CompositeTable sender,
			int currentObjectOffset, Control row) {
		// Always ok to change rows
		return true;
	}

	public void mouseDown(MouseEvent e) {
		setFocus();
	}

	/**
	 * Clients should override to implement their double-click behavior.
	 * 
	 * {@inheritDoc}
	 */
	public void mouseDoubleClick(MouseEvent e) {
		// noop
	};

	public void mouseUp(MouseEvent e) {
		// noop
	}

	private Object model = null;

	public void setModel(Object object) {
		this.model = object;
	}

	public Object getModel() {
		return model;
	}

	public void setMenu(Menu menu) {
		super.setMenu(menu);
		for (Iterator labelIter = labels.iterator(); labelIter.hasNext();) {
			Label label = (Label) labelIter.next();
			label.setMenu(menu);
		}
	}

	public void keyPressed(KeyEvent e) {
		// NOOP
	}

	public void keyReleased(KeyEvent e) {
		// Don't want to hard-code key bindings.  Clients override this method?
		
//	      if (e.character == SWT.DEL && e.stateMask == 0) {
//	          deleteSelectedObject();
//	       }
//	       if (e.character == SWT.CR && e.stateMask == 0) {
//	          runDoubleClickOpenAction();
//	       }
	}
	
	private int columnCount = -1;

	/**
	 * Method setColumnCount.  Sets the number of columns in the row.  This
	 * method must be called <b>exactly</b> once in the overridden constructor.
	 * 
	 * @param columnCount The number of columns in the row.
	 */
	public void setColumnCount(int columnCount) {
		if (this.columnCount > -1) {
			throw new IllegalArgumentException("Cannot setColumnCount more than once");
		}
		this.columnCount = columnCount;
		initialize();
		setRowColor(LIST_FOREGROUND, LIST_BACKGROUND);
	}
	
	private int getColumnCount() {
		return columnCount;
	}

}
