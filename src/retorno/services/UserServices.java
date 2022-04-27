package com.algamoney.services;

import java.util.List;

import com.algamoney.model.Roles;
import com.algamoney.model.User;

public interface UserServices {

    User saveUser(User user);
    Roles saveRoles(Roles roles);
    void addRolesToUser(String username,String rolename);
    User getUser(String username);
    List<User> getusers();


}
