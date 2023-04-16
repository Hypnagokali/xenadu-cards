package de.xenadu.learningcards.controller;

import de.xenadu.learningcards.domain.UserInfo;
import de.xenadu.learningcards.dto.LessonDto;
import de.xenadu.learningcards.exceptions.RestBadRequestException;
import de.xenadu.learningcards.exceptions.RestForbiddenException;
import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.entities.CardSet;
import de.xenadu.learningcards.persistence.entities.Lesson;
import de.xenadu.learningcards.persistence.mapper.LessonMapper;
import de.xenadu.learningcards.service.CardService;
import de.xenadu.learningcards.service.CardSetService;
import de.xenadu.learningcards.service.GetUserInfo;
import de.xenadu.learningcards.service.LessonService;
import java.util.List;
import javax.ws.rs.Consumes;
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
        assertCardSetBelongsToUser(cardSetId);

        Lesson lesson = lessonService.findByIdOrThrow(lessonId);
        if (lesson.getCardSet().getId() != cardSetId) {
            throw new RestForbiddenException();
        }

        return new LessonDto(lesson, cardSetId);
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

        lessonService.save(lesson);

        if (lesson.getCardSet().getId() != cardSetId) {
            throw new RestForbiddenException();
        }

        return new LessonDto(lesson, cardSetId);
    }


    /**
     * Create a new learn session for given card set.
     *
     * @param cardSetId ID of card set in which the lesson is to be created.
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
     * Assign an existing card to a lesson.
     *
     * @param cardSetId ID of card set in which the lesson is to be created.
     * @return Saved lesson as DTO.
     */
    @POST
    @Path("/{lessonId}/assign/{cardId}")
    @ResponseStatus(204)
    @Consumes(MediaType.APPLICATION_JSON)
    public void assignCardToLesson(
        @PathParam("cardSetId") long cardSetId,
        @PathParam("lessonId") long lessonId,
        @PathParam("cardId") long cardId) {

        assertCardSetBelongsToUser(cardSetId);

        Card card = cardService.findById(cardId)
            .orElseThrow(RestBadRequestException::new);
        Lesson lesson = lessonService.findByIdWithCards(lessonId);

        if (card.getCardSet().getId() != cardSetId
            || lesson.getCardSet().getId() != cardSetId) {
            throw new RestBadRequestException();
        }

        lesson.addCard(card);

        lessonService.save(lesson);
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
