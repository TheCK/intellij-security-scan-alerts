package org.ck.githubsecurityscanalerts.github.entity;

public record Analysis(
    String ref,
    String commit_sha,
    String analysis_key,
    String environment,
    String error,
    String category,
    String created_at,
    int results_count,
    int rules_count,
    int id,
    String url,
    String sarif_id,
    Tool tool,
    boolean deletable,
    String warning) {}
