package de.xenadu.learningcards.controller;

import de.xenadu.learningcards.domain.UserInfo;
import de.xenadu.learningcards.dto.LessonDto;
import de.xenadu.learningcards.persistence.entities.Lesson;
import de.xenadu.learningcards.service.CardSetService;
import de.xenadu.learningcards.service.GetUserInfo;
import de.xenadu.learningcards.service.LessonService;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;

@Path("/api/card-sets/{cardSetId}/lessons")
@RequiredArgsConstructor
public class LessonController {


    private final GetUserInfo getUserInfo;
    private final CardSetService cardSetService;
    private final LessonService lessonService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<LessonDto> findAll(@PathParam("cardSetId") long cardSetId) {
        UserInfo userInfo = getUserInfo.authenticatedUser();

        List<Lesson> allByUserId = lessonService.findAllByCardSetId(cardSetId);

        return allByUserId.stream().map(l ->
            new LessonDto(l.getId(), l.getName(), l.getCardSet().getId(), l.getCards().size())
        ).toList();
    }

}
