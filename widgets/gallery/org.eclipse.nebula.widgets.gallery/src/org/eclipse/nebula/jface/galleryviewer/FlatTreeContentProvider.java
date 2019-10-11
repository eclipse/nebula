/*******************************************************************************
 * Copyright (c) 2007-2008 Peter Centgraf.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors :
 *    Peter Centgraf - initial implementation
 *******************************************************************************/
package org.eclipse.nebula.jface.galleryviewer;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Adaptor that converts an {@link IStructuredContentProvider} into an
 * {@link ITreeContentProvider} that places the nested contents inside a single
 * root node.
 * 
 * @author Peter Centgraf
 * @since Dec 6, 2007
 */
public class FlatTreeContentProvider implements ITreeContentProvider {

	protected final Object rootNode;
	protected final IStructuredContentProvider provider;
	protected final Object[] roots;

	/**
	 * Adapts an {@link IStructuredContentProvider} into an
	 * {@link ITreeContentProvider} that places the nested contents inside a
	 * single root node.
	 * 
	 * @param provider
	 *            the {@link IStructuredContentProvider} to adapt
	 */
	public FlatTreeContentProvider(IStructuredContentProvider provider) {
		this(provider, "");
	}

	/**
	 * Adapts an {@link IStructuredContentProvider} into an
	 * {@link ITreeContentProvider} that places the nested contents inside the
	 * given root node.
	 * 
	 * @param provider
	 *            the {@link IStructuredContentProvider} to adapt
	 * @param the
	 *            single root node for the tree
	 */
	public FlatTreeContentProvider(IStructuredContentProvider provider,
			Object rootNode) {
		this.provider = provider;
		this.rootNode = rootNode;

		roots = new Object[] { rootNode };
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		if (element == rootNode) {
			return null;
		} else {
			return rootNode;
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		return element == rootNode;
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return roots;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		return provider.getElements(parentElement);
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		provider.dispose();
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		provider.inputChanged(viewer, oldInput, newInput);
	}

}
