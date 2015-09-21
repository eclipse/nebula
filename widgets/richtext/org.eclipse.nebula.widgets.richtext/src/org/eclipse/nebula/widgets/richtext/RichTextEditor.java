/*****************************************************************************
 * Copyright (c) 2015 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *		Dirk Fauth <dirk.fauth@googlemail.com> - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.nebula.widgets.richtext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.bindings.keys.SWTKeySupport;
import org.eclipse.nebula.widgets.richtext.toolbar.ToolbarButton;
import org.eclipse.nebula.widgets.richtext.toolbar.ToolbarConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * Rich Text Editor control that wraps a {@link Browser} with enabled Javascript that shows a simple
 * HTML template containing a ckeditor as rich text editor.
 * 
 * <p>
 * The following style bits are supported:
 * <ul>
 * <li>{@link SWT#RESIZE} - specify if the resize function of ckeditor is enabled (mostly used for
 * embedded usage)</li>
 * <li>{@link SWT#MIN} - specify if the configured minimum dimensions should be applied to the
 * resize function of ckeditor</li>
 * <li>{@link SWT#EMBEDDED} - specify if the rich text editor is used in embedded mode (e.g. as a
 * cell editor of a JFace viewer)</li>
 * </ul>
 * Additionally the SWT Browser style bits {@link SWT#MOZILLA} or {@link SWT#WEBKIT} can be set to
 * specify the native browser that should be used for rendering
 * </p>
 * 
 * @see <a href="http://ckeditor.com/">http://ckeditor.com/</a>
 */
public class RichTextEditor extends Composite {

	private Double CKEDITOR_ALT;
	private Double CKEDITOR_CTRL;
	private Double CKEDITOR_SHIFT;

	private boolean editorLoaded = false;
	private String initialValue = null;
	private boolean initialSetFocus = false;

	protected Rectangle resizedBounds = null;

	private final Browser browser;

	private final List<BrowserFunction> browserFunctions = new ArrayList<>();

	private final ListenerList modifyListener = new ListenerList(ListenerList.IDENTITY);
	private final ListenerList keyListener = new ListenerList(ListenerList.IDENTITY);
	private final ListenerList focusListener = new ListenerList(ListenerList.IDENTITY);

	private final ToolbarConfiguration toolbarConfig;

	private Shell embeddedShell;
	private Point mouseDragPosition;

	/**
	 * Key of the system property to specify a fixed directory to unpack the ckeditor resources to.
	 * If a system property for that key is registered and the rich text control is deployed within
	 * a JAR, the resources will be unpacked into the specified directory. If no value is registered
	 * for that key and the rich text control is deployed in a JAR, the resources will be unpacked
	 * into a temporary directory, that gets deleted when the runtime is shutdown. If the rich text
	 * control is not deployed within a JAR but as part of an Eclipse application, the bundle will
	 * be unpacked automatically. In this case this system property won't get interpreted.
	 */
	public static final String JAR_UNPACK_LOCATION_PROPERTY = "org.eclipse.nebula.widgets.richtext.jar.unpackdir";

	private static URL templateURL;
	static {
		locateTemplateURL();
	}

	/**
	 * Creates a {@link RichTextEditor} that wraps a {@link Browser} using the style bit
	 * {@link SWT#NONE} and the default {@link ToolbarConfiguration}.
	 * 
	 * @param parent
	 *            the parent composite where this rich text editor should be added to
	 */
	public RichTextEditor(Composite parent) {
		this(parent, null, SWT.NONE);
	}

	/**
	 * Creates a {@link RichTextEditor} that wraps a {@link Browser} using the style bit
	 * {@link SWT#NONE} and the given {@link ToolbarConfiguration}.
	 * 
	 * @param parent
	 *            the parent composite where this rich text editor should be added to
	 * @param toolbarConfig
	 *            the {@link ToolbarConfiguration} to use or <code>null</code> for using the default
	 *            {@link ToolbarConfiguration}
	 */
	public RichTextEditor(Composite parent, ToolbarConfiguration toolbarConfig) {
		this(parent, toolbarConfig, SWT.NONE);
	}

	/**
	 * Creates a {@link RichTextEditor} that wraps a {@link Browser} using the given style bit and
	 * the default {@link ToolbarConfiguration}.
	 * 
	 * @param parent
	 *            the parent composite where this rich text editor should be added to
	 * @param style
	 *            the style of widget to construct, see {@link Browser} for further style bit
	 *            information
	 */
	public RichTextEditor(Composite parent, int style) {
		this(parent, null, style);
	}

