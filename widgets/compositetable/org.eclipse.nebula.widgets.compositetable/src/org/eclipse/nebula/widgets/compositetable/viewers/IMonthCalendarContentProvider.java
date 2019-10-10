/*******************************************************************************
 * Copyright (c) 2008 Trevor S. Kaufman and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Trevor S. Kaufman - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.compositetable.viewers;

import java.util.Date;

import org.eclipse.jface.viewers.IContentProvider;

public interface IMonthCalendarContentProvider extends IContentProvider {
	
	public Object[] getElements(Date date, Object inputElement);

}
