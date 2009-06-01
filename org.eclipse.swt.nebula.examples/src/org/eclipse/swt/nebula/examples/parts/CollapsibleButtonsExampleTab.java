/*******************************************************************************
 * Copyright (c) 2006-2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Nicolas Richeton (nicolas.richeton@gmail.com) - initial implementation
 *******************************************************************************/
package org.eclipse.swt.nebula.examples.parts;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.nebula.animation.ScrollingSmoother;
import org.eclipse.nebula.animation.movement.BounceOut;
import org.eclipse.nebula.animation.movement.ElasticOut;
import org.eclipse.nebula.animation.movement.ExpoOut;
import org.eclipse.nebula.animation.movement.IMovement;
import org.eclipse.nebula.animation.movement.LinearInOut;
import org.eclipse.nebula.widgets.collapsiblebuttons.CollapsibleButtons;
import org.eclipse.nebula.widgets.collapsiblebuttons.IColorManager;
import org.eclipse.nebula.widgets.gallery.AbstractGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.AbstractGridGroupRenderer;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryGroupRenderer;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.nebula.widgets.gallery.ListItemRenderer;
import org.eclipse.nebula.widgets.gallery.NoGroupRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.nebula.examples.AbstractExampleTab;
import org.eclipse.swt.nebula.examples.ExamplesView;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;

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
	
	public Control createControl(Composite parent) {
		
		if( itemImage24 == null)
		 itemImage24 = new Image(parent.getDisplay(), Program
				.findProgram("jpg").getImageData().scaledTo(24, 24)); //$NON-NLS-1$
		
		if( itemImage16 == null)
			itemImage16 = new Image(parent.getDisplay(), Program
				.findProgram("jpg").getImageData().scaledTo(16, 16)); //$NON-NLS-1$

		parent.setLayout(new FillLayout());

		Composite inner = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(1, true);
		gl.marginBottom = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		inner.setLayout(gl);

		

		 collapsibleButtons = new CollapsibleButtons(inner,
				SWT.NONE, IColorManager.SKIN_OFFICE_2007);
		collapsibleButtons.setLayoutData(new GridData(GridData.GRAB_VERTICAL
				| GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_END));

		for (int i = 0; i < 5; i++) {
			collapsibleButtons.addButton("Button "+i, "Tooltip "+i, itemImage24,
					itemImage16);
		}

		return collapsibleButtons;
	}

	

	public String[] createLinks() {
		String[] links = new String[4];

		links[0] = "<a href=\"http://www.eclipse.org/nebula/widgets/collapsiblebuttons/collapsiblebuttons.php\">CollapsibleButtons Home Page</a>";

		links[1] = "<a href=\"http://www.eclipse.org/nebula/snippets.php#CollapsibleButtons\">Snippets</a>";

		links[2] = "<a href=\"https://bugs.eclipse.org/bugs/buglist.cgi?query_format=advanced&short_desc_type=allwordssubstr&short_desc=&classification=Technology&product=Nebula&component=CollapsibleButtons&long_desc_type=allwordssubstr&long_desc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&status_whiteboard_type=allwordssubstr&status_whiteboard=&keywords_type=allwords&keywords=&emailtype1=substring&email1=&emailtype2=substring&email2=&bugidtype=include&bug_id=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&cmdtype=doit&order=Reuse+same+sort+as+last+time&field0-0-0=noop&type0-0-0=noop&value0-0-0=\">Bugs</a>";

		links[3] = "<a href=\"http://www.eclipse.org/projects/project-plan.php?projectid=technology.nebula\">Projet plan</a>";

		return links;
	}

	

	
	public void createParameters(Composite parent) {
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(3).applyTo(
				parent);
	


	}

	
}
