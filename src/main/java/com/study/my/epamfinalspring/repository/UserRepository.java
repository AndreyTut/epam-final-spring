package com.study.my.epamfinalspring.repository;

import com.study.my.epamfinalspring.model.Role;
import com.study.my.epamfinalspring.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findById(int id);

    Optional<User> findByEmail(String email);

    Page<User> findByRoles(Role role, Pageable pageable);
}
