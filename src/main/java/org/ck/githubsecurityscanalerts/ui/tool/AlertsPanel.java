package org.ck.githubsecurityscanalerts.ui.tool;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.treeStructure.Tree;
import org.ck.githubsecurityscanalerts.store.alerts.Alert;
import org.ck.githubsecurityscanalerts.store.alerts.Alerts;
import org.ck.githubsecurityscanalerts.store.alerts.AlertsStore;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class AlertsPanel extends JPanel {

  private final Project project;
  private final Function<Alerts, Map<String, List<Alert>>> grouper;
  private final Function<Alert, String> getFirstLevel;
  private final Function<Alert, String> getSecondLevel;

  public AlertsPanel(
      Project project,
      Function<Alerts, Map<String, List<Alert>>> grouper,
      Function<Alert, String> getFirstLevel,
      Function<Alert, String> getSecondLevel) {
    super(new BorderLayout());
    this.project = project;
    this.grouper = grouper;
    this.getFirstLevel = getFirstLevel;
    this.getSecondLevel = getSecondLevel;

    init();
  }

  public void init() {
    this.removeAll();

    final Alerts savedAlerts = project.getService(AlertsStore.class).getState();

    DefaultMutableTreeNode root = prepareRoot(grouper.apply(savedAlerts), getFirstLevel);

    Tree tree = prepareTree(root, getSecondLevel);

    this.add(ScrollPaneFactory.createScrollPane(tree), BorderLayout.CENTER);
  }

  @NotNull
  private Tree prepareTree(
      final DefaultMutableTreeNode root, final Function<Alert, String> getSecondLevel) {
    Tree tree = new Tree(root);
    tree.expandRow(0);
    tree.setShowsRootHandles(false);
    tree.setCellRenderer(
        new DefaultTreeCellRenderer() {
          @Override
          public Component getTreeCellRendererComponent(
              JTree tree,
              Object value,
              boolean sel,
              boolean expanded,
              boolean leaf,
              int row,
              boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();
            if (userObject instanceof String string) {
              this.setText(string);
            } else if (userObject instanceof Alert alert) {
              this.setText(getSecondLevel.apply(alert));
            }

            return this;
          }
        });

    MouseListener byFileListener =
        new MouseAdapter() {
          @Override
          public void mousePressed(MouseEvent e) {
            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
            if (path != null && path.getPathCount() == 3 && e.getClickCount() == 2) {
              DefaultMutableTreeNode node =
                  (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
              open((Alert) node.getUserObject());
            }
          }
        };
    tree.addMouseListener(byFileListener);
    return tree;
  }

  @NotNull
  private static DefaultMutableTreeNode prepareRoot(
      final Map<String, List<Alert>> groupedAlerts, final Function<Alert, String> getFirstLevel) {
    DefaultMutableTreeNode root =
        new DefaultMutableTreeNode("Alerts (%d)".formatted(groupedAlerts.size()));
    for (Map.Entry<String, List<Alert>> group : groupedAlerts.entrySet()) {
      DefaultMutableTreeNode node =
          new DefaultMutableTreeNode(
              getFirstLevel.apply(group.getValue().get(0))
                  + " (%d)".formatted(group.getValue().size()));

      for (Alert alert : group.getValue()) {
        node.add(new DefaultMutableTreeNode(alert));
      }

      root.add(node);
    }
    return root;
  }

  private void open(Alert alert) {
    String path = alert.getPath();
    String[] parts = path.split("/");

    Collection<VirtualFile> files =
        FilenameIndex.getVirtualFilesByName(
            parts[parts.length - 1], GlobalSearchScope.allScope(project));

    Optional<VirtualFile> actualFile =
        files.stream().filter(f -> f.getPath().contains(path)).findFirst();

    actualFile.ifPresent(
        virtualFile ->
            FileEditorManager.getInstance(project)
                .openTextEditor(
                    new OpenFileDescriptor(
                        project, virtualFile, alert.getStartLine() - 1, alert.getStartColumn() - 1),
                    true));
  }
}
