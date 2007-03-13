set PATH=C:\Program Files\Java\jdk1.5.0_04\bin;%PATH%;C:\apache-ant-1.7.0\bin;c:\build\bin

cd\build

ant -buildfile masterbuild.xml -Dpassword=xxxxxx -Declipsehome=c:\eclipse33m5\eclipse