	/**
	 * Creates a {@link RichTextEditor} that wraps a {@link Browser} using the given style bit and
	 * the given {@link ToolbarConfiguration}.
	 * 
	 * @param parent
	 *            the parent composite where this rich text editor should be added to
	 * @param toolbarConfig
	 *            the {@link ToolbarConfiguration} to use or <code>null</code> for using the default
	 *            {@link ToolbarConfiguration}
	 * @param style
	 *            the style of widget to construct, see {@link Browser} for further style bit
	 *            information
	 * 
	 * @see Browser
	 */
	public RichTextEditor(Composite parent, ToolbarConfiguration toolbarConfig, int style) {
		super(parent, style);

		setLayout(new FillLayout());

		final boolean resizable = (getStyle() & SWT.RESIZE) != 0;
		final boolean embedded = (getStyle() & SWT.EMBEDDED) != 0;

		if (embedded) {
			this.embeddedShell = new Shell(parent.getShell(), SWT.MODELESS);
			this.embeddedShell.setLayout(new FillLayout());
		}

		// remove styles that are not relevant for the browser
		int browserStyle = style & ~SWT.RESIZE & ~SWT.MIN & ~SWT.EMBEDDED;

		this.browser = new Browser(!embedded ? this : this.embeddedShell, browserStyle);
		this.browser.setJavascriptEnabled(true);

		if (toolbarConfig == null) {
			this.toolbarConfig = new ToolbarConfiguration();
		}
		else {
			this.toolbarConfig = toolbarConfig;
		}

		this.toolbarConfig.setBrowser(this.browser);

		this.browser.setUrl(templateURL.toString());

		this.browserFunctions.add(new ModifyFunction(browser, "textModified"));
		this.browserFunctions.add(new KeyPressedFunction(browser, "keyPressed"));
		this.browserFunctions.add(new KeyReleasedFunction(browser, "keyReleased"));
		this.browserFunctions.add(new FocusInFunction(browser, "focusIn"));
		this.browserFunctions.add(new FocusOutFunction(browser, "focusOut"));
		this.browserFunctions.add(new BrowserFunction(browser, "configureToolbar") {
			@Override
			public Object function(Object[] arguments) {
				RichTextEditor.this.toolbarConfig.configureToolbar();
				return super.function(arguments);
			}
		});

		this.browser.addProgressListener(new ProgressListener() {

			@Override
			public void completed(ProgressEvent event) {
				browser.evaluate("initEditor();");

				CKEDITOR_ALT = (Double) browser.evaluate("return getCKEditorALT()");
				CKEDITOR_CTRL = (Double) browser.evaluate("return getCKEditorCTRL()");
				CKEDITOR_SHIFT = (Double) browser.evaluate("return getCKEditorSHIFT()");

				editorLoaded = true;

				if (resizable) {
					boolean specifyMin = ((getStyle() & SWT.MIN) != 0);
					int minWidth = specifyMin ? getMinimumWidth() : 0;
					int minHeight = specifyMin ? getMinimumHeight() : 0;
					browser.evaluate("setResizeEnabled(true, " + minWidth + ", " + minHeight + ");");
				}

				if (RichTextEditor.this.toolbarConfig.toolbarCollapsible) {
					browser.evaluate("setToolbarCollapsible(true);");
					browser.evaluate("setToolbarInitialExpanded(" + RichTextEditor.this.toolbarConfig.toolbarInitialExpanded + ");");
				}

				// only add this function for resizable inline editing
				if (resizable && embedded) {
					// register the callback to resize the browser if the
					// ckeditor is resized
					browserFunctions.add(new BrowserFunction(browser, "updateDimensions") {
						@Override
						public Object function(Object[] arguments) {
							// width and height +2 because the editor is 1 pixel
							// smaller than the browser container on every side
							setInlineContainerBounds(
									getBounds().x,
									getBounds().y,
									((Double) arguments[0]).intValue() + 2,
									((Double) arguments[1]).intValue() + 2);

							// also repaint the parent control to avoid
							// rendering glitches while resizing
							if (getParent() != null) {
								getParent().redraw();
								getParent().update();
							}

							return super.function(arguments);
						}
					});

					browser.evaluate("enableResizeCallback();");

					if (embedded) {
						browserFunctions.add(new BrowserFunction(browser, "activateShellDragMode") {
							@Override
							public Object function(Object[] arguments) {
								mouseDragPosition = new Point(((Double) arguments[0]).intValue(), ((Double) arguments[1]).intValue());
								return super.function(arguments);
							};
						});

						browserFunctions.add(new BrowserFunction(browser, "deactivateShellDragMode") {
							@Override
							public Object function(Object[] arguments) {
								mouseDragPosition = null;
								return super.function(arguments);
							};
						});

						browserFunctions.add(new BrowserFunction(browser, "moveShell") {
							@Override
							public Object function(Object[] arguments) {
								if (mouseDragPosition != null) {
									Point cursorLocation = Display.getDefault().getCursorLocation();
									embeddedShell.setLocation(cursorLocation.x - mouseDragPosition.x, cursorLocation.y - mouseDragPosition.y);
								}
								return super.function(arguments);
							};
						});

						browser.evaluate("addMoveAnchor();");
					}
				}

				if (initialValue != null) {
					setText(initialValue);
				}

				if (initialSetFocus) {
					setFocus();
				}
			}

			@Override
			public void changed(ProgressEvent event) {
			}
		});
	}

