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

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.nebula.widgets.xviewer.Activator;
import org.eclipse.nebula.widgets.xviewer.XViewerText;
import org.eclipse.nebula.widgets.xviewer.core.util.XmlUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * Generic label and text field object for use by single entry artifact attributes
 *
 * @author Donald G. Dunne
 */
public class XViewerTextWidget extends XViewerWidget {

   protected StyledText sText; // Contains visable representation of text
   private Composite parent;
   protected String text = ""; // Where actual text with xml tags is stored //$NON-NLS-1$
   private int maxTextChars = 0;

   private final static boolean debug = false;
   private int width = 0;
   private int height = 0;
   private Font font;

   public XViewerTextWidget() {
      super("AText", "text"); //$NON-NLS-1$//$NON-NLS-2$
   }

   public XViewerTextWidget(String displayLabel) {
      this(displayLabel, "text"); //$NON-NLS-1$
   }

   public XViewerTextWidget(String displayLabel, String xmlRoot) {
      this(displayLabel, xmlRoot, ""); //$NON-NLS-1$
   }

   public XViewerTextWidget(String displayLabel, String xmlRoot, String xmlSubRoot) {
      super(displayLabel, xmlRoot, xmlSubRoot);
   }

   public void setEnabled(boolean enabled) {
      sText.setEnabled(enabled);
   }

   public void setSize(int width, int height) {
      this.width = width;
      this.height = height;
      if (sText != null && !sText.isDisposed()) {
         sText.setSize(width, height);
      }
   }

   public void setHeight(int height) {
      this.height = height;
      if (sText != null && !sText.isDisposed()) {
         sText.setSize(sText.getSize().x, height);
      }
   }

   @Override
   public String toString() {
      return label + ": *" + text + "*"; //$NON-NLS-1$ //$NON-NLS-2$
   }

   @Override
   public Control getControl() {
      return sText;
   }

   /**
    * Create Text Widgets. Widgets Created: Label: "text entry" horizonatalSpan takes up 2 columns; horizontalSpan must
    * be >=2
    */
   @Override
   public void createWidgets(Composite parent, int horizontalSpan) {
      createWidgets(parent, horizontalSpan, true);
   }

   public void createWidgets(Composite parent, int horizontalSpan, boolean fillText) {

      if (!verticalLabel && (horizontalSpan < 2)) {
         horizontalSpan = 2;
      }

      this.parent = parent;
      Composite composite = null;

      composite = createComposite(parent, horizontalSpan);

      createLabelWidget(composite);

      if (fillVertically) {
         sText = new StyledText(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL);
      } else {
         sText = new StyledText(composite, SWT.BORDER | SWT.SINGLE);
      }
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.horizontalSpan = verticalLabel ? horizontalSpan : horizontalSpan - 1;
      gd.grabExcessHorizontalSpace = true;
      gd.horizontalAlignment = GridData.FILL;

      if (fillVertically) {
         gd.grabExcessVerticalSpace = true;
         gd.verticalAlignment = GridData.FILL;
         if (height > 0) {
            gd.heightHint = height;
         }
      }

      sText.setLayoutData(gd);
      sText.setMenu(getDefaultMenu());

      addModificationListener();

      if (text != null) {
         sText.setText(text);
      }
      if (width != 0 && height != 0) {
         sText.setSize(width, height);
      }

      if (maxTextChars > 0) {
         sText.setTextLimit(maxTextChars);
      }
      if (fillText) {
         updateTextWidget();
      }
      setLabelError();
      sText.setEditable(editable);
      if (font != null) {
         sText.setFont(font);
      }
      parent.layout();
   }

   private void addModificationListener() {
      sText.addListener(SWT.Modify, e -> {
            if (sText != null) {
               text = sText.getText();
               setLabelError();
               notifyXModifiedListeners();
            }
      });
   }

   private void createLabelWidget(Composite composite) {
      if (displayLabel && !label.equals("")) { //$NON-NLS-1$
         labelWidget = new Label(composite, SWT.NONE);
         labelWidget.setText(label + ":"); //$NON-NLS-1$
         if (toolTip != null) {
            labelWidget.setToolTipText(toolTip);
         }
      }
   }

