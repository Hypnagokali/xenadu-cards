package de.xenadu.learningcards.persistence.entities;

import de.xenadu.learningcards.persistence.mapper.GenericEntityFactory;

/**
 * Properties that all entities share.
 * Useful for MapStruct ObjectFactory: {@link GenericEntityFactory};
 */
public interface AbstractEntity {

    long getId();

}
