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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.nebula.widgets.richtext.RichTextEditor;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

/**
 * The toolbar configuration of the CKEditor toolbar. Contains the default
 * toolbar configuration via toolbar groups and gives the ability to dynamically
 * add/remove custom buttons.
 * <p>
 * To customize the CKEditor buttons shown in the toolbar, you need to override
 * {@link #getToolbarGroupConfiguration()} and
 * {@link #getRemoveButtonConfiguration()}
 * </p>
 * <p>
 * <b>Note:</b> A {@link ToolbarConfiguration} instance is directly connected to
 * the {@link Browser} instance of the editor. It can therefore not be re-used
 * for multiple {@link RichTextEditor} instances.
 * </p>
 */
public class ToolbarConfiguration {

	/**
	 * Configure whether to remove the <i>paste text</i> button from the
	 * toolbar. Default is <code>true</code>.
	 */
	public boolean removePasteText = true;
	/**
	 * Configure whether to remove the <i>paste from word</i> button from the
	 * toolbar. Default is <code>true</code>.
	 */
	public boolean removePasteFromWord = true;
	/**
	 * Configure whether to remove the <i>styles</i> combo box from the toolbar.
	 * Default is <code>true</code>.
	 */
	public boolean removeStyles = true;
	/**
	 * Configure whether to remove <i>format</i> combo box from the toolbar.
	 * Default is <code>true</code>.
	 */
	public boolean removeFormat = true;
	/**
	 * Configure if the toolbar should be collapsible. Default is
	 * <code>false</code>.
	 */
	public boolean toolbarCollapsible = false;
	/**
	 * Configure if the toolbar should be initially expanded. Is only
	 * interpreted if {@link #toolbarCollapsible} is set to <code>true</code>.
	 * Default is <code>true</code>.
	 */
	public boolean toolbarInitialExpanded = true;

	private Browser browser;

	private List<ToolbarButton> customButtons = new ArrayList<>();
	private Map<String, BrowserFunction> buttonCallbacks = new HashMap<>();

	private Set<String> removedButtons = new HashSet<>();

	/**
	 * Configures the toolbar of the CKEditor based on the configurations
	 * applied in this {@link ToolbarConfiguration}.
	 */
	public void configureToolbar() {
		browser.evaluate(getToolbarGroupConfiguration() + getRemoveButtonConfiguration() + getCustomButtonConfiguration());
	}

	/**
	 * 
	 * @return The toolbar group configuration for the CKEditor toolbar.
	 */
	protected String getToolbarGroupConfiguration() {
		return "CKEDITOR.config.toolbarGroups = ["
				+ "{ name: 'clipboard', groups: [ 'clipboard', 'undo', 'find' ] },"
				+ "{ name: 'other' },"
				+ "'/',"
				+ "{ name: 'paragraph', groups: [ 'list', 'indent', 'align' ] },"
				+ "{ name: 'colors' },"
				+ "'/',"
				+ "{ name: 'styles' },"
				+ "{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] }"
				+ "] ;";
	}

	/**
	 * 
	 * @return The configuration which default buttons should be removed from
	 *         the toolbar.
	 */
	protected String getRemoveButtonConfiguration() {
		// Subscript and Superscript are not supported styling options for the
		// Rich Text Viewer
		StringBuilder builder = new StringBuilder("CKEDITOR.config.removeButtons = 'Subscript,Superscript");
		if (removePasteText) {
			builder.append(",PasteText");
		}
		if (removePasteFromWord) {
			builder.append(",PasteFromWord");
		}
		if (removeStyles) {
			builder.append(",Styles");
		}
		if (removeFormat) {
			builder.append(",Format");
		}

		for (String removed : this.removedButtons) {
			builder.append(",").append(removed);
		}

		builder.append("';");
		return builder.toString();
	}

