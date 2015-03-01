package pl.cmil.wuff.plugin.diagnostic;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.Module;
import pl.cmil.wuff.plugin.diagnostic.rest.RestDiagnosticDataProvider;
import pl.cmil.wuff.plugin.diagnostic.rest.WebConsoleDiagnosticProvider;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EquinoxDiagnosisProcessListener extends ProcessAdapter {
    private Module appModule;
    private String json = "{\"status\":\"Bundle information: 3 bundles in total, 1 bundles active, 0 bundles active fragments, 2 bundles resolved.\",\"s\":[3,1,0,2,0],\"data\":[{\"id\":0,\"name\":\"OSGi System Bundle\",\"fragment\":false,\"stateRaw\":32,\"state\":\"Active\",\"version\":\"3.10.0.v20140606-1445\",\"symbolicName\":\"org.eclipse.osgi\",\"category\":\"\"},{\"id\":3,\"name\":\"Apache Commons FileUpload Bundle\",\"fragment\":false,\"stateRaw\":4,\"state\":\"Resolved\",\"version\":\"1.2.1\",\"symbolicName\":\"org.apache.commons.fileupload\",\"category\":\"\"},{\"id\":4,\"name\":\"Apache Commons IO Bundle\",\"fragment\":false,\"stateRaw\":4,\"state\":\"Installed\",\"version\":\"1.4\",\"symbolicName\":\"org.apache.commons.io\",\"category\":\"\"}]}";
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();


    public EquinoxDiagnosisProcessListener(Module appModule) {
        this.appModule = appModule;
    }


    @Override
    public void startNotified(ProcessEvent event) {
        super.startNotified(event);
        ServiceManager.getService(DiagnosticPanel.class);
        FinishedDiagnosticNotifier notifier =
                ApplicationManager.getApplication().getMessageBus()
                        .syncPublisher(FinishedDiagnosticNotifier.DIAGNOSTIC_TOPIC);

        notifier.diagnosisStarted();
        executor.scheduleWithFixedDelay(() -> startDiagnosticJob(notifier), 0, 10, TimeUnit.SECONDS);

    }

    private void startDiagnosticJob(FinishedDiagnosticNotifier notifier) {
        new WebConsoleDiagnosticProvider(new RestDiagnosticDataProvider("http://localhost:5310/system/console/", "admin", "admin"), executor
        ).diagnose().thenAccept(res -> notifier.diagnosisSuccessful(res)).whenComplete((e, t) -> t.printStackTrace());
    }

}
