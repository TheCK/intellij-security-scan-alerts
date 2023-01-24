package org.ck.githubsecurityscanalerts.store.alerts;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.serviceContainer.NonInjectable;

@State(
    name = "AlertsStore",
    storages = {@Storage("GitHubSecurityScanAlerts-alerts.xml")})
public final class AlertsStore implements PersistentStateComponent<Alerts> {

  private Alerts settings = new Alerts();

  public AlertsStore() {}

  @NonInjectable
  public AlertsStore(Alerts toCopy) {
    loadState(toCopy);
  }

  @Override
  public synchronized Alerts getState() {
    return settings;
  }

  @Override
  public synchronized void loadState(Alerts settings) {
    this.settings = settings;
  }
}
