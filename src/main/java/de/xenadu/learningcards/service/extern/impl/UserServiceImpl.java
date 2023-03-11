package de.xenadu.learningcards.service.extern.impl;

import de.xenadu.learningcards.domain.UserInfo;
import de.xenadu.learningcards.service.extern.ServiceInfo;
import de.xenadu.learningcards.service.extern.ServiceLookup;
import de.xenadu.learningcards.service.extern.api.UserService;
import io.quarkus.security.UnauthorizedException;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;
import io.vertx.mutiny.ext.web.codec.BodyCodec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ServerErrorException;
import java.time.Duration;

//@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final ServiceLookup serviceLookup;
    private final Vertx vertx;

    @Override
    public UserInfo getUserByEmail(String email) {
        return null;
    }

//    @Override
//    public UserInfo getUserByEmail(String email) {
//        final ServiceInfo serviceInfo = serviceLookup.getInfoOf("user_service");
//
//        WebClientOptions options = new WebClientOptions();
//        options.setLogActivity(true);
//
//        final HttpResponse<UserInfo> userInfoHttpResponse = WebClient.create(vertx, options)
//                .get(String.format("/users/%s",
//                        email))
//                .as(BodyCodec.json(UserInfo.class))
//                .host(serviceInfo.getInstance().getIpAddr())
//                .port(serviceInfo.getInstance().getPort())
//                .send()
//                .onItem()
//                .call(res -> Uni.createFrom().item(res))
//                .await()
//                .atMost(Duration.ofSeconds(10));
//
//        if (userInfoHttpResponse == null) {
//            throw new ServerErrorException("No response in 10 seconds error", 500);
//        }
//
//        if (userInfoHttpResponse.statusCode() >= 300) {
//            throw new BadRequestException("Request getUserByEmail ist fehlgeschlagen: " + userInfoHttpResponse.statusMessage());
//        }
//
//        return userInfoHttpResponse.body();
//    }
}
