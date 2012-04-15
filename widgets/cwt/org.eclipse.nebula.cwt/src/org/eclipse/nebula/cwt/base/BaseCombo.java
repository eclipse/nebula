/****************************************************************************
 * Copyright (c) 2005-2009 Jeremy Dowdall
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	 IBM Corporation - SWT's CCombo was relied upon _heavily_ for example and reference
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/

package org.eclipse.nebula.cwt.base;

import org.eclipse.nebula.cwt.animation.AnimationRunner;
import org.eclipse.nebula.cwt.animation.effects.Resize;
import org.eclipse.nebula.cwt.animation.movement.LinearInOut;
import org.eclipse.nebula.cwt.v.VButton;
import org.eclipse.nebula.cwt.v.VButtonPainter;
import org.eclipse.nebula.cwt.v.VControl;
import org.eclipse.nebula.cwt.v.VLayout;
import org.eclipse.nebula.cwt.v.VNative;
import org.eclipse.nebula.cwt.v.VPanel;
import org.eclipse.nebula.cwt.v.VSimpleLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * The AbstractCombo is an abstract class which provides the basic functionality
 * for a button with a DROP_DOWN, or "popup", shell component. When the user
 * selects the button the shell is set visible and the SWT Components which have
 * been placed on the "content" Composite will be shown.
 */
public abstract class BaseCombo extends Canvas {

	/**
	 * Special layout implementation to position the combo's drop-down Button
	 * within its Text.
	 */
	protected class DropComboLayout extends VLayout {

		protected Point computeSize(VPanel panel, int wHint, int hHint,
				boolean flushCache) {
			Point size = text.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			if (button.getVisible()) {
				size.x += size.y;
			}

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

			Point tSize = text.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			tSize.y += textMarginHeight;

			Point bSize = button.getVisible() ? new Point(tSize.y, tSize.y)
					: new Point(0, 0);

			if (leftAlign) {
				text.setBounds(cRect.x + bSize.x, cRect.y
						+ (win32 ? getBorderWidth() : 0),
						cRect.width - bSize.x, tSize.y);
				button.setBounds(cRect.x, cRect.y, bSize.x, bSize.y);
			} else {
				text.setBounds(cRect.x + (win32 ? 1 : 0), cRect.y
						+ (win32 ? 1 : 0), cRect.width - bSize.x
						- (win32 ? 2 : 0), tSize.y - (win32 ? 2 : 0));
				button.setBounds(
						win32 ? (cRect.x + cRect.width - cRect.height + 1)
								: (cRect.x + cRect.width - bSize.x), cRect.y,
						win32 ? (cRect.height - 1) : bSize.x,
						win32 ? cRect.height : bSize.y);
			}
		}
	}

	/**
	 * The value of {@link SWT#getVersion()} for the earliest known revision
	 * that fixes the SWT bug mentioned in bug 185739.
	 */
	protected static int SWT_MODAL_FIX_VERSION = 3346;

	/**
	 * true if the platform is carbon, false otherwise
	 */
	protected static final boolean carbon = "carbon".equals(SWT.getPlatform()); //$NON-NLS-1$

	/**
	 * true if the platform is gtk, false otherwise
	 */
	protected static final boolean gtk = "gtk".equals(SWT.getPlatform()); //$NON-NLS-1$

	/**
	 * true if the platform is win32, false otherwise
	 */
	protected static final boolean win32 = "win32".equals(SWT.getPlatform()); //$NON-NLS-1$

