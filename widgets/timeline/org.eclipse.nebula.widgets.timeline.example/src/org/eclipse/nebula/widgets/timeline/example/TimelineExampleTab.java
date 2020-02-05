package org.eclipse.nebula.widgets.timeline.example;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.eclipse.nebula.examples.AbstractExampleTab;
import org.eclipse.nebula.widgets.timeline.ILane;
import org.eclipse.nebula.widgets.timeline.ITimeline;
import org.eclipse.nebula.widgets.timeline.ITrack;
import org.eclipse.nebula.widgets.timeline.TimelineDataBinding;
import org.eclipse.nebula.widgets.timeline.jface.TimelineViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class TimelineExampleTab extends AbstractExampleTab {

	private static final int DISPLAY_UPDATE_DELAY = 300;

	public TimelineExampleTab() {
	}

	@Override
	public String[] createLinks() {
		return new String[0];
	}

	@Override
	public void createParameters(Composite parent) {
	}

	@Override
	public Control createControl(Composite parent) {
		TimelineViewer timelineViewer = new TimelineViewer(parent, SWT.NULL);
		final ITimeline model = (ITimeline) timelineViewer.getInput();
		new TimelineDataBinding(timelineViewer, model, DISPLAY_UPDATE_DELAY);

		createRandomContent(model);
		
		return timelineViewer.getControl();
	}

	private void createRandomContent(ITimeline model) {

		final ITrack track4 = model.createTrack("High Level");
		final ITrack track3 = model.createTrack("Low Level");

		final ILane requests = track4.createLane();
		final ILane responses = track4.createLane();

		final ILane one = track3.createLane();
		final ILane two = track3.createLane();
		final ILane three = track3.createLane();
		final ILane four = track3.createLane();

		final List<ILane> lanes = Arrays.asList(requests, responses, one, two, three, four);

		final Random random = new Random(12);
		int lastPosition = 0;
		for (int item = 0; item < 40; item++) {
			final int laneIndex = random.nextInt(lanes.size());
			final ILane lane = lanes.get(laneIndex);

			final int offset = random.nextInt(20);
			final int width = random.nextInt(150);
			lane.addEvent("Item " + item, (lastPosition + offset) + " - " + (lastPosition + offset + width),
					lastPosition + offset, lastPosition + offset + width, TimeUnit.NANOSECONDS);

			lastPosition += offset + width;
		}

		model.createCursor(122, TimeUnit.NANOSECONDS);
	}
}
