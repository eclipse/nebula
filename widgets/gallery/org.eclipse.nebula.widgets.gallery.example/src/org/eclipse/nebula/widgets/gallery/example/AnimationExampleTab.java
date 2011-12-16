/*******************************************************************************
 * Copyright (c) 2006-2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Nicolas Richeton (nicolas.richeton@gmail.com) - initial implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.gallery.example;

import org.eclipse.nebula.animation.AnimationRunner;
import org.eclipse.nebula.animation.effects.AlphaEffect;
import org.eclipse.nebula.animation.movement.ExpoOut;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.nebula.examples.AbstractExampleTab;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Demonstrates the Nebula animation package
 * 
 * @author Nicolas Richeton (nicolas.richeton@gmail.com)
 */
public class AnimationExampleTab extends AbstractExampleTab {

	public String[] createLinks() {
		String[] links = new String[1];
		links[0] = "<a href=\"http://www.eclipse.org/nebula/widgets/gallery/gallery.php\">Gallery Home Page (includes Animation)</a>";
		return links;
	}

	public Control createControl(Composite parent) {
		Composite c = new Composite(parent, SWT.None);
		c.setLayout(new RowLayout());
		Button b = new Button(c, SWT.None);
		b.setText("Fade");
		b.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				Shell s = new Shell(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell(), SWT.CLOSE
						| SWT.TITLE);
				s.setAlpha(0);
				s.open();
				AnimationRunner runner = new AnimationRunner();
				runner.runEffect(new AlphaEffect(s, 0, 255, 1000,
						new ExpoOut(), null, null));
				AlphaEffect.fadeOnClose(s, 1000, new ExpoOut(), runner);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		return c;
	}

	public void createParameters(Composite parent) {
		parent.setLayout(new RowLayout());
	}

}
