package pl.cmil.wuff.plugin.diagnostic.rest;

import com.google.gson.Gson;
import pl.cmil.wuff.plugin.diagnostic.BundleDiagnosis;
import pl.cmil.wuff.plugin.diagnostic.EquinoxDiagnosticProvider;
import pl.cmil.wuff.plugin.diagnostic.RawDiagnosticDataProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class WebConsoleDiagnosticProvider implements EquinoxDiagnosticProvider {

    private final Executor executor;
    private RawDiagnosticDataProvider dataProvider;

    private Gson gson = new Gson();

    public WebConsoleDiagnosticProvider(RawDiagnosticDataProvider dataProvider, Executor executor) {
        this.dataProvider = dataProvider;
        this.executor = executor;
    }

    private static BundleDiagnosis convert(WebConsoleBundleDataDto bundleDataDto) {
        return new BundleDiagnosis(bundleDataDto.getSymbolicName(), BundleDiagnosis.Status.getFrom(bundleDataDto.getState()), bundleDataDto.getId());
    }

    @Override
    public CompletableFuture<List<BundleDiagnosis>> diagnose() {
        return CompletableFuture.supplyAsync(this::performDiagnosis, executor);
    }

    private List<BundleDiagnosis> performDiagnosis() {
        WebConsoleBundlesDto bundles = gson.fromJson(dataProvider.getData(), WebConsoleBundlesDto.class);

        return bundles.getData().stream().map(WebConsoleDiagnosticProvider::convert).collect(Collectors.toList());
    }
}
