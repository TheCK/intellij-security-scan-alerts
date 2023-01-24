package org.ck.githubsecurityscanalerts.github.entity;

public record Location(
    String path, int start_line, int end_line, int start_column, int end_column) {}
