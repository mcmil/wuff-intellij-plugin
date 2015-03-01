package pl.cmil.wuff.plugin.diagnostic;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import pl.cmil.wuff.plugin.PersistentConfigurationValues;
import pl.cmil.wuff.plugin.diagnostic.rest.RestDiagnosticDataProvider;
import pl.cmil.wuff.plugin.diagnostic.rest.WebConsoleDiagnosticProvider;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EquinoxDiagnosisProcessListener extends ProcessAdapter {
    private final static Logger log = Logger.getInstance(EquinoxDiagnosisProcessListener.class);
    private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("equinox-diagnostic-process").build());

    private final PersistentConfigurationValues configurationValues;
    private final FinishedDiagnosticNotifier notifier;
    private Module appModule;


    public EquinoxDiagnosisProcessListener(Module appModule, PersistentConfigurationValues configurationValues) {
        this.appModule = appModule;
        this.configurationValues = configurationValues;
        notifier = ApplicationManager.getApplication().getMessageBus()
                .syncPublisher(FinishedDiagnosticNotifier.DIAGNOSTIC_TOPIC);
    }

    @Override
    public void startNotified(ProcessEvent event) {
        super.startNotified(event);

        notifier.diagnosisStarted();
        executor.schedule(this::startDiagnosticJob, 10, TimeUnit.SECONDS);
    }

    private void startDiagnosticJob() {
        RestDiagnosticDataProvider restDiagnosticDataProvider = new RestDiagnosticDataProvider(configurationValues.getDiagnosticUrl(),
                configurationValues.getDiagnosticUsername(), configurationValues.getDiagnosticPassword());
        WebConsoleDiagnosticProvider diagnosticProvider = new WebConsoleDiagnosticProvider(restDiagnosticDataProvider,
                executor);

        diagnosticProvider.diagnose().whenComplete(this::handleResult);
    }

    private void handleResult(List<BundleDiagnosis> bundles, Throwable exc) {
        if (exc == null) {
            notifier.diagnosisSuccessful(bundles);
        } else {
            log.warn("Diagnosis failed ", exc);
            notifier.diagnosisFailed(exc.getMessage());
        }
    }

}
