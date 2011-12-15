#!/bin/bash
export PATH=/bin:/usr/bin/:/usr/local/bin
export CVS_RSH=/usr/bin/ssh
umask 002

# if building in Hudson, use ${WORKSPACE}/build
# if building on your own box commandline, use /tmp/build
# if building on build.eclipse.org commandline, use /opt/public/cbi/build or similar 
writableBuildRoot=/opt/public/cbi/build

#default values
version=""; # REQUIRED unless buildDir is specified
branch=HEAD
projRelengBranch="HEAD"; # default set below
commonRelengBranch="HEAD"; # default set below
basebuilderBranch="r35x_v20090811";
antTarget=run; # see other target options in ../../buildAll.xml, such as runWithoutTest (don't run tests) or runWithoutTestBuild (dont build or run tests)
buildAlias=""
buildType=N
javaHome=""
downloadsDir=""; # default set below
thirdPartyJarsDir=""; # default set below
buildTimestamp=`date +%Y%m%d%H%M`
buildDir=""; # default set below
email=""
noclean=0; # clean up temp files when done
quietCVS=-Q; # QUIET!
quietSVN=-q; # quiet
depsFile=""; # dependencies file 
projRelengRoot=":pserver:anonymous@dev.eclipse.org:/cvsroot/tools"; # default if not specified when building
topprojectName="";
vmargs=""; # allow extra vmargs or other flags to be passed through to Ant
Dflags="";

norm="\033[0;39m";
grey="\033[1;30m";
green="\033[1;32m";
brown="\033[0;33m";
yellow="\033[1;33m";
blue="\033[1;34m";
cyan="\033[1;36m";
red="\033[1;31m";

