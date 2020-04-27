package com.piggybank.config;

import java.security.Key;
import java.time.format.DateTimeFormatter;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.security.Keys.secretKeyFor;

public interface Environment {
  String INPUT_DATE_FORMAT = "yyyy-MM-dd";

  DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(INPUT_DATE_FORMAT);

  //TODO use KeyStore to load the SecretKey
  Key SECRET_KEY = secretKeyFor(HS256);
}
