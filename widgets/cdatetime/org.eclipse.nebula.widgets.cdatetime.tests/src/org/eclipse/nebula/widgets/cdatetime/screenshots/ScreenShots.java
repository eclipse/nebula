/****************************************************************************
 * Copyright (c) 2008 Jeremy Dowdall
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/

package org.eclipse.nebula.widgets.cdatetime.screenshots;

import java.io.File;
import java.util.Date;

import org.eclipse.nebula.cwt.test.AbstractVTestCase;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.layout.GridLayout;

public class ScreenShots extends AbstractVTestCase {

	private CDateTime cdt;
	private int[] style = new int[] { CDT.BORDER | CDT.TIME_MEDIUM | CDT.DATE_LONG, CDT.BORDER | CDT.DATE_LONG, CDT.BORDER | CDT.TIME_MEDIUM };
	private int[] style_ext = new int[] { CDT.NONE, CDT.SPINNER, CDT.DROP_DOWN, CDT.SIMPLE, CDT.COMPACT | CDT.SIMPLE };

	@Override
	protected void setUp() throws Exception {
		setCapturePath(System.getProperty("user.home") + File.separator + "CDateTime" + File.separator + "ScreenShots");
		getShell().setLayout(new GridLayout());
		super.setUp();
	}

	public void setUp11() {
		cdt = new CDateTime(getShell(), style[0] | style_ext[0]);
	}

	public void setUp12() {
		cdt = new CDateTime(getShell(), style[0] | style_ext[1]);
	}

	public void setUp13() {
		cdt = new CDateTime(getShell(), style[0] | style_ext[2]);
	}

	public void setUp14() {
		cdt = new CDateTime(getShell(), style[0] | style_ext[3]);
	}

	private void doScreenShots() {
		capture(cdt, "null");
		syncExec(new Runnable() {
			public void run() {
				cdt.setSelection(new Date());
				getShell().pack();
			}
		});
		pause(500);
		capture(cdt, "set");

	}

	public void testScreenShot_11() {
		doScreenShots();
	}

	public void testScreenShot_12() {
		doScreenShots();
	}

	public void testScreenShot_13() {
		doScreenShots();
	}

	public void testScreenShot_14() {
		doScreenShots();
	}

}
