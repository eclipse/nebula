/*******************************************************************************
 * Copyright (c) 2008 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 248956)
 *******************************************************************************/
package org.eclipse.nebula.jface.viewer.radiogroup;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.nebula.widgets.radiogroup.RadioGroup;
import org.eclipse.nebula.widgets.radiogroup.RadioItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Widget;

/**
 * A concrete viewer based on an SWT <code>Composite</code> control. This class
 * is intended as an alternative to the JFace <code>ComboViewer</code>, which
 * displays its content as radio buttons in a composite rather than in a combo
 * box. Wherever possible, this class attempts to behave like ComboViewer.
 * <p>
 * 
 * This class is designed to be instantiated with a pre-existing SWT composite
 * control and configured with a domain-specific content provider, label
 * provider, element filter (optional), and element sorter (optional).
 * 
 * @since 3.5
 */
public class RadioGroupViewer extends StructuredViewer {
	private RadioGroup group;

	/**
	 * Creates a radio group viewer on the given composite control. The viewer
	 * has no input, no content provider, a default label provider, no sorter,
	 * and no filters.
	 * 
	 * @param composite
	 *            the composite control which is the immediate parent of all
	 *            radio buttons
	 */
	public RadioGroupViewer(RadioGroup group) {
		this.group = group;
		hookControl(group);
	}

	protected void inputChanged(Object input, Object oldInput) {
		getControl().setRedraw(false);
		try {
			preservingSelection(new Runnable() {
				public void run() {
					internalRefresh(getRoot());
				}
			});
		} finally {
			getControl().setRedraw(true);
		}
	}

	public void setLabelProvider(IBaseLabelProvider labelProvider) {
		Assert.isTrue(labelProvider instanceof ILabelProvider);
		super.setLabelProvider(labelProvider);
	}

	public Control getControl() {
		return group;
	}

	protected Widget doFindInputItem(Object element) {
		if (equals(element, getRoot())) {
			return group;
		}
		return null;
	}

	protected Widget doFindItem(Object element) {
		RadioItem[] children = group.getItems();
		for (int i = 0; i < children.length; i++) {
			Item item = children[i];
			Object data = item.getData();
			if (data != null && equals(data, element)) {
				return item;
			}
		}

		return null;
	}

	protected void doUpdateItem(Widget widget, Object element, boolean fullMap) {
		if (widget instanceof RadioItem) {
			final RadioItem item = (RadioItem) widget;

			// remember element we are showing
			if (fullMap) {
				associate(element, item);
			} else {
				Object data = item.getData();
				if (data != null) {
					unmapElement(data, item);
				}
				item.setData(element);
				mapElement(element, item);
			}

			ILabelProvider labelProvider = (ILabelProvider) getLabelProvider();

			String text = labelProvider.getText(element);
			item.setText(text == null ? "" : text);

			Image image = labelProvider.getImage(element);
			item.setImage(image);

			if (labelProvider instanceof IColorProvider) {
				IColorProvider colorProvider = (IColorProvider) labelProvider;

				Color foreground = colorProvider.getForeground(element);
				if (foreground != null)
					item.setForeground(foreground);

				Color background = colorProvider.getBackground(element);
				if (background != null)
					item.setBackground(background);
			}

			if (labelProvider instanceof IFontProvider) {
				IFontProvider fontProvider = (IFontProvider) labelProvider;

				Font font = fontProvider.getFont(element);
				if (font != null)
					item.setFont(font);
			}
		}
	}

	protected List getSelectionFromWidget() {
		RadioItem selection = group.getSelection();
		return selection == null ? Collections.EMPTY_LIST : Collections
				.singletonList(selection.getData());
	}

	protected void internalRefresh(Object element) {
		if (element == null || equals(element, getRoot())) {
			internalRefreshAll();
		} else {
			Widget w = findItem(element);
			if (w != null) {
				updateItem(w, element);
			}
		}
	}

	private void internalRefreshAll() {
		Object[] children = getSortedChildren(getRoot());
		RadioItem[] items = group.getItems();
		int min = Math.min(children.length, items.length);

		// disassociate changed elements first
		for (int i = 0; i < min; i++) {
			RadioItem item = items[i];

			if (!equals(children[i], item.getData())) {
				disassociate(item);
				group.clear(i);
			}
		}

		// dispose of all items beyond the end of the current elements
		if (min < items.length) {
			for (int i = items.length; --i >= min;) {
				disassociate(items[i]);
			}
			group.remove(min, items.length - 1);
		}

		// associate and update
		for (int i = 0; i < min; ++i) {
			updateItem(items[i], children[i]);
		}

		// create and associate new elements
		for (int i = min; i < children.length; i++) {
			updateItem(new RadioItem(group, SWT.NONE, i), children[i]);
		}
	}

	public void reveal(Object element) {
		Assert.isNotNull(element);
		RadioItem item = (RadioItem) findItem(element);
		group.reveal(item);
	}

	protected void setSelectionToWidget(List l, boolean reveal) {
		if (l.isEmpty()) {
			group.deselectAll();
			return;
		}

		Object element = l.get(0);
		RadioItem item = (RadioItem) findItem(element);
		if (item != null)
			group.setSelection(item);
	}
}
