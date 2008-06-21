/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.calendarcombo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

/**
 * <b>CalendarCombo - SWT Widget - 2005-2008. Version 1.0. &copy; Emil Crumhorn - emil dot crumhorn at gmail dot com.</b>
 * <p>
 * <b>Website</b><br>
 * If you want more info or more documentation, please visit: <a href="http://www.hexapixel.com/">http://www.hexapixel.com</a>
 * <p>
 * <b>Description</b><br>
 * CalendarCombo is a widget that opens a calendar when dropped down. The calendar is modelled after Microsoft Outlook's calendar widget and acts and behaves exactly the same (and
 * it is also theme based). The combo is not based on CCombo (as many other custom implementations), but is instead attached to the native Combo box.
 * <p>
 * <b>Example Creation Code</b><br>
 * <code>
 * CalendarCombo cCombo = new CalendarCombo(parentControl, SWT.READ_ONLY);<br>
 * ...<br>
 * </code>
 * <p>
 * <b>Another example using depending combos and date range selection on the first combo</b><br>
 * <code>
 * CalendarCombo cComboStart = new CalendarCombo(parentControl, SWT.READ_ONLY, true);<br>
 * CalendarCombo cComboEnd = new CalendarCombo(parentControl, SWT.READ_ONLY);<br>
 * cComboStart.setDependingCombo(cComboEnd);<br>
 * </code> <br>
 * This will cause the end date for the date range selection to be populated in the cComboEnd combo.
 * <p>
 * <b>Customizing</b><br>
 * There are two interfaces that are of importance for customizing, one is IColorManager and the other is ISettings. Let's start with the IColorManager.
 * <p>
 * <b>IColorManager</b><br>
 * If you don't specify a color manager, the DefaultColorManager will be used. The color manager's job is to return colors to the method that is painting all the actual days and
 * months etc. The colors that are returned from the ColorManager will determine everything as far as looks go.
 * <p>
 * <b>ISettings</b><br>
 * To control the spacing between dates, various formats and text values, you will want to implement the ISettings interface. If you don't specify one, DefaultSettings will be
 * used.
 * 
 * @author Emil Crumhorn
 * @version 1.1
 * 
 */
public class CalendarCombo extends Composite {

	// the main combo box
	private Combo					mCombo;

	private int						mComboStyle			= SWT.NONE;

	// the shell holding the CalendarComposite
	private Shell					mCalendarShell;

	private Listener				mKillListener;

	private Listener				mFilterListenerFocusIn;

	private Composite				mParentComposite;

	// values for determining when the last view of the mCalendarShell was.
	// this is to determine how quick clicks should behave
	private long					mLastShowRequest	= 0;

	private long					mLastKillRequest	= 0;

	private boolean					mAllowTextEntry;

	private CalendarCombo			mDependingCombo;

	private CalendarComposite		mCalendarComposite;

	private Calendar				mStartDate;

	private Calendar				mEndDate;

	private IColorManager			mColorManager;

	private ISettings				mSettings;

	private ArrayList				mListeners;

	private Calendar				mDisallowBeforeDate;

	private Calendar				mDisallowAfterDate;

	private boolean					isReadOnly;

	private int						arrowButtonWidth;

	private boolean					mAllowDateRange;

	private Calendar				mCarbonPrePopupDate;

	protected static final boolean	OS_CARBON			= "carbon".equals(SWT.getPlatform());
	protected static final boolean	OS_GTK				= "gtk".equals(SWT.getPlatform());
	protected static final boolean	OS_WINDOWS			= "win32".equals(SWT.getPlatform());

	/*
	 * // Windows JNI Code for making a non-activated canvas, for reference if searching MSDN int extStyle = OS.GetWindowLong(canvas.handle, OS.GWL_EXSTYLE); extStyle = extStyle |
	 * 0x80000; OS.SetWindowLong(canvas.handle, OS.GWL_EXSTYLE, extStyle); 0x008000000 = WS_EX_NOACTIVATE;
	 */

