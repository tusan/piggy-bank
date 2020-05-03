package com.piggybank.service.users;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface JpaUserRepository extends JpaRepository<PiggyBankUser, String> {
  Optional<PiggyBankUser> findByUsername(String username);
}
