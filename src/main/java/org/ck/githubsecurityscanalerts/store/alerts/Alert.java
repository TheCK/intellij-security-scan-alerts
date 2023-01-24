package org.ck.githubsecurityscanalerts.store.alerts;

public final class Alert {

  private String id = "";
  private String name = "";
  private String path = "";

  private int startLine = 0;

  private int startColumn = 0;

  public Alert() {}

  public Alert(
      final String id,
      final String name,
      final String path,
      final int startLine,
      final int startColumn) {
    this.id = id;
    this.name = name;
    this.path = path;
    this.startLine = startLine;
    this.startColumn = startColumn;
  }

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getPath() {
    return path;
  }

  public void setPath(final String path) {
    this.path = path;
  }

  public int getStartLine() {
    return startLine;
  }

  public void setStartLine(final int startLine) {
    this.startLine = startLine;
  }

  public int getStartColumn() {
    return startColumn;
  }

  public void setStartColumn(final int startColumn) {
    this.startColumn = startColumn;
  }
}
