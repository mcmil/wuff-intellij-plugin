Wuff IntelliJ Plugin
====================
[![Release](http://img.shields.io/badge/release-0.1.0-47b31f.svg)](https://github.com/mcmil/wuff-intellij-plugin/releases/latest)

A plugin which attempts to ease Eclipse E4 development using [Wuff](https://github.com/akhikhl/wuff) in IntelliJ IDEA. 

###Releases
Newest release available at [JetBrains Plugin Repository](http://plugins.jetbrains.com/plugin/7557?pr=).

* [0.1.0](https://github.com/mcmil/wuff-intellij-plugin/releases/tag/v0.1.0): Wuff Run Configuration for running a project built with Wuff 

###Features 
* Starting the application straighlty from the IDE (without using gradle)
* Incremental builds for classes and resources (gradle build not required)

#####Notes:
* Currently supports only [e(fx)clipse](http://www.eclipse.org/efxclipse/index.html) 
* In case a change in manifests or dependencies, a gradle build is required (at least the task `prepareRunConfig` must be executed) 
* IntelliJ Idea in version 13.1.4 is required at the moment (Gradle projects are imported in a different way in version 13.1.3)

