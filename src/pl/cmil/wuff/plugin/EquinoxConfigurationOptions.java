package pl.cmil.wuff.plugin;


public enum EquinoxConfigurationOptions {
    CONSOLE("-console"), CLEAR_PERSISTED_STATE("-clearPersistedState"), CLEAN("-clean"),
    CONSOLE_LOG("-consoleLog"), NO_EXIT("-noExit");
    private String parameter;

    EquinoxConfigurationOptions(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
