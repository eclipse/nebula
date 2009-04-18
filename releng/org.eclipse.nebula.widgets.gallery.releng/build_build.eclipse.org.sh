#!/bin/bash

# Parameters
source $1
if [[ ! $projectid ]]; then exit 1; fi
writableBuildRoot="$HOME/nebula-builds";
JAVA_HOME="/opt/public/common/ibm-java2-ppc-50"

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

$startPath/start.sh -projectid $projectid -sub $sub -version $version  $antTargetArgument -projRelengRoot ':pserver:anonymous@dev.eclipse.org:/cvsroot/technology'    -projRelengPath "$relengProjectPath" -javaHome $JAVA_HOME -writableBuildRoot $writableBuildRoot  -buildTimestamp $buildTimestamp
buildResult=$?

ls $buildDir/*

if [ $buildResult -eq "0" ]; then
   echo "Build successful";
   # Create update site. 
   unzip -o $buildDir/*-Update-*.zip -d ~/downloads/technology/nebula/$nebulaProjectId/update-$buildType/
     
   # Create drop
   mkdir -v ~/downloads/technology/nebula/$nebulaProjectId/downloads/drops/${buildType}${buildTimestamp}
   cp -v $buildDir/*ALL*.* ~/downloads/technology/nebula/$nebulaProjectId/downloads/drops/${buildType}${buildTimestamp}/
   cp -v -R $buildDir/compilelogs ~/downloads/technology/nebula/$nebulaProjectId/downloads/drops/${buildType}${buildTimestamp}/
   cp -v -R $buildDir/testresults ~/downloads/technology/nebula/$nebulaProjectId/downloads/drops/${buildType}${buildTimestamp}/

   # Create latest
   rm -v -R ~/downloads/technology/nebula/$nebulaProjectId/downloads/drops/latest
   mkdir -v ~/downloads/technology/nebula/$nebulaProjectId/downloads/drops/latest
   cp -v $buildDir/*ALL*.* ~/downloads/technology/nebula/$nebulaProjectId/downloads/drops/latest/
   cp -v -R $buildDir/compilelogs ~/downloads/technology/nebula/$nebulaProjectId/downloads/drops/latest/
   cp -v -R $buildDir/testresults ~/downloads/technology/nebula/$nebulaProjectId/downloads/drops/latest/
   
   # Remove build id
   cd ~/downloads/technology/nebula/$nebulaProjectId/downloads/drops/latest
   for file in *ALL*.*
   do
       mv "$file" `echo "$file" | sed 's/-N[0-9]*//g'`
   done
     
fi
