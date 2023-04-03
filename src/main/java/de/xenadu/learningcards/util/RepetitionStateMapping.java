package de.xenadu.learningcards.util;

import java.time.LocalDateTime;

/**
 * Hardcoded mapping for repetition states.
 */
public final class RepetitionStateMapping {

    private RepetitionStateMapping() {
    }

    /**
     * Takes as arguments the repetition state and returns when to repeat the card.
     *
     * @param repState The repetition state of a card
     * @return LocalDateTime when to repeat the card
     */
    public static LocalDateTime repStateToLocalDateTime(int repState) {
        final LocalDateTime now = LocalDateTime.now();
        switch (repState) {
            case 0, 1 -> {
                return now;
            }
            case 2 -> {
                return now.minusHours(1);
            }
            case 3 -> {
                return now.minusHours(12);
            }
            case 4 -> {
                return now.minusDays(2);
            }
            case 5 -> {
                return now.minusDays(8);
            }
            case 6 -> {
                return now.minusMonths(1);
            }
            case 7 -> {
                return now.minusMonths(2);
            }
            case 8 -> {
                return now.minusMonths(6);
            }
            default -> {
                throw new IllegalStateException(
                    String.format("Dieser repState wurde nicht gefunden. RepState = %d", repState));
            }
        }
    }
}
