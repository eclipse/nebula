set HOME=d:\nebula_build
set ECLIPSE_HOME=d:\nebula_build\eclipse33m6\eclipse
set JAVA="D:\ibm-jdk5.0-sr3"
set PATH=%JAVA%\bin;%PATH%;%ECLIPSE_HOME%\plugins\org.apache.ant_1.6.5\lib;.
set CVSROOT=:pserver:anonymous@dev.eclipse.org:/cvsroot/technology
d:
cd\nebula_build\org.eclipse.swt.nebula
..\cvs update -C org.eclipse.nebula.build/masterbuild.xml
..\cvs update -C org.eclipse.nebula.build/common.xml
cd org.eclipse.nebula.build
java -cp %ECLIPSE_HOME%/plugins/org.eclipse.equinox.launcher_1.0.0.v20070319.jar;..\..\jsch-0.1.32.jar org.eclipse.core.launcher.Main -application org.eclipse.ant.core.antRunner -buildfile masterbuild.xml -DbaseLocation=%ECLIPSE_HOME% -logfile ../../log.txt -Dpassword=XXXX