/*******************************************************************************
 * Copyright (c) 2011-2019 Laurent CARON
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: 
 * 	Laurent CARON (laurent.caron at gmail dot com) - Initial implementation and API
 *  Stefan Nöbauer - Bug 550437, Bug 550659
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.dialog;

import org.eclipse.nebula.widgets.opal.commons.ResourceManager;
import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Instances of this class are dialog box This component was inspired by the
 * Oxbow Project (http://code.google.com/p/oxbow/) by Eugene Ryzhikov
 */
public class Dialog {

	/**
	 * Types of opal dialog
	 */
	public enum OpalDialogType {
		CLOSE, YES_NO, OK, OK_CANCEL, SELECT_CANCEL, NO_BUTTON, OTHER, NONE
	}

	public enum CenterOption {
		CENTER_ON_SCREEN, CENTER_ON_DIALOG
	}

	private CenterOption centerPolicy = CenterOption.CENTER_ON_SCREEN;

	private String title;
	OpalDialogType buttonType;
	private final MessageArea messageArea;
	private final FooterArea footerArea;
	final Shell shell;

	private int minimumWidth = 400;
	private int minimumHeight = 150;
	
	private Point lastSize;

	/**
	 * Constructor
	 */
	public Dialog() {
		this(null);
	}

	/**
	 * Constructor
	 *
	 * @param resizable if <code>true</code>, the window is resizable
	 */
	public Dialog(final boolean resizable) {
		this(null, resizable);
	}

	/**
	 * Constructor
	 *
	 * @param parent parent shell
	 */
	public Dialog(final Shell parent) {
		this(parent, true);
	}

