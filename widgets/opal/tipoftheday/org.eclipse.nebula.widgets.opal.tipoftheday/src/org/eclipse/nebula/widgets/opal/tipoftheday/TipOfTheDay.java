/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API
 * and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.tipoftheday;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.nebula.widgets.opal.commons.ResourceManager;
import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.nebula.widgets.opal.header.Header;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Instances of this class are a "Tip of Day" box, which is composed of
 * <p>
 * <dl>
 * <dt><b>A tip</b></dt>
 * <dt><b>2 buttons to navigate between types</b></dt>
 * <dt><b>A close button</b></dt>
 * <dt><b>A checkbox "show tip on startup"</b></dt>
 * <dd>(optional)</dd>
 * <dt><b>A checkbox "remember the password"</b></dt>
 * <dd>(optional)</dd>
 * </dl>
 * </p>
 */
public class TipOfTheDay {

	private static final String BLUE_LIGHT_BULB = "images/light1.png";
	private static final String YELLOW_LIGHT_BULB = "images/light2.png";
	private static final String DEFAULT_FONT = "Arial";

	/**
	 * Types of opal dialog
	 */
	public enum TipStyle {
		TWO_COLUMNS, TWO_COLUMNS_LARGE, HEADER
	}

	private final List<String> tips;
	private boolean displayShowOnStartup = true;
	private boolean showOnStartup = true;
	private Shell shell;
	private Button close;
	private int index;
	private Browser tipArea;
	private String fontName;
	private TipStyle style;
	private Image image;

	/**
	 * Constructor
	 */
	public TipOfTheDay() {
		tips = new ArrayList<String>();
		index = -1;
		final Font temp = Display.getDefault().getSystemFont();
		final FontData[] fontData = temp.getFontData();
		if (fontData != null && fontData.length > 0) {
			fontName = fontData[0].getName();
		} else {
			fontName = DEFAULT_FONT;
		}
		style = TipStyle.TWO_COLUMNS;

	}

	/**
	 * Open the "tip of the day" box
	 *
	 * @param parent the parent shell
	 */
	public void open(final Shell parent) {
		if (index == -1) {
			index = new Random().nextInt(tips.size());
		}
		buildShell(parent);
		if (style == TipStyle.HEADER) {
			buildHeader();
		} else {
			buildLeftColumn();
		}
		buildTip();
		buildButtons();
		openShell();
	}

	/**
	 * Build the shell
	 *
	 * @param parent parent shell
	 */
	private void buildShell(final Shell parent) {
		shell = new Shell(parent, SWT.SYSTEM_MODAL | SWT.TITLE | SWT.BORDER | SWT.CLOSE | SWT.RESIZE);
		shell.setText(ResourceManager.getLabel(ResourceManager.TIP_OF_THE_DAY));
		shell.setLayout(new GridLayout(style == TipStyle.HEADER ? 1 : 2, false));

		shell.addListener(SWT.Traverse, event -> {
			switch (event.detail) {
				case SWT.TRAVERSE_ESCAPE:
					shell.dispose();
					event.detail = SWT.TRAVERSE_NONE;
					event.doit = false;
					break;
			}
		});
	}

	/**
	 * Build the header
	 */
	private void buildHeader() {
		final Header header = new Header(shell, SWT.NONE);
		final GridData gd = new GridData(GridData.FILL, GridData.BEGINNING, true, false);
		gd.heightHint = 80;
		header.setLayoutData(gd);
		header.setTitle(ResourceManager.getLabel(ResourceManager.DID_YOU_KNOW));
		if (image == null) {
			final Image img = SWTGraphicUtil.createImageFromFile(YELLOW_LIGHT_BULB);
			header.setImage(img);
			SWTGraphicUtil.addDisposer(shell, img);
		} else {
			header.setImage(image);
		}
	}

	/**
	 * Build the left column
	 */
	private void buildLeftColumn() {
		final Composite composite = new Composite(shell, SWT.NONE);
		int numberOfRows = 1;
		if (style == TipStyle.TWO_COLUMNS_LARGE) {
			numberOfRows = displayShowOnStartup ? 5 : 4;
		}

		final GridData gd = new GridData(GridData.FILL, GridData.BEGINNING, false, true, 1, numberOfRows);
		composite.setLayoutData(gd);
		final FillLayout compositeLayout = new FillLayout();
		compositeLayout.marginWidth = 2;
		composite.setLayout(compositeLayout);
		final Label label = new Label(composite, SWT.NONE);
		if (image == null) {
			final Image img = SWTGraphicUtil.createImageFromFile(BLUE_LIGHT_BULB);
			label.setImage(img);
			SWTGraphicUtil.addDisposer(shell, img);
		} else {
			label.setImage(image);
		}
	}

