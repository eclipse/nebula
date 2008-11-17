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

package org.eclipse.nebula.widgets.collapsiblebuttons;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;

/**
 * <b>CollapsibleButtonsWidget - SWT/JFace Widget - 2005-2007. Version 1.0.
 * &copy; Emil Crumhorn - emil.crumhorn@gmail.com.</b>
 * <p>
 * <b>Website</b><br>
 * If you want more info or more documentation, please visit: <a
 * href="http://www.hexapixel.com/">http://www.hexapixel.com</a>
 * <p>
 * <b>Description</b><br>
 * ButtonComposite is a Widget that displays a vertical row of buttons similar
 * to the way that Microsoft&reg; shows buttons in the bottom left of the
 * Microsoft Outlook 2005, and 2007 editions. The button bar is a collapsible
 * bar that contains an image, text, and an associated toolbar at the very
 * bottom where buttons that are currently "collapsed" are shown. There is also
 * a menu which is activated by clicking a small arrow icon on the toolbar that
 * will allow you to do actions that are similar to the actions you can do with
 * the mouse.
 * <p>
 * The bar is resized by dragging the handle-bar which is at the very top. When
 * the mouse is clicked on the handlebar and dragged either up or down, buttons
 * will be shown and hidden accordingly. The button bar also has a feature where
 * - if there's no space to show the actively shown buttons due to progam window
 * size, buttons will automatically collapse to conserve space, and then
 * automatically expand back to the original number of visible buttons when the
 * program window is returned to a size where they can be shown.
 * <p>
 * <b>Where to put it</b><br>
 * It is important to point out that due to the nature of the ButtonComposite,
 * it is important to put it inside a layout that will allow it to
 * expand/collapse with the widgets that are around it - as whenever a button is
 * shown/hidden, the actual physical size of the widget changes, which should
 * cause surrounding widgets to take up either more or less space. I personally
 * recommend putting the ButtonBar inside either a ViewForm, SashForm.
 * <p>
 * If you still wish to put it on a plain composite, I suggest doing it the
 * following way:
 * <p>
 * <code>
 * // outer layer wrapper<br>
 * Composite bcWrapper = new Composite(parentComposite, SWT.None);<br>
 * GridLayout gl = new GridLayout(1, true);<br>
 * gl.marginBottom = 0;<br>
 * gl.marginHeight = 0;<br>
 * gl.marginWidth = 0;<br>
 * gl.marginHeight = 0;<br>
 * inner.setLayout(gl);<br>
 * <br>
 * CollapsibleButtons cButtons = new CollapsibleButtons(bcWrapper, SWT.NONE);<br>
 * // will ensure the composite takes up the appropriate amount of space and gets aligned correctly, we align it at the end as that is where it should live<br>    
 * cButtons.setLayoutData(new GridData(GridData.GRAB_VERTICAL | GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_END));<br>
 * cButtons.addButton(...);<br>
 * </code>
 * <p>
 * <b>Customizing</b><br>
 * As many people wish to customize the widget beyond the capabilities it
 * already has, there are a few ways you may basically take control over as much
 * or little as you please. First, there are three interfaces that are of
 * importance (apart from the IButtonListener), one is the IButtonPainter, the
 * IColorManager and the ISettings. Let's start with the IColorManager.
 * <p>
 * <b>IColorManager</b><br>
 * If you don't specify a color manager, the DefaultColorManager will be used.
 * The color manager's job is to return colors to the method that is painting
 * the button when the button is drawn, when you move over a button, when you
 * select a button, and when you hover over a selected button. The colors that
 * are returned from the ColorManager will determine everything as far as looks
 * go.
 * <p>
 * <b>IButtonPainter</b><br>
 * Then there's the IButtonPainter. The IButtonPainter lets you take over 3
 * methods, which are how A. The background colors of the button are painted. B.
 * How the text is drawn, and C. How the image is painted. As this is basically
 * 100% of how a button is drawn, you can control every aspect of the button
 * look and feel. By default, if no IButtonPainter is assigned, the
 * DefaultButtonPainter will be used. The IButtonPainter's paintBackground(...)
 * method is also used to draw all aspects of the tool bar. That way, the
 * toolbar automatically gets the same colors etc like the normal buttons.
 * <p>
 * <b>ISettings</b><br>
 * To control a lot of the features, such as, whether to show the toolbar or
 * not, whether you want certain size buttons, painted borders, and so on, you
 * can create a class that implements ISettings and set it as the Settings
 * manager when you create a ButtonComposite. If you don't specify one,
 * DefaultSettings will be used.
 * <p>
 * Each button is esentially a composite inside a custom layout, and the toolbar
 * is a composite by itself.
 * <p>
 * If a toolbar is not wanted, you may control its visibility by implementing
 * ISettings
 * <p>
 * <b>Double Buffering</b><br>
 * It is also important to note that the widget uses custom double buffering as
 * neither Windows XP's or SWT's DOUBLE_BUFFER flag do the job well enough. To
 * do a proper double buffer, the widget is first drawn into a cached Graphics
 * object that is then copied onto the actual graphics object. This reduces
 * flickering and other odd widget behavior. If you intend to run the widget on
 * either Linux or Macintosh (which both know how to double buffer in the OS),
 * you may turn it off.
 * 
 * @author Emil Crumhorn
 * @version 1.0
 * 
 */
