package org.eclipse.swt.nebula.widgets.compositetable.perftest;
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.nebula.widgets.compositetable.AbsoluteLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class Header extends Composite {

	private Label label = null;
	private Label label1 = null;
	private Label label2 = null;
	private Label label3 = null;

	public Header(Composite parent, int style) {
		super(parent, style);
		initialize();
		this.setLayout( new AbsoluteLayout());
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        this.setSize(new Point(438, 54));
        label = new Label(this, SWT.NONE);
        label.setBounds(new Rectangle(6, 6, 53, 18));
        label.setText("Name");
        label1 = new Label(this, SWT.NONE);
        label1.setBounds(new Rectangle(6, 30, 79, 17));
        label1.setText("Address");
        label2 = new Label(this, SWT.NONE);
        label2.setBounds(new Rectangle(199, 9, 35, 17));
        label2.setText("City");
        label3 = new Label(this, SWT.NONE);
        label3.setBounds(new Rectangle(253, 7, 118, 17));
        label3.setText("State");
			
	}

}  //  @jve:decl-index=0:visual-constraint="11,16"
