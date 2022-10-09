package de.xenadu.learningcards.service.extern.api;

import de.xenadu.learningcards.domain.UserInfo;

public interface UserService {

    UserInfo getUserByEmail(String email);

}
