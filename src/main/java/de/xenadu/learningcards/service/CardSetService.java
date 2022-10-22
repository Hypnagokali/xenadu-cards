package de.xenadu.learningcards.service;

import de.xenadu.learningcards.domain.UserInfo;
import de.xenadu.learningcards.persistence.entities.CardSet;
import de.xenadu.learningcards.persistence.repositories.CardSetRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import java.util.*;

@ApplicationScoped
@Transactional
@NoArgsConstructor
public class CardSetService {

    private CardSetRepository cardSetRepository;

    @Inject
    public CardSetService(CardSetRepository cardSetRepository) {
        this.cardSetRepository = cardSetRepository;
    }

    public CardSet save(CardSet cardSet, UserInfo userInfo) {
        if (cardSet.getUserId() > 0 && userInfo.getId() != cardSet.getUserId()) {
            throw new ForbiddenException();
        }

        if (cardSet.getId() > 0) {
            cardSetRepository.getEntityManager().merge(cardSet);
        } else {
            cardSet.setUser(userInfo);
            cardSetRepository.persist(cardSet);
        }
        return cardSet;
    }

    public Set<CardSet> findAllByUserId(long userId) {
//        final List<CardSet> cardSetList = cardSetRepository.list("userId", userId);
        final List<CardSet> cardSetList = cardSetRepository
                .find("SELECT cs FROM CardSet cs LEFT JOIN FETCH cs.cards WHERE cs.userId=:userId",
                        new HashMap<>(){{ put("userId", userId); }})
                .list();

        return new LinkedHashSet<>(cardSetList);
    }

    public Optional<CardSet> findById(long cardId) {
        return Optional.ofNullable(cardSetRepository.findById(cardId));
    }

    public void deleteById(long cardSetId, UserInfo userInfo) {
        final CardSet found = cardSetRepository.findById(cardSetId);
        if (found == null) throw new NotFoundException();
        if (found.getUserId() != userInfo.getId()) throw new ForbiddenException();

        cardSetRepository.deleteById(cardSetId);
    }
}
