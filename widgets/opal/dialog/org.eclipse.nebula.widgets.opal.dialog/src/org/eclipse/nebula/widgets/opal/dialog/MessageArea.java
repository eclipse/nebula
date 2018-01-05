/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - Initial
 * implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.dialog;

import org.eclipse.nebula.widgets.opal.commons.ReadOnlyStyledText;
import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.nebula.widgets.opal.commons.StringUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;

/**
 * Instances of this class are message areas
 */
public class MessageArea extends DialogArea {
	private static final int DEFAULT_MIN_HEIGHT_FOR_EXCEPTIONS = 300;

	private static final int INDENT_NO_ICON = 8;
	private static final int DEFAULT_MARGIN = 10;

	// Main composite
	private Composite composite;

	// Informations for a simple dialog box
	private String title;
	private Image icon;
	private String text;

	// Informations for a radio choice dialog box
	private int radioChoice;
	private int radioDefaultSelection;
	private String[] radioValues;

	// Informations for a exception viewer dialog box
	private Throwable exception;
	private Text textException;

	// Informations for an input dialog box
	private String textBoxValue;

	// Informations for a choice dialog box
	private int choice;
	private int choiceDefaultSelection;
	private ChoiceItem[] choiceValues;

	// Informations for a progress bar displayed in a dialog box
	private ProgressBar progressBar;
	private int progressBarMinimumValue;
	private int progressBarMaximumValue;
	private int progressBarValue;

	private boolean verticalScrollbar = false;
	private int height = -1;

	private StyledText label;

	/**
	 * Constructor
	 *
	 * @param parent dialog that is composed of this message area
	 */
	public MessageArea(final Dialog parent) {
		super(parent);
		radioChoice = -1;
		choice = -1;
		progressBarValue = -1;
	}

	/**
	 * Add a choice
	 *
	 * @param defaultSelection default selection
	 * @param items a list of the choice item
	 * @return the current message area
	 */
	public MessageArea addChoice(final int defaultSelection, final ChoiceItem... items) {
		setInitialised(true);
		choiceDefaultSelection = defaultSelection;
		choiceValues = items;
		return this;
	}

	/**
	 * Add a choice composed of radio buttons
	 *
	 * @param defaultSelection default selection
	 * @param values values
	 * @return the current message area
	 */
	public MessageArea addRadioButtons(final int defaultSelection, final String... values) {
		setInitialised(true);
		radioDefaultSelection = defaultSelection;
		radioValues = values;
		return this;
	}

	/**
	 * Add a text box for input
	 *
	 * @param value defaut value of the textbox
	 * @return the current message area
	 */
	public MessageArea addTextBox(final String value) {
		setInitialised(true);
		textBoxValue = value;
		return this;
	}

	/**
	 * Add a progress bar
	 *
	 * @param mininum minimum value
	 * @param maximum maximum value
	 * @param value default value
	 * @return the current message area
	 */
	public MessageArea addProgressBar(final int mininum, final int maximum, final int value) {
		setInitialised(true);
		progressBarMinimumValue = mininum;
		progressBarMaximumValue = maximum;
		progressBarValue = value;
		return this;
	}

