/*****************************************************************************
 * Copyright (c) 2016, 2022 Dirk Fauth.
 *
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *		Dirk Fauth <dirk.fauth@googlemail.com> - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.nebula.widgets.richtext;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.nebula.widgets.richtext.painter.ResourceHelper;
import org.eclipse.nebula.widgets.richtext.toolbar.ToolbarButton;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * Configuration class that is used for general configurations of the CKEditor instance.
 * <p>
 * <b>Note:</b> This configuration class replaces the {@link org.eclipse.nebula.widgets.richtext.toolbar.ToolbarConfiguration}.
 * </p>
 *
 * @since 1.1
 */
public class RichTextEditorConfiguration {

	/**
	 * Key for the default language configuration.
	 */
	public static final String DEFAULT_LANGUAGE = "defaultLanguage";
	/**
	 * Key for the language configuration.
	 */
	public static final String LANGUAGE = "language";
	/**
	 * Key for toolbar groups configuration.
	 */
	public static final String TOOLBAR_GROUPS = "toolbarGroups";
	/**
	 * Key for toolbar buttons that should not be rendered.
	 */
	public static final String REMOVE_BUTTONS = "removeButtons";
	/**
	 * Key to configure whether the toolbar can be collapsed by the user.
	 */
	public static final String TOOLBAR_CAN_COLLAPSE = "toolbarCanCollapse";
	/**
	 * Key to configure whether the toolbar must start expanded when the editor is loaded.
	 */
	public static final String TOOLBAR_STARTUP_EXPANDED = "toolbarStartupExpanded";
	/**
	 * Key to configure a list of plugins that must not be loaded.
	 */
	public static final String REMOVE_PLUGINS = "removePlugins";
	/**
	 * Key to configure whether to enable the resizing feature.
	 */
	public static final String RESIZE_ENABLED = "resize_enabled";
	/**
	 * Key to configure the dimensions for which the editor resizing is enabled. Possible values are
	 * <i>both</i>, <i>vertical</i>, and <i>horizontal</i>.
	 */
	public static final String RESIZE_DIR = "resize_dir";
	/**
	 * Key to configure the minimum editor width, in pixels, when resizing the editor interface by
	 * using the resize handle..
	 */
	public static final String RESIZE_MINWIDTH = "resize_minWidth";
	/**
	 * Key to configure the minimum editor height, in pixels, when resizing the editor interface by
	 * using the resize handle..
	 */
	public static final String RESIZE_MINHEIGHT = "resize_minHeight";

	/**
	 * Collection of languages that are supported by CKEditor.
	 */
	public static final Collection<String> SUPPORTED_LANGUAGES = new HashSet<>();
	private static URL resourceURL;
	static {
		resourceURL = RichTextEditorConfiguration.class.getClassLoader().getResource("org/eclipse/nebula/widgets/richtext/resources/ckeditor/lang");

		// if we are in an OSGi context, we need to convert the bundle URL to a file URL
		Bundle bundle = FrameworkUtil.getBundle(RichTextEditor.class);
		if (bundle != null) {
			try {
				resourceURL = FileLocator.toFileURL(resourceURL);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (resourceURL.toString().startsWith("jar")) {
			BusyIndicator.showWhile(Display.getDefault(), new Runnable() {

				@Override
				public void run() {
					resourceURL = ResourceHelper.getRichTextResource("ckeditor/lang");
				}
			});
		}

		File directory = new File(resourceURL.getFile());
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				SUPPORTED_LANGUAGES.add(file.getName().substring(0, file.getName().indexOf('.')));
			}
		}
	}
	
	/**
	 * Configure whether to remove the <i>paste text</i> button from the
	 * toolbar. Default is <code>true</code>.
	 */
	private boolean removePasteText = true;
	/**
	 * Configure whether to remove the <i>paste from word</i> button from the
	 * toolbar. Default is <code>true</code>.
	 */
	private boolean removePasteFromWord = true;
	/**
	 * Configure whether to remove the <i>styles</i> combo box from the toolbar.
	 * Default is <code>true</code>.
	 */
	private boolean removeStyles = true;
	/**
	 * Configure whether to remove <i>format</i> combo box from the toolbar.
	 * Default is <code>true</code>.
	 */
	private boolean removeFormat = true;

	/**
	 * Configure if the IE should auto format URLs or not.
	 */
	private boolean autoUrlFormattingDisabled = false;

	private Set<String> removedButtons = new HashSet<>();

	private Browser browser;

	private Set<ToolbarButton> customButtons = new LinkedHashSet<>();
	private Map<String, BrowserFunction> buttonCallbacks = new HashMap<>();

	private Map<String, Object> options = new HashMap<>();

