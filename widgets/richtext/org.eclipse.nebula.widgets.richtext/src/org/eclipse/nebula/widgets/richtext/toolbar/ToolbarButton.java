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
package org.eclipse.nebula.widgets.richtext.toolbar;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.nebula.widgets.richtext.RichTextEditor;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * Representation of a toolbar button that should be added to the toolbar of the underlying
 * CKEditor. Can be used to either execute Javascript or Java via callbacks.
 * <p>
 * To execute Javascript in the browser, override {@link #getJavascriptToExecute()} to return the
 * Javascript code as String, that should be interpreted by the browser.
 * </p>
 * <p>
 * To execute Java via callback, override {@link #execute()} and ensure that
 * {@link #getJavascriptToExecute()} returns <code>null</code>.
 * </p>
 * <p>
 * The available toolbars a button can be added to are configured via
 * {@link ToolbarConfiguration#getToolbarGroupConfiguration()}. It is possible to specify the
 * position of the button in the toolbar group via comma separated index, e.g. <i>other,1</i> will
 * place a new button at the first position of the toolbar group with the name <i>other</i>.
 * </p>
 */
public abstract class ToolbarButton {

	private final String buttonName;
	private final String commandName;
	private final String buttonLabel;
	private final String toolbar;
	private final URL iconURL;

	/**
	 * Create a {@link ToolbarButton} with the given information.
	 * 
	 * @param buttonName
	 *            The unique name of the dynamically created CKEditor button.
	 * @param commandName
	 *            The unique name of the dynamically created CKEditor command that is called by
	 *            pressing this button.
	 * @param buttonLabel
	 *            The textual part of the button (if visible) and its tooltip.
	 * @param toolbar
	 *            The toolbar group into which the button will be added. An optional index value
	 *            (separated by a comma) determines the button position within the group.
	 * @param iconURL
	 *            The {@link URL} of the image that should be show as button icon.
	 */
	public ToolbarButton(String buttonName, String commandName, String buttonLabel, String toolbar, URL iconURL) {
		this.buttonName = buttonName;
		this.commandName = commandName;
		this.buttonLabel = buttonLabel;
		this.toolbar = toolbar;

		// if we are in an OSGi context, we need to convert the bundle URL to a file URL
		Bundle bundle = FrameworkUtil.getBundle(RichTextEditor.class);
		if (bundle != null) {
			try {
				iconURL = FileLocator.toFileURL(iconURL);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		this.iconURL = iconURL;
	}

	/**
	 * @return The unique name of the dynamically created CKEditor button.
	 */
	public String getButtonName() {
		return this.buttonName;
	}

	/**
	 * @return The unique name of the dynamically created CKEditor command that is called by
	 *         pressing this button.
	 */
	public String getCommandName() {
		return this.commandName;
	}

	/**
	 * @return The textual part of the button (if visible) and its tooltip.
	 */
	public String getButtonLabel() {
		return this.buttonLabel;
	}

	/**
	 * @return The toolbar group into which the button will be added. An optional index value
	 *         (separated by a comma) determines the button position within the group.
	 */
	public String getToolbar() {
		return this.toolbar;
	}

	/**
	 * @return The {@link URL} of the image that should be show as button icon.
	 */
	public URL getIconURL() {
		return this.iconURL;
	}

	/**
	 * This method can be used to specify Javascript calls that should be executed. If this method
	 * does not return <code>null</code>, the specified Javascript code is evaluated. Otherwise the
	 * Java code specified in {@link #execute()} is executed via Javascript callback.
	 * 
	 * @return The Javascript to execute or <code>null</code> to execute the callback.
	 */
	public String getJavascriptToExecute() {
		return null;
	}

	/**
	 * The code that should be executed via Javascript callback when this button is pressed.
	 * 
	 * @return A possible return value.
	 */
	public Object execute() {
		return null;
	}
}