	@Override
	public void dispose() {
		// dispose the toolbar configuration
		toolbarConfig.dispose();
		// dispose the registered BrowserFunctions
		for (BrowserFunction function : browserFunctions) {
			function.dispose();
		}
		// dispose the Browser
		browser.dispose();

		// dispose the embedded shell if we are in embedded mode
		if (this.embeddedShell != null && !this.embeddedShell.isDisposed()) {
			this.embeddedShell.dispose();
		}
		// call super to ensure the resources of this composite are released
		super.dispose();
	}

	@Override
	public void setVisible(boolean visible) {
		if (this.embeddedShell != null && !this.embeddedShell.isDisposed()) {
			this.embeddedShell.setVisible(visible);
		}
		super.setVisible(visible);
	}

	/**
	 * @return The text that is currently set in the editing area. Contains HTML tags for
	 *         formatting.
	 */
	public String getText() {
		if (browser != null && !browser.isDisposed()) {
			Object result = browser.evaluate("return getText()");
			return result != null ? result.toString() : null;
		}
		return null;
	}

	/**
	 * Set text to the editing area. Can contain HTML tags for styling.
	 * 
	 * @param text
	 *            The text to set to the editing area.
	 */
	public void setText(String text) {
		if (editorLoaded) {
			text = text.replace("\n", "\\n").replace("\r", "\\r");
			browser.evaluate("setText('" + text + "')");
		}
		else {
			initialValue = text;
		}
	}

	/**
	 * Insert text content into the currently selected position in the editor in WYSIWYG mode. The
	 * styles of the selected element will be applied to the inserted text. Spaces around the text
	 * will be left untouched.
	 * 
	 * @param text
	 *            Text to be inserted into the editor.
	 */
	public void insertText(String text) {
		browser.evaluate("insertText('" + text + "')");
	}

	/**
	 * Inserts HTML code into the currently selected position in the editor in WYSIWYG mode.
	 * 
	 * @param html
	 *            HTML code to be inserted into the editor.
	 */
	public void insertHTML(String html) {
		browser.evaluate("insertHTML('" + html + "')");
	}

	/**
	 * Returns the current selected text without any markup tags.
	 * 
	 * @return The current selected text without any markup tags.
	 */
	public String getSelectedText() {
		return "" + browser.evaluate("return getSelectedText();");
	}

	/**
	 * Returns the current selected text containing the markup tags for styling.
	 * <p>
	 * <i>Note: It will not contain the parent tag.</i>
	 * </p>
	 * 
	 * @return The current selected text containing any markup styling tags.
	 */
	public String getSelectedHTML() {
		Object html = browser.evaluate("return getSelectedHTML()");
		return html != null ? html.toString() : "";
	}

	/**
	 * Returns the editable state.
	 *
	 * @return whether or not the receiver is editable
	 * 
	 */
	public boolean isEditable() {
		Object result = browser.evaluate("return isEditable()");
		return result != null ? !Boolean.valueOf(result.toString()) : true;
	}

	/**
	 * Update the toolbar. Typically used if buttons where added or removed at runtime.
	 */
	public void updateToolbar() {
		toolbarConfig.configureToolbar();
		browser.evaluate("updateToolbar();");
	}

	/**
	 * Sets the editable state.
	 *
	 * @param editable
	 *            the new editable state
	 */
	public void setEditable(boolean editable) {
		browser.evaluate("setReadOnly(" + !editable + ")");
	}

