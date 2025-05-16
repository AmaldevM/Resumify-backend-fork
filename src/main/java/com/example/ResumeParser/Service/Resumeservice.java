package com.example.ResumeParser.Service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.ResumeParser.dto.ResumeWithSkillsDTO;
import com.example.ResumeParser.entity.Knownskill;
import com.example.ResumeParser.entity.Resume;
import com.example.ResumeParser.entity.Skill;
import com.example.ResumeParser.entity.User;
import com.example.ResumeParser.repository.Knownskillrepository;
import com.example.ResumeParser.repository.Resumerepository;
import com.example.ResumeParser.repository.Skillrepository;
import com.example.ResumeParser.repository.UserRepository;

import jakarta.transaction.Transactional;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class Resumeservice {

    @Autowired
    private Resumerepository resumeRepository;

    @Autowired
    private Skillrepository skillRepository;

    @Autowired
    private Knownskillrepository knownskillrepo;

    @Autowired
    private UserRepository userRepository;

    public List<ResumeWithSkillsDTO> filterResumes(List<String> skills, double minExp, Long userId) {
       System.out.println("hi from filter resumes");
        
        int skillCount = skills.size();
        List<Object[]> results = resumeRepository.filterBySkillsAndExperience(skills, minExp,skillCount, userId);

        List<ResumeWithSkillsDTO> finalList = new ArrayList<>();
        for (Object[] row : results) {
            ResumeWithSkillsDTO dto = new ResumeWithSkillsDTO(
                ((Number) row[0]).longValue(),
                (String) row[1],
                (String) row[2],
                (String) row[3],
                ((Number) row[4]).doubleValue(),
                Arrays.asList(((String) row[5]).split(",")) // split skills string into list
            );
            finalList.add(dto);
        }

        return finalList;
    }

    public void parseResume(MultipartFile file) {
        try {
            String content = extractTextFromFile(file);

            String name = extractName(content);
            String email = extractEmail(content);
            String phone = extractPhone(content);
            String experience = extractExperience(content);
            List<String> skills = extractSkills(content);

            System.out.println("Name: " + name);
            System.out.println("Email: " + email);
            System.out.println("Phone: " + phone);
            System.out.println("Experience: " + experience);
            System.out.println("Skills: " + skills);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String extractTextFromFile(MultipartFile file) throws IOException {
        if (file.getOriginalFilename().endsWith(".pdf")) {
            try (PDDocument document = PDDocument.load(file.getInputStream())) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(document);
            }
        } else {
            return new String(file.getBytes());
        }
    }

    private String extractEmail(String text) {
        Matcher matcher = Pattern.compile("\\b[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}\\b").matcher(text);
        return matcher.find() ? matcher.group() : "Not Found";
    }

    private String extractPhone(String text) {
        Matcher matcher = Pattern.compile("\\b\\d{10}\\b").matcher(text);
        return matcher.find() ? matcher.group() : "Not Found";
    }

    private String extractName(String text) {
        String[] lines = text.split("\n");
        return lines.length > 0 ? lines[0].trim() : "Not Found";
    }

    private String extractExperience(String text) {
        Matcher matcher = Pattern.compile("(\\d+)\\+?\\s+years?\\s+of\\s+experience", Pattern.CASE_INSENSITIVE).matcher(text);
        return matcher.find() ? matcher.group(1) + " years" : "Not Found";
    }

    private List<String> extractSkills(String text) {
        List<String> knownSkills = knownskillrepo.findAll()
                                .stream()
                                .map(Knownskill::getName)
                                .collect(Collectors.toList());

        return knownSkills.stream()
                .filter(skill -> text.toLowerCase().contains(skill.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveresume(MultipartFile file) {
        try {
            String content = extractTextFromFile(file);

            String name = extractName(content);
            String email = extractEmail(content);
            String phone = extractPhone(content);
            String experienceStr = extractExperience(content);
            List<String> skillsList = extractSkills(content);

            double experience = extractExperienceAsDouble(experienceStr);

            Resume resume = new Resume();
            resume.setName(name);
            resume.setEmail(email);
            resume.setPhoneNumber(phone);
            resume.setYearsOfExperience(experience);

            for (String skillName : skillsList) {
                Skill skill = skillRepository.findByName(skillName)
                        .orElseGet(() -> new Skill(skillName));
                resume.addSkill(skill);
            }

            resumeRepository.save(resume);
            System.out.println("Saved resume for: " + name);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double extractExperienceAsDouble(String experienceStr) {
        try {
            return Double.parseDouble(experienceStr.replaceAll("[^\\d.]", ""));
        } catch (Exception e) {
            return 0.0;
        }
    }

    public List<Resume> getAllResumes() {
        return resumeRepository.findAll();
    }

    // New method to get resumes by userId
    // public List<ResumeWithSkillsDTO> getResumesByUserId(Long userId) {
    //     User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    //     List<Resume> resumes = resumeRepository.findByUser(user);

    //     List<ResumeWithSkillsDTO> resumeDTOs = new ArrayList<>();
    //     for (Resume resume : resumes) {
    //         // Pass yearsOfExperience as double
    //         ResumeWithSkillsDTO dto = new ResumeWithSkillsDTO(
    //             resume.getId(),
    //             resume.getName(),
    //             resume.getPhoneNumber(),
    //             resume.getEmail(),
    //             resume.getYearsOfExperience(),  // No casting needed for double
    //             resume.getSkills().toString()
    //         );
    //         resumeDTOs.add(dto);
    //     }
    //     return resumeDTOs;
    // }


    // test method
    public List<ResumeWithSkillsDTO> getResumesByUserId(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Resume> resumes = resumeRepository.findByUser(user);
        List<ResumeWithSkillsDTO> resumeDTOs = new ArrayList<>();
    
        for (Resume resume : resumes) {
            List<String> skillNames = resume.getSkills().stream()
                .map(Skill::getName)  // Extract skill names
                .collect(Collectors.toList());
    
            ResumeWithSkillsDTO dto = new ResumeWithSkillsDTO(
                resume.getId(),
                resume.getName(),
                resume.getPhoneNumber(),
                resume.getEmail(),
                resume.getYearsOfExperience(),
                skillNames
            );
    
            resumeDTOs.add(dto);
        }
    
        return resumeDTOs;
    }
    @Transactional
public void uploadResumeForUser(Long userId, MultipartFile file) {
    try {
        // Extract resume content and details
        String content = extractTextFromFile(file);
        String name = extractName(content);
        String email = extractEmail(content);
        String phone = extractPhone(content);
        String experienceStr = extractExperience(content);
        List<String> skillsList = extractSkills(content);

        double experience = extractExperienceAsDouble(experienceStr);

        // Find the user by ID
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Create a new Resume object
        Resume resume = new Resume();
        resume.setName(name);
        resume.setEmail(email);
        resume.setPhoneNumber(phone);
        resume.setYearsOfExperience(experience);
        resume.setUser(user);  // Associate with the user

        // Save the skills
        for (String skillName : skillsList) {
            Skill skill = skillRepository.findByName(skillName)
                    .orElseGet(() -> new Skill(skillName));
            resume.addSkill(skill);
        }

        // Save the resume
        resumeRepository.save(resume);
        System.out.println("Resume uploaded and associated with user: " + name);

    } catch (Exception e) {
        e.printStackTrace();
    }
}



// get filtered resumes for specific user
// public List<ResumeWithSkillsDTO> filterResumesByUser(Long userId, List<String> skillNames, double minExperience) {
//     User user = userRepository.findById(userId)
//         .orElseThrow(() -> new RuntimeException("User not found"));

//     List<Resume> userResumes = resumeRepository.findByUser(user);

//     return userResumes.stream()
//         .filter(resume -> resume.getYearsOfExperience() >= minExperience)
//         .filter(resume -> {
//             List<String> resumeSkillNames = resume.getSkills().stream()
//                 .map(Skill::getName)
//                 .map(String::toLowerCase)
//                 .collect(Collectors.toList());
//             return skillNames.stream()
//                 .allMatch(s -> resumeSkillNames.contains(s.toLowerCase()));
//         })
//         .map(resume -> {
//             List<String> skillList = resume.getSkills().stream()
//                 .map(Skill::getName)
//                 .collect(Collectors.toList());

//             return new ResumeWithSkillsDTO(
//                 resume.getId(),
//                 resume.getName(),
//                 resume.getPhoneNumber(),
//                 resume.getEmail(),
//                 resume.getYearsOfExperience(),
//                 skillList
//             );
//         })
//         .collect(Collectors.toList());
// }


// deklete users resume with id


public void deleteResumesByUserId(Long userId) {
    User user = userRepository.findById(userId)
                  .orElseThrow(() -> new RuntimeException("User not found"));
    resumeRepository.deleteAllByUserId(userId);




}


}