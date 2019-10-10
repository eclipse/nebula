/*******************************************************************************
 * Copyright (c) 2008, 2012 Stepan Rutz.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Stepan Rutz - initial implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.geomap.internal.geomapbrowser;

import org.eclipse.nebula.widgets.geomap.GeoMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

/**
 * Base implementation of an abstract page, eg something that owns an swt
 * control. Common functions are a header widget and an embedded
 * {@link PageContainer}.
 *
 * <p>
 * This file is part of the showcase for the {@link GeoMap} but does not include
 * any core functionality that is typically embedded. Users who only want to
 * embed the swt-map as a widget don't typically use this class.
 * </p>
 *
 * @author stepan.rutz@gmx.de
 * @version $Revision$
 */
public abstract class AbstractPage implements Page {

	private PageContainer container;
	private Composite composite;

	protected PageContainer getContainer() {
		return container;
	}

	protected Composite getComposite() {
		return composite;
	}

	@Override
	public Control getControl(PageContainer container, Composite parent) {
		if (composite == null) {
			this.container = container;
			composite = new Composite(parent, SWT.NONE);
			composite.addDisposeListener(
					e -> AbstractPage.this.widgetDisposed(e));

			composite.setLayout(new GridLayout(2, false));
			container.adapt(composite);

			initContent(container, composite);
		}
		return composite;
	}

	protected void addHeaderRow(PageContainer container, Composite parent,
			String text) {
		HeaderControl header = new HeaderControl(parent);
		container.adapt(header);
		header.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING,
				true, false, 2, 1));
		header.setText(text);
	}

	protected Link addInfoText(PageContainer container, Composite parent,
			String text) {
		Link link = new Link(parent, SWT.WRAP | SWT.MULTI);
		container.adapt(link);
		GridData layoutData = new GridData(GridData.FILL, GridData.BEGINNING,
				true, false, 2, 1);
		link.setLayoutData(layoutData);
		link.setText(text);
		link.addListener(SWT.Selection, event -> {
			try {
				if (event.text != null && event.text.length() > 0) {
					Program.launch(event.text);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		return link;
	}

	protected void addActionLink(PageContainer container, Composite parent,
			String text, SelectionAdapter selectionAdapter) {
		Composite wrap = new Composite(parent, SWT.NONE);
		wrap.setLayoutData(
				new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		container.adapt(wrap);

		GridLayout layout = new GridLayout(2, false);
		layout.marginLeft = 8;
		layout.marginHeight = 0;
		wrap.setLayout(layout);

		Label titleImage = new Label(wrap, SWT.WRAP);
		container.adapt(titleImage);
		Link link = new Link(wrap, SWT.NONE);
		container.adapt(link);
		link.setText(text);
		link.addSelectionListener(selectionAdapter);
	}

	protected abstract void widgetDisposed(DisposeEvent e);

	protected abstract void initContent(PageContainer container,
			Composite composite);
}
