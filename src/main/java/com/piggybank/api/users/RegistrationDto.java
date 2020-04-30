package com.piggybank.api.users;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize
public abstract class RegistrationDto {

  @JsonCreator
  public static RegistrationDto forUsernameAndPassword(
      @JsonProperty("username") final String username,
      @JsonProperty("password") final String password) {
    return new AutoValue_RegistrationDto(username, password);
  }

  @JsonProperty("username")
  public abstract String username();

  @JsonProperty("password")
  public abstract String password();
}
