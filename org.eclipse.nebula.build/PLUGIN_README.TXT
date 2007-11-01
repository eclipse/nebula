The following are the instructions to follow to add a new project to the 
automated nebula build.

1. Add the following properties the end of build.properties in your project:
	nebula.productname -> name of the project/jar to create
	javadoc.packages -> the comma seperated list of packages that should javadoc generated
	javadoc.exclude -> the comma seperated list of packages that should be ignored by javadoc
	snippets.path -> the directory/path to the folder of snippets for the widget
5. Edit masterbuild.xml and add a line to the "buildall" target.
