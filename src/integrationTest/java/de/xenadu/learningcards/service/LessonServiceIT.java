package de.xenadu.learningcards.service;

import de.xenadu.learningcards.persistence.entities.Lesson;
import io.quarkus.test.junit.QuarkusTest;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class LessonServiceIT {

    @Inject
    LessonService lessonService;

    @Test
    void createALessonTest() {
        Lesson lesson = new Lesson("Lesson 1");

    }
}
