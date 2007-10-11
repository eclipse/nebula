/****************************************************************************
* Copyright (c) 2004-2006 Jeremy Dowdall
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
*****************************************************************************/

package org.eclipse.nebula.widgets.cdatetime;

import java.awt.ComponentOrientation;
import java.text.BreakIterator;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;


class Picker_Calendar extends AbstractPicker {
	
	private static final String DATE = "date";
	private static final String DAY 	= "day";
	private static final int DAYS_IN_WEEK = 7;
	private static final int NUM_ROWS = 6;

	private static Calendar tmpcal;

	private Composite header;
	private CButton monthLabel;
	private CButton monthPrev;
	private CButton calendarNow;
	private CButton monthNext;
	private Composite yearComposite;
	private CButton yearEditAccept;
	private CButton yearEditCancel;
	private Text yearText;
	private CButton yearLabel;
	private CButton yearPrev;
	private CButton yearNext;
	private Composite body;
	private Label dayLabels[];
	private CButton dayButtons[];
	private CButton footerButton;
	private MenuItem bodyItem;
	private MenuItem[] monthItems = new MenuItem[12];
	private MenuItem[] yearItems = new MenuItem[11];

	private int selDayButton;

	private boolean compact;
	private boolean editYear = false;

	boolean year, month, day;

	/**
	 * Constructs a new instance of this class given its parent, a style value 
	 * describing its behavior and appearance, a date to which the initial selection
	 * will be set, and the locale to use.
	 * @param parent a widget which will be the parent of the new instance (cannot be null)
	 * @param style the style of widget to construct
	 * @param date a Date object representing the initial selection
	 * @param locale the locale which this CDateTime is to use
	 */
	Picker_Calendar(Composite parent1, CDateTime parent, Date selection) {
		super(parent1, parent, selection);
		compact = (parent.style & CDT.COMPACT) != 0;
		tmpcal = Calendar.getInstance(combo.locale);

		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		setLayout(layout);

		setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
	}

	/**
	 * Modifies the given Calendar field by the given amount for every dayButton.<br/>
	 * calendar.add(CalendarField, amount)
	 * @param field Calendar Field
	 * @param amount adjustment to be added
	 */
	private void adjustDays(int field, int amount) {
		for(int day = 0; day < dayButtons.length; day++) {
			tmpcal.setTime((Date) dayButtons[day].getData(DATE)); 
			tmpcal.add(field, amount);
			dayButtons[day].setData(DATE, tmpcal.getTime());
		}
		selection = (Date) dayButtons[selDayButton].getData(DATE);
	}

	protected void clearContents() {
	}