	/**
	 * Adds the given {@link ToolbarButton} to the toolbar of the editor.
	 * 
	 * @param button
	 *            The button to add.
	 * 
	 * @see ToolbarConfiguration#addToolbarButton(ToolbarButton)
	 */
	public void addToolbarButton(ToolbarButton button) {
		toolbarConfig.addToolbarButton(button);
	}

	/**
	 * Adds the given {@link ToolbarButton} to the toolbar of the editor. Uses the given
	 * {@link BrowserFunction} as callback for the button.
	 * 
	 * @param button
	 *            The button to add.
	 * @param function
	 *            The function to use as callback.
	 * 
	 * @see ToolbarConfiguration#addToolbarButton(ToolbarButton, BrowserFunction)
	 */
	public void addToolbarButton(ToolbarButton button, BrowserFunction function) {
		toolbarConfig.addToolbarButton(button, function);
	}

	/**
	 * Removes the given {@link ToolbarButton} from the toolbar of the editor.
	 * 
	 * @param button
	 *            The button to remove.
	 * 
	 * @see ToolbarConfiguration#removeToolbarButton(ToolbarButton)
	 */
	public void removeToolbarButton(ToolbarButton button) {
		toolbarConfig.removeToolbarButton(button);
	}

	@Override
	public boolean setFocus() {
		if (editorLoaded) {
			browser.evaluate("setFocus();");
			Object result = browser.evaluate("return hasFocus();");
			return result != null ? Boolean.valueOf(result.toString()) : false;
		}
		else {
			initialSetFocus = true;
			return true;
		}
	}

	@Override
	public boolean forceFocus() {
		return setFocus();
	}

	@Override
	public boolean isFocusControl() {
		if (editorLoaded) {
			Object result = browser.evaluate("return hasFocus();");
			return result != null ? Boolean.valueOf(result.toString()) : false;
		}
		return false;
	}

	@Override
	public void addFocusListener(FocusListener listener) {
		checkWidget();
		if (listener == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		this.focusListener.add(listener);
	}

	@Override
	public void removeFocusListener(FocusListener listener) {
		checkWidget();
		if (listener == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		this.focusListener.remove(listener);
	}

	/**
	 * Notify the registered {@link FocusListener} that the editor gained focus.
	 * 
	 * @param event
	 *            The event to fire.
	 */
	public void notifyFocusGained(final FocusEvent event) {
		checkWidget();
		if (event == null) {
			return;
		}

		if (event.display != null) {
			event.display.asyncExec(new Runnable() {
				@Override
				public void run() {
					doNotifyFocusGained(event);
				}
			});
		}
		else {
			// no display in the event, fire the events synchronously
			doNotifyFocusGained(event);
		}
	}

	private void doNotifyFocusGained(FocusEvent event) {
		Object[] listeners = this.focusListener.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			((FocusListener) listeners[i]).focusGained(event);
		}
	}

	/**
	 * Notify the registered {@link FocusListener} that the editor lost focus.
	 * 
	 * @param event
	 *            The event to fire.
	 */
	public void notifyFocusLost(final FocusEvent event) {
		checkWidget();
		if (event == null) {
			return;
		}

		if (event.display != null) {
			event.display.asyncExec(new Runnable() {
				@Override
				public void run() {
					doNotifyFocusLost(event);
				}
			});
		}
		else {
			// no display in the event, fire the events synchronously
			doNotifyFocusLost(event);
		}
	}

	private void doNotifyFocusLost(FocusEvent event) {
		Object[] listeners = this.focusListener.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			((FocusListener) listeners[i]).focusLost(event);
		}
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		int newX = x;
		int newY = y;
		int newWidth = width;
		int newHeight = height;
		if (resizedBounds != null) {
			newX = resizedBounds.x;
			newY = resizedBounds.y;
			newWidth = resizedBounds.width;
			newHeight = resizedBounds.height;
		}
		else if ((getStyle() & SWT.EMBEDDED) != 0) {
			// ensure min size when opened inline
			newHeight = Math.max(height, getMinimumHeight());
			newWidth = Math.max(width, getMinimumWidth());
		}

		if (this.embeddedShell != null) {
			Point shellLocation = super.toDisplay(newX, newY);
			this.embeddedShell.setBounds(shellLocation.x, shellLocation.y, newWidth, newHeight);
			this.embeddedShell.setVisible(true);
		}
		else {
			super.setBounds(newX, newY, newWidth, newHeight);
		}
	}

