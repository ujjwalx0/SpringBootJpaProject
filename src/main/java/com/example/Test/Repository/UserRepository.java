package com.example.Test.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Test.Entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  
    User findByUsername(String username);
    User findByEmail(String email);
 
  User findByUsernameOrEmail(String username, String email);
  
}
