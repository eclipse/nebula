/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.snippets.cdatetime;

/*
 * CDateTime example snippet: edit the text of a tree item (in place, fancy)
 * A cut-and-paste of the SWT snippet - Snippet111 - with the text editor
 *  replaced with a CDateTime widget
 * 
 * For a list of all Nebula CDateTime example snippets see
 * http://www.eclipse.org/nebula/widgets/cdatetime/cdatetime.php?page=snippets
 */
import java.util.Date;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class CDTSnippet111 {

public static void main (String [] args) {
	final Display display = new Display ();
	Shell shell = new Shell (display);
	shell.setLayout (new FillLayout ());
	final Tree tree = new Tree (shell, SWT.BORDER);
	for (int i=0; i<16; i++) {
		TreeItem itemI = new TreeItem (tree, SWT.NONE);
		itemI.setData("date", new Date());
		itemI.setText ("Item " + i);
		for (int j=0; j<16; j++) {
			TreeItem itemJ = new TreeItem (itemI, SWT.NONE);
			itemJ.setData("date", new Date());
			itemJ.setText ("Item " + j);
		}
	}
	final TreeItem [] lastItem = new TreeItem [1];
	final TreeEditor editor = new TreeEditor (tree);
	editor.grabHorizontal = true;
	tree.addListener (SWT.Selection, new Listener () {
		public void handleEvent (Event event) {
			final TreeItem item = (TreeItem) event.item;
			if (item != null && item == lastItem [0]) {
				boolean isCarbon = SWT.getPlatform ().equals ("carbon");
				final Composite composite = new Composite (tree, SWT.NONE);
				final CDateTime cdt = new CDateTime (composite, CDT.DROP_DOWN);
				final int inset = isCarbon ? 0 : 1;
				editor.layout();
				composite.addListener (SWT.Resize, new Listener () {
					public void handleEvent (Event e) {
						Rectangle rect = composite.getClientArea ();
						cdt.setBounds (rect.x + inset, rect.y + inset, rect.width - inset * 2, rect.height - inset * 2);
					}
				});
				Listener listener = new Listener () {
					public void handleEvent (final Event e) {
						switch (e.type) {
							case SWT.FocusOut:
								composite.dispose();
								break;
							case SWT.DefaultSelection:
								item.setData("date", cdt.getSelection());
								item.setText (cdt.getText ());
								composite.dispose ();
								break;
							case SWT.Traverse:
								switch (e.detail) {
									case SWT.TRAVERSE_RETURN:
										item.setData("date", cdt.getSelection());
										item.setText (cdt.getText ());
										//FALL THROUGH
									case SWT.TRAVERSE_ESCAPE:
										composite.dispose ();
										e.doit = false;
								}
								break;
						}
					}
				};
				cdt.addListener(SWT.FocusOut, listener);
				cdt.addListener(SWT.Traverse, listener);
				cdt.addListener(SWT.DefaultSelection, listener);
				editor.setEditor(composite, item);
				cdt.setSelection((Date) item.getData("date"));
				cdt.setFocus();
			}
			lastItem [0] = item;
		}
	});
	shell.pack ();
	shell.open ();
	while (!shell.isDisposed()) {
		if (!display.readAndDispatch ()) display.sleep ();
	}
	display.dispose ();
}
}
