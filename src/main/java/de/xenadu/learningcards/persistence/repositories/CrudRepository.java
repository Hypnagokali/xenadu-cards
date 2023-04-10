package de.xenadu.learningcards.persistence.repositories;

import de.xenadu.learningcards.persistence.entities.AbstractEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

/**
 * General CRUD Repo.
 *
 * @param <T> must be a managed entity.
 */
public class CrudRepository<T extends AbstractEntity> implements PanacheRepository<T> {


    /**
     * Creates or updates an entity.
     *
     * @param entity Given entity.
     */
    public void save(T entity) {
        if (entity.getId() == 0) {
            persist(entity);
        } else {
            getEntityManager().merge(entity);
        }
    }
}