public class CollapsibleButtons extends Composite implements MouseListener, MouseMoveListener, MouseTrackListener {

	// cursors we'll use, hand is for mouse-button-overs, size-tool for resize
	// bar
	private static final Cursor	CURSOR_SIZENS			= CursorCache.getCursor(SWT.CURSOR_SIZENS);
	private static final Cursor	CURSOR_HAND				= CursorCache.getCursor(SWT.CURSOR_HAND);

	private int					mResizeBarSize;

	private Rectangle			mBounds;
	private Rectangle			mMoveBar;

	private List				mButtons;

	private int					mButtonHeight;

	private CustomButton		mSelectedButton;

	private boolean				mMouseIsDown;
	private int					mStartY					= 0;

	private int					mHiddenButtons			= 0;

	private ToolbarComposite	mToolBarComposite;

	// beats the built-in double buffering via SWT.DOUBLE_BUFFER
	private boolean				mEnableDoubleBuffering	= true;
	private boolean				mCreated;

	private int					mInvoluntaryButtonLevel	= -1;

	private List				mHidden;
	private Composite			mParent;

	private int					mColorTheme				= IColorManager.SKIN_AUTO_DETECT;

	private IColorManager		mColorManager;
	private List				mButtonListeners;

	private ISettings			mSettings;
	private ILanguageSettings	mLanguage;

	private List				mMenuListeners;

	/**
	 * Creates a new ButtonComposite. Add buttons using the addButton(...)
	 * method call.
	 * 
	 * @param parent Parent composite
	 * @param style Composite style, SWT.NO_BACKGROUND will be appended to the
	 *            style.
	 */
	public CollapsibleButtons(Composite parent, int style) {
		super(parent, checkStyle(style));
		this.mParent = parent;
		init();
	}

	/**
	 * Creates a new ButtonComposite with a given language manager.
	 *  
	 * @param parent Parent composite
	 * @param style style
	 * @param language Language manager
	 */
	public CollapsibleButtons(Composite parent, int style, ILanguageSettings language) {
		this(parent, style, IColorManager.SKIN_AUTO_DETECT, null, null, language);
	}
	
	/**
	 * Creates a new ButtonComposite with a given settings and language manager.
	 * 
	 * @param parent Parent composite 
	 * @param style style
	 * @param settings Settings manager
	 * @param language Language manager
	 */
	public CollapsibleButtons(Composite parent, int style, ISettings settings, ILanguageSettings language) {
		this(parent, style, IColorManager.SKIN_AUTO_DETECT, null, settings, language);
	}

	/**
	 * Creates a new ButtonComposite. Add buttons using the addButton(...)
	 * method call.
	 * 
	 * By default, unless you set a theme, the theme will be read from whatever
	 * the active color scheme is in Windows XP. If you are using a custom theme
	 * and the color scheme cannot be determined, the fall-back will be the
	 * Windows XP Blue theme.
	 * 
	 * NOTE: If you want the Office 2007 theme, you have to set it manually as
	 * there is no way to guess if you have office 2007 installed or 2005.
	 * 
	 * @param parent Parent composite
	 * @param style Composite style, SWT.NO_BACKGROUND will be appended to the
	 *            style
	 * @param theme IColorManager.STYLE_
	 */
	public CollapsibleButtons(Composite parent, int style, int theme) {
		super(parent, checkStyle(style));
		this.mColorTheme = theme;
		this.mParent = parent;
		init();
	}

