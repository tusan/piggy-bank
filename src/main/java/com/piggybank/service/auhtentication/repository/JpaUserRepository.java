package com.piggybank.service.auhtentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<PiggyBankUser, String> {
  Optional<PiggyBankUser> findByUsername(String username);

  Optional<PiggyBankUser> findByToken(String token);
}
