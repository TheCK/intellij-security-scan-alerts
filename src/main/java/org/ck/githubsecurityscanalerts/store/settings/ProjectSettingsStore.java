package org.ck.githubsecurityscanalerts.store.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.serviceContainer.NonInjectable;

@State(
    name = "ProjectSettingsStore",
    storages = {@Storage("GitHubSecurityScanAlerts.xml")})
public final class ProjectSettingsStore implements PersistentStateComponent<ProjectSettings> {

  private ProjectSettings settings = new ProjectSettings();

  public ProjectSettingsStore() {}

  @NonInjectable
  public ProjectSettingsStore(ProjectSettings toCopy) {
    loadState(toCopy);
  }

  @Override
  public synchronized ProjectSettings getState() {
    return settings;
  }

  @Override
  public synchronized void loadState(ProjectSettings settings) {
    this.settings = settings;
  }
}
