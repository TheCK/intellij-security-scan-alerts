package org.ck.githubsecurityscanalerts.github;

import com.google.gson.Gson;
import org.ck.githubsecurityscanalerts.github.entity.Alert;
import org.ck.githubsecurityscanalerts.github.entity.Alerts;
import org.ck.githubsecurityscanalerts.github.entity.Analyses;
import org.ck.githubsecurityscanalerts.github.entity.Analysis;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class GithubClient {

  private static final String ALERT_URL =
      "https://api.github.com/repos/%s/%s/code-scanning/alerts?state=open&per_page=100&page=%d";
  private static final String ALERT_COUNT_URL =
      "https://api.github.com/repos/%s/%s/code-scanning/analyses";

  private final String token;

  public GithubClient(final String token) {
    this.token = token;
  }

  public Set<Alert> fetchAlerts(String owner, String repo) {
    HttpClient client = HttpClient.newHttpClient();
    Set<Alert> alerts = new HashSet<>();

    boolean fetchMore = true;
    int i = 0;

    while (fetchMore) {
      HttpRequest request =
          HttpRequest.newBuilder(URI.create(ALERT_URL.formatted(owner, repo, i)))
              .header("accept", "application/vnd.github+json")
              .header("X-GitHub-Api-Version", "2022-11-28")
              .header("Authorization", "Bearer %s".formatted(token))
              .GET()
              .build();

      try {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        final Alerts currentAlerts = new Gson().fromJson(response.body(), Alerts.class);
        alerts.addAll(currentAlerts);

        if (currentAlerts.size() < 100) {
          fetchMore = false;
        }
      } catch (IOException | InterruptedException e) {
        // ignore
      }

      ++i;
    }

    return alerts;
  }

  public Optional<Long> getAlertCount(String owner, String repo) {
    HttpClient client = HttpClient.newHttpClient();

    HttpRequest request =
        HttpRequest.newBuilder(URI.create(ALERT_COUNT_URL.formatted(owner, repo)))
            .header("accept", "application/vnd.github+json")
            .header("X-GitHub-Api-Version", "2022-11-28")
            .header("Authorization", "Bearer %s".formatted(token))
            .GET()
            .build();

    try {
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        final Analyses analyses = new Gson().fromJson(response.body(), Analyses.class);

        return Optional.of(analyses.stream().mapToLong(Analysis::results_count).sum());
      }
    } catch (IOException | InterruptedException e) {
      // ignore
    }

    return Optional.empty();
  }
}
