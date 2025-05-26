package com.example.ResumeParser.Controller;

import org.springframework.web.multipart.MultipartFile;
import com.example.ResumeParser.Service.Resumeservice;
import com.example.ResumeParser.entity.Resume;
import com.example.ResumeParser.entity.User;
import com.example.ResumeParser.dto.ResumeWithSkillsDTO;
import com.example.ResumeParser.dto.Resumefilterrequest;
import com.example.ResumeParser.dto.SkillFilterRequest;
import com.example.ResumeParser.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

//for uploading resumes of a specific user 
    @PostMapping("/resumes/upload/{userId}")
    public ResponseEntity<String> uploadResume(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        System.out.println("Upload called with userId: " + userId);
        
        // ✅ Ensure the user exists before proceeding
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));

        // ✅ Let the service handle associating resume with this user
        try {
            resumeservice.uploadResumeForUser(user.getId(), file);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return ResponseEntity.ok("Resume uploaded and saved successfully for user with ID " + userId);
    }



// New endpoint to get resumes by userId
    @GetMapping("/resumesByUserId")
    public ResponseEntity<List<ResumeWithSkillsDTO>> getResumesByUserId(@RequestParam("userId") Long userId) {
        List<ResumeWithSkillsDTO> resumes = resumeservice.getResumesByUserId(userId);
        return ResponseEntity.ok(resumes);
    }

// for filtering resumes of a specific user
    @PostMapping("/filter")
        public List<ResumeWithSkillsDTO> filterResumes(@RequestBody Resumefilterrequest request) {
            return resumeservice.filterResumes(request.getSkills(), request.getMinYearsOfExperience(), request.getUserId());
    }
    
// Function to show the image of the resume
    @GetMapping("/resumes/image/{resumeId}")
    public ResponseEntity<Resource> getResumeImage(@PathVariable Long resumeId) {
        Resume resume = resumeservice.getResumeById(resumeId);
        if (resume == null || resume.getResumeImage() == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] imageData = resume.getResumeImage();
        ByteArrayResource resource = new ByteArrayResource(imageData);

        return ResponseEntity.ok()
                .contentLength(imageData.length)
                .contentType(MediaType.IMAGE_PNG) // Use actual image format if needed
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"resume_" + resumeId + ".png\"")
                .body(resource);
    }


// deleting the stored resumed details of a specific user
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUserResumes(@PathVariable Long userId) {
        resumeservice.deleteResumesByUserId(userId);
        return ResponseEntity.ok("User's resumes deleted successfully");
    }
    

}
