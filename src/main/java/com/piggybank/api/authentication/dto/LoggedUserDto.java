package com.piggybank.api.authentication.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize
public abstract class LoggedUserDto {

  @JsonCreator
  public static LoggedUserDto forUsernameAndToken(
      @JsonProperty("username") final String username, @JsonProperty("token") String token) {
    return new AutoValue_LoggedUserDto(username, token);
  }

  @JsonProperty("username")
  public abstract String username();

  @JsonProperty("token")
  public abstract String token();
}
