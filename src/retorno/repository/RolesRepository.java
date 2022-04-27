package com.algamoney.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.algamoney.model.Roles;

public interface RolesRepository extends JpaRepository<Roles, Long>{
    Roles findByName(String name);

}
