package com.piggybank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.mock.web.DelegatingServletInputStream;

import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;

public final class ServletStreamHelper {
  public static <T> ServletInputStream mockServletInputStream(
          final T mock, final ObjectMapper objectMapper) {
    try {
      return new DelegatingServletInputStream(
          new ByteArrayInputStream(objectMapper.writeValueAsBytes(mock)));
    } catch (final JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
