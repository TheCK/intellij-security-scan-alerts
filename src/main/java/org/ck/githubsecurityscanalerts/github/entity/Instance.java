package org.ck.githubsecurityscanalerts.github.entity;

import java.util.Map;
import java.util.Set;

public record Instance(
    String ref,
    String analysis_key,
    String environment,
    String state,
    String commit_sha,
    Map<String, String> message,
    Location location,
    Set<String> classifications) {}
