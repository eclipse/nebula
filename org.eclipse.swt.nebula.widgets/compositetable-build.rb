#!/usr/bin/ruby

# Auto-build script for creating compositetable.zip download.  Assumes
# that CWD is the root of the Nebula widgets project and that the snippets
# project is checked out in the same workspace.  Also assumes that it is
# running in a Unix-like environment or in Cygwin.

# ---Configuration variables---

@JAR_EXE='/cygdrive/C/java/bin/jar'
@ZIP_EXE='/usr/bin/zip'
@MV_EXE='/usr/bin/mv'
@RM_EXE='/usr/bin/rm'
@MKDIR_EXE='/usr/bin/mkdir'
@JAVA_EXE='/cygdrive/C/java/bin/java'
@SWT_LIB_PATH='C:\eclipse\eclipse\configuration\org.eclipse.osgi\bundles\79\1\.cp'

@COMPOSITETABLE_JAR='compositetable.jar'

@README=<<README
Welcome to CompositeTable, a Nebula control sponsored by Coconut Palm Software!
Coconut Palm Softwre may be found at http://www.coconut-palm-software.com

Thanks for trying CompositeTable!  I hope you like it and that it meets your 
needs in an advanced tabular input control for SWT.

For CompositeTable support, please post to the eclipse.technology.nebula 
newsgroup with [CompositeTable] in the subject.

Inside this archive you will find the JAR library for CompositeTable 
(#{@COMPOSITETABLE_JAR}), the sources (src.zip), and a copy of the current 
snippets (snippets.zip) as of the time when the CompositeTable library was 
built.
README

# ---End configuration---

@ORIG="/org/eclipse/swt/nebula/widgets/"
@SNIPPETS="/org/eclipse/swt/nebula/snippets/compositetable"

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

# Create CompositeTable download zip
print `#{@ZIP_EXE} compositetable.zip #{@COMPOSITETABLE_JAR} src.zip snippets.zip README`

# Make sure we can run using the JAR we just built
print `#{@JAVA_EXE} -classpath '#{@COMPOSITETABLE_JAR};../org.eclipse.swt.nebula.snippets/bin;c:/eclipse/eclipse/plugins/org.eclipse.swt_3.2.1.v3235e.jar;C:/eclipse/eclipse/plugins/org.eclipse.swt.win32.win32.x86_3.2.1.v3235.jar'  -Djava.library.path='#{@SWT_LIB_PATH}' org.eclipse.swt.nebula.snippets.compositetable.CompositeTableSnippet0`

# cleanup
print `#{@RM_EXE} -fr #{@COMPOSITETABLE_JAR} src.zip snippets.zip README bin_o`

