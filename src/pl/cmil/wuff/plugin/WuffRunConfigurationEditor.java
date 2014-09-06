package pl.cmil.wuff.plugin;

import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.roots.ui.configuration.ModulesCombobox;

import javax.swing.*;

public class WuffRunConfigurationEditor extends SettingsEditor<WuffRunConfiguration> {

    private  ModulesCombobox myModules = new ModulesCombobox();
    private WuffRunConfiguration config;

    public WuffRunConfigurationEditor(WuffRunConfiguration config) {
        this.config = config;
    }

    @Override
    protected void resetEditorFrom(WuffRunConfiguration wuffRunConfiguration) {
    wuffRunConfiguration.setModule(wuffRunConfiguration.getModule());
    }

    @Override
    protected void applyEditorTo(WuffRunConfiguration wuffRunConfiguration) throws ConfigurationException {
        wuffRunConfiguration.setModule(myModules.getSelectedModule());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        myModules.fillModules(config.getProject(), JavaModuleType.getModuleType());

        JPanel panel = new JPanel();
        panel.add(myModules);


        return panel;
    }


}
