/****************************************************************************
 * Copyright (c) 2008, 2009 Jeremy Dowdall
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/

package org.eclipse.nebula.cwt.v;

import org.eclipse.swt.widgets.Event;

public interface IControlPainter {

	public abstract void dispose();
	
	public abstract void paintBackground(VControl control, Event e);
	
	public abstract void paintBorders(VControl control, Event e);
	
	public abstract void paintContent(VControl control, Event e);
	
}
