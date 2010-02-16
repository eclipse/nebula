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

package org.eclipse.nebula.widgets.xviewer.util.internal;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.xviewer.Activator;
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
   protected String label = "";
   protected String xmlRoot = "";
   protected String xmlSubRoot = "";
   protected String toolTip = null;
   protected boolean requiredEntry = false;
   protected boolean editable = true;
   protected boolean verticalLabel = false;
   protected boolean fillVertically = false;
   protected boolean fillHorizontally = false;

   /**
    * @return the fillHorizontally
    */
   public boolean isFillHorizontally() {
      return fillHorizontally;
   }

   protected boolean displayLabel = true;
   private final Set<XViewerWidgetModifiedListener> modifiedListeners =
         new LinkedHashSet<XViewerWidgetModifiedListener>();
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
      if (this.labelWidget != null && !labelWidget.isDisposed()) this.labelWidget.setToolTipText(toolTip);
   }

   public void addXModifiedListener(XViewerWidgetModifiedListener listener) {
      modifiedListeners.add(listener);
   }

   public void notifyXModifiedListeners() {
      for (XViewerWidgetModifiedListener listener : modifiedListeners)
         listener.widgetModified(this);
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
            public void mouseDoubleClick(MouseEvent e) {
               openHelp();
            }

            public void mouseDown(MouseEvent e) {
            }

            public void mouseUp(MouseEvent e) {
            }
         };
         labelWidget.addMouseListener(mouseLabelListener);
      }
   }

   public abstract Control getControl();

   public void openHelp() {
      try {
         if (toolTip != null && label != null) MessageDialog.openInformation(
               PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), label + " Tool Tip", toolTip);
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
      if (getControl() != null) toolkit.adapt(getControl(), true, true);
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
    * 
    * @param str - value to set
    */
   public abstract void setXmlData(String str);

   /**
    * Return string to save off between xml tags Used by call to toXml() String returned will be sent through
    * AXml.textToXml() before being saved Used by: toXml
    * 
    * @return Return Xml data string.
    */
   public abstract String getXmlData();

   public abstract String toHTML(String labelFont);

   public String toXml() throws Exception {
      if (xmlSubRoot.equals("")) {
         return toXml(xmlRoot);
      } else {
         return toXml(xmlRoot, xmlSubRoot);
      }
   }

   public String toXml(String xmlRoot) throws Exception {
      String s = "<" + xmlRoot + ">" + XmlUtil.textToXml(getXmlData()) + "</" + xmlRoot + ">\n";
      return s;
   }

   public String toXml(String xmlRoot, String xmlSubRoot) throws Exception {
      String s =
            "<" + xmlRoot + ">" + "<" + xmlSubRoot + ">" + XmlUtil.textToXml(getXmlData()) + "</" + xmlSubRoot + ">" + "</" + xmlRoot + ">\n";
      return s;
   }

   public void setFromXml(String xml) throws IllegalStateException {
      Matcher m;
      m = Pattern.compile("<" + xmlRoot + ">(.*?)</" + xmlRoot + ">", Pattern.MULTILINE | Pattern.DOTALL).matcher(xml);
      if (m.find()) setXmlData(XmlUtil.xmlToText(m.group(1)));
   }

   /**
    * @return Returns vector of Strings.
    */
   public Vector<String> getDisplayLabels() {
      Vector<String> l = new Vector<String>();
      l.add(label);
      return l;
   }

   /**
    * @param displayLabel The displayLabel to set.
    */
   public void setDisplayLabel(String displayLabel) {
      this.label = displayLabel;
   }

   /**
    * @return Returns the editable.
    */
   public boolean isEditable() {
      return editable;
   }

   /**
    * @param editable The editable to set.
    */
   public void setEditable(boolean editable) {
      this.editable = editable;
   }

   /**
    * @return Returns the verticalLabel.
    */
   public boolean isVerticalLabel() {
      return verticalLabel;
   }

   /**
    * @param verticalLabel The verticalLabel to set.
    */
   public void setVerticalLabel(boolean verticalLabel) {
      this.verticalLabel = verticalLabel;
   }

   /**
    * @return Returns the xmlRoot.
    */
   public String getXmlRoot() {
      return xmlRoot;
   }

   /**
    * @param xmlRoot The xmlRoot to set.
    */
   public void setXmlRoot(String xmlRoot) {
      this.xmlRoot = xmlRoot;
   }

   /**
    * @return Returns the xmlSubRoot.
    */
   public String getXmlSubRoot() {
      return xmlSubRoot;
   }

   /**
    * @param xmlSubRoot The xmlSubRoot to set.
    */
   public void setXmlSubRoot(String xmlSubRoot) {
      this.xmlSubRoot = xmlSubRoot;
   }

   /**
    * @return Returns the toolTip.
    */
   public String getToolTip() {
      return toolTip;
   }

   /**
    * @return Returns the fillVertically.
    */
   public boolean isFillVertically() {
      return fillVertically;
   }

   /**
    * @param fillVertically The fillVertically to set.
    */
   public void setFillVertically(boolean fillVertically) {
      this.fillVertically = fillVertically;
   }

   /**
    * @return Returns the label.
    */
   public String getLabel() {
      return label;
   }

   /**
    * @param label The label to set.
    */
   public void setLabel(String label) {
      this.label = label;
   }

   /**
    * @return Returns the labelWidget.
    */
   public Label getLabelWidget() {
      return labelWidget;
   }

   /**
    * @param labelWidget The labelWidget to set.
    */
   public void setLabelWidget(Label labelWidget) {
      this.labelWidget = labelWidget;
   }

   /**
    * @return Returns the requiredEntry.
    */
   public boolean isRequiredEntry() {
      return requiredEntry;
   }

   /**
    * @param requiredEntry The requiredEntry to set.
    */
   public void setRequiredEntry(boolean requiredEntry) {
      this.requiredEntry = requiredEntry;
   }

   /**
    * @return Returns the reportType.
    */
   public int getReportType() {
      return reportType;
   }

   /**
    * @param reportType The reportType to set.
    */
   public void setReportType(int reportType) {
      this.reportType = reportType;
   }

   /**
    * Return data for display in Report (without label) NOTE: There should be no newlines at end of String
    * 
    * @return Return string.
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
    * 
    * @return String report
    */
   public String toReport(int rptType) {
      String s = label + ": ";
      switch (rptType) {
         case RPT_SINGLE_LINE:
            s += getReportData() + "\n\n";
            break;
         case RPT_MULTI_LINE:
            String data = getReportData();
            data = data.replaceAll("\n", "\n   ");
            s += "\n" + data + "\n\n";
            break;
         default:
            s += getReportData();
            break;
      }
      return s;
   }

   /**
    * If set, label will be displayed with entry widget.
    * 
    * @param displayLabel The displayLabel to set.
    */
   public void setDisplayLabel(boolean displayLabel) {
      this.displayLabel = displayLabel;
   }

   public void setFillHorizontally(boolean fillHorizontally) {
      this.fillHorizontally = fillHorizontally;
   }

   public abstract Object getData();

   /**
    * @return the displayLabel
    */
   public boolean isDisplayLabel() {
      return displayLabel;
   }
}