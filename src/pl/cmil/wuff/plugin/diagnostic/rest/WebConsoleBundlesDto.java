package pl.cmil.wuff.plugin.diagnostic.rest;


import java.util.ArrayList;
import java.util.List;

public class WebConsoleBundlesDto {
    private String status = "";
    private List<Integer> s = new ArrayList<>();
    private List<WebConsoleBundleDataDto> data = new ArrayList<>();

    public String getStatus() {
        return status;
    }

    public List<Integer> getS() {
        return s;
    }

    public List<WebConsoleBundleDataDto> getData() {
        return data;
    }
}
