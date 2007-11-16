/****************************************************************************
* Copyright (c) 2006 Jeremy Dowdall
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
*****************************************************************************/

package org.eclipse.nebula.widgets.cdatetime;

import java.text.AttributedCharacterIterator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat.Field;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.swt.widgets.Widget;


/**
 * The CDateTime provides both textual and graphical means for setting
 * the attributes of a java.util.Date class.  As with other components having
 * "combo" in their name, the base of this component is a text box which, if the
 * DROP_DOWN style is set, is complimented by a down-arrow that will open / pop
 * up / drop down a graphical component; for the CDateTime, the drop down
 * component is a CDateTime (automatically set a style appropriate for date
 * format of the text box).  If the style DROP_DOWN is not set, then this combo
 * will NOT show a drop down arrow button, but will instead show a spinner which
 * can be used to increment / decrement the selected date field in the text box.
 * <dt><b>Styles:</b></dt>
 * <dd>BORDER, DROP_DOWN, FOOTER</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection</dd>
 */
public class CDateTime extends AbstractCombo {

	/**
	 * The layout used for a "basic" CDateTime - when it is neither
	 * of style SIMPLE or DROP_DOWN.
	 * Note that there is a spinner, but no button for this style.
	 */
	private class BasicLayout extends Layout {
		
		protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
			Point size = text.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			size.x += spinner.computeSize(SWT.DEFAULT, SWT.DEFAULT).x - spinner.getClientArea().width;

			size.y += textMarginHeight;

			if(wHint != SWT.DEFAULT) {
				size.x = Math.min(size.x, wHint);
			}
			if(hHint != SWT.DEFAULT) {
				size.y = Math.min(size.y, hHint);
			}
			return size;
		}
		
