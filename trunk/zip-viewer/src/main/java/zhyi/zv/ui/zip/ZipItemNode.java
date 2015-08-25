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

import java.util.List;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;
import zhyi.zse.zip.ZipItem;
import zhyi.zse.zip.ZipSystem;

/**
 * Denotes a node in a {@link ZipTree}.
 * @author Zhao Yi
 */
public class ZipItemNode extends DefaultMutableTreeNode {
    private boolean childrenLoaded;

    public ZipItemNode(ZipItem zi) {
        super(zi, zi.isDirectory() || zi instanceof ZipSystem);
    }

    public ZipItem getZipItem() {
        return (ZipItem) userObject;
    }

    public boolean isChildrenLoaded() {
        return childrenLoaded;
    }

    public void setChildrenLoaded(boolean childrenLoaded) {
        this.childrenLoaded = childrenLoaded;
    }

    @SuppressWarnings("UseOfObsoleteCollectionType")
    public void setChildren(List<ZipItemNode> nodes) {
        for (ZipItemNode node : nodes) {
            node.parent = this;
        }
        children = new Vector(nodes);
    }

    @Override
    public boolean isLeaf() {
        return !allowsChildren;
    }
}
