/*
 * Copyright (c) 2005 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.core.grid;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.internal.util.PaperClipsUtil;
import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.SWT;

/**
 * Describes the properties of a column in a GridPrint.
 * 
 * @author Matthew Hall
 */
public class GridColumn {
	/**
	 * The default alignment used when alignment is not specified. Value is
	 * SWT.LEFT.
	 */
	public static final int DEFAULT_ALIGN = SWT.LEFT;

	/**
	 * The default size used when size is not specified. Value is SWT.DEFAULT.
	 */
	public static final int DEFAULT_SIZE = SWT.DEFAULT;

	/**
	 * The default weight used when weight is not specified. Value is 0.
	 */
	public static final int DEFAULT_WEIGHT = 0;

	/**
	 * The size property for this GridColumn. Possible values:
	 * <ul>
	 * <li>GridPrint.PREFERRED - indicates that the column should be as wide as
	 * the preferred width of its widest element.
	 * <li>SWT.DEFAULT - Similar to GridPrint.PREFERRED, except that the column
	 * may shrink down to its minimum width if space is scarce.
	 * <li>A value > 0 indicates that the column should be <code>size</code>
	 * points wide (72pts = 1").
	 * </ul>
	 */
	public final int size;

	/**
	 * The default alignment for Prints in this column. Possible values are
	 * SWT.LEFT, SWT.CENTER, SWT.RIGHT, or SWT.DEFAULT. Note that alignment
	 * affects the placement of PrintPieces within the grid's cell--the
	 * alignment elements of the PrintPiece themselves are not affected. Thus,
	 * in order to achieve the desired effect, a Print having an alignment
	 * property should be set to the same alignment as the grid cell it is added
	 * to. For example, a TextPrint in a right-aligned grid cell should be set
	 * to right alignment as well.
	 * <p>
	 * Cells that span multiple columns use the alignment of the left-most cell
	 * in the cell span.
	 */
	public final int align;

	/**
	 * The weight of this column. If the available print space is wider than the
	 * grid's preferred width, this field determines how much of that extra
	 * space should be given to this column. A larger weight causes the column
	 * to receive more of the extra width. A value of 0 indicates that the
	 * column should not be given any excess width.
	 */
	public final int weight;

	/**
	 * Constructs a GridColumn.
	 * 
	 * @param align
	 *            The default alignment for Prints in this column.
	 * @param size
	 *            The size this column should be given.
	 * @param weight
	 *            The weight this column should be given.
	 */
	public GridColumn(int align, int size, int weight) {
		this.align = checkAlign(align);
		this.size = checkSize(size);
		this.weight = checkWeight(weight);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + align;
		result = prime * result + size;
		result = prime * result + weight;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GridColumn other = (GridColumn) obj;
		if (align != other.align)
			return false;
		if (size != other.size)
			return false;
		if (weight != other.weight)
			return false;
		return true;
	}

	private static int checkAlign(int align) {
		align = PaperClipsUtil.firstMatch(align, new int[] { SWT.LEFT,
				SWT.CENTER, SWT.RIGHT, SWT.DEFAULT }, 0);
		if (align == 0)
			PaperClips
					.error(
							SWT.ERROR_INVALID_ARGUMENT,
							"Alignment argument must be one of SWT.LEFT, SWT.CENTER, SWT.RIGHT, or SWT.DEFAULT"); //$NON-NLS-1$
		if (align == SWT.DEFAULT)
			return DEFAULT_ALIGN;
		return align;
	}

	private static int checkSize(int size) {
		if (size != SWT.DEFAULT && size != GridPrint.PREFERRED && size <= 0)
			PaperClips
					.error(SWT.ERROR_INVALID_ARGUMENT,
							"Size argument must be SWT.DEFAULT, GridPrint.PREFERRED, or > 0"); //$NON-NLS-1$
		return size;
	}

	private static int checkWeight(int grow) {
		if (grow < 0)
			PaperClips.error(SWT.ERROR_INVALID_ARGUMENT,
					"Weight argument must be >= 0"); //$NON-NLS-1$
		return grow;
	}