	/**
	 * Creates a new calendar combo box with the given style.
	 * 
	 * @param parent Parent control
	 * @param style Combo style
	 */
	public CalendarCombo(Composite parent, int style) {
		super(parent, checkStyle(style));
		this.mComboStyle = style;
		this.mParentComposite = parent;
		init();
	}

	/**
	 * Creates a new calendar combo box with the given style.
	 * 
	 * @param parent Parent control
	 * @param style Combo style
	 * @param allowDateRange Whether to allow date range selection (note that if there is no depending CalendarCombo set you will have to deal with the date range event yourself).
	 */
	public CalendarCombo(Composite parent, int style, boolean allowDateRange) {
		super(parent, checkStyle(style));
		this.mComboStyle = style;
		this.mParentComposite = parent;
		this.mAllowDateRange = allowDateRange;
		init();
	}

	/**
	 * Creates a new calendar mCombo box with the given style, ISettings and IColorManager implementations.
	 * 
	 * @param parent Parent control
	 * @param style Combo style
	 * @param settings ISettings implementation
	 * @param colorManager IColorManager implementation
	 */
	public CalendarCombo(Composite parent, int style, ISettings settings, IColorManager colorManager) {
		super(parent, checkStyle(style));
		this.mComboStyle = style;
		this.mParentComposite = parent;
		this.mSettings = settings;
		this.mColorManager = colorManager;
		init();
	}

	/**
	 * Creates a new calendar mCombo box with the given style, ISettings and IColorManager implementations.
	 * 
	 * @param parent Parent control
	 * @param style Combo style
	 * @param settings ISettings implementation
	 * @param colorManager IColorManager implementation
	 * @param allowDateRange Whether to allow date range selection (note that if there is no depending CalendarCombo set you will have to deal with the date range event yourself).
	 */
	public CalendarCombo(Composite parent, int style, ISettings settings, IColorManager colorManager, boolean allowDateRange) {
		super(parent, checkStyle(style));
		this.mComboStyle = style;
		this.mParentComposite = parent;
		this.mSettings = settings;
		this.mColorManager = colorManager;
		this.mAllowDateRange = allowDateRange;
		init();
	}

	/**
	 * Lets you create a depending CalendarCombo box, which, when no dates are set on the current one will set the starting date when popped up to be the date of the pullDateFrom
	 * CalendarCombo, should that box have a date set in it.
	 * 
	 * @param parent
	 * @param style
	 * @param dependingCombo CalendarCombo from where start date is pulled when no date has been set locally.
	 */
	public CalendarCombo(Composite parent, int style, CalendarCombo dependingCombo) {
		super(parent, checkStyle(style));
		this.mParentComposite = parent;
		this.mDependingCombo = dependingCombo;
		this.mComboStyle = style;
		init();
	}

	/**
	 * Lets you create a depending CalendarCombo box, which, when no dates are set on the current one will set the starting date when popped up to be the date of the pullDateFrom
	 * CalendarCombo, should that box have a date set in it.
	 * 
	 * When the depending combo is set and the allowDateRange flag is true, the depending combo will be the recipient of the end date of any date range selection.
	 * 
	 * @param parent
	 * @param style
	 * @param dependingCombo CalendarCombo from where start date is pulled when no date has been set locally.
	 * @param allowDateRange Whether to allow date range selection
	 */
	public CalendarCombo(Composite parent, int style, CalendarCombo dependingCombo, boolean allowDateRange) {
		super(parent, checkStyle(style));
		this.mParentComposite = parent;
		this.mDependingCombo = dependingCombo;
		this.mComboStyle = style;
		this.mAllowDateRange = allowDateRange;
		init();
	}

	/**
	 * Lets you create a depending CalendarCombo box, which, when no dates are set on the current one will set the starting date when popped up to be the date of the pullDateFrom
	 * CalendarCombo, should that box have a date set in it.
	 * 
	 * @param parent
	 * @param style
	 * @param dependingCombo CalendarCombo from where start date is pulled when no date has been set locally.
	 * @param settings ISettings implementation
	 * @param colorManager IColorManager implementation
	 */
	public CalendarCombo(Composite parent, int style, CalendarCombo dependingCombo, ISettings settings, IColorManager colorManager) {
		super(parent, checkStyle(style));
		this.mParentComposite = parent;
		this.mDependingCombo = dependingCombo;
		this.mComboStyle = style;
		this.mSettings = settings;
		this.mColorManager = colorManager;
		init();
	}

