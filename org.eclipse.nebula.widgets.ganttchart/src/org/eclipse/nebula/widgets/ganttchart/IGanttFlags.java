package org.eclipse.nebula.widgets.ganttchart;

interface IGanttFlags {

	/**
	 * Creates the chart with an infinite scrollbar. This is the default unless one is set explicitly.
	 */
	public static final int	H_SCROLL_INFINITE		= 1 << 1;

	/**
	 * Creates the chart with a scrollbar fixed to the range of the dates on the chart.
	 */
	public static final int	H_SCROLL_FIXED_RANGE	= 1 << 2;

	/**
	 * Creates the chart with no horizontal scrollbar. It's up to you to handle any horizontal scrolling manually.
	 */
	public static final int	H_SCROLL_NONE			= 1 << 3;

}
