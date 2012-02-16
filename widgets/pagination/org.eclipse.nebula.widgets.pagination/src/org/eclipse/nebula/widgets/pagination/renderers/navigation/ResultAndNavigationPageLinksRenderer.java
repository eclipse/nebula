/*******************************************************************************
 * Copyright (C) 2011 Angelo Zerr <angelo.zerr@gmail.com>, Pascal Leclercq <pascal.leclercq@gmail.com>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo ZERR - initial API and implementation
 *     Pascal Leclercq - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.pagination.renderers.navigation;

import java.util.Locale;

import org.eclipse.nebula.widgets.pagination.AbstractPageControllerComposite;
import org.eclipse.nebula.widgets.pagination.PageableController;
import org.eclipse.nebula.widgets.pagination.PaginationHelper;
import org.eclipse.nebula.widgets.pagination.Resources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

/**
 * This SWT {@link Composite} display :
 * 
 * <ul>
 * <li>on the left region the result page.</li>
 * <li>on the right region the page links navigation by using SWT {@link Link}.</li>
 * </ul>
 * 
 * <p>
 * Example :
 * 
 * <pre>
 * 	Results 1-5 of 10          Previous 1 2 ...10 Next
 * </pre>
 * 
 * </p>
 * 
 */
public class ResultAndNavigationPageLinksRenderer extends
		AbstractPageControllerComposite implements SelectionListener {

	private static final RGB RED_COLOR = new RGB(255, 0, 0);

	private static final String END_HREF = "</a>";
	private static final String OPEN_START_HREF = "<a href=\"";
	private static final String OPEN_END_HREF = "\" >";

	/** the result label **/
	private Label resultLabel;
	/** the previous link **/
	private Link previousLink;
	/** the next link **/
	private Link nextLink;
	/** the page index links **/
	private Link pageLinks;

	/**
	 * Constructs a new instance of this class given its parent and a style
	 * value describing its behavior and appearance. Here the pagination
	 * controller is filled.
	 * 
	 * @param parent
	 *            a widget which will be the parent of the new instance (cannot
	 *            be null)
	 * @param style
	 *            the style of widget to construct
	 * @param controller
	 *            the pagination controller to observe and update.
	 * 
	 */
	public ResultAndNavigationPageLinksRenderer(Composite parent, int style,
			PageableController controller) {
		super(parent, style, controller);
		refreshEnabled(controller);
	}

	/**
	 * Create the UI like this :
	 * 
	 * <p>
	 * 
	 * <pre>
	 * Results 1-5 of 10          Previous 1 2 ...10 Next
	 * </pre>
	 * 
	 * </p>
	 */
	protected void createUI(Composite parent) {
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		this.setLayout(layout);
		// Create result label "Results 1-5 of 10"
		createLeftContainer(parent);
		// Create page links "Previous 1 2 ...10 Next"
		createRightContainer(parent);
	}

	/**
	 * Create result label "Results 1-5 of 10"
	 * 
	 * @param parent
	 */
	private void createLeftContainer(Composite parent) {
		Composite left = createComposite(parent, SWT.NONE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		left.setLayoutData(data);

		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		left.setLayout(layout);

		resultLabel = new Label(left, SWT.NONE);
		resultLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	/**
	 * Create page links "Previous 1 2 ...10 Next" with SWT Link.
	 * 
	 * @param parent
	 */
	private void createRightContainer(Composite parent) {
		Composite right = createComposite(parent, SWT.NONE);
		right.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 0;
		right.setLayout(layout);

		// Previous link
		previousLink = createHyperlink(right, SWT.NONE);
		setLinkText(previousLink, Resources.getText(
				Resources.PaginationRenderer_previous, getLocale()));
		previousLink.setLayoutData(new GridData(SWT.RIGHT));
		previousLink.addSelectionListener(this);

		// Page links
		pageLinks = createHyperlink(right, SWT.NONE);
		pageLinks.setForeground(getColor());
		pageLinks.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		pageLinks.addSelectionListener(this);

		// Next link
		nextLink = createHyperlink(right, SWT.NONE);
		setLinkText(nextLink, Resources.getText(
				Resources.PaginationRenderer_next, getLocale()));
		nextLink.setLayoutData(new GridData(SWT.LEFT));
		nextLink.addSelectionListener(this);

	}

	public void widgetDefaultSelected(SelectionEvent e) {
		// Do nothing
	}

	public void widgetSelected(SelectionEvent e) {
		// Link was clicked, update the page controller according to the
		// selected
		// link.
		Link hyperlink = (Link) e.getSource();
		int newCurrentPage = 0;
		if (hyperlink == previousLink) {
			newCurrentPage = getController().getCurrentPage() - 1;
		} else if (hyperlink == nextLink) {
			newCurrentPage = getController().getCurrentPage() + 1;
		} else if (hyperlink == pageLinks) {
			newCurrentPage = Integer.parseInt(e.text);
		}
		getController().setCurrentPage(newCurrentPage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.nebula.widgets.pagination.PageChangedListener#pageIndexChanged
	 * (int, int, org.eclipse.nebula.widgets.pagination.PaginationController)
	 */
	public void pageIndexChanged(int oldPageNumber, int newPageNumber,
			PageableController controller) {
		// Generate string of links
		StringBuilder s = new StringBuilder();
		int[] indexes = PaginationHelper.getPageIndexes(
				controller.getCurrentPage(), controller.getTotalPages(), 10);
		for (int i = 0; i < indexes.length; i++) {
			int j = indexes[i];
			if (i > 0) {
				s.append(' ');
			}
			if (j == PaginationHelper.SEPARATOR) {
				s.append(Resources.getText(
						Resources.PaginationRenderer_separator, getLocale()));
			} else if (j == newPageNumber)
				s.append(String.valueOf((j + 1)));
			else {
				addA(String.valueOf(j), String.valueOf(j + 1), s);
			}
		}
		// Update SWT page links with the string links
		pageLinks.setText(s.toString());
		// Update Previous/Next links
		refreshEnabled(controller);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.nebula.widgets.pagination.PageChangedListener#pageSizeChanged
	 * (int, int, org.eclipse.nebula.widgets.pagination.PaginationController)
	 */
	public void pageSizeChanged(int oldPageSize, int newPageSize,
			PageableController paginationController) {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.nebula.widgets.pagination.PageChangedListener#
	 * totalElementsChanged(long, long,
	 * org.eclipse.nebula.widgets.pagination.PaginationController)
	 */
	public void totalElementsChanged(long oldTotalElements,
			long newTotalElements, PageableController controller) {
		refreshEnabled(controller);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.nebula.widgets.pagination.PageChangedListener#sortChanged
	 * (java.lang.String, java.lang.String, int, int,
	 * org.eclipse.nebula.widgets.pagination.PaginationController)
	 */
	public void sortChanged(String oldPopertyName, String propertyName,
			int oldSortDirection, int sortDirection,
			PageableController paginationController) {
		// Do nothing
	}

	/**
	 * Update the enabled Next/Previous links + update the result label.
	 * 
	 * @param controller
	 */
	private void refreshEnabled(PageableController controller) {
		resultLabel.setText(PaginationHelper.getResultsText(controller,
				getLocale()));
		nextLink.setEnabled(controller.hasNextPage());
		previousLink.setEnabled(controller.hasPreviousPage());
	}

	/**
	 * Create SWT composite.
	 * 
	 * @param parent
	 * @param style
	 * @return
	 */
	protected Composite createComposite(Composite parent, int style) {
		return new Composite(parent, style);
	}

	/**
	 * Create hyperlink.
	 * 
	 * @param parent
	 * @param style
	 * @return
	 */
	protected Link createHyperlink(Composite parent, int style) {
		return new Link(parent, style);
	}

	/**
	 * Update the link with text content.
	 * 
	 * @param link
	 * @param text
	 */
	protected void setLinkText(Link link, String text) {
		StringBuilder a = new StringBuilder();
		addA(text, text, a);
		link.setText(a.toString());
	}

	/**
	 * Add <a href="$href" >$content</a> to the given {@link StringBuilder} a.
	 * 
	 * @param href
	 * @param content
	 * @param a
	 */
	private void addA(String href, String content, StringBuilder a) {
		a.append(OPEN_START_HREF);
		a.append(href);
		a.append(OPEN_END_HREF);
		a.append(content);
		a.append(END_HREF);
	}

	/**
	 * Returns the link Color.
	 * 
	 * @return
	 */
	protected Color getColor() {
		return Resources.getColor(RED_COLOR);
	}

	@Override
	public void setLocale(Locale locale) {
		super.setLocale(locale);
		// Local has changed, update the Previous/Next link text.
		setLinkText(previousLink, Resources.getText(
				Resources.PaginationRenderer_previous, getLocale()));
		setLinkText(nextLink, Resources.getText(
				Resources.PaginationRenderer_next, getLocale()));
		resultLabel.setText(PaginationHelper.getResultsText(getController(),
				getLocale()));
	}

}
