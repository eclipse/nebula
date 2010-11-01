/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.xviewer;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public interface IXViewerValueColumn {

   Color getBackground(Object element, XViewerColumn xCol, int columnIndex);

   Color getForeground(Object element, XViewerColumn xCol, int columnIndex);

   StyledString getStyledText(Object element, XViewerColumn viewerColumn, int columnIndex);

   Font getFont(Object element, XViewerColumn viewerColumn, int columnIndex);

   Image getColumnImage(Object element, XViewerColumn column, int columnIndex);

   String getColumnText(Object element, XViewerColumn column, int columnIndex);
}