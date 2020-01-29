/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API
 * and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.commons;

import java.util.ArrayList;
import java.util.List;

/**
 * Instance of this class are queues that have a fixed size.<br/>
 * When the queue is full, the elements are shifted and the first element is
 * lost.
 * 
 * @param <T> Type of objects stored in this queue
 */
public class FixedSizeQueue<T> {
	private T[] buffer;
	private int index;

	/**
	 * Constructor
	 * 
	 * @param capacity initial capacity
	 */
	@SuppressWarnings("unchecked")
	public FixedSizeQueue(final int capacity) {
		this.buffer = (T[]) new Object[capacity];
		this.index = 0;
	}

	/**
	 * Store an element in the buffer
	 * 
	 * @param element element to store
	 */
	public void put(final T element) {
		if (this.index == this.buffer.length) {
			// Full
			System.arraycopy(this.buffer, 1, this.buffer, 0, this.buffer.length - 1);
			this.buffer[this.index - 1] = element;
		} else {
			this.buffer[this.index++] = element;
		}
	}

	/**
	 * @return all values stored in this queue
	 */
	public List<T> getValues() {
		final List<T> list = new ArrayList<T>(this.index);
		for (int i = 0; i < this.index; i++) {
			if (this.buffer[i] != null) {
				list.add(this.buffer[i]);
			}
		}
		return list;
	}

	/**
	 * @return size of the buffer
	 */
	public int getSize() {
		return this.index;
	}

	/**
	 * @param newSize new size of the buffer. If newSize is lower than the actual
	 *            size, the buffer will contain the last elements that have been
	 *            stored
	 */
	@SuppressWarnings("unchecked")
	public void resizeTo(int newSize) {
		if (newSize < 0) {
			newSize = 1;
		}
		if (newSize == this.buffer.length) {
			return;
		}
		final T[] resizedBuffer = (T[]) new Object[newSize];
		if (newSize > this.buffer.length) {
			System.arraycopy(this.buffer, 0, resizedBuffer, 0, this.buffer.length);
		} else {
			final int startPos = Math.max(0, this.index - newSize);
			System.arraycopy(this.buffer, startPos, resizedBuffer, 0, newSize);
			this.index = newSize;
		}
		this.buffer = resizedBuffer;
	}

}
