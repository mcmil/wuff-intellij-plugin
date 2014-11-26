package pl.cmil.wuff.plugin;

import pl.cmil.wuff.plugin.diagnostic.DiagnosticPanel;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;

public class EquinoxStateToolWindowFactory implements ToolWindowFactory
{
    @Override
    public void createToolWindowContent( Project project, ToolWindow toolWindow )
    {

        DiagnosticPanel service = ServiceManager.getService(  DiagnosticPanel.class );
        toolWindow.getComponent().add( service.getComponent() );
    }
}
