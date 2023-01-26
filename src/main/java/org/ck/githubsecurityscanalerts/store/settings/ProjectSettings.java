package org.ck.githubsecurityscanalerts.store.settings;

public final class ProjectSettings {

  private String githubOwner = "";
  private String githubRepo = "";

  public String getGithubOwner() {
    return githubOwner;
  }

  public void setGithubOwner(final String githubOwner) {
    this.githubOwner = githubOwner;
  }

  public String getGithubRepo() {
    return githubRepo;
  }

  public void setGithubRepo(final String githubRepo) {
    this.githubRepo = githubRepo;
  }
}
