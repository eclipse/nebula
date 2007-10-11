package org.eclipse.nebula.widgets.cdatetime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;

class Picker_Clock_Analog extends AbstractPicker {

	private class BaseLayout extends Layout {
		protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
			Point size = dialComposite.computeSize(wHint, hHint, flushCache);
			if(!compact) {
				size.y += footerButton.computeSize(wHint, hHint, flushCache).y;
			} else if(hasSpinner) {
				int scw = spinner.getClientArea().width;
				if(scw == 0) {
					size.x += spinner.computeSize(SWT.DEFAULT, SWT.DEFAULT).x - 24;
				} else {
					size.x += spinner.computeSize(SWT.DEFAULT, SWT.DEFAULT).x - scw;
				}
			}
			return size;
		}
		protected void layout(Composite composite, boolean flushCache) {
			Rectangle r = composite.getClientArea();
			Point ssize = (compact && hasSpinner) ? 
					spinner.computeSize(SWT.DEFAULT, SWT.DEFAULT, flushCache) : new Point(0,0);
			Point fsize = compact ? new Point(0,0) : 
				footerButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, flushCache);
			int scw = 0;
			do {
				scw = (compact && hasSpinner) ? spinner.getClientArea().width : 0;
				int swidth = ssize.x - scw;
				int dwidth = r.width - swidth;
				int dheight = Math.min(dwidth, r.height-fsize.y);
				if(dheight < dwidth) dwidth = dheight;
				int dx = r.x+(r.width-dwidth-swidth)/2;
				int dy = r.y+(r.height-dheight-fsize.y)/2;
				dialComposite.setBounds(dx, dy, dwidth, dheight);
				if(compact && hasSpinner) spinner.setBounds(dx+dwidth-scw, dy+(dheight-ssize.y)/2, ssize.x, ssize.y);
			} while (compact && hasSpinner && scw == 0);
			
			if(!compact) {
				fsize.x += 10;
				Rectangle dc = dialComposite.getBounds();
				footerButton.setBounds(
						r.x+(r.width-fsize.x)/2+10,
						dc.y+dc.height, //r.y+r.height-fsize.y,
						fsize.x,
						fsize.y
						);
			} else if(hasSpinner){
				spinner.moveBelow(null);
			}
			
//			if(combo.isDropDown()) {
//				Point size = cancel.computeSize(-1, -1);
//				r = getClientArea();
//				int y = r.y+dialCenter.y-dialRadius-1;
//				cancel.setBounds(r.x+r.width-size.x-2, y, size.x, size.y);
//				accept.setBounds(r.x+r.width-(2*size.x+3), y, size.x, size.y);
//			}
		}
	}
	
	private class DialLayout extends Layout {
		protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
			return new Point(200,200); //calendarComposite.computeSize(wHint, hHint, flushCache);
		}
		protected void layout(Composite composite, boolean flushCache) {
			Rectangle r = composite.getClientArea();
			dialRadius = (Math.min(r.width, r.height) - 10) / 2;
			dialCenter.x = r.width / 2;
			dialCenter.y = r.height / 2;
			
			dialNow.setBounds(dialCenter.x-11, dialCenter.y-11, 22, 22);

			Point size = dialNow.getSize();
			dialAmPm.setBounds(
					dialCenter.x-(size.x/2),
					dialCenter.y+(dialRadius/3)-(size.y/2),
					size.x,
					size.y
					);
		}
	}
	
	private Composite dialComposite;
	private Spinner spinner;
	private CButton dialNow;
	private CButton dialAmPm;
	private CDateTime footerButton;
