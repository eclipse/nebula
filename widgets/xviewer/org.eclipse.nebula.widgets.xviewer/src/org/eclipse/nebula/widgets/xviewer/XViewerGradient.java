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
package org.eclipse.nebula.widgets.xviewer;

import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class XViewerGradient {

   private final XViewer xViewer;
   private boolean on = false;

   public XViewerGradient(XViewer xViewer) {
      this.xViewer = xViewer;
      setOn(true);
   }

   public boolean isOn() {
      return on;
   }

   public void setOn(boolean on) {
      if (this.on == on) {
         return;
      }
      this.on = on;
      if (this.on) {
         this.xViewer.getTree().addListener(SWT.PaintItem, paintListener);
      } else {
         this.xViewer.getTree().removeListener(SWT.PaintItem, paintListener);
      }
   }
   private final Listener paintListener = new Listener() {

      @Override
      public void handleEvent(Event event) {
         try {
            XViewerColumn xViewerColumn =
               ((IXViewerLabelProvider) xViewer.getLabelProvider()).getTreeColumnOffIndex(event.index);
            TreeItem item = (TreeItem) event.item;
            if (item.getData() == null) {
               return;
            }
            int percent =
               ((IXViewerLabelProvider) xViewer.getLabelProvider()).getColumnGradient(item.getData(), xViewerColumn,
                  event.index);
            if (percent == 0 || percent > 100 || percent < 0) {
               return;
            }
            GC gc = event.gc;
            Color foreground = gc.getForeground();
            Color background = gc.getBackground();
            gc.setForeground(xViewer.getTree().getDisplay().getSystemColor(SWT.COLOR_GREEN));
            gc.setBackground(xViewer.getTree().getDisplay().getSystemColor(SWT.COLOR_YELLOW));
            int width = (xViewerColumn.getWidth() - 1) * percent / 100;
            gc.fillGradientRectangle(event.x, event.y, width, event.height, true);
            Rectangle rect2 = new Rectangle(event.x, event.y, width - 1, event.height - 1);
            gc.setForeground(xViewer.getTree().getDisplay().getSystemColor(SWT.COLOR_BLACK));

            gc.drawRectangle(rect2);
            gc.setForeground(xViewer.getTree().getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
            String text =
               ((IXViewerLabelProvider) xViewer.getLabelProvider()).getColumnText(item.getData(), xViewerColumn,
                  event.index);
            Point size = event.gc.textExtent(text);
            int offset = Math.max(0, (event.height - size.y) / 2 + 1);
            gc.drawText(text, event.x + 5, event.y + offset, true);
            gc.setForeground(background);
            gc.setBackground(foreground);
         } catch (Exception ex) {
            XViewerLog.log(XViewerGradient.class, Level.SEVERE, ex);
            return;
         }
      }
   };
}
