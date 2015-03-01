package pl.cmil.wuff.plugin.diagnostic;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface EquinoxDiagnosticProvider {
    CompletableFuture<List<BundleDiagnosis>> diagnose();
}
