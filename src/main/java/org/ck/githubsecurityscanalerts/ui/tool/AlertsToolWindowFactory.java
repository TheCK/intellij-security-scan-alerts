package org.ck.githubsecurityscanalerts.ui.tool;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import org.ck.githubsecurityscanalerts.store.alerts.Alert;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class AlertsToolWindowFactory implements ToolWindowFactory {
  public static final String TAB_ALERTS = "Alerts";
  public static final String TAB_FILES = "Files";

  @Override
  public void createToolWindowContent(@NotNull Project project, final ToolWindow toolWindow) {
    ContentManager contentManager = toolWindow.getContentManager();
    ContentFactory contentFactory = contentManager.getFactory();

    contentManager.addContent(
        contentFactory.createContent(
            new AlertsPanel(
                project,
                alerts -> alerts.getAlerts().stream().collect(Collectors.groupingBy(Alert::getId)),
                Alert::getName,
                this::getRuleFile),
            TAB_ALERTS,
            false));

    contentManager.addContent(
        contentFactory.createContent(
            new AlertsPanel(
                project,
                alerts ->
                    alerts.getAlerts().stream().collect(Collectors.groupingBy(Alert::getPath)),
                this::getRuleFile,
                Alert::getName),
            TAB_FILES,
            false));
  }

  private String getRuleFile(final Alert alert) {
    String[] parts = alert.getPath().split("/");
    return parts[parts.length - 1];
  }
}
