/*
 * WordOrder.java
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
package com.zhyi.wordz.common;

/**
 * Supported orders for displaying words.
 */
public enum WordOrder {

    /**
     * Indicates words should be listed in alphabetical order.
     */
    ALPHABETICAL_ORDER("Alphabetical Order"),
    /**
     * Indicates words should be listed by last modified tiem.
     */
    CREATED_TIME("Created Time");

    private String displayName;

    private WordOrder(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

}
