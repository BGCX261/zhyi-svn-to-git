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

import java.util.UUID;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

/**
 * An entity template using a random UUID as the primary key, which is generated
 * when this entity is about to be persisted.
 * <p>Note {@code equals} and {@code hashCode} methods are implemented by
 * {@link AbstractEntity} based on the primary key.</p>
 * @author Zhao Yi
 */
@MappedSuperclass
public class UuidEntity extends AbstractEntity {
    @Id
    private String id;

    @Override
    public String getId() {
        return id;
    }

    @PrePersist
    private void generateId() {
        id = UUID.randomUUID().toString();
    }
}
