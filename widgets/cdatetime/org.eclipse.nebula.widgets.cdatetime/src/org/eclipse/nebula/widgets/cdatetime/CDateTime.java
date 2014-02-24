/****************************************************************************
 * Copyright (c) 2006-2008 Jeremy Dowdall
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *    Wim Jongman - https://bugs.eclipse.org/bugs/show_bug.cgi?id=362181
 *    Scott Klein - https://bugs.eclipse.org/bugs/show_bug.cgi?id=370605
 *    Baruch Youssin - https://bugs.eclipse.org/bugs/show_bug.cgi?id=261414
 *****************************************************************************/

package org.eclipse.nebula.widgets.cdatetime;

import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat.Field;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.eclipse.nebula.cwt.base.BaseCombo;
import org.eclipse.nebula.cwt.v.VButton;
import org.eclipse.nebula.cwt.v.VCanvas;
import org.eclipse.nebula.cwt.v.VGridLayout;
import org.eclipse.nebula.cwt.v.VLabel;
import org.eclipse.nebula.cwt.v.VLayout;
import org.eclipse.nebula.cwt.v.VNative;
import org.eclipse.nebula.cwt.v.VPanel;
import org.eclipse.nebula.cwt.v.VTracker;
import org.eclipse.nebula.widgets.cdatetime.CDT.PickerPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TypedListener;

/**
 * The CDateTime provides both textual and graphical means selecting a date.<br/>
 * As with other combo type widgets, there are three basic styles:
 * <ul>
 * <li>Text only (default)</li>
 * <li>Graphical only (CDT.SIMPLE)</li>
 * <li>Combo - a text selector with a drop-down graphical selector
 * (CDT.DROP_DOWN)</li>
 * </ul>
 * <p> 
 * Styles are set using the constants provided in the CDT class.
 * </p>
 * 
 * @see CDT
 */
public class CDateTime extends BaseCombo {

	/**
	 * A simple class used for editing a field numerically.
	 */
	private class EditField {

		private String buffer;
		private int digits;
		private int count = 0;

		EditField(int digits, int initialValue) {
			this.digits = digits;
			buffer = Integer.toString(initialValue);
		}

		/**
		 * Adds a character if it is a digit; in case the field exceeds its capacity, the oldest character is
		 * dropped from the buffer.  Non-digits are dropped.
		 * @param c
		 * @return true if the new character is a digit and with its addition the active field
		 * reaches or exceeds its capacity, false otherwise
		 */
		boolean addChar(char c) {
			if (Character.isDigit(c)) {
				buffer = (count > 0) ? buffer : ""; //$NON-NLS-1$
				buffer += String.valueOf(c);
				if (buffer.length() > digits) {
					buffer = buffer.substring(buffer.length() - digits,
							buffer.length());
				}
			}
			return (++count > (digits - 1));
		}

		int getValue() {
			return Integer.parseInt(buffer);
		}

		void removeLastCharacter() {
			if (buffer.length() > 0) {
				buffer = buffer.substring(0, buffer.length() - 1);
				count--;
			}
		}

		void reset() {
			count = 0;
		}

		public String toString() {
			if (buffer.length() < digits) {
				char[] ca = new char[digits - buffer.length()];
				Arrays.fill(ca, '0');
				buffer = String.valueOf(ca).concat(buffer);
			}
			return buffer;
		}
	}

	/**
	 * The layout used for a "basic" CDateTime - when it is neither of style
	 * SIMPLE or DROP_DOWN - with style of SPINNER.<br>
	 * Note that there is a spinner, but no button for this style.
	 */
	class SpinnerLayout extends VLayout {

		protected Point computeSize(VPanel panel, int wHint, int hHint,
				boolean flushCache) {
			Point size = text.computeSize(SWT.DEFAULT, SWT.DEFAULT);

			Rectangle sRect = spinner.getControl().computeTrim(0, 0, 0, 0);
			int sWidth = sRect.x + sRect.width
					- (2 * spinner.getControl().getBorderWidth()) + 1;

			size.x += sWidth;
			size.x++;
			size.y += textMarginHeight;

			if (wHint != SWT.DEFAULT) {
				size.x = Math.min(size.x, wHint);
			}
			if (hHint != SWT.DEFAULT) {
				size.y = Math.min(size.y, hHint);
			}
			return size;
		}

		protected void layout(VPanel panel, boolean flushCache) {
			Rectangle cRect = panel.getClientArea();
			if (cRect.isEmpty())
				return;

			Point tSize = text.getControl().computeSize(SWT.DEFAULT,
					SWT.DEFAULT);
			tSize.y += textMarginHeight;

			spinner.setBounds(cRect.x, cRect.y, cRect.width, tSize.y);

			Rectangle sRect = spinner.getControl().computeTrim(0, 0, 0, 0);
			int sWidth = sRect.x + sRect.width
					- (2 * spinner.getControl().getBorderWidth()) + 1;

			tSize.x = cRect.width - sWidth;

			text.setBounds(cRect.x, cRect.y + getBorderWidth(), tSize.x,
					tSize.y);
		}
	}

	private static final int FIELD_NONE = -1;

	private static final int DISCARD = 0;
	private static final int WRAP = 1;
	private static final int BLOCK = 2;

	private static int convertStyle(int style) {
		int rstyle = SWT.NONE;
		if ((style & CDT.DROP_DOWN) != 0) {
			rstyle |= SWT.DROP_DOWN;
		}
		if ((style & CDT.SIMPLE) != 0) {
			rstyle |= SWT.SIMPLE;
		}
		if ((style & CDT.READ_ONLY) != 0) {
			rstyle |= SWT.READ_ONLY;
		}
		if ((style & CDT.BUTTON_LEFT) != 0) {
			rstyle |= SWT.LEFT;
		}
		if ((style & CDT.TEXT_LEAD) != 0) {
			rstyle |= SWT.LEAD;
		}
		if ((style & CDT.BORDER) != 0) {
			rstyle |= SWT.BORDER;
		}
		if (win32) {
			rstyle |= SWT.DOUBLE_BUFFERED;
		}
		return rstyle;
	}

	VPanel picker;

	VNative<Spinner> spinner;
	boolean internalFocusShift = false;
	boolean rightClick = false;

	private Date cancelDate;
	private Calendar calendar;
	private DateFormat df;
	Locale locale;

	TimeZone timezone;
	Field[] field;
	int activeField;

	private boolean tabStops = false;
	// Store these values so that the style can be reset automatically
	// to update everything if/when the locale is changed
	int style;
	String pattern = null;

	int format = -1;

	private CDateTimePainter painter;

