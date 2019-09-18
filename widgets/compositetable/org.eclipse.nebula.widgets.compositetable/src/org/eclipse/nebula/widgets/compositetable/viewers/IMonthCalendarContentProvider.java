/*******************************************************************************
 * Copyright (c) 2008 Trevor S. Kaufman and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
