package pl.cmil.wuff.plugin;

import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.roots.ui.configuration.ModulesCombobox;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class WuffRunConfigurationEditor extends SettingsEditor<WuffRunConfiguration> {

    private ModulesCombobox myModules = new ModulesCombobox();
    private final LabeledComponent<RawCommandLineEditor> myVMParameters = new LabeledComponent<RawCommandLineEditor>();

    private JBCheckBox myConsole = new JBCheckBox(WuffBundle.message("wuff.run.config.console"));
    private JBCheckBox myConsoleLog = new JBCheckBox(WuffBundle.message("wuff.run.config.consoleLog"));
    private JBCheckBox myClearPersistedState = new JBCheckBox(WuffBundle.message("wuff.run.config.clearpersistedstate"));
    private JBCheckBox myOSGiClean = new JBCheckBox(WuffBundle.message("wuff.run.config.osgiclean"));
    private JBCheckBox myNoExit = new JBCheckBox(WuffBundle.message("wuff.run.config.noexit"));

    private JTextField myEquinoxMainClass = new JTextField();
    private JTextField myApplicationName = new JTextField();


    private WuffRunConfiguration config;
    private final List<JBCheckBox> launchConfigOptions;
    private Map<JBCheckBox, EquinoxConfigurationValues> optionMapping = new HashMap<JBCheckBox, EquinoxConfigurationValues>();

    public WuffRunConfigurationEditor(WuffRunConfiguration config) {
        this.config = config;
        launchConfigOptions = Arrays.asList(myConsole, myConsoleLog, myClearPersistedState, myNoExit, myOSGiClean);
        optionMapping.put(myConsole, EquinoxConfigurationValues.CONSOLE);
        optionMapping.put(myConsoleLog, EquinoxConfigurationValues.CONSOLE_LOG);
        optionMapping.put(myOSGiClean, EquinoxConfigurationValues.CLEAN);
        optionMapping.put(myClearPersistedState, EquinoxConfigurationValues.CLEAR_PERSISTED_STATE);
        optionMapping.put(myNoExit, EquinoxConfigurationValues.NO_EXIT);
    }

    @Override
    protected void resetEditorFrom(WuffRunConfiguration wuffRunConfiguration) {
        config.setModule(wuffRunConfiguration.getModule());
        config.setMainClass(wuffRunConfiguration.getMainClass());
        config.setVmArgs(wuffRunConfiguration.getVmArgs());
        config.setApplicationName(wuffRunConfiguration.getApplicationName());
        config.getEnabledConfigs().clear();
        config.getEnabledConfigs().addAll(wuffRunConfiguration.getEnabledConfigs());
    }

    @Override
    protected void applyEditorTo(WuffRunConfiguration wuffRunConfiguration) throws ConfigurationException {
        wuffRunConfiguration.setModule(myModules.getSelectedModule());
        wuffRunConfiguration.setMainClass(myEquinoxMainClass.getText());
        wuffRunConfiguration.setVmArgs(myVMParameters.getComponent().getText());
        wuffRunConfiguration.setApplicationName(myApplicationName.getText());
        java.util.List<EquinoxConfigurationValues> enabledConfigs = wuffRunConfiguration.getEnabledConfigs();
        enabledConfigs.clear();

        for(JBCheckBox cb : launchConfigOptions) {
            if(cb.isSelected()) {
                enabledConfigs.add(optionMapping.get(cb));
            }
        }


    }

    @NotNull
    @Override
    protected JComponent createEditor() {

        JPanel panel = new JPanel(new GridBagLayout());

        myModules.fillModules(config.getProject(), JavaModuleType.getModuleType());

        myVMParameters.setComponent(new RawCommandLineEditor());
        myVMParameters.setText(WuffBundle.message("wuff.run.config.jvmparams"));
        myVMParameters.getComponent().setText(config.getVmArgs());

        GridBagConstraints gc = new GridBagConstraints(0, GridBagConstraints.RELATIVE, 2, 1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), UIUtil.DEFAULT_HGAP, UIUtil.DEFAULT_VGAP);

        panel.add(new JBLabel(WuffBundle.message("wuff.run.config.equinoxappmodule")), gc);
        gc.gridx = 1;
        gc.weightx = 7;
        panel.add(myModules, gc);
        myModules.setSelectedModule(config.getModule());

        gc.gridx = 0;
        gc.weightx = 1;
        panel.add(new JBLabel(WuffBundle.message("wuff.run.config.equinoxmain")), gc);

        gc.gridx = 1;
        gc.weightx = 1;
        panel.add(myEquinoxMainClass, gc);
        myEquinoxMainClass.setText(config.getMainClass());

        gc.gridx = 0;
        gc.weightx = 1;
        panel.add(new JBLabel(WuffBundle.message("wuff.run.configuration.applicationname")), gc);

        gc.gridx = 1;
        gc.weightx = 1;
        panel.add(myApplicationName, gc);
        myApplicationName.setText(config.getApplicationName());

        gc.weightx = 10;
        gc.gridx = 0;
        panel.add(myVMParameters, gc);

        panel.add(createCheckboxPanel(), gc);

        return panel;
    }

    private Component createCheckboxPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(WuffBundle.message("wuff.run.config.launchparams")));
        for(JBCheckBox jb : launchConfigOptions) {
            if(config.getEnabledConfigs().contains(optionMapping.get(jb))) {
                jb.setSelected(true);
            }
            panel.add(jb);
        }

        return panel;
    }


}
