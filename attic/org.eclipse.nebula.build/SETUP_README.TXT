To setup the nebula build follow these steps.

1. Setup a build directory
2. checkout the org.eclipse.nebula.build directory. This should result in a structure of 
%BUILD_DIRECTORY%/org.eclipse.swt.nebula/org.eclipse.nebula.build
3. add the following files to the org.eclipse.nebula.build directory
	3.1 cvs.exe
	3.2 zip.exe
	3.3 jsch-0.1.32.jar
	3.4 ant-contrib-1.0b3.jar
4. Modify the build.bat
	4.1 Define where the eclipse is installed
	4.2 set the java path location
5. execute build.bat

A log file of the build will be placed in %BUILD_DIR%\log.txt
Resulting build artifacts can be found in the %BUILD_DIR%\nebula.build folder.