   private Composite createComposite(Composite parent, int horizontalSpan) {
      Composite composite;
      if (fillVertically) {
         composite = new Composite(parent, SWT.NONE);
         GridLayout layout = XViewerLib.getZeroMarginLayout(1, false);
         layout.verticalSpacing = 4;
         composite.setLayout(layout);
         composite.setLayoutData(new GridData(GridData.FILL_BOTH));
      } else {
         composite = new Composite(parent, SWT.NONE);
         GridLayout layout = XViewerLib.getZeroMarginLayout(2, false);
         layout.verticalSpacing = 4;
         composite.setLayout(layout);
         GridData gd = new GridData(GridData.FILL_HORIZONTAL);
         gd.horizontalSpan = horizontalSpan;
         composite.setLayoutData(gd);
      }
      return composite;
   }

   /**
    * @return text including xml tags replaced for references
    */
   public String getText() {
      return sText.getText();
   }

   public void setText(String text) {
      this.text = text;
      if (sText != null) {
         sText.setText(text);
      }
   }

   public Menu getDefaultMenu() {
      Menu menu = new Menu(sText.getShell());
      MenuItem cut = new MenuItem(menu, SWT.NONE);
      cut.setText(XViewerText.get("menu.cut")); //$NON-NLS-1$
      cut.addListener(SWT.Selection, e-> {
            sText.cut();
            sText.redraw();
      });
      MenuItem copy = new MenuItem(menu, SWT.NONE);
      copy.setText(XViewerText.get("menu.copy")); //$NON-NLS-1$
      copy.addListener(SWT.Selection, e-> sText.copy());

      MenuItem paste = new MenuItem(menu, SWT.NONE);
      paste.setText(XViewerText.get("menu.paste")); //$NON-NLS-1$
      paste.addListener(SWT.Selection, e->  {
            sText.paste();
            sText.redraw();
      });
      return menu;
   }

   @Override
   public void dispose() {
      if (font != null) {
         font.dispose();
      }
      if (labelWidget != null) {
         labelWidget.dispose();
      }
      if (sText != null) {
         sText.dispose();
         sText = null;
      }
      if (parent != null && !parent.isDisposed()) {
         parent.layout();
      }
   }

   @Override
   public void setFocus() {
      if (sText != null) {
         sText.setFocus();
      }
   }

   @Override
   public void setEditable(boolean editable) {
      super.setEditable(editable);
      if (sText != null && !sText.isDisposed()) {
         sText.setEditable(editable);
      }
   }

   /**
    * Set max character limit on text field
    *
    * @param limit - if 0, then limit is 999, else sets to limit
    */
   public void setMaxTextLimit(int limit) {
      this.maxTextChars = limit;
      if (sText != null) {
         if (limit == 0) {
            sText.setTextLimit(999);
         } else {
            sText.setTextLimit(limit);
         }
      }
   }

   public void forceFocus() {
      if (sText != null) {
         sText.forceFocus();
      }
   }

   public boolean isInteger() {
      try {
         Integer.valueOf(text);
      } catch (NumberFormatException e) {
         return false;
      }
      return true;
   }

   public boolean isFloat() {
      try {
         new Float(text);
      } catch (NumberFormatException e) {
         return false;
      }
      return true;
   }

   public int getInteger() {
      Integer num;
      try {
         num = Integer.valueOf(text);
      } catch (NumberFormatException e) {
         return 0;
      }
      return num.intValue();
   }

   public double getFloat() {
      Double num;
      try {
         num = new Double(text);
      } catch (NumberFormatException e) {
         return 0;
      }
      return num.doubleValue();
   }

   @Override
   public void setRequiredEntry(boolean requiredEntry) {
      super.setRequiredEntry(requiredEntry);
      setLabelError();
   }

   public boolean requiredEntry() {
      return requiredEntry;
   }

   public void addModifyListener(ModifyListener modifyListener) {
      if (sText != null) {
         sText.addModifyListener(modifyListener);
      }
   }

   public String get() {
      if (debug) {
         XViewerLog.log(Activator.class, Level.SEVERE, "text set *" + text + "*"); //$NON-NLS-1$//$NON-NLS-2$
      }
      return text;
   }

   @Override
   public String getXmlData() {
      if (sText == null || sText.isDisposed()) {
         return XmlUtil.textToXml(text);
      } else {
         try {
            return XmlUtil.textToXml(sText.getText());
         } catch (SWTException e) {
            return XmlUtil.textToXml(text);
         }
      }
   }