	/**
	 * Creates a new ButtonComposite. Add buttons using the addButton(...)
	 * method call.
	 * 
	 * @param parent Parent composite
	 * @param style Composite style, SWT.NO_BACKGROUND will be appended to the
	 *            style
	 * @param colorManager IColorManager implementation. Set to null to use the
	 *            default
	 */
	public CollapsibleButtons(Composite parent, int style, IColorManager colorManager) {
		super(parent, checkStyle(style));
		this.mParent = parent;
		init();
	}

	/**
	 * Creates a new ButtonComposite. Add buttons using the addButton(...)
	 * method call.
	 * 
	 * By default, unless you set a theme, the theme will be read from whatever
	 * the active color scheme is in Windows XP. If you are using a custom theme
	 * and the color scheme cannot be determined, the fall-back will be the
	 * Windows XP Blue theme.
	 * 
	 * NOTE: If you want the Office 2007 theme, you have to set it manually as
	 * there is no way to guess if you have office 2007 installed or 2005.
	 * 
	 * @param parent Parent composite
	 * @param style Composite style, SWT.NO_BACKGROUND will be appended to the
	 *            style
	 * @param theme IColorManager.STYLE_
	 * @param colorManager IColorManager implementation. Set to null to use the
	 *            default
	 */
	public CollapsibleButtons(Composite parent, int style, int theme, IColorManager colorManager) {
		super(parent, checkStyle(style));
		this.mColorTheme = theme;
		this.mParent = parent;
		init();
	}

	/**
	 * Creates a new ButtonComposite. Add buttons using the addButton(...)
	 * method call.
	 * 
	 * By default, unless you set a theme, the theme will be read from whatever
	 * the active color scheme is in Windows XP. If you are using a custom theme
	 * and the color scheme cannot be determined, the fall-back will be the
	 * Windows XP Blue theme.
	 * 
	 * NOTE: If you want the Office 2007 theme, you have to set it manually as
	 * there is no way to guess if you have office 2007 installed or 2005.
	 * 
	 * @param parent Parent composite
	 * @param style Composite style, SWT.NO_BACKGROUND will be appended to the
	 *            style
	 * @param theme IColorManager.STYLE_
	 * @param colorManager IColorManager implementation. Set to null to use the
	 *            default
	 * @param settings ISettings implementation. Set to null to use the default
	 * @param language ILanguage implementations. Set to null to use the
	 *            default.
	 */
	public CollapsibleButtons(Composite parent, int style, int theme, IColorManager colorManager, ISettings settings, ILanguageSettings language) {
		super(parent, checkStyle(style));
		this.mColorTheme = theme;
		this.mColorManager = colorManager;
		this.mParent = parent;
		this.mSettings = settings;
		this.mLanguage = language;
		init();
	}

	private static int checkStyle(int style) {
		int mask = SWT.BORDER | SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT | SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.MULTI | SWT.NO_FOCUS | SWT.CHECK | SWT.VIRTUAL;
		int newStyle = style & mask;
		newStyle |= SWT.NO_BACKGROUND;
		return newStyle;
	}

	/**
	 * Adds a new IButtonListener listener that will report clicks and other
	 * events.
	 * 
	 * @param listener IButtonListener
	 */
	public void addButtonListener(IButtonListener listener) {
		checkWidget();
		if (!mButtonListeners.contains(listener))
			mButtonListeners.add(listener);
	}

	/**
	 * Removes an IButtonListener
	 * 
	 * @param listener IButtonListener
	 */
	public void removeButtonListener(IButtonListener listener) {
		checkWidget();
		mButtonListeners.remove(listener);
	}

