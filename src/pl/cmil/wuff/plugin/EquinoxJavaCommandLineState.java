package pl.cmil.wuff.plugin;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.externalSystem.util.ExternalSystemConstants;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;
import org.jetbrains.annotations.NotNull;
import pl.cmil.wuff.plugin.diagnostic.EquinoxDiagnosisProcessListener;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class EquinoxJavaCommandLineState extends JavaCommandLineState {
    private final static Logger log = Logger.getInstance(EquinoxJavaCommandLineState.class);
    private final Module appModule;
    private final PersistentConfigurationValues configurationValues;
    private final String mainClass;
    private final String vmArgs;
    private final List<EquinoxConfigurationOptions> enabledConfigs;
    private final String applicationName;
    private final ExecutionEnvironment environment;
    private final Executor executor;

    protected EquinoxJavaCommandLineState(Module appModule, PersistentConfigurationValues configurationValues,
                                          ExecutionEnvironment environment, Executor executor) {
        super(environment);
        this.appModule = appModule;
        this.configurationValues = configurationValues;
        this.mainClass = configurationValues.getMainClass();
        this.vmArgs = configurationValues.getVmArgs();
        this.enabledConfigs = configurationValues.getEnabledConfigs();
        this.applicationName = configurationValues.getApplicationName();
        this.environment = environment;
        this.executor = executor;
    }

    @NotNull
    @Override
    protected OSProcessHandler startProcess() throws ExecutionException {
        validateIfProjectIsPrebuilt();

        final OSProcessHandler osProcessHandler = super.startProcess();
        osProcessHandler.addProcessListener(new EquinoxRestartProcessListener(appModule, environment, executor));

        if (configurationValues.isAutoDiagnostic()) {
            osProcessHandler.addProcessListener(new EquinoxDiagnosisProcessListener(appModule, configurationValues));
        }

        return osProcessHandler;
    }

    private void validateIfProjectIsPrebuilt() throws ExecutionException {
        File workingDirectory = new File(getJavaParameters().getWorkingDirectory());
        if (!workingDirectory.exists()) {
            throw new ExecutionException(getNotPrebuiltMessage());
        }
    }

    private String getNotPrebuiltMessage() {
        return WuffBundle.message("wuff.runtime.notprebuilt", appModule.getOptionValue(ExternalSystemConstants.EXTERNAL_SYSTEM_MODULE_GROUP_KEY));
    }

    @Override
    protected JavaParameters createJavaParameters() throws ExecutionException {
        JavaParameters params = new JavaParameters();

        setupLaunchConfiguration(params, getEnvironment());

        fillProgramParameters(params);
        setupClassPath(params);

        rebuildJars();

        return params;
    }

    private void setupLaunchConfiguration(JavaParameters params, ExecutionEnvironment executionEnvironment) {
        params.setJdk(ProjectRootManager.getInstance(executionEnvironment.getProject()).getProjectSdk());
        params.setWorkingDirectory(getModuleBuildPath(appModule) + File.separator + "run");
        params.getVMParametersList().addParametersString(vmArgs);

        params.setMainClass(mainClass);
    }

    private void fillProgramParameters(JavaParameters params) {
        ParametersList programParametersList = params.getProgramParametersList();
        for (EquinoxConfigurationOptions configurationValue : enabledConfigs) {
            programParametersList.add(configurationValue.getParameter());
            if (configurationValue == EquinoxConfigurationOptions.CONSOLE) {
                programParametersList.add("5555");
            }
        }

        programParametersList.add("-application", applicationName);
        programParametersList.add("-configuration", "configuration");
        programParametersList.add("%*");
    }

    private void setupClassPath(JavaParameters params) {
        for (VirtualFile lib : ModuleRootManager.getInstance(appModule).orderEntries().classes().getRoots()) {
            params.getClassPath().add(lib);
        }
    }

    private void rebuildJars() throws ExecutionException {
        Set<Module> modulesToRebuild = new HashSet<>();
        ModuleUtilCore.getDependencies(appModule, modulesToRebuild);

        log.info("Rebuilding modules: " + modulesToRebuild);

        for (Module module : modulesToRebuild) {
            rebuildModuleJar(module);
        }

        if (modulesToRebuild.stream().anyMatch(this::isGradleConfigurationNotClear)) {
            warnUserAboutBuildAmbiguity();
        }

    }

    private void rebuildModuleJar(Module module) throws ExecutionException {
        String moduleBuildPath = getModuleBuildPath(module);
        File manifestFile = new File(moduleBuildPath + File.separator + "tmp" + File.separator + "jar" + File.separator + "MANIFEST.MF");

        if (isProjectPrebuiltByGradle(manifestFile)) {
            Jar moduleJar = new Jar();
            moduleJar.setDuplicate((Zip.Duplicate) Zip.Duplicate.getInstance(Zip.Duplicate.class, "preserve"));
            moduleJar.setProject(new org.apache.tools.ant.Project());
            moduleJar.setManifest(manifestFile);

            File classDir = new File(moduleBuildPath + File.separator + "classes" + File.separator + "main" + File.separator);
            addDirectoryToJar(moduleJar, classDir);

            File resourceDirectory = new File(moduleBuildPath + File.separator + "resources" + File.separator + "main" + File.separator);
            if (resourceDirectory.exists()) {
                addDirectoryToJar(moduleJar, resourceDirectory);
            }

            File output = new File(moduleBuildPath + File.separator + "libs" + File.separator + getJarName(module));
            moduleJar.setDestFile(output);

            moduleJar.execute();
        } else {
            throw new ExecutionException(getNotPrebuiltMessage());
        }

    }

    private void warnUserAboutBuildAmbiguity() {
        Notification buildCheckNotification = new Notification("wuff.build.warning", "Wuff build warning",
                WuffBundle.message("wuff.build.warning.uncertain.configuration"), NotificationType.WARNING)
                .setImportant(true);
        Notifications.Bus.notify(buildCheckNotification);
    }

    private boolean isGradleConfigurationNotClear(Module module) {
        try (Stream<Path> paths = Files.walk(Paths.get(getModuleBuildPath(module) + File.separator + "libs"))) {
            return paths.filter(f -> f.getFileName().toString().endsWith(".jar")).count() > 1;
        } catch (Exception e) {
            log.warn("Cannot check gradle configuration. The build will continue ", e);
        }

        return false;
    }

    private void addDirectoryToJar(Jar moduleJar, File dir) {
        FileSet fileSet = new FileSet();
        fileSet.setDir(dir);
        moduleJar.addFileset(fileSet);
    }

    private boolean isProjectPrebuiltByGradle(File manifestFile) {
        return manifestFile.exists();
    }

    private String getJarName(Module myModule) {
        return myModule.getName() + "_" + myModule.getOptionValue(ExternalSystemConstants.EXTERNAL_SYSTEM_MODULE_VERSION_KEY) + ".jar";
    }

    private String getModuleBuildPath(Module module) {
        return ModuleRootManager.getInstance(module).getContentRoots()[0].getCanonicalPath() + File.separator + "build";
    }

}
