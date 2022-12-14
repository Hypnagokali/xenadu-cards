package de.xenadu.learningcards.domain;

import lombok.Getter;

import java.util.Objects;

@Getter
public class LearnSessionId {

    private final String value;

    public LearnSessionId(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LearnSessionId that = (LearnSessionId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
