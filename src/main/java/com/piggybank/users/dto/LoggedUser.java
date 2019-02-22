package com.piggybank.users.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize
public abstract class LoggedUser {

    @JsonCreator
    public static LoggedUser forUsernameAndToken(@JsonProperty("username") final String username, @JsonProperty("token") String token) {
        return new AutoValue_LoggedUser(username, token);
    }

    @JsonProperty("username")
    public abstract String username();

    @JsonProperty("token")
    public abstract String token();
}
