package com.piggybank.service.auhtentication.repository;

import com.google.common.base.MoreObjects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "users")
public class PiggyBankUser {
  @Id private String username;
  private String password;
  private String token;

  public PiggyBankUser() {}

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PiggyBankUser piggyBankUser = (PiggyBankUser) o;
    return Objects.equals(username, piggyBankUser.username)
        && Objects.equals(password, piggyBankUser.password)
        && Objects.equals(token, piggyBankUser.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, password, token);
  }
}
