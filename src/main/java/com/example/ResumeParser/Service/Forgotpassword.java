package com.example.ResumeParser.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.ResumeParser.entity.User;
import com.example.ResumeParser.repository.UserRepository;
@Service
public class Forgotpassword {
 @Autowired
 private UserRepository userRepository;
 public ResponseEntity<String> updatePassword(User userInput) {
        Optional<User> existingUserOpt = userRepository.findByEmail(userInput.getEmail());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            String newPassword = userInput.getPassword();
            

            if (newPassword != null ) {
                existingUser.setPassword(newPassword);
                userRepository.save(existingUser);

                return ResponseEntity.ok("✅ Password updated successfully for: " + userInput.getEmail());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("❌ Password and Confirm Password do not match.");
            }

        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("❌ Email not found in the system.");
        }
    }
}
