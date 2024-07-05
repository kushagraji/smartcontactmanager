package com.kushagra.project.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kushagra.project.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	User findByEmail(String email);

	User findByPassword(String password);
	

}
