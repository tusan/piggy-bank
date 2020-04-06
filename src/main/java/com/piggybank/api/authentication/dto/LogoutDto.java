package com.piggybank.api.authentication.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize
public abstract class LogoutDto {

  @JsonCreator
  public static LogoutDto forUsername(@JsonProperty("username") final String username) {
    return new AutoValue_LogoutDto(username);
  }

  @JsonProperty("username")
  public abstract String username();
}
