package de.xenadu.learningcards.controller;

import de.xenadu.learningcards.domain.UserInfo;
import de.xenadu.learningcards.dto.CardDto;
import de.xenadu.learningcards.dto.LessonDto;
import de.xenadu.learningcards.exceptions.RestBadRequestException;
import de.xenadu.learningcards.exceptions.RestForbiddenException;
import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.entities.CardSet;
import de.xenadu.learningcards.persistence.entities.Lesson;
import de.xenadu.learningcards.persistence.mapper.CardMapper;
import de.xenadu.learningcards.persistence.mapper.LessonMapper;
import de.xenadu.learningcards.service.CardService;
import de.xenadu.learningcards.service.CardSetService;
import de.xenadu.learningcards.service.GetUserInfo;
import de.xenadu.learningcards.service.LessonService;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.resteasy.reactive.ResponseStatus;

@Path("/api/card-sets/{cardSetId}/lessons")
@RequiredArgsConstructor
public class LessonController {


    private final GetUserInfo getUserInfo;
    private final CardSetService cardSetService;

    private final CardService cardService;

    private final LessonService lessonService;

    private final LessonMapper lessonMapper;
    private final CardMapper cardMapper;

    /**
     * Find all for authenticated user and card set.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<LessonDto> findAll(@PathParam("cardSetId") long cardSetId) {
        assertCardSetBelongsToUser(cardSetId);

        List<Lesson> allByUserId = lessonService.findAllByCardSetId(cardSetId);

        return allByUserId.stream().map(l ->
            new LessonDto(l.getId(), l.getName(), l.getCardSet().getId(), l.getCards().size())
        ).toList();
    }

    /**
     * Get a specific lesson.
     *
     * @param cardSetId CardSet in which lesson is expected.
     * @param lessonId  The ID of the lesson.
     * @return Lesson Resource as DTO.
     */
    @GET
    @Path("/{lessonId}")
    @Produces(MediaType.APPLICATION_JSON)
    public LessonDto findLesson(
        @PathParam("cardSetId") long cardSetId,
        @PathParam("lessonId") long lessonId) {
        CardSet cardSet = assertCardSetBelongsToUser(cardSetId);

        Lesson lesson =
            assertLessonBelongsToCardSet(lessonId, cardSet);

        if (lesson.getCardSet().getId() != cardSetId) {
            throw new RestForbiddenException();
        }

        return new LessonDto(lesson, cardSetId);
    }

    /**
     * Retrieves all cards in a card set that belong to a specific lesson.
     *
     * @param cardSetId ID of current card set.
     * @param lessonId  ID of lesson.
     * @return CardDto list of cards.
     */
    @GET
    @Path("/{lessonId}/cards")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CardDto> allCardsByLesson(
        @PathParam("cardSetId") long cardSetId,
        @PathParam("lessonId") long lessonId) {
        CardSet cardSet = assertCardSetBelongsToUser(cardSetId);

        Lesson lesson = assertLessonBelongsToCardSet(lessonId, cardSet);

        if (lesson.getCardSet().getId() != cardSetId) {
            throw new RestForbiddenException();
        }
        List<Card> cards =
            cardService.findAllByCardSetIdAndLessonId(cardSetId, lessonId);

        return cards.stream().map(cardMapper::mapToDto).toList();
    }

    private Lesson assertLessonBelongsToCardSet(long lessonId, CardSet cardSet) {
        Lesson lesson = lessonService.findByIdOrThrow(lessonId);

        if (lesson.getCardSet().getId() != cardSet.getId()) {
            throw new RestForbiddenException();
        }
        return lesson;
    }

    @PUT
    @Path("/{lessonId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public LessonDto updateLesson(
        @PathParam("cardSetId") long cardSetId,
        @PathParam("lessonId") long lessonId,
        @RequestBody LessonDto lessonDto) {
        assertCardSetBelongsToUser(cardSetId);

        if (lessonDto.id() == 0) {
            throw new RestBadRequestException("Expecting a lesson with an id > 0");
        }

        Lesson lesson = lessonMapper.mapToLesson(lessonDto);

        if (lesson.getCardSet().getId() != cardSetId) {
            throw new RestForbiddenException();
        }

        lessonService.save(lesson);

        return new LessonDto(lesson, cardSetId);
    }


