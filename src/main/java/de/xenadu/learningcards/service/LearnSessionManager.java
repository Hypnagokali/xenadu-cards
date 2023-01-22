package de.xenadu.learningcards.service;

import de.xenadu.learningcards.domain.*;
import de.xenadu.learningcards.persistence.entities.Card;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
@RequiredArgsConstructor
public class LearnSessionManager implements Serializable, LearnSessionEventCallback {

    private final CardService cardService;

    private final Map<String, LearnSession> learnSessionMap = new ConcurrentHashMap<>();
    private final CardDistributionStrategy cardDistributionStrategy;
    private final AnswerAuditor answerAuditor;

    @PostConstruct
    public void init() {
        System.out.println("ToDo: Start a thread that destroys old learn sessions");
    }


    public LearnSession startNewLearnSession(LearnSessionConfig learnSessionConfig) {
        Queue<Card> setOfCards = generateLearningSet(learnSessionConfig);

        // is it really a good idea to use the LearnSessionManager as a callback? (05.01.23, StS)
        final LearnSession learnSession = new LearnSession(learnSessionConfig, setOfCards, answerAuditor, this);

        learnSessionMap.put(learnSession.getLearnSessionId().getValue(), learnSession);

        return learnSession;
    }

    private Queue<Card> generateLearningSet(LearnSessionConfig learnSessionConfig) {
        final long cardSetId = learnSessionConfig.getCardSetId();
        final int numberOfNewCards = learnSessionConfig.getNumberOfNewCards();

        final List<Card> newCards = cardService.findNewCards(cardSetId, numberOfNewCards);
        if (newCards.size() < numberOfNewCards) {
            int diff = numberOfNewCards - newCards.size();
            learnSessionConfig.setNumberOfCardsForRepetition(learnSessionConfig.getNumberOfCardsForRepetition() + diff);
        }
        final Map<Integer, Queue<Card>> cardsToRepeat = cardDistributionStrategy.distribute(
                learnSessionConfig
        );

        Queue<Card> allCards = new LinkedList<>(newCards);

        cardsToRepeat.keySet().stream().sorted().forEach(repState ->
            allCards.addAll(cardsToRepeat.get(repState))
        );

        return allCards;
    }

    public Optional<LearnSession> getLearnSession(LearnSessionId learnSessionId) {
        return Optional.ofNullable(learnSessionMap.get(learnSessionId.getValue()));
    }


    @Override
    public void save(Card card) {
        cardService.saveCard(card);
    }

    @Override
    public void saveAll(Collection<Card> cards) {
        cardService.saveAll(cards);
    }

    @Override
    public void finish(LearnSession learnSession) {
        learnSessionMap.remove(learnSession.getLearnSessionId().getValue());
    }

    public Collection<LearnSession> getAllLearnSessions() {
        return learnSessionMap.values();
    }
}