	/**
	 * @see org.mihalis.opal.OpalDialog.DialogArea#render()
	 */
	@Override
	public void render() {
		if (!isInitialised()) {
			return;
		}

		composite = new Composite(parent.shell, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		composite.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		final boolean hasIcon = icon != null;
		final boolean hasTitle = !StringUtil.isEmpty(title);
		final boolean hasText = !StringUtil.isEmpty(text);
		final boolean hasRadio = radioValues != null;
		final boolean hasException = exception != null;
		final boolean hasTextbox = textBoxValue != null;
		final boolean hasChoice = choiceValues != null;
		final boolean hasProgressBar = progressBarValue != -1;

		final int numberOfColumns = hasIcon ? 2 : 1;
		int numberOfRows = hasTitle && hasText ? 2 : 1;

		if (hasRadio) {
			numberOfRows += radioValues.length;
		}

		if (hasChoice) {
			numberOfRows += choiceValues.length;
		}

		if (hasException || hasTextbox) {
			numberOfRows++;
		}

		if (hasProgressBar) {
			numberOfRows++;
		}

		final GridLayout gridLayout = new GridLayout(numberOfColumns, false);
		gridLayout.marginHeight = gridLayout.marginWidth = 0;
		gridLayout.marginRight = DEFAULT_MARGIN;
		gridLayout.marginLeft = DEFAULT_MARGIN;
		gridLayout.marginTop = DEFAULT_MARGIN;
		gridLayout.marginBottom = DEFAULT_MARGIN;
		composite.setLayout(gridLayout);

		if (hasIcon) {
			createIcon(numberOfRows);
		}

		if (hasTitle) {
			createTitle(hasIcon);
		}

		if (hasText) {
			createText(hasIcon, hasTitle);
		}

		if (hasRadio) {
			createRadioButtons();
		}

		if (hasException) {
			createTextException();
		}

		if (hasTextbox) {
			createTextBox();
		}

		if (hasChoice) {
			createChoice();
		}

		if (hasProgressBar) {
			createProgressBar();
		}

	}

	/**
	 * Create the icon
	 *
	 * @param numberOfRows number of rows displayed
	 */
	private void createIcon(final int numberOfRows) {
		final Label label = new Label(composite, SWT.NONE);
		label.setImage(icon);
		label.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		label.setLayoutData(new GridData(GridData.CENTER, GridData.BEGINNING, false, false, 1, numberOfRows));
	}

	/**
	 * Create the title
	 *
	 * @param hasIcon if <code>true</code> an icon is displayed
	 */
	private void createTitle(final boolean hasIcon) {
		final Label label = new Label(composite, SWT.NONE);
		label.setText(title);
		label.setFont(getBiggerFont());
		label.setForeground(getTitleColor());
		label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		final GridData gd = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1);

		if (hasIcon) {
			gd.horizontalIndent = INDENT_NO_ICON;
		} else {
			gd.horizontalIndent = DEFAULT_MARGIN;
			gd.verticalIndent = DEFAULT_MARGIN;
		}

		label.setLayoutData(gd);
	}

	/**
	 * Create the text
	 *
	 * @param hasIcon if <code>true</code> an icon is displayed
	 * @param hasTitle if <code>true</code> a title is displayed
	 */
	private void createText(final boolean hasIcon, final boolean hasTitle) {

		label = new ReadOnlyStyledText(composite, SWT.NONE | (verticalScrollbar ? SWT.V_SCROLL : SWT.NONE));
		label.setText(text);
		SWTGraphicUtil.applyHTMLFormating(label);
		label.setEditable(false);
		label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		final GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1);
		if (height != -1) {
			gd.heightHint = height;
		}

		if (hasIcon) {
			gd.horizontalIndent = INDENT_NO_ICON;
		} else {
			gd.horizontalIndent = DEFAULT_MARGIN * 2;
			if (hasTitle) {
				gd.verticalIndent = INDENT_NO_ICON;
			} else {
				gd.verticalIndent = DEFAULT_MARGIN * 2;
			}
		}

