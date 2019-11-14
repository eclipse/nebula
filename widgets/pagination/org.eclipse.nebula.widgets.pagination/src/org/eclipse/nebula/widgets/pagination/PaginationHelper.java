/*******************************************************************************
 * Copyright (C) 2011 Angelo Zerr <angelo.zerr@gmail.com>, Pascal Leclercq <pascal.leclercq@gmail.com>
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Angelo ZERR - initial API and implementation
 *     Pascal Leclercq - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.pagination;

import java.text.MessageFormat;
import java.util.Locale;

import org.eclipse.swt.widgets.Widget;

/**
 * Pagination Utilities.
 * 
 */
public class PaginationHelper {

	private static final String PAGINATION_CONTROLLER_KEY = "___PaginationController";
	public static final int SEPARATOR = -1;

	/**
	 * Returns an array of int with the page indexes for the given
	 * currentPageIndex, totalPages abd nbMax.
	 * 
	 * @param currentPageIndex
	 * @param totalPages
	 * @param nbMax
	 * @return
	 */
	public static int[] getPageIndexes(int currentPageIndex, int totalPages,
			int nbMax) {
		nbMax = nbMax > totalPages ? totalPages : nbMax;
		int[] indexes = new int[nbMax];
		if (totalPages <= nbMax) {
			// ALl indexes must be filled
			for (int i = 0; i < indexes.length; i++) {
				indexes[i] = i;
			}
		} else {
			if (currentPageIndex > (totalPages - nbMax) + 3) {
				int index = totalPages - 1;
				for (int i = indexes.length - 1; i >= 0; i--) {
					if (i == 0) {
						indexes[i] = i;
					} else if (i == 1) {
						indexes[i] = SEPARATOR;
					} else {
						indexes[i] = index--;
					}
				}
			} else {

				if (nbMax - currentPageIndex > 2) {
					for (int i = 0; i < indexes.length; i++) {
						if (i == nbMax - 1) {
							indexes[i] = totalPages - 1;
						} else if (i == nbMax - 2) {
							indexes[i] = SEPARATOR;
						} else {
							indexes[i] = i;
						}
					}
				} else {
					// Total page is > to nb max of pages to display
					// Compute list to have for instance 1 ... 10, 11, 12, 13,
					// 14,
					// ...
					// 20
					int middle = nbMax / 2;
					int index = currentPageIndex;
					int nbItems = 0;
					for (int i = middle; i > 0 && index > 0; i--) {
						if (i == 1) {
							// before last item, check if it's 2 item
							indexes[i] = index == 2 ? index : SEPARATOR;
						} else if (i == 0) {
							indexes[i] = 0;
						} else if (index > 0) {
							indexes[i] = index--;
						}
						nbItems++;
					}

					index = currentPageIndex;
					for (int i = nbItems; i < nbMax; i++) {
						if (i == nbMax - 2) {
							indexes[i] = index == totalPages ? index : SEPARATOR;
						} else if (i == nbMax - 1) {
							indexes[i] = totalPages - 1;
						} else {
							indexes[i] = index++;
						}
					}
				}
			}

		}
		return indexes;
	}

	/**
	 * Returns the attached {@link PageableController} to the given widget.
	 * 
	 * @param widget
	 * @return
	 */
	public static PageableController getController(Widget widget) {
		return (PageableController) widget.getData(PAGINATION_CONTROLLER_KEY);
	}

	/**
	 * Attach the given {@link PageableController} to the given widget.
	 * 
	 * @param widget
	 * @param controller
	 */
	public static void setController(Widget widget,
			PageableController controller) {
		widget.setData(PAGINATION_CONTROLLER_KEY, controller);
	}

	/**
	 * Returns the results text (ex: "Results 1-5 of 10") for the given
	 * pagination controller. The given locale is used to translate the results
	 * text.
	 * 
	 * @param controller
	 *            the pagination controller.
	 * @param locale
	 *            the locale.
	 * @return
	 */
	public static String getResultsText(PageableController controller,
			Locale locale) {
		String resultsMessage = Resources.getText(
				Resources.PaginationRenderer_results, locale);// "Results {0}-{1} of {2}";
		return getResultsText(controller, resultsMessage);
	}

	/**
	 * Returns the results text (ex: "Results 1-5 of 10") for the given
	 * pagination controller. The resultsMessage (ex: "Results {0}-{1} of {2}")
	 * is used to compute the text.
	 * 
	 * @param controller
	 *            the pagination controller.
	 * @param resultsMessage
	 *            the results message.
	 * @return
	 */
	public static String getResultsText(PageableController controller,
			String resultsMessage) {
		int start = controller.getPageOffset() + 1;
		int end = start + controller.getPageSize() - 1;
		long total = controller.getTotalElements();
		if (end > total) {
			end = (int) total;
		}
		return getResultsText(start, end, total, controller, resultsMessage);
	}

	/**
	 * Returns the results text (ex: "Results 1-5 of 10") for the given
	 * pagination information start, end and total. The resultsMessage (ex:
	 * "Results {0}-{1} of {2}") is used to compute the text.
	 * 
	 * @param start
	 *            first page offset.
	 * @param end
	 *            last page offset
	 * @param total
	 *            total elements.
	 * @param controller
	 * @param resultsMessage
	 * @return
	 */
	public static String getResultsText(int start, int end, long total,
			PageableController controller, String resultsMessage) {
		return MessageFormat.format(resultsMessage, start, end, total);
	}

	/**
	 * urns the page text (ex : Page 1/2).
	 * 
	 * @param pageIndex
	 *            the page index.
	 * @param totalPage
	 *            the total page.
	 * @param locale
	 *            the locale.
	 * @return
	 */
	public static String getPageText(int pageIndex, int totalPage, Locale locale) {
		String message = Resources.getText(Resources.PaginationRenderer_page,
				locale);
		return MessageFormat.format(message, pageIndex, totalPage);
	}
}
