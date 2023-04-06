package de.xenadu.learningcards.persistence.repositories;

import de.xenadu.learningcards.persistence.entities.AlternativeAnswer;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import java.util.HashMap;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AlternativeAnswerRepository implements PanacheRepository<AlternativeAnswer> {


    public List<AlternativeAnswer> findAllByCardId(long cardId) {
        return find("""
            SELECT a FROM AlternativeAnswer a
            WHERE a.card.id = :cardId
            """, new HashMap<>() {{
                    put("cardId", cardId);
                }}
        ).list();
    }

}
