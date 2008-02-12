/*******************************************************************************
 * Copyright (c) 2005, 2008 Eric Wuillai.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eric Wuillai (eric@wdev91.com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.formattedtext;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.SWT;

/**
 * Factory for the default formatters.<p>
 * 
 * This factory is called by <code>FormattedText</code> when a value is setted
 * and no formatter has been provided by constructor or <code>setFormatter()</code>.
 * The default formatter is based on the class of the value. The  cache is
 * searched first for the exact value's class. If no formatter is found, then
 * it is searched for any class that is an ancestor of the value's class.
 * The cache is a <code>HashMap</code> and the order of search cannot be
 * garanteed. The first valid ancestor found is returned.<p>
 * 
 * Default formatters provided by the factory are:
 * <ul>
 * <li><code>DateFormatter</code> for <code>Date</code> values</li>
 * <li><code>NumberFormatter</code> for <code>Number</code> values</li>
 * </ul>
 * 
 * Other formatters can be added with the <code>register()</code> method. Each
 * formatter class must implement <code>ITextFormatter</code> and must have a
 * default constructor.<p>
 * 
 * This class is not intended to be instanciated.
 */
public abstract class DefaultFormatterFactory {
	/** Cache of default formatter classes */
  private static HashMap formatters;

  static {
    formatters = new HashMap();
    formatters.put(String.class, StringFormatter.class);
    formatters.put(Date.class, DateFormatter.class);
    formatters.put(Number.class, NumberFormatter.class);
  }

  private DefaultFormatterFactory() {
  }

  /**
   * Creates a new default formatter for the given value. The formatter is based
   * on the value's class.
   * 
   * @param value Value for which to create a formatter
   * @return New formatter corresponding to the value's class, or null if the
   * class is unknown.
   */
  public static ITextFormatter createFormatter(Object value) {
    return createFormatter(value.getClass());
  }

  /**
   * Creates a new default formatter for the given class.
   * 
   * @param c Class for which to create a formatter
   * @return New formatter corresponding to the class, or null if the class is unknown.
   */
  public static ITextFormatter createFormatter(Class c) {
  	ITextFormatter f = null;
    Class fc = (Class) formatters.get(c);
    if ( fc == null ) {
    	for (Iterator it = formatters.keySet().iterator(); it.hasNext(); ) {
    		Class k = (Class) it.next();
        if ( k.isAssignableFrom(c)
       		 && (fc == null || fc.isAssignableFrom(k)) ) {
         fc = (Class) formatters.get(k);
       }
    	}
    }

    if ( fc != null ) {
      try {
        f = (ITextFormatter) fc.newInstance();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return f;
  }

  /**
   * Registers a new formatter class for a given class of values. The formatter
   * class must implement the <code>ITextFormatter</code> interface.
   * 
   * @param c Class of values
   * @param f Class of the formatter
   */
  public static void register(Class c, Class f) {
  	if ( c == null ) SWT.error(SWT.ERROR_NULL_ARGUMENT);
    if ( ! ITextFormatter.class.isAssignableFrom(f) ) {
    	SWT.error(SWT.ERROR_INVALID_ARGUMENT, null, "Must be an ITextFormatter"); //$NON-NLS-1$
    }
    formatters.put(c, f);
  }
}