	/**
	 * Creates a new instance for general configurations that are added to the created CKEditor
	 * instance at initialization.
	 */
	public RichTextEditorConfiguration() {
		this.options.put(DEFAULT_LANGUAGE, Locale.ENGLISH.getLanguage());
		this.options.put(LANGUAGE, getSupportedLanguage(Locale.getDefault()));
		// remove the bottom bar that shows the applied tags
		this.options.put(REMOVE_PLUGINS, "elementspath");
		// disable the ability to manually resize the editor
		this.options.put(RESIZE_ENABLED, Boolean.FALSE);

		// only show toolbar buttons for features that are supported by the RichTextPainter
		this.options.put(TOOLBAR_GROUPS, "["
				+ "{ name: 'clipboard', groups: [ 'clipboard', 'undo', 'find' ] },"
				+ "{ name: 'other' },"
				+ "'/',"
				+ "{ name: 'paragraph', groups: [ 'list', 'indent', 'align' ] },"
				+ "{ name: 'colors' },"
				+ "'/',"
				+ "{ name: 'styles' },"
				+ "{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] }"
				+ "]");
		this.options.put(REMOVE_BUTTONS, getRemoveButtonConfiguration());
	}

	/**
	 * Creates a {@link RichTextEditorConfiguration} that is initialized with the configuration
	 * values out of the given
	 * {@link org.eclipse.nebula.widgets.richtext.toolbar.ToolbarConfiguration}. This constructor is
	 * used for backwards compatibility only in case adopters use the old ToolbarConfiguration.
	 * Therefore it is deprecated from the beginning.
	 * 
	 * @param config
	 *            The {@link org.eclipse.nebula.widgets.richtext.toolbar.ToolbarConfiguration} that
	 *            should be used to initialized the {@link RichTextEditorConfiguration}.
	 * @deprecated Use the no-arg constructor instead and set the values directly to the created
	 *             {@link RichTextEditorConfiguration}
	 * @since 1.2
	 */
	@Deprecated
	public RichTextEditorConfiguration(org.eclipse.nebula.widgets.richtext.toolbar.ToolbarConfiguration config) {
		this();

		this.removePasteText = config.removePasteText;
		this.removePasteFromWord = config.removePasteFromWord;
		this.removeStyles = config.removeStyles;
		this.removeFormat = config.removeFormat;
		this.removedButtons.addAll(config.getRemovedButtons());

		setToolbarCollapsible(config.toolbarCollapsible);
		setToolbarInitialExpanded(config.toolbarInitialExpanded);

		String[] toolbarButtonConfigurations = config.getToolbarButtonConfigurations();

		// set the option like this in case the method itself was overridden by subclassing
		String tbc = toolbarButtonConfigurations[0];
		tbc = tbc.substring(tbc.indexOf("=")+1, tbc.length()-1);
		this.options.put(TOOLBAR_GROUPS, tbc);

		// set the option like this in case the method itself was overridden by subclassing
		String rbc = toolbarButtonConfigurations[1];
		rbc = rbc.substring(rbc.indexOf("'")+1, rbc.lastIndexOf("'"));
		this.options.put(REMOVE_BUTTONS, rbc.trim());

		this.customButtons.addAll(config.getCustomButtons());
		this.buttonCallbacks.putAll(config.getButtonCallbacks());
	}

	/**
	 * Adds a new option to the configuration.
	 *
	 * @param key
	 *            The configuration option key.
	 * @param value
	 *            The configuration option value.
	 *
	 * @see <a href="http://docs.ckeditor.com/#!/api/CKEDITOR.config">CKEDITOR.config</a>
	 */
	public void setOption(String key, Object value) {
		this.options.put(key, value);
	}

	/**
	 * Returns a configuration option set in this {@link RichTextEditorConfiguration}.
	 *
	 * @param key
	 *            The configuration option key for which the value is requested.
	 * @return The configuration option value for the given key or <code>null</code> in case there
	 *         is nothing configured for that key.
	 */
	public Object getOption(String key) {
		return this.options.get(key);
	}

	/**
	 * @return An unmodifiable map that contains all configuration option values.
	 */
	public Map<String, Object> getAllOptions() {
		return Collections.unmodifiableMap(this.options);
	}

	// convenience methods

