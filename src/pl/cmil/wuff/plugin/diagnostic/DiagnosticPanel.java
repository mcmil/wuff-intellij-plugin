package pl.cmil.wuff.plugin.diagnostic;

import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class DiagnosticPanel implements ApplicationComponent {
    private final JComponent component;

    public DiagnosticPanel(Application application) {
        component = new JBPanel(new BorderLayout());

        DefaultListModel<BundleDiagnosis> listModel = JBList.createDefaultListModel();

        JBList list = new JBList(listModel);
        JBScrollPane scrollPane = new JBScrollPane(list);

        list.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                super.setSelectionInterval(-1, -1);

            }
        });

        list.getEmptyText().setText("No diagnosis is ready");

        component.add(scrollPane, BorderLayout.CENTER);
        list.setCellRenderer(new ColoredListCellRenderer<BundleDiagnosis>() {
            @Override
            protected void customizeCellRenderer(JList jList, BundleDiagnosis o, int i, boolean b, boolean b1) {
                this.setBackground(null);
                if (o.getStatus() == BundleDiagnosis.Status.OK) {
                    this.setIcon(AllIcons.RunConfigurations.TestPassed);
                    append(o.getName(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
                } else {
                    this.setIcon(AllIcons.RunConfigurations.TestError);
                    append(o.getName(), SimpleTextAttributes.ERROR_ATTRIBUTES);
                }

                mySelected = false;

                append(" (" + o.getId() + ")", SimpleTextAttributes.GRAY_ATTRIBUTES);

            }
        });

        application.getMessageBus().connect()
                .subscribe(FinishedDiagnosticNotifier.DIAGNOSTIC_TOPIC, new FinishedDiagnosticNotifier() {
                    @Override
                    public void diagnosisSuccessful(java.util.List<BundleDiagnosis> bundles) {

                        ApplicationManager.getApplication().invokeLater(
                                () -> {
                                    listModel.clear();
                                    bundles.stream().forEach(e -> listModel.addElement(e));

                                    long errorBundles =
                                            bundles.stream().filter(e -> e.getStatus() == BundleDiagnosis.Status.NOT)
                                                    .count();
                                    if (errorBundles > 0) {
                                        Notification bundleErrorNotification =
                                                new Notification("wuff.diagnostic.error", "Wuff Diagnostic Error",
                                                        errorBundles + " bundle" + (errorBundles != 1 ? "s" : "")
                                                                + " not resolved", NotificationType.ERROR);
                                        bundleErrorNotification.setImportant(true);
                                        Notifications.Bus.notify(bundleErrorNotification);
                                    }

                                });

                    }

                    @Override
                    public void diagnosisStarted() {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            list.getEmptyText().setText("Diagnosis in progress...");
                            listModel.clear();
                        });
                    }

                    @Override
                    public void diagnosisFailed() {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            list.getEmptyText().setText("Diagnosis Failed");
                            listModel.clear();
                        });
                    }
                });
    }

    public JComponent getComponent() {
        return component;
    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "DiagnosticPanel";
    }
}
