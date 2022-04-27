package com.algamoney.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.algamoney.model.User;


public interface UserRepository extends JpaRepository<User,Long>{

    User findByUsername(String username);


}