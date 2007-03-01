set ECLIPSE_HOME=c:\eclipse33m5\eclipse
java -cp %ECLIPSE_HOME%\startup.jar org.eclipse.core.launcher.Main -application org.eclipse.ant.core.antRunner -buildfile build.xml -Dcomponent=nebula -Dconfigs="*,*,*" -Dbaseos=win32 -Dbasews=win32 -Dbasearch=x86 -Djavacfailonerror=true -Dpde.build.scripts=%ECLIPSE_HOME%/plugins/org.eclipse.pde.build_3.3.0.v20070209/scripts -DbaseLocation=%ECLIPSE_HOME%