	/**
	 * true if the platform is winXP, false otherwise
	 */
	protected static final boolean winxp = "win32".equals(SWT.getPlatform()) && "5.0".equals(System.getProperty("os.version")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	/**
	 * true if the platform is win32, false otherwise
	 */
	protected static final boolean vista = "win32".equals(SWT.getPlatform()) && "6.0".equals(System.getProperty("os.version")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	/**
	 * A constant value used to pad the computed height for this widget, so that
	 * the combo's Button will fit without clipping its top and bottom borders.
	 */
	protected static final int textMarginHeight = win32 ? 4 : 0;

	/**
	 * A style constant indicating that this combo will only have a drop down
	 * button, rather than a button and a text box.
	 */
	protected static final int BUTTON_ONLY = 0;

	/**
	 * A constant indicating that the drop down button is always visible. Valid
	 * only when style is DROP_DOWN.
	 * 
	 * @see #BUTTON_AUTO
	 * @see #BUTTON_NEVER
	 */
	protected static final int BUTTON_ALWAYS = 1;

	/**
	 * A constant indicating that the drop down button is never visible. Valid
	 * only when style is DROP_DOWN.
	 * <p>
	 * This setting may be useful if the subclass of this BaseCombo allows
	 * programmatic opening and closing of the drop down shell via
	 * {@link #setOpen(boolean)}, or a similar method.
	 * </p>
	 * 
	 * @see #BUTTON_AUTO
	 * @see #BUTTON_ALWAYS
	 * @see #setOpen(boolean)
	 * @see #setOpen(boolean, Runnable)
	 */
	protected static final int BUTTON_NEVER = 2;

	/**
	 * A constant indicating that the drop down button is visible when the text
	 * box has the focus, and is hidden otherwise. Valid only when style is
	 * DROP_DOWN.
	 * 
	 * @see #BUTTON_ALWAYS
	 * @see #BUTTON_NEVER
	 */
	protected static final int BUTTON_AUTO = 3;

	private static int checkStyle(int style) {
		int rstyle = SWT.NONE;
		if ((style & SWT.BORDER) != 0) {
			if (win32 || (style & SWT.SIMPLE) != 0) {
				rstyle |= SWT.BORDER;
			}
		}
		if (win32) {
			rstyle |= SWT.DOUBLE_BUFFERED;
		}
		return rstyle;
	}

	/**
	 * A recursive method to find out if a composite is an ancestor of a
	 * control.
	 * 
	 * @param control
	 * @param composite
	 * @return true if the composite is an ancestor, false otherwise.
	 */
	protected static boolean containsControl(Control control,
			Composite composite) {
		if (composite != null && !composite.isDisposed()) {
			Control[] children = composite.getChildren();
			for (Control child : children) {
				if (!child.isDisposed()) {
					if (child == control) {
						return true;
					} else if (child instanceof Composite) {
						return containsControl(control, (Composite) child);
					}
				}
			}
		}
		return false;
	}

	/**
	 * The VPanel that is the base of this widget. If the style is SIMPLE, this
	 * panel will be the base of the content area, otherwise, it is the base of
	 * the text/button area.
	 */
	protected VPanel panel = null;

	/**
	 * The Button widget of a DROP_DOWN style combo. This value may be null --
	 * protect all references to this field with the checkButton() method.
	 */
	protected VButton button = null;

	/**
	 * True if a default image should be used for the button; false otherwise -
	 * as is the case when an image is set using {@link #setButtonImage(Image)}
	 * 
	 * @see #setButtonImage(Image)
	 */
	protected boolean defaultButtonImage = true;

	/**
	 * The Text widget of a DROP_DOWN style combo. This value may be null --
	 * protect all references to this field with the checkText() method.
	 */
	protected VNative<Text> text = null;

	/**
	 * The popup Shell widget of a DROP_DOWN style combo. This value may be null
	 * -- protect all references to this field with the checkContentShell()
	 * method.
	 */
	protected Shell contentShell = null;

	/**
	 * The widget contents of the popup Shell in a DROP_DOWN combo or the full
	 * contents of a SIMPLE combo. This value may be null -- protect all
	 * references to this field with the checkContent() method.
	 */
	protected Control content;

	/**
	 * The style bits requested. NOTE: this may not match the value returned by
	 * {@link #getStyle()} if invalid bits were requested.
	 */
	protected int style;

	/**
	 * Flag to indicate that this is a SIMPLE style combo.
	 */
	protected boolean simple;

	/**
	 * Flag to indicate that this combo's BUTTON should be displayed on the left
	 * side of its Text.
	 */
	protected boolean leftAlign = false;

	private int buttonVisibility;

	private Listener buttonVisibilityListener;

	private boolean dropDown;

	private boolean open = false;

	private boolean holdOpen = false;

	private VControl positionControl;

	private VControl stretchControl;

	private Listener textListener;
	private Listener shellListener;
	private Listener comboListener = new Listener() {
		public void handleEvent(Event event) {
			switch (event.type) {
			case SWT.Move:
				if (isOpen()) {
					setOpen(false);
				}
				break;
			case SWT.Resize:
				if (isOpen()) {
					setOpen(false);
				}
				layout(true);
				break;
			}
		}
	};

	private Listener disposeListener = new Listener() {
		public void handleEvent(Event event) {
			if (!isDisposed()) {
				getShell().removeListener(SWT.Deactivate, comboListener);
				if (checkContentShell()) {
					contentShell.dispose();
				}
			}
		}
	};

	/**
	 * Main constructor -- must be called by all subclasses in their own
	 * constructors.
	 * <p>
	 * SWT.TOGGLE, SWT.PUSH, SWT.ARROW, SWT.FLAT, SWT.TRAIL, SWT.LEAD,
	 * SWT.BORDER, SWT.SIMPLE, SWT.DROP_DOWN
	 * </p>
	 * 
	 * @param parent
	 *            the visual parent of this widget
	 * @param style
	 *            the requested SWT style bitmask for this widget
	 */
	public BaseCombo(Composite parent, int style) {
		super(parent, checkStyle(style));

		panel = new VPanel(this, SWT.NONE);
		panel.setWidget(this);

		init(style);
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when the receiver's text is modified, by sending it one of the messages
	 * defined in the <code>ModifyListener</code> interface.<br/>
	 * Note that this is NOT the correct way to listen for changes in the
	 * underlying model for the combo. This should be provided by some other
	 * mechanism, such as a {@link SelectionListener}.
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
	 * 
	 * @see ModifyListener
	 * @see #removeModifyListener
	 */
	protected void addModifyListener(ModifyListener listener) {
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		if (checkText()) {
			text.getControl().addModifyListener(listener);
		}
	}

	private void addTextListener() {
		text.getControl().addListener(SWT.KeyDown, textListener);
		text.getControl().addListener(SWT.Modify, textListener);
	}

	/**
	 * @return true if the {@link #button} field is in a fit state to be used
	 */
	protected boolean checkButton() {
		return (button != null && !button.isDisposed());
	}

	/**
	 * @return true if the {@link #content} field is in a fit state to be used
	 */
	protected boolean checkContent() {
		return (content != null && !content.isDisposed());
	}

	/**
	 * @return true if the {@link #contentShell} field is in a fit state to be
	 *         used
	 */
	protected boolean checkContentShell() {
		return (contentShell != null && !contentShell.isDisposed());
	}

	/**
	 * @return true if the {@link #text} field is in a fit state to be used
	 */
	protected boolean checkText() {
		return (text != null && !text.isDisposed());
	}

	private void createButton(int style) {
		int mask = BUTTON_ONLY | SWT.TOGGLE | SWT.PUSH | SWT.ARROW | SWT.FLAT;
		int buttonStyle = style & mask;

		button = new VButton(panel, buttonStyle | SWT.NO_FOCUS);
		if ((style & BUTTON_ONLY) == 0) {
			button.setMargins(0, 0);
		}
		button.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				setOpen(!isOpen());
			}
		});

		if (win32) {
			button.setPainter(new VButtonPainter() {
				@Override
				public void paintBackground(VControl control, Event e) {
					VButton button = (VButton) control;
					if (button.hasState(VControl.STATE_ACTIVE)) {
						Rectangle r = button.getBounds();
						e.gc.setBackground(e.display
								.getSystemColor(SWT.COLOR_GRAY));
						e.gc.fillRoundRectangle(r.x, r.y, r.width - 1,
								r.height - 1, 2, 2);
						e.gc.drawRoundRectangle(r.x, r.y, r.width - 1,
								r.height - 1, 2, 2);
					}
				}
			});
		}
	}

