package pl.cmil.wuff.plugin.diagnostic;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.Module;

public class EquinoxDiagnosisProcessListener extends ProcessAdapter
{
    private Module appModule;

    public EquinoxDiagnosisProcessListener( Module appModule )
    {
        this.appModule = appModule;
    }

    @Override
    public void startNotified( ProcessEvent event )
    {
        super.startNotified( event );
        ServiceManager.getService( DiagnosticPanel.class );
        FinishedDiagnosticNotifier notifier =
            ApplicationManager.getApplication().getMessageBus()
                .syncPublisher( FinishedDiagnosticNotifier.DIAGNOSTIC_TOPIC );

        notifier.diagnosisStarted();
        Executors.newSingleThreadScheduledExecutor().schedule( ( ) -> {

            TelnetEquinoxDiagnostic diagnostic = new TelnetEquinoxDiagnostic();
            if( !diagnostic.diagnose() )
            {
                notifier.diagnosisFailed();
            }
            else
            {
                notifier.diagnosisSuccessful( diagnostic.getBundles(), diagnostic.getDiag() );

            }

        }, 10, TimeUnit.SECONDS );
    }

}
