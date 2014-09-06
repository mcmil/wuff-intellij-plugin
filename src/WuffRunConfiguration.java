import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.externalSystem.util.ExternalSystemConstants;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.types.FileSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class WuffRunConfiguration extends RunConfigurationBase implements ModuleRunConfiguration {

    private Module appModule;

    public WuffRunConfiguration(final Project project, final ConfigurationFactory factory, final String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new WuffRunConfigurationEditor(this);
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {

    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull final ExecutionEnvironment executionEnvironment) throws ExecutionException {
        JavaCommandLineState commandLineState = new JavaCommandLineState(executionEnvironment) {
            @Override
            protected JavaParameters createJavaParameters() throws ExecutionException {
                JavaParameters params = new JavaParameters();

                setupLaunchConfiguration(params, executionEnvironment);

                fillProgramParameters(params);
                setupClassPath(params);

                rebuildJars();

                return params;
            }

            @Override
            public boolean shouldAddJavaProgramRunnerActions() {
                return true;
            }
        };

        return commandLineState;
    }

    private void setupLaunchConfiguration(JavaParameters params, ExecutionEnvironment executionEnvironment) {
        params.setJdk(ProjectRootManager.getInstance(executionEnvironment.getProject()).getProjectSdk());
        params.setWorkingDirectory(getModuleBuildPath(appModule) + File.separator + "run");

        params.setMainClass("org.eclipse.equinox.launcher.Main");
    }

    private void fillProgramParameters(JavaParameters params) {
        ParametersList programParametersList = params.getProgramParametersList();
        programParametersList.add("-console");
        programParametersList.add("-consoleLog");
        programParametersList.add("-noExit");
        programParametersList.add("-clean");
        programParametersList.add("-clearPersistedState");
        programParametersList.add("-application", "org.eclipse.fx.ui.workbench.fx.application");
        programParametersList.add("-configuration", "configuration");
        programParametersList.add("%*");
    }

    private void setupClassPath(JavaParameters params) {
        for (VirtualFile lib : ModuleRootManager.getInstance(appModule).orderEntries().classes().getRoots()) {
            params.getClassPath().add(lib);
        }
    }

    private void rebuildJars() {
        rebuildModuleJar(appModule);
        for (Module depedenencyModule : ModuleRootManager.getInstance(appModule).getDependencies()) {
            rebuildModuleJar(depedenencyModule);
        }
    }

    private void rebuildModuleJar(Module module) {
        String moduleBuildPath = getModuleBuildPath(module);

        Jar moduleJar = new Jar();
        moduleJar.setProject(new org.apache.tools.ant.Project());
        moduleJar.setManifest(new File(moduleBuildPath + File.separator + "tmp" + File.separator + "jar" + File.separator + "MANIFEST.MF"));

        FileSet classSet = new FileSet();
        classSet.setDir(new File(moduleBuildPath + File.separator + "classes" + File.separator + "main" + File.separator));

        FileSet resSet = new FileSet();
        resSet.setDir(new File(moduleBuildPath + File.separator + "resources" + File.separator + "main" + File.separator));

        moduleJar.addFileset(classSet);
        moduleJar.addFileset(resSet);

        File output = new File(moduleBuildPath + File.separator + "libs" + File.separator + getJarName(module));
        moduleJar.setDestFile(output);

        moduleJar.execute();
    }

    private String getJarName(Module myModule) {
        return myModule.getName() + "-" + myModule.getOptionValue(ExternalSystemConstants.EXTERNAL_SYSTEM_MODULE_VERSION_KEY) + ".jar";
    }

    private String getModuleBuildPath(Module module) {
        return ModuleRootManager.getInstance(module).getContentRoots()[0].getCanonicalPath() + File.separator + "build";
    }

    @Nullable
    public Module getModule() {
        if (appModule != null && appModule.isDisposed()) {
            appModule = null;
        }
        return appModule;
    }

    public void setModule(Module module) {
        appModule = module;
    }

    @Override
    @NotNull
    public Module[] getModules() {
        final Module module = getModule();
        return module != null ? new Module[]{module} : Module.EMPTY_ARRAY;
    }
}
