/****************************************************************************
* Copyright (c) 2005-2006 Jeremy Dowdall
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* 	 IBM Corporation - SWT's CCombo was relied upon _heavily_ for example and reference
*    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
*****************************************************************************/

package org.eclipse.nebula.widgets.cdatetime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

/**
 * The AbstractCombo is an abstract class which provides the basic functionality for a button with a 
 * DROP_DOWN, or "popup", shell component.  When the user selects the button the shell is set visible
 * and the SWT Components which have been placed on the "content" Composite will be shown.
 */
public abstract class AbstractCombo extends Composite {

	/**
	 * The value of {@link SWT#getVersion()} for the earliest known revision that fixes
	 * the SWT bug mentioned in bug 185739.
	 */
	protected static int SWT_MODAL_FIX_VERSION = 3346;

	/**
	 * Special layout implementation to position the combo's drop-down Button within
	 * its Text.
	 */
	protected class DropComboLayout extends Layout {
		protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
			Point size = text.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			if(button.getVisible()) {  // use "get" rather than "is" so it works even when not actually showing
				size.x += button.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
			}

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

			Point tSize = text.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			tSize.y += textMarginHeight;
			
			Point bSize;
			if(button.getVisible()) {  // use "get" rather than "is" so it works even when not actually showing
				bSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, flushCache);
				bSize.y = Math.min(bSize.y, Math.min(tSize.y, cRect.height));
				bSize.x = Math.min(bSize.y, cRect.width);
				if(cRect.width == bSize.x) {
					bSize.x = cRect.width / 2;
					// if text has a zero width, bad things happen :(
				}
			} else {
				bSize = new Point(0,0);
			}
			
