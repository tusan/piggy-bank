package com.piggybank.security.jwt;

import java.time.Instant;

interface InstantMarker {
  Instant getCurrent();
}
