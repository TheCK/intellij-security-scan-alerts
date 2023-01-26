package org.ck.githubsecurityscanalerts.store.settings;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.openapi.project.Project;

public class StoreUtil {
  public static CredentialAttributes getTokenAttributes(Project project) {
    return new CredentialAttributes("GitHub Code Scanning Alerts - Token - " + project.getName());
  }
}
