/*******************************************************************************
 * Copyright (C) 2011 Angelo Zerr <angelo.zerr@gmail.com>, Pascal Leclercq <pascal.leclercq@gmail.com>
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Angelo ZERR - initial API and implementation
 *     Pascal Leclercq - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.picture.forms;

import org.eclipse.nebula.widgets.picture.AbstractPictureControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * {@link AbstractPictureControl} implementation with Forms {@link Hyperlink}.
 */
public class FormPictureControl extends AbstractPictureControl<Hyperlink> {

	private final FormToolkit toolkit;

	/**
	 * Constructor for {@link FormPictureControl} with default SWT styles.
	 * 
	 * @param parent
	 *            a composite control which will be the parent of the new
	 *            instance (cannot be null)
	 */
	public FormPictureControl(Composite parent, FormToolkit toolkit) {
		this(parent, toolkit, SWT.NONE, SWT.BORDER | SWT.CENTER, SWT.NONE);
	}

	/**
	 * Constructor for {@link FormPictureControl} with given SWT style .
	 * 
	 * @param parent
	 *            a composite control which will be the parent of the new
	 *            instance (cannot be null)
	 * 
	 * @param compositeStyle
	 *            SWT style of the SWT Composite which host Label+Link controls.
	 * @param labelStyle
	 *            SWT style of the Label control.
	 * @param linkStyle
	 *            SWT style of the Link control.
	 */

	public FormPictureControl(Composite parent, FormToolkit toolkit,
			int compositeStyle, int labelStyle, int linkStyle) {
		super(parent, compositeStyle, labelStyle, linkStyle, false);
		this.toolkit = toolkit;
		this.toolkit.adapt(this);
		super.createUI(labelStyle, linkStyle);
	}

	@Override
	protected Label createLabel(Composite parent, int style) {
		return toolkit.createLabel(parent, "", style);
	}

	@Override
	protected Hyperlink createLink(Composite parent, int style) {
		return toolkit.createHyperlink(parent, "", style);
	}

	@Override
	protected Composite createComposite(Composite parent, int style) {
		return toolkit.createComposite(parent, style);
	}

	@Override
	protected void setLinkText(Hyperlink link, String text) {
		link.setText(text);
	}

	@Override
	protected void addModifyImageHandler(Hyperlink modifyImageLink) {
		modifyImageLink.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				handleModifyImage();
			}
		});
	}

	@Override
	protected void addDeleteImageHandler(Hyperlink deleteImageLink) {
		deleteImageLink.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				handleDeleteImage();
			}
		});
	}
}
