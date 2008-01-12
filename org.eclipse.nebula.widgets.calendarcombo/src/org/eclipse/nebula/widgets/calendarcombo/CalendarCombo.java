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
import java.util.Locale;

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
 * <b>CalendarCombo - SWT/JFace Widget - 2005-2007. Version 1.0. &copy; Emil Crumhorn - emil.crumhorn@gmail.com.</b>
 * <p>
 * <b>Website</b><br>
 * If you want more info or more documentation, please visit: <a href="http://www.hexapixel.com/">http://www.hexapixel.com</a>
 * <p>
 * <b>Description</b><br>
 * CalendarCombo is a widget that opens a calendar when dropped down. The calendar is modelled after Microsoft Outlook's calendar widget and acts and 
 * behaves exactly the same (and it is also theme based). The combo is not based on CCombo (as many other custom implementations), but is instead 
 * attached to the native Combo box.
 * <p>
 * <b>Example Creation Code</b><br>
 * <code>
 * CalendarCombo cCombo = new CalendarCombo(inner, SWT.READ_ONLY);<br>
 * ...<br>
 * </code>
 * <p>
 * <b>Customizing</b><br>
 * There are two interfaces that are of importance for customizing, one is IColorManager and the other is ISettings. Let's start with the IColorManager.
 * <p>
 * <b>IColorManager</b><br>
 * If you don't specify a color manager, the DefaultColorManager will be used. The color manager's job is to return colors to the method that is painting all the actual
 * days and months etc. The colors that are returned from the ColorManager will determine everything as far as looks go. 
 * <p>
 * <b>ISettings</b><br>
 * To control the spacing between dates, various formats and text values, you will want to implement the ISettings interface. 
 * If you don't specify one, DefaultSettings will be used.
 *  
 * @author Emil Crumhorn
 * @version 1.0
 *
 */
public class CalendarCombo extends Composite {

	// the main combo box
	private Combo mCombo;

	private int mComboStyle = SWT.NONE;

	// the shell holding the CalendarComposite
	private Shell mCalendarShell;

	private Listener mKillListener;

	private Listener mFilterListenerFocusIn;

	private boolean mEditable;

	private String mDefaultText;

	private Composite mParentComposite;

	// values for determining when the last view of the mCalendarShell was.
	// this is to determine how quick clicks should behave
	private long mLastShowRequest = 0;

	private long mLastKillRequest = 0;

	private boolean mAllowTextEntry;

	private CalendarCombo mPullDateFrom;

	private CalendarComposite mCalendarComposite;

	private Calendar mSetDate;

	private IColorManager mColorManager;

	private ISettings mSettings;

	private ArrayList mListeners;

	protected static final boolean OS_CARBON = "carbon".equals(SWT.getPlatform()); //$NON-NLS-1$
	protected static final boolean OS_GTK = "gtk".equals(SWT.getPlatform()); //$NON-NLS-1$
	protected static final boolean OS_WINDOWS = "win32".equals(SWT.getPlatform()); //$NON-NLS-1$
	
	/*
	 * // Windows JNI Code for making a non-activated canvas, for reference if searching MSDN 
	 * int extStyle = OS.GetWindowLong(canvas.handle, OS.GWL_EXSTYLE); extStyle = extStyle | 0x80000; 
	 * OS.SetWindowLong(canvas.handle, OS.GWL_EXSTYLE, extStyle);
	 * 0x008000000 = WS_EX_NOACTIVATE;
	 */

	/**
	 * Creates a new calendar combo box with the given style.
	 * 
	 * @param parent
	 *        Parent control
	 * @param style
	 *        Combo style
	 */
	public CalendarCombo(Composite parent, int style) {
		super(parent, checkStyle(style));
		this.mComboStyle = style;
		this.mParentComposite = parent;
		this.mEditable = true;
		init();
	}

	/**
	 * Creates a new calendar mCombo box with the given style, ISettings and IColorManager implementations.
	 * 
	 * @param parent
	 *        Parent control
	 * @param style
	 *        Combo style
	 * @param settings ISettings implementation
	 * @param colorManager IColorManager implementation
	 */
	public CalendarCombo(Composite parent, int style, ISettings settings, IColorManager colorManager) {
		super(parent, checkStyle(style));
		this.mComboStyle = style;
		this.mParentComposite = parent;
		this.mEditable = true;
		this.mSettings = settings;
		this.mColorManager = colorManager;
		init();
	}

	/**
	 * Creates a CalendarCombo box on the given parent, style, default text (date text) and whether it should be editable or not.
	 * 
	 * @param parent
	 *        Parent control
	 * @param style
	 *        Combo style
	 * @param defaultText
	 *        default text to show
	 * @param editable
	 *        whether the mCombo is editable or read only
	 */
	public CalendarCombo(Composite parent, int style, String defaultText, boolean editable) {
		super(parent, checkStyle(style));
		this.mDefaultText = defaultText;
		this.mEditable = editable;
		this.mParentComposite = parent;
		this.mComboStyle = style;
		init();
	}

