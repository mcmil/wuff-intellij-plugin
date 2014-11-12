package pl.cmil.wuff.plugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.execution.CantRunException;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WuffRunConfiguration extends RunConfigurationBase implements ModuleRunConfiguration {
    private static final String CONFIG_STORAGE = "wuff-runner-config";

    private PersistentConfigurationValues configurationValues = new PersistentConfigurationValues();
    private Module module;

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
        if (getModule() == null) {
            throw new RuntimeConfigurationException(WuffBundle.message("wuff.run.configuration.equinoxapp.module.not.specified"));
        }
        if (configurationValues.getMainClass() == null || configurationValues.getMainClass().isEmpty()) {
            throw new RuntimeConfigurationException(WuffBundle.message("wuff.run.configuration.equinoxapp.main.class.empty"));
        }
        if (configurationValues.getApplicationName() == null || configurationValues.getApplicationName().isEmpty()) {
            throw new RuntimeConfigurationException(WuffBundle.message("wuff.run.configuration.application.name.empty"));
        }
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull final ExecutionEnvironment executionEnvironment) throws ExecutionException {
        if (getModule() == null) {
            throw new CantRunException(WuffBundle.message("wuff.run.configuration.equinoxapp.module.not.specified"));
        }
        if (ProjectRootManager.getInstance(executionEnvironment.getProject()).getProjectSdk() == null) {
            throw CantRunException.noJdkConfigured();
        }
        return new EquinoxJavaCommandLineState(getModule(), configurationValues.getMainClass(), configurationValues.getVmArgs(), configurationValues.getEnabledConfigs(), configurationValues.getApplicationName(), executionEnvironment, executor);
    }

    public PersistentConfigurationValues getConfigurationValues() {
        return configurationValues;
    }

    @Nullable
    public Module getModule() {
        if (module != null && module.getName() == configurationValues.getModuleName()) {
            return module;
        }

        if (configurationValues.getModuleName() != null) {
            module = ModuleManager.getInstance(getProject()).findModuleByName(configurationValues.getModuleName());
        } else {
            module = null;
        }
        return module;
    }


    @Override
    @NotNull
    public Module[] getModules() {
        final Module module = getModule();
        return module != null ? new Module[]{module} : Module.EMPTY_ARRAY;
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        Gson gson = new GsonBuilder().create();
        Element configElement = new Element(CONFIG_STORAGE);
        configElement.setText(gson.toJson(configurationValues));

        element.addContent(configElement);
        super.writeExternal(element);
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        Element config = element.getChild(CONFIG_STORAGE);
        if (config != null) {
                Gson gson = new GsonBuilder().create();
                PersistentConfigurationValues stored = gson.fromJson(config.getText(), PersistentConfigurationValues.class);
                configurationValues.copyFrom(stored);
            }
           super.readExternal(element);
    }


}
