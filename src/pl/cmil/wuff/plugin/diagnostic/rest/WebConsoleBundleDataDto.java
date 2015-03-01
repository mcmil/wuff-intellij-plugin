package pl.cmil.wuff.plugin.diagnostic.rest;

public class WebConsoleBundleDataDto {
    private int id;

    private String name;

    private boolean fragment;

    private int stateRaw;

    private String state;

    private String version;

    private String symbolicName;

    private String category;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isFragment() {
        return fragment;
    }

    public int getStateRaw() {
        return stateRaw;
    }

    public String getState() {
        return state;
    }

    public String getVersion() {
        return version;
    }

    public String getSymbolicName() {
        return symbolicName;
    }

    public String getCategory() {
        return category;
    }
}
