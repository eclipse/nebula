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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.xviewer.Activator;
import org.eclipse.nebula.widgets.xviewer.XViewerText;
import org.eclipse.nebula.widgets.xviewer.core.util.XmlUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Abstract class for all widgets used in Wizards and Editors
 *
 * @author Donald G. Dunne
 */
public abstract class XViewerWidget {

   protected Label labelWidget = null;
   protected String label = ""; //$NON-NLS-1$
   protected String xmlRoot = ""; //$NON-NLS-1$
   protected String xmlSubRoot = ""; //$NON-NLS-1$
   protected String toolTip = null;
   protected boolean requiredEntry = false;
   protected boolean editable = true;
   protected boolean verticalLabel = false;
   protected boolean fillVertically = false;
   protected boolean fillHorizontally = false;

   public boolean isFillHorizontally() {
      return fillHorizontally;
   }

   protected boolean displayLabel = true;
   private final Set<XViewerWidgetModifiedListener> modifiedListeners =
      new LinkedHashSet<>();
   private MouseListener mouseLabelListener;

   /**
    * Display "label: data"
    */
   public final static int RPT_NONE = 0;
   /**
    * Display "label: data\n\n" Default of AAtribute
    */
   public final static int RPT_SINGLE_LINE = 1;
   /**
    * Display "label:\n data\n\n"
    */
   public final static int RPT_MULTI_LINE = 2;
   protected int reportType = RPT_SINGLE_LINE;
   protected FormToolkit toolkit;

   public XViewerWidget(String label) {
      this.label = label;
   }

   public XViewerWidget(String label, String xmlRoot) {
      this.label = label;
      this.xmlRoot = xmlRoot;
   }

   public XViewerWidget(String label, String xmlRoot, String xmlSubRoot) {
      this.label = label;
      this.xmlRoot = xmlRoot;
      this.xmlSubRoot = xmlSubRoot;
   }

   public void setToolTip(String toolTip) {
      this.toolTip = toolTip;
      if (this.labelWidget != null && !labelWidget.isDisposed()) {
         this.labelWidget.setToolTipText(toolTip);
      }
   }

   public void addXModifiedListener(XViewerWidgetModifiedListener listener) {
      modifiedListeners.add(listener);
   }

   public void notifyXModifiedListeners() {
      for (XViewerWidgetModifiedListener listener : modifiedListeners) {
         listener.widgetModified(this);
      }
   }

