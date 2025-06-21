package com.jobportal.repostory;

import com.jobportal.entity.Recruiter;
import com.jobportal.entity.User;
import com.jobportal.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE u.email = ?1")
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.verifyToken = ?1")
    Optional<User> findByVerifyToken(String verifyToken);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email = ?1 AND u.role = ?2")
    Optional<User> findByEmailAndRole(String email, Role role);


}