	/**
	 * Build the tip area
	 */
	private void buildTip() {
		if (style == TipStyle.TWO_COLUMNS) {
			final Group group = new Group(shell, SWT.NONE);
			final GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true);
			gd.widthHint = 300;
			gd.heightHint = 120;
			group.setLayoutData(gd);
			group.setText(ResourceManager.getLabel(ResourceManager.DID_YOU_KNOW));
			final FillLayout fillLayout = new FillLayout();
			fillLayout.marginWidth = 15;
			group.setLayout(fillLayout);

			tipArea = new Browser(group, SWT.BORDER);
		} else if (style == TipStyle.TWO_COLUMNS_LARGE) {
			final Label title = new Label(shell, SWT.NONE);
			final GridData gd = new GridData(GridData.FILL, GridData.FILL, true, false);
			gd.verticalIndent = 15;
			title.setLayoutData(gd);
			final Font tempFont = SWTGraphicUtil.buildFontFrom(title, SWT.BOLD, 16);
			title.setText(ResourceManager.getLabel(ResourceManager.TIP_OF_THE_DAY));
			title.setFont(tempFont);
			SWTGraphicUtil.addDisposer(shell, tempFont);

			final Label separator = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
			separator.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

			tipArea = new Browser(shell, SWT.BORDER);
			final GridData gdTipArea = new GridData(GridData.FILL, GridData.FILL, true, true);
			gdTipArea.heightHint = 120;
			tipArea.setLayoutData(gdTipArea);

		} else {
			tipArea = new Browser(shell, SWT.BORDER);
			final GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true);
			gd.heightHint = 120;
			tipArea.setLayoutData(gd);
		}
		fillTipArea();

	}

	/**
	 * Fill the tip area with the selected tip
	 */
	private void fillTipArea() {
		tipArea.setText("<html><body bgcolor=\"#ffffff\" text=\"#000000\"><p style=\"font-family:" + //
				fontName + //
				";font-size=12px\">" + //
				tips.get(index) + //
				"</p></body></html>");
	}

	/**
	 * Build the button (checkbox, previous tip, next tip, close)
	 */
	private void buildButtons() {
		final Composite composite = new Composite(shell, SWT.NONE);
		int numberOfColumns;
		if (style == TipStyle.HEADER) {
			numberOfColumns = 1;
		} else if (style == TipStyle.TWO_COLUMNS) {
			numberOfColumns = 2;
		} else {
			numberOfColumns = 1;
		}

		final GridData gd = new GridData(GridData.FILL, GridData.BEGINNING, false, false, numberOfColumns, 1);
		composite.setLayoutData(gd);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 2;
		if (style == TipStyle.TWO_COLUMNS_LARGE) {
			composite.setLayout(new GridLayout(3, false));
		} else {
			composite.setLayout(new GridLayout(displayShowOnStartup ? 4 : 3, false));
		}

		final GridData gridShowOnStartup, gridPrevious, gridNext, gridClose;
		if (style == TipStyle.TWO_COLUMNS_LARGE) {
			gridShowOnStartup = new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false, 3, 1);

			gridPrevious = new GridData(GridData.END, GridData.CENTER, true, false);
			gridPrevious.widthHint = 120;

			gridNext = new GridData(GridData.CENTER, GridData.CENTER, false, false);
			gridNext.widthHint = 120;

			gridClose = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
			gridClose.widthHint = 120;

		} else {
			gridShowOnStartup = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);

			gridPrevious = new GridData(GridData.END, GridData.CENTER, showOnStartup ? false : true, false);
			gridPrevious.widthHint = 120;

			gridNext = new GridData(GridData.FILL, GridData.CENTER, false, false);
			gridNext.widthHint = 120;

			gridClose = new GridData(GridData.FILL, GridData.CENTER, false, false);
			gridClose.widthHint = 120;
		}

		if (displayShowOnStartup) {
			buildShowOnStartup(composite, gridShowOnStartup);
		}

		buildPreviousButton(composite, gridPrevious);
		buildNextButton(composite, gridNext);
		buildCloseButton(composite, gridClose);

	}

	/**
	 * Build the "show on startup" checkbox
	 *
	 * @param composite parent composite
	 * @param gridData associated grid data
	 */
	private void buildShowOnStartup(final Composite composite, final GridData gridData) {
		final Button checkBox = new Button(composite, SWT.CHECK);
		checkBox.setLayoutData(gridData);
		checkBox.setText(ResourceManager.getLabel(ResourceManager.SHOW_TIP_AT_STARTUP));
		checkBox.setSelection(showOnStartup);
		checkBox.addListener(SWT.Selection, event -> {
			showOnStartup = checkBox.getSelection();
		});

	}

	/**
	 * Build the "previous tip" button
	 *
	 * @param composite parent composite
	 * @param gridData associated grid data
	 */
	private void buildPreviousButton(final Composite composite, final GridData gridData) {
		final Button previous = new Button(composite, SWT.PUSH);
		previous.setText(ResourceManager.getLabel(ResourceManager.PREVIOUS_TIP));
		previous.setLayoutData(gridData);
		previous.addListener(SWT.Selection, event -> {
			if (index == 0) {
				setIndex(tips.size() - 1);
			} else {
				setIndex(index - 1);
			}
		});

	}

	/**
	 * Build the "next tip" button
	 *
	 * @param composite parent composite
	 * @param gridData associated grid data
	 */
	private void buildNextButton(final Composite composite, final GridData gridData) {
		final Button next = new Button(composite, SWT.PUSH);
		next.setText(ResourceManager.getLabel(ResourceManager.NEXT_TIP));

		next.setLayoutData(gridData);
		next.addListener(SWT.Selection, event -> {
			if (index == tips.size() - 1) {
				setIndex(0);
			} else {
				setIndex(index + 1);
			}

		});
	}

	/**
	 * Build the "close" button
	 *
	 * @param composite parent composite
	 * @param gridData associated grid data
	 */
	private void buildCloseButton(final Composite composite, final GridData gridData) {
		close = new Button(composite, SWT.PUSH);
		close.setText(ResourceManager.getLabel(ResourceManager.CLOSE));

		close.setLayoutData(gridData);
		close.addListener(SWT.Selection, event -> {
			shell.dispose();
		});
	}

	/**
	 * Open the shell
	 */
	private void openShell() {
		shell.setDefaultButton(close);
		shell.pack();
		shell.open();
		SWTGraphicUtil.centerShell(shell);

		while (!shell.isDisposed()) {
			if (!shell.getDisplay().readAndDispatch()) {
				shell.getDisplay().sleep();
			}
		}

	}

	/**
	 * Add a tip
	 *
	 * @param tip tip to add
	 * @return the current object
	 */
	public TipOfTheDay addTip(final String tip) {
		tips.add(tip);
		return this;
	}

	/**
	 * @return the image
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * @return the index of the current tip
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return the style of the window
	 */
	public TipStyle getStyle() {
		return style;
	}

	/**
	 * @return all the the tips
	 */
	public List<String> getTips() {
		return tips;
	}

	/**
	 * @return if <code>true</code>, the "Show On Startup" checkbox is displayed
	 */
	public boolean isDisplayShowOnStartup() {
		return displayShowOnStartup;
	}

	/**
	 * @return the value of the checkbox "Show On Startup"
	 */
	public boolean isShowOnStartup() {
		return showOnStartup;
	}

	/**
	 * @param if <code>true</code>, the checkbox "Show on startup" is displayed
	 */
	public void setDisplayShowOnStartup(final boolean displayShowOnStartup) {
		this.displayShowOnStartup = displayShowOnStartup;
	}

	/**
	 * @param index the index of the selected tip. By default, the tip is chosen
	 *            randomly
	 */
	public void setIndex(final int index) {
		if (index < 0 || index >= tips.size() || tips.get(index) == null) {
			throw new IllegalArgumentException("Index should be between 0 and " + (tips.size() - 1) + " (entered value:" + index + ")");
		}

		this.index = index;
		if (tipArea != null && !tipArea.isDisposed()) {
			fillTipArea();
		}
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(final Image image) {
		this.image = image;
	}

	/**
	 * @param the value of the checkbox "Show on startup"
	 */
	public void setShowOnStartup(final boolean showOnStartup) {
		this.showOnStartup = showOnStartup;
	}

	/**
	 * @param style the style of the window
	 */
	public void setStyle(final TipStyle style) {
		this.style = style;
	}

	/**
	 * @return the shell that contains the "tip of the day" box
	 */
	public Shell getShell() {
		return shell;
	}

}
