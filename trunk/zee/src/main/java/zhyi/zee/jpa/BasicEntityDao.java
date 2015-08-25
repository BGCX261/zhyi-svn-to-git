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

import java.util.List;
import java.util.Objects;
import javax.persistence.EntityManager;

/**
 * Provides implementation of basic CRUD operations for entities. Subclasses
 * need to implement {@link #getEntityManager()} method, and are typically
 * session bean classes.
 * @param T Type of the entity.
 * @author Zhao Yi
 */
public abstract class BasicEntityDao<T> {
    private Class<T> entityClass;
    private String entityClassName;
    private String findAllQuery;
    private String countQuery;

    protected BasicEntityDao(Class<T> entityClass) {
        this.entityClass = Objects.requireNonNull(entityClass);
        entityClassName = entityClass.getSimpleName();
        findAllQuery = "select e from " + entityClassName + " e";
        countQuery = "select count(e) from " + entityClassName + " e";
    }

    /**
     * Returns the {@link EntityManager} instance for DB operation.
     */
    protected abstract EntityManager getEntityManager();

    /**
     * Persists the specified entity.
     */
    public void persist(T entity) {
        getEntityManager().persist(entity);
    }

    /**
     * Finds an entity by primary key. Returns {@code null} if the entity
     * doesn't exist.
     */
    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    /**
     * Finds all entities.
     */
    public List<T> findAll() {
        return getEntityManager().createQuery(findAllQuery, entityClass).getResultList();
    }

    /**
     * Finds all entities within the specified range.
     * @param first The position of the first entity to retrieve.
     * @param max The maximum number of entities to retrieve.
     */
    public List<T> findRange(int first, int max) {
        return getEntityManager().createQuery(findAllQuery, entityClass)
                .setFirstResult(first).setMaxResults(max).getResultList();
    }

    /**
     * Returns the total number of entities.
     */
    public long count() {
        return (Long) getEntityManager().createQuery(countQuery).getSingleResult();
    }

    /**
     * Merges the specified entity.
     * @return The entity whose state was merged to.
     */
    public T merge(T entity) {
        return getEntityManager().merge(entity);
    }

    /**
     * Removes the specified entity.
     */
    public void remove(T entity) {
        getEntityManager().remove(merge(entity));
    }
}
