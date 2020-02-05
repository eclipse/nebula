package org.eclipse.nebula.widgets.timeline.views;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.eclipse.nebula.widgets.timeline.ILane;
import org.eclipse.nebula.widgets.timeline.ITimeline;
import org.eclipse.nebula.widgets.timeline.ITrack;
import org.eclipse.nebula.widgets.timeline.TimelineComposite;
import org.eclipse.nebula.widgets.timeline.TimelineDataBinding;
import org.eclipse.nebula.widgets.timeline.jface.TimelineViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class TimelineView extends ViewPart {

	private static final int DISPLAY_UPDATE_DELAY = 300;

	private TimelineComposite fTimingChart;
	private TimelineViewer fTimelineViewer;

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());

		fTimelineViewer = new TimelineViewer(parent, SWT.NULL);
		final ITimeline model = (ITimeline) fTimelineViewer.getInput();
		new TimelineDataBinding(fTimelineViewer, model, DISPLAY_UPDATE_DELAY);

		createRandomContent(model);
	}

	public TimelineViewer getTimelineViewer() {
		return fTimelineViewer;
	}

	private void createRandomContent(ITimeline model) {

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
			lane.addEvent("Item " + item, (lastPosition + offset) + " - " + (lastPosition + offset + width), lastPosition + offset,
					lastPosition + offset + width, TimeUnit.NANOSECONDS);

			lastPosition += offset + width;
		}

		model.createCursor(122, TimeUnit.NANOSECONDS);
	}

	@Override
	public void setFocus() {
	}

	/**
	 * @return the timingChart
	 */
	public TimelineComposite getTimingChart() {
		return fTimingChart;
	}
}
