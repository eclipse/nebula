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

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.nebula.widgets.radiogroup.RadioGroup;
import org.eclipse.nebula.widgets.radiogroup.RadioItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * A concrete viewer based on a Nebula {@link RadioGroup} control.
 * <p>
 * <b>PROVISIONAL</b>: The superclass hierarchy of this viewer is provisional
 * and expected to change. Specifically we plan to change this class to extend a
 * new ItemViewer class in the near future. Therefore clients should avoid using
 * API declared only in AbstractListViewer (however APIs declared in
 * StructuredViewer and its superclasses are safe to use). Unsafe methods are
 * tagged "noreference" in the javadoc.
 * 
 * @since 3.5
 */
public class RadioGroupViewer extends AbstractListViewer {
	private RadioGroup radioGroup;

	/**
	 * Creates a radio group viewer on a newly-created {@link RadioGroup}
	 * control under the given parent. The viewer has no input, no content
	 * provider, a default label provider, no sorter, and no filters.
	 * 
	 * @param parent
	 *            the parent control
	 */
	public RadioGroupViewer(Composite parent) {
		this(parent, SWT.NONE);
	}

	/**
	 * Creates a radio group viewer on a newly-created {@link RadioGroup}
	 * control under the given parent. The radio group control is created using
	 * the given style bits. The viewer has no input, no content provider, a
	 * default label provider, no sorter, and no filters.
	 * 
	 * @param parent
	 *            the parent control
	 * @param style
	 *            SWT style bits
	 */
	public RadioGroupViewer(Composite parent, int style) {
		this(new RadioGroup(parent, style));
	}

	/**
	 * Creates a radio group viewer on the given {@link RadioGroup} control. The
	 * viewer has no input, no content provider, a default label provider, no
	 * sorter, and no filters.
	 * 
	 * @param group
	 *            the RadioGroup control
	 */
	public RadioGroupViewer(RadioGroup group) {
		Assert.isNotNull(group);
		this.radioGroup = group;
		hookControl(group);
	}

	public Control getControl() {
		return radioGroup;
	}

	/**
	 * Returns this viewer's {@link RadioGroup} control.
	 * 
	 * @return the RadioGroup control
	 */
	public RadioGroup getRadioGroup() {
		return radioGroup;
	}

	/**
	 * @noreference Methods declared by AbstractListViewer should not be used by
	 *              clients. This class will be modified to extend ItemViewer at
	 *              some point in the future which will break references to
	 *              AbstractListViewer API.
	 */
	public void reveal(Object element) {
		// Do nothing -- element visibility is determined by layout
		// TODO - walk up parent hierarchy until we find a scrollable?
	}

	/**
	 * @noreference Methods declared by AbstractListViewer should not be used by
	 *              clients. This class will be modified to extend ItemViewer at
	 *              some point in the future which will break references to
	 *              AbstractListViewer API.
	 */
	protected void listShowSelection() {
		// Do nothing -- selection visibility is determined by layout
		// TODO - walk up parent hierarchy until we find a scrollable?
	}

	/**
	 * @noreference Methods declared by AbstractListViewer should not be used by
	 *              clients. This class will be modified to extend ItemViewer at
	 *              some point in the future which will break references to
	 *              AbstractListViewer API.
	 */
	protected void listSetSelection(int[] ixs) {
		for (int idx = 0; idx < ixs.length; idx++) {
			radioGroup.select(ixs[idx]);
		}
	}

	/**
	 * @noreference Methods declared by AbstractListViewer should not be used by
	 *              clients. This class will be modified to extend ItemViewer at
	 *              some point in the future which will break references to
	 *              AbstractListViewer API.
	 */
	protected int[] listGetSelectionIndices() {
		return new int[] { radioGroup.getSelectionIndex() };
	}

	/**
	 * @noreference Methods declared by AbstractListViewer should not be used by
	 *              clients. This class will be modified to extend ItemViewer at
	 *              some point in the future which will break references to
	 *              AbstractListViewer API.
	 */
	protected void listAdd(String string, int index) {
		radioGroup.setLayoutDeferred(true);
		try {
			RadioItem item = new RadioItem(radioGroup, SWT.NONE, index);
			item.setText(string);
		} finally {
			radioGroup.setLayoutDeferred(false);
		}
	}

	/**
	 * @noreference Methods declared by AbstractListViewer should not be used by
	 *              clients. This class will be modified to extend ItemViewer at
	 *              some point in the future which will break references to
	 *              AbstractListViewer API.
	 */
	protected void listDeselectAll() {
		radioGroup.deselectAll();
	}

