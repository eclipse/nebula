The following are the instructions to follow to add a new widget project to the 
automated nebula build.

1. Right click on your META-INF\MANIFEST.MF.
2. Select PDE TOOLS -> Create ANT Build File. This should add a new build.xml file to your project. 
3. Define a new nebula.properties file. 
4. Edit masterbuild.xml and add a line to the "buildall" target.


nebula.properties file should contain the following properties.
nebula.productname -> name of the project/jar to create
javadoc.packages -> the comma seperated list of packages that should javadoc generated
javadoc.exclude -> the comma seperated list of packages that should be ignored by javadoc
snippets.path -> the directory/path to the folder of snippets for the widget
