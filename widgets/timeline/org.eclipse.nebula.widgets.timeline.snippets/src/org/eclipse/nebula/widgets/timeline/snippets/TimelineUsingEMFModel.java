package org.eclipse.nebula.widgets.timeline.snippets;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.eclipse.nebula.widgets.timeline.ILane;
import org.eclipse.nebula.widgets.timeline.ITimeline;
import org.eclipse.nebula.widgets.timeline.ITrack;
import org.eclipse.nebula.widgets.timeline.jface.TimelineViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TimelineUsingEMFModel {

	protected Shell shell;

	/**
	 * Launch the application.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			final TimelineUsingEMFModel window = new TimelineUsingEMFModel();
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

		populateModel(shell);
	}

	private void populateModel(Shell parent) {
		final TimelineViewer timelineViewer = new TimelineViewer(parent, SWT.NULL);
		final ITimeline model = (ITimeline) timelineViewer.getInput();

		final ITrack track4 = model.createTrack("Layer 4");
		final ITrack track3 = model.createTrack("Layer 3");

		final ILane apdus = track4.createLane();
		final ILane apduResponses = track4.createLane();

		final ILane commands = track3.createLane();
		final ILane responses = track3.createLane();
		final ILane another = track3.createLane();
		final ILane another2 = track3.createLane();

		final List<ILane> lanes = Arrays.asList(apdus, apduResponses, commands, responses, another, another2);

		final Random random = new Random(12);
		int lastPosition = 0;
		for (int item = 0; item < 40; item++) {
			final int laneIndex = random.nextInt(lanes.size());
			final ILane lane = lanes.get(laneIndex);

			final int offset = random.nextInt(20);
			final int width = random.nextInt(150);
			lane.createEvent("Item " + item, (lastPosition + offset) + " - " + (lastPosition + offset + width), lastPosition + offset, width,
					TimeUnit.NANOSECONDS);

			lastPosition += offset + width;
		}

		model.createCursor(122, TimeUnit.NANOSECONDS);

		timelineViewer.refresh();
	}
}
