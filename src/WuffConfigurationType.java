import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by Michal on 2014-09-06.
 */
public class WuffConfigurationType implements ConfigurationType{

    private final ConfigurationFactory myFactory;

    public WuffConfigurationType() {
        myFactory = new ConfigurationFactory(this) {
            public RunConfiguration createTemplateConfiguration(Project project) {
                final WuffRunConfiguration runConfiguration = new WuffRunConfiguration(project, this, "");

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
                final WuffRunConfiguration pluginRunConfiguration = (WuffRunConfiguration)template;
                return super.createConfiguration(name, pluginRunConfiguration);
            }
        };
    }

    @Override
    public String getDisplayName() {
        return "Wuff configuration type";
    }

    @Override
    public String getConfigurationTypeDescription() {
        return "Well hello there Wuff";
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @NotNull
    @Override
    public String getId() {
        return "wuff-config type";
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[] { myFactory};
    }
}
