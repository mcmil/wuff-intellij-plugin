package pl.cmil.wuff.plugin;


public enum EquinoxConfigurationValues {
    CONSOLE("-console"), CLEAR_PERSISTED_STATE("-clearPersistedState"), CLEAN("-clean"),
    CONSOLE_LOG("-consoleLog"), NO_EXIT("-noExit");
    private String parameter;

    EquinoxConfigurationValues(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
