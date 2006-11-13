package org.eclipse.swt.nebula.widgets.compositetable.test2;
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
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;

public class Row extends Composite {

//	public Label name = null;
//	public Label address = null;
//	public Label city = null;
//	public Label state = null;
	
	public Text name = null;
	public Text address = null;
	public Text city = null;
	public Text state = null;
	private Button button = null;
	public Row(Composite parent, int style) {
		super(parent, style);
		initialize();
//		this.setLayout( new org.eclipse.jface.examples.databinding.compositetable.AbsoluteLayout());
	}

	private void initialize() {
		
		this.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		this.setSize(new Point(645, 26));
   		
//		name = new Label(this, SWT.NONE);
//		name.setBounds(new org.eclipse.swt.graphics.Rectangle(12,12,10,25));
//		address = new Label(this, SWT.NONE);
//		address.setBounds(new org.eclipse.swt.graphics.Rectangle(43,12,10,25));
//		city = new Label(this, SWT.NONE);
//		city.setBounds(new org.eclipse.swt.graphics.Rectangle(75,14,10,25));
//		state = new Label(this, SWT.NONE);
//		state.setBounds(new org.eclipse.swt.graphics.Rectangle(109,16,10,25));

		name = new Text(this, SWT.BORDER);
		name.setBounds(new Rectangle(6, 2, 97, 21));
		address = new Text(this, SWT.BORDER);
		address.setBounds(new Rectangle(108, 2, 91, 21));
		city = new Text(this, SWT.BORDER);
		city.setBounds(new Rectangle(210, 2, 115, 21));
		state = new Text(this, SWT.BORDER);
		state.setBounds(new Rectangle(330, 2, 222, 21));
		button = new Button(this, SWT.NONE);
		button.setBounds(new Rectangle(555, 2, 83, 21));
		button.setText("Browse");
	}

}  //  @jve:decl-index=0:visual-constraint="19,28"
