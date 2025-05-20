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
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    // public void parseResume(MultipartFile file) {
    //     try {
    //         String content = extractTextFromFile(file);

    //         String name = extractName(content);
    //         String email = extractEmail(content);
    //         String phone = extractPhone(content);
    //         String experience = extractExperience(content);
    //         List<String> skills = extractSkills(content);

    //         System.out.println("Name: " + name);
    //         System.out.println("Email: " + email);
    //         System.out.println("Phone: " + phone);
    //         System.out.println("Experience: " + experience);
    //         System.out.println("Skills: " + skills);

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }

    // private String extractTextFromFile(MultipartFile file) throws IOException {
    //     if (file.getOriginalFilename().endsWith(".pdf")) {
    //         try (PDDocument document = PDDocument.load(file.getInputStream())) {
    //             PDFTextStripper stripper = new PDFTextStripper();
    //             return stripper.getText(document);
    //         }
    //     } else {
    //         return new String(file.getBytes());
    //     }
    // }

    

    


    
    // old function that calculates experience
    // private double calculateExperienceDuration(String text) {
    //     Pattern dateRangePattern = Pattern.compile(
    //         "(?i)(\\b(?:jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|" +
    //         "jul(?:y)?|aug(?:ust)?|sep(?:t(?:ember)?)?|oct(?:ober)?|nov(?:ember)?|" +
    //         "dec(?:ember)?|\\d{4}))\\s*[-–to]{1,3}\\s*(\\b(?:present|current|jan(?:uary)?|" +
    //         "feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|jul(?:y)?|aug(?:ust)?|" +
    //         "sep(?:t(?:ember)?)?|oct(?:ober)?|nov(?:ember)?|dec(?:ember)?|\\d{4}))",
    //         Pattern.CASE_INSENSITIVE);
    
    //     Matcher matcher = dateRangePattern.matcher(text);
    //     int currentYear = java.time.Year.now().getValue();
    //     int minYear = Integer.MAX_VALUE;
    //     int maxYear = Integer.MIN_VALUE;
    
    //     while (matcher.find()) {
    //         String startStr = matcher.group(1).replaceAll("[^a-zA-Z0-9]", "").trim();
    //         String endStr = matcher.group(2).replaceAll("[^a-zA-Z0-9]", "").trim();
    
    //         int startYear = extractYear(startStr);
    //         int endYear = endStr.equalsIgnoreCase("present") || endStr.equalsIgnoreCase("current")
    //                 ? currentYear
    //                 : extractYear(endStr);
    
    //         if (startYear > 1900 && startYear <= currentYear) {
    //             minYear = Math.min(minYear, startYear);
    //         }
    //         if (endYear > 1900 && endYear <= currentYear) {
    //             maxYear = Math.max(maxYear, endYear);
    //         }
    //     }
    
    //     return (minYear <= maxYear && minYear != Integer.MAX_VALUE && maxYear != Integer.MIN_VALUE)
    //             ? maxYear - minYear
    //             : 0.0;
    // }
    
    private int extractYear(String value) {
        try {
            if (value.matches("\\d{4}")) {
                return Integer.parseInt(value);
            } else {
                Pattern pattern = Pattern.compile("(?:jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)(\\d{4})", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(value);
                if (matcher.find()) {
                    return Integer.parseInt(matcher.group(1));
                }
            }
        } catch (Exception ignored) {}
        return -1;
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

// new upload function from albin
public Resume uploadResumeForUser(Long userId,MultipartFile file) throws Exception {
    InputStream is = file.getInputStream();
    PDDocument document = PDDocument.load(is);
    String text = new PDFTextStripper().getText(document);
    String lowerText = text.toLowerCase();
    String[] lines = text.split("\n");

    String email = extractRegex(text, "[\\w\\.-]+@[\\w\\.-]+", "Email not found");
    String phone = extractRegex(text, "(\\+?\\d{1,3}[-.\\s]?)?(\\(?\\d{1,4}\\)?[-.\\s]?){1,5}\\d{1,4}", "Phone not found");
    double experience = extractExperienceYearsFromExperienceSection(text);
    
    if (experience == 0) {
        experience = extractExperienceYearsFromExperienceSection(text); // Fallback
    }

    // String skills = extractSkills(lowerText);
    List<String> skillsList = extractSkills(text);
    //         // Find the user by ID
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    String name = "Name not found";
    FontAwarePDFStripper fontStripper = new FontAwarePDFStripper();
    fontStripper.setStartPage(1);
    fontStripper.setEndPage(1);
    fontStripper.getText(document);
    String fontDetectedName = fontStripper.extractLargestText();
    if (!"Name not found".equals(fontDetectedName)) {
        name = fontDetectedName;
    } else {
        String emailLower = email.toLowerCase();
        for (int i = 0; i < lines.length; i++) {
            String cleanedLine = lines[i].replaceAll("[^\\p{Print}]", "").toLowerCase();
            if (!emailLower.isEmpty() && cleanedLine.contains(emailLower)) {
                int start = Math.max(0, i - 5);
                for (int j = i - 1; j >= start; j--) {
                    String candidate = lines[j].trim();
                    if (candidate.matches("[A-Za-z ]{3,40}")) {
                        int wordCount = candidate.split("\\s+").length;
                        if (wordCount >= 2 && wordCount <= 3) {
                            name = candidate;
                            break;
                        }
                    }
                }
                if (!"Name not found".equals(name))
                    break;
            }
        }
    }
    //  Save the skills
 

    document.close();

    Resume resume = new Resume();
    resume.setName(name);
    resume.setEmail(email);
    resume.setUser(user); 
    resume.setPhoneNumber(phone);
    resume.setYearsOfExperience(experience);
    for (String skillName : skillsList) {
        Skill skill = skillRepository.findByName(skillName)
                .orElseGet(() -> new Skill(skillName));
        resume.addSkill(skill);
    }
  

    return resumeRepository.save(resume);
}

private double extractExperienceYearsFromExperienceSection(String text) {
    String[] lines = text.split("\\r?\\n");
    boolean inExperienceSection = false;
    int currentYear = java.time.Year.now().getValue();
    List<Integer> years = new ArrayList<>();

    Pattern dateRangePattern = Pattern.compile(
        "(?i)(\\b(?:jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|" +
        "jul(?:y)?|aug(?:ust)?|sep(?:t(?:ember)?)?|oct(?:ober)?|nov(?:ember)?|" +
        "dec(?:ember)?|\\d{4}))\\s*[-–to]{1,3}\\s*" +
        "(\\b(?:present|current|jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|" +
        "may|jun(?:e)?|jul(?:y)?|aug(?:ust)?|sep(?:t(?:ember)?)?|oct(?:ober)?|" +
        "nov(?:ember)?|dec(?:ember)?|\\d{4}))"
    );

    List<String> experienceKeywords = Arrays.asList(
        "experience", "work history", "employment", "professional background"
    );
    List<String> stopSectionKeywords = Arrays.asList(
        "education", "skills", "certifications", "projects", "summary", "objective"
    );

    for (String line : lines) {
        String lower = line.toLowerCase().trim();

        // Check if entering the experience section
        if (!inExperienceSection && experienceKeywords.stream().anyMatch(lower::contains)) {
            inExperienceSection = true;
            continue;
        }

        // If in experience section and a new section starts, stop
        if (inExperienceSection && stopSectionKeywords.stream().anyMatch(lower::contains)) {
            break;
        }

        // If in experience section, look for date ranges in the line
        if (inExperienceSection) {
            Matcher matcher = dateRangePattern.matcher(line);
            while (matcher.find()) {
                int startYear = extractYear(matcher.group(1));
                int endYear = matcher.group(2).equalsIgnoreCase("present") || matcher.group(2).equalsIgnoreCase("current")
                    ? currentYear
                    : extractYear(matcher.group(2));

                if (startYear != -1 && endYear != -1 && startYear <= endYear) {
                    for (int y = startYear; y <= endYear; y++) {
                        if (!years.contains(y)) {
                            years.add(y);
                        }
                    }
                }
            }
        }
    }

    return years.isEmpty() ? 0.0 : years.size(); // each unique year = 1 year of experience
}





// deklete users resume with id


public void deleteResumesByUserId(Long userId) {
    User user = userRepository.findById(userId)
                  .orElseThrow(() -> new RuntimeException("User not found"));
    resumeRepository.deleteAllByUserId(userId);




}



private String extractRegex(String text, String regex, String defaultValue) {
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(text);
    return matcher.find() ? matcher.group() : defaultValue;
}

// private String extractSkills(String lowerText) {
//     Set<String> foundSkills = new HashSet<>();
//     for (String skill : KNOWN_SKILLS) {
//         if (lowerText.contains(skill)) {
//             foundSkills.add(skill);
//         }
//     }
//     return String.join(", ", foundSkills);
// }




}


































































