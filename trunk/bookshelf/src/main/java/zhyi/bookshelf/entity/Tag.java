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
package zhyi.bookshelf.entity;

import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import zhyi.zee.jpa.VersionedAbstractEntity;

/**
 * Maps {@code tag} table. A tag is used to categorize books, while a book can
 * have multiple tags. The tag's label is used as primary key.
 * @author Zhao Yi
 */
@Entity
public class Tag extends VersionedAbstractEntity {
    @Id
    @NotNull
    @Size(min = 1, max = 16)
    private String id;
    @Basic(fetch = FetchType.LAZY)
    @Size(max = 1024)
    private String info;
    @ManyToMany(mappedBy = "tags")
    private List<Book> books;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<Book> getBooks() {
        return books;
    }
}
