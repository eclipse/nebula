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

@RELEASE_VERSION='0.9.4'

@ZIP_EXE='/usr/bin/zip'
@MV_EXE='/bin/mv'
@RM_EXE='/bin/rm'
@MKDIR_EXE='/bin/mkdir'

@JAVA_EXE='/usr/local/java/current/bin/java'
@JAR_EXE='/usr/local/java/current/bin/jar'
@JAVADOC_EXE='/usr/local/java/current/bin/javadoc'

@ECLIPSE_HOME='/home/djo/bin/eclipse/current'
@SEP=':'  # Platform path separator

@COMPOSITETABLE_JAR='nebula_compositetable.jar'

@SWT='org.eclipse.swt_3.3.2.v3347.jar'
@SWT_NATIVE='org.eclipse.swt.gtk.linux.x86_3.3.2.v3347.jar'

@CLASSPATH="#{@COMPOSITETABLE_JAR}#{@SEP}../org.eclipse.swt.nebula.snippets/bin#{@SEP}#{@ECLIPSE_HOME}/plugins/#{@SWT}#{@SEP}#{@ECLIPSE_HOME}/plugins/#{@SWT_NATIVE}"
@SWT_LIB_PATH="#{@ECLIPSE_HOME}/configuration/org.eclipse.osgi/bundles/57/1/.cp"

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
	print `#{@MV_EXE} snippets.zip ../../org.eclipse.nebula.widgets.compositetable`
end

# Create README file
File.open('README', 'w') {|f| f << @README }

# Create JavaDoc
command="#{@JAVADOC_EXE} -classpath '#{@CLASSPATH}#{@SEP}./bin' -d JavaDoc -sourcepath src org.eclipse.swt.nebula.widgets.compositetable"
print command
print `#{command}`

# Create CompositeTable download zip
print `#{@ZIP_EXE} -r #{@DOWNLOAD_ZIP} #{@COMPOSITETABLE_JAR} src.zip snippets.zip JavaDoc README epl-v10.html`

# Make sure we can run using the JAR we just built
command="#{@JAVA_EXE} -classpath '#{@CLASSPATH}'  -Djava.library.path='#{@SWT_LIB_PATH}' org.eclipse.swt.nebula.snippets.compositetable.CompositeTableSnippet0"
print command
print `#{command}`

# cleanup
print `#{@RM_EXE} -fr #{@COMPOSITETABLE_JAR} src.zip snippets.zip README bin_o JavaDoc`

