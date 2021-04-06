# Nebula and Opal: Rich SWT Widgets
Welcome to the Eclipse Nebula project. This repositority contains a large set of UI elements that can be used in 
Fat or Thin client applications based on Java and SWT.

## Download
Please have a look at https://www.eclipse.org/nebula/downloads.php

You'll find update sites for all releases and also for the latest build

## Contact
* [Issues/Questions](https://github.com/eclipse/nebula/issues)
* [Mailing list](https://dev.eclipse.org/mailman/listinfo/nebula-dev)

## Patches 
We can accept your patches if you have signed the [Eclipse Contributors Agreement (ECA)](https://wiki.eclipse.org/ECA) 
which ensures users of these libraries that they can use your code without getting into any legal trouble.

For every patch we require a [corresponding Bugzilla issue.](https://bugs.eclipse.org/bugs/)

## Building on our build server
After admin approval, pull requests gets build by these two jobs:
 * [Stable Widgets](https://ci.eclipse.org/nebula/job/nebula.stable.github/)
 * [Incubation Widgets](https://ci.eclipse.org/nebula/job/nebula.incubation.github/)

## Building locally
    git clone https://github.com/eclipse/nebula.git
    cd releng/org.eclipse.nebula.nebula-release
    mvn verify
 
    cd releng/org.eclipse.nebula.nebula-incubation
    mvn verify (or install to install in the local maven repo)

More info can be found on the [the builds page](https://wiki.eclipse.org/Nebula/Builds)

## New Committers
Before you decide to contribute your code to Eclipse, keep in mind that your code must follow the EPL, Eclipse's Public License.  
This means that committers and contributors need to follow some rules.

A good overview of what this entails and how this affects you [can be read here](http://www.eclipse.org/legal/#Committers)

There are a number of ways you can contribute. 

* A new project inside Nebula
* New widgets in the Nebula Release project (production ready).
* New widgets in the Nebula Incubation project.
* Maintain existing widgets

If you choose any of these option contact the Nebula devs through the 
[mailing list](https://dev.eclipse.org/mailman/listinfo/nebula-dev)
and state your intentions.

## New Widgets
The 'New Widget' process is described here:

[https://wiki.eclipse.org/Nebula/Contributions/New_Widgets](https://wiki.eclipse.org/Nebula/Contributions/New_Widgets)

