/*
 * Copyright (c) 2007-2008 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */

package org.eclipse.nebula.paperclips.core.internal.util;

import java.util.Iterator;
import java.util.List;

import org.eclipse.nebula.paperclips.core.PrintPiece;

/**
 * Convenience methods specific to PaperClips
 * 
 * @author Matthew Hall
 */
public class PaperClipsUtil {
	private PaperClipsUtil() {
	} // no instances

	/**
	 * Disposes the print piece if not null.
	 * 
	 * @param piece
	 *            the print piece to dispose.
	 */
	public static void dispose(final PrintPiece piece) {
		if (piece != null)
			piece.dispose();
	}

	/**
	 * Disposes the arguments that are not null.
	 * 
	 * @param p1
	 *            print piece to dispose
	 * @param p2
	 *            print piece to dispose
	 */
	public static void dispose(PrintPiece p1, PrintPiece p2) {
		dispose(p1);
		dispose(p2);
	}

	/**
	 * Disposes the print pieces that are not null.
	 * 
	 * @param pieces
	 *            array of print pieces to dispose.
	 */
	public static void dispose(final PrintPiece[] pieces) {
		if (pieces != null)
			for (int i = 0; i < pieces.length; i++)
				dispose(pieces[i]);
	}

	/**
	 * Disposes the print pieces in the array from start (inclusive) to end
	 * (exclusive).
	 * 
	 * @param pages
	 *            array of print pieces to dispose.
	 * @param start
	 *            the start index.
	 * @param end
	 *            the end index.
	 */
	public static void dispose(PrintPiece[] pages, int start, int end) {
		for (int i = start; i < end; i++)
			pages[i].dispose();
	}

	/**
	 * Disposes the print pieces in the list.
	 * 
	 * @param pages
	 *            list of print pieces to dispose.
	 */
	public static void dispose(List pages) {
		for (Iterator it = pages.iterator(); it.hasNext();)
			((PrintPiece) it.next()).dispose();
		pages.clear();
	}

	/**
	 * Disposes the print pieces that are not null.
	 * 
	 * @param piece
	 *            a print piece to dispose
	 * @param pieces
	 *            array of print pieces to dispose
	 */
	public static void dispose(PrintPiece piece, final PrintPiece[] pieces) {
		dispose(piece);
		dispose(pieces);
	}

	/**
	 * Returns a copy of the array.
	 * 
	 * @param array
	 *            the array to copy
	 * @return a copy of the array.
	 */
	public static int[] copy(int[] array) {
		Util.notNull(array);
		return (int[]) array.clone();
	}

	/**
	 * Returns a deep copy of the array.
	 * 
	 * @param array
	 *            the array to copy
	 * @return a copy of the array.
	 */
	public static int[][] copy(int[][] array) {
		Util.notNull(array);
		int[][] result = (int[][]) array.clone();
		for (int i = 0; i < result.length; i++)
			result[i] = copy(result[i]);
		return result;
	}

	/**
	 * Returns the sum of all elements in the array.
	 * 
	 * @param array
	 *            the array
	 * @return the sum of all elements in the array.
	 */
	public static int sum(int[] array) {
		return PaperClipsUtil.sum(array, 0, array.length);
	}

	/**
	 * Returns the sum of all elements in the array in the range
	 * <code>[start, start+count)</code>.
	 * 
	 * @param array
	 *            the array containing the elements to add up.
	 * @param start
	 *            the index of the first element to add.
	 * @param count
	 *            the number of elements to add.
	 * @return the sum of all elements in the array in the specified range.
	 */
	public static int sum(final int[] array, final int start, final int count) {
		Util.notNull(array);
		int result = 0;
		final int end = start + count;
		for (int i = start; i < end; i++)
			result += array[i];
		return result;
	}

	/**
	 * Returns the sum of all elements in the array at the given indices.
	 * 
	 * @param array
	 *            the array of elements to add up.
	 * @param indices
	 *            the indices of the elements in the array to add up.
	 * @return the sum of all elements in the array at the given indices.
	 */
	public static int sumByIndex(final int[] array, final int[] indices) {
		Util.notNull(array);
		int result = 0;
		for (int i = 0; i < indices.length; i++)
			result += array[indices[i]];
		return result;
	}

	/**
	 * Converts the argument to an int[] array.
	 * 
	 * @param list
	 *            a List of Integers.
	 * @return a primitive int[] array.
	 */
	public static int[] toIntArray(List list) {
		final int[] array = new int[list.size()];
		for (int i = 0; i < array.length; i++)
			array[i] = ((Integer) list.get(i)).intValue();
		return array;
	}

	/**
	 * Converts the argument to an int[][] array.
	 * 
	 * @param list
	 *            a List of int[] arrays.
	 * @return a primitive int[][] array.
	 */
	public static int[][] toIntIntArray(List list) {
		final int[][] array = new int[list.size()][];
		for (int i = 0; i < array.length; i++)
			array[i] = (int[]) list.get(i);
		return array;
	}

	/**
	 * Returns the first element in masks where (value & mask[index]) ==
	 * mask[index].
	 * 
	 * @param value
	 *            the value to match
	 * @param masks
	 *            the possible values.
	 * @param defaultMask
	 *            the value to return if no match is found.
	 * @return the first value in possibleValues which is a bitwise match to
	 *         value, or 0 if none is found.
	 */
	public static int firstMatch(int value, int[] masks, int defaultMask) {
		Util.notNull(masks);
		for (int i = 0; i < masks.length; i++) {
			int mask = masks[i];
			if ((value & mask) == mask)
				return mask;
		}
		return defaultMask;
	}
}