	/**
	 * Parses the given column spec and returns a GridColumn matching that spec.
	 * <p>
	 * Format:
	 * 
	 * <pre>
	 *  [align:]size[:grow]
	 *  
	 *  align  = L | LEFT |
	 *           C | CENTER |
	 *           R | RIGHT
	 *  size   = P | PREF | PREFERRED |
	 *           D | DEF | DEFAULT |
	 *           (Positive number)[PT|IN|INCH|CM|MM]
	 *  weight = N | NONE |
	 *           G | GROW | G(#) | GROW(#)
	 * </pre>
	 * 
	 * The default alignment is LEFT. The
	 * 
	 * <code>weight</code> argument expresses the weight property: NONE
	 * indicates a weight of 0; GROW indicates a weight of 1; and GROW(3)
	 * indicates a weight of 3. The default weight (if <code>weight</code> is
	 * omitted) is 0.
	 * <p>
	 * Examples:
	 * 
	 * <pre>
	 * LEFT:DEFAULT:GROW // left-aligned, default size, weight=1
	 *  R:72PT:N          // light-aligned, 72 points (1&quot;) wide, weight=0
	 *  right:72          // identical to previous line
	 *  c:pref:none       // center-aligned, preferred size, weight=0
	 *  p                 // left-aligned (default), preferred size, weight=0
	 *  r:2inch           // right-aligned, 2 inches (50.8mm)
	 *  r:50.8mm          // right-aligned, 50.8 mm (2&quot;)
	 * </pre>
	 * 
	 * @param spec
	 *            the column spec that will be parsed.
	 * @return a GridColumn matching the column spec.
	 * @see #align
	 * @see #size
	 * @see #weight
	 */
	public static GridColumn parse(String spec) {
		Util.notNull(spec);

		String[] matches = spec.split("\\s*:\\s*"); //$NON-NLS-1$
		if (matches.length == 0)
			PaperClips.error(SWT.ERROR_INVALID_ARGUMENT, "Missing column spec"); //$NON-NLS-1$

		int align = DEFAULT_ALIGN;
		int size = DEFAULT_SIZE;
		int grow = DEFAULT_WEIGHT;

		if (matches.length == 1) {
			// One option: must be size
			size = parseSize(matches[0]);
		} else if (matches.length == 2) {
			// Two possible scenarios:
			// 1. align:size
			// 2. size:weight
			if (isAlign(matches[0])) {
				align = parseAlign(matches[0]);
				size = parseSize(matches[1]);
			} else {
				size = parseSize(matches[0]);
				grow = parseWeight(matches[1]);
			}
		} else if (matches.length == 3) {
			align = parseAlign(matches[0]);
			size = parseSize(matches[1]);
			grow = parseWeight(matches[2]);
		}

		return new GridColumn(align, size, grow);
	}

	// Alignment patterns
	private static final Pattern LEFT_ALIGN_PATTERN = Pattern.compile(
			"^l(eft)?$", //$NON-NLS-1$
			Pattern.CASE_INSENSITIVE);

	private static final Pattern CENTER_ALIGN_PATTERN = Pattern.compile(
			"^c(enter)?$", //$NON-NLS-1$
			Pattern.CASE_INSENSITIVE);

	private static final Pattern RIGHT_ALIGN_PATTERN = Pattern.compile(
			"^r(ight)?$", //$NON-NLS-1$
			Pattern.CASE_INSENSITIVE);

	private static final Pattern ANY_ALIGN_PATTERN = Pattern.compile(
			"^l(eft)?|c(enter)?|r(ight)?$", //$NON-NLS-1$
			Pattern.CASE_INSENSITIVE);

	private static boolean isAlign(String alignmentString) {
		return ANY_ALIGN_PATTERN.matcher(alignmentString).matches();
	}