		protected void layout(Composite composite, boolean flushCache) {
			Rectangle cRect = composite.getClientArea();
			if(cRect.isEmpty()) return;
			
			Point tSize = text.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			tSize.y += textMarginHeight;
			
			Point sSize;
			sSize = spinner.computeSize(SWT.DEFAULT, SWT.DEFAULT, flushCache);
			sSize.y = Math.min(sSize.y, Math.min(tSize.y, cRect.height));
			sSize.x = Math.min(sSize.x, cRect.width);
			
			spinner.setBounds(
					cRect.x+cRect.width-sSize.x,
					cRect.y+((tSize.y-sSize.y) / 2),
					sSize.x,
					sSize.y
					);

			text.setBounds(
					cRect.x,
					cRect.y + (win32 ? getBorderWidth() : 0),
					cRect.width-sSize.x+
					(win32 ? (sSize.x - button.computeSize(-1, -1).x) : 
						spinner.getClientArea().width),
					tSize.y
					);

			if(win32) {
				win32Hack.setBounds(
						cRect.x,
						cRect.y,
						cRect.width-button.computeSize(-1, -1).x,
						sSize.y
						);
			}
		}
	}

	/**
	 * A simple class used for editing a field numerically.
	 */
	private class EditField {

		private String buffer;
		private int digits;
		private int count = 0;
		
		public EditField(int digits, int initialValue) {
			this.digits = digits;
			buffer = Integer.toString(initialValue);
		}
		
		boolean addChar(char c) {
			if(Character.isDigit(c)) {
				buffer = (count > 0) ? buffer : "";
				buffer += String.valueOf(c);
				if(buffer.length() > digits) {
					buffer = buffer.substring(buffer.length()-digits, buffer.length());
				}
			}
			return(++count > (digits - 1));
		}

		int getValue() {
			return Integer.parseInt(buffer);
		}
		
		void removeLastCharacter() {
			if(buffer.length() > 0) {
				buffer = buffer.substring(0, buffer.length() - 1);
				count--;
			}
		}
		
		void reset() {
			count = 0;
		}
		
		public String toString() {
			if(buffer.length() < digits) {
				char[] ca = new char[digits - buffer.length()];
				Arrays.fill(ca, '0');
				buffer = String.valueOf(ca).concat(buffer);
			}
			return buffer;
		}
	}
	
	private static final int FIELD_ALL = -2;
	private static final int FIELD_NONE = -1;
	
	private static final int DISCARD		= 0;
	private static final int WRAP 		= 1;
	private static final int BLOCK		= 2;
	
	private AbstractPicker[] pickers;
	private SashForm pickerSash;
	private Composite[] pickerComps;
	private Spinner spinner;
	private Composite win32Hack;
	
	private boolean rightClick = false;
	private Date cancelDate;

	private Calendar calendar;
	private DateFormat df;
	Locale locale;
	
	private Field[] field;
	private int activeField;
	private boolean tabStops = false;

	// Store these values so that the style can be reset automatically
	//  to update everything if/when the locale is changed
	int style;
	String pattern = null;
	int format = -1;

	/**
	 * Delegates events to their appropriate handler
	 */
	private Listener textListener = new Listener() {
		public void handleEvent(Event event) {
			switch (event.type) {
			case SWT.KeyDown:
				handleKey(event);
				break;
			case SWT.MouseDown:
				if(event.button == 1) {
					fieldFromSelection();
				} else if(event.button == 2) {
					fieldNext();
				} else if(event.button == 3) {
					rightClick = true;
				}
				break;
			case SWT.MouseWheel:
				if(event.count > 0) {
					fieldAdjust(1);
				} else {
					fieldAdjust(-1);
				}
				event.doit = false;
				break;
			case SWT.MouseUp:
				if(event.button == 1) {
					fieldFromSelection();
				}
				break;
			case SWT.Traverse:
				handleTraverse(event);
				break;
			case SWT.Verify:
				verify(event);
				break;
			}
		}
	};

	private Point selectionOffset = new Point(0,0); // x = selOffset start, y = selOffset amount
	private EditField editField;
	private String[] separator;
	
	private boolean isNull = false;
	
	private HashMap nullTexts = new HashMap();
	
	/**
	 * Constructs a new instance of this class given its parent and a style value 
	 * describing its behavior and appearance.  The current date and the system's
	 * default locale are used.
	 * @param parent a widget which will be the parent of the new instance (cannot be null)
	 * @param style the style of widget to construct
	 */
	public CDateTime(Composite parent, int style) {
		super(parent, style);

		this.style = style;
		tabStops = (style & CDT.TAB_FIELDS) != 0;
		locale = Locale.getDefault();
		calendar = Calendar.getInstance(this.locale);
		
		setFormat(style);

		if(!isSimple()) {
			if(isDropDown()) {
				setButtonVisibility(CDT.BUTTON_ALWAYS);
			} else {
				setButtonVisibility(CDT.BUTTON_NEVER);
				if((style & CDT.SPINNER) != 0) {
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
							text.forceFocus();
							if(spinner.getSelection() > 25) {
								fieldAdjust(1);
							} else {
								fieldAdjust(-1);
							}
							spinner.setSelection(25);
						}
					});
					if(win32) {
						win32Hack = new Composite(this, SWT.NONE);
						win32Hack.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
						win32Hack.moveAbove(null);
						win32Hack.moveBelow(text);
					}
					setLayout(new BasicLayout());
				}
			}
			
			activeField = -5;
			setActiveField(FIELD_NONE);
			
			// Always start with a null date
			setSelection(null);
			
			addTextListener();
		}
	}

	/**
	 * Adds the textListener for the appropriate SWT events to handle incrementing fields.
	 */
	protected void addTextListener() {
		removeTextListener();
		
		text.addListener(SWT.KeyDown, textListener);
		text.addListener(SWT.MouseDown, textListener);
		text.addListener(SWT.MouseWheel, textListener);
		text.addListener(SWT.MouseUp, textListener);
		text.addListener(SWT.Traverse, textListener);
		text.addListener(SWT.Verify, textListener);
	}

	/**
	 * Adds the textListener for the appropriate SWT events to handle incrementing fields.
	 */
	protected void removeTextListener() {
		text.removeListener(SWT.KeyDown, textListener);
		text.removeListener(SWT.MouseDown, textListener);
		text.removeListener(SWT.MouseWheel, textListener);
		text.removeListener(SWT.MouseUp, textListener);
		text.removeListener(SWT.Traverse, textListener);
		text.removeListener(SWT.Verify, textListener);
	}
	
	/**
	 * Adds the listener to the collection of listeners who will be notified when the 
	 * receiver's selection changes, by sending it one of the messages defined in the 
	 * <code>SelectionListener</code> interface.
	 * <p>
	 * <code>widgetSelected</code> is called when the selection (date/time) changes.
	 * <code>widgetDefaultSelected</code> is when ENTER is pressed the text box.
	 * </p>
	 * The event's data field will contain the newly selected Date object.<br>
	 * The event's detail field will contain which Calendar Field was changed
	 * @param listener the listener which should be notified
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * @see SelectionListener
	 * @see #removeSelectionListener
	 * @see SelectionEvent
	 */
	public void addSelectionListener(SelectionListener listener) {
		checkWidget();
		if(listener != null) {
			TypedListener typedListener = new TypedListener (listener);
			addListener (SWT.Selection, typedListener);
			addListener (SWT.DefaultSelection, typedListener);
		}
	}

	/**
	 * The selection has been set by the graphical selector (picker).  Updates
	 * the open state, the local calendar time, the text, and then fires
	 * the appropriate selection event.
	 * @param field
	 * @param defaultSelection
	 */
	void setSelectionFromPicker(int field, boolean defaultSelection) {
		if(defaultSelection && isOpen()) {
			cancelDate = null;
			setOpen(false);
		}
		if (isNull) {
			isNull = false;
		}
		calendar.setTime(getPickerSelection());
		updateText();
		fireSelectionChanged(field, defaultSelection);
	}

	/**
	 * If a field is being edited (via keyboard), set the edit value to the 
	 * active field of the calendar.  Reset the count of the EditField so that a
	 * subsequent key press will overwrite its contents;
	 * @return true if the commit was successfull (the value was valid for the field) or there
	 * was no commit to be made (editField is null), false otherwise
	 */
	private boolean commitEditField() {
		if(editField != null) {
			int cf = getCalendarField();
			int val = editField.getValue();
			editField.reset();
			if(cf == Calendar.MONTH) {
				val--; // TODO: adjust for zero-based month field?
			}
			return fieldSet(cf, val, DISCARD);
		}
		return true;
	}
	
	protected void contentShellEvents(Event event) {
		if(isOpen()) {
			if(SWT.Deactivate == event.type) {
				Point p = Display.getCurrent().getCursorLocation();
				Rectangle r = Display.getCurrent().map(button.getParent(), null, button.getBounds());
				if(r.contains(p)) {
					cancelDate = null;
				}
			}
		}
		super.contentShellEvents(event);
	}
	
	private Date getPickerSelection() {
		Calendar cal = Calendar.getInstance(locale);
		for(int i = 0; i < pickers.length; i++) {
			int[] fa = pickers[i].getFields();
			int[] va = pickers[i].getFieldValues();
			for(int j = 0; j < fa.length; j++) {
				cal.set(fa[j], va[j]);
			}
		}
		return cal.getTime();
	}

	/**
	 * Adds the given amount to the active field, if there is one
	 */
	private void fieldAdjust(int amount) {
		if(isNull) {
			setSelection(calendar.getTime());
		} else {
			int cf = getCalendarField();
			if(cf >= 0) {
				fieldSet(cf, calendar.get(cf) + amount, WRAP);
			}
		}
	}

	/**
	 * Sets the active field from the select of the text box
	 */
	private void fieldFromSelection() {
		if(isNull) {
			setActiveField(FIELD_ALL);
		} else {
			Point sel = text.getSelection();
			AttributedCharacterIterator aci = df.formatToCharacterIterator(calendar.getTime());
			if(sel.x > selectionOffset.x) sel.x += selectionOffset.y;
			aci.setIndex(sel.x);
			Object[] oa = aci.getAttributes().keySet().toArray();
			if(oa.length == 0 && sel.x > 0) {
				sel.x -= 1;
				aci.setIndex(sel.x);
				oa = aci.getAttributes().keySet().toArray();
			}
			if(oa.length > 0) {
				for(int i = 0; i < field.length; i++) {
					if(oa[0].equals(field[i])) {
						setActiveField(i);
						break;
					}
				}
				updateText();
			}
		}
	}

	/**
	 * Sets the active field to the next field; wraps if necessary and sets to last
	 * field if there is no current active field
	 */
	private void fieldNext() {
		if(activeField >= 0 && activeField < field.length - 1) {
			setActiveField(activeField + 1);
		} else if(activeField == FIELD_ALL) {
			setActiveField(field.length - 1);
		} else {
			setActiveField(0);
		}
		updateText();
	}
	
	/**
	 * Sets the active field to the previous field; wraps if necessary and sets to first
	 * field if there is no current active field
	 */
	private void fieldPrev() {
		if(activeField > 0 && activeField < field.length) {
			setActiveField(activeField - 1);
		} else if(activeField == FIELD_ALL) {
			setActiveField(0);
		} else {
			setActiveField(field.length - 1);
		}
		updateText();
	}
	
	/**
	 * Sets the given calendar field to the given value.<br>
	 * <b>NOTE:</b> This is NOT the active field but a field in the
	 * "calendar" variable.
	 * @param calendarField the field of calendar to set
	 * @param value the value to set it to
	 * @param style the of set to perform; if the value is valid for the given calendarField then
	 * this has no affect, otherwise it will take an action according to this style int:
	 * <ul>
	 * 	<li>DISCARD: the value will be discarded and the method returns without performing and action</li>
	 * 	<li>WRAP: if value is higher than its maximum it will be set to its minimum, and visa versa</li>
	 * 	<li>BLOCK: if value is higher than its maximum it will be set to its maximum, and visa versa</li>
	 * </ul>
	 * @return true if the field was set, false otherwise (as is possible with a DISCARD style)
	 */
	private boolean fieldSet(int calendarField, int value, int style) {
		if(calendarField >= 0) {
			if(value > calendar.getActualMaximum(calendarField)) {
				if(style == DISCARD) {
					return false;
				} else if(style == WRAP) {
					value = calendar.getActualMinimum(calendarField);
				} else if(style == BLOCK) {
					value = calendar.getActualMaximum(calendarField);
				}
			} else if(value < calendar.getActualMinimum(calendarField)) {
				if(style == DISCARD) {
					return false;
				} else if(style == WRAP) {
					value = calendar.getActualMaximum(calendarField);
				} else if(style == BLOCK) {
					value = calendar.getActualMinimum(calendarField);
				}
			}
			calendar.set(calendarField, value);
			updateText();
			updatePickerSelection(calendar.getTime());
			fireSelectionChanged(calendarField, false);
		}
		return true;
	}
	
	/**
	 * Notifies listeners that the selected date for this CDateTime has changed,
	 * either by the text box or the drop down CDateTime.
	 * @param field the Calendar Field which caused the change, or -1 if <code>setTime</code>
	 * was called (thus setting all Calendar Fields)
	 * @param defaultSelection whether or not this event should be a "default" event, currently
	 * only set to true if the event is triggered by a Carriage Return.
	 */
	private void fireSelectionChanged(int field, boolean defaultSelection) {
		Event event = new Event();
		event.data = getSelection();
		event.detail = field;
		notifyListeners(defaultSelection ? SWT.DefaultSelection : SWT.Selection, event);
	}

