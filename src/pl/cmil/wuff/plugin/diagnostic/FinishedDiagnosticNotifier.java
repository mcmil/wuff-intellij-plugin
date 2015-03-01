package pl.cmil.wuff.plugin.diagnostic;

import java.util.List;

import com.intellij.util.messages.Topic;

public interface FinishedDiagnosticNotifier
{
    Topic< FinishedDiagnosticNotifier > DIAGNOSTIC_TOPIC = Topic.create( "wuff-diagnostic",
        FinishedDiagnosticNotifier.class );

    void diagnosisSuccessful( List< BundleDiagnosis > bundles );

    void diagnosisStarted();

    void diagnosisFailed(String message);

}
