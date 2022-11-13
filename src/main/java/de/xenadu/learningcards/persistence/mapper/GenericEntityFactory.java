package de.xenadu.learningcards.persistence.mapper;

import de.xenadu.learningcards.dto.AbstractDto;
import de.xenadu.learningcards.persistence.entities.AbstractEntity;
import org.mapstruct.ObjectFactory;
import org.mapstruct.TargetType;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.InvocationTargetException;

@ApplicationScoped
public class GenericEntityFactory {

    @PersistenceContext
    EntityManager em;

    @PostConstruct
    public void init() {
        System.out.println("Init EntityFactory");
    }


    public void setEm(EntityManager em) {
        this.em = em;
    }

    @ObjectFactory
    public <T extends AbstractEntity> T resolve(AbstractDto sourceDto, @TargetType Class<T> type) {
        T entity = em.find(type, sourceDto.getId());
        try {
            return entity != null ? entity : type.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException(String.format("Es konnte kein Objekt des Entitytyps '%s' erstellt werden.", type.getName()), e);
        }
    }

}
