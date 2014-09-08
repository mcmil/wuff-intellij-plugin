wuff-intellij-plugin
====================
A plugin which attempts to ease Eclipse E4 development using [Wuff](https://github.com/akhikhl/wuff) in IntelliJ IDEA. 
 
##Currently supported features:
* Starting the application straighlty from the IDE (without using gradle)
* Incremental builds for classes and resources (gradle build not required)

##Notes:
* Currently supports only [e(fx)clipse](http://www.eclipse.org/efxclipse/index.html) 
* In case a change in manifests or dependencies, a gradle build is required (at least the task `prepareRunConfig` must be executed) 
* IntelliJ Idea in version 13.1.4 is required at the moment (Gradle projects are imported in a different way in version 13.1.3)
