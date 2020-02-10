package org.eclipse.nebula.widgets.timeline.snippets;

import org.eclipse.nebula.widgets.timeline.ITimelineEvent;
import org.eclipse.nebula.widgets.timeline.ITimelineFactory;
import org.eclipse.nebula.widgets.timeline.TimelineComposite;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.TrackFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.lane.LaneFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TimelineProgrammatic {

	protected Shell shell;

	/**
	 * Launch the application.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			final TimelineProgrammatic window = new TimelineProgrammatic();
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

		populateControl(shell);
	}

	private void populateControl(Shell parent) {
		final TimelineComposite control = new TimelineComposite(parent, SWT.NONE);

		final ITimelineEvent event = ITimelineFactory.eINSTANCE.createTimelineEvent();
		event.setStartTimestamp(100);
		event.setDuration(400);
		event.setTitle("The best event ever");

		final TrackFigure track1 = control.getRootFigure().createTrackFigure("Track 1");
		final LaneFigure lane1 = control.getRootFigure().createLaneFigure(track1);
		control.getRootFigure().createEventFigure(lane1, event);
		control.getRootFigure().createCursor(200);
	}
}
