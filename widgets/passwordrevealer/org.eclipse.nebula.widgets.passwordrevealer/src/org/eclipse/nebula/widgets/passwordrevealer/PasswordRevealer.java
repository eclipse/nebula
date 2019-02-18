/*******************************************************************************
 * Copyright (c) 2019 Laurent CARON. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API
 * and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.passwordrevealer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.GestureListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SegmentEvent;
import org.eclipse.swt.events.SegmentListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TouchListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

/**
 * Instances of this class are selectable user interface
 * objects that allow the user to enter and modify passwords.
 * A "eye" button is drawned on the right side of the widget. When one clicks on the button,
 * the password is revealed, and when the user stops clicking the password is displayed with dots.
 * <p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>CENTER, ICON_CANCEL, ICON_SEARCH, LEFT, MULTI, PASSWORD, SEARCH, SINGLE, RIGHT, READ_ONLY, WRAP</dd>
 * <dt><b>Events:</b></dt>
 * <dd>DefaultSelection, Modify, Verify, OrientationChange</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles MULTI and SINGLE may be specified,
 * and only one of the styles LEFT, CENTER, and RIGHT may be specified.
 * </p>
 * <p>
 * Note: The styles ICON_CANCEL and ICON_SEARCH are hints used in combination with SEARCH.
 * When the platform supports the hint, the text control shows these icons. When an icon
 * is selected, a default selection event is sent with the detail field set to one of
 * ICON_CANCEL or ICON_SEARCH. Normally, application code does not need to check the
 * detail. In the case of ICON_CANCEL, the text is cleared before the default selection
 * event is sent causing the application to search for an empty string.
 * </p>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 *
 * @see <a href="http://www.eclipse.org/swt/snippets/#text">Text snippets</a>
 * @see <a href="http://www.eclipse.org/swt/examples.php">SWT Example: ControlExample</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further information</a>
 * @noextend This class is not intended to be subclassed by clients.
 */
public class PasswordRevealer extends Composite {

	protected Text passwordField;
	private final EyeButton eyeButton;
	private final Text textField;
	private final Composite comp;
	private Point currentCaretPosition;

