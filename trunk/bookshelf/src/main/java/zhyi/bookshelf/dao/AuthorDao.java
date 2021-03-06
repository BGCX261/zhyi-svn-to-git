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
package zhyi.bookshelf.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import zhyi.bookshelf.entity.Author;
import zhyi.zee.jpa.BasicEntityDao;

/**
 * Provides DB operations related to {@link Author} entities.
 * @author Zhao Yi
 */
@Stateless
public class AuthorDao extends BasicEntityDao<Author> {
    @PersistenceContext
    private EntityManager em;

    public AuthorDao() {
        super(Author.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
