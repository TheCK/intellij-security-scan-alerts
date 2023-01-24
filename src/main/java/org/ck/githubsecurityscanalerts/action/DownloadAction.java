package org.ck.githubsecurityscanalerts.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import org.ck.githubsecurityscanalerts.github.GithubClient;
import org.ck.githubsecurityscanalerts.github.entity.Alert;
import org.ck.githubsecurityscanalerts.store.alerts.Alerts;
import org.ck.githubsecurityscanalerts.store.alerts.AlertsStore;
import org.ck.githubsecurityscanalerts.store.settings.ProjectSettings;
import org.ck.githubsecurityscanalerts.store.settings.ProjectSettingsStore;
import org.ck.githubsecurityscanalerts.ui.tool.AlertsPanel;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class DownloadAction extends DumbAwareAction {
  private static final Logger LOGGER = Logger.getInstance(DownloadAction.class);

  @Override
  public void update(AnActionEvent e) {
    super.update(e);

    final ProjectSettings state = e.getProject().getService(ProjectSettingsStore.class).getState();

    e.getPresentation()
        .setEnabled(
            !state.getGithubRepo().isEmpty()
                && !state.getGithubOwner().isEmpty()
                && !state.getGithubToken().isEmpty());
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    final ProjectSettings state = e.getProject().getService(ProjectSettingsStore.class).getState();
    final Alerts savedAlerts = e.getProject().getService(AlertsStore.class).getState();

    Set<Alert> alerts =
        new GithubClient(state.getGithubToken())
            .fetchAlerts(state.getGithubOwner(), state.getGithubRepo());

    LOGGER.warn("fetched %d alerts".formatted(alerts.size()));

    savedAlerts.setAlerts(
        alerts.stream()
            .map(
                alert ->
                    new org.ck.githubsecurityscanalerts.store.alerts.Alert(
                        alert.rule().id(),
                        getRuleName(alert),
                        alert.most_recent_instance().location().path(),
                        alert.most_recent_instance().location().start_line(),
                        alert.most_recent_instance().location().start_column()))
            .toList());

    ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(e.getProject());
    ToolWindow gitHubSecurityAlerts =
        toolWindowManager.getToolWindow("GitHub Security Scan Alerts");
    for (Content content : gitHubSecurityAlerts.getContentManager().getContents()) {
      if (content.getComponent() instanceof AlertsPanel alertsPanel) {
        alertsPanel.init();
      }
    }
  }

  @Override
  public @NotNull ActionUpdateThread getActionUpdateThread() {
    return ActionUpdateThread.BGT;
  }

  private String getRuleName(final Alert alert) {
    if (!alert.rule().name().isEmpty()) {
      return alert.rule().name();
    }

    if (!alert.rule().description().isEmpty()) {
      return alert.rule().description();
    }

    return alert.most_recent_instance().message().get("text");
  }
}