//	private CButton accept;
//	private CButton cancel;
	private int dialRadius;
	private Point dialCenter = new Point(0,0);

	private boolean setH = false;
	private boolean setM = false;
	private boolean setS = false;
	private boolean overHour = false;
	private boolean overMin = false;
	private boolean overSec = false;
	private boolean is24Hour;
	private boolean hourHand;
	private boolean minHand;
	private boolean secHand;
	private boolean am_pm;
	private boolean compact;
	private boolean hasSpinner;
	private int[] snap = { 1, 1 };
	private long increment = 300000; // 5 minutes

	SelectionListener footerListener = new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			setSelection(footerButton.getSelection(), -1, NOTIFY_REGULAR);
		}
	};

	Picker_Clock_Analog(Composite parent1, CDateTime parent, Date selection) {
		super(parent1, parent, selection);
		compact = (parent.style & CDT.COMPACT) != 0;
		hasSpinner = (parent.style & CDT.SPINNER) != 0;

		createContents();
	}
	
	protected void clearContents() {
	}
	
	protected void createContents() {
		setLayout(new BaseLayout());

		dialComposite = new Composite(this, SWT.DOUBLE_BUFFERED);
//		dialComposite.setBackgroundMode(SWT.INHERIT_FORCE);
		dialComposite.setLayout(new DialLayout());
		
		if(!compact) {
			footerButton = new CDateTime(this, (hasSpinner ? CDT.SPINNER : 0) | CDT.BORDER);
			footerButton.addSelectionListener(footerListener);
		} else if(hasSpinner) {
			spinner = new Spinner(this, SWT.VERTICAL);
			spinner.setMinimum(0);
			spinner.setMaximum(50);
			spinner.setDigits(1);
			spinner.setIncrement(1);
			spinner.setPageIncrement(1);
			spinner.setSelection(25);
			spinner.addFocusListener(new FocusAdapter() {
				public void focusGained(FocusEvent e) {
					setFocus();
				}
			});
			spinner.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					Date sel = new Date(getSelection().getTime() + (spinner.getSelection() > 25 ? increment : -increment));
					setSelection(sel, -1, NOTIFY_REGULAR);
					spinner.setSelection(25);
				}
			});
		}
		
		dialNow = new CButton(dialComposite, SWT.NONE);
//		dialNow.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		dialNow.setFillColor(getDisplay().getSystemColor(SWT.COLOR_GRAY));
		dialNow.setMargins(4, 4);
		dialNow.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		dialNow.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setSelection(new Date(), -1, NOTIFY_REGULAR);
			}
		});

		dialAmPm = new CButton(dialComposite, SWT.NONE);
//		dialAmPm.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		dialAmPm.setMargins(4, 4);
		Listener tapl = new Listener() {
			public void handleEvent(Event event) {
				Calendar tmpcal = Calendar.getInstance(combo.locale);
				tmpcal.setTime(getSelection());
				tmpcal.set(Calendar.AM_PM, (tmpcal.get(Calendar.AM_PM) == 0) ? 1 : 0);
				setSelection(tmpcal.getTime(), Calendar.AM_PM, NOTIFY_REGULAR);
			}
		};
		dialAmPm.addListener(SWT.Selection, tapl);
		dialAmPm.addListener(SWT.MouseWheel, tapl);

