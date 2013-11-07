/****************************************************************************
 * Copyright (c) 2008, 2009 Jeremy Dowdall
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/
package org.eclipse.nebula.cwt.svg;

import org.eclipse.nebula.cwt.test.AbstractVTestCase;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class SvgRenderTests extends AbstractVTestCase {

	private Composite imgComposite;
	private Composite svgComposite;
	private Label svgLbl;
	private Image img;
	private SvgDocument svg;
	private Boolean success;
	
	protected void runTest(String file) {
		svg = SvgDocument.load(SvgRenderTests.class.getResourceAsStream("resources/"+file+".svg"));
		try {
			img = new Image(getDisplay(), SvgRenderTests.class.getResourceAsStream("resources/"+file+".png"));
		} catch (IllegalArgumentException e) {
			img = null;
		}
		syncExec(new Runnable() {
			public void run() {
				if(svg.getTitle() != null) {
					svgLbl.setText("SVG: \"" + svg.getTitle() + "\"");
				} else {
					svgLbl.setText("SVG");
				}
				svgComposite.setToolTipText(svg.getDescription());
			}
		});
		redraw(imgComposite);
		redraw(svgComposite);
		layoutShell();
		while(success == null) {
			try {
				Thread.sleep(100);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		assertTrue(success);
	}
	
	@Override
	protected void setUp() throws Exception {
		success = null;
		setDefaultShellSize(new Point(400, 600));
		
		Shell shell = getShell();
		shell.setLayout(new GridLayout());
		
		Label lbl = new Label(shell, SWT.NONE);
		lbl.setText("Bitmap");
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		imgComposite = new Composite(shell, SWT.BORDER);
		imgComposite.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		imgComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		imgComposite.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				if(img == null) {
					e.gc.drawString("Original is not available", 10, 10);
				} else {
					Rectangle ob = imgComposite.getClientArea();
					Rectangle ib = img.getBounds();
					float ar = ib.width / ib.height;
					int w = (int) (ob.width);
					int h = (int) (w / ar);
					if(h > ob.height) {
						h = ob.height;
						w = (int) (h * ar);
					}
					int x = (ob.width-w)/2;
					int y = (ob.height-h)/2;
					e.gc.drawImage(img, 0, 0, ib.width, ib.height, x, y, w, h);
				}
			}
		});

		svgLbl = new Label(shell, SWT.NONE);
		svgLbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		svgComposite = new Composite(shell, SWT.BORDER | SWT.DOUBLE_BUFFERED);
		svgComposite.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		svgComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		svgComposite.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				if(svg == null) {
					e.gc.drawText("loading...", 10, 10);
				} else {
					Rectangle bounds = svgComposite.getClientArea();
					svg.apply(e.gc, bounds);
				}
			}
		});
		
		Composite controls = new Composite(shell, SWT.BORDER);
		controls.setLayout(new GridLayout(2, true));
		controls.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false));
		
		Button b = new Button(controls, SWT.PUSH);
		b.setText("Yes");
		b.setLayoutData(new GridData());
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				success = true;
			}
		});
		
		b = new Button(controls, SWT.PUSH);
		b.setText("No");
		b.setLayoutData(new GridData());
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				success = false;
			}
		});
	}
	
	public void testLine() {
		runTest("line01");
	}
	
	public void testRectangle() {
		runTest("rect01");
	}
	
	public void testRoundedRectangle() {
		runTest("rect02");
	}

	public void testCircle() {
		runTest("circle01");
	}
	
	public void testEllipse() {
		runTest("ellipse01");
	}
	
	public void testPolygon() {
		runTest("polygon01");
	}
	
	public void testPolyline() {
		runTest("polyline01");
	}

	public void testPathTriangle() {
		runTest("triangle01");
	}

	public void testPathCubic() {
		runTest("cubic02");
	}
	
	public void testPathQuad() {
		runTest("quad01");
	}
	
	public void testPathArc1() {
		runTest("arcs01");
	}

	public void testPathArc2() {
		runTest("arcs02");
	}
	
	public void testLinearGradient1() {
		runTest("lingrad01");
	}
	
	public void testLinearGradient2() {
		runTest("lingrad02");
	}
	
	public void testRadialGradient() {
		runTest("radgrad01");
	}
	
}
