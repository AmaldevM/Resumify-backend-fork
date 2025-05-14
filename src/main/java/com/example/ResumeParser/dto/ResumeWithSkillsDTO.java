package com.example.ResumeParser.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor
public class ResumeWithSkillsDTO {
    private Long id;
    private String name;
    private String phoneNumber;
    private String email;
    private double yearsOfExperience;
    private List<String> skills;  // ✅ Correct

     public ResumeWithSkillsDTO(Long id, String name, String phoneNumber, String email, double yearsOfExperience, List<String> skills) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.yearsOfExperience = yearsOfExperience;
        this.skills = skills;
    }
}
