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
package zhyi.zv.ui.viewer;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import zhyi.zse.swing.ExceptionDialog;
import zhyi.zse.swing.SwingHelper;
import zhyi.zse.zip.ZipItem;

/**
 * Wraps several components to display the contents of a {@link ZipItem}.
 * @author Zhao Yi
 */
public class ViewerContainer extends JPanel {
    private ZipItem zipItem;
    private Map<ViewerType, Viewer> viewerMap;

    private PlainTextViewer plainTextViewer;
    private HtmlViewer htmlViewer;
    private RichTextViewer richTextViewer;
    private ImageViewer imageViewer;
    private HexViewer hexViewer;

    private JComboBox<ViewerType> viewerTypeComboBox;
    private JComboBox<Charset> charsetComboBox;
    private JScrollPane viewerScrollPane;

    @SuppressWarnings("LeakingThisInConstructor")
    public ViewerContainer(ZipItem zipItem, ViewerType viewerType,
            Charset charset) throws IOException {
        this.zipItem = Objects.requireNonNull(zipItem);
        viewerMap = new EnumMap<>(ViewerType.class);

        plainTextViewer = new PlainTextViewer();
        viewerMap.put(ViewerType.PLAIN_TEXT, plainTextViewer);
        htmlViewer = new HtmlViewer();
        viewerMap.put(ViewerType.HTML, htmlViewer);
        richTextViewer = new RichTextViewer();
        viewerMap.put(ViewerType.RICH_TEXT, richTextViewer);
        imageViewer = new ImageViewer();
        viewerMap.put(ViewerType.IMAGE, imageViewer);
        hexViewer = new HexViewer();
        viewerMap.put(ViewerType.HEXADECIMAL, hexViewer);

        JLabel viewAsLabel = new JLabel("View As:");
        viewerTypeComboBox = new JComboBox<>(ViewerType.values());
        viewerTypeComboBox.setSelectedItem(Objects.requireNonNull(viewerType));
        viewerTypeComboBox.setToolTipText("Switch between different viewers.");
        viewerTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view();
            }
        });

        JLabel charsetLabel = new JLabel("Charset:");
        charsetComboBox = new JComboBox<>(
                Charset.availableCharsets().values().toArray(new Charset[0]));
        charsetComboBox.setSelectedItem(Objects.requireNonNull(charset));
        charsetComboBox.setToolTipText(
                "<html><b>Select the charset to display text.</b>"
                + "<br>Note an improper charset may lead to messy codes.</html>");
        charsetComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    reloadContent();
                } catch (IOException ex) {
                    ExceptionDialog.showException(ex, ViewerContainer.this);
                }
            }
        });
        viewerScrollPane = new JScrollPane();

        GroupLayout gl = SwingHelper.createGroupLayout(this);
        gl.setHorizontalGroup(gl.createParallelGroup()
                .addGroup(gl.createSequentialGroup()
                        .addComponent(viewAsLabel)
                        .addComponent(viewerTypeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Integer.MAX_VALUE)
                        .addComponent(charsetLabel)
                        .addComponent(charsetComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
                .addComponent(viewerScrollPane));
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                        .addComponent(viewAsLabel)
                        .addComponent(viewerTypeComboBox)
                        .addComponent(charsetLabel)
                        .addComponent(charsetComboBox))
                .addComponent(viewerScrollPane));
        view();
    }

    public ViewerType getCurrentViewerType() {
        return (ViewerType) viewerTypeComboBox.getSelectedItem();
    }

    public void viewAs(ViewerType viewerType, Charset charset) {
        charsetComboBox.setSelectedItem(Objects.requireNonNull(charset));
        // `view()' will be automatically called by the `ActionListener'
        // registered to `viewerTypeComboBox'.
        viewerTypeComboBox.setSelectedItem(Objects.requireNonNull(viewerType));
    }

    private void view() {
        ViewerType vt = (ViewerType) viewerTypeComboBox.getSelectedItem();
        charsetComboBox.setEnabled(vt != ViewerType.IMAGE && vt != ViewerType.HEXADECIMAL);
        try {
            reloadContent();
        } catch (IOException ex) {
            ExceptionDialog.showException(ex, this);
        }
    }

    private void reloadContent() throws IOException {
        Viewer viewer = viewerMap.get((ViewerType) viewerTypeComboBox.getSelectedItem());
        viewer.view(zipItem, (Charset) charsetComboBox.getSelectedItem());
        if (viewerScrollPane.getViewport().getView() != viewer) {
            viewerScrollPane.setViewportView((Component) viewer);
        }
    }
}
