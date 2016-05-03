package io.cfp.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.cfp.auth.entity.User;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

	User findByEmail(String email);
    
}
