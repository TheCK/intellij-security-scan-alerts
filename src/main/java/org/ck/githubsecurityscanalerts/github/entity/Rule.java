package org.ck.githubsecurityscanalerts.github.entity;

import java.util.Set;

public record Rule(
    String id, String severity, Set<String> tags, String description, String name) {}
