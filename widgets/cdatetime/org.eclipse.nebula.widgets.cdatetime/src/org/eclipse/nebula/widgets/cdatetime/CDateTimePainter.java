/****************************************************************************
 * Copyright (c) 2008 Jeremy Dowdall
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/

package org.eclipse.nebula.widgets.cdatetime;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.nebula.cwt.v.IControlPainter;
import org.eclipse.nebula.cwt.v.VButton;
import org.eclipse.nebula.cwt.v.VButtonPainter;
import org.eclipse.nebula.cwt.v.VControl;
import org.eclipse.nebula.cwt.v.VLabelPainter;
import org.eclipse.nebula.cwt.v.VPanel;
import org.eclipse.nebula.cwt.v.VPanelPainter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;

public class CDateTimePainter implements IControlPainter {

	protected CDateTime cdt;

	protected IControlPainter buttonPainter;
	protected IControlPainter labelPainter;
	protected IControlPainter panelPainter;

	public CDateTimePainter() {
		buttonPainter = new VButtonPainter();
		labelPainter = new VLabelPainter();
		panelPainter = new VPanelPainter();
	}

	private void defaultPaintBackground(VControl control, Event e) {
		switch (control.getType()) {
		case Button:
			buttonPainter.paintBackground(control, e);
			break;
		case Label:
			labelPainter.paintBackground(control, e);
			break;
		case Panel:
			panelPainter.paintBackground(control, e);
			break;
		}
	}

	private void defaultPaintBorders(VControl control, Event e) {
		switch (control.getType()) {
		case Button:
			buttonPainter.paintBorders(control, e);
			break;
		case Label:
			labelPainter.paintBorders(control, e);
			break;
		case Panel:
			panelPainter.paintBorders(control, e);
			break;
		}
	}

	private void defaultPaintContent(VControl control, Event e) {
		switch (control.getType()) {
		case Button:
			buttonPainter.paintContent(control, e);
			break;
		case Label:
			labelPainter.paintContent(control, e);
			break;
		case Panel:
			panelPainter.paintContent(control, e);
			break;
		}
	}

	@Override
	public void dispose() {
		buttonPainter.dispose();
		labelPainter.dispose();
		panelPainter.dispose();
	}

	protected VPanel getPicker() {
		return cdt.picker;
	}

	protected final int indexOf(VControl control) {
		Object obj = control.getData(CDT.Key.Index);
		if (obj instanceof Integer) {
			return (Integer) obj;
		}
		return -1;
	}

	protected final boolean isActive(VControl control) {
		Object obj = control.getData(CDT.Key.Active);
		if (obj instanceof Boolean) {
			return (Boolean) obj;
		}
		return false;
	}

	protected final boolean isToday(VControl control) {
		Object obj = control.getData(CDT.Key.Today);
		if (obj instanceof Boolean) {
			return (Boolean) obj;
		}
		return false;
	}

	@Override
	public final void paintBackground(VControl control, Event e) {
		switch ((CDT.PickerPart) control.getData(CDT.PickerPart)) {
		case ClearButton:
			paintClearButtonBackground(control, e);
			break;
		case DateNow:
			paintDateNowBackground(control, e);
			break;
		case DayButton:
			paintDayButtonBackground(control, e);
			break;
		case DayOfWeekLabel:
			paintDayOfWeekLabelBackground(control, e);
			break;
		case DayOfWeekPanel:
			paintDayOfWeekPanelBackground(control, e);
			break;
		case DayPanel:
			paintDayPanelBackground(control, e);
			break;
		case TodayButton:
			paintFooterButtonBackground(control, e);
			break;
		case FooterPanel:
			paintFooterPanelBackground(control, e);
			break;
		case HeaderPanel:
			paintHeaderPanelBackground(control, e);
			break;
		case MonthLabel:
			paintMonthLabelBackground(control, e);
			break;
		case MonthNext:
			paintMonthNextBackground(control, e);
			break;
		case MonthPrev:
			paintMonthPrevBackground(control, e);
			break;
		case YearLabel:
			paintYearLabelBackground(control, e);
			break;
		case YearNext:
			paintYearNextBackground(control, e);
			break;
		case YearPrev:
			paintYearPrevBackground(control, e);
			break;
		default:
			defaultPaintBackground(control, e);
			break;
		}
	}

	@Override
	public final void paintBorders(VControl control, Event e) {
		switch ((CDT.PickerPart) control.getData(CDT.PickerPart)) {
		case ClearButton:
			paintClearButtonBorders(control, e);
			break;
		case DateNow:
			paintDateNowBorders(control, e);
			break;
		case DayButton:
			paintDayButtonBorders(control, e);
			break;
		case DayOfWeekLabel:
			paintDayOfWeekLabelBorders(control, e);
			break;
		case DayOfWeekPanel:
			paintDayOfWeekPanelBorders(control, e);
			break;
		case DayPanel:
			paintDayPanelBorders(control, e);
			break;
		case TodayButton:
			paintFooterButtonBorders(control, e);
			break;
		case FooterPanel:
			paintFooterPanelBorders(control, e);
			break;
		case HeaderPanel:
			paintHeaderPanelBorders(control, e);
			break;
		case MonthLabel:
			paintMonthLabelBorders(control, e);
			break;
		case MonthNext:
			paintMonthNextBorders(control, e);
			break;
		case MonthPrev:
			paintMonthPrevBorders(control, e);
			break;
		case YearLabel:
			paintYearLabelBorders(control, e);
			break;
		case YearNext:
			paintYearNextBorders(control, e);
			break;
		case YearPrev:
			paintYearPrevBorders(control, e);
			break;
		default:
			defaultPaintBorders(control, e);
			break;
		}
	}

	protected void paintClearButtonBackground(VControl control, Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintClearButtonBorders(VControl control, Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintClearButtonContent(VControl control, Event e) {
		defaultPaintContent(control, e);
	}

	@Override
	public final void paintContent(VControl control, Event e) {
		switch ((CDT.PickerPart) control.getData(CDT.PickerPart)) {
		case ClearButton:
			paintClearButtonContent(control, e);
			break;
		case DateNow:
			paintDateNowContent(control, e);
			break;
		case DayButton:
			paintDayButtonContent(control, e);
			break;
		case DayOfWeekLabel:
			paintDayOfWeekLabelContent(control, e);
			break;
		case DayOfWeekPanel:
			paintDayOfWeekPanelContent(control, e);
			break;
		case DayPanel:
			paintDayPanelContent(control, e);
			break;
		case TodayButton:
			paintFooterButtonContent(control, e);
			break;
		case FooterPanel:
			paintFooterPanelContent(control, e);
			break;
		case HeaderPanel:
			paintHeaderPanelContent(control, e);
			break;
		case MonthLabel:
			paintMonthLabelContent(control, e);
			break;
		case MonthNext:
			paintMonthNextContent(control, e);
			break;
		case MonthPrev:
			paintMonthPrevContent(control, e);
			break;
		case YearLabel:
			paintYearLabelContent(control, e);
			break;
		case YearNext:
			paintYearNextContent(control, e);
			break;
		case YearPrev:
			paintYearPrevContent(control, e);
			break;
		default:
			defaultPaintContent(control, e);
			break;
		}
	}

	protected void paintDateNowBackground(VControl control, Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintDateNowBorders(VControl control, Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintDateNowContent(VControl control, Event e) {
		defaultPaintContent(control, e);
	}

	protected void paintDayButtonBackground(VControl control, Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintDayButtonBorders(VControl control, Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintDayButtonContent(VControl control, Event e) {
		defaultPaintContent(control, e);
	}

	protected void paintDayOfWeekLabelBackground(VControl control, Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintDayOfWeekLabelBorders(VControl control, Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintDayOfWeekLabelContent(VControl control, Event e) {
		defaultPaintContent(control, e);
	}

	protected void paintDayOfWeekPanelBackground(VControl control, Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintDayOfWeekPanelBorders(VControl control, Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintDayOfWeekPanelContent(VControl control, Event e) {
		defaultPaintContent(control, e);
	}

	protected void paintDayPanelBackground(VControl control, Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintDayPanelBorders(VControl control, Event e) {
		defaultPaintBorders(control, e);
		Calendar cal = cdt.getCalendarInstance();
		VPanel picker = getPicker();
		if (picker instanceof DatePicker) {
			VButton[] days = ((DatePicker) picker).dayButtons;
			for (int i = 1; i < days.length; i++) {
				VButton day = days[i];
				cal.setTime(day.getData(CDT.Key.Date, Date.class));
				if (cal.get(Calendar.DAY_OF_MONTH) == 1 && !isActive(day)
						&& !isActive(days[i - 1])) {
					Rectangle bounds = day.getBounds();
					Rectangle pbounds = control.getBounds();
					if (indexOf(day) % 7 != 0) {
						e.gc.drawLine(bounds.x, bounds.y, bounds.x,
								bounds.y + bounds.height);
					}
					if (indexOf(day) > 7) {
						e.gc.drawLine(bounds.x, bounds.y,
								pbounds.x + pbounds.width, bounds.y);
					}
					e.gc.drawLine(pbounds.x, bounds.y + bounds.height, bounds.x,
							bounds.y + bounds.height);

					i += 28;
				}
			}
		}
	}

	protected void paintDayPanelContent(VControl control, Event e) {
		defaultPaintContent(control, e);
	}

	protected void paintFooterButtonBackground(VControl control, Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintFooterButtonBorders(VControl control, Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintFooterButtonContent(VControl control, Event e) {
		defaultPaintContent(control, e);
	}

	protected void paintFooterPanelBackground(VControl control, Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintFooterPanelBorders(VControl control, Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintFooterPanelContent(VControl control, Event e) {
		defaultPaintContent(control, e);
	}

	protected void paintHeaderPanelBackground(VControl control, Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintHeaderPanelBorders(VControl control, Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintHeaderPanelContent(VControl control, Event e) {
		defaultPaintContent(control, e);
	}

	protected void paintMonthLabelBackground(VControl control, Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintMonthLabelBorders(VControl control, Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintMonthLabelContent(VControl control, Event e) {
		defaultPaintContent(control, e);
	}

	protected void paintMonthNextBackground(VControl control, Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintMonthNextBorders(VControl control, Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintMonthNextContent(VControl control, Event e) {
		defaultPaintContent(control, e);
	}

	protected void paintMonthPrevBackground(VControl control, Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintMonthPrevBorders(VControl control, Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintMonthPrevContent(VControl control, Event e) {
		defaultPaintContent(control, e);
	}

	protected void paintYearLabelBackground(VControl control, Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintYearLabelBorders(VControl control, Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintYearLabelContent(VControl control, Event e) {
		defaultPaintContent(control, e);
	}

	protected void paintYearNextBackground(VControl control, Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintYearNextBorders(VControl control, Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintYearNextContent(VControl control, Event e) {
		defaultPaintContent(control, e);
	}

	protected void paintYearPrevBackground(VControl control, Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintYearPrevBorders(VControl control, Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintYearPrevContent(VControl control, Event e) {
		defaultPaintContent(control, e);
	}

	public final void setButtonPainter(IControlPainter painter) {
		this.buttonPainter = painter;
	}

	void setCDateTime(CDateTime cdt) {
		this.cdt = cdt;
	}

	public final void setLabelPainter(IControlPainter painter) {
		this.labelPainter = painter;
	}

	public final void update(VControl control) {
		if (control == null) {
			return;
		}
		switch ((CDT.PickerPart) control.getData(CDT.PickerPart)) {
		case ClearButton:
			updateClearButton(control);
			break;
		case DateNow:
			updateDateNow(control);
			break;
		case DayButton:
			updateDayButton(control);
			break;
		case DayOfWeekLabel:
			updateDayOfWeekLabel(control);
			break;
		case DayOfWeekPanel:
			updateDayOfWeekPanel(control);
			break;
		case DayPanel:
			updateDayPanel(control);
			break;
		case TodayButton:
			updateFooterButton(control);
			break;
		case FooterPanel:
			updateFooterPanel(control);
			break;
		case HeaderPanel:
			updateHeaderPanel(control);
			break;
		case MonthLabel:
			updateMonthLabel(control);
			break;
		case MonthNext:
			updateMonthNext(control);
			break;
		case MonthPrev:
			updateMonthPrev(control);
			break;
		case YearLabel:
			updateYearLabel(control);
			break;
		case YearNext:
			updateYearNext(control);
			break;
		case YearPrev:
			updateYearPrev(control);
			break;
		}
	}

	protected void updateClearButton(VControl control) {
	}

	protected void updateDateNow(VControl control) {
		control.setFill(control.getDisplay().getSystemColor(SWT.COLOR_GRAY));
	}

	protected void updateDayButton(VControl control) {
		if (isToday(control)) {
			final Color color = cdt.getPickerTodayColor();
			control.setForeground(color!=null?color:control.getDisplay().getSystemColor(SWT.COLOR_RED));
		} else if (isActive(control)) {
			final Color color = cdt.getPickerActiveDayColor();
			control.setForeground(color!=null?color:control.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		} else {
			final Color color = cdt.getPickerInactiveDayColor();
			control.setForeground(color!=null?color:control.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		}
	}

	protected void updateDayOfWeekLabel(VControl control) {
	}

	protected void updateDayOfWeekPanel(VControl control) {
		final Color color = cdt.getPickerBackgroundColor();
		control.setBackground(color!=null?color:control.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}

	protected void updateDayPanel(VControl control) {
		final Color color = cdt.getPickerBackgroundColor();
		control.setBackground(color!=null?color:control.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}

	protected void updateFooterButton(VControl control) {
	}

	protected void updateFooterPanel(VControl control) {
	}

	protected void updateHeaderPanel(VControl control) {
	}

	protected void updateMonthLabel(VControl control) {
	}

	protected void updateMonthNext(VControl control) {
		control.setFill(control.getDisplay().getSystemColor(SWT.COLOR_GRAY));
	}

	protected void updateMonthPrev(VControl control) {
		control.setFill(control.getDisplay().getSystemColor(SWT.COLOR_GRAY));
	}

	protected void updateYearLabel(VControl control) {
	}

	protected void updateYearNext(VControl control) {
		control.setFill(control.getDisplay().getSystemColor(SWT.COLOR_GRAY));
	}

	protected void updateYearPrev(VControl control) {
		control.setFill(control.getDisplay().getSystemColor(SWT.COLOR_GRAY));
	}

}
