package org.eclipse.nebula.widgets.ganttchart.dnd;

import org.eclipse.nebula.widgets.ganttchart.GanttComposite;
import org.eclipse.swt.dnd.DropTargetEffect;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

public class GanttDropTargetEffect extends DropTargetEffect {

	private GanttComposite	_parent;

	public GanttDropTargetEffect(GanttComposite parent) {
		super(parent);
		_parent = parent;
	}

	public Control getControl() {
		return _parent;
	}

	public Widget getItem(int x, int y) {
		return super.getItem(x, y);
	}


}
