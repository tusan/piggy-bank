package com.piggybank.service.users.repository;

import com.google.common.base.MoreObjects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@SuppressWarnings("ClassWithTooManyDependents")
@Entity
@Table(name = "users")
public final class PiggyBankUser {
  @Id private String username;
  private String password;
  @Column(length = 1000)
  private String token;

  @Deprecated
  private PiggyBankUser() {}

  public static PiggyBankUser forUsernameAndPassword(final String username, final String password) {
    return forUsernamePasswordAndToken(username, password, null);
  }

  public static PiggyBankUser forUsernamePasswordAndToken(
      final String username, final String password, final String token) {
    final PiggyBankUser user = new PiggyBankUser();
    user.setUsername(username);
    user.setPassword(password);
    user.setToken(token);

    return user;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public String getToken() {
    return token;
  }

  public void setToken(final String token) {
    this.token = token;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("username", username)
        .add("password", password)
        .add("token", token)
        .toString();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final PiggyBankUser piggyBankUser = (PiggyBankUser) o;
    return Objects.equals(username, piggyBankUser.username)
        && Objects.equals(password, piggyBankUser.password)
        && Objects.equals(token, piggyBankUser.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, password, token);
  }
}