	/**
	 * Creates a CalendarCombo box on the given parent with the given style, default text (date text), whether it should be editable or not 
	 * and the ISettings and IColorManager implementations.
	 * 
	 * @param parent
	 *        Parent control
	 * @param style
	 *        Combo style
	 * @param defaultText
	 *        default text to show
	 * @param editable
	 *        whether the mCombo is editable or read only
	 * @param settings ISettings implementation
	 * @param colorManager IColorManager implementation
	 */
	public CalendarCombo(Composite parent, int style, String defaultText, boolean editable, ISettings settings, IColorManager colorManager) {
		super(parent, checkStyle(style));
		this.mDefaultText = defaultText;
		this.mEditable = editable;
		this.mParentComposite = parent;
		this.mComboStyle = style;
		this.mSettings = settings;
		this.mColorManager = colorManager;
		init();
	}
	
	/**
	 * Lets you create a depending CalendarCombo box, which, when no dates are
	 * set on the current one will set the starting date when popped up to be
	 * the date of the pullDateFrom CalendarCombo, should that box have a date
	 * set in it.
	 * 
	 * @param parent
	 * @param style
	 * @param defaultText
	 * @param editable
	 * @param pullDateFrom
	 *        CalendarCombo from where start date is pulled when no date has
	 *        been set locally.
	 */
	public CalendarCombo(Composite parent, int style, String defaultText, boolean editable, CalendarCombo pullDateFrom) {
		super(parent, checkStyle(style));
		this.mDefaultText = defaultText;
		this.mEditable = editable;
		this.mParentComposite = parent;
		this.mPullDateFrom = pullDateFrom;
		this.mComboStyle = style;
		init();
	}

	/**
	 * Lets you create a depending CalendarCombo box, which, when no dates are
	 * set on the current one will set the starting date when popped up to be
	 * the date of the pullDateFrom CalendarCombo, should that box have a date
	 * set in it.
	 * 
	 * @param parent
	 * @param style
	 * @param defaultText
	 * @param editable
	 * @param pullDateFrom
	 *        CalendarCombo from where start date is pulled when no date has
	 *        been set locally.
	 * @param settings ISettings implementation
	 * @param colorManager IColorManager implementation
	 */
	public CalendarCombo(Composite parent, int style, String defaultText, boolean editable, CalendarCombo pullDateFrom, ISettings settings, IColorManager colorManager) {
		super(parent, checkStyle(style));
		this.mDefaultText = defaultText;
		this.mEditable = editable;
		this.mParentComposite = parent;
		this.mPullDateFrom = pullDateFrom;
		this.mComboStyle = style;
		this.mSettings = settings;
		this.mColorManager = colorManager;
		init();
	}
	
	private static int checkStyle(int style) {
		int mask = SWT.BORDER | SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT | SWT.H_SCROLL | SWT.V_SCROLL
        | SWT.SINGLE | SWT.MULTI | SWT.NO_FOCUS | SWT.CHECK | SWT.VIRTUAL;
        int newStyle = style & mask;
        return newStyle;
	}
	
