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

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ryan D. Brooks
 */
public class MatchFilter implements FilenameFilter {
   protected Matcher matcher;

   public MatchFilter(String pattern) {
      this.matcher = Pattern.compile(pattern).matcher("");
   }

   public boolean accept(File dir, String fileName) {
      matcher.reset(fileName);
      return matcher.matches();
   }
}