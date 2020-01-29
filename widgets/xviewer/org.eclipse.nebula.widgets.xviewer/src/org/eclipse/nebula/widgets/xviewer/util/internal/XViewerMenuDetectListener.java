/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.xviewer.util.internal;

import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Donald G. Dunne
 */
public class XViewerMenuDetectListener implements Listener {
   private final XViewer xViewer;

   public XViewerMenuDetectListener(XViewer xViewer) {
      this.xViewer = xViewer;
   }

   @Override
   public void handleEvent(Event event) {
      Point point = Display.getCurrent().map(null, xViewer.getTree(), new Point(event.x, event.y));
      xViewer.processRightClickMouseEvent(point);
   }

}
