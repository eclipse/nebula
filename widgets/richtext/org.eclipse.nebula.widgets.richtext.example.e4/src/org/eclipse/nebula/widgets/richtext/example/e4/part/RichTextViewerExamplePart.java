/*****************************************************************************
 * Copyright (c) 2015 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *		Dirk Fauth <dirk.fauth@googlemail.com> - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.nebula.widgets.richtext.example.e4.part;

import javax.annotation.PostConstruct;

import org.eclipse.nebula.widgets.richtext.example.RichTextViewerExample;
import org.eclipse.swt.widgets.Composite;

public class RichTextViewerExamplePart {

	@PostConstruct
	public void postConstruct(Composite parent) {
		RichTextViewerExample example = new RichTextViewerExample();
		example.createControls(parent);
	}

}