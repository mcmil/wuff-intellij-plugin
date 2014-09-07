package pl.cmil.wuff.plugin;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.externalSystem.util.ExternalSystemConstants;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.types.FileSet;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class EquinoxJavaCommandLineState extends JavaCommandLineState {
    private final Module appModule;
    private final String mainClass;
    private final String vmArgs;
    private final List<EquinoxConfigurationValues> enabledConfigs;
    private final String applicationName;


    protected EquinoxJavaCommandLineState(Module appModule, String mainClass, String vmArgs,
                                          List<EquinoxConfigurationValues> enabledConfigs, String applicationName, @NotNull ExecutionEnvironment environment) {
        super(environment);
        this.appModule = appModule;
        this.mainClass = mainClass;
        this.vmArgs = vmArgs;
        this.enabledConfigs = enabledConfigs;
        this.applicationName = applicationName;
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
        for (EquinoxConfigurationValues configurationValue : enabledConfigs) {
            programParametersList.add(configurationValue.getParameter());
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
        rebuildModuleJar(appModule);
        for (Module depedenencyModule : ModuleRootManager.getInstance(appModule).getDependencies()) {
            rebuildModuleJar(depedenencyModule);
        }
    }

    private void rebuildModuleJar(Module module) throws ExecutionException {
        String moduleBuildPath = getModuleBuildPath(module);
        File manifestFile = new File(moduleBuildPath + File.separator + "tmp" + File.separator + "jar" + File.separator + "MANIFEST.MF");

        if (isProjectPrebuiltByGradle(manifestFile)) {
            Jar moduleJar = new Jar();
            moduleJar.setProject(new org.apache.tools.ant.Project());
            moduleJar.setManifest(manifestFile);

            FileSet classSet = new FileSet();
            classSet.setDir(new File(moduleBuildPath + File.separator + "classes" + File.separator + "main" + File.separator));

            FileSet resSet = new FileSet();
            resSet.setDir(new File(moduleBuildPath + File.separator + "resources" + File.separator + "main" + File.separator));

            moduleJar.addFileset(classSet);
            moduleJar.addFileset(resSet);

            File output = new File(moduleBuildPath + File.separator + "libs" + File.separator + getJarName(module));
            moduleJar.setDestFile(output);

            moduleJar.execute();
        } else {
            throw new ExecutionException(WuffBundle.message("wuff.runtime.notprebuilt", appModule.getOptionValue(ExternalSystemConstants.EXTERNAL_SYSTEM_MODULE_GROUP_KEY)));
        }

    }

    private boolean isProjectPrebuiltByGradle(File manifestFile) {
        return manifestFile.exists();
    }

    private String getJarName(Module myModule) {
        return myModule.getName() + "-" + myModule.getOptionValue(ExternalSystemConstants.EXTERNAL_SYSTEM_MODULE_VERSION_KEY) + ".jar";
    }

    private String getModuleBuildPath(Module module) {
        return ModuleRootManager.getInstance(module).getContentRoots()[0].getCanonicalPath() + File.separator + "build";
    }

}