	/**
	 * This method is used to get the language String that is supported by CKEditor. There are only
	 * few languages that support the country code information, e.g. Portuguese Brasil, and for
	 * those the special language code needs to be found. Otherwise the language code only is
	 * supported.
	 * 
	 * @param locale
	 *            The locale for which the language is requested.
	 * @return The supported language code with country information if supported, or an empty
	 *         String.
	 */
	private String getSupportedLanguage(Locale locale) {
		if (SUPPORTED_LANGUAGES.isEmpty()) {
			// the supported languages could not be determined therefore we simply use the language
			// code of the given Locale
			return locale.getLanguage();
		}
		
		String localeString = locale.getLanguage();
		if (!locale.getCountry().isEmpty()) {
			localeString += "-" + locale.getCountry().toLowerCase();
		}
		if (SUPPORTED_LANGUAGES.contains(localeString)) {
			return localeString;
		} else if (SUPPORTED_LANGUAGES.contains(locale.getLanguage())) {
			return locale.getLanguage();
		}
		return "";
	}
	
	/**
	 * @param lang
	 *            The user interface language localization to use. If left empty, the editor will
	 *            automatically be localized to the user language. If the user language is not
	 *            supported, the language specified in the <i>defaultLanguage</i> configuration
	 *            setting is used.
	 */
	public void setLanguage(String lang) {
		this.options.put(LANGUAGE, lang);
	}

	/**
	 * @param locale
	 *            The user interface language localization to use. If left empty, the editor will
	 *            automatically be localized to the user language. If the user language is not
	 *            supported, the language specified in the <i>defaultLanguage</i> configuration
	 *            setting is used.
	 */
	public void setLanguage(Locale locale) {
		setLanguage(getSupportedLanguage(locale));
	}

	/**
	 * @param lang
	 *            The language to be used if the language setting is left empty and it is not
	 *            possible to localize the editor to the user language.
	 */
	public void setDefaultLanguage(String lang) {
		this.options.put(DEFAULT_LANGUAGE, lang);
	}

	/**
	 * @param locale
	 *            The language to be used if the language setting is left empty and it is not
	 *            possible to localize the editor to the user language.
	 */
	public void setDefaultLanguage(Locale locale) {
		String language = getSupportedLanguage(locale);
		if (language.isEmpty()) {
			// always fall back to English in case the given locale is not supported
			language = "en";
		}
		setDefaultLanguage(language);
	}

	/**
	 * Whether to enable the resizing feature. If this feature is disabled, the resize handle will
	 * not be visible.
	 *
	 * @param resizable
	 *            <code>true</code> to enable the resizing feature.
	 */
	public void setResizable(boolean resizable) {
		this.options.put(RESIZE_ENABLED, resizable);
	}

	/**
	 * The minimum editor size, in pixels, when resizing the editor interface by using the resize
	 * handle. Note: It falls back to editor's actual height if it is smaller than the default
	 * value.
	 *
	 * @param minWidth
	 *            the minimum editor width, in pixels
	 * @param minHeight
	 *            the minimum editor height, in pixels
	 */
	public void setMinSize(int minWidth, int minHeight) {
		this.options.put(RESIZE_MINWIDTH, minWidth);
		this.options.put(RESIZE_MINHEIGHT, minHeight);
	}

	/**
	 * @param direction
	 *            The dimensions for which the editor resizing is enabled. Possible values are
	 *            <code>both</code>, <code>vertical</code>, and <code>horizontal</code>.
	 */
	public void setResizeDirection(String direction) {
		this.options.put(RESIZE_DIR, direction);
	}

	/**
	 * Configure if the toolbar should be collapsible. Default is <code>false</code>.
	 *
	 * @param toolbarCollapsible
	 *            <code>true</code> if the toolbar should be collapsible, <code>false</code> if not.
	 */
	public void setToolbarCollapsible(boolean toolbarCollapsible) {
		this.options.put(TOOLBAR_CAN_COLLAPSE, toolbarCollapsible);
	}

	/**
	 * Configure if the toolbar should be initially expanded. Is only interpreted if
	 * {@link #toolbarCollapsible} is set to <code>true</code>. Default is <code>true</code>.
	 *
	 * @param toolbarInitialExpanded
	 *            <code>true</code> if the toolbar should be initially expanded, <code>false</code>
	 *            if not.
	 */
	public void setToolbarInitialExpanded(boolean toolbarInitialExpanded) {
		this.options.put(TOOLBAR_STARTUP_EXPANDED, toolbarInitialExpanded);
	}

	/**
	 *
	 * @param removePasteText
	 *            <code>true</code> to remove the <i>paste text</i> button from the toolbar.
	 */
	public void setRemovePasteText(boolean removePasteText) {
		this.removePasteText = removePasteText;
		this.options.put(REMOVE_BUTTONS, getRemoveButtonConfiguration());
	}

	/**
	 *
	 * @param removePasteFromWord
	 *            <code>true</code> to remove the <i>paste from word</i> button from the toolbar.
	 */
	public void setRemovePasteFromWord(boolean removePasteFromWord) {
		this.removePasteFromWord = removePasteFromWord;
		this.options.put(REMOVE_BUTTONS, getRemoveButtonConfiguration());
	}