	/**
	 * Constructs a new instance of this class given its parent and a style value
	 * describing its behavior and appearance.
	 * <p>
	 * The style value is either one of the style constants defined in class
	 * <code>SWT</code> which is applicable to instances of this class, or must be
	 * built by <em>bitwise OR</em>'ing together (that is, using the
	 * <code>int</code> "|" operator) two or more of those <code>SWT</code> style
	 * constants. The class description lists the style constants that are
	 * applicable to the class. Style bits are also inherited from superclasses.
	 * </p>
	 *
	 * @param parent a composite control which will be the parent of the new
	 *            instance (cannot be null)
	 * @param style the style of control to construct
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the parent</li>
	 *                <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed
	 *                subclass</li>
	 *                </ul>
	 *
	 * @see Widget#getStyle()
	 */
	public PasswordRevealer(Composite parent, int style) {
		super(parent, SWT.BORDER);
		final GridLayout gl = new GridLayout(2, false);
		gl.horizontalSpacing = gl.verticalSpacing = gl.marginHeight = gl.marginWidth = 0;
		setLayout(gl);

		comp = new Composite(this, SWT.NONE);
		final GridLayout glComp = new GridLayout(1, false);
		glComp.horizontalSpacing = glComp.verticalSpacing = glComp.marginHeight = glComp.marginWidth = 0;
		comp.setLayout(glComp);
		comp.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, true));

		passwordField = new Text(comp, style | SWT.PASSWORD | removeFields(style, SWT.BORDER));
		passwordField.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		textField = new Text(comp, style | removeFields(style, SWT.PASSWORD, SWT.BORDER));
		final GridData gdText = new GridData(GridData.FILL, GridData.FILL, true, false);
		textField.setLayoutData(gdText);
		textField.setVisible(false);
		gdText.exclude = true;

		eyeButton = new EyeButton(this, SWT.NONE);
		eyeButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
	}

	private int removeFields(int original, int... styles) {
		int returnedStyle = original;
		for (final int toBeRemoved : styles) {
			if ((returnedStyle & toBeRemoved) != 0) {
				returnedStyle = returnedStyle & ~toBeRemoved;
			}
		}
		return returnedStyle;
	}

	void revealPassword() {
		currentCaretPosition = passwordField.getSelection();
		final String currentText = passwordField.getText();
		textField.setText(currentText);
		swap(passwordField, textField);
	}

	private void swap(Control first, Control second) {
		try {
			setRedraw(false);
			first.setVisible(false);
			((GridData) first.getLayoutData()).exclude = true;

			second.setVisible(true);
			((GridData) second.getLayoutData()).exclude = false;

			first.getParent().layout(true);
		} finally {
			setRedraw(true);
			redraw();
		}
	}

	void hidePassword() {
		swap(textField, passwordField);
		textField.setText("");
		passwordField.setFocus();
		passwordField.setSelection(currentCaretPosition);
	}

	// Inherithed methods

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when the receiver's text is modified, by sending
	 * it one of the messages defined in the <code>ModifyListener</code>
	 * interface.
	 *
	 * @param listener the listener which should be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see ModifyListener
	 * @see #removeModifyListener
	 */
	public void addModifyListener(ModifyListener listener) {
		passwordField.addModifyListener(listener);
	}

	/**
	 * Adds a segment listener.
	 * <p>
	 * A <code>SegmentEvent</code> is sent whenever text content is being modified or
	 * a segment listener is added or removed. You can
	 * customize the appearance of text by indicating certain characters to be inserted
	 * at certain text offsets. This may be used for bidi purposes, e.g. when
	 * adjacent segments of right-to-left text should not be reordered relative to
	 * each other.
	 * E.g., multiple Java string literals in a right-to-left language
	 * should generally remain in logical order to each other, that is, the
	 * way they are stored.
	 * </p>
	 * <p>
	 * <b>Warning</b>: This API is currently only implemented on Windows and GTK.
	 * <code>SegmentEvent</code>s won't be sent on Cocoa.
	 * </p>
	 *
	 * @param listener the listener which should be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see SegmentEvent
	 * @see SegmentListener
	 * @see #removeSegmentListener
	 *
	 * @since 3.8
	 */
	public void addSegmentListener(SegmentListener listener) {
		passwordField.addSegmentListener(listener);
	}

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when the control is selected by the user, by sending
	 * it one of the messages defined in the <code>SelectionListener</code>
	 * interface.
	 * <p>
	 * <code>widgetSelected</code> is not called for texts.
	 * <code>widgetDefaultSelected</code> is typically called when ENTER is pressed in a single-line text,
	 * or when ENTER is pressed in a search text. If the receiver has the <code>SWT.SEARCH | SWT.ICON_CANCEL</code> style
	 * and the user cancels the search, the event object detail field contains the value <code>SWT.ICON_CANCEL</code>.
	 * Likewise, if the receiver has the <code>SWT.ICON_SEARCH</code> style and the icon search is selected, the
	 * event object detail field contains the value <code>SWT.ICON_SEARCH</code>.
	 * </p>
	 *
	 * @param listener the listener which should be notified when the control is selected by the user
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see SelectionListener
	 * @see #removeSelectionListener
	 * @see SelectionEvent
	 */
	public void addSelectionListener(SelectionListener listener) {
		passwordField.addSelectionListener(listener);
	}

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when the receiver's text is verified, by sending
	 * it one of the messages defined in the <code>VerifyListener</code>
	 * interface.
	 *
	 * @param listener the listener which should be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see VerifyListener
	 * @see #removeVerifyListener
	 */
	public void addVerifyListener(VerifyListener listener) {
		passwordField.addVerifyListener(listener);
	}

	/**
	 * Appends a string.
	 * <p>
	 * The new text is appended to the text at
	 * the end of the widget.
	 * </p>
	 *
	 * @param string the string to be appended
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the string is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void append(String string) {
		passwordField.append(string);
	}

	/**
	 * Clears the selection.
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void clearSelection() {
		passwordField.clearSelection();
	}

	/**
	 * Copies the selected text.
	 * <p>
	 * The current selection is copied to the clipboard.
	 * </p>
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void copy() {
		passwordField.copy();
	}

	/**
	 * Cuts the selected text.
	 * <p>
	 * The current selection is first copied to the
	 * clipboard and then deleted from the widget.
	 * </p>
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void cut() {
		passwordField.cut();
	}

	/**
	 * Returns the line number of the caret.
	 * <p>
	 * The line number of the caret is returned.
	 * </p>
	 *
	 * @return the line number
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public int getCaretLineNumber() {
		return passwordField.getCaretLineNumber();
	}

	/**
	 * Returns a point describing the location of the caret relative
	 * to the receiver.
	 *
	 * @return a point, the location of the caret
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public Point getCaretLocation() {
		return passwordField.getCaretLocation();
	}

	/**
	 * Returns the character position of the caret.
	 * <p>
	 * Indexing is zero based.
	 * </p>
	 *
	 * @return the position of the caret
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public int getCaretPosition() {
		return passwordField.getCaretPosition();
	}

	/**
	 * Returns the number of characters.
	 *
	 * @return number of characters in the widget
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public int getCharCount() {
		return passwordField.getCharCount();
	}

	/**
	 * Returns the double click enabled flag.
	 * <p>
	 * The double click flag enables or disables the
	 * default action of the text widget when the user
	 * double clicks.
	 * </p>
	 *
	 * @return whether or not double click is enabled
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public boolean getDoubleClickEnabled() {
		return passwordField.getDoubleClickEnabled();
	}

	/**
	 * Returns the echo character.
	 * <p>
	 * The echo character is the character that is
	 * displayed when the user enters text or the
	 * text is changed by the programmer.
	 * </p>
	 *
	 * @return the echo character
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see #setEchoChar
	 */
	public char getEchoChar() {
		return passwordField.getEchoChar();
	}

	/**
	 * Returns the editable state.
	 *
	 * @return whether or not the receiver is editable
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public boolean getEditable() {
		return passwordField.getEditable();
	}

	/**
	 * Returns the number of lines.
	 *
	 * @return the number of lines in the widget
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public int getLineCount() {
		return passwordField.getLineCount();
	}

	/**
	 * Returns the line delimiter.
	 *
	 * @return a string that is the line delimiter
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see #DELIMITER
	 */
	public String getLineDelimiter() {
		return passwordField.getLineDelimiter();
	}

	/**
	 * Returns the height of a line.
	 *
	 * @return the height of a row of text
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public int getLineHeight() {
		return passwordField.getLineHeight();
	}

	/**
	 * Returns the widget message. The message text is displayed
	 * as a hint for the user, indicating the purpose of the field.
	 * <p>
	 * Typically this is used in conjunction with <code>SWT.SEARCH</code>.
	 * </p>
	 *
	 * @return the widget message
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @since 3.3
	 */
	public String getMessage() {
		return passwordField.getMessage();
	}

	/**
	 * Returns a <code>Point</code> whose x coordinate is the
	 * character position representing the start of the selected
	 * text, and whose y coordinate is the character position
	 * representing the end of the selection. An "empty" selection
	 * is indicated by the x and y coordinates having the same value.
	 * <p>
	 * Indexing is zero based. The range of a selection is from
	 * 0..N where N is the number of characters in the widget.
	 * </p>
	 *
	 * @return a point representing the selection start and end
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public Point getSelection() {
		return passwordField.getSelection();
	}

	/**
	 * Returns the number of selected characters.
	 *
	 * @return the number of selected characters.
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public int getSelectionCount() {
		return passwordField.getSelectionCount();
	}

	/**
	 * Gets the selected text, or an empty string if there is no current selection.
	 *
	 * @return the selected text
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public String getSelectionText() {
		return passwordField.getSelectionText();
	}

	/**
	 * Returns the number of tabs.
	 * <p>
	 * Tab stop spacing is specified in terms of the
	 * space (' ') character. The width of a single
	 * tab stop is the pixel width of the spaces.
	 * </p>
	 *
	 * @return the number of tab characters
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public int getTabs() {
		return passwordField.getTabs();
	}

	/**
	 * Returns the widget text.
	 * <p>
	 * The text for a text widget is the characters in the widget, or
	 * an empty string if this has never been set.
	 * </p>
	 *
	 * @return the widget text
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public String getText() {
		return passwordField.getText();
	}

	/**
	 * Returns the widget's text as a character array.
	 * <p>
	 * The text for a text widget is the characters in the widget, or
	 * a zero-length array if this has never been set.
	 * </p>
	 * <p>
	 * Note: Use this API to prevent the text from being written into a String
	 * object whose lifecycle is outside of your control. This can help protect
	 * the text, for example, when the widget is used as a password field.
	 * However, the text can't be protected if an {@link SWT#Segments} or
	 * {@link SWT#Verify} listener has been added to the widget.
	 * </p>
	 *
	 * @return a character array that contains the widget's text
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see #setTextChars(char[])
	 *
	 * @since 3.7
	 */
	public char[] getTextChars() {
		return passwordField.getTextChars();
	}

	/**
	 * Returns a range of text. Returns an empty string if the
	 * start of the range is greater than the end.
	 * <p>
	 * Indexing is zero based. The range of
	 * a selection is from 0..N-1 where N is
	 * the number of characters in the widget.
	 * </p>
	 *
	 * @param start the start of the range
	 * @param end the end of the range
	 * @return the range of text
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public String getText(int start, int end) {
		return passwordField.getText(start, end);
	}

	/**
	 * Returns the maximum number of characters that the receiver is capable of holding.
	 * <p>
	 * If this has not been changed by <code>setTextLimit()</code>,
	 * it will be the constant <code>Text.LIMIT</code>.
	 * </p>
	 *
	 * @return the text limit
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see #LIMIT
	 */
	public int getTextLimit() {
		return passwordField.getTextLimit();
	}

	/**
	 * Returns the zero-relative index of the line which is currently
	 * at the top of the receiver.
	 * <p>
	 * This index can change when lines are scrolled or new lines are added or removed.
	 * </p>
	 *
	 * @return the index of the top line
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public int getTopIndex() {
		return passwordField.getTopIndex();
	}

	/**
	 * Returns the zero-relative index of the line which is currently
	 * at the top of the receiver.
	 * <p>
	 * This index can change when lines are scrolled or new lines are added or removed.
	 * </p>
	 *
	 * @return the index of the top line
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public int getTopPixel() {
		return passwordField.getTopPixel();
	}

	/**
	 * Inserts a string.
	 * <p>
	 * The old selection is replaced with the new text.
	 * </p>
	 *
	 * @param string the string
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the string is <code>null</code></li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void insert(String string) {
		passwordField.insert(string);
	}

	/**
	 * Pastes text from clipboard.
	 * <p>
	 * The selected text is deleted from the widget
	 * and new text inserted from the clipboard.
	 * </p>
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void paste() {
		passwordField.paste();
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when the receiver's text is modified.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see ModifyListener
	 * @see #addModifyListener
	 */
	public void removeModifyListener(ModifyListener listener) {
		passwordField.removeModifyListener(listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when the receiver's text is modified.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see SegmentEvent
	 * @see SegmentListener
	 * @see #addSegmentListener
	 *
	 * @since 3.8
	 */
	public void removeSegmentListener(SegmentListener listener) {
		passwordField.removeSegmentListener(listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when the control is selected by the user.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see SelectionListener
	 * @see #addSelectionListener
	 */
	public void removeSelectionListener(SelectionListener listener) {
		passwordField.removeSelectionListener(listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when the control is verified.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see VerifyListener
	 * @see #addVerifyListener
	 */
	public void removeVerifyListener(VerifyListener listener) {
		passwordField.removeVerifyListener(listener);
	}

	/**
	 * Selects all the text in the receiver.
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void selectAll() {
		passwordField.selectAll();
	}

	/**
	 * Sets the double click enabled flag.
	 * <p>
	 * The double click flag enables or disables the
	 * default action of the text widget when the user
	 * double clicks.
	 * </p>
	 * <p>
	 * Note: This operation is a hint and is not supported on
	 * platforms that do not have this concept.
	 * </p>
	 *
	 * @param doubleClick the new double click flag
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setDoubleClickEnabled(boolean doubleClick) {
		passwordField.setDoubleClickEnabled(doubleClick);
	}

	/**
	 * Sets the echo character.
	 * <p>
	 * The echo character is the character that is
	 * displayed when the user enters text or the
	 * text is changed by the programmer. Setting
	 * the echo character to '\0' clears the echo
	 * character and redraws the original text.
	 * If for any reason the echo character is invalid,
	 * or if the platform does not allow modification
	 * of the echo character, the default echo character
	 * for the platform is used.
	 * </p>
	 *
	 * @param echo the new echo character
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setEchoChar(char echo) {
		passwordField.setEchoChar(echo);
	}

	/**
	 * Sets the editable state.
	 *
	 * @param editable the new editable state
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setEditable(boolean editable) {
		passwordField.setEditable(editable);
	}

	/**
	 * Sets the font that the receiver will use to paint textual information
	 * to the font specified by the argument, or to the default font for that
	 * kind of control if the argument is null.
	 *
	 * @param font the new font (or null)
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	@Override
	public void setFont(Font font) {
		super.setFont(font);
		comp.setFont(font);
		passwordField.setFont(font);
		textField.setFont(font);
	}

	/**
	 * Sets the widget message. The message text is displayed
	 * as a hint for the user, indicating the purpose of the field.
	 * <p>
	 * Typically this is used in conjunction with <code>SWT.SEARCH</code>.
	 * </p>
	 *
	 * @param message the new message
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the message is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @since 3.3
	 */
	public void setMessage(String message) {
		passwordField.setMessage(message);
	}

	/**
	 * Sets the orientation of the receiver, which must be one
	 * of the constants <code>SWT.LEFT_TO_RIGHT</code> or <code>SWT.RIGHT_TO_LEFT</code>.
	 * <p>
	 * Note: This operation is a hint and is not supported on
	 * platforms that do not have this concept.
	 * </p>
	 *
	 * @param orientation new orientation style
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @since 2.1.2
	 */
	@Override
	public void setOrientation(int orientation) {
		passwordField.setOrientation(orientation);
	}

	/**
	 * Sets the selection.
	 * <p>
	 * Indexing is zero based. The range of
	 * a selection is from 0..N where N is
	 * the number of characters in the widget.
	 * </p>
	 * <p>
	 * Text selections are specified in terms of
	 * caret positions. In a text widget that
	 * contains N characters, there are N+1 caret
	 * positions, ranging from 0..N. This differs
	 * from other functions that address character
	 * position such as getText () that use the
	 * regular array indexing rules.
	 * </p>
	 *
	 * @param start new caret position
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setSelection(int start) {
		passwordField.setSelection(start);
	}

	/**
	 * Sets the selection to the range specified
	 * by the given start and end indices.
	 * <p>
	 * Indexing is zero based. The range of
	 * a selection is from 0..N where N is
	 * the number of characters in the widget.
	 * </p>
	 * <p>
	 * Text selections are specified in terms of
	 * caret positions. In a text widget that
	 * contains N characters, there are N+1 caret
	 * positions, ranging from 0..N. This differs
	 * from other functions that address character
	 * position such as getText () that use the
	 * usual array indexing rules.
	 * </p>
	 *
	 * @param start the start of the range
	 * @param end the end of the range
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setSelection(int start, int end) {
		passwordField.setSelection(start, end);
	}

	/**
	 * Sets the selection to the range specified
	 * by the given point, where the x coordinate
	 * represents the start index and the y coordinate
	 * represents the end index.
	 * <p>
	 * Indexing is zero based. The range of
	 * a selection is from 0..N where N is
	 * the number of characters in the widget.
	 * </p>
	 * <p>
	 * Text selections are specified in terms of
	 * caret positions. In a text widget that
	 * contains N characters, there are N+1 caret
	 * positions, ranging from 0..N. This differs
	 * from other functions that address character
	 * position such as getText () that use the
	 * usual array indexing rules.
	 * </p>
	 *
	 * @param selection the point
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the point is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setSelection(Point selection) {
		passwordField.setSelection(selection);
	}

	/**
	 * Sets the number of tabs.
	 * <p>
	 * Tab stop spacing is specified in terms of the
	 * space (' ') character. The width of a single
	 * tab stop is the pixel width of the spaces.
	 * </p>
	 *
	 * @param tabs the number of tabs
	 *
	 *            </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setTabs(int tabs) {
		passwordField.setTabs(tabs);
	}

	/**
	 * Sets the contents of the receiver to the given string. If the receiver has style
	 * SINGLE and the argument contains multiple lines of text, the result of this
	 * operation is undefined and may vary from platform to platform.
	 * <p>
	 * Note: If control characters like '\n', '\t' etc. are used
	 * in the string, then the behavior is platform dependent.
	 * </p>
	 * 
	 * @param string the new text
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the string is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setText(String string) {
		passwordField.setText(string);
	}

	/**
	 * Sets the contents of the receiver to the characters in the array. If the receiver
	 * has style <code>SWT.SINGLE</code> and the argument contains multiple lines of text
	 * then the result of this operation is undefined and may vary between platforms.
	 * <p>
	 * Note: Use this API to prevent the text from being written into a String
	 * object whose lifecycle is outside of your control. This can help protect
	 * the text, for example, when the widget is used as a password field.
	 * However, the text can't be protected if an {@link SWT#Segments} or
	 * {@link SWT#Verify} listener has been added to the widget.
	 * </p>
	 *
	 * @param text a character array that contains the new text
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the array is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see #getTextChars()
	 *
	 * @since 3.7
	 */
	public void setTextChars(char[] text) {
		passwordField.setTextChars(text);
	}

	/**
	 * Sets the maximum number of characters that the receiver
	 * is capable of holding to be the argument.
	 * <p>
	 * Instead of trying to set the text limit to zero, consider
	 * creating a read-only text widget.
	 * </p>
	 * <p>
	 * To reset this value to the default, use <code>setTextLimit(Text.LIMIT)</code>.
	 * Specifying a limit value larger than <code>Text.LIMIT</code> sets the
	 * receiver's limit to <code>Text.LIMIT</code>.
	 * </p>
	 *
	 * @param limit new text limit
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_CANNOT_BE_ZERO - if the limit is zero</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see #LIMIT
	 */
	public void setTextLimit(int limit) {
		passwordField.setTextLimit(limit);
	}

	/**
	 * Sets the zero-relative index of the line which is currently
	 * at the top of the receiver. This index can change when lines
	 * are scrolled or new lines are added and removed.
	 *
	 * @param index the index of the top item
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setTopIndex(int index) {
		passwordField.setTopIndex(index);
	}

	/**
	 * Shows the selection.
	 * <p>
	 * If the selection is already showing
	 * in the receiver, this method simply returns. Otherwise,
	 * lines are scrolled until the selection is visible.
	 * </p>
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void showSelection() {
		passwordField.showSelection();
	}

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when the control is moved or resized, by sending
	 * it one of the messages defined in the <code>ControlListener</code>
	 * interface.
	 *
	 * @param listener the listener which should be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see ControlListener
	 * @see #removeControlListener
	 */
	@Override
	public void addControlListener(ControlListener listener) {
		super.addControlListener(listener);
		comp.addControlListener(listener);
		passwordField.addControlListener(listener);
	}

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when a drag gesture occurs, by sending it
	 * one of the messages defined in the <code>DragDetectListener</code>
	 * interface.
	 *
	 * @param listener the listener which should be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see DragDetectListener
	 * @see #removeDragDetectListener
	 *
	 * @since 3.3
	 */
	@Override
	public void addDragDetectListener(DragDetectListener listener) {
		super.addDragDetectListener(listener);
		comp.addDragDetectListener(listener);
		passwordField.addDragDetectListener(listener);
	}

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when the control gains or loses focus, by sending
	 * it one of the messages defined in the <code>FocusListener</code>
	 * interface.
	 *
	 * @param listener the listener which should be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see FocusListener
	 * @see #removeFocusListener
	 */
	@Override
	public void addFocusListener(FocusListener listener) {
		super.addFocusListener(listener);
		comp.addFocusListener(listener);
		passwordField.addFocusListener(listener);
	}

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when gesture events are generated for the control,
	 * by sending it one of the messages defined in the
	 * <code>GestureListener</code> interface.
	 * <p>
	 * NOTE: If <code>setTouchEnabled(true)</code> has previously been
	 * invoked on the receiver then <code>setTouchEnabled(false)</code>
	 * must be invoked on it to specify that gesture events should be
	 * sent instead of touch events.
	 * </p>
	 * <p>
	 * <b>Warning</b>: This API is currently only implemented on Windows and Cocoa.
	 * SWT doesn't send Gesture or Touch events on GTK.
	 * </p>
	 *
	 * @param listener the listener which should be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see GestureListener
	 * @see #removeGestureListener
	 * @see #setTouchEnabled
	 *
	 * @since 3.7
	 */
	@Override
	public void addGestureListener(GestureListener listener) {
		super.addGestureListener(listener);
		comp.addGestureListener(listener);
		passwordField.addGestureListener(listener);
	}

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when help events are generated for the control,
	 * by sending it one of the messages defined in the
	 * <code>HelpListener</code> interface.
	 *
	 * @param listener the listener which should be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see HelpListener
	 * @see #removeHelpListener
	 */
	@Override
	public void addHelpListener(HelpListener listener) {
		super.addHelpListener(listener);
		comp.addHelpListener(listener);
		passwordField.addHelpListener(listener);
	}

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when keys are pressed and released on the system keyboard, by sending
	 * it one of the messages defined in the <code>KeyListener</code>
	 * interface.
	 * <p>
	 * When a key listener is added to a control, the control
	 * will take part in widget traversal. By default, all
	 * traversal keys (such as the tab key and so on) are
	 * delivered to the control. In order for a control to take
	 * part in traversal, it should listen for traversal events.
	 * Otherwise, the user can traverse into a control but not
	 * out. Note that native controls such as table and tree
	 * implement key traversal in the operating system. It is
	 * not necessary to add traversal listeners for these controls,
	 * unless you want to override the default traversal.
	 * </p>
	 * 
	 * @param listener the listener which should be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see KeyListener
	 * @see #removeKeyListener
	 */
	@Override
	public void addKeyListener(KeyListener listener) {
		super.addKeyListener(listener);
		comp.addKeyListener(listener);
		passwordField.addKeyListener(listener);
	}

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when the platform-specific context menu trigger
	 * has occurred, by sending it one of the messages defined in
	 * the <code>MenuDetectListener</code> interface.
	 *
	 * @param listener the listener which should be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see MenuDetectListener
	 * @see #removeMenuDetectListener
	 *
	 * @since 3.3
	 */
	@Override
	public void addMenuDetectListener(MenuDetectListener listener) {
		super.addMenuDetectListener(listener);
		comp.addMenuDetectListener(listener);
		passwordField.addMenuDetectListener(listener);
	}

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when mouse buttons are pressed and released, by sending
	 * it one of the messages defined in the <code>MouseListener</code>
	 * interface.
	 *
	 * @param listener the listener which should be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see MouseListener
	 * @see #removeMouseListener
	 */
	@Override
	public void addMouseListener(MouseListener listener) {
		super.addMouseListener(listener);
		comp.addMouseListener(listener);
		passwordField.addMouseListener(listener);
	}

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when the mouse passes or hovers over controls, by sending
	 * it one of the messages defined in the <code>MouseTrackListener</code>
	 * interface.
	 *
	 * @param listener the listener which should be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see MouseTrackListener
	 * @see #removeMouseTrackListener
	 */
	@Override
	public void addMouseTrackListener(MouseTrackListener listener) {
		super.addMouseTrackListener(listener);
		comp.addMouseTrackListener(listener);
		passwordField.addMouseTrackListener(listener);
	}

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when the mouse moves, by sending it one of the
	 * messages defined in the <code>MouseMoveListener</code>
	 * interface.
	 *
	 * @param listener the listener which should be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see MouseMoveListener
	 * @see #removeMouseMoveListener
	 */
	@Override
	public void addMouseMoveListener(MouseMoveListener listener) {
		super.addMouseMoveListener(listener);
		comp.addMouseMoveListener(listener);
		passwordField.addMouseMoveListener(listener);
	}

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when the mouse wheel is scrolled, by sending
	 * it one of the messages defined in the
	 * <code>MouseWheelListener</code> interface.
	 *
	 * @param listener the listener which should be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see MouseWheelListener
	 * @see #removeMouseWheelListener
	 *
	 * @since 3.3
	 */
	@Override
	public void addMouseWheelListener(MouseWheelListener listener) {
		super.addMouseWheelListener(listener);
		comp.addMouseWheelListener(listener);
		passwordField.addMouseWheelListener(listener);
	}

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when touch events occur, by sending it
	 * one of the messages defined in the <code>TouchListener</code>
	 * interface.
	 * <p>
	 * NOTE: You must also call <code>setTouchEnabled(true)</code> to
	 * specify that touch events should be sent, which will cause gesture
	 * events to not be sent.
	 * </p>
	 * <p>
	 * <b>Warning</b>: This API is currently only implemented on Windows and Cocoa.
	 * SWT doesn't send Gesture or Touch events on GTK.
	 * </p>
	 *
	 * @param listener the listener which should be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see TouchListener
	 * @see #removeTouchListener
	 * @see #setTouchEnabled
	 *
	 * @since 3.7
	 */
	@Override
	public void addTouchListener(TouchListener listener) {
		super.addTouchListener(listener);
		comp.addTouchListener(listener);
		passwordField.addTouchListener(listener);
	}

	/**
	 * Forces the receiver to have the <em>keyboard focus</em>, causing
	 * all keyboard events to be delivered to it.
	 *
	 * @return <code>true</code> if the control got focus, and <code>false</code> if it was unable to.
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see #setFocus
	 */
	@Override
	public boolean forceFocus() {
		return passwordField.forceFocus();
	}

	/**
	 * Returns <code>true</code> if the receiver has the user-interface
	 * focus, and <code>false</code> otherwise.
	 *
	 * @return the receiver's focus state
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	@Override
	public boolean isFocusControl() {
		return passwordField.isFocusControl();
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when the control is moved or resized.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see ControlListener
	 * @see #addControlListener
	 */
	@Override
	public void removeControlListener(ControlListener listener) {
		super.removeControlListener(listener);
		comp.removeControlListener(listener);
		passwordField.removeControlListener(listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when the control gains or loses focus.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see FocusListener
	 * @see #addFocusListener
	 */
	@Override
	public void removeFocusListener(FocusListener listener) {
		super.removeFocusListener(listener);
		comp.removeFocusListener(listener);
		passwordField.removeFocusListener(listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when gesture events are generated for the control.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see GestureListener
	 * @see #addGestureListener
	 *
	 * @since 3.7
	 */
	@Override
	public void removeGestureListener(GestureListener listener) {
		super.removeGestureListener(listener);
		comp.removeGestureListener(listener);
		passwordField.removeGestureListener(listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when the help events are generated for the control.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see HelpListener
	 * @see #addHelpListener
	 */
	@Override
	public void removeHelpListener(HelpListener listener) {
		super.removeHelpListener(listener);
		comp.removeHelpListener(listener);
		passwordField.removeHelpListener(listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when keys are pressed and released on the system keyboard.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see KeyListener
	 * @see #addKeyListener
	 */
	@Override
	public void removeKeyListener(KeyListener listener) {
		super.removeKeyListener(listener);
		comp.removeKeyListener(listener);
		passwordField.removeKeyListener(listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when the platform-specific context menu trigger has
	 * occurred.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see MenuDetectListener
	 * @see #addMenuDetectListener
	 *
	 * @since 3.3
	 */
	@Override
	public void removeMenuDetectListener(MenuDetectListener listener) {
		super.removeMenuDetectListener(listener);
		comp.removeMenuDetectListener(listener);
		passwordField.removeMenuDetectListener(listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when the mouse passes or hovers over controls.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see MouseTrackListener
	 * @see #addMouseTrackListener
	 */
	@Override
	public void removeMouseTrackListener(MouseTrackListener listener) {
		super.removeMouseTrackListener(listener);
		comp.removeMouseTrackListener(listener);
		passwordField.removeMouseTrackListener(listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when mouse buttons are pressed and released.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see MouseListener
	 * @see #addMouseListener
	 */
	@Override
	public void removeMouseListener(MouseListener listener) {
		super.removeMouseListener(listener);
		comp.removeMouseListener(listener);
		passwordField.removeMouseListener(listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when the mouse moves.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see MouseMoveListener
	 * @see #addMouseMoveListener
	 */
	@Override
	public void removeMouseMoveListener(MouseMoveListener listener) {
		super.removeMouseMoveListener(listener);
		comp.removeMouseMoveListener(listener);
		passwordField.removeMouseMoveListener(listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when the mouse wheel is scrolled.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see MouseWheelListener
	 * @see #addMouseWheelListener
	 *
	 * @since 3.3
	 */
	@Override
	public void removeMouseWheelListener(MouseWheelListener listener) {
		super.removeMouseWheelListener(listener);
		comp.removeMouseWheelListener(listener);
		passwordField.removeMouseWheelListener(listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when touch events occur.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see TouchListener
	 * @see #addTouchListener
	 *
	 * @since 3.7
	 */
	@Override
	public void removeTouchListener(TouchListener listener) {
		super.removeTouchListener(listener);
		comp.removeTouchListener(listener);
		passwordField.removeTouchListener(listener);
	}

	/**
	 * Sets the receiver's background color to the color specified
	 * by the argument, or to the default system color for the control
	 * if the argument is null.
	 * <p>
	 * Note: This operation is a hint and may be overridden by the platform.
	 * </p>
	 * 
	 * @param color the new color (or null)
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	@Override
	public void setBackground(Color color) {
		super.setBackground(color);
		comp.setBackground(color);
		passwordField.setBackground(color);
		textField.setBackground(color);
	}

	/**
	 * If the argument is <code>true</code>, causes the receiver to have
	 * all mouse events delivered to it until the method is called with
	 * <code>false</code> as the argument. Note that on some platforms,
	 * a mouse button must currently be down for capture to be assigned.
	 *
	 * @param capture <code>true</code> to capture the mouse, and <code>false</code> to release it
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	@Override
	public void setCapture(boolean capture) {
		super.setCapture(capture);
		comp.setCapture(capture);
		passwordField.setCapture(capture);
		textField.setCapture(capture);
	}

	/**
	 * Sets the receiver's cursor to the cursor specified by the
	 * argument, or to the default cursor for that kind of control
	 * if the argument is null.
	 * <p>
	 * When the mouse pointer passes over a control its appearance
	 * is changed to match the control's cursor.
	 * </p>
	 *
	 * @param cursor the new cursor (or null)
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	@Override
	public void setCursor(Cursor cursor) {
		super.setCursor(cursor);
		comp.setCursor(cursor);
		passwordField.setCursor(cursor);
		textField.setCursor(cursor);
	}

	/**
	 * Sets the receiver's drag detect state. If the argument is
	 * <code>true</code>, the receiver will detect drag gestures,
	 * otherwise these gestures will be ignored.
	 *
	 * @param dragDetect the new drag detect state
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @since 3.3
	 */
	@Override
	public void setDragDetect(boolean dragDetect) {
		super.setDragDetect(dragDetect);
		comp.setDragDetect(dragDetect);
		passwordField.setDragDetect(dragDetect);
	}

	/**
	 * Causes the receiver to have the <em>keyboard focus</em>,
	 * such that all keyboard events will be delivered to it. Focus
	 * reassignment will respect applicable platform constraints.
	 *
	 * @return <code>true</code> if the control got focus, and <code>false</code> if it was unable to.
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see #forceFocus
	 */
	@Override
	public boolean setFocus() {
		return passwordField.setFocus();
	}

	/**
	 * Sets the receiver's foreground color to the color specified
	 * by the argument, or to the default system color for the control
	 * if the argument is null.
	 * <p>
	 * Note: This operation is a hint and may be overridden by the platform.
	 * </p>
	 * 
	 * @param color the new color (or null)
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	@Override
	public void setForeground(Color color) {
		super.setForeground(color);
		comp.setForeground(color);
		passwordField.setForeground(color);
		textField.setForeground(color);
	}

	/**
	 * Sets the receiver's pop up menu to the argument.
	 * All controls may optionally have a pop up
	 * menu that is displayed when the user requests one for
	 * the control. The sequence of key strokes, button presses
	 * and/or button releases that are used to request a pop up
	 * menu is platform specific.
	 * <p>
	 * Note: Disposing of a control that has a pop up menu will
	 * dispose of the menu. To avoid this behavior, set the
	 * menu to null before the control is disposed.
	 * </p>
	 *
	 * @param menu the new pop up menu
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_MENU_NOT_POP_UP - the menu is not a pop up menu</li>
	 *                <li>ERROR_INVALID_PARENT - if the menu is not in the same widget tree</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the menu has been disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	@Override
	public void setMenu(Menu menu) {
		super.setMenu(menu);
		comp.setMenu(menu);
		passwordField.setMenu(menu);
		textField.setMenu(menu);
	}

	/**
	 * Sets the receiver's tool tip text to the argument, which
	 * may be null indicating that the default tool tip for the
	 * control will be shown. For a control that has a default
	 * tool tip, such as the Tree control on Windows, setting
	 * the tool tip text to an empty string replaces the default,
	 * causing no tool tip text to be shown.
	 * <p>
	 * The mnemonic indicator (character '&amp;') is not displayed in a tool tip.
	 * To display a single '&amp;' in the tool tip, the character '&amp;' can be
	 * escaped by doubling it in the string.
	 * </p>
	 * <p>
	 * NOTE: This operation is a hint and behavior is platform specific, on Windows
	 * for CJK-style mnemonics of the form " (&C)" at the end of the tooltip text
	 * are not shown in tooltip.
	 * </p>
	 *
	 * @param string the new tool tip text (or null)
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	@Override
	public void setToolTipText(String string) {
		super.setToolTipText(string);
		comp.setToolTipText(string);
		passwordField.setToolTipText(string);
		textField.setToolTipText(string);
	}

	/**
	 * Sets whether this control should send touch events (by default controls do not).
	 * Setting this to <code>false</code> causes the receiver to send gesture events
	 * instead. No exception is thrown if a touch-based input device is not
	 * detected (this can be determined with <code>Display#getTouchEnabled()</code>).
	 *
	 * @param enabled the new touch-enabled state
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *
	 * @see Display#getTouchEnabled
	 *
	 * @since 3.7
	 */
	@Override
	public void setTouchEnabled(boolean enabled) {
		super.setTouchEnabled(enabled);
		comp.setTouchEnabled(enabled);
		passwordField.setTouchEnabled(enabled);
		textField.setTouchEnabled(enabled);
	}

	/**
	 * Based on the argument, perform one of the expected platform
	 * traversal action. The argument should be one of the constants:
	 * <code>SWT.TRAVERSE_ESCAPE</code>, <code>SWT.TRAVERSE_RETURN</code>,
	 * <code>SWT.TRAVERSE_TAB_NEXT</code>, <code>SWT.TRAVERSE_TAB_PREVIOUS</code>,
	 * <code>SWT.TRAVERSE_ARROW_NEXT</code>, <code>SWT.TRAVERSE_ARROW_PREVIOUS</code>,
	 * <code>SWT.TRAVERSE_PAGE_NEXT</code> and <code>SWT.TRAVERSE_PAGE_PREVIOUS</code>.
	 *
	 * @param traversal the type of traversal
	 * @return true if the traversal succeeded
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	@Override
	public boolean traverse(int traversal) {
		return passwordField.traverse(traversal);
	}

	/**
	 * Performs a platform traversal action corresponding to a <code>KeyDown</code> event.
	 *
	 * <p>
	 * Valid traversal values are
	 * <code>SWT.TRAVERSE_NONE</code>, <code>SWT.TRAVERSE_MNEMONIC</code>,
	 * <code>SWT.TRAVERSE_ESCAPE</code>, <code>SWT.TRAVERSE_RETURN</code>,
	 * <code>SWT.TRAVERSE_TAB_NEXT</code>, <code>SWT.TRAVERSE_TAB_PREVIOUS</code>,
	 * <code>SWT.TRAVERSE_ARROW_NEXT</code>, <code>SWT.TRAVERSE_ARROW_PREVIOUS</code>,
	 * <code>SWT.TRAVERSE_PAGE_NEXT</code> and <code>SWT.TRAVERSE_PAGE_PREVIOUS</code>.
	 * If <code>traversal</code> is <code>SWT.TRAVERSE_NONE</code> then the Traverse
	 * event is created with standard values based on the KeyDown event. If
	 * <code>traversal</code> is one of the other traversal constants then the Traverse
	 * event is created with this detail, and its <code>doit</code> is taken from the
	 * KeyDown event.
	 * </p>
	 *
	 * @param traversal the type of traversal, or <code>SWT.TRAVERSE_NONE</code> to compute
	 *            this from <code>event</code>
	 * @param event the KeyDown event
	 *
	 * @return <code>true</code> if the traversal succeeded
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT if the event is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @since 3.6
	 */
	@Override
	public boolean traverse(int traversal, Event event) {
		return passwordField.traverse(traversal, event);
	}

	/**
	 * Performs a platform traversal action corresponding to a <code>KeyDown</code> event.
	 *
	 * <p>
	 * Valid traversal values are
	 * <code>SWT.TRAVERSE_NONE</code>, <code>SWT.TRAVERSE_MNEMONIC</code>,
	 * <code>SWT.TRAVERSE_ESCAPE</code>, <code>SWT.TRAVERSE_RETURN</code>,
	 * <code>SWT.TRAVERSE_TAB_NEXT</code>, <code>SWT.TRAVERSE_TAB_PREVIOUS</code>,
	 * <code>SWT.TRAVERSE_ARROW_NEXT</code>, <code>SWT.TRAVERSE_ARROW_PREVIOUS</code>,
	 * <code>SWT.TRAVERSE_PAGE_NEXT</code> and <code>SWT.TRAVERSE_PAGE_PREVIOUS</code>.
	 * If <code>traversal</code> is <code>SWT.TRAVERSE_NONE</code> then the Traverse
	 * event is created with standard values based on the KeyDown event. If
	 * <code>traversal</code> is one of the other traversal constants then the Traverse
	 * event is created with this detail, and its <code>doit</code> is taken from the
	 * KeyDown event.
	 * </p>
	 *
	 * @param traversal the type of traversal, or <code>SWT.TRAVERSE_NONE</code> to compute
	 *            this from <code>event</code>
	 * @param event the KeyDown event
	 *
	 * @return <code>true</code> if the traversal succeeded
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT if the event is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @since 3.6
	 */
	@Override
	public boolean traverse(int traversal, KeyEvent event) {
		return passwordField.traverse(traversal, event);
	}

}
