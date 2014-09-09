package pl.cmil.wuff.plugin;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Arrays;

/**
 * Created by Michal on 2014-09-06.
 */
public class WuffConfigurationType implements ConfigurationType {

    public static final String WUFF_ID = "wuff";

    private final ConfigurationFactory configurationFactory;

    public WuffConfigurationType() {
        configurationFactory = new ConfigurationFactory(this) {
            public RunConfiguration createTemplateConfiguration(Project project) {
                final WuffRunConfiguration runConfiguration = new WuffRunConfiguration(project, this, "");
                PersistentConfigurationValues configurationValues = runConfiguration.getConfigurationValues();

                configurationValues.setMainClass("org.eclipse.equinox.launcher.Main");
                configurationValues.replaceAllEnableConfigs(Arrays.asList(EquinoxConfigurationOptions.CLEAN,
                        EquinoxConfigurationOptions.CLEAR_PERSISTED_STATE, EquinoxConfigurationOptions.CONSOLE, EquinoxConfigurationOptions.CONSOLE_LOG));
                configurationValues.setApplicationName("org.eclipse.fx.ui.workbench.fx.application");
                configurationValues.setVmArgs("");
                return runConfiguration;
            }

            @Override
            public boolean isApplicable(@NotNull Project project) {
                return true;
            }

            @Override
            public boolean isConfigurationSingletonByDefault() {
                return true;
            }

            public RunConfiguration createConfiguration(String name, RunConfiguration template) {
                final WuffRunConfiguration templateConfig = (WuffRunConfiguration) template;

                WuffRunConfiguration configuration = new WuffRunConfiguration(templateConfig.getProject(), this, template.getName());
                configuration.getConfigurationValues().copyFrom(templateConfig.getConfigurationValues());

                return super.createConfiguration(name, configuration);
            }
        };
    }

    @Override
    public String getDisplayName() {
        return WuffBundle.message("wuff.run.configuration.name");
    }

    @Override
    public String getConfigurationTypeDescription() {
        return WuffBundle.message("wuff.run.configuration.description");
    }

    @Override
    public Icon getIcon() {
        return WuffIcons.WUFF_SMALL;
    }

    @NotNull
    @Override
    public String getId() {
        return WUFF_ID;
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{configurationFactory};
    }
}
