package org.ck.githubsecurityscanalerts.github.entity;

public record Alert(
    int number,
    String created_at,
    String url,
    String html_url,
    String state,
    String fixed_at,
    DismissedBy dismissed_by,
    String dismissed_at,
    String dismissed_reason,
    String dismissed_comment,
    Rule rule,
    Tool tool,
    Instance most_recent_instance,
    String instances_url) {}
