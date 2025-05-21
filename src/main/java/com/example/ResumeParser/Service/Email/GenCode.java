package com.example.ResumeParser.Service.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ResumeParser.entity.User;
import com.example.ResumeParser.repository.UserRepository;

@Service
public class GenCode {
    @Autowired
    private UserRepository userRepository;
    public User gencode(User user){
        int code = (int) (Math.random() * 900000) + 100000;
        String codeStr = String.valueOf(code);
        user.setValidationCode(codeStr);
         return userRepository.save(user);
    }
     public String AutogenerateCode() {
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }
}
