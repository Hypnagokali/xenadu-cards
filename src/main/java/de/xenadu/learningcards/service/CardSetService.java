package de.xenadu.learningcards.service;

import de.xenadu.learningcards.domain.CardSetInfos;
import de.xenadu.learningcards.domain.UserInfo;
import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.entities.CardSet;
import de.xenadu.learningcards.persistence.repositories.CardRepository;
import de.xenadu.learningcards.persistence.repositories.CardSetRepository;
import lombok.NoArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import java.util.*;

@ApplicationScoped
@Transactional
@NoArgsConstructor
public class CardSetService {

    private CardSetRepository cardSetRepository;
    private CardRepository cardRepository;

    @Inject
    public CardSetService(CardSetRepository cardSetRepository, CardRepository cardRepository) {
        this.cardSetRepository = cardSetRepository;
        this.cardRepository = cardRepository;
    }


    @Transactional
    public CardSetInfos getCardSetInfos(long cardSetId) {
        int numberOfNewCards = (int) cardRepository.find("cardSet.id = ?1 AND repetitionState = 0", cardSetId).count();
        int numberOfCardsForRep = (int) cardRepository.find("cardSet.id = ?1 AND repetitionState > 0", cardSetId).count();

        return new CardSetInfos(numberOfNewCards, numberOfCardsForRep, 0, 0);
    }

    public CardSet save(CardSet cardSet, UserInfo userInfo) {
        if (cardSet.getUserId() == 0 || userInfo.getId() != cardSet.getUserId()) {
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