   @Override
   public String toXml() {
      if (xmlSubRoot.equals("")) { //$NON-NLS-1$
         return toXml(xmlRoot);
      } else {
         return toXml(xmlRoot, xmlSubRoot);
      }
   }

   @Override
   public String toXml(String xmlRoot) {
      return "<" + xmlRoot + ">" + getXmlData() + "</" + xmlRoot + ">\n"; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
   }

   @Override
   public String toXml(String xmlRoot, String xmlSubRoot) {
      return "<" + xmlRoot + ">" + "<" + xmlSubRoot + ">" + getXmlData() + "</" + xmlSubRoot + ">" + "</" + xmlRoot + ">\n"; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
   }

   @Override
   public void setXmlData(String str) {
      set(str);
      if (debug) {
         XViewerLog.log(Activator.class, Level.SEVERE, "setFromXml *" + str + "*"); //$NON-NLS-1$//$NON-NLS-2$
      }
   }

   @Override
   public void setFromXml(String xml) {
      Matcher m;
      m = Pattern.compile("<" + xmlRoot + ">(.*?)</" + xmlRoot + ">", Pattern.MULTILINE | Pattern.DOTALL).matcher(xml); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

      if (m.find()) {
         String xmlStr = m.group(1);
         if (debug) {
            XViewerLog.log(Activator.class, Level.SEVERE, "xmlStr *" + xmlStr + "*"); //$NON-NLS-1$//$NON-NLS-2$
         }
         String str = XmlUtil.xmlToText(xmlStr);
         if (debug) {
            XViewerLog.log(Activator.class, Level.SEVERE, "str *" + str + "*"); //$NON-NLS-1$//$NON-NLS-2$
         }
         setXmlData(str);
      }
   }

   public int getInt() {
      Integer percent = Integer.valueOf(0);
      try {
         percent = Integer.valueOf(text);
      } catch (NumberFormatException e) {
         // do nothing
      }
      return percent.intValue();
   }

   protected void updateTextWidget() {
      if (sText == null || sText.isDisposed()) {
         return;
      }
      if (text.equals(sText.getText())) {
         return;
      }
      // Disable Listeners so not to fill Undo List
      sText.setText(text);
      // Re-enable Listeners
      setLabelError();
   }

   public void set(String text) {
      if (text == null) {
         this.text = ""; //$NON-NLS-1$
      } else {
         this.text = text;
      }
      if (debug) {
         XViewerLog.log(Activator.class, Level.SEVERE, "set *" + text + "*"); //$NON-NLS-1$ //$NON-NLS-2$
      }
      updateTextWidget();
   }

   public void set(XViewerTextWidget text) {
      set(text.get());
   }

   public void append(String text) {
      this.text = this.text + text;
      updateTextWidget();
   }

   @Override
   public void refresh() {
      updateTextWidget();
   }

   @Override
   public String getReportData() {
      StringBuilder sb = new StringBuilder();
      String textStr = text;
      if (fillVertically) {
         sb.append("\n"); //$NON-NLS-1$
         textStr = textStr.replaceAll("\n", "\n" + "      "); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
         textStr = "      " + textStr; //$NON-NLS-1$
      }
      sb.append(textStr);
      return sb.toString().replaceAll("\n$", ""); //$NON-NLS-1$//$NON-NLS-2$
   }

   public String toHTML(String labelFont, boolean newLineText) {
      String s = HtmlUtil.getLabelStr(labelFont, label + ": "); //$NON-NLS-1$
      if (newLineText) {
         s = "<dl><dt>" + s + "<dd>"; //$NON-NLS-1$//$NON-NLS-2$
      }
      s += text;
      if (newLineText) {
         s += "</dl>"; //$NON-NLS-1$
      }
      return s;
   }

   @Override
   public String toHTML(String labelFont) {
      return toHTML(labelFont, false);
   }

   @Override
   public boolean isValid() {
      if (isRequiredEntry() && get().equals("")) { //$NON-NLS-1$
         return false;
      }
      return true;
   }

   @Override
   public Object getData() {
      return sText.getText();
   }

   /**
    * @return the sText
    */
   public StyledText getStyledText() {
      return sText;
   }

   /**
    * @return the font
    */
   public Font getFont() {
      return font;
   }

   /**
    * @param font the font to set
    */
   public void setFont(Font font) {
      this.font = font;
      if (sText != null) {
         sText.setFont(font);
      }
   }

}