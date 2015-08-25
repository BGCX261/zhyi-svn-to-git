/*
 * Copyright (C) 2011 Zhao Yi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package zhyi.zv.ui.zip;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import zhyi.zse.io.FileType;
import zhyi.zse.zip.ZipItem;
import zhyi.zse.swing.ExceptionDialog;
import zhyi.zse.zip.ZipSystem;

/**
 * A tree component that can display multiple zip files at the same time. The
 * root node is invisible, direct children of root node are of type {@link
 * ZipSystemNode}, and the other nodes are of type {@link ZipItemNode}.
 * @author Zhao Yi
 */
public class ZipTree extends JTree {
    /**
     * Holds background tasks that are being executed. Keys are nodes
     * representing zip file items which are direct children of the root node.
     * This is used to cancel all running background tasks when a zip file item
     * is being remove.
     */
    private ConcurrentMap<ZipItemNode, CopyOnWriteArrayList<SwingWorker<?, ?>>> taskMap;

    /**
     * Constructs a new instance. The root node is hidden to simulate a
     * multi-roots tree.
     */
    public ZipTree() {
        // The root node is hidden.
        super(new DefaultMutableTreeNode());
        getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        setRootVisible(false);
        setShowsRootHandles(true);
        setCellRenderer(new ZipItemRenderer());
        addTreeWillExpandListener(new TreeWillExpandHandler());
        taskMap = new ConcurrentHashMap<>();
    }

    /**
     * Opens a zip system in the tree and expands the first level.
     */
    public void open(ZipSystem zs) {
        ZipItemNode node = new ZipItemNode(zs);
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) getModel().getRoot();
        getModel().insertNodeInto(node, rootNode, rootNode.getChildCount());
        taskMap.put(node, new CopyOnWriteArrayList<SwingWorker<?, ?>>());
        expandPath(new TreePath(node.getPath()));
    }

    /**
     * Removes the specified {@link ZipItemNode} if it's a child of the hidden
     * root node, otherwise do nothing. This is used for closing a zip file.
     */
    public void close(ZipItemNode node) {
        if (((DefaultMutableTreeNode) node.getParent()).isRoot()) {
            // Cancel all running tasks.
            for (SwingWorker<?, ?> task : taskMap.get(node)) {
                task.cancel(true);
            }
            taskMap.remove(node);
            getModel().removeNodeFromParent(node);
        }
    }

    /**
     * Removes all children of the hidden node. This is used for closing all zip
     * files.
     */
    public void closeAll() {
        for (Entry<ZipItemNode, CopyOnWriteArrayList<SwingWorker<?, ?>>> entry
                : taskMap.entrySet()) {
            for (SwingWorker<?, ?> task : entry.getValue()) {
                task.cancel(true);
            }
        }
        taskMap.clear();
        setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
    }

    @Override
    public DefaultTreeModel getModel() {
        return (DefaultTreeModel) treeModel;
    }

    private void lasilyLoadChildren(ZipItemNode node) {
        if (!node.isChildrenLoaded()) {
            // Displaying a loading node.
            DefaultMutableTreeNode loadingNode = new DefaultMutableTreeNode("Loading...");
            getModel().insertNodeInto(loadingNode, node, 0);
            new TreeNodeExpansionTask(node).execute();
        }
    }

    /**
     * Used for displaying file icons for nodes.
     */
    private class ZipItemRenderer implements TreeCellRenderer {
        private TreeCellRenderer defaultRenderer;

        public ZipItemRenderer() {
            defaultRenderer = getCellRenderer();
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            JLabel cell = (JLabel) defaultRenderer.getTreeCellRendererComponent(
                    tree, value, selected, expanded, leaf, row, hasFocus);
            if (value instanceof ZipItemNode) {
                ZipItem zi = ((ZipItemNode) value).getZipItem();
                if (!zi.isDirectory()) {
                    cell.setIcon(FileType.getType(zi.getName()).getSmallIcon());
                }
            }
            return cell;
        }
    }

    private class TreeWillExpandHandler implements TreeWillExpandListener {
        @Override
        public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
            Object o = event.getPath().getLastPathComponent();
            if (o instanceof ZipItemNode) {
                lasilyLoadChildren((ZipItemNode) o);
            }
        }

        @Override
        public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
        }
    }

    private class TreeNodeExpansionTask extends SwingWorker<List<ZipItemNode>, Void> {
        private ZipItemNode expandingNode;
        private ZipItemNode zipFileNode;

        public TreeNodeExpansionTask(ZipItemNode node) {
            this.expandingNode = node;
            zipFileNode = (ZipItemNode) node.getPath()[1];
        }

        @Override
        protected List<ZipItemNode> doInBackground() throws Exception {
            taskMap.get(zipFileNode).add(this);

            ZipItem zi = expandingNode.getZipItem();
            List<ZipItem> children = zi.isDirectory() ?
                    children = zi.listChildren() : ((ZipSystem) zi).listRoots();
            Collections.sort(children);

            List<ZipItemNode> nodes = new ArrayList<>(children.size());
            for (ZipItem child : children) {
                nodes.add(new ZipItemNode(child));
            }
            return nodes;
        }

        @Override
        protected void done() {
            taskMap.get(zipFileNode).remove(this);
            if (!isCancelled()) {
                try {
                    // Remove the loading node.
                    getModel().removeNodeFromParent(expandingNode.getFirstLeaf());
                    expandingNode.setChildren(get());
                    expandingNode.setChildrenLoaded(true);
                    int[] indices = new int[expandingNode.getChildCount()];
                    for (int i = 0; i < indices.length; i++) {
                        indices[i] = i;
                    }
                    getModel().nodesWereInserted(expandingNode, indices);
                } catch (InterruptedException | ExecutionException ex) {
                    // The node will be removed if opening a zip file fails.
                    close(expandingNode);
                    ExceptionDialog.showException(ex, ZipTree.this);
                }
            }
        }
    }
}
