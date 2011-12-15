package org.eclipse.nebula.widgets.xviewer.example;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerStyledTextLabelProvider;
import org.eclipse.nebula.widgets.xviewer.example.images.MyImageCache;
import org.eclipse.nebula.widgets.xviewer.example.model.ISomeTask;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class MyXViewerStyledTextLabelProvider extends XViewerStyledTextLabelProvider {

	private final MyXViewer xViewerTest;
	
	public MyXViewerStyledTextLabelProvider(MyXViewer viewer) {
		super(viewer);
		this.xViewerTest = viewer;
	}

	@Override
	public Image getColumnImage(Object element, XViewerColumn xCol, int column) throws Exception {
		 if (xCol.equals(MyXViewerFactory.Run_Col)) {
	         return xViewerTest.isRun((ISomeTask) element) ? MyImageCache.getImage("chkbox_enabled.gif") : MyImageCache.getImage("chkbox_disabled.gif");
	      }
	      if (xCol.equals(MyXViewerFactory.Name_Col) && xViewerTest.isScheduled((ISomeTask) element)) {
	         return MyImageCache.getImage("clock.gif");
	      }
	      return null;
	}

	@Override
	public StyledString getStyledText(Object element, XViewerColumn xCol, int column) throws Exception {
		 if (element instanceof String) {
	         if (column == 1) {
	            return new StyledString((String) element);
	         } else {
	            return new StyledString("");
	         }
	      }
	      ISomeTask task = ((ISomeTask) element);
	      if (task == null) {
	         return new StyledString("");
	      }
	      if (xCol.equals(MyXViewerFactory.Run_Col)) {
	         return new StyledString(String.valueOf(xViewerTest.isRun(task)), StyledString.COUNTER_STYLER);
	      }
	      if (xCol.equals(MyXViewerFactory.Name_Col)) {
	         return new StyledString(task.getId(), StyledString.DECORATIONS_STYLER);
	      }
	      if (xCol.equals(MyXViewerFactory.Schedule_Time)) {
	         return new StyledString(task.getStartTime(), StyledString.QUALIFIER_STYLER);
	      }
	      if (xCol.equals(MyXViewerFactory.Run_Db)) {
	         return new StyledString(task.getRunDb().name(), StyledString.COUNTER_STYLER);
	      }
	      if (xCol.equals(MyXViewerFactory.Task_Type)) {
	         return new StyledString(task.getTaskType().name(), StyledString.DECORATIONS_STYLER);
	      }
	      if (xCol.equals(MyXViewerFactory.Description)) {
	         return new StyledString(task.getDescription(), StyledString.COUNTER_STYLER);
	      }
	      if (xCol.equals(MyXViewerFactory.Category)) {
	         return new StyledString(task.getCategory(), StyledString.DECORATIONS_STYLER);
	      }
	      if (xCol.equals(MyXViewerFactory.Notification)) {
	         return new StyledString(task.getEmailAddress(), StyledString.QUALIFIER_STYLER);
	      }
	      return new StyledString("unhandled column");
	}

	@Override
	public Color getBackground(Object element, XViewerColumn viewerColumn, int columnIndex) throws Exception {
		return null;
	}

	@Override
	public Color getForeground(Object element, XViewerColumn viewerColumn, int columnIndex) throws Exception {
		return null;
	}

	@Override
	public Font getFont(Object element, XViewerColumn viewerColumn, int columnIndex) throws Exception {
		return null;
	}


}
