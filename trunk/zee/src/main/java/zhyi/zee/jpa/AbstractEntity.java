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
package zhyi.zee.jpa;

import java.io.Serializable;
import java.util.Objects;

/**
 * This is a convenient template for an entity class whose {@link #equals(Object)}
 * and {@link #hashCode()} methods are implemented based on the primary key.
 * Subclasses only needs to implement {@link #getId()} method.
 * @author Zhao Yi
 */
public abstract class AbstractEntity implements Serializable {
    /**
     * Returns the primary key.
     */
    public abstract Object getId();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return getId() == null ? false
                : getId().equals(((AbstractEntity) obj).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