//	/**
//	 * Returns an array of all locales which are fully supported for the given style.
//	 * If the style is of DROP_DOWN, then the fully supported Locales will be limited by the
//	 * CDateTime and this method will return the array obtained by calling
//	 * <code>CDateTime.getAvailableLocales()</code>.  If the style is not of DROP_DOWN then the
//	 * list of fully supported Locales is limited only by the system and the array returned is from 
//	 * <code>Calendar.getAvailableLocales()</code>.
//	 * @return an array of fully supported Locale objects
//	 * @see CDateTime#getAvailableLocales()
//	 * @see Calendar#getAvailableLocales()
//	 */
//	public Locale[] getAvailableLocales() {
//		 TODO: picker locales...
//		if(picker != null) return picker.getAvailableLocales();
//		return Calendar.getAvailableLocales();
//	}
	
	/**
	 * Gets the calendar field corresponding to the active field, if there is one.
	 * @return an int representing the calendar field, -1 if there isn't one.
	 */
	private int getCalendarField() {
		return hasField(activeField) ? getCalendarField(field[activeField]) : -1;
	}

	/**
	 * Get the pattern of this CDateTime as used to set its format.  If the format was NOT
	 * set using <code>setFormat(String)</code> this will return <code>null</code>.
	 * @return the pattern, null if there isn't one
	 * @see SimpleDateFormat
	 * @see #setFormat(int)
	 * @see #setPattern(String)
	 */
	public String getPattern() {
		checkWidget();
		return pattern;
	}
	
	/**
	 * The locale currently in use by this CDateTime.
	 * @return the locale
	 * @see #setLocale(Locale)
	 * @see CDateTime#setLocale(Locale)
	 */
	public Locale getLocale() {
		checkWidget();
		return locale;
	}
	
	/**
	 * Get the <code>java.util.Date</code> that is currently selected by this
	 * CDateTime widget.<br>
	 * Note that if a field is being edited, and has not yet been committed,
	 * then this value may not represent what is displayed in the text box.
	 * @return the date
	 * @see #setSelection(Date)
	 */
	public Date getSelection() {
		checkWidget();
		return isNull ? null : calendar.getTime();
	}

	public String getText() {
		return isNull ? 
				getNullText(locale) :
					df.format(calendar.getTime());
	}
	
	protected void handleFocus(int type, Widget widget) {
		if(isDisposed()) return;
		if(SWT.FocusIn == type) {
			if(!hasFocus) {
				rightClick = false;
				setActiveField(tabStops ? 0 : FIELD_ALL);
				updateText();
			}
		}
		
		super.handleFocus(type, widget);
		
		if(isDisposed()) return;
		if(SWT.FocusOut == type) {
			if(!hasFocus) {
				if(!rightClick) {
					setActiveField(FIELD_NONE);
					updateText();
				}
			}
		}
	}

	/**
	 * The Key event handler
	 * @param e the event
	 */
	private void handleKey(Event e) {
		if(SWT.DEL == e.keyCode) {
			e.doit = false;
			setSelection(null);
			fireSelectionChanged(FIELD_ALL, false);
		} else if(!hasField(activeField) && !isNull) {
			e.doit = false;
		} else {
			switch (e.keyCode) {
			case '-':
			case SWT.ARROW_DOWN:
			case SWT.KEYPAD_SUBTRACT:
				fieldAdjust(-1);
				break;
			case '=':
			case '+':
			case SWT.ARROW_UP:
			case SWT.KEYPAD_ADD:
				fieldAdjust(1);
				break;
			case SWT.BS:
				if(editField != null) editField.removeLastCharacter();
				break;
			default:
				if(hasField(activeField) && activeField + 1 < separator.length &&
						String.valueOf(e.character).equals(separator[activeField+1])) {
					fieldNext();
				}
			}
		}
	}

	/**
	 * The Travers event handler.  Note that ARROW_UP and ARROW_DOWN are
	 * handled in the <code>handleKey</code> method.
	 * @param e the event
	 */
	private void handleTraverse(Event e) {
		int cf = getCalendarField();
		switch (e.keyCode) {
		case SWT.ARROW_LEFT:
			fieldPrev();
			fireSelectionChanged(cf, false);
			break;
		case SWT.ARROW_RIGHT:
			fieldNext();
			fireSelectionChanged(cf, false);
			break;
		case SWT.CR:
			fieldNext();
			fireSelectionChanged(cf, true);
			break;
		case SWT.TAB:
			if(tabStops) {
				if(e.stateMask == SWT.SHIFT) {
					if(activeField != 0) {
						e.doit = false;
						if(activeField < 0) {
							fieldNext();
						} else {
							fieldPrev();
						}
						fireSelectionChanged(cf, false);
					}
				} else {
					if(activeField != field.length-1) {
						e.doit = false;
						if(activeField < 0) {
							fieldPrev();
						} else {
							fieldNext();
						}
						fireSelectionChanged(cf, false);
					}
				}
			}
			break;
		default:
		}
	}

	/**
	 * Determines if the given field number is backed by a real field.
	 * @param field the field number to check
	 * @return true if the given field number corresponds to a field in the field array
	 */
	private boolean hasField(int field) {
		return field >= 0 && field <= this.field.length;
	}

