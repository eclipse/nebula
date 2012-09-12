=========================================
PaperClips Core 1.0.4
http://www.eclipse.org/nebula/paperclips/
=========================================


Copyright (c) 2005-2009 Matthew Hall and others.
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html

A copy is found in the file epl-v10.html distributed in this package.

Contributors:
  Matthew Hall <matthall@woodcraftmill.com> - initial API and implementation

This copyright notice MUST APPEAR in all copies of the file!

Introduction
------------

Welcome to PaperClips: a simple, light weight, extensible Java printing library
for SWT.  PaperClips hides the complexity of laying out and rendering documents
on the printer, helping you focus on what to print instead of how to print it.

In a nutshell, PaperClips provides an assortment of document "building blocks",
which you can tweak and combine to form a custom document.  The assembled
document is then sent to PaperClips for printing.  PaperClips includes support
for printing text, images, borders, headers and footers, column layouts and
grid layouts, to name a few.  It can also be extended with your own printable
classes.

With PaperClips you do not have to track cursors, calculate line breaking, fool
around with font metrics, or manage system resources--it's all handled
internally. And unlike report-generation tools, you are not constrained to a
predefined document structure (like report bands).  Every document is custom
and the layout is up to you.

Requirements:
* Java 1.4 or later.
* SWT 3.2 or later.  SWT may be downloaded at http://www.eclipse.org/swt/.

Installation
------------

The PaperClips binary build jars can be used as regular jars, or as Eclipse
plugins.  If used as regular jars, the SWT classes must be available in the
classpath.

To use PaperClips as a binary plugin, simply save the jar into the plugins
folder of your target platform.

If you are extending PaperClips (or you want the latest bleeding edge
features), your best bet is to checkout the sources from from
git and import each project into your workspace.

Import these folders:
  org.eclipse.nebula.paperclips/           # core (required)
  org.eclipse.nebula.paperclips.widgets/   # screen widgets
  org.eclipse.nebula.paperclips.examples/  # snippets
  org.eclipse.nebula.paperclips.tests/     # unit tests

Credits
-------

PaperClips development team:
    Matthew Hall <matthall@woodcraftmill.com> - Developer / Project Admin
