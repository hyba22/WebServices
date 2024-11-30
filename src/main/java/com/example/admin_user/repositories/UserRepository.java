package com.example.admin_user.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.admin_user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
	User findByEmail (String email);
}
