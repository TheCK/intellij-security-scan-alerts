package org.ck.githubsecurityscanalerts.ui.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.ck.githubsecurityscanalerts.store.settings.ProjectSettingsStore;
import org.jetbrains.annotations.Nls;

import javax.swing.*;

public class ProjectConfigurable implements Configurable, Configurable.NoScroll {

  private final Project project;

  private SettingsPanel panel;

  public ProjectConfigurable(Project project) {
    this.project = project;
  }

  @Nls
  @Override
  public String getDisplayName() {
    return "Settings";
  }

  @Override
  public JComponent createComponent() {
    if (panel == null) {
      panel = new SettingsPanel(project);
    }
    return panel.getRootPane();
  }

  @Override
  public boolean isModified() {
    return panel != null
        && panel.isModified(project.getService(ProjectSettingsStore.class).getState());
  }

  @Override
  public void apply() throws ConfigurationException {
    if (panel != null) {
      var projectSettings = project.getService(ProjectSettingsStore.class).getState();
      panel.save(projectSettings);
    }
  }
}