	// layout everything
	private void init() {
		mListeners = new ArrayList();

		if (mColorManager == null)
			mColorManager = new DefaultColorManager();

		if (mSettings == null)
			mSettings = new DefaultSettings();

		GridLayout gl = new GridLayout();
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		setLayout(gl);

		mCombo = new Combo(this, mComboStyle);
		mCombo.setVisibleItemCount(0);
		mCombo.setLayoutData(new GridData(130, SWT.DEFAULT));

		mCombo.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				// a very small time between the close and the open means it was
				// a click on the
				// arrow down to close, and we don't open it again in that case.
				// The next click will open.
				// this is the expected behavior.
				mLastShowRequest = Calendar.getInstance().getTimeInMillis();

				long diff = mLastKillRequest - mLastShowRequest;
				if (diff > -100 && diff < 0)
					return;

				// TODO: How do we differentiate between a mousedown on the
				// combo vs. a mousedown in the text area
				// when the combo is not SWT.READ_ONLY? Combo doesn't let us
				// pull the text widget out, not even via getChildren()
				// and the focusControl is a combo no matter what we click

				showCalendar();
			}
		});

		if (!mEditable)
			mCombo.setEnabled(false);

		mKillListener = new Listener() {
			public void handleEvent(Event event) {
				kill(1);
			}
		};

		int[] comboEvents = {
				SWT.Dispose, SWT.Move, SWT.Resize
		};
		for (int i = 0; i < comboEvents.length; i++) {
			this.addListener(comboEvents[i], mKillListener);
		}

		int[] arrowEvents = {
			SWT.Selection
		};
		for (int i = 0; i < arrowEvents.length; i++) {
			mCombo.addListener(arrowEvents[i], mKillListener);
		}

		mFilterListenerFocusIn = new Listener() {
			public void handleEvent(Event event) {
				if (OS_CARBON) {
					Widget widget = event.widget;
					if (widget instanceof CalendarComposite == false)
						kill(2);

				}
				else {
					long now = Calendar.getInstance().getTimeInMillis();
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
				// actually clicked outside the widget and the CalendarComposite area, meaning -
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
					}
					else {
						event.doit = false;
					}
				}
			});
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

		// set the default text
		if (mDefaultText != null && mDefaultText.length() > 0)
			setText(mDefaultText);
	}

	/**
	 * Sets the current date.
	 * 
	 * @param date Date to set
	 */
	public synchronized void setDate(Date date) {
		checkWidget();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		setDate(cal);
	}

	/**
	 * Sets the current date.
	 * 
	 * @param cal Calendar to set
	 */
	public synchronized void setDate(Calendar cal) {
		checkWidget();
		setText(DateHelper.getDate(cal, mSettings.getDateFormat()));
		this.mSetDate = cal;
	}

	/**
	 * Sets the text in the combo area to the given value.
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

	// kills the popup area and unhooks various listeners
	private synchronized void kill(int debug) {
		if (mCalendarComposite != null && mCalendarComposite.isMonthPopupActive())
			return;

		if (mCalendarShell != null && !mCalendarShell.isDisposed()) {
			mLastKillRequest = Calendar.getInstance().getTimeInMillis();
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
		showCalendar();
	}

	/**
	 * Returns the set date as a String representation.
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
	 * @return Current date
	 */
	public Calendar getDate() {
		checkWidget();
		return mSetDate;
	}

	/*
	 * Returns the currently set date.
	 * 
	 * @return Current date
	 */
	/*
	 * public Date getDateAsDate() { try { return
	 * DateHelper.getDate(getDateAsString(), mDateFormat); } catch (Exception
	 * err) { return null; } }
	 */

	// shows the calendar area
	private synchronized void showCalendar() {
		try {
			mCombo.setCapture(true);

			// some weird bug with opening, selecting, closing, then opening
			// again, which blanks out the mCombo the first time around..
			mCombo.select(0);

			// kill any old
			if (isCalendarVisible()) {
				kill(11);
				return;
			}

			mCombo.setFocus();

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
			if (mCombo.getText() != null && mCombo.getText().length() > 1) {
				try {
					Date dat = DateHelper.getDate(mCombo.getText(), mSettings.getDateFormat());
					if (dat != null) {
						pre = Calendar.getInstance(Locale.getDefault());
						pre.setTime(dat);
					}
				}
				catch (Exception err) {
					// unparseable date, clear the box
					mCombo.setText(mSettings.getNoDateSetText());
				}
			}
			else {
				if (mPullDateFrom != null) {
					Calendar date = mPullDateFrom.getDate();
					if (date != null) {
						pre = Calendar.getInstance(Locale.getDefault());
						pre.setTime(date.getTime());
					}
				}
			}

			// create the calendar composite
			mCalendarComposite = new CalendarComposite(mCalendarShell, pre, mColorManager, mSettings);
			for (int i = 0; i < mListeners.size(); i++) {
				ICalendarListener listener = (ICalendarListener) mListeners.get(i);
				mCalendarComposite.addCalendarListener(listener);
			}

			mCalendarComposite.addMainCalendarListener(new ICalendarListener() {
				public void dateChanged(Calendar date) {
					mAllowTextEntry = true;
					mCombo.removeAll();

					if (date == null) {
						setText(" ");
						mAllowTextEntry = false;
						return;
					}

					mSetDate = date;
					updateDate();
					mAllowTextEntry = false;
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
		}
		catch (Exception e) {
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
		setText(DateHelper.getDate(mSetDate, mSettings.getDateFormat()));
	}

	/**
	 * Puts focus on the combo box.
	 */
	public void grabFocus() {
		checkWidget();
		mCombo.setFocus();
	}

	/**
	 * Returns whether the combo is editable or not.
	 * 
	 * @return true if editable
	 */
	public boolean isEditable() {
		checkWidget();
		return mEditable;
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
	 * @param ml
	 */
	public void addModifyListener(ModifyListener ml) {
		checkWidget();
		mCombo.addModifyListener(ml);
	}

	/**
	 * Removes a modification listener from the combo box.
	 * 
	 * @param ml
	 */
	public void removeModifyListener(ModifyListener ml) {
		checkWidget();
		mCombo.removeModifyListener(ml);
	}
}