//		if(combo.isDropDown()) {
//			accept = new CButton(this, SWT.OK);
//			accept.moveAbove(null);
//			accept.setMargins(5, CDT.gtk ? 10 : 8);
//			accept.addSelectionListener(new SelectionAdapter() {
//				public void widgetSelected(SelectionEvent e) {
//					setSelection(getSelection(), -1, NOTIFY_DEFAULT);
//				}
//			});
//	
//			cancel = new CButton(this, SWT.CANCEL);
//			cancel.moveAbove(null);
//			cancel.setMargins(5, CDT.gtk ? 10 : 8);
//			cancel.addSelectionListener(new SelectionAdapter() {
//				public void widgetSelected(SelectionEvent e) {
//					combo.setOpen(false);
//				}
//			});
//		}
		
		final Listener listener = new Listener() {
			public void handleEvent(Event event) {
				switch(event.type) {
				case SWT.MouseDown:
					if(overHour) {
						setH = true;
					} else if(overMin) {
						setM = true;
					} else if(overSec) {
						setS = true;
					}
					if(setH || setM || setS) {
						dialComposite.setCursor(event.display.getSystemCursor(SWT.CURSOR_SIZEALL));
					}
					break;
				case SWT.MouseMove:
					Calendar tmpcal = Calendar.getInstance(combo.locale);
					tmpcal.setTime(getSelection());
					int dx = event.x - dialCenter.x;
					int dy = event.y - dialCenter.y;
					double val;
					if(dx == 0) {
						if(dy > 0) val = 30;
						else val = 0;
					} else if(dy == 0) {
						if(dx > 0) val = 15;
						else val = 45;
					} else {
						val = (30*Math.atan((double)dy/(double)dx) / Math.PI) + 15;
						if(dx < 0) 
							val += 30;
					}
					if(setH) {
						val = is24Hour ? val/2.5 : val/5;
						int v = (int) ((val) - (double) tmpcal.get(Calendar.MINUTE) / 60 + .5);
						if(is24Hour && v > 23) v = 23;
						if(!is24Hour && v > 11) v = 11;
						int field = is24Hour ? Calendar.HOUR_OF_DAY : Calendar.HOUR;
						tmpcal.set(field, v);
						setSelection(tmpcal.getTime(), field, NOTIFY_REGULAR);
					} else if(setM) {
						int v = (int) (val + 0.5);
						if(v > 59) v = 59;
						tmpcal.set(Calendar.MINUTE, v);
						setSelection(tmpcal.getTime(), Calendar.MINUTE, NOTIFY_REGULAR);
					} else if(setS) {
						int v = (int) (val + 0.5);
						if(v > 59) v = 59;
						tmpcal.set(Calendar.SECOND, v);
						setSelection(tmpcal.getTime(), Calendar.SECOND, NOTIFY_REGULAR);
					} else {
						boolean rd = false;
						if(overHour || overMin || overSec) rd = true;
						overHour = false;
						overMin = false;
						overSec = false;
						if(Math.sqrt(dx*dx + dy*dy) < dialRadius) {
							double h = (tmpcal.get(is24Hour ? Calendar.HOUR_OF_DAY : Calendar.HOUR) + 
									(double) tmpcal.get(Calendar.MINUTE) / 60) * (is24Hour ? 2.5 : 5);
							int m = tmpcal.get(Calendar.MINUTE);
							int s = tmpcal.get(Calendar.SECOND);
							if(hourHand && val-1 < h && h <= val+1) {
								overHour = true;
								rd = true;
							} else if(minHand && val-1 < m && m <= val+1) {
								overMin = true;
								rd = true;
							} else if(secHand && val-1 < s && s <= val+1) {
								overSec = true;
								rd = true;
							}
						}
						if(rd) dialComposite.redraw();
					}
					break;
				case SWT.MouseUp:
					setH = setM = setS = false;
					dialComposite.setCursor(event.display.getSystemCursor(SWT.CURSOR_ARROW));
					break;
				case SWT.MouseWheel:
					long time = getSelection().getTime();
					time += (event.count > 0) ? increment : -increment;
					setSelection(new Date(time), -1, NOTIFY_REGULAR);
					break;
				case SWT.Paint:
					Rectangle r = dialComposite.getClientArea();
					Image timage = new Image(event.display, r.width, r.height);
					event.gc.copyArea(timage, 0, 0);
					GC gc = new GC(timage);
					
					gc.setAntialias(SWT.ON);
					gc.setTextAntialias(SWT.ON);

					// Paint Clock "spinner handle"
					if(compact && hasSpinner) {
						gc.setBackground(event.display.getSystemColor(SWT.COLOR_GRAY));
						int y = spinner.getBounds().y + 2 - dialComposite.getLocation().y;
						int h = spinner.getBounds().height - 5;
						gc.fillRectangle(dialCenter.x, y, 2*dialRadius, h);
						gc.setForeground(event.display.getSystemColor(SWT.COLOR_BLACK));
						gc.drawRectangle(dialCenter.x, y, 2*dialRadius, h);
					}
					
					// Paint Clock Face
					int dia = 2 * dialRadius;
					int x = dialCenter.x - dialRadius;
					int y = dialCenter.y - dialRadius;
					gc.setBackground(event.display.getSystemColor(SWT.COLOR_GRAY));
					gc.fillOval(x, y, dia, dia);
					gc.setForeground(event.display.getSystemColor(SWT.COLOR_BLACK));
					gc.drawOval(x, y, dia, dia);
					gc.setBackground(event.display.getSystemColor(SWT.COLOR_WHITE));
					gc.fillOval(x+4, y+4, dia-8, dia-8);
					gc.drawOval(x+4, y+4, dia-8, dia-8);
					int inc = 36;
					for(int i = 0; i < inc; i++) {
						int x2 = x + dialRadius + (int)((dialRadius-10) * (Math.cos(2 * (double)i * Math.PI / inc)));
						int y2 = y + dialRadius + (int)((dialRadius-10) * (Math.sin(2 * (double)i * Math.PI / inc)));
						gc.drawLine(dialCenter.x, dialCenter.y, x2, y2);
					}
					gc.setBackground(event.display.getSystemColor(SWT.COLOR_WHITE));
					gc.fillOval(x+13, y+13, dia-26, dia-26);
					inc = 12;
					for(int i = 0; i < inc; i++) {
						int x2 = x + dialRadius + (int)((dialRadius-8) * (Math.cos(2 * (double)i * Math.PI / inc)));
						int y2 = y + dialRadius + (int)((dialRadius-8) * (Math.sin(2 * (double)i * Math.PI / inc)));
						gc.drawLine(dialCenter.x, dialCenter.y, x2, y2);
					}
					gc.fillOval(x+15, y+15, dia-30, dia-30);
					tmpcal = Calendar.getInstance(combo.locale);
					tmpcal.set(1, 1, 1, 0, 0, 0);
					SimpleDateFormat sdf = new SimpleDateFormat(is24Hour ? "H" : "h", combo.locale);
					gc.setForeground(event.display.getSystemColor(SWT.COLOR_DARK_BLUE));
					gc.setAlpha(200);
					inc = 12;
					for(int i = 0; i < inc; i++) {
						int x2 = x + dialRadius + (int)((dialRadius-25) * 
								(Math.cos(2 * (double)i * Math.PI / inc - Math.PI/2)));
						int y2 = y + dialRadius + (int)((dialRadius-25) * 
								(Math.sin(2 * (double)i * Math.PI / inc - Math.PI/2)));
						String str = sdf.format(tmpcal.getTime());
						Point ss = gc.stringExtent(str);
						gc.drawString(str, x2-(ss.x/2), y2-(ss.y/2));
						tmpcal.add(Calendar.HOUR_OF_DAY, is24Hour ? 2 : 1);
					}

					tmpcal.setTime(getSelection());

					// paint "AM_PM"
					if(am_pm) {
						sdf.applyPattern("a"); //$NON-NLS-1$
						String str = sdf.format(tmpcal.getTime());
						Point ss = gc.stringExtent(str);
						gc.setForeground(event.display.getSystemColor(SWT.COLOR_BLACK));
						gc.setAlpha(255);
						gc.drawString(str, x+dialRadius-(ss.x/2), y+dialRadius+(dialRadius/3)-(ss.y/2));
					}
					
					double i;
					int x2, y2;

					// paint Second Hand
					if(secHand) {
						if(overSec) {
							gc.setBackground(event.display.getSystemColor(SWT.COLOR_BLACK));
							gc.setForeground(event.display.getSystemColor(SWT.COLOR_BLACK));
							gc.setLineWidth(4);
							gc.setAlpha(255);
						} else {
							gc.setForeground(event.display.getSystemColor(SWT.COLOR_DARK_GRAY));
							gc.setLineWidth(2);
							gc.setAlpha(175);
						}
						inc = 60;
						i = tmpcal.get(Calendar.SECOND);
						x2 = x + dialRadius + (int)((dialRadius-10) * 
								(Math.cos(2 * i * Math.PI / inc - Math.PI/2)));
						y2 = y + dialRadius + (int)((dialRadius-10) * 
								(Math.sin(2 * i * Math.PI / inc - Math.PI/2)));
						gc.drawLine(dialCenter.x, dialCenter.y, x2, y2);
						if(overSec) gc.fillOval(x2-2, y2-2, 4, 4);
					}
					
					// paint Minute Hand
					if(minHand) {
						if(overMin) {
							gc.setBackground(event.display.getSystemColor(SWT.COLOR_BLACK));
							gc.setForeground(event.display.getSystemColor(SWT.COLOR_BLACK));
							gc.setLineWidth(4);
							gc.setAlpha(255);
						} else {
							gc.setForeground(event.display.getSystemColor(SWT.COLOR_DARK_BLUE));
							gc.setLineWidth(2);
							gc.setAlpha(200);
						}
						inc = 60;
						i = tmpcal.get(Calendar.MINUTE);
						x2 = x + dialRadius + (int)((dialRadius-17) * 
								(Math.cos(2 * i * Math.PI / inc - Math.PI/2)));
						y2 = y + dialRadius + (int)((dialRadius-17) * 
								(Math.sin(2 * i * Math.PI / inc - Math.PI/2)));
						gc.drawLine(dialCenter.x, dialCenter.y, x2, y2);
						if(overMin) gc.fillOval(x2-2, y2-2, 4, 4);
					}
					
					// paint Hour Hand
					if(hourHand) {
						if(overHour) {
							gc.setBackground(event.display.getSystemColor(SWT.COLOR_BLACK));
							gc.setForeground(event.display.getSystemColor(SWT.COLOR_BLACK));
							gc.setLineWidth(4);
						} else {
							gc.setForeground(event.display.getSystemColor(SWT.COLOR_DARK_BLUE));
							gc.setLineWidth(2);
						}
						gc.setAlpha(255);
						inc = is24Hour ? 24 : 12;
						i = tmpcal.get(is24Hour ? Calendar.HOUR_OF_DAY : Calendar.HOUR);
						i += (double) tmpcal.get(Calendar.MINUTE) / 60;
						x2 = x + dialRadius + (int)((dialRadius-35) * 
								(Math.cos(2 * i * Math.PI / inc - Math.PI/2)));
						y2 = y + dialRadius + (int)((dialRadius-35) * 
								(Math.sin(2 * i * Math.PI / inc - Math.PI/2)));
						gc.drawLine(dialCenter.x, dialCenter.y, x2, y2);
						if(overHour) gc.fillOval(x2-2, y2-2, 4, 4);
					}
					
					// Paint "Go-To-Current-Time" Button
					gc.setBackground(event.display.getSystemColor(SWT.COLOR_BLACK));
					gc.fillOval(dialCenter.x-5, dialCenter.y-5, 10, 10);

					gc.setBackground(event.display.getSystemColor(SWT.COLOR_GRAY));
					gc.fillOval(dialCenter.x-4, dialCenter.y-4, 8, 8);

					dialNow.setImage(timage);
//					accept.setImage(timage,
//							accept.getLocation().x + accept.getSize().x/2 - dialCenter.x - dialComposite.getLocation().x,
//							accept.getLocation().y + accept.getSize().y/2 - dialCenter.y - dialComposite.getLocation().y);
					if(am_pm) dialAmPm.setImage(timage, 0, dialAmPm.getLocation().y - dialCenter.y + dialAmPm.getSize().y/2);
					event.gc.drawImage(timage, 0, 0);
//					dialComposite.setBackgroundImage(timage);					
					gc.dispose();
					timage.dispose();
					break;
				default:
				}
			}
		};
		dialComposite.addListener(SWT.MouseDown, listener);
		dialComposite.addListener(SWT.MouseMove, listener);
		dialComposite.addListener(SWT.MouseUp, listener);
		dialComposite.addListener(SWT.MouseWheel, listener);
		dialComposite.addListener(SWT.Paint, listener);
	}
	
	protected int[] getFields() {
		return new int[] { 
				Calendar.HOUR_OF_DAY,
				Calendar.HOUR,
				Calendar.MINUTE,
				Calendar.SECOND,
				Calendar.AM_PM };
	}

	long getIncrement() {
		return increment;
	}

	Date getSelection() {
		return selection;
	}

	/**
	 * Get the snap intervals used when setting the minutes and seconds.
	 * @return an int[2] -> int[0] is the minutes snap, and int[1] is
	 * the seconds snap
	 * @see #setTimeSnap(int, int)
	 */
	int[] getSnap() {
		return snap;
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
		hourHand = isSet(Calendar.HOUR) || isSet(Calendar.HOUR_OF_DAY);
		minHand = isSet(Calendar.MINUTE);
		secHand = isSet(Calendar.SECOND);
		am_pm = !is24Hour && isSet(Calendar.AM_PM);
		dialAmPm.setVisible(am_pm);
		if(!compact) {
			String pattern = "";
			boolean sepOK = false;
			for(int i = 0; i < combo.pattern.length(); i++) {
				char c = combo.pattern.charAt(i);
				if("Hhmsa".indexOf(c) > -1) {
					pattern += c;
					sepOK = true;
				} else {
					if(sepOK && ":., ".indexOf(c) > -1) {
						pattern += c;
					}
					sepOK = false;
				}
			}
			footerButton.setPattern(pattern);
		}
		updateLabels();
	}
	
	public boolean setFocus() {
		return dialNow.setFocus();
	}

	void setIncrement(long millis) {
		increment = millis;
	}


	protected void setSelection(Date date, int field, int notification) {
		selection = snap(date);
		if(!compact) {
			footerButton.removeSelectionListener(footerListener);
			footerButton.setSelection(selection);
			footerButton.addSelectionListener(footerListener);
		}
		dialComposite.redraw();
		
		if (notification != NOTIFY_NONE) {
			combo.setSelectionFromPicker(-1, (notification == NOTIFY_DEFAULT));
		}
	}
	
	/**
	 * Set the snap for the minutes and seconds.  If the value given for 
	 * either parameter is less than or equal to zero then its corresponding 
	 * snap will be set to its default of one (1).
	 * @param min the snap interval for the minutes
	 * @param sec the snap interval for the seconds
	 * @see #getSnap()
	 */
	void setSnap(int min, int sec) {
		snap[0] = (min < 0) ? 1 : min;
		snap[1] = (sec < 0) ? 1 : sec;
//		setSelection(getSelection());
	}

	/**
	 * perform the snap and return a new "snapped" Date object
	 */
	private Date snap(Date date) {
		Calendar tmpcal = Calendar.getInstance(combo.locale);
		tmpcal.setTime(date);

		int v = tmpcal.get(Calendar.MINUTE);
		int m = v % snap[0];
		if(m != 0) {
			v += (m > snap[0]/2) ? (snap[0]-m) : -m;
			if(v > 59) v = 0;
			tmpcal.set(Calendar.MINUTE, v);
		}
		
		v = tmpcal.get(Calendar.SECOND);
		m = v % snap[1];
		if(m != 0) {
			v += (m > snap[1]/2) ? (snap[1]-m) : -m;
			if(v > 59) v = 0;
			tmpcal.set(Calendar.SECOND, v);
		}

		return tmpcal.getTime();
	}


	protected void updateLabels() {
		dialNow.setToolTipText(Messages.getString("nav_current_time", combo.locale));//$NON-NLS-1$
//		if(combo.isDropDown()) {
//			accept.setToolTipText(Messages.getString("accept", combo.locale));//$NON-NLS-1$
//			cancel.setToolTipText(Messages.getString("cancel", combo.locale));//$NON-NLS-1$
//		}
	}

	protected void updateNullSelection() {
		// TODO Auto-generated method stub
	}
	
	protected void updateSelection() {
		// TODO Auto-generated method stub
	}
}