	private void init() {
		// we need one, or *crash*
		if (mColorManager == null)
			mColorManager = new DefaultColorManager(mColorTheme);

		// same here
		if (mSettings == null)
			mSettings = new DefaultSettings();

		if (mLanguage == null)
			mLanguage = new DefaultLanguageManager();

		// outlook 2007 specific
		if (mColorTheme == IColorManager.SKIN_OFFICE_2007)
			mResizeBarSize = mSettings.getOutlook2007ResizeBarSize();
		else
			mResizeBarSize = mSettings.getOutlook2005ResizeBarSize();

		mButtonHeight = mSettings.getButtonHeight();

		mMenuListeners = new ArrayList();
		mButtons = new ArrayList();
		mHidden = new ArrayList();
		mButtonListeners = new ArrayList();

		// this lets us auto-fit the buttons to the aviaiable space when the
		// parent composite is resized.
		// Outlook does the same thing when the buttons don't have enough space.
		// We hide 1 button per call,
		// which should be enough as the next call will to 99.9% certainty in
		// less than 31 pixels (or whatever the button size is).
		// when the control is manually resized we reset the invuluntary size as
		// a "new size" has been picked, and that's
		// the starting point for the next invuluntary size if any. Confusing?
		// Just try it then look at the code.
		mParent.addControlListener(new ControlListener() {
			public void controlMoved(ControlEvent event) {
			}

			public void controlResized(ControlEvent event) {
				int availableHeight = mParent.getClientArea().height;
				int neededHeight = getBounds().height;

				if (availableHeight < neededHeight) {
					if (mInvoluntaryButtonLevel == -1) {
						mInvoluntaryButtonLevel = getNumVisibleButtons();
					}

					hideNextButton();
				}

				if (mInvoluntaryButtonLevel != -1) {
					if (availableHeight - mButtonHeight > neededHeight) {
						if (getNumVisibleButtons() < mInvoluntaryButtonLevel) {
							showNextButton();
						}
					}
				}
			}
		});

		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
				repaint(event);
			}
		});

		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent event) {
			}

			public void focusLost(FocusEvent event) {
				redraw();
			}
		});

		addMouseTrackListener(new MouseTrackAdapter() {
			public void mouseEnter(MouseEvent event) {
			}

			public void mouseExit(MouseEvent event) {
				setCursor(null);
			}

			public void mouseHover(MouseEvent event) {
			}
		});

		addMouseListener(this);

		addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent event) {
				Point p = toDisplay(new Point(event.x, event.y));

				if (!mMouseIsDown) {
					if (isInside(event.x, event.y, mMoveBar)) {
						if (mSettings.allowButtonResizing()) {
							setCursor(CURSOR_SIZENS);
						}
					} else {
						setCursor(null);
					}
				}

				if (mMouseIsDown) {
					// reset the "forced size" value, as we resized it to pick
					// what size we wanted
					mInvoluntaryButtonLevel = -1;
					int diff = p.y - mStartY;

					if (diff > mButtonHeight) {
						// ensures bar doesn't get smaller unless mouse pointer
						// is south of the move-bar
						if (event.y < mMoveBar.y) {
							return;
						}

						hideNextButton();
						mStartY = p.y;
					} else {
						if (Math.abs(diff) > mButtonHeight) {
							showNextButton();
							mStartY = p.y;
						}
					}
				}
			}
		});

		setLayout(new VerticalLayout());
	}

	private void repaint(PaintEvent event) {
		GC gc = event.gc;
		if (mCreated && mEnableDoubleBuffering) {
			try {
				Image buffer = new Image(Display.getDefault(), super.getBounds());
				GC gc2 = new GC(buffer);
				drawOntoGC(gc2);

				// transfer the image buffer onto this canvas
				// just drawImage(buffer, w, h) didn't work, so we do the whole
				// source transfer call
				Rectangle b = getBounds();
				gc.drawImage(buffer, 0, 0, b.width, b.height, 0, 0, b.width, b.height);

				// dispose the buffer, very important or we'll run out of
				// address space for buffered images
				buffer.dispose();
				gc2.dispose();
			} catch (IllegalArgumentException iea) {
				// seems to come here for some reason when we switch phases
				// while the gantt chart is being viewed, I'm not sure why
				// but no time to figure it out for the demo.. so instead of
				// buffering, just draw it onto the GC
				drawOntoGC(gc);
			}
		} else {
			drawOntoGC(gc);
			mCreated = true;

		}
	}

	private void drawOntoGC(GC gc) {
		gc.setBackground(mColorManager.getBorderColor());
		gc.fillRectangle(getClientArea());

		mBounds = super.getBounds();

		gc.setBackground(mColorManager.getDarkResizeColor());
		gc.setForeground(mColorManager.getLightResizeColor());

		gc.fillGradientRectangle(0, 0, mBounds.width, mResizeBarSize, true);
		mMoveBar = new Rectangle(0, 0, mBounds.width, mResizeBarSize);

		// office 2007 draws a 1 pixel around the resize bar, let's do that too
		if (mColorManager.getTheme() == IColorManager.SKIN_OFFICE_2007) {
			// top line inside is white, rest is gradient down to the next dark
			// color that is the border
			gc.setForeground(IColorManager.white);
			gc.drawLine(0, 0, mBounds.width, 0);

			// do the gradient
			gc.setBackground(mColorManager.getDarkResizeColor());
			gc.setForeground(mColorManager.getLightResizeColor());

			gc.fillGradientRectangle(0, 2, mBounds.width, mResizeBarSize - 2, true);

			gc.setForeground(mColorManager.getBorderColor());
			gc.drawLine(0, 0, mBounds.width, 0);
			gc.drawLine(0, mResizeBarSize - 1, mBounds.width, mResizeBarSize - 1);

			if (mSettings.drawBorder()) {
				gc.drawLine(0, 0, 0, mResizeBarSize);
				gc.drawLine(mBounds.width - 1, 0, mBounds.width - 1, mResizeBarSize);
			}
		}

		drawMarkers(gc);
	}

	private void drawMarkers(GC gc) {
		int numMarkers;
		if (mColorManager.getTheme() == IColorManager.SKIN_OFFICE_2007)
			numMarkers = mSettings.getOutlook2007ResizeDotNumber();
		else
			numMarkers = mSettings.getOutlook2005ResizeDotNumber();

		int start = (mBounds.width / 2) - numMarkers * 2;
		int extra = 0;

		// -1 is to align
		int y = (mResizeBarSize / 2) - 1;
		if (y < 0)
			y = 0;

		for (int i = 0; i < numMarkers; i++) {
			drawMarker(gc, start + extra, y);
			extra += 4;
		}
	}

	// draws a squared "shaded" marker on the resize bar
	private void drawMarker(GC gc, int x, int y) {
		gc.setBackground(mColorManager.getDotDarkColor());
		gc.fillRectangle(x, y, 2, 2);
		gc.setBackground(mColorManager.getDotMiddleColor());
		gc.fillRectangle(x + 1, y + 1, 2, 2);
		gc.setBackground(mColorManager.getDotLightColor());
		gc.fillRectangle(x + 1, y + 1, 1, 1);
	}

	public void mouseMove(MouseEvent event) {
	}

	/**
	 * Returns the number of currently visible buttons.
	 * 
	 * @return Number of visible buttons
	 */
	public int getNumVisibleButtons() {
		checkWidget();
		int num = 0;
		for (int i = 0; i < mButtons.size(); i++) {
			CustomButton b = (CustomButton) mButtons.get(i);
			if (b.isVisible())
				num++;
		}
		return num;
	}

	/**
	 * Hides the button from the list and the toolbar.
	 * 
	 * @param button Button to hide
	 */
	public void permanentlyHideButton(CustomButton button) {
		checkWidget();
		if (mHidden.contains(button))
			return;

		mHidden.add(button);

		for (int i = 0; i < mButtons.size(); i++) {
			CustomButton b = (CustomButton) mButtons.get(i);
			if (b == button) {
				if (b.isVisible()) {
					b.setVisible(false);
					// Don't redraw until stuff is laid out, that way, no
					// toolbar ghosting!
					mHiddenButtons++;
					setRedraw(false);
					// parent needs to re-adjust it's size, which in turn
					// adjusts our size via (true) as that forces
					// children (us) to resize.
					getParent().layout(true);
					setRedraw(true);
				}

				// mToolBarComposite.removeItem(b);
				mToolBarComposite.hideButton(b);
				mToolBarComposite.redraw();
				break;
			}
		}
	}

	/**
	 * Un-hides a button that has been hidden from toolbar and ButtonComposite
	 * view.
	 * 
	 * @param button Button to show
	 */
	public void permanentlyShowButton(CustomButton button) {
		checkWidget();
		mHidden.remove(button);

		for (int i = 0; i < mButtons.size(); i++) {
			CustomButton b = (CustomButton) mButtons.get(i);

			if (b == button) {
				if (!b.isVisible()) {
					b.setVisible(true);
					mHiddenButtons--;
					// Don't redraw until stuff is laid out, that way, no
					// toolbar ghosting!
					setRedraw(false);
					// parent needs to re-adjust it's size, which in turn
					// adjusts our size via (true) as that forces
					// children (us) to resize.
					getParent().layout(true);
					setRedraw(true);
				}

				mToolBarComposite.removeItem(b);
				break;
			}
		}
	}

	/**
	 * Hides the given button (and adds it to the toolbar).
	 * 
	 * @param button Button to hide
	 */
	public void hideButton(CustomButton button) {
		checkWidget();
		for (int i = 0; i < mButtons.size(); i++) {
			CustomButton b = (CustomButton) mButtons.get(i);
			if (b == button) {
				// if (label.isVisible() == true) {
				b.setVisible(false);
				// Don't redraw until stuff is laid out, that way, no toolbar
				// ghosting!
				mHiddenButtons++;
				setRedraw(false);
				// parent needs to re-adjust it's size, which in turn adjusts
				// our size via (true) as that forces
				// children (us) to resize.
				getParent().layout(true);
				setRedraw(true);
				// }

				mToolBarComposite.addItem(b);
				break;
			}
		}
	}

	/**
	 * Shows the given button (and removes it from the toolbar).
	 * 
	 * @param button Button to show
	 */
	public void showButton(CustomButton button) {
		checkWidget();
		for (int i = 0; i < mButtons.size(); i++) {
			CustomButton b = (CustomButton) mButtons.get(i);

			if (b == button) {
				if (!b.isVisible()) {
					b.setVisible(true);
					mHiddenButtons--;
					// Don't redraw until stuff is laid out, that way, no
					// toolbar ghosting!
					setRedraw(false);
					// parent needs to re-adjust it's size, which in turn
					// adjusts our size via (true) as that forces
					// children (us) to resize.
					getParent().layout(true);
					setRedraw(true);
				}

				mToolBarComposite.addItem(b);
				mToolBarComposite.redraw();
				break;
			}
		}
	}

	/**
	 * If a button is permanently hidden or permanently shown.
	 * 
	 * @param button CustomButton to check
	 * @return true or false
	 */
	public boolean isVisible(CustomButton button) {
		checkWidget();
		return !mHidden.contains(button);
	}

	/**
	 * Hides the next button furthest down in the list. If there are no more
	 * buttons left to hide, nothing will happen.
	 * 
	 */
	public void hideNextButton() {
		checkWidget();
		if (!mSettings.allowButtonResizing()) {
			return;
		}

		for (int i = (mButtons.size() - 1); i >= 0; i--) {
			CustomButton b = (CustomButton) mButtons.get(i);

			if (mHidden.contains(b))
				continue;

			if (b.isVisible()) {
				mHiddenButtons++;
				b.setVisible(false);
				// laying out the parent with true forces us to layout too, so
				// don't overdo it by calling layout(true) locally
				setRedraw(false);
				getParent().layout(true);
				setRedraw(true);

				mToolBarComposite.addItem(b);
				break;
			}
		}
	}

	/**
	 * Should you ever need to force a re-layout of the composite, this is the
	 * method to call. It is not recommended to be used.
	 * 
	 */
	public void forceLayoutUpdate() {
		checkWidget();
		getParent().layout(true);
	}

	/**
	 * Shows the next button from the list of buttons that are currently hidden.
	 * If there are no more buttons hiding, nothing will happen.
	 * 
	 */
	public void showNextButton() {
		checkWidget();
		if (!mSettings.allowButtonResizing()) {
			return;
		}

		for (int i = 0; i < mButtons.size(); i++) {
			CustomButton b = (CustomButton) mButtons.get(i);

			if (mHidden.contains(b))
				continue;

			if (!b.isVisible()) {
				mHiddenButtons--;
				b.setVisible(true);
				// Don't redraw until stuff is laid out, that way, no toolbar
				// ghosting!
				setRedraw(false);
				// parent needs to re-adjust it's size, which in turn adjusts
				// our size via (true) as that forces
				// children (us) to resize.
				getParent().layout(true);
				setRedraw(true);

				mToolBarComposite.removeItem(b);
				break;
			}
		}
	}

	// rectangle intersection, the easier way
	private boolean isInside(int x, int y, Rectangle rect) {
		if (rect == null)
			return false;

		return x >= rect.x && y >= rect.y && x <= (rect.x + rect.width) && y <= (rect.y + rect.height);
	}

	/**
	 * Adds a button to the composite. Button will be added at the bottom of any
	 * previously existing buttons.
	 * 
	 * @param name Text that should be displayed on button. May be null.
	 * @param toolTip Tooltip that is displayed when mouse moves over both
	 *            button and tool bar icon. Recommended null.
	 * @param bigImage Image displayed on the button. Ideally 24x24 pixels
	 *            transparent PNG image.
	 * @param toolbarImage Image displayed on the toolbar and on any menu items.
	 *            Ideally 16x16 pixels transparent GIF image.
	 * @return CustomButton for further pre-launch modification.
	 */
	public CustomButton addButton(String name, String toolTip, Image bigImage, Image toolbarImage) {
		checkWidget();
		return addButton(name, toolTip, bigImage, toolbarImage, false);
	}

	// _addButton
	private CustomButton addButton(String name, String toolTip, Image bigImage, Image toolbarImage, boolean selected) {
		checkWidget();
		CustomButton cb = new CustomButton(this, SWT.FLAT, name, bigImage, toolbarImage, toolTip, mSettings);

		cb.addMouseListener(this);
		cb.addMouseTrackListener(this);

		mButtons.add(cb);

		if (mToolBarComposite == null)
			mToolBarComposite = new ToolbarComposite(this, SWT.NONE);

		mParent.redraw();
		mParent.layout();
		
		reindexButtons();
		return cb;

	}
	
	private void reindexButtons() {
		for (int i = 0; i < mButtons.size(); i++) {
			((CustomButton)mButtons.get(i)).setNumber(i);
		}
	}

	public void mouseDoubleClick(MouseEvent event) {
		checkWidget();
	}

	public void mouseDown(MouseEvent event) {
		checkWidget();
		Point p = toDisplay(new Point(event.x, event.y));
		mStartY = p.y;
		mMouseIsDown = true;

		if (event.widget instanceof CustomButton) {
			CustomButton cb = (CustomButton) event.widget;
			if (event.button == 1) {
				if (mSelectedButton != null && cb.equals(mSelectedButton))
					return;

				for (int i = 0; i < mButtonListeners.size(); i++) {
					IButtonListener inav = (IButtonListener) mButtonListeners.get(i);
					inav.buttonClicked(cb, event);
				}

				selectButton(cb);
			}
		}
	}

	public void mouseUp(MouseEvent event) {
		checkWidget();
		setCursor(null);
		mMouseIsDown = false;
	}

	public Point getSize() {
		checkWidget();
		int bs = mButtons.size() - mHiddenButtons;
		int y = bs * (mButtonHeight + 1);
		if (mSettings.showToolBar()) {
			y += mButtonHeight;
		}

		y += mResizeBarSize;

		return new Point(super.getSize().x, y);
	}

	public void mouseEnter(MouseEvent event) {
		checkWidget();
		if (event.widget instanceof CustomButton) {
			CustomButton cb = (CustomButton) event.widget;

			setCursor(CURSOR_HAND);

			cb.updateHover(true);

			for (int i = 0; i < mButtonListeners.size(); i++) {
				IButtonListener inav = (IButtonListener) mButtonListeners.get(i);
				inav.buttonEnter(cb, event);
			}
		}
	}

	public void mouseExit(MouseEvent event) {
		checkWidget();
		if (event.widget instanceof CustomButton) {
			CustomButton cb = (CustomButton) event.widget;
			cb.updateHover(false);

			setCursor(null);

			for (int i = 0; i < mButtonListeners.size(); i++) {
				IButtonListener inav = (IButtonListener) mButtonListeners.get(i);
				inav.buttonExit(cb, event);
			}
		}
	}

	public void mouseHover(MouseEvent event) {
		checkWidget();
		if (event.widget instanceof CustomButton) {
			CustomButton cb = (CustomButton) event.widget;

			for (int i = 0; i < mButtonListeners.size(); i++) {
				IButtonListener inav = (IButtonListener) mButtonListeners.get(i);
				inav.buttonHover(cb, event);
			}
		}
	}

	/**
	 * Flags a button as selected and pretends it got clicked.
	 * 
	 * @param button Button to select and click
	 */
	public void selectItemAndLoad(CustomButton button) {
		checkWidget();
		selectItem(button);
		for (int i = 0; i < mButtonListeners.size(); i++) {
			IButtonListener inav = (IButtonListener) mButtonListeners.get(i);
			inav.buttonClicked(getSelection(), null);
		}
	}

	/**
	 * Selects a specific CustomButton.
	 * 
	 * @param button CustomButton to select
	 */
	public void selectItem(CustomButton button) {
		checkWidget();
		for (int i = 0; i < mButtons.size(); i++) {
			CustomButton b = (CustomButton) mButtons.get(i);
			if (b == button) {
				selectButton(button);
				break;
			}
		}
	}

	/**
	 * Deselects all buttons
	 */
	public void deselectAll() {
		if (mSelectedButton != null) {
			mSelectedButton.updateSelection(false);
		}
		mSelectedButton = null;
		mToolBarComposite.setSelectedItem(null);
		redraw();
	}
	
	// selects a button
	private void selectButton(CustomButton button) {
		if (mSelectedButton != null) {
			if (mSelectedButton.equals(button))
				return;

			// clear old selection
			mSelectedButton.updateSelection(false);
		}

		// set new selection
		button.updateSelection(true);
		mSelectedButton = button;
		mToolBarComposite.setSelectedItem(mSelectedButton);
	}

	/**
	 * Returns the list of all buttons.
	 * 
	 * @return List of buttons
	 */
	public List getItems() {
		checkWidget();
		return mButtons;
	}

	/**
	 * Returns the current selection, or null if none.
	 * 
	 * @return Selected button.
	 */
	public CustomButton getSelection() {
		checkWidget();
		return mSelectedButton;
	}

	/**
	 * Returns the number of buttons in the list.
	 * 
	 * @return Button count
	 */
	public int itemCount() {
		checkWidget();
		return mButtons.size();
	}

	/**
	 * Returns the active color manager.
	 * 
	 * @return IColorManager
	 */
	public IColorManager getColorManager() {
		checkWidget();
		return mColorManager;
	}

	/**
	 * Returns the current Settings manager.
	 * 
	 * @return ISettings
	 */
	public ISettings getSettings() {
		checkWidget();
		return mSettings;
	}

	/**
	 * Returns the current Language settings manager.
	 * 
	 * @return ILanguageSettings
	 */
	public ILanguageSettings getLanguageSettings() {
		checkWidget();
		return mLanguage;
	}

	/**
	 * Returns the toolbar composite.
	 * 
	 * @return ToolbarComposite
	 */
	public ToolbarComposite getToolbarComposite() {
		checkWidget();
		return mToolBarComposite;
	}

	/**
	 * Adds a menu listener that is notified before and after the menu popup is shown.
	 * 
	 * @param listener Listener to add
	 */
	public void addMenuListener(IMenuListener listener) {
		if (!mMenuListeners.contains(listener))
			mMenuListeners.add(listener);
	}

	/**
	 * Removes a menu listener.
	 * 
	 * @param listener Listener to remove
	 */
	public void removeMenuListener(IMenuListener listener) {
		mMenuListeners.remove(listener);
	}

	/**
	 * Removes all buttons.
	 */
	public void removeAllButtons() {
		checkWidget();
		
		// remove them in reverse or we'll have some interesting issues
		for (int i = mButtons.size()-1; i >= 0; i--) {
			((CustomButton) mButtons.get(i)).dispose();
		}
		
		if (mToolBarComposite != null)
			mToolBarComposite.removeAll();
		
		mButtonListeners.clear();
		mButtons.clear();
		mParent.redraw();
		mParent.layout();		
	}

	/**
	 * Same method that is called when {@link CustomButton#dispose()} is called.
	 * 
	 * @param cb CustomButton to remove
	 */
	public void removeButton(CustomButton cb) {
		checkWidget();
		remove(cb, true);
	}
	
	// internal remove of button
	void remove(CustomButton cb, boolean callDispose) {
		cb.removeMouseListener(this);
		cb.removeMouseTrackListener(this);

		mButtons.remove(cb);
		
		if (mToolBarComposite != null)
			mToolBarComposite.removeItem(cb);
		
		mParent.redraw();
		mParent.layout();

		if (callDispose)
			cb.dispose();		
		
		reindexButtons();
	}
	
	List getMenuListeners() {
		return mMenuListeners;
	}

	// layout class that deals with the actual layout of the buttons, toolbar
	// and other drawn items
	class VerticalLayout extends Layout {

		public VerticalLayout() {
		}

		protected Point computeSize(Composite aComposite, int wHint, int hHint, boolean flushCache) {
			return getSize();
		}

		protected void layout(final Composite aComposite, boolean flushCache) {
			int top = mResizeBarSize;
			int left = (mSettings.drawBorder() ? 1 : 0);

			if (mSettings.showToolBar()) {
				int toolTop = top;

				// calculate where toolbar goes first, causes less ghosting
				for (int i = 0; i < mButtons.size(); i++) {
					CustomButton button = (CustomButton) mButtons.get(i);
					if (!button.isVisible()) {
						continue;
					}

					toolTop += mButtonHeight + 1;
				}

				if (mToolBarComposite != null)
					mToolBarComposite.setBounds(left, toolTop, aComposite.getBounds().width - (mSettings.drawBorder() ? 2 : 0), mButtonHeight);
			}

			// now set the toolbars
			for (int i = 0; i < mButtons.size(); i++) {
				CustomButton button = (CustomButton) mButtons.get(i);
				if (!button.isVisible()) {
					continue;
				}

				button.setBounds(left, top, aComposite.getBounds().width - (mSettings.drawBorder() ? 2 : 0), mButtonHeight);
				top += mButtonHeight + 1;
			}
		}
	}
}