	/**
	 * Constructor
	 *
	 * @param parent parent shell
	 * @param resizable if <code>true</code>, the window is resizable
	 */
	public Dialog(final Shell parent, final boolean resizable) {
		if (parent == null) {
			shell = new Shell(Display.getCurrent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | (resizable ? SWT.RESIZE : SWT.NONE));
		} else {
			shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | (resizable ? SWT.RESIZE : SWT.NONE));
			if (parent.getImage() != null) {
				shell.setImage(parent.getImage());
			}
		}
		messageArea = new MessageArea(this);
		footerArea = new FooterArea(this);
	}

	/**
	 * Show the dialog box
	 *
	 * @return the index of the selected button
	 */
	public int show() {
		final GridLayout gd = new GridLayout(1, true);
		gd.horizontalSpacing = 0;
		gd.verticalSpacing = 0;
		gd.marginHeight = gd.marginWidth = 0;
		shell.setLayout(gd);

		messageArea.render();
		footerArea.render();
		if (title != null) {
			shell.setText(title);
		}
		pack();
		center();
		
		shell.setMinimumSize(shell.computeSize(minimumWidth, SWT.DEFAULT));
		shell.open();

		final Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		return footerArea.getSelectedButton();
	}

	private void center() {
		final Point preferredSize = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT);

		if (preferredSize.x < minimumWidth) {
			preferredSize.x = minimumWidth;
		}

		if (preferredSize.y < minimumHeight) {
			preferredSize.y = minimumHeight;
		}
		
		final int centerX;
		final int centerY;

		if (centerPolicy == CenterOption.CENTER_ON_SCREEN || shell.getParent() == null) {
			Shell activeShell = shell.getDisplay().getActiveShell();
			if (activeShell == null) {
				activeShell = shell;
			}
			final Rectangle monitorBounds = SWTGraphicUtil.getBoundsOfMonitorOnWhichShellIsDisplayed(activeShell);
			centerX = monitorBounds.x + (monitorBounds.width - preferredSize.x) / 2;
			centerY = monitorBounds.y + (monitorBounds.height - preferredSize.y) / 2;
		} else {
			final Shell parent = (Shell) shell.getParent();
			centerX = parent.getLocation().x + (parent.getSize().x - preferredSize.x) / 2;
			centerY = parent.getLocation().y + (parent.getSize().y - preferredSize.y) / 2;
		}

		shell.setBounds(centerX, centerY, preferredSize.x, preferredSize.y);		
	}

	/**
	 * Close the dialog box
	 */
	public void close() {
		shell.dispose();
	}

	/**
	 * Compute the size of the shell
	 */
	void pack() {

		final Point preferredSize = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Rectangle bounds = shell.getBounds();
		
		preferredSize.x = Math.max(preferredSize.x, minimumWidth);
		preferredSize.y = Math.max(preferredSize.y, minimumHeight);
		

		if(lastSize != null) {
			preferredSize.x = Math.max(preferredSize.x, lastSize.x);
			preferredSize.y = Math.max(preferredSize.y, lastSize.y);
		}

		shell.setBounds(bounds.x, bounds.y, preferredSize.x, preferredSize.y);
		lastSize = null;
	}
	

	// ------------------------------------------- Convenient methods

	/**
	 * Create a dialog box that asks a question
	 *
	 * @param title title of the dialog box
	 * @param text text of the question
	 * @param defaultValue default value of the input
	 * @return the value typed by the user
	 */
	public static String ask(final String title, final String text, final String defaultValue) {
		return ask(null, title, text, defaultValue);
	}

	/**
	 * Create a dialog box that asks a question
	 *
	 * @shell parent shell
	 * @param title title of the dialog box
	 * @param text text of the question
	 * @param defaultValue default value of the input
	 * @return the value typed by the user
	 */
	public static String ask(final Shell shell, final String title, final String text, final String defaultValue) {
		Dialog dialog = buildAskDialog(shell, title, text, defaultValue);
		if (dialog.show() == 0) {
			return dialog.getMessageArea().getTextBoxValue();
		} else {
			return null;
		}
	}
	
	/**
	 * Build a dialog box that asks a question
	 *
	 * @shell parent shell
	 * @param title title of the dialog box
	 * @param text text of the question
	 * @param defaultValue default value of the input
	 * @return dialog
	 */
	public static Dialog buildAskDialog(final Shell shell, final String title, final String text,
			final String defaultValue) {
		final Dialog dialog = new Dialog(shell);
		dialog.setTitle(ResourceManager.getLabel(ResourceManager.INPUT));
		dialog.getMessageArea().setTitle(title).setText(text)
				.setIcon(Display.getCurrent().getSystemImage(SWT.ICON_QUESTION)).addTextBox(defaultValue);
		dialog.setButtonType(OpalDialogType.OK_CANCEL);
		return dialog;
	}

	/**
	 * Create a dialog box that displays an error message
	 *
	 * @param title title of the dialog box
	 * @param errorMessage Error message
	 */
	public static void error(final String title, final String errorMessage) {
		error(null, title, errorMessage);
	}

	/**
	 * Create a dialog box that displays an error message
	 *
	 * @param shell parent shell
	 * @param title title of the dialog box
	 * @param errorMessage Error message
	 */
	public static void error(final Shell shell, final String title, final String errorMessage) {
		buildErrorDialog(shell, title, errorMessage).show();
	}

	/**
	 * Build a dialog box that displays an error message
	 *
	 * @param shell parent shell
	 * @param title title of the dialog box
	 * @param errorMessage Error message
	 * @return dialog
	 */
	public static Dialog buildErrorDialog(final Shell shell, final String title, final String errorMessage) {
		final Dialog dialog = new Dialog(shell);
		dialog.setTitle(ResourceManager.getLabel(ResourceManager.APPLICATION_ERROR));
		dialog.getMessageArea().setTitle(title).//
				setText(errorMessage).//
				setIcon(Display.getCurrent().getSystemImage(SWT.ICON_ERROR));
		dialog.setButtonType(OpalDialogType.OK);
		return dialog;
	}

	/**
	 * Create a dialog box that inform the user
	 *
	 * @param title title of the dialog box
	 * @param text text to display
	 */
	public static void inform(final String title, final String text) {
		inform(null, title, text);
	}

	/**
	 * Create a dialog box that inform the user
	 *
	 * @param shell parent shell
	 * @param title title of the dialog box
	 * @param text text to display
	 */
	public static void inform(final Shell shell, final String title, final String text) {
		buildInformDialog(shell, title, text).show();
	}

	/**
	 * Build a dialog box that inform the user
	 *
	 * @param shell parent shell
	 * @param title title of the dialog box
	 * @param text text to display
	 * @return dialog
	 */
	public static Dialog buildInformDialog(final Shell shell, final String title, final String text) {
		final Dialog dialog = new Dialog(shell);
		dialog.setTitle(ResourceManager.getLabel(ResourceManager.INFORMATION));
		dialog.getMessageArea().setTitle(title).//
				setText(text).setIcon(Display.getCurrent().getSystemImage(SWT.ICON_INFORMATION));
		dialog.setButtonType(OpalDialogType.CLOSE);
		return dialog;
	}

	/**
	 * Create a dialog box that asks the user a confirmation
	 *
	 * @param title title of the dialog box
	 * @param text text to display
	 * @return <code>true</code> if the user confirmed, <code>false</code> otherwise
	 */
	public static boolean isConfirmed(final String title, final String text) {
		return isConfirmed(null, title, text, -1);
	}

	/**
	 * Create a dialog box that asks the user a confirmation
	 *
	 * @param shell parent shell
	 * @param title title of the dialog box
	 * @param text text to display
	 * @return <code>true</code> if the user confirmed, <code>false</code> otherwise
	 */
	public static boolean isConfirmed(final Shell shell, final String title, final String text) {
		return isConfirmed(shell, title, text, -1);
	}

	/**
	 * Create a dialog box that asks the user a confirmation. The button "yes" is
	 * not enabled before timer seconds
	 *
	 * @param title title of the dialog box
	 * @param text text to display
	 * @param timer number of seconds before enabling the yes button
	 * @return <code>true</code> if the user confirmed, <code>false</code> otherwise
	 */
	public static boolean isConfirmed(final String title, final String text, final int timer) {
		return isConfirmed(null, title, text, timer);
	}

	/**
	 * Create a dialog box that asks the user a confirmation. The button "yes" is
	 * not enabled before timer seconds
	 *
	 * @param shell parent shell
	 * @param title title of the dialog box
	 * @param text text to display
	 * @param timer number of seconds before enabling the yes button
	 * @return <code>true</code> if the user confirmed, <code>false</code> otherwise
	 */
	public static boolean isConfirmed(final Shell shell, final String title, final String text, final int timer) {
		return buildConfirmDialog(shell, title, text, timer).show() == 0;
	}

	/**
	 * Build a dialog box that asks the user a confirmation. The button "yes" is
	 * not enabled before timer seconds
	 *
	 * @param shell parent shell
	 * @param title title of the dialog box
	 * @param text text to display
	 * @param timer number of seconds before enabling the yes button
	 * @return dialog
	 */
	public static Dialog buildConfirmDialog(final Shell shell, final String title, final String text, final int timer) {
		final Dialog dialog = new Dialog(shell);
		dialog.setTitle(ResourceManager.getLabel(ResourceManager.WARNING));
		dialog.getMessageArea().setTitle(title).setText(text)
				.setIcon(Display.getCurrent().getSystemImage(SWT.ICON_WARNING));

		dialog.getFooterArea().setTimer(timer).setTimerIndexButton(0);
		dialog.setButtonType(OpalDialogType.YES_NO);
		return dialog;
	}

	/**
	 * Create a dialog box with a radio choice
	 *
	 * @param title title of the dialog box
	 * @param text text to display
	 * @param defaultSelection index of the default selection
	 * @param values values to display
	 * @return the index of the selection
	 */
	public static int radioChoice(final String title, final String text, final int defaultSelection, final String... values) {
		return radioChoice(null, title, text, defaultSelection, values);
	}

	/**
	 * Create a dialog box with a radio choice
	 *
	 * @param shell parent shell
	 * @param title title of the dialog box
	 * @param text text to display
	 * @param defaultSelection index of the default selection
	 * @param values values to display
	 * @return the index of the selection
	 */
	public static int radioChoice(final Shell shell, final String title, final String text, final int defaultSelection,
			final String... values) {
		final Dialog dialog = buildRadioChoiceDialog(shell, title, text, defaultSelection, values);
		if (dialog.show() == 0) {
			return dialog.getMessageArea().getRadioChoice();
		} else {
			return -1;
		}
	}
	
	/**
	 * Build a dialog box with a radio choice
	 *
	 * @param shell parent shell
	 * @param title title of the dialog box
	 * @param text text to display
	 * @param defaultSelection index of the default selection
	 * @param values values to display
	 * @return dialog
	 */
	public static Dialog buildRadioChoiceDialog(final Shell shell, final String title, final String text,
			final int defaultSelection, final String... values) {
		final Dialog dialog = new Dialog(shell);
		dialog.setTitle(ResourceManager.getLabel(ResourceManager.CHOICE));
		dialog.getMessageArea().setTitle(title).setText(text)
				.setIcon(Display.getCurrent().getSystemImage(SWT.ICON_QUESTION))
				.addRadioButtons(defaultSelection, values);
		dialog.setButtonType(OpalDialogType.SELECT_CANCEL);
		return dialog;
	}

	/**
	 * Display a dialog box with an exception
	 *
	 * @param exception exception to display
	 */
	public static void showException(final Throwable exception) {
		buildExceptionDialog(exception).show();
	}

	/**
	 * Build a dialog box with an exception
	 *
	 * @param exception exception to display
	 * @return dialog
	 */
	public static Dialog buildExceptionDialog(final Throwable exception) {
		final Dialog dialog = new Dialog();

		dialog.setTitle(ResourceManager.getLabel(ResourceManager.EXCEPTION));

		final String msg = exception.getMessage();
		final String className = exception.getClass().getName();
		final boolean noMessage = msg == null || msg.trim().length() == 0;

		dialog.getMessageArea().setTitle(noMessage ? className : msg).//
				setText(noMessage ? "" : className).//
				setIcon(Display.getCurrent().getSystemImage(SWT.ICON_ERROR)).//
				setException(exception);

		dialog.getFooterArea().setExpanded(true);

		dialog.setButtonType(OpalDialogType.CLOSE);
		return dialog;
	}

	/**
	 * Create a dialog box with a choice
	 *
	 * @param title title of the dialog box
	 * @param text text to display
	 * @param defaultSelection index of the default selection
	 * @param items items to display
	 * @return the index of the selected value
	 */
	public static int choice(final String title, final String text, final int defaultSelection,
			final ChoiceItem... items) {
		return choice(null, title, text, defaultSelection, items);
	}

	/**
	 * Create a dialog box with a choice
	 *
	 * @param shell parent shell
	 * @param title title of the dialog box
	 * @param text text to display
	 * @param defaultSelection index of the default selection
	 * @param items items to display
	 * @return the index of the selected value
	 */
	public static int choice(final Shell shell, final String title, final String text, final int defaultSelection, final ChoiceItem... items) {
		final Dialog dialog = buildChoiceDialog(shell, title, text, defaultSelection, items);
		dialog.show();
		return dialog.getMessageArea().getChoice();
	}
	
	/**
	 * Build a dialog box with a choice
	 *
	 * @param shell parent shell
	 * @param title title of the dialog box
	 * @param text text to display
	 * @param defaultSelection index of the default selection
	 * @param items items to display
	 * @return dialog
	 */
	public static Dialog buildChoiceDialog(final Shell shell, final String title, final String text,
			final int defaultSelection, final ChoiceItem... items) {
		final Dialog dialog = new Dialog(shell);
		dialog.setTitle(ResourceManager.getLabel(ResourceManager.CHOICE));
		dialog.getMessageArea().setTitle(title).setText(text)
				.setIcon(Display.getCurrent().getSystemImage(SWT.ICON_QUESTION)).addChoice(defaultSelection, items);
		dialog.setButtonType(OpalDialogType.NONE);
		return dialog;
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
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * @return the buttonType
	 */
	public OpalDialogType getButtonType() {
		return buttonType;
	}

	/**
	 * @param buttonType the buttonType to set
	 */
	public void setButtonType(final OpalDialogType buttonType) {
		this.buttonType = buttonType;

		switch (buttonType) {
			case CLOSE:
				footerArea.setButtonLabels(ResourceManager.getLabel(ResourceManager.CLOSE)).setDefaultButtonIndex(0);
				break;
			case NO_BUTTON:
				break;
			case OK:
				footerArea.setButtonLabels(ResourceManager.getLabel(ResourceManager.OK)).setDefaultButtonIndex(0);
				break;
			case OK_CANCEL:
				footerArea.setButtonLabels(ResourceManager.getLabel(ResourceManager.OK), ResourceManager.getLabel(ResourceManager.CANCEL)).setDefaultButtonIndex(-1);
				break;
			case SELECT_CANCEL:
				footerArea.setButtonLabels(ResourceManager.getLabel(ResourceManager.SELECT), ResourceManager.getLabel(ResourceManager.CANCEL)).setDefaultButtonIndex(-1);
				break;
			case YES_NO:
				footerArea.setButtonLabels(ResourceManager.getLabel(ResourceManager.YES), ResourceManager.getLabel(ResourceManager.NO)).setDefaultButtonIndex(0);
				break;
			default:
				break;
		}

	}

	/**
	 * @return the messageArea
	 */
	public MessageArea getMessageArea() {
		return messageArea;
	}

	/**
	 * @return the footerArea
	 */
	public FooterArea getFooterArea() {
		return footerArea;
	}

	/**
	 * @return the shell
	 */
	public Shell getShell() {
		return shell;
	}

	/**
	 * @return the index of the selected button
	 */
	public int getSelectedButton() {
		return getFooterArea().getSelectedButton();
	}

	/**
	 * @return the selection state of the checkbox
	 */
	public boolean getCheckboxValue() {
		return footerArea.getCheckBoxValue();
	}

	/**
	 * @return the minimum width of the dialog box
	 */
	public int getMinimumWidth() {
		return minimumWidth;
	}

	/**
	 * @param minimumWidth the minimum width of the dialog box to set
	 */
	public void setMinimumWidth(final int minimumWidth) {
		this.minimumWidth = minimumWidth;
	}

	/**
	 * @return the minimum height of the dialog box
	 */
	public int getMinimumHeight() {
		return minimumHeight;
	}

	/**
	 * @param minimumHeight the minimum height of the dialog box to set
	 */
	public void setMinimumHeight(final int minimumHeight) {
		this.minimumHeight = minimumHeight;
	}

	/**
	 * @return the center policy (Dialog centered on screen or centered in the
	 *         center of the parent window)
	 */
	public CenterOption getCenterPolicy() {
		return centerPolicy;
	}

	/**
	 * @param centerPolicy center policy
	 */
	public void setCenterPolicy(final CenterOption centerPolicy) {
		this.centerPolicy = centerPolicy;
	}
	
	void setLastSize(Point lastSize) {
		this.lastSize = lastSize;
	}

}