	/**
	 * Delegates events to their appropriate handler
	 */
	Listener textListener = new Listener() {
		public void handleEvent(Event event) {
			switch (event.type) {
			case SWT.FocusIn:
				rightClick = false;
				if (internalFocusShift) {
					if (activeField < 0) {
						fieldFirst();
						updateText();
					}
				} else {
					if (VTracker.getLastTraverse() == SWT.TRAVERSE_TAB_PREVIOUS) {
						fieldLast();
					} else {
						fieldFirst();
					}
					updateText();
				}
				break;
			case SWT.FocusOut:
				if (!rightClick && !internalFocusShift) {
					commitEditField();
					updateText();
				}
				break;
			case SWT.KeyDown:
				handleKey(event);
				break;
			case SWT.MouseDown:
				if (event.button == 1) {
					fieldFromTextSelection();
				} else if (event.button == 2) {
					fieldNext();
				} else if (event.button == 3) {
					rightClick = true;
				}
				break;
			case SWT.MouseWheel:
				if (event.count > 0) {
					fieldAdjust(1);
				} else {
					fieldAdjust(-1);
				}
				event.doit = false;
				break;
			case SWT.MouseUp:
				if (event.button == 1) {
					fieldFromTextSelection();
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
	private Point textSelectionOffset = new Point(0, 0); // x = selOffset start,
															// y = selOffset
															// amount
	private EditField editField;

	private String[] separator;
	private int[] calendarFields;
	private boolean isTime;
	private boolean isDate;
	// private boolean isNull = true;
	private String nullText = null;

	private boolean defaultNullText = true;
	private boolean singleSelection;

	// private boolean dragSelection;
	private Date[] selection = new Date[0];

	private boolean scrollable = true;

	CDateTimeBuilder builder;

	VPanel pickerPanel;

	private TimeZone[] allowedTimezones;

	/**
	 * Constructs a new instance of this class given its parent and a style
	 * value describing its behavior and appearance. The current date and the
	 * system's default locale are used.
	 * 
	 * @param parent
	 *            a widget which will be the parent of the new instance (cannot
	 *            be null)
	 * @param style
	 *            the style of widget to construct
	 */
	public CDateTime(Composite parent, int style) {
		super(parent, convertStyle(style));
		init(style);
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when the receiver's selection changes, by sending it one of the messages
	 * defined in the <code>SelectionListener</code> interface.
	 * <p>
	 * <code>widgetSelected</code> is called when the selection (date/time)
	 * changes. <code>widgetDefaultSelected</code> is when ENTER is pressed the
	 * text box.
	 * </p>
	 * The event's data field will contain the newly selected Date object.<br>
	 * The event's detail field will contain which Calendar Field was changed
	 * 
	 * @param listener
	 *            the listener which should be notified
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * @see SelectionListener
	 * @see #removeSelectionListener
	 * @see SelectionEvent
	 */
	public void addSelectionListener(SelectionListener listener) {
		if (listener != null) {
			TypedListener typedListener = new TypedListener(listener);
			addListener(SWT.Selection, typedListener);
			addListener(SWT.DefaultSelection, typedListener);
		}
	}

	/**
	 * Adds the textListener for the appropriate SWT events to handle
	 * incrementing fields.
	 */
	protected void addTextListener() {
		removeTextListener();

		Text control = text.getControl();
		control.addListener(SWT.FocusIn, textListener);
		control.addListener(SWT.FocusOut, textListener);
		control.addListener(SWT.KeyDown, textListener);
		control.addListener(SWT.MouseDown, textListener);
		control.addListener(SWT.MouseWheel, textListener);
		control.addListener(SWT.MouseUp, textListener);
		control.addListener(SWT.Verify, textListener);

		text.addListener(SWT.Traverse, textListener);
	}

	/**
	 * If a field is being edited (via keyboard), set the edit value to the
	 * active field of the calendar. Reset the count of the EditField so that a
	 * subsequent key press will overwrite its contents;
	 * 
	 * @return true if the commit was successfull (the value was valid for the
	 *         field) or there was no commit to be made (editField is null),
	 *         false otherwise
	 */
	private boolean commitEditField() {
		if (editField != null) {
			int cf = getCalendarField();
			int val = editField.getValue();
			editField.reset();
			if (cf == Calendar.MONTH) {
				val--;
			}
			return fieldSet(cf, val, DISCARD);
		}
		return true;
	}

	/**
	 * If style is neither SIMPLE or DROP_DOWN, then this method simply returns,
	 * otherwise it creates the picker.
	 */
	private void createPicker() {
		if (isSimple()) {
			pickerPanel = panel;
			setContent(panel.getComposite());
		} else if (isDropDown()) {
			disposePicker();

			Shell shell = getContentShell();
			int style = (isSimple() ? SWT.NONE : SWT.BORDER)
					| SWT.DOUBLE_BUFFERED;
			VCanvas canvas = new VCanvas(shell, style);
			pickerPanel = canvas.getPanel();
			pickerPanel.setWidget(canvas);
			VGridLayout layout = new VGridLayout();
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			layout.verticalSpacing = 1;
			pickerPanel.setLayout(layout);
			setContent(pickerPanel.getComposite());

			canvas.addListener(SWT.KeyDown, new Listener() {
				public void handleEvent(Event event) {
					if (SWT.ESC == event.keyCode) {
						event.doit = false;
						if (selection.length > 0 && selection[0] != cancelDate) {
							setSelection(cancelDate);
							fireSelectionChanged();
						}
						setOpen(false);
					}
				}
			});

			if (field.length > 1 || isTime) {
				createPickerToolbar(pickerPanel);
			}
		}

		if (isDate) {
			DatePicker dp = new DatePicker(this);
			dp.setScrollable(scrollable);
			dp.setFields(calendarFields);
			dp.updateView();
			picker = dp;
		} else if (isTime) {
			if ((style & CDT.CLOCK_DISCRETE) != 0) {
				DiscreteTimePicker dtp = new DiscreteTimePicker(this);
				dtp.setFields(calendarFields);
				dtp.updateView();
				picker = dtp;
			} else {
				AnalogTimePicker atp = new AnalogTimePicker(this);
				atp.setFields(calendarFields);
				atp.updateView();
				picker = atp;
			}
		}

		if (isDropDown()) {
			picker.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		}
	}

	private void createPickerToolbar(VPanel parent) {
		VPanel tb = new VPanel(parent, SWT.NONE);
		VGridLayout layout = new VGridLayout(3, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 2;
		tb.setLayout(layout);
		tb.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		tb.setData(CDT.PickerPart, PickerPart.Toolbar);

		VButton b = new VButton(tb, SWT.OK | SWT.NO_FOCUS);
		b.setData(CDT.PickerPart, PickerPart.OkButton);
		b.setToolTipText(Resources.getString("accept.text", locale)); //$NON-NLS-1$
		b.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		b.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				setOpen(false);
			}
		});

		b = new VButton(tb, SWT.CANCEL | SWT.NO_FOCUS);
		b.setData(CDT.PickerPart, PickerPart.CancelButton);
		b.setToolTipText(Resources.getString("cancel.text", locale)); //$NON-NLS-1$
		b.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		b.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				setSelection(cancelDate);
				fireSelectionChanged();
				setOpen(false);
			}
		});

		b = new VButton(tb, SWT.NO_FOCUS);
		b.setData(CDT.PickerPart, PickerPart.ClearButton);
		b.setText(Resources.getString("clear.text", locale)); //$NON-NLS-1$
		b.setToolTipText(Resources.getString("clear.text", locale)); //$NON-NLS-1$
		b.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		b.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				setOpen(false);
				setSelection(null);
				fireSelectionChanged();
			}
		});

		VLabel sep = new VLabel(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		sep.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
	}

	// void deselect(Date date) {
	// if(date != null && isSelected(date)) {
	// Date[] tmp = new Date[selection.length - 1];
	// for(int i = 0, j = 0; i < selection.length; i++) {
	// if(!selection[i].equals(date)) {
	// tmp[j++] = selection[i];
	// }
	// }
	// setSelection(tmp);
	// }
	// }
	//
	// void deselectAll() {
	// setSelectedDates((Date[]) null);
	// }

	private void disposePicker() {
		if (content != null) {
			if (picker != null) {
				picker.dispose();
				picker = null;
			}
			if (isDropDown()) {
				Control c = content;
				setContent(null);
				c.dispose();
				if (contentShell != null) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							if (contentShell != null
									&& !contentShell.isDisposed()) {
								contentShell.dispose();
								contentShell = null;
							}
						}
					});
				}
			}
		}
	}

	/**
	 * Adds the given amount to the active field, if there is one
	 */
	void fieldAdjust(int amount) {
		if (!hasSelection()) {
			setSelection(calendar.getTime());
			fireSelectionChanged();
		} else {
			int cf = getCalendarField();
			if (cf >= 0) {
				fieldRoll(cf, amount, WRAP);
			}
		}
	}

	void fieldFirst() {
		// If the user has opted to have the user be able to
		// change time zones then allow the next field to be the
		// time zone field
		if (this.allowedTimezones == null) {
			if (Calendar.ZONE_OFFSET == getCalendarField(field[0])) {
				setActiveField(1);
			} else {
				setActiveField(0);
			}
		} else {
			// allowed time zones have been set, so let the user edit it
			setActiveField(0);
		}
	}

	/**
	 * Sets the active field from the select of the text box
	 */
	void fieldFromTextSelection() {
		if (!hasSelection()) {
			// setActiveField(FIELD_ALL);
			fieldNext();
		} else {
			Point sel = text.getControl().getSelection();
			AttributedCharacterIterator aci = df
					.formatToCharacterIterator(calendar.getTime());
			if (sel.x > textSelectionOffset.x)
				sel.x += textSelectionOffset.y;
			aci.setIndex(sel.x);
			Object[] oa = aci.getAttributes().keySet().toArray();
			if (oa.length == 0 && sel.x > 0) {
				sel.x -= 1;
				aci.setIndex(sel.x);
				oa = aci.getAttributes().keySet().toArray();
			}
			if (oa.length > 0) {
				for (int i = 0; i < field.length; i++) {
					if (oa[0].equals(field[i])) {
						// If the user has opted to have the user be able to
						// change time zones then allow the next field to be the
						// time zone field
						if (this.allowedTimezones == null) {
							if (Calendar.ZONE_OFFSET != getCalendarField(field[i])) {
								setActiveField(i);
							}
						} else {
							// allowed time zones have been set, so let the user
							// edit it
							setActiveField(i);
						}
						break;
					}
				}
				updateText();
			}
		}
	}

	void fieldLast() {
		// If the user has opted to have the user be able to change
		// time zones then allow the next field to be the time zone field
		if (this.allowedTimezones == null) {
			if (Calendar.ZONE_OFFSET == getCalendarField(field[field.length - 1])) {
				setActiveField(field.length - 2);
			} else {
				setActiveField(field.length - 1);
			}
		} else {
			// allowed time zones have been set, so let the user edit it
			setActiveField(field.length - 1);
		}
	}

	/**
	 * Sets the active field to the next field; wraps if necessary and sets to
	 * last field if there is no current active field
	 */
	void fieldNext() {
		fieldNext(false);
	}

	/**
	 * Sets the active field to the next field; wraps if necessary and sets to
	 * last field if there is no current active field
	 * 
	 * @param If
	 *            true, the text update will be asynchronous (for changes to
	 *            text selection)
	 */
	void fieldNext(boolean async) {
		if (activeField >= 0 && activeField < field.length - 1) {
			// If the user has opted to have the user be able to change
			// time zones then allow the next field to be the time zone field
			if (this.allowedTimezones == null) {
				if (Calendar.ZONE_OFFSET == getCalendarField(field[activeField + 1])) {
					if (activeField < field.length - 2) {
						setActiveField(activeField + 2);
					} else {
						setActiveField(0);
					}
				} else {
					setActiveField(activeField + 1);
				}
			} else {
				// allowed time zones have been set, so let the user edit it
				setActiveField(activeField + 1);
			}
		} else {
			// If the user has opted to have the user be able to change
			// time zones then allow the next field to be the time zone field
			if (this.allowedTimezones == null) {
				if (Calendar.ZONE_OFFSET == getCalendarField(field[0])) {
					setActiveField(1);
				} else {
					setActiveField(0);
				}
			} else {
				// allowed time zones have been set, so let the user edit it
				setActiveField(0);
			}
		}
		updateText(async);
	}

	/**
	 * Sets the active field to the previous field; wraps if necessary and sets
	 * to first field if there is no current active field
	 */
	private void fieldPrev() {
		fieldPrev(false);
	}

	/**
	 * Sets the active field to the previous field; wraps if necessary and sets
	 * to first field if there is no current active field
	 * 
	 * @param If
	 *            true, the text update will be asynchronous (for changes to
	 *            text selection)
	 */
	void fieldPrev(boolean async) {
		if (activeField > 0 && activeField < field.length) {
			// If the user has opted to have the user be able to change
			// time zones then allow the next field to be the time zone field
			if (this.allowedTimezones == null) {
				if (Calendar.ZONE_OFFSET == getCalendarField(field[activeField - 1])) {
					if (activeField > 1) {
						setActiveField(activeField - 2);
					} else {
						setActiveField(field.length - 1);
					}
				} else {
					setActiveField(activeField - 1);
				}
			} else {
				// allowed time zones have been set, so let the user edit it
				setActiveField(activeField - 1);
			}
		} else {
			// If the user has opted to have the user be able to change
			// time zones then allow the next field to be the time zone field
			if (this.allowedTimezones == null) {

				if (Calendar.ZONE_OFFSET == getCalendarField(field[field.length - 1])) {
					setActiveField(field.length - 2);
				} else {
					setActiveField(field.length - 1);
				}
			} else {
				// allowed time zones have been set, so let the user edit it
				setActiveField(field.length - 1);
			}
		}
		updateText(async);
	}

	private boolean fieldRoll(final int calendarField, final int rollAmount,
			final int style) {
		if (!getEditable()) {
			return false;
		}

		if (calendarField == Calendar.ZONE_OFFSET
				&& this.allowedTimezones != null) {
			boolean timeZoneSet = false;
			for (int idx = 0; idx < this.allowedTimezones.length; idx++) {
				TimeZone activeTimeZone = this.getTimeZone();
				if (activeTimeZone.getID() == this.allowedTimezones[idx]
						.getID()) {
					if (rollAmount < 0) {
						if (idx == 0) {
							this.setTimeZone(this.allowedTimezones[this.allowedTimezones.length - 1]);
						} else {
							this.setTimeZone(this.allowedTimezones[idx - 1]);
						}
					} else if (rollAmount > 0) {
						if (idx == this.allowedTimezones.length - 1) {
							this.setTimeZone(this.allowedTimezones[0]);
						} else {
							this.setTimeZone(this.allowedTimezones[idx + 1]);
						}
					}
					timeZoneSet = true;
					break;
				}
			}
			if (!timeZoneSet) {
				this.setTimeZone(this.allowedTimezones[0]);
			}
		} else {
			calendar.roll(calendarField, rollAmount);
		}

		if (selection.length > 0) {
			selection[0] = calendar.getTime();
		}
		updateText();
		updatePicker();
		fireSelectionChanged(calendarField);

		return true;
	}

	/**
	 * Sets the given calendar field to the given value.<br>
	 * <b>NOTE:</b> This is NOT the active field but a field in the "calendar"
	 * variable.
	 * 
	 * @param calendarField
	 *            the field of calendar to set
	 * @param value
	 *            the value to set it to
	 * @param style
	 *            the of set to perform; if the value is valid for the given
	 *            calendarField then this has no affect, otherwise it will take
	 *            an action according to this style int:
	 *            <ul>
	 *            <li>DISCARD: the value will be discarded and the method
	 *            returns without performing and action</li>
	 *            <li>WRAP: if value is higher than its maximum it will be set
	 *            to its minimum, and visa versa</li>
	 *            <li>BLOCK: if value is higher than its maximum it will be set
	 *            to its maximum, and visa versa</li>
	 *            </ul>
	 * @return true if the field was set, false otherwise (as is possible with a
	 *         DISCARD style)
	 */
	private boolean fieldSet(int calendarField, int value, int style) {
		if (!getEditable()) {
			return false;
		}
		if (calendarField >= 0) {
			if (value > calendar.getActualMaximum(calendarField)) {
				if (style == DISCARD) {
					return false;
				} else if (style == WRAP) {
					value = calendar.getActualMinimum(calendarField);
				} else if (style == BLOCK) {
					value = calendar.getActualMaximum(calendarField);
				}
			} else if (value < calendar.getActualMinimum(calendarField)) {
				if (style == DISCARD) {
					return false;
				} else if (style == WRAP) {
					value = calendar.getActualMaximum(calendarField);
				} else if (style == BLOCK) {
					value = calendar.getActualMinimum(calendarField);
				}
			}
			calendar.set(calendarField, value);
			if (selection.length > 0) {
				selection[0] = calendar.getTime();
			}
			updateText();
			updatePicker();
			fireSelectionChanged(calendarField);
		}
		return true;
	}

	/**
	 * <p>
	 * Notifies listeners that the selection for this CDateTime has changed
	 * </p>
	 * <p>
	 * This will fire both a regular selection event, and a default selection
	 * event.
	 * </p>
	 * <p>
	 * The data field is populated by {@link #getSelectedDates()}.
	 * </p>
	 */
	void fireSelectionChanged() {
		fireSelectionChanged(false);
	}

	void fireSelectionChanged(boolean defaultSelection) {
		if (defaultSelection && isOpen()) {
			setOpen(false);
		}
		Event event = new Event();
		event.data = getSelection();
		notifyListeners(SWT.Selection, event);
		if (defaultSelection) {
			notifyListeners(SWT.DefaultSelection, event);
		}
	}

	/**
	 * <p>
	 * Notifies listeners that a field of the selected date for this CDateTime
	 * has changed
	 * </p>
	 * <p>
	 * Note that this is only valid when {@link #singleSelection} is true, and
	 * will only fire a regular selection event (not a default selection event)
	 * </p>
	 * <p>
	 * The data field is populated by {@link #getSelection()} and the detail
	 * field holds the field which was changed.
	 * </p>
	 * 
	 * @param field
	 *            the Calendar Field which caused the change, or -1 if
	 *            <code>setTime</code> was called (thus setting all Calendar
	 *            Fields)
	 */
	void fireSelectionChanged(int field) {
		Event event = new Event();
		event.data = getSelection();
		event.detail = field;
		if (this.field.length == 1) {
			if (isOpen()) {
				setOpen(false);
			}
			notifyListeners(SWT.Selection, event);
			notifyListeners(SWT.DefaultSelection, event);
		} else {
			notifyListeners(SWT.Selection, event);
		}
	}

	VButton getButtonWidget() {
		return button;
	}

	/**
	 * Gets the calendar field corresponding to the active field, if there is
	 * one.
	 * 
	 * @return an int representing the calendar field, -1 if there isn't one.
	 */
	int getCalendarField() {
		return hasField(activeField) ? getCalendarField(field[activeField])
				: -1;
	}

	int getCalendarField(Field field) {
		int cf = field.getCalendarField();
		if (cf < 0) {
			if (field.toString().indexOf("hour 1") > -1) { //$NON-NLS-1$
				cf = Calendar.HOUR;
			} else if (field.toString().contains("zone")) { //$NON-NLS-1$
				cf = Calendar.ZONE_OFFSET;
			}
		}
		return cf;
	}

	Calendar getCalendarInstance() {
		return getCalendarInstance(calendar.getTimeInMillis());
	}

	/**
	 * <p>
	 * <b>WARNING: Experimental API - this method may be removed in future
	 * versions</b>
	 * </p>
	 * Get a new instance of Calendar that is initialized with the timezone and
	 * locale of this CDateTime, and set to the given date.
	 * 
	 * @param date
	 *            the date that the Calendar will be set to, or null for the
	 *            current system time
	 * @return a new instance of Calendar
	 */
	public Calendar getCalendarInstance(Date date) {
		if (date == null) {
			return getCalendarInstance(System.currentTimeMillis());
		} else {
			return getCalendarInstance(date.getTime());
		}
	}

	/**
	 * <p>
	 * <b>WARNING: Experimental API - this method may be removed in future
	 * versions</b>
	 * </p>
	 * Get a new instance of Calendar that is initialized with the timezone and
	 * locale of this CDateTime, and set to the given date.
	 * 
	 * @param date
	 *            the date, in millis, that the Calendar will be set to
	 * @return a new instance of Calendar
	 */
	public Calendar getCalendarInstance(long date) {
		Calendar cal = Calendar.getInstance(timezone, locale);
		cal.setTimeInMillis(date);
		return cal;
	}

	Date getCalendarTime() {
		return calendar.getTime();
	}

	long getCalendarTimeInMillis() {
		return calendar.getTimeInMillis();
	}

	public boolean getEditable() {
		return !panel.hasStyle(SWT.READ_ONLY);
	}

	/**
	 * The locale currently in use by this CDateTime.
	 * 
	 * @return the locale
	 * @see #setLocale(Locale)
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Get the text which will be shown when the selection is set to null. Note
	 * that this will be equal to the default null text for the given locale
	 * unless the null text has been explicitly set using
	 * {@link #setNullText(String)}
	 * 
	 * @return the text shown when the selection is null
	 * @see #setNullText(String)
	 */
	public String getNullText() {
		if (nullText == null) {
			if (isDate) {
				return Resources.getString("null_text.date", locale); //$NON-NLS-1$
			} else {
				return Resources.getString("null_text.time", locale); //$NON-NLS-1$
			}
		}
		return nullText;
	}

	CDateTimePainter getPainter() {
		if (painter == null) {
			setPainter(new CDateTimePainter());
		}
		return painter;
	}

	/**
	 * Get the pattern of this CDateTime as used to set its format. If the
	 * format was NOT set using <code>setFormat(String)</code> this will return
	 * <code>null</code>.
	 * 
	 * @return the pattern, null if there isn't one
	 * @see SimpleDateFormat
	 * @see #setFormat(int)
	 * @see #setPattern(String)
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * Get the current selection of this CDateTime widget, or null if there is
	 * no selection.
	 * 
	 * @return the current selection
	 */
	public Date getSelection() {
		return hasSelection() ? selection[0] : null;
	}

	public int getStyle() {
		return style;
	}

	public String getText() {
		return checkText() ? text.getText() : null;
	}

	VNative<Text> getTextWidget() {
		return text;
	}

	/**
	 * The timezone currently in use by this CDateTime.
	 * 
	 * @return the timezone
	 * @see #setTimeZone(String)
	 * @see #setTimeZone(TimeZone)
	 */
	public TimeZone getTimeZone() {
		return timezone;
	}

	/**
	 * The Key event handler
	 * 
	 * @param event
	 *            the event
	 */
	void handleKey(Event event) {
		if (event.stateMask != 0) {
			return;
		}
		if ('\r' == event.keyCode || SWT.KEYPAD_CR == event.keyCode) {
			fireSelectionChanged(true);
		} else if (SWT.BS == event.keyCode || SWT.DEL == event.keyCode) {
			event.doit = false;
			setSelection((Date) null);
			fireSelectionChanged();
		} else if (!hasField(activeField) && !hasSelection()) {
			event.doit = false;
		} else {
			switch (event.keyCode) {
			case '-':
			case SWT.KEYPAD_SUBTRACT:
				fieldAdjust(-1);
				break;
			case '=':
			case '+':
			case SWT.KEYPAD_ADD:
				fieldAdjust(1);
				break;
			case SWT.BS:
				if (editField != null)
					editField.removeLastCharacter();
				break;
			case SWT.ARROW_DOWN:
				fieldAdjust(-1);
				updateText(true);
				break;
			case SWT.ARROW_UP:
				fieldAdjust(1);
				updateText(true);
				break;
			case SWT.ARROW_LEFT:
				fieldPrev(true);
				break;
			case SWT.ARROW_RIGHT:
				fieldNext(true);
				break;
			default:
				if (hasField(activeField)
						&& activeField + 1 < separator.length
						&& String.valueOf(event.character).equals(
								separator[activeField + 1])) {
					fieldNext();
				} else if (!hasSelection()
						&& String.valueOf(event.character).matches("[0-9]")) {
					fieldAdjust(0);
					fieldFirst();
				}
			}
		}
	}

	/**
	 * The Travers event handler. Note that ARROW_UP and ARROW_DOWN are handled
	 * in the <code>handleKey</code> method.
	 * 
	 * @param event
	 *            the event
	 */
	void handleTraverse(Event event) {
		boolean allowTimeZoneEdit = this.allowedTimezones != null;

		switch (event.detail) {
		case SWT.TRAVERSE_ARROW_NEXT:
			if (event.keyCode == SWT.ARROW_RIGHT) {
				fieldNext();
			} else if (event.keyCode == SWT.ARROW_DOWN) {
				fieldAdjust(-1);
			}
			break;
		case SWT.TRAVERSE_ARROW_PREVIOUS:
			if (event.keyCode == SWT.ARROW_LEFT) {
				fieldPrev();
			} else if (event.keyCode == SWT.ARROW_UP) {
				fieldAdjust(1);
			}
			break;
		case SWT.CR:
			fieldNext();
			fireSelectionChanged();
			break;
		case SWT.TRAVERSE_TAB_NEXT:
			if (tabStops && hasSelection()) {
				// if we are at the last field, allow the tab out of the control
				// the last field is also considered to be the 2nd to last if
				// the last is a time zone
				// we now check if the control allows time zone editing
				if (activeField == field.length - 1
						|| ((activeField == field.length - 2)
								&& (Calendar.ZONE_OFFSET == getCalendarField(field[field.length - 1])) && (!allowTimeZoneEdit))) {
					event.doit = true;
				} else {
					event.doit = false;
					if (activeField < 0) {
						fieldPrev();
					} else {
						fieldNext();
					}
				}
			}
			break;
		case SWT.TRAVERSE_TAB_PREVIOUS:
			if (tabStops && hasSelection()) {
				// if we are at the 1st field, allow the tab out of the control
				// the 1st field is also considered to the the 2nd if the 1st
				// is a time zone
				if (activeField == 0
						|| ((activeField == 1)
								&& (Calendar.ZONE_OFFSET == getCalendarField(field[0])) && (!allowTimeZoneEdit))) {
					event.doit = true;
				} else {
					event.doit = false;
					if (activeField < 0) {
						fieldNext();
					} else {
						fieldPrev();
					}
				}
			}
			break;
		default:
		}
	}

	/**
	 * Determines if the given field number is backed by a real field.
	 * 
	 * @param field
	 *            the field number to check
	 * @return true if the given field number corresponds to a field in the
	 *         field array
	 */
	private boolean hasField(int field) {
		return field >= 0 && field <= this.field.length;
	}

	/**
	 * Return true if this CDateTime has one or more dates selected;
	 * 
	 * @return true if a date is selected, false otherwise
	 */
	public boolean hasSelection() {
		return selection.length > 0;
	}

	private void init(int style) {
		this.style = style;
		locale = Locale.getDefault();
		try {
			timezone = TimeZone.getDefault();
		} catch (Exception e) {
			timezone = TimeZone.getTimeZone("GMT"); //$NON-NLS-1$
		}
		calendar = Calendar.getInstance(this.timezone, this.locale);
		calendar.setTime(new Date());
		tabStops = (style & CDT.TAB_FIELDS) != 0;
		singleSelection = ((style & CDT.SIMPLE) == 0)
				|| ((style & CDT.MULTI) == 0);

		setFormat(style);

		if (!isSimple()) {
			if (isDropDown()) {
				if ((style & CDT.BUTTON_AUTO) != 0) {
					setButtonVisibility(BaseCombo.BUTTON_AUTO);
				} else {
					setButtonVisibility(BaseCombo.BUTTON_ALWAYS);
				}
			} else {
				setButtonVisibility(BaseCombo.BUTTON_NEVER);
				if ((style & CDT.SPINNER) != 0) {
					int sStyle = SWT.VERTICAL;
					if (gtk && ((style & CDT.BORDER) != 0)) {
						sStyle |= SWT.BORDER;
					}
					spinner = VNative.create(Spinner.class, panel, sStyle);
					if (win32) {
						spinner.setBackground(text.getControl().getBackground());
					}
					spinner.getControl().setMinimum(0);
					spinner.getControl().setMaximum(50);
					spinner.getControl().setDigits(1);
					spinner.getControl().setIncrement(1);
					spinner.getControl().setPageIncrement(1);
					spinner.getControl().setSelection(25);
					spinner.getControl().addFocusListener(new FocusAdapter() {
						public void focusGained(FocusEvent e) {
							internalFocusShift = true;
							setFocus();
							internalFocusShift = false;
						}
					});
					spinner.getControl().addMouseListener(new MouseAdapter() {
						public void mouseDown(MouseEvent e) {
							if (e.button == 2) {
								fieldNext();
							}
						}
					});
					spinner.getControl().addSelectionListener(
							new SelectionAdapter() {
								public void widgetSelected(SelectionEvent e) {
									if (VTracker.getMouseDownButton() != 2) {
										if (spinner.getControl().getSelection() > 25) {
											fieldAdjust(1);
										} else {
											fieldAdjust(-1);
										}
										spinner.getControl().setSelection(25);
									}
								}
							});
					panel.setLayout(new SpinnerLayout());
				}
			}

			updateText();
			activeField = -5;
			setActiveField(FIELD_NONE);

			if (checkText()) {
				addTextListener();
			}
		}
	}

	boolean isSelected(Date date) {
		for (Date d : selection) {
			if (d.equals(date)) {
				return true;
			}
		}
		return false;
	}

	boolean isSingleSelection() {
		return singleSelection;
	}
	/**
	 * Determine whether the provided field is the most
	 * <b>precise</b> field. According to the used pattern, e.g.  
	 * <ul>
	 * <li>dd.mm.yyyy 
	 * <li>MMMM yyyy
	 * <li>yyyy
	 * </ul>
	 * the date picker provides the panels for selecting a day, month or year 
	 * respectively. The panel should close itself and set the selection in 
	 * the CDateTime field when the user selects the most precise field.
	 * The constants from the {@link Calendar} class may be used to determine
	 * the most precise field:
	 * <ul>
	 * <li>{@link Calendar#YEAR} -> 1
	 * <li>{@link Calendar#MONTH} -> 2
	 * <li>{@link Calendar#DATE} -> 5
	 * </ul>
	 * e.g. the <i>highest</i> constant is the closing field. 
	 * @param calendarField The calendar field identifying a pattern field
	 * @return true if the highest pattern field  
	 */
	boolean isClosingField(int calendarField) {
		// find the "highest" constant in the pattern fields
		int i = Integer.MIN_VALUE;
		for (Field f : field) {
			i = Math.max(i, f.getCalendarField());
		}
		// compare the highest constant with the field 
		if ( i == calendarField) {
			return true;
		}
		return false;
	}

	@Override
	protected void postClose(Shell popup) {
		disposePicker();
	}

	/**
	 * Removes the listener from the collection of listeners who will be
	 * notified when the receiver's selection changes.
	 * 
	 * @param listener
	 *            the listener which should no longer be notified
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * @see SelectionListener
	 * @see #addSelectionListener
	 */
	public void removeSelectionListener(SelectionListener listener) {
		if (listener != null) {
			TypedListener l = new TypedListener(listener);
			removeListener(SWT.Selection, l);
			removeListener(SWT.DefaultSelection, l);
		}
	}

	/**
	 * Removes the textListener for the appropriate SWT events to handle
	 * incrementing fields.
	 */
	protected void removeTextListener() {
		Text control = text.getControl();
		control.removeListener(SWT.KeyDown, textListener);
		control.removeListener(SWT.MouseDown, textListener);
		control.removeListener(SWT.MouseWheel, textListener);
		control.removeListener(SWT.MouseUp, textListener);
		control.removeListener(SWT.Verify, textListener);
		text.removeListener(SWT.Traverse, textListener);
	}

	// void select(Date date) {
	// if(date != null) {
	// Date[] tmp = new Date[selection.length + 1];
	// System.arraycopy(selection, 0, tmp, 1, selection.length);
	// tmp[0] = date;
	// setSelectedDates(tmp);
	// }
	// }
	//
	// void select(Date date1, Date date2, int field, int increment) {
	// if(date1 != null && date2 != null) {
	// Date start = date1.before(date2) ? date1 : date2;
	// Date end = date1.before(date2) ? date2 : date1;
	// List<Date> tmp = new ArrayList<Date>();
	// Calendar cal = getCalendarInstance(start);
	// while(cal.getTime().before(end)) {
	// tmp.add(cal.getTime());
	// cal.add(field, increment);
	// }
	// tmp.add(cal.getTime());
	// if(start == date2) {
	// Collections.reverse(tmp);
	// }
	// setSelectedDates(tmp.toArray(new Date[tmp.size()]));
	// }
	// }

	/**
	 * Sets the active field, which may or may not be a real field (it may also
	 * be <code>FIELD_NONE</code>)
	 * 
	 * @param field
	 *            the field to be set active
	 * @see CDateTime#hasField(int)
	 */
	private void setActiveField(int field) {
		if (activeField != field) {
			commitEditField();
			editField = null;
			activeField = field;
		}
	}

	/**
	 * <p>
	 * <b>WARNING: Experimental API - this method may be removed in future
	 * versions</b>
	 * </p>
	 * Sets the builder that this CDateTime widget will use to build its
	 * graphical selector to the given builder, or to a default builder if the
	 * given builder is null.
	 * 
	 * @param builder
	 *            the builder to use, or null to use a default builder
	 */
	public void setBuilder(CDateTimeBuilder builder) {
		this.builder = builder;
		if (picker != null) {
			disposePicker();
			createPicker();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.nebula.cwt.base.BaseCombo#setButtonImage(org.eclipse.swt.
	 * graphics.Image)
	 */
	public void setButtonImage(Image image) {
		super.setButtonImage(image);
	}

	@Override
	protected boolean setContentFocus() {
		if (checkPicker()) {
			internalFocusShift = true;
			boolean result = picker.setFocus();
			internalFocusShift = false;
			return result;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.nebula.cwt.base.BaseCombo#setEditable(boolean)
	 */
	public void setEditable(boolean editable) {
		super.setEditable(editable);
		if (checkPicker()) {
			if (picker instanceof DatePicker) {
				((DatePicker) picker).setEditable(editable);
			} else {
				picker.setActivatable(editable);
			}
		}
	}

	private boolean checkPicker() {
		return picker != null && !picker.isDisposed();
	}

	/**
	 * Set the date and time format of this CDateTime uses style constants which
	 * correspond to the various forms of DateFormat.getXxxInstance(int). <dt>
	 * <b>Valid Styles:</b></dt> <dd>DATE_SHORT, DATE_MEDIUM, DATE_LONG,
	 * TIME_SHORT, TIME_MEDIUM</dd>
	 * <p>
	 * Styles are bitwise OR'ed together, but only one "DATE" and one "TIME" may
	 * be set at a time.
	 * </p>
	 * Examples:<br>
	 * </code>setFormat(CDT.DATE_LONG);</code><br />
	 * </code>setFormat(CDT.DATE_SHORT | CDT.TIME_MEDIUM);</code><br />
	 * 
	 * @param format
	 *            the bitwise OR'ed Date and Time format to be set
	 * @throws IllegalArgumentException
	 * @see #getPattern()
	 * @see #setPattern(String)
	 */
	public void setFormat(int format) throws IllegalArgumentException {
		int dateStyle = (format & CDT.DATE_SHORT) != 0 ? DateFormat.SHORT
				: (format & CDT.DATE_MEDIUM) != 0 ? DateFormat.MEDIUM
						: (format & CDT.DATE_LONG) != 0 ? DateFormat.LONG : -1;
		int timeStyle = (format & CDT.TIME_SHORT) != 0 ? DateFormat.SHORT
				: (format & CDT.TIME_MEDIUM) != 0 ? DateFormat.MEDIUM : -1;
		String str = null;
		if (dateStyle != -1 && timeStyle != -1) {
			str = ((SimpleDateFormat) DateFormat.getDateTimeInstance(dateStyle,
					timeStyle, locale)).toPattern();
		} else if (dateStyle != -1) {
			str = ((SimpleDateFormat) DateFormat.getDateInstance(dateStyle,
					locale)).toPattern();
		} else if (timeStyle != -1) {
			str = ((SimpleDateFormat) DateFormat.getTimeInstance(timeStyle,
					locale)).toPattern();
		} else if (pattern == null) { // first call, so set to default
			format = CDT.DATE_SHORT;
			str = ((SimpleDateFormat) DateFormat.getDateInstance(
					DateFormat.SHORT, locale)).toPattern();
		}
		if (str != null) {
			this.format = format;
			setPattern(str);
		}
	}

	/**
	 * Sets the Locale to be used by this CDateTime and causes all affected
	 * attributes to be updated<br>
	 * If the provided locale is the same as the current locale then this method
	 * simply returns. If the provided Locale is null then this CDateTime will
	 * use the system's default locale.<br>
	 * If this <code>CDateTime</code> is of style <code>DROP_DOWN</code> then
	 * the associated <code>CDateTime</code> will be set to the same locale.
	 * 
	 * @param locale
	 *            the Locale, or null to use the system's default
	 * @see #getLocale()
	 */
	public void setLocale(Locale locale) {
		if (locale == null)
			locale = Locale.getDefault();
		if (!this.locale.equals(locale)) {
			this.locale = locale;
			if (format > 0) {
				setFormat(format);
			} else {
				setPattern(pattern);
			}
			updateNullText();
		}
	}

	protected void setModifyEventProperties(Event e) {
		e.data = calendar.getTime();
	}

	/**
	 * Set the text to be shown when the selection is null. Passing null into
	 * this method will cause the CDateTime widget to use a default null text
	 * for the given locale.
	 * 
	 * @param text
	 */
	public void setNullText(String text) {
		defaultNullText = false;
		nullText = text;
		updateText();
	}

	public void setOpen(boolean open) {
		setOpen(open, null);
	}

	public void setOpen(boolean open, Runnable callback) {
		if (open) {
			cancelDate = getSelection();
			createPicker();
		} else {
			cancelDate = null;
		}
		super.setOpen(open, callback);
		if (hasSelection()) {
			show(getSelection());
		}
	}

	/**
	 * <p>
	 * <b>WARNING: Experimental API - this method may be removed in future
	 * versions</b>
	 * </p>
	 * Sets the painter that this CDateTime widget will use to paint its
	 * graphical selector to the given painter, or to a default painter if the
	 * given painter is null.
	 * 
	 * @param painter
	 *            the painter to use, or null to use a default painter
	 */
	public void setPainter(CDateTimePainter painter) {
		if (painter != null) {
			painter.setCDateTime(this);
		}
		this.painter = painter;
	}

	/**
	 * Set the style of this CDateTime to work with dates and / or times as
	 * determined by the given pattern. This will set the fields shown in the
	 * text box and, if <code>DROP_DOWN</code> style is set, the fields of the
	 * drop down component.<br>
	 * This method is backed by an implementation of SimpleDateFormat, and as
	 * such, any string pattern which is valid for SimpleDateFormat may be used.
	 * Examples (US Locale):<br>
	 * </code>setPattern("MM/dd/yyyy h:mm a");</code><br />
	 * </code>setPattern("'Meeting @' h:mm a 'on' EEEE, MMM dd, yyyy");</code><br />
	 * 
	 * @param pattern
	 *            the pattern to use, if it is invalid, the original is restored
	 * @throws IllegalArgumentException
	 * @see SimpleDateFormat
	 * @see #getPattern()
	 * @see #setFormat(int)
	 */
	public void setPattern(String pattern) throws IllegalArgumentException {
		this.allowedTimezones = null;
		if (isOpen()) {
			setOpen(false);
		}
		df = new SimpleDateFormat(pattern, locale);
		df.setTimeZone(timezone);
		if (updateFields()) {
			this.pattern = pattern;
			this.format = -1;
			boolean wasDate = isDate;
			boolean wasTime = isTime;
			isDate = isTime = false;
			calendarFields = new int[field.length];
			for (int i = 0; i < calendarFields.length; i++) {
				calendarFields[i] = getCalendarField(field[i]);
				switch (calendarFields[i]) {
				case Calendar.AM_PM:
				case Calendar.HOUR:
				case Calendar.HOUR_OF_DAY:
				case Calendar.MILLISECOND:
				case Calendar.MINUTE:
				case Calendar.SECOND:
				case Calendar.ZONE_OFFSET:
					isTime = true;
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
					isDate = true;
					break;
				default:
					break;
				}
			}
			if (checkButton() && ((isDate != wasDate) || (isTime != wasTime))) {
				if (defaultButtonImage) {
					if (isDate && isTime) {
						doSetButtonImage(Resources.getIconCalendarClock());
					} else if (isDate) {
						doSetButtonImage(Resources.getIconCalendar());
					} else {
						doSetButtonImage(Resources.getIconClock());
					}
				}
				updateNullText();
			}
			if (checkText()) {
				updateText();
			}
			if (isSimple()) {
				disposePicker();
				createPicker();
			}
		} else {
			throw new IllegalArgumentException(
					"Problem setting pattern: \"" + pattern + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	void setScrollable(boolean scrollable) {
		this.scrollable = scrollable;
		if (isSimple() && !scrollable) {
			if (picker != null && picker instanceof DatePicker) {
				updatePicker();
			}
		}
	}

	/**
	 * Set the selection for this CDateTime to that of the provided
	 * <code>Date</code> object.<br>
	 * 
	 * @param selection
	 *            the new selection, or null to clear the selection
	 */
	public void setSelection(Date selection) {
		if (getEditable()) {
			if (selection == null) {
				this.selection = new Date[0];
			} else {
				this.selection = new Date[] { selection };
			}
		}
		if (singleSelection && this.selection.length > 0) {
			show(selection);
		} else {
			updateText();
			updatePicker();
		}
	}

	/**
	 * Sets the timezone to the timezone specified by the given zoneID, or to
	 * the system default if the given zoneID is null. If the give zoneID cannot
	 * be understood, then the timezone will be set to GMT.
	 * 
	 * @param zoneID
	 *            the id of the timezone to use, or null to use the system
	 *            default
	 * @see #setTimeZone(TimeZone)
	 */
	public void setTimeZone(String zoneID) {
		if (zoneID == null) {
			setTimeZone((TimeZone) null);
		} else {
			setTimeZone(TimeZone.getTimeZone(zoneID));
		}
	}

	/**
	 * Sets the timezone to the given timezone, or to the system's default
	 * timezone if the given timezone is null.
	 * 
	 * @param zone
	 *            the timezone to use, or null to use the system default
	 * @see #setTimeZone(String)
	 */
	public void setTimeZone(TimeZone zone) {
		if (zone == null) {
			timezone = TimeZone.getDefault();
		}
		if (!this.timezone.equals(zone)) {
			this.timezone = zone;
			calendar.setTimeZone(this.timezone);
			df.setTimeZone(this.timezone);
			updateText();
		}
	}

	/**
	 * Shows the given date if it can be shown by the selector. In other words,
	 * for graphical selectors such as a calendar, the visible range of time is
	 * moved so that the given date is visible.
	 * 
	 * @param date
	 *            the date to show
	 */
	public void show(Date date) {
		if (date == null) {
			calendar.setTime(new Date());
		} else {
			calendar.setTime(date);
		}
		updateText();
		updatePicker();
	}

	/**
	 * Show the selection if it can be shown by the selector. Has no affect if
	 * there is no selection.
	 * 
	 * @see #show(Date)
	 */
	public void showSelection() {
		if (selection.length > 0) {
			show(selection[0]);
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " {" + getCalendarTime() + "}"; //$NON-NLS-1$  //$NON-NLS-2$
	}

	/**
	 * inspects all of the calendar fields in the <code>field</code> array to
	 * determine what style is appropriate and then sets that style to the
	 * picker using the setPickerStyle method.<br>
	 */
	private boolean updateFields() {
		Field[] bak = new Field[(field == null) ? 0 : field.length];
		if (bak.length > 0)
			System.arraycopy(field, 0, bak, 0, field.length);

		AttributedCharacterIterator aci = df.formatToCharacterIterator(calendar
				.getTime());
		field = new Field[aci.getAllAttributeKeys().size()];
		separator = new String[field.length + 1]; // there can be a separator
													// before and after
		int i = 0;
		Object last = null;
		for (char c = aci.first(); c != CharacterIterator.DONE; c = aci.next()) {
			Object[] oa = aci.getAttributes().keySet().toArray();
			if (oa.length > 0) {
				if (oa[0] != last && i < field.length) {
					if (getCalendarField((Field) oa[0]) < 0) {
						if (bak.length > 0) {
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
				if (separator[i] == null)
					separator[i] = String.valueOf(c);
			}
		}

		df.setLenient(false);
		setActiveField(FIELD_NONE);
		return true;
	}

	private void updateNullText() {
		if (defaultNullText) {
			if (isDate) {
				nullText = Resources.getString("null_text.date", locale); //$NON-NLS-1$
			} else {
				nullText = Resources.getString("null_text.time", locale); //$NON-NLS-1$
			}
			if (!hasSelection()) {
				updateText();
			}
		}
	}

	/**
	 * tell the picker to update its view of the selection and reference time
	 */
	private void updatePicker() {
		if (picker != null) {
			if (picker instanceof DatePicker) {
				((DatePicker) picker).updateView();
			} else if (picker instanceof AnalogTimePicker) {
				((AnalogTimePicker) picker).updateView();
			} else if (picker instanceof DiscreteTimePicker) {
				((DiscreteTimePicker) picker).updateView();
			}
		}
	}

	/**
	 * This is the only way that text is set to the text box.<br>
	 * The selection of the text in the text box is also set here (the active field is selected) as
	 * well as if a field is being edited, it's "edit text" is inserted for
	 * display.
	 * The <code>getSelection</code> property of CDateTime remains unchanged.
	 */
	private void updateText() {
		updateText(false);
	}

	/**
	 * This is the only way that text is set to the text box.<br>
	 * The selection of the text in the text box is also set here (the active field is selected) as
	 * well as if a field is being edited, it's "edit text" is inserted for
	 * display.
	 * The <code>getSelection</code> property of CDateTime remains unchanged.
	 * 
	 * @param async
	 *            If true, this operation will be performed asynchronously (for
	 *            changes to text selection)
	 */
	private void updateText(boolean async) {
		// TODO: save previous state and only update on changes...?

		String buffer = hasSelection() ? df.format(getSelection())
				: getNullText();

		int s0 = 0;
		int s1 = 0;

		if (!hasSelection()) {
			s0 = 0;
			s1 = buffer.length();
		} else if (activeField >= 0 && activeField < field.length) {
			AttributedCharacterIterator aci = df
					.formatToCharacterIterator(getSelection());
			for (char c = aci.first(); c != CharacterIterator.DONE; c = aci
					.next()) {
				if (aci.getAttribute(field[activeField]) != null) {
					s0 = aci.getRunStart();
					s1 = aci.getRunLimit();
					if (editField != null) {
						String str = editField.toString();
						buffer = buffer.substring(0, s0) + str
								+ buffer.substring(s1);
						int oldS1 = s1;
						s1 = s0 + str.length();
						textSelectionOffset.x = Math.min(oldS1, s1);
						textSelectionOffset.y = (oldS1 - s0) - str.length();
					} else {
						textSelectionOffset.x = buffer.length() + 1;
						textSelectionOffset.y = 0;
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

		Runnable runnable = new Runnable() {
			public void run() {
				if ((text != null) && (!text.isDisposed())) {
					if (!string.equals(text.getText())) {
						text.getControl().removeListener(SWT.Verify,
								textListener);
						text.setText(string);
						text.getControl().addListener(SWT.Verify, textListener);
					}
					text.getControl().setSelection(selStart, selEnd);
				}
			}
		};

		if (async) {
			getDisplay().asyncExec(runnable);
		} else {
			getDisplay().syncExec(runnable);
		}
	}

	/**
	 * The Verify Event handler.<br>
	 * <b>EVERYTHING</b> is blocked via this handler (Event.doit is set to
	 * false). Depending upon the input, a course of action is determined and
	 * the displayed text is updated via the <code>updateText()</code> method.
	 * <br>
	 * This method implements the following logic:
	 * If the event is a paste, the pasted text is parsed for the entire date/time selection;
	 * if this parse is successful, the result is committed to the selection property and is displayed;
	 * otherwise, it is discarded.  One-character pastes are discarded without parsing.
	 * When user types characters one by one, all non-digits are discarded (if they have effects, they
	 * have already been processed by other event handlers) while digits are added to
	 * <code>this.editField</code> without affecting the selection.
	 * Once <code>this.editField</code> reaches its capacity for
	 * the active field, its contents are attempted to be committed.
	 * If the commit is successful, focus switches to the next field of CDateTime.
	 * Otherwise, the contents of <code>this.editField</code>
	 * are discarded and the previous value of the selection (before user started typing
	 * in this field) is restored; focus stays in the same field.
	 * <b>Example:</b> if the seconds field contains "23", and user types 8 in the seconds field,
	 * "08" is shown on screen while getSelection still returns 23.  If user types 9 after that,
	 * the field reaches its capacity, the attempt to commit 89 seconds fails, and 23 gets restored
	 * on screen.
	 * 
	 * @param e
	 *            the event
	 * @see CDateTime#updateText()
	 */
	void verify(Event e) {
		e.doit = false;
		if (field.length == 0 || activeField == FIELD_NONE)
			return;

		char c = e.character;
		if (((e.text.length() == 1) && String.valueOf(c).equals(e.text) && Character
				.isDigit(c)) || (e.text.length() > 1)) {
			if (e.text.length() == 1) {
				if (editField == null) {
					int cf = getCalendarField();
					if (cf >= 0) {
						int digits;
						switch (cf) {
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
						case Calendar.MILLISECOND:
							digits = 3;
							break;
						default:
							digits = 2;
						}
						editField = new EditField(digits, calendar.get(cf));
					} else {
						return;
					}
				}
				if (editField.addChar(c)) {
					if (commitEditField()) {
						fieldNext();
					} else {
						editField = null;
						if (selection.length > 0) {
							selection[0] = calendar.getTime();
						}
						updateText();
					}
				}
				if (selection.length > 0) {
					selection[0] = calendar.getTime();
				}
				updatePicker();
			} else {
				try {
					setSelection(df.parse(e.text));
					fireSelectionChanged();
				} catch (ParseException pe) {
					// do nothing
				}
			}
		}
		updateText();
	}

	/**
	 * @param pattern
	 * @param allowedTimeZones
	 * @throws IllegalArgumentException
	 */
	public void setPattern(final String pattern,
			final TimeZone[] allowedTimeZones) throws IllegalArgumentException {
		this.setPattern(pattern);
		if (pattern.indexOf('z') != -1) {
			this.allowedTimezones = allowedTimeZones;
		}
	}
}