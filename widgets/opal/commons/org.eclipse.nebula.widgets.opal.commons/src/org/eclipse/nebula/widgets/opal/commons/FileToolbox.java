/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.commons;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class FileToolbox {

    /**
     * Loads a file into a stream
     * 
     * @param fileName file name
     * @return a stream composed of this file
     */
    public static InputStream getInputStream(final String fileName) {
        if (fileName.startsWith("jar:")) {
            URL url;
            try {
                url = new URL(fileName);
                return url.openStream();
            } catch (final MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                return new FileInputStream(fileName);
            } catch (final FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