    /**
     * Create a new learn session for given card set.
     *
     * @param cardSetId ID of card set in which the lesson will be created in.
     * @param lessonDto The lesson send over http.
     * @return Saved lesson as DTO.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public LessonDto createNewLesson(
        @PathParam("cardSetId") long cardSetId,
        @RequestBody LessonDto lessonDto) {
        if (lessonDto.id() != 0) {
            throw new RestBadRequestException();
        }

        CardSet cardSet = assertCardSetBelongsToUser(cardSetId);

        Lesson lesson = lessonMapper.mapToLesson(lessonDto);
        lesson.setCardSet(cardSet);

        lessonService.save(lesson);
        return new LessonDto(lesson, cardSetId);

    }


    /**
     * Assigns an existing card to a lesson.
     *
     * @param cardSetId ID of card set in which the lesson can be found.
     * @param cardId    ID of Card that should be assigned to the lesson.
     * @param lessonId  ID of the lesson.
     */
    @POST
    @Path("/{lessonId}/assign/{cardId}")
    @ResponseStatus(204)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void assignCardToLesson(
        @PathParam("cardSetId") long cardSetId,
        @PathParam("lessonId") long lessonId,
        @PathParam("cardId") long cardId) {

        assertCardSetBelongsToUser(cardSetId);

        Card card = cardService.findById(cardId)
            .orElseThrow(RestBadRequestException::new);
        Lesson lesson = lessonService.findByIdOrThrow(lessonId);

        if (card.getCardSet().getId() != cardSetId
            || lesson.getCardSet().getId() != cardSetId) {
            throw new RestBadRequestException();
        }

        lessonService.assignCardToLesson(card, lesson);
    }

    /**
     * Removes a card from a lesson.
     *
     * @param cardSetId ID of card set in which the lesson can be found.
     * @param cardId    ID of card that should be removed from the lesson.
     * @param lessonId  ID of the lesson.
     */
    @POST
    @Path("/{lessonId}/remove/{cardId}")
    @ResponseStatus(204)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void removeCardFromLesson(
        @PathParam("cardSetId") long cardSetId,
        @PathParam("lessonId") long lessonId,
        @PathParam("cardId") long cardId) {

        assertCardSetBelongsToUser(cardSetId);

        Card card = cardService.findById(cardId)
            .orElseThrow(RestBadRequestException::new);
        Lesson lesson = lessonService.findByIdOrThrow(lessonId);

        if (card.getCardSet().getId() != cardSetId
            || lesson.getCardSet().getId() != cardSetId) {
            throw new RestBadRequestException();
        }

        lessonService.removeCardFromLesson(card, lesson);
    }


    /**
     * @param cardSetId ID of CardSet
     * @param lessonId  ID of Lesson to be deleted
     */
    @DELETE
    @Path("/{lessonId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteLesson(
        @PathParam("cardSetId") long cardSetId,
        @PathParam("lessonId") long lessonId) {
        assertCardSetBelongsToUser(cardSetId);

        Lesson lesson = lessonService.findByIdOrThrow(lessonId);

        if (lesson.getCardSet().getId() != cardSetId) {
            throw new RestForbiddenException();
        }

        lessonService.deleteById(lessonId);
    }

    private CardSet assertCardSetBelongsToUser(long cardSetId) {
        UserInfo userInfo = getUserInfo.authenticatedUser();
        CardSet cardSet = cardSetService.findById(cardSetId)
            .orElseThrow(RestBadRequestException::new);

        if (cardSet.getUserId() != userInfo.getId()) {
            throw new RestForbiddenException();
        }

        return cardSet;
    }

}
