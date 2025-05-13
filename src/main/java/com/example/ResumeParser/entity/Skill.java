package com.example.ResumeParser.entity;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "skills")
@Getter
@Setter
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "skills")
    @JsonBackReference
    private Set<Resume> resumes = new HashSet<>();

    public Skill() {}

    public Skill(String name) {
        this.name = name.toLowerCase();
    }

    // Override setter to maintain lowercase conversion
    public void setName(String name) {
        this.name = name.toLowerCase();
    }
}
