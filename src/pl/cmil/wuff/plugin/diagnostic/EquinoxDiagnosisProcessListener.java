package pl.cmil.wuff.plugin.diagnostic;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.module.Module;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EquinoxDiagnosisProcessListener  extends ProcessAdapter
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
        FinishedDiagnosticNotifier notifier =
            appModule.getProject().getMessageBus()
                .syncPublisher( FinishedDiagnosticNotifier.DIAGNOSTIC_TOPIC );

        notifier.diagnosisStarted();
        Executors.newSingleThreadScheduledExecutor().schedule(
            ( ) -> {

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
