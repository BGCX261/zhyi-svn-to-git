/*
 * SrtTableModel.java
 *
 * Copyright (C) 2010 Zhao Yi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.zhyi.sse.model;

import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Stores parsed SRT subtitles that can be displayed in a {@code JTable},
 * and provides methods to operate subtitles.
 */
public class SrtTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = {
        "Index", "Begin Time", "End Time", "Content"
    };
    private List<Subtitle> subtitles;

    public SrtTableModel(List<Subtitle> subtitles) {
        this.subtitles = subtitles;
    }

    public void addSubtitle(int index, Subtitle subtitle) {
        subtitles.add(index, subtitle);
        fireTableRowsInserted(index, index);
    }

    public void addSubtitles(int index, List<Subtitle> subtitles) {
        this.subtitles.addAll(index, subtitles);
        fireTableRowsInserted(index, index + subtitles.size() - 1);
    }

    public void updateSubtitle(int index, Subtitle subtitle) {
        subtitles.set(index, subtitle);
        fireTableRowsUpdated(index, index);
    }

    public void removeSubtitles(int fromIndex, int toIndex) {
        for (int i = toIndex; i >= fromIndex; i--) {
            subtitles.remove(i);
        }
        fireTableRowsDeleted(fromIndex, toIndex);
    }

    public Subtitle getSubtitle(int index) {
        return subtitles.get(index);
    }

    public List<Subtitle> getSubtitles() {
        return subtitles;
    }

    public void setSubtitles(List<Subtitle> subtitles) {
        this.subtitles = subtitles;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return subtitles.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Subtitle subtitle = subtitles.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return rowIndex + 1;
            case 1:
                return subtitle.getBeginTime();
            case 2:
                return subtitle.getEndTime();
            case 3:
            default:
                // This makes table cell render text in HTML.
                return "<html>" + subtitle.getContent() + "</html>";
        }
    }

}