function usage()
{
	echo "usage: "${0##*/}
	echo "-projectid          <REQUIRED: projectid to build, eg., modeling.emf.cdo, tools.gef>"
	echo "-version            <REQUIRED: version to use, eg., 1.0.0>"
	echo ""
	echo "-buildAlias         <Alias of the build (for named S and R builds), eg. 2.0.2RC1; default: none>"
	echo "-buildType          <Type of the build: N, I, M, S, R; default: N>"
	echo ""
	echo "-projNamespace      <OPTIONAL: namespace for plugins, features, and .releng project; default: org.eclipse.\$projectName>"
	echo "-projRelengRoot     <OPTIONAL: CVSROOT of org.eclipse.\$subprojectName.releng; default: $projRelengRoot>"
	echo "-projRelengName     <OPTIONAL: org.eclipse.\$subprojectName.releng; default: org.eclipse.\$subprojectName.releng>"
	echo "-projRelengPath     <OPTIONAL: path/to/org.eclipse.\$subprojectName.releng; default: org.eclipse.\$projectName/org.eclipse.\$subprojectName.releng>"
	echo "-projRelengBranch   <CVS branch of org.eclipse.\$subprojectName.releng; default: $projRelengBranch>"
	echo "-commonRelengBranch <CVS branch of org.eclipse.dash.common.releng; default: $commonRelengBranch>"
	echo "-basebuilderBranch  <CVS branch of org.eclipse.releng.basebuilder, eg. R35_M7; default: $basebuilderBranch>"
	
	echo "-localSourceCheckoutDir"
	echo "                    <If you have a CVS dump of the whole project tree already checked out, specify that dir to skip checkouts>"
	# http://help.eclipse.org/ganymede/index.jsp?topic=/org.eclipse.pde.doc.user/tasks/pde_fetch_phase.htm
	echo "-fetchTag           <If you would like to build from HEAD or R2_0_maintenance instead of using the tags in the map files, set this branch/tag value>"
	echo "-forceContextQualifier"
	echo "                    <If you would like to override 1.4.0.<tagInMap> with a 1.4.0.vYYYYMMDDhhmm timestamp in feature/plugin names, use this flag>"
	echo ""
	echo "-URL                <URLs of the Eclipse driver, EMF driver, and any other zips that need to be unpacked"
	echo "                     into the eclipse install dir to resolve dependencies. Enter one -URL [...] per required URL.>"     
	echo ""
	echo "-javaHome           <JAVA_HOME directory; default: $JAVA_HOME, if set; or, project.releng/build.properties#JAVA_HOME"
	echo "                     or else commonbuilder/server.properties#JAVA_HOME>"
	echo "-downloadsDir       <Dir where dependent zips are downloaded; default: \$writableBuildRoot/downloads>"
	echo "-thirdPartyJarsDir  <Dir with non-EPL jars, eg., ant-contrib.jar; default: \$writableBuildRoot/3rdPartyJars>"
	echo "-writableBuildRoot  <Dir where builds will occur, default: /opt/public/cbi/build (commandline) or \${WORKSPACE}/build (Hudson) >"
	echo "-buildDir           <Dir of this build; default: \$downloadsDir/drops/\$version/\$buildType\$buildTimestamp>"
	echo "-buildTimestamp     <YYYYmmddhhMM timestamp to be used to label the build; default will be generated>"
	echo ""
	echo "-email              <Email address(es) to be contacted when the tests complete. Separate multiple w/ commas>"
	echo "-noclean            <DON'T clean up temp files after build>"
	echo "-addSDK             <If used, add the resulting SDK zip to the specified dependencies file for use with other builds>"
	echo "-thirdPartyDownloadLicenseAcceptance  <If used, will force build to fetch third party deps like ant-contrib)>" 
	echo ""

	echo -e "${yellow}example 1: build in /tmp/build instead of in /opt/public/cbi/build${norm}"
	echo "./start_logger.sh -projectid tools.gef -version 3.4.0 \\"
	echo "  -projRelengRoot ':pserver:anonymous@dev.eclipse.org:/cvsroot/technology' \\"
	echo "  -projRelengPath 'org.eclipse.dash/athena/org.eclipse.dash.commonbuilder/org.eclipse.gef.releng' \\"
 	echo -e "${blue}  -writableBuildRoot /tmp/build ${norm}&"
	echo "" 

	echo -e "${yellow}example 2: build a Stable build with renamed zips (eg., GEF-SDK-3.4.0RC3.zip)${norm}"
	echo "./start_logger.sh -projectid tools.gef -version 3.4.0 \\"
	echo "  -projRelengRoot ':pserver:anonymous@dev.eclipse.org:/cvsroot/technology' \\"
	echo "  -projRelengPath 'org.eclipse.dash/athena/org.eclipse.dash.commonbuilder/org.eclipse.gef.releng' \\"
	echo -e "${blue}  -buildType S -buildAlias 3.4.0RC3 ${norm}&"
	echo ""

	echo -e "${yellow}example 3: use alternate requirement URL(s) (use multiple -URL for more than one input not in build.properties)${norm}"
	echo ./${0##*/}" -projectid tools.gef -version 3.4.0 \\"
	echo "  -projRelengRoot ':pserver:anonymous@dev.eclipse.org:/cvsroot/technology' \\"
	echo "  -projRelengPath 'org.eclipse.dash/athena/org.eclipse.dash.commonbuilder/org.eclipse.gef.releng' \\"
	echo -e "${blue}  -URL http://download.eclipse.org/eclipse/downloads/drops/S-3.5M5-200902021535/eclipse-SDK-3.5M5-linux-gtk-ppc.tar.gz${norm} \\"
	echo "  2>&1 | tee /tmp/buildlog_\`date +%H%M%S\`.txt"
	echo ""
	
	echo -e "${yellow}example 4: build from local sources, with specific JVM and basebuilder tag ${norm}"
	echo ./${0##*/}" -projectid tools.gef -version 3.4.0 \\"
	echo "  -projRelengRoot ':pserver:anonymous@dev.eclipse.org:/cvsroot/technology' \\"
	echo "  -projRelengPath 'org.eclipse.dash/athena/org.eclipse.dash.commonbuilder/org.eclipse.gef.releng' \\"
	echo -e "${blue}  -javaHome /opt/public/common/ibm-java2-142 -basebuilderBranch R35_M7${norm} \\"
	echo -e "${blue}  -localSourceCheckoutDir ~/workspace/org.eclipse.gef${norm} \\"
	echo "  2>&1 | tee /tmp/buildlog_\`date +%H%M%S\`.txt"
	
	echo ""
	echo -e "${yellow}example 5: build on macosx ${norm}"
	echo "./start_logger.sh -projectid tools.gef -version 3.4.0 \\"
	echo "  -projRelengRoot ':pserver:anonymous@dev.eclipse.org:/cvsroot/technology' \\"
	echo "  -projRelengPath 'org.eclipse.dash/athena/org.eclipse.dash.commonbuilder/org.eclipse.gef.releng' \\"
	echo -e "${blue}  -basebuilderBranch R35_M7${norm} \\"
	echo -e "${blue}  -URL http://download.eclipse.org/eclipse/downloads/drops/S-3.5M5-200902021535/eclipse-SDK-3.5M5-macosx-carbon.tar.gz${norm} \\"
	echo -e "${blue}  -javaHome /System/Library/Frameworks/JavaVM.framework/Home${norm} \\"
	echo -e "${blue}  -localSourceCheckoutDir ~/Documents/workspace/org.eclipse.gef.tree${norm} \\"
	echo -e "${blue}  -writableBuildRoot /tmp/build${norm} &"
	
	exit 1
}

if [[ $# -eq 0 ]]; then
	usage;
fi

# check for required platform stuff
missingRequirements=""
for f in cvs svn javac wget vncserver Xvfb Xvnc; do 
	check=$(${f} 2>&1 &); 
	check2=$(whereis $f); 
	if [[ ${check%%command not found} != ${check} ]]; then # not found
		if [[ $check2 == "$f:" ]]; then # not found
			missingRequirements="$missingRequirements $f"
		else
			echo "Found $check2"
		fi
	else
			echo "Found $f in PATH="
			echo ${PATH};
	fi
done
if [[ $missingRequirements ]]; then
	echo "WARNING: You may require 1 or more of the following tools for this build to proceed:"
	echo " $missingRequirements"
fi

echo "[start] [`date +%H\:%M\:%S`] Started on `date +%Y%m%d` with these options:"
# Create local variable based on the input
while [ "$#" -gt 0 ]; do
	case $1 in
		'-writableBuildRoot') 	
			writableBuildRoot=$2; 
			echo "   $1 $2"; 
			shift 1
			;;
		'-projectid')
			projectid=$2;
			# if x.y -> top.proj, proj=sub
			if [[ ${projectid%.*} == ${projectid%%.*} ]]; then # two-part projectid; single-match trim .* and greedy-match trim .* are the same
				topprojectName=${projectid%%.*}; # get first chunk
				projectName=${projectid##*.}; # get last chunk
				subprojectName=${projectName}; # proj == sub
			else # assume x.y.z -> top.proj.sub 
				topprojectName=${projectid%%.*}; # get first chunk
				subprojectName=${projectid##*.}; # get last chunk
				projectName=${projectid#${topprojectName}.}; # trim first chunk
				projectName=${projectName%.${subprojectName}}; # trim last chunk
			fi				
			#echo "Got: $topprojectName / $projectName / $subprojectName";
			echo "   $1 $2";
			shift 1
			;;
		'-top')
			topprojectName=$2;
			echo "   $1 $2";
			shift 1
			;;
		'-proj')
			projectName=$2;
			echo "   $1 $2";
			shift 1
			;;
		'-sub')
			subprojectName=$2;
			echo "   $1 $2";
			shift 1
			;;
		'-version')
			version=$2;
			echo "   $1 $2";
			shift 1
			;;
		'-branch')
			branch=$2;
			echo "   $1 $2";
			shift 1
			;;
		'-URL')
			if [ "x$dependencyURLs" != "x" ]; then dependencyURLs="$dependencyURLs,"; fi
			dependencyURLs=$dependencyURLs"$2";
			echo "   $1 $2";
			shift 1
			;;
		'-javaHome'|'-JAVA_HOME')
			javaHome=$2;
			echo "   $1 $2";
			shift 1
			;;
		'-buildAlias')
			buildAlias=$2;
			echo "   $1 $2";
			shift 1
			;;
		'-buildType')
			buildType=$2;
			echo "   $1 $2";
			shift 1
			;;
		'-buildDir')
			buildDir=$2;
			echo "   $1 $2";
			shift 1
			;;
		'-downloadsDir')
			downloadsDir=$2;
			echo "   $1 $2";
			shift 1
			;;
		'-thirdPartyJarsDir')
			thirdPartyJarsDir=$2;
			echo "   $1 $2";
			shift 1
			;;
		'-buildTimestamp')
			buildTimestamp=$2;
			echo "   $1 $2";
			shift 1
			;;
		'-email')
			email=$2;
			echo "   $1 $2";
			shift 1
			;;
			
		'-basebuilderBranch')
			basebuilderBranch=$2;
			echo "   $1 $2";
			shift 1
			;;
		'-projNamespace')
			projNamespace=$2;
			echo "   $1 $2";
			shift 1
			;;
		'-projRelengRoot')
			projRelengRoot=$2;
			echo "   $1 $2";
			shift 1
			;;
		'-projRelengName')
			projRelengName=$2;
			echo "   $1 $2";
			shift 1
			;;
		'-projRelengPath')
			projRelengPath=$2;
			echo "   $1 $2";
			shift 1
			;;
		'-projRelengBranch')
			projRelengBranch=$2;
			echo "   $1 $2";
			shift 1
			;;
		'-commonRelengBranch')
			commonRelengBranch=$2;
			echo "   $1 $2";
			shift 1
			;;
		'-noclean')
			noclean=1;
			echo "   $1";
			shift 0
			;;
		'-localSourceCheckoutDir')
			localSourceCheckoutDir=$2;
			echo "   $1 $2";
			shift 1
			;;
		'-relengBuilderDir')
			relengBuilderDir=$2;
			echo "   $1 $2";
			shift 1
			;;
		'-fetchTag') # support building from a branch/tag instead and of from maps; fetchTag = HEAD or R2_0_maintenance to make checkouts ignore tags in maps
			fetchTag=$2; # see http://help.eclipse.org/ganymede/index.jsp?topic=/org.eclipse.pde.doc.user/tasks/pde_fetch_phase.htm
			echo "   $1 $2";
			shift 1
			;;
		'-forceContextQualifier') # override 1.4.0.HEAD with a 1.4.0.vYYYYMMDDhhmm in feature/plugin names
			forceContextQualifier="v"$buildTimestamp; 
			echo "   $1";
			shift 0
			;;

		'-thirdPartyDownloadLicenseAcceptance')
			thirdPartyDownloadLicenseAcceptance="Y"
			echo "   $1";
			shift 0
			;;
			
		'-addSDK')
			depsFile="$2";
			echo "   $1 $2";
			shift 1
			;;
		'-vmargs') # everything after this flag will be collected as a vmarg
			echo "-vmargs"; 
			while [ "$#" -gt 0 ]; do
				shift 1
				vmargs=$vmargs" "$1;
				echo "$1"
			done
			;;
		'-D'*)
			Dflags=$Dflags" $1 $2";
			echo "   $1 $2";
			shift 1
			;;
	esac
	shift 1
