/*******************************************************************************
 * Copyright (c) 2006-2007 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Nicolas Richeton (nicolas.richeton@gmail.com) - initial implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.collapsiblebuttons.example;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.nebula.examples.AbstractExampleTab;
import org.eclipse.nebula.widgets.collapsiblebuttons.CollapsibleButtons;
import org.eclipse.nebula.widgets.collapsiblebuttons.IColorManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

/**
 * Demonstrates the CollapsibleButtons widget.
 *
 * @author Nicolas Richeton (nicolas.richeton@gmail.com)
 */
public class CollapsibleButtonsExampleTab extends AbstractExampleTab {

	Image womanImage = null;
	Image bgImage = null;
	Image eclipseImage = null;

	CollapsibleButtons collapsibleButtons = null;

	Image itemImage24 = null;
	Image itemImage16 = null;

	@Override
	public Control createControl(Composite parent) {

		itemImage24 = new Image(PlatformUI.getWorkbench().getDisplay(), PlatformUI.getWorkbench().getDisplay().getSystemImage(SWT.ICON_WORKING).getImageData().scaledTo(24, 24));
		itemImage16 = new Image(PlatformUI.getWorkbench().getDisplay(), PlatformUI.getWorkbench().getDisplay().getSystemImage(SWT.ICON_WORKING).getImageData().scaledTo(16, 16));

		parent.setLayout(new FillLayout());

		Composite inner = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(1, true);
		gl.marginBottom = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		inner.setLayout(gl);

		collapsibleButtons = new CollapsibleButtons(inner, SWT.NONE, IColorManager.SKIN_OFFICE_2007);
		collapsibleButtons.setLayoutData(
				new GridData(GridData.GRAB_VERTICAL | GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_END));

		for (int i = 0; i < 5; i++) {
			collapsibleButtons.addButton("Button " + i, "Tooltip " + i, itemImage24, itemImage16);
		}

		return collapsibleButtons;
	}

	@Override
	public String[] createLinks() {
		String[] links = new String[4];

		links[0] = "<a href=\"http://www.eclipse.org/nebula/widgets/collapsiblebuttons/collapsiblebuttons.php\">CollapsibleButtons Home Page</a>";

		links[1] = "<a href=\"http://www.eclipse.org/nebula/snippets.php#CollapsibleButtons\">Snippets</a>";

		links[2] = "<a href=\"https://bugs.eclipse.org/bugs/buglist.cgi?query_format=advanced&short_desc_type=allwordssubstr&short_desc=&classification=Technology&product=Nebula&component=CollapsibleButtons&long_desc_type=allwordssubstr&long_desc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&status_whiteboard_type=allwordssubstr&status_whiteboard=&keywords_type=allwords&keywords=&emailtype1=substring&email1=&emailtype2=substring&email2=&bugidtype=include&bug_id=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&cmdtype=doit&order=Reuse+same+sort+as+last+time&field0-0-0=noop&type0-0-0=noop&value0-0-0=\">Bugs</a>";

		links[3] = "<a href=\"http://www.eclipse.org/projects/project-plan.php?projectid=technology.nebula\">Projet plan</a>";

		return links;
	}

	@Override
	public void createParameters(Composite parent) {
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(3).applyTo(parent);
	}

}
