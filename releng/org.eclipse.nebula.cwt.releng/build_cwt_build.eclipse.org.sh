#!/bin/bash

# Parameters
nebulaProjectId="cwt"
relengProjectPath="org.eclipse.swt.nebula/releng/org.eclipse.nebula.cwt.releng"
projectid="technology.nebula.$nebulaProjectId"
sub="nebula.$nebulaProjectId"
version="0.9.0";
writableBuildRoot="$HOME/nebula_builds";
buildType="N"
#JAVA_HOME="/opt/public/common/jdk-1.5.0_16"
JAVA_HOME="/opt/public/common/ibm-java2-142"

buildTimestamp=`date +%Y%m%d%H%M`
if [[ ! $downloadsDir ]]; then downloadsDir="${writableBuildRoot}/downloads"; fi
if [[ ! $thirdPartyJarsDir ]]; then thirdPartyJarsDir="${writableBuildRoot}/3rdPartyJars"; fi
if [[ ! $buildDir ]]; then
	buildDir="${writableBuildRoot}/${projectid//.//}/downloads/drops/${version}/${buildType}${buildTimestamp}"
fi

startPath=".";
if [[  -f ${writableBuildRoot}/org.eclipse.dash.common.releng/tools/scripts/start_logger.sh ]]; then 
   startPath="${writableBuildRoot}/org.eclipse.dash.common.releng/tools/scripts";
fi

echo "Starting in $startPath";

$startPath/start.sh -projectid $projectid -sub $sub -version $version  -antTarget runWithoutTestBuild -projRelengRoot ':pserver:anonymous@dev.eclipse.org:/cvsroot/technology'    -projRelengPath "$relengProjectPath" -javaHome $JAVA_HOME -writableBuildRoot $writableBuildRoot  -buildTimestamp $buildTimestamp
buildResult=$?

ls $buildDir/*

if [ $buildResult -eq "0" ]; then
   echo "Build successful";

   unzip -o $buildDir/*-Update-*.zip -d ~/downloads/technology/nebula/$nebulaProjectId/update-$buildType/
     
   rm $buildDir/*Update*
   rm $buildDir/*Master*
   rm $buildDir/*ALL*

   rsync -aP $buildDir ~/downloads/technology/nebula/$nebulaProjectId/downloads/drops/

   cd $buildDir
   for file in *SDK*.zip
     do
       mv "$file" `echo "$file" | sed 's/-N[0-9]*//g'`
     done

     rsync -aP --delete-after $buildDir/ ~/downloads/technology/nebula/$nebulaProjectId/downloads/drops/latest/
fi