			if(leftAlign) {
				text.setBounds(
						cRect.x+bSize.x,
						cRect.y + (win32 ? getBorderWidth() : 0),
						cRect.width-bSize.x,
						tSize.y
						);
				button.setBounds(
						cRect.x,
						cRect.y+((tSize.y-bSize.y) / 2),
						bSize.x,
						bSize.y
						);
			} else {
				text.setBounds(
						cRect.x,
						cRect.y + (win32 ? getBorderWidth() : 0),
						cRect.width-bSize.x,
						tSize.y
						);
				button.setBounds(
						cRect.x+cRect.width-bSize.x,
						cRect.y+((tSize.y-bSize.y) / 2),
						bSize.x,
						bSize.y
						);
			}
		}
	}

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
	 * A constant value used to pad the computed height for this widget, so that the
	 * combo's Button will fit without clipping its top and bottom borders.
	 */
	protected static final int textMarginHeight = win32 ? 4 : 0;
	
	/**
	 * The Button widget of a DROP_DOWN style combo.  This value may be null -- protect
	 * all references to this field with the checkButton() method. 
	 */
	protected Button button = null;
	
	/**
	 * The Text widget of a DROP_DOWN style combo.  This value may be null -- protect
	 * all references to this field with the checkText() method.
	 */
	protected Text text = null;
	
	/**
	 * The popup Shell widget of a DROP_DOWN style combo.  This value may be null --
	 * protect all references to this field with the checkContentShell() method.
	 */
	protected Shell contentShell = null;
	
	/**
	 * The widget contents of the popup Shell in a DROP_DOWN combo or the full contents
	 * of a SIMPLE combo.  This value may be null -- protect all references to this field
	 * with the checkContent() method.
	 */
	protected Control content;
	
	/**
	 * The style bits requested.  NOTE: this may not match the value returned by
	 * {@link #getStyle()} if invalid bits were requested.
	 */
	protected int style;
	
	/**
	 * Flag to indicate that this is a SIMPLE style combo.
	 */
	protected boolean simple;
	
	/**
	 * Flag to indicate that this combo's BUTTON should be displayed on the left side of
	 * its Text.
	 */
	protected boolean leftAlign = false;
	
	private int buttonVisibility;
	private boolean dropDown;

	private boolean dontOpen = false;
	private boolean open = false;
	private boolean holdOpen = false;
	protected boolean hasFocus;
	
	private Control positionControl;
	private Control stretchControl;
	
	Listener listener, filter;

	/**
	 * Main constructor -- must be called by all subclasses in their own constructors.
	 * 
	 * @param parent the visual parent of this widget
	 * @param style the requested SWT style bitmask for this widget
	 */
	public AbstractCombo(Composite parent, int style) {
		super(parent, swtStyle(style));
		setBackgroundMode(SWT.INHERIT_DEFAULT);
		
		this.style = style;
		simple = (style & CDT.SIMPLE) != 0;
		dropDown = (style & CDT.DROP_DOWN) != 0;

		if(simple) {
			setLayout(new FillLayout());
			
			setPositionControl(this);

			listener = new Listener () {
				public void handleEvent (Event event) {
					if(AbstractCombo.this == event.widget) {
						baseEvents(event);
						return;
					}
					if(AbstractCombo.this.getShell () == event.widget) {
						handleFocus(SWT.FocusOut, event.widget);
					}
					for(Iterator i = getBaseControls().iterator(); i.hasNext(); ) {
						Control control = (Control) i.next();
						if(control == event.widget) {
							baseControlEvents(event);
							return;
						}
					}
				}
			};
			
			filter = new Listener() {
				public void handleEvent(Event event) {
					Shell shell = ((Control)event.widget).getShell();
					if(shell == AbstractCombo.this.getShell()) {
						handleFocus(SWT.FocusOut, event.widget);
					}
				}
			};
	
			int[] baseEvents = { SWT.Dispose, SWT.Move, SWT.Resize };
			for(int i = 0; i < baseEvents.length; i++) this.addListener (baseEvents[i], listener);
		} else {
			// TODO: if not DROP_DOWN, don't create the button!
			setLayout(new DropComboLayout());
	
			if(win32) setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
			
			leftAlign = ((style & CDT.BUTTON_LEFT) != 0);

			int textStyle = SWT.SINGLE;
			if(!win32) textStyle |= ((CDT.BORDER & style) != 0) ? SWT.BORDER : 0;
			if((style & CDT.TEXT_RIGHT) != 0) textStyle |= SWT.RIGHT_TO_LEFT;
			else if((style & CDT.TEXT_LEFT) != 0) textStyle |= SWT.LEFT_TO_RIGHT;

			boolean flat = ((style & SWT.FLAT) != 0);
			button = new Button(this, SWT.ARROW | SWT.DOWN | (flat? SWT.FLAT: SWT.NONE));
			text = new Text(this, textStyle);

			setPositionControl(button);
	
			listener = new Listener () {
				public void handleEvent (Event event) {
					if(AbstractCombo.this == event.widget) {
						baseEvents(event);
						return;
					}
					if(AbstractCombo.this.getShell () == event.widget) {
						handleFocus(SWT.FocusOut, event.widget);
					}
					if(contentShell == event.widget) {
						contentShellEvents(event);
						return;
					}
					for(Iterator i = getBaseControls().iterator(); i.hasNext(); ) {
						Control control = (Control) i.next();
						if(control == event.widget) {
							baseControlEvents(event);
							return;
						}
					}
					for(Iterator i = getContentControls().iterator(); i.hasNext(); ) {
						Control control = (Control) i.next();
						if(control == event.widget) {
							contentControlEvents(event);
							return;
						}
					}
				}
			};
			
			filter = new Listener() {
				public void handleEvent(Event event) {
					Shell shell = ((Control)event.widget).getShell();
					if(shell == AbstractCombo.this.getShell()) {
						handleFocus(SWT.FocusOut, event.widget);
					}
				}
			};
	
			int[] baseEvents = { SWT.Dispose, SWT.Move, SWT.Resize };
			for(int i = 0; i < baseEvents.length; i++) this.addListener (baseEvents[i], listener);
			
			int [] buttonEvents = {SWT.MouseDown, SWT.FocusIn};
			for (int i=0; i<buttonEvents.length; i++) button.addListener (buttonEvents [i], listener);
	
			int[] textEvents = { SWT.FocusIn, SWT.KeyDown, SWT.Modify };
			for(int i = 0; i < textEvents.length; i++) text.addListener(textEvents[i], listener);
		}
	}
	
	private static int swtStyle(int style) {
		if(win32 && ((style & CDT.BORDER) != 0)) return SWT.BORDER;
		return SWT.NONE;
	}

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when the receiver's text is modified, by sending
	 * it one of the messages defined in the <code>ModifyListener</code>
	 * interface.<br/>  Note that this is NOT the correct way to listen
	 * for changes in the underlying model for the combo.  This should
	 * be provided by some other mechanism, such as a 
	 * {@link SelectionListener}.
	 * 
	 * @param listener the listener which should be notified
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 * @see ModifyListener
	 * @see #removeModifyListener
	 */
	public void addModifyListener(ModifyListener listener) {
		checkWidget();
		if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
		if(checkText()) text.addModifyListener(listener);
	}

	public void addTraverseListener(TraverseListener listener) {
		checkWidget();
		if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
		if(checkText()) text.addTraverseListener(listener);
	}
	
	/**
	 * @return true iff the {@link #button} field is in a fit state to be used
	 */
	protected boolean checkButton() {
		return (button != null && !button.isDisposed());
	}

	/**
	 * @return true iff the {@link #text} field is in a fit state to be used
	 */
	protected boolean checkText() {
		return (text != null && !text.isDisposed());
	}

	/**
	 * @return true iff the {@link #contentShell} field is in a fit state to be used
	 */
	protected boolean checkContentShell() {
		return (contentShell != null && !contentShell.isDisposed());
	}

	/**
	 * @return true iff the {@link #content} field is in a fit state to be used
	 */
	protected boolean checkContent() {
		return (content != null && !content.isDisposed());
	}
	
	/**
	 * Event handler for the content Composite and its children.<br>
	 * TODO: re-evaluate
	 * @param event the original triggering event
	 */
	protected void contentControlEvents(Event event) {
			switch (event.type) {
			case SWT.FocusIn:
				handleFocus(event.type, event.widget);
				break;
			}
	}
	
	/**
	 * Event handler for the text and button controls.<br>
	 * TODO: re-evaluate
	 * @param event the original triggering event
	 */
	protected void baseControlEvents(Event event) {
		if(text == event.widget) {
			switch (event.type) {
			case SWT.FocusIn:
				handleFocus(event.type, event.widget);
				break;
			case SWT.KeyDown:
				if(event.stateMask == SWT.CTRL && event.keyCode == ' ')
					setOpen(true);
				break;
			case SWT.Modify:
				Event e = new Event();
				e.time = event.time;
				setModifyEventProperties(e);
				notifyListeners (SWT.Modify, e);
				break;
			}
		} else {
			if(button == event.widget) {
				if(gtk && (SWT.FocusIn == event.type)) {
					setFocus();
					return;
				}
				switch (event.type) {
				case SWT.FocusIn:
					handleFocus(event.type, event.widget);
					break;
				case SWT.MouseDown:
					if (!checkButton() || !text.getEditable()) {
						return;
					}
					if(!win32) {
						if(!dontOpen) {
							setOpen(!isOpen());
						}
						dontOpen = false;
					} else {
						setOpen(!isOpen());
					}
					break;
				}
			}
		}
	}

	private void createContentShell() {
		int pstyle = getParent().getShell().getStyle();
		GridLayout layout = new GridLayout();
		
		// Some versions of SWT before 3.3 had a bug where child Shells of modal shells
		// would not receive events.  This fix creates the popup with modal style, so
		// that the popup always receives events, but the parent does not.  This is
		// not ideal, so it is only applied if the SWT version is less than one that
		// is known to be good.
		// TODO: find the precise version at which the SWT bug was fixed
		if(SWT.getVersion() < SWT_MODAL_FIX_VERSION &&
				((pstyle & SWT.APPLICATION_MODAL) != 0) ||
				((pstyle & SWT.SYSTEM_MODAL) != 0)) {
			// TODO: find a fix other than setting popup's style to SWT.APPLICATION_MODAL
			contentShell = new Shell(getShell(), SWT.APPLICATION_MODAL);
			layout.marginWidth = 0;
			layout.marginHeight = 0;
		} else {
			contentShell = new Shell(getShell(), SWT.NO_TRIM | SWT.ON_TOP);
			layout.marginWidth = 1;
			layout.marginHeight = 1;
		}
		contentShell.setLayout(layout);
		
		int [] popupEvents = {SWT.Close, SWT.Deactivate};
		for (int i = 0; i < popupEvents.length; i++) contentShell.addListener (popupEvents[i], listener);
	}

	/**
	 * Event handler for this widget's main Composite.<br>
	 * TODO: re-evaluate
	 * @param event the original triggering event
	 */
	protected void baseEvents(Event event) {
		switch (event.type) {
		case SWT.Dispose:
			// remember that contentShell will be null if this isSimple
			if (checkContentShell()) {
				contentShell.dispose();
			}
			getShell().removeListener (SWT.Deactivate, listener);
			getDisplay().removeFilter(SWT.FocusIn, filter);
			break;
		case SWT.Move:
			if(isOpen()) setOpen(false);
			break;
		case SWT.Resize:
			if(isOpen()) setOpen(false);
			layout(true);
			break;
		}
	}

	/**
	 * @return the Control that was set as this popup shell's content
	 * with setContent(Control)
	 */
	protected Control getContent() {
//		checkWidget();
		return content;
	}
	
	/**
	 * @return a List<Control> of the children of the content, or if there are none,
	 * a List containing the content Control itself 
	 */
	protected List getContentControls() {
		if(content instanceof Composite) {
			return getControls((Composite) content);
		} else {
			return Collections.singletonList(content);
		}
	}
	
	private List getBaseControls() {
		return getControls(this);
	}

	/**
	 * A recursive utility function used to find every child control of a composite,
	 * including the children of children.<br/>
	 * NOTE: This method will <b>NOT</b> return disposed children.
	 * @param c the composite to start from
	 * @return all the children and grandchildren of the given composite
	 */
	protected static List getControls(Composite c) {
		if(c != null && !c.isDisposed()) {
			List l = new ArrayList();
			l.add(c);
			Control[] a = c.getChildren();
			for(int i = 0; i < a.length; i++) {
				if(!a[i].isDisposed()) {
					if(a[i] instanceof Composite) {
						l.addAll(getControls((Composite) a[i]));
					} else {
						l.add(a[i]);
					}
				}
			}
			return l;
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	/**
	 * Returns the editable state.
	 *
	 * @return whether or not the receiver is editable
	 * 
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public boolean getEditable() {
		return checkText()? text.getEditable(): getEnabled();
	}

	/**
	 * Fixes bug 181442: [CDateTime] Incorrect getEnabled()
	 */
	public boolean getEnabled() {
		return checkText()? text.getEnabled(): super.getEnabled();
	}

	/**
	 * returns the menu for this combo
	 */
	public Menu getMenu() {
		checkWidget();
		if(checkText()) return text.getMenu();
		return getMenu();
	}

	protected Composite getParentForContent() {
		if(isSimple()) return this;
		if(isDropDown()) return getContentShell();
		return null; // not supposed to have content unless either simple or drop_down...
	}

	protected Shell getContentShell() {
		checkWidget();
		if(contentShell == null) createContentShell();
		return contentShell;
	}

	protected Control getStretchControl() {
		return stretchControl;
	}

	public int getStyle() {
		checkWidget();
		return style;
	}
	
	/**
	 * Returns the text of this combo
	 * @return
	 */
	public String getText() {
		checkWidget();
		return checkText() ? text.getText() : ""; //$NON-NLS-1$
	}
	
	/**
	 * The Focus event handler.  Subclasses may override but should always the super.
	 */
	protected void handleFocus(int type, Widget widget) {
		if(isDisposed()) return;
		switch (type) {
			case SWT.FocusIn: {
				if(hasFocus) return;
				hasFocus = true;
				Shell shell = getShell();
				shell.removeListener(SWT.Deactivate, listener);
				shell.addListener(SWT.Deactivate, listener);
				Display display = getDisplay();
				display.removeFilter(SWT.FocusIn, filter);
				display.addFilter(SWT.FocusIn, filter);
				Event event = new Event();
				notifyListeners(SWT.FocusIn, event);
				break;
			}
			case SWT.FocusOut: {
				if(!hasFocus) return;
				Control focusControl = getDisplay().getFocusControl();
				for(Iterator i = getBaseControls().iterator(); i.hasNext(); ) {
					Control control = (Control) i.next();
					if(control == focusControl) return;
				}
				for(Iterator i = getContentControls().iterator(); i.hasNext(); ) {
					Control control = (Control) i.next();
					if(control == focusControl) return;
				}
				hasFocus = false;
				Shell shell = getShell();
				shell.removeListener(SWT.Deactivate, listener);
				Display display = getDisplay();
				display.removeFilter(SWT.FocusIn, filter);
				Event event = new Event ();
				notifyListeners (SWT.FocusOut, event);
				break;
			}
		}
		if(buttonVisibility == CDT.BUTTON_AUTO) {
			setButtonVisible(hasFocus && !isOpen());
		}
	}

	protected boolean isDropDown() {
		return dropDown;
	}

	/**
	 * returns the state of the popup shell's visibility
	 */
	public boolean isOpen() {
		checkWidget();
		return open;
	}

	/**
	 * returns the state of the holdOpen flag
	 */
	protected boolean getHoldOpen() {
		checkWidget();
		return holdOpen;
	}
	
	protected boolean isSimple() {
		return simple;
	}
	
	/**
	 * If pop is true, then opens the popup shell (sets to visible)<br>
	 * If pop is false, closes the popup shell (sets to not visible)<br>
	 * If <code>content == null</code> this method simply returns.<br>
	 * If <code>popup == null</code> then <code>popup</code> will be created.
	 * @param open
	 */
	public void setOpen(boolean open) {
		checkWidget();
		if(content == null) return;
		if(contentShell == null) createContentShell();
		
		if(getShell() != contentShell.getParent()) {
			contentShell.dispose();
			contentShell = null;
			createContentShell();
		}
		
		if(!open) {
			if(!holdOpen) {
				contentShell.setVisible(false);
				this.open = false;
				if(checkText()) text.setFocus();
			}
		} else {
			contentShell.pack(true);
			Point size = content.computeSize(-1, -1);
			Point location = positionControl.toDisplay(positionControl.getLocation());
			location.y -= positionControl.getLocation().y - positionControl.getSize().y + 1;
			int dHeight = getDisplay().getClientArea().height;
			if((location.y + size.y) > dHeight) {
				location.y = positionControl.toDisplay(positionControl.getLocation()).y - size.y - 4;
			}
			if((stretchControl != null) && (size.x < stretchControl.getSize().x)) {
				size.x = stretchControl.getSize().x;
				contentShell.setSize(size);
			}
			
			if(leftAlign) {
				location.x -= positionControl.getLocation().x;
			} else {
				location.x -= positionControl.getLocation().x + size.x - positionControl.getSize().x;
				if(location.x < 0) location.x = 0;
			}
			if(win32) {
				location.x -= 2;
			} else if(carbon) {
				location.y += 8;
			}
			contentShell.setLocation(location);

			// chance for subclasses to do something before the shell becomes visible
			aboutToOpen(contentShell);
			
			contentShell.setVisible(true);
			content.setFocus();
			this.open = true;
		}
		if(buttonVisibility == CDT.BUTTON_AUTO) {
			setButtonVisible(hasFocus && !open);
		}
	}

	/**
	 * called just before the content shell is set visible and "opens"
	 * <p>override if you want to do something with the shell prior to becoming visible</p>
	 */
	protected void aboutToOpen(Shell popup) {}
	
	protected void contentShellEvents(Event event) {
		switch (event.type) {
		case SWT.Close:
			event.doit = false;
			setOpen(false);
			break;
		case SWT.Deactivate:
			// check if deactivation happens due to activation of a component's menu
			boolean menuActive = false;
			for(Iterator i = getContentControls().iterator(); i.hasNext(); ) {
				Control control = (Control) i.next();
				if(control.getMenu() != null && control.getMenu().isVisible()) {
					menuActive = true;
					break;
				}
			}
			if(menuActive) break;
			
			// when the popup shell is deactivated by clicking the button,
			// we receive two Deactivate events on Win32, whereas on Carbon and GTK
			// we first receive one Deactivation event from the shell and
			// then a Selection event from the button.
			// as a work-around, set a flag (dontOpen) if running Carbon or GTK
			if(!win32) {
				Point loc = button.toControl(getDisplay().getCursorLocation());
				Point size = button.getSize();
				if((loc.x >= 0) && (loc.y >= 0) && (loc.x < size.x) && (loc.y < size.y)) {
					dontOpen = true;
				}
			}
			setOpen(false);
			break;
//		case SWT.Paint:
//			event.gc.setForeground(event.display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
//			event.gc.drawRectangle(event.x,event.y, event.width-1, event.height-1);
//			break;
		}
	}
	
	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when the receiver's text is modified.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 * @see ModifyListener
	 * @see #addModifyListener
	 */
	public void removeModifyListener (ModifyListener listener) {
		checkWidget();
		if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
		if(checkText()) text.removeModifyListener(listener);
	}

	public void removeTraverseListener(TraverseListener listener) {
		checkWidget();
		if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
		if(checkText()) text.removeTraverseListener(listener);
	}
	
	/**
	 * Set the visibility style of the drop button.
	 * <p>The style will be forced to NEVER if the contents are null</p>
	 * <dl>
	 * <dt><b>Styles:</b></dt>
	 * <dd>ALWAYS, AUTO, MANUAL, NEVER</dd>
	 * </dl>
	 * <dl>
	 * <dt><b>Style ALWAYS:</b></dt>
	 * <dd>Button will always be shown - standard SWT.DROP_DOWN behaviour.  The method
	 * setButtonVisible(boolean) has no affect with this style set</dd>
	 * <dt><b>Style AUTO:</b></dt>
	 * <dd>Button visibility will be handled automatically through focus events, popup events,
	 * as well as programmatically</dd>
	 * <dt><b>Style MANUAL:</b></dt>
	 * <dd>Button visibility will only be handled programmatically</dd>
	 * <dt><b>Style NEVER:</b></dt>
	 * <dd>Button will never be shown - standard SWT.SIMPLE behaviour.  The method
	 * setButtonVisible(boolean) has no affect with this style set</dd>
	 * </dl>
	 * @param visibility the visibility style constant
	 * @see #setButtonVisible(boolean)
	 */
	public void setButtonVisibility(int visibility) {
		checkWidget();
		if(content == null) visibility = CDT.BUTTON_NEVER;
		buttonVisibility = visibility;
		setButtonVisible(false);
	}

	/**
	 * Set the visible state of the button
	 * <p>Note: This method is only useful when the button's visibility style is 
	 * either AUTO or MANUAL.</p>
	 * @param visible
	 * @see #setButtonVisibility(int)
	 */
	public void setButtonVisible(boolean visible) {
		switch (buttonVisibility) {
		case CDT.BUTTON_ALWAYS:
			visible = true;
			break;
		case CDT.BUTTON_NEVER:
			visible = false;
			break;
		}
		
		if(checkButton()) button.setVisible(visible);

		layout(true);
		update();
	}

	/**
	 * set the content of the popup shell
	 * <p>Can be a single control, or a Composite consisting of many controls</p>
	 * @param popContents
	 */
	protected void setContent(Control contents) {
		checkWidget();
		if(checkContent()) {
			int[] contentsEvents = { SWT.FocusIn };
			for(int i = 0; i < contentsEvents.length; i++) this.content.removeListener(contentsEvents[i], listener);
		}
		this.content = contents;

		int[] contentsEvents = { SWT.FocusIn };
		for(int i = 0; i < contentsEvents.length; i++) {
			for(Iterator iter = getContentControls().iterator(); iter.hasNext(); ) {
				((Control) iter.next()).addListener(contentsEvents[i], listener);
			}
		}
	}

	public void setEditable(boolean editable) {
		checkWidget();
		if(checkText()) text.setEditable(editable);
		if(checkButton()) button.setEnabled(editable);
		
		if (editable) {
			setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));	
		}
		else {
			setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		}
	}
	
	public void setEnabled(boolean enabled) {
		checkWidget();
		if(checkButton()) button.setEnabled(enabled);
		if(checkText()) text.setEnabled(enabled);
		if(checkContent()) content.setEnabled(enabled);
		super.setEnabled(enabled);
	}
	
	public boolean setFocus() {
		if(simple) {
			if(checkContent()) return content.setFocus();
			return super.setFocus();
		} else {
			if(checkText()) return text.setFocus();
			return super.setFocus();
		}
	}

	public void setFont(Font font) {
		super.setFont(font);
		if(checkButton()) button.setFont(font);
		if(checkText()) text.setFont(font);
		if(checkContent()) content.setFont(font);
	}
	
	/**
	 * if holdOpen is true, the popup shell will not close regardless of events and/or calls
	 * to popUp(false) until holdOpen is first set false
	 * <p>merely sets the holdOpen flag, does not change popup visibility state</p>
	 * @param holdOpen
	 */
	public void setHoldOpen(boolean holdOpen) {
		checkWidget();
		this.holdOpen = holdOpen;
	}
	
	/**
	 * Sets the menu for the Text widget of this DropCombo
	 * <p>Note that setting the menu to null causes the native menu to be used</p>
	 * <p>If the intent is to disable the menu, then set it to a blank menu</p>
	 */
	public void setMenu(Menu menu) {
		checkWidget();
		if(checkText()) text.setMenu(menu);
		else setMenu(menu);
	}

	protected void setModifyEventProperties(Event e) {	}

	/**
	 * sets the control to which the popup will align itself
	 * <p>the control does not necessarily need to be "this" or the button,
	 * but will default to "this" if positionControl == null</p>
	 * @param positionControl
	 */
	protected void setPositionControl(Control positionControl) {
		checkWidget();
		if(positionControl == null) {
			this.positionControl = this;
		} else {
			this.positionControl = positionControl;
		}
	}

	/**
	 * If stretch is false, then the width of the popup will be
	 * set to its preferred width (via computeSize(SWT.DEFAULT, SWT.DEFAULT))
	 * <p>However, if stretchControl is true, the width of the popup will be
	 * stretched to equal the width of this control (if, however,
	 * popup's preferred width is greater than this control's width
	 * popup will not be shrunk down)</p>
	 * @param stretch
	 */
	public void setStretch(boolean stretch) {
		checkWidget();
		this.stretchControl = stretch ? this : null;
	}
	
	/**
	 * Sets the tooltip on the text and button parts of this Composite widget.
	 * 
	 * @param tooltip the new tooltip text
	 */
	public void setToolTipText(String tooltip) {
		checkWidget();
		if (checkText())
			text.setToolTipText(tooltip);
		if (checkButton())
			button.setToolTipText(tooltip);
	}
	
	public void setBackground(Color bgcolor) {
		checkWidget();
		super.setBackground(bgcolor);
		if (checkText())
			text.setBackground(bgcolor);
		if (checkButton())
			button.setBackground(bgcolor);
	}
	
	public void setForeground(Color fgcolor) {
		checkWidget();
		super.setForeground(fgcolor);
		if (checkText())
			text.setForeground(fgcolor);
		if (checkButton())
			button.setForeground(fgcolor);
	}
}
