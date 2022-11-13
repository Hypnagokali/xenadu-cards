package de.xenadu.learningcards.controller;

import de.xenadu.learningcards.domain.UserInfo;
import de.xenadu.learningcards.dto.CardSetDto;
import de.xenadu.learningcards.exceptions.RestBadRequestException;
import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.mapper.CardSetMapper;
import de.xenadu.learningcards.exceptions.RestForbiddenException;
import de.xenadu.learningcards.persistence.entities.CardSet;
import de.xenadu.learningcards.persistence.mapper.GenericEntityFactory;
import de.xenadu.learningcards.service.CardSetService;
import de.xenadu.learningcards.service.GetUserInfo;
import io.quarkus.security.Authenticated;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Path("/api/card-sets")
@Authenticated
@RequiredArgsConstructor
public class CardSetController {

    private final CardSetService cardSetService;
    private final GetUserInfo getUserInfo;
    private final GenericEntityFactory genericEntityFactory;
    private final CardSetMapper cardSetMapper;

    @POST
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public CardSetDto createCardSet(CardSetDto cardSetDto) {
        final CardSet cardSet = cardSetMapper.mapToEntity(cardSetDto);

        final UserInfo userInfo = getUserInfo.authenticatedUser();

        if (cardSetDto.getUserId() != userInfo.getId()) {
            throw new RestForbiddenException();
        }

        return cardSetMapper.mapToDto(cardSetService.save(cardSet, userInfo));
    }

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CardSetDto> getAllCardSets() {
        final UserInfo userInfo = getUserInfo.authenticatedUser();

        return cardSetService.findAllByUserId(userInfo.getId()).stream()
                .sorted(Comparator.comparing(CardSet::getId))
                .map(cardSetMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{cardSetId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public CardSetDto getCardSetById(@PathParam("cardSetId") long cardSetId) {
        final UserInfo userInfo = getUserInfo.authenticatedUser();

        return cardSetMapper.mapToDto(cardSetService.findById(cardSetId).orElseThrow(NotFoundException::new));
    }


    @PUT
    @Path("/{cardSetId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public CardSetDto updateCardSet(CardSetDto cardSetDto, @PathParam("cardSetId") long cardSetId) {
        final CardSet cardSet = cardSetMapper.mapToEntity(cardSetDto);
        if (cardSet.getId() == 0) {
            throw new RestBadRequestException(String.format("There is no CardSet with ID = %d", cardSetDto.getId()));
        }
        if (cardSetId != cardSet.getId()) {
            throw new RestBadRequestException("ID is not equal");
        }

        return cardSetMapper.mapToDto(
                cardSetService.save(cardSet, getUserInfo.authenticatedUser()));
    }

    @DELETE
    @Path("/{cardSetId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteCardSet(@PathParam("cardSetId") long cardSetId) {
        final UserInfo userInfo = getUserInfo.authenticatedUser();

        cardSetService.deleteById(cardSetId, userInfo);
    }

}
