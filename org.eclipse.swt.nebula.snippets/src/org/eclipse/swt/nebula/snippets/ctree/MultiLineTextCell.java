/****************************************************************************
 * Copyright (c) 2006 Jeremy Dowdall
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/
package org.eclipse.swt.nebula.snippets.ctree;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.nebula.widgets.cdatetime.CButton;
import org.eclipse.swt.nebula.widgets.ctree.AbstractItem;
import org.eclipse.swt.nebula.widgets.ctree.CTreeCell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * A Sample custom cell of style TITLE.
 * The CContainerCell's toggle is set on and off dynamically, and contains a
 * native SWT Text box in its Title Area.
 * <p>Note that the style SWT.TOP is also set so that the toggle will be drawn
 * at the top of the cell when it is expanded (compare to MultiLineCell which
 * does not have this style set).</p>
 */
public class MultiLineTextCell extends CTreeCell {

	private Text text;
	private Button b;
	
	public MultiLineTextCell(AbstractItem item, int style) {
		super(item, style | SWT.TOGGLE);
		marginHeight = 1;
		marginWidth = 1;
	}

	protected Control createControl(Composite parent) {
//		text = new Text(parent, SWT.BORDER | SWT.WRAP | SWT.MULTI);
//		text.setText("This is a text box with multiple lines This is a text box with multiple lines");
//		setExclusions(text);
//		return text;
//		b = new Button(parent, SWT.ARROW | SWT.DOWN);
//		b.setSquare(true);
//		setControlLayoutData(SWT.RIGHT, SWT.CENTER);
//		return b;
		return null;
	}
	
}
