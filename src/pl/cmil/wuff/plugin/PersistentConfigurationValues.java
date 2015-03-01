package pl.cmil.wuff.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Michal on 2014-09-09.
 */
public class PersistentConfigurationValues {
    private String appModuleName;

    private List<EquinoxConfigurationOptions> enabledConfigs = new ArrayList<>();
    private String mainClass = "";
    private String vmArgs = "";
    private String applicationName = "";

    private String diagnosticUrl = "";
    private String diagnosticUsername = "";
    private String diagnosticPassword = "";
    private boolean autoDiagnostic = false;

    public List<EquinoxConfigurationOptions> getEnabledConfigs() {
        return Collections.unmodifiableList(new ArrayList<>(enabledConfigs));
    }

    public void replaceAllEnableConfigs(List<EquinoxConfigurationOptions> configs) {
        enabledConfigs.clear();
        enabledConfigs.addAll(configs);
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

    public String getDiagnosticUrl() {
        return diagnosticUrl;
    }

    public void setDiagnosticUrl(String diagnosticUrl) {
        this.diagnosticUrl = diagnosticUrl;
    }

    public String getDiagnosticUsername() {
        return diagnosticUsername;
    }

    public void setDiagnosticUsername(String diagnosticUsername) {
        this.diagnosticUsername = diagnosticUsername;
    }

    public String getDiagnosticPassword() {
        return diagnosticPassword;
    }

    public void setDiagnosticPassword(String diagnosticPassword) {
        this.diagnosticPassword = diagnosticPassword;
    }

    public boolean isAutoDiagnostic() {
        return autoDiagnostic;
    }

    public void setAutoDiagnostic(boolean autoDiagnostic) {
        this.autoDiagnostic = autoDiagnostic;
    }

    public String getModuleName() {
        return appModuleName;
    }

    public void setModuleName(String moduleName) {
        appModuleName = moduleName;
    }


    public void copyFrom(PersistentConfigurationValues other) {
        setMainClass(other.getMainClass());
        setVmArgs(other.getVmArgs());
        setApplicationName(other.getApplicationName());

        setModuleName(other.getModuleName());

        setAutoDiagnostic(other.isAutoDiagnostic());
        setDiagnosticUrl(other.getDiagnosticUrl());
        setDiagnosticUsername(other.getDiagnosticUsername());
        setDiagnosticPassword(other.getDiagnosticPassword());

        replaceAllEnableConfigs(other.getEnabledConfigs());

    }
}