	/**
	 * @noreference Methods declared by AbstractListViewer should not be used by
	 *              clients. This class will be modified to extend ItemViewer at
	 *              some point in the future which will break references to
	 *              AbstractListViewer API.
	 */
	protected int listGetItemCount() {
		return radioGroup.getItemCount();
	}

	/**
	 * @noreference Methods declared by AbstractListViewer should not be used by
	 *              clients. This class will be modified to extend ItemViewer at
	 *              some point in the future which will break references to
	 *              AbstractListViewer API.
	 */
	protected void listRemove(int index) {
		radioGroup.remove(index);
	}

	/**
	 * @noreference Methods declared by AbstractListViewer should not be used by
	 *              clients. This class will be modified to extend ItemViewer at
	 *              some point in the future which will break references to
	 *              AbstractListViewer API.
	 */
	protected void listRemoveAll() {
		radioGroup.removeAll();
	}

	/**
	 * @noreference Methods declared by AbstractListViewer should not be used by
	 *              clients. This class will be modified to extend ItemViewer at
	 *              some point in the future which will break references to
	 *              AbstractListViewer API.
	 */
	protected void listSetItem(int index, String string) {
		RadioItem item = radioGroup.getItems()[index];
		item.setText(string);
	}

	/**
	 * @noreference Methods declared by AbstractListViewer should not be used by
	 *              clients. This class will be modified to extend ItemViewer at
	 *              some point in the future which will break references to
	 *              AbstractListViewer API.
	 */
	protected void listSetItems(String[] labels) {
		radioGroup.removeAll();

		for (int i = 0; i < labels.length; i++) {
			RadioItem item = new RadioItem(radioGroup, SWT.NONE);
			item.setText(labels[i]);
		}
	}

	/**
	 * @noreference Methods declared by AbstractListViewer should not be used by
	 *              clients. This class will be modified to extend ItemViewer at
	 *              some point in the future which will break references to
	 *              AbstractListViewer API.
	 */
	public void add(Object element) {
		super.add(element);
	}

	/**
	 * @noreference Methods declared by AbstractListViewer should not be used by
	 *              clients. This class will be modified to extend ItemViewer at
	 *              some point in the future which will break references to
	 *              AbstractListViewer API.
	 */
	public void add(Object[] elements) {
		super.add(elements);
	}

	/**
	 * @noreference Methods declared by AbstractListViewer should not be used by
	 *              clients. This class will be modified to extend ItemViewer at
	 *              some point in the future which will break references to
	 *              AbstractListViewer API.
	 */
	public void insert(Object element, int position) {
		super.insert(element, position);
	}

	/**
	 * @noreference Methods declared by AbstractListViewer should not be used by
	 *              clients. This class will be modified to extend ItemViewer at
	 *              some point in the future which will break references to
	 *              AbstractListViewer API.
	 */
	public Object getElementAt(int index) {
		return super.getElementAt(index);
	}

	/**
	 * @noreference Methods declared by AbstractListViewer should not be used by
	 *              clients. This class will be modified to extend ItemViewer at
	 *              some point in the future which will break references to
	 *              AbstractListViewer API.
	 */
	protected int indexForElement(Object element) {
		return super.indexForElement(element);
	}

	/**
	 * @noreference Methods declared by AbstractListViewer should not be used by
	 *              clients. This class will be modified to extend ItemViewer at
	 *              some point in the future which will break references to
	 *              AbstractListViewer API.
	 */
	protected int listGetTopIndex() {
		return super.listGetTopIndex();
	}

	/**
	 * @noreference Methods declared by AbstractListViewer should not be used by
	 *              clients. This class will be modified to extend ItemViewer at
	 *              some point in the future which will break references to
	 *              AbstractListViewer API.
	 */
	protected void listSetTopIndex(int index) {
		super.listSetTopIndex(index);
	}

	/**
	 * @noreference Methods declared by AbstractListViewer should not be used by
	 *              clients. This class will be modified to extend ItemViewer at
	 *              some point in the future which will break references to
	 *              AbstractListViewer API.
	 */
	public void remove(Object element) {
		super.remove(element);
	}

	/**
	 * @noreference Methods declared by AbstractListViewer should not be used by
	 *              clients. This class will be modified to extend ItemViewer at
	 *              some point in the future which will break references to
	 *              AbstractListViewer API.
	 */
	public void remove(Object[] elements) {
		super.remove(elements);
	}
}
