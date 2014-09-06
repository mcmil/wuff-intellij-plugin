import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.selectors.FilenameSelector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;


public class WuffRunConfiguration extends RunConfigurationBase implements ModuleRunConfiguration {

    private Module myModule;
    private static final Logger log = Logger.getInstance(WuffRunConfiguration.class);

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

                params.setMainClass("org.eclipse.equinox.launcher.Main");
                Sdk projectSdk = ProjectRootManager.getInstance(executionEnvironment.getProject()).getProjectSdk();

                params.setJdk(projectSdk);


                ParametersList programParametersList = params.getProgramParametersList();
                programParametersList.add("-console");
                programParametersList.add("-consoleLog");
                programParametersList.add("-noExit");
                programParametersList.add("-clean");
                programParametersList.add("-clearPersistedState");
                programParametersList.add("-application", "org.eclipse.fx.ui.workbench.fx.application");
                programParametersList.add("-configuration", "configuration");
                programParametersList.add("%*");


                ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(myModule);

                String moduleBuildPath = getModuleBuildPath(moduleRootManager);
                params.setWorkingDirectory( moduleBuildPath + "\\run");


                Jar moduleJar = new Jar();
                moduleJar.setProject(new org.apache.tools.ant.Project());
                moduleJar.setManifest(new File(moduleBuildPath+"\\tmp\\jar\\MANIFEST.MF"));

                FileSet classSet = new FileSet();
                classSet.setDir(new File(moduleBuildPath + "\\classes\\main\\"));

                FileSet resSet = new FileSet();
                resSet.setDir(new File(moduleBuildPath+"\\resources\\main\\"));

                moduleJar.addFileset(classSet);
                moduleJar.addFileset(resSet);

                File output = new File(moduleBuildPath + "\\libs\\org.eclipse.fx.demo.contacts.app-1.0.0-SNAPSHOT.jar");
                moduleJar.setDestFile(output);

                moduleJar.execute();








                /*Module[] dependencies = moduleRootManager.getDependencies();
                for (Module m : dependencies) {
                    String moduleBuildPath = getModuleBuildPath(ModuleRootManager.getInstance(m));

                    //instance.orderEntries().
                    //log.info(m.getOptionValue(ExternalSystemConstants.ROOT_PROJECT_PATH_KEY));
                }*/


                for (VirtualFile f : moduleRootManager.orderEntries().classes().getRoots()) {
                    params.getClassPath().add(f);
                }


                return params;
            }

            @Override
            public boolean shouldAddJavaProgramRunnerActions() {
                return true;
            }
        };

        return commandLineState;
    }

    private String getModuleBuildPath(ModuleRootManager moduleRootManager) {
        return moduleRootManager.getContentRoots()[0].getCanonicalPath() +  "\\build";
    }

    @Nullable
    public Module getModule() {
        if (myModule != null && myModule.isDisposed()) {
            myModule = null;
        }
        return myModule;
    }

    public void setModule(Module module) {
        myModule = module;
    }


    @Override
    @NotNull
    public Module[] getModules() {
        final Module module = getModule();
        return module != null ? new Module[]{module} : Module.EMPTY_ARRAY;
    }
}
