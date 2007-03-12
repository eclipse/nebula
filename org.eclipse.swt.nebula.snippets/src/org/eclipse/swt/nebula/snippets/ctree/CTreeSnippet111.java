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
package org.eclipse.swt.nebula.snippets.ctree;

/*
 * CTreeEditor example snippet: edit the text of a tree item (in place, fancy)
 * A cut-and-paste of the SWT snippet - Snippet111 - with all instances "Tree"
 *  replaced with "CTree"
 * 
 * For a list of all Nebula CTableTree example snippets see
 * http://www.eclipse.org/nebula/widgets/ctabletree/snippets.php
 */
import org.eclipse.nebula.widgets.ctree.CTree;
import org.eclipse.nebula.widgets.ctree.CTreeEditor;
import org.eclipse.nebula.widgets.ctree.CTreeItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CTreeSnippet111 {

public static void main (String [] args) {
	final Display display = new Display ();
	final Color black = display.getSystemColor (SWT.COLOR_BLACK);
	Shell shell = new Shell (display);
	shell.setLayout (new FillLayout ());
	final CTree tree = new CTree (shell, SWT.BORDER);
	for (int i=0; i<16; i++) {
		CTreeItem itemI = new CTreeItem (tree, SWT.NONE);
		itemI.setText ("Item " + i);
		for (int j=0; j<16; j++) {
			CTreeItem itemJ = new CTreeItem (itemI, SWT.NONE);
			itemJ.setText ("Item " + j);
		}
	}
	final CTreeItem [] lastItem = new CTreeItem [1];
	final CTreeEditor editor = new CTreeEditor (tree);
	tree.addListener (SWT.Selection, new Listener () {
		public void handleEvent (Event event) {
			final CTreeItem item = (CTreeItem) event.item;
			if (item != null && item == lastItem [0]) {
				boolean isCarbon = SWT.getPlatform ().equals ("carbon");
				final Composite composite = new Composite (tree, SWT.NONE);
				if (!isCarbon) composite.setBackground (black);
				final Text text = new Text (composite, SWT.NONE);
				final int inset = isCarbon ? 0 : 1;
				composite.addListener (SWT.Resize, new Listener () {
					public void handleEvent (Event e) {
						Rectangle rect = composite.getClientArea ();
						text.setBounds (rect.x + inset, rect.y + inset, rect.width - inset * 2, rect.height - inset * 2);
					}
				});
				Listener textListener = new Listener () {
					public void handleEvent (final Event e) {
						switch (e.type) {
							case SWT.FocusOut:
								item.setText (text.getText ());
								composite.dispose ();
								break;
							case SWT.Verify:
								String newText = text.getText ();
								String leftText = newText.substring (0, e.start);
								String rightText = newText.substring (e.end, newText.length ());
								GC gc = new GC (text);
								Point size = gc.textExtent (leftText + e.text + rightText);
								gc.dispose ();
								size = text.computeSize (size.x, SWT.DEFAULT);
								editor.horizontalAlignment = SWT.LEFT;
								Rectangle itemRect = item.getBounds (), rect = tree.getClientArea ();
								editor.minimumWidth = Math.max (size.x, itemRect.width) + inset * 2;
								int left = itemRect.x, right = rect.x + rect.width;
								editor.minimumWidth = Math.min (editor.minimumWidth, right - left);
								editor.minimumHeight = size.y + inset * 2;
								editor.layout ();
								break;
							case SWT.Traverse:
								switch (e.detail) {
									case SWT.TRAVERSE_RETURN:
										item.setText (text.getText ());
										//FALL THROUGH
									case SWT.TRAVERSE_ESCAPE:
										composite.dispose ();
										e.doit = false;
								}
								break;
						}
					}
				};
				text.addListener (SWT.FocusOut, textListener);
				text.addListener (SWT.Traverse, textListener);
				text.addListener (SWT.Verify, textListener);
				editor.setEditor (composite, item);
				text.setText (item.getText ());
				text.selectAll ();
				text.setFocus ();
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