	/**
	 * create the Calendar's body, which contains the dayLabels and dayButtons
	 */
	private void createBody() {
		body = new Composite(this, SWT.NONE);
		body.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridLayout layout = new GridLayout(7, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.marginBottom = 1;
		layout.horizontalSpacing = 1;
		layout.verticalSpacing = 1;
		body.setLayout(layout);
		body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		body.addListener(SWT.MouseWheel, new Listener() {
			public void handleEvent(Event event) {
				if(CDT.win32) {
					Point pt = getDisplay().getCursorLocation();
					if(monthLabel.getClientArea().contains(monthLabel.toControl(pt))) {
						tmpcal.setTime(selection);
						tmpcal.add(Calendar.MONTH, (event.count > 0) ? 1 : -1);
						setSelection(tmpcal.getTime(), true, NOTIFY_REGULAR);
					} else if(yearLabel.getClientArea().contains(yearLabel.toControl(pt))) {
						tmpcal.setTime(selection);
						tmpcal.add(Calendar.YEAR, (event.count > 0) ? 1 : -1);
						setSelection(tmpcal.getTime(), true, NOTIFY_REGULAR);
					} else {
						scrollCalendar((event.count > 0) ? SWT.ARROW_UP : SWT.ARROW_DOWN);
					}
				} else {
					scrollCalendar((event.count > 0) ? SWT.ARROW_UP : SWT.ARROW_DOWN);
				}
			}
		});
		body.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.setBackground(dayLabels[0].getBackground());
				e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
				Rectangle rect = body.getClientArea();
//				if(!gridVisible) {
					rect.height = dayLabels[0].getSize().y;
//				}
				e.gc.fillRectangle(rect);
				int y = dayLabels[0].getSize().y;
				e.gc.drawLine(rect.x, y, rect.x+rect.width, y);
				if(!compact) {
					y = rect.y + body.getClientArea().height - 1;
					e.gc.drawLine(rect.x, y, rect.x+rect.width, y);
				}
			}
		});

		Menu bodyMenu = new Menu(body);
		bodyItem = new MenuItem(bodyMenu, SWT.NONE);
		bodyItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				tmpcal.setTime(new Date());
				setSelection(tmpcal.getTime(), true, NOTIFY_REGULAR);
			}
		});
		body.setMenu(bodyMenu);

		dayLabels = new Label[DAYS_IN_WEEK];
		for(int day = 0; day < dayLabels.length; day++) {
			dayLabels[day] = new Label(body, SWT.CENTER);
			dayLabels[day].setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			dayLabels[day].addMouseListener(new MouseAdapter() {
				public void mouseDown(MouseEvent e) {
					if(editYear) setEditYearMode(false, true);
				}
			});
		}
		
		dayButtons = new CButton[DAYS_IN_WEEK * NUM_ROWS];
		for(int day = 0; day < dayButtons.length; day++) {
			// TODO: clean up dayButton listeners
			CButton dayButton = new CButton(body, SWT.TOGGLE);
			dayButtons[day] = dayButton;
			dayButton.setBackground(body.getBackground());
			dayButton.setSquare(true);
			dayButton.setMargins(4, 4);
			dayButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			dayButton.setData(DAY, new Integer(day));
			dayButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					Button button = (Button) e.widget;
					setSelection(((Integer) button.getParent().getData(DAY)).intValue(), NOTIFY_DEFAULT);
				}
			});
			dayButton.getButton().addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					// block the arrow keys because they are handled by the traverse listener
					if((e.keyCode != SWT.ARROW_DOWN) && (e.keyCode != SWT.ARROW_UP)) {
						scrollCalendar(e.keyCode);
					}
				}
			});
			dayButton.getButton().addMouseListener(new MouseAdapter() {
				public void mouseDown(MouseEvent e) {
					if(e.button == 3) {
						body.getMenu().setVisible(true);
					}
				}
			});
			dayButton.getButton().addTraverseListener(new TraverseListener() {
				public void keyTraversed(TraverseEvent e) {
					if((e.keyCode == SWT.KEYPAD_CR) ||
							(e.keyCode == '\n') ||
							(e.keyCode == '\r')) {
						combo.setSelectionFromPicker(-1, true);
					}
					traverseSelection(e.keyCode);
				}
			});
		}
	}

	protected void createContents() {

		if(month || year) {
			createHeader();
		}

		if((month || year) && day) {
			Label separator = new Label(this, SWT.HORIZONTAL | SWT.SEPARATOR);
			separator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		}
		
		if(day) {
			createBody();
		}

		if(!compact) {
			createFooter();
		}

		if(year) {
//			yearEditControls = new ArrayList(CDC.getControls(yearComposite));
//			yearEditControls.addAll(CDC.getControls(yearLabel));
//			yearEditControls.addAll(CDC.getControls(yearEditAccept));
//			yearEditControls.addAll(CDC.getControls(yearEditCancel));
		}		
	}
	
	/**
	 * create the footer (footerButton) for the Calendar part of this CDateTime<br/>
	 * there is currently no footer for the Clock part - should there be?  or
	 * should this footer span both parts?
	 */
	private void createFooter() {
		footerButton = new CButton(this, SWT.CENTER);
		GridData data = new GridData(SWT.FILL, SWT.CENTER, false, false);
		footerButton.setLayoutData(data);
		footerButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setSelection(new Date(), true, NOTIFY_DEFAULT);
			}
		});
	}

	/**
	 * create the header for the Calendar part of this CDateTime<br/>
	 * there is no equivalent for the Clock part
	 */
	private void createHeader() {
		header = new Composite(this, SWT.NONE);
		GridLayout layout = new GridLayout(6, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 2;
		layout.marginWidth = 2;
		header.setLayout(layout);
		header.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		if(month) {
			monthPrev = new CButton(header, SWT.ARROW | SWT.LEFT, getDisplay().getSystemColor(SWT.COLOR_GRAY));
			monthPrev.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
			monthPrev.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					tmpcal.setTime(selection);
					tmpcal.add(Calendar.MONTH, -1);
					setSelection(tmpcal.getTime(), true, NOTIFY_REGULAR);
				}
			});
	
			if(compact) {
				calendarNow = new CButton(header, SWT.NONE);
				calendarNow.setMargins(4, 0);
				calendarNow.setPolygon(new int[] { 7,7 }, getDisplay().getSystemColor(SWT.COLOR_GRAY));
				calendarNow.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
				calendarNow.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						setSelection(new Date(), true, NOTIFY_REGULAR);
					}
				});
			}
			
			monthNext = new CButton(header, SWT.ARROW | SWT.RIGHT, getDisplay().getSystemColor(SWT.COLOR_GRAY));
			monthNext.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
			monthNext.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					tmpcal.setTime(selection);
					tmpcal.add(Calendar.MONTH, 1);
					setSelection(tmpcal.getTime(), true, NOTIFY_REGULAR);
				}
			});

			monthLabel = new CButton(header, SWT.NONE);
			monthLabel.setAlignment(compact ? SWT.RIGHT : SWT.LEFT, SWT.CENTER);
			GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
			data.horizontalIndent = 2;
			monthLabel.setLayoutData(data);
			if(CDT.gtk) {
				monthLabel.getButton().addListener(SWT.MouseWheel, new Listener() {
					public void handleEvent(Event event) {
						if(SWT.MouseWheel == event.type) {
							tmpcal.setTime(selection);
							tmpcal.add(Calendar.MONTH, (event.count > 0) ? 1 : -1);
							setSelection(tmpcal.getTime(), true, NOTIFY_REGULAR);
						}
					}
				});
			}
			monthLabel.getButton().addMouseListener(new MouseAdapter() {
				public void mouseDown(MouseEvent e) {
					monthLabel.getMenu().setVisible(true);
				}
			});
	
			Menu monthMenu = new Menu(monthLabel);
			for(int i = 0; i < 12; i++) {
				monthItems[i] = new MenuItem(monthMenu, SWT.NONE);
				tmpcal.set(Calendar.MONTH, i);
				monthItems[i].setData("Month", new Integer(tmpcal.get(Calendar.MONTH)));
				monthItems[i].addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						MenuItem item = (MenuItem) e.widget;
						tmpcal.setTime(selection);
						tmpcal.set(Calendar.MONTH, ((Integer) item.getData("Month")).intValue());
						setSelection(tmpcal.getTime(), true, NOTIFY_REGULAR);
					}
				});
			}
			monthLabel.setMenu(monthMenu);
		}
		
		if(year) {
			yearComposite = new Composite(header, SWT.NONE);
			layout = new GridLayout();
			layout.marginWidth = 2;
			layout.marginHeight = 2;
			yearComposite.setLayout(layout);
			GridData data = new GridData();
			data.exclude = true;
			yearComposite.setLayoutData(data);
			yearComposite.setVisible(false);
			yearComposite.addPaintListener(new PaintListener() {
				public void paintControl(PaintEvent e) {
					e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_GRAY));
					Rectangle r = yearComposite.getClientArea();
					e.gc.drawRectangle(r.x, r.y, r.width-1, r.height-1);
				}
			});
			
			yearText = new Text(yearComposite, SWT.CENTER | SWT.SINGLE);
			yearText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			yearText.addListener(SWT.MouseWheel, new Listener() {
				public void handleEvent(Event event) {
					if(SWT.MouseWheel == event.type) {
						setEditYearMode(false, true);
						tmpcal.setTime(selection);
						tmpcal.add(Calendar.YEAR, (event.count > 0) ? 1 : -1);
						setSelection(tmpcal.getTime(), true, NOTIFY_REGULAR);
					}
				}
			});
			yearText.addTraverseListener(new TraverseListener() {
				public void keyTraversed(TraverseEvent e) {
					if(e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
						setEditYearMode(false, false);
					} else if(e.keyCode == SWT.ESC) {
						setEditYearMode(false, true);
						e.doit = false;
					}
				}
			});
			header.addMouseListener(new MouseAdapter() {
				public void mouseDown(MouseEvent e) {
					if(editYear) setEditYearMode(false, true);
				}
			});
			yearText.addVerifyListener(new VerifyListener() {
				public void verifyText(VerifyEvent e) {
					if(e.text == null || e.text.length() == 0) {
						e.doit = !Character.isLetter(e.character);
					} else {
						try {
							Integer.parseInt(e.text);
						} catch (NumberFormatException nfe) {
							e.doit = false;
						}
					}
				}	
			});

			yearEditAccept = new CButton(header, SWT.OK);
			data = new GridData(SWT.FILL, SWT.FILL, false, false);
			data.horizontalIndent = 2;
			data.exclude = true;
			yearEditAccept.setLayoutData(data);
			yearEditAccept.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					setEditYearMode(false, false);
				}
			});
	
			yearEditCancel = new CButton(header, SWT.CANCEL);
			data = new GridData(SWT.FILL, SWT.FILL, false, false);
			data.exclude = true;
			yearEditCancel.setLayoutData(data);
			yearEditCancel.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					setEditYearMode(false, true);
				}
			});

			yearLabel = new CButton(header, SWT.RIGHT);
			data = new GridData(SWT.RIGHT, SWT.BOTTOM, month || !compact ? false : true, false);
			if(!month) data.horizontalSpan = 2;
			yearLabel.setLayoutData(data);
			if(CDT.gtk) {
				yearLabel.getButton().addListener(SWT.MouseWheel, new Listener() {
					public void handleEvent(Event event) {
						if(SWT.MouseWheel == event.type) {
							tmpcal.setTime(selection);
							tmpcal.add(Calendar.YEAR, (event.count > 0) ? 1 : -1);
							setSelection(tmpcal.getTime(), true, NOTIFY_REGULAR);
						}
					}
				});
			}
			yearLabel.getButton().addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					setEditYearMode(true, false);
				}
			});

			Menu yearMenu = new Menu(yearLabel.getButton());
			for(int i = 0; i < 11; i++) {
				yearItems[i] = new MenuItem(yearMenu, SWT.NONE);
				yearItems[i].setData("Year", new Integer(i));
				yearItems[i].addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						MenuItem item = (MenuItem) e.widget;
						tmpcal.setTime(selection);
						tmpcal.add(Calendar.YEAR, ((Integer) item.getData("Year")).intValue() - 5);
						setSelection(tmpcal.getTime(), true, NOTIFY_REGULAR);
					}
				});
			}
			yearLabel.getButton().setMenu(yearMenu);

			if(!compact || !month) {
				yearPrev = new CButton(header, SWT.ARROW | SWT.LEFT, getDisplay().getSystemColor(SWT.COLOR_GRAY));
				data = new GridData(SWT.FILL, SWT.FILL, false, false);
				yearPrev.setLayoutData(data);
				yearPrev.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						tmpcal.setTime(selection);
						tmpcal.add(Calendar.YEAR, -1);
						setSelection(tmpcal.getTime(), true, NOTIFY_REGULAR);
					}
				});
		
				if(!month) {
					calendarNow = new CButton(header, SWT.NONE);
					calendarNow.setMargins(4, 0);
					calendarNow.setPolygon(new int[] { 7,7 }, getDisplay().getSystemColor(SWT.COLOR_GRAY));
					calendarNow.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
					calendarNow.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							setSelection(new Date(), true, NOTIFY_REGULAR);
						}
					});
				}

				yearNext = new CButton(header, SWT.ARROW | SWT.RIGHT, getDisplay().getSystemColor(SWT.COLOR_GRAY));
				data = new GridData(SWT.FILL, SWT.FILL, false, false);
				yearNext.setLayoutData(data);
				yearNext.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						tmpcal.setTime(selection);
						tmpcal.add(Calendar.YEAR, 1);
						setSelection(tmpcal.getTime(), true, NOTIFY_REGULAR);
					}
				});
			}
		}
	}

	protected int[] getFields() {
		return new int[] { 
				Calendar.YEAR, 
				Calendar.MONTH, 
				Calendar.DAY_OF_MONTH };
	}
	
	/**
	 * Scroll the Calendar's visible days just as a user would with the keyboard.
	 * <dt><b>Valid Keys:</b></dt>
	 * <dd>SWT.ARROW_UP, SWT.ARROW_DOWN, SWT.END, SWT.HOME, SWT.PAGE_DOWN, SWT.PAGE_UP</dd>
	 * @param keycode a SWT keycode
	 * @see #traverseSelection(int)
	 */
	void scrollCalendar(int keycode) {
		scrollCalendar(keycode, false);
	}

	/**
	 * perform the scroll by making a call to  {@link #adjustDays(int, int)} with the
	 * <code>field</code> set to Calendar.DATE and the <code>amount</code> 
	 * corresponding to the keycode.
	 */
	private void scrollCalendar(int keycode, boolean notify) {
		boolean update = false;
		switch (keycode) {
		case SWT.ARROW_DOWN:
			adjustDays(Calendar.DATE, 7);
			update = true;
			break;
		case SWT.ARROW_UP:
			adjustDays(Calendar.DATE, -7);
			update = true;
			break;
		case SWT.END:
			adjustDays(Calendar.DATE, 52*7);
			update = true;
			break;
		case SWT.HOME:
			adjustDays(Calendar.DATE, -52*7);
			update = true;
			break;
		case SWT.PAGE_DOWN:
			adjustDays(Calendar.DATE, 4*7);
			update = true;
			break;
		case SWT.PAGE_UP:
			adjustDays(Calendar.DATE, -4*7);
			update = true;
			break;
		}
		
		if(update) {
			updateHeader();
			updateDays();
		}
	}
	
	/**
	 * Set the date for each dayButton by starting with the given <code>firstDate</code>
	 * and iterating over all the dayButtons, adding 1 day to the date with each iteration.<br>
	 * The date is stored in the dayButton with: setData(DATE, date).<br>
	 * If <code>alignMonth</code> is true, then the actual first date used will be modified
	 * to be the first date of the visible calendar which includes the given 
	 * <code>firstDate</code>
	 * @param firstDate the first date of the dayButtons
	 * @param alignMonth whether or not to align the month
	 */
	private void setDays(Date firstDate, boolean alignMonth) {
		tmpcal.setTime(firstDate);

		if(alignMonth) {
			tmpcal.set(Calendar.DATE, 1);
			int firstDay = tmpcal.get(Calendar.DAY_OF_WEEK) - tmpcal.getFirstDayOfWeek();
			if(firstDay < 0) {
				firstDay += 7;
			}
			tmpcal.add(Calendar.DATE, -firstDay);
		}
		
		for(int day = 0; day < dayButtons.length; day++) {
			dayButtons[day].setData(DATE, tmpcal.getTime());
			tmpcal.add(Calendar.DATE, 1);
		}
		
		selection = (Date) dayButtons[selDayButton].getData(DATE);
	}

	/**
	 * Set the EditYearMode on or off.  If setting it off, accept or cancel.
	 * @param edit enter edit mode if true, exit otherwise
	 * @param cancel if exiting edit mode, cancel the edit if true, accept otherwise; 
	 * has no affect if entering edit mode (<code>edit</code> is true)
	 */
	private void setEditYearMode(boolean edit, boolean cancel) {
		if(editYear == edit) return;
		if(edit) { // cancel has no meaning here
			yearText.setText(yearLabel.getText());

			Rectangle tBounds = yearLabel.getBounds();
			Point lSize = yearLabel.getSize();
			Point tSize = yearComposite.computeSize(-1, -1);
			tBounds.x = tBounds.x - ((tSize.x-lSize.x)/2);
			tBounds.y = tBounds.y - ((tSize.y-lSize.y)/2);
			tBounds.width = tSize.x;
			tBounds.height = tSize.y;
			
			yearComposite.setBounds(tBounds);

			yearLabel.setVisible(false);
			yearComposite.setVisible(true);
			yearText.selectAll();
			yearText.setFocus();

			tBounds.width = yearEditCancel.computeSize(-1, -1).x;
			tBounds.x -= tBounds.width;
			yearEditCancel.setBounds(tBounds);
			tBounds.width = yearEditAccept.computeSize(-1, -1).x;
			tBounds.x -= tBounds.width;
			yearEditAccept.setBounds(tBounds);
			if(month) monthLabel.setVisible(false);
			yearEditCancel.setVisible(true);
			yearEditAccept.setVisible(true);
		} else {
			if(cancel) {
				yearText.setText(yearLabel.getText());
			} else {
				tmpcal.setTime(selection);
				tmpcal.set(Calendar.YEAR, Integer.parseInt(yearText.getText()));
				setSelection(tmpcal.getTime(), true, NOTIFY_REGULAR);
			}
			if(month) monthLabel.setVisible(true);
			yearLabel.setVisible(true);
			yearComposite.setVisible(false);
			yearEditCancel.setVisible(false);
			yearEditAccept.setVisible(false);
			header.layout();
		}
		editYear = edit;
	}
	
	protected void setFields(int[] calendarFields) {
		super.setFields(calendarFields);
		year = isSet(Calendar.YEAR);
		month = isSet(Calendar.MONTH);
		day = true;//isSet(Calendar.DAY_OF_MONTH);
		updateContents();
		if(month) {
			monthLabel.addControlListener(new ControlAdapter() {
				public void controlResized(ControlEvent e) {
					setMonthLabelText();
				}
			});
		}
	}

	private void setMonthLabelText() {
		SimpleDateFormat sdf = new SimpleDateFormat("MMMM", combo.locale);
		String str = sdf.format(selection);
		GC gc = new GC(getDisplay());
		int width = monthLabel.getClientArea().width;
		if(width > 0 && gc.stringExtent(str).x >= width) {
			sdf.applyPattern("MMM");
			str = sdf.format(selection);
		}
		gc.dispose();
		monthLabel.setText(str);
	}
	
	public boolean setFocus() {
		if(day) return dayButtons[selDayButton].setFocus();
		if(month) return monthLabel.setFocus();
		if(year) return yearLabel.setFocus();
		return forceFocus();
	}

	/**
	 * if the selection is different from the current, then updates the days,
	 * figures out which button corresponds to the given date, and calls
	 * {@link #setSelection(int, int)} to set it.
	 */
	private void setSelection(Date date, boolean alignMonth, int notification) {
		Calendar cal = Calendar.getInstance(combo.locale);
		cal.setTime(date);
//		if(!selection.equals(cal.getTime())) {
			if(alignMonth || cal.before(selection) || cal.after(selection)) {
				setDays(cal.getTime(), true);
			}
			
			for(int i = 0; i < dayButtons.length; i++) {
				tmpcal.setTime((Date) dayButtons[i].getData(DATE));
				if((cal.get(Calendar.DATE) == tmpcal.get(Calendar.DATE)) &&
						(cal.get(Calendar.MONTH) == tmpcal.get(Calendar.MONTH)) &&
						(cal.get(Calendar.YEAR) == tmpcal.get(Calendar.YEAR)) ) {
					setSelection(i, notification);
					break;
				}
			}
			updateHeader();
			updateDays();
//		}
	}
	
	/**
	 * actually sets the given dayButton as selected and fires a selection event notification
	 */
	private void setSelection(int dayButton, int notification) {
		if(selDayButton != dayButton && dayButton >= 0 && dayButton < dayButtons.length) {
			int old = selDayButton;
			selDayButton = dayButton;
			dayButtons[old].setSelection(false);
			if(!dayButtons[selDayButton].getSelection()) {
				dayButtons[selDayButton].setSelection(true);
				dayButtons[selDayButton].setFocus();
			}
			selection = (Date) dayButtons[selDayButton].getData(DATE);
			tmpcal.setTime(selection);
			int year = tmpcal.get(Calendar.YEAR);
			int month = tmpcal.get(Calendar.MONTH);
			tmpcal.setTime((Date) dayButtons[old].getData(DATE));
			if(((tmpcal.get(Calendar.YEAR)  != year) ||
				(tmpcal.get(Calendar.MONTH) != month)) ) {
				updateHeader();
				updateDays();
			}
		}
		if (notification != NOTIFY_NONE) {
			combo.setSelectionFromPicker(Calendar.DATE, notification == NOTIFY_DEFAULT);
		}
	}
	
	/**
	 * Traverse the selection programmatically just as a user would with the keyboard.
	 * <dt><b>Valid Keys:</b></dt>
	 * <dd>SWT.ARROW_UP, SWT.ARROW_DOWN, SWT.ARROW_LEFT, SWT.ARROW_RIGHT</dd>
	 * @param keycode a SWT traversal keycode
	 * @see #scrollCalendar(int)
	 */
	void traverseSelection(int keycode) {
		traverseSelection(keycode, NOTIFY_REGULAR);
	}

	/**
	 * perform the traversal by calling the appropriate {@link #setSelection(int, int)}
	 * or {@link #scrollCalendar(int)} method.
	 */
	private void traverseSelection(int keycode, int src) {
		switch (keycode) {
		case SWT.ARROW_UP:
			if(selDayButton > DAYS_IN_WEEK) {
				setSelection(selDayButton - DAYS_IN_WEEK, src);
			} else {
				scrollCalendar(SWT.ARROW_UP);
			}
			break;
		case SWT.ARROW_DOWN:
			if(selDayButton < DAYS_IN_WEEK * (NUM_ROWS-1)) {
				setSelection(selDayButton + DAYS_IN_WEEK, src);
			} else {
				scrollCalendar(SWT.ARROW_DOWN);
			}
			break;
		case SWT.ARROW_LEFT:
			if(selDayButton > 0) {
				setSelection(selDayButton - 1, src);
			} else {
				scrollCalendar(SWT.ARROW_UP);
				setSelection(selDayButton + (DAYS_IN_WEEK-1), src);
			}
			break;
		case SWT.ARROW_RIGHT:
			if(selDayButton < (DAYS_IN_WEEK * NUM_ROWS - 1)) {
				setSelection(selDayButton + 1, src);
			} else {
				scrollCalendar(SWT.ARROW_DOWN);
				setSelection(selDayButton - (DAYS_IN_WEEK-1), src);
			}
		}
	}
	
	/**
	 * set / update the text of the displayLabels.  these are the Week 
	 * column headers above the days on the Calendar part of the <code>CDateTime</code>.
	 */
	private void updateDayLabels() {
		tmpcal = Calendar.getInstance(combo.locale);
		SimpleDateFormat sdf = new SimpleDateFormat("E", combo.locale);
		tmpcal.set(Calendar.DAY_OF_WEEK, tmpcal.getFirstDayOfWeek());
		boolean ltr = (ComponentOrientation.getOrientation(combo.locale).isLeftToRight() &&
				!combo.locale.getLanguage().equals("zh"));
		BreakIterator iterator = BreakIterator.getCharacterInstance(combo.locale);
		for(int x = 0; x < dayLabels.length; x++) {
			String str = sdf.format(tmpcal.getTime());
			if(compact) {
				iterator.setText(str);
				int start, end;
				if(ltr) {
					start = iterator.first();
					end = iterator.next();
				} else {
					end = iterator.last();
					start = iterator.previous();
				}
				dayLabels[x].setText(str.substring(start, end));
			} else {
				dayLabels[x].setText(str);
			}
			tmpcal.add(Calendar.DAY_OF_WEEK, 1);
		}
	}

	/**
	 * set / update the text and font color of the <code>dayButton</code>s.
	 * currently done in an async runnable because it can be a costly operation.
	 * TODO: udpateDays() should to be redone.
	 */
	private void updateDays() {
//		getDisplay().asyncExec(new Runnable() {
//			public void run() {
//				if(isDisposed()) return;
				CButton dayButton;
				SimpleDateFormat sdf = new SimpleDateFormat("d", combo.locale);
				Calendar today = Calendar.getInstance(combo.locale);
				tmpcal.setTime(selection);
				int year = tmpcal.get(Calendar.YEAR);
				int month = tmpcal.get(Calendar.MONTH);
				for(int day = 0; day < dayButtons.length; day++) {
					if((dayButtons[day] != null) && !dayButtons[day].isDisposed()) {
						dayButton = dayButtons[day];
						
						// get date
						tmpcal.setTime((Date) dayButton.getData(DATE));
	
						// set font color
						if((tmpcal.get(Calendar.YEAR) == today.get(Calendar.YEAR)) && 
								(tmpcal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))) {
							dayButton.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
						} else if(!((tmpcal.get(Calendar.YEAR) == year) &&
									(tmpcal.get(Calendar.MONTH) == month)) ) {
							dayButton.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
						} else {
							dayButton.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
						}
						
						// set label
						dayButton.setText(sdf.format(tmpcal.getTime()));
					}
				}