		label.setLayoutData(gd);
	}

	/**
	 * Create radio buttons
	 */
	private void createRadioButtons() {
		for (int i = 0; i < radioValues.length; i++) {
			final Button button = new Button(composite, SWT.RADIO);
			button.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			button.setText(radioValues[i]);

			final Integer index = Integer.valueOf(i);
			button.addSelectionListener(new SelectionAdapter() {

				/**
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(final SelectionEvent e) {
					if (button.getSelection()) {
						radioChoice = index.intValue();
					}
				}

			});

			button.setSelection(i == radioDefaultSelection);
			final GridData gd = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1);
			gd.horizontalIndent = DEFAULT_MARGIN;
			button.setLayoutData(gd);
		}
	}

	/**
	 * Create the text that displays an exception
	 */
	private void createTextException() {
		textException = new Text(composite, SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		textException.setText(StringUtil.stackStraceAsString(exception));
		textException.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		final GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1);
		gd.minimumHeight = DEFAULT_MIN_HEIGHT_FOR_EXCEPTIONS;
		textException.setLayoutData(gd);
	}

	/**
	 * Create a text box
	 */
	private void createTextBox() {
		final Text textbox = new Text(composite, SWT.BORDER);
		textbox.setText(textBoxValue);
		textbox.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		final GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1);
		textbox.setLayoutData(gd);
		textbox.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				textBoxValue = textbox.getText();
			}
		});

		textbox.addListener(SWT.KeyUp, new Listener() {

			@Override
			public void handleEvent(final Event e) {
				if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					MessageArea.this.parent.shell.dispose();
					MessageArea.this.parent.getFooterArea().selectedButtonIndex = 0;
				}

			}
		});

		textbox.getShell().addListener(SWT.Activate, new Listener() {

			@Override
			public void handleEvent(final Event arg0) {
				textbox.forceFocus();
				textbox.setSelection(textbox.getText().length());
				textbox.getShell().removeListener(SWT.Activate, this);
			}
		});

	}

	/**
	 * Create a choice selection
	 */
	private void createChoice() {
		for (int i = 0; i < choiceValues.length; i++) {
			final ChoiceWidget choice = new ChoiceWidget(composite, SWT.RADIO);
			choice.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			choice.setChoiceItem(choiceValues[i]);

			final Integer index = Integer.valueOf(i);
			choice.addSelectionListener(new SelectionAdapter() {

				/**
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(final SelectionEvent e) {
					MessageArea.this.choice = index.intValue();
					MessageArea.this.parent.shell.dispose();
				}

			});

			choice.setSelection(i == choiceDefaultSelection);
			final GridData gd = new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1);
			choice.setLayoutData(gd);
		}
	}

	/**
	 * Create a progress bar
	 */
	private void createProgressBar() {
		progressBar = new ProgressBar(composite, SWT.SMOOTH | SWT.HORIZONTAL);
		progressBar.setMinimum(progressBarMinimumValue);
		progressBar.setMaximum(progressBarMaximumValue);
		progressBar.setSelection(progressBarValue);
		progressBar.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		final GridData gd = new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1);
		progressBar.setLayoutData(gd);
	}

	/**
	 * Hide the exception panel
	 */
	void hideException() {
		textException.dispose();
		parent.pack();
	}

	/**
	 * Show the exception panel
	 */
	void showException() {
		createTextException();
		parent.pack();
	}

	// ------------------------------------------- Getters & Setters

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 * @return the current message area
	 */
	public MessageArea setTitle(final String title) {
		this.title = title;
		setInitialised(true);
		return this;
	}

	/**
	 * @return the icon
	 */
	public Image getIcon() {
		return icon;
	}

	/**
	 * @param icon the icon to set
	 */
	public MessageArea setIcon(final Image icon) {
		this.icon = icon;
		setInitialised(true);
		return this;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public MessageArea setText(final String text) {
		this.text = text;
		setInitialised(true);
		if (progressBar != null && label != null && !label.isDisposed()) {
			label.setText(text);
			SWTGraphicUtil.applyHTMLFormating(label);
		}
		return this;
	}

	/**
	 * @return the radio choice
	 */
	public int getRadioChoice() {
		return radioChoice;
	}

	/**
	 * @return the exception
	 */
	public Throwable getException() {
		return exception;
	}

	/**
	 * @param exception the exception to set
	 * @return
	 */
	public MessageArea setException(final Throwable exception) {
		this.exception = exception;
		setInitialised(true);
		return this;
	}

	/**
	 * @return the choice
	 */
	public int getChoice() {
		return choice;
	}

	/**
	 * @return the value stored in the text box
	 */
	public String getTextBoxValue() {
		return textBoxValue;
	}

	/**
	 * @return the progress bar minimum value
	 */
	public int getProgressBarMinimumValue() {
		return progressBarMinimumValue;
	}

	/**
	 * @param progressBarMinimumValue the progress bar minimum value to set
	 */
	public void setProgressBarMinimumValue(final int progressBarMinimumValue) {
		this.progressBarMinimumValue = progressBarMinimumValue;
		if (progressBar != null && !progressBar.isDisposed()) {
			progressBar.setMinimum(progressBarMinimumValue);
		}
	}

	/**
	 * @return the progress bar maximum value
	 */
	public int getProgressBarMaximumValue() {
		return progressBarMaximumValue;
	}

	/**
	 * @param progressBarMaximumValue the progress bar minimum value to set
	 */
	public void setProgressBarMaximumValue(final int progressBarMaximumValue) {
		this.progressBarMaximumValue = progressBarMaximumValue;
		if (progressBar != null && !progressBar.isDisposed()) {
			progressBar.setMaximum(progressBarMaximumValue);
		}
	}

	/**
	 * @return the progress bar value
	 */
	public int getProgressBarValue() {
		return progressBarValue;
	}

	/**
	 * @param progressBarValue the progress bar value to set
	 */
	public void setProgressBarValue(final int progressBarValue) {
		this.progressBarValue = progressBarValue;
		if (progressBar != null && !progressBar.isDisposed()) {
			progressBar.setSelection(progressBarValue);
		}
	}

	/**
	 * @return the verticalScrollbar
	 */
	public boolean isVerticalScrollbar() {
		return verticalScrollbar;
	}

	/**
	 * @param verticalScrollbar the verticalScrollbar to set
	 */
	public void setVerticalScrollbar(final boolean verticalScrollbar) {
		this.verticalScrollbar = verticalScrollbar;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(final int height) {
		this.height = height;
	}

}
