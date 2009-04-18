#!/bin/bash

# Parameters
source $1
if [[ ! $projectid ]]; then exit 1; fi
writableBuildRoot="$HOME/Documents/Nebula/build";



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

$startPath/start.sh $antTargetArgument -projectid $projectid -sub $sub -version $version  -projRelengRoot ':pserver:anonymous@dev.eclipse.org:/cvsroot/technology'    -projRelengPath "$relengProjectPath" -javaHome $JAVA_HOME -writableBuildRoot $writableBuildRoot  -buildTimestamp $buildTimestamp
buildResult=$?

ls $buildDir/*

if [ $buildResult -eq "0" ]; then

if [[ -f $buildDir/*.zip ]]; then 



     # rm $buildDir/*Update*
     # rm $buildDir/*Master*
     # rm $buildDir/*ALL*


     #rsync -aP --list-only $buildDir rnicolas@dev.eclipse.org:/home/data/httpd/download.eclipse.org/technology/nebula/$nebulaProjectId/downloads/drops/
     # rsync -aP --list-only $buildDir/ rnicolas@dev.eclipse.org:/home/data/httpd/download.eclipse.org/technology/nebula/$nebulaProjectId/downloads/drops/latest/

	ls $buildDir/*
  fi
fi