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

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Donald G. Dunne
 */
public class XmlUtil {

   public static String getRootTag(String xmlStr) {
      Matcher m;
      m = Pattern.compile("^[\r\n \t]*<.*?>[\r\n \t]*<(.*?)>", Pattern.MULTILINE | Pattern.DOTALL).matcher(xmlStr);
      if (m.find()) {
         return m.group(1);
      }
      return "";
   }

   /**
    * Returns <elementName>data</elementName> NOTE: data is not sent through AXml.textToXml
    * 
    * @param elementName
    * @param data
    * @return String <elementName>data</elementName>
    */
   public static String addTagData(String elementName, String data) {
      return addTagData(elementName, data, false);
   }

   public static String addTagData(String elementName, String data, boolean newLine) {
      String str = "<" + elementName + ">" + data + "</" + elementName + ">";
      if (newLine)
         return str + "\n";
      else
         return str;
   }

   /**
    * Parse <xmlRoot name="this.is.name" value="this.is.value"> and return name, value
    * 
    * @param xmlRoot xml tag name
    * @param xmlStr string containing xml
    * @return String[]{name, value}
    */
   public static String[] getNameValue(String xmlRoot, String xmlStr) {
      String[] strs = new String[] {"", ""};
      Matcher m;
      m =
            Pattern.compile("<" + xmlRoot + " name=\"(.*?)\" value=\"(.*?)\" />", Pattern.MULTILINE | Pattern.DOTALL).matcher(
                  xmlStr);
      if (m.find()) {
         strs[0] = m.group(1);
         strs[1] = m.group(2);
      }
      return strs;
   }

   public static String getNameValueXml(String xmlRoot, String name, String value) {
      return String.format("<%s name=\"%s\" value=\"%s\"/>", xmlRoot, name, value);
   }

   public static String[] getTagDataArray(String xmlStr, String xmlRoot) {
      Vector<String> v = new Vector<String>();
      Matcher m;
      m =
            Pattern.compile("<" + xmlRoot + ">(.*?)</" + xmlRoot + ">", Pattern.MULTILINE | Pattern.DOTALL).matcher(
                  xmlStr);
      while (m.find()) {
         v.add(xmlToText(m.group(1)));
      }
      return v.toArray(new String[v.size()]);
   }

   /**
    * Returns data between <xmlRoot> and </xmlRoot> from xmlStr
    * 
    * @param xmlStr
    * @param xmlRoot
    * @return Return tag string
    */
   public static String getTagData(String xmlStr, String xmlRoot) {
      String tags[] = getTagDataArray(xmlStr, xmlRoot);
      if (tags.length > 0) {
         return tags[0];
      }
      return "";
   }

   public static int getTagIntData(String xmlStr, String xmlRoot) {
      String tags[] = getTagDataArray(xmlStr, xmlRoot);
      if (tags.length > 0) {
         String intStr = tags[0];
         return (new Integer(intStr)).intValue();
      }
      return 0;
   }

   public static Boolean getTagBooleanData(String xmlStr, String xmlRoot) {
      String tags[] = getTagDataArray(xmlStr, xmlRoot);
      if (tags.length > 0) {
         String intStr = tags[0];
         return (intStr.equals("true") ? true : false);
      }
      return false;
   }

   /**
    * Given text strings containing xml reserved characters, replace with valid xml representation characters > => & gt;
    * < => & lt; & => & amp; ' => & apos; " => & quot;
    * 
    * @param text text to be converted to valid XML representation characters
    * @return String valid xml string
    */
   public static String textToXml(String text) {
      if (text == null || text.equals("")) return "";
      String str = new String(text);
      str = str.replaceAll("&", "&amp;");
      str = str.replaceAll(">", "&gt;");
      str = str.replaceAll("<", "&lt;");
      str = str.replaceAll("'", "&apos;");
      str = str.replaceAll("\"", "&quot;");
      return str;
   }

   /**
    * Given xml strings containing xml reserved characters, replace with displayable characters > <= & gt; < <= & lt; &
    * <= & amp; ' <= & apos; " <= & quot;
    * 
    * @param xml
    * @return displayable string
    */
   public static String xmlToText(String xml) {
      if (xml == null || xml.equals("")) return "";
      String str = new String(xml);
      str = str.replaceAll("&gt;", ">");
      str = str.replaceAll("&lt;", "<");
      str = str.replaceAll("&apos;", "'");
      str = str.replaceAll("&quot;", "\"");
      str = str.replaceAll("&amp;", "&");
      return str;
   }

}
