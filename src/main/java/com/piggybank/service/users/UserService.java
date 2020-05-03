package com.piggybank.service.users;

import java.util.Optional;

public interface UserService {
    void addOrReplace(PiggyBankUser addedUser);
    Optional<PiggyBankUser> findByUsername(final String username);
}
