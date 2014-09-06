package pl.cmil.wuff.plugin;

import com.intellij.execution.CantRunException;
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
        if(getModule() == null) {
            throw new RuntimeConfigurationException(WuffBundle.message("wuff.run.configuration.equinoxapp.module.not.specified"));
        }
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull final ExecutionEnvironment executionEnvironment) throws ExecutionException {
        if(getModule() == null) {
            throw new CantRunException(WuffBundle.message("wuff.run.configuration.equinoxapp.module.not.specified"));
        }
        if(ProjectRootManager.getInstance(executionEnvironment.getProject()).getProjectSdk() == null) {
            throw CantRunException.noJdkConfigured();
        }
        return new EquinoxJavaCommandLineState(getModule(),executionEnvironment);
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