	/**
	 * Lets you create a depending CalendarCombo box, which, when no dates are set on the current one will set the starting date when popped up to be the date of the pullDateFrom
	 * CalendarCombo, should that box have a date set in it.
	 * 
	 * When the depending combo is set and the allowDateRange flag is true, the depending combo will be the recipient of the end date of any date range selection.
	 * 
	 * @param parent
	 * @param style
	 * @param dependingCombo CalendarCombo from where start date is pulled when no date has been set locally.
	 * @param settings ISettings implementation
	 * @param colorManager IColorManager implementation
	 * @param allowDateRange Whether to allow date range selection
	 */
	public CalendarCombo(Composite parent, int style, CalendarCombo dependingCombo, ISettings settings, IColorManager colorManager, boolean allowDateRange) {
		super(parent, checkStyle(style));
		this.mParentComposite = parent;
		this.mDependingCombo = dependingCombo;
		this.mComboStyle = style;
		this.mSettings = settings;
		this.mColorManager = colorManager;
		init();
	}

	// remove styles we don't allow
	private static int checkStyle(int style) {
		int mask = SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.MULTI | SWT.NO_FOCUS | SWT.CHECK | SWT.VIRTUAL;

		int newStyle = style & mask;
		return newStyle;
	}

	// lay out everything and add all our listeners
	private void init() {
		isReadOnly = ((mComboStyle ^ SWT.READ_ONLY) == 0);

		mListeners = new ArrayList();

		if (mColorManager == null)
			mColorManager = new DefaultColorManager();

		if (mSettings == null)
			mSettings = new DefaultSettings();

		arrowButtonWidth = mSettings.getWindowsButtonWidth();
		if (OS_CARBON)
			arrowButtonWidth = mSettings.getCarbonButtonWidth();
		else if (OS_GTK)
			arrowButtonWidth = mSettings.getGTKButtonWidth();

		GridLayout gl = new GridLayout();
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		setLayout(gl);

		mCombo = new Combo(this, mComboStyle);
		mCombo.setVisibleItemCount(0);
		mCombo.setLayoutData(new GridData(130, SWT.DEFAULT));

		// when a user types in a date we parse it when they traverse away in any sense or form (focus lost etc)
		if (!isReadOnly) {
			Listener traverseListener = new Listener() {
				public void handleEvent(Event event) {
					if (event.detail == 16 || event.detail == 8 || event.detail == 4 || event.detail == 0) {
						String comboText = mCombo.getText();
						try {
							Date dat = DateHelper.getDate(comboText, mSettings.getDateFormat());
						} catch (Exception err) {
							List otherFormats = mSettings.getAdditionalDateFormats();
							if (otherFormats != null) {
								try {
									for (int i = 0; i < otherFormats.size(); i++) {
										String format = (String) otherFormats.get(i);
										Date date = DateHelper.getDate(comboText, format);
										// success
										setDate(date);
										return;
									}
								} catch (Exception err2) {
									// don't care
									// err2.printStackTrace();
								}
							}

							// unparseable date, set the last used date if any,
							// otherwise set nodateset text
							if (mStartDate != null)
								setDate(mStartDate);
							else
								mCombo.setText(mSettings.getNoDateSetText());
						}
					}
				}
			};

			// deal with traverse-away/in/return events to parse dates
			mCombo.addListener(SWT.Traverse, traverseListener);
			mCombo.addListener(SWT.FocusOut, traverseListener);
		}

		mCombo.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				// click in the text area? ignore
				if (isTextAreaClick(event)) {
					if (isCalendarVisible())
						kill(16);

					return;
				}

				// kill calendar if visible and do nothing else
				if (isCalendarVisible()) {
					kill(15);
					return;
				}
				// a very small time between the close and the open means it was
				// a click on the
				// arrow down to close, and we don't open it again in that case.
				// The next click will open.
				// this is the expected behavior.
				mLastShowRequest = Calendar.getInstance(mSettings.getLocale()).getTimeInMillis();

				long diff = mLastKillRequest - mLastShowRequest;
				if (diff > -100 && diff < 0)
					return;

				showCalendar();
			}
		});

		mKillListener = new Listener() {
			public void handleEvent(Event event) {
				kill(1);
			}
		};

		int[] comboEvents = { SWT.Dispose, SWT.Move, SWT.Resize };
		for (int i = 0; i < comboEvents.length; i++) {
			this.addListener(comboEvents[i], mKillListener);
		}

		int[] arrowEvents = { SWT.Selection };
		for (int i = 0; i < arrowEvents.length; i++) {
			mCombo.addListener(arrowEvents[i], mKillListener);
		}

		mFilterListenerFocusIn = new Listener() {
			public void handleEvent(Event event) {
				if (OS_CARBON) {
					Widget widget = event.widget;
					if (widget instanceof CalendarComposite == false)
						kill(2);

				} else {
					long now = Calendar.getInstance(mSettings.getLocale()).getTimeInMillis();
					long diff = now - mLastShowRequest;
					if (diff > 0 && diff < 100)
						return;

					kill(3);
				}
			}
		};

		Shell parentShell = mParentComposite.getShell();

		if (parentShell != null) {
			parentShell.addControlListener(new ControlListener() {
				public void controlMoved(ControlEvent e) {
					kill(4);
				}

				public void controlResized(ControlEvent e) {
					kill(5);
				}
			});

			parentShell.addListener(SWT.Deactivate, new Listener() {
				public void handleEvent(Event event) {
					Point mouseLoc = Display.getDefault().getCursorLocation();

					// with no focus shells, buttons will steal focus, and cause
					// deactivate events when clicked
					// so if the deactivate came from a mouse being over any of
					// our buttons, that is the same as
					// if we clicked them.
					if (mCalendarComposite != null && mCalendarComposite.isDisposed() == false)
						mCalendarComposite.externalClick(mouseLoc);

					kill(6);
				}
			});

			parentShell.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {

				}

				public void focusLost(FocusEvent e) {
					kill(7);
				}
			});
		}

		Display.getDefault().addFilter(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				// This may seem odd, but if the event is the CalendarCombo, we
				// actually clicked outside the widget and the CalendarComposite
				// area, meaning -
				// we clicked outside our widget, so we close it.
				if (event.widget instanceof CalendarCombo) {
					kill(8);
					mCombo.setFocus();
				}
			}
		});

		// remove listener when mCombo is disposed
		Display.getDefault().addFilter(SWT.FocusIn, mFilterListenerFocusIn);
		mCombo.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event) {
				Display.getDefault().removeFilter(SWT.FocusIn, mFilterListenerFocusIn);
			}
		});

		// mac's editable combos behave differently
		if (OS_CARBON) {
			mCombo.addVerifyListener(new VerifyListener() {
				public void verifyText(VerifyEvent event) {
					if (isCalendarVisible() || mAllowTextEntry) {
						;
					} else {
						event.doit = false;
					}
				}
			});

			// this is the most messed up thing ever, but it works. Basically,
			// OSX will pop up a combo of 1 item to let you pick it
			// even when it's blantantly obvious that you can only pick that one
			// item.. duh.. Anyway, the Paint event actually fires PRIOR to the
			// combo
			// opening, and our "cure" to the popup issue is to quickly remove
			// the item from the combo before it does open, thus, it doesn't
			// show the selector list,
			// but our popup instead. The side effect is that the combo appears
			// blank while the calendar is open. But as we reset the date when
			// no selection has been made, it's all good! It ain't pretty, but
			// it does the job
			if (isReadOnly) {
				mCombo.addListener(SWT.Paint, new Listener() {
					public void handleEvent(Event event) {
						mCarbonPrePopupDate = getDate();
						mCombo.removeAll();

					}
				});
			}
		}

		parentShell.addShellListener(new ShellListener() {
			public void shellActivated(ShellEvent event) {
			}

			public void shellClosed(ShellEvent event) {
				// shell killed
				kill(9);
			}

			public void shellDeactivated(ShellEvent event) {
			}

			public void shellDeiconified(ShellEvent event) {
			}

			public void shellIconified(ShellEvent event) {
				// shell no longer in focus (like.. WindowsKey + M on WinXP or
				// such)
				kill(10);
			}
		});

	}

	// checks whether a click was actually in the text area of a combo and not
	// on the arrow button. This is a hack by all means,
	// as there is (currently) no way to get the actual button from a combo.
	private boolean isTextAreaClick(Event event) {
		// read-only combos open on click anywhere
		if (isReadOnly)
			return false;

		Point size = mCombo.getSize();
		Rectangle rect = null;

		rect = new Rectangle(0, 0, size.x - arrowButtonWidth, size.y);
		if (isInside(event.x, event.y, rect))
			return true;

		return false;
	}

	// check whether a pixel value is inside a rectangle
	private boolean isInside(int x, int y, Rectangle rect) {
		if (rect == null)
			return false;

		return x >= rect.x && y >= rect.y && x <= (rect.x + rect.width) && y <= (rect.y + rect.height);
	}

	/**
	 * Sets the current date. Date will be automatically displayed in the text area of the combo according to the defined date format (in settings).
	 * 
	 * @param date Date to set
	 */
	public synchronized void setDate(Date date) {
		checkWidget();
		Calendar cal = Calendar.getInstance(mSettings.getLocale());
		cal.setTime(date);
		setDate(cal);
	}

	/**
	 * Sets the current date. Date will be automatically displayed in the text area of the combo according to the defined date format (in settings).
	 * 
	 * @param cal Calendar to set
	 */
	public synchronized void setDate(Calendar cal) {
		checkWidget();
		setText(DateHelper.getDate(cal, mSettings.getDateFormat()));
		this.mStartDate = cal;
	}

	/**
	 * Sets the text in the combo area to the given value. Do note that if you set a text that is a non-pareseable date string according to the currently set date format, that
	 * string will be replaced or removed when the user opens the popup. This is mainly for disabled combos or combos where you need to add additional control to what's displayed
	 * in the text area.
	 * 
	 * @param text Text to display
	 */
	public synchronized void setText(final String text) {
		checkWidget();

		if (mCombo.getText().equals(text))
			return;

		mAllowTextEntry = true;
		mCombo.removeAll();
		mCombo.add(text);
		mCombo.select(0);
		mAllowTextEntry = false;
	}

	// kills the popup area and unhooks various listeners, takes an integer so
	// that we can debug where the close comes from easier
	private synchronized void kill(int debug) {
		if (mCalendarComposite != null && mCalendarComposite.isMonthPopupActive())
			return;

		if (mCalendarShell != null && !mCalendarShell.isDisposed()) {
			mLastKillRequest = Calendar.getInstance(mSettings.getLocale()).getTimeInMillis();
			mCalendarShell.setVisible(false);
			mCalendarShell.dispose();
		}

		Display.getDefault().removeFilter(SWT.KeyDown, mKillListener);

		if (mCombo != null && !mCombo.isDisposed()) {
			mCombo.setCapture(false);
			// have to traverse escape key after the fake popup is closed or the
			// "0 length" popup will have focus
			// traversing forces it to close
			mCombo.traverse(SWT.TRAVERSE_ESCAPE);
			// if (getDate().length() != 0)
			// setText(DateHelper.getFormattedDate(DateHelper.getDate(getDate(),
			// mDateFormat), mDateFormat));
		}

		if (OS_CARBON) {
			if (mCarbonPrePopupDate != null)
				setDate(mCarbonPrePopupDate);
			else {
				mCombo.removeAll();
				mCombo.setText(" ");
			}
		}
	}

	private boolean isCalendarVisible() {
		if (mCalendarShell != null && !mCalendarShell.isDisposed())
			return true;

		return false;
	}

	/**
	 * Pops open the calendar popup.
	 */
	public void openCalendar() {
		checkWidget();
		if (isCalendarVisible())
			return;

		showCalendar();
	}

	/**
	 * Closes the calendar popup.
	 */
	public void closeCalendar() {
		checkWidget();
		if (!isCalendarVisible())
			return;

		kill(99);
	}

	/**
	 * Returns the set date as the raw String representation that is currently displayed in the combo.
	 * 
	 * @return String date
	 */
	public String getDateAsString() {
		checkWidget();
		String text = mCombo.getText();
		if (text.equals(" "))
			return "";

		return text;
	}

	/**
	 * Returns the currently set date.
	 * 
	 * @return Calendar of date selection or null.
	 */
	public Calendar getDate() {
		checkWidget();
		if (!isReadOnly)
			setDateBasedOnComboText();

		return mStartDate;
	}

	private void setDateBasedOnComboText() {
		try {
			Date d = DateHelper.getDate(mCombo.getText(), mSettings.getDateFormat());
			setDate(d);
		} catch (Exception err) {
			// we don't care
		}
	}

	// shows the calendar area
	private synchronized void showCalendar() {
		try {
			// this is part of the OSX issue where the popup for a combo is shown even if there is only 1 item to select (dumb).
			// as we remove the date just prior to the popup opening, here, we add it again, so it doesn't look like anything happened to the user
			// when in fact we removed and added something in the blink of an eye, just so that the combo would not open its own popup. This seems to work
			// without a hitch -- fix: June 21, 2008
			if (OS_CARBON && isReadOnly && mCarbonPrePopupDate != null)
				setDate(mCarbonPrePopupDate);

			// bug fix: Apr 18, 2008 - if we do various operations prior to
			// actually fetching any newly entered text into the
			// (non-read only) combo, we'll lose that edit, so fetch the combo
			// text now so that we can check it later down in the code
			// reported by: B. Haje
			setDateBasedOnComboText();

			String comboText = mCombo.getText();

			mCombo.setCapture(true);
			// some weird bug with opening, selecting, closing, then opening
			// again, which blanks out the mCombo the first time around..
			mCombo.select(0);

			// kill any old
			if (isCalendarVisible()) {
				// bug fix part #2, apr 23. Repeated klicking open/close will
				// get here, so we need to do the same bug fix as before but
				// somewhat differently
				// as we need to update the date object as well. This is
				// basically only for non-read-only combos, but the fix is
				// universally applicable.
				mCombo.setText(comboText);
				setDateBasedOnComboText();
				kill(11);
				return;
			}

			mCombo.setFocus();

			// bug fix: Apr 18, 2008 (see above)
			// the (necessary) setFocus call above for some reason screws up any
			// text entered by the user, so we actually have to set the text
			// back onto the combo, despite the fact we pulled the data just a
			// few lines ago.
			mCombo.setText(comboText);

			Display.getDefault().addFilter(SWT.KeyDown, mKillListener);

			mCalendarShell = new Shell(Display.getDefault().getActiveShell(), SWT.ON_TOP | SWT.NO_TRIM | SWT.NO_FOCUS);
			mCalendarShell.setLayout(new FillLayout());
			if (OS_CARBON)
				mCalendarShell.setSize(mSettings.getCalendarWidthMacintosh(), mSettings.getCalendarHeightMacintosh());
			else
				mCalendarShell.setSize(mSettings.getCalendarWidth(), mSettings.getCalendarHeight());

			mCalendarShell.addShellListener(new ShellListener() {
				public void shellActivated(ShellEvent event) {
				}

				public void shellClosed(ShellEvent event) {
					// shell killed
					kill(12);
				}

				public void shellDeactivated(ShellEvent event) {
				}

				public void shellDeiconified(ShellEvent event) {
				}

				public void shellIconified(ShellEvent event) {
					// shell no longer in focus (like.. WindowsKey + M on WinXP
					// or such)
					kill(13);
				}
			});

			// if no date has been set, and we have another calendar widget to
			// pull from, grab that date.
			// if we do have a date set, load it as an object we can actually
			// use.
			Calendar pre = null;

			if (comboText != null && comboText.length() > 1) {
				try {
					Date dat = DateHelper.getDate(comboText, mSettings.getDateFormat());
					if (dat != null) {
						pre = Calendar.getInstance(mSettings.getLocale());
						pre.setTime(dat);
					}
				} catch (Exception err) {
					List otherFormats = mSettings.getAdditionalDateFormats();
					if (otherFormats != null) {
						boolean dateSet = false;
						try {
							for (int i = 0; i < otherFormats.size(); i++) {
								String format = (String) otherFormats.get(i);
								Date date = DateHelper.getDate(comboText, format);
								// success
								setDate(date);
								Calendar cal = Calendar.getInstance(mSettings.getLocale());
								cal.setTime(date);
								pre = cal;
								dateSet = true;
								break;
							}
						} catch (Exception err2) {
							// don't care
							// err2.printStackTrace();
						}
						
						if (!dateSet) {
							// unparseable date, set the last used date if any,
							// otherwise set nodateset text
							if (mStartDate != null)
								setDate(mStartDate);
							else
								mCombo.setText(mSettings.getNoDateSetText());
						}
					}
					else {					
						// unparseable date, set the last used date if any,
						// otherwise set nodateset text
						if (mStartDate != null)
							setDate(mStartDate);
						else
							mCombo.setText(mSettings.getNoDateSetText());
					}
				}

			} else {
				if (mDependingCombo != null) {
					// we need to pull the date from the depending combo's text
					// area as it may be non-read-only, so we can't rely on the
					// date
					Calendar date = null;
					try {
						Date d = DateHelper.getDate(mDependingCombo.getCombo().getText(), mSettings.getDateFormat());
						date = Calendar.getInstance(mSettings.getLocale());
						date.setTime(d);
					} catch (Exception err) {
						date = mDependingCombo.getDate();
					}

					if (date != null) {
						pre = Calendar.getInstance(mSettings.getLocale());
						pre.setTime(date.getTime());
					}
				}
			}

			// create the calendar composite
			mCalendarComposite = new CalendarComposite(mCalendarShell, pre, mDisallowBeforeDate, mDisallowAfterDate, mColorManager, mSettings, mAllowDateRange, mStartDate, mEndDate);
			for (int i = 0; i < mListeners.size(); i++) {
				ICalendarListener listener = (ICalendarListener) mListeners.get(i);
				mCalendarComposite.addCalendarListener(listener);
			}

			mCalendarComposite.addMainCalendarListener(new ICalendarListener() {
				public void dateChanged(Calendar date) {
					mAllowTextEntry = true;
					mCombo.removeAll();

					if (OS_CARBON)
						mCarbonPrePopupDate = date;

					mStartDate = date;
					if (date == null) {
						setText(" ");
					} else {
						updateDate();
					}

					mAllowTextEntry = false;
				}

				public void dateRangeChanged(Calendar start, Calendar end) {
					mAllowTextEntry = true;
					mCombo.removeAll();

					mStartDate = start;

					if (OS_CARBON)
						mCarbonPrePopupDate = start;

					mEndDate = end;
					if (start == null) {
						setText(" ");
					} else {
						updateDate();
					}

					mAllowTextEntry = false;

					if (mDependingCombo != null) {
						if (end != null)
							mDependingCombo.setDate(end);
						else
							mDependingCombo.setText(" ");
					}
				}

				public void popupClosed() {
					kill(14);
				}
			});

			// figure out where to put the calendar composite shell
			Point calLoc = mCombo.getLocation();
			Point size = mCombo.getSize();

			Point loc = null;
			if (mSettings.showCalendarInRightCorner())
				loc = new Point(calLoc.x + size.x - mCalendarShell.getSize().x, calLoc.y + size.y);
			else
				loc = new Point(calLoc.x, calLoc.y + size.y);

			loc = toDisplay(loc);

			mCalendarShell.setLocation(loc);
			mCalendarShell.setVisible(true);
		} catch (Exception e) {
			mCombo.setCapture(false);
			e.printStackTrace();
			// don't really care
		}
	}

	/**
	 * Adds a calendar listener.
	 * 
	 * @param listener Listener
	 */
	public void addCalendarListener(ICalendarListener listener) {
		checkWidget();
		if (!mListeners.contains(listener))
			mListeners.add(listener);
	}

	/**
	 * Removes a calendar listener.
	 * 
	 * @param listener Listener
	 */
	public void removeCalendarListener(ICalendarListener listener) {
		checkWidget();
		mListeners.remove(listener);
	}

	/**
	 * Returns the combo box widget.
	 * <p>
	 * <font color="red"><b>NOTE:</b> The Combo box has a lot of listeners on it, please be "careful" when using it as you may cause unplanned-for things to happen.</font>
	 * 
	 * @return Combo widget
	 */
	public Combo getCombo() {
		checkWidget();
		return mCombo;
	}

	private void updateDate() {
		setText(DateHelper.getDate(mStartDate, mSettings.getDateFormat()));
	}

	/**
	 * Puts focus on the combo box.
	 */
	public void grabFocus() {
		checkWidget();
		mCombo.setFocus();
	}

	public void setEnabled(boolean enabled) {
		checkWidget();
		mCombo.setEnabled(enabled);
	}

	public boolean isEnabled() {
		checkWidget();
		return mCombo.getEnabled();
	}

	/**
	 * Adds a modification listener to the combo box.
	 * 
	 * @param ml ModifyListener
	 */
	public void addModifyListener(ModifyListener ml) {
		checkWidget();
		mCombo.addModifyListener(ml);
	}

	/**
	 * Removes a modification listener from the combo box.
	 * 
	 * @param ml ModifyListener
	 */
	public void removeModifyListener(ModifyListener ml) {
		checkWidget();
		mCombo.removeModifyListener(ml);
	}

	/**
	 * The date prior to which selection is not allowed.
	 * 
	 * @return Date
	 */
	public Calendar getDisallowBeforeDate() {
		return mDisallowBeforeDate;
	}

	/**
	 * Sets the date prior to which selection is not allowed.
	 * 
	 * @param disallowBeforeDate Date
	 */
	public void setDisallowBeforeDate(Calendar disallowBeforeDate) {
		mDisallowBeforeDate = disallowBeforeDate;
	}

	/**
	 * Sets the date prior to which selection is not allowed.
	 * 
	 * @param disallowBeforeDate Date
	 */
	public void setDisallowBeforeDate(Date disallowBeforeDate) {
		Calendar cal = Calendar.getInstance(mSettings.getLocale());
		cal.setTime(disallowBeforeDate);
		mDisallowBeforeDate = cal;
	}

	/**
	 * The date after which selection is not allowed.
	 * 
	 * @return Date
	 */
	public Calendar getDisallowAfterDate() {
		return mDisallowAfterDate;
	}

	/**
	 * Sets the date after which selection is not allowed.
	 * 
	 * @param disallowAfterDate Date
	 */
	public void setDisallowAfterDate(Calendar disallowAfterDate) {
		mDisallowAfterDate = disallowAfterDate;
	}

	/**
	 * Sets the date after which selection is not allowed.
	 * 
	 * @param disallowAfterDate
	 */
	public void setDisallowAfterDate(Date disallowAfterDate) {
		Calendar cal = Calendar.getInstance(mSettings.getLocale());
		cal.setTime(disallowAfterDate);
		mDisallowAfterDate = cal;
	}

	/**
	 * Sets the CalendarCombo that will be the recipient of (end date) date range changes as well as date start date that will be used.
	 * 
	 * @param combo CalendarCombo
	 */
	public void setDependingCombo(CalendarCombo combo) {
		mDependingCombo = combo;
	}

	/**
	 * Returns the CalendarCombo that is the recipient of (end date) date range changes as well as date start date that will be used.
	 * 
	 * @return CalendarCombo
	 */
	public CalendarCombo getDependingCombo() {
		return mDependingCombo;
	}

}
