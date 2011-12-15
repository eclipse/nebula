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

import java.util.Arrays;
import java.util.List;

import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.swt.SWT;

/**
 * General use convenience methods: null checking, equality
 * 
 * @author Matthew Hall
 */
public class Util {
	/**
	 * Returns whether the objects are of the same class.
	 * 
	 * @param left
	 *            object to test
	 * @param right
	 *            object to test
	 * @return whether the objects are of the same class.
	 */
	public static boolean sameClass(Object left, Object right) {
		if (left == right)
			return true;
		if (left == null || right == null)
			return false;
		return left.getClass() == right.getClass();
	}

	/**
	 * Returns whether the arguments are equal.
	 * 
	 * @param left
	 *            object to test
	 * @param right
	 *            object to test
	 * @return whether the arguments are equal.
	 */
	public static boolean equal(Object left, Object right) {
		if (!sameClass(left, right))
			return false;
		if (left == right)
			return true;
		Class clazz = left.getClass();
		if (clazz.isArray()) {
			Class componentType = clazz.getComponentType();
			if (componentType.isPrimitive()) {
				if (componentType == Byte.TYPE)
					return Arrays.equals((byte[]) left, (byte[]) right);
				if (componentType == Short.TYPE)
					return Arrays.equals((short[]) left, (short[]) right);
				if (componentType == Integer.TYPE)
					return Arrays.equals((int[]) left, (int[]) right);
				if (componentType == Long.TYPE)
					return Arrays.equals((long[]) left, (long[]) right);
				if (componentType == Character.TYPE)
					return Arrays.equals((char[]) left, (char[]) right);
				if (componentType == Float.TYPE)
					return Arrays.equals((float[]) left, (float[]) right);
				if (componentType == Double.TYPE)
					return Arrays.equals((double[]) left, (double[]) right);
				if (componentType == Boolean.TYPE)
					return Arrays.equals((boolean[]) left, (boolean[]) right);
			}
			return equal((Object[]) left, (Object[]) right);
		}
		return left.equals(right);
	}

	private static boolean equal(Object[] left, Object[] right) {
		int length = left.length;
		if (length != right.length)
			return false;
		for (int i = 0; i < length; i++)
			if (!equal(left[i], right[i]))
				return false;
		return true;
	}

	/**
	 * Returns whether the arguments are equal.
	 * 
	 * @param left
	 *            double value to test
	 * @param right
	 *            double value to test
	 * @return whether the arguments are equal.
	 */
	public static boolean equal(double left, double right) {
		return Double.doubleToLongBits(left) == Double.doubleToLongBits(right);
	}

	/**
	 * Triggers a SWT.ERROR_NULL_ARGUMENT exception if the argument or any of
	 * its elements is null.
	 * 
	 * @param list
	 *            a list to test for null elements.
	 */
	public static void noNulls(List list) {
		notNull(list);
		if (list.contains(null))
			PaperClips.error(SWT.ERROR_NULL_ARGUMENT);
	}

	/**
	 * Triggers a SWT.ERROR_NULL_ARGUMENT exception if the argument or any of
	 * its elements is null.
	 * 
	 * @param objs
	 *            an array to test for null elements.
	 */
	public static void noNulls(Object[] objs) {
		notNull(objs);
		for (int i = 0; i < objs.length; i++)
			notNull(objs[i]);
	}

	/**
	 * Triggers a SWT.ERROR_NULL_ARGUMENT exception if the argument is null.
	 * 
	 * @param obj
	 *            the object to test for null.
	 */
	public static void notNull(Object obj) {
		if (obj == null)
			PaperClips.error(SWT.ERROR_NULL_ARGUMENT);
	}

	/**
	 * Triggers a SWT.ERROR_NULL_ARGUMENT exception if any argument is null.
	 * 
	 * @param o1
	 *            an object to test for null.
	 * @param o2
	 *            an object to test for null.
	 */
	public static void notNull(Object o1, Object o2) {
		notNull(o1);
		notNull(o2);
	}

	/**
	 * Triggers a SWT.ERROR_NULL_ARGUMENT exception if any argument is null.
	 * 
	 * @param o1
	 *            an object to test for null.
	 * @param o2
	 *            an object to test for null.
	 * @param o3
	 *            an object to test for null.
	 */
	public static void notNull(Object o1, Object o2, Object o3) {
		notNull(o1);
		notNull(o2);
		notNull(o3);
	}
}
