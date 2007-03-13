set PATH=C:\Program Files\Java\jdk1.5.0_04\bin;%PATH%;c:\build\bin
set ECLIPSE_HOME=c:\eclipse33m5\eclipse

cd\build

java -cp %ECLIPSE_HOME%\startup.jar org.eclipse.core.launcher.Main -application org.eclipse.ant.core.antRunner -buildfile masterbuild.xml -DbaseLocation=%ECLIPSE_HOME% -logfile log.txt -Dpassword=soldier*

