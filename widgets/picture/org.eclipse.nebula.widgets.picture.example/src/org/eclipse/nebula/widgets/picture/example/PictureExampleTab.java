/*******************************************************************************
 * Copyright (C) 2011 Angelo Zerr <angelo.zerr@gmail.com>, Pascal Leclercq <pascal.leclercq@gmail.com>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo ZERR - initial API and implementation
 *     Pascal Leclercq - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.picture.example;

import java.io.File;
import java.io.FileInputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.nebula.examples.AbstractExampleTab;
import org.eclipse.nebula.widgets.picture.PictureControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * Demonstrates the Nebula Picture Control
 * 
 * @author Angelo ZERR
 */
public class PictureExampleTab extends AbstractExampleTab {

	private static final String BUNDLE = "org.eclipse.nebula.widgets.picture.example";

	@Override
	public Control createControl(Composite parent) {

		Composite body = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		body.setLayout(layout);

		Label label = new Label(body, SWT.NONE);
		label.setText("Photo:");
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.TOP;
		label.setLayoutData(gridData);
		Image defaultImage = getDefaultPicture(body.getDisplay());

		// Create picture control
		PictureControl photoControl = createPictureControl(parent, defaultImage);
		photoControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return body;
	}

	/**
	 * Create Picture control and initialize it with default image.
	 * 
	 * @param parent
	 * @param defaultImage
	 * @return
	 */
	private PictureControl createPictureControl(Composite parent,
			Image defaultImage) {
		PictureControl photoControl = new PictureControl(parent);
		if (defaultImage != null) {
			// initialize it with default image
			photoControl.setDefaultImage(defaultImage);
		}
		return photoControl;
	}

	@Override
	public void createParameters(Composite parent) {

	}

	@Override
	public String[] createLinks() {
		String[] links = { "For mor information please read <a href=\"http://angelozerr.wordpress.com/2012/01/06/nebula_picture/\" >Picture Control Article</a>" };
		return links;
	}

	private Image getDefaultPicture(Device device) {
		try {
			final String path = FileLocator.getBundleFile(
					Platform.getBundle(BUNDLE)).getPath();
			final String EMPTY_PHOTO = path + "/EmptyPhoto.jpg";
			return new Image(device, new FileInputStream(new File(EMPTY_PHOTO)));
		} catch (Exception e) {
			// Should never thrown
			e.printStackTrace();
			return null;
		}
	}

}
