package com.example.ResumeParser.Controller;

import com.example.ResumeParser.entity.User;
import com.example.ResumeParser.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // @GetMapping("/{id}")
    // public User getUserById(@PathVariable int id) {
    //     return userRepository.findById(id).orElse(null);
    // }

    // @DeleteMapping("/{id}")
    // public void deleteUser(@PathVariable int id) {
    //     userRepository.deleteById(id);
    // }
}
