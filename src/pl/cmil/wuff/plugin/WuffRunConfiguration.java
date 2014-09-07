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
import java.util.ArrayList;
import java.util.List;

public class WuffRunConfiguration extends RunConfigurationBase implements ModuleRunConfiguration {

    private Module appModule;
    private List<EquinoxConfigurationValues> enabledConfigs = new ArrayList<EquinoxConfigurationValues>();
    private String mainClass ="";
    private String vmArgs = "";
    private String applicationName = "";



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
        if(getMainClass() == null || getMainClass().isEmpty()) {
            throw new RuntimeConfigurationException(WuffBundle.message("wuff.run.configuration.equinoxapp.main.class.empty"));
        }
        if(getApplicationName() == null || getApplicationName().isEmpty()) {
            throw new RuntimeConfigurationException(WuffBundle.message("wuff.run.configuration.application.name.empty"));
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
        return new EquinoxJavaCommandLineState(getModule(),getMainClass(), getVmArgs(), getEnabledConfigs(),getApplicationName(),executionEnvironment);
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

    public List<EquinoxConfigurationValues> getEnabledConfigs() {
        return enabledConfigs;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public String getVmArgs() {
        return vmArgs;
    }

    public void setVmArgs(String vmArgs) {
        this.vmArgs = vmArgs;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}
