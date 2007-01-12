package org.eclipse.swt.nebula.widgets.cdatetime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

class Picker_Clock_Discrete extends AbstractPicker {

	private CButton[] hours;
	private CButton[] minutes;
	private CButton[] am_pm;

	private boolean is24Hour;
	private boolean isHorizontal;
	


	Picker_Clock_Discrete(Composite parent1, CDateTime parent, Date selection) {
		super(parent1, parent, selection);
		isHorizontal = (parent.style & CDT.HORIZONTAL) != 0;
		selection = new Date();
	}

	protected void clearContents() {
		Control[] ca = getChildren();
		for(int i = 0; i < ca.length; i++) {
			ca[i].dispose();
			ca[i] = null;
		}
		hours = null;
		minutes = null;
		am_pm = null;
	}

	protected void createContents() {
		GridLayout layout = new GridLayout();
		layout.numColumns = isHorizontal ? (is24Hour ? 12 : 14) : (is24Hour ? 4 : 3);
		layout.makeColumnsEqualWidth = false;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		setLayout(layout);
		
		hours = new CButton[is24Hour ? 24 : 12];
		minutes = new CButton[12];
		am_pm = new CButton[is24Hour ? 0 : 2];
		if(isHorizontal) {
			createHorizontal();
		} else {
			createVertical();
		}
	}
	
	private void createHorizontal() {
		for(int i = 0; i < hours.length; i++) {
			hours[i] = new CButton(this, SWT.TOGGLE);
			hours[i].setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			hours[i].setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
			hours[i].addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
				}
				public void widgetSelected(SelectionEvent e) {
					for(int i = 0; i < hours.length; i++) {
						hours[i].setSelection(e.widget == hours[i].getButton());
						updateSelection();
					}
				}
			});

			if(!is24Hour) {
				if(i == 11) {
					Label lbl = new Label(this, SWT.SEPARATOR);
					GridData data = new GridData(SWT.FILL, SWT.FILL, false, false);
					data.verticalSpan = 3;
					lbl.setLayoutData(data);
					
					am_pm[0] = new CButton(this, SWT.TOGGLE);
					am_pm[0].setSquare(true);
					am_pm[0].setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
					am_pm[0].setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
					am_pm[0].addSelectionListener(new SelectionListener() {
						public void widgetDefaultSelected(SelectionEvent e) {
						}
						public void widgetSelected(SelectionEvent e) {
							for(int i = 0; i < am_pm.length; i++) {
								am_pm[i].setSelection(e.widget == am_pm[i].getButton());
								updateSelection();
							}
						}
					});
				}
			}
		}
		
		Label lbl = new Label(this, SWT.HORIZONTAL | SWT.SEPARATOR);
		GridData data = new GridData(SWT.FILL, SWT.CENTER, false, false);
		data.horizontalSpan = 12;
		lbl.setLayoutData(data);

		if(!is24Hour) {
			lbl = new Label(this, SWT.NONE);
			data = new GridData(SWT.FILL, SWT.CENTER, false, false);
			data.heightHint = 1;
			lbl.setLayoutData(data);
		}
		
		for(int i = 0; i < minutes.length; i++) {
			minutes[i] = new CButton(this, SWT.TOGGLE);
			minutes[i].setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			minutes[i].setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
			minutes[i].addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
				}
				public void widgetSelected(SelectionEvent e) {
					for(int i = 0; i < minutes.length; i++) {
						minutes[i].setSelection(e.widget == minutes[i].getButton());
						updateSelection();
					}
				}
			});
		}

		if(!is24Hour) {
			am_pm[1] = new CButton(this, SWT.TOGGLE);
			am_pm[1].setSquare(true);
			am_pm[1].setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			am_pm[1].setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
			am_pm[1].addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
				}
				public void widgetSelected(SelectionEvent e) {
					for(int i = 0; i < am_pm.length; i++) {
						am_pm[i].setSelection(e.widget == am_pm[i].getButton());
						updateSelection();
					}
				}
			});
		}
	}
	
	private void createVertical() {
		for(int i = 0; i < minutes.length; i++) {
			hours[i] = new CButton(this, SWT.TOGGLE);
			hours[i].setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			hours[i].setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
			hours[i].addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
				}
				public void widgetSelected(SelectionEvent e) {
					for(int i = 0; i < hours.length; i++) {
						hours[i].setSelection(e.widget == hours[i].getButton());
						updateSelection();
					}
				}
			});

			int j = i+12;
			if(j < hours.length) {
				hours[j] = new CButton(this, SWT.TOGGLE);
				hours[j].setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				hours[j].setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
				hours[j].addSelectionListener(new SelectionListener() {
					public void widgetDefaultSelected(SelectionEvent e) {
					}
					public void widgetSelected(SelectionEvent e) {
						for(int i = 0; i < hours.length; i++) {
							hours[i].setSelection(e.widget == hours[i].getButton());
							updateSelection();
						}
					}
				});
			}

			if(i == 0) {
				Label lbl = new Label(this, SWT.SEPARATOR);
				GridData data = new GridData(SWT.FILL, SWT.FILL, false, false);
				data.verticalSpan = 12;
				lbl.setLayoutData(data);
			}
			
			minutes[i] = new CButton(this, SWT.TOGGLE);
			minutes[i].setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			minutes[i].setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
			minutes[i].addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
				}
				public void widgetSelected(SelectionEvent e) {
					for(int i = 0; i < minutes.length; i++) {
						minutes[i].setSelection(e.widget == minutes[i].getButton());
						updateSelection();
					}
				}
			});
		}

		if(!is24Hour) {
			Label lbl = new Label(this, SWT.HORIZONTAL | SWT.SEPARATOR);
			GridData data = new GridData(SWT.FILL, SWT.FILL, false, false);
			data.horizontalSpan = 3;
			lbl.setLayoutData(data);
		}
		
		for(int i = 0; i < am_pm.length; i++) {
			am_pm[i] = new CButton(this, SWT.TOGGLE);
			am_pm[i].setSquare(true);
			am_pm[i].setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			am_pm[i].setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
			am_pm[i].addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
				}
				public void widgetSelected(SelectionEvent e) {
					for(int i = 0; i < am_pm.length; i++) {
						am_pm[i].setSelection(e.widget == am_pm[i].getButton());
						updateSelection();
					}
				}
			});

			if(i == 0) {
				new Label(this, SWT.NONE);
			}
		}
	}
	
	protected int[] getFields() {
		return new int[] { 
				Calendar.HOUR_OF_DAY,
				Calendar.MINUTE };
	}

	protected void setFields(int[] calendarFields) {
		super.setFields(calendarFields);
		if((combo.getStyle() & CDT.CLOCK_12_HOUR) != 0) {
			is24Hour = false;
		} else if((combo.getStyle() & CDT.CLOCK_24_HOUR) != 0) {
			is24Hour = true;
		} else {
			is24Hour = isSet(Calendar.HOUR_OF_DAY);
		}
		updateContents();
	}
	
	public boolean setFocus() {
		Calendar cal = Calendar.getInstance(combo.locale);
		cal.setTime(selection);
		int hour = cal.get(is24Hour ? Calendar.HOUR_OF_DAY : Calendar.HOUR);
		return hours[hour].setFocus();
	}

	protected void setSelection(Date date, int field, int notification) {
		clearButtons();
	}

	private void clearButtons() {
		for(int i = 0; i < hours.length; i++) {
			hours[i].setSelection(false);
		}
		for(int i = 0; i < minutes.length; i++) {
			minutes[i].setSelection(false);
		}
		for(int i = 0; i < am_pm.length; i++) {
			am_pm[i].setSelection(false);
		}
	}
	
