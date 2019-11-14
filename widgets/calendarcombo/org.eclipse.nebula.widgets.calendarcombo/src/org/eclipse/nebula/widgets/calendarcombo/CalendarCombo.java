/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.calendarcombo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

/**
 * <b>CalendarCombo - SWT Widget - 2005-2008. Version 1.0. &copy; Emil Crumhorn - emil dot crumhorn at gmail dot
 * com.</b>
 * <p>
 * <b>Website</b><br>
 * If you want more info or more documentation, please visit: <a
 * href="http://www.hexapixel.com/">http://www.hexapixel.com</a>
 * <p>
 * <b>Description</b><br>
 * CalendarCombo is a widget that opens a calendar when dropped down. The calendar is modelled after Microsoft Outlook's
 * calendar widget and acts and behaves exactly the same (and it is also theme based). The combo is not based on CCombo
 * (as many other custom implementations), but is instead attached to the native Combo box.
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
 * There are two interfaces that are of importance for customizing, one is IColorManager and the other is ISettings.
 * Let's start with the IColorManager.
 * <p>
 * <b>IColorManager</b><br>
 * If you don't specify a color manager, the DefaultColorManager will be used. The color manager's job is to return
 * colors to the method that is painting all the actual days and months etc. The colors that are returned from the
 * ColorManager will determine everything as far as looks go.
 * <p>
 * <b>ISettings</b><br>
 * To control the spacing between dates, various formats and text values, you will want to implement the ISettings
 * interface. If you don't specify one, DefaultSettings will be used.
 * 
 * @author Emil Crumhorn
 * @version 1.1.2008.11.25
 */
public class CalendarCombo extends Composite {

    // the main combo box
    private Combo                  mCombo;
    private FlatCalendarCombo      mFlatCombo;

    private Composite              mComboControl;

    private int                    mComboStyle      = SWT.NONE;

    // the shell holding the CalendarComposite
    private Shell                  mCalendarShell;

    private Listener               mKillListener;

    private Listener               mFilterListenerFocusIn;

    private Composite              mParentComposite;

    // values for determining when the last view of the mCalendarShell was.
    // this is to determine how quick clicks should behave
    private long                   mLastShowRequest = 0;

    private long                   mLastKillRequest = 0;

    private CalendarCombo          mDependingCombo;

    private CalendarComposite      mCalendarComposite;

    private Calendar               mStartDate;

    private Calendar               mEndDate;

    private IColorManager          mColorManager;

    private ISettings              mSettings;

    private ArrayList              mListeners;

    private Calendar               mDisallowBeforeDate;

    private Calendar               mDisallowAfterDate;

    private boolean                isReadOnly;

    private boolean                isFlat;

    private int                    arrowButtonWidth;

    private boolean                mAllowDateRange;

    private Calendar               mCarbonPrePopupDate;

    private Listener               mKeyDownListener;

    private boolean                mParsingDate;

    private int                    mLastFireTime;

    private Calendar               mLastNotificationDate;

    private Listener               mOobClickListener;

    private List                   mDateParseExceptionListeners;

    private Listener               mOobDisplayFilterListener;

    protected static final boolean OS_CARBON        = "carbon".equals(SWT.getPlatform());
    protected static final boolean OS_GTK           = "gtk".equals(SWT.getPlatform());
    protected static final boolean OS_WINDOWS       = "win32".equals(SWT.getPlatform());
	private Listener shellDeactivate;

