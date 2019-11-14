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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.nebula.widgets.xviewer.Activator;

/**
 * @author Donald G. Dunne
 */
public class HtmlUtil {
   private static final String HTTP_CHARSET_ENCODING =
      "<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">";
   private static final String begin = "<table ";
   public final static String LABEL_FONT = "<font color=\"black\" face=\"Arial\" size=\"-1\">";

   public static String getHyperlink(String url, String name) {
      return String.format("<a href=\"%s\">%s</a>", url, name);
   }

   public static String textToHtml(String text) {
      if (text == null) {
         return "";
      }
      text = text.replaceAll("&", "&amp;");
      text = text.replaceAll(">", "&gt;");
      text = text.replaceAll("<", "&lt;");
      text = text.replaceAll("\"", "&quot;");
      text = text.replaceAll("\\n", "<br/>");
      text = text.replaceAll("[\\x0B\\f\\r]+", "");
      return text;
   }

   public static String htmlToText(String html) {
      if (html == null) {
         return "";
      }
      html = html.replaceAll("&amp;", "&");
      html = html.replaceAll("&gt;", ">");
      html = html.replaceAll("&lt;", "<");
      html = html.replaceAll("&quot;", "\"");
      html = html.replaceAll("&nbsp;", " ");
      return html;
   }

   public static String getUrlPageHtml(String urlStr, InetSocketAddress addr) {
      StringBuilder buffer = new StringBuilder();
      try {
         URL url = new URL(urlStr);
         URLConnection connection = url.openConnection(new Proxy(Proxy.Type.HTTP, addr));
         BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
         String line = null;
         while ((line = rd.readLine()) != null) {
            buffer.append(line);
         }
         rd.close();
         return buffer.toString();
      } catch (Exception ex) {
         XViewerLog.log(Activator.class, Level.SEVERE, "Can't getUrlPageHtml");
         return simplePage("Exception opening url " + ex.getLocalizedMessage());
      }
   }

   public static String titledPage(String title, String text) {
      return simplePage("<head><title>" + title + "</title></head>" + text);
   }

   public static String pageEncoding(String html) {
      return HTTP_CHARSET_ENCODING + html;
   }

   public static String simplePage(String text) {
      return pageEncoding("<html>" + text + "</html>");
   }

   public static String simplePageNoPageEncoding(String text) {
      return "<html>" + text + "</html>";
   }

   public static String getLabelStr(String labelFont, String str) {
      return labelFont + "<b>" + textToHtml(str) + "</b></font>";
   }

   public static String getLabelValueStr(String labelFont, String label, String value) {
      return getLabelStr(labelFont, label) + value;
   }

   public static String getLabelValueStr(String label, String value) {
      return getLabelStr(LABEL_FONT, label + ":") + "&nbsp;&nbsp;" + value;
   }

   public static String color(String color, String str) {
      return "<font color=\"" + color + "\">" + str + "</font>";
   }

   public static String boldColor(String color, String str) {
      return "<font color=\"" + color + "\"><b>" + textToHtml(str) + "</b></font>";
   }

   public static String bold(String str) {
      return "<b>" + textToHtml(str) + "</b>";
   }

   public static String boldColorTags(String color, String str) {
      return "<font color=\"" + color + "\"><b>" + str + "</b></font>";
   }

   public static String imageBlock(String description, String filename) {
      String filenames[] = new String[1];
      filenames[0] = filename;
      return imageBlock(description, filenames);
   }

   public static String imageBlock(String description, String filenames[]) {
      StringBuilder str = new StringBuilder();
      str.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td>");
      if (!description.equals("")) {
         str.append(description);
         str.append(HtmlUtil.newline());
      }
      for (int i = 0; i < filenames.length; i++) {
         str.append("<IMG SRC=\"" + filenames[i] + "\"><br>");
      }
      str.append("</td></tr></table>");
      return str.toString();
   }