done	

if [[ ! $projectid ]] || [[ ! $version ]]; then usage; fi

#################################### DEFINE REQUIRED VARIABLES AND PATHS BASED ON INPUT ####################################
 
if [[ ! $projNamespace ]]; then
	projNamespace="org.eclipse.${projectName}"
fi
if [[ ! $projRelengName ]]; then
	projRelengName="org.eclipse.${subprojectName}.releng"
fi
if [[ ! $projRelengPath ]]; then
	projRelengPath="${projNamespace}/${projRelengName}"
fi

# set environment variables
export HOME=$writableBuildRoot

# set defaults from input values
if [[ ! $downloadsDir ]]; then downloadsDir="${writableBuildRoot}/downloads"; fi
if [[ ! $thirdPartyJarsDir ]]; then thirdPartyJarsDir="${writableBuildRoot}/3rdPartyJars"; fi
if [[ ! $buildDir ]]; then
	buildDir="${writableBuildRoot}/${projectid//.//}/downloads/drops/${version}/${buildType}${buildTimestamp}"
fi

# *.releng directories
relengBaseBuilderDir=${writableBuildRoot}/org.eclipse.releng.basebuilder
relengCommonBuilderDir=${writableBuildRoot}/org.eclipse.dash.common.releng
relengCommonScriptsDir=$relengCommonBuilderDir/tools/scripts
if [[ ! ${relengBuilderDir} ]]; then relengBuilderDir=${writableBuildRoot}/${projRelengName}; fi
serverPropertiesFile=$relengCommonBuilderDir/server.properties