    /*
     * // Windows JNI Code for making a non-activated canvas, for reference if
     * searching MSDN int extStyle = OS.GetWindowLong(canvas.handle,
     * OS.GWL_EXSTYLE); extStyle = extStyle | 0x80000;
     * OS.SetWindowLong(canvas.handle, OS.GWL_EXSTYLE, extStyle); 0x008000000 =
     * WS_EX_NOACTIVATE;
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
     * @param allowDateRange Whether to allow date range selection (note that if there is no depending CalendarCombo set
     *        you will have to deal with the date range event yourself).
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
     * @param allowDateRange Whether to allow date range selection (note that if there is no depending CalendarCombo set
     *        you will have to deal with the date range event yourself).
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
     * Lets you create a depending CalendarCombo box, which, when no dates are set on the current one will set the
     * starting date when popped up to be the date of the pullDateFrom CalendarCombo, should that box have a date set in
     * it.
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
     * Lets you create a depending CalendarCombo box, which, when no dates are set on the current one will set the
     * starting date when popped up to be the date of the pullDateFrom CalendarCombo, should that box have a date set in
     * it. When the depending combo is set and the allowDateRange flag is true, the depending combo will be the
     * recipient of the end date of any date range selection.
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
     * Lets you create a depending CalendarCombo box, which, when no dates are set on the current one will set the
     * starting date when popped up to be the date of the pullDateFrom CalendarCombo, should that box have a date set in
     * it.
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
     * Lets you create a depending CalendarCombo box, which, when no dates are set on the current one will set the
     * starting date when popped up to be the date of the pullDateFrom CalendarCombo, should that box have a date set in
     * it. When the depending combo is set and the allowDateRange flag is true, the depending combo will be the
     * recipient of the end date of any date range selection.
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
        int mask = SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.MULTI | SWT.NO_FOCUS | SWT.CHECK | SWT.VIRTUAL | SWT.FLAT;

        int newStyle = style & mask;
        return newStyle;
    }

    // lay out everything and add all our listeners
    private void init() {
        isReadOnly = ((mComboStyle & SWT.READ_ONLY) != 0);
        isFlat = ((mComboStyle & SWT.FLAT) != 0);

        mListeners = new ArrayList();
        mDateParseExceptionListeners = new ArrayList();

        // if click happens on a control that is not us, we kill
        mOobClickListener = new Listener() {
            public void handleEvent(Event event) {
                if (!isCalendarVisible()) return;

                Control cc = getDisplay().getCursorControl();

                if (cc != mCalendarComposite) {
                    if (cc != mCalendarShell) {
                        boolean killIt = false;
                        if (isFlat) {
                            if (cc != mFlatCombo.getTextControl() && cc != mFlatCombo.getArrowButton()) {
                                killIt = true;
                            }
                        } else {
                            if (cc != mCombo) {
                                killIt = true;
                            }
                        }

                        if (killIt) {
                            kill(44);
                        }
                    }
                }

            }
        };

        if (mColorManager == null) mColorManager = new DefaultColorManager();

        if (mSettings == null) mSettings = new DefaultSettings();

        arrowButtonWidth = mSettings.getWindowsButtonWidth();
        if (OS_CARBON) arrowButtonWidth = mSettings.getCarbonButtonWidth();
        else if (OS_GTK) arrowButtonWidth = mSettings.getGTKButtonWidth();

        GridLayout gl = new GridLayout();
        gl.horizontalSpacing = 0;
        gl.verticalSpacing = 0;
        gl.marginWidth = 0;
        gl.marginHeight = 0;
        setLayout(gl);

        if (isFlat) {
            mFlatCombo = new FlatCalendarCombo(this, this, mComboStyle | SWT.FLAT);
            mFlatCombo.setVisibleItemCount(0);
            mFlatCombo.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
            mComboControl = mFlatCombo;
        } else {
            mCombo = new Combo(this, mComboStyle);
            mCombo.setVisibleItemCount(0);
            mCombo.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
            mComboControl = mCombo;
        }

        // when a user types in a date we parse it when they traverse away in
        // any sense or form (focus lost etc)
        if (!isReadOnly) {
            Listener traverseListener = new Listener() {
                public void handleEvent(Event event) {
                    // double event, ignore
                    if (event.time == mLastFireTime) { return; }

                    if (event.detail == 16 || event.detail == 8 || event.detail == 4 || event.detail == 0) {
                        parseTextDate(true);
                    }

                    mLastFireTime = event.time;
                }
            };

            // deal with traverse-away/in/return events to parse dates
            mComboControl.addListener(SWT.Traverse, traverseListener);
            mComboControl.addListener(SWT.FocusOut, traverseListener);

            mKeyDownListener = new Listener() {
                public void handleEvent(Event event) {
                    // if event didn't happen on this combo, ignore it
                    if (isFlat) {
                        if (event.widget != mFlatCombo.getTextControl()) return;
                    } else {
                        if (event.widget != mCombo) { return; }
                    }

                    if (mSettings.keyboardNavigatesCalendar()) {

                        if (event.keyCode == SWT.ARROW_DOWN) {
                            Control ctrl = (isFlat ? (Control) mFlatCombo.getTextControl() : mCombo);

                            if (getDisplay().getFocusControl() == ctrl) {
                                if (!isCalendarVisible()) showCalendar();
                                else {
                                    mCalendarComposite.keyPressed(event.keyCode, event.stateMask);
                                    event.doit = false;
                                }
                            }
                        } else {
                            if (isCalendarVisible()) {
                                mCalendarComposite.keyPressed(event.keyCode, event.stateMask);
                                // eat event or cursor will jump around in combo
                                // as well
                                event.doit = false;
                            }
                        }
                    } else {
                        boolean acceptedEvent = (event.keyCode == SWT.ARROW_DOWN || event.keyCode == SWT.ARROW_UP);
                        if (OS_CARBON) {
                            acceptedEvent = (event.character == mSettings.getCarbonArrowDownChar() || event.character == mSettings.getCarbonArrowUpChar());
                        }

                        if (acceptedEvent) {
                            boolean up = event.keyCode == SWT.ARROW_UP;
                            if (OS_CARBON) up = event.character == mSettings.getCarbonArrowUpChar();

                            int cursorLoc = isFlat ? mFlatCombo.getSelection().x : mCombo.getSelection().x;
                            // first, parse the date, we don't care if it's some
                            // fantastic format we can parse, parse it again
                            parseTextDate(true);
                            // once it's parsed, set it to the default format,
                            // that way we KNOW where certain parts of the date
                            // are
                            if (mStartDate != null) {
                                setComboText(DateHelper.getDate(mStartDate, mSettings.getDateFormat()));

                                String df = mSettings.getDateFormat();

                                event.doit = false;
                                if (isFlat) mFlatCombo.setSelection(new Point(cursorLoc, cursorLoc));
                                else mCombo.setSelection(new Point(cursorLoc, cursorLoc));

                                // split the date format. we do this as a date
                                // format of M/d/yyyy for example can still have
                                // 2 digits as M or d .
                                String separatorChar = null;
                                char[] accepted = mSettings.getAcceptedDateSeparatorChars();
                                for (int i = 0; i < accepted.length; i++) {
                                    if (df.indexOf(String.valueOf(accepted[i])) > -1) {
                                        separatorChar = String.valueOf(accepted[i]);
                                    }
                                }

                                int sectionStart = 0;
                                int sectionEnd = 0;

                                int splitCount = 0;

                                // get the format
                                String oneChar = "";
                                if (separatorChar != null) {
                                    // now find how many separator chars we are
                                    // from the left side of the date in the box
                                    // to where the cursor is, that will tell us
                                    // what part of
                                    // the date format we're on
                                    String comboText = isFlat ? mFlatCombo.getText() : mCombo.getText();

                                    for (int i = 0; i < comboText.length(); i++) {
                                        if (i >= cursorLoc) break;

                                        if (comboText.charAt(i) == separatorChar.charAt(0)) splitCount++;
                                    }

                                    StringTokenizer st = new StringTokenizer(df, separatorChar);
                                    int count = 0;
                                    while (st.hasMoreTokens()) {
                                        String tok = st.nextToken();
                                        if (count == splitCount) {
                                            oneChar = tok;
                                            break;
                                        }
                                        count++;
                                    }
                                } else {
                                    oneChar = mSettings.getDateFormat().substring(cursorLoc, cursorLoc + 1);

                                    // get the whole part, bit tricky I suppose,
                                    // but
                                    // we fetch everything that matches the
                                    // format
                                    // at the position we're at, easy enough
                                    StringBuffer buf = new StringBuffer();
                                    int start = cursorLoc;

                                    while (start >= 0) {
                                        if (mSettings.getDateFormat().charAt(start) == oneChar.charAt(0)) {
                                            buf.append(mSettings.getDateFormat().charAt(start));
                                        } else {
                                            break;
                                        }
                                        start--;
                                    }
                                    start = cursorLoc + 1;
                                    while (start < mSettings.getDateFormat().length()) {
                                        if (mSettings.getDateFormat().charAt(start) == oneChar.charAt(0)) {
                                            buf.append(mSettings.getDateFormat().charAt(start));
                                        } else {
                                            break;
                                        }
                                        start++;
                                    }

                                    sectionStart = mSettings.getDateFormat().indexOf(buf.toString());
                                    sectionEnd = sectionStart + buf.toString().length();
                                }

                                // Korean dates and some others have spaces in
                                // them (!?)
                                oneChar = oneChar.replaceAll(" ", "");
                                if (oneChar.length() == 0) return;

                                // now we now what to increase/decrease, lets do
                                // it
                                int calType = DateHelper.getCalendarTypeForString(oneChar);

                                if (calType != -1) {
                                    mStartDate.add(calType, up ? 1 : -1);

                                    String newDate = DateHelper.getDate(mStartDate, mSettings.getDateFormat());

                                    setComboText(newDate);

                                    if (separatorChar != null) {
                                        // we need to update the selection after
                                        // we've set the date
                                        // figure out cursor location, now we
                                        // have to use the date in the box again
                                        StringTokenizer st = new StringTokenizer(newDate, separatorChar);
                                        int count = 0;
                                        boolean stop = false;
                                        while (st.hasMoreTokens()) {
                                            String tok = st.nextToken();

                                            // we found our section
                                            if (count == splitCount) {
                                                sectionEnd = sectionStart + tok.length();
                                                stop = true;
                                                break;
                                            }

                                            // if we're stopping, break out
                                            if (stop) break;

                                            // add on separator chars for each
                                            // loop iteration post 0
                                            sectionStart += 1;
                                            // and token length
                                            sectionStart += tok.length();

                                            count++;
                                        }
                                    }

                                    // set the selection for us
                                    if (isFlat) {
                                        mFlatCombo.setSelection(new Point(sectionStart, sectionEnd));
                                    } else {
                                        mCombo.setSelection(new Point(sectionStart, sectionEnd));

                                    }

                                }
                            }
                        }
                    }
                }
            };

            getDisplay().addFilter(SWT.KeyDown, mKeyDownListener);
        }

        if (isFlat) {
            mComboControl.addListener(SWT.FocusOut, new Listener() {
                public void handleEvent(Event event) {
                    kill(98);
                }
            });
        }

        mComboControl.addListener(SWT.MouseDown, new Listener() {
            public void handleEvent(Event event) {
                // click in the text area? ignore
                if (!isFlat) {
                    if (isTextAreaClick(event)) {
                        if (isCalendarVisible()) {
                            kill(16);
                        }

                        return;
                    }
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
                if (diff > -100 && diff < 0) { return; }

                showCalendar();
            }
        });

        mKillListener = new Listener() {
            public void handleEvent(Event event) {
                if (event.keyCode == SWT.ESC) {
                    kill(77);
                    return;
                }

                // ignore arrow down events for killing popup
                if (event.keyCode == SWT.ARROW_DOWN || event.keyCode == SWT.ARROW_UP || event.keyCode == SWT.ARROW_LEFT || event.keyCode == SWT.ARROW_RIGHT || event.keyCode == SWT.CR || event.keyCode == SWT.LF) { return; }

                kill(1);
            }
        };

        int[] comboEvents = { SWT.Dispose, SWT.Move, SWT.Resize };
        for (int i = 0; i < comboEvents.length; i++) {
            this.addListener(comboEvents[i], mKillListener);
        }

        int[] arrowEvents = {
        //SWT.Selection
        };
        for (int i = 0; i < arrowEvents.length; i++) {
            mComboControl.addListener(arrowEvents[i], mKillListener);
        }

        mFilterListenerFocusIn = new Listener() {
            public void handleEvent(Event event) {
                if (OS_CARBON) {
                    Widget widget = event.widget;
                    if (widget instanceof CalendarComposite == false) kill(2);

                    // on mac, select all text in combo if we are the control
                    // that gained focus
                    if (mComboControl == getDisplay().getFocusControl()) {
                        if (isFlat) {
                            mFlatCombo.getTextControl().selectAll();
                        } else {
                            mCombo.setSelection(new Point(0, mCombo.getText().length()));
                        }
                    }
                } else {
                    long now = Calendar.getInstance(mSettings.getLocale()).getTimeInMillis();
                    long diff = now - mLastShowRequest;
                    if (diff > 0 && diff < 100) return;

                    if (!isCalendarVisible()) return;

                    // don't force focus, user clicked another control, let it
                    // grab the focus or it'll be odd behavior
                    if (!isFlat) kill(3, true);
                }
            }
        };

        final Shell parentShell = mParentComposite.getShell();

        if (parentShell != null) {
            parentShell.addControlListener(new ControlListener() {
                public void controlMoved(ControlEvent e) {
                    kill(4);
                }

                public void controlResized(ControlEvent e) {
                    kill(5);
                }
            });

            shellDeactivate = new Listener() {
				
				public void handleEvent(Event event) {
                    // with no focus shells, buttons will steal focus, and cause
                    // deactivate events when clicked
                    // so if the deactivate came from a mouse being over any of
                    // our buttons, that is the same as
                    // if we clicked them.
                    if (mCalendarComposite != null && mCalendarComposite.isDisposed() == false) {
                    	mCalendarComposite.externalClick(getDisplay().getCursorLocation());
                    }

                    if (!isFlat) kill(6);
				}
			};
            
            parentShell.addListener(SWT.Deactivate, shellDeactivate); 
//            		new Listener() {
//                public void handleEvent(Event event) {
//                    Point mouseLoc = getDisplay().getCursorLocation();
//
//                    // with no focus shells, buttons will steal focus, and cause
//                    // deactivate events when clicked
//                    // so if the deactivate came from a mouse being over any of
//                    // our buttons, that is the same as
//                    // if we clicked them.
//                    if (mCalendarComposite != null && mCalendarComposite.isDisposed() == false) mCalendarComposite.externalClick(mouseLoc);
//
//                    if (!isFlat) kill(6);
//                }
//            });

            parentShell.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) {

                }

                public void focusLost(FocusEvent e) {
                    kill(7);
                }
            });
        }

        mOobDisplayFilterListener = new Listener() {
            public void handleEvent(Event event) {
                // This may seem odd, but if the event is the CalendarCombo, we
                // actually clicked outside the widget and the CalendarComposite
                // area, meaning -
                // we clicked outside our widget, so we close it.
                if (event.widget instanceof CalendarCombo) {
                    kill(8);
                    mComboControl.setFocus();
                }
            }
        };
        
        getDisplay().addFilter(SWT.MouseDown, mOobDisplayFilterListener); 

        // remove listener when mCombo is disposed
        getDisplay().addFilter(SWT.FocusIn, mFilterListenerFocusIn);
        mComboControl.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent event) {
            	parentShell.removeListener(SWT.Deactivate, shellDeactivate);
                getDisplay().removeFilter(SWT.FocusIn, mFilterListenerFocusIn);
                getDisplay().removeFilter(SWT.MouseDown, mOobDisplayFilterListener);
                if (mKeyDownListener != null) getDisplay().removeFilter(SWT.KeyDown, mKeyDownListener);

                if (mSettings.getCarbonDrawFont() != null) mSettings.getCarbonDrawFont().dispose();

                if (mSettings.getWindowsMonthPopupDrawFont() != null) mSettings.getWindowsMonthPopupDrawFont().dispose();
            }
        });

        // mac's editable combos behave differently
        if (OS_CARBON) {
            // this code seems obsolete as of June 24th 2008, Allow text input
            // on Mac, we parse it anyways now, which we didn't do prior
            // leaving code in for a while to remind myself
            /*
             * mCombo.addVerifyListener(new VerifyListener() { public void
             * verifyText(VerifyEvent event) { if (isCalendarVisible() ||
             * mAllowTextEntry) { ; } else { event.doit = false; } } });
             */

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
                if (isFlat) {
                    mFlatCombo.addListener(SWT.Paint, new Listener() {
                        public void handleEvent(Event event) {
                            mCarbonPrePopupDate = getDate();
                            // mFlatCombo.removeAll();
                        }
                    });
                } else {
                    mCombo.addListener(SWT.Paint, new Listener() {
                        public void handleEvent(Event event) {
                            mCarbonPrePopupDate = getDate();
                            mCombo.removeAll();

                        }
                    });
                }
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

