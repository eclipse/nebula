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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;

public class VLabel extends VControl {

	public VLabel(VPanel panel, int style) {
		super(panel, style | SWT.NO_FOCUS);
		if(hasStyle(SWT.SEPARATOR)) {
			setMargins(0, 0);
		}
		setPainter(new VLabelPainter());
	}
	
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		if(hasStyle(SWT.SEPARATOR)) {
			return new Point(2,2);
		} else {
			return super.computeSize(wHint, hHint, changed);
		}
	}
	
	@Override
	public Type getType() {
		return VControl.Type.Label;
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(
				x,
				y,
				(hasStyle(SWT.SEPARATOR) && !hasStyle(SWT.HORIZONTAL)) ? 2 : width,
				(hasStyle(SWT.SEPARATOR) && hasStyle(SWT.HORIZONTAL)) ? 2 : height
			);
	}

}
