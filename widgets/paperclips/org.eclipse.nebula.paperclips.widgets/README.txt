=========================================
PaperClips UI 1.0.3
http://www.eclipse.org/nebula/paperclips/
=========================================

Copyright (c) 2005-2006 Matthew Hall and others.

 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0

A copy is found in the file epl-2.0.html distributed in this package.

Contributors:
  Matthew Hall <matthall@woodcraftmill.com> - initial API and implementation

This copyright notice MUST APPEAR in all copies of the file!

Introduction
------------

Welcome to PaperClips UI.  Currently this project contains 2 main widgets for
you to use to display documents: PrintPreview (a true WYSIWYG preview) and
PrintViewer, a scrollable on-screen viewer.

Requirements:
* Java 1.4 or later.
* SWT 3.2 or later.  SWT may be downloaded at http://www.eclipse.org/swt/.
* The org.eclipse.nebula.paperclips plugin must be available in the classpath
  (for regular java apps) or in the plugins folder of your target platform
  (for Eclipse apps).

Installation
------------

* Ensure that the org.eclipse.nebula.paperclips plugin is available on the
  target platform or as a plug-in project in your workspace.
* Extract the project files to an empty folder.
* Open Eclipse.
* Select File->Import
* Select Plugin Development->Plug-ins and Fragments and click Next.
* Select the folder you extracted the project files into as the plug-in
  location.  You may have to uncheck "The target platform" first.
* Under "Import As", select "Project with source folders" and click Next.
* Add the org.eclipse.nebula.paperclips.ui plugin to the import list.
* Click Finish

Credits
-------

PaperClips development team:
    Matthew Hall <matthall@woodcraftmill.com> - Developer / Project Admin