   public void setLabelError() {
      if (labelWidget == null || labelWidget.isDisposed()) {
         return;
      }
      if (!isValid()) {
         labelWidget.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
      } else {
         labelWidget.setForeground(null);
      }
      if (mouseLabelListener == null) {
         mouseLabelListener = new MouseListener() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
               openHelp();
            }

            @Override
            public void mouseDown(MouseEvent e) {
               // do nothing
            }

            @Override
            public void mouseUp(MouseEvent e) {
               // do nothing
            }
         };
         labelWidget.addMouseListener(mouseLabelListener);
      }
   }

   public abstract Control getControl();

   public void openHelp() {
      try {
         if (toolTip != null && label != null) {
            MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
               label + " " + XViewerText.get("tooltip"), toolTip); //$NON-NLS-1$ //$NON-NLS-2$
         }
      } catch (Exception ex) {
         XViewerLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   /**
    * Create Widgets used to display label and entry for wizards and editors
    */
   public abstract void createWidgets(Composite parent, int horizontalSpan);

   public void createWidgets(FormToolkit toolkit, Composite parent, int horizontalSpan) {
      this.toolkit = toolkit;
      createWidgets(parent, horizontalSpan);
      adaptControls(toolkit);
   }

   public void adaptControls(FormToolkit toolkit) {
      if (getControl() != null) {
         toolkit.adapt(getControl(), true, true);
      }
      if (labelWidget != null) {
         toolkit.adapt(labelWidget, true, true);
         toolkit.adapt(labelWidget.getParent(), true, true);
      }
   }

   /**
    * Create Widgets used to display label and entry for wizards and editors
    */
   public abstract void dispose();

   /**
    * Set focus to the entry widget
    */
   public abstract void setFocus();

   /**
    * Refresh the entry widget
    */
   public abstract void refresh();

   /**
    * Return true if entry is valid
    *
    * @return Return boolean validity indication.
    */
   public abstract boolean isValid();

   /**
    * Called with string found between xml tags Used by setFromXml() String will be sent through AXml.xmlToText() before
    * being sent to setXmlData implementation. Used by: setFromXml
    */
   public abstract void setXmlData(String str);

   /**
    * Return string to save off between xml tags Used by call to toXml() String returned will be sent through
    * AXml.textToXml() before being saved Used by: toXml
    */
   public abstract String getXmlData();

   public abstract String toHTML(String labelFont);

   public String toXml() throws Exception {
      if (xmlSubRoot.equals("")) { //$NON-NLS-1$
         return toXml(xmlRoot);
      } else {
         return toXml(xmlRoot, xmlSubRoot);
      }
   }

   public String toXml(String xmlRoot) throws Exception {
      return "<" + xmlRoot + ">" + XmlUtil.textToXml(getXmlData()) + "</" + xmlRoot + ">\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
   }

   public String toXml(String xmlRoot, String xmlSubRoot) throws Exception {
      return "<" + xmlRoot + ">" + "<" + xmlSubRoot + ">" + XmlUtil.textToXml( //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
         getXmlData()) + "</" + xmlSubRoot + ">" + "</" + xmlRoot + ">\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
   }

   public void setFromXml(String xml) throws IllegalStateException {
      Matcher m;
      m = Pattern.compile("<" + xmlRoot + ">(.*?)</" + xmlRoot + ">", Pattern.MULTILINE | Pattern.DOTALL).matcher(xml); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      if (m.find()) {
         setXmlData(XmlUtil.xmlToText(m.group(1)));
      }
   }

   public List<String> getDisplayLabels() {
      List<String> l = new ArrayList<>();
      l.add(label);
      return l;
   }

   public void setDisplayLabel(String displayLabel) {
      this.label = displayLabel;
   }

   public boolean isEditable() {
      return editable;
   }

   public void setEditable(boolean editable) {
      this.editable = editable;
   }

   public boolean isVerticalLabel() {
      return verticalLabel;
   }

   public void setVerticalLabel(boolean verticalLabel) {
      this.verticalLabel = verticalLabel;
   }

   public String getXmlRoot() {
      return xmlRoot;
   }

   public void setXmlRoot(String xmlRoot) {
      this.xmlRoot = xmlRoot;
   }

   public String getXmlSubRoot() {
      return xmlSubRoot;
   }

   public void setXmlSubRoot(String xmlSubRoot) {
      this.xmlSubRoot = xmlSubRoot;
   }

   public String getToolTip() {
      return toolTip;
   }

   public boolean isFillVertically() {
      return fillVertically;
   }

   public void setFillVertically(boolean fillVertically) {
      this.fillVertically = fillVertically;
   }

   public String getLabel() {
      return label;
   }

   public void setLabel(String label) {
      this.label = label;
   }

   public Label getLabelWidget() {
      return labelWidget;
   }

   public void setLabelWidget(Label labelWidget) {
      this.labelWidget = labelWidget;
   }

   public boolean isRequiredEntry() {
      return requiredEntry;
   }

   public void setRequiredEntry(boolean requiredEntry) {
      this.requiredEntry = requiredEntry;
   }

   public int getReportType() {
      return reportType;
   }

   public void setReportType(int reportType) {
      this.reportType = reportType;
   }

   /**
    * Return data for display in Report (without label) NOTE: There should be no newlines at end of String
    */
   public abstract String getReportData();

   @Override
   public String toString() {
      return toReport();
   }

   public String toReport() {
      return toReport(reportType);
   }

   /**
    * RPT_NONE (label: data), RPT_SINGLE_LINE (label: data\n\n), RPT_MULTI_LINE (label:\n data\n\n)
    */
   public String toReport(int rptType) {
      String s = label + ": "; //$NON-NLS-1$
      switch (rptType) {
         case RPT_SINGLE_LINE:
            s += getReportData() + "\n\n"; //$NON-NLS-1$
            break;
         case RPT_MULTI_LINE:
            String data = getReportData();
            data = data.replaceAll("\n", "\n   "); //$NON-NLS-1$ //$NON-NLS-2$
            s += "\n" + data + "\n\n"; //$NON-NLS-1$ //$NON-NLS-2$
            break;
         default:
            s += getReportData();
            break;
      }
      return s;
   }

   /**
    * If set, label will be displayed with entry widget.
    */
   public void setDisplayLabel(boolean displayLabel) {
      this.displayLabel = displayLabel;
   }

   public void setFillHorizontally(boolean fillHorizontally) {
      this.fillHorizontally = fillHorizontally;
   }

   public abstract Object getData();

   public boolean isDisplayLabel() {
      return displayLabel;
   }
}