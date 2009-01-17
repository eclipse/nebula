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

package org.eclipse.nebula.cwt.v;

import java.lang.reflect.Constructor;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;

public class VNative<T extends Control> extends VControl {

	public static <T extends Control> VNative<T> create(Class<T> type, VPanel parent, int style) {
		try {
			Constructor<T> constructor = type.getConstructor(Composite.class, int.class);
			T control = constructor.newInstance(parent.getComposite(), style);
			VNative<T> vn = new VNative<T>(parent, style);
			vn.setControl(control);
			return vn;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private T control;
	private DisposeListener disposeListener;
	
	
	private VNative(VPanel panel, int style) {
		super(panel, style);
		disposeListener = new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				dispose();
			}
		};
	}

	@Override
	public void addListener(int eventType, Listener listener) {
		control.addListener(eventType, listener);
	}
	
	@Override
	void attachListeners(boolean key, boolean set) {
		// skip
	}
	
	private boolean checkControl() {
		return control != null && !control.isDisposed();
	}
	
	@Override
	public Point computeSize(int hint, int hint2, boolean changed) {
		return control.computeSize(hint, hint2, changed);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		if(control != null && !control.isDisposed()) {
			control.removeDisposeListener(disposeListener);
			control.dispose();
		}
	}
	
	@Override
	public Rectangle getClientArea() {
		if(control instanceof Composite) {
			return ((Composite) control).getClientArea();
		} else {
			return super.getClientArea();
		}
	}
	
	public T getControl() {
		return (T) control;
	}
	
	@Override
	public String getText() {
		try {
			return (String) control.getClass().getMethod("getText").invoke(control);
		} catch (Exception e) {
			e.printStackTrace();
			return super.getText();
		}
	}

	@Override
	public Type getType() {
		return Type.Native;
	}
	
	@Override
	public void removeListener(int eventType, Listener listener) {
		control.removeListener(eventType, listener);
	}

	@Override
	public void setBackground(Color color) {
		super.setBackground(color);
		if(checkControl()) {
			control.setBackground(color);
		}
	}
	
	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		control.setBounds(x, y, width, height);
	}

	private void setControl(T control) {
		this.control = control;
		control.addDisposeListener(disposeListener);
	}

	@Override
	public boolean setFocus() {
		return control.setFocus();
	}
	
	@Override
	public void setFont(Font font) {
		control.setFont(font);
	}
	
	@Override
	public void setForeground(Color color) {
		super.setForeground(color);
		if(checkControl()) {
			control.setForeground(color);
		}
	}

	@Override
	public void setText(String text) {
		try {
			control.getClass().getMethod("setText", String.class).invoke(control, text);
		} catch (Exception e) {
			e.printStackTrace();
			super.setText(text);
		}
	}
	
	@Override
	public void setToolTipText(String text) {
		control.setToolTipText(text);
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		control.setVisible(visible);
	}
	
}
