package com.example.ResumeParser.dto;

import lombok.Data;
import java.util.List;

@Data
public class Resumefilterrequest {
    private Long userId;
    private List<String> skills;
    private double minYearsOfExperience;
}
