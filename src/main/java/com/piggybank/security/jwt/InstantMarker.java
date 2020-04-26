package com.piggybank.security.jwt;

import java.time.Instant;

public interface InstantMarker {
  Instant getCurrent();
}
