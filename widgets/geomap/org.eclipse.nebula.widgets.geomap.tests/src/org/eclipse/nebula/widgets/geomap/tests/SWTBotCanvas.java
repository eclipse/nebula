package org.eclipse.nebula.widgets.geomap.tests;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBotControl;

public class SWTBotCanvas<T extends Canvas> extends AbstractSWTBotControl<T> {

	public SWTBotCanvas(T widget) throws WidgetNotFoundException {
		super(widget);
	}

	protected T getCanvas() {
		return widget;
	}
	
	protected void mouseDrag(int x1, int y1, int x2, int y2, int buttons, int interpolate) {
		mouseDown(x1, y1, buttons, 1);
		int dx = (x2 - x1) / (interpolate + 1), dy = (y2 - y1) / (interpolate + 1);
		for (int i = 0; i < interpolate; i++) {
			x1 += dx;
			y1 += dy;
			mouseMove(x1, y1, buttons, 1);
		}
		mouseMove(x2, y2, buttons, 1);
		mouseUp(x2, y2, buttons, 1);
	}

	protected void postEvent(final Event event) {
		asyncExec(new VoidResult() {
			public void run() {
//				display.post(event);
				widget.notifyListeners(event.type, event);
			}
		});
	}
	
	protected void postMouseEvent(final int type, final int x, final int y, final int buttons, final int count) {
		Event event = createMouseEvent(x, y, buttons, buttons, count);
		event.type = type;
		postEvent(event);
	}
	
	protected void mouseDown(final int x, final int y, final int buttons, final int count) {
		int type = count == 2 ? SWT.MouseDoubleClick : SWT.MouseDown;
		postMouseEvent(type, x, y, buttons, count);
	}
	
	protected void mouseMove(final int x, final int y, final int buttons, final int count) {
		postMouseEvent(SWT.MouseMove, x, y, buttons, count);
	}

	protected void mouseUp(final int x, final int y, final int buttons, final int count) {
		postMouseEvent(SWT.MouseUp, x, y, buttons, count);
	}

	protected void mouseClick(int x, int y, int buttons, int count) {
		for (int i = 1; i <= count; i++) {
			mouseDown(x, y, buttons, i);
			mouseUp(x, y, buttons, i);
		}
	}
}
