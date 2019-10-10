/*******************************************************************************
 * Copyright (c) 2019 Akuiteo (http://www.akuiteo.com).
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation
 * Frank DELPORTE - inspiration for his LED Number Display JavaFX widget
 * (https://webtechie.be/2019/10/02/led-number-display-javafx-library-published-to-maven)
 *******************************************************************************/
package org.eclipse.nebula.widgets.led;

import org.eclipse.swt.graphics.GC;

class LEDLine {
	private int[] coords;
	private boolean switchedOn;

	LEDLine(int[] coords) {
		super();
		this.coords = coords;
	}

	void setSwitechOnFlag(boolean newValue) {
		switchedOn = newValue;
	}

	public void paint(LED led) {
		GC gc = led.gc;
		gc.setBackground(switchedOn ? led.selectedColor : led.idleColor);
		gc.fillPolygon(coords);
	}
}
