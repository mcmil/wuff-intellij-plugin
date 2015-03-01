Wuff IntelliJ Plugin
====================
[![Release](http://img.shields.io/badge/release-v0.2.0-47b31f.svg)](https://github.com/mcmil/wuff-intellij-plugin/releases/latest)
[![Current](http://img.shields.io/badge/current-v0.3.1-47b31f.svg)](https://github.com/mcmil/wuff-intellij-plugin/releases/tag/v0.3.1)

A plugin which attempts to ease Eclipse E4 development using [Wuff](https://github.com/akhikhl/wuff) in IntelliJ IDEA. To try it out, you can use the [Wuff e(fx)clipse project](https://github.com/mcmil/wuff-efxclipse-samples)

###Latest news
* [0.2.0](https://github.com/mcmil/wuff-intellij-plugin/releases/tag/v0.2.0) Released as stable with new features and IntelliJ Idea 14 compatibility.
* [0.2.0.alpha](https://github.com/mcmil/wuff-intellij-plugin/releases/tag/v0.2.0.alpha) pre-release: Wuff 0.0.13-SNAPSHOT changes introduced (broken compatibility with 0.0.12)

###Releases
Newest release available at [JetBrains Plugin Repository](http://plugins.jetbrains.com/plugin/7557?pr=).
* [0.2.0](https://github.com/mcmil/wuff-intellij-plugin/releases/tag/v0.2.0): Compatibility with Wuff 0.0.13-SNAPSHOT. Supports Equinox restart. Tested for IntelliJ Idea 14.
* [0.1.0](https://github.com/mcmil/wuff-intellij-plugin/releases/tag/v0.1.0): Wuff Run Configuration for running a project built with Wuff 

###Features 
* Starting the application straighlty from the IDE (without using gradle)
* Incremental builds for classes and resources (gradle build not required)

Run configuration screen, along with available options is shown on the screenshot below:
![wuff run configuration](https://github.com/mcmil/wuff-intellij-plugin/blob/master/images/run_configuration_screenshot.png "Run configuration")


#####Notes:
* Currently tested only for [e(fx)clipse](http://www.eclipse.org/efxclipse/index.html) 
* In case a change in manifests or dependencies, a gradle build is required (at least the task `prepareRunConfig` must be executed) 
* IntelliJ Idea in version at least 13.1.4 is required at the moment (Gradle projects are imported in a different way in version 13.1.3)

