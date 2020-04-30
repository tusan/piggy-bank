package com.piggybank.security.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize
public abstract class LoginRequestDto {

  @JsonCreator
  public static LoginRequestDto forUsernameAndPassword(
      @JsonProperty("username") final String username,
      @JsonProperty("password") final String password) {
    return new AutoValue_LoginRequestDto(username, password);
  }

  @JsonProperty("username")
  public abstract String username();

  @JsonProperty("password")
  public abstract String password();
}
