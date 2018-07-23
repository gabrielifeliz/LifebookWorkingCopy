package com.lifebook.Service;

import com.lifebook.Model.AppRole;
import com.lifebook.Model.AppUser;
import com.lifebook.Model.AppUserDetails;
import com.lifebook.Repositories.AppRoleRepository;
import com.lifebook.Repositories.AppUserDetailsRepository;
import com.lifebook.Repositories.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    AppUserDetailsRepository details;

    @Autowired
    AppUserRepository users;

    @Autowired
    AppRoleRepository roles;

    @Autowired
    public UserService(AppUserRepository users) {
        this.users = users;
    }

    public AppUser findByUsername(String username) {
        return users.findByUsername(username);
    }

    public AppUser findByEmail(String email) {
        return users.findByEmail(email);
    }

    public AppUser findByConfirmationToken(String confirmationToken) {
        return users.findByConfirmationToken(confirmationToken);
    }

    public void saveUser(AppUser user) {

        AppRole userRole = new AppRole();
        userRole.setRole("USER");
        roles.save(userRole);

        user.getRoles().add(userRole);

        AppUserDetails detail = new AppUserDetails();
        detail.setProfilePic("/img/user.png");
        user.setDetail(detail);
        details.save(detail);
        users.save(user);
    }

    public void save(AppUser user) {
        users.save(user);
    }
}