	/**
	 * Used in embedded mode to support manual resizing of the editor. Executed via callback on
	 * ckeditor resize.
	 * 
	 * @param x
	 *            the new x coordinate for the receiver
	 * @param y
	 *            the new y coordinate for the receiver
	 * @param width
	 *            the new width for the receiver
	 * @param height
	 *            the new height for the receiver
	 */
	void setInlineContainerBounds(int x, int y, int width, int height) {
		this.resizedBounds = new Rectangle(x, y, width, height);
		if (this.embeddedShell != null) {
			Point shellLocation = this.embeddedShell.getLocation();
			this.embeddedShell.setBounds(shellLocation.x, shellLocation.y, width + 2, height + 2);
		}
		else {
			super.setBounds(x, y, width, height);
		}
	}

	/**
	 * Returns the minimum height that should be used for initially open the editor in embedded
	 * mode. It is also used to specify the resize minimum height if the editor was created using
	 * the style bit {@link SWT#MIN}. Using the default {@link ToolbarConfiguration} this is 150 for
	 * the toolbar and 50 for showing one row in the editor area.
	 * 
	 * @return The minimum height to use for initially open the editor in embedded mode and for
	 *         editor resize minimum in case the editor was created with {@link SWT#MIN}
	 */
	protected int getMinimumHeight() {
		return 200;
	}

	/**
	 * Returns the minimum width that should be used for initially open the editor in embedded mode.
	 * It is also used to specify the resize minimum width if the editor was created using the style
	 * bit {@link SWT#MIN}. Using the default {@link ToolbarConfiguration} this is 370 for showing
	 * the default options in three lines of the toolbar.
	 * 
	 * @return The minimum width to use for initially open the editor in embedded mode and for
	 *         editor resize minimum in case the editor was created with {@link SWT#MIN}
	 */
	protected int getMinimumWidth() {
		return 370;
	}

	/**
	 * Executes the specified script in the internal {@link Browser}. Can be used to execute
	 * Javascript directly in the browser from a listener if necessary.
	 * 
	 * @param script
	 *            the script with javascript commands
	 * @return <code>true</code> if the operation was successful and <code>false</code> otherwise
	 * @see Browser#execute(String)
	 */
	public boolean executeJavascript(String script) {
		return this.browser.execute(script);
	}

	/**
	 * Evaluates the specified script in the internal {@link Browser} and returns the result. Can be
	 * used to evaluate Javascript directly in the browser from a listener if necessary.
	 * 
	 * @param script
	 *            the script with javascript commands
	 * @return the return value, if any, of executing the script
	 * @see Browser#evaluate(String)
	 */
	public Object evaluateJavascript(String script) {
		return this.browser.evaluate(script);
	}

