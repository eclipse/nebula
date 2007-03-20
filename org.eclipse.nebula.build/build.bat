set ECLIPSE_HOME=d:\nebula_build\eclipse33m5\eclipse
set JAVA="D:\ibm-jdk5.0-sr3"
set PATH=%JAVA%\bin;%PATH%;%ECLIPSE_HOME%\plugins\org.apache.ant_1.6.5\lib;bin\
cvs -d:pserver:anonymous@dev.eclipse.org:/cvsroot/technology checkout org.eclipse.swt.nebula/org.eclipse.nebula.build
cd org.eclipse.swt.nebula/org.eclipse.nebula.build
java -cp %ECLIPSE_HOME%\startup.jar;bin\jsch-0.1.32.jar org.eclipse.core.launcher.Main -application org.eclipse.ant.core.antRunner -buildfile masterbuild.xml -DbaseLocation=%ECLIPSE_HOME% -logfile ../../log.txt -Dpassword=soldier*
cd ../../