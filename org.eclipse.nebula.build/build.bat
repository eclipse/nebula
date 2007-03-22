set ECLIPSE_HOME=d:\nebula_build\eclipse33m5\eclipse
set JAVA="D:\ibm-jdk5.0-sr3"
set PATH=%JAVA%\bin;%PATH%;%ECLIPSE_HOME%\plugins\org.apache.ant_1.6.5\lib;.
java -cp %ECLIPSE_HOME%\startup.jar;bin\jsch-0.1.32.jar;%ECLIPSE_HOME%\plugins\org.eclipse.swt_3.3.0.v3325g.jar;%ECLIPSE_HOME%\plugins\org.eclipse.swt.win32.win32.x86_3.3.0.v3325.jar org.eclipse.core.launcher.Main -application org.eclipse.ant.core.antRunner -buildfile masterbuild.xml -DbaseLocation=%ECLIPSE_HOME% -logfile ../../log.txt -Dpassword=XXXX