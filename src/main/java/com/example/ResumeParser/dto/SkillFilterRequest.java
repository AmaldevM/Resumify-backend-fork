package com.example.ResumeParser.dto;

import java.util.List;

public class SkillFilterRequest {

    private List<String> skills;
    private double minExp;

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public double getMinExp() {
        return minExp;
    }

    public void setMinExp(double minExp) {
        this.minExp = minExp;
    }
}
