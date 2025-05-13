package com.example.ResumeParser.repository;

import com.example.ResumeParser.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;  // Add this import


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
Optional<User> findById(Long id);  
}
