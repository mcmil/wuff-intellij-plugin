package pl.cmil.wuff.plugin;

import java.awt.*;
import java.util.List;

import javax.swing.*;

import com.intellij.notification.NotificationsConfiguration;
import pl.cmil.wuff.plugin.diagnostic.BundleDiagnosis;
import pl.cmil.wuff.plugin.diagnostic.FinishedDiagnosticNotifier;

import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;

public class EquinoxStateToolWindowFactory implements ToolWindowFactory
{
    @Override
    public void createToolWindowContent( Project project, ToolWindow toolWindow )
    {
        NotificationsConfiguration.getNotificationsConfiguration().register( "wuff.diagnostic",
            NotificationDisplayType.STICKY_BALLOON, true );

        JComponent component = toolWindow.getComponent();

        DefaultListModel< BundleDiagnosis > listModel = JBList.createDefaultListModel();

        JBList list = new JBList( listModel );
        JBScrollPane scrollPane = new JBScrollPane( list );
        JBLabel diagArea = new JBLabel();

        list.setSelectionModel( new DefaultListSelectionModel()
        {
            @Override
            public void setSelectionInterval( int index0, int index1 )
            {
                super.setSelectionInterval( -1, -1 );

            }
        } );

        list.getEmptyText().setText( "No diagnosis is ready" );

        component.add( scrollPane, BorderLayout.CENTER );
        component.add( diagArea, BorderLayout.SOUTH );
        list.setCellRenderer( new ColoredListCellRenderer< BundleDiagnosis >()
        {
            @Override
            protected void customizeCellRenderer( JList jList, BundleDiagnosis o, int i, boolean b, boolean b1 )
            {
                this.setBackground( null );
                if( o.getStatus() == BundleDiagnosis.Status.OK )
                {
                    this.setIcon( AllIcons.RunConfigurations.TestPassed );
                    append( o.getName(), SimpleTextAttributes.REGULAR_ATTRIBUTES );
                }
                else
                {
                    this.setIcon( AllIcons.RunConfigurations.TestError );
                    append( o.getName(), SimpleTextAttributes.ERROR_ATTRIBUTES );
                }

                mySelected = false;

                append( " (" + o.getId() + ")", SimpleTextAttributes.GRAY_ATTRIBUTES );

            }
        } );

        project.getMessageBus().connect()
            .subscribe( FinishedDiagnosticNotifier.DIAGNOSTIC_TOPIC, new FinishedDiagnosticNotifier()
            {
                @Override
                public void diagnosisSuccessful( List< BundleDiagnosis > bundles, String diag )
                {

                    ApplicationManager.getApplication().invokeLater(
                        ( ) -> {
                            listModel.clear();
                            bundles.stream().forEach( e -> listModel.addElement( e ) );
                            diagArea.setText( diag );

                            long errorBundles =
                                bundles.stream().filter( e -> e.getStatus() == BundleDiagnosis.Status.NOT )
                                    .count();
                            if( errorBundles > 0 )
                            {
                                Notification bundleErrorNotification =
                                    new Notification( "wuff.diagnostic", "Wuff Diagnostic Error",
                                        errorBundles + " bundles not resolved", NotificationType.ERROR );
                                Notifications.Bus.notify( bundleErrorNotification, project );

                            }

                        } );

                }

                @Override
                public void diagnosisStarted()
                {
                    ApplicationManager.getApplication().invokeLater( ( ) -> {
                        list.getEmptyText().setText( "Diagnosis in progress..." );
                        listModel.clear();
                        diagArea.setText( "Waiting for results..." );
                    } );
                }

                @Override
                public void diagnosisFailed()
                {
                    ApplicationManager.getApplication().invokeLater( ( ) -> {
                        list.getEmptyText().setText( "Diagnosis Failed" );
                        listModel.clear();
                        diagArea.setText( "" );
                    } );
                }
            } );

    }
}
