/*****************************************************************************
 *  Copyright (c) 2015, 2019 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *		Dirk Fauth <dirk.fauth@googlemail.com> - Initial API and implementation
 *      Laurent Caron <laurent.caron@gmail.com> - Bug 511353
 *
 *****************************************************************************/
package org.eclipse.nebula.widgets.richtext.painter;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class AnchorElement {
	public String url;
	public Point startingPoint;
	public Rectangle area;
}
