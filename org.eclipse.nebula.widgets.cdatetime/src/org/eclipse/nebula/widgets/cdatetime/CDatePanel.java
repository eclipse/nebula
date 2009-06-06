/****************************************************************************
 * Copyright (c) 2008 Jeremy Dowdall
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/

package org.eclipse.nebula.widgets.cdatetime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.eclipse.nebula.cwt.v.VButton;
import org.eclipse.nebula.cwt.v.VControl;
import org.eclipse.nebula.cwt.v.VGridLayout;
import org.eclipse.nebula.cwt.v.VLabel;
import org.eclipse.nebula.cwt.v.VNative;
import org.eclipse.nebula.cwt.v.VPanel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class CDatePanel extends Composite {

	private VPanel panel;
	private VPanel header;
	private VPanel body;
	private List<VNative<CDateTime>> pickers;
	private int headerSize;
	private int pickerSize;

	private CDateTimeBuilder builder;
	private CDateTimePainter painter;

	private Locale locale;
	private TimeZone timezone;
	private Calendar calendar;

	private SelectionAdapter listener = new SelectionAdapter() {
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			CDateTime cdt = (CDateTime) e.widget;
			for(VNative<CDateTime> picker : pickers) {
				if(picker.getControl() != e.widget) {
					picker.getControl().setSelection(cdt.getSelection());
				}
			}
		}
	};

	public CDatePanel(Composite parent, int style) {
		super(parent, style);

		panel = new VPanel(this, SWT.NONE);
		
		locale = Locale.getDefault();
		try {
			timezone = TimeZone.getDefault();
		} catch(Exception e) {
			timezone = TimeZone.getTimeZone("GMT");
		}
		calendar = Calendar.getInstance(this.timezone, this.locale);
		calendar.setTime(new Date());

		builder = new CDateTimeBuilder();
		builder.setHeader(Header.Month().align(SWT.RIGHT, SWT.FILL, true).readOnly(), Header.Year().align(SWT.LEFT,
				SWT.FILL, true).readOnly());
		builder.setBody(Body.Days().compact());

		painter = new CDateTimePainter() {
			@Override
			protected void paintDayPanelBorders(VControl control, Event e) {
				Rectangle r = control.getBounds();
				e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
				e.gc.drawLine(r.x, r.y, r.x, r.y + r.height - 1);
				e.gc.drawLine(r.x + r.width - 1, r.y, r.x + r.width - 1, r.y + r.height - 1);
				e.gc.drawLine(r.x, r.y + r.height - 1, r.x + r.width - 1, r.y + r.height - 1);
			}
		};

		pickers = new ArrayList<VNative<CDateTime>>();
		pickerSize = -1;

		VGridLayout layout = new VGridLayout();
		layout.verticalSpacing = 2;
		panel.setLayout(layout);

		createHeader();

		VLabel sep = new VLabel(panel, SWT.HORIZONTAL | SWT.SEPARATOR);
		sep.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		body = new VPanel(panel, SWT.NONE);
		body.setLayout(new VGridLayout());
		body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		body.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				handleResize();
			}
		});
	}

	private void addMonth() {
		VNative<CDateTime> picker = VNative.create(CDateTime.class, body, CDT.MULTI | CDT.SIMPLE);
		picker.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		CDateTime cdt = picker.getControl();
		cdt.setBuilder(builder);
		cdt.setPainter(painter);
		cdt.setPattern("MMMM d yyyy");
		cdt.setLocale(locale);
		cdt.setTimeZone(timezone);
		cdt.setScrollable(false);
		cdt.addSelectionListener(listener);

		pickers.add(picker);

		updateMonths();
	}
	
	private void createHeader() {
		header = new VPanel(panel, SWT.NONE);
		VGridLayout layout = new VGridLayout(3, true);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		header.setLayout(layout);
		header.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

		VButton b = new VButton(header, SWT.ARROW | SWT.LEFT | SWT.NO_FOCUS);
		b.setFill(getDisplay().getSystemColor(SWT.COLOR_GRAY));
		b.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		b.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				calendar.add(Calendar.MONTH, -1);
				updateMonths();
			}
		});

		b = new VButton(header, SWT.PUSH | SWT.NO_FOCUS);
		b.setText("Today");
		b.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		b.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				calendar.setTimeInMillis(System.currentTimeMillis());
				updateMonths();
			}
		});

		b = new VButton(header, SWT.ARROW | SWT.RIGHT | SWT.NO_FOCUS);
		b.setFill(getDisplay().getSystemColor(SWT.COLOR_GRAY));
		b.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		b.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				calendar.add(Calendar.MONTH, 1);
				updateMonths();
			}
		});

		headerSize = header.computeSize(-1, -1).y;
	}

	public VPanel getPanel() {
		return panel;
	}

	private void handleResize() {
		if(pickerSize == -1) {
			setMonthCount(1);
			pickerSize = pickers.get(0).computeSize(-1, -1).y;
		}

		int height = getClientArea().height;
		int count = (int) Math.ceil((double) (height - headerSize) / pickerSize);

		setMonthCount(count);
	}

	private void removeMonth() {
		if(!pickers.isEmpty()) {
			VNative<CDateTime> picker = pickers.remove(pickers.size() - 1);
			picker.dispose();
		}
	}

	public void setMonthCount(int count) {
		while(count > pickers.size()) {
			addMonth();
		}
		while(count < pickers.size()) {
			removeMonth();
		}
	}

	private void updateMonths() {
		Calendar tmpcal = Calendar.getInstance(timezone, locale);
		tmpcal.setTime(calendar.getTime());
		for(VNative<CDateTime> picker : pickers) {
			picker.getControl().show(tmpcal.getTime());
			tmpcal.add(Calendar.MONTH, 1);
		}
	}

}
