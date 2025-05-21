package com.example.ResumeParser.Service;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.ResumeParser.entity.User;
import com.example.ResumeParser.repository.UserRepository;

@Service
public class VerificationCode {
    @Autowired
    private UserRepository userRepository;
    public ResponseEntity<String>getCode(User usercode){
      Optional<User> existingUserOpt = userRepository.findByEmail(usercode.getEmail());
    if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            String savedCode = existingUser.getValidationCode(); // From DB
            String inputCode = usercode.getValidationCode();     // From frontend

            System.out.println("Input Code: " + inputCode);
            System.out.println("Saved Code: " + savedCode);

            // Step 2: Compare codes using .equals()
            if (savedCode != null && savedCode.equals(inputCode)) {
                return ResponseEntity.ok("✅ Verification successful for: " + usercode.getEmail());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("❌ Incorrect verification code.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("❌ Email not found in our records.");
        }
    }
}
