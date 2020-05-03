package com.piggybank.config;

import java.time.format.DateTimeFormatter;

public interface Environment {
  String INPUT_DATE_FORMAT = "yyyy-MM-dd";

  DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(INPUT_DATE_FORMAT);

  String JWT_KEY_ALIAS = "piggybank-local.com";
}
