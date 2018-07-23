package com.lifebook.Repositories;

import com.lifebook.Model.AppUser;
import org.springframework.data.repository.CrudRepository;

public interface AppUserRepository extends CrudRepository<AppUser,Long>{
    AppUser findByUsername(String username);
    AppUser findByEmail(String email);
    AppUser findByConfirmationToken(String confirmationToken);
}