   public static String urlBlock(String description, String urls[]) {
      StringBuilder str = new StringBuilder("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
      if (!description.equals("")) {
         str.append(description);
         str.append(HtmlUtil.newline());
      }
      for (int i = 0; i < urls.length; i++) {
         str.append("<A HREF=\"" + urls[i] + "\">");
         str.append(urls[i]);
         str.append("</A><br>");
      }
      str.append("</td></tr></table>");
      return str.toString();
   }

   public static String heading(int heading, String str, String id) {
      return "<h" + heading + (id != null && !id.equals("") ? " id=\"" + id + "\"" : "") + ">" + textToHtml(
         str) + "</h" + heading + ">";
   }

   public static String heading(int heading, String str) {
      return heading(heading, str, null);
   }

   public static String padSpace(int num, String str) {
      StringBuilder out = new StringBuilder();
      for (int i = 0; i < num; i++) {
         out.append("&nbsp;");
      }
      out.append(str);
      return out.toString();
   }

   public static String addSpace(int num) {
      StringBuilder out = new StringBuilder();
      for (int i = 0; i < num; i++) {
         out.append("&nbsp;");
      }
      return out.toString();
   }

   public static String para(String str) {
      return "<p>" + textToHtml(str) + "</p>";
   }

   public static String italics(String str) {
      return "<i>" + textToHtml(str) + "</i>";
   }

   public static String pre(String str) {
      return "<pre>" + str + "</pre>";
   }

   public static String newline() {
      return newline(1);
   }

   public static String newline(int num) {
      StringBuilder str = new StringBuilder();
      for (int i = 0; i < num; i++) {
         str.append("<br />");
      }
      return str + "";
   }

   public static String name(int num) {
      return nameTarget("" + num);
   }

   public static String nameTarget(String str) {
      if (str == null) {
         return "";
      }
      return "<A NAME=\"" + str + "\">";
   }

   public static String nameLink(int num, String text) {
      return nameLink("" + num, text);
   }

   public static String nameLink(String name, String text) {
      return "<A HREF=\"#" + name + "\">" + text + "</A>";
   }

   public static String nameLink(String name) {
      return "<A HREF=\"#" + name + "\">" + name + "</A>";
   }

   public static String simpleTable(String str) {
      return simpleTable(str, 100);
   }

   /**
    * Create a table with one row/colum containing str
    */
   public static String simpleTable(String str, int width) {
      return "<table border=\"0\" cellpadding=\"3\" cellspacing=\"0\" width=\"" + width + "%\">" + "<tr><td>" + str + "</td></tr>" + "</table>";
   }

   /**
    * Create a table with one row/colum containing str
    */
   public static String borderTable(String str, int width, String bgcolor, String caption) {
      return startBorderTable(width, bgcolor, caption) + str + endBorderTable();
   }

   public static String startBorderTable(int width, String bgcolor, String caption) {
      String capStr = "";
      if (!caption.equals("")) {
         capStr = "<caption ALIGN=top>" + caption + "</caption>";
      }
      return "<table border=\"1\" align=\"center\" bgcolor=\"" + bgcolor + "\" cellpadding=\"3\" cellspacing=\"0\" width=\"" + width + "%\">" + capStr + "<tr><td>";
   }

   public static String endBorderTable() {
      return "</td></tr></table>";
   }

   /**
    * Create a table with one row multi column containing str[]
    *
    * @param str = array of strings for columns
    * @return Return multi-column table string
    */
   public static String multiColumnTable(String[] str) {
      return multiColumnTable(str, 85);
   }

   /**
    * Create a table with one row multi column containing str[]
    *
    * @param str - array of strings for columns
    * @param width - percent (1..100) of screen for table
    * @return Return multi-column table string
    */
   public static String multiColumnTable(String[] str, int width) {
      StringBuilder sb = new StringBuilder("<table border=\"0\" cellpadding=\"3\" cellspacing=\"0\" width=\"");
      sb.append(width);
      sb.append("%\"><tr>");
      for (int i = 0; i < str.length; i++) {
         sb.append("<td>");
         sb.append(str[i]);
         sb.append("</td>");
      }
      sb.append("</tr></table>");
      return sb.toString();
   }

   public static String beginMultiColumnTable(int width) {
      return beginMultiColumnTable(width, 0);
   }

   public static String beginMultiColumnTable(int width, int border) {
      return beginMultiColumnTable(width, border, null);
   }

   public static String beginMultiColumnTable(int width, int border, Integer color) {
      return "<table border=\"" + border + "\" " + (color != null ? "color=\"" + color + "\"" : "") + " cellpadding=\"3\" cellspacing=\"0\" width=\"" + width + "%\">";
   }

   public static String endMultiColumnTable() {
      return "</table>";
   }

   public static String addRowMultiColumnTable(String... str) {
      return addRowMultiColumnTable(str, null, null);
   }

   public static String addRowMultiColumnTable(String[] str, String[] colOptions) {
      return addRowMultiColumnTable(str, colOptions, null);
   }

   public static String addRowMultiColumnTable(String[] str, String[] colOptions, String backgroundColor) {
      StringBuilder sb = new StringBuilder("<tr>");
      if (backgroundColor != null) {
         sb.append("<tr bgcolor=\"");
         sb.append(backgroundColor);
         sb.append("\">");
      }
      String show = "";
      for (int i = 0; i < str.length; i++) {
         show = str[i];
         if (show == null || show.equals("")) {
            show = HtmlUtil.addSpace(1);
         }
         String colOptionStr = "";
         if (colOptions != null) {
            colOptionStr = colOptions[i];
         }
         sb.append("<td");
         sb.append(((colOptionStr != null && !colOptionStr.equals("")) ? colOptionStr : ""));
         sb.append(">");
         sb.append(show);
         sb.append("</td>");
      }
      sb.append("</tr>");
      return sb.toString();
   }

   public static String addRowSpanMultiColumnTable(String str, int span) {
      return "<tr><td colspan=" + span + ">" + str + "</td></tr>";
   }

   public static class CellItem {
      String text;
      private final String fgColor;
      private final String bgColor;

      public CellItem(String text) {
         this(text, null, null);
      }

      public CellItem(String text, String fgColor, String bgColor) {
         this.text = text;
         this.fgColor = fgColor;
         this.bgColor = bgColor;
      }
   }

   public static String addRowMultiColumnTable(Collection<CellItem> items) {
      StringBuilder s = new StringBuilder("<tr>");
      for (CellItem item : items) {
         if (item.text == null || "".equals(item.text)) {
            item.text = ".";
         }
         if (item.bgColor != null) {
            s.append("<td bgcolor=\"" + item.bgColor + "\">");
         } else {
            s.append("<td>");
         }
         s.append(HtmlUtil.color(item.fgColor, item.text));
         s.append("</td>");
      }
      s.append("</tr>");
      return s.toString();
   }

   public static String addHeaderRowMultiColumnTable(String[] str) {
      return addHeaderRowMultiColumnTable(str, null);
   }

   public static String addHeaderRowMultiColumnTable(String[] str, Integer width[]) {
      StringBuilder s = new StringBuilder("<tr>");
      String widthStr = "";
      for (int i = 0; i < str.length; i++) {
         if (width != null) {
            widthStr = " width =\"" + width[i] + "\"";
         }
         s.append("<th");
         s.append(widthStr);
         s.append(">");
         s.append(str[i]);
         s.append("</th>");
      }
      s.append("</tr>");
      return s.toString();
   }

   public static String addSimpleTableRow(String str) {
      return "<tr><td>" + str + "</td></tr>";
   }

   public static String beginSimpleTable() {
      return "<table border=\"0\" cellpadding=\"10\" cellspacing=\"0\" width=\"100%\">";
   }

   public static String beginSimpleTable(int border, int width) {
      return "<table border=\"" + border + "\" cellpadding=\"10\" cellspacing=\"0\" width=\"+width+%\">";
   }

   public static String endSimpleTable() {
      return "</table>";
   }

   public static String createTable(List<String> datas, String[] headers, int numColumns, int cellPadding, int border) {
      StringBuilder table = new StringBuilder(begin);

      if (datas == null) {
         throw new IllegalArgumentException("The data can not be null");
      }
      if (datas.size() % numColumns != 0) {
         throw new IllegalArgumentException(
            "The table could not be created becuase the data does not match the column size");
      }
      if (border > 0) {
         table.append("border=\"" + border + "\"");
      }
      if (cellPadding > 0) {
         table.append("cellpadding=\"" + cellPadding + "\"");
      }
      table.append(">");

      if (headers != null && headers.length == numColumns) {
         table.append("<tr>");
         for (String header : headers) {
            table.append("<th>" + header + "</th>");
         }
         table.append("</tr>");
      }

      int colIndex = 0;
      for (String data : datas) {

         if (colIndex == 0) {
            table.append("<tr>");
         }
         table.append("<td>" + data + "</td>");
         colIndex++;

         if (colIndex == numColumns) {
            table.append("</tr>");
            colIndex = 0;
         }
      }
      return table.toString();
   }

   public static String getPreData(String data) {
      return "<style>pre { white-space: pre-wrap;       /* CSS 3 */ white-space: -moz-pre-wrap;  /* Mozilla, since 1999 */ white-space: -pre-wrap;      /* Opera 4-6 */ white-space: -o-pre-wrap;    /* Opera 7 */ word-wrap: break-word;       /* Internet Explorer 5.5+ */ }</style>" + HtmlUtil.pre(
         HtmlUtil.textToHtml(data));
   }

}