	private void createContentShell() {
		contentShell = new Shell(getShell(), SWT.NO_TRIM | SWT.ON_TOP);
		contentShell.addListener(SWT.Close, shellListener);
		contentShell.addListener(SWT.Deactivate, shellListener);
	}

	private void createText(int style) {
		textListener = new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.KeyDown:
					if (event.stateMask == SWT.CTRL && event.keyCode == ' ') {
						event.doit = false;
						setOpen(true);
					}
					break;
				case SWT.Modify:
					Event e = new Event();
					e.time = event.time;
					setModifyEventProperties(e);
					notifyListeners(SWT.Modify, e);
					break;
				}
			}
		};

		int mask = SWT.TRAIL | SWT.LEAD;
		int textStyle = SWT.SINGLE | (style & mask);
		if (!win32 && (style & SWT.BORDER) != 0) {
			textStyle |= SWT.BORDER;
		}

		text = VNative.create(Text.class, panel, textStyle);
		addTextListener();
	}

	/**
	 * @param image
	 */
	protected final void doSetButtonImage(Image image) {
		if (checkButton()) {
			button.setImage(image);
		}
	}

	/**
	 * @return the Control that was set as this popup shell's content with
	 *         setContent(Control)
	 */
	protected Control getContent() {
		return content;
	}

	/**
	 * @return the content shell
	 */
	protected Shell getContentShell() {
		if (contentShell == null) {
			createContentShell();
		}
		return contentShell;
	}

	/**
	 * Returns the editable state.
	 * 
	 * @return whether or not the receiver is editable
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public boolean getEditable() {
		return checkText() ? text.getControl().getEditable() : getEnabled();
	}

	/**
	 * Fixes bug 181442: [CDateTime] Incorrect getEnabled()
	 */
	public boolean getEnabled() {
		return checkText() ? text.getEnabled() : super.getEnabled();
	}

	/**
	 * @return the state of the holdOpen flag
	 */
	protected boolean getHoldOpen() {
		return holdOpen;
	}

	/**
	 * returns the menu for this combo
	 */
	public Menu getMenu() {
		if (checkText()) {
			return text.getMenu();
		}
		return super.getMenu();
	}

	/**
	 * @return the stretch control
	 */
	protected VControl getStretchControl() {
		return stretchControl;
	}

	public int getStyle() {
		return style;
	}

	/**
	 * Returns the text of this combo
	 * 
	 * @return the combo's text
	 */
	public String getText() {
		return checkText() ? text.getText() : ""; //$NON-NLS-1$
	}

	private void init(int style) {
		this.style = style;
		simple = (style & SWT.SIMPLE) != 0;
		dropDown = (style & (BUTTON_ONLY | SWT.DROP_DOWN)) != 0;
		leftAlign = (style & SWT.LEFT) != 0;

		if (simple) {
			panel.setLayout(new VSimpleLayout());
		} else if (dropDown) {
			createButton(style);
			if ((style & BUTTON_ONLY) == 0) {
				createText(style);
				if (win32) {
					setPositionControl(panel);
				} else {
					setPositionControl(text);
				}
				panel.setLayout(new DropComboLayout());
			} else {
				setPositionControl(button);
				panel.setLayout(new VSimpleLayout());
			}

			shellListener = new Listener() {
				public void handleEvent(Event event) {
					switch (event.type) {
					case SWT.Close:
						event.doit = false;
						setOpen(false);
						break;
					case SWT.Deactivate:
						if (!checkContent() || content.getMenu() == null
								|| !content.getMenu().isVisible()) {
							setOpen(false);
						}
						break;
					}
				}
			};

			addListener(SWT.Move, comboListener);
			addListener(SWT.Resize, comboListener);
		} else {
			panel.setLayout(new VSimpleLayout());
			createText(style);
		}

		addListener(SWT.Dispose, disposeListener);
	}

	/**
	 * @return true if style is CDT.DROP_DOWN
	 */
	protected boolean isDropDown() {
		return dropDown;
	}

	/**
	 * @return the state of the popup shell's visibility
	 */
	public boolean isOpen() {
		return open;
	}

	/**
	 * @return if Shell style is SWT.RIGHT_TO_LEFT
	 */
	protected boolean isRTL() {
		return (getShell().getStyle() & SWT.RIGHT_TO_LEFT) == SWT.RIGHT_TO_LEFT;
	}

	/**
	 * @return if style is CDT.SIMPLE
	 */
	protected boolean isSimple() {
		return simple;
	}

	/**
	 * called just <i>after</i> the content shell is set not-visible and has
	 * "closed"
	 * <p>
	 * override if you want to do something with the shell just after becoming
	 * not visible
	 * </p>
	 * 
	 * @param popup
	 * @see #preClose(Shell)
	 */
	protected void postClose(Shell popup) {
		// subclasses to implement if necessary
	}

	/**
	 * called <i>after</i> the content shell is set visible and has "opened"
	 * <p>
	 * override if you want to do something with the shell just after becoming
	 * visible
	 * </p>
	 * 
	 * @param popup
	 * @see #preOpen(Shell)
	 */
	protected void postOpen(Shell popup) {
		// subclasses to implement if necessary
	}

	/**
	 * called just <i>before</i> the content shell is set not-visible and
	 * "closes"
	 * <p>
	 * override if you want to do something with the shell prior to it becoming
	 * not visible
	 * </p>
	 * 
	 * @param popup
	 * @see #postClose(Shell)
	 */
	protected void preClose(Shell popup) {
		// subclasses to implement if necessary
	}

	/**
	 * called just <i>before</i> the content shell is set visible and "opens"
	 * <p>
	 * override if you want to do something with the shell prior to it becoming
	 * visible
	 * </p>
	 * 
	 * @param popup
	 * @see #postOpen(Shell)
	 */
	protected void preOpen(Shell popup) {
		// subclasses to implement if necessary
	}

	/**
	 * Removes the listener from the collection of listeners who will be
	 * notified when the receiver's text is modified.
	 * 
	 * @param listener
	 *            the listener which should no longer be notified
	 * 
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
	 * 
	 * @see ModifyListener
	 * @see #addModifyListener
	 */
	protected void removeModifyListener(ModifyListener listener) {
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		if (checkText()) {
			text.getControl().removeModifyListener(listener);
		}
	}

	private void removeTextListener() {
		text.getControl().removeListener(SWT.KeyDown, textListener);
		text.getControl().removeListener(SWT.Modify, textListener);
	}

	/**
	 * Set the alignment of the button in relation to the text box. Only valid
	 * if style is DROP_DOWN.
	 * 
	 * @param alignment
	 *            can be either SWT.LEFT or SWT.RIGHT. Other values have no
	 *            effect.
	 */
	protected void setButtonAlignment(int alignment) {
		if (SWT.LEFT == alignment) {
			leftAlign = true;
		} else if (SWT.RIGHT == alignment) {
			leftAlign = false;
		}
		layout(true);
	}

	/**
	 * Set the custom image for the drop down button. Only valid if style is
	 * DROP_DOWN. Passing null in will set the image to its default value.
	 * 
	 * @param image
	 */
	protected void setButtonImage(Image image) {
		doSetButtonImage(image);
	}

	/**
	 * Set the text for the drop down button. Only valid if style is DROP_DOWN.
	 * Passing null will clear the text.
	 * 
	 * @param text
	 */
	protected void setButtonText(String text) {
		if (checkButton()) {
			button.setText(text);
		}
	}

	/**
	 * Set the visibility style of the drop button.
	 * <p>
	 * The style will be forced to NEVER if the contents are null
	 * </p>
	 * <dl>
	 * <dt><b>Styles:</b></dt>
	 * <dd>BUTTON_ALWAYS, BUTTON_AUTO, BUTTON_MANUAL, BUTTON_NEVER</dd>
	 * </dl>
	 * <dl>
	 * <dt><b>Style BUTTON_ALWAYS:</b></dt>
	 * <dd>Button will always be shown - standard SWT.DROP_DOWN behaviour. The
	 * method setButtonVisible(boolean) has no affect with this style set</dd>
	 * <dt><b>Style BUTTON_AUTO:</b></dt>
	 * <dd>Button visibility will be handled automatically through focus events,
	 * popup events, as well as programmatically</dd>
	 * <dt><b>Style BUTTON_MANUAL:</b></dt>
	 * <dd>Button visibility will only be handled programmatically</dd>
	 * <dt><b>Style BUTTON_NEVER:</b></dt>
	 * <dd>Button will never be shown - standard SWT.SIMPLE behaviour. The
	 * method setButtonVisible(boolean) has no affect with this style set</dd>
	 * </dl>
	 * 
	 * @param visibility
	 *            the visibility style constant
	 * @see #setButtonVisible(boolean)
	 */
	protected void setButtonVisibility(int visibility) {
		buttonVisibility = visibility;
		setButtonVisible(false);
		if (buttonVisibility == BUTTON_AUTO) {
			buttonVisibilityListener = new Listener() {
				public void handleEvent(Event event) {
					switch (event.type) {
					case SWT.FocusIn:
						setButtonVisible(true);
						break;
					case SWT.FocusOut:
						setButtonVisible(false);
						break;
					}
				}
			};
			addListener(SWT.FocusIn, buttonVisibilityListener);
			addListener(SWT.FocusOut, buttonVisibilityListener);
		} else {
			if (buttonVisibilityListener != null) {
				removeListener(SWT.FocusIn, buttonVisibilityListener);
				removeListener(SWT.FocusOut, buttonVisibilityListener);
				buttonVisibilityListener = null;
			}
		}
	}

	/**
	 * Set the visible state of the button
	 * <p>
	 * Note: This method is only useful when the button's visibility style is
	 * either AUTO or MANUAL.
	 * </p>
	 * 
	 * @param visible
	 * @see #setButtonVisibility(int)
	 */
	protected void setButtonVisible(boolean visible) {

		// bug 352689
		if (!checkButton())
			return;

		if (BUTTON_ALWAYS == buttonVisibility) {
			visible = true;
		} else if (BUTTON_NEVER == buttonVisibility) {
			visible = false;
		}

		button.setVisible(visible);

		layout(true);
		update();
	}

	/**
	 * set the content of the popup shell
	 * <p>
	 * Can be a single control, or a Composite consisting of many controls
	 * </p>
	 * 
	 * @param content
	 */
	protected void setContent(Control content) {
		this.content = content;
		if (this.content != null) {
			if (!simple) {
				this.content.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
						true, true));
			}
		}
	}

	/**
	 * Called when the popup shell has been open, this method provides a
	 * location for subclasses to set the focus to the content.
	 * 
	 * @return true if the focus was set, false otherwise
	 */
	protected abstract boolean setContentFocus();

	/**
	 * Sets the editable state.
	 * 
	 * @param editable
	 *            the new editable state
	 */
	public void setEditable(boolean editable) {
		panel.setStyle(SWT.READ_ONLY, !editable);
		if (checkButton()) {
			button.setEnabled(editable);
		}
		if (checkText()) {
			if (editable != text.getControl().getEditable()) {
				text.getControl().setEditable(editable);
				if (editable) {
					addTextListener();
				} else {
					removeTextListener();
				}
			}
		}
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		panel.setEnabled(enabled);
	}

	public boolean setFocus() {
		return panel.setFocus();
	}

	public void setFont(Font font) {
		super.setFont(font);
		if (checkButton()) {
			button.setFont(font);
		}
		if (checkText()) {
			text.setFont(font);
		}
		if (checkContent()) {
			content.setFont(font);
		}
	}

	/**
	 * if holdOpen is true, the popup shell will not close regardless of events
	 * and/or calls to popUp(false) until holdOpen is first set false
	 * <p>
	 * merely sets the holdOpen flag, does not change popup visibility state
	 * </p>
	 * 
	 * @param holdOpen
	 */
	protected void setHoldOpen(boolean holdOpen) {
		this.holdOpen = holdOpen;
	}

	/**
	 * Sets the menu for the Text widget of this DropCombo
	 * <p>
	 * Note that setting the menu to null causes the native menu to be used
	 * </p>
	 * <p>
	 * If the intent is to disable the menu, then set it to a blank menu
	 * </p>
	 */
	public void setMenu(Menu menu) {
		if (checkText()) {
			text.getControl().setMenu(menu);
		} else {
			super.setMenu(menu);
		}
	}

	/**
	 * Provides a chance for subclasses to set the properties of the modify
	 * event called when the text is modified. Not valid if style is SIMPLE.
	 * <p>
	 * For example, CDateTime overrides this method to set the data field to the
	 * current time:<br/>
	 * <code style="margin-left:25px">e.data = calendar.getTime();<code>
	 * </p>
	 * 
	 * @param e
	 */
	protected void setModifyEventProperties(Event e) {
		// subclasses to implement
	}

	/**
	 * If pop is true, then opens the popup shell (sets to visible)<br>
	 * If pop is false, closes the popup shell (sets to not visible)<br>
	 * If <code>content == null</code> this method simply returns.<br>
	 * If <code>popup == null</code> then <code>popup</code> will be created.
	 * 
	 * @param open
	 *            true to open the popup shell, false to close it.
	 * @see BaseCombo#setOpen(boolean, Runnable)
	 */
	protected void setOpen(boolean open) {
		setOpen(open, null);
	}

	/**
	 * If pop is true, then opens the popup shell (sets to visible)<br>
	 * If pop is false, closes the popup shell (sets to not visible)<br>
	 * If <code>content == null</code> this method simply returns.<br>
	 * If <code>popup == null</code> then <code>popup</code> will be created.
	 * 
	 * @param open
	 *            true to open the popup shell, false to close it.
	 * @param callback
	 *            a runnable to be run when the operation completes.
	 * @see BaseCombo#setOpen(boolean)
	 */
	protected synchronized void setOpen(boolean open, final Runnable callback) {
		if (content == null || content.isDisposed()) {
			if (contentShell != null) {
				contentShell.dispose();
				contentShell = null;
			}
			this.open = false;
			return;
		}

		if (contentShell == null || contentShell.isDisposed()) {
			createContentShell();
		}

		if (getShell() != contentShell.getParent()) {
			content.setParent(this);
			contentShell.dispose();
			contentShell = null;
			createContentShell();
		}

		if (content.getParent() != contentShell) {
			content.setParent(contentShell);
		}

		if (!open) {
			if (!holdOpen) {
				this.open = false;

				preClose(contentShell);

				// Point location =
				// positionControl.getComposite().toDisplay(positionControl.getLocation());
				// Point contentLocation = contentShell.getLocation();
				// if(location.y > contentLocation.y) {
				// aStyle |= Animator.UP;
				// }

				Point start = contentShell.getSize();
				Point end = new Point(start.x, 0);
				Runnable runnable = new Runnable() {
					public void run() {
						postClose(contentShell);
						if (callback != null) {
							callback.run();
						}
					}
				};

				AnimationRunner runner = new AnimationRunner();
				runner.runEffect(new Resize(contentShell, start, end, 200,
						new LinearInOut(), runnable, runnable));

				if (checkText()) {
					text.setFocus();
				}
			}
		} else {
			this.open = true;

			Point size = content.computeSize(-1, -1);
			content.setSize(size);
			Point location = positionControl.getComposite().toDisplay(
					positionControl.getLocation());
			location.y += (positionControl.getSize().y + 2);
			int dHeight = getDisplay().getClientArea().height;
			if ((location.y + size.y) > dHeight) {
				location.y -= (positionControl.getSize().y + size.y + 4);
				// aStyle |= Animator.UP;
			}
			if ((stretchControl != null)
					&& (size.x < stretchControl.getSize().x)) {
				size.x = stretchControl.getSize().x;
				// contentShell.setSize(size);
			}

			if (leftAlign || isRTL()) {
				location.x -= positionControl.getLocation().x;
				// bug 336853
				if (isRTL()) {
					location.x -= getBounds().width;
				}

			} else {

				location.x += (positionControl.getSize().x - size.x);
				
				// <bug 373946> Edge detection
				Monitor monitor = positionControl.getControl().getMonitor();
				Rectangle monitorBounds = monitor.getBounds();
				location.x = Math.max(monitorBounds.x, location.x); 
				
			}
			if (win32) {
				location.x += 2;
			} else if (carbon) {
				location.y += 8;
			}

			contentShell.setBounds(location.x, location.y, size.x, 0);

			// chance for subclasses to do something before the shell becomes
			// visible
			preOpen(contentShell);

			Point start = new Point(size.x, 0);
			Point end = new Point(size.x, size.y);
			Runnable runnable = new Runnable() {
				public void run() {
					setContentFocus();
					postOpen(contentShell);
					if (callback != null) {
						callback.run();
					}
				}
			};

			contentShell.setVisible(true);
			AnimationRunner runner = new AnimationRunner();
			runner.runEffect(new Resize(contentShell, start, end, 200,
					new LinearInOut(), runnable, runnable));
			contentShell.setRedraw(true);
		}
		if (BUTTON_AUTO == buttonVisibility) {
			setButtonVisible(!open);
		}
	}

	/**
	 * sets the control to which the popup will align itself
	 * <p>
	 * the control does not necessarily need to be "this" or the button, but
	 * will default to "this" if positionControl == null
	 * </p>
	 * 
	 * @param positionControl
	 */
	protected void setPositionControl(VControl positionControl) {
		if (positionControl == null) {
			this.positionControl = panel;
		} else {
			this.positionControl = positionControl;
		}
	}

	/**
	 * If stretch is false, then the width of the popup will be set to its
	 * preferred width (via computeSize(SWT.DEFAULT, SWT.DEFAULT))
	 * <p>
	 * However, if stretchControl is true, the width of the popup will be
	 * stretched to equal the width of this control (if, however, popup's
	 * preferred width is greater than this control's width popup will not be
	 * shrunk down)
	 * </p>
	 * 
	 * @param stretch
	 */
	protected void setStretch(boolean stretch) {
		this.stretchControl = stretch ? panel : null;
	}

	/**
	 * Sets the tooltip on the text and button parts of this Composite widget.
	 * 
	 * @param tooltip
	 *            the new tooltip text
	 */
	public void setToolTipText(String tooltip) {
		text.setToolTipText(tooltip);
		button.setToolTipText(tooltip);
		super.setToolTipText(tooltip);
	}
}
