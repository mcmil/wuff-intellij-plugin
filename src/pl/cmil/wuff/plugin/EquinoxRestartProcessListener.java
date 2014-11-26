package pl.cmil.wuff.plugin;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import pl.cmil.wuff.plugin.diagnostic.FinishedDiagnosticNotifier;
import pl.cmil.wuff.plugin.diagnostic.TelnetEquinoxDiagnostic;

import com.intellij.execution.Executor;
import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;

/**
 * If the process started for equinox returns with a specific exit code (defined by Equinox) restarts the run
 * config.
 */
class EquinoxRestartProcessListener extends ProcessAdapter
{
    public static final int EQUINOX_RESTART_EXIT_CODE = 23;

    private Module appModule;
    private ExecutionEnvironment environment;
    private Executor executor;

    public EquinoxRestartProcessListener( Module appModule, ExecutionEnvironment environment,
        Executor executor )
    {
        this.appModule = appModule;
        this.environment = environment;
        this.executor = executor;
    }

    @Override
    public void processTerminated( ProcessEvent event )
    {
        super.processTerminated( event );
        int exitCode = event.getExitCode();

        if( exitCode == EQUINOX_RESTART_EXIT_CODE )
        {
            ApplicationManager.getApplication().runReadAction( new Runnable()
            {
                @Override
                public void run()
                {
                    ProgramRunnerUtil.executeConfiguration( appModule.getProject(),
                        environment.getRunnerAndConfigurationSettings(), executor );
                }
            } );

        }
    }
}
