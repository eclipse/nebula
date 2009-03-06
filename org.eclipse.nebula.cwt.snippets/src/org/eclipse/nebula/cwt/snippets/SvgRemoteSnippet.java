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

package org.eclipse.nebula.cwt.snippets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.nebula.cwt.svg.SvgDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class SvgRemoteSnippet {

	private static SvgDocument svg = null;
	private static float scale = 1.0f;

	private static String fetch(String address) {
		try {
			URL url = new URL(address);
	        URLConnection conn = url.openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        String inputLine;
	        StringBuilder sb = new StringBuilder();
	        while ((inputLine = in.readLine()) != null) {
	        	sb.append(inputLine);
	        }
	        in.close();
	        return sb.toString();
		} catch(MalformedURLException e1) {
			e1.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("SvgSnippet01");
		shell.setLayout(new GridLayout(2, false));
		
		final Text txt = new Text(shell, SWT.BORDER);
		txt.setText("http://upload.wikimedia.org/wikipedia/commons/9/93/BenboisClock.svg");
		txt.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		final Button b = new Button(shell, SWT.PUSH);
		b.setText("Go");
		b.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		final Canvas canvas = new Canvas(shell, SWT.BORDER);
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1));
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				if(svg == null) {
					e.gc.drawText("Enter a valid URL to an SVG file,\n and press Go", 10, 20);
				} else {
					e.gc.setAntialias(SWT.ON);
	
					Transform transform = new Transform(e.gc.getDevice());
					transform.scale(scale, scale);
					e.gc.setTransform(transform);
	
					svg.apply(e.gc, canvas.getClientArea());
	
					transform.dispose();
				}
			}
		});
		canvas.addMouseWheelListener(new MouseWheelListener() {
			public void mouseScrolled(MouseEvent e) {
				if(e.count > 0) {
					scale *= 1.1f;
				} else {
					scale *= 0.9f;
				}
				canvas.redraw();
			}
		});

		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String data = fetch(txt.getText());
				if(data != null) {
					svg = SvgDocument.load(data);
				} else {
					svg = null;
				}
				canvas.redraw();
			}
		});

		Point size = new Point(300, 350);
		Rectangle screen = display.getMonitors()[0].getBounds();
		shell.setBounds((screen.width - size.x) / 2, (screen.height - size.y) / 2, size.x, size.y);
		shell.open();
		while(!shell.isDisposed()) {
			if(!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