	@Override
	public void addKeyListener(KeyListener listener) {
		checkWidget();
		if (listener == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		this.keyListener.add(listener);
	}

	@Override
	public void removeKeyListener(KeyListener listener) {
		checkWidget();
		if (listener == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		this.keyListener.remove(listener);
	}

	/**
	 * Notify the registered {@link KeyListener} that a key was pressed.
	 * 
	 * @param event
	 *            The event to fire.
	 */
	public void notifyKeyPressed(final KeyEvent event) {
		checkWidget();
		if (event == null) {
			return;
		}

		if (event.display != null) {
			event.display.asyncExec(new Runnable() {
				@Override
				public void run() {
					doNotifyKeyPressed(event);
				}
			});
		}
		else {
			// no display in the event, fire the events synchronously
			doNotifyKeyPressed(event);
		}
	}

	private void doNotifyKeyPressed(KeyEvent event) {
		Object[] listeners = this.keyListener.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			((KeyListener) listeners[i]).keyPressed(event);
		}
	}

	/**
	 * Notify the registered {@link KeyListener} that a key was released.
	 * 
	 * @param event
	 *            The event to fire.
	 */
	public void notifyKeyReleased(final KeyEvent event) {
		checkWidget();
		if (event == null) {
			return;
		}

		if (event.display != null) {
			event.display.asyncExec(new Runnable() {
				@Override
				public void run() {
					doNotifyKeyReleased(event);
				}
			});
		}
		else {
			// no display in the event, fire the events synchronously
			doNotifyKeyReleased(event);
		}
	}

	private void doNotifyKeyReleased(KeyEvent event) {
		Object[] listeners = this.keyListener.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			((KeyListener) listeners[i]).keyReleased(event);
		}
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified when the receiver's
	 * text is modified, by sending it one of the messages defined in the
	 * <code>ModifyListener</code> interface.
	 *
	 * @param listener
	 *            the listener which should be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created
	 *                the receiver</li>
	 *                </ul>
	 *
	 * @see ModifyListener
	 * @see #removeModifyListener
	 */
	public void addModifyListener(ModifyListener listener) {
		checkWidget();
		if (listener == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		this.modifyListener.add(listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will be notified when the
	 * receiver's text is modified.
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
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created
	 *                the receiver</li>
	 *                </ul>
	 *
	 * @see ModifyListener
	 * @see #addModifyListener
	 */
	public void removeModifyListener(ModifyListener listener) {
		checkWidget();
		if (listener == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		this.modifyListener.remove(listener);
	}

	/**
	 * Notifies all of the receiver's listeners when the receiver's text is modified.
	 *
	 * @param eventType
	 *            the type of event which has occurred
	 * @param event
	 *            the event data
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created
	 *                the receiver</li>
	 *                </ul>
	 * 
	 * @see #addModifyListener(ModifyListener)
	 * @see #removeModifyListener(ModifyListener)
	 */
	public void notifyModifyListeners(final ModifyEvent event) {
		checkWidget();
		if (event == null) {
			return;
		}

		if (event.display != null) {
			event.display.asyncExec(new Runnable() {
				@Override
				public void run() {
					doNotifyModifyText(event);
				}
			});
		}
		else {
			// no display in the event, fire the events synchronously
			doNotifyModifyText(event);
		}
	}

	private void doNotifyModifyText(ModifyEvent event) {
		Object[] listeners = this.modifyListener.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			((ModifyListener) listeners[i]).modifyText(event);
		}
	}

	/**
	 * Creates a SWT {@link KeyEvent} out of the given informations.
	 * 
	 * @param keyCode
	 *            The keyCode sent by ckeditor.
	 * @param modifier
	 *            The modifier value sent by ckeditor.
	 * @return The {@link KeyEvent} containing the tranformed key event information.
	 */
	private KeyEvent createKeyEvent(Double keyCode, Double modifier) {
		Event event = new Event();
		event.display = this.getDisplay();
		event.widget = this;
		event.keyCode = keyCode.intValue();

		Double modifierOnly = modifier - keyCode;
		if (modifierOnly != 0) {
			if (modifierOnly.equals(CKEDITOR_ALT + CKEDITOR_CTRL + CKEDITOR_SHIFT)) {
				event.stateMask = SWT.MOD3 | SWT.MOD1 | SWT.MOD2;
			} else if (modifierOnly.equals(CKEDITOR_ALT + CKEDITOR_CTRL)) {
				event.stateMask = SWT.MOD3 | SWT.MOD1;
			} else if (modifierOnly.equals(CKEDITOR_ALT + CKEDITOR_SHIFT)) {
				event.stateMask = SWT.MOD3 | SWT.MOD2;
			} else if (modifierOnly.equals(CKEDITOR_CTRL + CKEDITOR_SHIFT)) {
				event.stateMask = SWT.MOD1 | SWT.MOD2;
			} else if (modifierOnly.equals(CKEDITOR_ALT)) {
				event.stateMask = SWT.MOD3;
			} else if (modifierOnly.equals(CKEDITOR_CTRL)) {
				event.stateMask = SWT.MOD1;
			} else if (modifierOnly.equals(CKEDITOR_SHIFT)) {
				event.stateMask = SWT.MOD2;
			}
		}

		// transform function keys
		switch (event.keyCode) {
			case 33:
				event.keyCode = SWT.PAGE_UP;
				break;
			case 34:
				event.keyCode = SWT.PAGE_DOWN;
				break;
			case 35:
				event.keyCode = SWT.END;
				break;
			case 36:
				event.keyCode = SWT.HOME;
				break;
			case 37:
				event.keyCode = SWT.ARROW_LEFT;
				break;
			case 38:
				event.keyCode = SWT.ARROW_UP;
				break;
			case 39:
				event.keyCode = SWT.ARROW_RIGHT;
				break;
			case 40:
				event.keyCode = SWT.ARROW_DOWN;
				break;
			case 45:
				event.keyCode = SWT.INSERT;
				break;
			case 46:
				event.keyCode = SWT.DEL;
				break;
			case 96:
				event.keyCode = SWT.KEYPAD_0;
				event.character = '0';
				break;
			case 97:
				event.keyCode = SWT.KEYPAD_1;
				event.character = '1';
				break;
			case 98:
				event.keyCode = SWT.KEYPAD_2;
				event.character = '2';
				break;
			case 99:
				event.keyCode = SWT.KEYPAD_3;
				event.character = '3';
				break;
			case 100:
				event.keyCode = SWT.KEYPAD_4;
				event.character = '4';
				break;
			case 101:
				event.keyCode = SWT.KEYPAD_5;
				event.character = '5';
				break;
			case 102:
				event.keyCode = SWT.KEYPAD_6;
				event.character = '6';
				break;
			case 103:
				event.keyCode = SWT.KEYPAD_7;
				event.character = '7';
				break;
			case 104:
				event.keyCode = SWT.KEYPAD_8;
				event.character = '8';
				break;
			case 105:
				event.keyCode = SWT.KEYPAD_9;
				event.character = '9';
				break;
			case 106:
				event.keyCode = SWT.KEYPAD_MULTIPLY;
				event.character = '*';
				break;
			case 107:
				event.keyCode = SWT.KEYPAD_ADD;
				event.character = '+';
				break;
			case 109:
				event.keyCode = SWT.KEYPAD_SUBTRACT;
				event.character = '-';
				break;
			case 110:
				event.keyCode = SWT.KEYPAD_DECIMAL;
				event.character = ',';
				break;
			case 111:
				event.keyCode = SWT.KEYPAD_DIVIDE;
				event.character = '/';
				break;
			case 112:
				event.keyCode = SWT.F1;
				break;
			case 113:
				event.keyCode = SWT.F2;
				break;
			case 114:
				event.keyCode = SWT.F3;
				break;
			case 115:
				event.keyCode = SWT.F4;
				break;
			case 116:
				event.keyCode = SWT.F5;
				break;
			case 117:
				event.keyCode = SWT.F6;
				break;
			case 118:
				event.keyCode = SWT.F7;
				break;
			case 119:
				event.keyCode = SWT.F8;
				break;
			case 120:
				event.keyCode = SWT.F9;
				break;
			case 121:
				event.keyCode = SWT.F10;
				break;
			case 122:
				event.keyCode = SWT.F11;
				break;
			case 123:
				event.keyCode = SWT.F12;
				break;
		}

		// character
		String keyCharString = SWTKeySupport.getKeyFormatterForPlatform().format(event.keyCode);
		if (keyCharString.length() == 1) {
			char keyChar = keyCharString.charAt(0);
			if (Character.isUpperCase(keyChar) && Character.isAlphabetic(keyChar)) {
				if (!((event.stateMask & SWT.MOD2) == SWT.MOD2)) {
					event.character = Character.toLowerCase(keyChar);
				} else {
					event.character = keyChar;
				}
			} else if (Character.isDigit(keyChar) && !((event.stateMask & SWT.MOD2) == SWT.MOD2) && !((event.stateMask & SWT.MOD3) == SWT.MOD3)) {
				event.character = keyChar;
			}
		}

		return new KeyEvent(event);
	}

	private FocusEvent createFocusEvent() {
		Event event = new Event();
		event.display = this.getDisplay();
		event.widget = this;
		return new FocusEvent(event);
	}

	private ModifyEvent createModifyEvent() {
		Event event = new Event();
		event.display = this.getDisplay();
		event.widget = this;
		return new ModifyEvent(event);
	}

	/**
	 * Callback function that is called via Javascript if a change event occurs in the editor part.
	 * Is registered for the Javascript function name <i>textModified</i>.
	 */
	class ModifyFunction extends BrowserFunction {

		public ModifyFunction(Browser browser, String name) {
			super(browser, name);
		}

		@Override
		public Object function(Object[] arguments) {
			notifyModifyListeners(createModifyEvent());
			return super.function(arguments);
		}
	}

	/**
	 * Callback function that is called via Javascript if a keydown event occurs in the editor part.
	 * Is registered for the Javascript function name <i>keyPressed</i>.
	 */
	class KeyPressedFunction extends BrowserFunction {

		public KeyPressedFunction(Browser browser, String name) {
			super(browser, name);
		}

		@Override
		public Object function(Object[] arguments) {
			Double keyCode = (Double) arguments[0];
			Double modifier = (Double) arguments[1];
			notifyKeyPressed(createKeyEvent(keyCode, modifier));
			return super.function(arguments);
		}
	}

	/**
	 * Callback function that is called via Javascript if a keyup event occurs in the editor part.
	 * Is registered for the Javascript function name <i>keyReleased</i>.
	 */
	class KeyReleasedFunction extends BrowserFunction {

		public KeyReleasedFunction(Browser browser, String name) {
			super(browser, name);
		}

		@Override
		public Object function(Object[] arguments) {
			Double keyCode = (Double) arguments[0];
			Double modifier = (Double) arguments[1];
			notifyKeyReleased(createKeyEvent(keyCode, modifier));
			return super.function(arguments);
		}
	}

	/**
	 * Callback function that is called via Javascript if the editor gains focus. Is registered for
	 * the Javascript function name <i>focusIn</i>.
	 */
	class FocusInFunction extends BrowserFunction {

		public FocusInFunction(Browser browser, String name) {
			super(browser, name);
		}

		@Override
		public Object function(Object[] arguments) {
			notifyFocusGained(createFocusEvent());
			return super.function(arguments);
		}
	}

	/**
	 * Callback function that is called via Javascript on blur in the editor part. Is registered for
	 * the Javascript function name <i>focusOut</i>.
	 */
	class FocusOutFunction extends BrowserFunction {

		public FocusOutFunction(Browser browser, String name) {
			super(browser, name);
		}

		@Override
		public Object function(Object[] arguments) {
			notifyFocusLost(createFocusEvent());
			return super.function(arguments);
		}
	}

	private static void locateTemplateURL() {
		templateURL = RichTextEditor.class.getResource("resources/template.html");

		// if we are in an OSGi context, we need to convert the bundle URL to a file URL
		Bundle bundle = FrameworkUtil.getBundle(RichTextEditor.class);
		if (bundle != null) {
			try {
				templateURL = FileLocator.toFileURL(templateURL);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (templateURL.toString().startsWith("jar")) {
			BusyIndicator.showWhile(Display.getDefault(), new Runnable() {

				@Override
				public void run() {
					URL jarURL = RichTextEditor.class.getProtectionDomain().getCodeSource().getLocation();
					File jarFileReference = null;
					if (jarURL.getProtocol().equals("file")) {
						try {
							String decodedPath = URLDecoder.decode(jarURL.getPath(), "UTF-8");
							jarFileReference = new File(decodedPath);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
					else {
						// temporary download of jar file
						// necessary to be able to unzip the resources
						try {
							final Path jar = Files.createTempFile("richtext", ".jar");
							Files.copy(jarURL.openStream(), jar, StandardCopyOption.REPLACE_EXISTING);
							jarFileReference = jar.toFile();

							// delete the temporary file
							Runtime.getRuntime().addShutdownHook(new Thread() {
								@Override
								public void run() {
									try {
										Files.delete(jar);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							});
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					if (jarFileReference != null) {
						try (JarFile jarFile = new JarFile(jarFileReference)) {
							String unpackDirectory = System.getProperty(JAR_UNPACK_LOCATION_PROPERTY);
							// create the directory to unzip to
							final java.nio.file.Path tempDir =
									(unpackDirectory == null)
											? Files.createTempDirectory("richtext") : Files.createDirectories(Paths.get(unpackDirectory));

							// only register the hook to delete the temp directory after shutdown if
							// a temporary directory was used
							if (unpackDirectory == null) {
								Runtime.getRuntime().addShutdownHook(new Thread() {
									@Override
									public void run() {
										try {
											Files.walkFileTree(tempDir, new SimpleFileVisitor<Path>() {
												@Override
												public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
													Files.delete(file);
													return FileVisitResult.CONTINUE;
												}

												@Override
												public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
													Files.delete(dir);
													return FileVisitResult.CONTINUE;
												}

											});
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								});
							}

							Enumeration<JarEntry> entries = jarFile.entries();
							while (entries.hasMoreElements()) {
								JarEntry entry = entries.nextElement();
								String name = entry.getName();
								if (name.startsWith("org/eclipse/nebula/widgets/richtext/resources")) {
									File file = new File(tempDir.toAbsolutePath() + File.separator + name);
									if (!file.exists()) {
										if (entry.isDirectory()) {
											file.mkdirs();
										}
										else {
											try (InputStream is = jarFile.getInputStream(entry);
													OutputStream os = new FileOutputStream(file)) {
												while (is.available() > 0) {
													os.write(is.read());
												}
											}
										}
									}

									// found the template.html in the jar entries, so remember the
									// URL for further usage
									if (name.endsWith("template.html")) {
										templateURL = file.toURI().toURL();
									}
								}
							}

						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
	}
}
