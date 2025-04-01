package com.example.chat.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    //maybe change this to optional
    User findByUsername(String username);

	List<User> findByUsernameStartingWith(String prefix);
}