//	void updateButtons() {
//		clearButtons();
//		Calendar cal = Calendar.getInstance(combo.locale);
//		cal.setTime(selection);
//		hours[cal.get(is24Hour ? Calendar.HOUR_OF_DAY : Calendar.HOUR)].setSelection(true);
//		minutes[cal.get(Calendar.MINUTE) / 5].setSelection(true);
//		if(!is24Hour) am_pm[cal.get(Calendar.AM_PM)].setSelection(true);
//	}

	protected void updateLabels() {
		SimpleDateFormat sdf = null;
		String pattern = ((SimpleDateFormat) SimpleDateFormat.getTimeInstance(DateFormat.SHORT, combo.locale)).toPattern();
		if(pattern.indexOf("HH") > -1) {
			sdf = new SimpleDateFormat("HH", combo.locale);
		} else if(pattern.indexOf("H") > -1) {
			sdf = new SimpleDateFormat("H", combo.locale);
		} else if(pattern.indexOf("hh") > -1) {
			sdf = new SimpleDateFormat("h", combo.locale);
		} else { //if(pattern.contains("h")) {
			sdf = new SimpleDateFormat("h", combo.locale);
		}
		Calendar cal = Calendar.getInstance(combo.locale);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		for(int i = 0; i < hours.length; i++) {
			hours[i].setText(sdf.format(cal.getTime()));
			cal.add(Calendar.HOUR_OF_DAY, 1);
		}

		sdf.applyPattern(":mm");
		for(int i = 0; i < minutes.length; i++) {
			minutes[i].setText(sdf.format(cal.getTime()));
			cal.add(Calendar.MINUTE, 5);
		}
		
		sdf.applyPattern("a");
		if(!is24Hour) {
			cal.set(Calendar.HOUR_OF_DAY, 1);
			am_pm[0].setText(sdf.format(cal.getTime()));
			cal.set(Calendar.HOUR_OF_DAY, 13);
			am_pm[1].setText(sdf.format(cal.getTime()));
		}
	}

	protected void updateNullSelection() {
		clearButtons();
	}
	
	protected void updateSelection() {
		Calendar cal = Calendar.getInstance(combo.locale);
		cal.setTime(selection);
		boolean hour_set = false;
		for(int i = 0; i < hours.length; i++) {
			if(hours[i].getSelection()) {
				cal.set(Calendar.HOUR_OF_DAY, i);
				hour_set = true;
				break;
			}
		}
		boolean min_set = false;
		for(int i = 0; i < minutes.length; i++) {
			if(minutes[i].getSelection()) {
				cal.set(Calendar.MINUTE, i*5);
				min_set = true;
				break;
			}
		}
		boolean ampm_set = is24Hour || am_pm[0].getSelection() || am_pm[1].getSelection();
		selection = cal.getTime();
		if(!is24Hour && am_pm[1].getSelection()) {
				cal.add(Calendar.HOUR_OF_DAY, 12);
		}
		selection = cal.getTime();
		combo.setSelectionFromPicker(-1, hour_set && min_set && ampm_set);
	}
}
