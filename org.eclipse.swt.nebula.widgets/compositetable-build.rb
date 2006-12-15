#!/usr/bin/ruby
#
#  Copyright (C) 2006 by Coconut Palm Software, Inc.  <djo@coconut-palm-software.com>
#  
#  All rights reserved. This program and the accompanying materials
#  are made available under the terms of the Eclipse Public License v1.0
#  which accompanies this distribution, and is available at
#  http://www.eclipse.org/legal/epl-v10.html
# 
#  Contributors:
#      Coconut Palm Software, Inc.     - Initial API and implementation
#

# Auto-build script for creating compositetable.zip download.  Assumes
# that CWD is the root of the Nebula widgets project and that the snippets
# project is checked out in the same workspace.  Also assumes that it is
# running in a Unix-like environment or in Cygwin.

# Other Nebula committers--feel free to make copies/versions for your controls

# ---Configuration variables---

@RELEASE_VERSION='0.8.1'

@JAR_EXE='/cygdrive/C/java/bin/jar'
@ZIP_EXE='/usr/bin/zip'
@MV_EXE='/usr/bin/mv'
@RM_EXE='/usr/bin/rm'
@MKDIR_EXE='/usr/bin/mkdir'
@JAVA_EXE='/cygdrive/C/java/bin/java'
@JAVADOC_EXE='/cygdrive/C/java/bin/javadoc'

@COMPOSITETABLE_JAR='nebula_compositetable.jar'

@CLASSPATH="#{@COMPOSITETABLE_JAR};../org.eclipse.swt.nebula.snippets/bin;c:/eclipse/eclipse/plugins/org.eclipse.swt_3.2.1.v3235e.jar;C:/eclipse/eclipse/plugins/org.eclipse.swt.win32.win32.x86_3.2.1.v3235.jar"
@SWT_LIB_PATH='C:\eclipse\eclipse\configuration\org.eclipse.osgi\bundles\79\1\.cp'

@DOWNLOAD_ZIP='nebula_compositetable_beta.zip'


@README=<<README
Copyright (C) 2005, 2006 by Coconut Palm Software, Inc.  
<djo@coconut-palm-software.com>

All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html
 
Contributors:
     Coconut Palm Software, Inc.  - Initial API, implementation
     Pampered Chef, Inc.          - Minor enhancements, bug fixes

-------------------------------------------------------------------------------

CompositeTable #{@RELEASE_VERSION}, an Eclipse Nebula control
http://www.eclipse.org/nebula

Thanks for trying CompositeTable!  We hope you like it and that it meets your 
needs in an advanced tabular input control for SWT.

For CompositeTable support, please post to the eclipse.technology.nebula 
newsgroup with [CompositeTable] in the subject.

Inside this archive you will find the JAR library for CompositeTable 
(#{@COMPOSITETABLE_JAR}), the sources (src.zip), JavaDoc (JavaDoc folder), 
and a copy of the current snippets (snippets.zip) as of the time when this
version (#{@RELEASE_VERSION}) of the CompositeTable library was built.
README

# ---End configuration---

@ORIG="/org/eclipse/swt/nebula/widgets/"
@SNIPPETS="/org/eclipse/swt/nebula/snippets/compositetable"

# Remove old download zip if it exists
print `#{@RM_EXE} -f #{@DOWNLOAD_ZIP}`

# Create binary jar
print `#{@RM_EXE} -fr bin_o`
print `#{@MKDIR_EXE} -p bin_o#{@ORIG}`
print `cp -R bin#{@ORIG}/compositetable bin_o#{@ORIG}`
Dir.chdir 'bin_o' do
	print `#{@JAR_EXE} cvf ../#{@COMPOSITETABLE_JAR} *`
end

# Create source zip
print `#{@ZIP_EXE} -r src.zip src#{@ORIG}compositetable`

# Create snippets zip
Dir.chdir '../org.eclipse.swt.nebula.snippets/src' do
	print `#{@ZIP_EXE} -r snippets.zip .#{@SNIPPETS}`
	print `#{@MV_EXE} snippets.zip ../../org.eclipse.swt.nebula.widgets`
end

# Create README file
File.open('README', 'w') {|f| f << @README }

# Create JavaDoc
command="#{@JAVADOC_EXE} -classpath '#{@CLASSPATH};./bin' -d JavaDoc -sourcepath src org.eclipse.swt.nebula.widgets.compositetable"
print command
print `#{command}`

# Create CompositeTable download zip
print `#{@ZIP_EXE} #{@DOWNLOAD_ZIP} #{@COMPOSITETABLE_JAR} src.zip snippets.zip JavaDoc README epl-v10.html`

# Make sure we can run using the JAR we just built
command="#{@JAVA_EXE} -classpath '#{@CLASSPATH}'  -Djava.library.path='#{@SWT_LIB_PATH}' org.eclipse.swt.nebula.snippets.compositetable.CompositeTableSnippet0"
print command
print `#{command}`

# cleanup
print `#{@RM_EXE} -fr #{@COMPOSITETABLE_JAR} src.zip snippets.zip README bin_o JavaDoc`