    // parses the date, and really tries
    private void parseTextDate(boolean notifyListeners) {
        // listeners by users can cause infinite loops as we notify, they set dates on
        // notify, etc, so don't allow parsing if we haven't finished internally yet.
        if (mParsingDate) { return; }

        try {
            mParsingDate = true;

            String comboText = (isFlat ? mFlatCombo.getText() : mCombo.getText());

            if (comboText.length() == 0 && mStartDate != null) {
                mStartDate = null;
                setText("");
                if (mLastNotificationDate != null && notifyListeners) {
                    notifyDateChangedToNull();
                }
                return;
            }

            mStartDate = DateHelper.parse(comboText, mSettings.getLocale(), mSettings.getDateFormat(), mSettings.getAcceptedDateSeparatorChars(), mSettings.getAdditionalDateFormats());
            updateDate();
            if (notifyListeners) {
            	notifyDateChanged();
            }
            mParsingDate = false;
        } catch (CalendarDateParseException dpe) {
            if (!mDateParseExceptionListeners.isEmpty()) {
                notifyDateParseException(dpe);
            } else {
                dpe.printStackTrace();
            }
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            mParsingDate = false;
        }
    }

    private void notifyDateParseException(CalendarDateParseException dpe) {
        for (int i = 0; i < mDateParseExceptionListeners.size(); i++) {
            ((IDateParseExceptionListener) mDateParseExceptionListeners.get(i)).parseExceptionThrown(dpe);
        }
    }

