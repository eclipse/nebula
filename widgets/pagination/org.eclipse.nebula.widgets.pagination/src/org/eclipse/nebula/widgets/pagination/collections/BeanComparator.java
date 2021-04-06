/*******************************************************************************
 * Copyright (C) 2011 Angelo Zerr <angelo.zerr@gmail.com>, Pascal Leclercq <pascal.leclercq@gmail.com>
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Angelo ZERR - initial API and implementation
 *     Pascal Leclercq - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.pagination.collections;

import java.util.Comparator;

import org.eclipse.swt.SWT;

/**
 * Implementation of {@link Comparator} to compare POJO.
 *
 */
@SuppressWarnings("rawtypes")
public class BeanComparator implements Comparator {

	/** property name used to sort **/
	private final String sortPropertyName;
	/** the sort direction **/
	private int sortDirection;

	public BeanComparator(String sortPropertyName, int sortDirection) {
		this.sortPropertyName = sortPropertyName;
		this.sortDirection = sortDirection;
	}

	public int compare(Object o1, Object o2) {
		if ((o1 instanceof Comparable) && (o2 instanceof Comparable)) {
			// Compare simple type like String, Integer etc
			Comparable c1 = ((Comparable) o1);
			Comparable c2 = ((Comparable) o2);
			return compare(c1, c2);
		}

		o1 = BeanUtils.getValue(o1, sortPropertyName);
		o2 = BeanUtils.getValue(o2, sortPropertyName);
		if ((o1 instanceof Comparable) && (o2 instanceof Comparable)) {
			// Compare simple type like String, Integer etc
			Comparable c1 = ((Comparable) o1);
			Comparable c2 = ((Comparable) o2);
			return compare(c1, c2);
		}

		return 0;
	}

	@SuppressWarnings("unchecked")
	private int compare(Comparable c1, Comparable c2) {
		if (sortDirection == SWT.UP) {
			return c2.compareTo(c1);
		}
		return c1.compareTo(c2);
	}

}
