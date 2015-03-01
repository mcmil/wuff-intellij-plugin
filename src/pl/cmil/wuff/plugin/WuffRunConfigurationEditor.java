package pl.cmil.wuff.plugin;

import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.roots.ui.configuration.ModulesCombobox;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.UIUtil;
import org.jdesktop.swingx.JXTitledSeparator;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class WuffRunConfigurationEditor extends SettingsEditor<WuffRunConfiguration> {

    private final LabeledComponent<RawCommandLineEditor> myVMParameters = new LabeledComponent<RawCommandLineEditor>();
    private final List<JBCheckBox> launchConfigOptions;
    private ModulesCombobox myModules = new ModulesCombobox();
    //Launch args
    private JBCheckBox myConsole = new JBCheckBox(WuffBundle.message("wuff.run.config.console"));
    private JBCheckBox myConsoleLog = new JBCheckBox(WuffBundle.message("wuff.run.config.consoleLog"));
    private JBCheckBox myClearPersistedState = new JBCheckBox(WuffBundle.message("wuff.run.config.clearpersistedstate"));
    private JBCheckBox myOSGiClean = new JBCheckBox(WuffBundle.message("wuff.run.config.osgiclean"));
    private JBCheckBox myNoExit = new JBCheckBox(WuffBundle.message("wuff.run.config.noexit"));
    //Diagnostics
    private JBCheckBox myAutomaticDiagnostic = new JBCheckBox(WuffBundle.message("wuff.run.config.automaticDiagnostic"));
    private JTextField myUrl = new JTextField();
    private JTextField myUser = new JTextField();
    private JPasswordField myPassword = new JPasswordField();
    //Run
    private JTextField myEquinoxMainClass = new JTextField();
    private JTextField myApplicationName = new JTextField();

    private WuffRunConfiguration config;
    private PersistentConfigurationValues configurationValues;
    private Map<JBCheckBox, EquinoxConfigurationOptions> optionMapping = new HashMap<JBCheckBox, EquinoxConfigurationOptions>();

    public WuffRunConfigurationEditor(WuffRunConfiguration config) {
        this.config = config;
        configurationValues = config.getConfigurationValues();

        launchConfigOptions = Arrays.asList(myConsole, myConsoleLog, myClearPersistedState, myNoExit, myOSGiClean);
        optionMapping.put(myConsole, EquinoxConfigurationOptions.CONSOLE);
        optionMapping.put(myConsoleLog, EquinoxConfigurationOptions.CONSOLE_LOG);
        optionMapping.put(myOSGiClean, EquinoxConfigurationOptions.CLEAN);
        optionMapping.put(myClearPersistedState, EquinoxConfigurationOptions.CLEAR_PERSISTED_STATE);
        optionMapping.put(myNoExit, EquinoxConfigurationOptions.NO_EXIT);
    }

    @Override
    protected void resetEditorFrom(WuffRunConfiguration other) {
        config.getConfigurationValues().copyFrom(other.getConfigurationValues());
        setCurrentValuesToControls();
    }

    @Override
    protected void applyEditorTo(WuffRunConfiguration wuffRunConfiguration) throws ConfigurationException {
        PersistentConfigurationValues configurationValues = wuffRunConfiguration.getConfigurationValues();

        Module selectedModule = myModules.getSelectedModule();
        String selectedModuleName = selectedModule == null ? null : selectedModule.getName();

        configurationValues.setModuleName(selectedModuleName);
        configurationValues.setMainClass(myEquinoxMainClass.getText());
        configurationValues.setVmArgs(myVMParameters.getComponent().getText());
        configurationValues.setApplicationName(myApplicationName.getText());
        java.util.List<EquinoxConfigurationOptions> enabledConfigsFromEditor = new ArrayList<>();

        for (JBCheckBox cb : launchConfigOptions) {
            if (cb.isSelected()) {
                enabledConfigsFromEditor.add(optionMapping.get(cb));
            }
        }
        configurationValues.replaceAllEnableConfigs(enabledConfigsFromEditor);

        configurationValues.setAutoDiagnostic(myAutomaticDiagnostic.isSelected());
        configurationValues.setDiagnosticUrl(myUrl.getText());
        configurationValues.setDiagnosticUsername(myUser.getText());
        configurationValues.setDiagnosticPassword(myPassword.getText());

    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        JPanel panel = new JPanel(new GridBagLayout());

        myVMParameters.setComponent(new RawCommandLineEditor());
        myVMParameters.setText(WuffBundle.message("wuff.run.config.jvmparams"));

        GridBagConstraints gc = new GridBagConstraints(0, GridBagConstraints.RELATIVE, 2, 1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), UIUtil.DEFAULT_HGAP, UIUtil.DEFAULT_VGAP);

        panel.add(new JBLabel(WuffBundle.message("wuff.run.config.equinoxappmodule")), gc);
        gc.gridx = 1;
        gc.weightx = 7;
        panel.add(myModules, gc);

        gc.gridx = 0;
        gc.weightx = 1;
        panel.add(new JBLabel(WuffBundle.message("wuff.run.config.equinoxmain")), gc);

        gc.gridx = 1;
        gc.weightx = 1;
        panel.add(myEquinoxMainClass, gc);

        gc.gridx = 0;
        gc.weightx = 1;
        panel.add(new JBLabel(WuffBundle.message("wuff.run.configuration.applicationname")), gc);

        gc.gridx = 1;
        gc.weightx = 1;
        panel.add(myApplicationName, gc);

        gc.weightx = 10;
        gc.gridx = 0;
        panel.add(myVMParameters, gc);

        panel.add(createCheckboxPanel(), gc);

        panel.add(new JXTitledSeparator(WuffBundle.message("wuff.run.config.diagnostics")), gc);

        gc.weightx = 1;
        panel.add(myAutomaticDiagnostic, gc);

        gc.gridx = 0;
        panel.add(new JBLabel(WuffBundle.message("wuff.run.config.diagnostic.url")), gc);
        gc.gridx = 1;
        panel.add(myUrl, gc);

        gc.gridx = 0;
        panel.add(new JBLabel(WuffBundle.message("wuff.run.config.diagnostic.user")), gc);
        gc.gridx = 1;
        panel.add(myUser, gc);

        gc.gridx = 0;
        panel.add(new JBLabel(WuffBundle.message("wuff.run.config.diagnostic.pass")), gc);
        gc.gridx = 1;
        panel.add(myPassword, gc);

        setCurrentValuesToControls();

        return panel;
    }

    private void setCurrentValuesToControls() {
        myModules.fillModules(config.getProject(), JavaModuleType.getModuleType());
        myVMParameters.getComponent().setText(configurationValues.getVmArgs());
        myModules.setSelectedModule(config.getModule());
        myEquinoxMainClass.setText(configurationValues.getMainClass());
        myApplicationName.setText(configurationValues.getApplicationName());
        for (JBCheckBox jb : launchConfigOptions) {
            if (configurationValues.getEnabledConfigs().contains(optionMapping.get(jb))) {
                jb.setSelected(true);
            }
        }

        myAutomaticDiagnostic.setSelected(configurationValues.isAutoDiagnostic());
        myUrl.setText(configurationValues.getDiagnosticUrl());
        myUser.setText(configurationValues.getDiagnosticUsername());
        myPassword.setText(configurationValues.getDiagnosticPassword());
    }

    private Component createCheckboxPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(WuffBundle.message("wuff.run.config.launchparams")));
        for (JBCheckBox jb : launchConfigOptions) {
            panel.add(jb);
        }

        return panel;
    }

}