	/**
	 *
	 * @param removeStyles
	 *            <code>true</code> to remove the <i>styles</i> combo box from the toolbar.
	 */
	public void setRemoveStyles(boolean removeStyles) {
		this.removeStyles = removeStyles;
		this.options.put(REMOVE_BUTTONS, getRemoveButtonConfiguration());
	}

	/**
	 *
	 * @param removeFormat
	 *            <code>true</code> to remove <i>format</i> combo box from the toolbar.
	 */
	public void setRemoveFormat(boolean removeFormat) {
		this.removeFormat = removeFormat;
		this.options.put(REMOVE_BUTTONS, getRemoveButtonConfiguration());
	}

	/**
	 * Adds the CKEditor default button for the given name to the toolbar.
	 * <p>
	 * <i>Note: This works only for buttons that have been removed using
	 * {@link #removeDefaultToolbarButton(String[])}</i>
	 * </p>
	 *
	 * @param buttonNames
	 *            The names of the CKEditor default button to add.
	 */
	public void addDefaultToolbarButton(String... buttonNames) {
		for (String buttonName : buttonNames) {
			this.removedButtons.remove(buttonName);
		}
		this.options.put(REMOVE_BUTTONS, getRemoveButtonConfiguration());
	}

	/**
	 * Removes the CKEditor default button for the given name from the toolbar.
	 *
	 * @param buttonNames
	 *            The names of the CKEditor default button to remove.
	 */
	public void removeDefaultToolbarButton(String... buttonNames) {
		// remember the button that should be removed
		for (String buttonName : buttonNames) {
			this.removedButtons.add(buttonName);
		}
		this.options.put(REMOVE_BUTTONS, getRemoveButtonConfiguration());
	}

	/**
	 *
	 * @return The configuration which default buttons should be removed from
	 *         the toolbar.
	 */
	private String getRemoveButtonConfiguration() {
		// Subscript and Superscript are not supported styling options for the
		// Rich Text Viewer
		StringBuilder builder = new StringBuilder("'Subscript,Superscript");
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

		builder.append("'");
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
				builder.append("javaExecutionStarted();");
				BrowserFunction function = this.buttonCallbacks.get(button.getCommandName());
				builder.append(function.getName()).append("();");
				builder.append("javaExecutionFinished()");
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
			if (button.getIconURL() != null) {
				builder.append("icon: '").append(button.getIconURL().toString()).append("',");
			}
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
		if (this.browser != null) {
			// create the BrowserFunction for the callback
			addToolbarButton(button, new BrowserFunction(browser, button.getCommandName()) {
				@Override
				public Object function(Object[] arguments) {
					return button.execute();
				}
			});
		} else if (!this.customButtons.contains(button)) {
			this.customButtons.add(button);
		}
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
		if (!this.customButtons.contains(button)) {
			this.customButtons.add(button);
		}
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
	 * Adds custom buttons to the toolbar of the CKEditor based on the configurations applied in
	 * this {@link RichTextEditorConfiguration}.
	 */
	public void customizeToolbar() {
		browser.evaluate(getCustomButtonConfiguration());
	}

	/**
	 * @return The {@link Browser} instance to which this
	 *         {@link RichTextEditorConfiguration} is connected to.
	 */
	public Browser getBrowser() {
		return browser;
	}

	/**
	 *
	 * @param browser
	 *            The {@link Browser} instance to which this
	 *            {@link RichTextEditorConfiguration} should be connected to.
	 */
	public void setBrowser(Browser browser) {
		this.browser = browser;

		// if a browser is set we ensure that the registered custom buttons
		// are registered and already registered BrowserFunctions are disposed
		for (ToolbarButton button : this.customButtons) {
			addToolbarButton(button);
		}
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
	 * 
	 * @return <code>true</code> if the auto-formatting of URLs in IE is disabled,
	 *         <code>false</code> if not.
	 *
	 * @since 1.5
	 */
	public boolean isAutoUrlFormattingDisabled() {
		return this.autoUrlFormattingDisabled;
	}

	/**
	 * Configure if the auto-formatting of URLs should be disabled on Windows systems with an
	 * Internet Explorer as system browser.
	 * 
	 * @param autoUrlFormattingDisabled
	 *            <code>true</code> if the auto-formatting of URLs in IE should be disabled,
	 *            <code>false</code> if not.
	 * 
	 * @since 1.5
	 */
	public void setAutoUrlFormattingDisabled(boolean autoUrlFormattingDisabled) {
		this.autoUrlFormattingDisabled = autoUrlFormattingDisabled;
	}
}
