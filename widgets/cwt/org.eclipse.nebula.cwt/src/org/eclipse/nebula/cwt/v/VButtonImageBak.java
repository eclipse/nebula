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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

class VButtonImageBak {

	private class ImageListener implements Listener {
		private String key;
		private Button b;

		ImageListener(String key, Button b) {
			this.key = key;
			this.b = b;
		}

		public void handleEvent(Event e) {
			GC gc = new GC(b);
			Image image = new Image(b.getDisplay(), e.width, e.height);
			gc.copyArea(image, 0, 0);
			ImageData data = image.getImageData();
			gc.dispose();
			image.dispose();
			images.put(key, data);
			keys.put(data, key);
			if(requests.containsKey(key)) {
				for(Iterator<VButton> iter = requests.get(key).iterator(); iter.hasNext();) {
					iter.next().redraw();
					iter.remove();
				}
				requests.remove(key);
			}
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if(!b.isDisposed() && b == b.getDisplay().getFocusControl()) {
						b.getParent().forceFocus();
					}
					b.dispose();
				}
			});
		}
	}

	private static VButtonImageBak instance;

	private static String getKey(VButton button) {
		StringBuilder sb = new StringBuilder();
		sb.append(button.getState());
		sb.append(button == VTracker.getFocusControl());
		sb.append(":"); //$NON-NLS-1$
		sb.append(button.bounds.width);
		sb.append(":"); //$NON-NLS-1$
		sb.append(button.bounds.height);
		return sb.toString();
	}

	public static VButtonImageBak instance() {
		if(instance == null) {
			instance = new VButtonImageBak();
		}
		return instance;
	}

	/**
	 * Maps a unique key to a single image data object. Key => {state} : {width} :
	 * {height}
	 */
	private Map<String, ImageData> images = new HashMap<String, ImageData>();

	/**
	 * Map an image data object back to its unique key.
	 */
	private Map<ImageData, String> keys = new HashMap<ImageData, String>();

	/**
	 * Maps a unique key to a list of buttons who have requested an image data
	 * object for that key.
	 */
	private Map<String, List<VButton>> requests = new HashMap<String, List<VButton>>();

	/**
	 * Maps an image data object to the set of buttons that use that image data
	 * object.
	 */
	private Map<ImageData, Set<VButton>> buttons = new HashMap<ImageData, Set<VButton>>();

	private VButtonImageBak() {
		// 
	}

	private void createImage(VButton button) {
		String key = getKey(button);
		if(requests.containsKey(key)) {
			requests.get(key).add(button);
		} else {
			requests.put(key, new ArrayList<VButton>());
			requests.get(key).add(button);

			int style = button.getStyle() & (SWT.CHECK | SWT.RADIO);
			if(style == 0) {
				style = SWT.TOGGLE; // defaults to, and converts PUSH buttons to, TOGGLE
			}

			Button b = new Button(button.composite, style);
			b.setBackground(button.getBackground());
			b.setBounds(button.getBounds());
			if(button.hasState(VControl.STATE_SELECTED)) {
				b.setSelection(true);
			}
			if(button == VTracker.getFocusControl()) {
				 b.setFocus();
			}
			b.addListener(SWT.Paint, new ImageListener(key, b));

			b.redraw();
			b.update();
		}
	}

	public ImageData getImageData(VButton button) {
		if(!button.bounds.isEmpty()) {
			String key = getKey(button);
			ImageData data = images.get(key);
			if(data == null) {
				createImage(button);
			} else {
				if(partialImage(key, data)) {
					images.remove(key);
					keys.remove(data);
				} else {
					if(button.oldImageData != null && button.oldImageData != data) {
						removeUnusedData();
					}
					if(!buttons.containsKey(data)) {
						buttons.put(data, new HashSet<VButton>());
					}
					if(!buttons.get(data).contains(button)) {
						buttons.get(data).add(button);
					}
				}
				return data;
			}
		}
		return null;
	}

	private boolean partialImage(String key, ImageData data) {
		String[] sa = key.split(":"); //$NON-NLS-1$
		int w = Integer.parseInt(sa[1]);
		int h = Integer.parseInt(sa[2]);
		return (data.height != h) || (data.width != w);
	}

	void removeUnusedData() {
		for(Iterator<ImageData> i1 = buttons.keySet().iterator(); i1.hasNext();) {
			ImageData data = i1.next();
			for(Iterator<VButton> i2 = buttons.get(data).iterator(); i2.hasNext();) {
				Point size = i2.next().getSize();
				if(size.x != data.width || size.y != data.height) {
					i2.remove();
				}
			}
			if(buttons.get(data).isEmpty()) {
				images.remove(keys.get(data));
				keys.remove(data);
				i1.remove();
			}
		}
	}

}