//			}
//		});
	}
	
	/**
	 * set / update the text of the <code>footerButton</code>.
	 */
	private void updateFooter() {
		if(footerButton != null && !footerButton.isDisposed()) {
			Calendar cal = Calendar.getInstance(combo.locale);
			Object[] margs = {
					cal.getTime(),
					Messages.getString("date_ordinal_" + cal.get(Calendar.DATE), combo.locale)
					};
			MessageFormat formatter = new MessageFormat(Messages.getString("label_today", combo.locale), combo.locale);//$NON-NLS-1$
			footerButton.setText(formatter.format(margs));
			footerButton.getParent().layout();
		}
	}

	/**
	 * set / update the text of the header - <code>monthLabel</code>, <code>yearLabel</code>,
	 * and the <code>monthLabel</code> context menu.
	 */
	private void updateHeader() {
		if(header != null && !header.isDisposed()) {
			SimpleDateFormat sdf = new SimpleDateFormat("MMMM", combo.locale);//$NON-NLS-1$
	
			if(month) {
				for(int i = 0; i < 12; i++) {
					tmpcal.set(Calendar.MONTH, i);
					monthItems[i].setText(sdf.format(tmpcal.getTime()));
					monthItems[i].setData("Month", new Integer(tmpcal.get(Calendar.MONTH)));//$NON-NLS-1$
				}
				setMonthLabelText();
//				String str = sdf.format(selection);
//				GC gc = new GC(getDisplay());
//				if(gc.stringExtent(str).x >= monthLabel.getClientArea().width) {
//					sdf.applyPattern("MMM");
//					str = sdf.format(selection);
//				}
//				gc.dispose();
//				monthLabel.setText(str);
			}
			
			if(year) {
				sdf.applyPattern("yyyy");//$NON-NLS-1$
				tmpcal.setTime(selection);
				tmpcal.add(Calendar.YEAR, -5);
				for(int i = 0; i < 11; i++) {
					yearItems[i].setText(sdf.format(tmpcal.getTime()));
					tmpcal.add(Calendar.YEAR, 1);
				}
				yearLabel.setText(sdf.format(selection));
			}
			
			header.layout();
		}
	}
	
	protected void updateLabels() {
//		setSelection(selection);
		updateLocale();
//		setDays(selection, true);
//		updateDays();
		updateHeader();
		updateDayLabels();
		updateFooter();
	}

	/**
	 * set / update, or calls methods to set / update, all components affected by the <code>locale</code>
	 * @see updateHeader();
	 * @see updateDayLabels();
	 * @see updateDays();
	 * @see updateFooter();
	 */
	private void updateLocale() {
		tmpcal = Calendar.getInstance(combo.locale);
		
		if(month) {
			monthPrev.setToolTipText(Messages.getString("nav_prev_month", combo.locale));//$NON-NLS-1$
			monthNext.setToolTipText(Messages.getString("nav_next_month", combo.locale));//$NON-NLS-1$
		}
		if((compact && (month || year)) || (!compact && !month && year)) {
			calendarNow.setToolTipText(Messages.getString("nav_current_day", combo.locale));//$NON-NLS-1$
		}
		if(year) {
			if(!compact) {
				yearPrev.setToolTipText(Messages.getString("nav_prev_year", combo.locale));//$NON-NLS-1$
				yearNext.setToolTipText(Messages.getString("nav_next_year", combo.locale));//$NON-NLS-1$
			}		
			yearEditAccept.setToolTipText(Messages.getString("accept", combo.locale));//$NON-NLS-1$
			yearEditCancel.setToolTipText(Messages.getString("cancel", combo.locale));//$NON-NLS-1$
		}
		if(!compact) {
			footerButton.setToolTipText(Messages.getString("nav_current_day", combo.locale));//$NON-NLS-1$
		}
		
		if(day) {
			bodyItem.setText(Messages.getString("nav_current_day", combo.locale));//$NON-NLS-1$
		}
	}

	protected void updateNullSelection() {
		dayButtons[selDayButton].setSelection(false);
	}
	
	protected void updateSelection() {
		setSelection(selection, true, NOTIFY_NONE);
	}
}