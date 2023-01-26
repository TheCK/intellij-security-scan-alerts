package org.ck.githubsecurityscanalerts.ui.settings;

import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBInsets;
import com.intellij.util.ui.JBUI;
import org.ck.githubsecurityscanalerts.github.GithubClient;
import org.ck.githubsecurityscanalerts.store.settings.ProjectSettings;
import org.ck.githubsecurityscanalerts.store.settings.ProjectSettingsStore;
import org.ck.githubsecurityscanalerts.store.settings.StoreUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Optional;

import static java.awt.GridBagConstraints.*;

public class SettingsPanel {

  private final Project project;
  private final JPanel root;

  private JBTextField githubToken;

  private JBTextField githubOwner;

  private JBTextField githubRepo;

  private JButton testButton;

  public SettingsPanel(Project project) {
    this.project = project;
    root = new JPanel(new BorderLayout());

    JPanel grid = new JPanel(new GridBagLayout());
    JBInsets insets = JBUI.insetsTop(2);

    grid.add(
        new JLabel("GitHub token:"),
        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets, 0, 0));
    githubToken = new JBTextField();
    githubToken.getEmptyText().setText("GitHub token");
    grid.add(
        githubToken, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets, 0, 0));

    grid.add(
        new JLabel("GitHub owner:"),
        new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, WEST, NONE, insets, 0, 0));
    githubOwner = new JBTextField();
    githubOwner.getEmptyText().setText("GitHub owner");
    grid.add(
        githubOwner, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets, 0, 0));

    grid.add(
        new JLabel("GitHub repo:"),
        new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, WEST, NONE, insets, 0, 0));
    githubRepo = new JBTextField();
    githubRepo.getEmptyText().setText("GitHub repo");
    grid.add(
        githubRepo, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets, 0, 0));

    testButton = new JButton();
    testButton.setAction(
        new AbstractAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            final Optional<Long> alertCount =
                new GithubClient(
                        PasswordSafe.getInstance()
                            .getPassword(StoreUtil.getTokenAttributes(project)))
                    .getAlertCount(getGithubOwner(), getGithubRepo());

            if (alertCount.isPresent()) {
              NotificationGroupManager.getInstance()
                  .getNotificationGroup("GitHub Security Scan Alerts")
                  .createNotification(
                      "Connection OK: %d alerts found".formatted(alertCount.get()),
                      NotificationType.INFORMATION)
                  .setImportant(true)
                  .notify(project);
            } else {
              NotificationGroupManager.getInstance()
                  .getNotificationGroup("GitHub Security Scan Alerts")
                  .createNotification("Connection failed", NotificationType.WARNING)
                  .setImportant(true)
                  .notify(project);
            }
          }
        });
    testButton.setText("Test Config");
    grid.add(testButton, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, WEST, NONE, insets, 0, 0));

    root.add(grid, BorderLayout.NORTH);
    load(project.getService(ProjectSettingsStore.class).getState());
  }

  public JPanel getRootPane() {
    return root;
  }

  public void load(ProjectSettings projectSettings) {
    if (PasswordSafe.getInstance().getPassword(StoreUtil.getTokenAttributes(project)) != null) {
      githubToken.getEmptyText().setText("< Saved Github token >");
    }

    githubOwner.setText(projectSettings.getGithubOwner());
    githubRepo.setText(projectSettings.getGithubRepo());
  }

  public void save(ProjectSettings projectSettings) {
    if (!getGithubToken().isEmpty()) {
      Credentials credentials = new Credentials(null, getGithubToken());
      PasswordSafe.getInstance().set(StoreUtil.getTokenAttributes(project), credentials);
    }

    projectSettings.setGithubOwner(getGithubOwner());
    projectSettings.setGithubRepo(getGithubRepo());
  }

  public boolean isModified(ProjectSettings projectSettings) {
    return !getGithubToken().isEmpty()
        || !projectSettings.getGithubOwner().equals(getGithubOwner())
        || !projectSettings.getGithubRepo().equals(getGithubRepo());
  }

  public String getGithubToken() {
    return githubToken.getText();
  }

  public String getGithubOwner() {
    return githubOwner.getText();
  }

  public String getGithubRepo() {
    return githubRepo.getText();
  }
}