//	protected boolean isNull() {
//		return isNull;
//	}
	
	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when the receiver's selection changes.
	 * @param listener the listener which should no longer be notified
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that 
	 *    created the receiver</li>
	 * </ul>
	 * @see SelectionListener
	 * @see #addSelectionListener
	 */
	public void removeSelectionListener(SelectionListener listener) {
		checkWidget ();
		if(listener != null) {
			removeListener(SWT.Selection, listener);
			removeListener(SWT.DefaultSelection, listener);
		}
	}

	/**
	 * Sets the active field, which may or may not be a real field (it may also
	 * be <code>FIELD_ALL</code> or <code>FIELD_NONE</code>
	 * @param field the field to be set active
	 * @see CDateTime#hasField(int)
	 */
	private void setActiveField(int field) {
		if(isNull) {
			activeField = FIELD_ALL;
		} else if(activeField != field) {
			commitEditField();
			editField = null;
			activeField = field;
			if(/*!win32 &&*/ spinner != null) {
				if(hasField(field)) {
					spinner.setEnabled(true);
					// TODO: When Spinner's ClientArea is covered, it doesn't show enabled until moused over
					if(gtk) {
						spinner.forceFocus();
						text.forceFocus();
					}
				} else {
					spinner.setEnabled(false);
				}
			}
		}
	}
	
	/**
	 * 
	 */
	public void setEditable(boolean editable) {
		super.setEditable(editable);
		
		if (checkText()) {
			if (editable) {
				addTextListener();
			}
			else {
				removeTextListener();
			}
		}
	}

	/**
	 * Set the date and time format of this CDateTime uses style constants which correspond
	 * to the various forms of DateFormat.getXxxInstance(int).
	 * <dt><b>Valid Styles:</b></dt>
	 * <dd>DATE_SHORT, DATE_MEDIUM, DATE_LONG, TIME_SHORT, TIME_MEDIUM</dd>
	 * <p>Styles are bitwise OR'ed together, but only one "DATE" and one "TIME" may be set at a time.</p>
	 * Examples:<br>
	 * </code>setFormat(CDT.DATE_LONG);</code><br />
	 * </code>setFormat(CDT.DATE_SHORT | CDT.TIME_MEDIUM);</code><br />
	 * @param format the bitwise OR'ed Date and Time format to be set
	 * @see #getPattern()
	 * @see #setPattern(String)
	 */
	public void setFormat(int format) throws IllegalArgumentException {
		checkWidget();
		int dateStyle = (format & CDT.DATE_SHORT) != 0 ? DateFormat.SHORT :
			(format & CDT.DATE_MEDIUM) != 0 ? DateFormat.MEDIUM :
				(format & CDT.DATE_LONG) != 0 ? DateFormat.LONG : -1;
		int timeStyle = (format & CDT.TIME_SHORT) != 0 ? DateFormat.SHORT :
			(format & CDT.TIME_MEDIUM) != 0 ? DateFormat.MEDIUM : -1;
		String str = null;
//		try {
		if(dateStyle != -1 && timeStyle != -1) {
			str = ((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(dateStyle, timeStyle, locale)).toPattern();
		} else if(dateStyle != -1) {
			str = ((SimpleDateFormat) SimpleDateFormat.getDateInstance(dateStyle, locale)).toPattern();
		} else if(timeStyle != -1) {
			str = ((SimpleDateFormat) SimpleDateFormat.getTimeInstance(timeStyle, locale)).toPattern();
		} else if(pattern == null) {  // first call, so set to default
			format = CDT.DATE_SHORT;
			str = ((SimpleDateFormat) SimpleDateFormat.getDateInstance(DateFormat.SHORT, locale)).toPattern();
		}
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		}
		if(str != null) {
			this.format = format;
			setPattern(str);
		}
	}
	
	public void setOpen(boolean open) {
		checkWidget();
		if(open) {
			cancelDate = getSelection();
			updatePickerSelection(calendar.getTime());
		} else if(cancelDate != null){
			setSelection(cancelDate);
			cancelDate = null;
			updateText();
		}
		super.setOpen(open);
	}
	
	/**
	 * Set the style of this CDateTime to work with dates and / or times 
	 * as determined by the given pattern. This will set the fields shown in the
	 * text box and, if <code>DROP_DOWN</code> style is set, the fields of the
	 * drop down component.<br>
	 * This method is backed by an implementation of SimpleDateFormat, and as such,
	 * any string pattern which is valid for SimpleDateFormat may be used.
	 * Examples (US Locale):<br>
	 * </code>setPattern("MM/dd/yyyy h:mm a");</code><br />
	 * </code>setPattern("'Meeting @' h:mm a 'on' EEEE, MMM dd, yyyy");</code><br />
	 * @param pattern the pattern to use, if it is invalid, the original is restored
	 * @throws IllegalArgumentException
	 * @see SimpleDateFormat
	 * @see #getPattern()
	 * @see #setFormat(int)
	 */
	public void setPattern(String pattern) throws IllegalArgumentException {
		checkWidget();
		if(isOpen()) setOpen(false);
//		if(pattern != null && pattern.trim().length() > 0) {
			df = new SimpleDateFormat(pattern, locale);
			if(updateFields()) {
				this.pattern = pattern;
				updateText();
				updatePickerFormat();
			} else {
				throw new IllegalArgumentException("Problem setting pattern: \"" + pattern + "\"");
			}
//		}
	}
	
	/**
	 * Sets the Locale to be used by this CDateTime and causes all affected 
	 * attributes to be updated<br>
	 * If the provided locale is the same as the current locale then this method simply
	 * returns.  If the provided Locale is null then this CDateTime will use
	 * the system's default locale.<br>
	 * If this <code>CDateTime</code> is of style <code>DROP_DOWN</code>
	 * then the associated <code>CDateTime</code> will be set to the same locale.
	 * @param locale the Locale, or null to use the system's default
	 * @see #getLocale()
	 */
	public void setLocale(Locale locale) {
		checkWidget();
		if(locale == null) locale = Locale.getDefault();
		if(!this.locale.equals(locale)) {
			this.locale = locale;
			if(format > 0) {
				setFormat(format);
			} else {
				setPattern(pattern);
			}
		}
	}
	
	protected void setModifyEventProperties(Event e) {	
		e.data = calendar.getTime();
	}

	/**
	 * Set the selection for this CDateTime to that of the provided
	 * <code>Date</code> object.<br>
	 * This method will update the text box and, if the <code>DROP_DOWN</code>
	 * style is set, the selection of the associated drop down CDateTime.
	 * @param date the <code>Date</code> object to use for the new selection
	 * @see #getSelection()
	 */
	public void setSelection(Date date) {
		checkWidget();
//		if(date != null && calendar.getTime().equals(date)) return;
		if(date == null) {
			isNull = true;
		} else {
			isNull = false;
			calendar.setTime(date);
		}
		updateText();
		if (date != null) {
			updatePickerSelection(date);
		}
	}
	
	/**
	 * If style is neither SIMPLE or DROP_DOWN, then this method simply returns,
	 * otherwise it sets the format of the picker.
	 * @see CDateTime#setPattern(int)
	 */
	private void updatePickerFormat() {
		if(!isSimple() && !isDropDown()) return;
		if(pickerSash != null && !pickerSash.isDisposed()) {
			pickerSash.dispose();
		}
		pickerSash = null;
		pickerSash = new SashForm(getParentForContent(), SWT.HORIZONTAL);
		pickerSash.setLayout(new FillLayout());
		setContent(pickerSash);

		boolean date = false;
		boolean time = false;
		int[] fa = new int[field.length];
		for(int i = 0; i < fa.length; i++) {
			fa[i] = getCalendarField(field[i]);
			switch(fa[i]) {
			case Calendar.AM_PM:
			case Calendar.HOUR:
			case Calendar.HOUR_OF_DAY:
			case Calendar.MILLISECOND:
			case Calendar.MINUTE:
			case Calendar.SECOND:
				time = true;
				break;
			case Calendar.DAY_OF_MONTH:
			case Calendar.DAY_OF_WEEK:
			case Calendar.DAY_OF_WEEK_IN_MONTH:
			case Calendar.DAY_OF_YEAR:
			case Calendar.ERA:
			case Calendar.MONTH:
			case Calendar.WEEK_OF_MONTH:
			case Calendar.WEEK_OF_YEAR:
			case Calendar.YEAR:
				date = true;
				break;
			default:
				break;
			}
		}

		pickerComps = new Composite[date && time ? 2 : 1];
		pickers = new AbstractPicker[date && time ? 2 : 1];

		if(date) {
			pickerComps[0] = new Composite(pickerSash, SWT.NONE);
			pickerComps[0].setLayout(new FillLayout());
			pickers[0] = new Picker_Calendar(pickerComps[0], this, calendar.getTime());
		}

		if(time) {
			int index = date ? 1 : 0;
			pickerComps[index] = new Composite(pickerSash, SWT.NONE);
			pickerComps[index].setLayout(new FillLayout());
			if((style & CDT.CLOCK_DISCRETE) != 0) {
				pickers[index] = new Picker_Clock_Discrete(pickerComps[index], this, calendar.getTime());
				if(date) {
					int w1 = pickerSash.getSize().x;
					int w2 = pickers[1].computeSize(-1, -1).x;
					w1 = (w1 > w2) ? w1-w2 : pickers[0].computeSize(-1, -1).x;
					pickerSash.setWeights(new int[] { w1, w2 });
				}
			} else {
				pickers[index] = new Picker_Clock_Analog(pickerComps[index], this, calendar.getTime());
			}
		}
		
		for(int i = 0; i < pickers.length; i++) {
			pickers[i].setFields(fa);
		}

		if(pickerComps.length == 1) pickerSash.setMaximizedControl(pickerComps[0]);
		pickers[0].setFocus();
		pickerSash.pack(true);
		if(date && time && (style & CDT.CLOCK_DISCRETE) != 0) {
			int w1 = pickers[0].computeSize(-1, -1).x;
			int w2 = pickers[1].computeSize(-1, -1).x;
			w1 = (w1 > w2) ? w1-w2 : pickers[0].computeSize(-1, -1).x;
			pickerSash.setWeights(new int[] { w1, w2 });
		}
	}

	private int getCalendarField(Field field) {
		int cf = field.getCalendarField();
		if(cf < 0 && field.toString().indexOf("hour 1") > -1) {
			cf = Calendar.HOUR;
		}
		return cf;
	}
	
	/**
	 * inspects all of the calendar fields in the <code>field</code> array to determine what
	 * style is appropriate and then sets that style to the picker using the setPickerStyle method.<br>
	 */
	private boolean updateFields() {
		Field[] bak = new Field[(field == null) ? 0 : field.length];
		if(bak.length > 0) System.arraycopy(field, 0, bak, 0, field.length);
		
		AttributedCharacterIterator aci = df.formatToCharacterIterator(calendar.getTime());
		field = new Field[aci.getAllAttributeKeys().size()];
		separator = new String[field.length+1]; // there can be a separator before and after
		int i = 0;
		Object last = null;
		for(char c = aci.first(); c != AttributedCharacterIterator.DONE; c = aci.next()) {
			Object[] oa = aci.getAttributes().keySet().toArray();
			if(oa.length > 0) {
				if(oa[0] != last && i < field.length) { // TODO: fail the update if i >= field.length?
					if(getCalendarField((Field) oa[0]) < 0) {
						if(bak.length > 0) {
							field = new Field[bak.length];
							System.arraycopy(bak, 0, field, 0, bak.length);
						}
						return false;
					} else {
						field[i] = (Field) oa[0];
						last = oa[0];
					}
					i++;
				}
			} else {
				if(separator[i] == null) separator[i] = String.valueOf(c);
			}
		}

		df.setLenient(false);
		setActiveField(FIELD_NONE);
		return true;
	}
	
	/**
	 * set the pickers' selection with the local calendar time
	 */
	private void updatePickerSelection(Date date) {
		if(pickers == null) return;
		for(int i = 0; i < pickers.length; i++) {
			pickers[i].updateSelection(date);
		}
	}

	/**
	 * This is the only way that text is set to the text box.<br>
	 * The selection is also set here (corresponding to the active field) as well as
	 * if a field is being edited, it's "edit text" is inserted for display.
	 */
	private void updateText() {
		String buffer = isNull ? 
				getNullText(locale) : 
					df.format(calendar.getTime());

		int s0 = 0;
		int s1 = 0;

		if(activeField == FIELD_ALL || isNull) {
			s0 = 0;
			s1 = buffer.length();
		} else if(activeField >= 0 && activeField < field.length){
			AttributedCharacterIterator aci = df.formatToCharacterIterator(calendar.getTime());
			for(char c = aci.first(); c != AttributedCharacterIterator.DONE; c = aci.next()) {
				if(aci.getAttribute(field[activeField]) != null) {
					s0 = aci.getRunStart();
					s1 = aci.getRunLimit();
					if(editField != null) {
						String str = editField.toString();
						buffer = buffer.substring(0, s0) + str + buffer.substring(s1);
						int oldS1 = s1;
						s1 = s0 +str.length();
						selectionOffset.x = Math.min(oldS1, s1);
						selectionOffset.y = (oldS1 - s0) - str.length();
					} else {
						selectionOffset.x = buffer.length()+1;
						selectionOffset.y = 0;
					}
					break;
				}
			}
		} else {
			setActiveField(FIELD_NONE);
		}

		final String string = buffer;
		final int selStart = s0;
		final int selEnd = s1;

		getDisplay().asyncExec(new Runnable() {
			public void run() {
				if((text != null) && (!text.isDisposed())) {
					if(!string.equals(text.getText())) {
						text.removeListener(SWT.Verify, textListener);
						text.setText(string);
						text.addListener(SWT.Verify, textListener);
					}
					text.setSelection(selStart, selEnd);
				}
			}
		});
	}

	/**
	 * The Verify Event handler.<br>
	 * <b>EVERYTHING</b> is blocked via this handler (Event.doit is set to false).
	 * Depending upon the input, a course of action is determined and the displayed 
	 * text is updated via the <code>updateText()</code> method.
	 * @param e the event
	 * @see CDateTime#updateText()
	 */
	private void verify(Event e) {
		e.doit = false;
		if(field.length == 0 || activeField == FIELD_NONE) return;
		
		char c = e.character;
		if(((e.text.length() == 1) && String.valueOf(c).equals(e.text) && Character.isDigit(c)) || 
				(e.text.length() > 1) ) {
			if(e.text.length() == 1) {
				if(isNull) isNull = false;
				if(activeField == FIELD_ALL) setActiveField(0);
				if(editField == null) {
					int cf = getCalendarField();
					if(cf >= 0) {
						int digits;
						switch(cf) {
						case Calendar.YEAR: 
							digits = 4;
							break;
						case Calendar.DAY_OF_YEAR:
							digits = 3;
							break;
						case Calendar.AM_PM:
						case Calendar.DAY_OF_WEEK:
						case Calendar.ERA:
							digits = 1;
							break;
						default:
							digits = 2;
						}
						editField = new EditField(digits, calendar.get(cf));
					} else {
						return;
					}
				}
				if(editField.addChar(c)) {
					if(commitEditField()) {
						fieldNext();
					} else {
						editField = null;
						updateText();
					}
				}
				updatePickerSelection(calendar.getTime());
			} else {
				try {
					setSelection(df.parse(e.text));
					fireSelectionChanged(-1, false);
				} catch (ParseException pe) {
				}
			}
		}
		updateText();
	}
	
	/**
	 * Gets the current value of the text that will be displayed for a null selection.
	 * 
	 * @param locale the locale for which to the text will be displayed
	 * @return the appropriate text for a null selection in the given locale
	 * @throws NullPointerException if locale == null
	 */
	public String getNullText(Locale locale) {
		if (locale == null) {
			throw new NullPointerException("Locale must be non-null to request null text");
		}
		
		if (nullTexts.containsKey(locale)) {
			return (String) nullTexts.get(locale);
		}
		else {
			String nullText = Messages.getString("null_text", locale); //$NON-NLS-1$
			if ("!null_text!".equals(nullText) && nullTexts.containsKey(null)) { //$NON-NLS-1$
				return (String) nullTexts.get(null);
			}
			else {
				return nullText;
			}
		}
	}
	
	/**
	 * Overrides the text that will be displayed for a null selection in the given locale.
	 * If the locale argument is null, the given text will be used for any locale that
	 * does not have a default value for null text, but it will not override the text
	 * for locales with a specific default value, such as en_US etc.
	 * 
	 * @param text the text to display in this widget for a null selection -- null is treated as an empty string
	 * @param locale the locale in which the given text should be used, or null
	 */
	public void setNullText(String text, Locale locale) {
		if (text == null) {
			text = "";  //$NON-NLS-1$
		}
		nullTexts.put(locale, text);
	}
}
