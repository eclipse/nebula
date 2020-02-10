package org.eclipse.nebula.widgets.timeline.snippets;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IToolTipProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.nebula.widgets.timeline.Timing;
import org.eclipse.nebula.widgets.timeline.jface.ITimelineContentProvider;
import org.eclipse.nebula.widgets.timeline.jface.ITimelineLabelProvider;
import org.eclipse.nebula.widgets.timeline.jface.TimelineViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TimelineUsingContentProvider {

	protected Shell shell;

	/**
	 * Launch the application.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			final TimelineUsingContentProvider window = new TimelineUsingContentProvider();
			window.open();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		final Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		createViewer(shell);
	}

	private void createViewer(Shell parent) {
		final TimelineViewer timelineViewer = new TimelineViewer(parent, SWT.NULL);
		timelineViewer.setLabelProvider(new TooltipLabelProvider());
		timelineViewer.setContentProvider(new TooltipContentProvider());

		timelineViewer.setInput(fEvents);
	}

	private static List<Integer> fEvents = Arrays.asList(100, 400, 1000, 5000, 6400, 1200, 22000);

	private class TooltipLabelProvider extends LabelProvider implements ITimelineLabelProvider, IToolTipProvider, IColorProvider {

		@Override
		public String getText(Object element) {
			return "Offset: " + super.getText(element);
		}

		@Override
		public String getToolTipText(Object element) {
			return "This is the tooltip for offset " + element;
		}

		@Override
		public Color getForeground(Object element) {
			if ("1200".equals(element.toString()))
				return ColorConstants.red;

			return null;
		}

		@Override
		public Color getBackground(Object element) {
			// ignored
			return null;
		}

		@Override
		public Timing getTimings(Object element) {
			if (element instanceof Integer)
				return new Timing((int) element, 100);

			return null;
		}
	}

	private class TooltipContentProvider extends ArrayContentProvider implements ITimelineContentProvider {

		@Override
		public Object[] getTracks(Object input) {
			return new Object[] { "One" };
		}

		@Override
		public Object[] getLanes(Object track) {
			return new Object[] { "Lane 1", "Lane 2" };
		}

		@Override
		public Object[] getEvents(Object lane) {
			switch (lane.toString()) {
			case "Lane 1":
				return fEvents.stream().filter(e -> e < 5000).collect(Collectors.toList()).toArray();
			case "Lane 2":
				return fEvents.stream().filter(e -> e >= 5000).collect(Collectors.toList()).toArray();
			default:
				return new Object[0];
			}
		}

		@Override
		public Object[] getCursors(Object input) {
			return new Object[0];
		}
	}
}
