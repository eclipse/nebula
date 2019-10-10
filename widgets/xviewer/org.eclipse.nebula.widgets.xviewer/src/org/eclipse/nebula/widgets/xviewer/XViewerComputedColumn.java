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

import java.util.Collection;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLog;

/**
 * @author Donald G. Dunne
 */
public abstract class XViewerComputedColumn extends XViewerValueColumn {

   protected XViewerColumn sourceXViewerColumn;
   protected XViewer xViewer;
   private final Pattern idPattern = Pattern.compile("^.*\\((.*?)\\)$");

   public XViewerComputedColumn(String id, String name, int width, XViewerAlign align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   public XViewerComputedColumn(XViewer viewer, String xml) {
      super(viewer, xml);
   }

   public XViewerColumn getSourceXViewerColumn() {
      return sourceXViewerColumn;
   }

   public void setSourceXViewerColumn(XViewerColumn sourceXViewerColumn) {
      this.sourceXViewerColumn = sourceXViewerColumn;
      id = getId();
      name = getName();
   }

   public String getSourceColumnId() {
      if (sourceXViewerColumn != null) {
         return sourceXViewerColumn.getId();
      }
      Matcher matcher = idPattern.matcher(id);
      if (matcher.find()) {
         return matcher.group(1);
      }
      return null;
   }

   public void setSourceXViewerColumnFromColumns(Collection<XViewerColumn> xViewerColumns) {
      String sourceColumnId = getSourceColumnId();
      if (sourceColumnId == null) {
         XViewerLog.log(Activator.class, Level.SEVERE, "Invalid null sourceColumnId");
         return;
      }
      for (XViewerColumn xCol : xViewerColumns) {
         if (xCol.getId().equals(sourceColumnId)) {
            setSourceXViewerColumn(xCol);
            return;
         }
      }
      XViewerLog.log(Activator.class, Level.SEVERE,
         String.format("Can't resolve sourceColumn for XViewerComputedColumn [%s]", this));

   }

   public abstract boolean isApplicableFor(XViewerColumn xViewerColumn);

   public abstract boolean isApplicableFor(String storedId);

   public abstract XViewerComputedColumn createFromStored(XViewerColumn storedColumn);

   @Override
   public XViewer getXViewer() {
      return xViewer;
   }

   @Override
   public void setXViewer(Object xViewer) {
      this.xViewer = (XViewer) xViewer;
   }

   @Override
   public abstract XViewerComputedColumn copy();

}
