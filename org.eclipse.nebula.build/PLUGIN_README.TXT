The following are the instructions to follow to add a new widget project to the 
automated nebula build.

1. Right click on your META-INF\MANIFEST.MF.
2. Select PDE TOOLS -> Create ANT Build File. This should add a new build.xml file to your project.
3. Remove any path references in the newly created javaCompiler..args file that point to your 
   computer.  Follow the examples in existing components such as grid or gallery.  
4. Add the following properties the end of build.properties in your project:
	nebula.properties file should contain the following properties.
	nebula.productname -> name of the project/jar to create
	javadoc.packages -> the comma seperated list of packages that should javadoc generated
	javadoc.exclude -> the comma seperated list of packages that should be ignored by javadoc
	snippets.path -> the directory/path to the folder of snippets for the widget
5. Edit masterbuild.xml and add a line to the "buildall" target.


..