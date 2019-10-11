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

/**
 * Handler used to do something before/after page loading process. You can for
 * instance display a "Loading" message when pagination start and close it when
 * data are loaded.
 * 
 * @param <T>
 *            the pagination controller.
 */
public interface IPageLoaderHandler<T extends PageableController> {

	/** Default Handler **/
	public static final IPageLoaderHandler<?> DEFAULT_HANDLER = new IPageLoaderHandler<PageableController>() {

		public void onBeforePageLoad(PageableController controller) {

		}

		public boolean onAfterPageLoad(PageableController controller,
				Throwable e) {
			if (e != null) {
				// Error while page loading was processed, display the stack
				// trace.
				e.printStackTrace();
			}
			return true;
		}

	};

	/**
	 * This method is called before page loading process.
	 * 
	 * @param controller
	 *            the pagination controller.
	 */
	void onBeforePageLoad(T controller);

	/**
	 * This method is called after page loading process. If there is an error
	 * the given exception is filled with the exception.
	 * 
	 * @param controller
	 * @param e
	 * @return true if exception (when there is an error) must be thrown and
	 *         false otherwise.
	 */
	boolean onAfterPageLoad(T controller, Throwable e);
}
