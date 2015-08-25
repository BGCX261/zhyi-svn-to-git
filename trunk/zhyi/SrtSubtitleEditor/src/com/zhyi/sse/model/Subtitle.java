/*
 * Subtitle.java
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

import com.zhyi.sse.common.SubtitleFormatException;
import com.zhyi.sse.common.SrtToolkit;

/**
 * Models a subtitle which contains begin time, end time and content.
 */
public class Subtitle {

    private String beginTime;
    private String endTime;
    private String content;

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) throws SubtitleFormatException {
        if (!SrtToolkit.isLegalTime(beginTime)) {
            throw new SubtitleFormatException(
                    "Begin time doesn't match \"hh:MM:ss,mmm\".");
        }
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) throws SubtitleFormatException {
        if (!SrtToolkit.isLegalTime(endTime)) {
            throw new SubtitleFormatException(
                    "End time doesn't match \"hh:MM:ss,mmm\".");
        }
        this.endTime = endTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
