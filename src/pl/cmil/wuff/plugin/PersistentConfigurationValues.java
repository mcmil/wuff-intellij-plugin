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

    public List<EquinoxConfigurationOptions> getEnabledConfigs() {
        return Collections.unmodifiableList(new ArrayList<>(enabledConfigs));
    }

    public void replaceAllEnableConfigs( List<EquinoxConfigurationOptions> configs) {
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

    public String getModuleName(){
        return appModuleName;
    }
    public void setModuleName(String moduleName){
        appModuleName = moduleName;
    }

    public void copyFrom(PersistentConfigurationValues other) {
        setMainClass(other.getMainClass());
        setVmArgs(other.getVmArgs());
        setApplicationName(other.getApplicationName());

        setModuleName(other.getModuleName());

        replaceAllEnableConfigs(other.getEnabledConfigs());

    }
}
