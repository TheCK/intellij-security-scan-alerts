package org.ck.githubsecurityscanalerts.store.alerts;

import java.util.ArrayList;
import java.util.List;

public final class Alerts {

  private List<Alert> alerts = new ArrayList<>();

  public List<Alert> getAlerts() {
    return alerts;
  }

  public void setAlerts(final List<Alert> alerts) {
    this.alerts = alerts;
  }
}
