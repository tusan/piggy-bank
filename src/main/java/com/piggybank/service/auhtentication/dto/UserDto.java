package com.piggybank.service.auhtentication.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;

@AutoValue
@JsonDeserialize(builder = UserDto.Builder.class)
public abstract class UserDto {

  public static Builder newBuilder() {
    return new AutoValue_UserDto.Builder();
  }

  @JsonProperty("id")
  @Nullable
  public abstract Long id();

  @JsonProperty("username")
  public abstract String username();

  @JsonProperty("password")
  public abstract String password();

  @JsonProperty("token")
  @Nullable
  public abstract String token();

  @AutoValue.Builder
  @JsonPOJOBuilder
  public abstract static class Builder {

    @JsonCreator
    private static Builder create() {
      return newBuilder();
    }

    @JsonProperty("id")
    @Nullable
    public abstract Builder setId(Long id);

    @JsonProperty("username")
    public abstract Builder setUsername(String username);

    @JsonProperty("password")
    public abstract Builder setPassword(String password);

    @JsonProperty("token")
    @Nullable
    public abstract Builder setToken(String token);

    public abstract UserDto build();
  }
}