	private static int parseAlign(String alignmentString) {
		if (LEFT_ALIGN_PATTERN.matcher(alignmentString).matches())
			return SWT.LEFT;
		else if (CENTER_ALIGN_PATTERN.matcher(alignmentString).matches())
			return SWT.CENTER;
		else if (RIGHT_ALIGN_PATTERN.matcher(alignmentString).matches())
			return SWT.RIGHT;
		PaperClips.error(SWT.ERROR_INVALID_ARGUMENT,
				"Unknown alignment \"" + alignmentString + "\""); //$NON-NLS-1$//$NON-NLS-2$
		return 0; // unreachable
	}

	// Size patterns.
	private static final Pattern DEFAULT_SIZE_PATTERN = Pattern.compile(
			"^d(ef(ault)?)?$", //$NON-NLS-1$
			Pattern.CASE_INSENSITIVE);

	private static final Pattern PREFERRED_SIZE_PATTERN = Pattern.compile(
			"^p(ref(erred)?)?", //$NON-NLS-1$
			Pattern.CASE_INSENSITIVE);

	private static final Pattern EXPLICIT_SIZE_PATTERN = Pattern.compile(
			"^(\\d+(\\.\\d+)?)\\s*(pt|in(ch)?|mm|cm)?$", //$NON-NLS-1$
			Pattern.CASE_INSENSITIVE);

	private static int parseSize(String sizeString) {
		Matcher matcher;
		if (DEFAULT_SIZE_PATTERN.matcher(sizeString).matches())
			return SWT.DEFAULT;
		else if (PREFERRED_SIZE_PATTERN.matcher(sizeString).matches())
			return GridPrint.PREFERRED;
		else if ((matcher = EXPLICIT_SIZE_PATTERN.matcher(sizeString))
				.matches()) {
			return (int) Math.ceil(convertToPoints(Double.parseDouble(matcher
					.group(1)), matcher.group(3)));
		} else {
			PaperClips.error(SWT.ERROR_INVALID_ARGUMENT,
					"Unknown size pattern: \"" + sizeString + "\""); //$NON-NLS-1$ //$NON-NLS-2$
			return 0; // unreachable
		}
	}

	private static double convertToPoints(double value, String unit) {
		if (unit == null || unit.length() == 0 || unit.equalsIgnoreCase("pt")) //$NON-NLS-1$
			return value;
		else if (unit.equalsIgnoreCase("in") || unit.equalsIgnoreCase("inch")) //$NON-NLS-1$ //$NON-NLS-2$
			return 72 * value;
		else if (unit.equalsIgnoreCase("cm")) //$NON-NLS-1$
			return 72 * value / 2.54;
		else if (unit.equalsIgnoreCase("mm")) //$NON-NLS-1$
			return 72 * value / 25.4;
		PaperClips.error(SWT.ERROR_INVALID_ARGUMENT,
				"Unknown unit \"" + unit + "\"."); //$NON-NLS-1$ //$NON-NLS-2$
		return 0;
	}

	private static final Pattern WEIGHTLESS_PATTERN = Pattern.compile(
			"n(one)?", //$NON-NLS-1$
			Pattern.CASE_INSENSITIVE);

	private static final Pattern WEIGHTED_PATTERN = Pattern.compile(
			"(g(row)?)(\\((\\d+)\\))?", // yikes //$NON-NLS-1$
			Pattern.CASE_INSENSITIVE);

	private static int parseWeight(String weightString) {
		Matcher matcher;
		if (WEIGHTLESS_PATTERN.matcher(weightString).matches())
			return 0;
		else if ((matcher = WEIGHTED_PATTERN.matcher(weightString)).matches()) {
			String weight = matcher.group(4);
			return (weight == null) ? 1 : Integer.parseInt(weight);
		} else {
			PaperClips.error(SWT.ERROR_INVALID_ARGUMENT,
					"Illegal grow pattern: \"" + weightString //$NON-NLS-1$
							+ "\""); //$NON-NLS-1$
			return 0; // unreachable
		}
	}

	GridColumn copy() {
		return new GridColumn(align, size, weight);
	}
}