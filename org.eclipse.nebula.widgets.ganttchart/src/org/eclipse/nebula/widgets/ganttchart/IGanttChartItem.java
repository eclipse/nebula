package org.eclipse.nebula.widgets.ganttchart;

public interface IGanttChartItem {

	public boolean isAutomaticRowHeight();
	public int getFixedRowHeight();
	public void setFixedRowHeight(int height);
	public void setAutomaticRowHeight();
	
}