if [[ "$branch" != "HEAD" ]] && [[ !$projRelengBranch ]]; then
	echo "  **** Default -projRelengBranch to $branch. If that's not good, override using a -debug build. ****"
	projRelengBranch="$branch"; # by default, if build from R1_0_maintenance, use same tag for o.e.*.releng
fi

#################################### CREATE BUILD DIRECTORIES ####################################
 
echo ""
echo "[start] Create build directory $buildDir"
mkdir -p $buildDir/eclipse $downloadsDir $thirdPartyJarsDir 
echo ""

#################################### GET SOURCES FROM CVS/CVN ####################################

pushd ${writableBuildRoot} >/dev/null
if [[ ! -f .cvspass ]]; then touch .cvspass; fi

if [[ ! -d ${writableBuildRoot}/${projRelengName} ]] && [[ ! -L ${writableBuildRoot}/${projRelengName} ]]; then
	if [[ ! ${projRelengRoot##*svn*} ]]; then # checkout from svn
		# svn -q export -r HEAD http://dev.eclipse.org/svnroot/technology/org.eclipse.linuxtools/releng/trunk/org.eclipse.linuxtools.releng org.eclipse.linuxtools.releng
		cmd="svn $quietSVN export -r $projRelengBranch ${projRelengRoot//\'/}/$projRelengPath ${projRelengName}";
	else
		cmd="cvs -d ${projRelengRoot//\'/} $quietCVS ex -r $projRelengBranch -d ${projRelengName} $projRelengPath";
	fi
	echo "  "$cmd; $cmd; 
	echo "[start] [`date +%H\:%M\:%S`] Export done."
else
  echo "[start] Export skipped: ${projRelengName} already exists."
fi
echo ""

if [[ ! -d ${writableBuildRoot}/org.eclipse.dash.common.releng ]] && [[ ! -L ${writableBuildRoot}/org.eclipse.dash.common.releng ]]; then
	echo "[start] [`date +%H\:%M\:%S`] Export org.eclipse.dash.common.releng using "$commonRelengBranch;
	cmd="cvs -d :pserver:anonymous@dev.eclipse.org:/cvsroot/technology $quietCVS ex -r $commonRelengBranch -d org.eclipse.dash.common.releng org.eclipse.dash/athena/org.eclipse.dash.commonbuilder/org.eclipse.dash.commonbuilder.releng";
	echo "  "$cmd; $cmd;
	chmod 754 $relengCommonScriptsDir/*.sh
	echo "[start] [`date +%H\:%M\:%S`] Export done."
else
  echo "[start] Export skipped: org.eclipse.dash.common.releng already exists."
fi
echo ""

if [[ ! -d ${writableBuildRoot}/org.eclipse.releng.basebuilder ]] && [[ ! -L ${writableBuildRoot}/org.eclipse.releng.basebuilder ]]; then
	if [[ $basebuilderBranch ]]; then
	  echo "[start] [`date +%H\:%M\:%S`] Export org.eclipse.releng.basebuilder using "$basebuilderBranch;
	else
	  echo "[start] [`date +%H\:%M\:%S`] Export org.eclipse.releng.basebuilder using HEAD";
	fi
	cmd="cvs -d :pserver:anonymous@dev.eclipse.org:/cvsroot/eclipse $quietCVS ex -r $basebuilderBranch org.eclipse.releng.basebuilder"
	echo "  "$cmd; $cmd;
	echo "[start] [`date +%H\:%M\:%S`] Export done."

	# reuse cached copy if available	
	if [[ $writableBuildRoot != /opt/public/cbi/build ]] && [[ -f /opt/public/cbi/build/org.eclipse.pde.build.svn-1.0.1RC2.zip ]]; then 
		cp /opt/public/cbi/build/org.eclipse.pde.build.svn-1.0.1RC2.zip $writableBuildRoot
	fi

	# get pde.build.svn plugin (http://sourceforge.net/projects/svn-pde-build/) and unpack into releng.basebuilder's root folder
	pushd $writableBuildRoot >/dev/null
	if [[ ! -f org.eclipse.pde.build.svn-1.0.1RC2.zip ]]; then
		echo "[start] [`date +%H\:%M\:%S`] Get org.eclipse.pde.build.svn-*.zip from Sourceforge ..."
		wget --no-clobber http://downloads.sourceforge.net/svn-pde-build/org.eclipse.pde.build.svn-1.0.1RC2.zip
	fi
	unzip -qq org.eclipse.pde.build.svn-1.0.1RC2.zip -d org.eclipse.pde.build.svn
	if [[ $debug -gt 1 ]]; then echo "[start] [`date +%H\:%M\:%S`] Unpack pde.build.svn into $writableBuildRoot/org.eclipse.releng.basebuilder_${basebuilderBranch} ..."; fi
	pushd org.eclipse.pde.build.svn/org.eclipse.releng.basebuilder/ >/dev/null
	#mkdir -p $writableBuildRoot/org.eclipse.releng.basebuilder_${basebuilderBranch}/features 
	mkdir -p $writableBuildRoot/org.eclipse.releng.basebuilder_${basebuilderBranch}/plugins
	for f in $(find . -maxdepth 2 -mindepth 2 -type f); do # remove "-type f" to collect features too; 
		g=${f:2}; 
		if [[ $debug -gt 1 ]]; then echo "                   $g"; fi
		if [[ -d $writableBuildRoot/org.eclipse.releng.basebuilder_${basebuilderBranch}/$g ]] || [[ -f $writableBuildRoot/org.eclipse.releng.basebuilder_${basebuilderBranch}/$g ]]; then
			rm -fr $writableBuildRoot/org.eclipse.releng.basebuilder_${basebuilderBranch}/$g;
		fi
		mv -f $g $writableBuildRoot/org.eclipse.releng.basebuilder_${basebuilderBranch}/$g
	done
	popd >/dev/null
	rm -fr org.eclipse.pde.build.svn
	popd >/dev/null
	echo "[start] SVN support added to basebuilder."
	
else
  echo "[start] Export skipped: org.eclipse.releng.basebuilder already exists."
fi
echo ""
popd >/dev/null

#################################### GET JAVA_HOME AND ANT_HOME ####################################

if [[ $javaHome ]]; then
	export JAVA_HOME=$javaHome;
else # use fallbacks: project's build.properties then server.properties
	jhtmp=`mktemp`; # create a temp file so that variables can be resolved between properties files
	echo "JAVA60_HOME=$($relengCommonScriptsDir/readProperty.sh $serverPropertiesFile JAVA60_HOME)" >> $jhtmp
	echo "JAVA50_64_HOME=$($relengCommonScriptsDir/readProperty.sh $serverPropertiesFile JAVA50_64_HOME)" >> $jhtmp
	echo "JAVA50_HOME=$($relengCommonScriptsDir/readProperty.sh $serverPropertiesFile JAVA50_HOME)" >> $jhtmp
	echo "JAVA14_HOME=$($relengCommonScriptsDir/readProperty.sh $serverPropertiesFile JAVA14_HOME)" >> $jhtmp
	echo "JAVA_HOME=$($relengCommonScriptsDir/readProperty.sh $relengBuilderDir/build.properties JAVA_HOME)" >> $jhtmp
	echo "if [[ ! \$JAVA_HOME ]] || [[ ! -d \$JAVA_HOME ]]; then" >> $jhtmp
	echo "  JAVA_HOME=$($relengCommonScriptsDir/readProperty.sh $serverPropertiesFile JAVA_HOME)" >> $jhtmp
	echo "fi" >> $jhtmp
	# cat $jhtmp
	. $jhtmp
	rm -f $jhtmp
fi
if [[ ! -x $JAVA_HOME/bin/java ]]; then
	echo "ERROR! Can't run $JAVA_HOME/bin/java - must pass in a value for JAVA_HOME"
	exit 2;
fi 
export JAVA_HOME=$JAVA_HOME

export ANT_HOME=$($relengCommonScriptsDir/readProperty.sh $serverPropertiesFile ANT_HOME);
ANT_BIN=$($relengCommonScriptsDir/readProperty.sh $serverPropertiesFile ANT_BIN);
if [[ $ANT_BIN ]]; then
	export ANT=$ANT_BIN
else
	export ANT=$ANT_HOME"/bin/ant"
fi

echo "Environment variables:"
echo "  HOME      = $HOME"
echo "  JAVA_HOME = $JAVA_HOME"
echo "  ANT_HOME  = $ANT_HOME"
echo "  ANT       = $ANT"
echo ""

if [[ $projRelengBranch ]]; then
	mapVersionTag=$projRelengBranch
else 
	mapVersionTag=$branch
fi

echo "[start] mapVersionTag: $mapVersionTag"

#################################### BEGIN BUILD ####################################

cp=`find $relengBaseBuilderDir/ -name "org.eclipse.equinox.launcher_*.jar" -o -name "org.eclipse.equinox.launcher.jar"| sort | head -1`; 
if [[ ! $cp ]]; then
	echo ""; echo "  ** ERROR: org.eclipse.equinox.launcher jar not found in $relengBaseBuilderDir ! **"; echo ""
	exit 3;
fi

echo "[start] [`date +%H\:%M\:%S`] Invoke Eclipse build";
command="$JAVA_HOME/bin/java ${vmargs} -enableassertions -cp ${cp} org.eclipse.equinox.launcher.Main"
command=$command" -application org.eclipse.ant.core.antRunner"
command=$command" -f ${relengCommonBuilderDir}/buildAll.xml run"
if [[ $thirdPartyDownloadLicenseAcceptance ]]; then command=$command" -DthirdPartyDownloadLicenseAcceptance=Y"; fi
command=$command" -Dprojectid=${projectid}"
command=$command" -DbuildTimestamp=${buildTimestamp}"
command=$command" -DbuildType=${buildType}"
if [[ $buildAlias ]]; then command=$command" -DbuildAlias=${buildAlias}"; fi
command=$command" -Dversion=${version}"
command=$command" -DwritableBuildRoot=${writableBuildRoot}"
command=$command" -DdownloadsDir=${downloadsDir}"
command=$command" -DthirdPartyJarsDir=${thirdPartyJarsDir}"
command=$command" -DbuildDir=${buildDir}"
command=$command" -DrelengBuilderDir=${relengBuilderDir}"
command=$command" -DrelengCommonBuilderDir=${relengCommonBuilderDir}"
command=$command" -DrelengBaseBuilderDir=${relengBaseBuilderDir}"
command=$command" -DJAVA_HOME=${JAVA_HOME}"
command=$command" -DmapVersionTag=${mapVersionTag}"
if [[ $forceContextQualifier ]]; then command=$command" -DforceContextQualifier=${forceContextQualifier}"; fi
if [[ $fetchTag ]]; then command=$command" -DfetchTag=${fetchTag}"; fi
if [[ $dependencyURLs ]]; then command=$command" -DdependencyURLs=${dependencyURLs}"; fi
if [[ $localSourceCheckoutDir ]]; then command=$command" -DlocalSourceCheckoutDir=${localSourceCheckoutDir}"; fi
if [[ $noclean -eq 1 ]]; then command=$command" -Dnoclean=1"; fi
if [[ $Dflags ]]; then command=$command" $Dflags"; fi
$relengCommonScriptsDir/executeCommand.sh "$command"

echo "[start] [`date +%H\:%M\:%S`] start.sh finished."
echo ""