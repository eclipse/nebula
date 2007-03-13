set ECLIPSE_HOME=c:\eclipse33m5\eclipse
java -cp %ECLIPSE_HOME%\startup.jar org.eclipse.core.launcher.Main -application org.eclipse.ant.core.antRunner -buildfile build.xml -DbaseLocation=%ECLIPSE_HOME% build.update.jar
