package de.xenadu.learningcards.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfo {

    private long id;
    private String email;
    private String firstName;
    private String lastName;

}
