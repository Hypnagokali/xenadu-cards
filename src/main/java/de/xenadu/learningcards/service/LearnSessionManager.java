package de.xenadu.learningcards.service;

import de.xenadu.learningcards.domain.LearnSession;
import de.xenadu.learningcards.domain.LearnSessionConfig;
import de.xenadu.learningcards.domain.LearnSessionId;
import de.xenadu.learningcards.persistence.entities.Card;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

@SessionScoped
@RequiredArgsConstructor
public class LearnSessionManager implements Serializable {

    private final CardService cardService;
    private final Map<String, LearnSession> learnSessionMap = new ConcurrentHashMap<>();
    private final CardDistributor cardDistributor;

    public LearnSession startNewLearnSession(LearnSessionConfig learnSessionConfig) {
        Queue<Card> setOfCards = generateLearningSet(learnSessionConfig);
        final LearnSession learnSession = new LearnSession(learnSessionConfig, setOfCards);

        init(learnSession);

        learnSessionMap.put(learnSession.getLearnSessionId().getValue(), learnSession);

        return learnSession;
    }

    private Queue<Card> generateLearningSet(LearnSessionConfig learnSessionConfig) {
        final long cardSetId = learnSessionConfig.getCardSetId();
        final int numberOfNewCards = learnSessionConfig.getNumberOfNewCards();

        final List<Card> newCards = cardService.findNewCards(cardSetId, numberOfNewCards);
        final Map<Integer, Queue<Card>> cardsToRepeat = cardDistributor.distribute(
                learnSessionConfig
        );


        return null;
    }

    public Optional<LearnSession> getLearnSession(LearnSessionId learnSessionId) {
        return Optional.ofNullable(learnSessionMap.get(learnSessionId.getValue()));
    }

    private void init(LearnSession learnSession) {
        List<Card> newCards = cardService.findNewCards(1, learnSession.getConfig().getNumberOfNewCards());

    }
}
