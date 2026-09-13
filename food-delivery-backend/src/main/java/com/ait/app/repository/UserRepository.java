package com.ait.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ait.app.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	boolean existsByPhonenumber(String phonenumber);

	boolean existsByEmailAndIdNot(String email, int id);

	boolean existsByPhonenumberAndIdNot(String phonenumber, int id);

}
