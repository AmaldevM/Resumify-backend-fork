package com.example.ResumeParser.Controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.example.ResumeParser.Service.Resumeservice;
import com.example.ResumeParser.entity.Resume;
import com.example.ResumeParser.entity.User;
import com.example.ResumeParser.dto.ResumeWithSkillsDTO;
import com.example.ResumeParser.dto.SkillFilterRequest;
import com.example.ResumeParser.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class Resumecontroller {

    @Autowired
    private Resumeservice resumeservice;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/hii")
    public String hello() {
        return "hello";
    }

    @PostMapping("/resumes/upload/{userId}")
    public ResponseEntity<String> uploadResume(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        System.out.println("Upload called with userId: " + userId);
        
        // ✅ Ensure the user exists before proceeding
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));

        // ✅ Let the service handle associating resume with this user
        resumeservice.uploadResumeForUser(user.getId(), file);

        return ResponseEntity.ok("Resume uploaded and saved successfully for user with ID " + userId);
    }
    // old method to save resume
    // @PostMapping("/saveresume")
    // public ResponseEntity<String> uploadAndSaveResume(@RequestParam("file") MultipartFile file) {
    //     resumeservice.saveresume(file);
    //     return ResponseEntity.ok("Resume parsed and saved successfully.");
    // }


        //old method to get all resumes in db
    // @GetMapping("/resumes")
    // public ResponseEntity<List<Resume>> getAllResumes() {
    //     List<Resume> resumes = resumeservice.getAllResumes();
    //     return ResponseEntity.ok(resumes);
    // }

    @PostMapping("/filter")
    public List<ResumeWithSkillsDTO> filterResumes(@RequestBody SkillFilterRequest request) {
        return resumeservice.filterResumes(request.getSkills(), (int) request.getMinExp());
    }

    // New endpoint to get resumes by userId
    @GetMapping("/resumesByUserId")
    public ResponseEntity<List<ResumeWithSkillsDTO>> getResumesByUserId(@RequestParam("userId") Long userId) {
        List<ResumeWithSkillsDTO> resumes = resumeservice.getResumesByUserId(userId);
        return ResponseEntity.ok(resumes);
    }
}