	/**
	 * 
	 * @return The configuration for adding custom commands and buttons to the
	 *         toolbar.
	 */
	protected String getCustomButtonConfiguration() {
		StringBuilder builder = new StringBuilder();

		for (ToolbarButton button : this.customButtons) {
			// add the command for the callback
			builder.append("CKEDITOR.instances.editor.addCommand('").append(button.getCommandName()).append("', {");
			builder.append("exec: function(edt) {");
			if (button.getJavascriptToExecute() == null) {
				BrowserFunction function = this.buttonCallbacks.get(button.getCommandName());
				builder.append(function.getName()).append("();");
			}
			else {
				builder.append(button.getJavascriptToExecute());
			}
			builder.append("}});");

			// add the button
			builder.append("CKEDITOR.instances.editor.ui.addButton('").append(button.getButtonName()).append("', {");
			builder.append("label: '").append(button.getButtonLabel()).append("',");
			builder.append("command: '").append(button.getCommandName()).append("',");
			builder.append("toolbar: '").append(button.getToolbar()).append("',");
			builder.append("icon: '").append(button.getIconURL().toString()).append("',");
			builder.append("});");
		}

		return builder.toString();
	}

	/**
	 * Adds a custom button to the CKEditor toolbar. Internally creates an
	 * anonymous {@link BrowserFunction} that executes
	 * {@link ToolbarButton#execute()} via callback on pressing the button.
	 * 
	 * @param button
	 *            The button to add.
	 */
	public void addToolbarButton(final ToolbarButton button) {
		// create the BrowserFunction for the callback
		addToolbarButton(button, new BrowserFunction(browser, button.getCommandName()) {
			@Override
			public Object function(Object[] arguments) {
				return button.execute();
			}
		});
	}

	/**
	 * Adds a custom button to the CKEditor toolbar. Executes the given
	 * {@link BrowserFunction} via callback on pressing the button.
	 * 
	 * @param button
	 *            The button to add.
	 * @param function
	 *            The {@link BrowserFunction} that should be called on pressing
	 *            the button.
	 */
	public void addToolbarButton(ToolbarButton button, BrowserFunction function) {
		if (this.buttonCallbacks.containsKey(button.getCommandName())) {
			// if there is already a BrowserFunction registered for the command
			// name we dispose it for clean resource handling so we can register
			// the new one
			this.buttonCallbacks.get(button.getCommandName()).dispose();
		}
		this.buttonCallbacks.put(button.getCommandName(), function);
		this.customButtons.add(button);
		// ensure that the added button wasn't removed before
		this.removedButtons.remove(button.getButtonName());
	}

	/**
	 * Removes the given {@link ToolbarButton} from the local list of custom
	 * toolbar buttons.
	 * 
	 * @param button
	 *            The {@link ToolbarButton} to remove.
	 */
	public void removeToolbarButton(ToolbarButton button) {
		// remove from local lists so it is not added again on reload
		this.customButtons.remove(button);
		if (this.buttonCallbacks.containsKey(button.getCommandName())) {
			this.buttonCallbacks.get(button.getCommandName()).dispose();
			this.buttonCallbacks.remove(button.getCommandName());
		}
		// remember the button that should be removed
		// I currently don't know a better way to do this
		this.removedButtons.add(button.getButtonName());
	}

	/**
	 * Adds the CKEditor default button for the given name to the toolbar.
	 * <p>
	 * <i>Note: This works only for buttons that have been removed using
	 * {@link #removeDefaultToolbarButton(String)}</i>
	 * </p>
	 * 
	 * @param buttonName
	 *            The name of the CKEditor default button to add.
	 */
	public void addDefaultToolbarButton(String buttonName) {
		this.removedButtons.remove(buttonName);
	}

	/**
	 * Removes the CKEditor default button for the given name from the toolbar.
	 * 
	 * @param buttonName
	 *            The name of the CKEditor default button to remove.
	 */
	public void removeDefaultToolbarButton(String buttonName) {
		// remember the button that should be removed
		this.removedButtons.add(buttonName);
	}

	/**
	 * Dispose the registered {@link BrowserFunction}s.
	 */
	public void dispose() {
		// dispose the registered BrowserFunctions
		for (BrowserFunction function : this.buttonCallbacks.values()) {
			function.dispose();
		}
	}

	/**
	 * @return The {@link Browser} instance to which this
	 *         {@link ToolbarConfiguration} is connected to.
	 */
	public Browser getBrowser() {
		return browser;
	}

	/**
	 * 
	 * @param browser
	 *            The {@link Browser} instance to which this
	 *            {@link ToolbarConfiguration} should be connected to.
	 */
	public void setBrowser(Browser browser) {
		this.browser = browser;
	}

}
