/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/ 

package org.eclipse.nebula.widgets.collapsiblebuttons;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

public interface IButtonPainter {

	public void paintBackground(GC gc, IColorManager colorManager, ISettings settings, Rectangle bounds, boolean hover, boolean selected);
	public void paintText(GC gc, IColorManager colorManager, ISettings settings, Rectangle bounds, Rectangle imageBounds, boolean hover, boolean selected, String text);
	public void paintImage(GC gc, IColorManager colorManager, ISettings settings, Rectangle bounds, boolean hover, boolean selected, Image image);
	
}