    private void notifyDateChangedToNull() {
        if (mLastNotificationDate == null) { return; }

        for (int i = 0; i < mListeners.size(); i++) {
            try {
                ((ICalendarListener) mListeners.get(i)).dateChanged(mStartDate);
            } catch (Exception err) {
                err.printStackTrace();
            }
        }

        mLastNotificationDate = null;
    }

    private void notifyDateRangeChanged() {
        for (int i = 0; i < mListeners.size(); i++) {
            try {
                ((ICalendarListener) mListeners.get(i)).dateRangeChanged(mStartDate, mEndDate);
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

    private void notifyDateChanged() {
        if (mStartDate == null) {
            notifyDateChangedToNull();
            return;
        }

        if (mLastNotificationDate != null && mStartDate != null) {
            if (DateHelper.sameDate(mLastNotificationDate, mStartDate)) { return; }
        }

        mLastNotificationDate = (Calendar) mStartDate.clone();

        for (int i = 0; i < mListeners.size(); i++) {
            try {
                ((ICalendarListener) mListeners.get(i)).dateChanged(mStartDate);
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

    // checks whether a click was actually in the text area of a combo and not
    // on the arrow button. This is a hack by all means,
    // as there is (currently) no way to get the actual button from a combo.
    private boolean isTextAreaClick(Event event) {
        // read-only combos open on click anywhere
        if (isReadOnly) return false;

        Point size = isFlat ? mFlatCombo.getSize() : mCombo.getSize();
        Rectangle rect = null;

        rect = new Rectangle(0, 0, size.x - arrowButtonWidth, size.y);
        if (isInside(event.x, event.y, rect)) return true;

        return false;
    }

    // check whether a pixel value is inside a rectangle
    private boolean isInside(int x, int y, Rectangle rect) {
        if (rect == null) return false;

        return x >= rect.x && y >= rect.y && x <= (rect.x + rect.width) && y <= (rect.y + rect.height);
    }

    /**
     * Sets the current date. Date will be automatically displayed in the text area of the combo according to the
     * defined date format (in settings).
     * 
     * @param date Date to set
     */
    public synchronized void setDate(Date date) {
        checkWidget();
        if (date == null) {
            clear();
        } else {
            Calendar cal = Calendar.getInstance(mSettings.getLocale());
            cal.setTime(date);
            setDate(cal);
        }
    }

    /**
     * Sets the current date. Date will be automatically displayed in the text area of the combo according to the
     * defined date format (in settings).
     * 
     * @param cal Calendar to set
     */
    public synchronized void setDate(Calendar cal) {
        checkWidget();
        mStartDate = cal;
        updateDate();
    }

    /**
     * Sets the text in the combo area to the given value. Do note that if you set a text that is a non-pareseable date
     * string according to the currently set date format, that string will be replaced or removed when the user opens
     * the popup. This is mainly for disabled combos or combos where you need to add additional control to what's
     * displayed in the text area.
     * 
     * @param text Text to display
     */
    public synchronized void setText(final String text) {
        checkWidget();

        String txt = isFlat ? mFlatCombo.getText() : mCombo.getText();

        if (txt.equals(text)) return;

        setComboText(text);
    }

    private void setComboText(String text) {

        if (isFlat) {
            // mFlatCombo.removeAll();
            mFlatCombo.setText(text);
            // mFlatCombo.select(0);
        } else {
            mCombo.removeAll();
            mCombo.add(text);
            mCombo.select(0);
        }
    }

    private synchronized void kill(int debug) {
        kill(debug, false);
    }

    // kills the popup area and unhooks various listeners, takes an integer so
    // that we can debug where the close comes from easier
    private synchronized void kill(int debug, boolean skipFocus) {
        if (mCalendarComposite == null) return;
        if (mCalendarComposite.isDisposed()) return;

        if (mCalendarComposite != null && mCalendarComposite.isMonthPopupActive()) return;

        // System.err.println(debug);

        if (mCalendarShell != null && !mCalendarShell.isDisposed()) {
            mLastKillRequest = Calendar.getInstance(mSettings.getLocale()).getTimeInMillis();
            mCalendarShell.setVisible(false);
            mCalendarShell.dispose();
        }

        getDisplay().removeFilter(SWT.KeyDown, mKillListener);
        getDisplay().removeFilter(SWT.MouseDown, mOobClickListener);
        getDisplay().removeFilter(SWT.MouseDown, mOobDisplayFilterListener);
        
        if (mComboControl != null && !mComboControl.isDisposed()) {
            mComboControl.setCapture(false);
            // have to traverse escape key after the fake popup is closed or the
            // "0 length" popup will have focus
            // traversing forces it to close
            mComboControl.traverse(SWT.TRAVERSE_ESCAPE);
            // if (getDate().length() != 0)
            // setText(DateHelper.getFormattedDate(DateHelper.getDate(getDate(),
            // mDateFormat), mDateFormat));
        }

        if (OS_CARBON) {
            if (mCarbonPrePopupDate != null) setDate(mCarbonPrePopupDate);
        }

        if (!skipFocus) {
            if (isFlat) {
                mFlatCombo.getTextControl().setFocus();
            } else {
                mCombo.setFocus();
            }
        }
    }

    private boolean isCalendarVisible() {
        /*		try {
        			throw new Exception();
        		}
        		catch (Exception err) {
        			err.printStackTrace();
        		}
        */return (mCalendarShell != null && !mCalendarShell.isDisposed());
    }

    /**
     * Pops open the calendar popup.
     */
    public void openCalendar() {
        checkWidget();
        if (isCalendarVisible()) { return; }

        showCalendar();
    }

    /**
     * Closes the calendar popup.
     */
    public void closeCalendar() {
        checkWidget();
        if (!isCalendarVisible()) { return; }

        kill(99);
    }

    /**
     * Returns the set date as the raw String representation that is currently displayed in the combo.
     * 
     * @return String date
     */
    public String getDateAsString() {
        checkWidget();
        String text = isFlat ? mFlatCombo.getText() : mCombo.getText();
        if (text.equals("")) return "";

        return text;
    }

    /**
     * Returns the currently set date.
     * 
     * @return Calendar of date selection or null.
     */
    public Calendar getDate() {
        checkWidget();
        if (!isReadOnly) {
        	parseTextDate(false);
        }

        return mStartDate;
    }

    /*
     * private void setDateBasedOnComboText() { try { Date d =
     * DateHelper.getDate(isFlat ? mFlatCombo.getText() : mCombo.getText(),
     * mSettings.getDateFormat()); setDate(d); } catch (Exception err) { // we
     * don't care } }
     */

    // shows the calendar area
    public synchronized void showCalendar() {
        try {
            // this is part of the OSX issue where the popup for a combo is
            // shown even if there is only 1 item to select (dumb).
            // as we remove the date just prior to the popup opening, here, we
            // add it again, so it doesn't look like anything happened to the
            // user
            // when in fact we removed and added something in the blink of an
            // eye, just so that the combo would not open its own popup. This
            // seems to work
            // without a hitch -- fix: June 21, 2008
            if (OS_CARBON && isReadOnly && mCarbonPrePopupDate != null) setDate(mCarbonPrePopupDate);

            // bug fix: Apr 18, 2008 - if we do various operations prior to
            // actually fetching any newly entered text into the
            // (non-read only) combo, we'll lose that edit, so fetch the combo
            // text now so that we can check it later down in the code
            // reported by: B. Haje
            parseTextDate(true);

            String comboText = isFlat ? mFlatCombo.getText() : mCombo.getText();

            mComboControl.setCapture(true);
            // some weird bug with opening, selecting, closing, then opening
            // again, which blanks out the mCombo the first time around..
            if (!isFlat) mCombo.select(0);

            // kill any old
            if (isCalendarVisible()) {
                // bug fix part #2, apr 23. Repeated klicking open/close will
                // get here, so we need to do the same bug fix as before but
                // somewhat differently
                // as we need to update the date object as well. This is
                // basically only for non-read-only combos, but the fix is
                // universally applicable.
                setComboText(comboText);
                parseTextDate(true);
                kill(11);
                return;
            }

            mComboControl.setFocus();

            // bug fix: Apr 18, 2008 (see above)
            // the (necessary) setFocus call above for some reason screws up any
            // text entered by the user, so we actually have to set the text
            // back onto the combo, despite the fact we pulled the data just a
            // few lines ago.
            setComboText(comboText);

            getDisplay().addFilter(SWT.KeyDown, mKillListener);
            getDisplay().addFilter(SWT.MouseDown, mOobClickListener);

            mCalendarShell = new Shell(getDisplay().getActiveShell(), SWT.ON_TOP | SWT.NO_TRIM | SWT.NO_FOCUS);
            mCalendarShell.setLayout(new FillLayout());
            if (OS_CARBON) mCalendarShell.setSize(mSettings.getCalendarWidthMacintosh(), mSettings.getCalendarHeightMacintosh());
            else mCalendarShell.setSize(mSettings.getCalendarWidth(), mSettings.getCalendarHeight());

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
                            if (mStartDate != null) setDate(mStartDate);
                            else {
                                setComboText(mSettings.getNoDateSetText());
                            }
                        }
                    } else {
                        // unparseable date, set the last used date if any,
                        // otherwise set nodateset text
                        if (mStartDate != null) setDate(mStartDate);
                        else {
                            setComboText(mSettings.getNoDateSetText());
                        }
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
            /*       mCalendarComposite.addCalendarListener(new ICalendarListener() {

                       public void dateChanged(Calendar date) {
                           mStartDate = date;
                           notifyDateChanged();
                       }

                       public void dateRangeChanged(Calendar start, Calendar end) {
                           mStartDate = start;
                           mEndDate = end;
                           notifyDateChanged();
                       }

                       public void popupClosed() {
                       }
                       
                   });
            */
            mCalendarComposite.addMainCalendarListener(new ICalendarListener() {
                public void dateChanged(Calendar date) {
                    if (!isFlat) mCombo.removeAll();

                    if (OS_CARBON) mCarbonPrePopupDate = date;

                    mStartDate = date;
                    if (date == null) {
                        setText("");
                    } else {
                        updateDate();
                    }

                    if (mStartDate == null) {
                        notifyDateChangedToNull();
                    } else {
                        notifyDateChanged();
                    }
                }

                public void dateRangeChanged(Calendar start, Calendar end) {
                    if (!isFlat) mCombo.removeAll();

                    mStartDate = start;

                    if (OS_CARBON) mCarbonPrePopupDate = start;

                    mEndDate = end;
                    if (start == null) {
                        setText("");
                    } else {
                        updateDate();
                    }

                    if (mDependingCombo != null) {
                        if (end != null) {
                            mDependingCombo.setDate(end);
                        } else {
                            mDependingCombo.setText("");
                        }

                        notifyDateRangeChanged();
                    } else {
                        notifyDateChanged();
                    }

                }

                public void popupClosed() {
                    kill(14);
                    for (int i = 0; i < mListeners.size(); i++) {
                        ((ICalendarListener) mListeners.get(i)).popupClosed();
                    }
                }
            });

            // figure out where to put the calendar composite shell
            Point calLoc = mComboControl.getLocation();
            Point size = mComboControl.getSize();

            Point loc = null;
            if (mSettings.showCalendarInRightCorner()) {
                loc = new Point(calLoc.x + size.x - mCalendarShell.getSize().x, calLoc.y + size.y);
            } else {
                loc = new Point(calLoc.x, calLoc.y + size.y);
            }
            loc = toDisplay(loc);
            // don't let it slip out on the left side of the screen
            if (loc.x < 0) {
                loc.x = 0;
            }

            mCalendarShell.setLocation(loc);
            mCalendarShell.setVisible(true);

        } catch (Exception e) {
            mComboControl.setCapture(false);
            e.printStackTrace();
            // don't really care
        }
    }

    /**
     * Adds a {@link IDateParseExceptionListener} that listens to date parse exceptions
     * 
     * @param listener to add
     */
    public void addDateParseExceptionListener(IDateParseExceptionListener listener) {
        checkWidget();
        if (!mDateParseExceptionListeners.contains(listener)) {
            mDateParseExceptionListeners.add(listener);
        }
    }

    /**
     * Removes a {@link IDateParseExceptionListener} listener.
     * 
     * @param listener to remove
     */
    public void removeDateParseExceptionListener(IDateParseExceptionListener listener) {
        checkWidget();
        mDateParseExceptionListeners.remove(listener);
    }

    /**
     * Adds a calendar listener.
     * 
     * @param listener Listener
     */
    public void addCalendarListener(ICalendarListener listener) {
        checkWidget();
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
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
     * <font color="red"><b>NOTE:</b> The Combo box has a lot of listeners on it, please be "careful" when using it as
     * you may cause unplanned-for things to happen.</font>
     * 
     * @return Combo widget
     */
    public Combo getCombo() {
        checkWidget();
        return mCombo;
    }

    public FlatCalendarCombo getCCombo() {
        checkWidget();
        return mFlatCombo;
    }

    private void updateDate() {
        if (mStartDate == null) {
            clear();
            return;
        }

        String toSet = DateHelper.getDate(mStartDate, DateHelper.dateFormatFix(mSettings.getDateFormat()));
        setText(toSet);
    }

    /**
     * Removes date and clears combo, same as setDate(null).
     */
    public void clear() {
        if (isFlat) {
            mFlatCombo.removeAll();
            mFlatCombo.setText(mSettings.getNoDateSetText());
        } else {
            mCombo.removeAll();
            mCombo.setText(mSettings.getNoDateSetText());
        }

        mStartDate = null;
        mEndDate = null;
    }

    /**
     * Puts focus on the combo box.
     * 
     * @deprecated please use {@link #setFocus()}
     */
    public void grabFocus() {
        checkWidget();
        if (isFlat) mFlatCombo.setFocus();
        else mCombo.setFocus();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Composite#setFocus()
     */
    public boolean setFocus() {
        checkWidget();
        if (this.isFlat) {
            return this.getCCombo().getTextControl().setFocus();
        } else {
            return this.mCombo.setFocus();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#forceFocus()
     */
    public boolean forceFocus() {
        checkWidget();
        if (this.isFlat) {
            return this.getCCombo().getTextControl().forceFocus();
        } else {
            return this.mCombo.forceFocus();
        }
    }

    public void setEnabled(boolean enabled) {
        checkWidget();
        if (isFlat) mFlatCombo.setEnabled(enabled);
        else mCombo.setEnabled(enabled);
    }

    public boolean isEnabled() {
        checkWidget();
        return isFlat ? mFlatCombo.getEnabled() : mCombo.getEnabled();
    }

    /**
     * Adds a modification listener to the combo box.
     * 
     * @param ml ModifyListener
     */
    public void addModifyListener(ModifyListener ml) {
        checkWidget();
        if (isFlat) {
            mFlatCombo.addModifyListener(ml);
        } else {
            mCombo.addModifyListener(ml);
        }
    }

    /**
     * Removes a modification listener from the combo box.
     * 
     * @param ml ModifyListener
     */
    public void removeModifyListener(ModifyListener ml) {
        checkWidget();
        if (isFlat) {
            mFlatCombo.removeModifyListener(ml);
        } else {
            mCombo.removeModifyListener(ml);
        }
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
     * Sets the CalendarCombo that will be the recipient of (end date) date range changes as well as date start date
     * that will be used.
     * 
     * @param combo CalendarCombo
     */
    public void setDependingCombo(CalendarCombo combo) {
        mDependingCombo = combo;
    }

    /**
     * Returns the CalendarCombo that is the recipient of (end date) date range changes as well as date start date that
     * will be used.
     * 
     * @return CalendarCombo
     */
    public CalendarCombo getDependingCombo() {
        return mDependingCombo;
    }

    public void addControlListener(ControlListener listener) {
        mComboControl.addControlListener(listener);
    }

    public void addDragDetectListener(DragDetectListener listener) {
        mComboControl.addDragDetectListener(listener);
    }

    public void addFocusListener(FocusListener listener) {
        mComboControl.addFocusListener(listener);
    }

    public void addHelpListener(HelpListener listener) {
        mComboControl.addHelpListener(listener);
    }

    public void addKeyListener(KeyListener listener) {
        mComboControl.addKeyListener(listener);
    }

    public void addMenuDetectListener(MenuDetectListener listener) {
        mComboControl.addMenuDetectListener(listener);
    }

    public void addMouseListener(MouseListener listener) {
        mComboControl.addMouseListener(listener);
    }

    public void addMouseMoveListener(MouseMoveListener listener) {
        mComboControl.addMouseMoveListener(listener);
    }

    public void addMouseTrackListener(MouseTrackListener listener) {
        mComboControl.addMouseTrackListener(listener);
    }

    public void addMouseWheelListener(MouseWheelListener listener) {
        mComboControl.addMouseWheelListener(listener);
    }

    public void addPaintListener(PaintListener listener) {
        mComboControl.addPaintListener(listener);
    }

    public void addTraverseListener(TraverseListener listener) {
        mComboControl.addTraverseListener(listener);
    }

    public void addDisposeListener(DisposeListener listener) {
        mComboControl.addDisposeListener(listener);
    }

    public void addListener(int eventType, Listener listener) {
        mComboControl.addListener(eventType, listener);
    }

    public void removeControlListener(ControlListener listener) {
        mComboControl.removeControlListener(listener);
    }

    public void removeDragDetectListener(DragDetectListener listener) {
        mComboControl.removeDragDetectListener(listener);
    }

    public void removeFocusListener(FocusListener listener) {
        mComboControl.removeFocusListener(listener);
    }

    public void removeHelpListener(HelpListener listener) {
        mComboControl.removeHelpListener(listener);
    }

    public void removeKeyListener(KeyListener listener) {
        mComboControl.removeKeyListener(listener);
    }

    public void removeMenuDetectListener(MenuDetectListener listener) {
        mComboControl.removeMenuDetectListener(listener);
    }

    public void removeMouseListener(MouseListener listener) {
        mComboControl.removeMouseListener(listener);
    }

    public void removeMouseMoveListener(MouseMoveListener listener) {
        mComboControl.removeMouseMoveListener(listener);
    }

    public void removeMouseTrackListener(MouseTrackListener listener) {
        mComboControl.removeMouseTrackListener(listener);
    }

    public void removeMouseWheelListener(MouseWheelListener listener) {
        mComboControl.removeMouseWheelListener(listener);
    }

    public void removePaintListener(PaintListener listener) {
        mComboControl.removePaintListener(listener);
    }

    public void removeTraverseListener(TraverseListener listener) {
        mComboControl.removeTraverseListener(listener);
    }

    public void notifyListeners(int eventType, Event event) {
        mComboControl.notifyListeners(eventType, event);
    }

    public void removeDisposeListener(DisposeListener listener) {
        mComboControl.removeDisposeListener(listener);
    }

    public void removeListener(int eventType, Listener listener) {
        mComboControl.removeListener(eventType, listener);
    }

    public Composite getActiveComboControl() {
        return mComboControl;
    }
}
